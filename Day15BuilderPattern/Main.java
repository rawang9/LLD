package Day15BuilderPattern;

class Main {

    public static void main(String[] args) {
        System.out.println("=== Builder Pattern: Database Config ===\n");

        DatabaseConfig config = new DatabaseConfig.Builder()
                .setHost("localhost")
                .setPort(5432)
                .setUsername("admin")
                .setPassword("secret")
                .setSslEnabled(true)
                .setConnectionTimeout(30)
                .build();

        System.out.println(config);

        DatabaseConfig minimal = new DatabaseConfig.Builder()
                .setHost("db.internal")
                .setPort(3306)
                .build();
        System.out.println("\n" + minimal);

        try {
            new DatabaseConfig.Builder().setHost("localhost").setPort(99999).build();
        } catch (IllegalStateException e) {
            System.out.println("\nValidation caught: " + e.getMessage());
        }
    }
}
