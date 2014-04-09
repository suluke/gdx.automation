package com.badlogic.gdx.automation.recorder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.automation.recorder.io.InputRecordReader;

/**
 * An extension of {@link InputPlayer} that registers itself at the original
 * {@link Gdx#input} as an {@link InputProcessor} to listen for certain events
 * that can be set programmatically. Those events will then be combined with the
 * recorded input. This enables, for example, to use the {@link InputCombinator}
 * to control a game's main actor in a tutorial while at the same time the user
 * can press a button to skip it.
 * 
 * This is still WIP TODO, so not public api
 * 
 * @author Lukas Böhm
 * 
 */
class InputCombinator extends InputPlayer {

	public InputCombinator(InputRecordReader reader) {
		super(reader);
	}

}
