package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.broker.services.remote.*

class BrokerPersistenceSpockTest extends SpockPersistenceTestAbstractClass implements SharedDefinitions {

    @Override
    def whenCreateInDatabase() {
        def broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF, BROKER_IBAN,
                new ActivityInterface(), new HotelInterface(), new CarInterface(), new BankInterface(), new TaxInterface())
        def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        new Adventure(broker, this.BEGIN, this.END, client, MARGIN, Adventure.BookRoom.DOUBLE, Adventure.RentVehicle.CAR)

        def bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, this.BEGIN, this.END)

        new Reference(bulk, REF_ONE)
    }

    @Override
    def thenAssert() {
        FenixFramework.getDomainRoot().getBrokerSet().size() == 1

        def brokers = new ArrayList<>(FenixFramework.getDomainRoot().getBrokerSet())
        def broker = brokers.get(0)

        assert broker.getCode().equals(BROKER_CODE)
        assert broker.getName().equals(BROKER_NAME)
        assert broker.getAdventureSet().size() == 1
        assert broker.getRoomBulkBookingSet().size() == 1
        assert broker.getNif().equals(BROKER_NIF)
        assert broker.getIban().equals(BROKER_IBAN)

        def adventures = new ArrayList<>(broker.getAdventureSet())
        def adventure = adventures.get(0)

        assert adventure.getID() != null
        assert adventure.getBroker() == broker
        assert adventure.getBegin() == BEGIN
        assert adventure.getEnd() == END
        assert adventure.getAge() == AGE
        assert adventure.getIban().equals(CLIENT_IBAN)
        assert adventure.getTime() != null
        assert adventure.getMargin() == MARGIN
        assert adventure.getCurrentAmount() == 0.0
        assert adventure.getClient().getAdventureSet().size() == 1
        assert adventure.getBookRoom() == Adventure.BookRoom.DOUBLE
        assert adventure.getRentVehicle() == Adventure.RentVehicle.CAR
        assert adventure.getPaymentConfirmation() == null
        assert adventure.getPaymentCancellation() == null
        assert adventure.getRentingConfirmation() == null
        assert adventure.getRentingCancellation() == null
        assert adventure.getActivityConfirmation() == null
        assert adventure.getActivityCancellation() == null
        assert adventure.getRentingConfirmation() == null
        assert adventure.getRentingCancellation() == null
        assert adventure.getInvoiceReference() == null
        assert !adventure.getInvoiceCancelled()

        assert adventure.getState().getValue() == Adventure.State.RESERVE_ACTIVITY
        assert adventure.getState().getNumOfRemoteErrors() == 0

        def bulks = new ArrayList<>(broker.getRoomBulkBookingSet())
        def bulk = bulks.get(0)

        assert bulk != null
        assert bulk.getArrival() == BEGIN
        assert bulk.getDeparture() == END
        assert bulk.getNumber() == NUMBER_OF_BULK
        assert !bulk.getCancelled()
        assert bulk.getNumberOfHotelExceptions() == 0
        assert bulk.getNumberOfRemoteErrors() == 0
        assert bulk.getReferenceSet().size() == 1

        def references = new ArrayList<>(bulk.getReferenceSet())
        def reference = references.get(0)
        assert reference.getValue().equals(REF_ONE)

        def client = adventure.getClient()
        assert client.getIban().equals(CLIENT_IBAN)
        assert client.getNif().equals(CLIENT_NIF)
        assert client.getAge() == AGE
        assert client.getDrivingLicense().equals(DRIVING_LICENSE)
    }

    @Override
    def deleteFromDatabase() {
        for (def broker : FenixFramework.getDomainRoot().getBrokerSet()) {
            broker.delete()
        }
    }
}
