package pt.ulisboa.tecnico.softeng.activity.domain;

import pt.ist.fenixframework.FenixFramework
import pt.ist.fenixframework.core.WriteOnReadError
import spock.lang.Specification

import javax.transaction.NotSupportedException
import javax.transaction.SystemException


abstract class SpockRollbackTestAbstractClass extends Specification {

	def setup() throws Exception {
		try {
			FenixFramework.getTransactionManager().begin(false)
			populate4Test()
		} catch (WriteOnReadError | NotSupportedException | SystemException e1) {
			e1.printStackTrace()
		}
	}

	def cleanup() {
		try {
			FenixFramework.getTransactionManager().rollback()
		} catch (IllegalStateException | SecurityException | SystemException e) {
			e.printStackTrace()
		}
	}

	abstract def populate4Test()

}
