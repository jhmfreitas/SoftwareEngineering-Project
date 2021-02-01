package pt.ulisboa.tecnico.softeng.tax.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pt.ulisboa.tecnico.softeng.tax.services.local.TaxInterface;

@Controller
@RequestMapping(value = "/tax")
public class IRSController {

    @RequestMapping(method = RequestMethod.DELETE)
    public String deleteRentACars(Model model) {
        TaxInterface.deleteIRS();

        return "redirect:/";
    }
    
}
