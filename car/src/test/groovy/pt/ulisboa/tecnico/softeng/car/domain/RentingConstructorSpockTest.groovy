package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.car.exception.CarException
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface


class RentingConstructorSpockTest extends SpockRollbackTestAbstractClass {
	def RENT_A_CAR_NAME = 'Eartz'
	def PLATE_CAR = '22-33-HZ'
	@Shared def DRIVING_LICENSE = 'br112233'
	@Shared def date1 = LocalDate.parse('2018-01-06')
	@Shared def date2 = LocalDate.parse('2018-01-07')
	@Shared def NIF = 'NIF'
	@Shared def IBAN = 'IBAN'
	@Shared def IBAN_BUYER = 'IBAN'
	@Shared def car

	@Override
	def populate4Test() {
		def bankInterface = new BankInterface()
		def taxInterface = new TaxInterface()
		def processor = new Processor(bankInterface, taxInterface)

		def rentACar = new RentACar(RENT_A_CAR_NAME, NIF,IBAN, processor)
		car = new Car(PLATE_CAR, 10, 10, rentACar)
	}

	def 'success'() {
		when:
		Renting renting = new Renting(DRIVING_LICENSE, date1, date2, car, NIF, IBAN_BUYER)

		then:
		renting.getDrivingLicense() == DRIVING_LICENSE
		car.getPrice() * (date2.getDayOfYear() - date1.getDayOfYear() + 1) == renting.getPrice()
	}

	@Unroll('RentACar: #dl | #d1 | #d2 | #veh | #nif | #iban')
	def 'exceptions'() {
		when: 'rentingOne with wrong arguments'
		new Renting(dl, d1, d2, veh, nif, iban)

		then: 'throws an exception'
		thrown(CarException)

		where:
		dl   | d1 | d2 | veh | nif | iban
		null | date1 | date2 | car | NIF | IBAN_BUYER
		''   | date1 | date2 | car | NIF | IBAN_BUYER
		'12' | date1 | date2 | car | NIF | IBAN_BUYER
		DRIVING_LICENSE | null  | date2 | car  | NIF | IBAN_BUYER
		DRIVING_LICENSE | date1 | null  | car  | NIF | IBAN_BUYER
		DRIVING_LICENSE | date2 | date1 | car  | NIF | IBAN_BUYER
		DRIVING_LICENSE | date1 | date2 | null | NIF | IBAN_BUYER
	}
}
