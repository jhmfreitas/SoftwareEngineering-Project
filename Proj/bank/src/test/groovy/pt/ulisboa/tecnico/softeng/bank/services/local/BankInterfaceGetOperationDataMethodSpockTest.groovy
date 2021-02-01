package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type
import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class BankInterfaceGetOperationDataMethodSpockTest extends SpockRollbackTestAbstractClass {
    def AMOUNT = 100
    def bank
    def account
    def reference

    @Override
    def populate4Test() {
        bank = new Bank('Money', 'BK01')
        def client = new Client(bank, 'António')
        account = new Account(bank, client)
        reference = account.deposit(AMOUNT).getReference()
    }

    def 'success'() {
        when:
        def data = BankInterface.getOperationData(reference)

        then:
        with(data) {
            getReference() == reference
            getSourceIban() == account.getIban()
            getType() == Type.DEPOSIT.name()
            getValue() == AMOUNT
            getTime() != null
        }
    }

    @Unroll('operationData: #label')
    def 'problem get operation data'() {
        when:
        BankInterface.getOperationData('')

        then:
        thrown(BankException)

        where:
        payConf | label
        null    | 'null reference'
        ''      | 'empty reference'
        'XPTO'  | 'not exists reference'
    }
}
