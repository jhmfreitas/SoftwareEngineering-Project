package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException
import spock.lang.Unroll

class TaxPaymentStateMethodSpockTest extends SpockRollbackTestAbstractClass {
    def broker
    def taxInterface
    def client
    def adventure

    @Override
    def populate4Test() {
        taxInterface = Mock(TaxInterface)
        broker = new Broker('BR01', 'eXtremeADVENTURE', BROKER_NIF, BROKER_IBAN,
                new ActivityInterface(), new HotelInterface(), new CarInterface(), new BankInterface(), taxInterface)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN, Adventure.BookRoom.DOUBLE, Adventure.RentVehicle.CAR)

        adventure.setState(Adventure.State.TAX_PAYMENT)
    }

    def 'success tax payment'() {
        given: 'the tax payment is successful'
        taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'a next step in the adventure is processed'
        adventure.process()

        then: 'the adventure state progresses to confirmed'
        adventure.getState().getValue() == Adventure.State.CONFIRMED
        and: 'the tax is confirmed'
        adventure.getInvoiceReference() == INVOICE_REFERENCE
    }

    @Unroll('#process_iterations #exception is thrown (state: #state.toString())')
    def '#process_iterations #exception exception'() {
        given: 'the tax payment throws an exception'
        taxInterface.submitInvoice(_) >> { throw mock_exception }

        when: 'a next step in the adventure is processed'
        1.upto(process_iterations) { adventure.process() }

        then: 'the adventure state progresses to either undo or tax payment'
        adventure.getState().getValue() == state
        and: 'the tax confirmation is null'
        adventure.getInvoiceReference() == null

        where:
        mock_exception              | state                       | process_iterations                    | exception
        new TaxException()          | Adventure.State.UNDO        | 1                                     | 'TaxException'
        new RemoteAccessException() | Adventure.State.TAX_PAYMENT | 1                                     | 'RemoteAccessException'
        new RemoteAccessException() | Adventure.State.TAX_PAYMENT | TaxPaymentState.MAX_REMOTE_ERRORS - 1 | 'RemoteAccessException'
        new RemoteAccessException() | Adventure.State.UNDO        | TaxPaymentState.MAX_REMOTE_ERRORS     | 'RemoteAccessException'
    }

    def 'two remote access exception one success'() {
        given: 'the tax payment throws a remote access exception and they return a reference'
        taxInterface.submitInvoice(_) >>
                { throw new RemoteAccessException() } >>
                { throw new RemoteAccessException() } >>
                INVOICE_REFERENCE

        when: 'the adventure is processed 4 times'
        1.upto(4) { adventure.process() }

        then: 'the adventure state progresses to confirmed'
        adventure.getState().getValue() == Adventure.State.CONFIRMED
        and: 'the tax payment is confirmed'
        adventure.getInvoiceReference() == INVOICE_REFERENCE
    }

    def 'one remote access exception and one tax exception'() {
        given: 'the tax payment throws a remote access exception'
        taxInterface.submitInvoice(_) >> { throw new RemoteAccessException() }

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state is not changed'
        adventure.getState().getValue() == Adventure.State.TAX_PAYMENT
        and: 'the number of errors is 1'
        adventure.getState().getNumOfRemoteErrors() == 1

        when: 'the adventure is processed again'
        adventure.process()

        then: 'the tax payment throws a tax exception'
        taxInterface.submitInvoice(_) >> { throw new TaxException() }
        and: 'the adventure state progresses to undo'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the tax payment confirmation is null'
        adventure.getInvoiceReference() == null
    }

}
