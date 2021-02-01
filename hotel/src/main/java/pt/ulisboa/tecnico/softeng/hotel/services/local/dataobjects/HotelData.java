package pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects;

import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HotelData {
    private String code;
    private String name;
    private String nif;
    private String iban;
    private Double priceSingle;
    private Double priceDouble;
    private List<RoomData> rooms;

    public HotelData() {
    }

    public HotelData(Hotel hotel) {
        this.code = hotel.getCode();
        this.name = hotel.getName();

        this.rooms = hotel.getRoomSet().stream().sorted(Comparator.comparing(Room::getNumber))
                .map(RoomData::new).collect(Collectors.toList());
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNif() {
        return this.nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getIban() {
        return this.iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public Double getPriceSingle() {
        return this.priceSingle;
    }

    public void setPriceSingle(Double priceSingle) {
        this.priceSingle = priceSingle;
    }

    public Double getPriceDouble() {
        return this.priceDouble;
    }

    public void setPriceDouble(Double priceDouble) {
        this.priceDouble = priceDouble;
    }

    public List<RoomData> getRooms() {
        return this.rooms;
    }

    public void setRooms(List<RoomData> rooms) {
        this.rooms = rooms;
    }

    public long getPriceSingleLong() {
        return Math.round(getPriceSingle() * Hotel.SCALE);
    }

    public long getPriceDoubleLong() {
        return Math.round(getPriceDouble() * Hotel.SCALE);
    }

}
