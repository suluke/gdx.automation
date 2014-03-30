package com.badlogic.demos.automation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Abstract screen, with all the basic things a screen needs.
 */
public abstract class AbstractScreen implements Screen {

	protected Game game;
	protected final Stage stage;
	protected final Table table;
	private Texture background;
	private String backgroundPath;
	private final OrthographicCamera camera;

	/**
	 * Super constructor for all screens. Initializes everything they share,
	 * e.g. their stage.
	 * 
	 * @param game
	 *            the back reference to the central game
	 */
	public AbstractScreen(Game game) {
		this.game = game;
		stage = new Stage(1024, 600, true, game.batch);

		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1024, 600);
		stage.setCamera(camera);
	}

	/**
	 * Returns the sceen's camera's viewport width, i.e. the number of virtual
	 * pixels that actors drawn onto this screen will assume the screen has
	 * horizontally.
	 * 
	 * @return the viewport width
	 */
	public float getViewportWidth() {
		return camera.viewportWidth;
	}

	/**
	 * Returns the sceen's camera's viewport height, i.e. the number of virtual
	 * pixels that actors drawn onto this screen will assume the screen has
	 * vertically.
	 * 
	 * @return the viewport height
	 */
	public float getViewportHeight() {
		return camera.viewportHeight;
	}

	/**
	 * Called when the screen should render itself.
	 */
	@Override
	public void render(float delta) {
		Gdx.gl.glColorMask(true, true, true, true);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		game.batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the background
		if (backgroundPath != null) {
			game.batch.begin();
			background = game.assetManager.get(backgroundPath, Texture.class);
			background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			game.batch.draw(background, 0, 0, getViewportWidth(),
					getViewportHeight());
			game.batch.end();
		}

		stage.act(delta);
		stage.draw();
		Table.drawDebug(stage);
	}

	public void setBackground(String backgroundPath) {
		try {
			FileHandle h = Gdx.files.internal(backgroundPath);
			if (!h.exists() || h.isDirectory()) {
				throw new GdxRuntimeException("Background not found");
			}
		} catch (GdxRuntimeException ex) {
			return;
		}
		AssetManager manager = game.assetManager;

		manager.load(backgroundPath, Texture.class);
		this.backgroundPath = backgroundPath;

	}

	/**
	 * Called when the application is resized. This can happen at any point
	 * during a non-paused state but will never happen before a call to
	 * create().
	 * 
	 * @param width
	 *            the width, which the newly resized screen will have.
	 * @param height
	 *            the height, which the newly resized screen will have.
	 */
	@Override
	public void resize(int width, int height) {
		stage.setViewport(1024, 600, true);
		camera.update();
	}

	/**
	 * Called in order to cause the screen to release all resources held.
	 */
	@Override
	public void dispose() {
		stage.dispose();
		background.dispose();
	}

	/**
	 * Called when this screen should no longer be the game's current screen.
	 */
	@Override
	public void hide() {

	}

	/**
	 * Called when this screen is paused. A screen is paused before it is
	 * destroyed, when the user pressed the Home button or e.g. an incoming call
	 * happens.
	 */
	@Override
	public void pause() {

	}

	/**
	 * Called in order to move the screen back from its paused state.
	 */
	@Override
	public void resume() {
	}

	/**
	 * Called when this screen should be the game's current screen. The method
	 * is final since it implements default behavior which must not be
	 * overridden. If you want to add code to be called on show, override the
	 * protected method onShow() instead.
	 */
	@Override
	public final void show() {
		// if the loading screen has initialized everything, this returns
		// instantly on its own
		game.assetManager.finishLoading();
		onShow();
		Gdx.input.setInputProcessor(stage);
	}

	/**
	 * Override this method to
	 */
	protected void onShow() {

	}
}
