package com.mygdx.crazysoccer;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.crazysoccer.Player.Directions;
import com.mygdx.crazysoccer.Player.States;
import com.mygdx.crazysoccer.Wind.WindDirections;

public class Field extends Stage {
	
	// Количество игроков на поле
	private final int PLAYERS_AMOUNT = 5;
	
	// Служебные переменные
	private int Z_INDEX;
	private boolean ballOutPlayed = false;
	
	private HashMap<Integer,Float> Z_POSITIONS = new HashMap<Integer,Float>();
	
	// Текстура для хранения спрайтов
	public static Texture sprites;
	
	// Екземпляр мяча
	private Ball ball;
	
	// Ворота
	public Gate[] gates = new Gate[2];
	
	// Екземпляр класса описывающего игрока
	public Player[] players = new Player[PLAYERS_AMOUNT];
	
	// Листья
	private Leaf[] leafs = new Leaf[10];
	
	// Капли
	private Drop[] drops = new Drop[10];
	
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
	
	public TiledMap fieldMap;
    public TiledMapRenderer fieldMapRenderer;
    public static OrthographicCamera camera;
    
    public ShapeRenderer shapeRenderer;
	
    // Сохранение нажатых клавиш и их времени
    public Actions actions = new Actions();
    
    // Класс для работы со звуком
	public Sounds sounds;
    
	public Field(ScreenViewport screenViewport) {
		super(screenViewport);
		
		//Загрузка музыки и звуковых эффектов  *
		loadSounds();
		
		// Загрузка спрайтов
		sprites = new Texture(Gdx.files.internal("sprites.png"));
		
		// Создание мяча
		ball = new Ball();
		ball.setActionsListener(actions);
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
		for (int i = 0; i < players.length; i++) {
			// Создание игрока
			players[i] = new Player(i);
			// Привязка слушателя ввода для игрока
			players[i].setActionsListener(actions);
			// Добавление игрока (актера) на сцену (поле)
			this.addActor(players[i]);
			this.addActor(players[i].shadow);
			
			players[i].attachField(this);
			players[i].attachBall(ball);
		}
		
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
		
		// Для отрисовки линий поля
		shapeRenderer = new ShapeRenderer();
		
		// Создание камеры
		camera = new OrthographicCamera(Vars.WINDOW_WIDTH, Vars.WINDOW_HEIGHT);
        camera.update();
	}
	
