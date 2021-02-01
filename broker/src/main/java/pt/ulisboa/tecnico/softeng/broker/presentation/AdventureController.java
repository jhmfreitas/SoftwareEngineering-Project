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
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.AdventureData;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.BrokerData;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.ClientData;

@Controller
@RequestMapping(value = "/brokers/{brokerCode}/clients/{clientNif}/adventures")
public class AdventureController {
    private static final Logger logger = LoggerFactory.getLogger(AdventureController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String showAdventures(Model model, @PathVariable String brokerCode, @PathVariable String clientNif) {
        logger.info("showAdventures brokerCode:{}, clientNif:{}", brokerCode, clientNif);

        ClientData clientData = BrokerInterface.getClientDataByBrokerCodeAndNif(brokerCode, clientNif);

        if (clientData == null) {
            model.addAttribute("error", "Error: it does not exist a client with the nif " + clientNif
                    + " in the broker with the code " + brokerCode);
            model.addAttribute("broker", new BrokerData());
            model.addAttribute("brokers", BrokerInterface.getBrokers());
            return "brokers";
        }

        model.addAttribute("adventure", new AdventureData());
        model.addAttribute("client", clientData);
        return "adventures";

    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitAdventure(Model model, @PathVariable String brokerCode, @PathVariable String clientNif,
                                  @ModelAttribute AdventureData adventureData) {
        logger.info("adventureSubmit brokerCode:{}, clientNif:{}, begin:{}, end:{},margin:{}, room:{} vehicle:{}",
                brokerCode, clientNif, adventureData.getBegin(), adventureData.getEnd(), adventureData.getMargin(),
                adventureData.getBookRoom() != null ? adventureData.getBookRoom().name() : "null",
                adventureData.getRentVehicle() != null ? adventureData.getRentVehicle().name() : "null");

        try {
            BrokerInterface.createAdventure(brokerCode, clientNif, adventureData);
        } catch (BrokerException be) {
            model.addAttribute("error", "Error: it was not possible to create the adventure");
            model.addAttribute("adventure", adventureData);
            model.addAttribute("client", BrokerInterface.getClientDataByBrokerCodeAndNif(brokerCode, clientNif));
            return "adventures";
        }

        return "redirect:/brokers/" + brokerCode + "/clients/" + clientNif + "/adventures";
    }

    @RequestMapping(value = "/{id}/process", method = RequestMethod.POST)
    public String processAdventure(Model model, @PathVariable String brokerCode, @PathVariable String clientNif,
                                   @PathVariable String id) {
        logger.info("processAdventure brokerCode:{}, adventureId:{}", brokerCode, id);

        BrokerInterface.processAdventure(brokerCode, id);
        return "redirect:/brokers/" + brokerCode + "/clients/" + clientNif + "/adventures";
    }

}
