package com.badlogic.gdx.input.recorder;

import java.util.Iterator;

import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.Text;

/**
 * Simple implementation of a {@link InputRecordReader} reading input data from
 * a json formatted file, most likely generated using
 * {@link JsonInputRecordWriter}.
 * 
 */
public class JsonInputRecordReader implements InputRecordReader {

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
