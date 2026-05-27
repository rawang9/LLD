Day16ChainOfRes — Chain of Responsibility (Loan Approval)
=========================================================

Flow
----
  LoanRequest
     -> CreditScoreHandler
     -> SalaryValidationHandler
     -> ManagerApprovalHandler
     -> LoggingHandler

Any handler may REJECT and STOP (next is not called).

Issues in original code
-----------------------
  1. No LoanRequest — only handle(int amount); credit/salary unused
  2. Chain broken: Manager never linked to Logging; lLH.setNextHandler() invalid
  3. CreditScoreHandler: wrong logic (only forwarded if amount < 1000)
  4. Salary/Manager always called next — NPE if chain incomplete
  5. LoggingHandler never received request from chain
  6. ILoanHandler is abstract class — renamed to LoanHandler

Compile & run (from LLD folder)
-------------------------------
  javac Day16ChainOfRes/*.java
  java Day16ChainOfRes.Main
