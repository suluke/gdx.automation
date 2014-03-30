package com.badlogic.demos.automation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

public class InputVisualizer extends Actor {
	private static final int MAX_EVENTS = 30;
	private static final int MAX_EXTENT = 50;

	private final ShaderProgram shader;
	private final Mesh mesh;
	private final int mouseEventShaderLoc;
	private final float[] mouseEvents = new float[3 * MAX_EVENTS];
	private final RingBuffer<MouseEvent> mouseEventBuffer;
	private final Pool<MouseEvent> eventPool;

	public InputVisualizer() {
		mouseEventBuffer = new RingBuffer<InputVisualizer.MouseEvent>(
				MAX_EVENTS);
		eventPool = new ReflectionPool<MouseEvent>(MouseEvent.class);

		shader = new ShaderProgram(
				Gdx.files.internal("assets/shaders/visualizer.vert"),
				Gdx.files.internal("assets/shaders/visualizer.frag"));
		if (!shader.isCompiled()) {
			Gdx.app.log("InputVisualizer", shader.getLog());
		}
		mouseEventShaderLoc = shader.getUniformLocation("u_mouseEvs[0]");
		if (mouseEventShaderLoc == -1) {
			Gdx.app.log("InputVisualizer",
					"No uniform with name u_mouseEvents found in shader");
		}
		mesh = new Mesh(false, 4, 4, VertexAttribute.Position());

		addListener(new MouseListener());
	}

	@Override
	protected void sizeChanged() {
		float[] vertices = new float[12];
		Vector2 pos = new Vector2(0, 0);
		pos = localToStageCoordinates(pos);
		vertices[0] = pos.x;
		vertices[1] = pos.y;
		pos.set(0, getHeight());
		pos = localToStageCoordinates(pos);
		vertices[3] = pos.x;
		vertices[4] = pos.y;
		pos.set(getWidth(), 0);
		pos = localToStageCoordinates(pos);
		vertices[6] = pos.x;
		vertices[7] = pos.y;
		pos.set(getWidth(), getHeight());
		pos = localToStageCoordinates(pos);
		vertices[9] = pos.x;
		vertices[10] = pos.y;

		mesh.setVertices(vertices);
		mesh.setIndices(new short[] { 0, 1, 2, 3 });
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.end();
		fillMouseEvents();
		shader.begin();
		shader.setUniform3fv(mouseEventShaderLoc, mouseEvents, 0, 30);
		shader.setUniformMatrix("u_projTrans", batch.getProjectionMatrix());
		mesh.render(shader, GL10.GL_TRIANGLE_STRIP);
		shader.end();
		batch.begin();
	}

	private void fillMouseEvents() {
		synchronized (mouseEventBuffer) {
			int i = 0;
			for (MouseEvent ev : mouseEventBuffer) {
				mouseEvents[i] = ev.x;
				mouseEvents[i + 1] = ev.y;
				mouseEvents[i + 2] = ev.extent;
				i += 3;
			}
		}
	}

	private class MouseListener implements EventListener {
		Vector2 v = new Vector2();

		@Override
		public boolean handle(Event event) {
			if (event instanceof InputEvent) {
				InputEvent ev = (InputEvent) event;
				if (ev.getType() == Type.mouseMoved) {
					MouseEvent mevent = eventPool.obtain();
					v.x = ev.getStageX();
					v.y = ev.getStageY();
					v = InputVisualizer.this.getStage()
							.stageToScreenCoordinates(v);
					mevent.x = v.x;
					mevent.y = Gdx.graphics.getHeight() - v.y;
					mevent.extent = 1;
					MouseEvent overwritten;
					synchronized (mouseEventBuffer) {
						overwritten = mouseEventBuffer.push(mevent);
					}
					if (overwritten != null) {
						eventPool.free(overwritten);
					}
				}
			}
			return false;
		}
	}

	private static class MouseEvent {
		public float x;
		public float y;
		/**
		 * Amount of pixels the event wave has spread
		 */
		public float extent;
	}
}
