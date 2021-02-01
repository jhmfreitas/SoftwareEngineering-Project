package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.*
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import pt.ulisboa.tecnico.softeng.bank.services.remote.dataobjects.RestBankOperationData
import spock.lang.Shared
import spock.lang.Unroll

class BankInterfaceProcessPaymentMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def TRANSACTION_SOURCE = 'ADVENTURE'
    @Shared def TRANSACTION_REFERENCE = 'REFERENCE'
    def bank
    def sourceAccount
    @Shared def sourceIban
    def targetAccount
    @Shared def targetIban

    @Override
    def populate4Test() {
        bank = new Bank('Money', 'BK01')

        def sourceClient = new Client(bank, 'António')
        sourceAccount = new Account(bank, sourceClient)
        sourceAccount.deposit(500)
        sourceIban = sourceAccount.getIban()

        def targetClient = new Client(bank, 'José')
        targetAccount = new Account(bank, targetClient)
        targetIban = targetAccount.getIban()
    }

    def 'success'() {
        when: 'a payment is processed for this sourceAccount'
        def newReference = BankInterface.processPayment(new RestBankOperationData(sourceIban, targetIban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

        then: 'the operation occurs and a reference is generated'
        newReference != null
        newReference.startsWith('BK01')
        bank.getOperation(newReference) != null
        bank.getOperation(newReference).getType() == Operation.Type.TRANSFER
        bank.getOperation(newReference).getValue() == 100.0
        sourceAccount.getBalance() == 400.0
        targetAccount.getBalance() == 100.0
    }

    def 'success two banks'() {
        given:
        def otherBank = new Bank('Money', 'BK02')
        def otherClientOne = new Client(otherBank, 'Manuel')
        def otherAccountOne = new Account(otherBank, otherClientOne)
        def otherIbanOne = otherAccountOne.getIban()
        otherAccountOne.deposit(1000)
        def otherClientTwo = new Client(otherBank, 'Ferreira')
        def otherAccountTwo = new Account(otherBank, otherClientTwo)
        def otherIbanTwo = otherAccountTwo.getIban()

        when:
        BankInterface.processPayment(new RestBankOperationData(otherIbanOne, otherIbanTwo, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

        then:
        otherAccountOne.getBalance() == 900.0

        when:
        BankInterface.processPayment(new RestBankOperationData(sourceIban, targetIban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE + 'PLUS'))

        then:
        sourceAccount.getBalance() == 400
    }

    def 'success between different banks'() {
        given: 'another bank'
        def otherBank = new Bank('Money', 'BK02')
        def otherClientOne = new Client(otherBank, 'Manuel')
        def otherAccountOne = new Account(otherBank, otherClientOne)
        otherAccountOne.deposit(1000)
        def otherIbanOne = otherAccountOne.getIban()

        when: 'a transference between accounts belonging to different banks'
        BankInterface.processPayment(new RestBankOperationData(otherIbanOne, targetIban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

        then: 'the balances are correct'
        otherAccountOne.getBalance() == 900.0
        targetAccount.getBalance() == 100.0
        and: 'the operations were created'
        otherBank.getOperationSet().size() == 3
        bank.getOperationSet().size() == 2
    }

    def 'redo an already payed'() {
        given: 'a payment to the sourceAccount'
        def firstReference = BankInterface.processPayment(new RestBankOperationData(sourceIban, targetIban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

        when: 'when there is a second payment for the same reference'
        def secondReference = BankInterface.processPayment(new RestBankOperationData(sourceIban, targetIban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

        then: 'the operation is idempotent'
        secondReference == firstReference
        and: 'does not withdraw twice'
        sourceAccount.getBalance() == 400.0
    }

    def 'one amount'() {
        when: 'a payment of 1'
        BankInterface.processPayment(new RestBankOperationData(this.sourceIban, targetIban, 1, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

        then:
        sourceAccount.getBalance() == 499.0
    }


    @Unroll('bank operation data, process payment #label: #ibnSource, #ibnTarget, #val, #transaction_source, #transaction_reference')
    def 'problem process payment '() {
        when: 'process payment'
        BankInterface.processPayment(
                new RestBankOperationData(sourceIbn, targetIbn, val, transaction_source, transaction_reference))

        then: 'throw exception'
        thrown(BankException)

        where: 'for incorrect arguments'
        sourceIbn  | targetIbn  | val | transaction_source       | transaction_reference | label
        null       | targetIban | 100 | TRANSACTION_SOURCE       | TRANSACTION_REFERENCE | 'null sourceIban'
        '  '       | targetIban | 100 | TRANSACTION_SOURCE       | TRANSACTION_REFERENCE | 'blank sourceIban'
        ''         | targetIban | 100 | TRANSACTION_SOURCE       | TRANSACTION_REFERENCE | 'empty sourceIban'
        'other'    | targetIban | 100 | TRANSACTION_SOURCE       | TRANSACTION_REFERENCE | 'sourceAccount does not exist for other sourceIban'
        sourceIban | null       | 100 | TRANSACTION_SOURCE       | TRANSACTION_REFERENCE | 'null targetIban'
        sourceIban | '  '       | 100 | TRANSACTION_SOURCE       | TRANSACTION_REFERENCE | 'blank targetIban'
        sourceIban | ''         | 100 | TRANSACTION_SOURCE       | TRANSACTION_REFERENCE | 'empty targetIban'
        sourceIban | 'other'    | 100 | TRANSACTION_SOURCE       | TRANSACTION_REFERENCE | 'targetAccount does not exist for other iban'
        sourceIban | targetIban | 0   | TRANSACTION_SOURCE       | TRANSACTION_REFERENCE | '0 amount'
        sourceIban | targetIban | 0   | null                     | TRANSACTION_REFERENCE | 'null transaction source'
        sourceIban | targetIban | 0   | '  '                     | TRANSACTION_REFERENCE | 'blank transaction source'
        sourceIban | targetIban | 0   | ''                       | TRANSACTION_REFERENCE | 'empty transaction source'
        sourceIban | targetIban | 0   | 'whatever'               | TRANSACTION_REFERENCE | 'whatever transaction source'
        sourceIban | targetIban | 0   | TransferOperation.REVERT | TRANSACTION_REFERENCE | 'REVERT transaction source'
        sourceIban | targetIban | 0   | TRANSACTION_SOURCE       | null                  | 'null transaction reference'
        sourceIban | targetIban | 0   | TRANSACTION_SOURCE       | '  '                  | 'blank transaction reference'
        sourceIban | targetIban | 0   | TRANSACTION_SOURCE       | ''                    | 'empty transaction reference'
        sourceIban | targetIban | 0   | TRANSACTION_SOURCE       | 'whatever'            | 'whatever transaction reference'
    }

    def 'no banks'() {
        given: 'remove all banks'
        bank.delete()

        when: 'process payment'
        BankInterface.processPayment(
                new RestBankOperationData(sourceIban, targetIban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

        then: 'an exception is thrown'
        thrown(BankException)
    }
}
