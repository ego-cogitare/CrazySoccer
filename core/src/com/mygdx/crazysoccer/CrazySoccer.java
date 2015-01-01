package com.mygdx.crazysoccer;

import java.util.HashMap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.mygdx.crazysoccer.Field.GameStates;
import com.mygdx.crazysoccer.GameScreen.ScreenNames;
import com.mygdx.crazysoccer.Movie.MovieTypes;

public class CrazySoccer extends ApplicationAdapter {
	
	public static BitmapFont font;
	
	public static ShapeRenderer shapeRenderer;
	
	public static Matrix4 shapeProjectionMatrix;
	
	public static SpriteBatch batch;
	
	public static HashMap<String,Menu> menus = new HashMap<String,Menu>();
	
	// Игровое поле
	private Field field; 
	
	// Класс для проигрывания анимации
	private Movie movie;
	
	// Класс для работы со звуком
	public Sounds sounds;
	
	@Override
	public void create () {
		// Инициализация игровых экранов
		GameScreen.init();
		
		// Инициализация класса для отображения анимации
		movie = new Movie();
		
		// Класс для работы с музыкой и звуковыми эффектами
		sounds = new Sounds();
		
		// Для отрисовки линий поля
		shapeRenderer = new ShapeRenderer();
		shapeProjectionMatrix = new Matrix4(shapeRenderer.getProjectionMatrix());
		
		System.out.println(shapeProjectionMatrix);
		
		font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"),Gdx.files.internal("fonts/font.png"),false);
		
		batch = new SpriteBatch();
		
		menus.put("main_menu", new Menu());
		menus.get("main_menu").addItem(new Menu.Item(130,260,"TACTICS",0));
		menus.get("main_menu").addItem(new Menu.Item(130,210,"PLAYERS",0));
		menus.get("main_menu").addItem(new Menu.Item(130,160,"PLAYERS INFO",0));
		menus.get("main_menu").addItem(new Menu.Item(130,110,"START!",0));
		menus.get("main_menu").addItem(new Menu.Item(650,260,"WEATHER",1));
		menus.get("main_menu").addItem(new Menu.Item(650,210,"TEAMS",1));
		menus.get("main_menu").addItem(new Menu.Item(650,160,"MUSIC",1));
		menus.get("main_menu").addItem(new Menu.Item(650,110,"EXIT",1));
		menus.get("main_menu").setActive(3);
		
		menus.put("music_select", new Menu());
		menus.get("music_select").addItem(new Menu.Item(400,620,"GOAL 3 END THEME",0));
		menus.get("music_select").addItem(new Menu.Item(400,550,"IKE-IKE HOCKEY",0));
		menus.get("music_select").addItem(new Menu.Item(400,480,"KNUKLE FIGHT END THEME",0));
		menus.get("music_select").addItem(new Menu.Item(400,410,"GOAL 3 BATTLE THEME",0));
		menus.get("music_select").addItem(new Menu.Item(400,100,"BACK TO MENU",0));
		
		menus.put("give_up", new Menu());
		menus.get("give_up").addItem(new Menu.Item(520,GraphUtils.screenCenterY() + 100,"No",0));
		menus.get("give_up").addItem(new Menu.Item(580,GraphUtils.screenCenterY() + 100,"Yes",1));
	}
	
	private void drawTitle() {
		batch.begin();
		font.draw(
			CrazySoccer.batch,
			GameScreen.getScreen().getTitle(), 
			Gdx.graphics.getWidth() / 2 - font.getBounds(
				GameScreen.getScreen().getTitle()
			).width / 2, 
			Gdx.graphics.getHeight() - 50
		);
		batch.end();
	}

	private void drawMenuCursor(String menuName) {
		if (movie.isLoaded(MovieTypes.CURSOR)) {
			movie.getMovie(MovieTypes.CURSOR).play(true);
		
			movie.getMovie(MovieTypes.CURSOR).setXY(menus.get(menuName).getActive().getX() - 42, menus.get(menuName).getActive().getY() - 30);
		}
		else {
			movie.load(MovieTypes.CURSOR, "graphics/ball.png", 0, 0, 16, 16, 999.0f);
			movie.getMovie(MovieTypes.CURSOR).setScaleXY(2.0f);
		}
	}
	
