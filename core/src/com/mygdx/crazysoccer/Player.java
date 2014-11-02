package com.mygdx.crazysoccer;

import java.util.Map;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion; 
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.crazysoccer.Actions.Controls;

public class Player extends Actor {
	
	private Field field;
	private Ball ball;
	
	// Идентификатор игрока (используется при определении нажатия управляющих кнопок)
	private int PLAYER_ID = 0;
	
	// Идентификатор ворот по которым будет наносить удар игрок
	private int DESTINATION_GATE_ID = 0;
		
	private static final int FRAME_COLS = 8;    
    private static final int FRAME_ROWS = 8;
    
    // Спрайт для отрисовки персонажа
    public SpriteBatch spriteBatch;
    
    // Параметры спрайта
    private int SPRITE_WIDTH = 32;
    private int SPRITE_HEIGHT = 32;
    private float SPRITE_SCALE = 3.0f;
    
    // Размеры игрока с учетом масштабирования
    private float PLAYER_WIDTH = SPRITE_SCALE * SPRITE_WIDTH;
    private float PLAYER_HEIGHT = SPRITE_SCALE * SPRITE_HEIGHT;
    
    // Текущая высота прыжка персонажа
    private float JUMP_HEIGHT = 0;
    
    // Масса персонажа
    private float MASS = 62.0f;
    
    // Сила персонажа
    private float STRENGTH = 210.0f;
    
    // Сила удара мяча
    private float KICK_STRENGTH = 50.0f;
    
    // Текущий показатель коэффициента трения игрока
    private float FRICTION = -0.50f;
    
    // Коэффициент трения игрока о газон
    private float GRASS_FRICTION = -0.25f;
    
    // Коэффициент трения игрока в луже
    private float WATER_FRICTION = -0.03f;
    
    // Текущая скорость движения персонажа при прыжке 
    private float JUMP_VELOCITY = 0.0f;
    
    // Флаг хранящий использовал ли игрок лимит в одно действие в полете (удар/пас/вертушка и т.д.)
    private boolean ACTION_DONE = false;
    
    // Параметры перемещения персонажа
    private float CURENT_SPEED_X = 0.0f;
    private float CURENT_SPEED_Y = 0.0f;
    private float WALKING_SPEED = 3.5f;
    private float RUN_SPEED = 6.0f;
    private float TOP_RUN_SPEED = 9.0f;
    private float POS_X = 0.0f;
    private float POS_Y = 0.0f;
    
    // Флаг принимает значение true если игрок контролирует мяч
    private boolean CATCH_BALL = false;
    
	// Перечень возможных состояний героя
	public static enum States {
		STAY,     			// Состояние покоя
		WALKING,			// Ходьба
		RUN,	  			// Бег 
		TOP_RUN,	  		// Супер бег 
		DECELERATION,	    // Торможение
		KNEE_CATCH,			// Прием мяча ногой
		CHEST_CATCH,		// Прием мяча грудью
		FOOT_KICK, 			// Удар ногой
		HEAD_KICK,			// Удар головой
		BACK_KICK,			// Удар через себя
		FISH_KICK,			// Удар рыбкой (в полете животом вниз)
		WHIRLIGIG_KICK,		// Удар вертушкой (юла)
		JUMP,				// Прыжок
		SIT,				// Присел
		PASS,				// Пасс
		HEAD_PASS,			// Пасс головой
		DEAD,				// Убит
		CATCH_BALL,			// Прием мяча
		LAY_BACK,			// Лежание на спине
		LAY_BELLY			// Лежание на животе
	} 
	
	
	// В какую сторону повернут персонаж
	public static enum Directions {
		RIGHT, LEFT
	}
	// Направление персонажа
	public Directions direction;
	
	// Слушатель ввода
	private Actions actionsListener;
    
    // Набор анимаций персонажа
    public Map<States, Animation> animations;
    
	// Текущий кадр анимации
	public TextureRegion currentFrame; 
	
	// Тень персонажа
	public Shadow shadow;
	
	public Texture animationSheet;
	public Texture statesSheet;
	public TextureRegion[][] animationMap;
	public TextureRegion[][] animationStatesMap;
    
	// Текущее состояние героя
	public Map<States, Boolean> state = new HashMap<States, Boolean>();
	
    public float stateTime = 0.0f; 
	
	public Player(int playerId) {
		super();
		
		this.PLAYER_ID = playerId;
		
		animations = new HashMap<States, Animation>();
		
        // Первоначальная инициализация состояний анимаций персонажа
        Do(States.STAY, true);
        
        // Изначальное направление персонажа
        direction = Directions.RIGHT;
		
        // Загрузка изображения с анимацией персонажа
        animationSheet = new Texture(Gdx.files.internal("kunio.png"));
        
        // Загрузка изображения с анимациями состояний
        statesSheet = new Texture(Gdx.files.internal("states.png"));
        
        // Загрузка карты анимаций персонажа
        animationMap = TextureRegion.split(animationSheet, animationSheet.getWidth()/FRAME_COLS, animationSheet.getHeight()/FRAME_ROWS);
        
        // Загрузка карты анимаций состояний игрока
        animationStatesMap = TextureRegion.split(statesSheet, statesSheet.getWidth()/FRAME_COLS, statesSheet.getHeight()/FRAME_ROWS);
        
        // Спрайт для отрисовки персонажа
        spriteBatch = new SpriteBatch(); 
        
        // Создаем анимацию покоя
        animations.put(States.STAY, 
    		new Animation(1.0f, 
				animationMap[0][0]
			)
        );
        
        // Создаем анимацию ходьбы
        animations.put(States.WALKING, 
    		new Animation(0.14f, 
				animationMap[0][2],
				animationMap[0][1],
				animationMap[0][2],
				animationMap[0][0]
			)
		);
        
        // Создаем анимацию ползания
//        animations.put(States.CRAWLING, 
//    		new Animation(0.25f, 
//				animationMap[1][2], 
//				animationMap[1][3]
//			)
//		);
        
        // Создаем анимацию бега
        animations.put(States.RUN, 
    		new Animation(0.13f, 
				animationMap[1][4], 
				animationMap[0][0]
			)
        );
        
        // Создаем анимацию супер пробежки
        animations.put(States.TOP_RUN, 
    		new Animation(0.07f, 
				animationMap[3][1], 
				animationMap[3][2], 
				animationMap[3][1], 
				animationMap[3][2], 
				animationMap[3][1], 
				animationMap[3][2], 
				animationMap[3][2]
			)
		);
        
        // Создаем анимацию торможения
        animations.put(States.DECELERATION, 
    		new Animation(1.0f, 
				animationMap[2][3]
			)
        );
        
        // Создаем анимацию приседания
        animations.put(States.SIT, 
    		new Animation(0.20f, 
				animationMap[0][3]
			)
		);
        
        // Создаем анимацию паса
        animations.put(States.PASS, 
    		new Animation(0.08f, 
				animationMap[0][5], 
				animationMap[0][5],
				animationMap[0][5],
				animationMap[0][6],
				animationMap[0][6],
				animationMap[0][6]
			)
        );
        
        // Создаем анимацию паса
        animations.put(States.HEAD_PASS, 
    		new Animation(0.25f, 
				animationMap[1][6],
				animationMap[1][7],
				animationMap[1][7]
			)
        );
        
        // Создаем анимацию приема мяча ногой
        animations.put(States.KNEE_CATCH, 
    		new Animation(0.3f, 
				animationMap[1][5]
			)
        );
        
        // Создаем анимацию приема мяча грудью
        animations.put(States.CHEST_CATCH, 
    		new Animation(0.4f, 
				animationMap[1][6]
			)
        );
        
        animations.put(States.FOOT_KICK, 
    		new Animation(0.05f, 
	    		animationMap[0][0], 
	    		animationMap[0][7],
	    		animationMap[0][7],
	    		animationMap[0][7],
	    		animationMap[1][0],
	    		animationMap[1][1],
	    		animationMap[1][1],
	    		animationMap[1][1],
	    		animationMap[1][1],
	    		animationMap[1][1],
	    		animationMap[1][1]
			)
        );
        
        animations.put(States.FISH_KICK, 
    		new Animation(0.35f, 
	    		animationMap[2][2], 
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3],
	    		animationMap[1][3]
			)
        );
        
