package com.mygdx.crazysoccer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.crazysoccer.GameScreen.ScreenNames;
import com.mygdx.crazysoccer.Player.AddictedTo;
import com.mygdx.crazysoccer.Player.Directions;
import com.mygdx.crazysoccer.Player.States;
import com.mygdx.crazysoccer.Players.Amplua;
import com.mygdx.crazysoccer.Wind.WindDirections;

public class Field extends Stage {
	
	// Состояние игры игра / пауза
	public static enum GameStates {
		RUN,
		PAUSE
	}
	
	// Текущее состояние игры
	public GameStates gameState;
	
	// Количество игроков на поле
	private final int PLAYERS_AMOUNT = 12;
	
	// Служебные переменные
	private int Z_INDEX;
	private boolean ballOutPlayed = false;
	
	private HashMap<Integer,Float> Z_POSITIONS = new HashMap<Integer,Float>();
	
	// Текстура для хранения спрайтов
	public static Texture sprites;
	public static int SPRITES_WIDTH;
	public static int SPRITES_HEIGHT;
	
	// Екземпляр мяча
	public Ball ball;
	
	// Флаг хранящий состояние был ли забит гол
	private boolean GOAL_SCORED = false;
	
	// Идентификатор игрока, который последний раз забил гол
	private int GOAL_SCORED_BY = -1;
	
	// Идентификатор ворот, в которые был в последний раз забит гол
	private int GOAL_SCORED_GATES = -1;
	
	// Ворота
	public Gate[] gates = new Gate[2];
	
	// Екземпляр класса описывающего игрока
	public Player[] players = new Player[PLAYERS_AMOUNT];
	
	public AI[] ai = new AI[2];
	
	// Листья
	private Leaf[] leafs = new Leaf[3];
	
	// Капли
	private Drop[] drops = new Drop[3];
	
	// Размеры поля в клетках
	private int CELLS_X;
	private int CELLS_Y;
	
	// Четыре точки определяющие размеры поля
	private Vector2 leftBottom;
	private Vector2 rightBottom;
	private Vector2 rightTop;
	private Vector2 leftTop;
	
	// Размры игрового (футбольного) поля
	public int fieldMinWidth;
	public int fieldMaxWidth;
	public int fieldHeight;
	
	// Размеры штрафной
	int innerBoxWidth = 130;
	int innerBoxHeight = 150;
	int outerBoxWidth = 300;
	int outerBoxHeight = 280;

	// Параметры игрового экрана
	public float camMaxX;
	public float camMaxY;
	public float worldWidth;
	public float worldHeight;
	
	// Смещение игрового поля относительно карты (левого нижнего угла)
	public int fieldOffsetX;
	public int fieldOffsetY;  
	
	float[][] playersArrangment = new float[12][2];
	
	public TiledMap fieldMap;
    public TiledMapRenderer fieldMapRenderer;
    public OrthographicCamera camera;
	
    // Сохранение нажатых клавиш и их времени
    public Actions actions = new Actions(PLAYERS_AMOUNT);
    
	// Вершины многоугольников описывающих лужи / болота
	private ArrayList<Vector2> diche;
	private ArrayList<ArrayList<Vector2>> diches = new ArrayList<ArrayList<Vector2>>();
	
	// Локализация ячеек карты (находятся ли они внутри озера / болота)
	private boolean[][] cellsLocation;
    
	public Field() {
		// Установка состояния игры - активна
		gameState = GameStates.RUN;
		
		// Загрузка музыки и звуковых эффектов  *
		loadSounds();
		
		// Загрузка спрайтов
		sprites = new Texture(Gdx.files.internal("graphics/atlas.png")); 
		SPRITES_WIDTH = sprites.getWidth() / 32;
		SPRITES_HEIGHT = sprites.getHeight() / 32;
		
		// Создание мяча
		ball = new Ball();
		this.addActor(ball);
		this.addActor(ball.shadow);
		ball.attachField(this);
		
		// Создание ворот
		for (int i = 0; i < gates.length; i++) {
			// Создание первого игрока
			gates[i] = new Gate(i);

			// Добавление игрока (актера) на сцену (поле)
			this.addActor(gates[i]);
		}
		
		// Создание игроков
		for (int i = 0; i < players.length; i++) 
		{
			// Создание игрока
			players[i] = new Player(i);
			
			// Привязка слушателя ввода для игрока
			players[i].setActionsListener(actions);

			if (i <= 5) 
			{
				// Установка ворот, по которым игрок должен наносить удар
				players[i].setDestinationGateId(Gate.RIGHT_GATES);
				
				// Привязка игрока к команде
				players[i].setTeamId(Teams.NEKKETSU);
			}
			else
			{
				players[i].setDestinationGateId(Gate.LEFT_GATES);
				
				players[i].setTeamId(Teams.ITALY);
			}
			
			// Установка амплуа игрока
			players[i].setAmplua(
				Players.getParams(i).amplua
			);

			// Кем управляется игрок
			if (i == 0) {
				players[i].addictedTo = AddictedTo.HUMAN;
			}
			else {
				players[i].addictedTo = AddictedTo.AI;
			}
			
			// Добавление игрока (актера) на сцену (поле)
			this.addActor(players[i]);
			this.addActor(players[i].shadow);
			
			players[i].attachField(this);
			players[i].attachBall(ball);
		}
		
		
		
		// Экземпляр класса ИИ
		ai[0] = new AI();
		
		// Передаем ИИ ссылку на поле
		ai[0].attachField(this);
		
		// Добавляем ID игрока, за которого будет играть ИИ 
		ai[0].addPlayer(11);
		ai[0].addPlayer(10);
		ai[0].addPlayer(9);
		ai[0].addPlayer(8);
		ai[0].addPlayer(7);
		ai[0].addPlayer(6);
		
		// Экземпляр класса ИИ
		ai[1] = new AI();
		
		// Передаем ИИ ссылку на поле
		ai[1].attachField(this);
		
		// Добавляем ID игрока, за которого будет играть ИИ 
//		ai[1].addPlayer(0);
		ai[1].addPlayer(1);
		ai[1].addPlayer(2);
		ai[1].addPlayer(3);
		ai[1].addPlayer(4);
		ai[1].addPlayer(5);
		
		// Создание листьев
		for (int i = 0; i < leafs.length; i++) {
			leafs[i] = new Leaf(WindDirections.TOP_DOWN);
			leafs[i].setPosition(
				(float)Math.random() * Gdx.graphics.getWidth(), 
				(float)Math.random() * Gdx.graphics.getHeight()
			);
			this.addActor(leafs[i]);
		}
		
		// Создание капель
		for (int i = 0; i < drops.length; i++) {
			drops[i] = new Drop();
			this.addActor(drops[i]);
		}
		
		// Создание камеры
		camera = new OrthographicCamera(Vars.WINDOW_WIDTH, Vars.WINDOW_HEIGHT);
        camera.update();
	}
	
