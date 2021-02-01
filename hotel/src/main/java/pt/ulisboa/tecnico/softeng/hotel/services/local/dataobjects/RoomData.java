package pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.softeng.hotel.domain.Booking;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;

public class RoomData {
	private String hotelCode;
	private String hotelName;
	private String number;
	private Room.Type type;
	private List<RoomBookingData> bookings;

	public RoomData() {
	}

	public RoomData(Room room) {
		this.hotelCode = room.getHotel().getCode();
		this.hotelName = room.getHotel().getName();
		this.number = room.getNumber();
		this.type = room.getType();

		this.bookings = room.getBookingSet().stream().sorted(Comparator.comparing(Booking::getArrival))
				.map(RoomBookingData::new).collect(Collectors.toList());
	}

	public String getHotelCode() {
		return this.hotelCode;
	}

	public void setHotelCode(String hotelCode) {
		this.hotelCode = hotelCode;
	}

	public String getHotelName() {
		return this.hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Room.Type getType() {
		return this.type;
	}

	public void setType(Room.Type type) {
		this.type = type;
	}

	public List<RoomBookingData> getBookings() {
		return this.bookings;
	}

	public void setBookings(List<RoomBookingData> bookings) {
		this.bookings = bookings;
	}

}
