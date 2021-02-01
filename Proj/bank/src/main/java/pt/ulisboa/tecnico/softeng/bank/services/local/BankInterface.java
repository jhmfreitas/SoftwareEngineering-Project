package pt.ulisboa.tecnico.softeng.bank.services.local;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.bank.domain.*;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.AccountData;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankData;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.ClientData;
import pt.ulisboa.tecnico.softeng.bank.services.remote.dataobjects.RestBankOperationData;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BankInterface {

    @Atomic(mode = TxMode.READ)

    public static List<BankData> getBanks() {
        return FenixFramework.getDomainRoot().getBankSet().stream()
                .sorted(Comparator.comparing(Bank_Base::getName)).map(BankData::new)
                .collect(Collectors.toList());
    }

    @Atomic(mode = TxMode.WRITE)
    public static void createBank(BankData bankData) {
        new Bank(bankData.getName(), bankData.getCode());
    }

    @Atomic(mode = TxMode.READ)
    public static BankData getBankDataByCode(String code) {
        Bank bank = getBankByCode(code);
        if (bank == null) {
            return null;
        }

        return new BankData(bank);
    }

    @Atomic(mode = TxMode.WRITE)
    public static void createClient(String code, ClientData client) {
        Bank bank = getBankByCode(code);
        if (bank == null) {
            throw new BankException();
        }

        new Client(bank, client.getName());
    }

    @Atomic(mode = TxMode.READ)
    public static ClientData getClientDataById(String code, String id) {
        Bank bank = getBankByCode(code);
        if (bank == null) {
            return null;
        }

        Client client = bank.getClientById(id);
        if (client == null) {
            return null;
        }

        return new ClientData(client);
    }

    @Atomic(mode = TxMode.WRITE)
    public static void createAccount(String code, String id) {
        Bank bank = getBankByCode(code);
        if (bank == null) {
            throw new BankException();
        }

        Client client = bank.getClientById(id);
        if (client == null) {
            throw new BankException();
        }

        new Account(bank, client);
    }

    @Atomic(mode = TxMode.READ)
    public static AccountData getAccountData(String iban) {
        Account account = getAccountByIban(iban);
        if (account == null) {
            throw new BankException();
        }

        return new AccountData(account);
    }

    @Atomic(mode = TxMode.WRITE)
    public static void deposit(String iban, long amount) {
        Account account = getAccountByIban(iban);
        if (account == null) {
            throw new BankException();
        }

        account.deposit(amount);
    }

    @Atomic(mode = TxMode.WRITE)
    public static void withdraw(String iban, long amount) {
        Account account = getAccountByIban(iban);
        if (account == null) {
            throw new BankException();
        }

        account.withdraw(amount);
    }

    @Atomic(mode = TxMode.WRITE)
    public static String processPayment(RestBankOperationData bankOperationData) {
        if (bankOperationData.getTransactionSource() != null && bankOperationData.getTransactionSource().equals(TransferOperation.REVERT)) {
            throw new BankException();
        }

        Operation operation = getOperationBySourceAndReference(bankOperationData.getTransactionSource(),
                bankOperationData.getTransactionReference());
        if (operation != null) {
            return operation.getReference();
        }

        Account sourceAccount = FenixFramework.getDomainRoot().getBankSet().stream()
                .flatMap(bank -> bank.getAccountSet().stream())
                .filter(account -> account.getIban().equals(bankOperationData.getSourceIban()))
                .findAny().orElse(null);
        Account targetAccount = FenixFramework.getDomainRoot().getBankSet().stream()
                .flatMap(bank -> bank.getAccountSet().stream())
                .filter(account -> account.getIban().equals(bankOperationData.getTargetIban()))
                .findAny().orElse(null);

        if (sourceAccount != null && targetAccount != null) {
            WithdrawOperation withdrawOperation = sourceAccount.withdraw(bankOperationData.getValue());
            DepositOperation depositOperation = targetAccount.deposit(bankOperationData.getValue());

            TransferOperation transferOperation = new TransferOperation();
            transferOperation.init(withdrawOperation, depositOperation, bankOperationData.getTransactionSource(), bankOperationData.getTransactionReference());

            return transferOperation.getReference();
        }
        throw new BankException();
    }

    @Atomic(mode = TxMode.WRITE)
    public static String cancelPayment(String paymentConfirmation) {
        Operation operation = getOperationByReference(paymentConfirmation);

        if (operation == null) {
            throw new BankException();
        } else if (operation.getCancellation() != null) {
            return operation.getCancellation();
        } else {
            return operation.revert();
        }

    }

    @Atomic(mode = TxMode.READ)
    public static RestBankOperationData getOperationData(String reference) {
        Operation operation = getOperationByReference(reference);
        if (operation != null) {
            return new RestBankOperationData(operation);
        }
        throw new BankException();
    }

    @Atomic(mode = TxMode.WRITE)
    public static void deleteBanks() {
        FenixFramework.getDomainRoot().getBankSet().stream().forEach(b -> b.delete());
    }

    private static Operation getOperationByReference(String reference) {
        for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
            Operation operation = bank.getOperation(reference);
            if (operation != null) {
                return operation;
            }
        }
        return null;
    }

    @Atomic(mode = TxMode.WRITE)
    public static void revertOperation(String reference){
        Operation op = getOperationByReference(reference);

        if(op == null || op.getCancellation() != null){
            throw new BankException();
        }

        op.revert();

    }

    private static Operation getOperationBySourceAndReference(String transactionSource, String transactionReference) {
        for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
            Operation operation = bank.getOperationBySourceAndReference(transactionSource, transactionReference);
            if (operation != null) {
                return operation;
            }
        }
        return null;
    }

    private static Bank getBankByCode(String code) {
        return FenixFramework.getDomainRoot().getBankSet().stream().filter(b -> b.getCode().equals(code)).findFirst()
                .orElse(null);
    }

    private static Account getAccountByIban(String iban) {
        Account account = FenixFramework.getDomainRoot().getBankSet().stream().filter(b -> b.getAccount(iban) != null)
                .map(b -> b.getAccount(iban)).findFirst().orElse(null);
        return account;
    }

}
