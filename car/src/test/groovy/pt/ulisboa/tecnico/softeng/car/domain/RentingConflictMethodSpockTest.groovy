package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.car.exception.CarException
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface


class RentingConflictMethodSpockTest extends SpockRollbackTestAbstractClass {
	def PLATE_CAR = '22-33-HZ'
	def DRIVING_LICENSE = 'br112233'
	@Shared def date0 = LocalDate.parse('2018-01-05')
	@Shared def date1 = LocalDate.parse('2018-01-06')
	@Shared def date2 = LocalDate.parse('2018-01-07')
	@Shared def date3 = LocalDate.parse('2018-01-08')
	@Shared def date4 = LocalDate.parse('2018-01-09')
	def RENT_A_CAR_NAME ='Eartz'
	def NIF = 'NIF'
	def IBAN = 'IBAN'
	def IBAN_BUYER = 'IBAN'
	def car

	@Override
	def populate4Test() {
		def bankInterface = new BankInterface()
		def taxInterface = new TaxInterface()
		def processor = new Processor(bankInterface, taxInterface)

		def rentACar = new RentACar(RENT_A_CAR_NAME,NIF,IBAN, processor)
		car = new Car(PLATE_CAR,10,10,rentACar)
	}

	@Unroll("conflict and non-conflict test: #dt1, #dt2, #dt3, #dt4 || #res")
	def 'conflict'() {
		when: 'when rentingOne for a given days'
		def renting = new Renting(DRIVING_LICENSE,dt1,dt2,car,NIF,IBAN_BUYER)

		then: 'check it does not conflict'
		renting.conflict(dt3,dt4) == res

		where:
		dt1   | dt2   | dt3   | dt4   || res
		date1 | date2 | date3 | date4 || false
		date1 | date2 | date3 | date3 || false
		date1 | date2 | date2 | date3 || true
		date1 | date2 | date1 | date1 || true
		date1 | date2 | date0 | date3 || true
	}

	def 'end before begin'() {
		given: 'given a rentingOne'
		def renting = new Renting(DRIVING_LICENSE,date1,date2,car,NIF,IBAN_BUYER)

		when: 'throws an exception if end is before start'
		renting.conflict(date2,date1)

		then:
		thrown(CarException)
	}
}
