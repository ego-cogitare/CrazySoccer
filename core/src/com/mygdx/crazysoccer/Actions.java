package com.mygdx.crazysoccer;

import java.util.HashMap;
import java.util.Map;

import com.mygdx.crazysoccer.Vars;

// Допустимые действия для обработки
public class Actions {
	
	public static class ActionDescription {
		public boolean state;
		public boolean doublePressed;
	}
	
	private Map<Vars.Action, Long> actionTime = new HashMap<Vars.Action, Long>();
	public Map<Vars.Action, Boolean> actions = new HashMap<Vars.Action, Boolean>();
	public Map<Vars.Action, Boolean> doublePressed = new HashMap<Vars.Action, Boolean>();
	public ActionDescription ad;
	
	public Actions() {
		ad = new ActionDescription();
		clear();
		//System.out.println("Actions class initialized...");
	}
	
	public void add(Vars.Action action) {
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
	
	public ActionDescription get(Vars.Action action) {
		ad.state = actions.get(action);
		ad.doublePressed = doublePressed.get(action);
		
		return ad;
	}
	
	public void remove(Vars.Action action) {
		actions.put(action, false);
	}
	
	public void clear() {
		for (int i = 0; i < Vars.Action.values().length; i++) {
			actions.put(Vars.Action.values()[i], false);
			actionTime.put(Vars.Action.values()[i], 0L);
			doublePressed.put(Vars.Action.values()[i], false);
		}
	}
	
	public void debug() {
		for (int i = 0; i < Vars.Action.values().length; i++) {
			System.out.println("Action: " + Vars.Action.values()[i] + "\nState: " + actions.get(Vars.Action.values()[i]) + "\nDouble pressed: " + doublePressed.get(Vars.Action.values()[i])+"\n");
		}
	}
}