	private void loadSounds() {
		// Класс для работы с музыкой и звуковыми эффектами
		sounds = new Sounds();
		
		// Загрузка фоновой музыки
		sounds.load("bg01", "sound/bg/background01.ogg");
		sounds.play("bg01");
		sounds.loop("bg01", true);
		
		// Звук паса
		sounds.load("pass01", "sound/sfx/pass01.ogg");
		
		// Звук полята мяча после удара
		sounds.load("kick01", "sound/sfx/kick01.ogg");
		
		// Звук пробежки
		sounds.load("run01", "sound/sfx/run01.ogg");
		
		// Звук приземления игрока
		sounds.load("landing01", "sound/sfx/landing01.ogg");
		
		// Звук приема мяча полевым игроком
		sounds.load("catchball01", "sound/sfx/catchball01.ogg");
		
		// Звук начала прыжка
		sounds.load("jump01", "sound/sfx/jump01.ogg");
		
		// Звук несильного ветра
		sounds.load("wind01", "sound/sfx/wind01.ogg");
		
		// Звук сильного ветра
		sounds.load("wind02", "sound/sfx/wind02.ogg");
		
		// Звук удара мяча о поле
		sounds.load("balllanding02", "sound/sfx/balllanding02.ogg");
		
		// Звук выхода мяча за пределы поля
		sounds.load("ballout01", "sound/sfx/whistle01.ogg");
		
		// Звук сигнала о забитом голе
		sounds.load("goalin01", "sound/sfx/goalin01.ogg");
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
        		
        		// Ширина игрового мира. Подсчет ведется с учетом того, что смещение справа такое же как и слева
        		this.worldWidth = this.CELLS_X * 32;
        		this.worldHeight = this.CELLS_Y * 32;
        		
        		// Определение наибольшей координаты X, в которую можно смещать камеру     
        		this.camMaxX = this.worldWidth - Gdx.graphics.getWidth() / 2.0f;
        		this.camMaxY = this.worldHeight - Gdx.graphics.getHeight() / 2.0f;
        		
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
		 shapeRenderer.setProjectionMatrix(camera.combined);
		 shapeRenderer.begin(ShapeType.Line);
		 shapeRenderer.setColor(1, 1, 1, 1);
		 
		 // Рамка поля
		 shapeRenderer.line(leftBottom.x + fieldOffsetX, leftBottom.y + fieldOffsetY, rightBottom.x + fieldOffsetX, rightBottom.y + fieldOffsetY);
		 shapeRenderer.line(rightBottom.x + fieldOffsetX, rightBottom.y + fieldOffsetY, rightTop.x + fieldOffsetX, rightTop.y + fieldOffsetY);
		 shapeRenderer.line(rightTop.x + fieldOffsetX, rightTop.y + fieldOffsetY, leftTop.x + fieldOffsetX, leftTop.y + fieldOffsetY);
		 shapeRenderer.line(leftTop.x + fieldOffsetX, leftTop.y + fieldOffsetY, leftBottom.x + fieldOffsetX, leftBottom.y + fieldOffsetY);
		 
		 // Центр поля
		 shapeRenderer.line(leftBottom.x + fieldMaxWidth / 2.0f + fieldOffsetX, leftBottom.y + fieldOffsetY, leftBottom.x + fieldMaxWidth / 2.0f + fieldOffsetX, leftTop.y + fieldOffsetY);
		 
		 // Круг в центре поля
		 shapeRenderer.circle(leftBottom.x + fieldMaxWidth / 2.0f + fieldOffsetX, fieldHeight / 2.0f + fieldOffsetY, 200);
		 
		 // Кружки для подачи угловых
		 shapeRenderer.arc(leftBottom.x + fieldOffsetX, leftBottom.y + fieldOffsetY, 60, 0, 82);
		 shapeRenderer.arc(leftTop.x + fieldOffsetX, leftTop.y + fieldOffsetY, 60, -97, 97);
		 shapeRenderer.arc(rightTop.x + fieldOffsetX, rightTop.y + fieldOffsetY, 60, 180, 97);
		 shapeRenderer.arc(rightBottom.x + fieldOffsetX, rightBottom.y + fieldOffsetY, 60, 97, 82);
		 
		 // Левые ворота
		 float yCenter = leftBottom.y + fieldHeight / 2.0f + fieldOffsetY;
		 shapeRenderer.line(leftBottom.x + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter + innerBoxHeight)), yCenter + innerBoxHeight, innerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter + innerBoxHeight)), yCenter + innerBoxHeight);
		 shapeRenderer.line(leftBottom.x + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter - innerBoxHeight)), yCenter - innerBoxHeight, innerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter - innerBoxHeight)), yCenter - innerBoxHeight);
		 shapeRenderer.line(innerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter + innerBoxHeight)), yCenter + innerBoxHeight, innerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter - innerBoxHeight)), yCenter - innerBoxHeight);
		 shapeRenderer.line(leftBottom.x + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter + outerBoxHeight)), yCenter + outerBoxHeight, outerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter + outerBoxHeight)), yCenter + outerBoxHeight);
		 shapeRenderer.line(leftBottom.x + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter - outerBoxHeight)), yCenter - outerBoxHeight, outerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter - outerBoxHeight)), yCenter - outerBoxHeight);
		 shapeRenderer.line(outerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter + outerBoxHeight)), yCenter + outerBoxHeight, outerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter - outerBoxHeight)), yCenter - outerBoxHeight);
		 shapeRenderer.arc(outerBoxWidth + fieldOffsetX + mGetSideLineProjection(Math.round(yCenter)), yCenter, 100, 263f, 180);
		 
		 // Правые ворота
		 shapeRenderer.line(rightBottom.x - mGetSideLineProjection(Math.round(yCenter + innerBoxHeight)) + fieldOffsetX, yCenter + innerBoxHeight, rightBottom.x - mGetSideLineProjection(Math.round(yCenter + innerBoxHeight)) - innerBoxWidth + fieldOffsetX, yCenter + innerBoxHeight);
		 shapeRenderer.line(rightBottom.x - mGetSideLineProjection(Math.round(yCenter - innerBoxHeight)) + fieldOffsetX, yCenter - innerBoxHeight, rightBottom.x - mGetSideLineProjection(Math.round(yCenter - innerBoxHeight)) - innerBoxWidth + fieldOffsetX, yCenter - innerBoxHeight);
		 shapeRenderer.line(rightBottom.x - mGetSideLineProjection(Math.round(yCenter + innerBoxHeight)) - innerBoxWidth + fieldOffsetX, yCenter + innerBoxHeight, rightBottom.x - mGetSideLineProjection(Math.round(yCenter - innerBoxHeight)) - innerBoxWidth + fieldOffsetX, yCenter - innerBoxHeight);
		 shapeRenderer.line(rightBottom.x - mGetSideLineProjection(Math.round(yCenter + outerBoxHeight)) + fieldOffsetX, yCenter + outerBoxHeight, rightBottom.x - mGetSideLineProjection(Math.round(yCenter + outerBoxHeight)) - outerBoxWidth + fieldOffsetX, yCenter + outerBoxHeight);
		 shapeRenderer.line(rightBottom.x - mGetSideLineProjection(Math.round(yCenter - outerBoxHeight)) + fieldOffsetX, yCenter - outerBoxHeight, rightBottom.x - mGetSideLineProjection(Math.round(yCenter - outerBoxHeight)) - outerBoxWidth + fieldOffsetX, yCenter - outerBoxHeight);
		 shapeRenderer.line(rightBottom.x - mGetSideLineProjection(Math.round(yCenter + outerBoxHeight)) - outerBoxWidth + fieldOffsetX, yCenter + outerBoxHeight, rightBottom.x - mGetSideLineProjection(Math.round(yCenter - outerBoxHeight)) - outerBoxWidth + fieldOffsetX, yCenter - outerBoxHeight);
		 shapeRenderer.arc(rightBottom.x - mGetSideLineProjection(Math.round(yCenter)) - outerBoxWidth + fieldOffsetX, yCenter, 100, 97f, 180);
		 
		 shapeRenderer.end();
		 
		 // Точка в центрe поля
		 shapeRenderer.begin(ShapeType.Filled);
		 shapeRenderer.circle(fieldOffsetX + fieldMaxWidth / 2.0f, fieldOffsetY + fieldHeight / 2.0f, 15);
		 shapeRenderer.end();
	}
	
	// Расположение игроков по полю
	public void actorsArrangement() {
		ball.setAbsX(worldWidth / 2.0f);
		ball.setAbsY(worldHeight / 2.0f);
		
		players[0].setAbsX(worldWidth / 2.0f);
		players[0].setAbsY(fieldOffsetY + fieldHeight / 2.0f);
		
		players[1].setAbsX(600);
		players[1].setAbsY(500);
		
		players[2].setAbsX(600);
		players[2].setAbsY(worldHeight - 500);
		
		players[3].setAbsX(worldWidth / 2.0f - 350);
		players[3].setAbsY(500);
		
		players[4].setAbsX(worldWidth / 2.0f - 350);
		players[4].setAbsY(worldHeight - 500);
		
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
		drawField();
		
		// Изменение силы ветра
		if (Math.random() > 0.995f) {
			float windVelocity = (float)Math.random() * 25 + 5;
			
			for (int i = 0; i < leafs.length; i++) {
				leafs[i].setWindVelocity(windVelocity);
				drops[i].setWindVelocity(windVelocity / 3);
			}
			
			// Если сила ветра больше 20 то воспроизводим звук вьюги
			if (leafs[0].windDirection != WindDirections.NONE && windVelocity > 20) 
				sounds.play("wind02", true);
			// При изменении ветра воспроизводим звук ветра
			else if (leafs[0].windDirection != WindDirections.NONE && windVelocity > 10) 
				sounds.play("wind01", true);
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
		
		// Сортировака спрайтов по глубине
		zIndexSorting();
		
		// Автоматическая озучка определенных действий
		loopSfxCheck();
		
		// Отслеживание столкновений
		detectCollisions();
	}
	
	public void loopSfxCheck() {
		// Проверка, нужно ли включить проигрывание звука пробежки
		if (ball.isCatched()) {
			for (int i = 0; i < players.length; i++) {
				if (players[i].catchBall()) {
					if (players[i].curentState() == States.RUN) {
						sounds.play("run01");
						sounds.loop("run01", true);
					}
					else {
						sounds.stop("run01");
						sounds.loop("run01", false);
					}
				}
			}
		}
		else {
			sounds.stop("run01");
			sounds.loop("run01", false);
		}
		
		
		// Если мяч вышел за пределы игрового поля
		if (this.inField(ball.getAbsX(),ball.getAbsY())) {
			ballOutPlayed = false;
		}
		else if (!this.inField(ball.getAbsX(),ball.getAbsY()) && !ballOutPlayed) {
			// Если мяч влетел в ворота
			if (ball.isGoalIn() > 0) {
				sounds.play("goalin01", true);
			}
			// Если мяч ушел за пределы поля вне ворот
			else {
				sounds.play("ballout01", true);
			}
			ballOutPlayed = true;
		}
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
				ball.setVelocityX(-ball.getVelocityX());
				
				// Звук удара мяча о каркас ворот
				sounds.play("balllanding02", true);
			}
		}
	}
	
	// Переносим тени от персонажей в самую глубину чтобы не перекрывать спрайты
	private void hideShadows() {
		ball.shadow.setZIndex(0);
		for (int i = 0; i < players.length; i++) {
			players[i].shadow.setZIndex(0);
		}
	}

	public void dispose() {
		this.dispose();
	}
	
	@Override
	public boolean keyUp(int keycode) {
		switch (keycode)
		{
			// Кнопки управления первым игроком
			case Keys.UP: //UP
				actions.remove(Actions.Action.UP_1);
			break;
			
			case Keys.DOWN: //DOWN
				actions.remove(Actions.Action.DOWN_1);
			break;
				
			case Keys.LEFT: //LEFT
				actions.remove(Actions.Action.LEFT_1);
			break;
				
			case Keys.RIGHT: //RIGHT
				actions.remove(Actions.Action.RIGHT_1);
			break;
			
			case Keys.Q:
				actions.remove(Actions.Action.ACTION1_1);
			break;
			
			case Keys.W:
				actions.remove(Actions.Action.ACTION2_1);
			break;
			
			case Keys.E:
				actions.remove(Actions.Action.ACTION3_1);
			break;
			
			
			
			case Keys.A:
				actions.remove(Actions.Action.ACTION1_2);
			break;
			
			case Keys.S:
				actions.remove(Actions.Action.ACTION2_2);
			break;
			
			case Keys.D:
				actions.remove(Actions.Action.ACTION3_2);
			break;
			
			case Keys.NUMPAD_4:
				actions.remove(Actions.Action.LEFT_2);
			break;
			
			case Keys.NUMPAD_6:
				actions.remove(Actions.Action.RIGHT_2);
			break;
			
			case Keys.NUMPAD_5:
				actions.remove(Actions.Action.DOWN_2);
			break;
			
			case Keys.NUMPAD_8:
				actions.remove(Actions.Action.UP_2);
			break;
			
			
			
			case Keys.Z:
				actions.remove(Actions.Action.ACTION1_3);
			break;
			
			case Keys.X:
				actions.remove(Actions.Action.ACTION2_3);
			break;
			
			case Keys.C:
				actions.remove(Actions.Action.ACTION3_3);
			break;
			
			case Keys.NUM_1:
				actions.remove(Actions.Action.LEFT_3);
			break;
			
			case Keys.NUM_2:
				actions.remove(Actions.Action.RIGHT_3);
			break;
			
			case Keys.NUM_3:
				actions.remove(Actions.Action.DOWN_3);
			break;
			
			case Keys.NUM_4:
				actions.remove(Actions.Action.UP_3);
			break;
		}
		
//		actions.debug();
		return false;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		
		switch (keycode)
		{
			// Кнопки управления первым игроком
			case Keys.UP: //UP
				actions.add(Actions.Action.UP_1);
			break;
			
			case Keys.DOWN: //DOWN
				actions.add(Actions.Action.DOWN_1);
			break;
				
			case Keys.LEFT: //LEFT
				actions.add(Actions.Action.LEFT_1);
			break;
				
			case Keys.RIGHT: //RIGHT
				actions.add(Actions.Action.RIGHT_1);
			break;
			
			case Keys.Q:
				actions.add(Actions.Action.ACTION1_1);
			break;
			
			case Keys.W:
				actions.add(Actions.Action.ACTION2_1);
			break;
			
			case Keys.E:
				actions.add(Actions.Action.ACTION3_1);
			break;
			
			
			// Кнопки управления вторым игроком
			case Keys.A:
				actions.add(Actions.Action.ACTION1_2);
			break;
			
			case Keys.S:
				actions.add(Actions.Action.ACTION2_2);
			break;
			
			case Keys.D:
				actions.add(Actions.Action.ACTION3_2);
			break;
			
			case Keys.NUMPAD_4:
				actions.add(Actions.Action.LEFT_2);
			break;
			
			case Keys.NUMPAD_6:
				actions.add(Actions.Action.RIGHT_2);
			break;
			
			case Keys.NUMPAD_5:
				actions.add(Actions.Action.DOWN_2);
			break;
			
			case Keys.NUMPAD_8:
				actions.add(Actions.Action.UP_2);
			break;
			
			
			// Кнопки управления вторым игроком
			case Keys.Z:
				actions.add(Actions.Action.ACTION1_3);
			break;
			
			case Keys.X:
				actions.add(Actions.Action.ACTION2_3);
			break;
			
			case Keys.C:
				actions.add(Actions.Action.ACTION3_3);
			break;
			
			case Keys.NUM_1:
				actions.add(Actions.Action.LEFT_3);
			break;
			
			case Keys.NUM_2:
				actions.add(Actions.Action.RIGHT_3);
			break;
			
			case Keys.NUM_3:
				actions.add(Actions.Action.DOWN_3);
			break;
			
			case Keys.NUM_4:
				actions.add(Actions.Action.UP_3);
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