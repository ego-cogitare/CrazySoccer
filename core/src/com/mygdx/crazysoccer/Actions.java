package com.mygdx.crazysoccer;

import java.util.HashMap;
import java.util.Map;

// Допустимые действия для обработки
public class Actions {
	
	// Перечень допустимых действий
	public static enum Action {
		UP_1, DOWN_1, LEFT_1, RIGHT_1, ACTION1_1, ACTION2_1, ACTION3_1,
		UP_2, DOWN_2, LEFT_2, RIGHT_2, ACTION1_2, ACTION2_2, ACTION3_2
	}
	
	public static enum Controls {
		UP, DOWN, LEFT, RIGHT, ACTION1, ACTION2, ACTION3
	}
	
	public static class ActionDescription {
		public boolean pressed;
		public boolean doublePressed;
	}
	
	private Map<Action, Long> actionTime = new HashMap<Action, Long>();
	public Map<Action, Boolean> actions = new HashMap<Action, Boolean>();
	public Map<Action, Boolean> doublePressed = new HashMap<Action, Boolean>();
	public ActionDescription ad;
	
	public Actions() {
		ad = new ActionDescription();
		clear();
		//System.out.println("Actions class initialized...");
	}
	
	public void add(Action action) {
		long timeAction = System.nanoTime();
		
		// Время с момента последнего вызова этого действия
		long deltaTime = Math.round((timeAction - actionTime.get(action)) / 1000000);
		
		if (deltaTime <= 300 && actionTime.get(action) > 0) {
			doublePressed.put(action, true);
		} else {
			doublePressed.put(action, false);
		}
		
		actions.put(action, true);
		actionTime.put(action, timeAction);
		//debug();
	}
	
	public ActionDescription get(Action action) {
		ad.pressed = actions.get(action);
		ad.doublePressed = doublePressed.get(action);
		
		return ad;
	}
	
	public void remove(Action action) {
		
		actions.put(action, false);
	}
	
	// Отключение действия
	public void disableAction(Controls control, int playerId) {
		
		switch (playerId) {
			case 1:
				switch (control) {
					case UP: 
						actions.put(Action.UP_1, false);
					break;
					
					case DOWN: 
						actions.put(Action.DOWN_1, false);
					break;
					
					case LEFT: 
						actions.put(Action.LEFT_1, false);
					break;
					
					case RIGHT: 
						actions.put(Action.RIGHT_1, false);
					break;
					
					case ACTION1: 
						actions.put(Action.ACTION1_1, false);
					break;
					
					case ACTION2: 
						actions.put(Action.ACTION2_1, false);
					break;
					
					case ACTION3: 
						actions.put(Action.ACTION3_1, false);
					break;
				}
			break;
			
			case 2:
				switch (control) {
					case UP: 
						actions.put(Action.UP_2, false);
					break;
					
					case DOWN: 
						actions.put(Action.DOWN_2, false);
					break;
					
					case LEFT: 
						actions.put(Action.LEFT_2, false);
					break;
					
					case RIGHT: 
						actions.put(Action.RIGHT_2, false);
					break;
					
					case ACTION1: 
						actions.put(Action.ACTION1_2, false);
					break;
					
					case ACTION2: 
						actions.put(Action.ACTION2_2, false);
					break;
					
					case ACTION3: 
						actions.put(Action.ACTION3_2, false);
					break;
				}
			break;
		}
	}
	
	public ActionDescription getActionStateFor(Controls control, int playerId) {
		
		ActionDescription result = this.ad; 
		
		switch (playerId) {
			case 1:
				switch (control) {
					case UP: 
						result = this.get(Action.UP_1);
					break;
					
					case DOWN: 
						result = this.get(Action.DOWN_1);
					break;
					
					case LEFT: 
						result = this.get(Action.LEFT_1);
					break;
					
					case RIGHT: 
						result = this.get(Action.RIGHT_1);
					break;
					
					case ACTION1: 
						result = this.get(Action.ACTION1_1);
					break;
					
					case ACTION2: 
						result = this.get(Action.ACTION2_1);
					break;
					
					case ACTION3: 
						result = this.get(Action.ACTION3_1);
					break;
				}
			break;
			
			case 2:
				switch (control) {
					case UP: 
						result = this.get(Action.UP_2);
					break;
					
					case DOWN: 
						result = this.get(Action.DOWN_2);
					break;
					
					case LEFT: 
						result = this.get(Action.LEFT_2);
					break;
					
					case RIGHT: 
						result = this.get(Action.RIGHT_2);
					break;
					
					case ACTION1: 
						result = this.get(Action.ACTION1_2);
					break;
					
					case ACTION2: 
						result = this.get(Action.ACTION2_2);
					break;
					
					case ACTION3: 
						result = this.get(Action.ACTION3_2);
					break;
				}
			break;
		}
		
		return result;
	}
	
	public void clear() {
		for (int i = 0; i < Action.values().length; i++) {
			actions.put(Action.values()[i], false);
			actionTime.put(Action.values()[i], 0L);
			doublePressed.put(Action.values()[i], false);
		}
	}
	
	public void debug() {
		for (int i = 0; i < Action.values().length; i++) {
			if (actions.get(Action.values()[i])) {
				System.out.println("Action: " + Action.values()[i] + "\nState: " + actions.get(Action.values()[i]) + "\nDouble pressed: " + doublePressed.get(Action.values()[i])+"\n");
			}
		}
	}
}