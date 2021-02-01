package pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects;

import java.util.List;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.softeng.broker.domain.Client;

public class ClientData {
	private String brokerCode;
	private String iban;
	private String nif;
	private String drivingLicense;
	private Integer age;
	private List<AdventureData> adventures;

	public ClientData() {
	}

	public ClientData(Client client) {
		this.setBrokerCode(client.getBroker().getCode());
		this.iban = client.getIban();
		this.nif = client.getNif();
		this.drivingLicense = client.getDrivingLicense();
		this.age = client.getAge();

		this.setAdventures(client.getAdventureSet().stream().map(a -> new AdventureData(a))
				.sorted((a1, a2) -> a1.getId().compareTo(a2.getId())).collect(Collectors.toList()));
	}

	public String getBrokerCode() {
		return this.brokerCode;
	}

	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}

	public String getIban() {
		return this.iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getNif() {
		return this.nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getDrivingLicense() {
		return this.drivingLicense;
	}

	public void setDrivingLicense(String drivingLicense) {
		this.drivingLicense = drivingLicense;
	}

	public Integer getAge() {
		return this.age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public List<AdventureData> getAdventures() {
		return this.adventures;
	}

	public void setAdventures(List<AdventureData> adventures) {
		this.adventures = adventures;
	}

}
