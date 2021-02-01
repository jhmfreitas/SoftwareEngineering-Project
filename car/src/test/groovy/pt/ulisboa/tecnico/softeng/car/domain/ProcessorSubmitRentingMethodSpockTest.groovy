package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.BankException
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.RemoteAccessException
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.TaxException
import spock.lang.Unroll


class ProcessorSubmitRentingMethodSpockTest extends SpockRollbackTestAbstractClass {
    def INVOICE_REFERENCE = 'InvoiceReference'
    def PAYMENT_REFERENCE = 'PaymentReference'
    def CANCEL_PAYMENT_REFERENCE = 'CancelPaymentReference'
    def PLATE_CAR = '22-33-HZ'
    def DRIVING_LICENSE = 'br112233'
    def date0 = LocalDate.parse('2018-01-05')
    def date1 = LocalDate.parse('2018-01-06')
    def date2 = LocalDate.parse('2018-01-07')
    def date3 = LocalDate.parse('2018-01-08')
    def RENT_A_CAR_NAME = 'Eartz'
    def NIF = 'NIF'
    def NIF_CUSTOMER = 'NIF1'
    def IBAN = 'IBAN'
    def IBAN_CUSTOMER = 'IBAN'
    def rentACar
    def car
    def rentingOne
    def rentingTwo
    def bankInterface
    def taxInterface

    @Override
    def populate4Test() {
        bankInterface = Mock(BankInterface)
        taxInterface = Mock(TaxInterface)
        def processor = new Processor(bankInterface, taxInterface)

        rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN, processor)
        car = new Car(PLATE_CAR, 10, 10, rentACar)
        rentingOne = new Renting(DRIVING_LICENSE, date0, date1, car, NIF_CUSTOMER, IBAN_CUSTOMER)
        rentingTwo = new Renting(DRIVING_LICENSE, date2, date3, car, NIF_CUSTOMER, IBAN_CUSTOMER)
    }

    def 'success'() {
        given: 'mocking the remote invocations to succeed and return references'
        bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'renting a car'
        rentACar.getProcessor().submitRenting(rentingOne)

        then: 'renting contains the correct references'
        rentingOne.paymentReference == PAYMENT_REFERENCE
        rentingOne.invoiceReference == INVOICE_REFERENCE
    }

    @Unroll('the #failure occurred')
    def 'one failure on submit invoice'() {
        when: 'renting a car'
        rentACar.getProcessor().submitRenting(rentingOne)

        then: 'the process payment succeeds'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        and: 'the tax interface throws a TaxException'
        1 * taxInterface.submitInvoice(_) >> { throw exception }
        and: 'renting contains payment reference but not the invoice reference'
        rentingOne.paymentReference == PAYMENT_REFERENCE
        rentingOne.invoiceReference == null

        when: 'doing another renting'
        rentACar.getProcessor().submitRenting(rentingTwo)

        then: 'only the second renting invokes the bank interface'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        and: 'both invoke the tax interface'
        2 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE
        and: 'both rentings succeed'
        rentingOne.paymentReference == PAYMENT_REFERENCE
        rentingOne.invoiceReference == INVOICE_REFERENCE
        rentingTwo.paymentReference == PAYMENT_REFERENCE
        rentingTwo.invoiceReference == INVOICE_REFERENCE

        where:
        exception                   | failure
        new TaxException()          | 'tax exception'
        new RemoteAccessException() | 'remote access exception'
    }

    @Unroll('the #failure occurred')
    def 'one failure on process payment'() {
        when: 'renting a car'
        rentACar.getProcessor().submitRenting(rentingOne)

        then: 'the process payment throws a BankException'
        1 * bankInterface.processPayment(_) >> { throw exception }
        and: 'the tax interface is not invoked'
        0 * taxInterface.submitInvoice(_)
        and: 'both references are null'
        rentingOne.paymentReference == null
        rentingOne.invoiceReference == null

        when: 'doing another renting'
        rentACar.getProcessor().submitRenting(rentingTwo)

        then: 'both invoke the bank interface'
        2 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        and: 'both invoke the tax interface'
        2 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE
        and: 'both rentings succeed'
        rentingOne.paymentReference == PAYMENT_REFERENCE
        rentingOne.invoiceReference == INVOICE_REFERENCE
        rentingTwo.paymentReference == PAYMENT_REFERENCE
        rentingTwo.invoiceReference == INVOICE_REFERENCE

        where:
        exception                   | failure
        new BankException()         | 'tax exception'
        new RemoteAccessException() | 'remote access exception'
    }

    def 'successful cancel'() {
        when: 'a successful renting'
        rentACar.getProcessor().submitRenting(rentingOne)

        then: 'the remote invocations succeed'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'cancelling the renting'
        rentingOne.cancel()

        then: 'a cancel payment succeeds'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        and: 'the invoice is cancelled'
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'a cancel reference is stored'
        rentingOne.cancelledPaymentReference == CANCEL_PAYMENT_REFERENCE
        and: 'the renting state is cancelled'
        rentingOne.cancelledInvoice
        and: 'the original references are kept'
        rentingOne.paymentReference == PAYMENT_REFERENCE
        rentingOne.invoiceReference == INVOICE_REFERENCE
    }

    @Unroll('the #failure occurred')
    def 'one failure on cancel payment'() {
        when: 'a successful renting'
        rentACar.getProcessor().submitRenting(rentingOne)

        then: 'the remote invocations succeed'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'cancelling the renting'
        rentingOne.cancel()

        then: 'a BankException is thrown'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> { throw exception }
        and: 'the cancel of the invoice is not done'
        0 * taxInterface.cancelInvoice(INVOICE_REFERENCE)

        when: 'a new renting is done'
        rentACar.getProcessor().submitRenting(rentingTwo)

        then: 'renting one is completely cancelled'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'renting two is completed'
        1 * bankInterface.processPayment(_)
        1 * taxInterface.submitInvoice(_)

        where:
        exception                   | failure
        new BankException()         | 'bank exception'
        new RemoteAccessException() | 'remote access exception'
    }

    @Unroll('the #failure occurred')
    def 'one failure on cancel invoice'() {
        when: 'a successful renting'
        rentACar.getProcessor().submitRenting(rentingOne)

        then: 'the remote invocations succeed'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'cancelling the renting'
        rentingOne.cancel()

        then: 'the payment is cancelled'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        and: 'the cancel of the invoice throws a TaxException'
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw exception }

        when: 'a new renting is done'
        rentACar.getProcessor().submitRenting(rentingTwo)

        then: 'renting one is completely cancelled'
        0 * bankInterface.cancelPayment(PAYMENT_REFERENCE)
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'renting two is completed'
        1 * bankInterface.processPayment(_)
        1 * taxInterface.submitInvoice(_)

        where:
        exception                   | failure
        new TaxException()          | 'tax exception'
        new RemoteAccessException() | 'remote access exception'
    }

}
