package com.badlogic.gdx.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class InputProcessorProxyTest {
	private InputProcessor testProcessor;

	@Before
	public void setup() {
		testProcessor = new InputProcessor() {
			@Override
			public boolean keyDown(int keycode) {
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer,
					int button) {
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer,
					int button) {
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				return false;
			}
		};
	}

	@Test
	public void testInputProcessorProxyInputProcessor() {
		InputProcessorProxy proxy = new InputProcessorProxy(testProcessor) {
		};
		assertEquals(testProcessor, proxy.getProxied());
	}

	@Test
	public void testGetSetProxied() {
		InputProcessorProxy proxy = new InputProcessorProxy() {
		};
		assertNull(proxy.getProxied());
		proxy.setProxied(testProcessor);

	}

	@Test
	public void testRemoveProxyFromGdxInput() {
		Gdx.input = new InputProxy() {
			private InputProcessor processor;

			@Override
			public void setInputProcessor(InputProcessor processor) {
				this.processor = processor;
			}

			@Override
			public InputProcessor getInputProcessor() {
				return processor;
			}
		};
		InputProcessorProxy proxy = new InputProcessorProxy(testProcessor) {
		};
		Gdx.input.setInputProcessor(proxy);
		assertTrue(InputProcessorProxy.removeProxyFromGdxInput(proxy));
		assertEquals(testProcessor, Gdx.input.getInputProcessor());
	}

}
