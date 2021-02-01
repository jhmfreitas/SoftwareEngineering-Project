package pt.ulisboa.tecnico.softeng.car.services.local;

import org.joda.time.LocalDate;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.car.domain.*;
import pt.ulisboa.tecnico.softeng.car.exception.CarException;
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.RentACarData;
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.RentingData;
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.VehicleData;
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.car.services.remote.dataobjects.RestRentingData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RentACarInterface {
    @Atomic(mode = Atomic.TxMode.READ)
    public List<RentACarData> getRentACars() {
        return FenixFramework.getDomainRoot().getRentACarSet().stream()
                .map(r -> new RentACarData(r.getCode(), r.getName(), r.getNif(), r.getIban(), r.getVehicleSet().size()))
                .collect(Collectors.toList());
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void createRentACar(final RentACarData rentACarData) {
        final Processor processor = new Processor(new BankInterface(), new TaxInterface());
        new RentACar(rentACarData.getName(), rentACarData.getNif(), rentACarData.getIban(), processor);
    }

    @Atomic(mode = Atomic.TxMode.READ)
    public List<VehicleData> getVehicles(final String code) {
        final RentACar rentACar = getRentACar(code);
        return rentACar.getVehicleSet().stream().map(v -> new VehicleData(getVehicleType(v), v.getPlate(),
                v.getKilometers(), v.getPrice(), toRentACarData(v.getRentACar()))).collect(Collectors.toList());
    }

    @Atomic(mode = Atomic.TxMode.READ)
    public List<RentingData> getRentings(final String code, final String plate) {
        final Vehicle vehicle = getVehicle(code, plate);
        return vehicle.getRentingSet().stream().map(RentingData::new).collect(Collectors.toList());
    }

    @Atomic(mode = Atomic.TxMode.READ)
    public List<RentingData> getPendingRentings(final String code) {
        RentACar rentACar = getRentACar(code);
        Processor processor = rentACar.getProcessor();
        return processor.getRentingSet().stream().map(RentingData::new).collect(Collectors.toList()); 
    }

    @Atomic(mode = Atomic.TxMode.READ)
    public RentingData getRentingData(final String code, final String plate, final String reference) {
        final Renting renting = getVehicle(code, plate).getRentingSet().stream()
                .filter(r -> r.getReference().equals(reference)).findFirst().orElse(null);

        return renting == null ? new RentingData() : new RentingData(renting);
    }

    @Atomic(mode = Atomic.TxMode.READ)
    public RestRentingData getRentingData(final String reference) {
        return new RestRentingData(getRenting(reference));
    }

    private Renting getRenting(final String reference) {
        return FenixFramework.getDomainRoot().getRentACarSet().stream()
                .flatMap(rac -> rac.getVehicleSet().stream()).flatMap(v -> v.getRentingSet().stream())
                .filter(r -> r.getReference().equals(reference)).findFirst().orElseThrow(CarException::new);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public RentingData cancelRenting(final String code, final String plate, final String reference) {
        final Renting renting = getVehicle(code, plate).getRentingSet().stream()
                .filter(r -> r.getReference().equals(reference)).findFirst().orElse(null);

        if (renting == null) {
            return new RentingData();
        } else {
            renting.cancel();
            return new RentingData(renting);
        }
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public String cancelRenting(final String reference) {
        final Renting renting = getRenting(reference);
        if (renting.getCancellationReference() != null) {
            return renting.getCancellationReference();
        }

        renting.cancel();
        return renting.getCancellationReference();
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public RentingData checkoutRenting(final String code, final String plate, final String reference, final Integer kms) {
        if (kms == null) {
            throw new CarException();
        }

        final Renting renting = getVehicle(code, plate).getRentingSet().stream()
                .filter(r -> r.getReference().equals(reference)).findFirst().orElse(null);

        if (renting == null) {
            return new RentingData();
        } else {
            renting.checkout(kms);
            return new RentingData(renting);
        }
    }

    @Atomic(mode = Atomic.TxMode.READ)
    public RentACarData getRentACarData(final String code) {
        return toRentACarData(getRentACar(code));
    }


    @Atomic(mode = Atomic.TxMode.WRITE)
    public void createVehicle(final String code, final VehicleData vehicleData) {
        if (vehicleData.getKilometers() == null || vehicleData.getPrice() == null) {
            throw new CarException();
        }

        final RentACar rentACar = getRentACar(code);
        if (vehicleData.getType() == Vehicle.Type.CAR) {
            new Car(vehicleData.getPlate(), vehicleData.getKilometers(), vehicleData.getPrice() != null ? vehicleData.getPriceLong() : -1, rentACar);
        } else {
            new Motorcycle(vehicleData.getPlate(), vehicleData.getKilometers(), vehicleData.getPrice() != null ? vehicleData.getPriceLong() : -1, rentACar);
        }
    }

    @Atomic(mode = Atomic.TxMode.READ)
    public VehicleData getVehicleByPlate(final String code, final String plate) {
        return getVehicles(code).stream().filter(v -> v.getPlate().equals(plate)).findFirst().orElse(null);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public String rent(final String code, final String plate, final String drivingLicense, final String buyerNIF, final String buyerIBAN,
                       final LocalDate begin, final LocalDate end, final String adventureId) {

        final Renting renting = getReting4AdventureId(adventureId);
        if (renting != null) {
            return renting.getReference();
        }

        return getVehicle(code, plate).rent(drivingLicense, begin, end, buyerNIF, buyerIBAN, adventureId)
                .getReference();
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public RestRentingData rent(final String type, final String license, final String nif, final String iban, final LocalDate begin, final LocalDate end,
                                final String adventureId) {
        final Renting renting = getReting4AdventureId(adventureId);
        if (renting != null) {
            return new RestRentingData(renting);
        }

        return new RestRentingData(
                rent(type.equals("CAR") ? Car.class : Motorcycle.class, license, nif, iban, begin, end, adventureId));

    }

    @Atomic(mode = Atomic.TxMode.READ)
    public VehicleData getVehicleData(final String code, final String plate) {
        final Vehicle v = getVehicle(code, plate);
        return new VehicleData(getVehicleType(v), v.getPlate(), v.getKilometers(), v.getPrice(), getRentACarData(code));
    }

    private Vehicle getVehicle(final String code, final String plate) {
        return getRentACar(code).getVehicleSet().stream().filter(v -> v.getPlate().equals(plate)).findFirst()
                .orElse(null);
    }

    private RentACarData toRentACarData(final RentACar rentACar) {
        return new RentACarData(rentACar.getCode(), rentACar.getName(), rentACar.getNif(), rentACar.getIban(),
                rentACar.getVehicleSet().size());
    }

    private RentACar getRentACar(final String code) {
        return FenixFramework.getDomainRoot().getRentACarSet().stream().filter(h -> h.getCode().equals(code))
                .findFirst().orElseThrow(() -> new CarException());
    }

    private Vehicle.Type getVehicleType(final Vehicle vehicle) {
        if (vehicle instanceof Car) {
            return Vehicle.Type.CAR;
        } else {
            return Vehicle.Type.MOTORCYCLE;
        }
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void deleteRentACars() {
        for (final RentACar rentACar : FenixFramework.getDomainRoot().getRentACarSet()) {
            rentACar.delete();
        }
    }

    private Renting getReting4AdventureId(final String adventureId) {
        for (final RentACar rentACar : FenixFramework.getDomainRoot().getRentACarSet()) {
            final Renting renting = rentACar.getRenting4AdventureId(adventureId);
            if (renting != null) {
                return renting;
            }
        }
        return null;
    }

    private Set<Vehicle> getAllAvailableVehicles(final Class<?> cls, final LocalDate begin, final LocalDate end) {
        final Set<Vehicle> vehicles = new HashSet<>();
        for (final RentACar rentACar : FenixFramework.getDomainRoot().getRentACarSet()) {
            vehicles.addAll(rentACar.getAvailableVehicles(cls, begin, end));
        }
        return vehicles;
    }

    private Set<Vehicle> getAllAvailableMotorcycles(final LocalDate begin, final LocalDate end) {
        return getAllAvailableVehicles(Motorcycle.class, begin, end);
    }

    private Set<Vehicle> getAllAvailableCars(final LocalDate begin, final LocalDate end) {
        return getAllAvailableVehicles(Car.class, begin, end);
    }

    private Renting rent(final Class<? extends Vehicle> vehicleType, final String drivingLicense, final String buyerNif, final String buyerIban,
                         final LocalDate begin, final LocalDate end, final String adventureId) {
        final Set<Vehicle> availableVehicles;

        if (vehicleType == Car.class) {
            availableVehicles = getAllAvailableCars(begin, end);
        } else {
            availableVehicles = getAllAvailableMotorcycles(begin, end);
        }

        return availableVehicles.stream().findFirst()
                .map(v -> v.rent(drivingLicense, begin, end, buyerNif, buyerIban, adventureId))
                .orElseThrow(CarException::new);
    }

}
