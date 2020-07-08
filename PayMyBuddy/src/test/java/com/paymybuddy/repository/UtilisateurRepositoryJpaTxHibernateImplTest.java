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
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.paymybuddy.configuration.RepositoryDataSource;
import com.paymybuddy.entity.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

public class UtilisateurRepositoryJpaTxHibernateImplTest {

	private static String hibernateConfigFile = "src/test/resources/hibernateTest.cfg.xml";

	private static RepositoryTxManagerHibernate repositoryTxManager;

	private static ResourceDatabasePopulator resourceDatabasePopulator;

	private static DriverManagerDataSource dataSource;

	private IUtilisateurRepository utilisateurRepositoryImplUnderTest;

	@BeforeAll
	private static void setUpAllTest() {
		// We get a dataSource
		dataSource = RepositoryDataSource.getDataSource("org.postgresql.Driver",
				"jdbc:postgresql://localhost/PayMyBuddyTest", "postgres", "admin");

		// We get a resourceDatabasePopulator
		resourceDatabasePopulator = new ResourceDatabasePopulator();
		resourceDatabasePopulator.addScript(new ClassPathResource("/cleanDBForTests.sql"));
	}

	@BeforeEach
	private void setUpPerTest() {
		// We clear the database
		DatabasePopulatorUtils.execute(resourceDatabasePopulator, dataSource);

		repositoryTxManager = RepositoryTxManagerHibernate.getRepositoryTxManagerHibernate(hibernateConfigFile);

		utilisateurRepositoryImplUnderTest = RepositoryFactory.getUtilisateurRepository(repositoryTxManager);

		repositoryTxManager.openCurrentSessionWithTx();
	}

	@AfterEach
	private void afterPerTest() {

		repositoryTxManager.closeCurrentSession();
	}

	@Test
	public void createUtilisateur() {
		// ARRANGE
		Utilisateur utilisateurToCreate = new Utilisateur();
		utilisateurToCreate.setEmail("abc@test.com");
		utilisateurToCreate.setPassword("abc");
		utilisateurToCreate.setSolde(123d);

		// ACT
		utilisateurRepositoryImplUnderTest.create(utilisateurToCreate);

		repositoryTxManager.commitTx();

		// ASSERT
		assertNotNull(utilisateurRepositoryImplUnderTest.read(utilisateurToCreate.getEmail()));
		assertEquals(utilisateurToCreate.getEmail(),
				utilisateurRepositoryImplUnderTest.read(utilisateurToCreate.getEmail()).getEmail());
		assertEquals(utilisateurToCreate.getPassword(),
				utilisateurRepositoryImplUnderTest.read(utilisateurToCreate.getEmail()).getPassword());
		assertEquals(utilisateurToCreate.getSolde(),
				utilisateurRepositoryImplUnderTest.read(utilisateurToCreate.getEmail()).getSolde());
	}

	@Test
	public void deleteUtilisateur() {
		// ARRANGE
		Utilisateur utilisateurToDelete = new Utilisateur();
		utilisateurToDelete.setEmail("abc@test.com");
		utilisateurToDelete.setPassword("abc");
		utilisateurToDelete.setSolde(123d);

		utilisateurRepositoryImplUnderTest.create(utilisateurToDelete);

		// ACT
		utilisateurRepositoryImplUnderTest.delete(utilisateurToDelete.getEmail());
		repositoryTxManager.commitTx();

		// ASSERT
		assertNull(utilisateurRepositoryImplUnderTest.read(utilisateurToDelete.getEmail()));
	}