	private void loadSounds() {
		// Загрузка фоновой музыки
		Sounds.load("bg", CrazySoccer.BG_TRACK);
		Sounds.play("bg");
		Sounds.loop("bg", true);
		
		// Звук паса
		Sounds.load("pass01", "sound/sfx/pass01.ogg");
		
		// Звук полята мяча после удара
		Sounds.load("kick01", "sound/sfx/kick01.ogg");
		
		// Звук пробежки
		Sounds.load("run01", "sound/sfx/run01.ogg");
		
		// Звук приземления игрока
		Sounds.load("landing01", "sound/sfx/landing01.ogg");
		
		// Звук приема мяча полевым игроком
		Sounds.load("catchball01", "sound/sfx/catchball01.ogg");
		
		// Звук начала прыжка
		Sounds.load("jump01", "sound/sfx/jump01.ogg");
		
		// Звук "юлы"
		Sounds.load("whirligid01", "sound/sfx/whirligid01.ogg");
		
		// Звук "дрибблинга"
		Sounds.load("dribbling01", "sound/sfx/dribbling01.ogg");
		
		// Звук подката / удара плечом
		Sounds.load("tackle01", "sound/sfx/tackle01.ogg");
		
		// Звук столкновения при ударе плечом
		Sounds.load("hit01", "sound/sfx/hit01.ogg");
		
		// Звук несильного ветра
		Sounds.load("wind01", "sound/sfx/wind01.ogg");
		
		// Звук сильного ветра
		Sounds.load("wind02", "sound/sfx/wind02.ogg");
		
		// Звук удара мяча о поле
		Sounds.load("balllanding02", "sound/sfx/balllanding02.ogg");
		
		// Звук выхода мяча за пределы поля
		Sounds.load("ballout01", "sound/sfx/whistle01.ogg");
		
		// Звук сигнала о забитом голе
		Sounds.load("goalin01", "sound/sfx/goalin01.ogg");
	}
	
