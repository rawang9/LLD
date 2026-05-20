package Day11TemplateMethodPattern;

/**
 * Concrete class: physical shipment. Overrides deliver() and enables gift-wrap hook.
 */
public class PhysicalOrder extends OrderProcessingTemplate {

    @Override
    protected void packItems() {
        System.out.println("[Physical] Items packed in box for shipping");
    }

    @Override
    protected boolean isGiftWrapEnabled() {
        return true; // hook — template will call giftWrap()
    }

    @Override
    protected void giftWrap() {
        System.out.println("[Physical] Gift wrap added");
    }

    @Override
    protected void deliver() {
        System.out.println("[Physical] Shipped to customer address");
    }
}
