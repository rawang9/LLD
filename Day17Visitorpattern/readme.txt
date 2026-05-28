Day17Visitorpattern — Visitor (Employee Analytics)
====================================================

Elements (stable)
-----------------
  Employee <- Developer, Manager, Designer
  Each only implements accept(Visitor) + data getters.

Visitors (operations — add new files only)
------------------------------------------
  SalaryVisitor, PerformanceVisitor, BonusVisitor, HRReportVisitor

Goal
----
  Add new analytics operations WITHOUT modifying Developer/Manager/Designer.

Double dispatch
---------------
  employee.accept(visitor) -> visitor.visit(concreteEmployeeType)

Compile & run (from LLD folder)
-------------------------------
  javac Day17Visitorpattern/*.java
  java Day17Visitorpattern.Main
