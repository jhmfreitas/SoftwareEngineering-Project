package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

public class Booking extends Booking_Base {
    private static final String SPORT_TYPE = "SPORT";

    public Booking(ActivityProvider provider, ActivityOffer offer, int age, String buyerNif, String buyerIban) {
        checkArguments(provider, offer, age, buyerNif, buyerIban);

        setReference(offer.getActivity().getActivityProvider().getCode() + Integer.toString(provider.getCounter()));
        setActivityOffer(offer);
        setProviderNif(provider.getNif());
        setBuyerNif(buyerNif);
        setProviderIban(provider.getIban());
        setBuyerIban(buyerIban);
        setAge(age);
        setAmount(offer.getPrice());
        setDate(offer.getBegin());
        setType(SPORT_TYPE);
        setCancelledInvoice(false);
        setTime(DateTime.now());

        offer.addBooking(this);
    }

    public Booking(ActivityProvider provider, ActivityOffer offer, String buyerNif, String buyerIban) {
        this(provider, offer, 0, buyerNif, buyerIban);
    }

    private void checkArguments(ActivityProvider provider, ActivityOffer offer, int age, String buyerNIF,
                                String buyerIban) {
        if (provider == null || offer == null || age < 0 || buyerNIF == null || buyerNIF.trim().length() == 0
                || buyerIban == null || buyerIban.trim().length() == 0) {
            throw new ActivityException();
        }

        if (offer.getCapacity() == offer.getNumberActiveOfBookings()) {
            throw new ActivityException();
        }
    }

    void delete() {
        setActivityOffer(null);

        setProcessor(null);

        deleteDomainObject();
    }

    public String cancel() {
        setCancel("CANCEL" + getReference());
        setCancellationDate(new LocalDate());

        getActivityOffer().getActivity().getActivityProvider().getProcessor().submitBooking(this);

        return getCancel();
    }

    public boolean isCancelled() {
        return getCancel() != null;
    }

}
