package com.character.creator.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.character.creator.Application;

public class DesktopLauncher
{
    public static void main(String[] arg)
    {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.useGL30 = false;
        config.title = "Character Creator 2.0";
        config.width = 1280;
        config.height = 720;
        config.addIcon("CCicon.png", FileType.Internal);
        config.addIcon("CCicon32.png", FileType.Internal);
        config.addIcon("CCicon16.png", FileType.Internal);
        new LwjglApplication(new Application(), config);
    }
}