	public void LoadMap(String mapName) {
		fieldMap = new TmxMapLoader().load(mapName);
		fieldMapRenderer = new OrthogonalTiledMapRenderer(fieldMap, 1.0f);
		
		for (int i = 0; i < fieldMap.getLayers().get("field").getObjects().getCount(); i++) {
        	MapObject ma = (MapObject)fieldMap.getLayers().get("field").getObjects().get(i);
        	
        	if ((Object)ma instanceof PolylineMapObject) {
        		Polyline polyline = ((PolylineMapObject)ma).getPolyline();
        		
        		// Получение опорных точек трапеции поля
        		leftBottom  = new Vector2(polyline.getVertices()[0],polyline.getVertices()[1]);
        		rightBottom = new Vector2(polyline.getVertices()[2],polyline.getVertices()[3]);
        		rightTop    = new Vector2(polyline.getVertices()[4],polyline.getVertices()[5]);
        		leftTop     = new Vector2(polyline.getVertices()[6],polyline.getVertices()[7]);
        		
        		Arrays.sort(polyline.getVertices());
        		
        		// Получение размеров игрового поля
        		this.fieldMaxWidth = (int)polyline.getVertices()[9] - (int)polyline.getVertices()[0];
        		this.fieldMinWidth = (int)polyline.getVertices()[8] - (int)polyline.getVertices()[5];
        		this.fieldHeight   = (int)polyline.getVertices()[6] - (int)polyline.getVertices()[0];
        		
        		// Получение смещения разметки поля относительно карты
        		this.fieldOffsetX = Math.round(ma.getProperties().get("x", Float.class));
        		this.fieldOffsetY = Math.round(ma.getProperties().get("y", Float.class));
        		
        		this.CELLS_X = this.fieldMap.getProperties().get("width", Integer.class);
        		this.CELLS_Y = this.fieldMap.getProperties().get("height", Integer.class);
        		
        		// Установка размеров в зависимости от размеров карты
        		cellsLocation = new boolean[this.CELLS_Y][this.CELLS_X];
        		
        		// Ширина игрового мира. Подсчет ведется с учетом того, что смещение справа такое же как и слева
        		this.worldWidth = this.CELLS_X * 32;
        		this.worldHeight = this.CELLS_Y * 32;
        		
        		// Определение наибольшей координаты X, в которую можно смещать камеру     
        		this.camMaxX = this.worldWidth - Gdx.graphics.getWidth() / 2.0f;
        		this.camMaxY = this.worldHeight - Gdx.graphics.getHeight() / 2.0f;
        		
        		playersArrangment[0][0] = worldWidth / 2.0f - 40;
        		playersArrangment[0][1] = fieldOffsetY + fieldHeight / 2.0f;
        		
        		playersArrangment[1][0] = 800;
        		playersArrangment[1][1] = 500;
        		
        		playersArrangment[2][0] = 800;
        		playersArrangment[2][1] = worldHeight - 500;
        		
        		playersArrangment[3][0] = worldWidth / 2.0f - 350;
        		playersArrangment[3][1] = 500;
        		
        		playersArrangment[4][0] = worldWidth / 2.0f - 350;
        		playersArrangment[4][1] = worldHeight - 500;
        		
        		// Левый вратарь
        		playersArrangment[5][0] = 450;
        		playersArrangment[5][1] = fieldOffsetY + fieldHeight / 2.0f;
        		
        		
        		
        		playersArrangment[6][0] = worldWidth / 2.0f + 350;
        		playersArrangment[6][1] = 500;
        		
        		playersArrangment[7][0] = worldWidth - 800;
        		playersArrangment[7][1] = worldHeight - 500;
        		
        		playersArrangment[8][0] = worldWidth - 800;
        		playersArrangment[8][1] = 500;
        		
        		playersArrangment[9][0] = worldWidth / 2.0f + 350;
        		playersArrangment[9][1] = worldHeight - 500;
        		
        		playersArrangment[10][0] = worldWidth / 2.0f + 200;
        		playersArrangment[10][1] = fieldOffsetY + fieldHeight / 2.0f;
        		
        		// Правый вратарь
        		playersArrangment[11][0] = worldWidth - 430;
        		playersArrangment[11][1] = fieldOffsetY + fieldHeight / 2.0f;
        		
        		// Расстановка игроков
        		this.actorsArrangement();
        		
        		// Определение кооридат штанг
        		float y1 = this.worldHeight / 2.0f - gates[0].height() / 2.0f;
        		float y2 = this.worldHeight / 2.0f + gates[0].width() / 2.0f;
        		gates[0].setBottomBar(new Vector2(fieldOffsetX + this.mGetSideLineProjection(y1), y1));
        		gates[0].setTopBar(new Vector2(fieldOffsetX + this.mGetSideLineProjection(y2), y2));
        		
        		// Указание проекции ворот на плоскость поля для подсчета столкновений
        		gates[0].gateProjection[0][0] = gates[0].getBottomBar().x - 100;
        		gates[0].gateProjection[0][1] = gates[0].getBottomBar().y - 10;
        		
        		gates[0].gateProjection[1][0] = gates[0].getTopBar().x - 100;
        		gates[0].gateProjection[1][1] = gates[0].getTopBar().y + 5;
        		
        		gates[0].gateProjection[2][0] = gates[0].getTopBar().x;
        		gates[0].gateProjection[2][1] = gates[0].getTopBar().y + 5;
        		
        		gates[0].gateProjection[3][0] = gates[0].getTopBar().x;
        		gates[0].gateProjection[3][1] = gates[0].getTopBar().y - 5;
        		
        		gates[0].gateProjection[4][0] = gates[0].getTopBar().x - 90;
        		gates[0].gateProjection[4][1] = gates[0].getTopBar().y - 5;
        		
        		gates[0].gateProjection[5][0] = gates[0].getBottomBar().x - 90;
        		gates[0].gateProjection[5][1] = gates[0].getBottomBar().y + 5;
        		
        		gates[0].gateProjection[6][0] = gates[0].getBottomBar().x;
        		gates[0].gateProjection[6][1] = gates[0].getBottomBar().y + 5;
        		
        		gates[0].gateProjection[7][0] = gates[0].getBottomBar().x;
        		gates[0].gateProjection[7][1] = gates[0].getBottomBar().y - 10;
        		
        		
        		gates[1].setBottomBar(new Vector2(fieldOffsetX + fieldMaxWidth - this.mGetSideLineProjection(y1), y1));
        		gates[1].setTopBar(new Vector2(fieldOffsetX + fieldMaxWidth - this.mGetSideLineProjection(y2), y2));
        		
        		gates[1].gateProjection[0][0] = gates[1].getBottomBar().x + 100;
        		gates[1].gateProjection[0][1] = gates[1].getBottomBar().y - 10;
        		
        		gates[1].gateProjection[1][0] = gates[1].getTopBar().x + 100;
        		gates[1].gateProjection[1][1] = gates[1].getTopBar().y + 5;
        		
        		gates[1].gateProjection[2][0] = gates[1].getTopBar().x;
        		gates[1].gateProjection[2][1] = gates[1].getTopBar().y + 5;
        		
        		gates[1].gateProjection[3][0] = gates[1].getTopBar().x;
        		gates[1].gateProjection[3][1] = gates[1].getTopBar().y - 5;
        		
        		gates[1].gateProjection[4][0] = gates[1].getTopBar().x + 90;
        		gates[1].gateProjection[4][1] = gates[1].getTopBar().y - 5;
        		
        		gates[1].gateProjection[5][0] = gates[1].getBottomBar().x + 90;
        		gates[1].gateProjection[5][1] = gates[1].getBottomBar().y + 5;
        		
        		gates[1].gateProjection[6][0] = gates[1].getBottomBar().x;
        		gates[1].gateProjection[6][1] = gates[1].getBottomBar().y + 5;
        		
        		gates[1].gateProjection[7][0] = gates[1].getBottomBar().x;
        		gates[1].gateProjection[7][1] = gates[1].getBottomBar().y - 10;
        	}
		}
		
		// Парсинг карты на наличие препятствий (озера / болота)
		for (int i = 0; i < fieldMap.getLayers().get("diches").getObjects().getCount(); i++) {
        	MapObject ma = (MapObject)fieldMap.getLayers().get("diches").getObjects().get(i);
        	
        	if ((Object)ma instanceof PolylineMapObject) {
        		Polyline polyline = ((PolylineMapObject)ma).getPolyline();
        		
        		// Получение координаты расположения препятствия
        		float dichX = Math.round(ma.getProperties().get("x", Float.class));
    			float dichY = Math.round(ma.getProperties().get("y", Float.class));
    			
    			float pointX = 0;
    			float pointY = 0;
    			
    			diche = new ArrayList<Vector2>();
    			
    			// Получение верших озера / болота
        		for (int j = 0; j < polyline.getVertices().length; j++) {
        			if (j % 2 == 0) {
        				pointX = dichX + polyline.getVertices()[j];
        			}
        			else {
        				pointY = dichY + polyline.getVertices()[j];
        				
        				diche.add(new Vector2(pointX, pointY));
        			}
        		}
        		
        		diches.add(diche);
        	}
		}
		
		// Локализация ячеек карты
		for (int i = 0; i < this.CELLS_Y; i++) {
			for (int j = 0; j < this.CELLS_X; j++) {
				for (int k = 0; k < diches.size(); k++) {
					int c = MathUtils.intersectCount(j * 32 + 16, i * 32 + 16, diches.get(k));
					
					// Если пустая ячейка находится внутри многоугольника (островки)
					if (c % 2 != 0 && cellsLocation[i][j]) {
						cellsLocation[i][j] = false;
					}
					// Если количество пересечений луча нечетное, то персонаж внутри озера / болота
					else if (c % 2 != 0) {
						cellsLocation[i][j] = true;
					}
				}
			}
		}
	}
	
	/**
	 * Получение ID ближайшего игрока к игроку с ID = playerId
	 * 
	 * @param playerId<int> - ID игрока, для которого искать ближайшего игрока
	 * 
	 * @param searchAmong<int> - Среди кого искать
	 * 	 Player.NEAREST_RIVAL - искать среди противников
	 * 	 Player.NEAREST_ALLY - искать среди союзников
	 * 	 Player.NEAREST_BOTH - искать среди всех
	 * 
	 * @param aliveOnly<boolean> - Искать только среди жывих (не States.DEAD) игроков
	 *  
	 * @return ID - ближайшего игрока 
	 */
	public int playerNearest(int playerId, int searchAmong, boolean aliveOnly, boolean up, boolean right, boolean down, boolean left) { 
		
		int nearestPlayerId = -1; 
		float nearestPlayerDist = 99999; 
		float curPlayerDist = 0.0f;
		boolean flag      = false, 
				aliveFlag = false, 
				upFlag    = false, 
				rightFlag = false, 
				downFlag  = false, 
				leftFlag  = false;
		
		if (up) down = false;
		if (right) left = false;
		
		for (int i = 0; i < players.length; i++) {
			
			// Искать среди всех и живых и прибитых
			if (aliveOnly == false)
				aliveFlag = true;
			// Искать только среди живых
			else
				aliveFlag = !players[i].isDead();
			
			//upFlag = rightFlag = downFlag = leftFlag = false;
			
			if (up) 
				upFlag = players[i].getAbsY() >= players[playerId].getAbsY();
			else 
				upFlag = true;
			
			if (right) 
				rightFlag = players[i].getAbsX() >= players[playerId].getAbsX();
			else 
				rightFlag = true;
			
			if (down) 
				downFlag = players[i].getAbsY() <= players[playerId].getAbsY();
			else 
				downFlag = true;
			
			if (left) 
				leftFlag = players[i].getAbsX() <= players[playerId].getAbsX();
			else 
				leftFlag = true;
			
			
			if (searchAmong == 0) {
				flag = (players[playerId].getTeamId() != players[i].getTeamId() && aliveFlag) ? true : false;
			}
			else if (searchAmong == 1) {
				flag = (players[playerId].getTeamId() == players[i].getTeamId() && aliveFlag) ? true : false;
			}
			else if (searchAmong == 2) {
				flag = aliveFlag; 
			}
			
			if (players[i].getPlayerId() != playerId && flag && upFlag && rightFlag && downFlag && leftFlag) {
				// Расстояние к текущему игроку
				curPlayerDist = MathUtils.distance(players[playerId].getAbsX(), players[playerId].getAbsY(), players[i].getAbsX(), players[i].getAbsY());
				
				// Если текущий игрок ближе
				if (curPlayerDist < nearestPlayerDist) {
					nearestPlayerDist = curPlayerDist;
					nearestPlayerId = players[i].getPlayerId();
				}				
			}
		}
		
		return nearestPlayerId;
	}
	
