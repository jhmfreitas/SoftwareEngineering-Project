package pt.ulisboa.tecnico.softeng.tax.domain;

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaxPayer extends TaxPayer_Base {
    private final static int PERCENTAGE = 5;

    protected TaxPayer() {
        // this is a FenixFramework artifact; if not present, compilation fails.
        // the empty constructor is used by the base class to materialize objects from
        // the database, and in this case the classes Seller_Base and Buyer_Base, which
        // extend this class, have the empty constructor, which need to be present in
        // their superclass
        super();
    }

    public TaxPayer(IRS irs, String nif, String name, String address) {
        checkArguments(irs, nif, name, address);

        setNif(nif);
        setName(name);
        setAddress(address);
        irs.addTaxPayer(this);
    }

    void delete() {
        setIrs(null);

        getAllInvoices().forEach(invoice -> invoice.delete());

        deleteDomainObject();
    }

    private void checkArguments(IRS irs, String nif, String name, String address) {
        if (nif == null || nif.length() != 9) {
            throw new TaxException();
        }

        if (name == null || name.length() == 0) {
            throw new TaxException();
        }

        if (address == null || address.length() == 0) {
            throw new TaxException();
        }

        if (irs.getTaxPayerByNif(nif) != null) {
            throw new TaxException();
        }

    }

    public Invoice getInvoiceByReference(String invoiceReference) {
        if (invoiceReference == null || invoiceReference.isEmpty()) {
            throw new TaxException();
        }

        return getAllInvoices()
                .filter(invoice -> invoice.getReference().equals(invoiceReference)).findAny().orElse(null);
    }

    private Stream<Invoice> getAllInvoices() {
        return Stream.concat(getBuyerInvoiceSet().stream(), getSellerInvoiceSet().stream()).distinct();
    }

    public long toPay(int year) {
        if (year < 1970) {
            throw new TaxException();
        }

        long result = 0;
        for (Invoice invoice : getSellerInvoiceSet()) {
            if (!invoice.isCancelled() && invoice.getDate().getYear() == year) {
                result = result + invoice.getIva();
            }
        }
        return result;
    }

    public Map<Integer, Long> getToPayPerYear() {
        return getSellerInvoiceSet().stream().map(i -> i.getDate().getYear()).distinct()
                .collect(Collectors.toMap(y -> y, this::toPay));
    }

    public long taxReturn(int year) {
        if (year < 1970) {
            throw new TaxException();
        }

        double result = 0;
        for (Invoice invoice : getBuyerInvoiceSet()) {
            if (!invoice.isCancelled() && invoice.getDate().getYear() == year) {
                result = result + new Double(invoice.getIva());
            }
        }
        return Math.round(result * PERCENTAGE / 100.0);
    }

    public Map<Integer, Long> getTaxReturnPerYear() {
        return getBuyerInvoiceSet().stream().map(i -> i.getDate().getYear()).distinct()
                .collect(Collectors.toMap(y -> y, y -> taxReturn(y)));
    }

}
