package com.mygdx.crazysoccer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CrazySoccer extends ApplicationAdapter {
	
	private Field field; 
	
	@Override
	public void create () {
		field = new Field(new ScreenViewport());
		field.LoadMap("field.tmx");
		
		Gdx.input.setInputProcessor(field);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		field.camera.update();
		field.fieldMapRenderer.setView(field.camera);
		field.fieldMapRenderer.render();
		
		// Обработка входящей информации от игрока и изменение поведения героев
		field.act();
		
		field.processGame();
		
		// Отрисовка сцены и всех добавленных в нее актеров
		field.draw();
	}
}
