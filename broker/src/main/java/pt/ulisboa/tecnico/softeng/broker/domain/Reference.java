package pt.ulisboa.tecnico.softeng.broker.domain;

public class Reference extends Reference_Base {
    public Reference(BulkRoomBooking bulk, String value) {
        setValue(value);

        setBulkRoomBooking(bulk);
    }

    public void delete() {
        setBulkRoomBooking(null);

        deleteDomainObject();
    }

}
