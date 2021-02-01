package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import spock.lang.Unroll

class RentVehicleStateMethodSpockTest extends SpockRollbackTestAbstractClass {
    def carInterface
    def taxInterface

    def adventure
    def rentingData

    @Override
    def populate4Test() {
        carInterface = Mock(CarInterface)
        taxInterface = Mock(TaxInterface)

        def broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF, BROKER_IBAN, new ActivityInterface(), new HotelInterface(), carInterface, new BankInterface(), taxInterface)
        def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN, Adventure.BookRoom.DOUBLE, Adventure.RentVehicle.CAR)

        rentingData = new RestRentingData()
        rentingData.setReference(RENTING_CONFIRMATION)
        rentingData.setPrice(Math.round(76.78 * Adventure.SCALE))

        adventure.setState(Adventure.State.RENT_VEHICLE)
    }

    def 'successRentVehicle'() {
        given: 'mocking of renting a car succeeds and returns value rentingData'
        carInterface.rentCar(Adventure.RentVehicle.CAR, DRIVING_LICENSE, BROKER_NIF, BROKER_IBAN,
                BEGIN, END, _) >> rentingData

        when: 'adventure is processed'
        adventure.process()

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT
    }

    @Unroll('#label (rentCar): adventure in state #adventure_state ')
    def 'exceptions rentCar'() {
        given: 'rentCar fails #iterations time(s)'
        iterations * carInterface.rentCar(Adventure.RentVehicle.CAR, DRIVING_LICENSE, BROKER_NIF, BROKER_IBAN,
                BEGIN, END, _) >> { throw mock_exception }

        when: 'adventure is processed'
        1.upto(iterations) {
            adventure.process()
        }

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == adventure_state

        where:
        mock_exception              | adventure_state              | iterations                             | label
        new CarException()          | Adventure.State.UNDO         | 1                                      | 'car exception'
        new RemoteAccessException() | Adventure.State.RENT_VEHICLE | 1                                      | 'remote access exception'
        new RemoteAccessException() | Adventure.State.UNDO         | RentVehicleState.MAX_REMOTE_ERRORS     | 'max remote access exception'
        new RemoteAccessException() | Adventure.State.RENT_VEHICLE | RentVehicleState.MAX_REMOTE_ERRORS - 1 | 'max minus one remote access exception'
    }

    def 'twoRemoteAccessExceptionOneSuccess'() {
        given: 'renting a car fails with two remote exceptions and then succeeds'
        carInterface.rentCar(Adventure.RentVehicle.CAR, DRIVING_LICENSE, BROKER_NIF, BROKER_IBAN,
                BEGIN, END, _) >>
                { throw new RemoteAccessException() } >>
                { throw new RemoteAccessException() } >>
                rentingData

        when: 'adventure is processed 3 times'
        1.upto(3) {
            adventure.process()
        }

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT
    }

    def 'oneRemoteAccessExceptionOneCarException'() {
        given: 'renting a car fails with a remote exception followed by a car exception'
        carInterface.rentCar(Adventure.RentVehicle.CAR, DRIVING_LICENSE, BROKER_NIF, BROKER_IBAN,
                BEGIN, END, _) >>
                { throw new RemoteAccessException() } >>
                { throw new CarException() }

        when: 'adventure is processed twice'
        1.upto(2) {
            adventure.process()
        }

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == Adventure.State.UNDO
    }

}
