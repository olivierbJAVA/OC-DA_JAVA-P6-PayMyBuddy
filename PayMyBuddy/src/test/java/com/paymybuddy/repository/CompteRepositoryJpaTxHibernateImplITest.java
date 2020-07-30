package com.paymybuddy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
import com.paymybuddy.entity.Compte;
import com.paymybuddy.entity.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

/**
 * Class including integration (with the database) tests for the
 * UtilisateurRepositoryJpaTxHibernateImpl Class.
 */
public class CompteRepositoryJpaTxHibernateImplITest {

	private static String paymybuddyPropertiesFile = "paymybuddyTest.properties";

	private static ResourceDatabasePopulator resourceDatabasePopulator;

	private static DriverManagerDataSource dataSource;

	private RepositoryTxManagerHibernate repositoryTxManager;

	private IUtilisateurRepository utilisateurRepositoryImpl;
	
	private ICompteRepository compteRepositoryImplUnderTest;

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
		compteRepositoryImplUnderTest = RepositoryFactory.getCompteRepository(repositoryTxManager);

		repositoryTxManager.openCurrentSessionWithTx();
	}

	@AfterEach
	private void afterPerTest() {
		repositoryTxManager.closeCurrentSession();
		repositoryTxManager.closeSessionFactory();
	}

	@Test
	public void createCompte() {
		// ARRANGE
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setEmail("abc@test.com");
		utilisateur.setPassword("abc");
		utilisateur.setSolde(123d);
		utilisateurRepositoryImpl.create(utilisateur);
		
		Compte compteToCreate = new Compte();
		compteToCreate.setBanque("SocieteGenerale");
		compteToCreate.setNumero("123SG");
		compteToCreate.setType("bancaire");
		compteToCreate.setUtilisateur(utilisateur);
			
		// ACT
		compteRepositoryImplUnderTest.create(compteToCreate);

		repositoryTxManager.commitTx();

		// ASSERT
		assertNotNull(compteRepositoryImplUnderTest.read(compteToCreate.getNumero()));
		
		assertEquals(compteToCreate.getBanque(),
				compteRepositoryImplUnderTest.read(compteToCreate.getNumero()).getBanque());
		assertEquals(compteToCreate.getNumero(),
				compteRepositoryImplUnderTest.read(compteToCreate.getNumero()).getNumero());
		assertEquals(compteToCreate.getType(),
				compteRepositoryImplUnderTest.read(compteToCreate.getNumero()).getType());
	}

	@Test
	public void deleteCompte() {
		// ARRANGE
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setEmail("abc@test.com");
		utilisateur.setPassword("abc");
		utilisateur.setSolde(123d);
		utilisateurRepositoryImpl.create(utilisateur);
		
		Compte compteToDelete = new Compte();
		compteToDelete.setBanque("SocieteGenerale");
		compteToDelete.setNumero("123SG");
		compteToDelete.setType("bancaire");
		compteToDelete.setUtilisateur(utilisateur);
		compteRepositoryImplUnderTest.create(compteToDelete);
		
		// ACT
		compteRepositoryImplUnderTest.delete(compteToDelete.getNumero());
		repositoryTxManager.commitTx();

		// ASSERT
		assertNull(compteRepositoryImplUnderTest.read(compteToDelete.getNumero()));
	}

	@Test
	public void updateCompte() {
		// ARRANGE
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setEmail("abc@test.com");
		utilisateur.setPassword("abc");
		utilisateur.setSolde(123d);
		utilisateurRepositoryImpl.create(utilisateur);
		
		Compte compteToUpdate = new Compte();
		compteToUpdate.setBanque("SG");
		compteToUpdate.setNumero("123SG");
		compteToUpdate.setType("bancaire");
		compteToUpdate.setUtilisateur(utilisateur);
		compteRepositoryImplUnderTest.create(compteToUpdate);
		
		Compte compteUpdated = new Compte();
		compteUpdated.setBanque("SocieteGenerale");
		compteUpdated.setNumero("123SG");
		compteUpdated.setType("bancaire");
		compteUpdated.setUtilisateur(utilisateur);

		// ACT
		compteRepositoryImplUnderTest.update(compteUpdated);
		repositoryTxManager.commitTx();

		// ASSERT
		assertEquals(compteUpdated.getBanque(),
				compteRepositoryImplUnderTest.read(compteToUpdate.getNumero()).getBanque());
		assertEquals(compteUpdated.getNumero(),
				compteRepositoryImplUnderTest.read(compteToUpdate.getNumero()).getNumero());
		assertEquals(compteUpdated.getType(),
				compteRepositoryImplUnderTest.read(compteToUpdate.getNumero()).getType());
	}

	@Test
	public void readCompte_whenCompteExist() {
		// ARRANGE
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setEmail("abc@test.com");
		utilisateur.setPassword("abc");
		utilisateur.setSolde(123d);
		utilisateurRepositoryImpl.create(utilisateur);
		
		Compte compteToRead = new Compte();
		compteToRead.setBanque("SG");
		compteToRead.setNumero("123SG");
		compteToRead.setType("bancaire");
		compteToRead.setUtilisateur(utilisateur);
		compteRepositoryImplUnderTest.create(compteToRead);
		
		repositoryTxManager.commitTx();

		// ACT
		Compte compteRead = compteRepositoryImplUnderTest.read(compteToRead.getNumero());

		// ASSERT
		assertNotNull(compteRead);
		assertEquals(compteToRead.getBanque(), compteRead.getBanque());
		assertEquals(compteToRead.getNumero(), compteRead.getNumero());
		assertEquals(compteToRead.getType(), compteRead.getType());
	}

	@Test
	public void readCompte_whenCompteNotExist() {
		// ACT & ASSERT
		assertNull(compteRepositoryImplUnderTest.read("CompteNotExist"));
	}
