package com.mygdx.crazysoccer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Shadow {
	
	private float x;
	private float y;
	private boolean visibility;
	
	public Texture shadow;
	public SpriteBatch shadowSprite;
	
	public void setX(float newX) {
		this.x = newX;
	}
	
	public void setY(float newY) {
		this.y = newY;
	}
	
	public void setXY(float newX, float newY) {
		this.x = newX;
		this.y = newY;
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public Shadow() {
		this.x = 0;
		this.y = 0;
		this.visibility = true;
		shadow = new Texture(Gdx.files.internal("shadow.png"));
		shadowSprite = new SpriteBatch(); 
	}
	
	public void setVisibility (boolean visibility) {
		this.visibility = visibility;
	}
	
	public boolean getVisibility() {
		return this.visibility;
	}
	
	public void draw() {
		if (this.visibility)  {
			shadowSprite.begin();
			shadowSprite.draw(shadow, this.x, this.y);
			shadowSprite.end();
		}
	}
}