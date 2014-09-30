package com.mygdx.crazysoccer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
    private float STRENGTH = 220.0f;
    
    // Текущая скорость движения персонажа при прыжке 
    private float JUMP_VELOCITY = 0.0f;
    
    
    // Параметры персонажа
    private float CURENT_SPEED_X = 0.0f;
    private float CURENT_SPEED_Y = 0.0f;
    private float WALKING_SPEED = 5.0f;
    private float RUN_SPEED = 10.0f;
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
		FOOT_KICK, 			// Удар правой ногой
		JUMP,				// Прыжок
		SIT,				// Присел
		PASS,				// Пасс
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
    		new Animation(0.25f, 
				animationMap[0][5], 
				animationMap[0][6]
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
    		new Animation(0.6f, 
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
						!state.get(States.SIT) &&
						!state.get(States.DEAD) &&
						!state.get(States.LAY_BACK) &&
						!state.get(States.LAY_BELLY) &&
						!state.get(States.JUMP) &&
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
						!state.get(States.JUMP);
			break;
			
			/* 
			 * Персонаж может выполнить удар рукой если он:
			 * 	1. не бъет рукой
			 *  2. не бъет ногой
			 */
			case KNEE_CATCH: 
				isCan = !state.get(States.KNEE_CATCH) && 
						!state.get(States.DEAD) &&
						!state.get(States.SIT) &&
						!state.get(States.LAY_BACK) &&
						!state.get(States.LAY_BELLY) &&
						!state.get(States.FOOT_KICK);
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
						!state.get(States.LAY_BELLY) &&
						!state.get(States.PASS);
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
						!state.get(States.LAY_BELLY) &&
						!state.get(States.PASS);
			break;
			
			/* 
			 * Персонаж может прыгнуть если он:
			 * 	1. не бъет ногой
			 *  2. не бъет рукой
			 *  3. не находится в воздухе
			 */
			case JUMP: 
				isCan = !state.get(States.KNEE_CATCH) && 
						!state.get(States.FOOT_KICK) &&
						!state.get(States.DEAD) &&
						!state.get(States.SIT) &&
						!state.get(States.LAY_BACK) &&
						!state.get(States.LAY_BELLY) &&
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
			case FOOT_KICK:
				disableDirections();
				actionsListener.disableAction(Controls.ACTION1, this.PLAYER_ID);
				this.stateTime = 0.0f;
				
				// Если при ударе ногой игрок не находится в воздухе то останавливаем его мгновенно
				if (this.JUMP_HEIGHT == 0) {
					this.CURENT_SPEED_X = 0.0f;
					this.CURENT_SPEED_Y = 0.0f;
				}
			break;
			
			case PASS:
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
				this.CURENT_SPEED_X = 0.0f;
				this.CURENT_SPEED_Y = 0.0f;
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
	
//	public void Stop(States state) {
//		switch (state) {
//			case RUN:
//				field.sounds.stop("run01");
//			break;
//		}
//		
//		this.state.put(state, false);
//	}
	
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
	
	private float calcKickAlpha() {
		float sinA = 0;
		float delta = 0;
		
		// Получение координаты Х нижней штанги ворот (для определение расстояния к воротам) 
		float gatesBarX = (this.direction == Directions.RIGHT) ? field.gates[1].getBottomBar().x : field.gates[0].getBottomBar().x;
		
		// Опеределение расстояние от игрока до ворот
		float AC = gatesBarX + 8 - ball.getAbsX();
		float BC = Math.abs(field.worldHeight / 2.0f - ball.getAbsY());
		
		sinA = BC / (float)Math.sqrt(AC * AC + BC * BC);
		sinA = (float)(sinA * 180 / Math.PI);
		
		// Привносим случайную величину отклонения при ударе по воротам
		// (иначе мяч всегда летит в центр ворот)
//		delta = (float)Math.random() * 14 - 7;
//		System.out.println(delta);
		
		sinA += delta;
		
		if (direction == Directions.RIGHT) {
			if (ball.getAbsY() < field.worldHeight / 2.0f) sinA = -sinA;
			sinA += 90;
		}
		else {
			if (ball.getAbsY() > field.worldHeight / 2.0f) sinA = -sinA;
			sinA += 270;
		}
		
		return sinA;
	}
	
	@Override
	public void act(float delta) {
		
		if (actionsListener.getActionStateFor(Controls.UP, this.PLAYER_ID).pressed) { 
			if (Can(States.WALKING)) {
				// Если персонаж не бежит, и нажата клавиша вверх
				if (curentState() != States.RUN) {
					Do(States.WALKING, true);
				}
				this.CURENT_SPEED_Y = this.WALKING_SPEED;
			}
		}
		
		if (actionsListener.getActionStateFor(Controls.DOWN, this.PLAYER_ID).pressed) { 
			if (Can(States.WALKING)) {
				// Если персонаж не бежит, и нажата клавиша вверх
				if (curentState() != States.RUN) {
					Do(States.WALKING, true);
				}
				this.CURENT_SPEED_Y = -this.WALKING_SPEED;
			}
		}
		
		
		if (actionsListener.getActionStateFor(Controls.LEFT, this.PLAYER_ID).pressed) { 
			// Персонаж может менять свое направления только находясь на земле
			if (Can(States.WALKING)) {
				this.direction = Directions.LEFT;
				
				// Если было двойное нажатие кнопки, то делаем персонаж бегущим
				if (actionsListener.getActionStateFor(Controls.LEFT, this.PLAYER_ID).doublePressed && Can(States.RUN)) {
					Do(States.RUN, true);
				} 
				else if (Can(States.WALKING)) {
					this.CURENT_SPEED_X = -this.WALKING_SPEED;
					Do(States.WALKING, true);
				}
			}
		}
		
		if (actionsListener.getActionStateFor(Controls.RIGHT, this.PLAYER_ID).pressed) {
			// Персонаж может менять свое направления только находясь на земле
			if (Can(States.WALKING)) {
				this.direction = Directions.RIGHT;
				
				if (actionsListener.getActionStateFor(Controls.RIGHT, this.PLAYER_ID).doublePressed && Can(States.RUN)) {
					Do(States.RUN, true);
				} 
				else if (Can(States.WALKING)) {
					this.CURENT_SPEED_X = this.WALKING_SPEED;
					Do(States.WALKING, true);
				}
			}
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
		
		// Перемещение персонажа
		movePlayerBy(new Vector2(this.CURENT_SPEED_X, this.CURENT_SPEED_Y));
		
		// Если игрок находится в непосредственной близости возле мяча
		if (this.ballIsNear()) {
			// Если в настоящий момент игроком производится удар по мячу
			if (curentState() == States.FOOT_KICK) {
	
				// Если при ударе игрок уже приземляется то придаем ему небольшой импульс
				// чтобы он успел выполнить удар по мячу до момента соприкосновения с землей
				if (this.JUMP_VELOCITY < 0 && this.catchBall()) {
					//this.setVelocityX(this.getVelocityX() / 1.5f);
					this.setJumpVelocity(3f);
				}
				
				// Если мяч находится рядом с игроком когда он сделал замах по нему
				// то выполняем удар по мячу
				if (currentFrame() >= 4 && currentFrame() <= 7) {
					
					ball.kick(55, calcKickAlpha());
					
					// Отмечаем что игрок потерял мяч
					this.catchBall(false);
					
					// Звук удара мяча
					field.sounds.play("kick01", true);
				}
				else if (currentFrame() < 5 && this.getAbsH() > 0 ) {
					if (direction == Directions.RIGHT) 
						ball.setVelocityX(2);
					else 
						ball.setVelocityX(-2);
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
					if (this.getAbsH() > 0 || ball.absVelocity() < 10) {
						
					}
					// Если игрок находится на земле
					else {
						// Если высота полета мяча выше 35 то игрок принимает мяч на грудь 
						if (ball.getAbsH() > 35) {
							Do(States.CHEST_CATCH, true);
						}
						// Иначе на ногу
						else {
							Do(States.KNEE_CATCH, true);
						}
					}
					
					// Отмечаем что игрок заполучил мяч
					this.catchBall(true);
					
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
				}
			}
		}
		
		// Удар правой ногой
		if (actionsListener.getActionStateFor(Controls.ACTION1, this.PLAYER_ID).pressed && Can(States.FOOT_KICK)) {
			Do(States.FOOT_KICK, true);
		}
		
		// Пас
		if (actionsListener.getActionStateFor(Controls.ACTION2, this.PLAYER_ID).pressed && Can(States.PASS)) {
			Do(States.PASS, true);
		}
		
		// Прыжок
		if (actionsListener.getActionStateFor(Controls.ACTION3, this.PLAYER_ID).pressed && Can(States.JUMP)) {
			Do(States.JUMP, true);
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
		if (!actionsListener.getActionStateFor(Controls.UP, this.PLAYER_ID).pressed && 
			!actionsListener.getActionStateFor(Controls.DOWN, this.PLAYER_ID).pressed &&
			 state.get(States.RUN)) 
		{
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
			// Когда персонаж не мертв то при приземлении он приседает
			if (curentState() != States.DEAD) { 
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
	
	// Находится ли мяч рядом
	public boolean ballIsNear() {
		return Math.abs(ball.getAbsX() - this.getAbsX()) <= 40 && 
				ball.getAbsY() - this.getAbsY() >= -40 && 
				ball.getAbsY() - this.getAbsY() <= 20 &&
				ball.getAbsH() + ball.getDiameter() > this.getAbsH() && 
				ball.getAbsH() < this.getAbsH() + this.getHeight();
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
			if (this.catchBall() && ball.inField()) {
				if (this.direction == Directions.RIGHT) {
					ball.moveBallBy(new Vector2(this.getAbsX()-ball.getAbsX() + 33, this.getAbsY()-ball.getAbsY() - 1));
				}
				else {
					ball.moveBallBy(new Vector2(this.getAbsX()-ball.getAbsX() - 33, this.getAbsY()-ball.getAbsY() - 1));
				}
				ball.setAbsH(this.getAbsH());
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
		animations.get(this.curentState()).setPlayMode(PlayMode.LOOP);
		currentFrame = animations.get(this.curentState()).getKeyFrame(stateTime, true); 

		// Если окончено текущее действие
		if (animations.get(this.curentState()).isAnimationFinished(stateTime)) {
			/* Если игрок лежал после получение удара, то перед тем как встать должна 
			 * проиграться анимация присевшего игрока */
			if (this.curentState() == States.LAY_BACK || this.curentState() == States.LAY_BELLY) {
				Do(States.SIT, true);
			}
			else if (this.curentState() != States.RUN && this.curentState() != States.JUMP) {
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