/*
	@Test
	public void addAConnection_whenNoExistingConnection() {
		// ARRANGE
		Utilisateur utilisateurToAddConnection = new Utilisateur();
		utilisateurToAddConnection.setEmail("abc@test.com");
		utilisateurToAddConnection.setPassword("abc");
		utilisateurToAddConnection.setSolde(123d);

		utilisateurRepositoryImplUnderTest.create(utilisateurToAddConnection);

		Utilisateur utilisateurNewConnection = new Utilisateur();
		utilisateurNewConnection.setEmail("def@test.com");
		utilisateurNewConnection.setPassword("def");
		utilisateurNewConnection.setSolde(456d);

		utilisateurRepositoryImplUnderTest.create(utilisateurNewConnection);

		Set<Utilisateur> connections = new HashSet<>();
		connections.add(utilisateurNewConnection);
		utilisateurToAddConnection.setConnection(connections);

		// ACT
		utilisateurRepositoryImplUnderTest.addConnection(utilisateurToAddConnection, utilisateurNewConnection);
		repositoryTxManager.commitTx();

		// ASSERT
		Utilisateur utilisateurConnectionAdded = utilisateurRepositoryImplUnderTest
				.read(utilisateurToAddConnection.getEmail());
		Set<Utilisateur> connectionsUtilisateur = utilisateurConnectionAdded.getConnection();
		Utilisateur connectionAdded = connectionsUtilisateur.iterator().next();

		assertEquals(utilisateurNewConnection.getEmail(), connectionAdded.getEmail());
		assertEquals(utilisateurNewConnection.getPassword(), connectionAdded.getPassword());
		assertEquals(utilisateurNewConnection.getSolde(), connectionAdded.getSolde());
	}

	@Test
	public void addAConnection_whenExistingConnection() {
		// ARRANGE
		Utilisateur utilisateurToAddConnection = new Utilisateur();
		utilisateurToAddConnection.setEmail("abc@test.com");
		utilisateurToAddConnection.setPassword("abc");
		utilisateurToAddConnection.setSolde(123d);

		Utilisateur utilisateurExistingConnection = new Utilisateur();
		utilisateurExistingConnection.setEmail("def@test.com");
		utilisateurExistingConnection.setPassword("def");
		utilisateurExistingConnection.setSolde(456d);

		utilisateurRepositoryImplUnderTest.create(utilisateurExistingConnection);

		Set<Utilisateur> connections = new HashSet<>();
		connections.add(utilisateurExistingConnection);
		utilisateurToAddConnection.setConnection(connections);

		utilisateurRepositoryImplUnderTest.create(utilisateurToAddConnection);

		utilisateurRepositoryImplUnderTest.addConnection(utilisateurToAddConnection, utilisateurExistingConnection);
		;

		Utilisateur utilisateurNewConnection = new Utilisateur();
		utilisateurNewConnection.setEmail("ghi@test.com");
		utilisateurNewConnection.setPassword("ghi");
		utilisateurNewConnection.setSolde(789d);

		utilisateurRepositoryImplUnderTest.create(utilisateurNewConnection);

		connections.add(utilisateurNewConnection);
		utilisateurToAddConnection.setConnection(connections);

		// ACT
		utilisateurRepositoryImplUnderTest.addConnection(utilisateurToAddConnection, utilisateurNewConnection);
		;
		repositoryTxManager.commitTx();

		// ASSERT
		Utilisateur utilisateurConnectionAdded = utilisateurRepositoryImplUnderTest
				.read(utilisateurToAddConnection.getEmail());
		Set<Utilisateur> connectionsUtilisateur = utilisateurConnectionAdded.getConnection();
		Utilisateur connectionAdded = null;
		for (Utilisateur connection : connectionsUtilisateur) {
			if (connection.getEmail().equals(utilisateurNewConnection.getEmail())) {
				connectionAdded = connection;
			}
		}

		assertEquals(utilisateurNewConnection.getEmail(), connectionAdded.getEmail());
		assertEquals(utilisateurNewConnection.getPassword(), connectionAdded.getPassword());
		assertEquals(utilisateurNewConnection.getSolde(), connectionAdded.getSolde());
	}

	@Test
	public void addAConnection_whenConnectionAlreadyExisting() {
		// ARRANGE
		Utilisateur utilisateurToAddConnection = new Utilisateur();
		utilisateurToAddConnection.setEmail("abc@test.com");
		utilisateurToAddConnection.setPassword("abc");
		utilisateurToAddConnection.setSolde(123d);

		Utilisateur utilisateurExistingConnection = new Utilisateur();
		utilisateurExistingConnection.setEmail("def@test.com");
		utilisateurExistingConnection.setPassword("def");
		utilisateurExistingConnection.setSolde(456d);

		utilisateurRepositoryImplUnderTest.create(utilisateurExistingConnection);

		Set<Utilisateur> connections = new HashSet<>();
		connections.add(utilisateurExistingConnection);
		utilisateurToAddConnection.setConnection(connections);

		utilisateurRepositoryImplUnderTest.create(utilisateurToAddConnection);

		utilisateurRepositoryImplUnderTest.addConnection(utilisateurToAddConnection, utilisateurExistingConnection);

		// ACT
		utilisateurRepositoryImplUnderTest.addConnection(utilisateurToAddConnection, utilisateurExistingConnection);
		repositoryTxManager.commitTx();

		// ASSERT
		Utilisateur utilisateurConnectionAdded = utilisateurRepositoryImplUnderTest
				.read(utilisateurToAddConnection.getEmail());
		Set<Utilisateur> connectionsUtilisateur = utilisateurConnectionAdded.getConnection();
		Utilisateur connectionAdded = connectionsUtilisateur.iterator().next();

		assertEquals(1, connectionsUtilisateur.size());

		assertEquals(utilisateurExistingConnection.getEmail(), connectionAdded.getEmail());
		assertEquals(utilisateurExistingConnection.getPassword(), connectionAdded.getPassword());
		assertEquals(utilisateurExistingConnection.getSolde(), connectionAdded.getSolde());
	}
*/
}
