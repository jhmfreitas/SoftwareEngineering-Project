package pt.ulisboa.tecnico.softeng.hotel.services.remote;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.local.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestRoomBookingData;

@RestController
@RequestMapping(value = "/rest/hotels")
public class HotelRestController {
	private static Logger logger = LoggerFactory.getLogger(HotelRestController.class);

	private static final HotelInterface hi = new HotelInterface();

	@RequestMapping(value = "/reserve", method = RequestMethod.POST)
	public ResponseEntity<RestRoomBookingData> reserve(@RequestBody RestRoomBookingData roomBookingData) {
		logger.info("reserve type:{}, arrival:{}, departure:{}, nif:{}, iba:{}, adventureId:{}",
				roomBookingData.getBookRoom(), roomBookingData.getArrival(), roomBookingData.getDeparture(),
				roomBookingData.getBuyerNif(), roomBookingData.getBuyerIban(), roomBookingData.getAdventureId());
		try {
			return new ResponseEntity<RestRoomBookingData>(hi.reserveRoom(roomBookingData), HttpStatus.OK);
		} catch (HotelException be) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public ResponseEntity<String> cancel(@RequestParam String reference) {
		logger.info("cancel reference:{}", reference);
		try {
			return new ResponseEntity<>(hi.cancelBooking(reference), HttpStatus.OK);
		} catch (HotelException be) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/booking", method = RequestMethod.GET)
	public ResponseEntity<RestRoomBookingData> booking(@RequestParam String reference) {
		logger.info("booking reference:{}", reference);
		try {
			return new ResponseEntity<>(hi.getRoomBookingData(reference), HttpStatus.OK);
		} catch (HotelException be) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/bulk", method = RequestMethod.POST)
	public ResponseEntity<String[]> bulk(@RequestParam int number,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate arrival,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate departure, @RequestParam String nif,
			@RequestParam String iban, @RequestParam String bulkId) {
		logger.info("bulk number:{}, arrival:{}, departure:{}, nif:{}, iban:{}, bulkId:{}", number, arrival, departure,
				nif, iban, bulkId);
		try {
			return new ResponseEntity<>(hi.bulkBooking(number, arrival, departure, nif, iban, bulkId)
					.stream().toArray(size -> new String[size]), HttpStatus.OK);
		} catch (HotelException be) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
