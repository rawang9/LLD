/*
 * Concrete OBSERVER — simple display (temperature + humidity only).
 *
 * WHY HOLD A REFERENCE TO THE STATION?
 *   After `update()` runs, this class pulls latest values via getters — classic "pull" side of
 *   push/pull. With a singleton subject, `main` typically passes `weatherStation.getInstance()` here.
 *
 * JAVA BASICS:
 *   - `implements iObeservers` — must implement `update()`.
 *   - `this.weatherStation = weatherStation` stores the subject for later getter calls.
 */
package observers;

import observable.weatherStation;

public class normalDisplayObserver implements iObeservers {

    private final weatherStation weatherStation;

    public normalDisplayObserver(weatherStation weatherStation) {
        this.weatherStation = weatherStation;
    }

    @Override
    public void update() {
        float temp = weatherStation.getTemp();
        float humidity = weatherStation.getHumidity();
        System.out.println("Normal Display: " + temp + "F degrees and " + humidity + "% humidity");
    }
}
