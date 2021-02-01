package pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.joda.time.LocalDate;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure;


public class RestRoomBookingData {
    private String reference;
    private String cancellation;
    private String hotelName;
    private String hotelCode;
    private String roomNumber;
    private String bookRoom;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate arrival;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate departure;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate cancellationDate;
    private long price;
    private String paymentReference;
    private String invoiceReference;
    private String buyerNif;
    private String buyerIban;
    private String adventureId;

    public RestRoomBookingData() {
    }

    public RestRoomBookingData(Adventure.BookRoom single, LocalDate arrival, LocalDate departure, String nifAsBuyer, String iban,
                               String adventureId) {
        this.bookRoom = single.toString();
        this.arrival = arrival;
        this.departure = departure;
        this.buyerNif = nifAsBuyer;
        this.buyerIban = iban;
        this.adventureId = adventureId;
    }

    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCancellation() {
        return this.cancellation;
    }

    public void setCancellation(String cancellation) {
        this.cancellation = cancellation;
    }

    public String getHotelName() {
        return this.hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getHotelCode() {
        return this.hotelCode;
    }

    public void setHotelCode(String hotelCode) {
        this.hotelCode = hotelCode;
    }

    public String getRoomNumber() {
        return this.roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBookRoom() {
        return this.bookRoom;
    }

    public void setBookRoom(String bookRoom) {
        this.bookRoom = bookRoom;
    }

    public LocalDate getArrival() {
        return this.arrival;
    }

    public void setArrival(LocalDate arrival) {
        this.arrival = arrival;
    }

    public LocalDate getDeparture() {
        return this.departure;
    }

    public void setDeparture(LocalDate departure) {
        this.departure = departure;
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

    public void setPaymentReference(String reference) {
        this.paymentReference = reference;
    }

    public String getInvoiceReference() {
        return this.invoiceReference;
    }

    public void setInvoiceReference(String invoiceReference) {
        this.invoiceReference = invoiceReference;
    }

    public String getBuyerNif() {
        return this.buyerNif;
    }

    public void setBuyerNif(String buyerNif) {
        this.buyerNif = buyerNif;
    }

    public String getBuyerIban() {
        return this.buyerIban;
    }

    public void setBuyerIban(String buyerIban) {
        this.buyerIban = buyerIban;
    }

    public String getAdventureId() {
        return this.adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }

}
