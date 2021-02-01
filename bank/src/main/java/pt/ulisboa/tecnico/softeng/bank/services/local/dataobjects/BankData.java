package pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects;

import pt.ulisboa.tecnico.softeng.bank.domain.Bank;
import pt.ulisboa.tecnico.softeng.bank.domain.Client;
import pt.ulisboa.tecnico.softeng.bank.domain.Operation;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BankData {
    private String code;
    private String name;
    private List<ClientData> clients;
    private List<BankOperationData> operations;

    public BankData() {
    }

    public BankData(Bank bank) {
        this.code = bank.getCode();
        this.name = bank.getName();

        this.clients = bank.getClientSet().stream().sorted(Comparator.comparing(Client::getName))
                .map(c -> new ClientData(c)).collect(Collectors.toList());

        this.setOperations(bank.getOperationSet().stream().sorted(Comparator.comparing(Operation::getType))
                .map(c -> new BankOperationData(c)).collect(Collectors.toList()));
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ClientData> getClients() {
        return this.clients;
    }

    public void setClients(List<ClientData> clients) {
        this.clients = clients;
    }

    public List<BankOperationData> getOperations() {
        return this.operations;
    }

    public void setOperations(List<BankOperationData> operations) {
        this.operations = operations;
    }

}
