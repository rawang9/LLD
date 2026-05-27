package Day16Mediator;

/**
 * Colleague: communicates only with mediator, never with other planes.
 *
 * Learning fix: depend on {@link AirTrafficControl} interface, not concrete mediator class.
 */
public class Airplane {

    private final String name;
    private final AirTrafficControl atc;

    public Airplane(String name, AirTrafficControlMediator mediator) {
        this.name = name;
        this.atc = mediator;
        mediator.register(this);
    }

    public String getName() {
        return name;
    }

    public void requestLanding() {
        if (atc.requestLanding(this)) {
            System.out.println("  " + name + ": cleared to land");
        } else {
            System.out.println("  " + name + ": holding — runway not available");
        }
    }

    public void requestTakeoff() {
        if (atc.requestTakeoff(this)) {
            System.out.println("  " + name + ": cleared for takeoff");
        } else {
            System.out.println("  " + name + ": cannot take off yet");
        }
    }

    /**
     * Called by mediator only — broadcast to all registered planes (not plane-to-plane).
     */
    void onRunwayStatusUpdate(boolean runwayFree, String occupiedBy) {
        if (runwayFree) {
            System.out.println("  [Broadcast -> " + name + "] Runway is FREE");
        } else {
            System.out.println("  [Broadcast -> " + name + "] Runway OCCUPIED by " + occupiedBy);
        }
    }
}
