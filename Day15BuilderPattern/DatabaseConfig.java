package Day15BuilderPattern;

/**
 * Immutable product built by {@link Builder}.
 * Private constructor — only Builder can create instances.
 */
public final class DatabaseConfig {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean sslEnabled;
    private final int connectionTimeout;

    private DatabaseConfig(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.username = builder.username;
        this.password = builder.password;
        this.sslEnabled = builder.sslEnabled;
        this.connectionTimeout = builder.connectionTimeout;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

  /** Bonus: readable config summary (password masked). */
    @Override
    public String toString() {
        return "DatabaseConfig{"
                + "host='" + host + '\''
                + ", port=" + port
                + ", username=" + (username != null ? username : "(none)")
                + ", password=" + (password != null ? "****" : "(none)")
                + ", sslEnabled=" + sslEnabled
                + ", connectionTimeout=" + connectionTimeout + "s"
                + '}';
    }

    public static class Builder {

        private String host;
        private int port;
        private String username;
        private String password;
        private boolean sslEnabled;
        private int connectionTimeout = 30; // optional default (seconds)

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setSslEnabled(boolean sslEnabled) {
            this.sslEnabled = sslEnabled;
            return this;
        }

        public Builder setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public DatabaseConfig build() {
            if (host == null || host.isBlank()) {
                throw new IllegalStateException("host is required");
            }
            if (port < 1 || port > 65535) {
                throw new IllegalStateException("port must be between 1 and 65535");
            }
            return new DatabaseConfig(this);
        }
    }
}
