package Day16Mediator;

class Main {

    public static void main(String[] args) {
        System.out.println("=== Mediator Pattern: Air Traffic Control ===\n");

        AirTrafficControlMediator atc = new AirTrafficControlMediator();
        Airplane akasa = new Airplane("Akasa", atc);
        Airplane indigo = new Airplane("Indigo", atc);

        System.out.println("--- Akasa lands (runway taken) ---");
        akasa.requestLanding();

        System.out.println("\n--- Indigo tries to land (denied) ---");
        indigo.requestLanding();

        System.out.println("\n--- Indigo tries takeoff without runway (denied) ---");
        indigo.requestTakeoff();

        System.out.println("\n--- Akasa takes off (runway free) ---");
        akasa.requestTakeoff();

        System.out.println("\n--- Indigo lands after runway free ---");
        indigo.requestLanding();
    }
}
