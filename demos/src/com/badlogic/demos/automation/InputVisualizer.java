package com.badlogic.demos.automation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class InputVisualizer extends Actor {
	private final ShaderProgram shader;
	private final Mesh mesh;
	private final int mouseEventShaderLoc;
	private final float[] mouseEvents = new float[3 * 30];

	public InputVisualizer() {
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
		Gdx.gl.glDepthMask(false);
		shader.begin();
		shader.setUniform3fv(mouseEventShaderLoc, mouseEvents, 0, 30);
		shader.setUniformMatrix("u_projTrans", batch.getProjectionMatrix());
		// Vector3 x = new Vector3(1024, 1, 1);
		// System.out.println(x.mul(getStage().getCamera().combined));
		mesh.render(shader, GL10.GL_TRIANGLE_STRIP);
		shader.end();
		batch.begin();
	}
}
