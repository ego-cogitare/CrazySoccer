package com.mygdx.crazysoccer;

import java.util.Map;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion; 
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.crazysoccer.Vars;

public class Player extends Actor {

	private static final int FRAME_COLS = 9;    
    private static final int FRAME_ROWS = 7;     
    
    // Спрайт для отрисовки персонажа
    public SpriteBatch spriteBatch;
    
    // Высота спрайта
    public int spriteHeight = 90;
    
	// Перечень возможных состояний героя
	public static enum States {
		STAY,     			// Состояние покоя
		WALKING,  			// Ходьба
		RUN_RIGHT,	  		// Бег вправо
		RUN_LEFT,	  		// Бег влево
		FATIGUE,  			// Усталость
		CELEBRATE,			// Празднование победы
		LEFT_HAND_KICK		// Удар левой рукой
	} 
	
	private Actions actionsListener;
    
    // Набор анимаций персонажа
    public Map<States, Animation> animations;
    
	// Кадры анимации покоя
	public TextureRegion[] stayFrames; 
	// Кадры анимации хотьбы
	public TextureRegion[] walkFrames; 
	// Кадры анимации бега вправо
	public TextureRegion[] runRightFrames;
	// Кадры анимации бега влево
	public TextureRegion[] runLeftFrames;
	// Кадры анимации усталости
	public TextureRegion[] fetigueFrames;
	// Кадры анимации праздования победы
	public TextureRegion[] celebrateFrames;
	// Кадры анимации праздования победы
	public TextureRegion[] leftHandHitFrames;
	
	
	public TextureRegion currentFrame; 
	
	public Texture animationSheet;
	public TextureRegion[][] animationMap;
    
	// Текущее состояние героя
	public Map<States, Boolean> states = new HashMap<States, Boolean>();
	
	
	
    public float stateTime; 
	
	public Player() {
		super();
		
		animations = new HashMap<States, Animation>();
		
		setX(Vars.WINDOW_WIDTH / 2.0f);
        setY(10);        
        stateTime = 0f;
        
        // Первоначальная инициализация состояний анимаций героя
        stopAll();
		
        // Загрузка изображения с анимацией героя
        animationSheet = new Texture(Gdx.files.internal("rikki.gif"));
        
        // Загрузка карты анимаций героя
        animationMap = TextureRegion.split(animationSheet, animationSheet.getWidth()/FRAME_COLS, animationSheet.getHeight()/FRAME_ROWS);
        
        // Спрайт для отрисовки героя
        spriteBatch = new SpriteBatch(); 
        
        // Создаем анимацию покоя
        stayFrames = new TextureRegion[1];
        stayFrames[0] = animationMap[0][2];
        animations.put(States.STAY, new Animation(0.5f, stayFrames));
        
        // Создаем анимацию ходьбы
        walkFrames = new TextureRegion[2];
        walkFrames[0] = animationMap[0][0];
        walkFrames[1] = animationMap[0][1];
        animations.put(States.WALKING, new Animation(0.25f, walkFrames));
        
        // Создаем анимацию бега вправо
        runRightFrames = new TextureRegion[6];
        runRightFrames[0] = animationMap[0][3];
        runRightFrames[1] = animationMap[0][4];
        runRightFrames[2] = animationMap[0][5];
        runRightFrames[3] = animationMap[0][6];
        runRightFrames[4] = animationMap[0][7];
        runRightFrames[5] = animationMap[0][8];
        animations.put(States.RUN_RIGHT, new Animation(0.10f, runRightFrames));
        
        // Создаем анимацию бега влево
        animations.put(States.RUN_LEFT, new Animation(0.10f, runRightFrames));
        
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
        leftHandHitFrames = new TextureRegion[4];
        leftHandHitFrames[0] = animationMap[1][0];
        leftHandHitFrames[1] = animationMap[1][2];
        leftHandHitFrames[2] = animationMap[1][1];
//        leftHandHitFrames[3] = animationMap[1][2];
        leftHandHitFrames[3] = animationMap[1][0];
        animations.put(States.LEFT_HAND_KICK, new Animation(0.07f, leftHandHitFrames));
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
			states.put(States.values()[i], false);
		}
		states.put(States.STAY, true);
	}
	
	@Override
	public void act(float delta) {
		// Если ничего не нажималось, но нужно выполнять какуюто анимацию
		if (states.get(States.RUN_RIGHT)) {
			movePlayerBy(new Vector2(12,0));
		}
		
		if (states.get(States.RUN_LEFT)) {
			movePlayerBy(new Vector2(-12,0));
		}
		
		if (actionsListener.get(Vars.Action.ACTION2).state) {
			stopAll();
			states.put(States.LEFT_HAND_KICK, true);
		}
		
		if (actionsListener.get(Vars.Action.UP).state) {
			// Если персонаж бежит и нажата кнопка вверх
			if (!states.get(States.RUN_RIGHT) && !states.get(States.RUN_LEFT)) {
				stopAll();
				states.put(States.WALKING, true);
			}
			movePlayerBy(new Vector2(0,2));
		}
		
		if (actionsListener.get(Vars.Action.DOWN).state) {
			// Если персонаж бежит и нажата кнопка вверх
			if (!states.get(States.RUN_RIGHT) && !states.get(States.RUN_LEFT)) {
				stopAll();
				states.put(States.WALKING, true);
			}
			movePlayerBy(new Vector2(0,-2));
		}
		
		if (actionsListener.get(Vars.Action.LEFT).state) {
			if (actionsListener.get(Vars.Action.LEFT).doublePressed) {
				stopAll();
				states.put(States.RUN_LEFT, true);
			} else {
				stopAll();
				states.put(States.WALKING, true);
				movePlayerBy(new Vector2(-3,0));
			}
		}
		
		if (actionsListener.get(Vars.Action.RIGHT).state) {
			//actionsListener.debug();
			if (actionsListener.get(Vars.Action.RIGHT).doublePressed) {
				stopAll();
				states.put(States.RUN_RIGHT, true);
			} else {
				stopAll();
				states.put(States.WALKING, true);
				movePlayerBy(new Vector2(3,0));
			}
		}
		
		// Если ниодна из кнопок направления движения не нажата
		if (!actionsListener.get(Vars.Action.UP).state   && 
			!actionsListener.get(Vars.Action.DOWN).state &&
			!actionsListener.get(Vars.Action.LEFT).state &&
			!actionsListener.get(Vars.Action.RIGHT).state) {
			
			// Если анимация была WALKING то отключаем ее
			if (states.get(States.WALKING)) {
				states.put(States.WALKING, false);
			}
		}
//		System.out.println(states);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		stateTime += Gdx.graphics.getDeltaTime();
		
		// Анимирование персонажа
		for (int i = 0; i < States.values().length; i++) {
			if (states.get(States.values()[i])) {
				currentFrame = animations.get(States.values()[i]).getKeyFrame(stateTime, true); 
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
		
		// Установка высоты спрайта
		currentFrame.setRegionHeight(this.spriteHeight);
		currentFrame.setRegionWidth(this.spriteHeight);
		
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, this.getX(), this.getY());       
        spriteBatch.end();
	}
}