Day15FlightWeightPattern — Flyweight (Bullet Rendering)
=========================================================

Assignment
----------
  100_000 bullets with shared intrinsic state and unique extrinsic state.

Intrinsic (Flyweight — shared)
  BulletType: image, damageType, color

Extrinsic (per bullet)
  Bullet: x, y, velocity

Factory
  BulletTypeFactory reuses same BulletType for matching bullets.

How we implemented
------------------
1. BulletTypeFactory caches by key (image|damage|color).
2. Each Bullet holds extrinsic x,y,velocity + reference to shared BulletType.
3. 100_000 bullets created but only 2 BulletType objects in cache (fire/red, ice/green).

Without Flyweight
-----------------
  100_000 bullets × (image + damage + color strings) duplicated in memory.

With Flyweight
--------------
  2 BulletType objects shared; bullets only store position/velocity + reference.

Compile & run (from LLD folder)
-------------------------------
  javac Day15FlightWeightPattern/*.java
  java Day15FlightWeightPattern.Main

Flyweight vs Singleton
----------------------
  Singleton: one instance of a class globally
  Flyweight: one instance per distinct shared state key (many flyweights possible)
