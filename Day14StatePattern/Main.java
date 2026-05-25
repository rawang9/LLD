package Day14StatePattern;

class Main {

    public static void main(String[] args) {
        System.out.println("=== State Pattern: Music Player State Machine ===\n");

        MusicPlayerStateMachine player = new MusicPlayerStateMachine();

        player.play();
        player.pause();
        player.play();
        player.stop();
        player.pause();
        player.play();

        System.out.println("\nFinal state: " + player.currentStateName());
    }
}
