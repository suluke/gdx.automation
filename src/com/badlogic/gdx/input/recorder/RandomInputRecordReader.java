package com.badlogic.gdx.input.recorder;

import java.util.Iterator;

import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.Text;

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

}
