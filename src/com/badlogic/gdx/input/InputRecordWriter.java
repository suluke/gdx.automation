package com.badlogic.gdx.input;

public interface InputRecordWriter {

	void flush();

	void writeTextInput(String input);

	void writePlaceholderTextInput(String input);
}
