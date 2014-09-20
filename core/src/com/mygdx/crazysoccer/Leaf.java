package com.mygdx.crazysoccer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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
	public SpriteBatch leafSprite;
	public WindDirections windDirection;
	
	public Leaf(WindDirections wd) {
		leaf = new TextureRegion(Field.sprites);
		leaf.setRegion(45,5,8,8);
		leafSprite = new SpriteBatch();
		windDirection = wd;
		setWindDirection(windDirection);
	}
	
	public void setWindVelocity(float v) {
		this.maxVelocity = v;
		this.minVelocity = this.maxVelocity * 0.5f;
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
				this.velocityY = this.velocityX * randomDeviation();
				//this.velocityY = getRandomVelocity() * -1;
			break;
			
			case LEFT_TOP:
				this.velocityX = getRandomVelocity();
				this.velocityY = this.velocityX * randomDeviation();
				//this.velocityY = getRandomVelocity();
			break;
			
			case TOP_DOWN:
				this.velocityX = randomDeviation();
				this.velocityY = getRandomVelocity() * -1;
			break;
			
			case TOP_LEFT:
				this.velocityX = getRandomVelocity() * -1;
				this.velocityY = this.velocityX * randomDeviation();
				//this.velocityY = getRandomVelocity() * -1;
			break;
			
			case TOP_RIGHT:
				this.velocityX = getRandomVelocity();
				this.velocityY = -this.velocityX * randomDeviation();
				//this.velocityY = getRandomVelocity() * -1;
			break;
			
			case RIGHT_LEFT:
				this.velocityX = getRandomVelocity() * -1;
				this.velocityY = randomDeviation();
			break;
			
			case RIGHT_BOTTOM:
				this.velocityX = getRandomVelocity() * -1;
				//this.velocityY = getRandomVelocity() * -1;
				this.velocityY = this.velocityX * randomDeviation();
			break;
			
			case RIGHT_TOP:
				this.velocityX = getRandomVelocity() * -1;
				//this.velocityY = getRandomVelocity();
				this.velocityY = -this.velocityX * randomDeviation();
			break;
			
			case BOTTOM_TOP:
				this.velocityX = randomDeviation();
				this.velocityY = getRandomVelocity();
			break;
			
			case BOTTOM_LEFT:
				this.velocityX = getRandomVelocity() * -1;
				//this.velocityY = getRandomVelocity();
				this.velocityY = -this.velocityX * randomDeviation();
			break;
			
			case BOTTOM_RIGHT:
				this.velocityX = getRandomVelocity();
				//this.velocityY = getRandomVelocity();
				this.velocityY = this.velocityX * randomDeviation();
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
		
		moveBy(this.velocityX, this.velocityY);
		
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
		if (windDirection != WindDirections.NONE) {
			leafSprite.begin();
			leafSprite.draw(new TextureRegion(leaf), getX(), getY(), 10, 10, 20, 20, 0.9f, 0.9f, rotation);
			leafSprite.end();
		}
	}
}
