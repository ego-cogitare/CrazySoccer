package com.mygdx.crazysoccer;

import java.util.Arrays;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Movie {
    private Texture framesSheet;
    private TextureRegion currentFrame;                               
    
    public class MovieParams 
    {
    	public String fileName;
    	public Animation animation;
    	public MovieTypes movieType;
    	private int width;
    	private int height;
    	
        private float SCALE_X = 1.0f;
        private float SCALE_Y = 1.0f;
        private float POS_X = 0; 
        private float POS_Y = 0;
        private boolean FLIP_X = false;
        private boolean FLIP_Y = false;
        
        float stateTime = 0;
        
        private float R = -1, G = -1, B = -1;
    	
    	public MovieParams(MovieTypes movieType, String fileName, Animation animation, int width, int height) {
    		this.movieType = movieType;
    		this.fileName = fileName;
    		this.animation = animation;
    		this.width = width;
    		this.height = height;
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
        
        public int width() {
        	return (int)(SCALE_X * width);
        }
        
        public int height() {
        	return (int)(SCALE_Y * height);
        }

        public void setFlipX(boolean flipX) {
        	this.FLIP_X = flipX;
        }
        
        public void setFlipY(boolean flipY) {
        	this.FLIP_Y = flipY;
        }
        
        public boolean play(boolean playOnce)
        {
        	if (this.animation != null) 
        	{
        		// Текущий кадр анимации
				currentFrame = animation.getKeyFrame(stateTime, true);
				  
				switch (movieType)
				{
					case SPLASH:
						CrazySoccer.batch.begin();
						CrazySoccer.batch.draw(currentFrame, getX(), getY(), width(), height());             
						CrazySoccer.batch.end();
						        
						Color color = GraphUtils.getPixelAt(getX(), getY());
								
						R = color.r;
						G = color.g;
						B = color.b;
								
						Gdx.gl.glClearColor(R, G, B, 1);
						Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); 
					break;
						
					default:
						// Если задан цвет очистки фона
						if (R != -1 || G != -1 || B != -1) 
						{
							Gdx.gl.glClearColor(R, G, B, 1);
							Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
						}
					break;
				}
				
				CrazySoccer.batch.begin();
				//CrazySoccer.batch.draw(currentFrame, 0, 0);
				CrazySoccer.batch.draw(
		    		currentFrame.getTexture(), 
		    		getX(), 
		    		getY(), 
		    		0, 
		    		0, 
		    		width(), 
		    		height(), 
		    		1, 
		    		1, 
		    		0, 
		    		currentFrame.getRegionX(), 
		    		currentFrame.getRegionY(), 
		    		width,
		    		height, 
		    		FLIP_X, 
		    		FLIP_Y
				);
				CrazySoccer.batch.end();
				  
				// Условия для одноразового / циклического проигрывания анимации
				if (!playOnce || !animation.isAnimationFinished(stateTime + animation.getFrameDuration())) {
					stateTime += Gdx.graphics.getDeltaTime();
				}
				// Проигрывание окончено
				else 
				{
					return true;
				}
        	}
		
        	return false;
		}
        
        public int getCurentFrame() {
        	return animation.getKeyFrameIndex(stateTime);
        }
        
        public void setClearColor(float R, float G, float B) {
        	this.R = R;
        	this.G = G;
        	this.B = B;
        }

		public void reset() {
			stateTime = 0f;
		}
	}
    
    public static enum MovieTypes {
    	SPLASH,
    	TEACH_TEACHER,
    	TEACHER_HEAD,
    	TEACH_PUPIL,
    	TEACH_BLACKBOARD,
    	CURSOR,
    	MUSIC_DANCE
    }
    
    public HashMap<MovieTypes, MovieParams> movies;
    
    public Movie() {
    	movies = new HashMap<MovieTypes, MovieParams>();
    }
    
    public void load(MovieTypes name, String fileName, int srcX, int srcY, int width, int height, float frameDuration) {
    	
    	movies.put(name, new MovieParams(name, fileName, null, width, height));
    	framesSheet = new Texture(Gdx.files.internal(movies.get(name).fileName));
    	movies.get(name).animation = 
			new Animation(frameDuration,
				new TextureRegion(framesSheet, srcX, srcY, width, height)
			);
    }
    
    public void load(MovieTypes name, Animation animation) {
    	movies.put(
			name, 
			new MovieParams(
				name, 
				"", 
				animation, 
				animation.getKeyFrame(0).getRegionWidth(),
				animation.getKeyFrame(0).getRegionHeight()
			)
		);
    }
    
    public void load(MovieTypes name, String fileName, int cellsX, int cellsY) 
    {
    	movies.put(name, new MovieParams(name, fileName, null, 0, 0));
    	
    	// Читаем файл с кадрами анимации
    	framesSheet = new Texture(Gdx.files.internal(movies.get(name).fileName));
    	
    	// Подсчет ширины и высоты кадра
    	movies.get(name).width = framesSheet.getWidth() / cellsX;
    	movies.get(name).height = framesSheet.getHeight() / cellsY;
    	
        TextureRegion[][] frames = TextureRegion.split(framesSheet, movies.get(name).width, movies.get(name).height); 
        
        switch (name) 
        {
	        case SPLASH:
		        movies.get(name).animation = 
		    		new Animation(0.028f,
						frames[0][0], frames[0][1], frames[0][2], frames[0][3], frames[1][0], frames[1][1], frames[1][2], frames[1][3],
						frames[2][0], frames[2][1], frames[2][2], frames[2][3], frames[3][0], frames[3][1], frames[3][2], frames[3][3],
						frames[4][0], frames[4][1], frames[4][2], frames[4][3], frames[5][0], frames[5][0], frames[5][0], frames[5][1],
						frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1],
						frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1],
						frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1],
						frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1],
						frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1],
						frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1],
						frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1],
						frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1],
						frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1], frames[5][1]
					); 
	        break;
	        
	        case TEACH_TEACHER:
	        	movies.get(name).animation = 
        			new Animation(0.25f,
    					frames[2][5],
    					frames[2][6]
					);
        	break;
        	
	        case TEACH_PUPIL:
	        	movies.get(name).animation = 
        			new Animation(0.12f,
    					frames[0][5],
    					frames[0][5],
    					frames[0][5],
    					frames[0][5],
    					frames[0][5],
    					frames[0][5],
    					frames[0][5],
    					frames[0][5],
    					frames[0][5],
    					frames[0][5],
    					frames[0][6],
    					frames[0][5],
    					frames[0][6],
    					frames[0][5],
    					frames[0][6],	
    					frames[0][5],
    					frames[0][6]
					);
        	break;
        	
        	default:
    		break;
        }
    }
  
  	public MovieParams getMovie(MovieTypes movieType) {
  		return movies.get(movieType);
  	}
    
  	public boolean isLoaded(MovieTypes movieType) {
  		return (movies != null && movies.get(movieType) != null);
  	}
  	
    public void unload(MovieTypes movieType) {
    	movies.remove(movieType);
    }
}
