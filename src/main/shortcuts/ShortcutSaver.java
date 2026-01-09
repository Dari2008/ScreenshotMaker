package main.shortcuts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.json.JSONException;
import org.json.JSONObject;

public class ShortcutSaver {

	private static JSONObject data;
	
	private static final File SAVE_FILE = new File("shortcuts.json");
	
	public static void loadData() {
		if(!SAVE_FILE.exists()) {
			data = new JSONObject();
			try {
				SAVE_FILE.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		try {
			data = new JSONObject(new String(Files.readAllBytes(SAVE_FILE.toPath())));
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getKeyCodeFor(String action, String defaultvalue) {
		if(data == null) {
			loadData();
		}
		return data.optString(action, defaultvalue);
	}
	
	public static void setKeyCodeFor(String action, String keyCode) {
		if(data == null) {
			loadData();
		}
		data.put(action, keyCode);
		try {
			Files.write(SAVE_FILE.toPath(), data.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
