package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface

class CarPersistenceSpockTest extends SpockPersistenceTestAbstractClass {

    def ADVENTURE_ID = 'AdventureId'
    def NAME1 = 'eartz'
    def PLATE_CAR1 = 'aa-00-11'
    def PLATE_CAR2 = 'aa-00-12'
    def DRIVING_LICENSE = 'br123'
    def date1 = LocalDate.parse('2018-01-06')
    def date2 = LocalDate.parse('2018-01-07')
    def NIF = 'NIF'
    def IBAN = 'IBAN'
    def IBAN_BUYER = 'IBAN'

    @Override
    def whenCreateInDatabase() {
        def bankInterface = new BankInterface()
        def taxInterface = new TaxInterface()
        def processor = new Processor(bankInterface, taxInterface)

        def rentACar = new RentACar(NAME1, NIF, IBAN, processor)
        def car = new Car(PLATE_CAR1, 10, 10, rentACar)
        def motorcycle = new Motorcycle(PLATE_CAR2, 20, 5, rentACar)
        car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID)
    }

    @Override
    def thenAssert() {
        assert FenixFramework.getDomainRoot().getRentACarSet().size() == 1

        def rentACar = new ArrayList<>(FenixFramework.getDomainRoot().getRentACarSet()).get(0)
        assert rentACar.getVehicleSet().size() == 2
        def processor = rentACar.getProcessor()
        assert rentACar.getName().equals(NAME1)
        assert rentACar.getNif().equals(NIF)
        assert rentACar.getIban().equals(IBAN)
        assert processor != null
        assert processor.getRentingSet().size() == 1

        for (def vehicle : rentACar.getVehicleSet()) {
            if (vehicle instanceof Car) {
                assert vehicle.getPlate().equals(PLATE_CAR1.toUpperCase())
                assert vehicle.getKilometers().intValue() == 10
                assert vehicle.getPrice() == 10
            }
            if (vehicle instanceof Motorcycle) {
                assert vehicle.getPlate().equals(PLATE_CAR2.toUpperCase())
                assert vehicle.getKilometers().intValue() == 20
                assert vehicle.getPrice() == 5
            }
        }

        for (def vehicle : rentACar.getVehicleSet()) {
            if (vehicle instanceof Car) {
                assert vehicle.getRentingSet().size() == 1
                def renting = new ArrayList<>(vehicle.getRentingSet()).get(0)
                assert renting.getDrivingLicense().equals(DRIVING_LICENSE)
                assert renting.getBegin() == date1
                assert renting.getEnd() == date2
                assert renting.getClientNif().equals(NIF)
                assert renting.getClientIban().equals(IBAN)
                assert renting.getTime() != null
                assert renting.getProcessor() != null
            }
            if (vehicle instanceof Motorcycle) {
                assert vehicle.getRentingSet().size() == 0
            }
        }
    }

    @Override
    def deleteFromDatabase() {
        for (def ra : FenixFramework.getDomainRoot().getRentACarSet()) {
            ra.delete()
        }
    }
}
