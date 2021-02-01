package pt.ulisboa.tecnico.softeng.broker.services.remote;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

public class CarInterface {
    private static final Logger logger = LoggerFactory.getLogger(CarInterface.class);

    private static final String ENDPOINT = "http://localhost:8084";

    public RestRentingData rentCar(Adventure.RentVehicle vehicleType, String drivingLicense, String nif, String iban,
                                   LocalDate begin, LocalDate end, String adventureId) {
        logger.info("rentCar vehicleType:{}, drivingLicense:{}, nif:{}, iban:{}, begin:{}, end:{}, adventureId:{}",
                vehicleType, drivingLicense, nif, iban, begin, end, adventureId);
        RestTemplate restTemplate = new RestTemplate();
        try {
            RestRentingData rentingData = new RestRentingData(vehicleType.toString(), drivingLicense, nif, iban, begin,
                    end, adventureId);
            return restTemplate.postForObject(ENDPOINT + "/rest/rentacars/rent", rentingData,
                    RestRentingData.class);
        } catch (HttpClientErrorException e) {
            logger.info(
                    "rentCar HttpClientErrorException vehicleType:{}, drivingLicense:{}, nif:{}, iban:{}, begin:{}, end:{}, adventureId:{}",
                    vehicleType, drivingLicense, nif, iban, begin, end, adventureId);
            throw new CarException();
        } catch (Exception e) {
            logger.info(
                    "rentCar Exception vehicleType:{}, drivingLicense:{}, nif:{}, iban:{}, begin:{}, end:{}, adventureId:{}",
                    vehicleType, drivingLicense, nif, iban, begin, end, adventureId);
            throw new RemoteAccessException();
        }
    }

    public String cancelRenting(String rentingReference) {
        logger.info("cancelRenting rentingReference:{}", rentingReference);
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.postForObject(
                    ENDPOINT + "/rest/rentacars/cancel?reference=" + rentingReference, null, String.class);
        } catch (HttpClientErrorException e) {
            logger.info("cancelRenting HttpClientErrorException rentingReference:{}", rentingReference);
            throw new CarException();
        } catch (Exception e) {
            logger.info("cancelRenting Exception rentingReference:{}", rentingReference);
            throw new RemoteAccessException();
        }
    }

    public RestRentingData getRentingData(String reference) {
        logger.info("getRentingData reference:{}", reference);
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate
                    .getForObject(ENDPOINT + "/rest/rentacars/renting?reference=" + reference, RestRentingData.class);
        } catch (HttpClientErrorException e) {
            logger.info("getRentingData HttpClientErrorException:{}", reference);
            throw new CarException();
        } catch (Exception e) {
            logger.info("getRentingData Exception:{}", reference);
            throw new RemoteAccessException();
        }

    }

}
