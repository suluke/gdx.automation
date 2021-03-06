package com.badlogic.gdx.automation.recorder;

import java.util.Iterator;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.automation.recorder.EventBufferAccessHelper.KeyEvent;
import com.badlogic.gdx.automation.recorder.EventBufferAccessHelper.PointerEvent;
import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty.PlaceholderText;
import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty.Text;
import com.badlogic.gdx.automation.recorder.InputProperty.StaticProperties;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty;

/**
 * A simple implementation of the {@link Input} interface, designed to simply
 * play back what is stored in an enclosed {@link InputState}
 * 
 * @author Lukas Böhm
 */
class PlaybackInput extends InputProxy {

	private final Iterator<Text> textIterator;
	private final Iterator<PlaceholderText> placeholderTextIterator;
	private final StaticProperties features;

	private InputProcessor processor = null;
	private final InputState state;
	private long currentEventTimeStamp;

	public PlaybackInput(Iterator<Text> textIterator,
			Iterator<PlaceholderText> placeholderTextIterator,
			StaticProperties features) {
		state = new InputState(20);
		this.textIterator = textIterator;
		this.placeholderTextIterator = placeholderTextIterator;
		this.features = features;
	}

	InputState getState() {
		return state;
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
		switch (peripheral) {
		case Accelerometer:
			return features.accelerometerAvailable;
		case Compass:
			return features.compassAvailable;
		case HardwareKeyboard:
			return features.keyboardAvailable;
		case MultitouchScreen:
			return features.hasMultitouch;
		case OnscreenKeyboard:
			return features.onscreenKeyboard;
		case Vibrator:
			return features.vibrator;
		default:
			return false;
		}
	}

	@Override
	public Orientation getNativeOrientation() {
		return features.nativeOrientation;
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
		return currentEventTimeStamp;
	}

	@Override
	public int getRotation() {
		return state.orientation;
	}

	@Override
	public void getTextInput(TextInputListener listener, String title,
			String text) {
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
	}

	@Override
	public void getPlaceholderTextInput(TextInputListener listener,
			String title, String placeholder) {
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
	}

	/**
	 * Code mainly stolen from AndroidInput and LwjglInput, adapted to work with
	 * the remaining recorder code.
	 */
	void processEvents() {
		synchronized (state) {
			state.justTouched = false;

			if (processor != null) {
				final InputProcessor processor = this.processor;

				int len = state.keyEvents.size();
				for (int i = 0; i < len; i++) {
					KeyEvent e = state.keyEvents.get(i);
					currentEventTimeStamp = e.timeStamp;
					switch (e.type) {
					case KEY_DOWN: {
						processor.keyDown(e.keyCode);
						break;
					}
					case KEY_UP: {
						processor.keyUp(e.keyCode);
						break;
					}
					case KEY_TYPED: {
						processor.keyTyped(e.keyChar);
						break;
					}
					}
				}

				len = state.pointerEvents.size();
				for (int i = 0; i < len; i++) {
					PointerEvent e = state.pointerEvents.get(i);
					currentEventTimeStamp = e.timeStamp;
					switch (e.type) {
					case TOUCH_DOWN: {
						processor.touchDown(e.x, e.y, e.pointer, e.button);
						break;
					}
					case TOUCH_UP: {
						processor.touchUp(e.x, e.y, e.pointer, e.button);
						break;
					}
					case TOUCH_DRAGGED: {
						processor.touchDragged(e.x, e.y, e.pointer);
						break;
					}
					case TOUCH_MOVED: {
						processor.mouseMoved(e.x, e.y);
						break;
					}
					case TOUCH_SCROLLED: {
						processor.scrolled(e.scrollAmount);
						break;
					}
					}
				}
			} else {
				int len = state.pointerEvents.size();
				for (int i = 0; i < len; i++) {
					PointerEvent e = state.pointerEvents.get(i);
					if (e.type == SyncProperty.PointerEvent.Type.TOUCH_DOWN) {
						state.justTouched = true;
					}
				}
			}

			if (state.pointerEvents.size() == 0) {
				for (int i = 0; i < state.deltaX.length; i++) {
					state.deltaX[0] = 0;
					state.deltaY[0] = 0;
				}
			}

			state.keyEvents.clear();
			state.pointerEvents.clear();
		}
	}
}