	/**
	 * Есть ли в радиусе r от мяча количество игроков союзника / соперника n
	 * @param x
	 * @param y
	 * @return true - если необходимое количество найдено
	 */
	public boolean playersNearThePoint(int playerId, float r, int n, int searchAmong)
	{
		boolean flag = false;
		int found = 0;
		
		for (int i = 0; i < players.length; i++) {
			
			if (playerId != players[i].getPlayerId()) 
			{
				if (searchAmong == 0) {
					flag = (players[playerId].getTeamId() != players[i].getTeamId()) ? true : false;
				}
				else if (searchAmong == 1) {
					flag = (players[playerId].getTeamId() == players[i].getTeamId()) ? true : false;
				}
				else if (searchAmong == 2) {
					flag = true; 
				}
				
				if 
				(
					flag && 
					MathUtils.distance(
						players[playerId].getAbsX(), 
						players[playerId].getAbsY(), 
						players[i].getAbsX(), 
						players[i].getAbsY()
					) < r
				) 
				{
					if (++found >= n) return true;
				}
			}
		}
		
		return false;
	}
	
	public int findNearesPlayerToBall(ArrayList<Amplua> ampluas, ArrayList<AddictedTo> addictedTo, boolean aliveOnly, int destGates) {
		
		float minDist = 9999.0f; 
		
		int nearestPlayer = -1;
		
		for (int i = 0; i < players.length; i++) {
			
			// Поиск среди живых или всех
			boolean flag1 = (aliveOnly) ? !players[i].isDead() : true;
			
			// Расстояние от мяча к игроку
			float curDist = MathUtils.distance(
								ball.getAbsX(), 
								ball.getAbsY(), 
								players[i].getAbsX(), 
								players[i].getAbsY()
							);
			
			if 
			(
				flag1 && 
				addictedTo.indexOf(players[i].addictedTo) != -1 &&
				curDist < minDist && 
				players[i].getDestinationGateId() == destGates && 
				ampluas.indexOf(players[i].getAmplua()) != -1
			)
			{
				minDist = curDist;
				nearestPlayer = players[i].getPlayerId();
			}
		}
		
		return nearestPlayer;
	}

	// Проверка находится ли клетка внутри полигона (озера / болота)
	public boolean isCellInPolygon(float x, float y) {
		int cellX = (int)(x / 32);
		int cellY = (int)(y / 32);
		
		if (cellX >= this.CELLS_X) cellX = this.CELLS_X - 1;
		if (cellY >= this.CELLS_Y) cellY = this.CELLS_Y - 1;
		
		return cellsLocation[cellY][cellX];
	}
	
