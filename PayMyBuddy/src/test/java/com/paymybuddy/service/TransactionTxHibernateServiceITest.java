package com.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.paymybuddy.configuration.RepositoryDataSource;
import com.paymybuddy.configuration.RepositoryRessourceDatabasePopulator;
import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.factory.ServiceFactory;
import com.paymybuddy.repository.ITransactionRepository;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

/**
 * Class including integration tests for the TransactionTxHibernateService
 * Class.
 */
public class TransactionTxHibernateServiceITest {

	private static String paymybuddyPropertiesFile = "paymybuddyTest.properties";

	private static ResourceDatabasePopulator resourceDatabasePopulator;

	private static DriverManagerDataSource dataSource;

	private RepositoryTxManagerHibernate repositoryTxManager;

	private IUtilisateurRepository utilisateurRepositoryImpl;

	private ITransactionRepository transactionRepositoryImpl;

	private TransactionTxHibernateService transactionTxHibernateServiceUnderTest;

	@BeforeAll
	private static void setUpAllTest() {
		// We get a dataSource
		dataSource = RepositoryDataSource.getDataSource(paymybuddyPropertiesFile);

		// We get a resourceDatabasePopulator
		resourceDatabasePopulator = RepositoryRessourceDatabasePopulator
				.getResourceDatabasePopulator("/DataTransactionsForTests.sql");

		// We close the dataSource
		RepositoryDataSource.closeDatasource();
	}

	@BeforeEach
	private void setUpPerTest() {
		// We prepare the database
		DatabasePopulatorUtils.execute(resourceDatabasePopulator, dataSource);

		RepositoryTxManagerHibernate.resetTxManager();
		repositoryTxManager = RepositoryTxManagerHibernate.getRepositoryTxManagerHibernate(paymybuddyPropertiesFile);

		RepositoryFactory.resetUtilisateurRepository();
		utilisateurRepositoryImpl = RepositoryFactory.getUtilisateurRepository(repositoryTxManager);

		RepositoryFactory.resetTransactionRepository();
		transactionRepositoryImpl = RepositoryFactory.getTransactionRepository(repositoryTxManager);

		ServiceFactory.resetTransactionService();
		transactionTxHibernateServiceUnderTest = ServiceFactory.getTransactionService(repositoryTxManager,
				utilisateurRepositoryImpl, transactionRepositoryImpl);
	}

	@AfterEach
	private void afterPerTest() {
		repositoryTxManager.closeSessionFactory();
	}

	@Test
	public void getTransactionsWhenUtilisateurExist() {
		// ARRANGE
		repositoryTxManager.openCurrentSessionWithTx();
		Transaction transactionToGet1 = new Transaction();
		Utilisateur initiateur1 = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartie1 = utilisateurRepositoryImpl.read("def@test.com");
		transactionToGet1.setInitiateur(initiateur1);
		transactionToGet1.setContrepartie(contrepartie1);
		transactionToGet1.setMontant(1d);
		transactionToGet1 = transactionRepositoryImpl.create(transactionToGet1);

		Transaction transactionToGet2 = new Transaction();
		transactionToGet2.setInitiateur(initiateur1);
		transactionToGet2.setContrepartie(contrepartie1);
		transactionToGet2.setMontant(2d);
		transactionToGet2 = transactionRepositoryImpl.create(transactionToGet2);

		Transaction transactionToGet3 = new Transaction();
		Utilisateur contrepartie2 = utilisateurRepositoryImpl.read("ghi@test.com");
		transactionToGet3.setInitiateur(initiateur1);
		transactionToGet3.setContrepartie(contrepartie2);
		transactionToGet3.setMontant(3d);
		transactionToGet3 = transactionRepositoryImpl.create(transactionToGet3);
		repositoryTxManager.commitTxAndCloseCurrentSession();

		// ACT
		List<Transaction> transactionsGet = new ArrayList<>();
		transactionsGet = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		// ASSERT
		assertEquals(3, transactionsGet.size());

		Iterator<Transaction> iter = transactionsGet.iterator();

		Transaction transactionGet1 = iter.next();
		Transaction transactionGet2 = iter.next();
		Transaction transactionGet3 = iter.next();

		// TransactionGet3 = TransactionToGet1 as transactions are get with DESC order
		assertEquals(transactionToGet1.getInitiateur().getEmail(), transactionGet3.getInitiateur().getEmail());
		assertEquals(transactionToGet1.getInitiateur().getPassword(), transactionGet3.getInitiateur().getPassword());
		assertEquals(transactionToGet1.getInitiateur().getSolde(), transactionGet3.getInitiateur().getSolde());

		assertEquals(transactionToGet1.getContrepartie().getEmail(), transactionGet3.getContrepartie().getEmail());
		assertEquals(transactionToGet1.getContrepartie().getPassword(),
				transactionGet3.getContrepartie().getPassword());
		assertEquals(transactionToGet1.getContrepartie().getSolde(), transactionGet3.getContrepartie().getSolde());

		assertEquals(transactionToGet1.getMontant(), transactionGet3.getMontant());

		// TransactionGet2 = TransactionToGet2 as transactions are get with DESC order
		assertEquals(transactionToGet2.getInitiateur().getEmail(), transactionGet2.getInitiateur().getEmail());
		assertEquals(transactionToGet2.getInitiateur().getPassword(), transactionGet2.getInitiateur().getPassword());
		assertEquals(transactionToGet2.getInitiateur().getSolde(), transactionGet2.getInitiateur().getSolde());

		assertEquals(transactionToGet2.getContrepartie().getEmail(), transactionGet2.getContrepartie().getEmail());
		assertEquals(transactionToGet2.getContrepartie().getPassword(),
				transactionGet2.getContrepartie().getPassword());
		assertEquals(transactionToGet2.getContrepartie().getSolde(), transactionGet2.getContrepartie().getSolde());

		assertEquals(transactionToGet2.getMontant(), transactionGet2.getMontant());

		// TransactionGet1 = TransactionToGet3 as transactions are get with DESC order
		assertEquals(transactionToGet3.getInitiateur().getEmail(), transactionGet1.getInitiateur().getEmail());
		assertEquals(transactionToGet3.getInitiateur().getPassword(), transactionGet1.getInitiateur().getPassword());
		assertEquals(transactionToGet3.getInitiateur().getSolde(), transactionGet1.getInitiateur().getSolde());

		assertEquals(transactionToGet3.getContrepartie().getEmail(), transactionGet1.getContrepartie().getEmail());
		assertEquals(transactionToGet3.getContrepartie().getPassword(),
				transactionGet1.getContrepartie().getPassword());
		assertEquals(transactionToGet3.getContrepartie().getSolde(), transactionGet1.getContrepartie().getSolde());

		assertEquals(transactionToGet3.getMontant(), transactionGet1.getMontant());
	}

