package pt.ulisboa.tecnico.softeng.car.services.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.softeng.car.exception.CarException;
import pt.ulisboa.tecnico.softeng.car.services.local.RentACarInterface;
import pt.ulisboa.tecnico.softeng.car.services.remote.dataobjects.RestRentingData;

@RestController
@RequestMapping(value = "/rest/rentacars")
public class CarRestController {
	private static Logger logger = LoggerFactory.getLogger(CarRestController.class);

	@RequestMapping(value = "/rent", method = RequestMethod.POST)
	public ResponseEntity<RestRentingData> rent(@RequestBody RestRentingData rentingData) {
		logger.info("rent type:{}, license:{}, nif:{}, iban:{}, begin:{}, end:{}, adventureId:{}",
				rentingData.getTypeValue(), rentingData.getDrivingLicense(), rentingData.getBuyerNIF(),
				rentingData.getBuyerIBAN(), rentingData.getBegin(), rentingData.getEnd(), rentingData.getAdventureId());

		RentACarInterface rentACarInterface = new RentACarInterface();

		try {
			return new ResponseEntity<RestRentingData>(rentACarInterface.rent(rentingData.getTypeValue(),
					rentingData.getDrivingLicense(), rentingData.getBuyerNIF(), rentingData.getBuyerIBAN(),
					rentingData.getBegin(), rentingData.getEnd(), rentingData.getAdventureId()), HttpStatus.OK);

		} catch (CarException be) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public ResponseEntity<String> cancel(@RequestParam String reference) {
		logger.info("cancel reference:{}", reference);

		RentACarInterface rentACarInterface = new RentACarInterface();

		try {
			return new ResponseEntity<>(rentACarInterface.cancelRenting(reference), HttpStatus.OK);
		} catch (CarException be) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/renting", method = RequestMethod.GET)
	public ResponseEntity<RestRentingData> renting(@RequestParam String reference) {
		logger.info("renting reference:{}", reference);

		RentACarInterface rentACarInterface = new RentACarInterface();

		try {
			RestRentingData rentingData = rentACarInterface.getRentingData(reference);
			return new ResponseEntity<RestRentingData>(rentingData, HttpStatus.OK);
		} catch (CarException be) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
