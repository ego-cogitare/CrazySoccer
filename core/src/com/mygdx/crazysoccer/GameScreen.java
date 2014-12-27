package com.mygdx.crazysoccer;

public class GameScreen {
	
	// Игровые экраны
	public static enum GameScreens {
		SPLASH_SCREEN,			// Начальная заставка
		PREPORATIONS,			// Главное меню игры
		GAME,					// Игра
		TEAM_SELECT,			// Выбор команды
		MUSIC_SELECT, 			// Выбор музыкального сопровождения
		PLAYER_STAT,			// Просмотр информации об игроке
		WEATHER_FORECAST,		// Просмотр прогноза погоды 		
	}
	
	// Текущий игровой экран
	public static GameScreens gameScreen = GameScreens.PREPORATIONS;
	
	public static void setScreen(GameScreens screen) {
		gameScreen = screen;
	}
	
	public static GameScreens getScreen() {
		return gameScreen;
	}
}
