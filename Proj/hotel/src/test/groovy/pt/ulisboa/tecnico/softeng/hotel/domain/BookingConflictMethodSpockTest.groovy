package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import spock.lang.Unroll

class BookingConflictMethodSpockTest extends SpockRollbackTestAbstractClass {
    def ARRIVAL = new LocalDate(2016, 12, 19)
    def DEPARTURE = new LocalDate(2016, 12, 24)
    def NIF_HOTEL = '123456700'
    def NIF_BUYER = '123456789'
    def IBAN_BUYER = 'IBAN_BUYER'
    def booking
    def room

    @Override
    def populate4Test() {
        def hotel = new Hotel('XPTO123', 'Londres', NIF_HOTEL, 'IBAN', 20, 30, new Processor(new BankInterface(), new TaxInterface()))
        room = new Room(hotel, '01', Room.Type.SINGLE)
    }

    @Unroll('from #arrival to #departure should not overlap with period from 2016, 12, 19 to 2016, 12, 24')
    def 'dates do not overlap'() {
        given: 'a booking'
        booking = new Booking(room, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

        expect: 'it does not conflic with non overlapping dates'
        booking.conflict(arrival, departure) == result

        where:
        arrival                     | departure                   || result
        new LocalDate(2016, 12, 9)  | new LocalDate(2016, 12, 15) || false
        new LocalDate(2016, 12, 9)  | new LocalDate(2016, 12, 19) || false
        new LocalDate(2016, 12, 26) | new LocalDate(2016, 12, 30) || false
        new LocalDate(2016, 12, 24) | new LocalDate(2016, 12, 30) || false
    }

    def 'no conflict because it is cancelled'() {
        given: 'a booking'
        booking = new Booking(room, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

        and: 'the booking is cancelled'
        booking.cancel()

        expect: 'it does not conflict with conflicting dates'
        booking.conflict(booking.getArrival(), booking.getDeparture()) == false
    }

    def 'arguments are inconsistent'() {
        given: 'a booking'
        booking = new Booking(room, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

        when: 'start date is later than end date'
        booking.conflict(new LocalDate(2016, 12, 15), new LocalDate(2016, 12, 9))

        then: 'an Hotel Exception is thrown'
        thrown(HotelException)
    }

    def 'begin equals end day'() {
        given: 'a booking'
        booking = new Booking(room, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

        expect: 'it conflicts same day not overlapping'
        booking.conflict(new LocalDate(2016, 12, 9), new LocalDate(2016, 12, 9))
    }

    @Unroll('from #arrival to #departure should not overlap with period from 2016, 12, 19 to 2016, 12, 24')
    def 'dates do overlap'() {
        given: 'a booking'
        booking = new Booking(room, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

        expect: 'it does conflic with overlapping dates'
        booking.conflict(arrival, departure) == result

        where:
        arrival                     | departure                   || result
        new LocalDate(2016, 12, 9)  | new LocalDate(2016, 12, 30) || true
        new LocalDate(2016, 12, 19) | new LocalDate(2016, 12, 29) || true
        new LocalDate(2016, 12, 7)  | new LocalDate(2016, 12, 24) || true
        new LocalDate(2016, 12, 8)  | new LocalDate(2016, 12, 21) || true
        new LocalDate(2016, 12, 21) | new LocalDate(2016, 12, 30) || true
        new LocalDate(2016, 12, 20) | new LocalDate(2016, 12, 22) || true
    }
}
