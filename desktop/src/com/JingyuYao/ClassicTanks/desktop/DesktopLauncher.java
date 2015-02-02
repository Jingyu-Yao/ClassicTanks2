package com.JingyuYao.ClassicTanks.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.JingyuYao.ClassicTanks.ClassicTanks;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = ClassicTanks.SCREEN_SIZE;
        config.height = ClassicTanks.SCREEN_SIZE;
		new LwjglApplication(new ClassicTanks(), config);
	}
}
