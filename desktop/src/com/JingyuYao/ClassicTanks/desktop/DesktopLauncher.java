package com.JingyuYao.ClassicTanks.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.JingyuYao.ClassicTanks.ClassicTanks;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 640;
        config.height = 640;
		new LwjglApplication(new ClassicTanks(), config);
	}
}
