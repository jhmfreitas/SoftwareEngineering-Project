package pt.ulisboa.tecnico.softeng.tax.domain;

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class ItemType extends ItemType_Base {

	public ItemType(IRS irs, String name, int tax) {
		checkArguments(irs, name, tax);
		setName(name);
		setTax(tax);

		irs.addItemType(this);
	}

	public void delete() {
		setIrs(null);

		for (Invoice invoice : getInvoiceSet()) {
			invoice.delete();
		}

		deleteDomainObject();
	}

	private void checkArguments(IRS irs, String name, int tax) {
		if (name == null || name.isEmpty()) {
			throw new TaxException();
		}

		if (irs.getItemTypeByName(name) != null) {
			throw new TaxException();
		}

		if (tax < 0) {
			throw new TaxException();
		}
	}
}
