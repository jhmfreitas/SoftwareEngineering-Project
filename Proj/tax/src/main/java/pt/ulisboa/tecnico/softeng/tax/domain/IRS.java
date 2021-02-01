package pt.ulisboa.tecnico.softeng.tax.domain;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class IRS extends IRS_Base {
    public static final int SCALE = 1000;

    public static IRS getIRSInstance() {
        if (FenixFramework.getDomainRoot().getIrs() == null) {
            return createIrs();
        }
        return FenixFramework.getDomainRoot().getIrs();
    }

    @Atomic(mode = TxMode.WRITE)
    private static IRS createIrs() {
        return new IRS();
    }

    private IRS() {
        setRoot(FenixFramework.getDomainRoot());
    }

    public void delete() {
        setRoot(null);

        clearAll();

        deleteDomainObject();
    }

    public TaxPayer getTaxPayerByNif(String nif) {
        for (TaxPayer taxPayer : getTaxPayerSet()) {
            if (taxPayer.getNif().equals(nif)) {
                return taxPayer;
            }
        }
        return null;
    }

    public ItemType getItemTypeByName(String name) {
        for (ItemType itemType : getItemTypeSet()) {
            if (itemType.getName().equals(name)) {
                return itemType;
            }
        }
        return null;
    }

    private void clearAll() {
        for (ItemType itemType : getItemTypeSet()) {
            itemType.delete();
        }

        for (TaxPayer taxPayer : getTaxPayerSet()) {
            taxPayer.delete();
        }

        for (Invoice invoice : getInvoiceSet()) {
            invoice.delete();
        }

    }

    @Override
    public int getCounter() {
        int counter = super.getCounter() + 1;
        setCounter(counter);
        return counter;
    }

}
