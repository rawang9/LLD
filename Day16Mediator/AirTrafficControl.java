package Day16Mediator;

/**
 * Mediator interface: all runway coordination goes through here.
 * Colleagues ({@link Airplane}) never talk to each other directly.
 */
public interface AirTrafficControl {

    void register(Airplane plane);

    boolean requestLanding(Airplane plane);

    boolean requestTakeoff(Airplane plane);
}
