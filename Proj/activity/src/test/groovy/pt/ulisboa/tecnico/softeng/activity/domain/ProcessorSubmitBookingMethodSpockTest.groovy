package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.BankException
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.RemoteAccessException
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.TaxException
import spock.lang.Unroll

class ProcessorSubmitBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
    def CANCEL_PAYMENT_REFERENCE = 'CancelPaymentReference'
    def INVOICE_REFERENCE = 'InvoiceReference'
    def PAYMENT_REFERENCE = 'PaymentReference'
    def AMOUNT = 30
    def IBAN = 'IBAN'
    def NIF = '123456789'

    def provider
    def offer
    def booking
    def booking2

    def bankInterface
    def taxInterface
    
    @Override
    def populate4Test() {
        bankInterface = Mock(BankInterface)
        taxInterface = Mock(TaxInterface)
        def processor = new Processor(bankInterface, taxInterface)

        provider = new ActivityProvider('XtremX', 'ExtremeAdventure', NIF, IBAN, processor)
        def activity = new Activity(provider, 'Bush Walking', 18, 80, 10)

        def begin = new LocalDate(2016, 12, 19)
        def end = new LocalDate(2016, 12, 21)
        offer = new ActivityOffer(activity, begin, end, AMOUNT)
        booking = new Booking(provider, offer, NIF, IBAN)
        booking2 = new Booking(provider, offer, NIF, IBAN)
    }

    def 'success'() {
        given:
        bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'booking an activity works fine'
        provider.getProcessor().submitBooking(booking)

        then:
        booking.getPaymentReference() == PAYMENT_REFERENCE
        booking.getInvoiceReference() == INVOICE_REFERENCE
    }

    @Unroll('one #label failure on submit invoice')
    def 'one #label failure on submit invoice'() {
        when: 'booking an activity'
        provider.getProcessor().submitBooking(booking)

        then: 'the process payment succeeds'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        and: 'the tax interface throws a TaxException'
        1 * taxInterface.submitInvoice(_) >> { throw exception }
        and: 'booking contains payment reference but not the invoice reference'
        booking.paymentReference == PAYMENT_REFERENCE
        booking.invoiceReference == null

        when: 'doing another booking'
        provider.getProcessor().submitBooking(booking2)

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
        exception                    | label
        new TaxException()           | 'tax'
        new RemoteAccessException()  | 'remote'
    }

    @Unroll('one #label failure on process payment')
    def 'one #label failure on process payment'() {
        when: 'booking an activity'
        provider.getProcessor().submitBooking(booking)

        then: 'the process payment succeeds'
        1 * bankInterface.processPayment(_) >> { throw exception }
        and: 'the tax interface throws a TaxException'
        0 * taxInterface.submitInvoice(_)
        and: 'booking contains payment reference but not the invoice reference'
        booking.paymentReference == null
        booking.invoiceReference == null

        when: 'doing another booking'
        provider.getProcessor().submitBooking(booking2)

        then: 'only the second booking invokes the bank interface'
        2 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        and: 'both invoke the tax interface'
        2 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE
        and: 'both bookings succeed'
        booking.paymentReference == PAYMENT_REFERENCE
        booking.invoiceReference == INVOICE_REFERENCE
        booking2.paymentReference == PAYMENT_REFERENCE
        booking2.invoiceReference == INVOICE_REFERENCE

        where:
        exception                    | label
        new BankException()          | 'bank'
        new RemoteAccessException()  | 'remote'
    }

    def 'successful cancel'() {
        when: 'a successful booking'
        provider.getProcessor().submitBooking(booking)

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
        provider.getProcessor().submitBooking(booking)

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
        provider.getProcessor().submitBooking(booking2)

        then: 'booking one is completely cancelled'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'booking two is completed'
        1 * bankInterface.processPayment(_)
        1 * taxInterface.submitInvoice(_)
    }

    def 'one remote exception on cancel payment'() {
        when: 'a successful booking'
        provider.getProcessor().submitBooking(booking)

        then: 'the remote invocations succeed'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'cancelling the booking'
        booking.cancel()

        then: 'a BankException is thrown'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> { throw new RemoteAccessException() }
        and: 'the cancel of the invoice is not done'
        0 * taxInterface.cancelInvoice(INVOICE_REFERENCE)

        when: 'a new booking is done'
        provider.getProcessor().submitBooking(booking2)

        then: 'booking one is completely cancelled'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'booking two is completed'
        1 * bankInterface.processPayment(_)
        1 * taxInterface.submitInvoice(_)
    }

    def 'one tax exception on cancel invoice'() {
        when: 'a successful booking'
        provider.getProcessor().submitBooking(booking)

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
        provider.getProcessor().submitBooking(booking2)

        then: 'booking one is completely cancelled'
        0 * bankInterface.cancelPayment(PAYMENT_REFERENCE)
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'booking two is completed'
        1 * bankInterface.processPayment(_)
        1 * taxInterface.submitInvoice(_)
    }

    def 'one remote exception on cancel invoice'() {
        when: 'a successful booking'
        provider.getProcessor().submitBooking(booking)

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
        provider.getProcessor().submitBooking(booking2)

        then: 'booking one is completely cancelled'
        0 * bankInterface.cancelPayment(PAYMENT_REFERENCE)
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'booking two is completed'
        1 * bankInterface.processPayment(_)
        1 * taxInterface.submitInvoice(_)
    }

}
