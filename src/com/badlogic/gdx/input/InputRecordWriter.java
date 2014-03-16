package com.badlogic.gdx.input;

import com.badlogic.gdx.Input;

public interface InputRecordWriter {

	/**
	 * Writes all input changes that were not yet persisted back to the
	 * underlying datastructure.
	 */
	void flush();

	/**
	 * Adds a response to a
	 * {@link Input#getTextInput(com.badlogic.gdx.Input.TextInputListener, String, String)
	 * getTextInput} request to the response queue. If the given argument is
	 * null, the request was canceled, which will also be recorded.
	 * 
	 * @param input
	 */
	void writeTextInput(String input);

	/**
	 * Adds a response to a
	 * {@link Input#getPlaceholderTextInput(com.badlogic.gdx.Input.TextInputListener, String, String)
	 * getTextInput} request to the response queue. If the given argument is
	 * null, the request was canceled, which will also be recorded.
	 * 
	 * @param input
	 */
	void writePlaceholderTextInput(String input);
}
