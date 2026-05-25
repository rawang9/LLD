package Day14StatePattern.MusicState;

import Day14StatePattern.MusicPlayerStateMachine;

public class StoppedState implements MusicPlayerState {

    private final MusicPlayerStateMachine machine;

    public StoppedState(MusicPlayerStateMachine machine) {
        this.machine = machine;
    }

    @Override
    public String name() {
        return "STOPPED";
    }

    @Override
    public void play() {
        System.out.println("Starting playback...");
        machine.changeState(new PlayingState(machine));
    }

    @Override
    public void pause() {
        // Learning fix: cannot pause when stopped — invalid transition, no state change
        System.out.println("Cannot pause — player is stopped. Press play first.");
    }

    @Override
    public void stop() {
        System.out.println("Already stopped.");
    }
}
