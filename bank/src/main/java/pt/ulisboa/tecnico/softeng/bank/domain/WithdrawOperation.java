package pt.ulisboa.tecnico.softeng.bank.domain;

public class WithdrawOperation extends WithdrawOperation_Base {
    @Override
    public void delete() {
        if (getTransferOperationAsWithdraw() != null) {
            TransferOperation transferOperation = getTransferOperationAsWithdraw();
            setTransferOperationAsWithdraw(null);
            transferOperation.delete();
        }

        super.delete();
    }

    @Override
    public Operation.Type getType() {
        return Type.WITHDRAW;
    }

    @Override
    protected String doRevert() {
        return getAccount().deposit(getValue()).getReference();
    }
}
