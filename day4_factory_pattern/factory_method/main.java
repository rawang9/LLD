package day4_factory_pattern.factory_method;

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
 * Creator (Factory Method): each concrete creator decides which {@link Notification} to build.
 * No channel string—polymorphism picks the product type.
 */
interface NotificationCreator {
    Notification createNotification();
}

/** Concrete creator: always produces email notifications. */
class EmailNotificationCreator implements NotificationCreator {
    @Override
    public Notification createNotification() {
        return new EmailNotification();
    }
}

/** Concrete creator: always produces SMS notifications. */
class SmsNotificationCreator implements NotificationCreator {
    @Override
    public Notification createNotification() {
        return new SMSNotification();
    }
}

/**
 * Optional hook: same client code can work with any {@link NotificationCreator}.
 * The factory method is {@code createNotification()}; each subclass fixes the concrete product.
 */
class Main {
    public static void main(String[] args) {
        System.out.println("Learning the Factory Method pattern");

        NotificationCreator emailCreator = new EmailNotificationCreator();
        NotificationCreator smsCreator = new SmsNotificationCreator();

        Notification email = emailCreator.createNotification();
        Notification sms = smsCreator.createNotification();

        email.notifyUser();
        sms.notifyUser();
    }
}
