package com.mygdx.crazysoccer;

import java.util.HashMap;

public class GameScreen {
	
	// Игровые экраны
	public static enum ScreenNames {
		SPLASH_SCREEN,			// Начальная заставка
		PREPORATIONS,			// Главное меню игры
		GAME,					// Игра
		TEAM_SELECT,			// Выбор команды
		MUSIC_SELECT, 			// Выбор музыкального сопровождения
		PLAYER_STAT,			// Просмотр информации об игроке
		WEATHER_FORECAST,		// Просмотр прогноза погоды 		
	}
	
	public static class GameScreens {
		private String screenTitle;
		
		public GameScreens(String name) {
			this.screenTitle = name;
		}
		
		public String getTitle() {
			return screenTitle;
		}
		
		public ScreenNames getName() {
			return curentScreen;
		}
	}
	
	public static HashMap<ScreenNames,GameScreens> gameScreen = new HashMap<ScreenNames,GameScreens>();
	
	// Текущий игровой экран
	public static ScreenNames curentScreen;
	
	public static void init() {
		curentScreen = ScreenNames.SPLASH_SCREEN;
		
		gameScreen.put(ScreenNames.SPLASH_SCREEN, new GameScreens(""));
		gameScreen.put(ScreenNames.PREPORATIONS, new GameScreens("PREPORATIONS"));
		gameScreen.put(ScreenNames.GAME, new GameScreens(""));
		gameScreen.put(ScreenNames.TEAM_SELECT, new GameScreens("TEAM SELECT"));
		gameScreen.put(ScreenNames.MUSIC_SELECT, new GameScreens("MUSIC SELECT"));
		gameScreen.put(ScreenNames.PLAYER_STAT, new GameScreens("PLAYER INFORMATION"));
		gameScreen.put(ScreenNames.WEATHER_FORECAST, new GameScreens("WEATHER FORECAST"));
	}
	
	public static void setScreen(ScreenNames screen) {
		curentScreen = screen;
	}
	
	public static GameScreens getScreen() {
		return gameScreen.get(curentScreen);
	}
}