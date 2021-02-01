package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.*
import spock.lang.Unroll

class AdventureSequenceSpockTest extends SpockRollbackTestAbstractClass {
    def taxInterface
    def bankInterface
    def activityInterface
    def hotelInterface
    def carInterface

    def bookingActivityData
    def bookingRoomData
    def rentingData
    def broker
    def client

    @Override
    def populate4Test() {
        taxInterface = Mock(TaxInterface)
        bankInterface = Mock(BankInterface)
        activityInterface = Mock(ActivityInterface)
        hotelInterface = Mock(HotelInterface)
        carInterface = Mock(CarInterface)

        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF, BROKER_IBAN,
                activityInterface, hotelInterface, carInterface, bankInterface, taxInterface)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

        bookingActivityData = new RestActivityBookingData()
        bookingActivityData.setReference(ACTIVITY_CONFIRMATION)
        bookingActivityData.setPrice(70 * Adventure.SCALE)
        bookingActivityData.setPaymentReference(PAYMENT_CONFIRMATION)
        bookingActivityData.setInvoiceReference(INVOICE_REFERENCE)

        bookingRoomData = new RestRoomBookingData()
        bookingRoomData.setReference(ROOM_CONFIRMATION)
        bookingRoomData.setPrice(80 * Adventure.SCALE)
        bookingRoomData.setPaymentReference(PAYMENT_CONFIRMATION)
        bookingRoomData.setInvoiceReference(INVOICE_REFERENCE)

        rentingData = new RestRentingData()
        rentingData.setReference(RENTING_CONFIRMATION)
        rentingData.setPrice(60 * Adventure.SCALE)
        rentingData.setPaymentReference(PAYMENT_CONFIRMATION)
        rentingData.setInvoiceReference(INVOICE_REFERENCE)
    }

    @Unroll
    def 'success sequence with car #car, hotel #hotel'() {
        given: 'an adventure with rent vehicle as #car'
        def adventure = new Adventure(broker, ARRIVAL, end, client, MARGIN, hotel, car)
        and: 'an activity reservation'
        activityInterface.reserveActivity(_) >> bookingActivityData

        and: 'a room booking'
        if (hotel != Adventure.BookRoom.NONE) {
            hotelInterface.reserveRoom(_) >> bookingRoomData
        }
        and: 'a car renting'
        if (car != Adventure.RentVehicle.NONE) {
            carInterface.rentCar(*_) >> rentingData
        }

        and: 'a bank payment'
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        and: 'a tax payment'
        taxInterface.submitInvoice(_) >> INVOICE_DATA
        and: 'the correct return of the data associated with each reservation and payment'
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData
        if (car != Adventure.RentVehicle.NONE) {
            carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData
        }
        if (hotel != Adventure.BookRoom.NONE) {
            hotelInterface.getRoomBookingData(ROOM_CONFIRMATION) >> bookingRoomData
        }
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)

        when: 'the life cycle of the adventure'
        1.upto(cycles) { adventure.process() }

        then: 'the final state is confirmed'
        adventure.getState().getValue() == State.CONFIRMED
        adventure.getAmount() / Adventure.SCALE == amount

        where:
        cycles | car                        | hotel                     | end       | amount
        6      | Adventure.RentVehicle.CAR  | Adventure.BookRoom.SINGLE | DEPARTURE | (70 + 80 + 60) * 1.03
        5      | Adventure.RentVehicle.NONE | Adventure.BookRoom.SINGLE | DEPARTURE | (70 + 80) * 1.03
        5      | Adventure.RentVehicle.CAR  | Adventure.BookRoom.NONE   | ARRIVAL   | (70 + 60) * 1.03
        4      | Adventure.RentVehicle.NONE | Adventure.BookRoom.NONE   | ARRIVAL   | 70 * 1.03
    }

    def 'unsuccess sequence fail activity'() {
        given: 'an adventure'
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, Adventure.BookRoom.SINGLE, Adventure.RentVehicle.NONE)
        and: 'an activity exception'
        activityInterface.reserveActivity(_) >> { throw new ActivityException() }

        when: 'the life cycle of the adventure'
        1.upto(2) { adventure.process() }

        then: 'the final state is cancelled'
        State.CANCELLED == adventure.getState().getValue()
    }

    def 'unsuccess sequence fail hotel'() {
        given: 'an adventure'
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, Adventure.BookRoom.SINGLE, Adventure.RentVehicle.NONE)
        and: 'an activity reservation'
        activityInterface.reserveActivity(_) >> bookingActivityData
        and: 'an hotel exception'
        hotelInterface.reserveRoom(_) >> { throw new HotelException() }
        and: 'an activity reservation cancelation'
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

        when: 'the life cycle of the adventure'
        1.upto(4) { adventure.process() }

        then: 'the final state is cancelled'
        State.CANCELLED == adventure.getState().getValue()
    }

    def 'unsuccess sequence fail car'() {
        given: 'an adventure with rent vehicle'
        def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN, Adventure.BookRoom.NONE, Adventure.RentVehicle.MOTORCYCLE)
        and: 'an activity reservation'
        activityInterface.reserveActivity(_) >> bookingActivityData
        and: 'a car exception'
        carInterface.rentCar(*_) >> { throw new CarException() }
        and: 'an activity reservation cancelation'
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

        when: 'the life cycle of the adventure'
        1.upto(4) { adventure.process() }

        then: 'the final state is cancelled'
        State.CANCELLED == adventure.getState().getValue()
    }

    def 'unsuccess sequence fail payment'() {
        given: 'an adventure with rent vehicle'
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, Adventure.BookRoom.DOUBLE, Adventure.RentVehicle.MOTORCYCLE)
        and: 'an activity reservation'
        activityInterface.reserveActivity(_) >> bookingActivityData
        and: 'a room booking'
        hotelInterface.reserveRoom(_) >> bookingRoomData
        and: 'a car rental'
        carInterface.rentCar(*_) >> rentingData
        and: 'a bank exception'
        bankInterface.processPayment(_) >> { throw new BankException() }
        and: 'the correct return of the data associated with each cancelation'
        activityInterface.cancelReservation(_) >> ACTIVITY_CANCELLATION
        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION

        when: 'the life cycle of the adventure'
        1.upto(6) { adventure.process() }

        then: 'the final state is cancelled'
        State.CANCELLED == adventure.getState().getValue()
    }

    def 'unsuccess sequence fail tax'() {
        given: 'an adventure with rent vehicle'
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, Adventure.BookRoom.DOUBLE, Adventure.RentVehicle.MOTORCYCLE)
        and: 'an activity reservation'
        activityInterface.reserveActivity(_) >> bookingActivityData
        and: 'a room booking'
        hotelInterface.reserveRoom(_) >> bookingRoomData
        and: 'a car rental'
        carInterface.rentCar(Adventure.RentVehicle.MOTORCYCLE, *_) >> rentingData
        and: 'a bank payment'
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        and: 'a tax exception'
        taxInterface.submitInvoice(_) >> { throw new TaxException() }
        and: 'the correct return of the data associated with each cancelation'
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION
        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> PAYMENT_CANCELLATION

        when: 'the life cycle of the adventure'
        1.upto(6) { adventure.process() }

        then: 'the final state is cancelled'
        State.CANCELLED == adventure.getState().getValue()
    }
}
