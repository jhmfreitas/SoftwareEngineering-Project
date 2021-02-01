package pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class RestActivityBookingData {
	private String reference;
	private String nif;
	private String iban;
	private String name;
	private String code;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate begin;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate end;
	private int age;
	private String adventureId;
	private String cancellation;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate cancellationDate;
	private long price;
	private String paymentReference;
	private String invoiceReference;

	public RestActivityBookingData() {
	}

	public RestActivityBookingData(LocalDate begin, LocalDate end, int age, String nifAsBuyer, String iban,
			String adventureId) {
		this.begin = begin;
		this.end = end;
		this.age = age;
		this.nif = nifAsBuyer;
		this.iban = iban;
		this.adventureId = adventureId;
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getNif() {
		return this.nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getIban() {
		return this.iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public int getAge() {
		return this.age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getAdventureId() {
		return this.adventureId;
	}

	public void setAdventureId(String adventureId) {
		this.adventureId = adventureId;
	}

	public String getCancellation() {
		return this.cancellation;
	}

	public void setCancellation(String cancellation) {
		this.cancellation = cancellation;
	}

	public LocalDate getCancellationDate() {
		return this.cancellationDate;
	}

	public void setCancellationDate(LocalDate cancellationDate) {
		this.cancellationDate = cancellationDate;
	}

	public long getPrice() {
		return this.price;
	}

	public void setPrice(long price) {
		this.price = price;
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

}
