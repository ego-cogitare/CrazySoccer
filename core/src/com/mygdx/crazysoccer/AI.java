package com.mygdx.crazysoccer;

import java.util.ArrayList;

import com.mygdx.crazysoccer.Actions.Controls;
import com.mygdx.crazysoccer.Player.States;

public class AI {
	
	// Ссылка на игровой мир, где происходят все действия
	private Field field;
	
	// Список игроков, за которых будет играть ИИ
	private ArrayList<Integer> PLAY_FOR = new ArrayList<Integer>(); 
	
	// Время последнего принятия решения ИИ
	private long LAST_PLAY_TIME = 0; 
	
	// Продолжительность нажатия кнопки ИИ по умолчанию
	private int DEF_PRESS_DURATION = 200;
	
	// Добавление игрока, за которого будет играть ИИ
	public void addPlayer(int playerId) {
		PLAY_FOR.add(playerId);
	}
	
	// Получение ссылки на игровой мир
	public void attachField(Field field) {
		this.field = field;
	}
	
	private class AIButton {
		
		// Идентификатор игрока, для которого адресовано действие
		private int PLAYER_ID;
		
		// Продолжительность нажатия кнопки
		private long DURATION;
		
		// Время, начиная с которого кнопка нажата
		private long START;
		
		// Кнопка, которая нажата
		private Controls CONTROL;
		
		// Отослана ли команда игроку
		private boolean SENT = false;
		
		public AIButton(int playerId, long start, long duration, Controls control) {
			
			this.PLAYER_ID = playerId;
			this.START = System.nanoTime() + start;
			this.DURATION = duration;
			this.CONTROL = control;
		}
		
		@Override
		public String toString() {
			return "["+this.PLAYER_ID+", "+this.START+", "+this.DURATION+", "+this.CONTROL.toString()+"]";
		}
	}
	
	private AIButton aiButton;
	
	// Очередь действий, которые ИИ решил применить к игроку
	public ArrayList<ArrayList<AIButton>> actionsStack = new ArrayList<ArrayList<AIButton>>(); 
	
	public AI() {
		
		for (int i = 0; i < 10; i++) {
			actionsStack.add(i, new ArrayList<AIButton>());
		}
	}
	
	private long curTime() {
		return System.nanoTime();
	}
	
	private ArrayList<AIButton> getActionQueue(int playerId) {
		ArrayList<AIButton> result = new ArrayList<AIButton>();
		for (int i = 0; i < this.actionsStack.get(playerId).size(); i++) {
			if (this.actionsStack.get(playerId).get(i).PLAYER_ID == playerId) {
				result.add(this.actionsStack.get(playerId).get(i));
			}
		}
		return result;
	}
	
	private String queueToString(int playerId) {
		String result = "[";
		for (int i = 0; i < this.getActionQueue(playerId).size(); i++) {
			result += this.getActionQueue(playerId).get(i).toString() + ", ";
		}
		return result + "]";
	}
	
	/**
	 * Выполнение действий в порядке поступления в очередь
	 */
	private void fetchFirstAction(int playerId) {
		for (int i = 0; i < this.getActionQueue(playerId).size(); i++) {
			// Посылка команды от ИИ к игроку, если она еще не отослана
			if (this.getActionQueue(playerId).get(i).SENT == false && this.curTime() >= this.getActionQueue(playerId).get(i).START) {
				
				// Отсылаем команду
				this.sendCommandToPlayer(playerId, this.getActionQueue(playerId).get(i).CONTROL);
				
				// Помечаем, что команда отослана
				this.getActionQueue(playerId).get(i).SENT = true;
			}
		}
	}
	
	/**
	 * Отключение команд срок действия которых истек, которые посылает ИИ игроку
	 * @param playerId
	 * @param command
	 */
	private void disableOldActions(int playerId) {
		for (int i = 0; i < this.getActionQueue(playerId).size(); i++) {
			if (this.getActionQueue(playerId).get(i).START + this.getActionQueue(playerId).get(i).DURATION < this.curTime()) {
				
				// Отключаем команду у игрока
				this.removeCommandFromPlayer(playerId, this.getActionQueue(playerId).get(i).CONTROL);
				
				// Удаляем команду из стека
				this.actionsStack.get(playerId).remove(i); 
			}
		}
	}
	
	private void aiCommand(int playerId, long timeOffset, long pressDuration, Controls button) {
		if (this.actionsStack.get(playerId).size() < 4) {
			this.actionsStack.get(playerId).add(
				new AIButton(playerId, timeOffset * 1000000, pressDuration * 1000000, button)
			);
		}
	}
	
	private void sendCommandToPlayer(int playerId, Controls command) {
		field.actions.add(command, playerId);
	}
	
	private void removeCommandFromPlayer(int playerId, Controls command) {
		field.actions.remove(command, playerId);
	}
	
	private Player getPlayer(int playerId) {
		return field.players[playerId];
	}
	
	// Игра ИИ
	public void play() {
		
		if (System.nanoTime() - LAST_PLAY_TIME < 100000000L) {
			
			for (int i = 0; i < PLAY_FOR.size(); i++) {
				
				int playerId = PLAY_FOR.get(i);
			
				fetchFirstAction(playerId);
				disableOldActions(playerId);
			}
			
			//field.actions.debug(playerId);
		}
		else {
			LAST_PLAY_TIME = System.nanoTime();
			
			for (int i = 0; i < PLAY_FOR.size(); i++) {
				
				int playerId = PLAY_FOR.get(i);
			
				if (getPlayer(playerId).catchBall()) {
					
					aiCommand(playerId,   0, this.DEF_PRESS_DURATION, Controls.ACTION3);
					aiCommand(playerId, 200, this.DEF_PRESS_DURATION, Controls.RIGHT);
					aiCommand(playerId, 200, this.DEF_PRESS_DURATION, Controls.ACTION1);
					
//					if (!getPlayer(playerId).rightPressed()) 
//						aiCommand(playerId,    0, 500, Controls.RIGHT);
//					
//					if (!getPlayer(playerId).downPressed())
//						aiCommand(playerId,  1000, 500, Controls.DOWN);
//					
//					if (!getPlayer(playerId).leftPressed())
//						aiCommand(playerId, 2000, 500, Controls.LEFT);
//					
//					if (!getPlayer(playerId).upPressed())
//						aiCommand(playerId, 3000, 500, Controls.UP);
					
					System.out.println(playerId + " : " + this.queueToString(playerId));
				}
				else {
				
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
				
			}
			
		}
	}
}