    // Сортировка коллекции по ключам
    private static HashMap<Integer, Float> sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
             public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                   .compareTo(((Map.Entry) (o2)).getValue());
             }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
		   Map.Entry entry = (Map.Entry) it.next();
		   sortedHashMap.put(entry.getKey(), entry.getValue());
        } 
        return sortedHashMap;
    }
	
	// Получение длины проекции отрезка на ось аута поля (используется для проверки
	// находится ли объект в пределах поля)
	public float mGetSideLineProjection(float h) {
		float b = (fieldMaxWidth - fieldMinWidth) / 2.0f;
		float c = (float)Math.sqrt(fieldHeight * fieldHeight + b * b);
		float sinAlpha = (float)fieldHeight / c;
		float cosAlpha = (float)b / c;
		
		return Math.abs(h / (sinAlpha / cosAlpha));
	}
	
	public String printFieldSizes() {
		return "Min width:"+fieldMinWidth+"\n"+"Max width:"+fieldMaxWidth+"\n"+"Height:"+fieldHeight;
	}
	
	
	public void resize (int width, int height) {
	    // See below for what true means.
	    this.getViewport().update(width, height, true);
	}
	
	// Отрисовка поля
	public void drawField() {
		 Gdx.gl20.glLineWidth(8);
		 CrazySoccer.shapeRenderer.setProjectionMatrix(camera.combined);
		 CrazySoccer.shapeRenderer.begin(ShapeType.Line);
		 CrazySoccer.shapeRenderer.setColor(1, 1, 1, 1);
		 
		 // Рамка поля
		 CrazySoccer.shapeRenderer.line(leftBottom.x + fieldOffsetX, leftBottom.y + fieldOffsetY, rightBottom.x + fieldOffsetX, rightBottom.y + fieldOffsetY);
		 CrazySoccer.shapeRenderer.line(rightBottom.x + fieldOffsetX, rightBottom.y + fieldOffsetY, rightTop.x + fieldOffsetX, rightTop.y + fieldOffsetY);
		 CrazySoccer.shapeRenderer.line(rightTop.x + fieldOffsetX, rightTop.y + fieldOffsetY, leftTop.x + fieldOffsetX, leftTop.y + fieldOffsetY);
		 CrazySoccer.shapeRenderer.line(leftTop.x + fieldOffsetX, leftTop.y + fieldOffsetY, leftBottom.x + fieldOffsetX, leftBottom.y + fieldOffsetY);
		 
		 // Центр поля
		 CrazySoccer.shapeRenderer.line(leftBottom.x + fieldMaxWidth / 2.0f + fieldOffsetX, leftBottom.y + fieldOffsetY, leftBottom.x + fieldMaxWidth / 2.0f + fieldOffsetX, leftTop.y + fieldOffsetY);
		 
		 // Круг в центре поля
		 CrazySoccer.shapeRenderer.circle(leftBottom.x + fieldMaxWidth / 2.0f + fieldOffsetX, fieldHeight / 2.0f + fieldOffsetY, 200);
		 
		 // Кружки для подачи угловых
		 CrazySoccer.shapeRenderer.arc(leftBottom.x + fieldOffsetX, leftBottom.y + fieldOffsetY, 60, 0, 82);
		 CrazySoccer.shapeRenderer.arc(leftTop.x + fieldOffsetX, leftTop.y + fieldOffsetY, 60, -97, 97);
		 CrazySoccer.shapeRenderer.arc(rightTop.x + fieldOffsetX, rightTop.y + fieldOffsetY, 60, 180, 97);
		 CrazySoccer.shapeRenderer.arc(rightBottom.x + fieldOffsetX, rightBottom.y + fieldOffsetY, 60, 97, 82);
		 
		 // Левые ворота
		 float yCenter = leftBottom.y + fieldHeight / 2.0f + fieldOffsetY;
		 CrazySoccer.shapeRenderer.line(leftBottom.x + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter + innerBoxHeight)), yCenter + innerBoxHeight, innerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter + innerBoxHeight)), yCenter + innerBoxHeight);
		 CrazySoccer.shapeRenderer.line(leftBottom.x + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter - innerBoxHeight)), yCenter - innerBoxHeight, innerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter - innerBoxHeight)), yCenter - innerBoxHeight);
		 CrazySoccer.shapeRenderer.line(innerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter + innerBoxHeight)), yCenter + innerBoxHeight, innerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter - innerBoxHeight)), yCenter - innerBoxHeight);
		 CrazySoccer.shapeRenderer.line(leftBottom.x + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter + outerBoxHeight)), yCenter + outerBoxHeight, outerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter + outerBoxHeight)), yCenter + outerBoxHeight);
		 CrazySoccer.shapeRenderer.line(leftBottom.x + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter - outerBoxHeight)), yCenter - outerBoxHeight, outerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter - outerBoxHeight)), yCenter - outerBoxHeight);
		 CrazySoccer.shapeRenderer.line(outerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter + outerBoxHeight)), yCenter + outerBoxHeight, outerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter - outerBoxHeight)), yCenter - outerBoxHeight);
		 CrazySoccer.shapeRenderer.arc(outerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter)), yCenter, 100, 263f, 180);
		 
		 // Правые ворота
		 CrazySoccer.shapeRenderer.line(rightBottom.x - mGetSideLineProjection(Math.round(yCenter + innerBoxHeight)) + fieldOffsetX, yCenter + innerBoxHeight, rightBottom.x - mGetSideLineProjection(Math.round(yCenter + innerBoxHeight)) - innerBoxWidth + fieldOffsetX, yCenter + innerBoxHeight);
		 CrazySoccer.shapeRenderer.line(rightBottom.x - mGetSideLineProjection(Math.round(yCenter - innerBoxHeight)) + fieldOffsetX, yCenter - innerBoxHeight, rightBottom.x - mGetSideLineProjection(Math.round(yCenter - innerBoxHeight)) - innerBoxWidth + fieldOffsetX, yCenter - innerBoxHeight);
		 CrazySoccer.shapeRenderer.line(rightBottom.x - mGetSideLineProjection(Math.round(yCenter + innerBoxHeight)) - innerBoxWidth + fieldOffsetX, yCenter + innerBoxHeight, rightBottom.x - mGetSideLineProjection(Math.round(yCenter - innerBoxHeight)) - innerBoxWidth + fieldOffsetX, yCenter - innerBoxHeight);
		 CrazySoccer.shapeRenderer.line(rightBottom.x - mGetSideLineProjection(Math.round(yCenter + outerBoxHeight)) + fieldOffsetX, yCenter + outerBoxHeight, rightBottom.x - mGetSideLineProjection(Math.round(yCenter + outerBoxHeight)) - outerBoxWidth + fieldOffsetX, yCenter + outerBoxHeight);
		 CrazySoccer.shapeRenderer.line(rightBottom.x - mGetSideLineProjection(Math.round(yCenter - outerBoxHeight)) + fieldOffsetX, yCenter - outerBoxHeight, rightBottom.x - mGetSideLineProjection(Math.round(yCenter - outerBoxHeight)) - outerBoxWidth + fieldOffsetX, yCenter - outerBoxHeight);
		 CrazySoccer.shapeRenderer.line(rightBottom.x - mGetSideLineProjection(Math.round(yCenter + outerBoxHeight)) - outerBoxWidth + fieldOffsetX, yCenter + outerBoxHeight, rightBottom.x - mGetSideLineProjection(Math.round(yCenter - outerBoxHeight)) - outerBoxWidth + fieldOffsetX, yCenter - outerBoxHeight);
		 CrazySoccer.shapeRenderer.arc(rightBottom.x - mGetSideLineProjection(Math.round(yCenter)) - outerBoxWidth + fieldOffsetX, yCenter, 100, 97f, 180);
		 
		 CrazySoccer.shapeRenderer.end();
		 
		 // Точка в центрe поля
		 CrazySoccer.shapeRenderer.begin(ShapeType.Filled);
		 CrazySoccer.shapeRenderer.circle(fieldOffsetX + fieldMaxWidth / 2.0f, fieldOffsetY + fieldHeight / 2.0f, 15);
		 CrazySoccer.shapeRenderer.end();
		 
