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
import com.mygdx.crazysoccer.Actions.Action;
import com.mygdx.crazysoccer.Actions.Controls;

public class Player extends Actor {
	
	private Field field;
	private Ball ball;
	
	// Идентификатор игрока (используется при определении нажатия управляющих кнопок)
	private int PLAYER_ID = 0;
		
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
    
    // Парамерты прыжка персонажа
    private float JUMP_HEIGHT = 0;
    
    // Масса персонажа
    private float MASS = 62.0f;
    
    // Сила персонажа
    private float STRENGTH = 230.0f;
    
    // Сила удара мяча
    private float KICK_STRENGTH = 50.0f;
    
    // Коэффициент трения игрока о газон
    private float GRASS_FRICTION = -0.15f;
    
    // Текущая скорость движения персонажа при прыжке 
    private float JUMP_VELOCITY = 0.0f;
    
    // Параметры персонажа
    private float CURENT_SPEED_X = 0.0f;
    private float CURENT_SPEED_Y = 0.0f;
    private float WALKING_SPEED = 4.0f;
    private float RUN_SPEED = 7.0f;
    private float POS_X = 0.0f;
    private float POS_Y = 0.0f;
    
    // Флаг принимает значение true если игрок контролирует мяч
    private boolean CATCH_BALL = false;
    
