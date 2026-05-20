package Day11TemplateMethodPattern;

/**
 * Concrete class: digital delivery. Only deliver() must differ; hooks use defaults.
 */
public class DigitalOrder extends OrderProcessingTemplate {

    @Override
    protected void packItems() {
        // Learning: digital can override a common step when behavior differs
        System.out.println("[Digital] License / download link prepared");
    }

    @Override
    protected void deliver() {
        System.out.println("[Digital] Delivered via email");
    }

    // isGiftWrapEnabled() stays false (default) — no empty giftWrap() override needed
}
