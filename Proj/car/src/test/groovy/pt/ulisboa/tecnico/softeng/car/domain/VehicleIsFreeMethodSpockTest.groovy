package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface


class VehicleIsFreeMethodSpockTest extends SpockRollbackTestAbstractClass {
	def ADVENTURE_ID = 'AdventureId'
	def PLATE_CAR='22-33-HZ'
	def RENT_A_CAR_NAME='Eartz'
	def DRIVING_LICENSE='lx1423'
	def NIF='NIF'
	def IBAN='IBAN'
	def IBAN_BUYER='IBAN'
	@Shared def date1= LocalDate.parse('2018-01-06')
	@Shared def date2= LocalDate.parse('2018-01-07')
	@Shared def date3= LocalDate.parse('2018-01-08')
	@Shared def date4= LocalDate.parse('2018-01-09')

	def car

	@Override
	def populate4Test() {
		def bankInterface = new BankInterface()
		def taxInterface = new TaxInterface()
		def processor = new Processor(bankInterface, taxInterface)

		def rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN, processor)
		car = new Car(PLATE_CAR,10,10,rentACar)
	}


	@Unroll('#begin, #end')
	def 'no booking was made'() {
		expect: 'no booking, hence car is free for given dates'
		car.isFree(begin,end)

		where:
		begin | end
		date1 | date2
		date1 | date3
		date3 | date4
		date4 | date4
	}

	@Unroll('#begin, #end')
	def 'bookings were made'() {
		given: 'the car is booked'
		car.rent(DRIVING_LICENSE, date2, date2, NIF, IBAN_BUYER, ADVENTURE_ID)
		car.rent(DRIVING_LICENSE, date3, date4, NIF, IBAN_BUYER, ADVENTURE_ID)

		expect: 'the car is not free'
		!car.isFree(begin, end)

		where:
		begin | end
		date1 | date2
		date1 | date3
		date3 | date4
		date4 | date4
	}
}

