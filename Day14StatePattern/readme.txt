Day14StatePattern — State (Music Player State Machine)
========================================================

How we implemented (short note)
-------------------------------
1. Context: MusicPlayerStateMachine holds currentState and exposes play(), pause(), stop().
2. State interface: MusicPlayerState — each state implements the same three actions differently.
3. Concrete states: PlayingState, PausedState, StoppedState.
4. Runtime transitions: when an action is valid, the state object calls
   machine.changeState(new OtherState(machine)) — context pointer updates at runtime.
5. Client (Main) only calls play/pause/stop on the machine; it never switches states directly.

Why State (not if-else in MusicPlayer)?
---------------------------------------
  if (state == PLAYING) { ... } else if (state == PAUSED) { ... }
  grows messy. Each state class owns its own transition rules.

Transition table
----------------
  STOPPED  --play-->  PLAYING
  PLAYING  --pause--> PAUSED
  PLAYING  --stop-->  STOPPED
  PAUSED   --play-->  PLAYING
  PAUSED   --stop-->  STOPPED
  STOPPED  --pause--> (invalid, no transition)

Compile & run (from LLD folder)
-------------------------------
  javac Day14StatePattern/MusicState/*.java Day14StatePattern/*.java
  java Day14StatePattern.Main
