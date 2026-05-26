Day15ProtoTypePattern — Prototype (Character Creation)
========================================================

Assignment
----------
  Prototype: GameCharacterPrototype.cloneCharacter()
  Concrete: GameCharacter (name, weapon, armor)
  Create baseWarrior, clone warrior1/2/3 without rebuilding from scratch

How we implemented
------------------
1. baseWarrior is the template object (built once).
2. cloneCharacter() creates new GameCharacter with copied field values.
3. Each clone only changes name — weapon/armor setup reused via prototype.

Bonus: Shallow vs deep copy (interview)
---------------------------------------
  weapon is a Weapon object (reference type).

  Shallow clone (cloneCharacterShallow):
    new GameCharacter(name, this.weapon, armor)  // SAME Weapon reference
    Changing clone's weapon name changes baseWarrior too.

  Deep clone (cloneCharacter):
    new GameCharacter(name, this.weapon.copy(), armor)  // NEW Weapon instance
    Clones are independent — safe mutation.

When to use Prototype
---------------------
  Object creation is costly or complex; many similar instances needed.
  Copy existing configured object instead of running full constructor logic again.

Compile & run (from LLD folder)
-------------------------------
  javac Day15ProtoTypePattern/*.java
  java Day15ProtoTypePattern.Main

Prototype vs Builder
--------------------
  Prototype: copy existing object
  Builder: assemble new object step by step