//		 for (int i = 0; i < diches.size(); i++) {
//			 for (int j = 0; j < diches.get(i).size() - 1; j++) {
//				 shapeRenderer.begin(ShapeType.Line);
//				 Gdx.gl20.glLineWidth(3);
//				 shapeRenderer.line(diches.get(i).get(j).x, diches.get(i).get(j).y, diches.get(i).get(j+1).x, diches.get(i).get(j+1).y);
//				 shapeRenderer.end();
//			 }
//		 }
	}
	 
	// Расположение игроков по полю
	public void actorsArrangement() {
		ball.setAbsX(worldWidth / 2.0f);
		ball.setAbsY(worldHeight / 2.0f);
		
		for (int i = 0; i < playersArrangment.length; i++) {
			players[i].setAbsX(playersArrangment[i][0]);
			players[i].setAbsY(playersArrangment[i][1]);
		}

		for (int i = 0; i < playersArrangment.length; i++) {
			players[i].setAbsX(playersArrangment[i][0]);
			players[i].setHomeX(playersArrangment[i][0]);
			players[i].setAbsY(playersArrangment[i][1]);
			players[i].setHomeY(playersArrangment[i][1]);
		}
		
		players[6].direction = 
		players[7].direction = 
		players[8].direction = 
		players[9].direction = 
		players[10].direction = 
		players[11].direction = Directions.LEFT;

		gates[0].setAbsX(290);
		gates[0].setAbsY(worldHeight / 2 - 100);
		
		gates[1].setAbsX(3448);
		gates[1].setAbsY(worldHeight / 2 - 100);
	}
	
	public void moveCamera() {
		// Перемещение всех персонажей относительно персонажа (мяча) за которым следит камера
		for (int i = 0; i < players.length; i++) {
			players[i].setX(players[i].getAbsX() - camera.position.x + Gdx.graphics.getWidth() / 2.0f);
			players[i].setY(players[i].getAbsY() - camera.position.y + Gdx.graphics.getHeight() / 2.0f);
		}
		
		ball.setX(ball.getAbsX() - camera.position.x + Gdx.graphics.getWidth() / 2.0f);
		ball.setY(ball.getAbsY() - camera.position.y + Gdx.graphics.getHeight() / 2.0f);
		
		for (int i = 0; i < gates.length; i++) {
			gates[i].setX(gates[i].getAbsX() - camera.position.x + Gdx.graphics.getWidth() / 2.0f);
			gates[i].setY(gates[i].getAbsY() - camera.position.y + Gdx.graphics.getHeight() / 2.0f);
		}
	}
	
	public void processGame() {
		
		if (Gdx.input.isKeyPressed(Input.Keys.F1)) camera.translate(10, 0, 0);
		if (Gdx.input.isKeyPressed(Input.Keys.F2)) camera.translate(-10, 0, 0);
		if (Gdx.input.isKeyPressed(Input.Keys.F3)) camera.translate(0, 10, 0);
		if (Gdx.input.isKeyPressed(Input.Keys.F4)) camera.translate(0, -10, 0);
		
		drawField();
		
		if (gameState == GameStates.RUN) {
			// Изменение силы ветра
			if (Math.random() > 0.995f) {
				float windVelocity = (float)Math.random() * 25 + 5;
				
				for (int i = 0; i < leafs.length; i++) {
					leafs[i].setWindVelocity(windVelocity);
					drops[i].setWindVelocity(windVelocity / 3);
				}
				
				// Если сила ветра больше 20 то воспроизводим звук вьюги
				if (leafs[0].windDirection != WindDirections.NONE && windVelocity > 20) 
					Sounds.play("wind02", true);
				// При изменении ветра воспроизводим звук ветра
				else if (leafs[0].windDirection != WindDirections.NONE && windVelocity > 10) 
					Sounds.play("wind01", true);
			}
			
			
			// Произвольное изменение направления ветра
			if (Math.random() > 0.993f) {
				int j = (int)Math.round(Math.random() * WindDirections.values().length);
				if (j >= WindDirections.values().length) j = WindDirections.values().length - 1;
				
				for (int i = 0; i < leafs.length; i++) {
					leafs[i].setWindDirection(WindDirections.values()[j]);
					drops[i].setWindDirection(WindDirections.values()[j]);
				}
			}
			
			// Отмечаем что был забит гол
			if (ball.ballInNet() && !GOAL_SCORED) {
				GOAL_SCORED = true;
				GOAL_SCORED_BY = ball.lastManagerByBlayer();
				GOAL_SCORED_GATES = ball.scoredInGates();
				
				ball.allowGravityFrom(999.0f);
				
				System.out.println("Последний коснувшийся мяча     : " + Players.getParams(ball.lastTouchedByBlayer()).stringName);
				System.out.println("Последний контролировавший мяч : " + Players.getParams(ball.lastManagerByBlayer()).stringName);
				System.out.println("Забито в ворота : " + ball.scoredInGates());
			}
			
			if (GOAL_SCORED)
			{
				for (Player player : players) {
					if (player.addictedTo == AddictedTo.AI) {
						player.setVelocityX(0);
						player.setVelocityY(0);
						player.disableDirections();
						
						// Если мяч забит в нужные для игрока ворота, то он радуется
						if (player.getDestinationGateId() == GOAL_SCORED_GATES) 
						{
							// Если устанавливается анимация празднования забитого гола для игрока забившего гол
							if (player.getPlayerId() == GOAL_SCORED_BY) 
							{
								if (player.Can(States.REJOICE2)) {
									player.Do(States.REJOICE2, true);
								}
							}
							// Анимация празднования остальных игроков 
							else if (player.Can(States.REJOICE1)) 
							{
								player.Do(States.REJOICE1, true);
							}
						}
						else 
						{
							int cry = MathUtils.random(1, 3);
							
							if (player.Can(States.valueOf("CRY"+String.valueOf(cry)))) {
								player.Do(States.valueOf("CRY"+String.valueOf(cry)), true);
							}
						}
					}
					// Анимация игрока человека
					else if (player.getAbsH() == 0 && player.maxVelocity() == 0) 
					{
						if (player.getDestinationGateId() == GOAL_SCORED_GATES) 
						{
							 if (player.Can(States.REJOICE4))
							 {
								 player.Do(States.REJOICE4, true);
							 }
						}
						else 
						{
							 if (player.Can(States.CRY1)) 
							 {
								 player.Do(States.CRY1, true);
							 }
						}
					}
				}
			}
			else
			{
				ai[0].play();
				ai[1].play();
			}
			
			// Сортировака спрайтов по глубине
			zIndexSorting();
			
			// Автоматическая озучка определенных действий
			loopSfxCheck();
			
			// Отслеживание столкновений
			detectCollisions();
		}
		
		// Пауза / продолжение игры
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			if (gameState == GameStates.RUN) {
				setPause();
			}
			else if (gameState == GameStates.PAUSE) {
				setResume();
			}
		}
		
		// Вывод FPS
