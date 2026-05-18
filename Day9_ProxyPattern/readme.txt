Day9_ProxyPattern — Proxy (Document Access)
===========================================

Interview problem
-----------------
Confidential documents require role validation and audit logging before opening.

Pattern intent
--------------
Provide a surrogate that controls access to another object (RealSubject).

Components
----------
  Subject (interface)  -> Document.open()
  RealSubject          -> ConfidentialDocument
  Proxy                -> DocumentProxy (auth + audit + lazy load)
  Client               -> Main

Proxy flow
----------
  client.open()
      -> proxy.open()
            -> validate role
            -> log access
            -> create real document (lazy, first time only)
            -> realDocument.open()

Why Proxy (not auth inside ConfidentialDocument)?
-------------------------------------------------
Auth, logging, caching, lazy loading are cross-cutting concerns.
Keeping them in the proxy keeps the real object focused on "open content".

Proxy vs Decorator
------------------
  Decorator  -> adds behavior (features stack)
  Proxy      -> controls access / lifecycle (when object is created, who may use it)

Real-world proxies
------------------
API gateway, CDN, nginx reverse proxy, Hibernate lazy loading, SDK wrappers.

Extensions (interview)
----------------------
  Caching proxy  -> Map<docId, Document> return cached instance
  Virtual proxy  -> this module: real object created on first successful open

Compile & run (from LLD folder)
-------------------------------
  javac Day9_ProxyPattern/*.java
  java Day9_ProxyPattern.Main

BONUS — Senior Q&A
------------------
1) Why does Proxy keep the SAME interface as the real object?
   So the client can treat proxy and real object interchangeably (transparent
   substitution). Client code stays: Document d = ...; d.open();

2) Why does Facade usually NOT implement subsystem interfaces?
   Facade offers a higher-level workflow API (placeOrder), not the low-level
   contract of each subsystem. It simplifies many interfaces into one coarse API.

3) Can Proxy and Decorator combine?
   Yes. Example stack: Client -> SecurityProxy -> EncryptionDecorator -> RealDocument.
   Proxy guards access; Decorator adds features around the same interface.

4) Why can Facade become an anti-pattern?
   When it becomes a God Object that owns all logic and hides poor subsystem design.
   Subsystems should remain usable and testable on their own.
