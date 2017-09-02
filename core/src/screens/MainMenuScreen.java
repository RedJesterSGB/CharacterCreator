package screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.character.creator.Application;
import com.character.creator.Data;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;

public class MainMenuScreen implements Screen
{

    private final Application app;

    private Stage stage;

    public MainMenuScreen(final Application app)
    {
        this.app = app;
        this.stage = new Stage(new FitViewport(Application.V_WIDTH, Application.V_HEIGHT,
                app.camera), app.batch);
        app.data.loadPacks();

    }

    @Override
    public void show()
    {
        app.log("MAIN MENU");
        // chooser creation
        app.fileChooser.setListener(new FileChooserAdapter()
        {
            @Override
            public void selected(Array<FileHandle> file)
            {
                FileHandle f = file.get(0);
                FileHandle parent = f.parent();
                while (!parent.name().equals("ResourcePacks"))
                {
                    parent = parent.parent();
                }
                int parentLength = parent.path().length();

                FileHandle config = Gdx.files.absolute(System.getProperty("user.dir")
                        + Data.CONFIG_PATH);
                String newFile = f.path().substring(parentLength + 1);
                String gender = null;
                for (String g : Data.GENDERS)
                {
                    if (newFile.contains("_" + g))
                    {
                        gender = g;
                    }
                }
                if (gender != null)
                {
                    config.writeString(System.lineSeparator() + newFile, true);
                    app.data.loadGenderFile(f);
                }
                else
                {
                    app.log("Tried to load "
                            + newFile
                            + " but folder name does not containe _Male or _Female so pack was not added to config");
                }
            }
        });

        Gdx.input.setInputProcessor(stage);
        stage.clear();

        Image background = new Image(app.assets.get("MainMenuBackground.png", Texture.class));
        background.setSize(Application.V_WIDTH, Application.V_HEIGHT);
        stage.addActor(background);
        Label label = new Label("Character Creator", this.app.skin, "font64");
        label.setPosition(Application.V_WIDTH / 2 - (label.getWidth() / 2), Application.V_HEIGHT
                / 2 - (label.getHeight() / 2) + 120);
        stage.addActor(label);
        app.log("Main menu background and title created");
        initButtons();
        app.log("Main menu control buttons loaded");
    }

    private void update(float delta)
    {
        stage.act(delta);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        stage.draw();
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height, true);
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

    }

    @Override
    public void dispose()
    {
        stage.dispose();
        app.log("Main Menu screen disposed");
    }

    private void initButtons()
    {
        TextButton buttonPlay = new TextButton("New Character", app.skin, "default");
        buttonPlay.setSize(280, 60);
        buttonPlay.setPosition(Application.V_WIDTH / 2 - (buttonPlay.getWidth() / 2),
                Application.V_HEIGHT / 2 - (buttonPlay.getHeight() / 2));
        buttonPlay.addAction(sequence(alpha(0),
                parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonPlay.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                app.setScreen(new CharacterCreationScreen(app));
            }
        });

        TextButton buttonTutorial = new TextButton("Help", app.skin, "default");
        buttonTutorial.setSize(135, 60);
        buttonTutorial.setPosition(Application.V_WIDTH / 2 - (buttonTutorial.getWidth()) - 5,
                Application.V_HEIGHT / 2 - (buttonTutorial.getHeight() / 2) - 70);
        buttonTutorial.addAction(sequence(alpha(0),
                parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonTutorial.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                app.setScreen(new TutorialScreen(app));
            }
        });

        TextButton buttonConfig = new TextButton("Add File", app.skin, "default");
        buttonConfig.setSize(135, 60);
        buttonConfig.setPosition(Application.V_WIDTH / 2 + 5, Application.V_HEIGHT / 2
                - (buttonConfig.getHeight() / 2) - 70);
        buttonConfig.addAction(sequence(alpha(0),
                parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonConfig.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                stage.addActor(app.fileChooser.fadeIn());
            }
        });

        TextButton buttonExit = new TextButton("Exit", app.skin, "default");
        buttonExit.setSize(280, 60);
        buttonExit.setPosition(Application.V_WIDTH / 2 - (buttonExit.getWidth() / 2),
                Application.V_HEIGHT / 2 - (buttonExit.getHeight() / 2) - 140);
        buttonExit.addAction(sequence(alpha(0),
                parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonExit.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Gdx.app.exit();
            }
        });

        stage.addActor(buttonPlay);
        stage.addActor(buttonTutorial);
        stage.addActor(buttonConfig);
        stage.addActor(buttonExit);
    }
}
