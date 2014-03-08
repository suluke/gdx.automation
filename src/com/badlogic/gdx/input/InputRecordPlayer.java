package com.badlogic.gdx.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Pixmap;

abstract class InputRecordPlayer implements Input {

	public InputRecordPlayer(InputRecordReader reader) {
		// TODO
	}

	private Input input = null;
	private InputProcessor processor = null;
	private InputState state;

	public void setProxiedInput(Input input) {
		this.input = input;
	}

	@Override
	public void getTextInput(TextInputListener listener, String title,
			String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getPlaceholderTextInput(TextInputListener listener,
			String title, String placeholder) {
		// TODO Auto-generated method stub

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
		if (input != null) {
			input.setInputProcessor(processor);
		}
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

}
