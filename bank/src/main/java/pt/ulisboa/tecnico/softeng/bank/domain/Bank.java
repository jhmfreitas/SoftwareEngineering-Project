package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class Bank extends Bank_Base {
    public static final int SCALE = 1000;

    public static final int CODE_SIZE = 4;

    public Bank(String name, String code) {
        checkArguments(name, code);

        setName(name);
        setCode(code);

        FenixFramework.getDomainRoot().addBank(this);
    }

    public void delete() {
        setRoot(null);

        for (Client client : getClientSet()) {
            client.delete();
        }

        for (Account account : getAccountSet()) {
            account.delete();
        }

        for (Operation operation : getOperationSet()) {
            operation.delete();
        }

        deleteDomainObject();
    }

    private void checkArguments(String name, String code) {
        if (name == null || code == null || name.trim().equals("") || code.trim().equals("")) {
            throw new BankException();
        }

        if (code.length() != Bank.CODE_SIZE) {
            throw new BankException();
        }

        for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
            if (bank.getCode().equals(code)) {
                throw new BankException();
            }
        }
    }

    @Override
    public int getCounter() {
        int counter = super.getCounter() + 1;
        setCounter(counter);
        return counter;
    }

    public Account getAccount(String IBAN) {
        if (IBAN == null || IBAN.trim().equals("")) {
            throw new BankException();
        }

        for (Account account : getAccountSet()) {
            if (account.getIban().equals(IBAN)) {
                return account;
            }
        }

        return null;
    }

    public Operation getOperation(String reference) {
        for (Operation operation : getOperationSet()) {
            if (operation.getReference().equals(reference)) {
                return operation;
            }
        }
        return null;
    }

    public TransferOperation getOperationBySourceAndReference(String transactionSource, String transactionReference) {
        return getOperationSet().stream().filter(TransferOperation.class::isInstance).map(TransferOperation.class::cast)
                .filter(transferOperation -> transferOperation.getTransactionSource() != null
                        && transferOperation.getTransactionReference() != null
                        && transferOperation.getTransactionSource().equals(transactionSource)
                        && transferOperation.getTransactionReference().equals(transactionReference)).findAny().orElse(null);

    }

    public Client getClientById(String id) {
        return getClientSet().stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

}
