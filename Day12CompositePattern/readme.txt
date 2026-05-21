Day12CompositePattern — Composite (Organization Hierarchy)
============================================================

Assignment
----------
  Component: EmployeeComponent.showDetails()
  Leaf: Developer, Designer
  Composite: Manager (holds developers, designers, other managers)
  Client: manager.showDetails() prints full tree recursively

Structure
---------
  EmployeeComponent
        |
   +----+----+
   |         |
  Leaf    Manager (composite)
Developer     |
Designer   reports: List<EmployeeComponent>

What you did right
------------------
  - Manager holds List<EmployeeComponent>
  - showDetails() loops children and calls showDetails() on each (recursion)
  - Nested manager (design manager under platform manager)

Learning fixes
--------------
  1. EmployeeComponent desManager = ... then desManager.addEmployee() fails:
     addEmployee is only on Manager, not the Component interface.
     Fix: build tree with Manager type; call showDetails() via EmployeeComponent.

  2. Indentation: pass depth into showDetails(int) so nested levels print clearly.

  3. Naming: assignment uses Developer/Designer (not only *Leaf class names).

Compile & run (from LLD folder)
-------------------------------
  javac Day12CompositePattern/BaseComponent/*.java \
        Day12CompositePattern/leafs/*.java \
        Day12CompositePattern/Composite/*.java \
        Day12CompositePattern/Main.java
  java Day12CompositePattern.Main

Composite vs Facade
-------------------
  Composite: tree of parts, same interface for leaf and group
  Facade: one simplified API over unrelated subsystems
