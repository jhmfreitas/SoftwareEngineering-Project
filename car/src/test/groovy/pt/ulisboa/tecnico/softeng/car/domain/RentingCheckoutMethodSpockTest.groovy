package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.car.exception.CarException
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface


class RentingCheckoutMethodSpockTest extends SpockRollbackTestAbstractClass {
	def ADVENTURE_ID = 'AdventureId'
	def NAME1='eartz'
	def PLATE_CAR1='aa-00-11'
	def DRIVING_LICENSE='br123'
	def date1= LocalDate.parse('2018-01-06')
	def date2= LocalDate.parse('2018-01-07')
	def NIF='NIF'
	def IBAN='IBAN'
	def IBAN_BUYER='IBAN'
	def car

	@Override
	def populate4Test() {
		def bankInterface = new BankInterface()
		def taxInterface = new TaxInterface()
		def processor = new Processor(bankInterface, taxInterface)

		def rentACar = new RentACar(NAME1,NIF,IBAN, processor)
		car = new Car(PLATE_CAR1,10,10,rentACar)
	}

	def 'checkout'() {
		given: 'given a renting'
		def renting = car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID)

		when: 'when checking out with a valid number of km'
		renting.checkout(100)

		then: 'then car is updated with that number'
		car.getKilometers() == 110
	}

	def 'fail checkout'() {
		given: 'given a renting'
		def renting = car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID)

		when: 'when checking out with a negative number of km'
		renting.checkout(-10)

		then: 'an exception is thrown'
		thrown(CarException)
	}
}
