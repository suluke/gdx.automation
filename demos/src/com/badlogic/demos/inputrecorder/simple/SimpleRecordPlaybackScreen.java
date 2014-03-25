package com.badlogic.demos.inputrecorder.simple;

import com.badlogic.demos.inputrecorder.AbstractScreen;
import com.badlogic.demos.inputrecorder.Game;
import com.badlogic.demos.inputrecorder.StyleHelper;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.esotericsoftware.tablelayout.Cell;

public class SimpleRecordPlaybackScreen extends AbstractScreen {

	private final Cell playpause;

	public SimpleRecordPlaybackScreen(Game game) {
		super(game);

		TextButton playpause = new TextButton("Play", StyleHelper.getInstance()
				.getTextButtonStyle());
		this.playpause = table.add(playpause);
	}

}
