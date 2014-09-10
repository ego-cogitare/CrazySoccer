package com.mygdx.crazysoccer;

import java.awt.event.ActionListener;
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
import com.mygdx.crazysoccer.Vars;

public class Player extends Actor {

	private static final int FRAME_COLS = 9;    
    private static final int FRAME_ROWS = 7;     
    
    // Спрайт для отрисовки персонажа
    public SpriteBatch spriteBatch;
    
    // Параметры спрайта
    public int SPRITE_WIDTH = 118;
    public int SPRITE_HEIGHT = 118;
    public float SPRITE_SCALE = 0.65f;
    
	// Перечень возможных состояний героя
	public static enum States {
		STAY,     			// Состояние покоя
		WALKING,			// Ходьба
		RUN,	  			// Бег 
		FATIGUE,  			// Усталость
		CELEBRATE,			// Празднование победы
		LEFT_HAND_KICK,		// Удар левой рукой
		RIGHT_FOOT_KICK 	// Удар правой ногой
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
	
	
	public TextureRegion currentFrame; 
	
	public Texture animationSheet;
	public TextureRegion[][] animationMap;
    
	// Текущее состояние героя
	public Map<States, Boolean> state = new HashMap<States, Boolean>();
	
    public float stateTime = 0.0f; 
	
