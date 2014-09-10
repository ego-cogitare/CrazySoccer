package com.mygdx.crazysoccer;

import java.util.HashMap;
import java.util.Map;

// Допустимые действия для обработки
public class Actions {
	
	// Перечень допустимых действий
	public static enum Action {
		UP, DOWN, LEFT, RIGHT, ACTION1, ACTION2, ACTION3
	}
	
	public static class ActionDescription {
		public boolean state;
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
	}
	
	public ActionDescription get(Action action) {
		ad.state = actions.get(action);
		ad.doublePressed = doublePressed.get(action);
		
		return ad;
	}
	
	public void remove(Action action) {
		actions.put(action, false);
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