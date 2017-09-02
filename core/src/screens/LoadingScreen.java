package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.character.creator.Application;

public class LoadingScreen implements Screen
{

    private final Application app;

    private ShapeRenderer shapeRenderer;
    private float progress;

    public LoadingScreen(final Application app)
    {
        this.app = app;
        this.shapeRenderer = new ShapeRenderer();
    }

    private void queueAssets()
    {
        app.assets.load("uiskin.atlas", TextureAtlas.class);
        app.assets.load("Background.png", Texture.class);
        app.assets.load("MainMenuBackground.png", Texture.class);
        app.assets.load("Tutorial.png", Texture.class);
        app.assets.load("pvglogo.png", Texture.class);
        app.assets.load("aschmidtlogo.png", Texture.class);
        app.assets.load("badlogic.jpg", Texture.class);
    }

    @Override
    public void show()
    {
        app.log("LOADING");
        shapeRenderer.setProjectionMatrix(app.camera.combined);
        this.progress = 0f;
        queueAssets();
        app.log("Assets queued");
    }

    private void update(float delta)
    {
        progress = MathUtils.lerp(progress, app.assets.getProgress(), .1f);
        if (app.assets.update() && progress >= app.assets.getProgress() - .001f)
        {
            app.log("loading screen fully loaded");

            app.skin = new Skin();
            app.skin.addRegions(app.assets.get("uiskin.atlas", TextureAtlas.class));
            app.skin.add("default-font", app.font24);
            app.skin.add("font64", app.font64);
            app.skin.load(Gdx.files.internal("uiskin.json"));

            app.log("Skin created");
            app.setScreen(new SplashScreen(app));
        }
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        if (shapeRenderer == null)
            return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer
        .rect(32, app.camera.viewportHeight / 2 - 8, app.camera.viewportWidth - 64, 64);

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(32, app.camera.viewportHeight / 2 - 8, progress
                * (app.camera.viewportWidth - 64), 64);

        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height)
    {

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
        shapeRenderer.dispose();
        shapeRenderer = null;
        app.log("Loading screen disposed");
    }
}
