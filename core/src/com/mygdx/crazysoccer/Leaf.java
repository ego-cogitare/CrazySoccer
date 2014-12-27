package com.mygdx.crazysoccer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.crazysoccer.Wind.WindDirections;

public class Leaf extends Actor {
	
	private float rotation;
	private float velocityX, velocityY;
	private float minVelocity = 0.0f;
	private float maxVelocity = 0.0f;
	
	public TextureRegion leaf;
	public WindDirections windDirection;
	
	public Leaf(WindDirections wd) {
		leaf = new TextureRegion(Field.sprites);
		leaf.setRegion(13*32,0,32,32);
		windDirection = wd;
		setWindDirection(windDirection);
	}
	
	public void setWindVelocity(float v) {
		this.maxVelocity = v;
		this.minVelocity = this.maxVelocity * 0.5f;
		
		if (windDirection == WindDirections.TOP_DOWN || windDirection == WindDirections.NONE) this.maxVelocity = this.minVelocity = 0;
	}
	
	public float getWindVelocity() {
		return this.velocityX;
	}
	
	public WindDirections getWindDirection() {
		return windDirection;
	}
	
	public void setWindDirection(WindDirections wd) {
		windDirection = wd;
		
		switch (wd) {
			case LEFT_RIGHT:
				this.velocityX = getRandomVelocity();
				this.velocityY = randomDeviation();
			break;
			
			case LEFT_BOTTOM:
				this.velocityX = getRandomVelocity();
				this.velocityY = -Math.abs(this.velocityX * randomDeviation());
			break;
			
//			case LEFT_TOP:
//				this.velocityX = getRandomVelocity();
//				this.velocityY = this.velocityX * randomDeviation();
//			break;
			
			case TOP_DOWN:
				this.velocityX = randomDeviation();
				this.velocityY = -Math.abs(getRandomVelocity());
			break;
			
			case TOP_LEFT:
				this.velocityX = -Math.abs(getRandomVelocity());
				this.velocityY = -Math.abs(this.velocityX * randomDeviation());
			break;
			
			case TOP_RIGHT:
				this.velocityX = Math.abs(getRandomVelocity());
				this.velocityY = -Math.abs(this.velocityX * randomDeviation());
			break;
			
			case RIGHT_LEFT:
				this.velocityX = -Math.abs(getRandomVelocity());
				this.velocityY = randomDeviation();
			break;
			
			case RIGHT_BOTTOM:
				this.velocityX = -Math.abs(getRandomVelocity());
				this.velocityY = -Math.abs(this.velocityX * randomDeviation());
			break;
			
//			case RIGHT_TOP:
//				this.velocityX = getRandomVelocity() * -1;
//				this.velocityY = Math.abs(this.velocityX * randomDeviation());
//			break;
			
//			case BOTTOM_TOP:
//				this.velocityX = randomDeviation();
//				this.velocityY = getRandomVelocity();
//			break;
//			
//			case BOTTOM_LEFT:
//				this.velocityX = getRandomVelocity() * -1;
//				//this.velocityY = getRandomVelocity();
//				this.velocityY = -this.velocityX * randomDeviation();
//			break;
//			
//			case BOTTOM_RIGHT:
//				this.velocityX = getRandomVelocity();
//				//this.velocityY = getRandomVelocity();
//				this.velocityY = this.velocityX * randomDeviation();
//			break;
			
			default:
			break;
		}
	}
	
	private float randomDeviation() {
		return (float)Math.random() + 0.5f;
	}
	
	private float getRandomVelocity() {
		return (float)Math.random() * (maxVelocity - minVelocity) + minVelocity;
	}
	
	public float getRotation() {
		return this.rotation;
	}
	
	public void setRotation(float r) {
		this.rotation = r;
	}
	
	public float getRandomAngle() {
		return (float)Math.random() * 360.0f;
	}
	
	@Override
	public void act(float delta) {
		
		moveBy(this.velocityX, -Math.abs(this.velocityY));
		
		if (getX() > Gdx.graphics.getWidth()) {
			this.setX(0);
		}
		
		if (getX() < 0) {
			this.setX(Gdx.graphics.getWidth());
		}
		
		if (getY() > Gdx.graphics.getHeight()) {
			this.setY(0);
		}
		
		if (getY() < 0) {
			this.setY(Gdx.graphics.getHeight());
		}
		
		// Произвольно меняем скорость листья в допустимых пределах
		if (Math.random() > 0.9f) {			
			// Проверка направления движения листка
			this.velocityX = (this.velocityX < 0) ? getRandomVelocity() * -1 : getRandomVelocity();
			this.velocityY = (this.velocityY < 0) ? Math.abs(this.velocityX) * -1 : Math.abs(this.velocityX);
		}
		
		// Случайный поворот листка
		if (Math.random() > 0.99f) {	
			setRotation(getRandomAngle());
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (Math.abs(velocityX) > 0 || Math.abs(velocityY) > 0) {
			CrazySoccer.batch.begin();
			CrazySoccer.batch.draw(leaf, getX(), getY(), 32, 32, 32, 32, 1.0f, 1.0f, rotation);
			CrazySoccer.batch.end();
		}
	}
}
