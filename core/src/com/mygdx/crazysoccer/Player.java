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
import com.mygdx.crazysoccer.Vars;

public class Player extends Actor {
	
	// Идентификатор игрока (используется при определении нажатия управляющих кнопок)
	public int PLAYER_ID = 0;
		
	private static final int FRAME_COLS = 9;    
    private static final int FRAME_ROWS = 7;     
    
    // Спрайт для отрисовки персонажа
    public SpriteBatch spriteBatch;
    
    // Параметры спрайта
    public int SPRITE_WIDTH = 118;
    public int SPRITE_HEIGHT = 118;
    public float SPRITE_SCALE = 0.8f;
    
    // Парамерты прыжка персонажа
    public float CURENT_JUMP_HEIGHT = 0;
    public int MAX_JUMP_HEIGHT = 120;
    public float JUMP_INCREMENT = 9;
    public int JUMP_FROM = 0;
    
    // Параметры персонажа
    public float CURENT_SPEED_X = 0.0f;
    public float WALKING_SPEED = 1.5f;
    public float RUN_SPEED = 4.0f;
    
	// Перечень возможных состояний героя
	public static enum States {
		STAY,     			// Состояние покоя
		WALKING,			// Ходьба
		RUN,	  			// Бег 
		FATIGUE,  			// Усталость
		CELEBRATE,			// Празднование победы
		LEFT_HAND_KICK,		// Удар левой рукой
		RIGHT_FOOT_KICK, 	// Удар правой ногой
		JUMP				// Прыжок
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
    
	// Кадры анимации покоя
	public TextureRegion[] stayFrames; 
	// Кадры анимации хотьбы
	public TextureRegion[] walkFrames; 
	// Кадры анимации бега
	public TextureRegion[] runFrames;
	// Кадры анимации усталости
	public TextureRegion[] fetigueFrames;
	// Кадры анимации праздования победы
	public TextureRegion[] celebrateFrames;
	// Кадры анимации праздования победы
	public TextureRegion[] leftHandHitFrames;
	// Кадры анимации праздования победы
	public TextureRegion[] rightFootHitFrames;
	// Кадры анимации праздования победы
	public TextureRegion[] jumpFrames;
	
	// Текущий кадр анимации
	public TextureRegion currentFrame; 
	
	// Тень персонажа
	public Shadow shadow;
	
	public Texture animationSheet;
	public TextureRegion[][] animationMap;
    
	// Текущее состояние героя
	public Map<States, Boolean> state = new HashMap<States, Boolean>();
	
    public float stateTime = 0.0f; 
	
	public Player(int playerId) {
		super();
		
		this.PLAYER_ID = playerId;
		
		animations = new HashMap<States, Animation>();
		
		setX(Vars.WINDOW_WIDTH / 2.0f);
        setY(Vars.WINDOW_HEIGHT / 2.0f);        
        
        // Первоначальная инициализация состояний анимаций персонажа
//        stopAll();
        Do(States.STAY, true);
        
        // Изначальное направление персонажа
        direction = Directions.RIGHT;
		
        // Загрузка изображения с анимацией персонажа
        animationSheet = new Texture(Gdx.files.internal("rikki.gif"));
        
        // Загрузка карты анимаций персонажа
        animationMap = TextureRegion.split(animationSheet, animationSheet.getWidth()/FRAME_COLS, animationSheet.getHeight()/FRAME_ROWS);
        
        // Спрайт для отрисовки персонажа
        spriteBatch = new SpriteBatch(); 
        
        // Создаем анимацию покоя
        stayFrames = new TextureRegion[1];
        stayFrames[0] = animationMap[1][0];
        animations.put(States.STAY, new Animation(1.0f, stayFrames));
        
        // Создаем анимацию ходьбы
        walkFrames = new TextureRegion[2];
        walkFrames[0] = animationMap[0][0];
        walkFrames[1] = animationMap[0][1];
        animations.put(States.WALKING, new Animation(0.25f, walkFrames));
        
        // Создаем анимацию бега
        runFrames = new TextureRegion[6];
        runFrames[0] = animationMap[0][3];
        runFrames[1] = animationMap[0][4];
        runFrames[2] = animationMap[0][5];
        runFrames[3] = animationMap[0][6];
        runFrames[4] = animationMap[0][7];
        runFrames[5] = animationMap[0][8];
        animations.put(States.RUN, new Animation(0.10f, runFrames));
        
        // Создаем анимацию усталости
        fetigueFrames = new TextureRegion[2];
        fetigueFrames[0] = animationMap[4][0];
        fetigueFrames[1] = animationMap[4][1];
        animations.put(States.FATIGUE, new Animation(0.25f, fetigueFrames));
        
        // Создаем анимацию празднования победы
        celebrateFrames = new TextureRegion[2];
        celebrateFrames[0] = animationMap[6][1];
        celebrateFrames[1] = animationMap[6][2];
        animations.put(States.CELEBRATE, new Animation(0.25f, celebrateFrames));
        
        // Создаем анимацию удара левой рукой
        leftHandHitFrames = new TextureRegion[3];
        leftHandHitFrames[0] = animationMap[1][2];
        leftHandHitFrames[1] = animationMap[1][1];
        leftHandHitFrames[2] = animationMap[1][2];
        animations.put(States.LEFT_HAND_KICK, new Animation(0.04f, leftHandHitFrames));
        
        // Создаем анимацию удара левой рукой
        rightFootHitFrames = new TextureRegion[3];
        rightFootHitFrames[0] = animationMap[1][4];
        rightFootHitFrames[1] = animationMap[1][3];
        rightFootHitFrames[2] = animationMap[1][4];
        animations.put(States.RIGHT_FOOT_KICK, new Animation(0.06f, rightFootHitFrames));
        
        // Создаем анимацию прыжка
        jumpFrames = new TextureRegion[1];
//        jumpFrames[0] = animationMap[2][5];
        jumpFrames[0] = animationMap[2][6];
//        jumpFrames[2] = animationMap[2][5];
        animations.put(States.JUMP, new Animation(0.2f, jumpFrames));
        
        shadow = new Shadow();
	}
	
