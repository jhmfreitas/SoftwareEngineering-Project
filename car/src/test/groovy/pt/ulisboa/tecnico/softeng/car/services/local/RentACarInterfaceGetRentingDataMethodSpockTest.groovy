package pt.ulisboa.tecnico.softeng.car.services.local

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.car.domain.Car
import pt.ulisboa.tecnico.softeng.car.domain.Processor
import pt.ulisboa.tecnico.softeng.car.domain.RentACar
import pt.ulisboa.tecnico.softeng.car.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.car.exception.CarException
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface

class RentACarInterfaceGetRentingDataMethodSpockTest extends SpockRollbackTestAbstractClass {
    def ADVENTURE_ID = 'AdventureId'
    def NAME1 = 'eartz'
    def PLATE_CAR1 = 'aa-00-11'
    def DRIVING_LICENSE = 'br123'
    def date1 = LocalDate.parse('2018-01-06')
    def date2 = LocalDate.parse('2018-01-07')
    def NIF = 'NIF'
    def IBAN = 'IBAN'
    def IBAN_BUYER = 'IBAN'
    def car
    def rentACarInterface


    @Override
    def populate4Test() {
        rentACarInterface = new RentACarInterface()

        def bankInterface = new BankInterface()
        def taxInterface = new TaxInterface()
        def processor = new Processor(bankInterface, taxInterface)

        def rentACar1 = new RentACar(NAME1, NIF, IBAN, processor)
        car = new Car(PLATE_CAR1, 10, 10, rentACar1)
    }

    def 'success'() {
        given: 'renting a car is assumed to have happened'
        def renting = car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID)

        when: 'fetching the renting data'
        def rentingData = rentACarInterface.getRentingData(renting.getReference())

        then: 'values should be according to renting'
        with(rentingData) {
            getReference() == renting.getReference()
            getRentACarCode() == car.getRentACar().getCode()
            getPlate().toLowerCase().equals(PLATE_CAR1)
        }
    }

    def 'get renting data fail'() {
        when: 'wrong renting data'
        rentACarInterface.getRentingData('1')

        then: 'throws an exception'
        thrown(CarException)
    }
}
