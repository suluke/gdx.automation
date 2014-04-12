package com.badlogic.gdx.automation.recorder.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Defines a set of files that make up a JsonInputRecord
 * 
 * @author Lukas Böhm
 * 
 */
class JsonInputRecord {
	protected final FileHandle outputFile;
	protected final FileHandle syncValuesFile;
	protected final FileHandle asyncValuesFile;
	protected final FileHandle staticValuesFile;

	public JsonInputRecord(FileHandle jsonBaseFile) {
		this.outputFile = jsonBaseFile;
		// TODO be more graceful with existing files, care ore about the actual
		// FileHandle given (maybe merge on close) etc.
		syncValuesFile = Gdx.files.getFileHandle(
				outputFile.pathWithoutExtension() + "-sync.json",
				outputFile.type());
		asyncValuesFile = Gdx.files.getFileHandle(
				outputFile.pathWithoutExtension() + "-async.json",
				outputFile.type());
		staticValuesFile = Gdx.files.getFileHandle(
				outputFile.pathWithoutExtension() + "-static.json",
				outputFile.type());
	}
}
