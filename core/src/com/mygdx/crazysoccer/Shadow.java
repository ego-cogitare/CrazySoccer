package com.mygdx.crazysoccer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Shadow extends Actor {
	
	private boolean visibility;
	
	public TextureRegion shadow;
	
	public Shadow() {
		this.visibility = true;
		
		// Загрузка текстуры тени
		shadow = new TextureRegion(Field.sprites);
		shadow.setRegion(13*32,64,32,32);
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
			CrazySoccer.batch.begin();
			CrazySoccer.batch.draw(
				shadow.getTexture(), 
	    		this.getX(), 
	    		this.getY(), 
	    		0, 
	    		0, 
	    		32, 
	    		32, 
	    		1.0f, 
	    		1.0f, 
	    		0,
	    		shadow.getRegionX(), 
	    		shadow.getRegionY(), 
	    		32, 
	    		32, 
	    		false, 
	    		false
			);
			CrazySoccer.batch.end();
		}
	}
}