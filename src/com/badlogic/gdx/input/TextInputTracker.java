package com.badlogic.gdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * 
 *
 */
class TextInputTracker extends InputProxy {

	private boolean running = false;

	public TextInputTracker(InputRecorder inputTracker) {
		super();
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
		if (Gdx.input == this) {
			Gdx.input = getProxiedInput();
		} else {
			Gdx.app.log(InputRecorder.LOG_TAG,
					"Cannot unregister TextInputTracker");
		}
		running = false;
	}

	@Override
	public void getTextInput(TextInputListener listener, String title,
			String text) {
		super.getTextInput(new TextInputListenerProxy(listener), title, text);
	}

	@Override
	public void getPlaceholderTextInput(TextInputListener listener,
			String title, String placeholder) {
		super.getPlaceholderTextInput(new TextInputListenerProxy(listener),
				title, placeholder);
	}

	private void input(String text) {
		// TODO Auto-generated method stub
		// Implement

	}

	private void canceled() {
		// TODO Auto-generated method stub

	}

	private class TextInputListenerProxy implements TextInputListener {

		private final TextInputListener listener;

		public TextInputListenerProxy(TextInputListener proxied) {
			listener = proxied;
		}

		@Override
		public void input(String text) {
			TextInputTracker.this.input(text);
			listener.input(text);
		}

		@Override
		public void canceled() {
			TextInputTracker.this.canceled();
			listener.canceled();
		}

	}

	public boolean isTracking() {
		return running;
	}
}
