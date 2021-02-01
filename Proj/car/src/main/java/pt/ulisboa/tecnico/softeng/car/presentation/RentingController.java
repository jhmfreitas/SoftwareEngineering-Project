package pt.ulisboa.tecnico.softeng.car.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pt.ulisboa.tecnico.softeng.car.exception.CarException;
import pt.ulisboa.tecnico.softeng.car.services.local.RentACarInterface;
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.RentingData;

@Controller
@RequestMapping(value = "/rentacars/rentacar/{code}/vehicles/vehicle/{plate}/rentings/{reference}")
public class RentingController {
    private static final Logger logger = LoggerFactory.getLogger(RentingController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String rentingForm(final Model model, @PathVariable final String code, @PathVariable final String plate,
                              @PathVariable final String reference) {
        logger.debug("rentingForm");

        final RentACarInterface rentACarInterface = new RentACarInterface();

        final RentingData rentingData = rentACarInterface.getRentingData(code, plate, reference);
        model.addAttribute("rentacar", rentACarInterface.getRentACarData(code));
        model.addAttribute("renting", rentingData);
        model.addAttribute("rentings", rentACarInterface.getRentings(code, plate));
        model.addAttribute("vehicle", rentACarInterface.getVehicleByPlate(code, plate));
        return "rentingView";
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public String cancelSubmit(final Model model, @PathVariable final String code, @PathVariable final String plate,
                               @PathVariable final String reference) {
        logger.debug("cancelSubmit");

        final RentACarInterface rentACarInterface = new RentACarInterface();

        try {
            final RentingData rentingData = rentACarInterface.cancelRenting(code, plate, reference);
            model.addAttribute("rentacar", rentACarInterface.getRentACarData(code));
            model.addAttribute("renting", rentingData);
            model.addAttribute("rentings", rentACarInterface.getRentings(code, plate));
            model.addAttribute("vehicle", rentACarInterface.getVehicleByPlate(code, plate));

            return "redirect:/rentacars/rentacar/" + code + "/vehicles/vehicle/" + plate + "/rentings/" + reference;

        } catch (final CarException carEx) {

            model.addAttribute("error", "Error: Cannot cancel this renting!");
            model.addAttribute("rentacar", rentACarInterface.getRentACarData(code));
            model.addAttribute("renting", rentACarInterface.getRentingData(code, plate, reference));
            model.addAttribute("rentings", rentACarInterface.getRentings(code, plate));
            model.addAttribute("vehicle", rentACarInterface.getVehicleByPlate(code, plate));

            return "rentingView";
        }
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public String checkoutSubmit(final Model model, @PathVariable final String code, @PathVariable final String plate,
                                 @PathVariable final String reference, @ModelAttribute final RentingData rentingData) {
        logger.debug("checkoutSubmit");

        final RentACarInterface rentACarInterface = new RentACarInterface();

        try {
            final RentingData rentData = rentACarInterface.checkoutRenting(code, plate, reference,
                    rentingData.getKilometers());

            model.addAttribute("rentacar", rentACarInterface.getRentACarData(code));
            model.addAttribute("renting", rentData);
            model.addAttribute("rentings", rentACarInterface.getRentings(code, plate));
            model.addAttribute("vehicle", rentACarInterface.getVehicleByPlate(code, plate));
            model.addAttribute("notification", "Checkout out done!");

            return "redirect:/rentacars/rentacar/" + code + "/vehicles/vehicle/" + plate + "/rentings/" + reference;
        } catch (final CarException carEx) {

            model.addAttribute("error", "Error: Cannot cancel this renting!");
            model.addAttribute("rentacar", rentACarInterface.getRentACarData(code));
            model.addAttribute("renting", rentACarInterface.getRentingData(code, plate, reference));
            model.addAttribute("rentings", rentACarInterface.getRentings(code, plate));
            model.addAttribute("vehicle", rentACarInterface.getVehicleByPlate(code, plate));

            return "rentingView";
        }
    }

}
