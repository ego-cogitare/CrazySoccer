package com.mygdx.crazysoccer;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Field extends Stage {
	
	private Stage stage;
	
	// Екземпляр класса описывающего игрока
	private Player player;
	
	public int KEY_FLAGS = 0;
	/* Numbers of bits 
	 * 1. Up
	 * 2. Down
	 * 3. Left
	 * 4. Right
	 * 5. Up */
	public static boolean UP = false;
	public static boolean DOWN = false;
	public static boolean LEFT = false;
	public static boolean RIGHT = false;
	public static boolean JUMP = false;
	public static boolean LEFT_HAND_KICK = false;
	
	
	// Четыре точки определяющие размеры поля
	private Vector2 leftBottom;
	private Vector2 rightBottom;
	private Vector2 rightTop;
	private Vector2 leftTop;
	
	// Размры игрового (футбольного) поля
	private int fieldMinWidth;
	private int fieldMaxWidth;
	private int fieldHeight;
	
	// Смещение игрового поля относительно карты (левого нижнего угла)
	private int fieldOffsetX;
	private int fieldOffsetY;
	
	public TiledMap fieldMap;
    public TiledMapRenderer fieldMapRenderer;
    public static OrthographicCamera camera;
    
    public ShapeRenderer shapeRenderer;
	
	
	public Field(ScreenViewport screenViewport) {
		super(screenViewport);
		
		player = new Player();
		// Добавление игрока (актера) на сцену (поле)
		this.addActor(player);
		
		// Для отрисовки линий поля
		shapeRenderer = new ShapeRenderer();
		
		// Создание камеры
		camera = new OrthographicCamera(Vars.WINDOW_WIDTH, Vars.WINDOW_HEIGHT);
        camera.position.set(Vars.WINDOW_WIDTH / 2.0f, Vars.WINDOW_HEIGHT / 2.0f, 0);
        camera.update();
	}
	
	public void LoadMap(String mapName) {
		fieldMap = new TmxMapLoader().load(mapName);
		fieldMapRenderer = new OrthogonalTiledMapRenderer(fieldMap, 1.0f);
		
		
		for (int i = 0; i < fieldMap.getLayers().get("field").getObjects().getCount(); i++) {
        	MapObject ma = (MapObject)fieldMap.getLayers().get("field").getObjects().get(i);
        	
        	if ((Object)ma instanceof PolylineMapObject) {
        		Polyline polyline = ((PolylineMapObject)ma).getPolyline();
        		
        		// Получение опорных точек трапеции поля
        		leftBottom  = new Vector2(polyline.getVertices()[0],polyline.getVertices()[1]);
        		rightBottom = new Vector2(polyline.getVertices()[2],polyline.getVertices()[3]);
        		rightTop    = new Vector2(polyline.getVertices()[4],polyline.getVertices()[5]);
        		leftTop     = new Vector2(polyline.getVertices()[6],polyline.getVertices()[7]);
        		
        		Arrays.sort(polyline.getVertices());
        		
        		// Получение размеров игрового поля
        		fieldMinWidth = (int)polyline.getVertices()[9] - (int)polyline.getVertices()[0];
        		fieldMaxWidth = (int)polyline.getVertices()[8] - (int)polyline.getVertices()[5];
        		fieldHeight   = (int)polyline.getVertices()[6] - (int)polyline.getVertices()[0];
        		
        		// Получение смещения разметки поля относительно карты
        		fieldOffsetX = Math.round(ma.getProperties().get("x", Float.class));
        		fieldOffsetY = Math.round(ma.getProperties().get("y", Float.class));
        		
//        		for (int h = 0; h < fieldHeight; h++) {
//        			System.out.println(h+" "+mGetSideLineProjection(h));
//        		}
        	}
		}
	}
	
	// Получение длины проекции отрезка на ось аута поля (используется для проверки
	// находится ли объект в пределах поля)
	private float mGetSideLineProjection(int h) {
		float b = (fieldMaxWidth - fieldMinWidth) / 2.0f;
		float c = (float)Math.sqrt(fieldHeight * fieldHeight + b * b);
		float sinAlpha = (float)fieldHeight / c;
		float cosAlpha = (float)b / c;
		
		return Math.abs(h / (sinAlpha / cosAlpha));
	}
	
	public String printFieldSizes() {
		return "Min width:"+fieldMinWidth+"\n"+"Max width:"+fieldMaxWidth+"\n"+"Height:"+fieldHeight;
	}
	
	
	public void resize (int width, int height) {
	    // See below for what true means.
	    stage.getViewport().update(width, height, true);
	}
	
	// Отрисовка поля
	public void drawField() {
		 Gdx.gl20.glLineWidth(10);
		 shapeRenderer.setProjectionMatrix(camera.combined);
		 shapeRenderer.begin(ShapeType.Line);
		 shapeRenderer.setColor(1, 1, 1, 1);
		 shapeRenderer.line(leftBottom.x + fieldOffsetX, leftBottom.y + fieldOffsetY, rightBottom.x + fieldOffsetX, rightBottom.y + fieldOffsetY);
		 shapeRenderer.line(rightBottom.x + fieldOffsetX, rightBottom.y + fieldOffsetY, rightTop.x + fieldOffsetX, rightTop.y + fieldOffsetY);
		 shapeRenderer.line(rightTop.x + fieldOffsetX, rightTop.y + fieldOffsetY, leftTop.x + fieldOffsetX, leftTop.y + fieldOffsetY);
		 shapeRenderer.line(leftTop.x + fieldOffsetX, leftTop.y + fieldOffsetY, leftBottom.x + fieldOffsetX, leftBottom.y + fieldOffsetY);
		 
		 shapeRenderer.line(leftBottom.x + fieldMaxWidth / 2.0f + fieldOffsetX, leftBottom.y + fieldOffsetY, leftBottom.x + fieldMaxWidth / 2.0f + fieldOffsetX, leftTop.y + fieldOffsetY);
		 
		 shapeRenderer.circle(leftBottom.x + fieldMaxWidth / 2.0f + fieldOffsetX, leftBottom.y + fieldHeight / 2.0f + fieldOffsetY, 200);
		 
		 shapeRenderer.arc(leftBottom.x + fieldOffsetX, leftBottom.y + fieldOffsetY, 100, 0, 88);
		 shapeRenderer.arc(leftTop.x + fieldOffsetX, leftTop.y + fieldOffsetY, 100, -91, 91);
		 shapeRenderer.end();
	}

	public void processGame() { 
		drawField();
	}

	public void dispose() {
	    stage.dispose();
	}
	
	@Override
	public boolean keyUp(int keycode) {
		switch (keycode)
		{
			case Keys.UP: //UP
				this.KEY_FLAGS = this.KEY_FLAGS & 0x1E;
			break;
			
			case Keys.DOWN: //DOWN
				this.KEY_FLAGS = this.KEY_FLAGS & 0x1D;
			break;
				
			case Keys.LEFT: //LEFT
				this.KEY_FLAGS = this.KEY_FLAGS & 0x1B;
			break;
				
			case Keys.RIGHT: //RIGHT
				this.KEY_FLAGS = this.KEY_FLAGS & 0x17;
			break;
			
			case Keys.SPACE: //SPACE
				this.KEY_FLAGS = this.KEY_FLAGS & 0x0F;
			break;
			
			case Keys.Q: //Q - Hand kick
				this.KEY_FLAGS = this.KEY_FLAGS & 0x1F;
			break;
		}
		
		binaryToKeys();

		return false;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		
		switch (keycode)
		{
			case Keys.UP: //UP
				this.KEY_FLAGS = this.KEY_FLAGS | 0x01;
			break;
			
			case Keys.DOWN: //DOWN
				this.KEY_FLAGS = this.KEY_FLAGS | 0x02;
			break;
				
			case Keys.LEFT: //LEFT
				this.KEY_FLAGS = this.KEY_FLAGS | 0x04;
			break;
				
			case Keys.RIGHT: //RIGHT
				this.KEY_FLAGS = this.KEY_FLAGS | 0x08;
			break;
			
			case Keys.SPACE: //SPACE
				this.KEY_FLAGS = this.KEY_FLAGS | 0x10;
			break;
			
			case Keys.Q: //Q - Hand kick
				this.KEY_FLAGS = this.KEY_FLAGS | 0x20;
			break;
		}
		
		binaryToKeys();
		
		return false;
	}
	
	private void binaryToKeys() {
		this.UP    = ((this.KEY_FLAGS & 0x01) == 0x01);
		this.DOWN  = ((this.KEY_FLAGS & 0x02) == 0x02);
		this.LEFT  = ((this.KEY_FLAGS & 0x04) == 0x04);
		this.RIGHT = ((this.KEY_FLAGS & 0x08) == 0x08);
		this.JUMP  = ((this.KEY_FLAGS & 0x10) == 0x10);
		this.LEFT_HAND_KICK = ((this.KEY_FLAGS & 0x20) == 0x20);
		
//		String s = "";
//		if (this.UP)    s += "UP, ";
//		if (this.DOWN)  s += "DOWN, ";
//		if (this.LEFT)  s += "LEFT, ";
//		if (this.RIGHT) s += "RIGHT, ";
//		if (this.JUMP)  s += "JUMP, ";
//		if (this.HAND_KICK)  s += "HAND KICK, ";
//		System.out.println(s);
	}
}