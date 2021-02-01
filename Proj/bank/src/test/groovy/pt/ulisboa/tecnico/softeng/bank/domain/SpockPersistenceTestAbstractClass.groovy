package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ist.fenixframework.FenixFramework
import pt.ist.fenixframework.core.WriteOnReadError
import spock.lang.Specification

import javax.transaction.NotSupportedException
import javax.transaction.SystemException

abstract class SpockPersistenceTestAbstractClass extends Specification {

	def 'test persistence'() {
		when:
		atomicProcess()

		then:
		atomicAssert()
	}

	def atomicProcess() throws Exception {
		try {
			FenixFramework.getTransactionManager().begin(false)
		} catch (WriteOnReadError | NotSupportedException | SystemException e1) {
			e1.printStackTrace()
		}
		try {
			whenCreateInDatabase()
		} catch (Exception e) {
			FenixFramework.getTransactionManager().rollback()
		}


		try {
			FenixFramework.getTransactionManager().commit()
		} catch (IllegalStateException | SecurityException | SystemException e) {
			e.printStackTrace()
		}
	}

	def atomicAssert() throws Exception {
		try {
			FenixFramework.getTransactionManager().begin(true)
		} catch (WriteOnReadError | NotSupportedException | SystemException e1) {
			e1.printStackTrace()
		}

		thenAssert()

		try {
			FenixFramework.getTransactionManager().commit()
		} catch (IllegalStateException | SecurityException | SystemException e) {
			e.printStackTrace()
		}

		return true
	}


	def cleanup() {
		try {
			FenixFramework.getTransactionManager().begin(false)
		} catch (WriteOnReadError | NotSupportedException | SystemException e1) {
			deleteFromDatabase()
			e1.printStackTrace()
		}

		deleteFromDatabase();

		try {
			FenixFramework.getTransactionManager().commit()
		} catch (IllegalStateException | SecurityException | SystemException e) {
			e.printStackTrace()
		}
	}

	abstract def whenCreateInDatabase()
	abstract def thenAssert()
	abstract def deleteFromDatabase()
}
