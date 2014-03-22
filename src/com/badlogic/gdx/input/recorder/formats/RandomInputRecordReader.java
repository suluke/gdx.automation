package com.badlogic.gdx.input.recorder.formats;

import java.util.Iterator;

import com.badlogic.gdx.input.recorder.InputValue;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.Text;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue;
import com.badlogic.gdx.input.recorder.InputValue.StaticValues;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue;

/**
 * Reader to generate random input to be fed to the application. Replaces
 * android's monkey tool.
 * 
 */
public class RandomInputRecordReader implements InputRecordReader {

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
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
