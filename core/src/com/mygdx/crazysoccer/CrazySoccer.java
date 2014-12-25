package com.mygdx.crazysoccer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.crazysoccer.Movies.Movie;

public class CrazySoccer extends ApplicationAdapter {
	
	private Field field; 
	
	private Movies movies;
	
	// Игровые экраны
	public static enum GameScreens {
		SPLASH_SCREEN,			// Начальная заставка
		GAME,					// Игра
		TEAM_SELECT,			// Выбор команды
		MUSIC_SELECT, 			// Выбор музыкального сопровождения
		PLAYER_STAT,			// Просмотр информации об игроке
		WEATHER_FORECAST,		// Просмотр прогноза погоды 		
	}
	
	// Текущий игровой экран
	public GameScreens gameScreen = GameScreens.SPLASH_SCREEN;
	
	@Override
	public void create () {
		movies = new Movies();
	}

	@Override
	public void render () {
		// Обработка игры в зависимости от игрового вида
		switch (gameScreen) 
		{
			case SPLASH_SCREEN:
				movies.setXY(Gdx.graphics.getWidth() / 2 - movies.getWidth() / 2, Gdx.graphics.getHeight() - 400);
				movies.setScaleXY(3);
				
				// Если окончено проигрывание анимации
				if (movies.play(Movie.SPLASH, true)) {
					gameScreen = GameScreens.GAME;
				}
			break;
		
			case GAME:
				Gdx.gl.glClearColor(0, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				
				// Проверка иницилизировано ли игровое поле
				if (field == null) {
					field = new Field(new ScreenViewport());
					field.LoadMap("level01.tmx"); 
					
					// Привязываем слушатель ввода
					Gdx.input.setInputProcessor(field);
				}
				
				Field.camera.update();
				field.fieldMapRenderer.setView(Field.camera);
				field.fieldMapRenderer.render();
				
				// Обработка входящей информации от игрока и изменение поведения героев
				field.act();
					
				// Обработчик процесса игры
				field.processGame();
				
				// Отрисовка сцены и всех добавленных в нее актеров
				field.draw();
			break;
			
			default:
			break;
		}
	}
}