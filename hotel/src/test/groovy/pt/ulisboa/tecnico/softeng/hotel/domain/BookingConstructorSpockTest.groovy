package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll

class BookingConstructorSpockTest extends SpockRollbackTestAbstractClass {
    @Shared
    def ARRIVAL = new LocalDate(2016, 12, 19)
    @Shared
    def DEPARTURE = new LocalDate(2016, 12, 21)
    @Shared
    def ROOM_PRICE = 20.0
    @Shared
    def NIF_BUYER = '123456789'
    @Shared
    def IBAN_BUYER = 'IBAN_BUYER'
    def room

    @Override
    def populate4Test() {
        def hotel = new Hotel('XPTO123', 'Londres', 'NIF', 'IBAN', 20, 30, new Processor(new BankInterface(), new TaxInterface()))
        this.room = new Room(hotel, '01', Room.Type.SINGLE)
    }

    def 'success'() {
        when: 'a booking is created'
        def booking = new Booking(this.room, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

        then: 'it correctly instantiates its fields'
        booking.getReference().startsWith(this.room.getHotel().getCode())
        booking.getReference().length() > Hotel.CODE_SIZE
        booking.getArrival().equals(ARRIVAL)
        booking.getDeparture().equals(DEPARTURE)
        booking.getPrice() == ROOM_PRICE * 2
    }

    def 'arrival and departure in same day'() {
        when: 'a booking is created with departure equal to arrival'
        def booking = new Booking(this.room, ARRIVAL, ARRIVAL, NIF_BUYER, IBAN_BUYER)

        then: 'it is created'
        this.room.getBookingSet().size() == 1
    }

    @Unroll('one of the following arguments is not allowed: #room | #arrival | #departure | #buyerNif | #buyerIban')
    def 'incorrect input parameters'() {
        when: 'a booking is created with incorrect inputs'
        new Booking(room, arrival, departure, buyerNif, buyerIban)

        then: 'a HotelException is thrown'
        def error = thrown(HotelException)

        where:
        room      | arrival | departure            | buyerNif  | buyerIban
        null      | ARRIVAL | DEPARTURE            | NIF_BUYER | IBAN_BUYER
        this.room | null    | DEPARTURE            | NIF_BUYER | IBAN_BUYER
        this.room | ARRIVAL | null                 | NIF_BUYER | IBAN_BUYER
        this.room | ARRIVAL | ARRIVAL.minusDays(1) | NIF_BUYER | IBAN_BUYER
        this.room | ARRIVAL | DEPARTURE            | null      | IBAN_BUYER
        this.room | ARRIVAL | DEPARTURE            | ' '       | IBAN_BUYER
        this.room | ARRIVAL | DEPARTURE            | NIF_BUYER | null
        this.room | ARRIVAL | DEPARTURE            | NIF_BUYER | '   '
    }
}
