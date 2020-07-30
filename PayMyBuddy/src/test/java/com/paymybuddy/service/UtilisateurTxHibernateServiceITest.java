package com.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.paymybuddy.configuration.RepositoryDataSource;
import com.paymybuddy.configuration.RepositoryRessourceDatabasePopulator;
import com.paymybuddy.entity.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.factory.ServiceFactory;
import com.paymybuddy.repository.ICompteRepository;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

/**
 * Class including integration tests (with the database) for the
 * UtilisateurTxHibernateService Class.
 */
public class UtilisateurTxHibernateServiceITest {

	private static String paymybuddyPropertiesFile = "paymybuddyTest.properties";

	private static ResourceDatabasePopulator resourceDatabasePopulator;

	private static DriverManagerDataSource dataSource;

	private RepositoryTxManagerHibernate repositoryTxManager;

	private IUtilisateurRepository utilisateurRepositoryImpl;

	private ICompteRepository compteRepositoryImpl;
	
	private UtilisateurTxHibernateService utilisateurTxHibernateServiceUnderTest;

	@BeforeAll
	private static void setUpAllTest() {
		// We get a dataSource
		dataSource = RepositoryDataSource.getDataSource(paymybuddyPropertiesFile);

		// We get a resourceDatabasePopulator
		resourceDatabasePopulator = RepositoryRessourceDatabasePopulator
				.getResourceDatabasePopulator("/CleanDBForTests.sql");

		// We close the dataSource
		RepositoryDataSource.closeDatasource();
	}

	@BeforeEach
	private void setUpPerTest() {
		// We clear the database
		DatabasePopulatorUtils.execute(resourceDatabasePopulator, dataSource);

		RepositoryTxManagerHibernate.resetTxManager();
		repositoryTxManager = RepositoryTxManagerHibernate.getRepositoryTxManagerHibernate(paymybuddyPropertiesFile);

		RepositoryFactory.resetUtilisateurRepository();
		utilisateurRepositoryImpl = RepositoryFactory.getUtilisateurRepository(repositoryTxManager);

		RepositoryFactory.resetCompteRepository();
		compteRepositoryImpl = RepositoryFactory.getCompteRepository(repositoryTxManager);
		
		ServiceFactory.resetUtilisateurService();
		utilisateurTxHibernateServiceUnderTest = ServiceFactory.getUtilisateurService(repositoryTxManager,
				utilisateurRepositoryImpl, compteRepositoryImpl);
	}

	@AfterEach
	private void afterPerTest() {
		repositoryTxManager.closeSessionFactory();
	}

	@Test
	public void registerToApplication_whenUtilisateurNotAlreadyExist() {
		// ARRANGE
		Utilisateur utilisateurToRegister = new Utilisateur();
		utilisateurToRegister.setEmail("abc@test.com");
		utilisateurToRegister.setPassword("abc");
		utilisateurToRegister.setSolde(0d);
		
		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.registerToApplication(utilisateurToRegister.getEmail(),
				utilisateurToRegister.getPassword());

		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur utilisateurRegistered = utilisateurRepositoryImpl.read(utilisateurToRegister.getEmail());
		repositoryTxManager.commitTxAndCloseCurrentSession();

		// ASSERT
		assertTrue(result);

		assertEquals(utilisateurToRegister.getEmail(), utilisateurRegistered.getEmail());
		assertEquals(utilisateurToRegister.getPassword(), utilisateurRegistered.getPassword());
		assertEquals(utilisateurToRegister.getSolde(), utilisateurRegistered.getSolde());
	}

	@Test
	public void registerToApplication_whenUtilisateurAlreadyExist() {
		// ARRANGE
		Utilisateur utilisateurToRegister = new Utilisateur();
		utilisateurToRegister.setEmail("abc@test.com");
		utilisateurToRegister.setPassword("abc");
		utilisateurToRegister.setSolde(123d);

		utilisateurTxHibernateServiceUnderTest.registerToApplication(utilisateurToRegister.getEmail(),
				utilisateurToRegister.getPassword());

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.registerToApplication(utilisateurToRegister.getEmail(),
				utilisateurToRegister.getPassword());

		// ASSERT
		assertFalse(result);
	}

