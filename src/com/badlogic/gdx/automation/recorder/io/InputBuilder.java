package com.badlogic.gdx.automation.recorder.io;


/**
 * Class to programmatically create high-level input sequences which can be
 * played back later by obtaining an {@link InputRecordReader}. The API is
 * designed to feel builder-style.
 * 
 * This is still WIP TODO, so not public api
 * 
 * @author Lukas Böhm
 * 
 */
class InputBuilder {

	private final MemoryInputRecordWriter writer;
	private int nextDelta = 0;

	private final float eventsPerSecond = 30;

	public InputBuilder() {
		writer = new MemoryInputRecordWriter();
	}

	/**
	 * Adds a timeout to the input sequence
	 * 
	 * @param seconds
	 *            seconds to delay the following input event
	 */
	public void timeout(float seconds) {
		nextDelta = (int) (seconds * 1000);
	}

	/**
	 * 
	 * @param pointer
	 *            the index of the pointer that drags
	 * @param startX
	 *            the x coordinate of the initial drag point
	 * @param startY
	 *            the y coordinate of the initial drag point
	 * @param endX
	 *            the x coordinate of the final drag point
	 * @param endY
	 *            the y coordinate of the final drag point
	 * @param seconds
	 *            the amount of time that passes between drag start and drag
	 *            stop
	 */
	public void drag(int pointer, int startX, int startY, int endX, int endY,
			float seconds) {

	}

	public InputRecordReader getReader() {
		return writer.getReader();
	}
}
