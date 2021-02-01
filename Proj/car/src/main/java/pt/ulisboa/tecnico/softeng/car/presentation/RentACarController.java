package pt.ulisboa.tecnico.softeng.car.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.car.exception.CarException;
import pt.ulisboa.tecnico.softeng.car.services.local.RentACarInterface;
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.RentACarData;

@Controller
@RequestMapping(value = "/rentacars")
public class RentACarController {
	private static Logger logger = LoggerFactory.getLogger(RentACarController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String rentacarForm(Model model) {
		logger.info("rentacar");

		RentACarInterface rentACarInterface = new RentACarInterface();

		model.addAttribute("rentacar", new RentACarData());
		model.addAttribute("rentacars", rentACarInterface.getRentACars());
		return "rentacarsView";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String rentacarSubmit(Model model, @ModelAttribute RentACarData rentacarData) {
		logger.info("rentacarSubmit name:{}, nif:{}, iban:{}", rentacarData.getName(), rentacarData.getNif(),
				rentacarData.getIban());

		RentACarInterface rentACarInterface = new RentACarInterface();

		try {
			rentACarInterface.createRentACar(rentacarData);
		} catch (CarException be) {
			model.addAttribute("error", "Error: it was not possible to create the Rent-A-Car");
			model.addAttribute("rentacar", rentacarData);
			model.addAttribute("rentacars", rentACarInterface.getRentACars());
			return "rentacarsView";
		}

		return "redirect:/rentacars";
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public String deleteRentACars(Model model) {
		logger.info("deleteRentACars");

		RentACarInterface rentACarInterface = new RentACarInterface();

		rentACarInterface.deleteRentACars();

		return "redirect:/rentacars";
	}
}
