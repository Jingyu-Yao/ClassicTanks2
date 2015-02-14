package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

import com.JingyuYao.ClassicTanks.GameObj.Direction;
import com.JingyuYao.ClassicTanks.Tank.TankType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * Created by Jingyu on 1/22/2015.
 */
public class GameScreenKeyboardListener extends InputListener {

    private final Player player;

    public GameScreenKeyboardListener(Player player) {
        this.player = player;
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        switch (keycode) {
            case Keys.LEFT:
                player.moveTowards(Direction.LEFT);
                return true;
            case Keys.RIGHT:
                player.moveTowards(Direction.RIGHT);
                return true;
            case Keys.UP:
                player.moveTowards(Direction.UP);
                return true;
            case Keys.DOWN:
                player.moveTowards(Direction.DOWN);
                return true;
            case Keys.SPACE:
                player.startShooting();
                return true;
            case Keys.S:
                player.setHp(5);
                return true;
            /*
            // for testing
            case Keys.B:
                player.setTankType(TankType.BARRAGE);
                return true;
            case Keys.A:
                player.setTankType(TankType.ARMORED);
                return true;
            case Keys.F:
                player.setTankType(TankType.FAST);
                return true;
            case Keys.D:
                player.setTankType(TankType.DUAL);
                return true;
            case Keys.N:
                player.setTankType(TankType.NORMAL);
                return true;
            case Keys.S:
                player.setTankType(TankType.SUPER);
                return true;
                */
            case Keys.P:
                System.out.println(player.toString());
                return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(InputEvent event, int keycode) {
        switch (keycode) {
            case Keys.LEFT:
                if (player.getMoveTowards() == Direction.LEFT) {
                    player.moveTowards(Direction.NONE);
                }
                return true;
            case Keys.RIGHT:
                if (player.getMoveTowards() == Direction.RIGHT) {
                    player.moveTowards(Direction.NONE);
                }
                return true;
            case Keys.UP:
                if (player.getMoveTowards() == Direction.UP) {
                    player.moveTowards(Direction.NONE);
                }
                return true;
            case Keys.DOWN:
                if (player.getMoveTowards() == Direction.DOWN) {
                    player.moveTowards(Direction.NONE);
                }
                return true;
            case Keys.SPACE:
                player.stopShooting();
                return true;
        }
        return false;
    }

}
