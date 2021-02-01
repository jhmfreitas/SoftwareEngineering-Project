package pt.ulisboa.tecnico.softeng.car.domain;

import org.joda.time.LocalDate;

public class Car extends Car_Base {

	public Car(String plate, int kilometers, long price, RentACar rentACar) {
        checkArguments(plate, kilometers, rentACar);

        setPlate(plate.toUpperCase());
	    setKilometers(kilometers);
	    setPrice(price);
	    setRentACar(rentACar);
	}

	public Renting rent(String drivingLicense, LocalDate begin, LocalDate end, String buyerNIF, String buyerIBAN,
						String adventureId) {
		return super.rent(drivingLicense, begin, end, buyerNIF, buyerIBAN, adventureId);
	}
}
