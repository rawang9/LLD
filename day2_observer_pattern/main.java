/*
 * PROGRAM ENTRY POINT — wires the Observer pattern together.
 *
 * REPO / PACKAGE LAYOUT (important for Java beginners):
 *   - `observable/`  → package `observable`  (subject side: `WeatherStation`, `IObservable`)
 *   - `observers/`   → package `observers`   (observer implementations)
 *   - This file has NO `package` line → it lives in the **default package**.
 *
 *   Imports like `import observable.weatherStation` tell the compiler where those types live.
 *   Compile from this folder (the parent of `observable/` and `observers/`):
 *       javac observable/*.java observers/*.java main.java
 *   Run:
 *       java Main
 *   (JVM looks for class `Main`; filename `main.java` vs `Main.java` can bite on case-sensitive OSes.)
 *
 * EXECUTION FLOW IN main():
 *   1. Obtain the single `weatherStation` via `getInstance()` (thread-safe singleton).
 *   2. Create displays and register them with `addObserver` (constructors no longer auto-register).
 *   3. Each `setMeasurements(...)` updates internal state then calls `notifyObservers()` internally,
 *      which calls `update()` on every observer — you'll see two lines printed per change.
 *
 * Observer pattern recap:
 *   Subject ↔ many Observers, loosely coupled: station does not know display class names, only
 *   `iObeservers`. New displays = new classes + `new ...(...)` here, no edits inside WeatherStation.
 */
import observable.weatherStation;
import observers.advanceDisplay;
import observers.normalDisplayObserver;

class Main {

    public static void main(String[] args) {
        System.out.println("The weather station is starting...");
        weatherStation ws = weatherStation.getInstance();
        normalDisplayObserver ndo = new normalDisplayObserver(ws);
        ws.addObserver(ndo);
        advanceDisplay ad = new advanceDisplay(ws);
        ws.addObserver(ad);
        ws.setMeasurements(80, 65, 30.4f);
        ws.setMeasurements(82, 70, 29.2f);
        ws.setMeasurements(78, 65, 29.2f);
        System.out.println("The weather station is stopping...");
    }
}
