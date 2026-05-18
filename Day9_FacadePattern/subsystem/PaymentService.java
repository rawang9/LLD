package Day9_FacadePattern.subsystem;

/**
 * Subsystem: processes payment. Deep payment rules stay here, not in the facade.
 */
public class PaymentService {

    public void pay(double amount) {
        System.out.println("[Payment] Charged $" + amount);
    }
}
