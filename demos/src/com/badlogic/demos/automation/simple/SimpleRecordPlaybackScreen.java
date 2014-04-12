package com.badlogic.demos.automation.simple;

import java.io.IOException;

import com.badlogic.demos.automation.AbstractScreen;
import com.badlogic.demos.automation.Game;
import com.badlogic.demos.automation.InputVisualizer;
import com.badlogic.demos.automation.StyleHelper;
import com.badlogic.gdx.automation.recorder.InputPlayer;
import com.badlogic.gdx.automation.recorder.InputRecorder;
import com.badlogic.gdx.automation.recorder.InputRecorderConfiguration;
import com.badlogic.gdx.automation.recorder.PlaybackAdapter;
import com.badlogic.gdx.automation.recorder.io.MemoryInputRecordWriter;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.esotericsoftware.tablelayout.Cell;

public class SimpleRecordPlaybackScreen extends AbstractScreen {

	private final InputRecorder recorder;
	private final MemoryInputRecordWriter writer;

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
		InputRecorderConfiguration config = new InputRecorderConfiguration();
		writer = new MemoryInputRecordWriter();
		config.writer = writer;
		recorder = new InputRecorder(config);

		record = new TextButton("Record", new TextButtonStyle(StyleHelper
				.getInstance().getTextButtonStyle()));
		record.addListener(new StartRecordListener());

		stopRecord = new TextButton("Stop", new TextButtonStyle(StyleHelper
				.getInstance().getTextButtonStyle()));
		stopRecord.addListener(new StopRecordListener());

		playback = new TextButton("Play back", new TextButtonStyle(StyleHelper
				.getInstance().getTextButtonStyle()));
		playback.addListener(new StartPlaybackListener());

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
			Cell<?> visualizerCell = table.add(visualizer);
			visualizerCell.width(800).height(600).left().expandY();

			Cell<?> playCell = table.add(playback);
			playCell.width(200).height(300).right().expandY();
			break;
		}
		case BEFORE_RECORD: {
			Cell<?> visualizerCell = table.add(visualizer);
			visualizerCell.width(800).height(600).left().expandY();

			Cell<?> recordCell = table.add(record);
			recordCell.width(200).height(300).right().expandY();
			break;
		}
		case PLAYBACK: {
			Cell<?> visualizerCell = table.add(visualizer);
			visualizerCell.width(800).height(600).left().expandY();
			break;
		}
		case RECORDING: {
			Cell<?> visualizerCell = table.add(visualizer);
			visualizerCell.width(800).height(600).left().expandY();

			Cell<?> stopCell = table.add(stopRecord);
			stopCell.width(200).height(300).right().expandY();
			break;
		}
		default:
			throw new IllegalStateException();
		}
	}

	private class StartRecordListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			if (event instanceof ChangeEvent) {
				currentState = State.RECORDING;
				layout();
				try {
					recorder.startRecording();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
	}

	private class StopRecordListener implements EventListener {
		@Override
		public boolean handle(Event event) {
			if (event instanceof ChangeEvent) {
				currentState = State.AFTER_RECORD;
				try {
					recorder.stopRecording();
				} catch (IOException e) {
					e.printStackTrace();
				}
				layout();
			}
			return true;
		}
	}

	private class StartPlaybackListener implements EventListener {
		@Override
		public boolean handle(Event event) {
			if (event instanceof ChangeEvent) {
				currentState = State.PLAYBACK;
				InputPlayer player = new InputPlayer(writer.getReader());
				player.addPlaybackListener(new PlaybackAdapter() {
					@Override
					public void onSynchronousFinish() {
						currentState = State.BEFORE_RECORD;
						layout();
					}
				});
				layout();
				player.startPlayback();
			}
			return true;
		}
	}
}
