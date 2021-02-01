package pt.ulisboa.tecnico.softeng.broker.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;
import pt.ulisboa.tecnico.softeng.broker.services.local.BrokerInterface;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.BrokerData;

@Controller
@RequestMapping(value = "/brokers")
public class BrokerController {
    private static final Logger logger = LoggerFactory.getLogger(BrokerController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String brokerForm(Model model) {
        logger.info("brokerForm");
        model.addAttribute("broker", new BrokerData());
        model.addAttribute("brokers", BrokerInterface.getBrokers());
        return "brokers";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String brokerSubmit(Model model, @ModelAttribute BrokerData brokerData) {
        logger.info("brokerSubmit name:{}, code:{}, nif:{}, iban:{}", brokerData.getName(),
                brokerData.getCode(), brokerData.getNif(), brokerData.getIban());
        try {
            BrokerInterface.createBroker(brokerData);
        } catch (BrokerException be) {
            model.addAttribute("error", "Error: it was not possible to create the broker");
            model.addAttribute("broker", brokerData);
            model.addAttribute("brokers", BrokerInterface.getBrokers());
            return "brokers";
        }

        return "redirect:/brokers";
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public String deleteBrokers(Model model) {
        logger.info("deleteBrokers");

        BrokerInterface.deleteBrokers();

        return "redirect:/brokers";
    }
}