        animations.put(States.WHIRLIGIG_KICK, 
			new Animation(0.08f, 
				animationMap[1][0], 
				animationMap[1][1], 
				animationMap[1][0],
				animationMap[1][1],
				animationMap[1][0],
				animationMap[1][0], 
				animationMap[1][1], 
				animationMap[1][0],
				animationMap[1][1],
				animationMap[1][0],
				animationMap[1][0], 
				animationMap[1][1], 
				animationMap[1][0],
				animationMap[1][1],
				animationMap[1][0]
			)
		);
        
        animations.put(States.BACK_KICK, 
    		new Animation(0.10f,
				animationMap[0][4], 
				animationMap[0][4], 
				animationMap[1][6], 
				animationMap[2][4], 
				animationMap[0][5], 
				animationMap[0][6], 
				animationMap[2][4], 
				animationMap[0][4],
				animationMap[0][4]
			)
		);
        
        animations.put(States.HEAD_KICK, 
    		new Animation(0.23f, 
	    		animationMap[1][6], 
	    		animationMap[1][7], 
	    		animationMap[1][7],
	    		animationMap[1][7]
			)
        );
        
        // Создаем анимацию прыжка
        animations.put(States.JUMP, 
    		new Animation(0.2f, 
				animationMap[0][4]
			)
		);
        
        // Создаем анимацию ударенного мячом игрока
        animations.put(States.DEAD, 
    		new Animation(10.0f, 
				animationStatesMap[0][0]
			)
        );
        
        // Создаем анимацию игрока лежащего на спине
        animations.put(States.LAY_BACK, 
    		new Animation(3.0f, 
				animationStatesMap[0][1]
			)
        );
        
        // Создаем анимацию игрока лежащего на животе
        animations.put(States.LAY_BELLY, 
    		new Animation(3.0f, 
				animationStatesMap[0][2]
			)
        );
        
