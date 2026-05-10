/*
 * Concrete SUBJECT in the Observer pattern.
 *
 * RESPONSIBILITIES:
 *   1. Own the "state" observers care about: temperature, humidity, pressure.
 *   2. Keep a list of registered observers (the interface cannot hold this list for you).
 *   3. When state changes (`setMeasurements`), notify all observers so they can refresh their view.
 *
 * PUSH vs PULL (your code uses a hybrid):
 *   - PUSH: `notifyObservers()` actively calls each observer (`observer.update()`).
 *   - PULL: inside `update()`, each observer reads fresh values via `getTemp()`, etc.
 *   Pure push would pass (temp, humidity, pressure) into `update(...)`; pure pull would only
 *   signal "something changed" and observers fetch everything. Both are valid teaching examples.
 *
 * JAVA BASICS:
 *   - `implements IObservable` means this class promises to implement all methods from that interface.
 *   - `private final List<...> observerList` — `final` means the reference never changes (you always
 *     use the same ArrayList object), but you can still `.add()` / `.remove()` elements inside it.
 *   - `this.temp = temp` disambiguates field vs parameter with the same name.
 *
 * SINGLETON + THREAD SAFETY:
 *   - Only one `weatherStation` exists app-wide: private constructor + `getInstance()`.
 *   - `getInstance()` is `static synchronized` so many threads cannot create two instances at startup.
 *   - Instance methods that touch `observerList` or measurements use `synchronized` so the list and
 *     readings are not updated concurrently in a racy way.
 *   - `notifyObservers()` copies the list inside `synchronized (this)` then calls `update()` outside
 *     the lock so an observer cannot deadlock the station by registering another observer mid-update.
 */
package observable;

import java.util.ArrayList;
import java.util.List;

import observers.iObeservers;

public class weatherStation implements IObservable {

    private static weatherStation instance;

    /**
     * Lazy singleton: created on first use. {@code synchronized} on the method serializes threads
     * so only one passes the {@code if (instance == null)} check at a time (no double instance).
     */
    public static synchronized weatherStation getInstance() {
        if (instance == null) {
            instance = new weatherStation();
        }
        return instance;
    }

    /** Outside callers must use {@link #getInstance()}; prevents {@code new weatherStation()}. */
    private weatherStation() {}

    /** All observers currently interested in this station's measurements. */
    private final List<iObeservers> observerList = new ArrayList<>();

    private float temp;
    private float humidity;
    private float pressure;

    @Override
    public synchronized void addObserver(iObeservers observer) {
        observerList.add(observer);
    }

    @Override
    public synchronized void removeObserver(iObeservers observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers() {
        List<iObeservers> snapshot;
        synchronized (this) {
            snapshot = new ArrayList<>(observerList);
        }
        for (iObeservers observer : snapshot) {
            observer.update();
        }
    }

    /**
     * Domain API: when measurements change, update fields then broadcast.
     * Order matters: set state first so when observers call getters they see the new values.
     */
    public synchronized void setMeasurements(float temp, float humidity, float pressure) {
        this.temp = temp;
        this.humidity = humidity;
        this.pressure = pressure;
        notifyObservers();
    }

    public synchronized float getTemp() {
        return temp;
    }

    public synchronized float getHumidity() {
        return humidity;
    }

    public synchronized float getPressure() {
        return pressure;
    }
}
