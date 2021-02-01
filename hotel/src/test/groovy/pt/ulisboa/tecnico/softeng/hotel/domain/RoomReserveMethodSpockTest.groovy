package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll

class RoomReserveMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared
    def NIF_HOTEL = '123456700'
    @Shared
    def NIF_BUYER = '123456789'
    @Shared
    def IBAN_BUYER = 'IBAN_BUYER'
    @Shared
    def ARRIVAL = new LocalDate(2016, 12, 19)
    @Shared
    def DEPARTURE = new LocalDate(2016, 12, 24)
    def room;

    @Override
    def populate4Test() {
        def hotel = new Hotel('XPTO123', 'Lisboa', NIF_HOTEL, 'IBAN', 20, 30, new Processor(new BankInterface(), new TaxInterface()))
        room = new Room(hotel, '01', Type.SINGLE)
    }

    def 'success'() {
        when: 'a booking for an available room occurs'
        Booking booking = room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

        then:
        room.getBookingSet().size() == 1
        booking.getReference().length() > 0
        booking.getArrival() == ARRIVAL
        booking.getDeparture() == DEPARTURE
    }

    def 'no double'() {
        when: 'a booking for an unavailable room occurs'
        room.reserve(Type.DOUBLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

        then: 'a HotelException is thrown'
        def error = thrown(HotelException)
    }

    def 'room is already reserved'() {
        given: 'a booking for a room'
        room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

        when: 'when a booking is done for the same period'
        room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

        then: 'an HotelException is thrown'
        def error = thrown(HotelException)
        room.getBookingSet().size() == 1
    }

    @Unroll('one of the arguments is invalid: #type | #arrival | #departure | #buyerNIF | #buyerIban')
    def 'incorrect arguments'() {
        when: 'a reserve is done with an incorrect argument'
        room.reserve(type, arrival, departure, buyerNIF, buyerIban)

        then: 'an HotelException is thrown'
        def error = thrown(HotelException)

        where:
        type        | arrival | departure | buyerNIF  | buyerIban
        null        | ARRIVAL | DEPARTURE | NIF_BUYER | IBAN_BUYER
        Type.SINGLE | null    | DEPARTURE | NIF_BUYER | IBAN_BUYER
        Type.SINGLE | ARRIVAL | null      | NIF_BUYER | IBAN_BUYER
        Type.SINGLE | ARRIVAL | DEPARTURE | null      | IBAN_BUYER
        Type.SINGLE | ARRIVAL | DEPARTURE | NIF_BUYER | null
    }
}
