package com.mygdx.crazysoccer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Movies {
    private int FRAME_COLS = 0;        
    private int FRAME_ROWS = 0;        
    private int FRAME_WIDTH = 0;
    private int FRAME_HEIGHT = 0;
    
    private float SCALE_X = 1.0f;
    private float SCALE_Y = 1.0f;
    
    private float POS_X = 0;
    private float POS_Y = 0;

    Animation                       animation;          // #3
    Texture                         framesSheet;
    TextureRegion[]                 walkFrames;             // #5
    SpriteBatch                     spriteBatch;            // #6
    TextureRegion                   currentFrame;           // #7

    float stateTime;                                        // #8
    
    public static enum Movie {
    	SPLASH
    }
    
    private HashMap<Movie, Animation> movies;
    
    private HashMap<Movie, String> movieFiles = new HashMap<Movie, String>();

    public Movies() {
    	movies      = new HashMap<Movie, Animation>();
    	spriteBatch = new SpriteBatch();
        stateTime   = 0f;
        
        movieFiles.put(Movie.SPLASH, "movies/splash.png");
    }
    
    public void load(Movie name) {
    	framesSheet = new Texture(Gdx.files.internal(movieFiles.get(name))); 
    	
    	// Размер сетки кадров анимации
    	switch (name)
    	{
    		case SPLASH:
    			FRAME_COLS = 4;
    			FRAME_ROWS = 6;
			break;
    	}
    	
    	FRAME_WIDTH  = framesSheet.getWidth()  / FRAME_COLS;
    	FRAME_HEIGHT = framesSheet.getHeight() / FRAME_ROWS;
    	
        TextureRegion[][] frames = TextureRegion.split(framesSheet, FRAME_WIDTH, FRAME_HEIGHT); 
        
        movies.put(name, 
    		new Animation(0.026f,
				frames[0][0],
				frames[0][1],
				frames[0][2],
				frames[0][3],
				frames[1][0],
				frames[1][1],
				frames[1][2],
				frames[1][3],
				frames[2][0],
				frames[2][1],
				frames[2][2],
				frames[2][3],
				frames[3][0],
				frames[3][1],
				frames[3][2],
				frames[3][3],
				frames[4][0],
				frames[4][1],
				frames[4][2],
				frames[4][3],
				frames[5][0],
				frames[5][0],
				frames[5][0],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][1],
				frames[5][2],
				frames[5][3]
			)
		);    
    }
    
    public void unload(Movie name)
    {
    	movies.remove(name);
    }
    
    public void setScaleXY(float scaleXY) {
    	SCALE_X = SCALE_Y = scaleXY; 
    }
    
    public void setScaleX(float scaleX) {
    	SCALE_X = scaleX; 
    }
    
    public void setScaleY(float scaleY) {
    	SCALE_Y = scaleY; 
    }
    
    public void setX(float x) {
    	POS_X = x;
    }
    
    public void setY(float y) {
    	POS_Y = y;
    }
    
    public void setXY(float x, float y) {
    	POS_X = x;
    	POS_Y = y;
    }
    
    public float getX() {
    	return POS_X;
    }
    
    public float getY() {
    	return POS_Y;
    }
    
    public float getWidth() {
    	return FRAME_WIDTH * SCALE_X;
    }
    
    public float getHeight() {
    	return FRAME_WIDTH * SCALE_X;
    }
    
    public void reset() {
    	stateTime = 0f;
    }
    
    float R = 0.1882353f,
			G = 0.3137255f,
			B = 0.5019608f;
    
    public boolean play(Movie name, boolean playOnce) 
    {
    	if (movies.containsKey(name)) 
    	{
    		// Текущий кадр анимации
	        currentFrame = movies.get(name).getKeyFrame(stateTime, true); 
	        
	        spriteBatch.begin();
	        spriteBatch.draw(currentFrame, POS_X, POS_Y, FRAME_WIDTH * SCALE_X, FRAME_HEIGHT * SCALE_Y);             
	        spriteBatch.end();
	        
	        Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
    		ByteBuffer pixels = pixmap.getPixels();
    		Gdx.gl.glReadPixels((int)POS_X, (int)POS_Y, 1, 1, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);
    		Color color = new Color();
    		Color.rgba8888ToColor(color, pixmap.getPixel(0,0));
    		pixmap.dispose();
    		
    		R = color.r;
    		G = color.g;
    		B = color.b;
    		
    		Gdx.gl.glClearColor(R, G, B, 1);
    		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); 
    		
    		spriteBatch.begin();
	        spriteBatch.draw(currentFrame, POS_X, POS_Y, FRAME_WIDTH * SCALE_X, FRAME_HEIGHT * SCALE_Y);             
	        spriteBatch.end();
	        
	        // Условия для одноразового / циклического проигрывания анимации
	        if (!playOnce || !movies.get(name).isAnimationFinished(stateTime + movies.get(name).getFrameDuration())) {
    			stateTime += Gdx.graphics.getDeltaTime();
	        }
	        // Проигрывание окончено
	        else {
	        	return true;
	        }
    	}
    	else
    	{
    		load(name);
    		
    		stateTime = 0f;
    	}
    	
    	return false;
    }
}
