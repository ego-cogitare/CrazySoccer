package com.mygdx.crazysoccer;

import java.util.ArrayList;

import com.mygdx.crazysoccer.Actions.Controls;
import com.mygdx.crazysoccer.Player.States;

public class AI {
	
	// Ссылка на игровой мир, где происходят все действия
	private Field field;
	
	// Список игроков, за которых будет играть ИИ
	private ArrayList<Integer> PLAY_FOR = new ArrayList<Integer>(); 
	
	private long LAST_PLAY_TIME = 0; 
	
	// Добавление игрока, за которого будет играть ИИ
	public void addPlayer(int playerId) {
		PLAY_FOR.add(playerId);
	}
	
	// Получение ссылки на игровой мир
	public void attachField(Field field) {
		this.field = field;
	}
	
	// Очередь действий, которые ИИ решил применить к игроку
	public ArrayList<ArrayList<Controls>> actionsStack = new ArrayList<ArrayList<Controls>>(); 
	
	public AI() {
		
		for (int i = 0; i < 10; i++) {
			actionsStack.add(i, new ArrayList<Controls>());
		}
	}
	
	// Игра ИИ
	public void play() {
		
		if (System.nanoTime() - LAST_PLAY_TIME < 100000000L) {
			return;
		}
		else {
			LAST_PLAY_TIME = System.nanoTime();
			
			for (int i = 0; i < PLAY_FOR.size(); i++) {
				
				int playerId = PLAY_FOR.get(i);
			
				// Действия ИИ зависят от того владеет ли игрок мячом
				if (!field.players[playerId].catchBall()) {
					
					// Игрок стремится подойти к мячу
					if (field.ball.getAbsX() - field.players[playerId].getAbsX() > 20 && field.players[playerId].rightPressed() == false) {
						actionsStack.get(playerId).add(Controls.RIGHT);
					}
					else if (field.ball.getAbsX() - field.players[playerId].getAbsX() < -20 && field.players[playerId].leftPressed() == false) {
						actionsStack.get(playerId).add(Controls.LEFT);
					}
					else {
						field.actions.remove(Controls.LEFT, playerId);
						field.actions.remove(Controls.RIGHT, playerId);
					}
					
					if (field.ball.getAbsY() - field.players[playerId].getAbsY() > 20 && field.players[playerId].upPressed() == false) {
						actionsStack.get(playerId).add(Controls.UP);
					}
					else if (field.ball.getAbsY() - field.players[playerId].getAbsY() < -20 && field.players[playerId].downPressed() == false) {
						actionsStack.get(playerId).add(Controls.DOWN);
					}
					else {
						field.actions.remove(Controls.UP, playerId);
						field.actions.remove(Controls.DOWN, playerId);
					}
				}
				else {
					
					actionsStack.get(playerId).add(Controls.ACTION1);
				}
			
			}
			
			for (int i = 0; i < actionsStack.size(); i++) {
				for (int j = 0; j < actionsStack.get(i).size(); j++) {
					
					field.actions.add(actionsStack.get(i).get(j), i);
					actionsStack.get(i).remove(0);
				}
			}
		}
	}
	
	
	
	// Отображение ID игроков, за которых играет ИИ
	public String getPlayFor() {
		return this.PLAY_FOR.toString();
	}
}