	@Test
	public void connectToApplication_whenUtilisateurExistAndPasswordIsCorrect() {
		// ARRANGE
		Utilisateur utilisateurToConnect = new Utilisateur();
		utilisateurToConnect.setEmail("abc@test.com");
		utilisateurToConnect.setPassword("abc");
		utilisateurToConnect.setSolde(123d);

		utilisateurTxHibernateServiceUnderTest.registerToApplication(utilisateurToConnect.getEmail(),
				utilisateurToConnect.getPassword());

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.connectToApplication(utilisateurToConnect.getEmail(),
				utilisateurToConnect.getPassword());

		// ASSERT
		assertTrue(result);
	}

	@Test
	public void connectToApplication_whenUtilisateurExistAndPasswordIsWrong() {
		// ARRANGE
		Utilisateur utilisateurToConnect = new Utilisateur();
		utilisateurToConnect.setEmail("abc@test.com");
		utilisateurToConnect.setPassword("abc");
		utilisateurToConnect.setSolde(123d);

		utilisateurTxHibernateServiceUnderTest.registerToApplication(utilisateurToConnect.getEmail(),
				utilisateurToConnect.getPassword());

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.connectToApplication(utilisateurToConnect.getEmail(),
				"WrongPassword");

		// ASSERT
		assertFalse(result);
	}

	@Test
	public void connectToApplication_whenUtilisateurNotExist() {
		// ARRANGE

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.connectToApplication("UtilisateurNotExist", "Password");

		// ASSERT
		assertFalse(result);
	}

	@Test
	public void addConnection_whenUtilisateurExistAndConnectionExistAndNotAlreadyConnected() {
		// ARRANGE
		Utilisateur utilisateurToAddConnection = new Utilisateur();
		utilisateurToAddConnection.setEmail("abc@test.com");
		utilisateurToAddConnection.setPassword("abc");
		utilisateurToAddConnection.setSolde(0d);

		Utilisateur utilisateurNewConnection = new Utilisateur();
		utilisateurNewConnection.setEmail("def@test.com");
		utilisateurNewConnection.setPassword("def");
		utilisateurNewConnection.setSolde(0d);

		repositoryTxManager.openCurrentSessionWithTx();
		utilisateurRepositoryImpl.create(utilisateurToAddConnection);
		utilisateurRepositoryImpl.create(utilisateurNewConnection);
		repositoryTxManager.commitTxAndCloseCurrentSession();

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.addConnection(utilisateurToAddConnection.getEmail(),
				utilisateurNewConnection.getEmail());

		// ASSERT
		assertTrue(result);

		repositoryTxManager.openCurrentSessionWithTx();

		Utilisateur utilisateurWithAddedConnection = utilisateurRepositoryImpl
				.read(utilisateurToAddConnection.getEmail());
		Set<Utilisateur> utilisateurConnections = new HashSet<>();
		utilisateurConnections = utilisateurWithAddedConnection.getConnection();
		Utilisateur connectionAdded = utilisateurConnections.iterator().next();

		assertEquals(1, utilisateurConnections.size());

		assertEquals(utilisateurNewConnection.getEmail(), connectionAdded.getEmail());
		assertEquals(utilisateurNewConnection.getPassword(), connectionAdded.getPassword());
		assertEquals(utilisateurNewConnection.getSolde(), connectionAdded.getSolde());

		repositoryTxManager.commitTxAndCloseCurrentSession();
	}

