package pt.ulisboa.tecnico.softeng.car.services.local

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.car.domain.Car
import pt.ulisboa.tecnico.softeng.car.domain.Motorcycle
import pt.ulisboa.tecnico.softeng.car.domain.Processor
import pt.ulisboa.tecnico.softeng.car.domain.RentACar
import pt.ulisboa.tecnico.softeng.car.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface

class RentACarInterfaceGetAllAvailableVehiclesMethodSpockTest extends SpockRollbackTestAbstractClass {
	def ADVENTURE_ID = 'AdventureId'
	def NAME1 = 'eartz'
	def NAME2 = 'eartz'
	def PLATE_CAR1 = 'aa-00-11'
	def PLATE_CAR2 = 'aa-00-22'
	def PLATE_MOTORCYCLE = '44-33-HZ'
	def DRIVING_LICENSE = 'br123'
	def date1 = LocalDate.parse('2018-01-06')
	def date2 = LocalDate.parse('2018-01-07')
	def date3 = LocalDate.parse('2018-01-08')
	def date4 = LocalDate.parse('2018-01-09')
	def NIF = 'NIF'
	def IBAN = 'IBAN'
	def IBAN_BUYER = 'IBAN'
	def rentACar1
	def rentACar2
	def rentACarInterface

	@Override
	def populate4Test() {
		rentACarInterface = new RentACarInterface()

		def bankInterface = new BankInterface()
		def taxInterface = new TaxInterface()
		def processor = new Processor(bankInterface, taxInterface)

		rentACar1 = new RentACar(NAME1,NIF,IBAN, processor)

		bankInterface = new BankInterface()
		taxInterface = new TaxInterface()
		processor = new Processor(bankInterface, taxInterface)

		rentACar2 = new RentACar(NAME2,NIF + '1',IBAN, processor)
	}

	def 'only cars'() {
		given:
		def car1 = new Car(PLATE_CAR1,10,10, rentACar1)
		car1.rent(DRIVING_LICENSE,date1,date2,NIF,IBAN_BUYER,ADVENTURE_ID)
		def car2 = new Car(PLATE_CAR2,10,10, rentACar2)
		def motorcycle = new Motorcycle(PLATE_MOTORCYCLE,10,10, rentACar1)

		when:
		def cars = rentACarInterface.getAllAvailableCars(date3,date4)

		then:
		cars.contains(car1)
		cars.contains(car2)
		!cars.contains(motorcycle)
	}

	def 'only available cars'() {
		given: 'creating two cars, and rentingOne one'
		def car1 = new Car(PLATE_CAR1, 10, 10, rentACar1)
		def car2 = new Car(PLATE_CAR2, 10, 10, rentACar2)
		car1.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID)

		when: 'when fetching available cars'
		def cars = rentACarInterface.getAllAvailableCars(date1, date2)

		then: 'car2 should be in the returned list'
		!cars.contains(car1)
		cars.contains(car2)
	}

	def 'only motorcycles'() {
		given: 'creating one car, and one motorcycle'
		def car = new Car(PLATE_CAR1,10,10, rentACar1)
		def motorcycle = new Motorcycle(PLATE_MOTORCYCLE,10,10, rentACar1)

		when: 'when fetching available motorcycle'
		def cars = rentACarInterface.getAllAvailableMotorcycles(date3,date4)

		then: 'only the motorcycle should be in the list'
		cars.contains(motorcycle)
		!cars.contains(car)
	}
}
