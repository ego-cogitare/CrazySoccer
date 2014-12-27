package com.mygdx.crazysoccer;

import java.util.HashMap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.crazysoccer.GameScreen.GameScreens;
import com.mygdx.crazysoccer.Movie.MovieTypes;

public class CrazySoccer extends ApplicationAdapter {
	
	public static BitmapFont font;
	
	public static SpriteBatch batch;
	
	private HashMap<String,Menu> menus = new HashMap<String,Menu>();
	
	// Игровое поле
	private Field field; 
	
	// Класс для проигрывания анимации
	private Movie movie;
	
	// Класс для работы со звуком
	public Sounds sounds;
	
	@Override
	public void create () {
		// Инициализация класса для отображения анимации
		movie = new Movie();
		
		// Класс для работы с музыкой и звуковыми эффектами
		sounds = new Sounds();
		
		font = new BitmapFont();
		
		batch = new SpriteBatch();
		
		menus.put("main_menu", new Menu());
		menus.get("main_menu").addItem(new Menu.Item(100,190,"TACTICS",0));
		menus.get("main_menu").addItem(new Menu.Item(100,160,"PLAYERS",0));
		menus.get("main_menu").addItem(new Menu.Item(100,100,"START!",0));
		menus.get("main_menu").addItem(new Menu.Item(500,190,"WEATHER",1));
		menus.get("main_menu").addItem(new Menu.Item(500,160,"TEAMS",1));
		menus.get("main_menu").addItem(new Menu.Item(500,130,"PLAYERS INFO",1));
		menus.get("main_menu").addItem(new Menu.Item(500,100,"MUSIC",1));
		
		
		menus.put("music_select", new Menu());
		menus.get("music_select").addItem(new Menu.Item(100,220,"Goal 3 end title",0));
		menus.get("music_select").addItem(new Menu.Item(100,190,"Ike-ike hockey",0));
		menus.get("music_select").addItem(new Menu.Item(100,160,"Nekketsu kakutou dentetsu end title",0));
		menus.get("music_select").addItem(new Menu.Item(100,130,"Goal 3 battle theme",0));
	}

	@Override
	public void render () {
		// Обработка игры в зависимости от игрового вида
		switch (GameScreen.getScreen()) 
		{
			case SPLASH_SCREEN:
				if (movie.isLoaded(MovieTypes.SPLASH))
				{
					// Если окончено проигрывание анимации
					if (movie.getMovie(MovieTypes.SPLASH).play(true)) {
						GameScreen.setScreen(GameScreens.PREPORATIONS);
						
						// Освобождаем ресурсы
						movie.unload(MovieTypes.SPLASH);
					}
				}
				else
				{
					movie.load(MovieTypes.SPLASH, "movies/splash.png", 4, 6);
				
					movie.getMovie(MovieTypes.SPLASH).setScaleXY(3.0f);
					
					movie.getMovie(MovieTypes.SPLASH).setClearColor(1, 1, 1);
					
					movie.getMovie(MovieTypes.SPLASH).setXY(
						Gdx.graphics.getWidth() / 2 - movie.getMovie(MovieTypes.SPLASH).width() / 2, 
						Gdx.graphics.getHeight() - 400
					);
				}
			break;
			
			case MUSIC_SELECT:
				
				if (movie.isLoaded(MovieTypes.TEACH_TEACHER))
				{
					movie.getMovie(MovieTypes.TEACH_TEACHER).play(false);
					
//					Sounds.load("bg", "sound/bg/background01.ogg");
//					Sounds.play("bg");
//					Sounds.loop("bg", true);
//					Sounds.volume("bg", 0.2f);
				}
				else
				{
					movie.load(MovieTypes.TEACH_TEACHER, "graphics/kunio.png", 8, 8);
					
					// Установка коеффициента увеличения кадра
					movie.getMovie(MovieTypes.TEACH_TEACHER).setScaleXY(3);
					
					// Цвет фона, которым будет очищаться окно (бекграунд)
					movie.getMovie(MovieTypes.TEACH_TEACHER).setClearColor(0.1875f, 0.3125f, 0.5f);
					
					movie.getMovie(MovieTypes.TEACH_TEACHER).setFlipX(true);
					
					// Установка позиции проигрыания
					movie.getMovie(MovieTypes.TEACH_TEACHER).setXY(500,500);
				}
				
				
				if (Gdx.input.isKeyJustPressed(Keys.UP))
					menus.get("music_select").cursorMove(Keys.UP);
				
				if (Gdx.input.isKeyJustPressed(Keys.DOWN))
					menus.get("music_select").cursorMove(Keys.DOWN);
				
				if (Gdx.input.isKeyJustPressed(Keys.LEFT))
					menus.get("music_select").cursorMove(Keys.LEFT);
				
				if (Gdx.input.isKeyJustPressed(Keys.RIGHT))
					menus.get("music_select").cursorMove(Keys.RIGHT);
				
				menus.get("music_select").draw();
				
				
				if (movie.isLoaded(MovieTypes.CURSOR)) {
					movie.getMovie(MovieTypes.CURSOR).play(true);
				
					movie.getMovie(MovieTypes.CURSOR).setXY(menus.get("music_select").getActive().getX() - 24, menus.get("music_select").getActive().getY() - 16);
				}
				else {
					movie.loadStatic(MovieTypes.CURSOR, "graphics/ball.png", 0, 0, 16, 16);
				}
				
				if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.DOWN) || !Sounds.isLoaded("bg")) {
					
					// Выгружаем текущую музыку с памяти
					Sounds.unload("bg");
					
					switch (menus.get("music_select").getActive().getId()) {
						
						case 0:
							Sounds.load("bg", "sound/bg/background01.ogg");
						break;
						
						case 1:
							Sounds.load("bg", "sound/bg/background02.ogg");
						break;
						
						case 2:
							Sounds.load("bg", "sound/bg/background03.ogg");
						break;
						
						case 3:
							Sounds.load("bg", "sound/bg/background04.ogg");
						break;
					}
					
					Sounds.play("bg");
					Sounds.loop("bg", true);
					Sounds.volume("bg", 0.2f);
				}
				else if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
					
