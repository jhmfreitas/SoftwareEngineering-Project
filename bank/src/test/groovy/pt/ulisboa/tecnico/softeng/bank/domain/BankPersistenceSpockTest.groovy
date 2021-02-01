package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.bank.services.local.BankInterface
import pt.ulisboa.tecnico.softeng.bank.services.remote.dataobjects.RestBankOperationData

class BankPersistenceSpockTest extends SpockPersistenceTestAbstractClass {
    def TRANSACTION_SOURCE = 'transactionSource'
    def TRANSACTION_REFERENCE = 'transactionReference'
    def BANK_NAME = 'Money'
    def BANK_CODE = 'BK01'
    def CLIENT_NAME = 'João dos Anzóis'

    @Override
    def whenCreateInDatabase() {
        def bank = new Bank(BANK_NAME, BANK_CODE)
        def client = new Client(bank, CLIENT_NAME)
        def sourceAccount = new Account(bank, client)
        def targetAccount = new Account(bank, client)
        sourceAccount.deposit(100)
        sourceAccount.withdraw(50)
        BankInterface.processPayment(new RestBankOperationData(sourceAccount.getIban(), targetAccount.getIban(), 50,
                TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
    }

    @Override
    def thenAssert() {
        assert FenixFramework.getDomainRoot().getBankSet().size() == 1

        def banks = new ArrayList<>(FenixFramework.getDomainRoot().getBankSet())
        def bank = banks.get(0)

        assert bank.getName() == BANK_NAME
        assert bank.getCode() == BANK_CODE
        assert bank.getClientSet().size() == 1
        assert bank.getAccountSet().size() == 2
        assert bank.getOperationSet().size() == 5

        def clients = new ArrayList<>(bank.getClientSet())
        def client = clients.get(0)

        assert client.getName() == CLIENT_NAME

        for (Account account : bank.getAccountSet()) {
            assert account.getClient() == client
            assert account.getIban() != null
            assert account.getBalance() == 0.0 || account.getBalance() == 50.0

        }

        for (Operation operation : bank.getOperationSet()) {
            assert operation.getValue() == 100.0 || operation.getValue() == 50.0
            assert operation.getReference() != null
            assert operation.getTime() != null
            assert operation.getSourceIban() != null
            switch (operation.getType()) {
                case Operation.Type.DEPOSIT:
                    assert operation.getValue() == 100.0 || operation.getValue() == 50.0
                    assert operation.getTargetIban() == null;
                    assert operation.getTransactionSource() == null
                    assert operation.getTransactionReference() == null
                    break
                case Operation.Type.WITHDRAW:
                    assert operation.getValue() == 50.0
                    assert operation.getTargetIban() == null;
                    assert operation.getTransactionSource() == null
                    assert operation.getTransactionReference() == null
                    break
                case Operation.Type.TRANSFER:
                    assert operation.getValue() == 50.0
                    assert operation.getTargetIban() != null;
                    assert operation.getTransactionSource() != null
                    assert operation.getTransactionReference() != null
                    break
                default:
                    assert false
            }

        }

    }

    @Override
    def deleteFromDatabase() {
        for (def bank : FenixFramework.getDomainRoot().getBankSet()) {
            bank.delete()
        }
    }

}
