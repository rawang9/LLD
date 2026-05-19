day10_bridge_pattern — Bridge (Gaming Weapon System)
======================================================

Assignment goal
---------------
Avoid class explosion: FireSword, IceSword, FireGun, IceGun.
Use Bridge so weapon type and enchantment vary independently.

Roles
-----
  Abstraction     -> Weapons (Sword, Gun)
  Implementor     -> IEnchantments (FireEnchantment, IceEnchantment)
  Bridge          -> Weapons holds IEnchantments reference; composed in constructor

Without Bridge (M weapons × N enchantments = M×N classes)
---------------------------------------------------------
  FireSword, IceSword, FireGun, IceGun, ...

With Bridge (M + N classes)
---------------------------
  new Sword(new FireEnchantment())
  new Gun(new IceEnchantment())

What you did right
------------------
  - Composition: Gun/Sword take IEnchantments in constructor
  - Separate packages for abstraction vs implementation side
  - Runtime mixing of weapon + enchantment

Learning fixes applied
----------------------
  1. Weapons.attack() was empty — should be abstract (forces real behavior in subclasses)
  2. Variable names like FireGun looked like missing Bridge classes — use swordWithFire, gunWithIce
  3. Main now shows compose-at-runtime and optional runtime swap of enchantment
  4. File must be Gun.java (capital G) for public class Gun — IDE errors otherwise

Compile & run (from LLD folder)
-------------------------------
  javac day10_bridge_pattern/DependentInterfaceEnchantments/*.java \
        day10_bridge_pattern/IndependentAbstractionWeapons/*.java \
        day10_bridge_pattern/Main.java
  java day10_bridge_pattern.Main

Extension
---------
  Add PoisonEnchantment — no new Sword/Gun classes needed.

Bridge vs Strategy?
-------------------
  Bridge: splits abstraction + implementation hierarchies that evolve separately
  Strategy: interchangeable algorithm for one task; usually one dimension
