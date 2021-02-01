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
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.VehicleData;
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.RentingData;

@Controller
@RequestMapping(value = "/rentacars/rentacar/{code}/vehicles")
public class VehiclesController {
    private static final Logger logger = LoggerFactory.getLogger(VehiclesController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String vehiclesForm(Model model, @PathVariable String code) {
        logger.info("vehiclesForm");

        RentACarInterface rentACarInterface = new RentACarInterface();

        model.addAttribute("rentacar", rentACarInterface.getRentACarData(code));
        model.addAttribute("vehicle", new VehicleData());
        model.addAttribute("vehicles", rentACarInterface.getVehicles(code));
        model.addAttribute("renting", new RentingData());
        model.addAttribute("pendingRentings",rentACarInterface.getPendingRentings(code));
        model.addAttribute("pendingNumber",rentACarInterface.getPendingRentings(code).size());
        return "vehiclesView";
    }

    @RequestMapping(value = "/vehicle", method = RequestMethod.POST)
    public String vehicleSubmit(Model model, @PathVariable String code, @ModelAttribute VehicleData vehicleData) {
        logger.info("vehicleSubmit plate:{}, km:{}, price:{}, type:{}", vehicleData.getPlate(),
                vehicleData.getKilometers(), vehicleData.getPrice(), vehicleData.getType());

        RentACarInterface rentACarInterface = new RentACarInterface();

        try {
            rentACarInterface.createVehicle(code, vehicleData);
        } catch (CarException be) {
            model.addAttribute("error", "Error: it was not possible to create the Rent-A-Car");
            model.addAttribute("rentacar", rentACarInterface.getRentACarData(code));
            model.addAttribute("vehicle", vehicleData);
            model.addAttribute("vehicles", rentACarInterface.getVehicles(code));
            model.addAttribute("renting", new RentingData());
            model.addAttribute("pendingRentings",rentACarInterface.getPendingRentings(code));
            model.addAttribute("pendingNumber",rentACarInterface.getPendingRentings(code).size());
            return "vehiclesView";
        }

        return "redirect:/rentacars/rentacar/" + code + "/vehicles";
    }
}