        shadow = new Shadow();
	}
	
	public void attachField(Field f) {
		this.field = f;
	}
	
	public void attachBall(Ball b) {
		this.ball = b;
	}
	
	public int getPlayerId() {
		return this.PLAYER_ID;
	}
	
	// Получение идентификатора ворот по которым игрок должен наносить удар
	public int getDestinationGateId() {
		return this.DESTINATION_GATE_ID;
	}
	
	// Установка идентификатора ворот по которым игрок должен наносить удар
	public void setDestinationGateId(int gateId) {
		this.DESTINATION_GATE_ID = gateId;
	}
	
	public float width() {
		return this.SPRITE_SCALE * this.SPRITE_WIDTH;
	}
	
	private int frameOffsetX() {
		int offset = 0;
		
		// Текущий кадр анимации
		int cf = currentFrame(); 
		
		switch (curentState()) {
		
			case WHIRLIGIG_KICK:
				offset = (cf == 3 || cf == 4 || cf == 8 || cf == 9 || cf == 13 || cf == 14) ? -20 : 0;
			break;
			
			default:
				offset = 0;
			break;
		}
		
		return offset;
	}
	
	private int frameOffsetY() {
		int offset = 0;
		
		// Текущий кадр анимации
		int cf = currentFrame(); 
		
		switch (curentState()) {
			
			case WALKING:
				offset = (cf == 1 || cf == 3) ? 2 : 0;
			break;
		
			default:
				offset = 0;
			break;
		}
		
		return offset;
	}
	
	// Проверка необходимости зеркалирования по горизонтали спрайта персонажа
	private boolean getFlipX() {
		
		// Флаг необходимости зеркалирования по горизонтали
		boolean flip = false;
		
		// Текущий кадр анимации
		int cf = currentFrame(); 
		
		switch (curentState()) {
		
			case WHIRLIGIG_KICK:
				flip = (cf == 3 || cf == 4 || cf == 8 || cf == 9 || cf == 13 || cf == 14);
			break;
			
			case BACK_KICK:
				flip = (this.DESTINATION_GATE_ID == Gate.RIGHT_GATES) ? (cf == 0 || cf == 1 || cf == 2 || cf == 6 || cf == 7 || cf == 8) : (cf == 3 || cf == 4 || cf == 5);
			break;
			
			default:
				flip = (this.direction == Directions.LEFT);
			break;
		}
		
		return flip;
	}
	
	// Проверка необходимости зеркалирования по вертикали спрайта персонажа
	private boolean getFlipY() {
		
		// Флаг необходимости зеркалирования по вертикали
		boolean flip = false;
		
		// Текущий кадр анимации
		int cf = currentFrame(); 
		
		switch (curentState()) {
		
			case BACK_KICK:
				flip = (cf == 4 || cf == 5 || cf == 6);
			break;
			
			default:
				flip = false;
			break;
		}
		
		return flip;
	}
	
	// Возвращает текущую высоту прыжка персонажа
	public float jumpHeight() {
		return this.JUMP_HEIGHT;
	}
	
	public float getJumpVelocity() {
		return this.JUMP_VELOCITY;
	}
	
	public void setJumpVelocity(float v) {
		this.JUMP_VELOCITY = v;
	}
	
	public float getMass() {
		return this.MASS;
	}
	
	public void setMass(float m) {
		this.MASS = m;
	}
	
	public float getStrength() {
		return this.STRENGTH;
	}
	
	public void setStrength(float f) {
		this.STRENGTH = f;
	}
	
	public float getKickStrength() {
		return this.KICK_STRENGTH;
	}
	
	public void setKickStrength(float s) {
		this.KICK_STRENGTH = s;
	}
	
	public float getWidth() {
		return this.PLAYER_WIDTH;
	}
	
	public float getHeight() {
		return this.PLAYER_HEIGHT;
	}
	
	public float getAbsX() {
		return this.POS_X;
	}
	
	public float getAbsY() {
		return this.POS_Y;
	}
	
	public float getAbsH() {
		return this.JUMP_HEIGHT;
	}
	
	public void setAbsX(float x) {
		this.POS_X = x;
	}
	
	public void setAbsY(float y) {
		this.POS_Y = y;
	}
	
	public void setAbsH(float h) {
		this.JUMP_HEIGHT = h;
	}
	
	public void setActionsListener(Actions al) {
		this.actionsListener = al;
	}
	
	public void setVelocityX(float v) {
		this.CURENT_SPEED_X = v;
	}
	
	public void setVelocityY(float v) {
		this.CURENT_SPEED_Y = v;
	}
	
	public float getVelocityX() {
		return this.CURENT_SPEED_X;
	}
	
	public float getVelocityY() {
		return this.CURENT_SPEED_Y;
	}
	
	// Возвращает большую по модулю скорость по осям OX / OY
	public float getModVelocity() {
		if (Math.abs(this.getVelocityX()) > Math.abs(this.getVelocityY())) {
			return this.getVelocityX();
		}
		else {
			return this.getVelocityY();
		}
	}
	
	public float maxVelocity() {
		return Math.max(Math.abs(this.CURENT_SPEED_X), Math.abs(this.CURENT_SPEED_Y));
	}
	
	// Сброс скорости до указанной
	private void resetVelocityTo(float velocityX, float velocityY) {
		
		if (this.getVelocityX() > velocityX) 
			this.setVelocityX(velocityX);
		else if (this.getVelocityX() < -velocityX) 
			this.setVelocityX(-velocityX);
		
		if (this.getVelocityY() > velocityY) 
			this.setVelocityY(velocityY);
		else if (this.getVelocityY() < -velocityY) 
			this.setVelocityY(-velocityY);
	}
	
	// Проверка может ли персонаж выполнить действие
	public boolean Can(States stateToCheck) {
		boolean isCan = false;
		
		switch (stateToCheck) {
		
			/* 
			 * Персонаж может идти если он:
			 * 	1. не бъет ногой
			 *  2. не бъет рукой
			 */
			case WALKING: 
				isCan = !state.get(States.KNEE_CATCH)  && 
						!state.get(States.FOOT_KICK) &&
						!state.get(States.HEAD_KICK) &&
						!state.get(States.SIT) &&
						!state.get(States.DEAD) &&
						!state.get(States.HEAD_PASS) &&
						!state.get(States.LAY_BACK) &&
						!state.get(States.LAY_BELLY) &&
						!state.get(States.JUMP) &&
						!state.get(States.FISH_KICK) &&
						(!state.get(States.TOP_RUN) || upPressed() || downPressed()) &&
						!state.get(States.DECELERATION) &&
						this.getAbsH() == 0;
			break;
			
			case DECELERATION: 
				isCan = (
							(((rightPressed() && (direction == Directions.LEFT)) || (leftPressed() && (direction == Directions.RIGHT)) || (downPressed() && (getVelocityY() > 0)) || (upPressed() && (getVelocityY() < 0))) && (((maxVelocity() > WALKING_SPEED) && (FRICTION == GRASS_FRICTION)) || (((maxVelocity() > 0) && (FRICTION != GRASS_FRICTION))))) ||
							(false) 
						) &&
						this.getAbsH() == 0;
			break;
			
			/* 
			 * Персонаж может бежать если он:
			 * 	1. не бъет ногой
			 *  2. не бъет рукой
			 */
			case RUN: case TOP_RUN: 
				isCan = !state.get(States.KNEE_CATCH)  && 
						!state.get(States.FOOT_KICK) &&
						!state.get(States.SIT) &&
						!state.get(States.DEAD) &&
						!state.get(States.LAY_BACK) &&
						!state.get(States.LAY_BELLY) &&
						!state.get(States.HEAD_PASS) &&
						!state.get(States.FISH_KICK) &&
						!state.get(States.TOP_RUN) &&
						!state.get(States.JUMP);
			break;
			
			/* 
			 * Персонаж может выполнить удар рукой если он:
			 * 	1. не бъет рукой
			 *  2. не бъет ногой
			 */
			case KNEE_CATCH: 
				isCan = this.getAbsH() == 0 &&
						this.getVelocityX() == 0 &&
						this.getVelocityY() == 0 &&
						ball.getAbsH() < 35;
			break;
			
			case CHEST_CATCH: 
				isCan = this.getAbsH() == 0 &&
						this.getVelocityX() == 0 &&
						this.getVelocityY() == 0 &&
						ball.getAbsH() >= 35;
			break;
			
			/* 
			 * Персонаж может выполнить удар ногой если он:
			 * 	1. не дает пасс
			 *  2. не бьет ногой
			 */
			case PASS: 
				isCan = !state.get(States.FOOT_KICK) &&
						!state.get(States.DEAD) &&
						!state.get(States.SIT) &&
						!state.get(States.LAY_BACK) &&
						!state.get(States.HEAD_PASS) &&
						!state.get(States.LAY_BELLY) &&
						!state.get(States.FISH_KICK) &&
						!state.get(States.WHIRLIGIG_KICK) &&
						!state.get(States.PASS);
			break;
			
			case HEAD_PASS: 
				isCan = !state.get(States.FOOT_KICK) &&
						!state.get(States.DEAD) &&
						!state.get(States.SIT) &&
						!state.get(States.LAY_BACK) &&
						!state.get(States.HEAD_PASS) &&
						!state.get(States.LAY_BELLY) &&
						!state.get(States.FISH_KICK) &&
						!state.get(States.PASS) &&
						(
							(ball.getAbsH() - this.getAbsH() > 140) || (this.getAbsH() == 0 && ball.getAbsH() > 70)
						);
			break;
			
			/* 
			 * Персонаж может выполнить удар ногой если он:
			 * 	1. не дает пасс
			 *  2. не бьет ногой
			 */
			case FOOT_KICK: 
				isCan = !state.get(States.FOOT_KICK) && 
						!state.get(States.DEAD) &&
						!state.get(States.SIT) &&
						!state.get(States.LAY_BACK) &&
						!state.get(States.HEAD_PASS) &&
						!state.get(States.LAY_BELLY) &&
						!state.get(States.FISH_KICK) &&
						!state.get(States.WHIRLIGIG_KICK) &&
						!this.ACTION_DONE &&
						!state.get(States.PASS);
			break;
			
			case FISH_KICK: 
				// Расстояние от игрока к мячу
				float l = MathUtils.distance(getAbsX(), getAbsY(), ball.getAbsX(), ball.getAbsY());
				
				isCan = !state.get(States.FOOT_KICK) && 
						!state.get(States.DEAD) &&
						!state.get(States.SIT) &&
						!state.get(States.FISH_KICK) &&
						!state.get(States.LAY_BACK) &&
						!state.get(States.HEAD_PASS) &&
						!state.get(States.LAY_BELLY) &&
						!state.get(States.PASS) &&
						!state.get(States.WHIRLIGIG_KICK) &&
						!this.catchBall() &&
						this.getAbsH() == 0 &&
						((l > 70 && l < 230 && Math.abs(getVelocityX()) > 0) || velArrowPressed());
			break;
			
			case HEAD_KICK: 
				isCan = !state.get(States.FOOT_KICK) && 
						!state.get(States.FISH_KICK) &&
						!state.get(States.BACK_KICK) &&
						!state.get(States.DEAD) &&
						!state.get(States.SIT) && 
						!state.get(States.LAY_BACK) &&
						!state.get(States.LAY_BELLY) &&
						!state.get(States.HEAD_PASS) &&
						!state.get(States.PASS) && 
						!this.ACTION_DONE &&
						(
							((dArrowPressed()) || (this.getAbsH() == 0 && ball.getAbsH() > 70)) && !ball.isCatched() || 
							(ball.isCatched() && dArrowPressed() && getAbsH() > 0)
						);
			break;
			
			case BACK_KICK: 
				isCan = !state.get(States.FOOT_KICK) && 
						!state.get(States.FISH_KICK) &&
						!state.get(States.BACK_KICK) &&
						!state.get(States.DEAD) &&
						!state.get(States.SIT) && 
						!state.get(States.LAY_BACK) &&
						!state.get(States.LAY_BELLY) &&
						!state.get(States.HEAD_PASS) &&
						!state.get(States.PASS) && 
						!this.ACTION_DONE &&
						(
							((conterdArrowPressed()) || (this.getAbsH() == 0 && ball.getAbsH() > 70)) && !ball.isCatched() || 
							(ball.isCatched() && conterdArrowPressed() && getAbsH() > 0)
						);
				break;
			
			case WHIRLIGIG_KICK:
				isCan = this.getAbsH() > 25 &&
						this.maxVelocity() > 0 &&
						ball.getAbsH() > 0 &&
						!this.ACTION_DONE &&
						!this.catchBall() &&
						!state.get(States.WHIRLIGIG_KICK) &&
						!state.get(States.FISH_KICK);
			break;
			
			/* 
			 * Персонаж может прыгнуть если он:
			 * 	1. не бъет ногой
			 *  2. не бъет рукой
			 *  3. не находится в воздухе
			 */
			case JUMP: 
				isCan = !state.get(States.KNEE_CATCH) && 
						!state.get(States.FISH_KICK) &&
						!state.get(States.FOOT_KICK) &&
						!state.get(States.DEAD) &&
						!state.get(States.SIT) &&
						!state.get(States.LAY_BACK) &&
						!state.get(States.LAY_BELLY) &&
						!state.get(States.PASS) &&
						!state.get(States.HEAD_PASS) &&
						!state.get(States.JUMP);
			break;
			
			/*
			 * Игрок может принять мяч:
			 * 	1. Если он без мяча
			 *  2. Он не мертв 
			 */ 
			case CATCH_BALL:
				isCan = !catchBall() && 
						!state.get(States.DEAD) &&
						!state.get(States.SIT) &&
						!state.get(States.LAY_BACK) &&
						!state.get(States.LAY_BELLY);
			break;
			
			case DEAD:
				isCan = !state.get(States.DEAD) &&
						!state.get(States.LAY_BACK) &&
						!state.get(States.LAY_BELLY);
			break;
			
			default:
				isCan = false;
			break;
		}
		
		return isCan;
	}
	
	// Постановка задания на выполнение действия
	public void Do(States state, boolean stopAll) {
		// Если установлен флаг stopAll то останавливаем все анимации
		if (stopAll) this.stopAll();
		
		// Если нужно выполнить дополнительные действия
		switch (state) {
			case FOOT_KICK: case HEAD_KICK:
				//disableDirections();
				actionsListener.disableAction(Controls.ACTION1, this.PLAYER_ID);
				this.stateTime = 0.0f;
				this.ACTION_DONE = true;
				
				// Установка необходимости подбросить игрока
				if (this.getAbsH() > 0) this.setJumpVelocity(3.5f);
				
				// Для того чтобы при ударе головой мяч двигался вверх
				if (state == States.HEAD_KICK) ball.allowGravityFrom(999);
				
				// Поворачиваем игрока в нужную сторону перед нанесением удара
				if (DESTINATION_GATE_ID == Gate.LEFT_GATES && direction == Directions.RIGHT) {
					direction = Directions.LEFT;
				} 
				else if (DESTINATION_GATE_ID == Gate.RIGHT_GATES && direction == Directions.LEFT) {
					direction = Directions.RIGHT;
				}
				
				// Сброс скорости персонажа при нанесении удара
				this.resetVelocityTo(Math.abs(getVelocityX() / 1.8f), 1.5f);
			break;
			
			case BACK_KICK:
				actionsListener.disableAction(Controls.ACTION1, this.PLAYER_ID);
				this.stateTime = 0.0f;
				this.ACTION_DONE = true;
				this.setJumpVelocity(3.5f);
				ball.allowGravityFrom(999);
				
				// Сброс скорости персонажа при нанесении удара
				this.resetVelocityTo(Math.abs(getVelocityX() / 1.8f), 1.5f);
			break;
			
			case FISH_KICK:
				this.setJumpVelocity(2.5f);
				this.setVelocityX(direction == Directions.RIGHT ? this.RUN_SPEED : -this.RUN_SPEED);
				this.stateTime = 0.0f;
			break;
			
			case WHIRLIGIG_KICK:
				this.stateTime = 0.0f;
				this.ACTION_DONE = true;
				this.setJumpVelocity(0);
			break;
			
			case PASS: case HEAD_PASS:
				disableDirections();
				actionsListener.disableAction(Controls.ACTION2, this.PLAYER_ID);
				this.stateTime = 0.0f;
				
				// Если при ударе ногой игрок не находится в воздухе то останавливаем его мгновенно
				if (this.JUMP_HEIGHT == 0) {
					this.CURENT_SPEED_X = 0.0f;
					this.CURENT_SPEED_Y = 0.0f;
				}
			break;
			
			case JUMP: 
				actionsListener.disableAction(Controls.ACTION3, this.PLAYER_ID);
				this.stateTime = 0.0f;
				this.setJumpVelocity(this.STRENGTH / this.MASS);
				
				// Для того чтобы во время прыжка на него не действовала гравитация (он привязан к игроку)
				if (ball.isCatched()) ball.allowGravityFrom(0);
				
				field.sounds.play("jump01",true);
			break;
			
			case RUN: 
				this.setVelocityX(direction == Directions.RIGHT ? this.RUN_SPEED : -this.RUN_SPEED); 
				this.stateTime = 0.0f;
				actionsListener.disableAction(Controls.LEFT, this.PLAYER_ID);
				actionsListener.disableAction(Controls.RIGHT, this.PLAYER_ID);
				field.sounds.play("run01");
				field.sounds.loop("run01", true);
			break;
			
			case TOP_RUN: 
				this.setVelocityX(direction == Directions.RIGHT ? this.TOP_RUN_SPEED : -this.TOP_RUN_SPEED); 
				this.stateTime = 0.0f;
				actionsListener.disableAction(Controls.LEFT, this.PLAYER_ID);
				actionsListener.disableAction(Controls.RIGHT, this.PLAYER_ID);
				field.sounds.play("run01");
				field.sounds.loop("run01", true);
			break;
			
			case STAY: 
				// Если игрок не находится в воздухе то устанавливаем его скорости по осям равными 0
//				if (this.getAbsH() == 0) {
//					this.CURENT_SPEED_X = 0.0f;
//					this.CURENT_SPEED_Y = 0.0f;
//				}
			break;
			
			case SIT: 
				this.stateTime = 0.0f;
//				this.CURENT_SPEED_X = 0.0f;
//				this.CURENT_SPEED_Y = 0.0f;
			break;
			
			case DEAD:
				this.stateTime = 0.0f;
			break;
			
			case CHEST_CATCH:
				this.stateTime = 0.0f;
			break;
			
			case KNEE_CATCH: 
				this.stateTime = 0.0f;
				disableDirections();
				actionsListener.disableAction(Controls.ACTION2, this.PLAYER_ID);
			break;
			
			default:
			break;
		}
		
		// Добавляем задачу на исполнение
		this.state.put(state, true);
	}
	
	// Отключение всех действий к перемещению персонажа
	private void disableDirections() {
		actionsListener.disableAction(Controls.RIGHT, this.PLAYER_ID);
		actionsListener.disableAction(Controls.LEFT, this.PLAYER_ID);
		actionsListener.disableAction(Controls.UP, this.PLAYER_ID);
		actionsListener.disableAction(Controls.DOWN, this.PLAYER_ID);
	}
	
	// Текущее состояние персонажа
	public States curentState() {
		for (int i = 0; i < States.values().length; i++) {
			if (state.get(States.values()[i])) {
				return States.values()[i];
			}
		}
		return States.STAY;
	}
	
	// Установка текущего состояния персонажа (для подмены анимации)
	public void curentState(States s) {
		this.stopAll();
		state.put(States.JUMP, true); 
	}
	
	// Полностью остановить игрока
	private void stopAll() {
		for (int i = 0; i < States.values().length; i++) {
			state.put(States.values()[i], false);
		}
	}
	
	private int currentFrame() {
		return animations.get(this.curentState()).getKeyFrameIndex(stateTime);
	}
	
	private boolean upPressed() {
		return actionsListener.getActionStateFor(Controls.UP, this.PLAYER_ID).pressed;
	}
	
	private boolean downPressed() {
		return actionsListener.getActionStateFor(Controls.DOWN, this.PLAYER_ID).pressed;
	}
	
	private boolean leftPressed() {
		return actionsListener.getActionStateFor(Controls.LEFT, this.PLAYER_ID).pressed;
	}
	
	private boolean leftDblPressed() {
		return actionsListener.getActionStateFor(Controls.LEFT, this.PLAYER_ID).doublePressed;
	}
	
	private boolean leftTriplePressed() {
		return actionsListener.getActionStateFor(Controls.LEFT, this.PLAYER_ID).triplePressed;
	}
	
	private boolean rightPressed() {
		return actionsListener.getActionStateFor(Controls.RIGHT, this.PLAYER_ID).pressed;
	}
	
	private boolean rightDblPressed() {
		return actionsListener.getActionStateFor(Controls.RIGHT, this.PLAYER_ID).doublePressed;
	}
	
	private boolean rightTriplePressed() {
		return actionsListener.getActionStateFor(Controls.RIGHT, this.PLAYER_ID).triplePressed;
	}
	
	/*
	private boolean action1DblPressed() {
		return actionsListener.getActionStateFor(Controls.ACTION1, this.PLAYER_ID).doublePressed;
	}
	
	private boolean action2DblPressed() {
		return actionsListener.getActionStateFor(Controls.ACTION2, this.PLAYER_ID).doublePressed;
	}
	
	private boolean action3DblPressed() {
		return actionsListener.getActionStateFor(Controls.ACTION3, this.PLAYER_ID).doublePressed;
	}
	*/
	
	private boolean action1Pressed() {
		return actionsListener.getActionStateFor(Controls.ACTION1, this.PLAYER_ID).pressed;
	}
	
	private boolean action2Pressed() {
		return actionsListener.getActionStateFor(Controls.ACTION2, this.PLAYER_ID).pressed;
	}
	
	private boolean action3Pressed() {
		return actionsListener.getActionStateFor(Controls.ACTION3, this.PLAYER_ID).pressed;
	}
	
	// Нажата ли клавиша совпадающая с направлением движения (право / лево)
	private boolean dArrowPressed() {
		
		return (this.DESTINATION_GATE_ID == 0 && this.leftPressed() || this.DESTINATION_GATE_ID == 1 && this.rightPressed());
	}
	
	// Нажата ли клавиша не совпадающая с направлением движения (право / лево)
	private boolean conterdArrowPressed() {
		
		return (this.DESTINATION_GATE_ID == 0 && this.rightPressed() || this.DESTINATION_GATE_ID == 1 && this.leftPressed());
	}
	
	// Нажата ли клавиша совпадающая с направлением движения (право / лево)
	private boolean velArrowPressed() {
		boolean b = false;
		
		if (this.leftPressed() && direction == Directions.LEFT) 
			b = true;
		else if (this.rightPressed() && direction == Directions.RIGHT) 
			b = true;
		return b;
		
	}
	
	// Минимальная высота на которой возможно произведение суперудара
	private float superKickMinHeight() {
		
		float minHeight = 0;
		
		switch (this.PLAYER_ID) {
			
			case 0: // Стальная нога
				minHeight = 50;
			break;
			
			default:
				minHeight = 50;
			break;
		}
		
		// Если производится удар головой или через себя, то увеличиваем минимальную 
		// высоту на которой должен быть мяч чтобы выполнить суперудар
		if (this.curentState() == States.HEAD_KICK || this.curentState() == States.BACK_KICK) {
			minHeight += 70;
		}
		
		return minHeight;
	}
	
	// Максимальное расстояние до ворот на котором игрок может выполнить суперудар
	private float superKickMaxLength() {
		
		float maxLength = 0;
		
		switch (this.PLAYER_ID) {
			
			case 0: // Стальная нога
				maxLength = 2300;
			break;
			
			default:
				maxLength = 2300;
			break;
		}
		
		return maxLength;
	}
	
	// Может ли игрок выполнить суперудар
	private boolean superKickAviable() {
		
		if (MathUtils.distance(ball.getAbsX(), ball.getAbsY(), field.gates[this.DESTINATION_GATE_ID].getAbsX(), field.gates[this.DESTINATION_GATE_ID].getAbsY()) < this.superKickMaxLength()) {
			return (ball.getAbsH() > this.superKickMinHeight());
		}
		else {
			return false;
		}
	}
	
	@Override
	public void act(float delta) {
		
		// Если игрок находится на земле, то сбрасываем флаг того, что 
		// игрок исчерпал лимит ударов в полете
		if (this.ACTION_DONE && this.getAbsH() == 0) {
			this.ACTION_DONE = false;
		}
		
		if (upPressed()) { 
			if (Can(States.DECELERATION)) {
				Do(States.DECELERATION, true);
			}
			else if (Can(States.WALKING)) {
				// Если персонаж не бежит, и нажата клавиша вверх
				if (curentState() != States.RUN && curentState() != States.TOP_RUN) {
					Do(States.WALKING, true);
				}
				this.CURENT_SPEED_Y = this.WALKING_SPEED;
			}
		}
		
		if (downPressed()) { 
			if (Can(States.DECELERATION)) {
				Do(States.DECELERATION, true);
			}
			else if (Can(States.WALKING)) {
				// Если персонаж не бежит, и нажата клавиша вверх
				if (curentState() != States.RUN && curentState() != States.TOP_RUN) {
					Do(States.WALKING, true);
				}
				this.CURENT_SPEED_Y = -this.WALKING_SPEED;
			}
		}
		
		
		if (leftPressed()) {
			if (Can(States.DECELERATION)) {
				Do(States.DECELERATION, true);
			}
			else if (Can(States.WALKING)) {
				this.direction = Directions.LEFT;
				
				// Если было тройное нажатие кнопки, то делаем персонаж бегущим
				if (leftTriplePressed() && Can(States.TOP_RUN)) {
					Do(States.TOP_RUN, true);
				} 
				// Если было двойное нажатие кнопки, то делаем персонаж бегущим
				else if (leftDblPressed() && Can(States.RUN)) {
					Do(States.RUN, true);
				} 
				else if (Can(States.WALKING)) {
					setVelocityX(-this.WALKING_SPEED);
					Do(States.WALKING, true);
				}
			}
		}
		
		if (rightPressed()) {
			if (Can(States.DECELERATION)) {
				Do(States.DECELERATION, true);
			}
			else if (Can(States.WALKING)) {
				this.direction = Directions.RIGHT;
				
				// Если было тройное нажатие кнопки, то делаем персонаж бегущим
				if (rightTriplePressed() && Can(States.TOP_RUN)) {
					Do(States.TOP_RUN, true);
				} 
				// Если было двойное нажатие кнопки, то делаем персонаж бегущим
				else if (rightDblPressed() && Can(States.RUN)) {
					Do(States.RUN, true);
				} 
				else if (Can(States.WALKING)) {
					setVelocityX(this.WALKING_SPEED);
					Do(States.WALKING, true);
				}
			}
		}
		
		// Удар головой / ногой
		if (action1Pressed()) {
			// Если сейчас игрок делает вернушку, то отключаем ее
			if (curentState() == States.WHIRLIGIG_KICK) {
				Do(States.STAY, true);
			}
			
			// Можно ли выполнить удар юлой
			if (Can(States.WHIRLIGIG_KICK)) {
				Do(States.WHIRLIGIG_KICK, true);
			}
			// Может ли игрок ударить рыбкой
			else if (Can(States.FISH_KICK)) { 
				Do(States.FISH_KICK, true);
			}
			// иначе, можети ли ударить головой
			else if (Can(States.HEAD_KICK)) { 
				Do(States.HEAD_KICK, true);
			}
			// иначе, можети ли ударить через себя
			else if (Can(States.BACK_KICK)) { 
				Do(States.BACK_KICK, true);
			}
			// иначе, может ли ударить ногой
			else if (Can(States.FOOT_KICK)) { 
				Do(States.FOOT_KICK, true);
			}
			
			// Отключаем кнопку, чтобы не сбрасывалось действие (например удар юлой)
			actionsListener.disableAction(Controls.ACTION1, this.PLAYER_ID);
		}
		
		// Пас
		if (action2Pressed()) {
			if (Can(States.HEAD_PASS)) {
				Do(States.HEAD_PASS, true);
			}
			else if (Can(States.PASS)) {
				Do(States.PASS, true);
			}
			
			actionsListener.disableAction(Controls.ACTION2, this.PLAYER_ID);
		}
		
		// Прыжок
		if (action3Pressed() && Can(States.JUMP)) {
			Do(States.JUMP, true);
			
			actionsListener.disableAction(Controls.ACTION3, this.PLAYER_ID);
		}
		
//		// Если нажат прыжок, когда игрок скользит лежа по газону, то поднимаем его
//		if (action3Pressed() && curentState() == States.FISH_KICK) {
//			Do(States.SIT, true);
//		}
		
		// Если при следующем шаге персонаж будет находиться внутри толщи ворот то останавливаем его 
		if (MathUtils.intersectCount(getAbsX() + getVelocityX(), getAbsY() + getVelocityY(), field.gates[Gate.LEFT_GATES].gateProjection) == 1 ||
			MathUtils.intersectCount(getAbsX() + getVelocityX(), getAbsY() + getVelocityY(), field.gates[Gate.RIGHT_GATES].gateProjection) == 1) 
		{
			setVelocityX(0);
			setVelocityY(0);
			if (getAbsH() == 0) Do(States.WALKING, true);
		}
		
		if (this.PLAYER_ID == 0) {
			
			// Определение коэффициента трения
			if (field.isCellInPolygon(getAbsX(),getAbsY())) {
				FRICTION = WATER_FRICTION;
			}
			else {
				FRICTION = GRASS_FRICTION;
			}
		}
		
		/********************************************************************
		 *      Воздействия сили трения с учетом трения игрока о 			*
		 *      текущий фрагмент поверхности на которой он находится 		*
		 * ******************************************************************/
		if ((curentState() != States.WALKING && curentState() != States.RUN && curentState() != States.TOP_RUN) && getAbsH() == 0) {
			
			float dX = this.CURENT_SPEED_X * this.FRICTION;
			float dY = this.CURENT_SPEED_Y * this.FRICTION;
			
			if (dX > 0) {
				if (dX > 0.5f) dX = 0.5f;
				if (dX < 0.15f) dX = 0.15f;
			}
			else {
				if (dX < -0.5f) dX = -0.5f;
				if (dX > -0.15f) dX = -0.15f;
			}
			
			if (dY > 0) {
				if (dY > 0.5f) dY = 0.5f;
				if (dY < 0.15f) dY = 0.15f;
			}
			else {
				if (dY < -0.5f) dY = -0.5f;
				if (dY > -0.15f) dY = -0.15f;
			}
			
			// Замедление движения игрока
			this.CURENT_SPEED_X += dX;
			this.CURENT_SPEED_Y += dY;
			
			// Если скорость по осям упала до 0,35 то останавливаем игрока
			if (Math.abs(getVelocityX()) + Math.abs(getVelocityY()) < 0.35f) {
				setVelocityX(0);
				setVelocityY(0);
				
				/* Если производился удар "рыбкой", то то после того, как игрок остановился
				 * он должен присесть (привстать) */
				if (curentState() == States.FISH_KICK) {
					Do(States.SIT, true);
				}
				else if (curentState() == States.DECELERATION) {
					Do(States.STAY, true);
				}
			}
		}
		
		// Торможения игрока
		if (Can(States.DECELERATION)) {
			Do(States.DECELERATION, true);
		}
		
		// Перемещение персонажа
		movePlayerBy(new Vector2(this.CURENT_SPEED_X, this.CURENT_SPEED_Y));
		
		// Если игрок находится в непосредственной близости возле мяча
		if (this.ballIsNear()) {
			
			switch (this.curentState()) {
			
				case WHIRLIGIG_KICK:
					this.ballKick();
				break;
				
				case FOOT_KICK:
					// Если высота мяча больше чем минимальная высота которая необходима для того 
					// чтобы игрок выполняющий удар смог произвести суперудар, и расстояние до ворот
					// меньше чем максимальное расстояние на которое игрок может выполнить суперудар
					// то игрок производит суперудар
					if (this.superKickAviable()) {
						ball.Do(Ball.States.FOOT_SUPER_KICK, true);
					}
					
					// Выполнение удара по мячу
					if (currentFrame() >= 4 && currentFrame() <= 7) this.ballKick();
				break;
				
				case HEAD_KICK:
					// Перемещение мяча вверх при ударе
					if (this.getAbsH() != 0) ball.setJumpVelocity(5.8f);
					
					// Выполнение удара по мячу
					if ((currentFrame() >= 1 && curentState() == States.HEAD_KICK && ball.isCatched()) || !ball.isCatched()) {
						
						// Если доступен суперудар головой
						if (this.superKickAviable()) {
							ball.Do(Ball.States.HEAD_BACK_SUPER_KICK, true);
							
							this.ballHeadBackSuperKick();
						}
						// Выполнение обычного удара головой
						else {
							this.ballKick();
						}
					}
				break;
				
				case BACK_KICK:
					// Перемещение мяча вверх при ударе
					if (this.getAbsH() != 0) ball.setJumpVelocity(3.5f);
					
					// Выполнение удара по мячу
					if ((currentFrame() >= 4 && curentState() == States.BACK_KICK && ball.isCatched()) || !ball.isCatched()) {
						
						// Если доступен суперудар головой
						if (this.superKickAviable()) {
							ball.Do(Ball.States.HEAD_BACK_SUPER_KICK, true);
							
							this.ballHeadBackSuperKick();
						}
						// Выполнение обычного удара головой
						else {
							this.ballKick();
						}
					}
					break;
				
				case FISH_KICK:
					// Выполнение удара по мячу
					if (currentFrame() < 2) this.ballKick();
				break;
				
				case HEAD_PASS:
					// Отмечаем что игрок теряет мяч
					this.catchBall(false);
					
					// Мяч не контроллируется никем
					ball.managerByBlayer(-1);
					
					// Проигрывание звука паса
					field.sounds.play("pass01", true);
					
					// Определение кому отдать пас
					if (this.PLAYER_ID == 0)
						ball.pass(field.players[1].getAbsX(),field.players[1].getAbsY());
					else 
						ball.pass(field.players[0].getAbsX(),field.players[0].getAbsY());
				break;
				
				case PASS:
					// Начало полета мяча при пасе не должно начинаться сразу же после начала анимации паса,
					// а с некоторой задержкой
					if (currentFrame() >= 2) {
						
						// Отмечаем что игрок теряет мяч
						this.catchBall(false);
						
						// Мяч не контроллируется никем
						ball.managerByBlayer(-1);
						
						// Проигрывание звука паса
						field.sounds.play("pass01", true);
						
						// Определение кому отдать пас
						if (this.PLAYER_ID == 0)
							ball.pass(field.players[1].getAbsX(),field.players[1].getAbsY());
						else 
							ball.pass(field.players[0].getAbsX(),field.players[0].getAbsY());
					}
				break;
				
				// Если персонаж ничего не делает и мяч никем не контролируется
				default:
					if (!ball.isCatched()) {
						/* 
						 * В зависимости от сили мяча игрок либо принимает его либо мяч его убивает
						 */
						// Когда скорость мяча меньше 15, то игрок принимает мяч
						if (ball.absVelocity() < 14 && Can(States.CATCH_BALL)) {
							// Если игрок находится в воздухе то его анимация при приеме мяча не меняется
							if (Can(States.CHEST_CATCH)) {
								Do(States.CHEST_CATCH, true);
							}
							else if (Can(States.KNEE_CATCH)) {
								Do(States.KNEE_CATCH, true);
							}
							
							// Отмечаем что игрок заполучил мяч
							this.catchBall(true);
							
							// Устанавливаем ID игрока, так как мяч конторллируется им
							ball.managerByBlayer(this.PLAYER_ID);
							
							// Останавливаем движение мяча, так как он привязан к игроку
							ball.setVelocityX(0);
							ball.setVelocityY(0);
						}
						// Если скорость больше 14 то мяч убивает игрока
						else if (ball.absVelocity() >= 14) {
							if (Can(States.DEAD)) {
								Do(States.DEAD, true);
								this.setJumpVelocity(this.STRENGTH / this.MASS / 1.5f);
							}
							
							// Отмечаем что игрок потерял мяч
							this.catchBall(false);
							
							// Мяч не контроллируется никем
							ball.managerByBlayer(-1);
						}
					}
				break;
			}
		}
		
		// Если ниодна из кнопок направления движения не нажата
		if (!upPressed() && !downPressed() && curentState() == States.WALKING) {
			this.CURENT_SPEED_Y = 0;
		}
		
		if (!leftPressed() && !rightPressed() && curentState() == States.WALKING) {
			this.CURENT_SPEED_X = 0;
		}
		
		// Если персонаж бежит и не нажата кнопка вверх или вниз
		if (!this.upPressed() && !this.downPressed() && (state.get(States.RUN) || state.get(States.TOP_RUN))) {
			this.CURENT_SPEED_Y = 0.0f;
		}
		
		// Если игрок находится в воздухе и его анимация "STAY" то подменяем ее на "JUMP"
		if (this.curentState() == States.STAY && this.getAbsH() > 0) {
			this.curentState(States.JUMP);
		}
		
		
		/**********************************************************************
		 *                       Реализация гравитации                        *
		 **********************************************************************/
		
		// Пока персонаж находится в воздухе его скорость взлета / падения меняется
		this.JUMP_VELOCITY += (this.JUMP_HEIGHT > 0 && curentState() != States.WHIRLIGIG_KICK) ? -8.0f * Gdx.graphics.getDeltaTime() : 0;
		this.JUMP_HEIGHT += this.JUMP_VELOCITY;
		
		// Если игрок приземлился
		if (this.JUMP_HEIGHT < 0) {
			/* Когда персонаж бил рыбкой, то он должен прокатиться по газону 
			 * перед тем как встать, поэтому нельзя чтобы она сразу начал вставать */
			if (curentState() == States.FISH_KICK) {
				
			}
			// Когда персонаж не мертв то при приземлении он приседает
			else if (curentState() != States.DEAD) { 
				Do(States.SIT, true);
			}
			// Если мертв то он ложится на газон на спину или на живот, в зависимости от направления игрока
			else {
				if (direction == Directions.LEFT) {
					Do(States.LAY_BELLY, true);
				}
				else {
					Do(States.LAY_BACK, true);
				}
			}
			this.JUMP_HEIGHT = 0.0f;
			this.JUMP_VELOCITY = 0.0f;
			
			// Проигрывание звука приземления
			field.sounds.play("landing01", true);
		}
	}
	
	// Выполнение удара по мячу
	private void ballKick() {
		
		if (curentState() != States.FISH_KICK) {
			// Определение точки куда бить игроку
			ball.kick(this.getKickStrength(), this.netCenter(this.DESTINATION_GATE_ID).x, this.netCenter(this.DESTINATION_GATE_ID).y, this.upPressed());
		}
		else {
			// Если производится удар "рыбкой" то удар выполняется по воротам в сторону которых
			// повернут игрок, а не по воротам DESTINATION_GATE_ID
			int gateId = (direction == Directions.RIGHT) ? Gate.RIGHT_GATES : Gate.LEFT_GATES;
			
			// Определение точки куда бить игроку
			ball.kick(this.getKickStrength(), this.netCenter(gateId).x, this.netCenter(gateId).y, this.upPressed());
		}
		
		// Отмечаем что игрок потерял мяч
		this.catchBall(false);
		
		// Мяч не контроллируется никем
		ball.managerByBlayer(-1);
		
		// Звук удара мяча
		field.sounds.play("kick01", true);
	}
	
	// Выполнение суперудара ноловой / через себя по мячу
	private void ballHeadBackSuperKick() {
		// Определение точки куда бить игроку
		ball.headBackSuperKick(this.getKickStrength() * 1.2f, this.netCenter(this.DESTINATION_GATE_ID).x, this.netCenter(this.DESTINATION_GATE_ID).y);
		
		// Отмечаем что игрок потерял мяч
		this.catchBall(false);
		
		// Мяч не контроллируется никем
		ball.managerByBlayer(-1);
		
		// Звук удара мяча
		field.sounds.play("kick01", true);
	}
	
	// Определение точки середины ворот
	private Vector2 netCenter(int gateId) {
		// Определение точки куда бить игроку
		float dstX = field.gates[gateId].getBottomBar().x;
		float dstY = field.worldHeight / 2.0f;
		
		return new Vector2(dstX, dstY);
	}
	
	// Находится ли мяч рядом
	public boolean ballIsNear() {
		/* В зависимости от состояния игрока (удар рукой / ногой / рыбкой в полете) будем вносить поправки на каком
		   расстоянии от мяча можно сделать то или иное действие:
		   Например находясь на небольшом расстоянии при выполнении удара ногой нужно разрешать 
		   это действие */
		
		float dX = 0;
		float dY = 0;
		
		switch (this.curentState()) {
			// При ударе ногой увеличиваем расстояние к мячу на котором игрок может нанести удар на 20px
			case FOOT_KICK:
				dX = 25;
				dY = 0;
			break; 
			
			default:
				dX = 0;
				dY = 0;
			break;
		}
		
		return Math.abs(ball.getAbsX() - this.getAbsX()) <= 40 + dX && 
				ball.getAbsY() - this.getAbsY() >= -40 && 
				ball.getAbsY() - this.getAbsY() <= 20 + dY &&
				ball.getAbsH() + ball.getDiameter() > this.getAbsH() && 
				ball.getAbsH() < this.getAbsH() + this.getHeight() - 35;
	}
	
	private void movePlayerBy(Vector2 movePoint) {
		
		boolean doStop = false;
		
		if (this.POS_X >= 0 && this.POS_X <= field.worldWidth && this.POS_Y >= 0 && this.POS_Y <= field.worldHeight) {
			
			// Ограничение выхода персонажа за пределы поля
			if (this.POS_X + movePoint.x < 0) {
				movePoint.x = -this.POS_X;
				doStop = true;
			}
			else if (this.POS_X + movePoint.x > field.worldWidth) { 
				movePoint.x = field.worldWidth - this.POS_X;
				doStop = true;
			}
			
			if (this.POS_Y + movePoint.y < 0) {
				movePoint.y = -this.POS_Y;
				doStop = true;
			}
			else if (this.POS_Y + movePoint.y > field.worldHeight) { 
				movePoint.y = field.worldHeight - this.POS_Y;
				doStop = true;
			}
			
			// Если персонаж достиг границы игрового мира, то останавливаем его
			if (doStop) Do(States.STAY,true);
			
			this.POS_X += movePoint.x;
			this.POS_Y += movePoint.y;
			
			
			// Если игрок владеет мячом то привязываем перемещение мяча к этом игроку
			if (this.catchBall() && field.inField(ball.getAbsX(),ball.getAbsY())) {
				if (this.direction == Directions.RIGHT) {
					ball.moveBallBy(new Vector2(this.getAbsX()-ball.getAbsX() + 40, this.getAbsY()-ball.getAbsY() - 1));
				}
				else {
					ball.moveBallBy(new Vector2(this.getAbsX()-ball.getAbsX() - 40, this.getAbsY()-ball.getAbsY() - 1));
				}
				
				if (ball.getJumpVelocity() >= 0 && curentState() != States.HEAD_KICK && curentState() != States.BACK_KICK) {
					ball.setAbsH(this.getAbsH());
				}
				
				// Если мяч контроллируется игроком то не позволяем опуститься мячу ниже высоты игрока
				if (ball.getAbsH() < this.getAbsH()) 
					ball.setAbsH(this.getAbsH());
				else if (ball.getAbsH() > this.getAbsH() + 60) 
					ball.setAbsH(this.getAbsH() + 60);
			}
		}
	}
	
	
	// Контролирует ли мяч игрок
	public boolean catchBall() {
		return this.CATCH_BALL;
	}
	
	public void catchBall(boolean c) {
		if (c) field.sounds.play("catchball01",true);
		ball.isCatched(c);
		this.CATCH_BALL = c;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		stateTime += Gdx.graphics.getDeltaTime();
		
		int offsetX = this.frameOffsetX();
		int offsetY = this.frameOffsetY();
		
		// Анимирование персонажа
		animations.get(this.curentState()).setPlayMode(PlayMode.LOOP);
		currentFrame = animations.get(this.curentState()).getKeyFrame(stateTime, true); 

		// Если окончено текущее действие
		if (animations.get(this.curentState()).isAnimationFinished(stateTime)) {
			/* Если игрок лежал после получение удара, то перед тем как встать должна 
			 * проиграться анимация присевшего игрока */
			if (curentState() == States.LAY_BACK || curentState() == States.LAY_BELLY || curentState() == States.FISH_KICK) {
				Do(States.SIT, true);
			}
			else if (curentState() == States.TOP_RUN) {
				Do(States.RUN, true);
				resetVelocityTo(this.RUN_SPEED, Math.abs(getVelocityY()));
			}
			else if (curentState() != States.RUN && curentState() != States.JUMP  && curentState() != States.DECELERATION) {
				Do(States.STAY, true);
			}
		}

        spriteBatch.begin();
        spriteBatch.draw(
    		currentFrame.getTexture(), 
    		this.getX() - this.width() / 2.0f + offsetX, 
    		this.getY() + this.JUMP_HEIGHT + offsetY, 
    		0, 
    		0, 
    		this.SPRITE_WIDTH, 
    		this.SPRITE_HEIGHT, 
    		this.SPRITE_SCALE, 
    		this.SPRITE_SCALE, 
    		0, 
    		currentFrame.getRegionX(), 
    		currentFrame.getRegionY(), 
    		this.SPRITE_WIDTH, 
    		this.SPRITE_HEIGHT, 
    		this.getFlipX(), 
    		this.getFlipY()
		);
        spriteBatch.end();
        
        // Ресование тени персонажа
        shadow.setX(getX() - 15);
        shadow.setY(getY() - 13);
        shadow.setVisibility(this.JUMP_HEIGHT > 0);
	}
}