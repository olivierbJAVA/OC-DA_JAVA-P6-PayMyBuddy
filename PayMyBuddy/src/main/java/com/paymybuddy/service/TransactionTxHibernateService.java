package com.paymybuddy.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.Utilisateur;
import com.paymybuddy.repository.ITransactionRepository;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

/**
 * Class managing the services related to financial transactions using Hibernate
 * Tx management.
 */
public class TransactionTxHibernateService {

	private static final Logger logger = LoggerFactory.getLogger(TransactionTxHibernateService.class);

	private RepositoryTxManagerHibernate repositoryTxManager;

	private IUtilisateurRepository utilisateurRepository;

	private ITransactionRepository transactionRepository;

	public TransactionTxHibernateService(RepositoryTxManagerHibernate repositoryTxManager,
			IUtilisateurRepository utilisateurRepository, ITransactionRepository transactionRepository) {
		this.repositoryTxManager = repositoryTxManager;
		this.utilisateurRepository = utilisateurRepository;
		this.transactionRepository = transactionRepository;
	}

	/**
	 * Return all financial transactions made by an user.
	 * 
	 * @param utilisateurEmail The email of the user
	 * 
	 * @return The financial transactions
	 */
	public List<Transaction> getTransactions(String utilisateurEmail) {

		List<Transaction> transactions = new ArrayList<>();

		try {
			repositoryTxManager.openCurrentSessionWithTx();

			if (utilisateurRepository.read(utilisateurEmail) == null) {
				logger.error("Get all transactions : Utilisateur {} does not exist", utilisateurEmail);
			} else {
				transactions = transactionRepository.getTransactions(utilisateurEmail);

				repositoryTxManager.commitTx();

				logger.info("Get all transactions for Utilisateur {} : success", utilisateurEmail);
			}
		} catch (Exception e) {
			logger.error("Get all transactions for Utilisateur {} : error", utilisateurEmail);

			repositoryTxManager.rollbackTx();
		} finally {

			repositoryTxManager.closeCurrentSession();
		}

		return transactions;
	}

	/**
	 * Method making a financial transaction between an initiator user and a
	 * counterpart user for a certain amount.
	 * 
	 * @param initiateurEmail   The email of the initiator of the transaction
	 * 
	 * @param contrepartieEmail The email of the counterpart of the transaction
	 * 
	 * @return True if the transaction has been successfully executed, false if it
	 *         has failed
	 */
	public boolean makeATransaction(String initiateurEmail, String contrepartieEmail, Double montant) {

		boolean transactionDone = false;

		try {
			repositoryTxManager.openCurrentSessionWithTx();

			Utilisateur utilisateur = utilisateurRepository.read(initiateurEmail);
			Utilisateur connection = utilisateurRepository.read(contrepartieEmail);

			if (utilisateur == null) {
				logger.error("Make a transaction : Utilisateur initiateur {} does not exist", initiateurEmail);
			} else if (connection == null) {
				logger.error("Make a transaction : Utilisateur contrepartie {} does not exist", contrepartieEmail);
			} else {

				Set<Utilisateur> utilisateurConnections = new HashSet<>();
				utilisateurConnections = utilisateur.getConnection();

				if (!utilisateurConnections.contains(connection)) {
					logger.error("Make a transaction : Utilisateur {} not connected with {}", initiateurEmail,
							contrepartieEmail);
				} else if (utilisateur.getSolde() < montant) {
					logger.error(
							"Make a transaction : Utilisateur {} solde = {} not sufficient for transaction amount = {}",
							initiateurEmail, utilisateur.getSolde(), montant);
				} else {
					utilisateur.setSolde(utilisateur.getSolde() - montant);
					connection.setSolde(connection.getSolde() + montant);
					utilisateurRepository.update(utilisateur);
					utilisateurRepository.update(connection);

					Transaction transaction = new Transaction();
					transaction.setInitiateur(utilisateur);
					transaction.setContrepartie(connection);
					transaction.setMontant(montant);

					transactionRepository.create(transaction);

					repositoryTxManager.commitTx();

					logger.info(
							"Transaction made by Utilisateur intitiateur {} to Utilisateur contrepartie {} for amount = {} : done",
							initiateurEmail, contrepartieEmail, montant);

					transactionDone = true;
				}
			}
		} catch (Exception e) {
			logger.error("Make a transaction by Utilisateur intitateur {} : error", initiateurEmail);

			repositoryTxManager.rollbackTx();
		} finally {

			repositoryTxManager.closeCurrentSession();
		}

		return transactionDone;
	}
}
