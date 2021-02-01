package pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects;

import pt.ulisboa.tecnico.softeng.bank.domain.Client;

import java.util.List;
import java.util.stream.Collectors;

public class ClientData {
    private String bankCode;
    private String bankName;
    private String id;
    private String name;
    private List<AccountData> accounts;

    public ClientData() {
    }

    public ClientData(Client client) {
        this.bankCode = client.getBank().getCode();
        this.bankName = client.getBank().getName();
        this.id = client.getId();
        this.name = client.getName();

        this.accounts = client.getAccountSet().stream().sorted((a1, a2) -> a1.getIban().compareTo(a2.getIban()))
                .map(a -> new AccountData(a)).collect(Collectors.toList());
    }

    public String getBankCode() {
        return this.bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AccountData> getAccounts() {
        return this.accounts;
    }

    public void setAccounts(List<AccountData> accounts) {
        this.accounts = accounts;
    }

}
