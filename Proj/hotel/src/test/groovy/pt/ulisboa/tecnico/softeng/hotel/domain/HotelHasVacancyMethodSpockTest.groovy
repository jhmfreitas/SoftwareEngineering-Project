package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll

class HotelHasVacancyMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared
    def NIF_HOTEL = "123456700"
    @Shared
    def NIF_BUYER = "123456789"
    @Shared
    def IBAN_BUYER = "IBAN_BUYER"
    @Shared
    def ARRIVAL = new LocalDate(2016, 12, 19)
    @Shared
    def DEPARTURE = new LocalDate(2016, 12, 21)

    def hotel
    def room

    @Override
    def populate4Test() {
        hotel = new Hotel("XPTO123", "Paris", NIF_HOTEL, "IBAN", 20, 30, new Processor(new BankInterface(), new TaxInterface()))
        room = new Room(hotel, "01", Type.DOUBLE)
    }

    def "has vacancy"() {
        when: "it has vacancy"
        def room = hotel.hasVacancy(Type.DOUBLE, ARRIVAL, DEPARTURE)

        then: "it returns a room"
        room != null
        room.getNumber().equals("01")
    }

    def "no vacancy"() {
        given: "a booking"
        room.reserve(Type.DOUBLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER);

        when: "looking for a vacancy in the same period"
        room = hotel.hasVacancy(Type.DOUBLE, ARRIVAL, DEPARTURE)

        then: "it does have an available room"
        room == null
    }

    def "no vacancy empty room set"() {
        given: "an hotel without rooms"
        def otherHotel = new Hotel("XPTO124", "Paris Germain", "NIF2", "IBAN", 25, 35, new Processor(new BankInterface(), new TaxInterface()))

        when: "looking for a vancancy"
        room = otherHotel.hasVacancy(Type.DOUBLE, ARRIVAL, DEPARTURE)

        then: "it does have an available room"
        room == null
    }

    @Unroll('one of the following arguments is invalid: #type | #arrival | #departure')
    def "incorrect arguments"() {
        when: "looking for a vacancy"
        hotel.hasVacancy(type, arrival, departure)

        then: "an HotelException is thrown"
        def error = thrown(HotelException)

        where:
        type        | arrival | departure
        null        | ARRIVAL | DEPARTURE
        Type.DOUBLE | null    | DEPARTURE
        Type.DOUBLE | ARRIVAL | null
    }
}
