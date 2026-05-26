Day15BuilderPattern — Builder (Database Configuration)
========================================================

Assignment
----------
Build immutable DatabaseConfig with required host/port and optional
username, password, sslEnabled, connectionTimeout.

Expected usage
--------------
  DatabaseConfig config = new DatabaseConfig.Builder()
      .setHost("localhost")
      .setPort(5432)
      .setUsername("admin")
      .setPassword("secret")
      .setSslEnabled(true)
      .setConnectionTimeout(30)
      .build();

What you practiced
------------------
  - private constructor on product
  - static inner Builder class
  - method chaining (return this)
  - validation in build() — host required, port 1-65535
  - immutable final fields

Learning fixes (from earlier attempt)
-------------------------------------
  1. Renamed DatabaseConnection -> DatabaseConfig (domain name)
  2. port as int (not String) — type-safe
  3. Required fields validated in build(), not builder constructor
  4. setHost/setPort fluent API (assignment style)
  5. Optional fields: null or defaults, not empty-string checks
  6. Fixed port validation bug (throw was always reached after if)
  7. toString() with masked password (bonus)

Compile & run (from LLD folder)
-------------------------------
  javac Day15BuilderPattern/*.java
  java Day15BuilderPattern.Main

Builder vs Factory
------------------
  Builder: step-by-step assembly of complex object, many optional fields
  Factory: creates object in one shot, often hides which concrete type
