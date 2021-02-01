package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface

class ActivityPersistenceSpockTest extends SpockPersistenceTestAbstractClass {
    def ADVENTURE_ID = 'AdventureId'
    def ACTIVITY_NAME = 'Activity_Name'
    def PROVIDER_NAME = 'Wicket'
    def PROVIDER_CODE = 'A12345'
    def IBAN = 'IBAN'
    def NIF = 'NIF'
    def BUYER_IBAN = 'IBAN2'
    def BUYER_NIF = 'NIF2'
    def CAPACITY = 25
    def AMOUNT = 30
    def begin = new LocalDate(2017, 04, 01)
    def end = new LocalDate(2017, 04, 15)

    @Override
    def whenCreateInDatabase() {
        def processor = new Processor(new BankInterface(), new TaxInterface())
        def activityProvider = new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN, processor)

        def activity = new Activity(activityProvider, ACTIVITY_NAME, 18, 65, CAPACITY)

        def offer = new ActivityOffer(activity, this.begin, this.end, AMOUNT)
        offer.book(activityProvider, offer, 54, BUYER_NIF, BUYER_IBAN, ADVENTURE_ID)
    }

    @Override
    def thenAssert() {
        assert FenixFramework.getDomainRoot().getActivityProviderSet().size() == 1

        def providers = new ArrayList<>(FenixFramework.getDomainRoot().getActivityProviderSet())
        def provider = providers.get(0)

        verifyAll {
            PROVIDER_CODE == provider.getCode()
            PROVIDER_NAME == provider.getName()
            provider.getActivitySet().size() == 1
            provider.getNif() == NIF
            provider.getIban() == IBAN
        }

        Processor processor = provider.getProcessor()
        assert processor != null
        assert processor.getBookingSet().size() == 1

        def activities = new ArrayList<>(provider.getActivitySet())
        def activity = activities.get(0)
        verifyAll {
            ACTIVITY_NAME == activity.getName()
            activity.getCode().startsWith(PROVIDER_CODE)
            activity.getMinAge() == 18
            activity.getMaxAge() == 65
            activity.getCapacity() == CAPACITY
            activity.getActivityOfferSet().size() == 1
        }

        def offers = new ArrayList<>(activity.getActivityOfferSet())
        def offer = offers.get(0)
        verifyAll {
            offer.getBegin() == begin
            offer.getEnd() == end
            offer.getCapacity() == CAPACITY
            offer.getBookingSet().size() == 1
            offer.getPrice() == AMOUNT
        }

        def bookings = new ArrayList<>(offer.getBookingSet())
        def booking = bookings.get(0)
        verifyAll {
            booking.getReference() != null
            booking.getCancel() == null
            booking.getCancellationDate() == null
            booking.getPaymentReference() == null
            booking.getInvoiceReference() == null
            !booking.getCancelledInvoice()
            booking.getCancelledPaymentReference() == null
            booking.getType() == 'SPORT'
            booking.getBuyerNif() == BUYER_NIF
            booking.getBuyerIban() == BUYER_IBAN
            booking.getProviderNif() == NIF
            booking.getProviderIban() == IBAN
            booking.getAmount() == AMOUNT
            booking.getAdventureId() == ADVENTURE_ID
            booking.getDate() == begin
            booking.getTime() != null
            booking.getProcessor() != null
        }
    }

    @Override
    def deleteFromDatabase() {
        for (def activityProvider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
            activityProvider.delete()
        }
    }
}
