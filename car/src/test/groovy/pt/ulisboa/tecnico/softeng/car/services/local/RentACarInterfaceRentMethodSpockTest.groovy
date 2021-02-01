package pt.ulisboa.tecnico.softeng.car.services.local

import org.joda.time.LocalDate
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.car.domain.*
import pt.ulisboa.tecnico.softeng.car.exception.CarException
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface

class RentACarInterfaceRentMethodSpockTest extends SpockRollbackTestAbstractClass {
    def ADVENTURE_ID = 'AdventureId'
    def PLATE_CAR = '22-33-HZ'
    def RENT_A_CAR_NAME = 'Eartz'
    def DRIVING_LICENSE = 'lx1423'
    def BEGIN = LocalDate.parse('2018-01-06')
    def END = LocalDate.parse('2018-01-09')
    def NIF = 'NIF'
    def IBAN = 'IBAN'
    def IBAN_BUYER = 'IBAN'
    def rentACar
    def car
    def rentACarInterface

    @Override
    def populate4Test() {
        rentACarInterface = new RentACarInterface()

        def bankInterface = new BankInterface()
        def taxInterface = new TaxInterface()
        def processor = new Processor(bankInterface, taxInterface)

        rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN, processor)
    }

    def 'rent a car has car available'() {
        given: 'given a car available'
        car = new Car(PLATE_CAR, 10, 10, rentACar)

        when: 'when renting the car'
        def reference = rentACarInterface.rent(Car, DRIVING_LICENSE, NIF, IBAN_BUYER, BEGIN, END, ADVENTURE_ID)

        then: 'then it should succeed: get a renting reference and car becomes not free'
        reference != null
        !car.isFree(BEGIN, END)
    }

    @Unroll('no car/motorcycle: #name')
    def 'exceptions'() {
        when: 'if the rent-a-car has no vehicles'
        rentACarInterface.rent(type, DRIVING_LICENSE, NIF, IBAN_BUYER, BEGIN, END, ADVENTURE_ID)

        then: 'renting a vehicle should throw an exception'
        thrown(CarException)

        where:
        name         | type
        'car'        | Car.class
        'motorcycle' | Motorcycle.class
    }

    def 'no rent a cars'() {
        given: 'if there are no rent a cars'
        rentACar.delete()

        when: 'trying to rent a car'
        rentACarInterface.rent(Car, DRIVING_LICENSE, NIF, IBAN_BUYER, BEGIN, END, ADVENTURE_ID)

        then: 'throws an exception'
        thrown(CarException)
    }
}
