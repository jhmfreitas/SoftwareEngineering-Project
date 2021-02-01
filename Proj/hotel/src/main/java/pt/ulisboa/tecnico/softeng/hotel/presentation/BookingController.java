package pt.ulisboa.tecnico.softeng.hotel.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.local.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.HotelData;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.RoomBookingData;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.RoomData;

@Controller
@RequestMapping(value = "/hotels/{code}/rooms/{number}/bookings")
public class BookingController {
	private static Logger logger = LoggerFactory.getLogger(BookingController.class);

	private static final HotelInterface hotelInterface = new HotelInterface();

	@RequestMapping(method = RequestMethod.GET)
	public String bookingForm(Model model, @PathVariable String code, @PathVariable String number) {
		logger.info("bookingForm hotelCode:{}, roomNumber", code, number);

		RoomData roomData = hotelInterface.getRoomDataByNumber(code, number);

		if (roomData == null) {
			model.addAttribute("error",
					"Error: it does not exist a room with number " + number + " in hotel with code " + code);
			model.addAttribute("hotel", new HotelData());
			model.addAttribute("hotels", hotelInterface.getHotels());
			return "hotels";
		} else {
			model.addAttribute("booking", new RoomBookingData());
			model.addAttribute("room", roomData);
			return "bookings";
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public String bookingSubmit(Model model, @PathVariable String code, @PathVariable String number,
			@ModelAttribute RoomBookingData booking) {
		logger.info("bookingSubmit hotelCode:{}, roomNumber:{}, arrival:{}, departure:{}, nif:{}, iban:{}", code,
				number, booking.getArrival(), booking.getDeparture(), booking.getBuyerNif(), booking.getBuyerIban());

		try {

			hotelInterface.createBooking(code, number, booking);
		} catch (HotelException be) {
			model.addAttribute("error", "Error: it was not possible to book the room");
			model.addAttribute("booking", booking);
			model.addAttribute("room", hotelInterface.getRoomDataByNumber(code, number));
			return "bookings";
		}

		return "redirect:/hotels/" + code + "/rooms/" + number + "/bookings";
	}

	@RequestMapping(value = "/{reference}/process", method = RequestMethod.POST)
	public String cancelBooking(Model model, @PathVariable String code, @PathVariable String number,
			@ModelAttribute RoomBookingData booking,@PathVariable String reference) {
		logger.info("cancelBooking hotelCode:{}, roomNumber:{}, arrival:{}, departure:{}, nif:{}, iban:{}", code,
				number, booking.getArrival(), booking.getDeparture(), booking.getBuyerNif(), booking.getBuyerIban());

		hotelInterface.cancelBooking(reference);

		return "redirect:/hotels/" + code + "/rooms/" + number + "/bookings";
	}
}
