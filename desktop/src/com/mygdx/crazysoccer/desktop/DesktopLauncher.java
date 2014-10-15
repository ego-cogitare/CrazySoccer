package com.mygdx.crazysoccer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.crazysoccer.CrazySoccer;
import com.mygdx.crazysoccer.Vars;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = Vars.WINDOW_WIDTH;
		config.height = Vars.WINDOW_HEIGHT;
		config.title = "Goal III rebirth";
//		config.fullscreen = true;
		new LwjglApplication(new CrazySoccer(), config);
	}
}
