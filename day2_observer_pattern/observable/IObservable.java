/*
 * Observer pattern (Gang of Four) — "Subject" role (sometimes called Observable).
 *
 * WHAT THIS FILE IS:
 *   The *contract* for anything that has observers: register, unregister, and broadcast changes.
 *   It does NOT hold the list of observers — that lives in each concrete class (e.g. WeatherStation),
 *   because Java interfaces cannot have per-instance mutable fields (only static constants).
 *
 * JAVA BASICS — interfaces:
 *   - Methods here are implicitly `public abstract` (you do not need to write those keywords).
 *   - A class `implements` this interface and must provide real bodies for these methods.
 *
 * LLD NOTE:
 *   Program to abstractions: code can depend on `IObservable` instead of `WeatherStation`, so you
 *   could swap subjects or test with fakes — as long as they honor this contract.
 */
package observable;

import observers.iObeservers;

public interface IObservable {

    /** Subscribe: the subject will call this observer when something changes. */
    void addObserver(iObeservers observer);

    /** Unsubscribe: stop notifying this observer. */
    void removeObserver(iObeservers observer);

    /**
     * Tell every registered observer that state may have changed.
     * In your project, observers then call getters on the station ("pull" part of push/pull).
     */
    void notifyObservers();
}
