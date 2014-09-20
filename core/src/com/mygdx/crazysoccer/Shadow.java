package com.mygdx.crazysoccer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Shadow extends Actor {
	
	private boolean visibility;
	
	public TextureRegion shadow;
	public SpriteBatch shadowSprite;
	
	public Shadow() {
		this.visibility = true;
		
		// Загрузка текстуры тени
		shadow = new TextureRegion(Field.sprites);
		shadow.setRegion(53,0,48,16);
		
		shadowSprite = new SpriteBatch(); 
		this.setZIndex(31);		
	}
	
	public void setVisibility (boolean visibility) {
		this.visibility = visibility;
	}
	
	public boolean getVisibility() {
		return this.visibility;
	}
	
	//@Override
	public void draw() {
		if (this.visibility)  {
			shadowSprite.begin();
			shadowSprite.draw(shadow, getX(), getY());
			shadowSprite.end();
		}
	}
}