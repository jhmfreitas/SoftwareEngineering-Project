package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public abstract class SingleOperation extends SingleOperation_Base {

    public void init(Account account, long value) {
        checkArguments(account, value);

        setAccount(account);
        setValue(value);

        init(account.getBank());
    }

    private void checkArguments(Account account, double value) {
        if (account == null || value <= 0) {
            throw new BankException();
        }
    }

    @Override
    public void delete() {
        setAccount(null);

        super.delete();
    }

    @Override
    public Account getSourceAccount() {
        return getAccount();
    }

    @Override
    public Account getTargetAccount() {
        return null;
    }

    @Override
    public String getSourceIban() {
        return getSourceAccount().getIban();
    }

    @Override
    public String getTargetIban() {
        return null;
    }


    @Override
    public String getTransactionSource() {
        return null;
    }

    @Override
    public String getTransactionReference() {
        return null;
    }

}
