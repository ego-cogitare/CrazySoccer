package com.mygdx.crazysoccer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Leaf extends Actor {
	
	private float rotation;
	private float velocityX, velocityY;
	
	public Texture leaf;
	public SpriteBatch leafSprite;
	
	public static enum WindDirections {
		LEFT_RIGHT,
		LEFT_BOTTOM,
		LEFT_TOP,
		TOP_DOWN,
		TOP_LEFT,
		TOP_RIGHT,
		RIGHT_LEFT,
		RIGHT_BOTTOM,
		RIGHT_TOP,
		BOTTOM_TOP,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}
	
	public WindDirections windDirection;
	
	public Leaf() {
		leaf = new Texture(Gdx.files.internal("leaf.png"));
		leafSprite = new SpriteBatch(); 
		windDirection = WindDirections.LEFT_RIGHT;
		
		setRandomVelocity();
	}
	
	public void setWindDirection(WindDirections wd) {
		windDirection = wd;
		
		switch (wd) {
			case LEFT_RIGHT:
				this.velocityY = 0;
			break;
			
			case LEFT_BOTTOM:
				this.velocityY = this.velocityX;
			break;
			
			case LEFT_TOP:
				this.velocityY = this.velocityX;
			break;
			
			case TOP_DOWN:
				this.velocityY = this.velocityX;
				this.velocityX = 0.0f;
			break;
			
			case TOP_LEFT:
				this.velocityY = -this.velocityX;
				this.velocityX = -this.velocityX;
			break;
			
			case TOP_RIGHT:
				this.velocityY = this.velocityX;
			break;
			
//			case TOP_DOWN:
//				this.velocityY = this.velocityX;
//				this.velocityX = 0.0f;
//			break;
		}
	}
	
	public void setRandomVelocity() {
		this.velocityX = (float)Math.random() * 20 + 8;
	}
	
	public float getRotation() {
		return this.rotation;
	}
	
	public void setRotation(float r) {
		this.rotation = r;
	}
	
	public void setRandomAngle() {
		this.rotation = (float)Math.random() * 360.0f;
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
		
		// Произвольно меняем направление листьев
		if (Math.random() > 0.95f) {
			// Произвольный поворот листа
			setRandomAngle();
			
			// Проверка направления движения листка
			boolean vNegative = (this.velocityX < 0);
			setRandomVelocity();
			if (vNegative) this.velocityX *= -1;
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		leafSprite.begin();
		leafSprite.draw(new TextureRegion(leaf), getX(), getY(), 10, 10, 20, 20, 1.0f, 1.0f, rotation);
		leafSprite.end();
	}
}
