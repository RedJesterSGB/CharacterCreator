package com.character.creator;

import screens.CharacterCreationScreen;
import screens.LoadingScreen;
import screens.MainMenuScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;

public class Application extends Game
{

    public static final float VERSION = .8f;
    public static final int V_WIDTH = 1280;
    public static final int V_HEIGHT = 720;

    public OrthographicCamera camera;
    public SpriteBatch batch;

    public BitmapFont font24;
    public BitmapFont font64;
    public Skin skin;

    public AssetManager assets;

    public LoadingScreen loadingScreen;
    public MainMenuScreen mainScreen;

    private FileHandle log = null;

    public FileChooser fileChooser;

    public Data data;

    public void log(String str)
    {
        if (log == null)
        {
            FileHandle directory = Gdx.files.absolute(System.getProperty("user.dir"));
            log = Gdx.files.absolute(directory.path() + Data.LOG_PATH);
            if (log.exists())
            {
                log.delete();
                log = Gdx.files.absolute(directory.path() + Data.LOG_PATH);
            }
            log("log file generated");
        }
        long time = System.nanoTime();
        long seconds = (long) (time / 1E9);
        long minutes = seconds / 60;
        long hours = minutes / 60;
        log.writeString(str + " at " + hours + ":" + (minutes % 60) + ":" + (seconds % 60) + "\n",
                true);
    }

    static public ShaderProgram createDefaultShader()
    {
        String vertexShader = "#version 330 core\n" + "in vec4 " + ShaderProgram.POSITION_ATTRIBUTE
                + ";\n" //
                + "in vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "in vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "uniform mat4 u_projTrans;\n" //
                + "out vec4 v_color;\n" //
                + "out vec2 v_texCoords;\n" //
                + "\n" //
                + "void main()\n" //
                + "{\n" //
                + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "   v_color.a = v_color.a * (255.0/254.0);\n" //
                + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "}\n";
        String fragmentShader = "#version 330 core\n" + "#ifdef GL_ES\n" //
                + "#define LOWP lowp\n" //
                + "precision mediump float;\n" //
                + "#else\n" //
                + "#define LOWP \n" //
                + "#endif\n" //
                + "in LOWP vec4 v_color;\n" //
                + "in vec2 v_texCoords;\n" //
                + "out vec4 fragColor;\n" //
                + "uniform sampler2D u_texture;\n" //
                + "void main()\n"//
                + "{\n" //
                + "  fragColor = v_color * texture(u_texture, v_texCoords);\n" //
                + "}";

        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (shader.isCompiled() == false)
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        return shader;
    }

    @Override
    public void create()
    {
        try
        {
            log("App started");
            VisUI.load();
            log("Vis loaded");
            data = new Data(this);
            log("Data storage created");
            assets = new AssetManager();
            log("Assets manager created");
            camera = new OrthographicCamera();
            log("Camera created");
            camera.setToOrtho(false, V_WIDTH, V_HEIGHT);
            log("Camera size set created");
            batch = new SpriteBatch(1000, createDefaultShader());
            log("Render resources created");

            initFonts();
            log("Fonts loaded");
            fileChooser = new FileChooser(Mode.OPEN);
            fileChooser.setSelectionMode(SelectionMode.DIRECTORIES);
            fileChooser.setDirectory(Gdx.files.absolute(System.getProperty("user.dir") + "/"
                    + Data.RESOURCE_FOLDER_NAME));
            FileChooser.setDefaultPrefsName("com.character.creator.filechooser");
            log("File chooser created");

            loadingScreen = new LoadingScreen(this);
            log("Loading screen created");
            mainScreen = new MainMenuScreen(this);
            log("Main Menu Screen created");

            this.setScreen(loadingScreen);
        }
        catch (Exception e)
        {
            log(e.getMessage());
        }
    }

    @Override
    public void render()
    {
        try
        {
            super.render();
        }
        catch (Exception e)
        {
            log(e.getMessage());
        }
    }

    @Override
    public void dispose()
    {
        log("Trying to dispose Application");
        VisUI.dispose();
        batch.dispose();
        font24.dispose();
        font64.dispose();
        assets.dispose();
        mainScreen.dispose();
        if (this.getScreen() instanceof CharacterCreationScreen)
            this.getScreen().dispose();
        log("Application disposed");
    }

    private void initFonts()
    {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("GenBasR.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 24;
        font24 = generator.generateFont(parameter); // font size 12
        parameter.size = 64;
        font64 = generator.generateFont(parameter); // font size 12
        // pixels
        generator.dispose();
    }
}
