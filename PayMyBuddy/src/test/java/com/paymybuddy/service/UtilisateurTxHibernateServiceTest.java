package com.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.entity.Utilisateur;
import com.paymybuddy.factory.ServiceFactory;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;
import com.paymybuddy.service.UtilisateurTxHibernateService;

/**
 * Class including unit tests for the TransactionTxHibernateService Class.
 */
@ExtendWith(MockitoExtension.class)
public class UtilisateurTxHibernateServiceTest {

	private static String paymybuddyPropertiesFile = "paymybuddyTest.properties";

	private RepositoryTxManagerHibernate repositoryTxManager;

	@Mock
	private IUtilisateurRepository utilisateurRepositoryMock;

	private UtilisateurTxHibernateService utilisateurTxHibernateServiceUnderTest;

	@BeforeEach
	private void setUpPerTest() {

		repositoryTxManager = RepositoryTxManagerHibernate.getRepositoryTxManagerHibernate(paymybuddyPropertiesFile);

		utilisateurTxHibernateServiceUnderTest = ServiceFactory.getUtilisateurService(repositoryTxManager,
				utilisateurRepositoryMock);
	}

	@Test
	public void registerToApplication_whenUtilisateurNotAlreadyExist() {
		// ARRANGE
		Utilisateur utilisateurToRegister = new Utilisateur();
		utilisateurToRegister.setEmail("abc@test.com");
		utilisateurToRegister.setPassword("abc");
		utilisateurToRegister.setSolde(0d);

		when(utilisateurRepositoryMock.read("abc@test.com")).thenReturn(null);

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.registerToApplication("abc@test.com", "abc");

		// ASSERT
		assertTrue(result);
		verify(utilisateurRepositoryMock, times(1)).create(utilisateurToRegister);
	}

