package com.mygdx.crazysoccer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Gate extends Actor {

	private int id;
	
	private float POS_X;
	private float POS_Y;
	
	private TextureRegion gate;
	private SpriteBatch gateSprite;
	
	public Gate(int id) {
		super();
		
		this.id = id;
		
		gate = new TextureRegion(Field.sprites);
		gate.setRegion(0,0,45,125);
		
		gateSprite = new SpriteBatch();
	}
	
	public float getAbsX() {
		return this.POS_X;
	}
	
	public float getAbsY() {
		return this.POS_Y;
	}
	
	public void setAbsX(float x) {
		this.POS_X = x;
	}
	
	public void setAbsY(float y) {
		this.POS_Y = y;
	}
	
	@Override
	public void act(float delta) {
		
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		gateSprite.begin();
		gateSprite.draw(
			gate.getTexture(), 
    		this.getX(), 
    		this.getY(), 
    		0, 
    		0, 
    		45, 
    		125, 
    		3.0f, 
    		3.0f, 
    		0,
    		gate.getRegionX(), 
    		gate.getRegionY(), 
    		45, 
    		125, 
    		id == 0, 
    		false
		);
		gateSprite.end();
	}
}
