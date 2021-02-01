package pt.ulisboa.tecnico.softeng.car.services.local.dataobjects;

import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import pt.ulisboa.tecnico.softeng.car.domain.RentACar;
import pt.ulisboa.tecnico.softeng.car.domain.Renting;
import pt.ulisboa.tecnico.softeng.car.domain.Vehicle;

public class RentingData {
	private String reference;
	private String plate;
	private String drivingLicense;
	private String rentACarCode;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate begin;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate end;
	private String paymentReference;
	private String invoiceReference;
	private String cancellationReference;
	private Double price;
	private String buyerNIF;
	private String buyerIBAN;
	private Vehicle.Type type;
	private String typeValue;
	private Integer kilometers;
	private String adventureId;

	public RentingData() {
	}

	public RentingData(Renting renting) {
		this.reference = renting.getReference();
		this.plate = renting.getVehicle().getPlate();
		this.drivingLicense = renting.getDrivingLicense();
		this.rentACarCode = renting.getVehicle().getRentACar().getCode();
		this.begin = renting.getBegin();
		this.end = renting.getEnd();
		this.paymentReference = renting.getPaymentReference();
		this.invoiceReference = renting.getInvoiceReference();
		this.cancellationReference = renting.getCancellationReference();
		this.price = new Double(renting.getPrice()) / RentACar.SCALE;
		this.adventureId = renting.getAdventureId();
	}

	/**
	 * @return the renting reference
	 */
	public String getReference() {
		return this.reference;
	}

	/**
	 * @return the vehicle plate
	 */
	public String getPlate() {
		return this.plate;
	}

	/**
	 * @return the drivingLicense
	 */
	public String getDrivingLicense() {
		return this.drivingLicense;
	}

	/**
	 * @return the rentACarCode
	 */
	public String getRentACarCode() {
		return this.rentACarCode;
	}

	/**
	 * @return the begin
	 */
	public LocalDate getBegin() {
		return this.begin;
	}

	/**
	 * @return the end
	 */
	public LocalDate getEnd() {
		return this.end;
	}

	public String getPaymentReference() {
		return this.paymentReference;
	}

	public String getInvoiceReference() {
		return this.invoiceReference;
	}

	public Double getPrice() {
		return this.price;
	}

	public long getPriceLong() { return Math.round(getPrice() * RentACar.SCALE); }

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

	public Vehicle.Type getType() {
		return this.type;
	}

	public void setType(Vehicle.Type type) {
		this.type = type;
	}

	public void setDrivingLicense(String drivingLicense) {
		this.drivingLicense = drivingLicense;
	}

	public void setBegin(LocalDate begin) {
		this.begin = begin;
	}

	public void setEnd(LocalDate end) {
		this.end = end;
	}

	public String getCancellationReference() {
		return this.cancellationReference;
	}

	public void setCancellationReference(String cancellationReference) {
		this.cancellationReference = cancellationReference;
	}

	public Integer getKilometers() {
		return this.kilometers;
	}

	public void setKilometers(Integer kilometers) {
		this.kilometers = kilometers;
	}

	public String getTypeValue() {
		return this.typeValue;
	}

	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}

	public String getAdventureId() {
		return this.adventureId;
	}

	public void setAdventureId(String adventureId) {
		this.adventureId = adventureId;
	}
}