	@Test
	public void registerToApplication_whenUtilisateurAlreadyExist() {
		// ARRANGE
		Utilisateur utilisateurToRegister = new Utilisateur();
		utilisateurToRegister.setEmail("abc@test.com");
		utilisateurToRegister.setPassword("abc");
		utilisateurToRegister.setSolde(123d);

		when(utilisateurRepositoryMock.read("abc@test.com")).thenReturn(utilisateurToRegister);

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.registerToApplication("abc@test.com", "abc");

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).create(utilisateurToRegister);
	}

	@Test
	public void connectToApplication_whenUtilisateurExistAndPasswordIsCorrect() {
		// ARRANGE
		Utilisateur utilisateurToConnect = new Utilisateur();
		utilisateurToConnect.setEmail("abc@test.com");
		utilisateurToConnect.setPassword("abc");
		utilisateurToConnect.setSolde(123d);

		when(utilisateurRepositoryMock.read("abc@test.com")).thenReturn(utilisateurToConnect);

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.connectToApplication("abc@test.com", "abc");

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

		when(utilisateurRepositoryMock.read("abc@test.com")).thenReturn(utilisateurToConnect);

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.connectToApplication("abc@test.com", "WrongPassword");

		// ASSERT
		assertFalse(result);
	}

	@Test
	public void connectToApplication_whenUtilisateurNotExist() {
		// ARRANGE
		when(utilisateurRepositoryMock.read("UtilisateurNotExist")).thenReturn(null);

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.connectToApplication("UtilisateurNotExist", "Password");

		// ASSERT
		assertFalse(result);
	}

	@Test
	public void withdrawalFromAccount_whenUtilisateurExistAndSoldeSufficientAndAmountPositive() {
		// ARRANGE
		Utilisateur utilisateurToWithdrawFromAccount = new Utilisateur();
		utilisateurToWithdrawFromAccount.setEmail("abc@test.com");
		utilisateurToWithdrawFromAccount.setPassword("abc");
		utilisateurToWithdrawFromAccount.setSolde(123d);

		when(utilisateurRepositoryMock.read("abc@test.com")).thenReturn(utilisateurToWithdrawFromAccount);

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.withdrawalFromAccount("abc@test.com", 10d);

		// ASSERT
		assertTrue(result);
		verify(utilisateurRepositoryMock, times(1)).update(utilisateurToWithdrawFromAccount);
	}

	@Test
	public void withdrawalFromAccount_whenAmountNegative() {
		// ARRANGE

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.withdrawalFromAccount("abc@test.com", -10d);

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).update(any(Utilisateur.class));
	}

	@Test
	public void withdrawalFromAccount_whenUtilisateurExistAndSoldeNotSufficient() {
		// ARRANGE
		Utilisateur utilisateurToWithdrawFromAccount = new Utilisateur();
		utilisateurToWithdrawFromAccount.setEmail("abc@test.com");
		utilisateurToWithdrawFromAccount.setPassword("abc");
		utilisateurToWithdrawFromAccount.setSolde(1d);

		when(utilisateurRepositoryMock.read("abc@test.com")).thenReturn(utilisateurToWithdrawFromAccount);

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.withdrawalFromAccount("abc@test.com", 10d);

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).update(utilisateurToWithdrawFromAccount);
	}

	@Test
	public void withdrawalFromAccount_whenUtilisateurNotExist() {
		// ARRANGE
		when(utilisateurRepositoryMock.read("abc@test.com")).thenReturn(null);

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.withdrawalFromAccount("abc@test.com", 10d);

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).update(any(Utilisateur.class));
	}

	@Test
	public void wireToAccount_whenUtilisateurExistAndAmountPositive() {
		// ARRANGE
		Utilisateur utilisateurToWireToAccount = new Utilisateur();
		utilisateurToWireToAccount.setEmail("abc@test.com");
		utilisateurToWireToAccount.setPassword("abc");
		utilisateurToWireToAccount.setSolde(123d);

		when(utilisateurRepositoryMock.read("abc@test.com")).thenReturn(utilisateurToWireToAccount);

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.wireToAccount("abc@test.com", 10d);

		// ASSERT
		assertTrue(result);
		verify(utilisateurRepositoryMock, times(1)).update(utilisateurToWireToAccount);
	}

	@Test
	public void wireToAccount_whenAmountNegative() {
		// ARRANGE

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.wireToAccount("abc@test.com", -10d);

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).update(any(Utilisateur.class));
	}

	@Test
	public void wireToAccount_whenUtilisateurNotExist() {
		// ARRANGE
		when(utilisateurRepositoryMock.read("abc@test.com")).thenReturn(null);

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.wireToAccount("abc@test.com", 10d);

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).update(any(Utilisateur.class));
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

		doReturn(utilisateurToAddConnection).when(utilisateurRepositoryMock).read("abc@test.com");

		doReturn(utilisateurNewConnection).when(utilisateurRepositoryMock).read("def@test.com");

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.addConnection("abc@test.com", "def@test.com");

		// ASSERT
		assertTrue(result);
		verify(utilisateurRepositoryMock, times(1)).addConnection(utilisateurToAddConnection, utilisateurNewConnection);
	}

	@Test
	public void addConnection_whenUtilisateurNotExist() {
		// ARRANGE
		Utilisateur utilisateurNewConnection = new Utilisateur();
		utilisateurNewConnection.setEmail("def@test.com");
		utilisateurNewConnection.setPassword("def");
		utilisateurNewConnection.setSolde(0d);

		doReturn(null).when(utilisateurRepositoryMock).read("abc@test.com");

		doReturn(utilisateurNewConnection).when(utilisateurRepositoryMock).read("def@test.com");

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.addConnection("abc@test.com", "def@test.com");

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).addConnection(any(Utilisateur.class), any(Utilisateur.class));
	}

	@Test
	public void addConnection_whenUtilisateurExistAndConnectionNotExist() {
		// ARRANGE
		Utilisateur utilisateurToAddConnection = new Utilisateur();
		utilisateurToAddConnection.setEmail("abc@test.com");
		utilisateurToAddConnection.setPassword("abc");
		utilisateurToAddConnection.setSolde(0d);

		doReturn(utilisateurToAddConnection).when(utilisateurRepositoryMock).read("abc@test.com");

		doReturn(null).when(utilisateurRepositoryMock).read("def@test.com");

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.addConnection("abc@test.com", "def@test.com");

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).addConnection(any(Utilisateur.class), any(Utilisateur.class));
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

		doReturn(utilisateurToAddConnection).when(utilisateurRepositoryMock).read("abc@test.com");

		doReturn(utilisateurNewConnection).when(utilisateurRepositoryMock).read("def@test.com");

		Set<Utilisateur> connections = new HashSet<>();
		connections.add(utilisateurNewConnection);
		utilisateurToAddConnection.setConnection(connections);

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.addConnection("abc@test.com", "def@test.com");

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).addConnection(any(Utilisateur.class), any(Utilisateur.class));
	}

	@Test
	public void addConnection_whenUtilisateurAndConnectionAreSame() {
		// ARRANGE

		// ACT
		boolean result = utilisateurTxHibernateServiceUnderTest.addConnection("utilisateurSameAsConnectionToAdd",
				"utilisateurSameAsConnectionToAdd");

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).addConnection(any(Utilisateur.class), any(Utilisateur.class));
	}
}
