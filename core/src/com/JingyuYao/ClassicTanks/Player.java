package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by Jingyu_Yao on 1/16/2015.
 */
public class Player extends Tank {
    public Player(GameScreen gameScreen, float x, float y, TankType type, Direction direction) {
        super(gameScreen, x, y, type, direction);
    }

    /**
     * Updates the tanks position while preventing rounding error of final position.
     * Also moves the camera when if necessary.
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        moveCamera();
        //Vector3 cameraPosition = gameScreen.camera.position;
        //float cameraX = cameraPosition.x, cameraY = cameraPosition.y;
        //System.out.println("cameraX: " + cameraX + " cameraY: " + cameraY);
        //System.out.println("playerX: " + body.x + " playerY: " + body.y);
    }

    /**
     * TODO: Bound distance changes with direction to allow larger view
     * Changes the x and y position of the camera to follow the player.
     * The movement allowance is set by {@code GameScreen.cameraInnerBound}
     */
    private void moveCamera(){
        Vector3 cameraPosition = gameScreen.camera.position;
        float cameraX = cameraPosition.x, cameraY = cameraPosition.y;
        int innerBound = gameScreen.cameraInnerBound;

        float dx = 0, dy = 0;

        if(cameraX - body.x > innerBound){
            dx = body.x - cameraX + innerBound;
        }
        else if(body.x - cameraX > innerBound){
            dx = body.x - cameraX - innerBound;
        }
        if(cameraY - body.y > innerBound){
            dy = body.y - cameraY + innerBound;
        }
        else if(body.y - cameraY > innerBound){
            dy = body.y - cameraY - innerBound;
        }
        if(dx != 0 || dy != 0){
            gameScreen.camera.translate(dx, dy);
            gameScreen.camera.update();
        }
    }
}
