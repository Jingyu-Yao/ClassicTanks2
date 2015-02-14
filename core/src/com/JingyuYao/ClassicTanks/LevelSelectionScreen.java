package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LevelSelectionScreen implements Screen {

    public static final int PANEL_SIZE = 100;

    private final ClassicTanks game;

    private final String instructions =
            "Arrow keys to move, hold down space to shoot. \n" +
            "Shiny enemies drop buffs:\n" +
            "Red = kill everything\n" +
            "Green = hp++\n" +
            "Blue = freeze enemies for 10s\n" +
            "Yellow = advance tank type";

    /*
    Renderer objects
     */
    private final Viewport viewPort;
    private final OrthographicCamera camera;
    private final Stage stage;
    private final Table table;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch; //From game
    private final BitmapFont font; //From game

    public LevelSelectionScreen(final ClassicTanks g) {
        game = g;
        batch = game.batch;
        font = game.font;

        // Camera setup
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewPort = new ScreenViewport();
        viewPort.setCamera(camera);
        shapeRenderer = new ShapeRenderer();

        // Stage setup
        table = new Table();
        //The positioning is mad weird.
        table.setPosition(PANEL_SIZE*1.5f, Gdx.graphics.getHeight() - PANEL_SIZE/2);
        stage = new Stage();
        stage.setViewport(viewPort);
        stage.addActor(table);

        //TODO: automate level adding according to files in folder.
        addLevel(1, Color.GREEN);
        addLevel(2, Color.BLUE);
        addLevel(3, Color.BLACK);
    }

    public void startLevel(int levelNumber){
        game.setToGameScreen(levelNumber);
    }

    public OrthographicCamera getCamera(){
        return camera;
    }

    /**
     * Factory method to create and add {@code LevelButton} to the stage
     * @param levelNumber
     */
    private void addLevel(int levelNumber, Color color){
        TextButton.TextButtonStyle tempB = new TextButton.TextButtonStyle();
        tempB.font = font;
        tempB.fontColor = color;
        table.add(new LevelButton(this, levelNumber, tempB)).size(PANEL_SIZE,PANEL_SIZE);
    }

    @Override
    public void render(float delta) {
        // Delay guard
        if (delta > 0.1f) {
            return;
        }

        Gdx.gl20.glClearColor(1.0f, 1.0f, 1.0f, 0);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        //Test mode stuff
        batch.begin();
        font.drawMultiLine(batch, instructions, 10, 200);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewPort.update(width,height);
    }

    @Override
    public void show() {
        System.out.println("LevelSelection show");
        Gdx.input.setInputProcessor(stage);
        font.setColor(Color.RED);
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
        System.out.println("Levelselection dispose");
        stage.dispose();
        shapeRenderer.dispose();
    }

}
