package com.paymybuddy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.paymybuddy.configuration.RepositoryDataSource;
import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

public class TransactionRepositoryJpaTxHibernateImplTest {

	private static String hibernateConfigFile = "src/test/resources/hibernateTest.cfg.xml";

	private static RepositoryTxManagerHibernate repositoryTxManager;

	private static ResourceDatabasePopulator resourceDatabasePopulator;

	private static DriverManagerDataSource dataSource;

	private ITransactionRepository transactionRepositoryImplUnderTest;

	private IUtilisateurRepository utilisateurRepositoryImplUnderTest;

	@BeforeAll
	private static void setUpAllTest() {
		// We get a dataSource
		dataSource = RepositoryDataSource.getDataSource("org.postgresql.Driver",
				"jdbc:postgresql://localhost/PayMyBuddyTest", "postgres", "admin");

		// We get a resourceDatabasePopulator
		resourceDatabasePopulator = new ResourceDatabasePopulator();
		resourceDatabasePopulator.addScript(new ClassPathResource("/DataTransactionsForTests.sql"));
	}

	@BeforeEach
	private void setUpPerTest() {
		// We prepare the database
		DatabasePopulatorUtils.execute(resourceDatabasePopulator, dataSource);

		repositoryTxManager = RepositoryTxManagerHibernate.getRepositoryTxManagerHibernate(hibernateConfigFile);

		transactionRepositoryImplUnderTest = RepositoryFactory.getTransactionRepository(repositoryTxManager);

		utilisateurRepositoryImplUnderTest = RepositoryFactory.getUtilisateurRepository(repositoryTxManager);

		repositoryTxManager.openCurrentSessionWithTx();
	}

	@AfterEach
	private void afterPerTest() {
		
		repositoryTxManager.closeCurrentSession();
	}

	@Test
	public void createTransaction() {
		// ARRANGE
		Utilisateur initiateur = utilisateurRepositoryImplUnderTest.read("abc@test.com");
		Utilisateur contrepartie = utilisateurRepositoryImplUnderTest.read("def@test.com");

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
		Utilisateur initiateur = utilisateurRepositoryImplUnderTest.read("abc@test.com");
		Utilisateur contrepartie = utilisateurRepositoryImplUnderTest.read("def@test.com");

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
		Utilisateur initiateur = utilisateurRepositoryImplUnderTest.read("abc@test.com");
		Utilisateur contrepartie = utilisateurRepositoryImplUnderTest.read("def@test.com");

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
		Utilisateur initiateur = utilisateurRepositoryImplUnderTest.read("abc@test.com");
		Utilisateur contrepartie = utilisateurRepositoryImplUnderTest.read("def@test.com");

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
		Utilisateur initiateur1 = utilisateurRepositoryImplUnderTest.read("abc@test.com");
		Utilisateur contrepartie1 = utilisateurRepositoryImplUnderTest.read("def@test.com");
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
		Utilisateur contrepartie2 = utilisateurRepositoryImplUnderTest.read("ghi@test.com");
		transactionToGet3.setInitiateur(initiateur1);
		transactionToGet3.setContrepartie(contrepartie2);
		transactionToGet3.setMontant(3d);
		transactionToGet3 = transactionRepositoryImplUnderTest.create(transactionToGet3);

		// ACT
		List<Transaction> transactionsGet = new ArrayList<>();
		transactionsGet = transactionRepositoryImplUnderTest.getTransactions("abc@test.com");
		repositoryTxManager.commitTx();

		// ASSERT
		assertNotNull(transactionsGet);
		assertEquals(3, transactionsGet.size());
	}

}
