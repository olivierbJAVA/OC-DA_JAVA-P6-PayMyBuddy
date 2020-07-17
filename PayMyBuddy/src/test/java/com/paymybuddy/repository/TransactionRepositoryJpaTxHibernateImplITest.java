package com.paymybuddy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

/**
 * Class including integration tests (with the database) for the
 * TransactionRepositoryJpaTxHibernateImpl Class.
 */
public class TransactionRepositoryJpaTxHibernateImplITest {

	private static String paymybuddyPropertiesFile = "paymybuddyTest.properties";

	private static ResourceDatabasePopulator resourceDatabasePopulator;

	private static DriverManagerDataSource dataSource;

	private RepositoryTxManagerHibernate repositoryTxManager;

	private ITransactionRepository transactionRepositoryImplUnderTest;

	private IUtilisateurRepository utilisateurRepositoryImp;

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
		utilisateurRepositoryImp = RepositoryFactory.getUtilisateurRepository(repositoryTxManager);

		RepositoryFactory.resetTransactionRepository();
		transactionRepositoryImplUnderTest = RepositoryFactory.getTransactionRepository(repositoryTxManager);

		repositoryTxManager.openCurrentSessionWithTx();
	}

	@AfterEach
	private void afterPerTest() {
		repositoryTxManager.closeCurrentSession();
		repositoryTxManager.closeSessionFactory();
	}

	@Test
	public void createTransaction() {
		// ARRANGE
		Utilisateur initiateur = utilisateurRepositoryImp.read("abc@test.com");
		Utilisateur contrepartie = utilisateurRepositoryImp.read("def@test.com");

		Transaction transactionToCreate = new Transaction();
		transactionToCreate.setInitiateur(initiateur);
		transactionToCreate.setContrepartie(contrepartie);
		transactionToCreate.setMontant(123d);
		transactionToCreate.setCommentaire("Transaction created for test purpose");

		// ACT
		Transaction transactionCreated = transactionRepositoryImplUnderTest.create(transactionToCreate);
		repositoryTxManager.commitTx();

		// ASSERT
		assertNotNull(transactionCreated);

		assertEquals(transactionToCreate.getInitiateur().getEmail(), transactionCreated.getInitiateur().getEmail());
		assertEquals(transactionToCreate.getInitiateur().getPassword(),
				transactionCreated.getInitiateur().getPassword());
		assertEquals(transactionToCreate.getInitiateur().getSolde(), transactionCreated.getInitiateur().getSolde());

		assertEquals(transactionToCreate.getContrepartie().getEmail(), transactionCreated.getContrepartie().getEmail());
		assertEquals(transactionToCreate.getContrepartie().getPassword(),
				transactionToCreate.getContrepartie().getPassword());
		assertEquals(transactionToCreate.getContrepartie().getSolde(), transactionCreated.getContrepartie().getSolde());

		assertEquals(transactionToCreate.getMontant(), transactionCreated.getMontant());
		assertEquals(transactionToCreate.getCommentaire(), transactionCreated.getCommentaire());
	}

	@Test
	public void deleteTransaction() {
		// ARRANGE
		Utilisateur initiateur = utilisateurRepositoryImp.read("abc@test.com");
		Utilisateur contrepartie = utilisateurRepositoryImp.read("def@test.com");

		Transaction transactionToDelete = new Transaction();
		transactionToDelete.setInitiateur(initiateur);
		transactionToDelete.setContrepartie(contrepartie);
		transactionToDelete.setMontant(123d);
		transactionToDelete.setCommentaire("Transaction created for test purpose");

		Transaction transactionDeleted = transactionRepositoryImplUnderTest.create(transactionToDelete);

		// ACT
		transactionRepositoryImplUnderTest.delete(transactionDeleted.getIdTransaction());
		repositoryTxManager.commitTx();

		// ASSERT
		assertNull(transactionRepositoryImplUnderTest.read(transactionDeleted.getIdTransaction()));
	}

	@Test
	public void updateTransaction() {
		// ARRANGE
		Utilisateur initiateur = utilisateurRepositoryImp.read("abc@test.com");
		Utilisateur contrepartie = utilisateurRepositoryImp.read("def@test.com");

		Transaction transactionToUpdate = new Transaction();
		transactionToUpdate.setInitiateur(initiateur);
		transactionToUpdate.setContrepartie(contrepartie);
		transactionToUpdate.setMontant(321d);
		transactionToUpdate.setCommentaire("Transaction created for test purpose");

		transactionToUpdate = transactionRepositoryImplUnderTest.create(transactionToUpdate);

		// ACT
		transactionToUpdate.setMontant(123d);
		transactionToUpdate.setCommentaire("Transaction udpated");

		transactionRepositoryImplUnderTest.update(transactionToUpdate);
		repositoryTxManager.commitTx();

		// ASSERT
		Transaction transactionUdpated = transactionRepositoryImplUnderTest
				.read(transactionToUpdate.getIdTransaction());
		assertEquals("Transaction udpated", transactionUdpated.getCommentaire());
		assertEquals(123d, transactionUdpated.getMontant());
	}

	@Test
	public void readTransaction_whenTransactionExist() {
		// ARRANGE
		Utilisateur initiateur = utilisateurRepositoryImp.read("abc@test.com");
		Utilisateur contrepartie = utilisateurRepositoryImp.read("def@test.com");

		Transaction transactionToRead = new Transaction();
		transactionToRead.setInitiateur(initiateur);
		transactionToRead.setContrepartie(contrepartie);
		transactionToRead.setMontant(123d);
		transactionToRead.setCommentaire("Transaction created for test purpose");

		transactionToRead = transactionRepositoryImplUnderTest.create(transactionToRead);

		// ACT
		Transaction transactionRead = transactionRepositoryImplUnderTest.read(transactionToRead.getIdTransaction());
		repositoryTxManager.commitTx();

		// ASSERT
		assertNotNull(transactionRead);

		assertEquals(transactionToRead.getInitiateur().getEmail(), transactionRead.getInitiateur().getEmail());
		assertEquals(transactionToRead.getInitiateur().getPassword(), transactionRead.getInitiateur().getPassword());
		assertEquals(transactionToRead.getInitiateur().getSolde(), transactionRead.getInitiateur().getSolde());

		assertEquals(transactionToRead.getContrepartie().getEmail(), transactionRead.getContrepartie().getEmail());
		assertEquals(transactionToRead.getContrepartie().getPassword(),
				transactionRead.getContrepartie().getPassword());
		assertEquals(transactionToRead.getContrepartie().getSolde(), transactionRead.getContrepartie().getSolde());

		assertEquals(transactionToRead.getMontant(), transactionRead.getMontant());
		assertEquals(transactionToRead.getCommentaire(), transactionRead.getCommentaire());
	}

	@Test
	public void readTransaction_whenTransactionNotExist() {
		// ACT & ASSERT
		assertNull(transactionRepositoryImplUnderTest.read(123));
	}

	@Test
	public void getAllTransactions() {
		// ARRANGE
		Transaction transactionToGet1 = new Transaction();
		Utilisateur initiateur1 = utilisateurRepositoryImp.read("abc@test.com");
		Utilisateur contrepartie1 = utilisateurRepositoryImp.read("def@test.com");
		transactionToGet1.setInitiateur(initiateur1);
		transactionToGet1.setContrepartie(contrepartie1);
		transactionToGet1.setMontant(1d);
		transactionToGet1 = transactionRepositoryImplUnderTest.create(transactionToGet1);

		Transaction transactionToGet2 = new Transaction();
		transactionToGet2.setInitiateur(initiateur1);
		transactionToGet2.setContrepartie(contrepartie1);
		transactionToGet2.setMontant(2d);
		transactionToGet2 = transactionRepositoryImplUnderTest.create(transactionToGet2);

		Transaction transactionToGet3 = new Transaction();
		Utilisateur contrepartie2 = utilisateurRepositoryImp.read("ghi@test.com");
		transactionToGet3.setInitiateur(initiateur1);
		transactionToGet3.setContrepartie(contrepartie2);
		transactionToGet3.setMontant(3d);
		transactionToGet3 = transactionRepositoryImplUnderTest.create(transactionToGet3);

		// ACT
		List<Transaction> transactionsGet = new ArrayList<>();
		transactionsGet = transactionRepositoryImplUnderTest.getTransactions("abc@test.com");
		repositoryTxManager.commitTx();

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
}
