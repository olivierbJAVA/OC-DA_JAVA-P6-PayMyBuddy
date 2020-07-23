package com.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.Utilisateur;
import com.paymybuddy.factory.ServiceFactory;
import com.paymybuddy.repository.ITransactionRepository;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

/**
 * Class including unit tests for the TransactionTxHibernateService Class.
 */
@ExtendWith(MockitoExtension.class)
public class TransactionTxHibernateServiceTest {

	@Mock
	private RepositoryTxManagerHibernate repositoryTxManagerMock;

	@Mock
	private IUtilisateurRepository utilisateurRepositoryMock;

	@Mock
	private ITransactionRepository transactionRepositoryMock;

	private TransactionTxHibernateService transactionTxHibernateServiceUnderTest;

	@BeforeEach
	private void setUpPerTest() {
		ServiceFactory.resetTransactionService();
		transactionTxHibernateServiceUnderTest = ServiceFactory.getTransactionService(repositoryTxManagerMock,
				utilisateurRepositoryMock, transactionRepositoryMock);
	}

	@Test
	public void getTransactionsWhenUtilisateurExist() {
		// ARRANGE
		Utilisateur initiateur = new Utilisateur();
		initiateur.setEmail("abc@test.com");
		initiateur.setPassword("abc");
		initiateur.setSolde(0d);

		Utilisateur contrepartie1 = new Utilisateur();
		contrepartie1.setEmail("def@test.com");
		contrepartie1.setPassword("def");
		contrepartie1.setSolde(0d);

		Utilisateur contrepartie2 = new Utilisateur();
		contrepartie2.setEmail("ghi@test.com");
		contrepartie2.setPassword("ghi");
		contrepartie2.setSolde(0d);

		Transaction transactionToGet1 = new Transaction();
		transactionToGet1.setInitiateur(initiateur);
		transactionToGet1.setContrepartie(contrepartie1);
		transactionToGet1.setMontant(1d);

		Transaction transactionToGet2 = new Transaction();
		transactionToGet2.setInitiateur(initiateur);
		transactionToGet2.setContrepartie(contrepartie1);
		transactionToGet2.setMontant(2d);

		Transaction transactionToGet3 = new Transaction();
		transactionToGet3.setInitiateur(initiateur);
		transactionToGet3.setContrepartie(contrepartie2);
		transactionToGet3.setMontant(3d);

		when(utilisateurRepositoryMock.read("abc@test.com")).thenReturn(initiateur);

		List<Transaction> transactionsToReturn = new ArrayList<>();
		// Transactions are returned with DESC order
		transactionsToReturn.add(transactionToGet3);
		transactionsToReturn.add(transactionToGet2);
		transactionsToReturn.add(transactionToGet1);

		when(transactionRepositoryMock.getTransactions("abc@test.com")).thenReturn(transactionsToReturn);

		List<Transaction> transactionsGet = new ArrayList<>();

		when(repositoryTxManagerMock.openCurrentSessionWithTx()).thenReturn(null);

		doNothing().when(repositoryTxManagerMock).commitTx();

		// ACT
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

		verify(transactionRepositoryMock, times(1)).getTransactions(initiateur.getEmail());
	}

	@Test
	public void getTransactionsWhenUtilisateurNotExist() {
		// ARRANGE
		when(utilisateurRepositoryMock.read("UtilisateurNotExist")).thenReturn(null);

		when(repositoryTxManagerMock.openCurrentSessionWithTx()).thenReturn(null);

		List<Transaction> transactionsGet = new ArrayList<>();

		// ACT
		transactionsGet = transactionTxHibernateServiceUnderTest.getTransactions("UtilisateurNotExist");

		// ASSERT
		assertTrue(transactionsGet.isEmpty());
		verify(transactionRepositoryMock, never()).getTransactions(anyString());
	}

	@Test
	public void makeATransactionWhenAmountIsPositiveAndInitiateurAndContrepartieExistAndAreConnectedAndSoldeSufficient() {
		// ARRANGE
		Utilisateur initiateur = new Utilisateur();
		initiateur.setEmail("abc@test.com");
		initiateur.setPassword("abc");
		initiateur.setSolde(123d);

		Utilisateur contrepartie = new Utilisateur();
		contrepartie.setEmail("def@test.com");
		contrepartie.setPassword("def");
		contrepartie.setSolde(0d);

		Set<Utilisateur> connections = new HashSet<>();
		connections.add(contrepartie);
		initiateur.setConnection(connections);

		Transaction transaction = new Transaction();
		transaction.setInitiateur(initiateur);
		transaction.setContrepartie(contrepartie);
		transaction.setMontant(10d);
		transaction.setCommentaire("Transaction test");
		
		doReturn(initiateur).when(utilisateurRepositoryMock).read("abc@test.com");

		doReturn(contrepartie).when(utilisateurRepositoryMock).read("def@test.com");

		doNothing().when(utilisateurRepositoryMock).update(initiateur);

		doNothing().when(utilisateurRepositoryMock).update(contrepartie);

		when(transactionRepositoryMock.create(transaction)).thenReturn(transaction);

		when(repositoryTxManagerMock.openCurrentSessionWithTx()).thenReturn(null);

		doNothing().when(repositoryTxManagerMock).commitTx();

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction("abc@test.com", "def@test.com", 10d,
				"Transaction test");

		// ASSERT
		assertTrue(result);
		verify(utilisateurRepositoryMock, times(2)).update(any(Utilisateur.class));
		verify(transactionRepositoryMock, times(1)).create(transaction);
	}

