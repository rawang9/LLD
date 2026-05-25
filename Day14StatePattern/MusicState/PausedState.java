package Day14StatePattern.MusicState;

import Day14StatePattern.MusicPlayerStateMachine;

public class PausedState implements MusicPlayerState {

    private final MusicPlayerStateMachine machine;

    public PausedState(MusicPlayerStateMachine machine) {
        this.machine = machine;
    }

    @Override
    public String name() {
        return "PAUSED";
    }

    @Override
    public void play() {
        System.out.println("Resuming...");
        machine.changeState(new PlayingState(machine));
    }

    @Override
    public void pause() {
        System.out.println("Already paused.");
    }

    @Override
    public void stop() {
        System.out.println("Stopping from pause...");
        machine.changeState(new StoppedState(machine));
    }
}
