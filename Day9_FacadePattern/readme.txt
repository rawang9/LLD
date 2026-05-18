Day9_FacadePattern — Facade (Food Delivery)
==========================================

Interview problem
-----------------
Design a food delivery system where the client places an order via a single API,
while Restaurant, Payment, Delivery, and Notification subsystems work together.

Pattern intent
--------------
Provide a simplified unified interface to a set of interfaces in a subsystem.
Facade coordinates workflow; it does NOT replace subsystem business rules.

Components
----------
  Client              -> Main
  Facade              -> FoodDeliveryFacade.placeOrder()
  Subsystems          -> RestaurantService, PaymentService,
                         DeliveryService, NotificationService

UML (text)
----------
                  Client
                     |
                     v
          +----------------------+
          | FoodDeliveryFacade   |
          | placeOrder()         |
          +----------------------+
             /      |       \
            v       v        v
     Restaurant  Payment   Delivery
                    \
                     v
               Notification

Why Facade here?
----------------
Without facade, the client must know call order and every subsystem API.
Facade centralizes orchestration and reduces coupling.

Common mistake
--------------
God Object Facade — putting all business logic inside the facade.
Facade should ORCHESTRATE, not absorb every subsystem responsibility.

Extension (interview)
---------------------
Add LoyaltyService inside placeOrder() flow; client API stays the same.

Compile & run (from LLD folder)
-------------------------------
  javac Day9_FacadePattern/subsystem/*.java Day9_FacadePattern/*.java
  java Day9_FacadePattern.Main

BONUS — Senior Q&A
------------------
1) Why does Proxy keep the SAME interface as the real object?
   (See Day9_ProxyPattern readme — Proxy is about transparent substitution.)

2) Why does Facade usually NOT implement subsystem interfaces?
   Facade exposes a higher-level workflow API (placeOrder), not each subsystem's
   low-level contract. It simplifies; it does not masquerade as a subsystem.

3) Can Proxy and Decorator combine?
   Yes. Example: Proxy controls access; Decorator adds features (encryption, watermark)
   around the same interface — often layered: Client -> Proxy -> Decorator -> Real.

4) Why can Facade become an anti-pattern?
   When it grows into a God Object: all logic, all dependencies, hard to test and change.
   Keep subsystems independent; facade only coordinates.
