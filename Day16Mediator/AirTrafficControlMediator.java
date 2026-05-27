package Day16Mediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete mediator: owns runway state and decides which plane may land/take off.
 *
 * Bonus: only one plane on runway at a time — tracks {@link #runwayOccupiedBy}.
 */
public class AirTrafficControlMediator implements AirTrafficControl {

    private boolean runwayAvailable = true;
    private Airplane runwayOccupiedBy;
    private final List<Airplane> colleagues = new ArrayList<>();

    @Override
    public void register(Airplane plane) {
        colleagues.add(plane);
    }

    @Override
    public boolean requestLanding(Airplane plane) {
        if (runwayAvailable) {
            runwayAvailable = false;
            runwayOccupiedBy = plane;
            System.out.println("[ATC] Runway granted to " + plane.getName() + " for landing");
            broadcastRunwayOccupied(plane.getName());
            return true;
        }
        System.out.println("[ATC] Landing denied for " + plane.getName()
                + " — runway busy (" + runwayOccupiedBy.getName() + " on runway)");
        return false;
    }

    @Override
    public boolean requestTakeoff(Airplane plane) {
        if (runwayOccupiedBy == plane) {
            runwayOccupiedBy = null;
            runwayAvailable = true;
            System.out.println("[ATC] Runway cleared — " + plane.getName() + " took off");
            broadcastRunwayFree();
            return true;
        }
        System.out.println("[ATC] Takeoff denied for " + plane.getName()
                + " — not holding the runway");
        return false;
    }

    public List<Airplane> getColleagues() {
        return Collections.unmodifiableList(colleagues);
    }

    /** Uses colleagues list: notify every registered plane (mediator broadcast). */
    private void broadcastRunwayOccupied(String planeName) {
        System.out.println("[ATC] Broadcasting: runway occupied");
        for (Airplane colleague : colleagues) {
            colleague.onRunwayStatusUpdate(false, planeName);
        }
    }

    private void broadcastRunwayFree() {
        System.out.println("[ATC] Broadcasting: runway free");
        for (Airplane colleague : colleagues) {
            colleague.onRunwayStatusUpdate(true, null);
        }
    }
}
