package com.fandangle.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fandangle.Constants;
import com.fandangle.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int) (Constants.VIEWPORT_WIDTH);
		config.height = (int) (Constants.VIEWPORT_HEIGHT);
		config.fullscreen = true;
		config.samples = 4;
		config.title = "Fandangle";

		new LwjglApplication(new Main(), config);
	}
}
