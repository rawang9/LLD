package Day11TemplateMethodPattern;

/**
 * Abstract class defines the Template Method: fixed workflow, variable steps.
 *
 * Learning: processOrder() is final — subclasses cannot change the algorithm order.
 * They only override specific steps (deliver) or hooks (isGiftWrapEnabled).
 */
public abstract class OrderProcessingTemplate {

    /**
     * Template method — skeleton of the algorithm. Do not override.
     */
    public final void processOrder() {
        validateOrder();
        processPayment();
        packItems();

        // Hook: optional step — base decides WHEN; subclass decides IF (override hook)
        if (isGiftWrapEnabled()) {
            giftWrap();
        }

        deliver(); // abstract — each order type must implement
        sendNotification();
    }

    // --- Common steps (default behavior; override only if needed) ---

    protected void validateOrder() {
        System.out.println("[Common] Order validated");
    }

    protected void processPayment() {
        System.out.println("[Common] Payment processed");
    }

    protected void packItems() {
        System.out.println("[Common] Items packed");
    }

    protected void sendNotification() {
        System.out.println("[Common] Notification sent");
    }

    // --- Abstract step: must differ per order type ---

    protected abstract void deliver();

    // --- Hook methods (bonus): optional override, default = "off" ---

    /**
     * Hook: returns whether giftWrap() runs. Physical orders enable this.
     * Learning: empty giftWrap() in subclass for digital was a smell — use a hook instead.
     */
    protected boolean isGiftWrapEnabled() {
        return false;
    }

    protected void giftWrap() {
        System.out.println("[Common] Gift wrap applied");
    }
}
