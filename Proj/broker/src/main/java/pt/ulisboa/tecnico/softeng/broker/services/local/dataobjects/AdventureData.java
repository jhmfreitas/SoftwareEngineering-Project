package pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects;

import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure;
import pt.ulisboa.tecnico.softeng.broker.domain.Client;

public class AdventureData {

    private String id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate begin;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;
    private Client client;
    private Double margin;
    private Adventure.BookRoom bookRoom;
    private Adventure.RentVehicle rentVehicle;
    private Double amount;
    private Adventure.State state;

    private String paymentConfirmation;
    private String paymentCancellation;
    private String roomConfirmation;
    private String roomCancellation;
    private String activityConfirmation;
    private String activityCancellation;

    public AdventureData() {
    }

    AdventureData(Adventure adventure) {
        this.id = adventure.getID();
        this.begin = adventure.getBegin();
        this.end = adventure.getEnd();
        this.client = adventure.getClient();
        this.margin = new Double(adventure.getMargin()) / Adventure.SCALE;
        this.amount = new Double(adventure.getCurrentAmount()) / Adventure.SCALE;
        this.bookRoom = adventure.getBookRoom();
        this.rentVehicle = adventure.getRentVehicle();
        this.state = adventure.getState().getValue();

        this.paymentConfirmation = adventure.getPaymentConfirmation();
        this.paymentCancellation = adventure.getPaymentCancellation();
        this.roomConfirmation = adventure.getRoomConfirmation();
        this.roomCancellation = adventure.getRoomCancellation();
        this.activityConfirmation = adventure.getActivityConfirmation();
        this.activityCancellation = adventure.getActivityCancellation();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Double getAmount() {
        return this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public long getAmountLong() {
        return Math.round(getAmount() * Adventure.SCALE);
    }

    public Adventure.State getState() {
        return this.state;
    }

    public void setState(Adventure.State state) {
        this.state = state;
    }

    public String getPaymentConfirmation() {
        return this.paymentConfirmation;
    }

    public void setPaymentConfirmation(String paymentConfirmation) {
        this.paymentConfirmation = paymentConfirmation;
    }

    public String getPaymentCancellation() {
        return this.paymentCancellation;
    }

    public void setPaymentCancellation(String paymentCancellation) {
        this.paymentCancellation = paymentCancellation;
    }

    public String getRoomConfirmation() {
        return this.roomConfirmation;
    }

    public void setRoomConfirmation(String roomConfirmation) {
        this.roomConfirmation = roomConfirmation;
    }

    public String getRoomCancellation() {
        return this.roomCancellation;
    }

    public void setRoomCancellation(String roomCancellation) {
        this.roomCancellation = roomCancellation;
    }

    public String getActivityConfirmation() {
        return this.activityConfirmation;
    }

    public void setActivityConfirmation(String activityConfirmation) {
        this.activityConfirmation = activityConfirmation;
    }

    public String getActivityCancellation() {
        return this.activityCancellation;
    }

    public void setActivityCancellation(String activityCancellation) {
        this.activityCancellation = activityCancellation;
    }

    public Double getMargin() {
        return margin;
    }

    public long getMarginLong() {
        return Math.round(getMargin() * Adventure.SCALE);
    }

    public void setMargin(Double margin) {
        this.margin = margin;
    }

    public Adventure.BookRoom getBookRoom() {
        return this.bookRoom;
    }

    public void setBookRoom(Adventure.BookRoom bookRoom) {
        this.bookRoom = bookRoom;
    }


    public Adventure.RentVehicle getRentVehicle() {
        return this.rentVehicle;
    }

    public void setRentVehicle(Adventure.RentVehicle rentVehicle) {
        this.rentVehicle = rentVehicle;
    }

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

}
