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
	protected final FileHandle syncPropertiesFile;
	protected final FileHandle asyncPropertiesFile;
	protected final FileHandle staticPropertiesFile;
	protected final FileHandle recordPropertiesFile;

	public JsonInputRecord(FileHandle jsonBaseFile) {
		this.outputFile = jsonBaseFile;
		// TODO be more graceful with existing files, care ore about the actual
		// FileHandle given (maybe merge on close) etc.
		syncPropertiesFile = Gdx.files.getFileHandle(
				outputFile.pathWithoutExtension() + "-sync.json",
				outputFile.type());
		asyncPropertiesFile = Gdx.files.getFileHandle(
				outputFile.pathWithoutExtension() + "-async.json",
				outputFile.type());
		staticPropertiesFile = Gdx.files.getFileHandle(
				outputFile.pathWithoutExtension() + "-static.json",
				outputFile.type());
		recordPropertiesFile = Gdx.files.getFileHandle(
				outputFile.pathWithoutExtension() + "-properties.json",
				outputFile.type());
	}
}
