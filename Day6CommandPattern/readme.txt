Day6CommandPattern — Command pattern (hands-on)
================================================

What this module demonstrates
-------------------------------
You separate "what to do" (a Command object) from "who does the work" (Receiver)
and "who triggers and remembers work" (Invoker).

Roles in this codebase
----------------------
1) Command (interface: BankCommand.Command)
   - Declares execute() and undo().
   - Lets the invoker treat deposit, withdraw, and transfer uniformly.

2) Concrete commands (BankCommand.DepositCommand, WithdrawCommand, TransferCommand)
   - Each instance captures everything needed for one transaction: accounts and
     amount. That way undo() does not need the caller to pass the amount again
     (a common mistake when the "command" is really a reusable service object).

3) Receiver (BankCommand.BankAccount)
   - Knows balances and performs deposit/withdraw. Commands call into it.

4) Invoker (TransactionManager)
   - Calls command.execute() and stores the command in List<Command> history.
   - undoLastTransaction() removes the last command and calls undo() on it.
   - This is where the pattern pays off: macro logging, queues, undo stacks, and
     transaction scripts all become straightforward.

What changed from the earlier version (learning notes)
------------------------------------------------------
- Old: AccountInvoker held three "template" commands (one deposit, one withdraw,
  one transfer). The client passed amounts into execute/undo. That mixes the
  invoker with fixed operation types and forces the client to remember amounts
  for undo — easy to get wrong.

- New: Each transaction is its own Command instance with the amount baked in.
  The invoker only exposes execute(Command) and undoLastTransaction(). History
  records exactly what ran, in order.

How to compile and run (from the LLD folder)
--------------------------------------------
  javac Day6CommandPattern/BankCommand/*.java Day6CommandPattern/TransactionManager.java Day6CommandPattern/Main.java
  java Day6CommandPattern.Main

  (Use Main.java with capital M so the filename matches the class name on
  case-sensitive filesystems.)

Extension ideas
---------------
- Add validation (e.g. insufficient funds) and optionally avoid pushing failed
  commands onto history.
- Add redo() with a second stack.
- Batch commands as a composite Command (macro).
