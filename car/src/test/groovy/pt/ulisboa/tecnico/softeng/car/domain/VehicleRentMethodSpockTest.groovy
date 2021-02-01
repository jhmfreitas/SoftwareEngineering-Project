package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.car.exception.CarException
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll


class VehicleRentMethodSpockTest extends SpockRollbackTestAbstractClass {
	def ADVENTURE_ID = 'AdventureId'
	def PLATE_CAR = '22-33-HZ'
	def RENT_A_CAR_NAME = 'Eartz'
	def DRIVING_LICENSE = 'lx1423'
	@Shared def date1 = LocalDate.parse('2018-01-06')
	@Shared def date2 = LocalDate.parse('2018-01-09')
	def NIF = 'NIF'
	def IBAN = 'IBAN'
	def IBAN_BUYER = 'IBAN'

	def rentACar

	@Override
	def populate4Test() {
		def bankInterface = new BankInterface()
		def taxInterface = new TaxInterface()
		def processor = new Processor(bankInterface, taxInterface)

		rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN, processor)
	}

	def 'double rent'() {
		given: 'given a car is rented'
		def car = new Car(PLATE_CAR,10,10, rentACar)
		car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID)

		when: 'rentingOne it twice'
		car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID)

		then: 'throws an exception'
		thrown(CarException)
	}

	@Unroll('#begin, #end')
	def 'exceptions'() {
		given: 'given a car available'
		def car = new Car(PLATE_CAR,10,10,rentACar)

		when: 'wrong parameters for renting'
		car.rent(DRIVING_LICENSE, begin, end, NIF + "1", IBAN_BUYER, ADVENTURE_ID)

		then: 'throws an exception'
		thrown(CarException)

		where:
		begin | end
		date1 | null
		null  | date2
	}
}
