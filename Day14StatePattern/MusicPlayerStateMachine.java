package Day14StatePattern;

import Day14StatePattern.MusicState.MusicPlayerState;
import Day14StatePattern.MusicState.StoppedState;

/**
 * Context: delegates button presses to the current state object.
 * State changes happen at runtime via {@link #changeState(MusicPlayerState)}.
 */
public class MusicPlayerStateMachine {

    private MusicPlayerState currentState;

    public MusicPlayerStateMachine() {
        currentState = new StoppedState(this);
        System.out.println("Initial state: " + currentState.name());
    }

    public void play() {
        currentState.play();
    }

    public void pause() {
        currentState.pause();
    }

    public void stop() {
        currentState.stop();
    }

    /** Runtime transition — called by state classes when behavior changes. */
    public void changeState(MusicPlayerState newState) {
        System.out.println("Transition: " + currentState.name() + " -> " + newState.name());
        currentState = newState;
    }

    public String currentStateName() {
        return currentState.name();
    }
}
