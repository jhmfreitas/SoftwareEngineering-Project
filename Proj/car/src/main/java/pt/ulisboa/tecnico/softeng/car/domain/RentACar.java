package pt.ulisboa.tecnico.softeng.car.domain;

import org.joda.time.LocalDate;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.car.exception.CarException;

import java.util.HashSet;
import java.util.Set;

public class RentACar extends RentACar_Base {
    public final static int SCALE = 1000;

    public RentACar(final String name, final String nif, final String iban, final Processor processor) {
        checkArguments(name, nif, iban);

        setCode(nif + getCounter());
        setName(name);
        setNif(nif);
        setIban(iban);
        setProcessor(processor);

        FenixFramework.getDomainRoot().addRentACar(this);
    }

    public void delete() {
        setRoot(null);

        getProcessor().delete();

        for (final Vehicle vehicle : getVehicleSet()) {
            vehicle.delete();
        }

        deleteDomainObject();
    }

    private void checkArguments(final String name, final String nif, final String iban) {
        if (name == null || name.isEmpty() || nif == null || nif.isEmpty() || iban == null || iban.isEmpty()) {

            throw new CarException();
        }

        for (final RentACar rental : FenixFramework.getDomainRoot().getRentACarSet()) {
            if (rental.getNif().equals(nif)) {
                throw new CarException();
            }
        }
    }

    public boolean hasVehicle(final String plate) {
        return getVehicleSet().stream().anyMatch(v -> v.getPlate().equals(plate));
    }

    public Set<Vehicle> getAvailableVehicles(final Class<?> cls, final LocalDate begin, final LocalDate end) {
        final Set<Vehicle> availableVehicles = new HashSet<>();
        for (final Vehicle vehicle : getVehicleSet()) {
            if (cls == vehicle.getClass() && vehicle.isFree(begin, end)) {
                availableVehicles.add(vehicle);
            }
        }
        return availableVehicles;
    }

    @Override
    public int getCounter() {
        final int counter = super.getCounter() + 1;
        setCounter(counter);
        return counter;
    }

    public Renting getRenting4AdventureId(final String adventureId) {
        return getVehicleSet().stream().flatMap(v -> v.getRentingSet().stream())
                .filter(r -> r.getAdventureId() != null && r.getAdventureId().equals(adventureId)).findFirst()
                .orElse(null);

    }

}
