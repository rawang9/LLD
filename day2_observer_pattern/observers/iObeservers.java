/*
 * Observer pattern — "Observer" role (subscriber / listener).
 *
 * NAMING NOTE:
 *   The spelling `iObeservers` is a typo for "Observers"; keeping the name avoids breaking imports
 *   everywhere. In real code, prefer `WeatherObserver` or `IObserver` for clarity.
 *
 * WHAT THIS INTERFACE PROMISES:
 *   Any class that implements this type can be attached to a subject (`IObservable`) and will receive
 *   `update()` whenever the subject chooses to notify subscribers.
 *
 * JAVA BASICS:
 *   - Implementors **must** declare `public void update()` with the same signature (no args).
 *
 * LLD NOTE:
 *   This is the stable extension point: new UIs (mobile app, logging, alerts) = new classes that
 *   implement this interface, without editing `WeatherStation`'s measurement logic.
 */
package observers;

public interface iObeservers {

    /** Called by the subject after something changed; observer reacts (e.g. print new readings). */
    void update();
}
