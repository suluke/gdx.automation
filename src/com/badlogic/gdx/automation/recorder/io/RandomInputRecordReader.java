package com.badlogic.gdx.automation.recorder.io;

import java.util.Iterator;

import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty.PlaceholderText;
import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty.Text;
import com.badlogic.gdx.automation.recorder.InputProperty.StaticProperties;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty;
import com.badlogic.gdx.automation.recorder.RecordProperties;

/**
 * Reader to generate random input to be fed to the application. Replaces
 * android's monkey tool.
 * 
 * This is still WIP TODO, so not public api
 * 
 * @author Lukas Böhm
 */
class RandomInputRecordReader implements InputRecordReader {

	@Override
	public Iterator<Text> getTextIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<PlaceholderText> getPlaceholderTextIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<SyncProperty> getSyncValueIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StaticProperties getStaticValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public RecordProperties getRecordProperties() {
		// TODO Auto-generated method stub
		return null;
	}

}
