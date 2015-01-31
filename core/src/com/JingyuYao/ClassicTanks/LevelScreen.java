package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LevelScreen implements Screen {

    public static final int CAMERA_SIZE = 640;

    final ClassicTanks game;

    /*
    Renderer objects
     */
    Viewport viewPort;
    OrthographicCamera camera;
    Stage stage;

    public LevelScreen(final ClassicTanks g) {
        game = g;

        // Camera setup
        camera = new OrthographicCamera();
        camera.setToOrtho(false, CAMERA_SIZE, CAMERA_SIZE);
        viewPort = new ScreenViewport();
        viewPort.setCamera(camera);

        // Stage setup
        stage = new Stage();
        stage.setViewport(viewPort);

        stage.addActor(new LevelPanel(this, 100, CAMERA_SIZE - 100, 1));

        Gdx.input.setInputProcessor(stage);
    }

    /**
     * TODO
     * The main game loop for this screen.
     *
     * @param delta
     */
    @Override
    public void render(float delta) {
        // Delay guard
        if (delta > 0.1f) {
            return;
        }

        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewPort.update(width,height);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

}
