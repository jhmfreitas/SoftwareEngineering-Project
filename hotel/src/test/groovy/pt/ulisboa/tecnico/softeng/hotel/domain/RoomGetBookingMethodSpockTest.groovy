package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface

class RoomGetBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
    def NIF_BUYER = '123456789';
    def IBAN_BUYER = 'IBAN_BUYER';
    def ARRIVAL = new LocalDate(2016, 12, 19)
    def DEPARTURE = new LocalDate(2016, 12, 24)

    def hotel
    def room
    def booking

    @Override
    def populate4Test() {
        hotel = new Hotel('XPTO123', 'Lisboa', 'NIF', 'IBAN', 20, 30, new Processor(new BankInterface(), new TaxInterface()))
        room = new Room(hotel, '01', Type.SINGLE)
    }

    def 'success'() {
        given: 'a booking'
        booking = room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

        expect: 'get booking using cancellation reference'
        room.getBooking(booking.getReference()) == booking
    }

    def 'success cancelled'() {
        given: 'booking is cancelled'
        booking = room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)
        booking.cancel()

        expect: 'get booking using cancellation reference'
        room.getBooking(booking.getCancellation()) == booking
    }

    def 'does not exist'() {
        expect: 'a null from a non existing reference'
        room.getBooking('XPTO') == null
    }
}
