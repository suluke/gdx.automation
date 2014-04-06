package com.badlogic.gdx.automation.recorder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.automation.recorder.formats.InputRecordReader;

/**
 * An extension of {@link InputPlayer} that registers itself at the original
 * {@link Gdx#input} as an {@link InputProcessor} to listen for certain events
 * that can be set programmatially. Those events will then be multiplexed with
 * the recorded input. This enables, for example, to use the
 * {@link MultiplexedInputPlayer} to control a game's main actor in a tutorial
 * while at the same time the user can press a runtime triggerable button to
 * skip it.
 * 
 * This is WIP TODO, so not public api
 * 
 * @author Lukas Böhm
 * 
 */
class MultiplexedInputPlayer extends InputPlayer {

	public MultiplexedInputPlayer(InputRecordReader reader) {
		super(reader);
	}

}
