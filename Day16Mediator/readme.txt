Day16Mediator — Mediator (Air Traffic Control)
================================================

Assignment
----------
  Mediator: AirTrafficControl
  Colleague: Airplane — requestLanding(), requestTakeoff()
  Planes never communicate directly; all coordination via mediator.

Bonus
-----
  Mediator tracks runway availability — only one plane at a time (runwayOccupiedBy).
  colleagues list: broadcast runway OCCUPIED / FREE to all registered planes.

How it works
------------
1. Airplane calls atc.requestLanding(this) — not another plane.
2. Mediator checks runwayAvailable; grants or denies.
3. Takeoff only allowed for the plane currently holding the runway.
4. After takeoff, runway is free for the next plane.

Issues fixed from original
--------------------------
  - Mediator did not know WHICH plane requested (anyone could free runway).
  - Airplane depended on concrete AirTrafficControlMediator.
  - Takeoff always succeeded and freed runway.

Compile & run (from LLD folder)
-------------------------------
  javac Day16Mediator/*.java
  java Day16Mediator.Main

Mediator vs Chain of Responsibility
-----------------------------------
  Mediator: colleagues are peers; central coordinator handles interaction.
  Chain: request passed along until one handler processes it.
