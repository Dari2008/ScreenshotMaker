package main.shortcuts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;


public class ShortcutManager implements NativeKeyListener {
	
	private ArrayList<Integer> pressedKeys = new ArrayList<>();
	private ShortcutAction takeScreenshotListener;
	private ShortcutAction retakeButtonListener;

    static {

		try {
			GlobalScreen.registerNativeHook();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public ShortcutManager(ShortcutAction takeScreenshotAction, ShortcutAction retakeButtonAction) {
		GlobalScreen.addNativeKeyListener(this);
		this.takeScreenshotListener = takeScreenshotAction;
		this.retakeButtonListener = retakeButtonAction;
	}
	
	private void checkForPressed() {
		StringBuilder sb = new StringBuilder();
		ArrayList<Integer> sortedKeys = new ArrayList<>(pressedKeys);
		sortedKeys.sort(Integer::compareTo);
		for(int keyCode : sortedKeys) {
			if(sb.length() > 0)sb.append("+");
			sb.append(NativeKeyEvent.getKeyText(keyCode));
		}
		String pressedShortcut = ShortcutRecorder.sortKeyPress(sb.toString());
		System.out.println(pressedShortcut);
		
		String takeScreenshotShortcut = ShortcutSaver.getKeyCodeFor("takeScreenshot", "Ctrl+Shift+S");
		String retakeButtonShortcut = ShortcutSaver.getKeyCodeFor("retakeButton", "Ctrl+R");
		
		if(pressedShortcut.equalsIgnoreCase(takeScreenshotShortcut)) {
			this.takeScreenshotListener.onAction();
		}else if(pressedShortcut.equalsIgnoreCase(retakeButtonShortcut)) {
			this.retakeButtonListener.onAction();
		}
	}
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if(pressedKeys.contains(e.getKeyCode()))return;
		pressedKeys.add(e.getKeyCode());
		checkForPressed();
		System.out.println(e.getKeyCode());
	}
	
	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		pressedKeys.remove((Integer)e.getKeyCode());
	}
	
	public static interface ShortcutAction{
		void onAction();
	}
	
}
