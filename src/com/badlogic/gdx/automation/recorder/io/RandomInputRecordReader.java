package com.badlogic.gdx.automation.recorder.io;

import java.util.Iterator;

import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue.Text;
import com.badlogic.gdx.automation.recorder.InputValue.StaticValues;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue;

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
	public Iterator<SyncValue> getSyncValueIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StaticValues getStaticValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
