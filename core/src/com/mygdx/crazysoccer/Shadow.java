package com.mygdx.crazysoccer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.crazysoccer.Wind.WindDirections;

public class Shadow extends Actor {
	
	private boolean visibility;
	
	public Texture shadow;
	public SpriteBatch shadowSprite;
	
	public Shadow() {
		this.visibility = true;
		shadow = new Texture(Gdx.files.internal("shadow.png"));
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