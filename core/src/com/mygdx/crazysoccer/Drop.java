package com.mygdx.crazysoccer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.crazysoccer.Wind.WindDirections;

public class Drop extends Actor {
	
	// Скорость падения капли
	private float follingVelocity = 19.0f;
	
	private float POS_X;
	private float POS_Y;
	
	private float DROP_BREAK_X;
	private float DROP_BREAK_Y;
	private long DROP_BREAK_TIME = 0l;
	
	private float SIDE_VELOCITY;
	private WindDirections windDirection;
	
	private TextureRegion dropTexture;
	private SpriteBatch dropSprite;
	private TextureRegion dropBreakTexture;
	private SpriteBatch dropBreakSprite;
	
	public TextureRegion[] dropBreakFrames; 
	
	// Высота на которой капля разобъется (определяется случайно)
	private float landingHeight;
	
	public Drop() {
		super();
		
		dropTexture = new TextureRegion(Field.sprites,14*32,32,32,32);
		dropSprite = new SpriteBatch();
		
		dropBreakTexture = new TextureRegion(Field.sprites, 13*32, 32, 32, 32);
		dropBreakSprite = new SpriteBatch();
		
		setRandomPos();
	}
	
	public void setWindVelocity(float v) { 
		this.SIDE_VELOCITY = this.SIDE_VELOCITY < 0 ? -v : v;
		if (windDirection == WindDirections.TOP_DOWN || windDirection == WindDirections.NONE) this.SIDE_VELOCITY = 0;
	}
	
	public float getWindVelocity() {
		return this.SIDE_VELOCITY;
	}
	
	public void setWindDirection(WindDirections wd) {
		switch (wd) {
			case LEFT_RIGHT: case LEFT_BOTTOM: /*case LEFT_TOP:*/ case TOP_RIGHT: /*case BOTTOM_RIGHT:*/
				this.SIDE_VELOCITY = Math.abs(this.SIDE_VELOCITY);
			break;
			
			case TOP_LEFT: case RIGHT_LEFT: case RIGHT_BOTTOM: /*case RIGHT_TOP:*/ /*case BOTTOM_LEFT:*/
				this.SIDE_VELOCITY = -Math.abs(this.SIDE_VELOCITY);
			break;
			
			case TOP_DOWN: case NONE:  
				this.SIDE_VELOCITY = 0;
			break;
		}
		windDirection = wd;
//		System.out.println(SIDE_VELOCITY);
	}
	
	// Произвольно устанавливаем положение капли
	private void setRandomPos() {
		setX(Gdx.graphics.getWidth() * (float)Math.random() - SIDE_VELOCITY * 15);
		setY(Gdx.graphics.getHeight());
		
		landingHeight = Gdx.graphics.getHeight() * (float)Math.random();
	}
	
	public float getAbsX() {
		return this.POS_X;
	}
	
	public float getAbsY() {
		return this.POS_Y;
	}
	
	@Override
	public void act(float deltaTime) {
		setX(getX() + this.getWindVelocity());
		setY(getY() - follingVelocity);
		
		// Если капля дошла до высоты где ей было суждено разбиться, то капля исчезает
		if (getY() < landingHeight) {
			// Фиксируем время приземления капли
			DROP_BREAK_TIME = System.nanoTime();
			
			// Запоминаем координаты где разбилась капля
			DROP_BREAK_X = getX();
			DROP_BREAK_Y = getY();
			
			// Произвольно устанавливаем положение капли по оси OX
			setRandomPos();
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		dropSprite.begin();
		dropSprite.draw(dropTexture, getX() + this.getWindVelocity(), getY(), 32, 32, 32, 32, 0.7f, 0.7f, 0);
		dropSprite.end();
		
		if (System.nanoTime() - DROP_BREAK_TIME < 100000000) {
			dropBreakSprite.begin();
			dropBreakSprite.draw(dropBreakTexture, DROP_BREAK_X, DROP_BREAK_Y, 32, 32, 32, 32, 1.0f, 1.0f, 0);
			dropBreakSprite.end();
		}
	}
}
