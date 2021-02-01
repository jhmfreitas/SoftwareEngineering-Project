package pt.ulisboa.tecnico.softeng.car.services.local.dataobjects;

public class RentACarData {
    private String code;
    private String name;
    private String nif;
    private String iban;
    private Integer numVehicles;

    public RentACarData() {
    }

    public RentACarData(String code, String name, String nif, String iban, Integer numVehicles) {
        this.code = code;
        this.name = name;
        this.nif = nif;
        this.iban = iban;
        this.numVehicles = numVehicles;
    }

    public Integer getNumVehicles() {
        return numVehicles;
    }

    public void setNumVehicles(int numVehicles) {
        this.numVehicles = numVehicles;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }
}
