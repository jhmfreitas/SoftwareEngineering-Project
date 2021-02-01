package pt.ulisboa.tecnico.softeng.car.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.car.exception.CarException;

public class Renting extends Renting_Base {
	private static String drivingLicenseFormat = "^[a-zA-Z]+\\d+$";
	private static final String TYPE = "RENTAL";

	public Renting(String drivingLicense, LocalDate begin, LocalDate end, Vehicle vehicle, String buyerNif,
			String buyerIban) {
		checkArguments(drivingLicense, begin, end, vehicle);

		setKilometers(-1);
		setCancelledInvoice(false);

		setReference(Integer.toString(vehicle.getRentACar().getCounter()));
		setDrivingLicense(drivingLicense);
		setBegin(begin);
		setEnd(end);
		setClientNif(buyerNif);
		setClientIban(buyerIban);
		setPrice(vehicle.getPrice() * (end.getDayOfYear() - begin.getDayOfYear() + 1));
		setTime(DateTime.now());

		setVehicle(vehicle);
	}

	public void delete() {
		setVehicle(null);
		setProcessor(null);
		deleteDomainObject();
	}

	private void checkArguments(String drivingLicense, LocalDate begin, LocalDate end, Vehicle vehicle) {
		if (drivingLicense == null || !drivingLicense.matches(drivingLicenseFormat) || begin == null || end == null
				|| vehicle == null || end.isBefore(begin)) {
			throw new CarException();
		}
	}

	public boolean isCancelled() {
		return getCancellationReference() != null && getCancellationDate() != null;
	}

	/**
	 * @param begin
	 * @param end
	 * @return <code>true</code> if this Renting conflicts with the given date
	 *         range.
	 */
	public boolean conflict(LocalDate begin, LocalDate end) {
		if (end.isBefore(begin)) {
			throw new CarException("Error: end date is before begin date.");
		} else if ((begin.equals(this.getBegin()) || begin.isAfter(this.getBegin()))
				&& (begin.isBefore(this.getEnd()) || begin.equals(this.getEnd()))) {
			return true;
		} else if ((end.equals(this.getEnd()) || end.isBefore(this.getEnd()))
				&& (end.isAfter(this.getBegin()) || end.isEqual(this.getBegin()))) {
			return true;
		} else if (begin.isBefore(this.getBegin()) && end.isAfter(this.getEnd())) {
			return true;
		}

		return false;
	}

	/**
	 * Settle this renting and update the kilometers in the vehicle.
	 * 
	 * @param kilometers
	 */
	public void checkout(Integer kilometers) {
		if (isCancelled()) {
			throw new CarException();
		}

		setKilometers(kilometers);
		getVehicle().addKilometers(kilometers);
	}

	public String cancel() {
		if (getKilometers() != -1) {
			throw new CarException();
		}

		setCancellationReference(getReference() + "CANCEL");
		setCancellationDate(LocalDate.now());

		this.getVehicle().getRentACar().getProcessor().submitRenting(this);

		return getCancellationReference();
	}

	public String getType() {
		return TYPE;
	}

	public boolean isCancelledInvoice() {
		return getCancelledInvoice();
	}

}