					// Выгружаем прослушиваемый трек
					Sounds.unload("bg");
					
					// Переключаемся на главное меню
					GameScreen.setScreen(GameScreens.PREPORATIONS);
				}
			break;
			
			case PREPORATIONS:
				
				if (!Sounds.isLoaded("mainmenu")) {
					Sounds.load("mainmenu", "sound/bg/mainmenu.ogg");
					Sounds.play("mainmenu");
					Sounds.loop("mainmenu", true);
					Sounds.volume("mainmenu", 0.5f);
				}
				
				if (movie.isLoaded(MovieTypes.TEACH_TEACHER))
					movie.getMovie(MovieTypes.TEACH_TEACHER).play(false);
				else {
					movie.load(MovieTypes.TEACH_TEACHER, "graphics/kunio.png", 8, 8);
					
					// Установка коеффициента увеличения кадра
					movie.getMovie(MovieTypes.TEACH_TEACHER).setScaleXY(3);
					
					// Цвет фона, которым будет очищаться окно (бекграунд)
					movie.getMovie(MovieTypes.TEACH_TEACHER).setClearColor(0.1875f, 0.3125f, 0.5f);
					
					movie.getMovie(MovieTypes.TEACH_TEACHER).setFlipX(true);
					
					// Установка позиции проигрыания
					movie.getMovie(MovieTypes.TEACH_TEACHER).setXY(500,500);
				}
				
				
				if (movie.isLoaded(MovieTypes.TEACH_PUPIL))
					movie.getMovie(MovieTypes.TEACH_PUPIL).play(false);
				else {
					movie.load(MovieTypes.TEACH_PUPIL, "graphics/states.png", 8, 8);
					
					// Установка коеффициента увеличения кадра
					movie.getMovie(MovieTypes.TEACH_PUPIL).setScaleXY(3);
					
					// Установка позиции проигрыания
					movie.getMovie(MovieTypes.TEACH_PUPIL).setXY(370,490);
				}
				
				
				if (movie.isLoaded(MovieTypes.TEACH_BLACKBOARD)) 
					movie.getMovie(MovieTypes.TEACH_BLACKBOARD).play(true);
				else {
					movie.loadStatic(MovieTypes.TEACH_BLACKBOARD, "graphics/atlas.png", 512, 0, 240, 204);
					
					movie.getMovie(MovieTypes.TEACH_BLACKBOARD).setXY(650,500);
				}
				
				if (Gdx.input.isKeyJustPressed(Keys.UP))
					menus.get("main_menu").cursorMove(Keys.UP);
				
				if (Gdx.input.isKeyJustPressed(Keys.DOWN))
					menus.get("main_menu").cursorMove(Keys.DOWN);
				
				if (Gdx.input.isKeyJustPressed(Keys.LEFT))
					menus.get("main_menu").cursorMove(Keys.LEFT);
				
				if (Gdx.input.isKeyJustPressed(Keys.RIGHT))
					menus.get("main_menu").cursorMove(Keys.RIGHT);
				
				menus.get("main_menu").draw();
				
				
				if (movie.isLoaded(MovieTypes.CURSOR)) {
					movie.getMovie(MovieTypes.CURSOR).play(true);
				
					movie.getMovie(MovieTypes.CURSOR).setXY(menus.get("main_menu").getActive().getX() - 24, menus.get("main_menu").getActive().getY() - 16);
				}
				else 
					movie.loadStatic(MovieTypes.CURSOR, "graphics/ball.png", 0, 0, 16, 16);
				
				
				
				if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
					
					switch (menus.get("main_menu").getActive().getId()) {
						
						// Tactics
						case 0:
						break;
						
						// Players
						case 1:
						break;
						
						// Start!
						case 2:
							Sounds.unload("mainmenu");
							movie.unload(MovieTypes.TEACH_TEACHER);
							movie.unload(MovieTypes.TEACH_PUPIL);
							
							// Переключение игрового экрана
							GameScreen.setScreen(GameScreens.GAME);
						break;
						
						// Weather
						case 3:
						break;
						
						// Teams 
						case 4:
						break;
							
						// Players info
						case 5:
						break;
							
						// Music
						case 6:
							Sounds.unload("mainmenu");
							movie.unload(MovieTypes.TEACH_TEACHER);
							movie.unload(MovieTypes.TEACH_PUPIL);
							
							// Переключение игрового экрана
							GameScreen.setScreen(GameScreens.MUSIC_SELECT);
						break;
					}
				}
			break;
		
			case GAME:
				Gdx.gl.glClearColor(0, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				
				// Проверка иницилизировано ли игровое поле
				if (field == null) {
					field = new Field(new ScreenViewport());
					field.LoadMap("levels/level01.tmx"); 
					
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