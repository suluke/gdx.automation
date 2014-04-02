package com.badlogic.demos.automation.simple;

import com.badlogic.demos.automation.Game;

public class SimpleRecordPlayback extends Game {

	private SimpleRecordPlaybackScreen screen;

	@Override
	public void onCreate() {
		screen = new SimpleRecordPlaybackScreen(this);
		setScreen(screen);
	}

	public static void main(String[] args) {
		try {
			startDemo(new SimpleRecordPlayback());
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

}
