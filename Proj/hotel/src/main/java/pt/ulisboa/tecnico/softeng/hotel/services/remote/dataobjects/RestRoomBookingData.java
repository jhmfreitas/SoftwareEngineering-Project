package pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.joda.time.LocalDate;
import pt.ulisboa.tecnico.softeng.hotel.domain.Booking;

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

    public RestRoomBookingData(Booking booking) {
        this.reference = booking.getReference();
        this.cancellation = booking.getCancellation();
        this.hotelName = booking.getRoom().getHotel().getName();
        this.hotelCode = booking.getRoom().getHotel().getCode();
        this.roomNumber = booking.getRoom().getNumber();
        this.bookRoom = booking.getRoom().getType().name();
        this.arrival = booking.getArrival();
        this.departure = booking.getDeparture();
        this.price = booking.getPrice();
        this.buyerNif = booking.getBuyerNif();
        this.buyerIban = booking.getBuyerIban();
        this.cancellationDate = booking.getCancellationDate();
        this.paymentReference = booking.getPaymentReference();
        this.invoiceReference = booking.getInvoiceReference();
        this.adventureId = booking.getAdventureId();
    }

    public RestRoomBookingData(String roomType, LocalDate arrival, LocalDate departure, String nifBuyer,
                               String ibanBuyer, String adventureId) {
        this.bookRoom = roomType;
        this.arrival = arrival;
        this.departure = departure;
        this.buyerNif = nifBuyer;
        this.buyerIban = ibanBuyer;
        this.adventureId = adventureId;
    }

    public String getReference() {
        return this.reference;
    }

    public String getCancellation() {
        return this.cancellation;
    }

    public String getHotelName() {
        return this.hotelName;
    }

    public String getHotelCode() {
        return this.hotelCode;
    }

    public String getRoomNumber() {
        return this.roomNumber;
    }

    public String getBookRoom() {
        return this.bookRoom;
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

    public long getPrice() {
        return this.price;
    }

    public String getPaymentReference() {
        return this.paymentReference;
    }

    public String getInvoiceReference() {
        return this.invoiceReference;
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