	// Перечень возможных состояний героя
	public static enum States {
		STAY,     			// Состояние покоя
		WALKING,			// Ходьба
		RUN,	  			// Бег 
		FATIGUE,  			// Усталость
		CELEBRATE,			// Празднование победы
		KNEE_CATCH,			// Прием мяча ногой
		CHEST_CATCH,		// Прием мяча грудью
		FOOT_KICK, 			// Удар ногой
		HEAD_KICK,			// Удар головой
		FISH_KICK,			// Удар рыбкой (в полете животом вниз)
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
    		new Animation(0.13f, 
				animationMap[0][1], 
				animationMap[0][0], 
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
	
	public float width() {
		return this.SPRITE_SCALE * this.SPRITE_WIDTH;
	}
	
	// Проверка необходимости зеркалирования спрайта персонажа
	private boolean getFlipX() {
		return (this.direction == Directions.LEFT);
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
	
	// Проверка может ли персонаж выполнить действие
	public boolean Can(States stateToCheck) {
		boolean isCan = false;
		
		switch (stateToCheck) {
		
			/* 
			 * Персонаж может идти если он:
			 * 	1. не бъет ногой
			 *  2. не бъет рукой
			 */
			case WALKING : 
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
						this.getAbsH() == 0;
			break;
			
			/* 
			 * Персонаж может бежать если он:
			 * 	1. не бъет ногой
			 *  2. не бъет рукой
			 */
			case RUN: 
				isCan = !state.get(States.KNEE_CATCH)  && 
						!state.get(States.FOOT_KICK) &&
						!state.get(States.SIT) &&
						!state.get(States.DEAD) &&
						!state.get(States.LAY_BACK) &&
						!state.get(States.LAY_BELLY) &&
						!state.get(States.HEAD_PASS) &&
						!state.get(States.FISH_KICK) &&
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
						!this.catchBall() &&
						this.getAbsH() == 0 &&
						((l > 70 && l < 230 && Math.abs(getVelocityX()) > 0) || dArrowPressed());
			break;
			
			case HEAD_KICK: 
				isCan = !state.get(States.FOOT_KICK) && 
						!state.get(States.FISH_KICK) &&
						!state.get(States.DEAD) &&
						!state.get(States.SIT) && 
						!state.get(States.LAY_BACK) &&
						!state.get(States.LAY_BELLY) &&
						!state.get(States.HEAD_PASS) &&
						!state.get(States.PASS) && 
						(
							((ball.getAbsH() - this.getAbsH() > 150 && dArrowPressed()) || (this.getAbsH() == 0 && ball.getAbsH() > 70)) && !ball.isCatched() || 
							(ball.isCatched() && dArrowPressed() && getAbsH() > 0)
						);
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
				
				// Установка необходимости подбросить игрока
				if (this.getAbsH() > 0) this.setJumpVelocity(2.5f);
				
				// Для того чтобы при ударе головой мяч двигался вверх
				if (state == States.HEAD_KICK) ball.allowGravityFrom(999);
				
				// Если при ударе ногой игрок не находится в воздухе то останавливаем его мгновенно
				if (this.JUMP_HEIGHT == 0) {
					this.CURENT_SPEED_X = 0.0f;
					this.CURENT_SPEED_Y = 0.0f;
				}
			break;
			
			case FISH_KICK:
				this.setJumpVelocity(2.5f);
				this.setVelocityX(this.RUN_SPEED);
				this.stateTime = 0.0f;
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
				this.CURENT_SPEED_X = this.RUN_SPEED;
				
				field.sounds.play("run01");
				field.sounds.loop("run01", true);
			break;
			
			case STAY: 
				// Если игрок не находится в воздухе то устанавливаем его скорости по осям равными 0
				if (this.getAbsH() == 0) {
					this.CURENT_SPEED_X = 0.0f;
					this.CURENT_SPEED_Y = 0.0f;
				}
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
	
	// Отключение действия
	private void disableAction(Action disableAction) {
		actionsListener.remove(disableAction);
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
	
	private boolean rightPressed() {
		return actionsListener.getActionStateFor(Controls.RIGHT, this.PLAYER_ID).pressed;
	}
	
	private boolean rightDblPressed() {
		return actionsListener.getActionStateFor(Controls.RIGHT, this.PLAYER_ID).doublePressed;
	}
	
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
		boolean b = false;
		
		if (this.leftPressed() && direction == Directions.LEFT) b = true;
		else if (this.rightPressed() && direction == Directions.RIGHT) b = true;
		
		return b;
	}
	
	@Override
	public void act(float delta) {
		
		if (upPressed()) { 
			if (Can(States.WALKING)) {
				// Если персонаж не бежит, и нажата клавиша вверх
				if (curentState() != States.RUN) {
					Do(States.WALKING, true);
				}
				this.CURENT_SPEED_Y = this.WALKING_SPEED;
			}
		}
		
		if (downPressed()) { 
			if (Can(States.WALKING)) {
				// Если персонаж не бежит, и нажата клавиша вверх
				if (curentState() != States.RUN) {
					Do(States.WALKING, true);
				}
				this.CURENT_SPEED_Y = -this.WALKING_SPEED;
			}
		}
		
		
		if (leftPressed()) { 
			if (Can(States.WALKING)) {
				this.direction = Directions.LEFT;
				
				// Если было двойное нажатие кнопки, то делаем персонаж бегущим
				if (leftDblPressed() && Can(States.RUN)) {
					Do(States.RUN, true);
				} 
				else if (Can(States.WALKING)) {
					this.CURENT_SPEED_X = -this.WALKING_SPEED;
					Do(States.WALKING, true);
				}
			}
		}
		
		if (rightPressed()) {
			System.out.println("E");
			
			if (Can(States.WALKING)) {
				this.direction = Directions.RIGHT;
				
				if (rightDblPressed() && Can(States.RUN)) {
					Do(States.RUN, true);
				} 
				else if (Can(States.WALKING)) {
					this.CURENT_SPEED_X = this.WALKING_SPEED;
					Do(States.WALKING, true);
				}
			}
		}
		
		// Удар головой / ногой
		if (action1Pressed()) {
			//System.out.println(Can(States.HEAD_KICK));
			
			// Может ли игрок ударить рыбкой
			if (Can(States.FISH_KICK)) { 
				//System.out.println("Fish kick...");
				
				Do(States.FISH_KICK, true);
			}
			// иначе, можети ли ударить головой
			else if (Can(States.HEAD_KICK)) { 
				Do(States.HEAD_KICK, true);
			}
			// иначе, может ли ударить ногой
			else if (Can(States.FOOT_KICK)) { 
				Do(States.FOOT_KICK, true);
			}
		}
		
		// Пас
		if (action2Pressed()) {
			if (Can(States.HEAD_PASS)) {
				Do(States.HEAD_PASS, true);
			}
			else if (Can(States.PASS)) {
				Do(States.PASS, true);
			}
		}
		
		// Прыжок
		if (action3Pressed() && Can(States.JUMP)) {
			Do(States.JUMP, true);
		}
		
		// Если нажат прыжок, когда игрок скользит лежа по газону, то поднимаем его
		if (action3Pressed() && curentState() == States.FISH_KICK) {
			Do(States.SIT, true);
		}
		
		// Меняем вектор направления скорости в зависимости от направления движения персонажа
		if (this.CURENT_SPEED_X > 0) {
			if (this.direction == Directions.LEFT) { 
				 this.CURENT_SPEED_X = -this.CURENT_SPEED_X;
			}
		}
		else {
			if (this.direction == Directions.RIGHT) { 
				 this.CURENT_SPEED_X = -this.CURENT_SPEED_X;
			}
		}
		
		// Если при следующем шаге персонаж будет находиться внутри объекта то останавливаем его 
		if (MathUtils.intersectCount(getAbsX() + getVelocityX(), getAbsY() + getVelocityY(), field.gates[0].gateProjection) == 1 ||
			MathUtils.intersectCount(getAbsX() + getVelocityX(), getAbsY() + getVelocityY(), field.gates[1].gateProjection) == 1) 
		{
			setVelocityX(0);
			setVelocityY(0);
			if (getAbsH() == 0) Do(States.WALKING, true);
		}
		
		/********************************************************************
		 *      Воздействия сили трения с учетом трения игрока о 			*
		 *      текущий фрагмент поверхности на которой он находится 		*
		 * ******************************************************************/
		if ((curentState() != States.WALKING && curentState() != States.RUN) && getAbsH() == 0) {
			
			// Замедление движения игрока
			this.CURENT_SPEED_X += this.CURENT_SPEED_X * this.GRASS_FRICTION;
			this.CURENT_SPEED_Y += this.CURENT_SPEED_Y * this.GRASS_FRICTION;
			
			// Если скорость по осям упала до 0,45 то останавливаем игрока
			if (Math.abs(getVelocityX()) < 0.45f && Math.abs(getVelocityY()) < 0.45f) {
				setVelocityX(0);
				setVelocityY(0);
				
				/* Если производился удар "рыбкой", то то после того, как игрок остановился
				 * он должен присесть (привстать) */
				if (curentState() == States.FISH_KICK) {
					Do(States.SIT, true);
				}
			}
		}
		
		
		// Перемещение персонажа
		movePlayerBy(new Vector2(this.CURENT_SPEED_X, this.CURENT_SPEED_Y));
		
		// Если игрок находится в непосредственной близости возле мяча
		if (this.ballIsNear(curentState())) {
			// Если в настоящий момент игроком производится удар по мячу
			if (curentState() == States.FOOT_KICK) {
	
				// Выполнение удара по мячу
				if (currentFrame() >= 4 && currentFrame() <= 7) this.ballKick();
				
			}
			else if (curentState() == States.HEAD_KICK) {
				
				// Перемещение мяча вверх при ударе
				if (this.getAbsH() != 0) ball.setJumpVelocity(5.8f);
				
				// Выполнение удара по мячу
				if ((currentFrame() >= 1 && ball.isCatched()) || !ball.isCatched()) this.ballKick();
			} 
			else if (curentState() == States.FISH_KICK) {
				
				// Выполнение удара по мячу
				if (currentFrame() < 2) 
					this.ballKick();
			}
			else if (curentState() == States.HEAD_PASS) {
				// Отмечаем что игрок теряет мяч
				this.catchBall(false);
				
				// Проигрывание звука паса
				field.sounds.play("pass01", true);
				
				// Определение кому отдать пас
				if (this.PLAYER_ID == 0)
					ball.pass(field.players[1].getAbsX(),field.players[1].getAbsY());
				else 
					ball.pass(field.players[0].getAbsX(),field.players[0].getAbsY());
			}
			else if (curentState() == States.PASS) {
				// Начало полета мяча при пасе не должно начинаться сразу же после начала анимации паса,
				// а с некоторой задержкой
				if (currentFrame() >= 2) {
					
					// Отмечаем что игрок теряет мяч
					this.catchBall(false);
					
					// Проигрывание звука паса
					field.sounds.play("pass01", true);
					
					// Определение кому отдать пас
					if (this.PLAYER_ID == 0)
						ball.pass(field.players[1].getAbsX(),field.players[1].getAbsY());
					else 
						ball.pass(field.players[0].getAbsX(),field.players[0].getAbsY());
				}
			}
			// Если персонаж ничего не делает и мяч никем не контролируется
			else if (!ball.isCatched()) {
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
					
					// Останавливаем движение мяча, так как он привязан к игроку
					ball.setVelocityX(0);
					ball.setVelocityY(0);
				}
				// Если скорость больше 14 то мяч убивает игрока
				else {
					if (Can(States.DEAD)) {
						Do(States.DEAD, true);
						this.setJumpVelocity(this.STRENGTH / this.MASS / 1.5f);
					}
					
					// Отмечаем что игрок потерял мяч
					this.catchBall(false);
				}
			}
		}
		
		// Если ниодна из кнопок направления движения не нажата
		if (!actionsListener.getActionStateFor(Controls.UP, this.PLAYER_ID).pressed && 
			!actionsListener.getActionStateFor(Controls.DOWN, this.PLAYER_ID).pressed &&
			!actionsListener.getActionStateFor(Controls.LEFT, this.PLAYER_ID).pressed &&
			!actionsListener.getActionStateFor(Controls.RIGHT, this.PLAYER_ID).pressed &&
			!actionsListener.getActionStateFor(Controls.ACTION1, this.PLAYER_ID).pressed &&
			!actionsListener.getActionStateFor(Controls.ACTION2, this.PLAYER_ID).pressed &&
			!actionsListener.getActionStateFor(Controls.ACTION3, this.PLAYER_ID).pressed) {
			
			// Если анимация была WALKING то отключаем ее
			if (state.get(States.WALKING)) {
				Do(States.STAY, true);
			}
		}
		
		// Если персонаж бежит и не нажата кнопка вверх или вниз
		if (!this.upPressed() && !this.downPressed() && state.get(States.RUN)) {
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
		this.JUMP_VELOCITY += this.JUMP_HEIGHT > 0 ? -8.0f * Gdx.graphics.getDeltaTime() : 0;
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
		// Определение точки куда бить игроку
		ball.kick(this.getKickStrength(), this.netCenter().x, this.netCenter().y, this.upPressed());
		
		// Отмечаем что игрок потерял мяч
		this.catchBall(false);
		
		// Звук удара мяча
		field.sounds.play("kick01", true);
	}
	
	// Определение точки середины ворот
	private Vector2 netCenter() {
		// Определение точки куда бить игроку
		float dstX = (this.direction == Directions.RIGHT) ? field.gates[1].getBottomBar().x : field.gates[0].getBottomBar().x;
		float dstY = field.worldHeight / 2.0f;
		
		return new Vector2(dstX, dstY);
	}
	
	// Находится ли мяч рядом
	public boolean ballIsNear(States state) {
		/* В зависимости от состояния игрока (удар рукой / ногой / рыбкой в полете) будем вносить поправки на каком
		   расстоянии от мяча можно сделать то или иное действие:
		   Например находясь на небольшом расстоянии при выполнении удара ногой нужно разрешать 
		   это действие */
		
		float dX = 0;
		float dY = 0;
		
		switch (state) {
			// При ударе ногой увеличиваем расстояние к мячу на котором игрок может нанести удар на 20px
			case FOOT_KICK:
				dX = 25;
				dY = 0;
			break; 
		}
		
		return Math.abs(ball.getAbsX() - this.getAbsX()) <= 40 + dX && 
				ball.getAbsY() - this.getAbsY() >= -40 && 
				ball.getAbsY() - this.getAbsY() <= 20 &&
				ball.getAbsH() + ball.getDiameter() > this.getAbsH() && 
				ball.getAbsH() < this.getAbsH() + this.getHeight() - 10;
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
					ball.moveBallBy(new Vector2(this.getAbsX()-ball.getAbsX() + 33, this.getAbsY()-ball.getAbsY() - 1));
				}
				else {
					ball.moveBallBy(new Vector2(this.getAbsX()-ball.getAbsX() - 33, this.getAbsY()-ball.getAbsY() - 1));
				}
				
				if (ball.getJumpVelocity() >= 0 && curentState() != States.HEAD_KICK) {
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
		
		// Анимирование персонажа
		animations.get(this.curentState()).setPlayMode(PlayMode.NORMAL);
		currentFrame = animations.get(this.curentState()).getKeyFrame(stateTime, true); 

		// Если окончено текущее действие
		if (animations.get(this.curentState()).isAnimationFinished(stateTime)) {
			/* Если игрок лежал после получение удара, то перед тем как встать должна 
			 * проиграться анимация присевшего игрока */
			if (curentState() == States.LAY_BACK || curentState() == States.LAY_BELLY || curentState() == States.FISH_KICK) {
				Do(States.SIT, true);
			}
			else if (curentState() != States.RUN && curentState() != States.JUMP) {
				Do(States.STAY, true);
			}
		}

        spriteBatch.begin();
        spriteBatch.draw(
    		currentFrame.getTexture(), 
    		this.getX() - this.width() / 2.0f, 
    		this.getY() + this.JUMP_HEIGHT, 
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
    		false
		);
        spriteBatch.end();
        
        // Ресование тени персонажа
        shadow.setX(getX() - 10);
        shadow.setY(getY());
        shadow.setVisibility(this.JUMP_HEIGHT > 0);
//        shadow.draw();
	}
}