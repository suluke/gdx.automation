package com.badlogic.gdx.automation.recorder;

/**
 * Class to encapsulate additional information about a record to be able to play
 * it back later.
 * 
 * @author Lukas Böhm
 * 
 */
public class RecordProperties {
	public boolean absouluteCoords;

	public void set(RecordProperties properties) {
		absouluteCoords = properties.absouluteCoords;
	}
}
