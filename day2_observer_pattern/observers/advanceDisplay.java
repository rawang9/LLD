/*
 * Concrete OBSERVER — "advanced" display that also shows pressure.
 *
 * Same structure as normalDisplayObserver; demonstrates adding behavior without changing the subject.
 */
package observers;

import observable.weatherStation;

public class advanceDisplay implements iObeservers {

    private final weatherStation weatherStation;

    public advanceDisplay(weatherStation weatherStation) {
        this.weatherStation = weatherStation;
    }

    @Override
    public void update() {
        float temp = weatherStation.getTemp();
        float humidity = weatherStation.getHumidity();
        float pressure = weatherStation.getPressure();
        System.out.println(
                "Advance Display: " + temp + "F degrees and " + humidity + "% humidity and "
                        + pressure + " pressure");
    }
}