	@Test
	public void getTransactionsWhenUtilisateurNotExist() {
		// ARRANGE
		// ACT
		List<Transaction> transactionsGet = new ArrayList<>();
		transactionsGet = transactionTxHibernateServiceUnderTest.getTransactions("UtilisateurNotExist");

		// ASSERT
		assertTrue(transactionsGet.isEmpty());
	}

	@Test
	public void makeATransactionWhenAmountIsPositiveAndInitiateurAndContrepartieExistAndAreConnectedAndSoldeSufficient() {
		// ARRANGE
		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurBeforeNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieBeforeNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		List<Transaction> listTransactionsBeforeNewTransaction = new ArrayList<>();
		listTransactionsBeforeNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction(
				initiateurBeforeNewTransaction.getEmail(), contrepartieBeforeNewTransaction.getEmail(), 10d, "Transaction test");

		// ASSERT
		assertTrue(result);

		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurAfterNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieAfterNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		assertEquals((double) (initiateurBeforeNewTransaction.getSolde() - 10),
				(double) (initiateurAfterNewTransaction.getSolde()));
		// connection get the amount of the transaction minus the commission of 0.5%
		assertEquals((double) (contrepartieBeforeNewTransaction.getSolde() + 10 * (1 - 0.005)),
				(double) (contrepartieAfterNewTransaction.getSolde()));

		List<Transaction> listTransactionsAfterNewTransaction = new ArrayList<>();
		listTransactionsAfterNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		assertEquals(listTransactionsBeforeNewTransaction.size() + 1, listTransactionsAfterNewTransaction.size());

		Transaction newTransaction = listTransactionsAfterNewTransaction.iterator().next();

		assertEquals(10d, newTransaction.getMontant());
		assertEquals(initiateurBeforeNewTransaction.getEmail(), newTransaction.getInitiateur().getEmail());
		assertEquals(contrepartieBeforeNewTransaction.getEmail(), newTransaction.getContrepartie().getEmail());
	}

	@Test
	public void makeATransactionWhenAmountIsNegative() {
		// ARRANGE
		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurBeforeNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieBeforeNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		List<Transaction> listTransactionsBeforeNewTransaction = new ArrayList<>();
		listTransactionsBeforeNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction(
				initiateurBeforeNewTransaction.getEmail(), contrepartieBeforeNewTransaction.getEmail(), -10d, "Transaction test");

		// ASSERT
		assertFalse(result);

		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurAfterNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieAfterNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		assertEquals((double) (initiateurBeforeNewTransaction.getSolde()),
				(double) (initiateurAfterNewTransaction.getSolde()));
		assertEquals((double) (contrepartieBeforeNewTransaction.getSolde()),
				(double) (contrepartieAfterNewTransaction.getSolde()));

		List<Transaction> listTransactionsAfterNewTransaction = new ArrayList<>();
		listTransactionsAfterNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		assertEquals(listTransactionsBeforeNewTransaction.size(), listTransactionsAfterNewTransaction.size());
	}

