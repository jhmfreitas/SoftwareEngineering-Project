package pt.ulisboa.tecnico.softeng.broker.exception;

public class BrokerException extends RuntimeException {
  public BrokerException() {
    super();
  }

  public BrokerException(String message) {
    super(message);
  }
}
