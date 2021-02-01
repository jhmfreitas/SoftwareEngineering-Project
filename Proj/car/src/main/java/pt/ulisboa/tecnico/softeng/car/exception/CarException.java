package pt.ulisboa.tecnico.softeng.car.exception;

@SuppressWarnings("serial")
public class CarException extends RuntimeException {
  public CarException() {
    super();
  }

  public CarException(String message) {
    super(message);
  }
}
