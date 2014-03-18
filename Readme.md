# Input recorder and playback for libGDX
## Introduction
For the last project I was involved in, our team was supposed to drive the test coverage as high as possible.
We had based it on libGDX, which is great, but with its scene2d.ui package renders most of the GUI tools out there unusable.
Unfortunately though, our project consisted to a major part of GUI code.
As most will know, it is pretty pointless to write unit tests for GUI tools.
So the one thing we were left with was manually running predefined sequences to gain at least some sort of coverage report including our GUI.
This was when I came up with the idea to automate the tests by replaying input, majorly inspired by [this article](http://bitiotic.com/blog/2012/04/05/libgdx-test-automation-through-input-abuse/).
For android, I could have used monkeyrunner, but loving platform independence I wanted to create something libGDX specific running also on desktop.

This is the result.
I hope, there are people out there who find it useful.
Below I explain what can already be achieved with the project, and in the "Future development" section you will find additional ideas on what could be made possible.

## How to use
### Recording
```java
public static void main(String[] args) { // or onCreate on Android
	// initialize your app first...
	InputRecorderConfiguration inputConfig = new InputRecorderConfiguration();
	// maybe alter the configuration
	// create and set a custom InputRecordWriter
	InputRecorder recorder = new InputRecorder(inputConfig);
	recorder.startRecording();
}
```
### Playback
This is not yet implemented
```java
public T myMethod(...) { // wherever you want to playback
	// create and alter a PlayerConfiguration
	// set the input file and configure the cool features of the player
	// create and set a custom InputRecordReader
	InputRecordPlayer player = new InputRecordPlayer();
	player.startPlayback();
}
```

## Currently working
* tested on desktop, code written to also support android (not tested)
* recorded input legible for both poll- and event-based applications
* architecture supporting three types of input: 
  1. static (e.g. which sensors are supported)
  2. synchronous (propagated in main loop, e.g. touch events)
  3. asynchronous (callback supplied, e.g. getPlaceholderText)
* advanced configurability
  * specify which input states you are interested in
  * where do you store the recorded input
  * which format is used (write your own format)
  
## Not supportd
I need to get this working somehow.
This is why I currently ignored to deal with software triggered input capabilities.
This simply should not be necessary to be recorded, as recordings are supposed to record what input influences the program flow.
Not how the program flow influences the input.
Although, I guess, it wouldn't be too hard to record them.
This is what won't be ensured during playback for it simply doesn't get recorded:
* catching back key on android
* catch menu key on android
* catching cursor on desktop and saving its success (getCursorCatched)
* setting the cursor 
* making the software keyboard visible

## Future development
* get recording to work with simple writer/ output format
* implement player
* use less reflection. Real backends with real access to platform specific code.
* implement possibility to respond to certain inputs while recording
  * enables writing a gui or something with hotkeys to start/pause/resume/stop recording
* implement a player supporting to mix the recorded input with the actual input
  * e.g. specify regions that still listen to touch events, so an onscreen button can stop a playback.
    This would be great for tutorials
* implement callbacks 
  * on replay finished, so e.g. an introduction will automatically be replayed
  * on certain input events, so they can be visualized
