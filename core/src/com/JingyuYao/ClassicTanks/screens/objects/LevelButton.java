package com.JingyuYao.ClassicTanks.screens.objects;

import com.JingyuYao.ClassicTanks.screens.LevelSelectionScreen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Created by Jingyu_Yao on 1/30/2015.
 * TODO: Nine-patch
 */
public class LevelButton extends TextButton {

    public LevelButton(final LevelSelectionScreen levelSelectionScreen, final int levelNumber, TextButtonStyle style) {
        super("Level " + levelNumber + " ", style);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                levelSelectionScreen.startLevel(levelNumber);
                return true;
            }
        });
    }
}
