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

public class Ball extends Actor {
	
	private Field field;
	
	private static final int FRAME_COLS = 8;    
    private static final int FRAME_ROWS = 8;     
    
    // Спрайт для отрисовки персонажа
    public SpriteBatch spriteBatch;

    public int SPRITE_WIDTH = 16;
    public int SPRITE_HEIGHT = 16;
    public float SPRITE_SCALE = 3.0f;
    
    // Параметры скорости и расположения мяча
 	private float CURENT_SPEED_X = 0.0f;
 	private float CURENT_SPEED_Y = 0.0f;
 	private float POS_X = 0.0f;
 	private float POS_Y = 0.0f;
 	private float MASS = 3.0f;
 	private float DIAMETER = SPRITE_SCALE * SPRITE_WIDTH;
 	private float PASS_IMPULSE = 30.0f;
    
    // Парамерты высоты расположения мяча
    private float JUMP_HEIGHT = 0;
    private float JUMP_VELOCITY = 0;
    
    // Коэффициент трения мяча о воздух 
    private float AIR_FRICTION = -0.005f;
    
    // Коэффициент трения мяча о газон
    private float GRASS_FRICTION = -0.04f;
    
    // Коефициент отпрыгивания мяча от газона
    private float RESTITUTION = 0.6f;
    
    // Контролируется ли мяч каким-то из игроков
    private boolean CATCHED = false;
    
    // ID игрока, которым контроллируется мяч
    private int MANAGED_PLAYER_ID = 0;
    
    // Скорость мяча при которой на мяч начинает действовать гравитация
    private float ALLOW_GRAVITY_FROM = 999.0f;
    
    // Скорость полета мяча при ударе, начиная с которой на него начинает действовать гравитация
    private float ALLOW_GRAVITY_KICK = 14.0f;
    
    // Максимальное растояние на котороем можно отдать пас
    private float MAX_PASS_LENGTH = 1700.0f;
    
    // Угол Alpha под которым последний раз направлялся мяч
    private float LAST_BALL_ALPHA = 0.0f;
    
    // Принимает значение true если мяч влетел в ворота
    private boolean IS_BALL_IN_NET = false;
    
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
		