	// Проверка необходимости зеркалирования спрайта персонажа
	private boolean getFlipX() {
		return (this.direction == Directions.LEFT);
	}
	
	public void setActionsListener(Actions al) {
		this.actionsListener = al;
	}
	
	public void movePlayerBy(Vector2 movePoint) {
		
		if (Field.camera.position.x <= Vars.WINDOW_WIDTH / 2.0f) {
			if (getX() + movePoint.x > 0) {
				setX(getX() + movePoint.x);
			} else {
				setX(1);
			}
			
			Field.camera.position.set(Vars.WINDOW_WIDTH / 2.0f, Field.camera.position.y, 0);
			
			if (getX() > Vars.WINDOW_WIDTH / 2.0f) {
				Field.camera.position.set(Vars.WINDOW_WIDTH / 2.0f + 1, Field.camera.position.y, 0);
			}
		} else if (getX() > Vars.WINDOW_WIDTH / 2.0f) {
			Field.camera.position.set(Field.camera.position.x + movePoint.x, Field.camera.position.y + movePoint.y, 0);
			Field.camera.update();
		}
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
				isCan = !state.get(States.LEFT_HAND_KICK)  && 
						!state.get(States.RIGHT_FOOT_KICK) &&
						!state.get(States.JUMP);
			break;
			
			/* 
			 * Персонаж может бежать если он:
			 * 	1. не бъет ногой
			 *  2. не бъет рукой
			 */
			case RUN: 
				isCan = !state.get(States.LEFT_HAND_KICK)  && 
						!state.get(States.RIGHT_FOOT_KICK) &&
						!state.get(States.JUMP);
			break;
			
			/* 
			 * Персонаж может выполнить удар рукой если он:
			 * 	1. не бъет рукой
			 *  2. не бъет ногой
			 */
			case LEFT_HAND_KICK: 
				isCan = !state.get(States.LEFT_HAND_KICK)  && 
						!state.get(States.RIGHT_FOOT_KICK);
			break;
			
			/* 
			 * Персонаж может выполнить удар ногой если он:
			 * 	1. не бъет ногой
			 *  2. не бъет рукой
			 */
			case RIGHT_FOOT_KICK: 
				isCan = !state.get(States.LEFT_HAND_KICK)  && 
						!state.get(States.RIGHT_FOOT_KICK);
			break;
			
			/* 
			 * Персонаж может прыгнуть если он:
			 * 	1. не бъет ногой
			 *  2. не бъет рукой
			 *  3. не находится в воздухе
			 */
			case JUMP: 
				isCan = !state.get(States.LEFT_HAND_KICK)  && 
						!state.get(States.RIGHT_FOOT_KICK) &&
						!state.get(States.JUMP);
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
			case RIGHT_FOOT_KICK:
				disableDirections();
				actionsListener.disableAction(Controls.ACTION1, this.PLAYER_ID);
			break;
			
			case LEFT_HAND_KICK: 
				disableDirections();
				actionsListener.disableAction(Controls.ACTION2, this.PLAYER_ID);
			break;
			
			case JUMP: 
				actionsListener.disableAction(Controls.ACTION3, this.PLAYER_ID);
			break;
			
			case RUN: 
				this.CURENT_SPEED_X = (this.direction == Directions.RIGHT) ? this.RUN_SPEED : -this.RUN_SPEED;
			break;
			
			case WALKING: 
				this.CURENT_SPEED_X = (this.direction == Directions.RIGHT) ? this.WALKING_SPEED : -this.WALKING_SPEED;
			break;
			
			case STAY: 
				this.CURENT_SPEED_X = 0.0f;
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
	
	// Отключение действия
	private void disableAction(Action disableAction) {
		actionsListener.remove(disableAction);
	}
	
	// Полностью остановить игрока
	private void stopAll() {
		for (int i = 0; i < States.values().length; i++) {
			state.put(States.values()[i], false);
		}
		//this.CURENT_SPEED_X = 0.0f;
	}
	
	@Override
	public void act(float delta) {
		
		if (actionsListener.getActionStateFor(Controls.UP, this.PLAYER_ID).pressed) { 
			// Если персонаж не бежи, и нажата клавиша вверх
			if (curentState() != States.RUN && Can(States.WALKING)) {
				Do(States.WALKING, true);
			}
			if (curentState() != States.JUMP) {
				movePlayerBy(new Vector2(0, this.WALKING_SPEED));
			}
		}
		
		if (actionsListener.getActionStateFor(Controls.DOWN, this.PLAYER_ID).pressed) { 
			// Если персонаж не бежи, и нажата клавиша вниз
			if (curentState() != States.RUN && Can(States.WALKING)) {
				Do(States.WALKING, true);
			}
			if (curentState() != States.JUMP) {
				movePlayerBy(new Vector2(0, -this.WALKING_SPEED));
			}
		}
		
		
		if (actionsListener.getActionStateFor(Controls.LEFT, this.PLAYER_ID).pressed) { 
			this.direction = Directions.LEFT;
			
			// Если было двойное нажатие кнопки, то делаем персонаж бегущим
			if (actionsListener.getActionStateFor(Controls.LEFT, this.PLAYER_ID).doublePressed && Can(States.RUN)) {
				Do(States.RUN, true);
			} 
			else if (Can(States.WALKING)) {
				Do(States.WALKING, true);
			}
		}
		
		if (actionsListener.getActionStateFor(Controls.RIGHT, this.PLAYER_ID).pressed) { 
			this.direction = Directions.RIGHT;
			
			if (actionsListener.getActionStateFor(Controls.RIGHT, this.PLAYER_ID).doublePressed && Can(States.RUN)) {
				Do(States.RUN, true);
			} 
			else if (Can(States.WALKING)) {
				Do(States.WALKING, true);
			}
		}
		
		// Перемещение персонажа
		movePlayerBy(new Vector2(this.CURENT_SPEED_X, 0));
		
		// Удар правой ногой
		if (actionsListener.getActionStateFor(Controls.ACTION1, this.PLAYER_ID).pressed && Can(States.RIGHT_FOOT_KICK)) {
			stateTime = 0.0f;
			
			Do(States.RIGHT_FOOT_KICK, true);
		}
		
		// Удар левой рукой
		if (actionsListener.getActionStateFor(Controls.ACTION2, this.PLAYER_ID).pressed && Can(States.LEFT_HAND_KICK)) {
			stateTime = 0.0f;
			
			Do(States.LEFT_HAND_KICK, true);
		}
		
		// Прыжок
		if (actionsListener.getActionStateFor(Controls.ACTION3, this.PLAYER_ID).pressed && Can(States.JUMP)) {
			stateTime = 0.0f;
			
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

		// Если ничего не нажималось, но нужно выполнять какуюто анимацию
		if (curentState() == States.RUN) {
			if (this.direction == Directions.RIGHT) {
				movePlayerBy(new Vector2(this.RUN_SPEED, 0));
			} 
			else if (this.direction == Directions.LEFT) {
				movePlayerBy(new Vector2(-this.RUN_SPEED, 0));
			}
		} 
		else if (curentState() == States.JUMP) {
			this.CURENT_JUMP_HEIGHT += this.JUMP_INCREMENT;
			
			movePlayerBy(new Vector2(this.CURENT_SPEED_X, 0));
			
			// Если персонаж достиг наивысшей точки прыжка, начинаем его приземление
			if (this.CURENT_JUMP_HEIGHT >= this.MAX_JUMP_HEIGHT) {
				this.JUMP_INCREMENT = -this.JUMP_INCREMENT;
			}
			
			if (this.JUMP_INCREMENT > 0) {
				this.JUMP_INCREMENT *= 0.94f;
			}
			else {
				this.JUMP_INCREMENT *= 1.11f;
			}
			
			if (this.CURENT_JUMP_HEIGHT <= 0) {
				Do(States.STAY, true);
				this.CURENT_JUMP_HEIGHT = 0;
				this.JUMP_INCREMENT = -this.JUMP_INCREMENT;
				this.JUMP_INCREMENT = 9; 
			}
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		stateTime += Gdx.graphics.getDeltaTime();
		
		// Анимирование персонажа
		animations.get(this.curentState()).setPlayMode(PlayMode.LOOP);
		currentFrame = animations.get(this.curentState()).getKeyFrame(stateTime, true); 
		
		// Если окончено текущее действие
		if (animations.get(this.curentState()).isAnimationFinished(stateTime)) {
			if (this.curentState() != States.RUN && this.curentState() != States.JUMP) {
				Do(States.STAY, true);
			}
		}

        spriteBatch.begin();
        spriteBatch.draw(
    		currentFrame.getTexture(), 
    		this.getX(), 
    		this.getY() + this.CURENT_JUMP_HEIGHT, 
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
        shadow.setXY(getX() + 25, getY()-5);
        shadow.setVisibility(this.CURENT_JUMP_HEIGHT > 0);
        shadow.draw();
	}
}