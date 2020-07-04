package com.paymybuddy.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.Utilisateur;
import com.paymybuddy.factory.RepositoryDataSourceFactory;
import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.factory.RepositoryRessourceDatabasePopulatorFactory;

public class TransactionRepositoryJpaImplTest {

	private static String persistence = "persistencePostgreTest";

	private static ResourceDatabasePopulator resourceDatabasePopulator;

	private static DriverManagerDataSource dataSource;

	private ITransactionRepository transactionRepositoryImplUnderTest;

	private IUtilisateurRepository utilisateurRepositoryImplUnderTest;

	@BeforeAll
	private static void setUpAllTest() {
		// We get a dataSource
		dataSource = RepositoryDataSourceFactory.getDataSource("org.postgresql.Driver",
				"jdbc:postgresql://localhost/PayMyBuddyTest", "postgres", "admin");

		// We get a resourceDatabasePopulator
		resourceDatabasePopulator = RepositoryRessourceDatabasePopulatorFactory
				.getResourceDatabasePopulator("/DataTransactionsForTests.sql");
	}

	@BeforeEach
	private void setUpPerTest() {
		// We clear the database
		DatabasePopulatorUtils.execute(resourceDatabasePopulator, dataSource);

		transactionRepositoryImplUnderTest = RepositoryFactory.getTransactionRepository(persistence);

		utilisateurRepositoryImplUnderTest = RepositoryFactory.getUtilisateurRepository(persistence);
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

		// ASSERT
		assertEquals(transactionToCreate, transactionCreated);
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

		transactionToDelete = transactionRepositoryImplUnderTest.create(transactionToDelete);

		// ACT
		transactionRepositoryImplUnderTest.delete(transactionToDelete.getIdTransaction());

		// ASSERT
		assertNull(transactionRepositoryImplUnderTest.read(transactionToDelete.getIdTransaction()));
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
		transactionToUpdate.setCommentaire("Transaction to update for test purpose");

		transactionToUpdate = transactionRepositoryImplUnderTest.create(transactionToUpdate);

		transactionToUpdate.setCommentaire("Transaction udpated");

		// ACT
		transactionRepositoryImplUnderTest.update(transactionToUpdate);

		// ASSERT
		Transaction transactionUdpated = transactionRepositoryImplUnderTest
				.read(transactionToUpdate.getIdTransaction());

		assertEquals(transactionToUpdate, transactionUdpated);
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
		transactionToRead.setCommentaire("Transaction to read for test purpose");

		transactionToRead = transactionRepositoryImplUnderTest.create(transactionToRead);

		// ACT
		Transaction transactionRead = transactionRepositoryImplUnderTest.read(transactionToRead.getIdTransaction());

		// ASSERT
		assertEquals(transactionToRead, transactionRead);
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

		// ASSERT
		assertEquals(3, transactionsGet.size());

		assertThat(transactionsGet).containsExactly(transactionToGet3, transactionToGet2, transactionToGet1);
	}
}
