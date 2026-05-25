Day14NullPattern — Null Object (Payment Processing)
====================================================

Assignment
----------
  Interface: PaymentProcessor.pay()
  Real: CreditCardPayment, UpiPayment
  Null: NoPaymentProcessor — "No payment method selected" or silent no-op

How we implemented (short note)
-------------------------------
1. PaymentProcessor — one interface for all payment types + null object.
2. Real objects do actual (simulated) payment work.
3. NoPaymentProcessor — default when nothing selected; pay() is safe empty behavior.
4. Checkout always holds a PaymentProcessor (starts as NoPaymentProcessor), never null.
5. Client calls processor.pay() without if (processor == null).

Null Object vs null reference
-----------------------------
  Bad:  if (processor != null) processor.pay();
  Good: processor.pay();  // NoPaymentProcessor handles the "none" case

Compile & run (from LLD folder)
-------------------------------
  javac Day14NullPattern/*.java
  java Day14NullPattern.Main
