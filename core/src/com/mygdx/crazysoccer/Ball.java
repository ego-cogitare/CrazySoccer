package com.mygdx.crazysoccer;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.crazysoccer.Actions.Controls;

public class Ball extends Actor {
	
	private Field field;
	
	private static final int FRAME_COLS = 8;    
    private static final int FRAME_ROWS = 8;     
    
    // Спрайт для отрисовки персонажа
    public SpriteBatch spriteBatch;

	// Параметры скорости и расположения мяча
	public float CURENT_SPEED_X = 0.0f;
    public float CURENT_SPEED_Y = 0.0f;
    public float POS_X = 0.0f;
    public float POS_Y = 0.0f;
    
    public int SPRITE_WIDTH = 16;
    public int SPRITE_HEIGHT = 16;
    public float SPRITE_SCALE = 3.0f;
    
    // Парамерты высоты расположения мяча
    private float JUMP_HEIGHT = 0;
    
    // Набор анимаций мяча
    public Map<States, Animation> animations;
    
	// Кадры анимации покоя
	public TextureRegion[] stayFrames; 
    
    // Текущее состояние мяча
 	public Map<States, Boolean> state = new HashMap<States, Boolean>();
    
    public static enum States {
		STOP,     			// Состояние покоя
	}
	
    // Текущий кадр анимации
 	public TextureRegion currentFrame; 
 	
 	// Тень мяча
 	public Shadow shadow;
 	
 	public Texture animationSheet;
 	public TextureRegion[][] animationMap;
 	
    public float stateTime = 0.0f; 
    
    
    
    // Слушатель ввода
 	private Actions actionsListener;
	public void setActionsListener(Actions al) {
		this.actionsListener = al;
	}
    
    
	public Ball() {
		super();
		
		animations = new HashMap<States, Animation>();
		
        // Первоначальная инициализация состояний анимаций персонажа
        Do(States.STOP, true);
        
        // Загрузка изображения с анимацией персонажа
        animationSheet = new Texture(Gdx.files.internal("ball.png"));
        
        // Загрузка карты анимаций персонажа
        animationMap = TextureRegion.split(animationSheet, animationSheet.getWidth()/FRAME_COLS, animationSheet.getHeight()/FRAME_ROWS);
        
        // Спрайт для отрисовки персонажа
        spriteBatch = new SpriteBatch(); 
        
        // Создаем анимацию покоя
        stayFrames = new TextureRegion[1];
        stayFrames[0] = animationMap[0][0];
        animations.put(States.STOP, new Animation(1.0f, stayFrames));
        
        shadow = new Shadow();
	}
	
	public void attachField(Field f) {
		this.field = f;
	}
	
	public boolean Can(States stateToCheck) {
		boolean isCan = false;
		
		switch (stateToCheck) {
		
		}
		
		return isCan;
	}
	
	// Постановка задания на выполнение действия
	public void Do(States state, boolean stopAll) {
		// Если установлен флаг stopAll то останавливаем все анимации
		if (stopAll) this.stopAll();
		
		switch (state) {
			
		}
	}
	
	// Текущее состояние персонажа
	public States curentState() {
		for (int i = 0; i < States.values().length; i++) {
			if (state.get(States.values()[i])) {
				return States.values()[i];
			}
		}
		return States.STOP;
	}
	
