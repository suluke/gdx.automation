package com.badlogic.gdx.input.recorder;

import java.util.Iterator;

public interface InputRecordReader {
	Iterator<InputValue.AsyncValue.Text> getTextIterator();

	Iterator<InputValue.AsyncValue.PlaceholderText> getPlaceholderTextIterator();
}
