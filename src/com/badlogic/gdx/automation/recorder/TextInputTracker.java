package com.badlogic.gdx.automation.recorder;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue.Text;

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
		if (isTracking()) {
			Gdx.app.log(InputRecorder.LOG_TAG,
					"Starting TextInputTracker more than once");
			return;
		}
		synchronized (Gdx.input) {
			setProxiedInput(getTrackedInput());
			Gdx.input = this;
		}
		running = true;
	}

	public synchronized void stopTracking() {
		if (!isTracking()) {
			Gdx.app.log(InputRecorder.LOG_TAG,
					"Stopping TextInputTracker more than once");
			return;
		}
		if (!InputProxy.removeProxyFromGdx(this)) {
			Gdx.app.log(InputRecorder.LOG_TAG,
					"Cannot unregister TextInputTracker");
			throw new IllegalStateException();
		}
		running = false;
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
		try {
			recorder.getRecordWriter().writeAsyncValues(new Text(text));
		} catch (IOException e) {
			recorder.notifyError(e);
		}
	}

	private void canceledText() {
		try {
			recorder.getRecordWriter().writeAsyncValues(new Text(null));
		} catch (IOException e) {
			// TODO the error handling should get some reconsideration...
			e.printStackTrace();
			recorder.notifyError(e);
		}
	}

	private void inputPlaceholderText(String text) {
		try {
			recorder.getRecordWriter().writeAsyncValues(
					new PlaceholderText(text));
		} catch (IOException e) {
			recorder.notifyError(e);
		}
	}

	private void canceledPlaceholderText() {
		try {
			recorder.getRecordWriter().writeAsyncValues(
					new PlaceholderText(null));
		} catch (IOException e) {
			recorder.notifyError(e);
		}
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
