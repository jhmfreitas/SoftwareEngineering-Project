package pt.ulisboa.tecnico.softeng.bank.domain;

import org.joda.time.DateTime;

public abstract class Operation extends Operation_Base {
    public enum Type {
        DEPOSIT, WITHDRAW, TRANSFER
    }

    public void init(Bank bank) {
        setReference(bank.getCode() + bank.getCounter());
        setTime(DateTime.now());

        setBank(bank);
    }

    public void delete() {
        setBank(null);

        deleteDomainObject();
    }

    public String revert() {
        setCancellation(getReference() + "_CANCEL");
        return doRevert();
    }

    public abstract Operation.Type getType();

    protected abstract String doRevert();

    public abstract Account getSourceAccount();

    public abstract Account getTargetAccount();

    public abstract String getSourceIban();

    public abstract String getTargetIban();

    public abstract long getValue();

    public abstract String getTransactionSource();

    public abstract String getTransactionReference();

}
