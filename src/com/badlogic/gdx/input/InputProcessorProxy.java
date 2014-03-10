package com.badlogic.gdx.input;

import com.badlogic.gdx.InputProcessor;

public class InputProcessorProxy implements InputProcessor {

	private InputProcessor proxied;

	public InputProcessorProxy(InputProcessor proxied) {
		this.proxied = proxied;
	}

	public InputProcessorProxy() {

	}

	public void setProxied(InputProcessor proxied) {
		this.proxied = proxied;
	}

	public InputProcessor getProxied() {
		return proxied;
	}

	@Override
	public boolean keyDown(int keycode) {
		onEvent();
		if (proxied == null) {
			return true;
		}
		return proxied.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		onEvent();
		if (proxied == null) {
			return true;
		}
		return proxied.keyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		onEvent();
		if (proxied == null) {
			return true;
		}
		return proxied.keyTyped(character);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		onEvent();
		if (proxied == null) {
			return true;
		}
		return proxied.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		onEvent();
		if (proxied == null) {
			return true;
		}
		return proxied.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		onEvent();
		if (proxied == null) {
			return true;
		}
		return proxied.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		onEvent();
		if (proxied == null) {
			return true;
		}
		return proxied.mouseMoved(screenX, screenY);
	}

	@Override
	public boolean scrolled(int amount) {
		onEvent();
		if (proxied == null) {
			return true;
		}
		return proxied.scrolled(amount);
	}

	/**
	 * Called whenever any of the event receiver methods are called. Useful if a
	 * descendent class does not care for the exact event but rather wants to be
	 * notified whenever the {@link InputProcessor} is invoked
	 */
	protected void onEvent() {

	}

}
