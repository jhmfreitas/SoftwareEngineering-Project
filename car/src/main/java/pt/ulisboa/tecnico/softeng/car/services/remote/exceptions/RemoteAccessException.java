package pt.ulisboa.tecnico.softeng.car.services.remote.exceptions;

public class RemoteAccessException extends RuntimeException {
	public RemoteAccessException() {
		super();
	}

	public RemoteAccessException(String message) {
		super(message);
	}
}
