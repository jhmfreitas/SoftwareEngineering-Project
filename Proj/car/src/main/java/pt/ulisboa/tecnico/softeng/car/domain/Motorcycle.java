package pt.ulisboa.tecnico.softeng.car.domain;

public class Motorcycle extends Motorcycle_Base {
	public Motorcycle(String plate, int kilometers, long price, RentACar rentACar) {
		checkArguments(plate, kilometers, rentACar);

		setPlate(plate.toUpperCase());
		setKilometers(kilometers);
		setPrice(price);
		setRentACar(rentACar);
	}
}
