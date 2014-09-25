package com.mygdx.crazysoccer;

import com.badlogic.gdx.graphics.g2d.Batch;
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
		shadow.setRegion(53,0,8,4);
		
		shadowSprite = new SpriteBatch(); 
	}
	
	public void setVisibility (boolean visibility) {
		this.visibility = visibility;
	}
	
	public boolean getVisibility() {
		return this.visibility;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (this.visibility)  {
			shadowSprite.begin();
			shadowSprite.draw(
				shadow.getTexture(), 
	    		this.getX(), 
	    		this.getY(), 
	    		0, 
	    		0, 
	    		8, 
	    		4, 
	    		3.0f, 
	    		3.0f, 
	    		0,
	    		shadow.getRegionX(), 
	    		shadow.getRegionY(), 
	    		8, 
	    		4, 
	    		false, 
	    		false
			);
			shadowSprite.end();
		}
	}
}