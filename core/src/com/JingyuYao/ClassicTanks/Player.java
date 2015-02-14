package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jingyu_Yao on 1/16/2015.
 */
public class Player extends Tank {

    /*
    For player hp = life, not the hit points needed to kill the tank.
    Player tank always die in one hit.
     */

    public Player(Level level, float x, float y, TankType type, Direction direction) {
        super(level, x, y, type, direction);
        gameObjType = GameObjType.PLAYER;
        addListener(new GameScreenKeyboardListener(this));
    }

    /**
     * TODO: Bound distance changes with direction to allow larger view
     * Changes the x and y position of the camera to follow the player.
     * The movement allowance is set by {@code GameScreen.CAMERA_INNER_BOUND}
     */
    private void moveCamera() {
        float cameraX = getLevel().viewPort.getCamera().position.x, cameraY = getLevel().viewPort.getCamera().position.y;
        float playerX = getX(), playerY = getY();

        float dx = 0, dy = 0;
        if (cameraX - playerX > GameScreen.CAMERA_INNER_BOUND) {
            dx = playerX - cameraX + GameScreen.CAMERA_INNER_BOUND;
        } else if (playerX - cameraX > GameScreen.CAMERA_INNER_BOUND) {
            dx = playerX - cameraX - GameScreen.CAMERA_INNER_BOUND;
        }
        if (cameraY - playerY > GameScreen.CAMERA_INNER_BOUND) {
            dy = playerY - cameraY + GameScreen.CAMERA_INNER_BOUND;
        } else if (playerY - cameraY > GameScreen.CAMERA_INNER_BOUND) {
            dy = playerY - cameraY - GameScreen.CAMERA_INNER_BOUND;
        }
        if (dx != 0 || dy != 0) {
            getLevel().viewPort.getCamera().translate(dx,dy,0);
            getLevel().viewPort.getCamera().update();
        }
    }

    @Override
    protected void handleBuff(Buff buff){
        switch(buff.getBuffType()){
            case STAR:
                //Normal -> Fast -> Barrage -> Dual -> Super
                switch (getTankType()){
                    case NORMAL:
                        setTankType(TankType.FAST);
                        break;
                    case BARRAGE:
                        setTankType(TankType.DUAL);
                        break;
                    case DUAL:
                        setTankType(TankType.SUPER);
                        break;
                    case FAST:
                        setTankType(TankType.BARRAGE);
                        break;
                    case SUPER:
                        break;
                }
                break;
            case FREEZE:
                break;
            case BOOM:
                break;
            case ARMOR_UP:
                break;
        }
        getLevel().removeObject(buff);
    }

    @Override
    public void act(float deltaTime){
        super.act(deltaTime);
        moveCamera();
    }

    @Override
    public void setTankType(TankType t) {
        int oldHP = getHp();
        super.setTankType(t);
        setHp(oldHP);
    }

    @Override
    public void damage(){
        if(hp > 0){
            hp--;
        }
        if(hp == 0){
            getLevel().removeObject(this);
        }else{
            setTankType(TankType.NORMAL);
            setDirection(Direction.UP);
            Vector2 startPos = getLevel().getStartPosition();
            setPosition(startPos.x*Level.TILE_SIZE, startPos.y*Level.TILE_SIZE);
        }
    }
}
