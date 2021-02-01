package pt.ulisboa.tecnico.softeng.activity.domain;

import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.BankException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.TaxException;

import java.util.HashSet;
import java.util.Set;

public class Processor extends Processor_Base {
    private static final String TRANSACTION_SOURCE = "ACTIVITY";

    private BankInterface bankInterface;
    private TaxInterface taxInterface;

    public Processor(BankInterface bankInterface, TaxInterface taxInterface) {
        this.bankInterface = bankInterface;
        this.taxInterface = taxInterface;
    }

    void delete() {
        setActivityProvider(null);

        for (Booking booking : getBookingSet()) {
            booking.delete();
        }

        deleteDomainObject();
    }

    public void submitBooking(Booking booking) {
        addBooking(booking);
        processInvoices();
    }

    private void processInvoices() {
        Set<Booking> failedToProcess = new HashSet<>();
        for (Booking booking : getBookingSet()) {
            if (!booking.isCancelled()) {
                if (booking.getPaymentReference() == null) {
                    try {
                        booking.setPaymentReference(getBankInterface().processPayment(new RestBankOperationData(
                                booking.getBuyerIban(), booking.getProviderIban(), booking.getAmount(), TRANSACTION_SOURCE, booking.getReference())));
                    } catch (BankException | RemoteAccessException ex) {
                        failedToProcess.add(booking);
                        continue;
                    }
                }
                RestInvoiceData invoiceData = new RestInvoiceData(booking.getProviderNif(), booking.getBuyerNif(),
                        booking.getType(), booking.getAmount(), booking.getDate(), booking.getTime());
                try {
                    booking.setInvoiceReference(getTaxInterface().submitInvoice(invoiceData));
                } catch (TaxException | RemoteAccessException ex) {
                    failedToProcess.add(booking);
                }
            } else {
                try {
                    if (booking.getCancelledPaymentReference() == null) {
                        booking.setCancelledPaymentReference(
                                getBankInterface().cancelPayment(booking.getPaymentReference()));
                    }
                    getTaxInterface().cancelInvoice(booking.getInvoiceReference());
                    booking.setCancelledInvoice(true);
                } catch (BankException | TaxException | RemoteAccessException ex) {
                    failedToProcess.add(booking);
                }

            }
        }

        for (Booking booking : getBookingSet()) {
            removeBooking(booking);
        }

        for (Booking booking : failedToProcess) {
            addBooking(booking);
        }
    }

    private BankInterface getBankInterface() {
        if (this.bankInterface == null) {
            this.bankInterface = new BankInterface();
        }
        return this.bankInterface;
    }

    private TaxInterface getTaxInterface() {
        if (this.taxInterface == null) {
            this.taxInterface = new TaxInterface();
        }
        return this.taxInterface;
    }

}