	private void movePlayerBy(Vector2 movePoint) {
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		float camX = Field.camera.position.x;
		float camY = Field.camera.position.y;
		
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
			
			// Если персонаж достик границы игрового мира, то останавливаем его
			if (doStop) Do(States.STOP,true);
			
			// Перемещение по оси X
			if (this.POS_X < field.fieldMaxWidth / 2.0f) {
				if (this.POS_X >= w / 2) {
					if (this.POS_X + movePoint.x < w / 2.0f) {
						field.camera.position.set(w / 2.0f, camY, 0);
						setX(movePoint.x + this.POS_X);
					}
					else {
						field.camera.position.set(camX + movePoint.x, camY, 0);
					}
					this.POS_X += movePoint.x;
				} 
				else {
					if (this.POS_X + movePoint.x > w / 2.0f) {
						setX(w / 2.0f);
						field.camera.position.set(camX + this.POS_X + movePoint.x - w / 2.0f, camY, 0);
					}
					else {
						setX(getX() + movePoint.x);
					}
					this.POS_X += movePoint.x;
				}
			}
			else {
				if (getX() <= w / 2.0f) {
					if (this.POS_X + movePoint.x <= field.camMaxX) {
						field.camera.position.set(camX + movePoint.x, camY, 0);
					}
					else {
						field.camera.position.set(field.camMaxX, camY, 0);
						setX(w / 2.0f + this.POS_X + movePoint.x - field.camMaxX);
					}
					this.POS_X += movePoint.x;
				} 
				else {
					if (this.POS_X + movePoint.x > field.camMaxX) {
						field.camera.position.set(field.camMaxX, camY, 0);
						setX(w / 2.0f + this.POS_X + movePoint.x - field.camMaxX);
					}
					else {
						field.camera.position.set(this.POS_X + movePoint.x, camY, 0);
						setX(w / 2.0f);
					}
					this.POS_X += movePoint.x;
				}
			}
			
			// Перемещение по оси Y
			camX = Field.camera.position.x;
			camY = Field.camera.position.y;
			
			if (this.POS_Y < field.fieldHeight / 2.0f) {
				if (this.POS_Y >= h / 2) {
					if (this.POS_Y + movePoint.y < h / 2.0f) {
						field.camera.position.set(camX, h / 2.0f, 0);
						setY(movePoint.y + this.POS_Y);
					}
					else {
						field.camera.position.set(camX, camY + movePoint.y, 0);
					}
					this.POS_Y += movePoint.y;
				} 
				else {
					if (this.POS_Y + movePoint.y > h / 2.0f) {
						setY(h / 2.0f);
						field.camera.position.set(camX, camY + this.POS_Y + movePoint.y - h / 2.0f, 0);
					}
					else {
						setY(getY() + movePoint.y);
					}
					this.POS_Y += movePoint.y;
				}
			}
			else {
				if (getY() <= h / 2.0f) {
					if (this.POS_Y + movePoint.y <= field.camMaxY) {
						field.camera.position.set(camX, camY + movePoint.y, 0);
					}
					else {
						field.camera.position.set(camX, field.camMaxY, 0);
						setY(h / 2.0f + this.POS_Y + movePoint.y - field.camMaxY);
					}
					this.POS_Y += movePoint.y;
				}
				else {
					if (this.POS_Y + movePoint.y > field.camMaxY) {
						field.camera.position.set(camX, field.camMaxY, 0);
						setY(h / 2.0f + this.POS_Y + movePoint.y - field.camMaxY);
					}
					else {
						field.camera.position.set(camX, this.POS_Y + movePoint.y, 0);
						setY(h / 2.0f);
					}
					this.POS_Y += movePoint.y;
				}
			}
		}
		
		// Перемещение всех спрайтов относительно актера, за которым следит камера
		field.moveCamera();
	}
	
	// Полностью остановить анимации
	private void stopAll() {
		for (int i = 0; i < States.values().length; i++) {
			state.put(States.values()[i], false);
		}
	}
	
	@Override
	public void act(float delta) {
		
		if (actionsListener.getActionStateFor(Controls.UP, 2).pressed) { 
			this.CURENT_SPEED_X = 0.0f;
			this.CURENT_SPEED_Y = 24.0f;
		}
		
		if (actionsListener.getActionStateFor(Controls.DOWN, 2).pressed) { 
			this.CURENT_SPEED_X = 0.0f;
			this.CURENT_SPEED_Y = -24.0f;
		}
		
		if (actionsListener.getActionStateFor(Controls.LEFT, 2).pressed) { 
			this.CURENT_SPEED_X = -24.0f;
			this.CURENT_SPEED_Y = 0.0f;
		}
		
		if (actionsListener.getActionStateFor(Controls.RIGHT, 2).pressed) { 
			this.CURENT_SPEED_X = 24.0f;
			this.CURENT_SPEED_Y = 0.0f;
		}
		
		// Перемещение мяча
		movePlayerBy(new Vector2(this.CURENT_SPEED_X, this.CURENT_SPEED_Y));
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		
		stateTime += Gdx.graphics.getDeltaTime();
		
		// Анимирование мяча
		animations.get(this.curentState()).setPlayMode(PlayMode.LOOP);
		currentFrame = animations.get(this.curentState()).getKeyFrame(stateTime, true); 
		
		// Если окончено текущее действие
		if (animations.get(this.curentState()).isAnimationFinished(stateTime)) {
			Do(States.STOP, true);
		}
		
		spriteBatch.begin();
        spriteBatch.draw(
    		currentFrame.getTexture(), 
    		this.getX(), 
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
    		false, 
    		false
		);
        spriteBatch.end();
		
		// Ресование тени персонажа
        shadow.setX(getX() + 25);
        shadow.setY(getY() - 5);
        shadow.setVisibility(this.JUMP_HEIGHT > 0);
        shadow.draw();
	}
}