	@Test
	public void updateUtilisateur() {
		// ARRANGE
		Utilisateur utilisateurToUpdate = new Utilisateur();
		utilisateurToUpdate.setEmail("abc@test.com");
		utilisateurToUpdate.setPassword("abc");
		utilisateurToUpdate.setSolde(123d);

		utilisateurRepositoryImplUnderTest.create(utilisateurToUpdate);

		Utilisateur utilisateurUpdated = new Utilisateur();
		utilisateurUpdated.setEmail("abc@test.com");
		utilisateurUpdated.setPassword("abc");
		utilisateurUpdated.setSolde(456d);

		// ACT
		utilisateurRepositoryImplUnderTest.update(utilisateurUpdated);
		repositoryTxManager.commitTx();

		// ASSERT
		assertEquals(utilisateurUpdated.getSolde(),
				utilisateurRepositoryImplUnderTest.read(utilisateurToUpdate.getEmail()).getSolde());
		assertEquals(utilisateurUpdated.getEmail(),
				utilisateurRepositoryImplUnderTest.read(utilisateurToUpdate.getEmail()).getEmail());
		assertEquals(utilisateurUpdated.getPassword(),
				utilisateurRepositoryImplUnderTest.read(utilisateurToUpdate.getEmail()).getPassword());
	}

	@Test
	public void readUtilisateur_whenUtilisateurExist_whenUtilisateurHasNoConnection() {
		// ARRANGE
		Utilisateur utilisateurToRead = new Utilisateur();
		utilisateurToRead.setEmail("abc@test.com");
		utilisateurToRead.setPassword("abc");
		utilisateurToRead.setSolde(123d);

		utilisateurRepositoryImplUnderTest.create(utilisateurToRead);
		repositoryTxManager.commitTx();

		// ACT
		Utilisateur utilisateurRead = utilisateurRepositoryImplUnderTest.read(utilisateurToRead.getEmail());

		// ASSERT
		assertNotNull(utilisateurRead);
		assertEquals(utilisateurToRead.getEmail(), utilisateurRead.getEmail());
		assertEquals(utilisateurToRead.getPassword(), utilisateurRead.getPassword());
		assertEquals(utilisateurToRead.getSolde(), utilisateurRead.getSolde());
	}

	@Test
	public void readUtilisateur_whenUtilisateurExist_whenUtilisateurHasAConnection() {
		// ARRANGE
		Utilisateur utilisateurToRead = new Utilisateur();
		utilisateurToRead.setEmail("abc@test.com");
		utilisateurToRead.setPassword("abc");
		utilisateurToRead.setSolde(123d);
		utilisateurRepositoryImplUnderTest.create(utilisateurToRead);

		Utilisateur utilisateurConnectionToRead = new Utilisateur();
		utilisateurConnectionToRead.setEmail("def@test.com");
		utilisateurConnectionToRead.setPassword("def");
		utilisateurConnectionToRead.setSolde(456d);
		utilisateurRepositoryImplUnderTest.create(utilisateurConnectionToRead);

		Set<Utilisateur> connectionsToRead = new HashSet<>();
		connectionsToRead.add(utilisateurConnectionToRead);
		utilisateurToRead.setConnection(connectionsToRead);

		utilisateurRepositoryImplUnderTest.addConnection(utilisateurToRead, utilisateurConnectionToRead);

		// ACT
		Utilisateur utilisateurRead = utilisateurRepositoryImplUnderTest.read(utilisateurToRead.getEmail());
		repositoryTxManager.commitTx();

		// ASSERT
		assertNotNull(utilisateurRead);
		assertEquals(utilisateurToRead.getEmail(), utilisateurRead.getEmail());
		assertEquals(utilisateurToRead.getPassword(), utilisateurRead.getPassword());
		assertEquals(utilisateurToRead.getSolde(), utilisateurRead.getSolde());

		Set<Utilisateur> connectionsRead = utilisateurRead.getConnection();
		Utilisateur utilisateurConnectionRead = connectionsRead.iterator().next();
		assertEquals(utilisateurConnectionToRead.getEmail(), utilisateurConnectionRead.getEmail());
		assertEquals(utilisateurConnectionToRead.getPassword(), utilisateurConnectionRead.getPassword());
		assertEquals(utilisateurConnectionToRead.getSolde(), utilisateurConnectionRead.getSolde());
	}

	@Test
	public void readUtilisateur_whenUtilisateurNotExist() {
		// ACT & ASSERT
		assertNull(utilisateurRepositoryImplUnderTest.read("UtilisateurNotExist"));
	}

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

}
