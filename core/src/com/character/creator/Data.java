package com.character.creator;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Data {

	// Gender -> Type -> File
	public HashMap<String, HashMap<String, ArrayList<FileHandle>>> data;
	public HashMap<String, FileHandle> shadow;
	public HashMap<String, TextureRegion[][]> shadowTex;
	public static ArrayList<String> KEYS = new ArrayList<String>();
	public static ArrayList<String> TITLES = new ArrayList<String>();

	public static final String[] REQUIRED_SPRITESHEETS = { "Bust", "Bust_1",
		"Paperdoll", "Face", "Mastersheet", "default" };
	public static final String[] PICS_TO_LOAD = {
		REQUIRED_SPRITESHEETS[0].toLowerCase(), REQUIRED_SPRITESHEETS[5],
		REQUIRED_SPRITESHEETS[3].toLowerCase() };
	public static final String[] LOOKUPS = { "png", "Shadow", "cloth" };
	public static final String[] GENDERS = { "Male", "Female" };
	public static final String RESOURCE_FOLDER_NAME = "ResourcePacks";
	public static final String GENERATED_NAME = "Generated_Characters";
	public static final String CONFIG_PATH = "/" + RESOURCE_FOLDER_NAME
			+ "/config.txt";
	public static final String GENERATED_PATH = "/" + RESOURCE_FOLDER_NAME
			+ "/" + GENERATED_NAME + "/";
	public static final String LOG_PATH = "/" + RESOURCE_FOLDER_NAME
			+ "/log.txt";
	public static final String ANIMATION_SPEED_CONFIG_TAG = "AnimationSpeed:";
	public static float ANIMATION_SPEED = 0.1f;
	public static final String WIP_NAME = "Last_Character_worked_on";

	public HashMap<String, String> ids;
	public static String TEMPLATE;

	private ArrayList<String> extraPacks;

	private Application app;

	public Data(Application app) {
		this.app = app;
		extraPacks = new ArrayList<String>();
		loadKeys();

		data = new HashMap<String, HashMap<String, ArrayList<FileHandle>>>();
		shadow = new HashMap<String, FileHandle>();
		shadowTex = new HashMap<String, TextureRegion[][]>();
		for (String gender : GENDERS) {
			data.put(gender, new HashMap<String, ArrayList<FileHandle>>());
			for (String key : KEYS) {
				data.get(gender).put(key, new ArrayList<FileHandle>());
			}
		}
	}

	private FileHandle getHandleWith(FileHandle file, String str) {
		if (file == null) {
			return null;
		}
		for (FileHandle f : file.list()) {
			if (f.name().contains(str)) {
				return f;
			}
		}
		return null;
	}

	private void loadKeys() {
		ids = new HashMap<String, String>();

		FileHandle config = Gdx.files.absolute(System.getProperty("user.dir")
				+ CONFIG_PATH);

		if (!config.exists()) {
			Gdx.files.internal("config.txt").copyTo(config);
		}
		for (int i = 0; i < REQUIRED_SPRITESHEETS.length; i++) {
			TITLES.add(REQUIRED_SPRITESHEETS[i] + ".");
		}

		int stage = 0;
		String[] lines = config.readString().split("\\r?\\n");
		for (String line : lines) {
			if (line.contains("END_KEYS") || line.contains("END_SPRITESHEETS")) {
				stage++;
				continue;
			}

			if (stage == 0) {
				String[] parts = line.split(",");
				KEYS.add(parts[0]);
				for (int i = 0; i < parts.length; i++) {
					ids.put(parts[i], parts[0]);
				}
			} else if (stage == 1) {
				TITLES.add(line + ".");
			} else if (stage == 2) {
				String[] speed = line.split(":");
				try {
					ANIMATION_SPEED = Float.parseFloat(speed[1]);
				} catch (Exception e) {
					ANIMATION_SPEED = 0.1f;
				}
				stage++;
			} else if (stage == 3) {
				extraPacks.add(line);
			}
		}
		TEMPLATE = KEYS.get(0);
	}

	public void loadPacks() {
		String basePath = System.getProperty("user.dir") + "/"
				+ RESOURCE_FOLDER_NAME;

		for (String path : extraPacks) {
			loadGenderFile(Gdx.files.absolute(basePath + "/" + path));
		}

		for (String gender : GENDERS) {
			FileHandle image = getHandleWith(shadow.get(gender),
					REQUIRED_SPRITESHEETS[4]);
			if (image != null && !app.assets.isLoaded(image.path())) {
				app.assets.load(image.path(), Texture.class);
				app.assets.finishLoadingAsset(image.path());
			}
			shadowTex.put(gender, TextureRegion.split(
					app.assets.get(image.path(), Texture.class), 128, 128));
		}
	}

	public void loadGenderFile(FileHandle pack) {
		String gender = null;
		for (String g : GENDERS) {
			if (pack.name().contains("_" + g)) {
				gender = g;
			}
		}
		if (gender == null) {
			app.log("Tried to load "
					+ pack.name()
					+ " but folder name does not containe _Male or _Female so pack was not loaded");
			return;
		}
		ArrayList<FileHandle> handles = new ArrayList<FileHandle>();
		// gets all .png files and loads them
		getHandles(pack, handles);
		for (FileHandle entry : pack.list()) {
			if (entry.name().toLowerCase().contains(LOOKUPS[2])) {
				for (FileHandle cloth : entry.list()) {
					addFile(cloth, gender);
				}
			} else {
				addFile(entry, gender);
			}
		}
	}

	private void getHandles(FileHandle begin, ArrayList<FileHandle> handles) {
		FileHandle[] newHandles = begin.list();
		for (FileHandle f : newHandles) {
			if (f.isDirectory()) {
				getHandles(f, handles);
			} else if (f.extension().equals(LOOKUPS[0])) {
				for (String type : PICS_TO_LOAD) {
					if (f.name().toLowerCase().contains(type)) {
						handles.add(f);
						break;
					}
				}
			}
		}
	}

	private void addFile(FileHandle entry, String gender) {
		boolean found = false;
		for (String key : ids.keySet()) {
			if (entry.name().contains("_" + key)) {
				if (entry.name().contains(gender)) {
					// copy(entry, gender, ids.get(key));
					data.get(gender).get(ids.get(key)).add(entry);
					found = true;

					break;
				}
			}
		}

		if (!found) {
			if (entry.name().contains(gender)) {
				if (!entry.name().toLowerCase()
						.contains(LOOKUPS[1].toLowerCase())) {
					// copy(entry, gender, TEMPLATE);
					data.get(gender).get(TEMPLATE).add(entry);
				} else {
					shadow.put(gender, entry);
				}
			}
		}
		System.out.println(entry.path());
	}

	public void copy(FileHandle handle, String gender, String key) {
		for (FileHandle h : handle.list()) {
			for (String type : TITLES) {
				if (!type.equals("Mastersheet.") && !type.equals("default.")
						&& !type.equals("Face.") && h.name().contains(type)) {
					String k = gender + "_" + key + "_"
							+ data.get(gender).get(key).size();
					FileHandle h2 = new FileHandle(
							"C:/CharacterCreator/desktop/ResourcePacks/CompliedItems/"
									+ gender + "/" + k + "/" + k + "_" + type
									+ "png");
					h.copyTo(h2);
				}
			}
		}
	}

}
