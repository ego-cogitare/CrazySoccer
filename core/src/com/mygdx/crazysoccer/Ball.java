package com.mygdx.crazysoccer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.crazysoccer.Player.AddictedTo;
import com.mygdx.crazysoccer.Players.Amplua;

public class Ball extends Actor {
	
	private Field field;
	
	private static final int FRAME_COLS = 8;    
    private static final int FRAME_ROWS = 8;     
    
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
 	
 	// Координаты приземления мяча
 	private float LANDING_X = 0;
 	private float LANDING_Y = 0;
 	
 	// Координаты, где мяч впоследний раз вышел за пределы поля
 	private float OUT_X = -1;
 	private float OUT_Y = -1;
 	
 	// Идентификатор игрока который был назначен для ввода мяча в игру
 	private int PLAYER_TO_THROW_IN = -1;
    
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
    
    // ID игрока, который последним управлял мячом
    private int LAST_MANAGED_PLAYER_ID = -1;
    
    // ID игрока, который последним косался мяча
    private int LAST_TOUCHED_PLAYER_ID = -1;
    
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
    
    // Находится ли мяч в пределах поля
    private boolean IS_BALL_IN_FIELD = true;
    
    // Введен ли мяч в игру
    private boolean THROWED_IN = false;
    
    // Было ли первое касание мяча
    private boolean IS_FIRST_TOUCH_HAPPENED = false;
    
    // Набор анимаций мяча
    public Map<States, Animation> animations;
    
	// Кадры анимации полета при обычном ударе
	public TextureRegion[] flyFrames; 
    
    // Текущее состояние мяча
 	public Map<States, Boolean> state = new HashMap<States, Boolean>();
 	
 	// Типы вбрасывания мяча в поле
 	public static enum ThrowInType {
 		TOP_OUT,
 		BOTTOM_OUT,
 		LEFT_BOTTOM_CORNER,
 		LEFT_TOP_CORNER,
 		RIGHT_TOP_CORNER,
 		RIGHT_BOTTOM_CORNER,
 		LEFT_FREE_KICK,
 		RIGHT_FREE_KICK
 	}
 	
 	// Способ ввода мяча в игру
 	public ThrowInType howToThrowIn;
    
    public static enum States {
		STOP,     			// Состояние покоя
		FLY_FAST,     		// Состояние полета
		FLY_MEDIUM,     	// Состояние полета
		FLY_SLOW,     		// Состояние полета
		FOOT_SUPER_KICK,      // Суперудар ногой
		HEAD_BACK_SUPER_KICK  // Суперудар голой / через себя
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
        animationSheet = new Texture(Gdx.files.internal("graphics/ball.png"));
        
        // Загрузка карты анимаций персонажа
        animationMap = TextureRegion.split(animationSheet, animationSheet.getWidth()/FRAME_COLS, animationSheet.getHeight()/FRAME_ROWS);
        
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
	
	public boolean inField() {
		return IS_BALL_IN_FIELD;
	}
	
