package pt.ulisboa.tecnico.softeng.tax.services.local.dataobjects;

import pt.ulisboa.tecnico.softeng.tax.domain.IRS;
import pt.ulisboa.tecnico.softeng.tax.domain.TaxPayer;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TaxPayerData {
    private String nif;
    private String name;
    private String address;
    private Map<Integer, Double> taxes = new TreeMap<>();
    private Map<Integer, Double> returns = new TreeMap<>();

    public TaxPayerData() {
    }

    public TaxPayerData(TaxPayer taxPayer) {
        this.nif = taxPayer.getNif();
        this.name = taxPayer.getName();
        this.address = taxPayer.getAddress();
        this.taxes = taxPayer.getToPayPerYear().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new Double(e.getValue()) / IRS.SCALE));
        this.returns = taxPayer.getTaxReturnPerYear().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new Double(e.getValue()) / IRS.SCALE));
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNif() {
        return this.nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<Integer, Double> getTaxes() {
        return this.taxes;
    }

    public void setTaxes(Map<Integer, Double> taxes) {
        this.taxes = taxes;
    }

    public Map<Integer, Double> getReturns() {
        return this.returns;
    }

    public void setReturns(Map<Integer, Double> returns) {
        this.returns = returns;
    }
}
