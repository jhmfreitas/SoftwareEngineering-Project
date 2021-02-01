package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll

class BookingContructorMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def provider
    @Shared def offer
    @Shared def AMOUNT = 30
    @Shared def IBAN = 'IBAN'
    @Shared def NIF = '123456789'

    @Override
    def populate4Test() {
        def processor = new Processor(new BankInterface(), new TaxInterface())
        provider = new ActivityProvider('XtremX', 'ExtremeAdventure', 'NIF', IBAN, processor)

        def activity = new Activity(provider, 'Bush Walking', 18, 80, 3)

        def begin = new LocalDate(2016, 12, 19)
        def end = new LocalDate(2016, 12, 21)

        offer = new ActivityOffer(activity, begin, end, AMOUNT)
    }

    def 'success'() {
        when:
        def booking = new Booking(provider, offer, NIF, IBAN)

        then:
        with(booking) {
            getReference().startsWith(provider.getCode())
            getReference().length() > ActivityProvider.CODE_SIZE
            getBuyerNif() == NIF
            getBuyerIban() == IBAN
            getAmount() == 30
        }
        offer.getNumberActiveOfBookings() == 1
    }

    @Unroll('exceptions: #prov, #off, #nif, #iban')
    def 'exceptions'() {
        when:
        new Booking(prov, off, nif, iban)

        then:
        thrown(ActivityException)

        where:
        prov     | off   | nif  | iban
        null     | offer | NIF  | IBAN
        provider | null  | NIF  | IBAN
        provider | offer | null | IBAN
        provider | offer | '  ' | IBAN
        provider | offer | NIF  | null
        provider | offer | NIF  | '   '
    }

    def 'booking equal capacity'() {
        given: 'it is complete'
        new Booking(provider, offer, NIF, IBAN)
        new Booking(provider, offer, NIF, IBAN)
        new Booking(provider, offer, NIF, IBAN)

        when: 'a booking'
        new Booking(provider, offer, NIF, IBAN)

        then: 'throws an exception'
        def error = thrown(ActivityException)
        offer.getNumberActiveOfBookings() == 3
    }

    def 'booking equal capacity but has cancelled'() {
        given: 'is complete'
        new Booking(provider, offer, NIF, IBAN)
        new Booking(provider, offer, NIF, IBAN)
        def booking = new Booking(provider, offer, NIF, IBAN)
        and: 'there is a cancel'
        booking.cancel()

        when: 'booking'
        new Booking(provider, offer, NIF, IBAN)

        then: 'succeeds and is complete'
        offer.getNumberActiveOfBookings() == 3
    }
}
