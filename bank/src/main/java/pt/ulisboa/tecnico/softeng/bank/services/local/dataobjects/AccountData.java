package pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects;

import pt.ulisboa.tecnico.softeng.bank.domain.Account;
import pt.ulisboa.tecnico.softeng.bank.domain.Bank;

public class AccountData {
    private String iban;
    private Double balance;
    private Double amount;

    public AccountData() { }

    public AccountData(Account account) {
        this.iban = account.getIban();
        this.balance = new Double(account.getBalance()) / Bank.SCALE;
    }

    public String getIban() {
        return this.iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public Double getBalance() {
        return this.balance;
    }

    public long getBalanceLong() {
        return Math.round(getBalance() * Bank.SCALE) ;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getAmount() {
        return this.amount;
    }

    public long getAmountLong() {
        return Math.round(getAmount() * Bank.SCALE) ;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}
