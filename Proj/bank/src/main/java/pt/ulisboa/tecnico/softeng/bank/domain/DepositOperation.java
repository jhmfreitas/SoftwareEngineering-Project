package pt.ulisboa.tecnico.softeng.bank.domain;

public class DepositOperation extends DepositOperation_Base {

    @Override
    public void delete() {
        if (getTransferOperationAsDeposit() != null) {
            TransferOperation transferOperation = getTransferOperationAsDeposit();
            setTransferOperationAsDeposit(null);
            transferOperation.delete();
        }

        super.delete();
    }

    @Override
    public Operation.Type getType() {
        return Type.DEPOSIT;
    }

    @Override
    protected String doRevert() {
        return getAccount().withdraw(getValue()).getReference();
    }

}
