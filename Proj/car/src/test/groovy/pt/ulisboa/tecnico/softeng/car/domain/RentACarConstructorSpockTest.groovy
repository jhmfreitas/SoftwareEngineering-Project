package pt.ulisboa.tecnico.softeng.car.domain

import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.car.exception.CarException
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface


class RentACarConstructorSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def NAME = 'eartz'
	@Shared def NIF = 'NIF'
	@Shared def IBAN = 'IBAN'

	def processor

	@Override
	def populate4Test() {
		def bankInterface = new BankInterface()
		def taxInterface = new TaxInterface()
		processor = new Processor(bankInterface, taxInterface)
	}

	def 'success'() {
		when: 'creating a new RentACar'
		def rentACar = new RentACar(NAME, NIF, IBAN, processor)

		then: 'should succeed'
		rentACar.getName() == NAME
	}


	@Unroll('RentACar: #name')
	def 'exceptions'() {
		when: 'creating a RentACar with invalid arguments'
		new RentACar(name, nif, iban, processor)

		then: 'throws an exception'
		thrown(CarException)

		where:
		name | nif | iban
		null | NIF | IBAN
		''   | NIF | IBAN
	}
}
