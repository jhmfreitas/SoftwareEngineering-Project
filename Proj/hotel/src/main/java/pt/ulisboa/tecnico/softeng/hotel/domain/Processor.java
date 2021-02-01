package pt.ulisboa.tecnico.softeng.hotel.domain;

import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestInvoiceData;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.BankException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.TaxException;

import java.util.HashSet;
import java.util.Set;

public class Processor extends Processor_Base {
    private static final String TRANSACTION_SOURCE = "HOTEL";

    private BankInterface bankInterface;
    private TaxInterface taxInterface;

    public Processor(BankInterface bankInterface, TaxInterface taxInterface) {
        this.bankInterface = bankInterface;
        this.taxInterface = taxInterface;
    }

    public void delete() {
        setHotel(null);

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
                        booking.setPaymentReference(
                                getBankInterface().processPayment(new RestBankOperationData(booking.getBuyerIban(), booking.getProviderIban(),
                                        booking.getPrice(), TRANSACTION_SOURCE, booking.getReference())));
                    } catch (BankException | RemoteAccessException ex) {
                        failedToProcess.add(booking);
                        continue;
                    }
                }
                RestInvoiceData invoiceData = new RestInvoiceData(booking.getProviderNif(), booking.getBuyerNif(),
                        Booking.getType(), booking.getPrice(), booking.getArrival(), booking.getTime());
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
                    if (!booking.getCancelledInvoice()) {
                        getTaxInterface().cancelInvoice(booking.getInvoiceReference());
                        booking.setCancelledInvoice(true);
                    }
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