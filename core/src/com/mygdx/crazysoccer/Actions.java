package com.mygdx.crazysoccer;

import java.util.HashMap;
import java.util.Map;

// Допустимые действия для обработки
public class Actions {
	
	public static enum Controls {
		UP, DOWN, LEFT, RIGHT, ACTION1, ACTION2, ACTION3
	}
	
	public static class ActionDescription {
		public boolean pressed;
		public boolean doublePressed;
		public boolean triplePressed;
	}
	
	// Количество игроков, для которых необходимо обрабатывать ввод действий
	private int PLAYERS_AMOUNT = 0;
	
	private Map<String, Long> actionTime = new HashMap<String, Long>();
	public Map<String, Boolean> actions = new HashMap<String, Boolean>();
	public Map<String, Boolean> doublePressed = new HashMap<String, Boolean>();
	public Map<String, Boolean> triplePressed = new HashMap<String, Boolean>();
	public ActionDescription ad;
	
	public Actions(int playersAmount) {
		this.PLAYERS_AMOUNT = playersAmount;
		ad = new ActionDescription();
		init();
	}
	
	private String playerAction(Controls control, int playerId) {
		return control.toString() + "_" + String.valueOf(playerId);
	}
	
	public void add(Controls control, int playerId) {
		long timeAction = System.nanoTime();
		
		// Получаем action для игрока
		String action = playerAction(control, playerId);
		
		// Время с момента последнего вызова этого действия
		long deltaTime = Math.round((timeAction - actionTime.get(action)) / 1000000);

		if (doublePressed.get(action) && deltaTime <= 200) {
			triplePressed.put(action, true);
		}		
		else {
			triplePressed.put(action, false);
		}
		
		if (deltaTime <= 300 && actionTime.get(action) > 0) {
			doublePressed.put(action, true);
		} else {
			doublePressed.put(action, false);
		}
		
		actions.put(action, true);
		actionTime.put(action, timeAction);
		
		//debug(9);
	}
	
	public void remove(Controls control, int playerId) {
		
		String action = playerAction(control, playerId);
		
		actions.put(action, false);
	}
	
	public ActionDescription get(String action) {
		ad.pressed = actions.get(action);
		ad.doublePressed = doublePressed.get(action);
		ad.triplePressed = triplePressed.get(action);
		return ad;
	}
	
	// Отключение действия
	public void disableAction(Controls control, int playerId) {
		switch (control) {
			case UP: 
				actions.put("UP_"+String.valueOf(playerId), false);
			break;
			
			case DOWN: 
				actions.put("DOWN_"+String.valueOf(playerId), false);
			break;
			
			case LEFT: 
				actions.put("LEFT_"+String.valueOf(playerId), false);
			break;
			
			case RIGHT: 
				actions.put("RIGHT_"+String.valueOf(playerId), false);
			break;
			
			case ACTION1: 
				actions.put("ACTION1_"+String.valueOf(playerId), false);
			break;
			
			case ACTION2: 
				actions.put("ACTION2_"+String.valueOf(playerId), false);
			break;
			
			case ACTION3: 
				actions.put("ACTION3_"+String.valueOf(playerId), false);
			break;
		}
	}
	
	public ActionDescription getActionStateFor(Controls control, int playerId) {
		
		ActionDescription result = this.ad;
		
		switch (control) {
			case UP: 
				result = this.get("UP_"+String.valueOf(playerId));
			break;
			
			case DOWN: 
				result = this.get("DOWN_"+String.valueOf(playerId));
			break;
			
			case LEFT: 
				result = this.get("LEFT_"+String.valueOf(playerId));
			break;
			
			case RIGHT: 
				result = this.get("RIGHT_"+String.valueOf(playerId));
			break;
			
			case ACTION1: 
				result = this.get("ACTION1_"+String.valueOf(playerId));
			break;
			
			case ACTION2: 
				result = this.get("ACTION2_"+String.valueOf(playerId));
			break;
			
			case ACTION3: 
				result = this.get("ACTION3_"+String.valueOf(playerId));
			break;
		}
		
		return result;
	}
	
	private void init() {
		for (int i = 0; i < Controls.values().length; i++) {
			
			for (int j = 0; j < this.PLAYERS_AMOUNT; j++) {
				
				actions.put(Controls.values()[i].toString() + "_" + String.valueOf(j), false);
				actionTime.put(Controls.values()[i].toString() + "_" + String.valueOf(j), 0L);
				doublePressed.put(Controls.values()[i].toString() + "_" + String.valueOf(j), false);
				triplePressed.put(Controls.values()[i].toString() + "_" + String.valueOf(j), false);
			}
		}
	}
	
	public void debug(int playerId) {
		for (int i = 0; i < Controls.values().length; i++) {
			System.out.println(
				"Action : " + Controls.values()[i].toString() + "\n"+
				"1      : " + (actions.get(Controls.values()[i].toString() + "_" + String.valueOf(playerId)) ? 1 : 0)  + 
				"\n2      : " + (doublePressed.get(Controls.values()[i].toString() + "_" + String.valueOf(playerId)) ? 1 : 0) + 
				"\n3      : " + (triplePressed.get(Controls.values()[i].toString() + "_" + String.valueOf(playerId)) ? 1 : 0) + "\n");
		}
	}
}