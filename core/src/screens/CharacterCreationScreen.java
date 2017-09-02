package screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.character.creator.Application;
import com.character.creator.CharacterScroll;
import com.character.creator.CharacterScroll.Clothing;
import com.character.creator.Data;
import com.character.creator.RandomNameGenerator;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;

public class CharacterCreationScreen implements Screen
{

    private final Application app;

    public Stage stage;

    private Image background;
    private CharacterScroll scroll;
    public ColorPicker colorPicker;
    public Clothing selected;

    private RandomNameGenerator names;
    private Label loading;

    // false->bust, true->paperdoll
    public boolean view = false;

    public CharacterCreationScreen(final Application app)
    {
        this.app = app;
        this.stage = new Stage(new FitViewport(Application.V_WIDTH, Application.V_HEIGHT,
                app.camera), app.batch);

        names = new RandomNameGenerator("names.txt", 3, 5);
        app.log("Random Name Generator loaded");

        colorPicker = new ColorPicker(new ColorPickerAdapter()
        {
            @Override
            public void finished(Color newColor)
            {
                selected.setColor(newColor);
            }
        });
        app.log("color picker setup");
    }

    @Override
    public void show()
    {
        app.log("CHARACTER CREATION");
        Gdx.input.setInputProcessor(stage);
        // chooser creation
        app.fileChooser.setListener(new FileChooserAdapter()
        {
            @Override
            public void selected(Array<FileHandle> file)
            {
                FileHandle f = file.get(0);
                scroll.load(f.name());
            }
        });
        app.log("file chooser setup");

        background = new Image(app.assets.get("Background.png", Texture.class));
        background.setSize(Application.V_WIDTH, Application.V_HEIGHT);
        loading = new Label("Loading...", app.skin);
        loading.setPosition(Application.V_WIDTH / 2 - 75, 0);
        loading.addAction(Actions.forever(Actions.sequence(
                Actions.alpha(1f, 1f, Interpolation.sineIn),
                Actions.alpha(.4f, 1f, Interpolation.sineOut))));
        app.log("background loaded");

        initButtons();
        app.log("buttons created");

        scroll.load(Data.WIP_NAME);

    }

    private void update(float delta)
    {
        stage.act(delta);
        if (!app.assets.update())
        {
            loading.setVisible(true);
        }
        else
        {
            loading.setVisible(false);
        }
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        stage.draw();
        stage.getBatch().begin();
        scroll.render(stage.getBatch(), 100, 0, delta);
        stage.getBatch().end();
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
        dispose();
    }

    @Override
    public void dispose()
    {
        stage.dispose();
        colorPicker.dispose();
        scroll.save(Data.WIP_NAME);
        app.log("saved last WIP");
    }

    private void initButtons()
    {
        stage.clear();
        stage.addActor(background);
        stage.addActor(loading);

        initControlButtons();
        scroll = new CharacterScroll(app, app.skin, this);
        stage.addActor(scroll);
    }

    private void initControlButtons()
    {
        TextButton back = new TextButton("Back", app.skin);
        back.setSize(140, 40);
        back.setPosition(15, Application.V_HEIGHT - 40);
        back.addAction(sequence(alpha(0),
                parallel(fadeIn(1f), moveBy(0, -20, 1f, Interpolation.pow5Out))));
        back.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                app.setScreen(app.mainScreen);
            }
        });

        final TextField name = new TextField("Character", app.skin);
        name.setSize(305, 40);
        name.setPosition(340, Application.V_HEIGHT - 100);
        name.addAction(sequence(alpha(0),
                parallel(fadeIn(1f), moveBy(0, -20, 1f, Interpolation.pow5Out))));

        TextButton random = new TextButton("Random", app.skin);
        random.setSize(140, 40);
        random.setPosition(15, Application.V_HEIGHT - 100);
        random.addAction(sequence(alpha(0),
                parallel(fadeIn(1f), moveBy(0, -20, 1f, Interpolation.pow5Out))));
        random.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                app.log("Generating random character");
                scroll.random();
                app.log("Random Character generated");
            }
        });

        TextButton randomName = new TextButton("R. Name", app.skin);
        randomName.setSize(140, 40);
        randomName.setPosition(175, Application.V_HEIGHT - 100);
        randomName.addAction(sequence(alpha(0),
                parallel(fadeIn(1f), moveBy(0, -20, 1f, Interpolation.pow5Out))));
        randomName.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                name.setText(names.NextName());
            }
        });

        TextButton export = new TextButton("Export", app.skin);
        export.setSize(140, 40);
        export.setPosition(175, Application.V_HEIGHT - 40);
        export.addAction(sequence(alpha(0),
                parallel(fadeIn(1f), moveBy(0, -20, 1f, Interpolation.pow5Out))));
        export.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                app.log("Exporting " + name.getText() + " this may take a few seconds");
                scroll.export(name.getText());
                app.log("Exported " + name.getText());
            }
        });

        TextButton load = new TextButton("Load", app.skin);
        load.setSize(140, 40);
        load.setPosition(340, Application.V_HEIGHT - 40);
        load.addAction(sequence(alpha(0),
                parallel(fadeIn(1f), moveBy(0, -20, 1f, Interpolation.pow5Out))));
        load.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                app.log("Loading character");
                stage.addActor(app.fileChooser.fadeIn());
            }
        });

        TextButton save = new TextButton("Save WIP", app.skin);
        save.setSize(140, 40);
        save.setPosition(505, Application.V_HEIGHT - 40);
        save.addAction(sequence(alpha(0),
                parallel(fadeIn(1f), moveBy(0, -20, 1f, Interpolation.pow5Out))));
        save.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                app.log("Saving " + name.getText() + " as a work in progress");
                scroll.save(name.getText());
                app.log("Saved " + name.getText());
            }
        });

        final TextButton view = new TextButton(Data.REQUIRED_SPRITESHEETS[0], app.skin);
        view.setSize(140, 40);
        view.setPosition(175, Application.V_HEIGHT - 160);
        view.addAction(sequence(alpha(0),
                parallel(fadeIn(1f), moveBy(0, -20, 1f, Interpolation.pow5Out))));
        view.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if (view.getText().toString().equals(Data.REQUIRED_SPRITESHEETS[0]))
                    view.setText(Data.REQUIRED_SPRITESHEETS[2]);
                else
                {
                    view.setText(Data.REQUIRED_SPRITESHEETS[0]);
                }
                CharacterCreationScreen.this.view = !CharacterCreationScreen.this.view;
            }
        });

        final TextButton animate = new TextButton("Animate: On", app.skin);
        animate.setSize(140, 40);
        animate.setPosition(15, Application.V_HEIGHT - 160);
        animate.addAction(sequence(alpha(0),
                parallel(fadeIn(1f), moveBy(0, -20, 1f, Interpolation.pow5Out))));
        animate.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if (animate.getText().toString().equals("Animate: On"))
                    animate.setText("Animate: Off");
                else
                {
                    animate.setText("Animate: On");
                }
                CharacterCreationScreen.this.scroll.toggleAnimate();
            }
        });

        stage.addActor(back);
        stage.addActor(random);
        stage.addActor(randomName);
        stage.addActor(export);
        stage.addActor(load);
        stage.addActor(save);
        stage.addActor(name);
        stage.addActor(view);
        stage.addActor(animate);
    }
}
