Day11TemplateMethodPattern — Template Method (Online Orders)
============================================================

Assignment
----------
Common workflow processOrder():
  validateOrder -> processPayment -> packItems -> deliver -> sendNotification

Order types: DigitalOrder, PhysicalOrder (different deliver implementations).

Pattern roles
-------------
  Template method  -> OrderProcessingTemplate.processOrder() [final]
  Abstract steps   -> deliver() [abstract]
  Concrete steps   -> validateOrder, processPayment, ... [defaults in base]
  Hook methods     -> isGiftWrapEnabled(), giftWrap() [optional override]

What you did right
------------------
  - final processOrder() defines fixed workflow order
  - deliver() abstract in subclasses
  - Physical vs Digital concrete classes

Learning fixes
--------------
  1. giftWrap() was always called; Digital relied on empty override.
     Better: hook isGiftWrapEnabled() — template decides IF, subclass decides policy.

  2. Use protected on step methods (template steps are for subclasses, not public API).

  3. Typo class names (Dilevery/Delevery) -> PhysicalOrder, DigitalOrder.

  4. Client should call processOrder() on template type, not concrete-only logic.

Hook vs abstract
----------------
  abstract deliver()  -> MUST implement (required variation)
  hook isGiftWrapEnabled() -> optional; default false, Physical returns true

Compile & run (from LLD folder)
-------------------------------
  javac Day11TemplateMethodPattern/*.java
  java Day11TemplateMethodPattern.Main

Template Method vs Strategy
---------------------------
  Template Method: inheritance, fixed algorithm skeleton, some steps vary
  Strategy: composition, entire algorithm interchangeable
