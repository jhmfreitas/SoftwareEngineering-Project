package pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.activity.domain.Booking;

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

    public RestActivityBookingData(Booking booking) {
        this.reference = booking.getReference();
        this.cancellation = booking.getCancel();
        this.name = booking.getActivityOffer().getActivity().getActivityProvider().getName();
        this.code = booking.getActivityOffer().getActivity().getActivityProvider().getCode();
        this.nif = booking.getBuyerNif();
        this.iban = booking.getBuyerIban();
        this.begin = booking.getActivityOffer().getBegin();
        this.end = booking.getActivityOffer().getEnd();
        this.age = booking.getAge();
        this.adventureId = booking.getAdventureId();
        this.cancellationDate = booking.getCancellationDate();
        this.price = booking.getAmount();
        this.paymentReference = booking.getPaymentReference();
        this.invoiceReference = booking.getInvoiceReference();
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
