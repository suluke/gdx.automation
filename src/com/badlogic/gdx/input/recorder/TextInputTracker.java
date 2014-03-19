package com.badlogic.gdx.input.recorder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.Text;

/**
 * 
 *
 */
class TextInputTracker extends InputProxy {

	private boolean running = false;
	private final InputRecorder recorder;

	public TextInputTracker(InputRecorder recorder) {
		super();
		this.recorder = recorder;
	}

	public synchronized void startTracking() {
		setProxiedInput(getTrackedInput());
		Gdx.input = this;
		running = true;
	}

	private Input getTrackedInput() {
		if (Gdx.input == null) {
			throw new IllegalStateException(
					"Cannot track input without an instance of Input");
		}
		if (Gdx.input == this) {
			// nothing to do
			return getProxiedInput();
		} else if (Gdx.input instanceof TextInputTracker) {
			Gdx.app.log(InputRecorder.LOG_TAG,
					"Warning: Multiple instances of TextInputTracker occurring simultaneously");
			return ((TextInputTracker) Gdx.input).getProxiedInput();
		} else {
			return Gdx.input;
		}
	}

	public synchronized void stopTracking() {
		if (!InputProxy.removeProxyFromGdx(this)) {
			Gdx.app.log(InputRecorder.LOG_TAG,
					"Cannot unregister TextInputTracker");
		}
		running = false;
	}

	@Override
	public void getTextInput(TextInputListener listener, String title,
			String text) {
		super.getTextInput(new TextInputListenerProxy(listener, TextType.TEXT),
				title, text);
	}

	@Override
	public void getPlaceholderTextInput(TextInputListener listener,
			String title, String placeholder) {
		super.getPlaceholderTextInput(new TextInputListenerProxy(listener,
				TextType.PLACEHOLDER_TEXT), title, placeholder);
	}

	private void inputText(String text) {
		recorder.getRecordWriter().writeAsyncValues(new Text(text));
	}

	private void canceledText() {
		recorder.getRecordWriter().writeAsyncValues(new Text(null));
	}

	private void inputPlaceholderText(String text) {
		recorder.getRecordWriter().writeAsyncValues(new PlaceholderText(text));
	}

	private void canceledPlaceholderText() {
		recorder.getRecordWriter().writeAsyncValues(new PlaceholderText(null));
	}

	private enum TextType {
		TEXT, PLACEHOLDER_TEXT
	}

	private class TextInputListenerProxy implements TextInputListener {
		private final TextInputListener listener;
		private final TextType type;

		public TextInputListenerProxy(TextInputListener proxied, TextType type) {
			listener = proxied;
			this.type = type;
		}

		@Override
		public void input(String text) {
			if (type == TextType.TEXT) {
				TextInputTracker.this.inputText(text);
			} else {
				TextInputTracker.this.inputPlaceholderText(text);
			}
			listener.input(text);
		}

		@Override
		public void canceled() {
			if (type == TextType.TEXT) {
				TextInputTracker.this.canceledText();
			} else {
				TextInputTracker.this.canceledPlaceholderText();
			}
			listener.canceled();
		}

	}

	public boolean isTracking() {
		return running;
	}
}
