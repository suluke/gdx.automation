package com.badlogic.demos.inputrecorder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Singleton to manage Styles.
 **/
public class StyleHelper {

	private static StyleHelper instance;
	private Skin skin;
	private TextureAtlas atlas;
	private final com.badlogic.gdx.assets.AssetManager manager;

	private StyleHelper(AssetManager manager) {
		this.manager = manager;
		manager.load("assets/uiskin.atlas", TextureAtlas.class);
	}

	/**
	 * Gets the current instance.
	 * 
	 * @return the current instance
	 */
	public static StyleHelper getInstance() {
		if (instance == null) {
			throw new IllegalStateException(
					"Stylehelper must be initialized before first usage");
		}
		if (instance.skin == null) {
			instance.manager.finishLoading();
			instance.atlas = instance.manager.get("assets/uiskin.atlas",
					TextureAtlas.class);
			instance.skin = new Skin(Gdx.files.internal("assets/uiskin.json"),
					instance.atlas);
		}
		return instance;
	}

	/**
	 * Creates a new stylehelper that will from now on be returned by
	 * getInstance
	 */
	public static void initialize(AssetManager manager) {
		instance = new StyleHelper(manager);
	}

	/**
	 * Gets the used skin.
	 * 
	 * @return the skin
	 */
	public Skin getSkin() {
		return skin;
	}

	public void dispose() {
		if (skin != null) {
			skin.dispose();
		}
	}

	/**
	 * Gets the default button style.
	 * 
	 * @return
	 */
	public ButtonStyle getButtonStyle() {
		return skin.get(ButtonStyle.class);
	}

	/**
	 * Gets the style of the text button.
	 * 
	 * @return the text button's style
	 */
	public TextButtonStyle getTextButtonStyle() {
		return skin.get(TextButtonStyle.class);
	}

	/**
	 * Gets the style of the image button.
	 * 
	 * @return the image button's style
	 */
	public ImageButtonStyle getImageButtonStyle() {
		return skin.get(ImageButtonStyle.class);
	}

	/**
	 * Returns the style for an image button with the given icon as image.
	 * 
	 * @param icon
	 *            the identifier of the icon in the texture atlas
	 * @return
	 */
	public ImageButtonStyle getImageButtonStyle(String icon) {
		ImageButtonStyle style = new ImageButtonStyle(getImageButtonStyle());
		style.imageUp = skin.getDrawable(icon);
		return style;
	}

	/**
	 * Gets the style of the image text button.
	 * 
	 * @return the image text button's style
	 */
	public ImageTextButtonStyle getImageTextButtonStyle() {
		return skin.get(ImageTextButtonStyle.class);
	}

	public ImageTextButtonStyle getImageTextButtonStyle(String icon) {
		ImageTextButtonStyle style = new ImageTextButtonStyle(
				getImageTextButtonStyle());
		style.imageUp = skin.getDrawable(icon);
		return style;
	}

	public LabelStyle getLabelStyle() {
		return skin.get(LabelStyle.class);
	}

	public CheckBoxStyle getCheckBoxStyle() {
		return skin.get(CheckBoxStyle.class);
	}

	public SliderStyle getSliderStyle() {
		return skin.get(SliderStyle.class);
	}

	public TextFieldStyle getTextFieldStyle() {
		return skin.get(TextFieldStyle.class);
	}

	public SelectBoxStyle getSelectBoxStyle() {
		return skin.get(SelectBoxStyle.class);
	}

	public WindowStyle getWindowStyle() {
		return skin.get(WindowStyle.class);
	}

	public Drawable getDrawable(String path) {
		return skin.getDrawable(path);
	}

	public BitmapFont getFont() {
		return skin.getFont("default-font");
	}
}
