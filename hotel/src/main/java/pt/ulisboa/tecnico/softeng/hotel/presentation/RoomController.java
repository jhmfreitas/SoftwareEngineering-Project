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
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.RoomData;

@Controller
@RequestMapping(value = "/hotels/{code}/rooms")
public class RoomController {
	private static Logger logger = LoggerFactory.getLogger(RoomController.class);

	private static final HotelInterface hi = new HotelInterface();

	@RequestMapping(method = RequestMethod.GET)
	public String roomForm(Model model, @PathVariable String code) {
		logger.info("roomForm hotelCode:{}", code);

		HotelData hotelData = hi.getHotelDataByCode(code);

		if (hotelData == null) {
			model.addAttribute("error", "Error: it does not exist a hotel with the code " + code);
			model.addAttribute("hotel", new HotelData());
			model.addAttribute("hotels", hi.getHotels());
			return "hotels";
		} else {
			model.addAttribute("room", new RoomData());
			model.addAttribute("hotel", hotelData);
			return "rooms";
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public String roomSubmit(Model model, @PathVariable String code, @ModelAttribute RoomData roomData) {
		logger.info("roomSubmit hotelCode:{}, number:{}, type:{}", code, roomData.getNumber(), roomData.getType());

		try {
			hi.createRoom(code, roomData);
		} catch (HotelException be) {
			model.addAttribute("error", "Error: it was not possible to create the room");
			model.addAttribute("room", roomData);
			model.addAttribute("hotel", hi.getHotelDataByCode(code));
			return "rooms";
		}

		return "redirect:/hotels/" + code + "/rooms";
	}
}
