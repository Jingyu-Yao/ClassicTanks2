package com.JingyuYao.ClassicTanks.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.JingyuYao.ClassicTanks.ClassicTanks;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(ClassicTanks.SCREEN_SIZE, ClassicTanks.SCREEN_SIZE);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new ClassicTanks();
        }
}