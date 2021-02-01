package pt.ulisboa.tecnico.softeng.hotel.exception;

public class HotelException extends RuntimeException {
  public HotelException() {
    super();
  }

  public HotelException(String message) {
    super(message);
  }
}
