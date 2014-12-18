package com.mygdx.crazysoccer;

import java.util.ArrayList;

import com.mygdx.crazysoccer.Actions.Controls;
import com.mygdx.crazysoccer.Player.Directions;
import com.mygdx.crazysoccer.Player.States;

public class AI {
	
	// ID до ближайшего игрока-соперника
	private int nrstRival;
	
	//Расстояние от текущего игрока до мяча
	private float dstToBall;
	
	// Ссылка на игровой мир, где происходят все действия
	private Field field;
	
	// Список игроков, за которых будет играть ИИ
	private ArrayList<Integer> PLAY_FOR = new ArrayList<Integer>(); 
	
	// Список идентификаторов игроков из команды соперников
	private ArrayList<ArrayList<Integer>> opponents = new ArrayList<ArrayList<Integer>>();
	
	// Время последнего принятия решения ИИ
	private long LAST_PLAY_TIME = 0; 
	
	// Продолжительность нажатия кнопки ИИ по умолчанию
	private int DEF_PRESS_DURATION = 200;
	
	// Добавление игрока, за которого будет играть ИИ
	public void addPlayer(int playerId) {
		PLAY_FOR.add(playerId);
		
		setOpponentPlayers(playerId);
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
	
	// Очередь действий, которые ИИ решил применить к игроку
	public ArrayList<ArrayList<AIButton>> actionsStack = new ArrayList<ArrayList<AIButton>>(); 
	
	public AI() {
		
		for (int i = 0; i < 10; i++) {
			actionsStack.add(i, new ArrayList<AIButton>());
			opponents.add(i, new ArrayList<Integer>());
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
		if (this.actionsStack.get(playerId).size() < 5) {
			this.actionsStack.get(playerId).add(
				new AIButton(playerId, timeOffset * 1000000, pressDuration * 1000000, button)
			);
		}
	}
	
	private boolean aiCommandExists(int playerId, Controls control) {
		for (int i = 0; i < actionsStack.get(playerId).size(); i++) {
			if (actionsStack.get(playerId).get(i).CONTROL == control) {
				return true;
			}
		}
		return false;
	}
	
	private void sendCommandToPlayer(int playerId, Controls command) {
		field.actions.add(command, playerId);
	}
	
	private void removeCommandFromPlayer(int playerId, Controls command) {
		field.actions.remove(command, playerId);
	}
	
	private void removeCommandFromPlayer(int playerId, Controls command1, Controls command2) {
		
//		if (command1 != Controls.UP && command1 != Controls.DOWN && command2 != Controls.UP && command2 != Controls.DOWN ) {
		field.actions.remove(command1, playerId);
		field.actions.remove(command2, playerId);
//		}
	}
	
	private Player getPlayer(int playerId) {
		return field.players[playerId];
	}
	
	private void updateTicker() {
		LAST_PLAY_TIME = System.nanoTime();
	}
	
	/**
	 * Установка списка игроков из команды оппонентов
	 * @param playerId
	 */
	private void setOpponentPlayers(int playerId) {
		for (int i = 0; i < field.players.length; i++) {
			if (getPlayer(playerId).getTeamId() != field.players[i].getTeamId()) {
				this.opponents.get(playerId).add(field.players[i].getPlayerId());
			}
		}
	}
	
	/**
	 * Перемещение персонажа в нужную позицию
	 * @param followX
	 * @param followY
	 * @param playerId
	 */
	private boolean moveTo(float followX, float followY, int playerId, float dX, float dY) {
		
		boolean acieveX = false;
		boolean acieveY = false;
		
		if (followX - getPlayer(playerId).getAbsX() > dX) {
			removeCommandFromPlayer(playerId, Controls.LEFT);
			makeRun(playerId, Controls.RIGHT);
		}
		else if (followX - getPlayer(playerId).getAbsX() < -dX) {
			removeCommandFromPlayer(playerId, Controls.RIGHT);
			makeRun(playerId, Controls.LEFT);
		}
		
		if (followY - getPlayer(playerId).getAbsY() > dY && !getPlayer(playerId).upPressed()) {
			removeCommandFromPlayer(playerId, Controls.DOWN);
			sendCommandToPlayer(playerId, Controls.UP);
		}
		else if (followY - getPlayer(playerId).getAbsY() < -dY && !getPlayer(playerId).downPressed()) {
			removeCommandFromPlayer(playerId, Controls.UP);
			sendCommandToPlayer(playerId, Controls.DOWN);
		}
		
		// Проверка достиг ли игрок цели по оси OX
		if (MathUtils.distance(followX, 0, getPlayer(playerId).getAbsX(), 0) < dX) {
			if (getPlayer(playerId).getVelocityX() >= getPlayer(playerId).getRunSpeed()) {
				aiCommand(playerId, 0, this.DEF_PRESS_DURATION, Controls.LEFT);
			}
			else if (getPlayer(playerId).getVelocityX() <= -getPlayer(playerId).getRunSpeed()) {
				aiCommand(playerId, 0, this.DEF_PRESS_DURATION, Controls.RIGHT);
			}
			acieveX = true;
		}
		else {
			acieveX = false;
		}
		
		// Проверка достиг ли игрок цели по оси OY
		if (MathUtils.distance(followY, 0, getPlayer(playerId).getAbsY(), 0) < dY) {
			removeCommandFromPlayer(playerId, Controls.UP, Controls.DOWN);
			acieveY = true;
		}
		else {
			acieveY = false;
		}
		
		return acieveX & acieveY;
	}
	
	/**
	 * Полная остановка игрока
	 * @param playerId
	 */
	public void playerStop(int playerId) {
		
		removeCommandFromPlayer(playerId, Controls.LEFT, Controls.RIGHT);
		removeCommandFromPlayer(playerId, Controls.UP, Controls.DOWN);
		
		if (getPlayer(playerId).curentState() == States.RUN) {
			
			if (getPlayer(playerId).getVelocityX() > 0) {
				sendCommandToPlayer(playerId, Controls.LEFT);
			}
			else if (getPlayer(playerId).getVelocityX() < 0) {
				sendCommandToPlayer(playerId, Controls.RIGHT);
			}
		}
		
		if (getPlayer(playerId).Can(States.STAY)) {
			getPlayer(playerId).Do(States.STAY, true);
		}
	}

	/**
	 * Очистка очереди выполнения команд игрока
	 * @param playerId
	 */
	private void clearActionStack(int playerId) {
		actionsStack.get(playerId).clear();
	}
	
	/**
	 * Перечень возможных суперударов 
	 * @author ASRock960
	 */
	private static enum SuperKickTypes {
		HEAD,
		BACK,
		FOOT
	}
	
	/**
	 * Переводит игрока в состояние бега в указанном направлении
	 * @param playerId
	 * @param contorl
	 */
	private void makeRun(int playerId, Controls control) {
		// Если игрок бежит в противоположном направлении
		if (getPlayer(playerId).curentState() == States.RUN) {
			if (control == Controls.RIGHT && getPlayer(playerId).getVelocityX() < 0) {
				aiCommand(playerId, 0, this.DEF_PRESS_DURATION, Controls.RIGHT);
			}
			else if (control == Controls.LEFT && getPlayer(playerId).getVelocityX() > 0) {
				aiCommand(playerId, 0, this.DEF_PRESS_DURATION, Controls.LEFT);
			}
		}
		else {
			aiCommand(playerId, 100, this.DEF_PRESS_DURATION, control);
			aiCommand(playerId, 150, this.DEF_PRESS_DURATION, control);
		}
	}
	
	/**
	 * Выполнение суперудара 
	 */
	private void superKick(SuperKickTypes type, int playerId) {
		
		switch (type) {
		
			case HEAD:
				aiCommand(playerId,   0, this.DEF_PRESS_DURATION, Controls.ACTION3);
				aiCommand(playerId, 200, this.DEF_PRESS_DURATION, getPlayer(playerId).getDestinationGateId() == Gate.LEFT_GATES ? Controls.LEFT : Controls.RIGHT);
				aiCommand(playerId, 200, this.DEF_PRESS_DURATION, Controls.ACTION1);
			break;
			
			case FOOT:
				aiCommand(playerId,   0, this.DEF_PRESS_DURATION, Controls.ACTION3);
				aiCommand(playerId, 200, this.DEF_PRESS_DURATION, Controls.ACTION1);
			break;
			
			case BACK:
				aiCommand(playerId,   0, this.DEF_PRESS_DURATION, Controls.ACTION3);
				aiCommand(playerId, 200, this.DEF_PRESS_DURATION, getPlayer(playerId).getDestinationGateId() == Gate.LEFT_GATES ? Controls.RIGHT : Controls.LEFT);
				aiCommand(playerId, 200, this.DEF_PRESS_DURATION, Controls.ACTION1);
			break;
		}
	}
	
	// Игра ИИ
	public void play() {
		
		if (System.nanoTime() - LAST_PLAY_TIME < 120000000L) {
			
			for (int i = 0; i < PLAY_FOR.size(); i++) {
				
				int playerId = PLAY_FOR.get(i);
			
				fetchFirstAction(playerId);
				disableOldActions(playerId);
			}
		}
		else {
			this.updateTicker();
			
			
			// Проход по игрокам и принятие решения какое действие должен выполнять игрок
			for (int i = 0; i < PLAY_FOR.size(); i++) {
				
				// Получение идентификатора игрока
				int playerId = PLAY_FOR.get(i);
				
//				if (playerId == 9) {
//					System.out.println(queueToString(playerId));
//				}
				
				/**
				 * Поведение игрока зависит от того, кто контроллирует мяч.
				 * Если мяч контроллирует кто-то из команды этого игрока, то игрок должен либо пытаться вернуться на загимаемую позицию
				 * либо способствовать продвижению атаки. В противном случае игрок должен пытаться отобрать мяч, если мяч находится в 
				 * зоне ответственности игрока.
				 */
				
				// Куда следовать для отбора мяча
				float followX = -1;
				float followY = -1;
				float dX = 48;
				float dY = 16;
				
				if (getPlayer(playerId).ballManagedByOpponents()) 
				{
					followX = getPlayer(field.ball.managerByBlayer()).getAbsX();
					followY = getPlayer(field.ball.managerByBlayer()).getAbsY();
					dX = 64;
					dY = 16;
				}
				else if (!field.ball.isCatched()) 
				{
					// Если мяч находится на земле то игрок следует в позицию местонахождения мяча
					if (field.ball.getAbsH() < 10 || field.ball.absVelocity() < 0.5f) 
					{
						followX = field.ball.getAbsX();
						followY = field.ball.getAbsY();
						
						dX = 32;
						dY = 16;
					}
					// Иначе игрок следует в позицию где мяч приземлится
					else 
					{
						followX = field.ball.getLandingX();
						followY = field.ball.getLandingY();
						
						dX = 32;
						dY = 16;
					}
				}
				
				// ID ближайшего живого игрока соперника
				nrstRival = field.playerNearest(playerId, Player.RIVAL, true, false, false, false, false);
				
				// Расстояние от текущего игрока до мяча
				dstToBall = MathUtils.distance(getPlayer(playerId).getAbsX(), getPlayer(playerId).getAbsY(), field.ball.getAbsX(), field.ball.getAbsY());
				
				// Если мяч находится в зоне видимости AI
				if (dstToBall < 600) 
				{
					/****************************************************
					 * Если мяч ничейный или контроллируется оппонентом *
					 ****************************************************/
					if (followX != -1 && followY != -1) 
					{
						
						/****************************************************************** 
						 * Защита игроком самого себя, чтобы не давать себя ударить мячом *
						 ******************************************************************/
						if 
						(
							!field.ball.isCatched() &&
							
							!getPlayer(playerId).isDead() &&
							
							field.ball.getAbsH() < 150 &&
							
							getPlayer(playerId).isEnoughToKill(field.ball.impulse()) &&
							
							Math.abs(getPlayer(playerId).getAbsY() - field.ball.getAbsY()) < 50 &&
							
							Math.abs(getPlayer(playerId).getAbsX() - field.ball.getAbsX()) < 250 &&
							(
								(field.ball.getVelocityX() > 0 && getPlayer(playerId).getAbsX() > field.ball.getAbsX()) ||
								(field.ball.getVelocityX() < 0 && getPlayer(playerId).getAbsX() < field.ball.getAbsX())
							)
						) 
						{
							//aiCommand(playerId, 0, this.DEF_PRESS_DURATION, Controls.LEFT);
							aiCommand(playerId, 0, this.DEF_PRESS_DURATION, Controls.ACTION1);
						}
						
						
						/************************************************************************************************** 
						 * Ситуация когда нужно бить рыбкой:															  *
						 * 	1. когда игрок находится достаточно близко к мячу чтобы выполнить FISH_KICK;				  *
						 *  2. ближайший игрок к мячу это игрок соперника или игрок соперника находится достаточно близко *
						 **************************************************************************************************/
						if 
						(
							// Если мяч ничейный 	
							!field.ball.isCatched() &&
							
							field.ball.getAbsH() < 150 &&
							
							// и игрок который мог бы выполнить удар - живой
							!getPlayer(playerId).isDead() &&
							
							// и он находится напротив мяча
							Math.abs(getPlayer(playerId).getAbsY() - field.ball.getAbsY()) < 40 &&
							
							// и расстояние от него до мяча по оси OX меньше чем расстояние которое необходимо для удара "рыбкой"
							Math.abs(getPlayer(playerId).getAbsX() - field.ball.getAbsX()) < 230
						) 
						{
							// Случай, когда игрок находится возле мяча, чтобы выполнить удар по воротам / вынос мяча рыбкой
							if 
							(
								// Ближайший игрок найден	
								nrstRival != -1 &&
								
								// и расстояние к нему меньше 300
								MathUtils.distance(
									getPlayer(playerId).getAbsX(), 
									getPlayer(playerId).getAbsY(), 
									getPlayer(nrstRival).getAbsX(), 
									getPlayer(nrstRival).getAbsY()
								) < 300
							) 
							{
								// Выполнение добивания рыбкой
								if 
								(
									// и игрок повернут в сторону ворот соперника
									getPlayer(playerId).turnedToDestinationGates() &&
									
									// и расстояние до ворот небольшое
									getPlayer(playerId).distanceToDestGates() < 800
								)
								{
									// Выполняем удар "рыбкой"
									aiCommand(playerId, 0, this.DEF_PRESS_DURATION, Controls.ACTION1);
								}
								
								// Выполнение выноса мяча рыбкой
								else if 
								(
									// и игрок повернут в сторону ворот соперника
									getPlayer(playerId).turnedToDestinationGates() &&
									
									// и расстояние до ворот небольшое
									getPlayer(playerId).distanceToOwnGates() < 800
								)
								{
									// Выполняем удар "рыбкой"
									aiCommand(playerId, 0, this.DEF_PRESS_DURATION, Controls.ACTION1);
								}
							}
						}
						
						
//						System.out.println(followX+" "+followY);
//						System.out.println(field.ball.getLandingX()+" "+field.ball.getLandingY());
						
						/********************************************************************************************
						 *                                   Если игрок достиг мяча                                 *
						 ********************************************************************************************/ 
						if (moveTo(followX, followY, playerId, dX, dY)) 
						{
							// Если мяч контроллируется игроком-соперником, тогда пробуем отобрать мяч
							if (field.ball.isCatched()) 
							{
								// Если игрок добрался до мяча, то выполняется попытка отбора мяча
								if (Math.random() > 0.5f) {
									aiCommand(playerId, 0, this.DEF_PRESS_DURATION, Controls.ACTION2);
								}
								else {
									aiCommand(playerId, 0, this.DEF_PRESS_DURATION, Controls.ACTION1);
								}
							}
							else if (!field.ball.inField()) 
							{
								aiCommand(playerId, 0, this.DEF_PRESS_DURATION, Controls.ACTION2);
							}
						}
						// Пока игрок движется к ничейному мячу (или к мячу который контроллирует оппонентом)
						else 
						{
							/* Целесообразно выполнять удар в прыжке при следующих ситуациях:
							 *   1. Когда игрок доганяет игрока сзади (т.е. его скорость больше скорости убегающего)
							 */
							
							// Если мяч контроллируется игроком соперником
							if (field.ball.isCatched()) 
							{
								// ID игрока, который управляет мячом
								int managedBy = field.ball.managerByBlayer();
								
								if
								(
									managedBy >= 0 &&
									getPlayer(playerId).ballManagedByOpponents() &&
									Math.abs(getPlayer(playerId).getVelocityX()) > Math.abs(getPlayer(managedBy).getVelocityX()) &&
									MathUtils.distance(getPlayer(playerId).getAbsX(), getPlayer(playerId).getAbsY(), getPlayer(managedBy).getAbsX(), getPlayer(managedBy).getAbsY()) < 200 &&
									(
										(
											getPlayer(managedBy).getVelocityX() != 0 &&
											getPlayer(managedBy).getAbsH() == 0
										)
										||
										(
											getPlayer(managedBy).getAbsH() > 0
										)
									)
								)
								{
									// Игрок подпрыгивает
									aiCommand(playerId, 0, this.DEF_PRESS_DURATION, Controls.ACTION3);
							
									// и бъет ногами или корпусом
									if (Math.random() > 0.5f)  
										aiCommand(playerId, 100, this.DEF_PRESS_DURATION, Controls.ACTION1);
									else
										aiCommand(playerId, 100, this.DEF_PRESS_DURATION, Controls.ACTION2);
								}
							}
							// Игрок на пути к ничейному мячу
							else
							{
								
							}
						}
					}
					
					
					/***************************************************************************************
					 *  Мяч контроллируется игроком или игроком команды к которой принадлежит данный игрок *
					 ***************************************************************************************/
					else 
					{
						// Если мяч контроллируется игроком
						if (this.getPlayer(playerId).catchBall()) 
						{
							/**
							 * Анализ игровой ситуации игроком который владеет мячом 
							 */
							
							
							/**
							 * 1: Попытки уклонения от игроков соперника
							 * 	 1.1. Если в опасной зоне игрока 1 противник, то игрок пытается уклониться от него
							 *   1.2. Если в опасной зоне игрока количество противников более 1, то игрок делает атаку корпусом 
							 */
							float dstBtwPlayers = (nrstRival == -1) ? 9999 : getPlayer(playerId).getAbsX() - getPlayer(nrstRival).getAbsX();
							
							if 
							(
								// Ближайший соперник найден	
								nrstRival != -1 &&
								
								// и расстояние по OY между игроками меньше 70
								Math.abs(getPlayer(playerId).getAbsY() - getPlayer(nrstRival).getAbsY()) < 70 &&
								
								// и нету команды на выполнение
								!aiCommandExists(playerId, Controls.UP) && !aiCommandExists(playerId, Controls.DOWN)
								
								&&
								(
									getPlayer(playerId).direction == Directions.RIGHT &&
									(
										(
											dstBtwPlayers > -400 && dstBtwPlayers < 0
										) 
										|| 		
										(
											dstBtwPlayers < 100 && dstBtwPlayers > 0
										) 		
									)
									||
									getPlayer(playerId).direction == Directions.LEFT &&
									(
										(
											dstBtwPlayers < 400 && dstBtwPlayers > 0
										) 
										|| 		
										(
											dstBtwPlayers > -100 && dstBtwPlayers < 0
										) 		
									)
								)
							) 
							{
								// Если количество соперников рядом более одного то делаем удар корпусом
								if (field.playersNearThePoint(playerId, 400, 2, Player.RIVAL))
								{
									if (getPlayer(playerId).getAbsY() < getPlayer(nrstRival).getAbsY()) 
									{
										aiCommand(playerId,   0, this.DEF_PRESS_DURATION, Controls.UP);
										aiCommand(playerId, 100, this.DEF_PRESS_DURATION, Controls.UP);
									}
									else
									{
										aiCommand(playerId,   0, this.DEF_PRESS_DURATION, Controls.DOWN);
										aiCommand(playerId, 100, this.DEF_PRESS_DURATION, Controls.DOWN);
									}
								}
								// Иначе AI уклоняется от игрока-соперника 
								else
								{
									if (getPlayer(playerId).getAbsY() < getPlayer(nrstRival).getAbsY())
									{
										aiCommand(playerId, 0, 350, Controls.DOWN);
									}
									else
									{
										aiCommand(playerId, 0, 350, Controls.UP);
									}
								}
							}
							
							
							// 2: когда и кому отдать пас
							
							// 3: атака мячом с целью нейтрализовать противника
							
							
							/*********************************************************************************
							 *  Если уклоняясь игрок приблизился к ауту то отключаем его дальнейшее движение *
							 *  за пределы поля																 *
							 *********************************************************************************/
							if 
							(
								getPlayer(playerId).getAbsY() < field.fieldOffsetY + 80 || 
								getPlayer(playerId).getAbsY() > field.fieldOffsetY + field.fieldHeight - 80
							) 
							{
								removeCommandFromPlayer(playerId, Controls.UP, Controls.DOWN);
							}
							
							
							
							// Если доступен суперудар, то выполняем его, иначе движемся в сторону ворот, по которым нужно бить
							if (getPlayer(playerId).distanceToDestGates() < getPlayer(playerId).superKickMaxLength()) {
								
								// Произвольным образом выбираем тип суперудара, которым будет бить игрок
								int ind = MathUtils.random(0, SuperKickTypes.values().length - 1);
								
								// Выполнение суперудара
								superKick(SuperKickTypes.values()[ind], playerId);
							}
							else 
							{
								makeRun(
									playerId, 
									getPlayer(playerId).getDestinationGateId() == Gate.LEFT_GATES ? Controls.LEFT : Controls.RIGHT
								);
								
								//removeCommandFromPlayer(playerId, Controls.UP, Controls.DOWN);
							}
						}
						// Мяч контроллируется другим игроком команды к которой принадлежит игрок
						else 
						{
							// Игрок возвращается на свою позицию на поле
							if (moveTo(getPlayer(playerId).getHomeX(), getPlayer(playerId).getHomeY(), playerId, 100, 30)) {
								
								// Если игрок достиг своей позиции на поле, за которой он закреплен поворачиваем его в нужную сторону
								getPlayer(playerId).direction = getPlayer(playerId).getDestinationGateId() == Gate.LEFT_GATES ? Directions.LEFT : Directions.RIGHT;
							}
						}
					}
				}
				// AI возвращает игрока на свою позицию
				else
				{
					// Игрок возвращается на свою позицию на поле
					if (moveTo(getPlayer(playerId).getHomeX(), getPlayer(playerId).getHomeY(), playerId, 100, 30)) {
						
						// Если игрок достиг своей позиции на поле, за которой он закреплен поворачиваем его в нужную сторону
						getPlayer(playerId).direction = getPlayer(playerId).getDestinationGateId() == Gate.LEFT_GATES ? Directions.LEFT : Directions.RIGHT;
					}
				}
				
			}
			
		}
	}
}