package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class Account extends Account_Base {

    public Account(Bank bank, Client client) {
        checkArguments(bank, client);

        setIban(bank.getCode() + bank.getCounter());
        setBalance(0);

        setClient(client);
        setBank(bank);
    }

    public void delete() {
        setBank(null);
        setClient(null);

        for (Operation operation : getSingleOperationSet()) {
            operation.delete();
        }

        deleteDomainObject();
    }

    private void checkArguments(Bank bank, Client client) {
        if (bank == null || client == null) {
            throw new BankException();
        }

        if (!bank.getClientSet().contains(client)) {
            throw new BankException();
        }
    }

    public DepositOperation deposit(long amount) {
        if (amount <= 0) {
            throw new BankException();
        }

        setBalance(getBalance() + amount);

        DepositOperation depositOperation = new DepositOperation();
        depositOperation.init(this, amount);

        return depositOperation;
    }

    public WithdrawOperation withdraw(long amount) {
        if (amount <= 0 || amount > getBalance()) {
            throw new BankException();
        }

        setBalance(getBalance() - amount);

        WithdrawOperation withdrawOperation = new WithdrawOperation();
        withdrawOperation.init(this, amount);

        return withdrawOperation;
    }

}
