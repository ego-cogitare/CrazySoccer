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
		
		if (System.nanoTime() - LAST_PLAY_TIME < 200000000L) {
			return;
		}
		else {
			LAST_PLAY_TIME = System.nanoTime();
			
			for (int i = 0; i < PLAY_FOR.size(); i++) {
				
				int playerId = PLAY_FOR.get(i);
			
				// Действия ИИ зависят от того владеет ли игрок мячом
				if (!field.players[playerId].catchBall()) {
					
					// Игрок стремится подойти к мячу
					if (field.ball.getAbsX() - field.players[playerId].getAbsX() > 50 && field.players[playerId].rightPressed() == false) {
						field.actions.remove(Controls.LEFT, playerId);
						
						if (field.players[playerId].curentState() != States.RUN) {
							field.actions.add(Controls.RIGHT, playerId);
							//field.actions.add(Controls.RIGHT, playerId);
						}
					}
					else if (field.ball.getAbsX() - field.players[playerId].getAbsX() < -50 && field.players[playerId].leftPressed() == false) {
						field.actions.remove(Controls.RIGHT, playerId);
						
						if (field.players[playerId].curentState() != States.RUN) {
							field.actions.add(Controls.LEFT, playerId);
							//field.actions.add(Controls.LEFT, playerId);
						}
					}
					
					if (Math.abs(field.ball.getAbsX() - field.players[playerId].getAbsX()) <= 50) {
						field.actions.remove(Controls.LEFT, playerId);
						field.actions.remove(Controls.RIGHT, playerId);
						
						if (field.players[playerId].curentState() == States.RUN) {
							if (field.players[playerId].getVelocityX() < 0) 
								field.actions.add(Controls.RIGHT, playerId);
							else
								field.actions.add(Controls.LEFT, playerId);
						}
					}
					
					//System.out.println(field.ball.getAbsX() - field.players[playerId].getAbsX());
					//System.out.println(field.ball.getAbsY() - field.players[playerId].getAbsY());
					
					if (field.ball.getAbsY() - field.players[playerId].getAbsY() > 30 && field.players[playerId].upPressed() == false) {
						field.actions.add(Controls.UP, playerId);
						field.actions.remove(Controls.DOWN, playerId);
					}
					else if (field.ball.getAbsY() - field.players[playerId].getAbsY() < -30 && field.players[playerId].downPressed() == false) {
						field.actions.add(Controls.DOWN, playerId);
						field.actions.remove(Controls.UP, playerId);
					}
					
					if (Math.abs(field.ball.getAbsY() - field.players[playerId].getAbsY()) <= 30) {
						field.actions.remove(Controls.UP, playerId);
						field.actions.remove(Controls.DOWN, playerId);
					}
				}
				// Игрок владеет мячом
				else {
					
					// Команда удар
					this.sendCommand(playerId, Controls.ACTION1);
				}
			
				
				if (playerId == 9) {
					field.actions.debug(playerId);
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
	
	/**
	 * Отправка команды от ИИ к игроку
	 * @param playerId
	 * @param control
	 */
	private void sendCommand(int playerId, Controls control) {
		
		actionsStack.get(playerId).add(control);
	}
	
	
	/**
	 * Отображение ID игроков, за которых играет ИИ
	 * @return
	 */
	public String getPlayFor() {
		return this.PLAY_FOR.toString();
	}
}
