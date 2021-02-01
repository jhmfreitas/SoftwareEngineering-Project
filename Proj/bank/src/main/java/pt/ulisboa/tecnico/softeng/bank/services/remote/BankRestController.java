package pt.ulisboa.tecnico.softeng.bank.services.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.bank.services.local.BankInterface;
import pt.ulisboa.tecnico.softeng.bank.services.remote.dataobjects.RestBankOperationData;

@RestController
@RequestMapping(value = "/rest/banks")
public class BankRestController {
    private static final Logger logger = LoggerFactory.getLogger(BankRestController.class);

    @RequestMapping(value = "/accounts/{iban}/processPayment", method = RequestMethod.POST)
    public ResponseEntity<String> processPayment(@RequestBody RestBankOperationData bankOperationData) {
        logger.info("processPayment sourceIban:{}, targetIban:{}, amount:{}, transactionSource:{}, transactionReference:{}",
                bankOperationData.getSourceIban(), bankOperationData.getTargetIban(), bankOperationData.getValue(), bankOperationData.getTransactionSource(),
                bankOperationData.getTransactionReference());
        try {
            return new ResponseEntity<>(BankInterface.processPayment(bankOperationData), HttpStatus.OK);
        } catch (BankException be) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public ResponseEntity<String> cancelPayment(@RequestParam String reference) {
        logger.info("cancelPayment reference:{}", reference);
        try {
            return new ResponseEntity<>(BankInterface.cancelPayment(reference), HttpStatus.OK);
        } catch (BankException be) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/operation", method = RequestMethod.GET)
    public ResponseEntity<RestBankOperationData> getOperationData(@RequestParam String reference) {
        logger.info("getOperationData reference:{}", reference);
        try {
            RestBankOperationData result = BankInterface.getOperationData(reference);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (BankException be) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
