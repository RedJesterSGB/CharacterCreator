package screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.character.creator.Application;

public class TutorialScreen implements Screen
{

    private final Application app;
    private Stage stage;
    private Image image;

    public TutorialScreen(final Application app)
    {
        this.app = app;
        this.stage = new Stage(new FitViewport(Application.V_WIDTH, Application.V_HEIGHT,
                app.camera), app.batch);
    }

    @Override
    public void show()
    {
        app.log("TUTORIAL");

        stage.clear();
        Gdx.input.setInputProcessor(stage);

        image = new Image(app.assets.get("Tutorial.png", Texture.class));
        image.setSize(Application.V_WIDTH, Application.V_HEIGHT);
        image.addAction(sequence(alpha(0), fadeIn(2f, Interpolation.pow2)));

        stage.addActor(image);
        app.log("Tutorial screen loaded");
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        stage.draw();
    }

    public void update(float delta)
    {
        stage.act(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY))
        {
            app.setScreen(app.mainScreen);
        }
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height, false);
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {
        dispose();
    }

    @Override
    public void dispose()
    {
        stage.dispose();
        app.log("Tutorial disposed");
    }

}