	@Test
	public void makeATransactionWhenAmountIsNegative() {
		// ARRANGE

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction("abc@test.com", "def@test.com", -10d,
				"Transaction test");

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).update(any(Utilisateur.class));
		verify(transactionRepositoryMock, never()).create(any(Transaction.class));
	}

	@Test
	public void makeATransactionWhenInitiateurNotExist() {
		// ARRANGE
		doReturn(null).when(utilisateurRepositoryMock).read("UtilisateurNotExist");

		Utilisateur contrepartie = new Utilisateur();
		contrepartie.setEmail("def@test.com");
		contrepartie.setPassword("def");
		contrepartie.setSolde(0d);

		doReturn(contrepartie).when(utilisateurRepositoryMock).read("def@test.com");

		when(repositoryTxManagerMock.openCurrentSessionWithTx()).thenReturn(null);

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction("UtilisateurNotExist", "def@test.com",
				10d, "Transaction test");

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).update(any(Utilisateur.class));
		verify(transactionRepositoryMock, never()).create(any(Transaction.class));
	}

	@Test
	public void makeATransactionWhenInitiateurExistAndContrepartieNotExist() {
		// ARRANGE
		Utilisateur initiateur = new Utilisateur();
		initiateur.setEmail("abc@test.com");
		initiateur.setPassword("abc");
		initiateur.setSolde(123d);

		doReturn(initiateur).when(utilisateurRepositoryMock).read("abc@test.com");

		doReturn(null).when(utilisateurRepositoryMock).read("ContrepartieNotExist");

		when(repositoryTxManagerMock.openCurrentSessionWithTx()).thenReturn(null);

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction("abc@test.com", "ContrepartieNotExist",
				10d, "Transaction test");

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).update(any(Utilisateur.class));
		verify(transactionRepositoryMock, never()).create(any(Transaction.class));
	}

	@Test
	public void makeATransactionWhenInitiateurAndContrepartieExistAndAreNotConnected() {
		// ARRANGE
		Utilisateur initiateur = new Utilisateur();
		initiateur.setEmail("abc@test.com");
		initiateur.setPassword("abc");
		initiateur.setSolde(123d);

		Utilisateur contrepartie = new Utilisateur();
		contrepartie.setEmail("def@test.com");
		contrepartie.setPassword("def");
		contrepartie.setSolde(0d);

		doReturn(initiateur).when(utilisateurRepositoryMock).read("abc@test.com");

		doReturn(contrepartie).when(utilisateurRepositoryMock).read("def@test.com");

		when(repositoryTxManagerMock.openCurrentSessionWithTx()).thenReturn(null);

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction("abc@test.com", "def@test.com", 10d,
				"Transaction test");

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).update(any(Utilisateur.class));
		verify(transactionRepositoryMock, never()).create(any(Transaction.class));
	}

	@Test
	public void makeATransactionWhenInitiateurAndContrepartieExistAndAreConnectedAndSoldeNotSufficient() {
		// ARRANGE
		Utilisateur initiateur = new Utilisateur();
		initiateur.setEmail("abc@test.com");
		initiateur.setPassword("abc");
		initiateur.setSolde(123d);

		Utilisateur contrepartie = new Utilisateur();
		contrepartie.setEmail("def@test.com");
		contrepartie.setPassword("def");
		contrepartie.setSolde(0d);

		Set<Utilisateur> connections = new HashSet<>();
		connections.add(contrepartie);
		initiateur.setConnection(connections);

		doReturn(initiateur).when(utilisateurRepositoryMock).read("abc@test.com");

		doReturn(contrepartie).when(utilisateurRepositoryMock).read("def@test.com");

		when(repositoryTxManagerMock.openCurrentSessionWithTx()).thenReturn(null);

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction("abc@test.com", "def@test.com", 1000d,
				"Transaction test");

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).update(any(Utilisateur.class));
		verify(transactionRepositoryMock, never()).create(any(Transaction.class));
	}

	@Test
	public void makeATransactionWhenInitiateurAndContrepartieAreSame() {
		// ARRANGE

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction("initiateurSameAsContrepartie",
				"initiateurSameAsContrepartie", 10d, "Transaction test");

		// ASSERT
		assertFalse(result);
		verify(utilisateurRepositoryMock, never()).update(any(Utilisateur.class));
		verify(transactionRepositoryMock, never()).create(any(Transaction.class));
	}

}
