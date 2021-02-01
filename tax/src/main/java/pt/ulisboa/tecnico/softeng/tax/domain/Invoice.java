package pt.ulisboa.tecnico.softeng.tax.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class Invoice extends Invoice_Base {

    public Invoice(long value, LocalDate date, ItemType itemType, TaxPayer seller, TaxPayer buyer, DateTime time) {
        checkArguments(value, date, itemType, seller, buyer, time);

        setReference(Integer.toString(seller.getIrs().getCounter()));
        setValue(value);
        setDate(date);
        setCancelled(false);
        setItemType(itemType);
        setSeller(seller);
        setBuyer(buyer);
        setTime(time);

        setIva(Math.round(value * itemType.getTax() / 100.0));

        setIrs(getSeller().getIrs());

    }

    public Invoice(long value, LocalDate date, ItemType itemType, TaxPayer seller, TaxPayer buyer) {
        this(value, date, itemType, seller, buyer, DateTime.now());
    }

    void delete() {
        setIrs(null);
        setSeller(null);
        setBuyer(null);
        setItemType(null);

        deleteDomainObject();
    }

    private void checkArguments(long value, LocalDate date, ItemType itemType, TaxPayer seller, TaxPayer buyer,
                                DateTime time) {
        if (value <= 0) {
            throw new TaxException();
        }

        if (date == null || date.getYear() < 1970) {
            throw new TaxException();
        }

        if (itemType == null) {
            throw new TaxException();
        }

        if (seller == null || buyer == null || seller == buyer) {
            throw new TaxException();
        }

        if (time == null) {
            throw new TaxException();
        }

    }

    public void cancel() {
        setCancelled(true);
    }

    public boolean isCancelled() {
        return getCancelled();
    }

}
