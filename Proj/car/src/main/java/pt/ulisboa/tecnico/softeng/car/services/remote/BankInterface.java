package pt.ulisboa.tecnico.softeng.car.services.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.softeng.car.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.BankException;
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.RemoteAccessException;

public class BankInterface {
    private final Logger logger = LoggerFactory.getLogger(BankInterface.class);

    private final String ENDPOINT = "http://localhost:8082";

    public String processPayment(RestBankOperationData bankOperationData) {
        this.logger.info("processPayment sourceIban:{}, targetIban:{}, amount:{}, transactionSource:{}, transactionReference:{}",
                bankOperationData.getSourceIban(), bankOperationData.getTargetIban(), bankOperationData.getValue(), bankOperationData.getTransactionSource(),
                bankOperationData.getTransactionReference());

        RestTemplate restTemplate = new RestTemplate();
        try {
            String result = restTemplate.postForObject(
                    this.ENDPOINT + "/rest/banks/accounts/" + bankOperationData.getSourceIban() + "/processPayment",
                    bankOperationData, String.class);
            return result;
        } catch (HttpClientErrorException e) {
            this.logger.info(
                    "processPayment HttpClientErrorException  sourceIban:{}, targetIban:{}, amount:{}, transactionSource:{}, transactionReference:{}",
                    bankOperationData.getSourceIban(), bankOperationData.getTargetIban(), bankOperationData.getValue(), bankOperationData.getTransactionSource(),
                    bankOperationData.getTransactionReference());
            throw new BankException();
        } catch (Exception e) {
            this.logger.info(
                    "processPayment Exception sourceIban:{}, targetIban:{}, amount:{}, transactionSource:{}, transactionReference:{}",
                    bankOperationData.getSourceIban(), bankOperationData.getTargetIban(), bankOperationData.getTransactionSource(),
                    bankOperationData.getTransactionReference());
            throw new RemoteAccessException();
        }
    }

    public String cancelPayment(String reference) {
        this.logger.info("cancelPayment reference:{}", reference);

        RestTemplate restTemplate = new RestTemplate();
        try {
            String result = restTemplate.postForObject(this.ENDPOINT + "/rest/banks/cancel?reference=" + reference,
                    null, String.class);
            return result;
        } catch (HttpClientErrorException e) {
            this.logger.info("cancelPayment HttpClientErrorException reference:{}", reference);
            throw new BankException();
        } catch (Exception e) {
            this.logger.info("cancelPayment Exception reference:{}", reference);
            throw new RemoteAccessException();
        }
    }

}