//		batch.begin();
//		font.draw(batch, "FPS: " + Integer.valueOf(Gdx.graphics.getFramesPerSecond()).toString(), 10, Gdx.graphics.getHeight() - 10);
//		batch.end();
	}
	
	@Override
	public void act(float delta) {
		
		if (gameState == GameStates.RUN) {
			for (int i = 0; i < players.length; i++) {
				players[i].act(Gdx.graphics.getDeltaTime());
			}
			
			ball.act(Gdx.graphics.getDeltaTime());
			
			gates[0].act(Gdx.graphics.getDeltaTime());
			gates[1].act(Gdx.graphics.getDeltaTime());
		}
		
		for (int i = 0; i < leafs.length; i++) {
			leafs[i].act(Gdx.graphics.getDeltaTime());
		}
		for (int i = 0; i < drops.length; i++) {
			drops[i].act(Gdx.graphics.getDeltaTime());
		}
	}
	
	public void loopSfxCheck() {
		// Проверка, нужно ли включить проигрывание звука пробежки
		if (ball.isCatched()) {
			for (int i = 0; i < players.length; i++) {
				if (players[i].catchBall()) {
					if (players[i].curentState() == States.RUN) {
						Sounds.play("run01");
						Sounds.loop("run01", true);
					}
					else {
						Sounds.stop("run01");
						Sounds.loop("run01", false);
					}
				}
			}
		}
		else {
			Sounds.stop("run01");
			Sounds.loop("run01", false);
		}
		
		
		// Если мяч находится в поле
		if (this.inField(ball.getAbsX(),ball.getAbsY())) {
			ballOutPlayed = false;
		}
		else if (!this.inField(ball.getAbsX(),ball.getAbsY()) && !ballOutPlayed) {
			// Если мяч влетел в ворота
			if (ball.scoredInGates() > -1) {
				Sounds.play("goalin01", true);		
			}
			// Если мяч ушел за пределы поля вне ворот
			else {
				Sounds.play("ballout01", true);
			}
			ballOutPlayed = true;
		}
		
		// Отмечаем находится ли мяч в поле или вне его
		ball.inField(!ballOutPlayed);
	}
	
	public void zIndexSorting() {
		// Сортировка по глубине
		for (int i = 0; i < players.length; i++) {
			Z_POSITIONS.put(players[i].getPlayerId(), players[i].getY());
		}
		
		// Мяч
		Z_POSITIONS.put(PLAYERS_AMOUNT+1, ball.getY());
		
		// Ворота
		Z_POSITIONS.put(PLAYERS_AMOUNT+2, gates[0].getY());
		
		// Сортировка по глубине
		Z_POSITIONS = sortByValues(Z_POSITIONS);
		Z_INDEX = PLAYERS_AMOUNT + 2;
		for (Map.Entry<Integer, Float> entry : Z_POSITIONS.entrySet()) {
			if (entry.getKey() == PLAYERS_AMOUNT + 1)   	 // Мяч
				ball.setZIndex(Z_INDEX);
			else if (entry.getKey() == PLAYERS_AMOUNT + 2) { // Ворота
				gates[0].setZIndex(Z_INDEX);
				gates[1].setZIndex(Z_INDEX);
			}
			else 									 		 // If player
				players[entry.getKey()].setZIndex(Z_INDEX);
			
			Z_INDEX--;
		}
		
		// Если высота мяча больше высоты ворот, то мяч должен рисоваться поверх ворот
		if (ball.getAbsH() > gates[0].getHeight()) {
			ball.setZIndex(31);
		}
		
		// Поскольку для тени при устанавливается такой же z-index как и для класса в котором
		// она создавалась, то при каждой отрисовке нужно прятать тени спрайтов, перенося их
		// назад, устанавливая минимальный z-index
		hideShadows();
	}
	
	// Находится ли объект в пределах поля
	public boolean inField(float x, float y) {
		float offs = mGetSideLineProjection(y);
		
		return 
		    y + 8 > fieldOffsetY && 
		    y - 4 < fieldOffsetY + fieldHeight &&
		    x + 12 > fieldOffsetX + offs &&
			x - 12 < fieldOffsetX + fieldMaxWidth - offs;
	}
	
	// Отслеживание столкновений
	private void detectCollisions() {
		
		// Обработка столкновений производится лишь в случае когда мяч находится в поле
		if (this.inField(ball.getAbsX(),ball.getAbsY())) {
			// Если мяч попадает штангу или в перекладину
			if (
				(
					(ball.getVelocityX() < 0 && Math.abs(ball.getAbsY() - gates[0].getBottomBar().y) <= 10 && ball.getAbsX() < gates[0].getBottomBar().x + 10) || 
					(ball.getVelocityX() < 0 && Math.abs(ball.getAbsY() - gates[0].getTopBar().y) <= 10 && ball.getAbsX() < gates[0].getTopBar().x + 10)
				) 
				||
				(
					(ball.getVelocityX() > 0 && Math.abs(ball.getAbsY() - gates[1].getBottomBar().y) <= 10 && ball.getAbsX() > gates[1].getBottomBar().x - 10) || 
					(ball.getVelocityX() > 0 && Math.abs(ball.getAbsY() - gates[1].getTopBar().y) <= 10 && ball.getAbsX() > gates[1].getTopBar().x - 10)
				) 
				||
					// Проверка столкновения с перекладиной 
					(
						(Math.abs(ball.getAbsH() - gates[0].getHeight()) <= 10) && 
						(
							(ball.getVelocityX() < 0 && ball.getAbsX() < gates[0].getBottomBar().x+35 && ball.getAbsY() > gates[0].getBottomBar().y - 10 && ball.getAbsY() < gates[0].getTopBar().y + 10) ||
							(ball.getVelocityX() > 0 && ball.getAbsX() > gates[1].getBottomBar().x-35 && ball.getAbsY() > gates[1].getBottomBar().y - 10 && ball.getAbsY() < gates[1].getTopBar().y + 10)
						)
					)
				) 
			{
				// Меняем направление движения мяча на противоположное
				ball.setVelocityX(-ball.getVelocityX() * ball.getRestitution());
				
				// Переводим мяч в режим обычного полета (на случай если он летел после суперудара, когда гравитация на мяч отключена)
				ball.Do(Ball.States.FLY_MEDIUM, true);
				ball.allowGravityFrom(999.0f);
				ball.setJumpVelocity(0.0f);
				
				// Звук удара мяча о каркас ворот
				Sounds.play("balllanding02", true);
			}
		}
		
		
		// Проверка столкновений игроков, при выполнении ударов / подкатов (для отбора мяча)
		for (int i = 0; i < PLAYERS_AMOUNT - 1; i++) {
			
			for (int j = i + 1; j < PLAYERS_AMOUNT; j++) {
				
				// Проверка, не находятся ли игроки в одной и той же моканде
				if (players[i].getTeamId() != players[j].getTeamId()) {
					
					float distanceX = Math.abs(players[i].getAbsX() - players[j].getAbsX());
					float distanceY = Math.abs(players[i].getAbsY() - players[j].getAbsY());
					
					if (distanceX < 65 && distanceY < 35) {
						
						// Игрок i атакует игрока j
						if (attackStates.indexOf(players[i].curentState()) >= 0 && attackStates.indexOf(players[j].curentState()) == -1) {
							attackPlayerByPlayer(j,i);
						}
						// Игрок j атакует игрока i
						else if (attackStates.indexOf(players[j].curentState()) >= 0 && attackStates.indexOf(players[i].curentState()) == -1) {
							attackPlayerByPlayer(i,j);
						}
						// Игрок i атакует игрока j, игрок j атакует игрока i
						else if (attackStates.indexOf(players[i].curentState()) >= 0 && attackStates.indexOf(players[j].curentState()) >= 0) {
							attackPlayerByPlayer(j,i);
							attackPlayerByPlayer(i,j);
						}
					}
				}
			}
		}
	}
	
	public void attackPlayerByPlayer(int playerA, int playerB) {
		if (players[playerA].Can(States.DEAD)) {
			players[playerA].Do(States.DEAD, true);
			
			Sounds.play("hit01", true);
			
			// Если это не подкат, то откидывает игрока, которого атаковали
			if (players[playerB].curentState() != States.TACKLE_ATTACK) {
				players[playerA].setJumpVelocity(4.0f);
				players[playerA].setVelocityX(players[playerB].getVelocityX() / 2.0f);
				players[playerA].setVelocityY(players[playerB].getVelocityY() / 2.0f);
			}
		}
	}
	
	// Перечень атакующих действий
	public static ArrayList<States> attackStates = new ArrayList<States>(
		Arrays.asList(
			States.BODY_ATTACK,
			States.TACKLE_ATTACK,
			States.FISH_KICK,
			States.DRIBBLING_DOWN,
			States.DRIBBLING_UP,
			States.WHIRLIGIG_KICK,
			States.FOOT_ATTACK
		)
	); 
	
	// Переносим тени от персонажей в самую глубину чтобы не перекрывать спрайты
	private void hideShadows() {
		ball.shadow.setZIndex(0);
		for (int i = 0; i < players.length; i++) {
			players[i].shadow.setZIndex(0);
		}
	}
	
	// Ставит игру на паузу
	public void setPause() {
		gameState = GameStates.PAUSE;
		
		// Останавливаем все звуки, кроме того, что передано в except
		Sounds.stopAll("bg");
		//Sounds.pause(BG_TRACK);
		Sounds.volume("bg", 0.3f);
		
		Sounds.play("ballout01");
	}
	
	// Возобновляет игру
	public void setResume() {
		gameState = GameStates.RUN;
		Sounds.play("ballout01");
		//Sounds.resume(BG_TRACK);
		Sounds.volume("bg", 1.0f);
		
		// Если в меню было выбрано "сдаться"
		if (CrazySoccer.menus.get("give_up").getActive().getId() == 1) {
			// Переключение екрана
			GameScreen.setScreen(ScreenNames.PREPORATIONS);
		}
		CrazySoccer.menus.get("give_up").reset();
	}

	@Override
	public void dispose() {
		
		Sounds.unload("bg");
		Sounds.unload("pass01");
		Sounds.unload("kick01");
		Sounds.unload("run01");
		Sounds.unload("landing01");
		Sounds.unload("catchball01");
		Sounds.unload("jump01");
		Sounds.unload("whirligid01");
		Sounds.unload("dribbling01");
		Sounds.unload("tackle01");
		Sounds.unload("hit01");
		Sounds.unload("wind01");
		Sounds.unload("wind02");
		Sounds.unload("balllanding02");
		Sounds.unload("ballout01");
		Sounds.unload("goalin01");
		
		sprites.dispose();
		
		ball.remove();
		
		for (int i = 0; i < players.length; i++) 
			players[i].remove();
		
		for (int i = 0; i < gates.length; i++) 
			gates[i].remove();
		
		for (int i = 0; i < leafs.length; i++) 
			leafs[i].remove();
			
		for (int i = 0; i < drops.length; i++) 
			drops[i].remove();
		
		fieldMap.dispose();
		
		CrazySoccer.shapeRenderer.setProjectionMatrix(CrazySoccer.shapeProjectionMatrix);
	}
	
	@Override
	public boolean keyUp(int keycode) {
		switch (keycode)
		{
			// Кнопки управления первым игроком
			case Keys.UP: //UP
				actions.remove(Actions.Controls.UP, 0);
			break;
			
			case Keys.DOWN: //DOWN
				actions.remove(Actions.Controls.DOWN, 0);
			break;
				
			case Keys.LEFT: //LEFT
				actions.remove(Actions.Controls.LEFT, 0);
			break;
				
			case Keys.RIGHT: //RIGHT
				actions.remove(Actions.Controls.RIGHT, 0);
			break;
			
			case Keys.Q:
				actions.remove(Actions.Controls.ACTION1, 0);
			break;
			
			case Keys.W:
				actions.remove(Actions.Controls.ACTION2, 0);
			break;
			
			case Keys.E:
				actions.remove(Actions.Controls.ACTION3, 0);
			break;
			
			
			case Keys.A:
				actions.remove(Actions.Controls.ACTION1, 9);
			break;
			
			case Keys.S:
				actions.remove(Actions.Controls.ACTION2, 9);
			break;
			
			case Keys.D:
				actions.remove(Actions.Controls.ACTION3, 9);
			break;
			
			case Keys.NUMPAD_4:
				actions.remove(Actions.Controls.LEFT, 9);
			break;
			
			case Keys.NUMPAD_6:
				actions.remove(Actions.Controls.RIGHT, 9);
			break;
			
			case Keys.NUMPAD_5:
				actions.remove(Actions.Controls.DOWN, 9);
			break;
			
			case Keys.NUMPAD_8:
				actions.remove(Actions.Controls.UP, 9);
			break;
		}
		
		return false;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		
		switch (keycode)
		{
			// Кнопки управления первым игроком
			case Keys.UP: //UP
				actions.add(Actions.Controls.UP, 0);
			break;
			
			case Keys.DOWN: //DOWN
				actions.add(Actions.Controls.DOWN, 0);
			break;
				
			case Keys.LEFT: //LEFT
				actions.add(Actions.Controls.LEFT, 0);
			break;
				
			case Keys.RIGHT: //RIGHT
				actions.add(Actions.Controls.RIGHT, 0);
			break;
			
			case Keys.Q:
				actions.add(Actions.Controls.ACTION1, 0);
			break;
			
			case Keys.W:
				actions.add(Actions.Controls.ACTION2, 0);
			break;
			
			case Keys.E:
				actions.add(Actions.Controls.ACTION3, 0);
			break;
			
			
			// Кнопки управления вторым игроком
			case Keys.A:
				actions.add(Actions.Controls.ACTION1, 9);
			break;
			
			case Keys.S:
				actions.add(Actions.Controls.ACTION2, 9);
			break;
			
			case Keys.D:
				actions.add(Actions.Controls.ACTION3, 9);
			break;
			
			case Keys.NUMPAD_4:
				actions.add(Actions.Controls.LEFT, 9);
			break;
			
			case Keys.NUMPAD_6:
				actions.add(Actions.Controls.RIGHT, 9);
			break;
			
			case Keys.NUMPAD_5:
				actions.add(Actions.Controls.DOWN, 9);
			break;
			
			case Keys.NUMPAD_8:
				actions.add(Actions.Controls.UP, 9);
			break;
		}
//		actions.debug();
		return false;
	}
	
	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		 players[0].direction = (players[0].direction == Directions.LEFT) ? Directions.RIGHT : Directions.LEFT;
		 players[0].Do(States.RUN, true);
		 
		 return false;
	}
}