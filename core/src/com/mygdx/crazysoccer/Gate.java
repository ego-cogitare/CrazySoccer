package com.mygdx.crazysoccer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Gate extends Actor {
	// 930 732
	private int ID;
	private float POS_X;
	private float POS_Y;
	
	private float WIDTH = 195.0f;
	private float HEIGHT = 195.0f;
	private Vector2 BOTTOM_BAR;
	private Vector2 TOP_BAR;
	
	// Для отрисовки вздувшихся ворот
	private long DRAW_FLATUS_TIME_START = 0L;
	
	// Минимальная скорость удара мяча о сетку, для того чтобы отрисовывать вздувшуюся сетку
	private float MIN_FLATUS_VELOCITY = 4.2f; 
	
	// Идентификатор ворот
	public static final int LEFT_GATES = 0;
	public static final int RIGHT_GATES = 1;
	
	private TextureRegion gate;
	
	// Фундамент ворот (используется для поиска пересечений)
	public float[][] gateProjection = new float[8][2];
	
	public Gate(int id) {
		super();
		
		this.ID = id; 
		
		gate = new TextureRegion(Field.sprites);
		gate.setRegion(192,0,135,376);
	}
	
	public float getHeight() {
		return this.HEIGHT;
	}
	
	public float getWidth() {
		return this.WIDTH;
	}
	
	public void drawFlatus() {
		this.DRAW_FLATUS_TIME_START = System.nanoTime();
	}
	
	public float minFlatusVelocity() {
		return this.MIN_FLATUS_VELOCITY;
	}
	
	public Vector2 getBottomBar() {
		return this.BOTTOM_BAR;
	}
	
	public Vector2 getTopBar() {
		return this.TOP_BAR;
	}
	
	public void setBottomBar(Vector2 p) {
		this.BOTTOM_BAR = p;
	}
	
	public void setTopBar(Vector2 p) {
		this.TOP_BAR = p;
	}
	
	public float getAbsX() {
		return this.POS_X;
	}
	
	public float getAbsY() {
		return this.POS_Y;
	}
	
	public float height() {
		return this.HEIGHT;
	}
	
	public float width() {
		return this.WIDTH;
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
		CrazySoccer.batch.begin();
		
		// Рисовать ли вздутие ворот
		if (System.nanoTime() - this.DRAW_FLATUS_TIME_START < 200000000L) {
			CrazySoccer.batch.draw(
				Field.sprites, 
				this.getX() + ((ID == 0) ? -18 : 91), 
				this.getY() + 30, 
				64, 
				320, 
				352, 
				0, 
				64, 
				320, 
				ID == 0, 
				false
			);
		}
		
		CrazySoccer.batch.draw(
			gate.getTexture(), 
    		this.getX(), 
    		this.getY(), 
    		0, 
    		0, 
    		gate.getRegionWidth(), 
    		gate.getRegionHeight(),
    		1.0f, 
    		1.0f, 
    		0,
    		gate.getRegionX(), 
    		gate.getRegionY(), 
    		gate.getRegionWidth(), 
    		gate.getRegionHeight(), 
    		ID == 0, 
    		false
		);
		CrazySoccer.batch.end();
	}
}
