package com.badlogic.demos.automation.simple;

import com.badlogic.demos.automation.AbstractScreen;
import com.badlogic.demos.automation.Game;
import com.badlogic.demos.automation.InputVisualizer;
import com.badlogic.demos.automation.StyleHelper;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.esotericsoftware.tablelayout.Cell;

public class SimpleRecordPlaybackScreen extends AbstractScreen {

	private enum State {
		/**
		 * Option to record is shown
		 */
		BEFORE_RECORD,
		/**
		 * Option to stop recording is shown
		 */
		RECORDING,
		/**
		 * Option to play back recorded input and to drop record is shown
		 */
		AFTER_RECORD,
		/**
		 * Nothing is shown. Only return to AFTER_RECORD when playback is over
		 */
		PLAYBACK
	}

	private State currentState = State.BEFORE_RECORD;

	private final TextButton record;
	private final TextButton stopRecord;
	private final TextButton playback;
	private final TextButton dropRecord;
	private final InputVisualizer visualizer;

	public SimpleRecordPlaybackScreen(Game game) {
		super(game);

		record = new TextButton("Record", new TextButtonStyle(StyleHelper
				.getInstance().getTextButtonStyle()));
		stopRecord = new TextButton("Stop", new TextButtonStyle(StyleHelper
				.getInstance().getTextButtonStyle()));
		record.addListener(new PlayPauseListener());
		playback = new TextButton("Play back", new TextButtonStyle(StyleHelper
				.getInstance().getTextButtonStyle()));
		dropRecord = new TextButton("Drop recorded", new TextButtonStyle(
				StyleHelper.getInstance().getTextButtonStyle()));

		record.getStyle().font.setScale(2);
		stopRecord.getStyle().font.setScale(2);
		playback.getStyle().font.setScale(2);
		dropRecord.getStyle().font.setScale(2);

		visualizer = new InputVisualizer();

		layout();
	}

	private void layout() {
		table.clear();
		switch (currentState) {
		case AFTER_RECORD: {
			break;
		}
		case BEFORE_RECORD: {
			Cell<?> visualizerCell = table.add(visualizer);
			visualizerCell.width(800).height(600).left().expandY();

			Cell<?> recordCell = table.add(record);
			recordCell.width(200).height(300).right().expandY();
			break;
		}
		case PLAYBACK:
			break;
		case RECORDING: {
			break;
		}
		default:
			break;

		}
	}

	private class PlayPauseListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			if (event instanceof ChangeEvent) {
				currentState = State.RECORDING;
				layout();
			}
			return false;
		}
	}

}