	@Test
	public void makeATransactionWhenInitiateurNotExist() {
		// ARRANGE
		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur contrepartieBeforeNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction("UtilisateurInitiateurNotExist",
				contrepartieBeforeNewTransaction.getEmail(), 10d, "Transaction test");

		// ASSERT
		assertFalse(result);

		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur contrepartieAfterNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		assertEquals(contrepartieBeforeNewTransaction.getSolde(), contrepartieAfterNewTransaction.getSolde());
	}

	@Test
	public void makeATransactionWhenInitiateurExistContrepartieNotExist() {
		// ARRANGE
		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurBeforeNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		List<Transaction> listTransactionsBeforeNewTransaction = new ArrayList<>();
		listTransactionsBeforeNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest
				.makeATransaction(initiateurBeforeNewTransaction.getEmail(), "UtilisateurContrepartieNotExist", 10d, "Transaction test");

		// ASSERT
		assertFalse(result);

		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurAfterNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		assertEquals(initiateurBeforeNewTransaction.getSolde(), initiateurAfterNewTransaction.getSolde());

		List<Transaction> listTransactionsAfterNewTransaction = new ArrayList<>();
		listTransactionsAfterNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		assertEquals(listTransactionsBeforeNewTransaction.size(), listTransactionsAfterNewTransaction.size());
	}

	@Test
	public void makeATransactionWhenInitiateurAndContrepartieExistAndAreNotConnected() {
		// ARRANGE
		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurBeforeNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieBeforeNewTransaction = utilisateurRepositoryImpl.read("klm@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		List<Transaction> listTransactionsBeforeNewTransaction = new ArrayList<>();
		listTransactionsBeforeNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction(
				initiateurBeforeNewTransaction.getEmail(), contrepartieBeforeNewTransaction.getEmail(), 10d, "Transaction test");

		// ASSERT
		assertFalse(result);

		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurAfterNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieAfterNewTransaction = utilisateurRepositoryImpl.read("klm@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		assertEquals(initiateurBeforeNewTransaction.getSolde(), initiateurAfterNewTransaction.getSolde());
		assertEquals(contrepartieBeforeNewTransaction.getSolde(), contrepartieAfterNewTransaction.getSolde());

		List<Transaction> listTransactionsAfterNewTransaction = new ArrayList<>();
		listTransactionsAfterNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		assertEquals(listTransactionsBeforeNewTransaction.size(), listTransactionsAfterNewTransaction.size());
	}

	@Test
	public void makeATransactionWhenInitiateurAndContrepartieExistAndAreConnectedAndSoldeNotSufficient() {
		// ARRANGE
		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurBeforeNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieBeforeNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		List<Transaction> listTransactionsBeforeNewTransaction = new ArrayList<>();
		listTransactionsBeforeNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction(
				initiateurBeforeNewTransaction.getEmail(), contrepartieBeforeNewTransaction.getEmail(), 1000d, "Transaction test");

		// ASSERT
		assertFalse(result);

		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurAfterNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieAfterNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		assertEquals(initiateurBeforeNewTransaction.getSolde(), initiateurAfterNewTransaction.getSolde());
		assertEquals(contrepartieBeforeNewTransaction.getSolde(), contrepartieAfterNewTransaction.getSolde());

		List<Transaction> listTransactionsAfterNewTransaction = new ArrayList<>();
		listTransactionsAfterNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		assertEquals(listTransactionsBeforeNewTransaction.size(), listTransactionsAfterNewTransaction.size());
	}

	@Test
	public void makeATransactionWhenInitiateurAndContrepartieAreSame() {
		// ARRANGE
		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurContrepartieBeforeTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		List<Transaction> listTransactionsBeforeNewTransaction = new ArrayList<>();
		listTransactionsBeforeNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction(
				initiateurContrepartieBeforeTransaction.getEmail(), initiateurContrepartieBeforeTransaction.getEmail(),
				10d, "Transaction test");

		// ASSERT
		assertFalse(result);

		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurContrepartieAfterTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		assertEquals((double) (initiateurContrepartieBeforeTransaction.getSolde()),
				(double) (initiateurContrepartieAfterTransaction.getSolde()));

		List<Transaction> listTransactionsAfterNewTransaction = new ArrayList<>();
		listTransactionsAfterNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		assertEquals(listTransactionsBeforeNewTransaction.size(), listTransactionsAfterNewTransaction.size());
	}
}
