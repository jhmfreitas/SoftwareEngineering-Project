package pt.ulisboa.tecnico.softeng.activity.services.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.local.ActivityInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData;

@RestController
@RequestMapping(value = "/rest/providers")
public class ActivityRestController {
	private static Logger logger = LoggerFactory.getLogger(ActivityRestController.class);

	private static final ActivityInterface activityInterface = new ActivityInterface();

	@RequestMapping(value = "/reserve", method = RequestMethod.POST)
	public ResponseEntity<RestActivityBookingData> reserve(@RequestBody RestActivityBookingData activityBookingData) {
		logger.info("reserveActivity begin:{}, end:{}, age:{}, nif:{}, iban:{}, adventureId:{}",
				activityBookingData.getBegin(), activityBookingData.getEnd(), activityBookingData.getAge(),
				activityBookingData.getNif(), activityBookingData.getIban(), activityBookingData.getAdventureId());
		try {
			return new ResponseEntity<RestActivityBookingData>(activityInterface.reserveActivity(activityBookingData),
					HttpStatus.OK);
		} catch (ActivityException be) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public ResponseEntity<String> cancel(@RequestParam String reference) {
		logger.info("cancel reference:{}", reference);
		try {
			return new ResponseEntity<>(activityInterface.cancelReservation(reference), HttpStatus.OK);
		} catch (ActivityException be) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/reservation", method = RequestMethod.GET)
	public ResponseEntity<RestActivityBookingData> reservation(@RequestParam String reference) {
		logger.info("reservation reference:{}", reference);
		try {
			return new ResponseEntity<>(activityInterface.getActivityReservationData(reference), HttpStatus.OK);
		} catch (ActivityException be) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
