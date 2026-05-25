package Day14StatePattern.MusicState;

import Day14StatePattern.MusicPlayerStateMachine;

public class PlayingState implements MusicPlayerState {

    private final MusicPlayerStateMachine machine;

    public PlayingState(MusicPlayerStateMachine machine) {
        this.machine = machine;
    }

    @Override
    public String name() {
        return "PLAYING";
    }

    @Override
    public void play() {
        System.out.println("Already playing.");
    }

    @Override
    public void pause() {
        System.out.println("Pausing...");
        machine.changeState(new PausedState(machine));
    }

    @Override
    public void stop() {
        System.out.println("Stopping...");
        machine.changeState(new StoppedState(machine));
    }
}
