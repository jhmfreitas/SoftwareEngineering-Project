package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import spock.lang.Unroll

class BrokerGetRoomBookingFromBulkBookingsSpockTest extends SpockRollbackTestAbstractClass {
    def broker
    def hotelInterface
    def bulk
    def bookingData

    @Override
    def populate4Test() {
        hotelInterface = Mock(HotelInterface)
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF, BROKER_IBAN,
                new ActivityInterface(), hotelInterface, new CarInterface(), new BankInterface(), new TaxInterface())
        bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END)
        new Reference(bulk, REF_ONE)
        new Reference(bulk, REF_TWO)

        bookingData = new RestRoomBookingData()
        bookingData.setBookRoom(SINGLE)
        bookingData.setArrival(BEGIN)
        bookingData.setDeparture(END)
    }

    def 'success room'() {
        given: 'that the hotel interface returns a booking data for a single room'
        hotelInterface.getRoomBookingData(_) >> bookingData

        when: 'it is requested a SINGLE room from the set of bulked booked rooms'
        bookingData = broker.getRoomBookingFromBulkBookings(SINGLE, BEGIN, END)

        then: 'a the booking of a single room is returned'
        bookingData.getBookRoom() == SINGLE
        and: 'the number of references is decremented'
        bulk.getReferences().size() == 1
        and: 'the bulk is not cancelled'
        !bulk.getCancelled()
    }


    def 'success one bulk booking is cancelled'() {
        given: 'that the hotel interface returns a booking data for a single room'
        hotelInterface.getRoomBookingData(_) >> bookingData
        and: 'one of the bulk bookings is cancelled'
        def otherBulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END)
        new Reference(otherBulk, REF_THREE)
        new Reference(otherBulk, REF_FOUR)
        otherBulk.setCancelled(true)

        when: 'it is requested a SINGLE room from the set of bulked booked rooms'
        bookingData = broker.getRoomBookingFromBulkBookings(SINGLE, BEGIN, END)

        then: 'a the booking of a single room is returned'
        bookingData.getBookRoom() == SINGLE
        and: 'the number of references is decremented'
        bulk.getReferences().size() == 1
        and: 'the bulk is not cancelled'
        !bulk.getCancelled()
    }

    def 'fail both bulk booking are cancelled'() {
        given: 'that the hotel interface returns a booking data for a single room'
        hotelInterface.getRoomBookingData(_) >> bookingData
        and: 'both bulk bookings are cancelled'
        bulk.setCancelled(true)
        def otherBulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END)
        new Reference(otherBulk, REF_THREE)
        new Reference(otherBulk, REF_FOUR)
        otherBulk.setCancelled(true)

        when: 'it is requested a SINGLE room from the set of bulked booked rooms'
        bookingData = broker.getRoomBookingFromBulkBookings(SINGLE, BEGIN, END)

        then: 'a null is returned'
        bookingData == null
    }

    @Unroll('#exception is thrown')
    def 'fail and an #exception exception is thrown'() {
        given: 'that the hotel interface throws an exception'
        hotelInterface.getRoomBookingData(_) >> { throw mock_exception }

        when: 'it is requested a SINGLE room from the set of bulked booked rooms'
        def bookingData = broker.getRoomBookingFromBulkBookings(SINGLE, BEGIN, END)

        then: 'a null is returned'
        bookingData == null

        where:
        mock_exception              | exception
        new HotelException()        | 'HotelException'
        new RemoteAccessException() | 'RemoteAccessException'

    }

}
