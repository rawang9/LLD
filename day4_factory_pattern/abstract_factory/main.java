package day4_factory_pattern.abstract_factory;

/** Product family A (email): abstraction for different email providers. */
interface EmailNotification {
    void sendEmail();
}

/** Concrete product: Gmail-style email sender. */
class GmailEmailNotification implements EmailNotification {
    @Override
    public void sendEmail() {
        System.out.println("Sending email via Gmail");
    }
}

/** Concrete product: Yahoo-style email sender. */
class YahooEmailNotification implements EmailNotification {
    @Override
    public void sendEmail() {
        System.out.println("Sending email via Yahoo");
    }
}

/** Product family B (SMS): abstraction for different SMS apps/providers. */
interface SmsNotification {
    void sendSms();
}

/** Concrete product: SMS via WhatsApp. */
class WhatsAppSmsNotification implements SmsNotification {
    @Override
    public void sendSms() {
        System.out.println("Sending SMS via WhatsApp");
    }
}

/** Concrete product: SMS via Telegram. */
class TelegramSmsNotification implements SmsNotification {
    @Override
    public void sendSms() {
        System.out.println("Sending SMS via Telegram");
    }
}

/**
 * Abstract factory: creates a <em>set</em> of related products (one email + one SMS)
 * without exposing concrete classes to the client.
 */
interface MessagingAbstractFactory {
    EmailNotification createEmailNotification();

    SmsNotification createSmsNotification();
}

/** Concrete factory: compatible family — Gmail + WhatsApp. */
class GmailWhatsAppMessagingFactory implements MessagingAbstractFactory {
    @Override
    public EmailNotification createEmailNotification() {
        return new GmailEmailNotification();
    }

    @Override
    public SmsNotification createSmsNotification() {
        return new WhatsAppSmsNotification();
    }
}

/** Concrete factory: compatible family — Yahoo + Telegram. */
class YahooTelegramMessagingFactory implements MessagingAbstractFactory {
    @Override
    public EmailNotification createEmailNotification() {
        return new YahooEmailNotification();
    }

    @Override
    public SmsNotification createSmsNotification() {
        return new TelegramSmsNotification();
    }
}

/** Demo: same client code, two swappable product families. */
class Main {
    public static void main(String[] args) {
        System.out.println("Learning the Abstract Factory pattern");

        System.out.println("\n--- Family 1: Gmail + WhatsApp ---");
        sendThroughFactory(new GmailWhatsAppMessagingFactory());

        System.out.println("\n--- Family 2: Yahoo + Telegram ---");
        sendThroughFactory(new YahooTelegramMessagingFactory());
    }

    /** Client depends only on the abstract factory + product interfaces. */
    private static void sendThroughFactory(MessagingAbstractFactory factory) {
        EmailNotification email = factory.createEmailNotification();
        SmsNotification sms = factory.createSmsNotification();
        email.sendEmail();
        sms.sendSms();
    }
}
