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

    public int SPRITE_WIDTH = 14;
    public int SPRITE_HEIGHT = 14;
    public float SPRITE_SCALE = 3.0f;
    
    // Параметры скорости и расположения мяча
 	private float CURENT_SPEED_X = 0.0f;
 	private float CURENT_SPEED_Y = 0.0f;
 	private float POS_X = 0.0f;
 	private float POS_Y = 0.0f;
 	private float MASS = 3.0f;
 	private float DIAMETER = SPRITE_SCALE * SPRITE_WIDTH;
    
    // Парамерты высоты расположения мяча
    private float JUMP_HEIGHT = 0;
    private float JUMP_VELOCITY = 0;
    
    // Коэффициент трения мяча о воздух 
    private float AIR_FRICTION = -0.010f;
    
    // Коэффициент трения мяча о газон
    private float GRASS_FRICTION = -0.03f;
    
    // Коефициент отпрыгивания мяча от газона
    private float RESTITUTION = 0.6f;
    
    // Контролируется ли мяч какимто из игроков
    private boolean CATCHED = false;
    
    // Набор анимаций мяча
    public Map<States, Animation> animations;
    
	// Кадры анимации полета при обычном ударе
	public TextureRegion[] flyFrames; 
    
    // Текущее состояние мяча
 	public Map<States, Boolean> state = new HashMap<States, Boolean>();
    
    public static enum States {
		STOP,     			// Состояние покоя
		FLY_FAST,     		// Состояние полета
		FLY_MEDIUM,     	// Состояние полета
		FLY_SLOW,     		// Состояние полета
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
        flyFrames = new TextureRegion[8];
        flyFrames[0] = animationMap[0][0];
        flyFrames[1] = animationMap[0][1];
        flyFrames[2] = animationMap[0][2];
        flyFrames[3] = animationMap[0][3];
        flyFrames[4] = animationMap[0][4];
        flyFrames[5] = animationMap[0][5];
        flyFrames[6] = animationMap[0][6];
        flyFrames[7] = animationMap[0][7];
        
        animations.put(States.STOP, new Animation(10.0f, flyFrames));
        animations.put(States.FLY_FAST, new Animation(0.06f, flyFrames));
        animations.put(States.FLY_MEDIUM, new Animation(0.1f, flyFrames));
        animations.put(States.FLY_SLOW, new Animation(0.25f, flyFrames));
        
        shadow = new Shadow();
	}
	
	public void attachField(Field f) {
		this.field = f;
	}
	
	public void kick(float impulse, float alpha) {
		// Подсчет скорости при ударе
		float v = impulse / this.MASS;
		
		this.CURENT_SPEED_X = v * (float)Math.sin(alpha * Math.PI / 180.0f);
		this.CURENT_SPEED_Y = v * (float)Math.cos(alpha * Math.PI / 180.0f);
		
		this.JUMP_VELOCITY = 0;
		
		// Если в момент удара мяч находится на земле то поднимаем его на 25px
		if (this.JUMP_HEIGHT <= 0) this.JUMP_HEIGHT = 30;
		
		Do(getAnimationByVelocity(v), true);
	}
	
	// Подбор анимации в зависимости от скорости полета мяча
	public States getAnimationByVelocity(float v) {
		if (v >= 10) {
			return States.FLY_FAST;
		}
		else if (v >= 3) {			
			return States.FLY_MEDIUM;
		} 
		else if (v > 0) {
			return States.FLY_SLOW;
		}
		else {
			return States.STOP;
		}
	}
	
	public void setJumpVelocity(float v) {
		this.JUMP_VELOCITY = v;
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
			case FLY_FAST:
			break;
		}

		// Добавляем задачу на исполнение
		this.state.put(state, true);
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
	
	// Возвращает диаметр мяча с учетом параметра Scale
	public float getDiameter() {
		return DIAMETER;
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
	
	public float getVelocityX() {
		return this.CURENT_SPEED_X;
	}
	
	public float getVelocityY() {
		return this.CURENT_SPEED_Y;
	}
	
	public void setVelocityX(float v) {
		this.CURENT_SPEED_X = v;
	}
	
	public void setVelocityY(float v) {
		this.CURENT_SPEED_Y = v;
	}
	
	public void isCatched(boolean c) {
		this.CATCHED = c;
	}
	
	public boolean isCatched() {
		return this.CATCHED;
	}
	
	// Механизм передвижения мяча и камеры относительно него
	public void moveBallBy(Vector2 movePoint) {
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		float camX = field.camera.position.x;
		float camY = field.camera.position.y;
		
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
			camX = field.camera.position.x;
			camY = field.camera.position.y;
			
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
			
			// Перемещение всех спрайтов относительно актера, за которым следит камера
			field.moveCamera();
		}
	}
	
	// Полностью остановить анимации
	public void stopAll() {
		for (int i = 0; i < States.values().length; i++) {
			state.put(States.values()[i], false);
		}
	}
	
	public boolean inField() {
		return 
		    this.getAbsY() + 8 > field.fieldOffsetY && 
		    this.getAbsY() - 4 < field.fieldOffsetY + field.fieldHeight &&
		    this.getAbsX() + 12 > field.fieldOffsetX + field.mGetSideLineProjection(this.getAbsY()) &&
			this.getAbsX() - 12 < field.fieldOffsetX + field.fieldMaxWidth - field.mGetSideLineProjection(this.getAbsY());
	}
	
	@Override
	public void act(float delta) {
		
		if (actionsListener.getActionStateFor(Controls.UP, 2).pressed) { 
			this.CURENT_SPEED_X = 0.0f;
			this.CURENT_SPEED_Y = 5.4f;
		}
		
		if (actionsListener.getActionStateFor(Controls.DOWN, 2).pressed) { 
			this.CURENT_SPEED_X = 0.0f;
			this.CURENT_SPEED_Y = -5.4f;
		}
		
		if (actionsListener.getActionStateFor(Controls.LEFT, 2).pressed) { 
			this.CURENT_SPEED_X = -5.4f;
			this.CURENT_SPEED_Y = 0.0f;
		}
		
		if (actionsListener.getActionStateFor(Controls.RIGHT, 2).pressed) { 
			this.CURENT_SPEED_X = 5.4f;
			this.CURENT_SPEED_Y = 0.0f;
		}

		if (actionsListener.getActionStateFor(Controls.ACTION1, 2).pressed) { 
			kick(50, 270);
		}
		
		if (actionsListener.getActionStateFor(Controls.ACTION2, 2).pressed) { 
			kick(50, 90);
		}
		
		// Замедление полета мяча (трение воздуха)
		this.CURENT_SPEED_X += this.CURENT_SPEED_X * this.AIR_FRICTION; 
		this.CURENT_SPEED_Y += this.CURENT_SPEED_Y * this.AIR_FRICTION;
		
		// Останавливаем мяч, если его суммарная скорость по осям
		if (this.absVelocity() < 0.25f && curentState() != States.STOP) {
			this.CURENT_SPEED_X = 0.0f;
			this.CURENT_SPEED_Y = 0.0f;
			this.JUMP_VELOCITY = 0.0f;
			
			// Устанавливаем окончательный угол поворота мяча и останавливаем анимацию
			stateTime = animations.get(States.FLY_SLOW).getKeyFrameIndex(stateTime) * animations.get(States.STOP).getFrameDuration();

			Do(States.STOP, true);
		}
		
		Do(getAnimationByVelocity(this.absVelocity()), true);

		
		// Реализация гравитации (гравитация начинает действовать только когда сумма абсолютных
		// скоростей по осям OX и OY < 15)
		if (this.absVelocity() < 14 && (this.JUMP_HEIGHT > 0 || this.JUMP_VELOCITY > 0)) {
			// Текущая вертикальная скорость мяча
			this.JUMP_VELOCITY -= 6.0f * Gdx.graphics.getDeltaTime();
			
			// Изменение высоты мяча
			this.JUMP_HEIGHT += this.JUMP_VELOCITY;
			
			// Момент контакта мяча с газоном
			//  1. изменение направление движения мяча по оси OY - вверх
			//  2. уменьшение скорости на коеффициент трения мяча о газон
			if (this.JUMP_HEIGHT <= 0.0f) {
				// Отскок мяча от поверхности газона и придание ему вертикального ускорения
				this.JUMP_VELOCITY =  this.RESTITUTION * Math.abs(this.JUMP_VELOCITY);
				
				// Уменьшаем скорость мяча с учетом коефициента трения газона
				this.CURENT_SPEED_Y += this.CURENT_SPEED_Y * this.GRASS_FRICTION;
				this.CURENT_SPEED_X += this.CURENT_SPEED_X * this.GRASS_FRICTION;
				
				this.JUMP_HEIGHT = 0;
			}
		}
		
		// Перемещение мяча
		moveBallBy(new Vector2(this.CURENT_SPEED_X, this.CURENT_SPEED_Y));
	}
	
	// Суммарная скорость мяча по осям
	public float absVelocity() {
		return Math.abs(this.CURENT_SPEED_X) + Math.abs(this.CURENT_SPEED_Y);
	}
	
	private boolean getFlip() {
		if (this.CURENT_SPEED_X < 0 || this.CURENT_SPEED_Y < 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		stateTime += Gdx.graphics.getDeltaTime();
		
		// Анимирование мяча
		animations.get(this.curentState()).setPlayMode(PlayMode.LOOP);
		currentFrame = animations.get(curentState()).getKeyFrame(stateTime, true); 
		
		spriteBatch.begin();
        spriteBatch.draw(
    		currentFrame.getTexture(), 
    		this.getX() - getDiameter() / 2, 
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
    		this.getFlip(),
    		false
		);
        spriteBatch.end();
		
		// Ресование тени персонажа
        shadow.setX(getX() - 12);
        shadow.setY(getY() - 2);
        shadow.setVisibility(this.JUMP_HEIGHT > 0);
	}
}