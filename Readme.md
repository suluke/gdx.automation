# gdx.automation
### Input recorder and playback for libGDX

## Introduction
User input is a powerful concept.  
Most developers just take user input interfaces as granted and intuitionally build their applications to respond to them.
Usually, though, they forget about this powerful control mechanism they have already created when it comes to programmatically operate their software.
Especially in games, where everything is built firmly around visual representation and direct input, developers will often run into the following situations:
1. The game engine works flawlessly, but demonstration (e.g. in tutorials) needs additional code to either script the engine or play a huge video of a demo
2. Most of the code is gui related. Writing unit tests seems pointless most of the time or is not possible at all
3. During the presentation of early project stages, certain input may cause crashes, which is very undesirable and should be prevented

All these situations have in common that simply feeding the application custom input through the standard way could be an option for a valid solution.
Inspired by [this article](http://bitiotic.com/blog/2012/04/05/libgdx-test-automation-through-input-abuse/), I started this little project to provide a means to face those situations.
Luckily, libGDX abstracts all input and presents it in a nice and straight-forward fashion, making it possible to record it without interfering with the usual usage pattern.

I hope, there are people out there who find it useful.  
Below I explain what can already be achieved with the project, and in the "Future development" section you will find additional ideas on what is planned to be made possible soon.

## Features
* tested on desktop, code written to also support android (not tested)
* recorded input legible for both poll- and event-based applications  
  ([RemoteSender](https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/input/RemoteSender.java) only supports input retrieved via InputProcessors)
* offering means to record and playback input with just 3 additional lines of code, see below
* replacing android's monkeyrunner already, platform independently and in the language you already work with!
* easy to replace monkey tool, too. (currently TODO, see `RandomInputRecordReader`)
* architecture supporting three types of input, for easy extensibility: 
  1. static (e.g. which sensors are supported)
  2. synchronous (propagated in main loop, e.g. touch events)
  3. asynchronous (callback supplied, e.g. getPlaceholderText)
* advanced configurability
  * specify which input values you are interested in
  * where you store the recorded input
  * which format is used (write your own format!)
  
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
```java
public T myMethod(...) { // wherever you want to playback
	// create and alter a PlayerConfiguration
	// set the input file and configure the cool features of the player
	// create and set a custom InputRecordReader
	InputRecordPlayer player = new InputRecordPlayer();
	player.startPlayback();
}
```
See the demos folder and the eclipse project within for code in action:
* `com.badlogic.demos.automation.simple`: Simple application to visualize input while recrding it with the ability to play it back

## Future development
* ~~get recording to work with simple writer/ output format~~ CHECK!
* implement `InputPlayer`
* use less reflection. Use real backends with real access to platform specific code.
* comply with libGdx' style guidelines
* implement possibility to respond to certain inputs while recording
  * enables writing a gui or something with hotkeys to start/pause/resume/stop recording
* `InputCombinator`: implement a player supporting to mix the recorded input with the actual input
  * e.g. specify regions that still listen to current device events, so an onscreen button can stop a playback.
    This would be great for tutorials.
* implement callbacks during playback
  * ~~on replay finished, so e.g. a tutorial will automatically be replayed~~ CHECK!
* `RandomInputRecordReader`: implement an android-style monkey tool

## Contributing
I would be happy to receive any pull requests with improvements.
Note that I am more likely to accept contributions that comply with the [libGdx contribution guidelines](https://github.com/libgdx/libgdx/wiki/Contributing), though.  
Regarding the Contributor License Agreement, I will not demand it for smaller contributions (e.g. fixing a bug, adding new features that are non-essential for the whole project).
However, if someone comes an asks me to replace huge parts of already existing code with his own, I will probably insist on a written assurance that he won't revoke his consent for his code being used in gdx-automation later.

## Licensing
Copyright 2014 Lukas Böhm

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