	@Test
	public void addConnection_whenUtilisateurNotExist() {
		// ARRANGE
		Utilisateur utilisateurToAddConnection = new Utilisateur();
		utilisateurToAddConnection.setEmail("abc@test.com");
		utilisateurToAddConnection.setPassword("abc");
		utilisateurToAddConnection.setSolde(0d);

		Utilisateur utilisateurNewConnection = new Utilisateur();
		utilisateurNewConnection.setEmail("def@test.com");
		utilisateurNewConnection.setPassword("def");
		utilisateurNewConnection.setSolde(0d);

		repositoryTxManager.openCurrentSessionWithTx();
		utilisateurRepositoryImpl.create(utilisateurNewConnection);
		repositoryTxManager.commitTxAndCloseCurrentSession();

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.addConnection(utilisateurToAddConnection.getEmail(),
				utilisateurNewConnection.getEmail());

		// ASSERT
		assertFalse(result);
	}

	@Test
	public void addConnection_whenUtilisateurExistAndConnectionNotExist() {
		// ARRANGE
		Utilisateur utilisateurToAddConnection = new Utilisateur();
		utilisateurToAddConnection.setEmail("abc@test.com");
		utilisateurToAddConnection.setPassword("abc");
		utilisateurToAddConnection.setSolde(0d);

		Utilisateur utilisateurNewConnection = new Utilisateur();
		utilisateurNewConnection.setEmail("def@test.com");
		utilisateurNewConnection.setPassword("def");
		utilisateurNewConnection.setSolde(0d);

		repositoryTxManager.openCurrentSessionWithTx();
		utilisateurRepositoryImpl.create(utilisateurToAddConnection);
		repositoryTxManager.commitTx();
		repositoryTxManager.closeCurrentSession();

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.addConnection(utilisateurToAddConnection.getEmail(),
				utilisateurNewConnection.getEmail());

		// ASSERT
		assertFalse(result);

		repositoryTxManager.openCurrentSessionWithTx();

		Utilisateur utilisateurWithAddedConnection = utilisateurRepositoryImpl
				.read(utilisateurToAddConnection.getEmail());
		Set<Utilisateur> utilisateurConnections = new HashSet<>();
		utilisateurConnections = utilisateurWithAddedConnection.getConnection();

		assertEquals(0, utilisateurConnections.size());

		repositoryTxManager.commitTxAndCloseCurrentSession();
	}

	@Test
	public void addConnection_whenUtilisateurExistAndConnectionExistAndAlreadyConnected() {
		// ARRANGE
		Utilisateur utilisateurToAddConnection = new Utilisateur();
		utilisateurToAddConnection.setEmail("abc@test.com");
		utilisateurToAddConnection.setPassword("abc");
		utilisateurToAddConnection.setSolde(0d);

		Utilisateur utilisateurNewConnection = new Utilisateur();
		utilisateurNewConnection.setEmail("def@test.com");
		utilisateurNewConnection.setPassword("def");
		utilisateurNewConnection.setSolde(0d);

		repositoryTxManager.openCurrentSessionWithTx();
		utilisateurRepositoryImpl.create(utilisateurToAddConnection);
		utilisateurRepositoryImpl.create(utilisateurNewConnection);
		repositoryTxManager.commitTx();
		repositoryTxManager.closeCurrentSession();

		utilisateurTxHibernateServiceUnderTest.addConnection(utilisateurToAddConnection.getEmail(),
				utilisateurNewConnection.getEmail());

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.addConnection(utilisateurToAddConnection.getEmail(),
				utilisateurNewConnection.getEmail());

		// ASSERT
		assertFalse(result);

		repositoryTxManager.openCurrentSessionWithTx();

		Utilisateur utilisateurWithAddedConnection = utilisateurRepositoryImpl
				.read(utilisateurToAddConnection.getEmail());

		Set<Utilisateur> utilisateurConnections = new HashSet<>();
		utilisateurConnections = utilisateurWithAddedConnection.getConnection();

		assertEquals(1, utilisateurConnections.size());

		repositoryTxManager.commitTxAndCloseCurrentSession();
	}
}
