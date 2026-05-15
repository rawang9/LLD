package Day6CommandPattern;

import Day6CommandPattern.BankCommand.BankAccount;
import Day6CommandPattern.BankCommand.DepositCommand;
import Day6CommandPattern.BankCommand.TransferCommand;
import Day6CommandPattern.BankCommand.WithdrawCommand;

class Main {

    public static void main(String[] args) {
        System.out.println("Learning the Command pattern");
        BankAccount account1 = new BankAccount(1000);
        BankAccount account2 = new BankAccount(2000);

        TransactionManager transactions = new TransactionManager();
        System.out.println("Initial balance: Account 1: " + account1.getBalance());
        System.out.println("Initial balance: Account 2: " + account2.getBalance());
        System.out.println("--------------------------------");


        transactions.execute(new DepositCommand(account1, 500));
        transactions.execute(new WithdrawCommand(account1, 200));
        transactions.execute(new TransferCommand(account1, account2, 300));

        System.out.println("\n--- Undo last 3 transactions (LIFO) ---");
        transactions.undoLastTransaction();
        transactions.undoLastTransaction();
        transactions.undoLastTransaction();
    }
}
