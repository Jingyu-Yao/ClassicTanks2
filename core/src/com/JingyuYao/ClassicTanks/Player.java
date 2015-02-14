package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import jdk.nashorn.internal.objects.Global;

/**
 * Created by Jingyu_Yao on 1/16/2015.
 */
public class Player extends Tank {

    private final float FREEZE_DURATION = 10f;

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
     * Change tank type while maintaining current HP
     * @param t
     */
    @Override
    public void setTankType(TankType t) {
        int oldHP = getHp();
        super.setTankType(t);
        setHp(oldHP);
    }

    @Override
    protected void handleBuff(Buff buff){
        Array<Actor> actors = getStage().getActors();
        switch(buff.getBuffType()){
            case STAR:
                System.out.println("Ate STAR");
                advanceType();
                break;
            case FREEZE:
                System.out.println("Ate FREEZE");
                //Remove starting at last index so index won't get messed up
                for(int i = 0; i < actors.size; i++){
                    GameObj obj = (GameObj) actors.get(i);
                    if(obj.getGameObjType() == GameObjType.ENEMY){
                        ((Enemy)obj).freeze(FREEZE_DURATION);
                    }
                }
                break;
            case BOOM:
                System.out.println("Ate BOOM");
                // KILL ALL ENEMIES!
                //Remove starting at last index so index won't get messed up
                for(int i = actors.size - 1; i > 0; i--){
                    GameObj obj = (GameObj) actors.get(i);
                    if(obj.getGameObjType() == GameObjType.ENEMY){
                        getLevel().removeObject(obj);
                        System.out.println("killed");
                    }
                }
                break;
            case LIFE:
                System.out.println("Ate LIFE");
                setHp(getHp() + 1);
                break;
        }
        getLevel().removeObject(buff);
    }

    private void advanceType(){
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
    }

    @Override
    protected void postFiring(){
        getLevel().shoot.play();
    }

    @Override
    public void act(float deltaTime){
        super.act(deltaTime);
        moveCamera();
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
