package pt.ulisboa.tecnico.softeng.bank.exception;

public class BankException extends RuntimeException {
  public BankException() {
    super();
  }

  public BankException(String message) {
    super(message);
  }
}