	private void handleMenu(String menuName) {
		if (Gdx.input.isKeyJustPressed(Keys.UP))
			menus.get(menuName).cursorMove(Keys.UP);
		
		if (Gdx.input.isKeyJustPressed(Keys.DOWN))
			menus.get(menuName).cursorMove(Keys.DOWN);
		
		if (Gdx.input.isKeyJustPressed(Keys.LEFT))
			menus.get(menuName).cursorMove(Keys.LEFT);
		
		if (Gdx.input.isKeyJustPressed(Keys.RIGHT))
			menus.get(menuName).cursorMove(Keys.RIGHT);
	}
	
	@Override
	public void render () {
		// Обработка игры в зависимости от игрового вида
		switch (GameScreen.getScreen().getName()) 
		{
			case SPLASH_SCREEN:
				if (movie.isLoaded(MovieTypes.SPLASH))
				{
					// Если окончено проигрывание анимации
					if (movie.getMovie(MovieTypes.SPLASH).play(true)) {
						GameScreen.setScreen(ScreenNames.PREPORATIONS);
						
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
				
				if (movie.isLoaded(MovieTypes.MUSIC_DANCE))
				{
					movie.getMovie(MovieTypes.MUSIC_DANCE).setFlipX(
						movie.getMovie(MovieTypes.MUSIC_DANCE).getCurentFrame() % 2 == 0
					);
					
					movie.getMovie(MovieTypes.MUSIC_DANCE).play(false);
				}
				else
				{
					movie.load(MovieTypes.MUSIC_DANCE, 
						new Animation(
							0.4f,
							new TextureRegion(
								new Texture(Gdx.files.internal("graphics/atlas.png")), 768, 0, 128, 128
							),
							new TextureRegion(
								new Texture(Gdx.files.internal("graphics/atlas.png")), 768, 0, 128, 128
							)
						)
					);
					
					// Установка режима воспроизведения анимации
					movie.getMovie(MovieTypes.MUSIC_DANCE).animation.setPlayMode(PlayMode.LOOP);
					
					// Цвет фона, которым будет очищаться окно (бекграунд)
					movie.getMovie(MovieTypes.MUSIC_DANCE).setClearColor(0.1875f, 0.3125f, 0.5f);
					
					// Установка позиции проигрыания
					movie.getMovie(MovieTypes.MUSIC_DANCE).setXY(100,200);
				}
				
				// Слушатель нажатия клавиш меню
				this.handleMenu("music_select");
				
				// Отрисовка меню
				menus.get("music_select").draw();
				
				for (int i = 0; i < menus.get("music_select").getItems().size(); i++) {
					GraphUtils.dottedVertLine(
							menus.get("music_select").getItem(i).getX(),
							menus.get("music_select").getItem(i).getY() - 40,
							menus.get("music_select").getItem(i).getId() != 4 ? 570 : 225, 
							3, 
							3
					);
				}
				
				// Заголовок окна
				this.drawTitle();

				// Отрисовка курсора
				this.drawMenuCursor("music_select");
				
				if (Gdx.input.isKeyJustPressed(Keys.ENTER) || !Sounds.isLoaded("bg")) {
					
					if (menus.get("music_select").getActive().getId() != 4) 
					{
					
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
					}
					else 
					{
						// Сбрасываем меню на первый пункт
						menus.get("music_select").reset();
						
						// Выгружаем прослушиваемый трек
						Sounds.unload("bg");
						
						// Переключаемся на главное меню
						GameScreen.setScreen(ScreenNames.PREPORATIONS);
					}
				}
			break;
			
			case PREPORATIONS:
				
				if (field != null) {
					field.dispose();
					field = null;
				}
				
				// 
				if (!Sounds.isLoaded("main_menu")) {
					Sounds.load("main_menu", "sound/bg/mainmenu.ogg");
					Sounds.play("main_menu");
					Sounds.loop("main_menu", true);
					Sounds.volume("main_menu", 0.5f);
				}
				
				// Капитан команды
				if (movie.isLoaded(MovieTypes.TEACH_TEACHER))
					movie.getMovie(MovieTypes.TEACH_TEACHER).play(false);
				else {
					movie.load(MovieTypes.TEACH_TEACHER, "graphics/body.png", 8, 8);
					
					// Установка коеффициента увеличения кадра
					movie.getMovie(MovieTypes.TEACH_TEACHER).setScaleXY(3);
					
					// Цвет фона, которым будет очищаться окно (бекграунд)
					movie.getMovie(MovieTypes.TEACH_TEACHER).setClearColor(0.1875f, 0.3125f, 0.5f);
					
					movie.getMovie(MovieTypes.TEACH_TEACHER).setFlipX(true);
					
					// Установка позиции проигрыания
					movie.getMovie(MovieTypes.TEACH_TEACHER).setXY(500,400);
				}
				
				// Полевой игрок
				if (movie.isLoaded(MovieTypes.TEACH_PUPIL))
					movie.getMovie(MovieTypes.TEACH_PUPIL).play(false);
				else {
					movie.load(MovieTypes.TEACH_PUPIL, "graphics/states.png", 8, 8);
					
					// Установка коеффициента увеличения кадра
					movie.getMovie(MovieTypes.TEACH_PUPIL).setScaleXY(3);
					
					// Установка позиции проигрыания
					movie.getMovie(MovieTypes.TEACH_PUPIL).setXY(370,390);
				}
				
				// Доска
				if (movie.isLoaded(MovieTypes.TEACH_BLACKBOARD)) 
					movie.getMovie(MovieTypes.TEACH_BLACKBOARD).play(true);
				else {
					movie.load(MovieTypes.TEACH_BLACKBOARD, "graphics/atlas.png", 512, 0, 240, 204, 999.0f);
					
					movie.getMovie(MovieTypes.TEACH_BLACKBOARD).setXY(650,400);
				}
				
				// Отрисовка заголовка окна
				this.drawTitle();
				
				// Слушатель нажатия клавиш меню
				this.handleMenu("main_menu");
				
				// Отрисовка меню
				menus.get("main_menu").draw();
				
				// Отрисовка указателя меню
				this.drawMenuCursor("main_menu");
				
				// Белая рамка
				Gdx.gl20.glLineWidth(8);
				shapeRenderer.begin(ShapeType.Line);
				shapeRenderer.rect(50, 50, Gdx.graphics.getWidth() - 100, 250);
				shapeRenderer.end();
				
				// Переход в соответствующий раздел игры в зависимости от выбранного пункта меню
				if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
					
					switch (menus.get("main_menu").getActive().getId()) {
						
						// Tactics
						case 0:
						break;
						
						// Players
						case 1:
						break;
						
						// Players info
						case 2:
						break;
						
						// Start!
						case 3:
							Sounds.unload("main_menu");
							movie.unload(MovieTypes.TEACH_TEACHER);
							movie.unload(MovieTypes.TEACH_PUPIL);
							
							// Переключение игрового экрана
							GameScreen.setScreen(ScreenNames.GAME);
						break;
						
						// Weather
						case 4:
						break;
						
						// Teams 
						case 5:
						break;
							
						// Music
						case 6:
							Sounds.unload("main_menu");
							movie.unload(MovieTypes.TEACH_TEACHER);
							movie.unload(MovieTypes.TEACH_PUPIL);
							
							// Переключение игрового экрана
							GameScreen.setScreen(ScreenNames.MUSIC_SELECT);
						break;
						
						
						// Players info
						case 7:
							Gdx.app.exit();
						break;
					}
				}
			break;
		
			case GAME:
				Gdx.gl.glClearColor(0, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				
				// Проверка иницилизировано ли игровое поле
				if (field == null) 
				{
					field = new Field();
					field.LoadMap("levels/level01.tmx"); 
					
					// Привязываем слушатель ввода
					Gdx.input.setInputProcessor(field);
				}
				else 
				{
					field.camera.update();
					field.fieldMapRenderer.setView(field.camera);
					field.fieldMapRenderer.render();
					
					// Обработка входящей информации от игрока и изменение поведения героев
					field.act();
						
					// Обработчик процесса игры
					field.processGame();
					
					// Отрисовка сцены и всех добавленных в нее актеров
					field.draw();
					
					if (field.gameState == GameStates.PAUSE) {
						GraphUtils.blackCurtain(
							field.camera.position.x - Gdx.graphics.getWidth() / 2, 
							field.camera.position.y - Gdx.graphics.getHeight() / 2, 
							Gdx.graphics.getWidth(),
							Gdx.graphics.getHeight(),
							0.9f
						);
						
						GraphUtils.drawText(
							0,
							GraphUtils.screenCenterY() + 100, 
							"GIVE UP?          ",
							true
						);
						
						this.handleMenu("give_up");
						
						// Отрисовка меню
						menus.get("give_up").draw();
					}
				}
			break;
			
			default:
			break;
		}
	}
}