	public Player() {
		super();
		
		animations = new HashMap<States, Animation>();
		
		setX(Vars.WINDOW_WIDTH / 2.0f);
        setY(10);        
        
        // Первоначальная инициализация состояний анимаций персонажа
        stopAll();
        
        // Изначальное направление персонажа
        direction = Directions.RIGHT;
		
        // Загрузка изображения с анимацией персонажа
        animationSheet = new Texture(Gdx.files.internal("rikki128.gif"));
        
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
        leftHandHitFrames = new TextureRegion[7];
        leftHandHitFrames[0] = animationMap[1][0];
        leftHandHitFrames[1] = animationMap[1][2];
        leftHandHitFrames[2] = animationMap[1][1];
        leftHandHitFrames[3] = animationMap[1][1];
        leftHandHitFrames[4] = animationMap[1][2];
        leftHandHitFrames[5] = animationMap[1][0];
        leftHandHitFrames[6] = animationMap[1][0];
        animations.put(States.LEFT_HAND_KICK, new Animation(0.03f, leftHandHitFrames));
        
        // Создаем анимацию удара левой рукой
        rightFootHitFrames = new TextureRegion[4];
        rightFootHitFrames[0] = animationMap[1][4];
        rightFootHitFrames[1] = animationMap[1][3];
        rightFootHitFrames[2] = animationMap[1][4];
        rightFootHitFrames[3] = animationMap[1][0];
        animations.put(States.RIGHT_FOOT_KICK, new Animation(0.06f, rightFootHitFrames));
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
	
	// Полностью остановить игрока
	public void stopAll() {
		for (int i = 0; i < States.values().length; i++) {
			state.put(States.values()[i], false);
		}
		state.put(States.STAY, true);
	}
	
	@Override
	public void act(float delta) {

		// Если ничего не нажималось, но нужно выполнять какуюто анимацию
		if (state.get(States.RUN)) {
			if (this.direction == Directions.RIGHT) {
				movePlayerBy(new Vector2(7,0));
			} 
			else if (this.direction == Directions.LEFT) {
				movePlayerBy(new Vector2(-7,0));
			}
		}
		
		// Если не нажата ни кнопка удара ногой ни кнопка удара рукой, то можно 
		if (!actionsListener.get(Action.ACTION1).state && !actionsListener.get(Action.ACTION2).state) {
			
			if (actionsListener.get(Action.UP).state) {
				// Если персонаж бежит и нажата кнопка вверх
				if (!state.get(States.RUN)) {
					stopAll();
					state.put(States.WALKING, true);
				}
				movePlayerBy(new Vector2(0,1.3f));
			}
			
			if (actionsListener.get(Action.DOWN).state) {
				// Если персонаж бежит и нажата кнопка вверх
				if (!state.get(States.RUN)) {
					stopAll();
					state.put(States.WALKING, true);
				}
				movePlayerBy(new Vector2(0,-1.3f));
			}
			
			if (actionsListener.get(Action.LEFT).state) {
				this.direction = Directions.LEFT;
				
				stopAll();
				
				// Если было двойное нажатие кнопки, то делаем персонаж бегущим
				if (actionsListener.get(Action.LEFT).doublePressed) {
					state.put(States.RUN, true);
				} else {
					state.put(States.WALKING, true);
					movePlayerBy(new Vector2(-2,0));
				}
			}
			
			if (actionsListener.get(Action.RIGHT).state) {
				this.direction = Directions.RIGHT;
				
				stopAll();
				
				if (actionsListener.get(Action.RIGHT).doublePressed) {
					state.put(States.RUN, true);
				} else {
					state.put(States.WALKING, true);
					movePlayerBy(new Vector2(2,0));
				}
			}
		}
		
		// Удар правой ногой
		if (actionsListener.get(Action.ACTION1).state && !state.get(States.RIGHT_FOOT_KICK) && !state.get(States.LEFT_HAND_KICK)) {
			stateTime = 0.0f;
			stopAll();
			state.put(States.RIGHT_FOOT_KICK, true);
			
			actionsListener.remove(Action.RIGHT);
			actionsListener.remove(Action.LEFT);
			actionsListener.remove(Action.ACTION1);
		}
		
		// Удар левой рукой
		if (actionsListener.get(Action.ACTION2).state && !state.get(States.RIGHT_FOOT_KICK) && !state.get(States.LEFT_HAND_KICK)) {
			stateTime = 0.0f;
			stopAll();
			state.put(States.LEFT_HAND_KICK, true);
			
			actionsListener.remove(Action.RIGHT);
			actionsListener.remove(Action.LEFT);
			actionsListener.remove(Action.ACTION2);
		}
		
		// Если ниодна из кнопок направления движения не нажата
		if (!actionsListener.get(Action.UP).state   && 
			!actionsListener.get(Action.DOWN).state &&
			!actionsListener.get(Action.LEFT).state &&
			!actionsListener.get(Action.RIGHT).state) {
			
			// Если анимация была WALKING то отключаем ее
			if (state.get(States.WALKING)) {
				state.put(States.WALKING, false);
			}
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		stateTime += Gdx.graphics.getDeltaTime();
		
		// Анимирование персонажа
		for (int i = 0; i < States.values().length; i++) {
			if (state.get(States.values()[i])) {
				animations.get(States.values()[i]).setPlayMode(PlayMode.LOOP);
				currentFrame = animations.get(States.values()[i]).getKeyFrame(stateTime, true); 
				
				// Если окончено действие удара левой рукой
				if (animations.get(States.LEFT_HAND_KICK).isAnimationFinished(stateTime)) {
					state.put(States.LEFT_HAND_KICK, false);
				}
				
				if (animations.get(States.RIGHT_FOOT_KICK).isAnimationFinished(stateTime)) {
					state.put(States.RIGHT_FOOT_KICK, false);
				}
				
				//System.out.println("Animation : " + States.values()[i] + "\nDuration: " + animations.get(States.values()[i]).getAnimationDuration());
			}
		}

		
//		if (states.get(States.WALKING)) {
//			currentFrame = animations.get(States.WALKING).getKeyFrame(stateTime, true); 
//		} 
//		else if (states.get(States.RUN)) {
//			currentFrame = animations.get(States.RUN).getKeyFrame(stateTime, true); 
//		} 
//		else if (states.get(States.FATIGUE)) {
//			currentFrame = animations.get(States.FATIGUE).getKeyFrame(stateTime, true); 
//		}
//		else if (states.get(States.CELEBRATE)) {
//			currentFrame = animations.get(States.CELEBRATE).getKeyFrame(stateTime, true); 
//		}
//		else if (states.get(States.LEFT_HAND_KICK)) {
//			currentFrame = animations.get(States.LEFT_HAND_KICK).getKeyFrame(stateTime, true);
//		}
//		else {
//			currentFrame = animations.get(States.STAY).getKeyFrame(stateTime, true); 
//		} 
		
		// Установка размеров спрайта спрайта
        spriteBatch.begin();
        spriteBatch.draw(
    		currentFrame.getTexture(), 
    		this.getX(), 
    		this.getY(), 
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
	}
}