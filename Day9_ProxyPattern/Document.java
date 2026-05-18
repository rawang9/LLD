package Day9_ProxyPattern;

/**
 * Subject: common interface for real document and proxy.
 * Client depends on this — it cannot tell proxy from real object apart.
 */
public interface Document {

    void open();
}
