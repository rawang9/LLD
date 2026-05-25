package Day14StatePattern.MusicState;

/**
 * State interface: each concrete state defines valid transitions for play / pause / stop.
 */
public interface MusicPlayerState {

    void play();

    void pause();

    void stop();

    String name();
}
