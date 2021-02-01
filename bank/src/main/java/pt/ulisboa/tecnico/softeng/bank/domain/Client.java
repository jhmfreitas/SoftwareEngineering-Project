package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class Client extends Client_Base {

    public Client(Bank bank, String name) {
        checkArguments(bank, name);

        setId(Integer.toString(bank.getCounter()));
        setName(name);

        setBank(bank);
    }

    public void delete() {
        setBank(null);

        for (Account account : getAccountSet()) {
            account.delete();
        }

        deleteDomainObject();
    }

    private void checkArguments(Bank bank, String name) {
        if (bank == null || name == null || name.trim().equals("")) {
            throw new BankException();
        }
    }

}
