package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.BankException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.RemoteAccessException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.TaxException
import spock.lang.Unroll

class ProcessorSubmitBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
    def INVOICE_REFERENCE = 'InvoiceReference'
    def PAYMENT_REFERENCE = 'PaymentReference'
    def CANCEL_PAYMENT_REFERENCE = 'CancelPaymentReference'

    def arrival = new LocalDate(2016, 12, 19)
    def departure = new LocalDate(2016, 12, 24)
    def arrivalTwo = new LocalDate(2016, 12, 25)
    def departureTwo = new LocalDate(2016, 12, 28)
    def NIF_HOTEL = "123456700"
    def NIF_BUYER = "123456789"
    def IBAN_BUYER = "IBAN_BUYER"

    def hotel
    def room
    def booking
    def booking2

    def bankInterface
    def taxInterface

    @Override
    def populate4Test() {
        bankInterface = Mock(BankInterface)
        taxInterface = Mock(TaxInterface)
        def processor = new Processor(bankInterface, taxInterface)

        hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, "IBAN", 20, 30, processor)
        room = new Room(hotel, "01", Room.Type.SINGLE)
        booking = new Booking(room, arrival, departure, NIF_BUYER, IBAN_BUYER)
        booking2 = new Booking(room, arrivalTwo, departureTwo, NIF_BUYER, IBAN_BUYER)
    }

    def 'success'() {
        given: 'mocking the remote invocations to succeed and return references'
        bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'booking a room works fine'
        hotel.getProcessor().submitBooking(booking)

        then:
        booking.paymentReference == PAYMENT_REFERENCE
        booking.invoiceReference == INVOICE_REFERENCE
    }

    @Unroll('one #label failure on submit invoice')
    def 'one #label failure on submit invoice'() {
        when: 'booking a room'
        hotel.getProcessor().submitBooking(booking)

        then: 'the process payment succeeds'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        and: 'the tax interface throws a TaxException'
        1 * taxInterface.submitInvoice(_) >> { throw exception }
        and: 'booking contains payment reference but not the invoice reference'
        booking.paymentReference == PAYMENT_REFERENCE
        booking.invoiceReference == null

        when: 'doing another booking'
        hotel.getProcessor().submitBooking(booking2)

        then: 'only the second booking invokes the bank interface'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        and: 'both invoke the tax interface'
        2 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE
        and: 'both bookings succeed'
        booking.paymentReference == PAYMENT_REFERENCE
        booking.invoiceReference == INVOICE_REFERENCE
        booking2.paymentReference == PAYMENT_REFERENCE
        booking2.invoiceReference == INVOICE_REFERENCE

        where:
        exception                   | label
        new TaxException()          | 'tax'
        new RemoteAccessException() | 'remote'
    }

    @Unroll('one #label failure on process payment')
    def 'one #label failure on process payment'() {
        when: 'booking a room'
        hotel.getProcessor().submitBooking(booking)

        then: 'the process payment throws a BankException'
        1 * bankInterface.processPayment(_) >> { throw exception }
        and: 'the tax interface is not invoked'
        0 * taxInterface.submitInvoice(_)
        and: 'both references are null'
        booking.paymentReference == null
        booking.invoiceReference == null

        when: 'doing another booking'
        hotel.getProcessor().submitBooking(booking2)

        then: 'both invoke the bank interface'
        2 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        and: 'both invoke the tax interface'
        2 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE
        and: 'both bookings succeed'
        booking.paymentReference == PAYMENT_REFERENCE
        booking.invoiceReference == INVOICE_REFERENCE
        booking2.paymentReference == PAYMENT_REFERENCE
        booking2.invoiceReference == INVOICE_REFERENCE

        where:
        exception                   | label
        new BankException()         | 'tax'
        new RemoteAccessException() | 'remote'
    }

    def 'successful cancel'() {
        when: 'a successful booking'
        hotel.getProcessor().submitBooking(booking)

        then: 'the remote invocations succeed'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'cancelling the booking'
        booking.cancel()

        then: 'a cancel payment succeeds'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        and: 'the invoice is cancelled'
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'a cancel reference is stored'
        booking.cancelledPaymentReference == CANCEL_PAYMENT_REFERENCE
        and: 'the booking state is cancelled'
        booking.cancelledInvoice
        and: 'the original references are kept'
        booking.paymentReference == PAYMENT_REFERENCE
        booking.invoiceReference == INVOICE_REFERENCE
    }

    def 'one bank exception on cancel payment'() {
        when: 'a successful booking'
        hotel.getProcessor().submitBooking(booking)

        then: 'the remote invocations succeed'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'cancelling the booking'
        booking.cancel()

        then: 'a BankException is thrown'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> { throw new BankException() }
        and: 'the cancel of the invoice is not done'
        0 * taxInterface.cancelInvoice(INVOICE_REFERENCE)

        when: 'a new booking is done'
        hotel.getProcessor().submitBooking(booking2)

        then: 'booking one is completely cancelled'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'booking two is completed'
        1 * bankInterface.processPayment(_)
        1 * taxInterface.submitInvoice(_)
    }

    def 'one remote exception on cancel payment'() {
        when: 'a successful booking'
        hotel.getProcessor().submitBooking(booking)

        then: 'the remote invocations succeed'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'cancelling the booking'
        booking.cancel()

        then: 'a RemoteAccessException is thrown'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> { throw new RemoteAccessException() }
        and: 'the cancel of the invoice is not done'
        0 * taxInterface.cancelInvoice(INVOICE_REFERENCE)

        when: 'a new booking is done'
        hotel.getProcessor().submitBooking(booking2)

        then: 'booking one is completely cancelled'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'booking two is completed'
        1 * bankInterface.processPayment(_)
        1 * taxInterface.submitInvoice(_)
    }

    def 'one tax exception on cancel invoice'() {
        when: 'a successful booking'
        hotel.getProcessor().submitBooking(booking)

        then: 'the remote invocations succeed'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'cancelling the booking'
        booking.cancel()

        then: 'the payment is cancelled'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        and: 'the cancel of the invoice throws a TaxException'
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw new TaxException() }

        when: 'a new booking is done'
        hotel.getProcessor().submitBooking(booking2)

        then: 'booking one is completely cancelled'
        0 * bankInterface.cancelPayment(PAYMENT_REFERENCE)
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'booking two is completed'
        1 * bankInterface.processPayment(_)
        1 * taxInterface.submitInvoice(_)
    }

    def 'one remote exception on cancel invoice'() {
        when: 'a successful booking'
        hotel.getProcessor().submitBooking(booking)

        then: 'the remote invocations succeed'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'cancelling the booking'
        booking.cancel()

        then: 'the payment is cancelled'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        and: 'the cancel of the invoice throws a RemoteAccessException'
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw new RemoteAccessException() }

        when: 'a new booking is done'
        hotel.getProcessor().submitBooking(booking2)

        then: 'booking one is completely cancelled'
        0 * bankInterface.cancelPayment(PAYMENT_REFERENCE)
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'booking two is completed'
        1 * bankInterface.processPayment(_)
        1 * taxInterface.submitInvoice(_)
    }
}
