package day4_factory_pattern.simple_factory;

/** Product abstraction: all notification channels expose the same operation. */
interface Notification {
    void notifyUser();
}

/** Concrete product: email channel implementation. */
class EmailNotification implements Notification {
    @Override
    public void notifyUser() {
        System.out.println("Sending email notification");
    }
}

/** Concrete product: SMS channel implementation. */
class SMSNotification implements Notification {
    @Override
    public void notifyUser() {
        System.out.println("Sending SMS notification");
    }
}

/**
 * Simple factory: one class centralizes object creation behind a method.
 * Callers depend on the product interface, not on {@code new} of concrete types.
 */
class NotificationFactory {
    /**
     * @param channel {@code "email"} or {@code "sms"}
     * @throws IllegalArgumentException if {@code channel} is unknown
     */
    public Notification createNotification(String channel) {
        if ("email".equalsIgnoreCase(channel)) {
            return new EmailNotification();
        }
        if ("sms".equalsIgnoreCase(channel)) {
            return new SMSNotification();
        }
        throw new IllegalArgumentException("Unknown channel: " + channel);
    }
}

/** Demo: client uses factory + interface only. */
class Main {
    public static void main(String[] args) {
        System.out.println("Learning the Simple Factory pattern");
        NotificationFactory factory = new NotificationFactory();
        Notification email = factory.createNotification("email");
        Notification sms = factory.createNotification("sms");
        email.notifyUser();
        sms.notifyUser();
    }
}