		FOOT_SUPER_KICK,      // Суперудар ногой
		HEAD_BACK_SUPER_KICK, // Суперудар голой / через себя
	}
	
    // Текущий кадр анимации
 	public TextureRegion currentFrame; 
 	
 	// Тень мяча
 	public Shadow shadow;
 	
 	public Texture animationSheet;
 	public TextureRegion[][] animationMap;
 	
    public float stateTime = 0.0f; 
 
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
        
        // Анимация движущегося мяча
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
        
        // Анимация суперудара ногой
        animations.put(States.FOOT_SUPER_KICK, 
    		new Animation(0.08f,
	    		animationMap[1][1],
	    		animationMap[1][2],
	    		animationMap[1][0]
			)
        );
        
        // Анимация суперудара головой / через себя
        animations.put(States.HEAD_BACK_SUPER_KICK, 
    		new Animation(0.08f,
				animationMap[1][4],
				animationMap[1][4],
				animationMap[0][1],
				animationMap[0][2],
				animationMap[0][3],
				animationMap[1][3],
				animationMap[1][3],
				animationMap[0][5],
				animationMap[0][6],
				animationMap[0][7]
			)
		);
        
        shadow = new Shadow();
	}
	
	public void attachField(Field f) {
		this.field = f;
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
	
	// Установка ID игрока, которым контроллируется мяч
	public void managerByBlayer(int playerId) {
		this.MANAGED_PLAYER_ID = playerId;
	}
	
	// Получение ID игрока которым контроллируется мяч
	public int managerByBlayer() {
		return this.MANAGED_PLAYER_ID;
	}
	
	public void kick(float impulse, float dstX, float dstY, boolean upFlag) {
		
		float alpha = calcAlpha(dstX, dstY);
		
		// Подсчет скорости мяча при ударе
		float v = impulse / this.MASS;
		
//		this.CURENT_SPEED_X = v * (float)Math.sin(alpha * Math.PI / 180.0f);
//		this.CURENT_SPEED_Y = v * (float)Math.cos(alpha * Math.PI / 180.0f);
		
		this.setVelocityX(v * (float)Math.sin(alpha));
		this.setVelocityY(v * (float)Math.cos(alpha));
		
		// Если осуществляя удар зажата кнопка вверх, то мяч должен лететь по параболе
		if (upFlag) {
			this.setJumpVelocity((impulse == this.PASS_IMPULSE) ? 0.75f : 7.2f);
			this.allowGravityFrom(999);
		}
		// Иначе мяч летит прямо, и начинает терять высоту только начиная с момента
		// когда его скорость станет меншье 14
		else {
			// Скорость полета мяча при ударе, начиная с которой на него начинает действовать гравитация
			this.allowGravityFrom(this.ALLOW_GRAVITY_KICK);
			
			// Вертикальная скорость мяча при ударе
			this.setJumpVelocity(0);
		
			// Если в момент удара мяч находится на земле то поднимаем его на 30px
			if (this.getAbsH() <= 30) this.setAbsH(30);
		}
	}
	
	public void pass(float dstX, float dstY) {
		// Сила с которой нужно давать пас, чтобы мяч достиг адресата
		float f = 0;
		
		// Расстояние к точке куда давать пас
		float l = MathUtils.distance(dstX, dstY, getAbsX(), getAbsY());
		
		// Если расстояние больше чем максимальное растояние на которое можно отдать пас
		if (l > this.MAX_PASS_LENGTH) l = this.MAX_PASS_LENGTH; 
		
		// Если расстояние к игроку которому дают пас < 300, то пас дается низом
		if (l < 350) {
			kick(this.PASS_IMPULSE, dstX, dstY, true);
		}
		// Иначе пас дается навесом
		else {
			// Высота паса (чем больше растояние тем выше полет мяча)
			float h = l / 100.0f;
			
			// Ограничиваем минимальное и максимальное значение высоты
			if (h < 5.5f) h = 5.5f; else if (h > 8.5f) h = 8.5f;
			
			// Сила, с которой нужно отправить мяч, чтобы он долетел
			f = l / (h * 3.50f);
			
			// Удар по мячу
			kick(f, dstX, dstY, true);
			
			// Устанавливаем начальную вертикальную скорость полета мяча
			this.setJumpVelocity(h);
		}
	}
	
	// Выполнение суперудара по мячу головой / через себя
	public void headBackSuperKick(float impulse, float dstX, float dstY) {
		// Сила с которой нужно ударить мяч, чтобы он долетел до ворот
		float f = 0, h = 0, k = 0;
		
		// Расстояние к точке куда делать удар
		//float l = Math.abs(dstX - getAbsX());
		float l = MathUtils.distance(dstX, dstY, getAbsX(), getAbsY());
		
		// Если расстояние до ворот меньше 90 то удар будет выполняться не навесом, а низом
		if (l < 900) {
			kick(impulse, dstX, dstY, false);
		}
		// Иначе удар делается навесом
		else {
			// Высота полета мяча (чем больше растояние тем выше полет мяча)
			h = l / 180.0f;
			k = 4.5f;
			
			if (l > 1000) {
				h = l / 180.0f;
				k = 4.1f;
			}
			
			if (l > 1200) {
				h = l / 190.0f;
				k = 3.7f;
			}
			
			if (l > 1500) {
				h = l / 180.0f;
				k = 3.5f;
			}
			
			if (l > 1750) {
				h = l / 170.0f;
				k = 3.15f;
			}
			
			if (l > 1900) {
				h = l / 190.0f;
				k = 3.4f;
			}
			
			if (l > 2100) {
				h = l / 200.0f;
				k = 3.2f;
			}
			
			// Ограничиваем минимальное и максимальное значение высоты
			if (h < 4.0f) h = 4.0f; else if (h > 15.0f) h = 15.0f;
			
			// Сила, с которой нужно отправить мяч, чтобы он долетел
			f = l / (h * k);
			
			// Удар по мячу
			kick(f, dstX, dstY, true);
			
			// Устанавливаем начальную вертикальную скорость полета мяча
			this.setJumpVelocity(h);
		}
	}
	
	// Подсчет угла под которым нужно послать мяч, чтобы он достиг цели (dstX, dstY)
	// с текущего положения мяча
	private float calcAlpha(float dstX, float dstY) {
		float alpha = 0;
		
		// Опеределение расстояния от мяча до заданой точки по осям XOY
		float AC = Math.abs(dstX - getAbsX());
		float BC = Math.abs(dstY - getAbsY());
		
		alpha = (float)Math.asin(BC / Math.sqrt(AC * AC + BC * BC));
		
		if (getAbsX() < dstX) {
			if (getAbsY() < dstY) alpha = -alpha;
			alpha += 0.5f * Math.PI;
		}
		else {
			if (getAbsY() > dstY) alpha = -alpha;
			alpha += 1.5f * Math.PI;
		}
		
		// Запоминаем значение под которым направлен мяч (значение используется для
		// абсолютного значения скорости мяча по осям XOY)
		this.LAST_BALL_ALPHA = alpha;
		
//		if (this.getAbsY() < field.worldHeight / 2.0f) {
//			if (alpha < 0.18f) 
//				alpha = 0.18f;
//			else if (alpha > 6.09f) 
//				alpha = 6.09f;
//		}
//		else {
//			if (alpha > 2.71f && this.getAbsX() > field.worldWidth / 2.0f) 
//				alpha = 2.71f;
//			else if (alpha < 3.56f && this.getAbsX() <= field.worldWidth / 2.0f) 
//				alpha = 3.56f;
//		}

		
		return alpha;
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

	public float getJumpVelocity() {
		return this.JUMP_VELOCITY;
	}

	// Постановка задания на выполнение действия
	public void Do(States state, boolean stopAll) {
		// Если установлен флаг stopAll то останавливаем все анимации
		if (stopAll) this.stopAll();
		
		switch (state) {
			case STOP:
				this.setVelocityX(0);
				this.setVelocityY(0);
			break;
			
			default:
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
	
	// Механизм передвижения мяча и камеры относительно него
	public void moveBallBy(Vector2 movePoint) {
		
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
			
			// Если мяч достиг границы игрового мира, то останавливаем его
			if (doStop) Do(States.STOP, true);
			
			// Перемещение по оси X
			if (this.POS_X < field.fieldMaxWidth / 2.0f) {
				if (this.POS_X >= w / 2) {
					if (this.POS_X + movePoint.x < w / 2.0f) {
						Field.camera.position.set(w / 2.0f, camY, 0);
						setX(movePoint.x + this.POS_X);
					}
					else {
						Field.camera.position.set(camX + movePoint.x, camY, 0);
					}
					this.POS_X += movePoint.x;
				} 
				else {
					if (this.POS_X + movePoint.x > w / 2.0f) {
						setX(w / 2.0f);
						Field.camera.position.set(camX + this.POS_X + movePoint.x - w / 2.0f, camY, 0);
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
						Field.camera.position.set(camX + movePoint.x, camY, 0);
					}
					else {
						Field.camera.position.set(field.camMaxX, camY, 0);
						setX(w / 2.0f + this.POS_X + movePoint.x - field.camMaxX);
					}
					this.POS_X += movePoint.x;
				} 
				else {
					if (this.POS_X + movePoint.x > field.camMaxX) {
						Field.camera.position.set(field.camMaxX, camY, 0);
						setX(w / 2.0f + this.POS_X + movePoint.x - field.camMaxX);
					}
					else {
						Field.camera.position.set(this.POS_X + movePoint.x, camY, 0);
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
						Field.camera.position.set(camX, h / 2.0f, 0);
						setY(movePoint.y + this.POS_Y);
					}
					else {
						Field.camera.position.set(camX, camY + movePoint.y, 0);
					}
					this.POS_Y += movePoint.y;
				} 
				else {
					if (this.POS_Y + movePoint.y > h / 2.0f) {
						setY(h / 2.0f);
						Field.camera.position.set(camX, camY + this.POS_Y + movePoint.y - h / 2.0f, 0);
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
						Field.camera.position.set(camX, camY + movePoint.y, 0);
					}
					else {
						Field.camera.position.set(camX, field.camMaxY, 0);
						setY(h / 2.0f + this.POS_Y + movePoint.y - field.camMaxY);
					}
					this.POS_Y += movePoint.y;
				}
				else {
					if (this.POS_Y + movePoint.y > field.camMaxY) {
						Field.camera.position.set(camX, field.camMaxY, 0);
						setY(h / 2.0f + this.POS_Y + movePoint.y - field.camMaxY);
					}
					else {
						Field.camera.position.set(camX, this.POS_Y + movePoint.y, 0);
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
	
	@Override
	public void act(float delta) {
		
		// Замедление полета мяча (трение воздуха)
		this.CURENT_SPEED_X += this.CURENT_SPEED_X * this.AIR_FRICTION; 
		this.CURENT_SPEED_Y += this.CURENT_SPEED_Y * this.AIR_FRICTION;
		
		// Останавливаем мяч, если его суммарная скорость по осям
		if (this.absVelocity() < 0.25f && curentState() != States.STOP && curentState() != States.HEAD_BACK_SUPER_KICK) {
			this.CURENT_SPEED_X = 0.0f;
			this.CURENT_SPEED_Y = 0.0f;
			this.JUMP_VELOCITY = 0.0f;
			
			// Устанавливаем окончательный угол поворота мяча и останавливаем анимацию
			stateTime = animations.get(States.FLY_SLOW).getKeyFrameIndex(stateTime) * animations.get(States.STOP).getFrameDuration();

			Do(States.STOP, true);
		}
		
		// Если выполняется суперудар то отключаем трение о воздух
		if (curentState() == States.FOOT_SUPER_KICK && field.inField(getAbsX(), getAbsY())) {
			
			// Отключаем воздействие трения воздуха
			this.AIR_FRICTION = 0.0f; 
			
			// Отключаем воздействие гравитации
			this.ALLOW_GRAVITY_FROM = 0;
		}
		else if (curentState() == States.HEAD_BACK_SUPER_KICK && field.inField(getAbsX(), getAbsY())) {
			
			// Отключаем воздействие трения воздуха
			this.AIR_FRICTION = 0.0f;
			
			// Отключаем воздействие гравитации
			this.ALLOW_GRAVITY_FROM = 999;
		}
		else {
			// Включаем воздействие трения воздуха
			this.AIR_FRICTION = -0.005f;
			
			// Определение анимации мяча в зависимости от его скорости
			Do(getAnimationByVelocity(this.absVelocity()), true);
		}
		
		
		// Если мяч вышел за пределы поля
		if (!field.inField(getAbsX(),getAbsY())) {
			// Отмечам что мяч никем не контроллируется
			this.isCatched(false);
			this.MANAGED_PLAYER_ID = -1;
			
			// Включаем воздействие гравитации
			this.ALLOW_GRAVITY_FROM = 999.0f;
		}
		
		if (this.ballInNet() && !field.inField(getAbsX(),getAbsY())) {
			// Ограничение движение мяча в сетке левых ворот
			if (this.getAbsX() < field.gates[0].getBottomBar().x - 70 && this.getVelocityX() < 0) {
				this.setVelocityX(-0.3f * this.getVelocityX());
			}
			
			if (this.getAbsY() < field.gates[0].getBottomBar().y + 10 && this.getVelocityY() < 0) {
				this.setVelocityY(-0.6f * this.getVelocityY());
			}
			
			if (this.getAbsY() > field.gates[0].getTopBar().y - 10 && this.getVelocityY() > 0) {
				this.setVelocityY(-0.6f * this.getVelocityY());
			}
			
			if (this.getAbsH() > field.gates[0].height() - 20) {
				this.setJumpVelocity(-3);
			}
			
			
			// Ограничение движение мяча в сетке правых ворот
			if (this.getAbsX() > field.gates[1].getBottomBar().x + 70 && this.getVelocityX() > 0) {
				this.setVelocityX(-0.3f * this.getVelocityX());
			}
			
			if (this.getAbsY() < field.gates[1].getBottomBar().y + 10 && this.getVelocityY() < 0) {
				this.setVelocityY(-0.6f * this.getVelocityY());
			}
			
			if (this.getAbsY() > field.gates[1].getTopBar().y - 10 && this.getVelocityY() > 0) {
				this.setVelocityY(-0.6f * this.getVelocityY());
			}
			
			if (this.getAbsH() > field.gates[1].height() - 20) {
				this.setJumpVelocity(-3);
			}
		}
		
		
		// Реализация гравитации (гравитация начинает действовать только когда сумма абсолютных
		// скоростей по осям OX и OY < 15)
		if (this.absVelocity() < this.ALLOW_GRAVITY_FROM && (this.JUMP_HEIGHT > 0 || this.JUMP_VELOCITY > 0)) {
//			System.out.println(this.JUMP_VELOCITY);
			
			// Текущая вертикальная скорость мяча
			if (curentState() == States.HEAD_BACK_SUPER_KICK) {
				this.JUMP_VELOCITY -= 15.0f * Gdx.graphics.getDeltaTime();
			}
			else {
				this.JUMP_VELOCITY -= 8.0f * Gdx.graphics.getDeltaTime();
			}
			
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
				
				// Воспроизводим звук удара мяча о газон
				if (this.JUMP_VELOCITY > 0.15f) field.sounds.play("balllanding02",true);
				
				this.JUMP_HEIGHT = 0;
			}
		}
		
		// Перемещение мяча
		moveBallBy(new Vector2(this.CURENT_SPEED_X, this.CURENT_SPEED_Y));
	}
	
	private boolean ballInNet() {
		return this.IS_BALL_IN_NET;
	}
	
	public void ballInNet(boolean b) {
		this.IS_BALL_IN_NET = b;
	}
	
	// Проверка находится ли мяч в воротах (гол забит)
	// Метод возвращает:
	//  0 - мяч не в воротах
	//  1 - мяч в левых воротах
	//  2 - мяч в правых воротах
	public int isGoalIn() {
		int r = 0;
		
		if (!field.inField(getAbsX(),getAbsY())) {
			
			if ((this.getAbsH() < field.gates[0].getHeight()) && 
				(this.getAbsX() + 10 < field.fieldOffsetX + field.mGetSideLineProjection(this.getAbsY()) && this.getAbsY() > field.gates[0].getBottomBar().y && this.getAbsY() < field.gates[0].getTopBar().y)) {
				
				r = 1;
			}
			else if ((this.getAbsH() < field.gates[1].getHeight()) && 
					 (this.getAbsX() - 10 > field.fieldOffsetX + field.fieldMaxWidth - field.mGetSideLineProjection(this.getAbsY()) && this.getAbsY() > field.gates[1].getBottomBar().y && this.getAbsY() < field.gates[1].getTopBar().y)) {
				 
				r = 2;
			}
			
		}
		
		// Если было зафиксировано, что мяч находится в воротах то устанавливаем флаг того что мяч в воротах 
		if (!this.ballInNet() && r != 0) 
			this.ballInNet(true);
		else if (this.ballInNet() && r == 0)
			this.ballInNet(false);
		
		return r;
	}
	
	// Устанавливает значение скорости полета мяча начиная с которой на него начинает действовать 
	// гравитация. Это нужно для того, чтобы при ударе мяч мог лететь некоторое время не теряя
	// своей высоты
	public void allowGravityFrom(float v) {
		this.ALLOW_GRAVITY_FROM = v;
	}
	
	// Скорость игрока, который контроллирует в настоящий момент мяч
	private float getManagerPlayerVelocity() {
		return this.managerByBlayer() > -1 ? field.players[this.managerByBlayer()].getModVelocity() / 2.0f : 0;
	}
	
	// Суммарная скорость мяча по осям
	public float absVelocity() {
		//System.out.println("Ball velocity:"+this.absVelocity());
		//System.out.println("Ball catched:"+isCatched());
		
		// Если мяч контроллируется игроком то скорость перемещения мяча равне скорости перемещения игрока
		if (this.managerByBlayer() > -1 && this.getAbsH() == 0) {
			return Math.abs(this.getManagerPlayerVelocity());
		}
		
		// Скорость по оси OX
		double ox = Math.abs(this.getVelocityX() * Math.sin(LAST_BALL_ALPHA));
		
		// Скорость по оси OY
		double oy = Math.abs(this.getVelocityY() * Math.cos(LAST_BALL_ALPHA));
		
		return (float)(ox + oy);
	}
	
	// Нужно ли зеркалировать изобаржение мяча (в зависимоти от движения мяча)
	private boolean getFlip() {
		if (this.CURENT_SPEED_X < 0 || this.CURENT_SPEED_Y < 0 || this.getManagerPlayerVelocity() < 0) {
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
    		this.getY() + this.getAbsH(), 
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
        
		// Ресование тени мяча
        shadow.setX(getX() - 16);
        shadow.setY(getY() - 8);
        shadow.setVisibility(this.getAbsH() > 0);
	}
}