	public void inField(boolean b) {
		
//		if (!b) {
//			// Отмечаем что мяч не контроллируется никаким из игроков
//			this.managerByBlayer(-1);
//			
//			// Отмечаем что мяч свободен
//			this.isCatched(false);
//			
//			// Отмечаем, что никто из игроков не контроллирует мяч
//			for (int i = 0; i < field.players.length; i++) {
//				field.players[i].catchBall(false);
//			}
//		}
		
		// Если мяч вышел за пределы игрового поля
		if (IS_BALL_IN_FIELD && !b && PLAYER_TO_THROW_IN == -1) {
			
			// Сохраняем координату где мяч покинул пределы поля
			setOutXY(getAbsX(), getAbsY());
			
			// Отмечаем, что мяч не введен в игру
			setThrowedIn(false);
			
			// Сдвиг кривой ограничивающей поле справа и слева для данной высоты
			float offs = field.mGetSideLineProjection(getAbsY());
			
			// Определяем способ как должен быть введен мяч в игру согласно футбольным правилам
			if (getOutX() >= field.fieldOffsetX && getOutX() <= field.fieldOffsetX + field.fieldMaxWidth && getOutY() < field.fieldOffsetY) 
				howToThrowIn = ThrowInType.BOTTOM_OUT;
			
			else if (getOutX() >= field.fieldOffsetX + offs && getOutX() <= field.fieldOffsetX + field.fieldMaxWidth - offs && getOutY() > field.fieldOffsetY + field.fieldHeight) 
				howToThrowIn = ThrowInType.TOP_OUT;
			
			// Мяч вышел слева
			else if (getOutX() < field.fieldMaxWidth / 2)
			{
				// Если угловой с левой стороны 
				if (field.players[LAST_TOUCHED_PLAYER_ID].getDestinationGateId() == Gate.RIGHT_GATES)
					howToThrowIn = 
						(getOutY() < field.fieldOffsetY + (field.fieldHeight / 2)) ? 
							ThrowInType.LEFT_BOTTOM_CORNER : ThrowInType.LEFT_TOP_CORNER;
				// Левый свободный удар
				else
					howToThrowIn = ThrowInType.LEFT_FREE_KICK;
			}
			
			// Мяч вышел справа
			else
			{
				// Если угловой с левой стороны 
				if (field.players[LAST_TOUCHED_PLAYER_ID].getDestinationGateId() == Gate.LEFT_GATES)
					howToThrowIn = 
						(getOutY() < field.fieldOffsetY + (field.fieldHeight / 2)) ? 
							ThrowInType.RIGHT_BOTTOM_CORNER : ThrowInType.RIGHT_TOP_CORNER;
				// Левый свободный удар
				else
					howToThrowIn = ThrowInType.RIGHT_FREE_KICK;
			}
			
			// Поиск ближайшего игрока, который мог бы ввести мяч в игру в зависимости от способа ввода
			switch (howToThrowIn)
			{
				case RIGHT_FREE_KICK:
					PLAYER_TO_THROW_IN = 
						field.findNearesPlayerToBall(
							new ArrayList<Amplua>(
								Arrays.asList(
									Amplua.GK
								)
							),
							new ArrayList<AddictedTo>(
								Arrays.asList(
									AddictedTo.AI,
									AddictedTo.HUMAN
								)
							),
							false,
							Gate.LEFT_GATES
						);
				break;
				
				case LEFT_FREE_KICK:
					PLAYER_TO_THROW_IN = 
						field.findNearesPlayerToBall(
							new ArrayList<Amplua>(
								Arrays.asList(
									Amplua.GK
								)
							),
							new ArrayList<AddictedTo>(
								Arrays.asList(
									AddictedTo.AI,
									AddictedTo.HUMAN
								)
							),
							false,
							Gate.RIGHT_GATES
						);
				break;
				
				default:
					PLAYER_TO_THROW_IN = 
						field.findNearesPlayerToBall(
							new ArrayList<Amplua>(
								Arrays.asList(
									Amplua.FW,
									Amplua.DF,
									Amplua.MF
								)
							),
							new ArrayList<AddictedTo>(
								Arrays.asList(
									AddictedTo.AI
								)
							),
							true,
							field.players[LAST_TOUCHED_PLAYER_ID].getDestinationGateId() == Gate.RIGHT_GATES ? 
								Gate.LEFT_GATES : Gate.RIGHT_GATES
						);
				break;
			}
		}
		
		IS_BALL_IN_FIELD = b;
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
	
	public float getMass() {
		return this.MASS;
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
		if (!c) this.MANAGED_PLAYER_ID = -1;
				
		this.CATCHED = c;
	}
	
	public boolean isCatched() {
		if (!this.CATCHED) {
			this.MANAGED_PLAYER_ID = -1;
		}
		
		return this.CATCHED;
	}
	
	/**
	 * Было ли касания мяча после начала матча / розыграша мяча после гола
	 * @return
	 */
	public boolean isFirstTouchHappened() {
		return IS_FIRST_TOUCH_HAPPENED;
	}
	
	public void setFirstTouchHappened(boolean ft) {
		// Если первого касания еще небыло и устанавливается флаг, что 
		// оно произошло - отмечаем что мяч введен в игру  
		if (!IS_FIRST_TOUCH_HAPPENED && ft) {
			THROWED_IN = true;
		}
		IS_FIRST_TOUCH_HAPPENED = ft;
	}
	
	/**
	 * Введен ли мяч в игру
	 * @return
	 */
	public boolean isThrowedIn() {
		return THROWED_IN;
	}
	
	/**
	 * Установка признака введен ли мяч в игру
	 * @return
	 */
	public void setThrowedIn(boolean thwdIn) {
		if (thwdIn) PLAYER_TO_THROW_IN = -1;
		
		THROWED_IN = thwdIn;
	}
	
	// Установка ID игрока, которым контроллируется мяч
	public void managerByBlayer(int playerId) {
		// Сохранение ID игрока, который последним контроллировал мяч
		if (playerId >= 0) this.LAST_MANAGED_PLAYER_ID = this.LAST_TOUCHED_PLAYER_ID = playerId;
		
		this.MANAGED_PLAYER_ID = playerId;
	}
	
	public boolean managedByGK() {
		return isCatched() && MANAGED_PLAYER_ID != -1 && field.players[MANAGED_PLAYER_ID].getAmplua() == Amplua.GK;
	}
	
	// Получение ID игрока которым контроллируется мяч
	public int managerByBlayer() {
		return this.MANAGED_PLAYER_ID;
	}
	
	/**
	 *  Предполагаемая X-координата приземления мяча
	 * @return
	 */
	public float getLandingX() {
		return LANDING_X;
	}
	
	/**
	 *  Предполагаемая Y-координата приземления мяча
	 * @return
	 */
	public float getLandingY() {
		return LANDING_Y;
	}
	
	/**
	 * Установка точки где мяч покинул пределы поля
	 * @param x
	 * @param y
	 */
	public void setOutXY(float x, float y) {
		this.OUT_X = x;
		this.OUT_Y = y;
	}
	
	/**
	 * Получение координаты X где мяч покинул пределы поля
	 * @return
	 */
	public float getOutX() {
		return this.OUT_X;
	}
	
	/**
	 * Получение координаты Y где мяч покинул пределы поля
	 * @return
	 */
	public float getOutY() {
		return this.OUT_Y;
	}
	
	public int getPlayerToThrowIn() {
//		if (this.MANAGED_PLAYER_ID != -1 && this.PLAYER_TO_THROW_IN != -1 && this.MANAGED_PLAYER_ID != this.PLAYER_TO_THROW_IN) {
//			field.players[this.MANAGED_PLAYER_ID].catchBall(false);
//			this.isCatched(false);
//		}
//		
//		if (this.PLAYER_TO_THROW_IN != -1) {
//			field.players[this.PLAYER_TO_THROW_IN].catchBall(true);
//			this.isCatched(true);
//		}
		return PLAYER_TO_THROW_IN;
	}
	
	// Получение ID игрока которым контроллируется мяч
	public int lastManagerByBlayer() {
		return this.LAST_MANAGED_PLAYER_ID;
	}
	
	public void lastManagerByBlayer(int playerId) {
		this.LAST_MANAGED_PLAYER_ID = playerId;
	}
	
	// ID игрока, который последним касался мяча
	public int lastTouchedByBlayer() {
		return this.LAST_TOUCHED_PLAYER_ID;
	}
	
	public void lastTouchedByBlayer(int playerId) {
		this.LAST_TOUCHED_PLAYER_ID = playerId;
	}
	
	// Импульс удара мяча
	public float impulse() {
		return absVelocity() * getMass();
	}
	
	/**
	 * Нанесения удара по мячу
	 */
	public void kick(float impulse, float dstX, float dstY, boolean upFlag) {
		
		// Сохранение предполагаемой точки приземления мяча
		this.LANDING_X = dstX;
		this.LANDING_Y = dstY;
		
		float alpha = calcAlpha(dstX, dstY);
		
		// Подсчет скорости мяча при ударе
		float v = impulse / this.MASS;
		
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
	
	/**
	 * Выполнение паса 
	 */
	public void pass(float dstX, float dstY) 
	{
		// Сохранение предполагаемой точки приземления мяча
		this.LANDING_X = dstX;
		this.LANDING_Y = dstY;
	
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
			f = l / (h * 3.75f);
			
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
	public void moveBallBy(float x, float y) {
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		float camX = field.camera.position.x;
		float camY = field.camera.position.y;
		
		boolean doStop = false;
		
		if (this.POS_X >= 0 && this.POS_X <= field.worldWidth && this.POS_Y >= 0 && this.POS_Y <= field.worldHeight) {
			
			// Ограничение выхода персонажа за пределы поля
			if (this.POS_X + x < 0) {
				x = -this.POS_X;
				doStop = true;
			}
			else if (this.POS_X + x > field.worldWidth) { 
				x = field.worldWidth - this.POS_X;
				doStop = true;
			}
			
			if (this.POS_Y + y < 0) {
				y = -this.POS_Y;
				doStop = true;
			}
			else if (this.POS_Y + y > field.worldHeight) { 
				y = field.worldHeight - this.POS_Y;
				doStop = true;
			}
			
			// Если мяч достиг границы игрового мира, то останавливаем его
			if (doStop) Do(States.STOP, true);
			
			// Перемещение по оси X
			if (this.POS_X < field.fieldMaxWidth / 2.0f) {
				if (this.POS_X >= w / 2) {
					if (this.POS_X + x < w / 2.0f) {
						field.camera.position.set(w / 2.0f, camY, 0);
						setX(x + this.POS_X);
					}
					else {
						field.camera.position.set(camX + x, camY, 0);
					}
					this.POS_X += x;
				} 
				else {
					if (this.POS_X + x > w / 2.0f) {
						setX(w / 2.0f);
						field.camera.position.set(camX + this.POS_X + x - w / 2.0f, camY, 0);
					}
					else {
						setX(getX() + x);
					}
					this.POS_X += x;
				}
			}
			else {
				if (getX() <= w / 2.0f) {
					if (this.POS_X + x <= field.camMaxX) {
						field.camera.position.set(camX + x, camY, 0);
					}
					else {
						field.camera.position.set(field.camMaxX, camY, 0);
						setX(w / 2.0f + this.POS_X + x - field.camMaxX);
					}
					this.POS_X += x;
				} 
				else {
					if (this.POS_X + x > field.camMaxX) {
						field.camera.position.set(field.camMaxX, camY, 0);
						setX(w / 2.0f + this.POS_X + x - field.camMaxX);
					}
					else {
						field.camera.position.set(this.POS_X + x, camY, 0);
						setX(w / 2.0f);
					}
					this.POS_X += x;
				}
			}
			
			// Перемещение по оси Y
			camX = field.camera.position.x;
			camY = field.camera.position.y;
			
			if (this.POS_Y < field.fieldHeight / 2.0f) {
				if (this.POS_Y >= h / 2) {
					if (this.POS_Y + y < h / 2.0f) {
						field.camera.position.set(camX, h / 2.0f, 0);
						setY(y + this.POS_Y);
					}
					else {
						field.camera.position.set(camX, camY + y, 0);
					}
					this.POS_Y += y;
				} 
				else {
					if (this.POS_Y + y > h / 2.0f) {
						setY(h / 2.0f);
						field.camera.position.set(camX, camY + this.POS_Y + y - h / 2.0f, 0);
					}
					else {
						setY(getY() + y);
					}
					this.POS_Y += y;
				}
			}
			else {
				if (getY() <= h / 2.0f) {
					if (this.POS_Y + y <= field.camMaxY) {
						field.camera.position.set(camX, camY + y, 0);
					}
					else {
						field.camera.position.set(camX, field.camMaxY, 0);
						setY(h / 2.0f + this.POS_Y + y - field.camMaxY);
					}
					this.POS_Y += y;
				}
				else {
					if (this.POS_Y + y > field.camMaxY) {
						field.camera.position.set(camX, field.camMaxY, 0);
						setY(h / 2.0f + this.POS_Y + y - field.camMaxY);
					}
					else {
						field.camera.position.set(camX, this.POS_Y + y, 0);
						setY(h / 2.0f);
					}
					this.POS_Y += y;
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
			
			if (isThrowedIn()) {
				// Отмечаем что мяч никем не контроллируется
				this.isCatched(false);
				this.MANAGED_PLAYER_ID = -1;
				
				// Включаем воздействие гравитации
				this.ALLOW_GRAVITY_FROM = 999.0f;
			}
		}
		
		if (this.ballInNet() && !field.inField(getAbsX(),getAbsY())) {
			// Ограничение движение мяча в сетке левых ворот
			if (this.getAbsX() < field.gates[0].getBottomBar().x - 70 && this.getVelocityX() < 0) {
				this.setVelocityX(-0.3f * this.getVelocityX());
				
				if (Math.abs(this.getVelocityX()) > field.gates[0].minFlatusVelocity()) {
					field.gates[0].drawFlatus();
				}
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
				
				if (Math.abs(this.getVelocityX()) >= field.gates[1].minFlatusVelocity()) {
					field.gates[1].drawFlatus();
				}
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
		if ((this.absVelocity() < this.ALLOW_GRAVITY_FROM || this.absVelocity() == 0) && (this.getAbsH() > 0 || this.getJumpVelocity() > 0)) {
			
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
				
				if (curentState() == States.HEAD_BACK_SUPER_KICK) {
					float k = field.players[this.LAST_MANAGED_PLAYER_ID].getAccSuperKick() / Math.max(Math.abs(this.getVelocityX()), Math.abs(this.getVelocityY()));
					
					this.CURENT_SPEED_X *= k;
					this.CURENT_SPEED_Y *= k;
				}
				
				// Отскок мяча от поверхности газона и придание ему вертикального ускорения
				this.JUMP_VELOCITY =  this.RESTITUTION * Math.abs(this.JUMP_VELOCITY);
				
				// Уменьшаем скорость мяча с учетом коефициента трения газона
				this.CURENT_SPEED_Y += this.CURENT_SPEED_Y * this.GRASS_FRICTION;
				this.CURENT_SPEED_X += this.CURENT_SPEED_X * this.GRASS_FRICTION;
				
				// Воспроизводим звук удара мяча о газон
				if (this.JUMP_VELOCITY > 0.15f) Sounds.play("balllanding02",true);
				
				this.JUMP_HEIGHT = 0;
				
				// Сохраняем координаты приземления мяча
				LANDING_X = getAbsX();
				LANDING_Y = getAbsY();
			}
		}
		
		// Перемещение мяча
		moveBallBy(this.CURENT_SPEED_X, this.CURENT_SPEED_Y);
	}
	
	public boolean ballInNet() {
		return this.IS_BALL_IN_NET;
	}
	
	public float getRestitution() {
		return this.RESTITUTION;
	}
	
	public void ballInNet(boolean b) {
		this.IS_BALL_IN_NET = b;
	}
	
	// Проверка находится ли мяч в воротах (гол забит)
	// Метод возвращает:
	//  0 - мяч не в воротах
	//  1 - мяч в левых воротах
	//  2 - мяч в правых воротах
	public int scoredInGates() {
		int r = -1;
		
		if (!field.inField(getAbsX(),getAbsY())) {
			
			if ((this.getAbsH() < field.gates[0].getHeight()) && 
				(this.getAbsX() + 10 < field.fieldOffsetX + field.mGetSideLineProjection(this.getAbsY()) && this.getAbsY() > field.gates[0].getBottomBar().y && this.getAbsY() < field.gates[0].getTopBar().y)) {
				
				r = 0;
			}
			else if ((this.getAbsH() < field.gates[1].getHeight()) && 
					 (this.getAbsX() - 10 > field.fieldOffsetX + field.fieldMaxWidth - field.mGetSideLineProjection(this.getAbsY()) && this.getAbsY() > field.gates[1].getBottomBar().y && this.getAbsY() < field.gates[1].getTopBar().y)) {
				 
				r = 1;
			}
			
		}
		
		// Если было зафиксировано, что мяч находится в воротах то устанавливаем флаг того что мяч в воротах 
		if (!this.ballInNet() && r != -1) 
			this.ballInNet(true);
		else if (this.ballInNet() && r == -1)
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
		if (field.gameState == Field.GameStates.RUN) {
			stateTime += Gdx.graphics.getDeltaTime();
		}
		
		// Анимирование мяча
		animations.get(this.curentState()).setPlayMode(PlayMode.LOOP);
		currentFrame = animations.get(curentState()).getKeyFrame(stateTime, true); 
		
		CrazySoccer.batch.begin();
		CrazySoccer.batch.draw(
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
		CrazySoccer.batch.end();
        
		// Ресование тени мяча
        shadow.setX(getX() - 16);
        shadow.setY(getY() - 8);
        shadow.setVisibility(this.getAbsH() > 0);
	}
}