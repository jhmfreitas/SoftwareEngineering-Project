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

@Controller
@RequestMapping(value = "/brokers/{brokerCode}/bulks")
public class BulkController {
	private static Logger logger = LoggerFactory.getLogger(AdventureController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String showBulks(Model model, @PathVariable String brokerCode) {
		logger.info("showBulks code:{}", brokerCode);

		BrokerData brokerData = BrokerInterface.getBrokerDataByCode(brokerCode, CopyDepth.BULKS);

		if (brokerData == null) {
			model.addAttribute("error", "Error: it does not exist a broker with the code " + brokerCode);
			model.addAttribute("broker", new BrokerData());
			model.addAttribute("brokers", BrokerInterface.getBrokers());
			return "brokers";
		} else {
			model.addAttribute("bulk", new BulkData());
			model.addAttribute("broker", brokerData);
			return "bulks";
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public String submitBulk(Model model, @PathVariable String brokerCode, @ModelAttribute BulkData bulkData) {
		logger.info("submitBulk brokerCode:{}, number:{}, arrival:{}, departure:{}", brokerCode,
				bulkData.getNumber(), bulkData.getArrival(), bulkData.getDeparture());

		try {
			BrokerInterface.createBulkRoomBooking(brokerCode, bulkData);
		} catch (BrokerException be) {
			model.addAttribute("error", "Error: it was not possible to create the bulk room booking");
			model.addAttribute("bulk", bulkData);
			model.addAttribute("broker", BrokerInterface.getBrokerDataByCode(brokerCode, CopyDepth.BULKS));
			return "bulks";
		}

		return "redirect:/brokers/" + brokerCode + "/bulks";
	}

	@RequestMapping(value = "/{bulkId}/process", method = RequestMethod.POST)
	public String processBulk(Model model, @PathVariable String brokerCode, @PathVariable String bulkId) {
		logger.info("processBulk brokerCode:{}, bulkId:{}, ", brokerCode, bulkId);

		BrokerInterface.processBulk(brokerCode, bulkId);

		return "redirect:/brokers/" + brokerCode + "/bulks";
	}




}
