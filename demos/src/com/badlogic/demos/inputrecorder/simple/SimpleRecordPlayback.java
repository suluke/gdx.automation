package com.badlogic.demos.inputrecorder.simple;

import com.badlogic.demos.inputrecorder.Game;

public class SimpleRecordPlayback extends Game {

	private SimpleRecordPlaybackScreen screen;

	@Override
	public void onCreate() {
		screen = new SimpleRecordPlaybackScreen(this);
		setScreen(screen);
	}

	public static void main(String[] args) {
		startDemo(new SimpleRecordPlayback());
	}

}
