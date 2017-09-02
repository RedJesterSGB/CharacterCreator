package screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.character.creator.Application;

public class SplashScreen implements Screen
{

    private final Application app;
    private Stage stage;

    private Image pvg, bl, schmidt;

    public SplashScreen(final Application app)
    {
        this.app = app;
        this.stage = new Stage(new FitViewport(Application.V_WIDTH, Application.V_HEIGHT,
                app.camera), app.batch);
    }

    @Override
    public void show()
    {
        app.log("SPLASH");

        pvg = new Image(app.assets.get("pvglogo.png", Texture.class));
        pvg.setPosition(stage.getWidth() / 2 - pvg.getWidth() / 2,
                stage.getHeight() / 2 + pvg.getHeight() / 2);
        pvg.addAction(sequence(Actions.alpha(0), fadeIn(1.5f, Interpolation.pow2), delay(1.5f),
                fadeOut(1f)));
        bl = new Image(app.assets.get("badlogic.jpg", Texture.class));
        bl.setPosition(stage.getWidth() / 2, stage.getHeight() / 2 - bl.getHeight());
        bl.addAction(sequence(Actions.alpha(0), delay(.2f), fadeIn(1.5f, Interpolation.pow2),
                delay(1.3f), fadeOut(1f)));
        schmidt = new Image(app.assets.get("aschmidtlogo.png", Texture.class));
        schmidt.setSize(256, 256);
        schmidt.setPosition(stage.getWidth() / 2 - (schmidt.getWidth() * 3 / 2), stage.getHeight()
                / 2 - schmidt.getHeight());
        schmidt.addAction(sequence(Actions.alpha(0), delay(.4f), fadeIn(1.5f, Interpolation.pow2),
                delay(1.1f), fadeOut(1f), Actions.run(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        app.setScreen(app.mainScreen);
                    }
                })));

        stage.addActor(pvg);
        stage.addActor(bl);
        stage.addActor(schmidt);
        app.log("Logos loaded");
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
        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)
                || Gdx.input.isButtonPressed(Input.Buttons.LEFT))
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
        app.log("Splash disposed");
    }
}
