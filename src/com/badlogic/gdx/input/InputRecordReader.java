package com.badlogic.gdx.input;

import java.util.Stack;

import com.badlogic.gdx.Input;

public interface InputRecordReader {
	/**
	 * Returns a Stack of Strings that were received by the recorded application
	 * where the topmost element is the first answer to a
	 * {@link Input#getTextInput getTextInput} call during recording.
	 * 
	 * @return
	 */
	public Stack<String> getTextInputStack();

	/**
	 * Returns a Stack of Strings that were received by the recorded application
	 * where the topmost element is the first answer to a
	 * {@link Input#getPlaceholderTextInput getPlaceholderTextInput} call during
	 * recording.
	 * 
	 * @return
	 */
	public Stack<String> getPlaceholderTextInputStack();
}
