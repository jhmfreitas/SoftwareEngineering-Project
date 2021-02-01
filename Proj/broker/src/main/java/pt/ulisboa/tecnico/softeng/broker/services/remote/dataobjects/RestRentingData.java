package pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class RestRentingData {
	private String reference;
	private String plate;
	private String drivingLicense;
	private String rentACarCode;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate begin;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate end;
	private String paymentReference;
	private String invoiceReference;
	private String cancellationReference;
	private long price;
	private String buyerNIF;
	private String buyerIBAN;
	private String typeValue;
	private Integer kilometers;
	private String adventureId;

	public RestRentingData() {
	}

	public RestRentingData(String type, String drivingLicense, String nif, String iban, LocalDate begin, LocalDate end,
			String adventureId) {
		this.typeValue = type;
		this.drivingLicense = drivingLicense;
		this.buyerNIF = nif;
		this.buyerIBAN = iban;
		this.begin = begin;
		this.end = end;
		this.setAdventureId(adventureId);
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getPlate() {
		return this.plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	public String getDrivingLicense() {
		return this.drivingLicense;
	}

	public void setDrivingLicense(String drivingLicense) {
		this.drivingLicense = drivingLicense;
	}

	public String getRentACarCode() {
		return this.rentACarCode;
	}

	public void setRentACarCode(String rentACarCode) {
		this.rentACarCode = rentACarCode;
	}

	public LocalDate getBegin() {
		return this.begin;
	}

	public void setBegin(LocalDate begin) {
		this.begin = begin;
	}

	public LocalDate getEnd() {
		return this.end;
	}

	public void setEnd(LocalDate end) {
		this.end = end;
	}

	public String getPaymentReference() {
		return this.paymentReference;
	}

	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}

	public String getInvoiceReference() {
		return this.invoiceReference;
	}

	public void setInvoiceReference(String invoiceReference) {
		this.invoiceReference = invoiceReference;
	}

	public String getCancellationReference() {
		return this.cancellationReference;
	}

	public void setCancellationReference(String cancellationReference) {
		this.cancellationReference = cancellationReference;
	}

	public long getPrice() {
		return this.price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public String getBuyerNIF() {
		return this.buyerNIF;
	}

	public void setBuyerNIF(String buyerNIF) {
		this.buyerNIF = buyerNIF;
	}

	public String getBuyerIBAN() {
		return this.buyerIBAN;
	}

	public void setBuyerIBAN(String buyerIBAN) {
		this.buyerIBAN = buyerIBAN;
	}

	public String getTypeValue() {
		return this.typeValue;
	}

	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}

	public Integer getKilometers() {
		return this.kilometers;
	}

	public void setKilometers(Integer kilometers) {
		this.kilometers = kilometers;
	}

	public String getAdventureId() {
		return this.adventureId;
	}

	public void setAdventureId(String adventureId) {
		this.adventureId = adventureId;
	}

}
