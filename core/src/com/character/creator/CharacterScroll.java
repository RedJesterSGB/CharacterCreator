package com.character.creator;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.ArrayList;

import screens.CharacterCreationScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class CharacterScroll extends Group
{

    private Application app;
    private Skin skin;

    private ScrollPane pane;
    private VerticalGroup list;
    private ArrayList<Clothing> cloths;
    private CheckBox shadow;
    public String gender;

    private int numSliders;
    private int maxSliders = 64;

    private final CheckBox male;
    private final CheckBox female;

    private CharacterCreationScreen ccs;

    private int animationCounter = 0;
    private int timesPlayed = 0;
    private int directionCounter = 0;
    private float time = 0;
    private boolean animate = true;
    private Group colorPane;

    public CharacterScroll(Application app, Skin skin, CharacterCreationScreen ccs)
    {
        super();
        this.ccs = ccs;

        male = new CheckBox(" " + Data.GENDERS[0] + " ", skin);
        female = new CheckBox(" " + Data.GENDERS[1] + " ", skin);

        Group g = new Group();
        g.setSize(570, 550);
        g.setPosition(703, 30);
        addAction(sequence(alpha(0),
                parallel(fadeIn(1f), Actions.moveBy(0, -20, 1f, Interpolation.pow5Out))));

        gender = Data.GENDERS[0];
        this.app = app;
        this.skin = skin;
        list = new VerticalGroup();
        cloths = new ArrayList<Clothing>();
        pane = new ScrollPane(list, skin);
        pane.setSize(450, 490);
        pane.setFlickScroll(false);
        pane.setFadeScrollBars(false);
        pane.setFillParent(true);
        g.addActor(pane);

        Group controlsPane = new Group();
        controlsPane.setSize(570, 140);
        controlsPane.setPosition(703, 590);
        VerticalGroup list2 = new VerticalGroup();
        Table controls = getNewControls();
        list2.addActor(controls);
        Table gender = initGenderSelection();
        list2.addActor(gender);
        Table titles = initTitles();
        list2.addActor(titles);
        ScrollPane pane2 = new ScrollPane(list2, skin);
        pane2.setSize(450, 130);
        pane2.setFlickScroll(false);
        pane2.setFadeScrollBars(false);
        pane2.setFillParent(true);
        controlsPane.addActor(pane2);

        colorPane = new Group();
        colorPane.setSize(305, 40);
        colorPane.setPosition(175 + 160, Application.V_HEIGHT - 160);
        VerticalGroup list3 = new VerticalGroup();
        ScrollPane pane3 = new ScrollPane(list3, skin);
        pane3.setSize(450, 130);
        pane3.setFlickScroll(false);
        pane3.setFadeScrollBars(false);
        pane3.setFillParent(true);
        colorPane.addActor(pane3);

        this.addActor(colorPane);
        this.addActor(g);
        this.addActor(controlsPane);

    }

    public void toggleAnimate()
    {
        animate = !animate;
        animationCounter = 0;
        timesPlayed = 0;
        directionCounter = 0;
        time = 0;
    }

    public void render(Batch batch, int x, int y, float delta)
    {
        if (animate)
        {
            time += delta;
            if (time >= Data.ANIMATION_SPEED)
            {
                time = 0;
                animationCounter++;
                if (animationCounter == 8)
                {
                    animationCounter = 0;
                    timesPlayed++;
                }
                if (timesPlayed == 3)
                {
                    timesPlayed = 0;
                    directionCounter = (directionCounter + 1) % 4;
                }
            }
        }
        app.assets.update();
        if (shadow.isChecked())
        {
            if (animate)
            {
                batch.draw(
                        app.data.shadowTex.get(gender)[(0 + (directionCounter * 8) + animationCounter) / 24][(0 + (directionCounter * 8) + animationCounter) % 24],
                        x - 82, y);
                batch.draw(
                        app.data.shadowTex.get(gender)[(32 + (directionCounter * 8) + animationCounter) / 24][(32 + (directionCounter * 8) + animationCounter) % 24],
                        x - 82, y + 128);
                batch.draw(
                        app.data.shadowTex.get(gender)[(64 + (directionCounter * 8) + animationCounter) / 24][(64 + (directionCounter * 8) + animationCounter) % 24],
                        x - 82, y + 256);
                batch.draw(
                        app.data.shadowTex.get(gender)[(96 + (directionCounter * 8) + animationCounter) / 24][(96 + (directionCounter * 8) + animationCounter) % 24],
                        x - 82, y + 384);
            }
            else
            {
                FileHandle image = getHandleWith(app.data.shadow.get(gender),
                        Data.REQUIRED_SPRITESHEETS[5]);
                if (image != null && !app.assets.isLoaded(image.path()))
                {
                    app.assets.load(image.path(), Texture.class);
                }
                if (image != null && app.assets.isLoaded(image.path()))
                {
                    batch.draw(TextureRegion.split(app.assets.get(image.path(), Texture.class),
                            128, 512)[0][1], x - 82, y);
                }
            }
        }
        for (int i = 0; i < maxSliders; i++)
        {
            for (Clothing c : cloths)
            {
                if (((IntSpinnerModel) (c.position.getModel())).getValue() == i
                        && c.hide.isChecked())
                {
                    String key = c.label.getText().toString();
                    if (app.data.data.get(gender).get(key).size() == 0)
                        break;
                    FileHandle f = app.data.data.get(gender).get(key).get((int) c.base.getValue());
                    FileHandle image = null;
                    image = getHandleWith(f,
                            ccs.view ? Data.REQUIRED_SPRITESHEETS[2]
                                    : (key.equals(Data.TEMPLATE) ? Data.REQUIRED_SPRITESHEETS[1]
                                            : Data.REQUIRED_SPRITESHEETS[0]));
                    batch.setColor(c.color);
                    if (image != null && !app.assets.isLoaded(image.path()))
                    {
                        app.assets.load(image.path(), Texture.class);
                    }
                    if (image != null && app.assets.isLoaded(image.path()))
                    {
                        batch.draw(app.assets.get(image.path(), Texture.class), x, y);
                    }
                    image = getHandleWith(f, Data.REQUIRED_SPRITESHEETS[3]);
                    if (image != null && !app.assets.isLoaded(image.path()))
                    {
                        app.assets.load(image.path(), Texture.class);
                    }
                    if (image != null && app.assets.isLoaded(image.path()))
                    {
                        batch.draw(TextureRegion.split(app.assets.get(image.path(), Texture.class),
                                144, 144)[0][0], x + 440, y + 325);
                    }
                    image = getHandleWith(f, animate ? Data.REQUIRED_SPRITESHEETS[4]
                            : Data.REQUIRED_SPRITESHEETS[5]);
                    if (image != null && !app.assets.isLoaded(image.path()))
                    {
                        app.assets.load(image.path(), Texture.class);
                    }
                    if (image != null && app.assets.isLoaded(image.path()))
                    {
                        if (animate)
                        {
                            c.updateRegion(image);
                            if (c.regions != null)
                            {
                                batch.draw(
                                        c.regions[(0 + (directionCounter * 8) + animationCounter) / 24][(0 + (directionCounter * 8) + animationCounter) % 24],
                                        x - 82, y);
                                batch.draw(
                                        c.regions[(32 + (directionCounter * 8) + animationCounter) / 24][(32 + (directionCounter * 8) + animationCounter) % 24],
                                        x - 82, y + 128);
                                batch.draw(
                                        c.regions[(64 + (directionCounter * 8) + animationCounter) / 24][(64 + (directionCounter * 8) + animationCounter) % 24],
                                        x - 82, y + 256);
                                batch.draw(
                                        c.regions[(96 + (directionCounter * 8) + animationCounter) / 24][(96 + (directionCounter * 8) + animationCounter) % 24],
                                        x - 82, y + 384);
                            }
                        }
                        else
                        {
                            batch.draw(TextureRegion.split(
                                    app.assets.get(image.path(), Texture.class), 128, 512)[0][1],
                                    x - 82, y);
                        }
                    }
                    break;
                }
            }
        }
    }

    private FileHandle getHandleWith(FileHandle file, String str)
    {
        if (file == null)
        {
            return null;
        }
        for (FileHandle f : file.list())
        {
            if (f.name().contains(str))
            {
                return f;
            }
        }
        return null;
    }

    public void save(String name)
    {
        FileHandle file = Gdx.files.absolute(System.getProperty("user.dir") + Data.GENERATED_PATH
                + name + "/" + name + ".txt");
        file.writeString(gender + "," + shadow.isChecked() + "\n", false);
        for (Clothing c : cloths)
        {
            String key = c.label.getText().toString();
            if (c.base.getMaxValue() > 0)
            {
                FileHandle resource = app.data.data.get(gender).get(key)
                        .get((int) c.base.getValue());
                file.writeString(
                        key + "," + resource.name() + ","
                                + ((IntSpinnerModel) (c.position.getModel())).getValue() + ","
                                + c.hide.isChecked() + "," + Color.argb8888(c.color) + "\n", true);
            }
        }
    }

    public void load(String name)
    {
        FileHandle file = Gdx.files.absolute(System.getProperty("user.dir") + Data.GENERATED_PATH
                + name + "/" + name + ".txt");
        app.log("Trying to load " + file.name());
        if ((!file.parent().parent().name().equals(Data.GENERATED_NAME) || !file.parent()
                .isDirectory()))
        {
            app.log("Loading failed, make sure you selected the folder for the character, not the text file");
            return;
        }

        String[] lines = file.readString().split("\\r?\\n");
        String[] primary = lines[0].split(",");
        gender = primary[0];
        female.setChecked(gender.equals(Data.GENDERS[1]));
        male.setChecked(gender.equals(Data.GENDERS[0]));
        init();
        shadow.setChecked(Boolean.parseBoolean(primary[1]));
        for (int i = 1; i < lines.length; i++)
        {
            String[] parts = lines[i].split(",");
            Clothing c = new Clothing(parts[0]);
            c.setItem(parts[1]);
            c.setPosition(Integer.parseInt(parts[2]));
            c.hide.setChecked(Boolean.parseBoolean(parts[3]));
            Color color = new Color();
            Color.argb8888ToColor(color, Integer.parseInt(parts[4]));
            c.setColor(color);
            list.addActor(c);
            cloths.add(c);
        }
    }

    public void export(String name)
    {
        save(name);
        for (String title : Data.TITLES)
        {
            Texture finalMap = null;
            FileHandle tempFile = null;
            FrameBuffer fbo = null;
            SpriteBatch batch = null;

            for (int i = 0; i < maxSliders; i++)
            {
                for (Clothing c : cloths)
                {
                    if (c.base.getMaxValue() > 0
                            && ((IntSpinnerModel) (c.position.getModel())).getValue() == i
                            && c.hide.isChecked())
                    {
                        String key = c.label.getText().toString();
                        FileHandle f = app.data.data.get(gender).get(key)
                                .get((int) c.base.getValue());
                        String useTitle = title;
                        if (!key.equals(Data.TEMPLATE)
                                && title.contains(Data.REQUIRED_SPRITESHEETS[0]))
                        {
                            useTitle = Data.REQUIRED_SPRITESHEETS[0];
                        }

                        tempFile = getHandleWith(f, useTitle);
                        if (tempFile == null)
                            continue;

                        Texture tex = new Texture(tempFile);
                        if (finalMap == null)
                        {
                            finalMap = new Texture(tex.getWidth(), tex.getHeight(), Format.RGBA8888);

                            fbo = new FrameBuffer(Format.RGBA8888, tex.getWidth(), tex.getHeight(),
                                    false);
                            batch = new SpriteBatch(1000, Application.createDefaultShader());
                            OrthographicCamera camera = new OrthographicCamera(tex.getWidth(),
                                    tex.getHeight());
                            camera.translate(-tex.getWidth(), -tex.getHeight());
                            batch.setProjectionMatrix(camera.combined);
                            fbo.begin();

                            Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
                            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                            batch.begin();
                            if (shadow.isChecked())
                            {
                                FileHandle t = getHandleWith(app.data.shadow.get(gender), useTitle);
                                if (t != null)
                                {
                                    Texture tx = new Texture(t);
                                    batch.draw(tx, 0 - (tex.getWidth() / 2),
                                            0 - (tex.getHeight() / 2));
                                }
                            }
                        }
                        batch.setColor(c.color);
                        batch.draw(tex, 0 - (tex.getWidth() / 2), 0 - (tex.getHeight() / 2));
                        break;
                    }
                }
            }
            if (batch == null)
            {
                continue;
            }
            batch.end();

            byte[] data = ScreenUtils.getFrameBufferPixels(0, 0, finalMap.getWidth(),
                    finalMap.getHeight(), true);

            Pixmap map = new Pixmap(finalMap.getWidth(), finalMap.getHeight(), Format.RGBA8888);
            map.getPixels().put(data);

            PixmapIO.writePNG(
                    Gdx.files.absolute(System.getProperty("user.dir") + Data.GENERATED_PATH + name
                            + "/" + name + "_" + title + "png"), map);
            fbo.end();
            fbo.dispose();
        }

    }

    private void init()
    {
        list = new VerticalGroup();
        cloths = new ArrayList<Clothing>();
        pane.setWidget(list);
        numSliders = 0;
        VerticalGroup list3 = new VerticalGroup();
        ScrollPane pane3 = new ScrollPane(list3, skin);
        pane3.setSize(450, 130);
        pane3.setFlickScroll(false);
        pane3.setFadeScrollBars(false);
        pane3.setFillParent(true);
        colorPane.addActor(pane3);
    }

    public void random()
    {
        init();
        for (String key : Data.KEYS)
        {
            if (key.equals(Data.TEMPLATE) || Math.random() * 10 <= 8)
            {
                if (numSliders < maxSliders - 1
                        && app.data.data.get(gender).get(key).size() - 1 >= 0)
                {
                    Clothing c = new Clothing(key);
                    c.base.setValue((int) (Math.random() * c.max));
                    list.addActor(c);
                    cloths.add(c);
                }
            }
        }
    }

    public void rebuild()
    {
        list.clearChildren();
        for (int i = 0; i < maxSliders; i++)
        {
            for (Clothing c : cloths)
            {
                if (((IntSpinnerModel) (c.position.getModel())).getValue() == i)
                {
                    list.addActor(c);
                }
            }
        }
    }

    private Table getNewControls()
    {
        shadow = new CheckBox(" " + Data.LOOKUPS[1], skin);
        shadow.setSize(140, 40);

        final SelectBox<String> select = new SelectBox<String>(skin);
        select.setSize(140, 40);
        select.setItems(Data.KEYS.toArray(new String[]
        {}));

        TextButton add = new TextButton("Add", skin);
        add.setSize(140, 40);
        add.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                if (numSliders < maxSliders - 1
                        && app.data.data.get(gender).get(select.getSelected()).size() - 1 >= 0)
                {
                    Clothing c = new Clothing(select.getSelected());
                    list.addActor(c);
                    cloths.add(c);
                }
            }
        });
        Table h = new Table();
        h.row();
        h.add(shadow).width(150).pad(5);
        h.add(select).width(200).pad(5);
        h.add(add).width(80).pad(5);
        return h;
    }

    private Table initGenderSelection()
    {
        male.setSize(140, 40);
        male.setPosition(500, Application.V_HEIGHT - 35);
        male.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                gender = Data.GENDERS[0];
                female.setChecked(false);
                init();
            }
        });

        female.setSize(140, 40);
        female.setPosition(670, Application.V_HEIGHT - 35);
        female.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                gender = Data.GENDERS[1];
                male.setChecked(false);
                init();
            }
        });
        male.setChecked(gender.equals(Data.GENDERS[0]));
        female.setChecked(gender.equals(Data.GENDERS[1]));

        TextButton clearAll = new TextButton("Clear All", skin);
        clearAll.setSize(140, 40);
        clearAll.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                float value = -1;
                for (Clothing cloth : cloths)
                {
                    if (cloth.label.getText().toString().contains(Data.TEMPLATE))
                    {
                        value = cloth.base.getValue();
                    }
                    break;
                }
                init();
                if (value != -1)
                {
                    Clothing c = new Clothing(Data.TEMPLATE);
                    c.base.setValue((int) value);
                    list.addActor(c);
                    cloths.add(c);
                }
            }
        });

        Table t = new Table();
        t.add(male).width(150).pad(5);
        t.add(female).width(150).pad(5);
        t.add(clearAll).width(130).pad(5);

        return t;
    }

    private Table initTitles()
    {
        Label hide = new Label("Hide|Color|Remove|", skin);
        Label pos = new Label("|Position", skin);

        Table t = new Table();
        t.add(hide).width(460).pad(5);
        t.add(pos).width(80).pad(5);

        return t;
    }

    public class Clothing extends Table
    {
        CheckBox hide;
        public Button colorButton;
        public Color color;
        private TextButton remove;
        public TextButton label;
        Slider base;
        private TextButton left, right;
        public Spinner position;
        private int previousPos;
        public int max;
        private TextureRegion[][] regions;
        private FileHandle image;

        public Clothing(final String type)
        {
            this.row();

            hide = new CheckBox("", skin);
            hide.setChecked(true);
            final Clothing c = this;

            color = Color.WHITE;
            colorButton = new Button(skin);
            colorButton.setColor(color);
            colorButton.setSize(50, 30);
            colorButton.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event, Actor actor)
                {
                    ccs.selected = c;
                    ccs.stage.addActor(ccs.colorPicker.fadeIn());
                    ccs.colorPicker.setPosition(800, Application.V_HEIGHT / 2 - 140);
                }
            });

            remove = new TextButton("X", skin);
            remove.setColor(Color.RED);
            remove.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event, Actor actor)
                {
                    c.remove();
                    cloths.remove(c);
                    numSliders--;
                    for (Clothing c2 : cloths)
                    {
                        if (((IntSpinnerModel) (c2.position.getModel())).getValue() >= ((IntSpinnerModel) (c.position
                                .getModel())).getValue())
                        {
                            c2.position.decrement();
                        }
                    }
                }
            });

            label = new TextButton(type, skin);
            label.addListener(new ChangeListener()
            {

                @Override
                public void changed(ChangeEvent event, Actor actor)
                {
                    setFocus();
                }

            });

            max = app.data.data.get(gender).get(type).size() - 1;
            if (max < 0)
                max = 0;
            app.log("No "
                    + type
                    + " loaded, try making sure you have all the right packs for the character you are trying to create");

            Integer[] numList = new Integer[max];
            for (int i = 0; i < max; i++)
            {
                numList[i] = i;
            }

            base = new Slider(0, max, 1, false, skin);

            left = new TextButton("<", skin);
            left.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event, Actor actor)
                {
                    base.setValue(base.getValue() - 1);
                }
            });
            right = new TextButton(">", skin);
            right.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event, Actor actor)
                {
                    base.setValue(base.getValue() + 1);
                }
            });
            final IntSpinnerModel intModel = new IntSpinnerModel(numSliders, 0, maxSliders, 1);
            position = new Spinner("", intModel);
            previousPos = numSliders;
            position.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event, Actor actor)
                {
                    setPosition(intModel.getValue());
                }
            });

            this.add(remove).width(45).pad(5);
            this.add(label).width(110).pad(5).padTop(5);
            this.add(left).width(15).pad(5);
            this.add(base).width(270).pad(5);
            this.add(right).width(15).pad(5);
            this.add(position).width(35).pad(5);
            numSliders++;
            this.setSize(40, 450);
        }

        public void updateRegion(FileHandle image)
        {
            if (this.image != image)
            {
                this.image = image;
                regions = TextureRegion
                        .split(app.assets.get(image.path(), Texture.class), 128, 128);
            }
        }

        public void setItem(String string)
        {
            for (int i = 0; i < max; i++)
            {
                if (app.data.data.get(gender).get(label.getText().toString()).get(i).name()
                        .equals(string))
                {
                    base.setValue(i);
                }
            }
        }

        public void setPosition(int number)
        {
            final Clothing c = this;
            for (Clothing c1 : cloths)
            {
                if (c != c1
                        && ((IntSpinnerModel) (c1.position.getModel())).getValue() == ((IntSpinnerModel) (c.position
                                .getModel())).getValue())
                {
                    ((IntSpinnerModel) (c1.position.getModel())).setValue(previousPos);
                    c1.previousPos = previousPos;
                    previousPos = number;
                    break;
                }
            }
            CharacterScroll.this.rebuild();
        }

        @Override
        public void setColor(Color color)
        {
            colorButton.setColor(color);
            this.color = color;
        }

        @Override
        public String toString()
        {
            return label.getText() + " : " + ((IntSpinnerModel) (position.getModel())).getValue();
        }

        private void setFocus()
        {
            Table list3 = new Table();
            list3.add(hide).width(30).pad(5);
            list3.add(
                    new Label(label.getText() + ": "
                            + ((IntSpinnerModel) (position.getModel())).getValue(), skin))
                    .width(130).pad(5);
            list3.add(new Label("Color: ", skin)).width(60).pad(5);
            list3.add(colorButton).width(30).pad(5);

            ScrollPane pane3 = new ScrollPane(list3, skin);
            pane3.setSize(300, 37);
            pane3.setScrollingDisabled(true, true);
            pane3.setFlickScroll(false);
            pane3.setFadeScrollBars(false);
            pane3.setFillParent(true);
            colorPane.addActor(pane3);
        }
    }
}
