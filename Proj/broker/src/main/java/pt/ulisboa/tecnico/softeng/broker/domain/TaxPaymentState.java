package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestInvoiceData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException;

public class TaxPaymentState extends TaxPaymentState_Base {
    public static final int MAX_REMOTE_ERRORS = 3;

    @Override
    public State getValue() {
        return State.TAX_PAYMENT;
    }

    @Override
    public void process() {
        TaxInterface taxInterface = getAdventure().getBroker().getTaxInterface();
        try {
            RestInvoiceData invoiceData = new RestInvoiceData(getAdventure().getBroker().getNif(),
                    getAdventure().getClient().getNif(), "ADVENTURE", getAdventure().getAmount(),
                    getAdventure().getBegin(), getAdventure().getTime());
            getAdventure().setInvoiceReference(taxInterface.submitInvoice(invoiceData));
        } catch (TaxException be) {
            getAdventure().setState(State.UNDO);
            return;
        } catch (RemoteAccessException rae) {
            incNumOfRemoteErrors();
            if (getNumOfRemoteErrors() == MAX_REMOTE_ERRORS) {
                getAdventure().setState(State.UNDO);
            }
            return;
        }

        getAdventure().setState(State.CONFIRMED);
    }

}
