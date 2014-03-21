package com.badlogic.gdx.input.recorder;

import java.util.Iterator;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.Text;

class PlaybackInput extends InputProxy {

	private final Iterator<Text> textIterator;
	private final Iterator<PlaceholderText> placeholderTextIterator;

	private InputProcessor processor = null;
	private InputState state;
	private long currentEventTime;

	private boolean paused = false;

	public PlaybackInput(Iterator<Text> textIterator,
			Iterator<PlaceholderText> placeholderTextIterator) {
		this.textIterator = textIterator;
		this.placeholderTextIterator = placeholderTextIterator;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	@Override
	public void setOnscreenKeyboardVisible(boolean visible) {
		if (input != null) {
			input.setOnscreenKeyboardVisible(visible);
		}
	}

	@Override
	public void vibrate(int milliseconds) {
		if (input != null) {
			input.vibrate(milliseconds);
		}
	}

	@Override
	public void vibrate(long[] pattern, int repeat) {
		if (input != null) {
			input.vibrate(pattern, repeat);
		}
	}

	@Override
	public void cancelVibrate() {
		if (input != null) {
			input.cancelVibrate();
		}
	}

	@Override
	public void setCatchBackKey(boolean catchBack) {
		if (input != null) {
			input.setCatchBackKey(catchBack);
		}
	}

	@Override
	public void setCatchMenuKey(boolean catchMenu) {
		if (input != null) {
			input.setCatchMenuKey(catchMenu);
		}
	}

	@Override
	public void setInputProcessor(InputProcessor processor) {
		this.processor = processor;
	}

	@Override
	public InputProcessor getInputProcessor() {
		return processor;
	}

	@Override
	public boolean isPeripheralAvailable(Peripheral peripheral) {
		if (input != null) {
			return input.isPeripheralAvailable(peripheral);
		}
		return false;
	}

	@Override
	public Orientation getNativeOrientation() {
		if (input != null) {
			return input.getNativeOrientation();
		}
		return Orientation.Landscape;
	}

	@Override
	public void setCursorCatched(boolean catched) {
		if (input != null) {
			input.setCursorCatched(catched);
		}
	}

	@Override
	public void setCursorPosition(int x, int y) {
		if (input != null) {
			input.setCursorPosition(x, y);
		}
	}

	@Override
	public void setCursorImage(Pixmap pixmap, int xHotspot, int yHotspot) {
		if (input != null) {
			input.setCursorImage(pixmap, xHotspot, yHotspot);
		}
	}

	@Override
	public boolean isCursorCatched() {
		return input.isCursorCatched();
	}

	@Override
	public float getAccelerometerX() {
		return state.accelerometerX;
	}

	@Override
	public float getAccelerometerY() {
		return state.accelerometerY;
	}

	@Override
	public float getAccelerometerZ() {
		return state.accelerometerX;
	}

	@Override
	public int getX() {
		return state.getX();
	}

	@Override
	public int getX(int pointer) {
		return state.getX(pointer);
	}

	@Override
	public int getDeltaX() {
		return state.getDeltaX();
	}

	@Override
	public int getDeltaX(int pointer) {
		return state.getDeltaX(pointer);
	}

	@Override
	public int getY() {
		return state.getY();
	}

	@Override
	public int getY(int pointer) {
		return state.getY(pointer);
	}

	@Override
	public int getDeltaY() {
		return state.getDeltaY();
	}

	@Override
	public int getDeltaY(int pointer) {
		return state.getDeltaY(pointer);
	}

	@Override
	public boolean isTouched() {
		return state.isTouched();
	}

	@Override
	public boolean justTouched() {
		return state.justTouched();
	}

	@Override
	public boolean isTouched(int pointer) {
		return state.isTouched(pointer);
	}

	@Override
	public boolean isButtonPressed(int button) {
		return state.isButtonPressed(button);
	}

	@Override
	public boolean isKeyPressed(int key) {
		return state.isKeyPressed(key);
	}

	@Override
	public float getAzimuth() {
		return state.azimuth;
	}

	@Override
	public float getPitch() {
		return state.pitch;
	}

	@Override
	public float getRoll() {
		return state.roll;
	}

	@Override
	public void getRotationMatrix(float[] matrix) {
		if (matrix.length >= 9 && matrix.length < 16) {
			System.arraycopy(state.rotationMatrix, 0, matrix, 0, 3);
			System.arraycopy(state.rotationMatrix, 4, matrix, 3, 3);
			System.arraycopy(state.rotationMatrix, 8, matrix, 6, 3);
		} else if (matrix.length >= 16) {
			System.arraycopy(state.rotationMatrix, 0, matrix, 0, 16);
		}
	}

	@Override
	public long getCurrentEventTime() {
		return currentEventTime;
	}

	@Override
	public int getRotation() {
		return state.orientation;
	}

	@Override
	public void getTextInput(TextInputListener listener, String title,
			String text) {
		if (!paused) {
			if (textIterator.hasNext()) {
				String answer = textIterator.next().input;
				if (answer == null) {
					listener.canceled();
				} else {
					listener.input(answer);
				}
			} else {
				listener.canceled();
			}
		} else {
			// TODO pause with delaying request answers or delegate to proxied
			// input or just cancel?
			listener.canceled();
		}
	}

	@Override
	public void getPlaceholderTextInput(TextInputListener listener,
			String title, String placeholder) {
		if (!paused) {
			if (placeholderTextIterator.hasNext()) {
				String answer = placeholderTextIterator.next().input;
				if (answer == null) {
					listener.canceled();
				} else {
					listener.input(answer);
				}
			} else {
				listener.canceled();
			}
		} else {
			// TODO pause with delaying request answers or delegate to proxied
			// input or just cancel?
			listener.canceled();
		}
	}
}
