package com.badlogic.gdx.automation.recorder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Pixmap;

abstract class InputProxy implements Input {

	protected Input input;

	public InputProxy(Input proxied) {
		testNullProxied(proxied);
		this.input = proxied;
	}

	InputProxy() {

	}

	public void setProxiedInput(Input proxied) {
		testNullProxied(proxied);
		this.input = proxied;
	}

	public Input getProxiedInput() {
		return input;
	}

	private void testNullProxied(Input proxied) {
		if (proxied == null) {
			throw new IllegalArgumentException("Cannot proxy null as Input");
		}
	}

	@Override
	public float getAccelerometerX() {
		return input.getAccelerometerX();
	}

	@Override
	public float getAccelerometerY() {
		return input.getAccelerometerY();
	}

	@Override
	public float getAccelerometerZ() {
		return input.getAccelerometerZ();
	}

	@Override
	public int getX() {
		return input.getX();
	}

	@Override
	public int getX(int pointer) {
		return input.getX(pointer);
	}

	@Override
	public int getDeltaX() {
		return input.getDeltaX();
	}

	@Override
	public int getDeltaX(int pointer) {
		return input.getDeltaX(pointer);
	}

	@Override
	public int getY() {
		return input.getY();
	}

	@Override
	public int getY(int pointer) {
		return input.getY(pointer);
	}

	@Override
	public int getDeltaY() {
		return input.getDeltaY();
	}

	@Override
	public int getDeltaY(int pointer) {
		return input.getDeltaY(pointer);
	}

	@Override
	public boolean isTouched() {
		return input.isTouched();
	}

	@Override
	public boolean justTouched() {
		return input.justTouched();
	}

	@Override
	public boolean isTouched(int pointer) {
		return input.isTouched(pointer);
	}

	@Override
	public boolean isButtonPressed(int button) {
		return input.isButtonPressed(button);
	}

	@Override
	public boolean isKeyPressed(int key) {
		return input.isKeyPressed(key);
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
		input.setOnscreenKeyboardVisible(visible);
	}

	@Override
	public void vibrate(int milliseconds) {
		input.vibrate(milliseconds);
	}

	@Override
	public void vibrate(long[] pattern, int repeat) {
		input.vibrate(pattern, repeat);
	}

	@Override
	public void cancelVibrate() {
		input.cancelVibrate();
	}

	@Override
	public float getAzimuth() {
		return input.getAzimuth();
	}

	@Override
	public float getPitch() {
		return input.getPitch();
	}

	@Override
	public float getRoll() {
		return input.getRoll();
	}

	@Override
	public void getRotationMatrix(float[] matrix) {
		input.getRotationMatrix(matrix);
	}

	@Override
	public long getCurrentEventTime() {
		return input.getCurrentEventTime();
	}

	@Override
	public void setCatchBackKey(boolean catchBack) {
		input.setCatchBackKey(catchBack);
	}

	@Override
	public void setCatchMenuKey(boolean catchMenu) {
		input.setCatchMenuKey(catchMenu);
	}

	@Override
	public void setInputProcessor(InputProcessor processor) {
		input.setInputProcessor(processor);
	}

	@Override
	public InputProcessor getInputProcessor() {
		return input.getInputProcessor();
	}

	@Override
	public boolean isPeripheralAvailable(Peripheral peripheral) {
		return input.isPeripheralAvailable(peripheral);
	}

	@Override
	public int getRotation() {
		return input.getRotation();
	}

	@Override
	public Orientation getNativeOrientation() {
		return input.getNativeOrientation();
	}

	@Override
	public void setCursorCatched(boolean catched) {
		input.setCursorCatched(catched);
	}

	@Override
	public boolean isCursorCatched() {
		return input.isCursorCatched();
	}

	@Override
	public void setCursorPosition(int x, int y) {
		input.setCursorPosition(x, y);
	}

	@Override
	public void setCursorImage(Pixmap pixmap, int xHotspot, int yHotspot) {
		input.setCursorImage(pixmap, xHotspot, yHotspot);
	}

	/**
	 * Goes down the hierarchy of InputProxies, starting at Gdx.input and
	 * removes the given InputProxy, if it exists. Returns if the given
	 * {@link InputProxy} was found and removed.
	 * 
	 * @param proxy
	 *            the proxy to be found and removed
	 * @return true if the specified proxy was removed, false otherwise
	 */
	public static boolean removeProxyFromGdx(InputProxy proxy) {
		if (Gdx.input == null) {
			return false;
		}
		if (Gdx.input.equals(proxy)) {
			synchronized (Gdx.input) {
				Gdx.input = proxy.getProxiedInput();
			}
			return true;
		}
		Input current = Gdx.input;
		InputProxy asProxy;
		while (current != null && current instanceof InputProxy) {
			asProxy = (InputProxy) current;
			if (asProxy.getProxiedInput() == proxy) {
				asProxy.setProxiedInput(proxy.getProxiedInput());
				return true;
			}
			current = asProxy.getProxiedInput();
		}
		return false;
	}
}
