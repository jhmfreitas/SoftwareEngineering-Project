package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import spock.lang.Unroll

class ReserveActivityStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
    def activityInterface
    def broker
    def client
    def adventure
    def bookingData

    @Override
    def populate4Test() {
        activityInterface = Mock(ActivityInterface)
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF, BROKER_IBAN,
                activityInterface, new HotelInterface(), new CarInterface(), new BankInterface(),
                new TaxInterface())
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

        adventure = new Adventure(broker, BEGIN, END, client, MARGIN, Adventure.BookRoom.DOUBLE, Adventure.RentVehicle.CAR)
        adventure.setState(Adventure.State.RESERVE_ACTIVITY)

        bookingData = new RestActivityBookingData()
        bookingData.setReference(ACTIVITY_CONFIRMATION)
        bookingData.setPrice(Math.round(76.78 * Adventure.SCALE))
    }

    @Unroll('#label: #rent_a_car #adventure_state')
    def 'success processing adventure'() {
        given: 'activity reserved'
        activityInterface.reserveActivity(_) >> bookingData
        and: 'an adventure on the same day'
        def sameDayAdventure = new Adventure(broker, BEGIN, BEGIN, client, MARGIN, Adventure.BookRoom.NONE, rent_a_car)
        sameDayAdventure.setState(Adventure.State.RESERVE_ACTIVITY)

        when: 'the adventure is processed'
        sameDayAdventure.process()

        then: 'state of adventure is as expected'
        sameDayAdventure.getState().getValue() == adventure_state

        where:
        rent_a_car                 | adventure_state                 | label
        Adventure.RentVehicle.CAR  | Adventure.State.RENT_VEHICLE    | 'success to rent vehicle'
        Adventure.RentVehicle.NONE | Adventure.State.PROCESS_PAYMENT | 'success no book room'
    }

    def 'success book room'() {
        given: 'activity reserved'
        activityInterface.reserveActivity(_) >> bookingData

        when: 'adventure is processed'
        adventure.process()

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == Adventure.State.BOOK_ROOM
    }

    @Unroll('#label: #mock_exception')
    def 'exceptional states'() {
        given: 'activity reservation throws exception'
        activityInterface.reserveActivity(_) >> { throw mock_exception }

        when: 'adventure is processed #process_iterations time(s)'
        1.upto(process_iterations) {
            adventure.process()
        }

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == adventure_state

        where:
        mock_exception              | adventure_state                  | process_iterations | label
        new ActivityException()     | Adventure.State.UNDO             | 1                  | 'activity exception'
        new RemoteAccessException() | Adventure.State.RESERVE_ACTIVITY | 1                  | 'single remote access exception'
        new RemoteAccessException() | Adventure.State.UNDO             | 5                  | 'max remote access exception'
        new RemoteAccessException() | Adventure.State.RESERVE_ACTIVITY | 4                  | 'max minus one remote access exception'
    }


    def 'two remote access exception and success'() {
        given: 'activity reservation fails with two remote exceptions and then succeeds'
        activityInterface.reserveActivity(_) >>
                { throw new RemoteAccessException() } >>
                { throw new RemoteAccessException() } >>
                bookingData

        when: 'adventure is processes 3 times'
        1.upto(3) {
            adventure.process()
        }

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == Adventure.State.BOOK_ROOM
    }

    def 'one remote access exception and one activity exception'() {
        given: 'activity reservation fails with a remote exception followed by an activity exception'
        activityInterface.reserveActivity(_) >>
                { throw new RemoteAccessException() } >>
                { throw new ActivityException() }

        when: 'adventure is processes 2 times'
        1.upto(2) {
            adventure.process()
        }

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == Adventure.State.UNDO
    }
}