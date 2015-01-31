package com.JingyuYao.ClassicTanks;

/**
 * Created by Jingyu_Yao on 1/16/2015.
 */
public class Player extends Tank {

    public Player(Level level, float x, float y, TankType type, Direction direction) {
        super(level, x, y, type, direction);
        gameObjType = GameObjType.PLAYER;
        addListener(new KeyboardInputListener(this));
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
    public void act(float deltaTime){
        super.act(deltaTime);
        moveCamera();
    }
}
