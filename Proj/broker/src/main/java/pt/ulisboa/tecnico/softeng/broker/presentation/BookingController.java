package pt.ulisboa.tecnico.softeng.broker.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;
import pt.ulisboa.tecnico.softeng.broker.services.local.BrokerInterface;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.BrokerData;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.BrokerData.CopyDepth;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.BulkData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;

import java.util.ArrayList;

@Controller
@RequestMapping(value = "/brokers/{brokerCode}/bulks/{bulkId}/bookings")
public class BookingController {
	private static Logger logger = LoggerFactory.getLogger(AdventureController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String showBookings(Model model, @PathVariable String brokerCode, @PathVariable String bulkId) {
		logger.info("showBookings broker_code:{}, bulk_id:{}", brokerCode, bulkId);


		BrokerData brokerData = BrokerInterface.getBrokerDataByCode(brokerCode, CopyDepth.BULKS);

		BulkData bulkData = BrokerInterface.getBulk(brokerData,bulkId);

		ArrayList<RestRoomBookingData> bulkRestRoomBookingData = new ArrayList<>();
		HotelInterface hotelInterface = new HotelInterface();

		for (String r : bulkData.getReferences())  {
			bulkRestRoomBookingData.add(hotelInterface.getRoomBookingData(r));
		}



		model.addAttribute("bookings", bulkRestRoomBookingData);
		model.addAttribute("bulk", bulkData);
		model.addAttribute("broker", brokerData);
		return "bookings";

	}



	@RequestMapping(value = "/{reference}/cancel", method = RequestMethod.POST)
	public String cancelReservation(Model model, @PathVariable String brokerCode, @PathVariable String bulkId, @ModelAttribute BulkData bulkData, @PathVariable String reference) {
		logger.info("cancel reservation brokerCode:{}, bulkId:{}, reference:{} ", brokerCode, bulkId, reference);

		try {
			HotelInterface hotelInterface = new HotelInterface();
			hotelInterface.cancelBooking(reference);
		} catch (BrokerException be) {
			model.addAttribute("error", "Error: it was not possible to cancel the room booking");
			model.addAttribute("bulk", bulkData);
			model.addAttribute("broker", BrokerInterface.getBrokerDataByCode(brokerCode, CopyDepth.BULKS));
			return "bulks";
		}

		return "redirect:/brokers/" + brokerCode + "/bulks/" + bulkId + "/bookings";
	}


}
