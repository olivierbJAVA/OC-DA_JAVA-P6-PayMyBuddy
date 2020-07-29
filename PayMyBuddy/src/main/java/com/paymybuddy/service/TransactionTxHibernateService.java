package com.paymybuddy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entity.Compte;
import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.Utilisateur;
import com.paymybuddy.repository.ICompteRepository;
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

	private ICompteRepository compteRepository;

	public TransactionTxHibernateService(RepositoryTxManagerHibernate repositoryTxManager,
			IUtilisateurRepository utilisateurRepository, ITransactionRepository transactionRepository,
			ICompteRepository compteRepository) {
		this.repositoryTxManager = repositoryTxManager;
		this.utilisateurRepository = utilisateurRepository;
		this.transactionRepository = transactionRepository;
		this.compteRepository = compteRepository;
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

			// We check that the utilisateur for which we want to get the financial
			// transactions is registered in the application
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
	 * Method making an internal transfert between an initiator user and a
	 * counterpart user for a certain amount.
	 * 
	 * @param initiateurEmail   The email of the initiator of the transaction
	 * 
	 * @param contrepartieEmail The email of the counterpart of the transaction
	 * 
	 * @return True if the transfert has been successfully executed, false if it has
	 *         failed
	 */
	public boolean transfertCompteACompte(String initiateurEmail, String contrepartieEmail, Double montant,
			String commentaire) {

		boolean transfertCompteACompteDone = false;

		// We check that the initiateur and the contrepartie of the transaction are not
		// the same
		if (initiateurEmail.equals(contrepartieEmail)) {
			logger.error("Make a transaction : Utilisateur initiateur {} same as Utilisateur contrepatie {}",
					initiateurEmail, contrepartieEmail);

		// We check that the transaction amount is positive
		} else if (montant <= 0) {
			logger.error("Make a transaction : Utilisateur {}, amount = {} must be positive", initiateurEmail, montant);

		} else {

			try {
				repositoryTxManager.openCurrentSessionWithTx();

				Utilisateur utilisateur = utilisateurRepository.read(initiateurEmail);
				Utilisateur connection = utilisateurRepository.read(contrepartieEmail);

				// We check that the initiateur of the transaction is registered in the
				// application
				if (utilisateur == null) {
					logger.error("Make a transaction : Utilisateur initiateur {} does not exist", initiateurEmail);

				// We check that the contrepartie of the transaction is registered in the
				// application
				} else if (connection == null) {
					logger.error("Make a transaction : Utilisateur contrepartie {} does not exist", contrepartieEmail);

				} else {
					Set<Utilisateur> utilisateurConnections = utilisateur.getConnection();

					// We check that the initiateur of the transaction is connected to the
					// contrepartie
					if (utilisateurConnections == null || !utilisateurConnections.contains(connection)) {
						logger.error("Make a transaction : Utilisateur {} not connected with {}", initiateurEmail,
								contrepartieEmail);

					// We check that the initiateur of the transaction has a sufficient solde
					// compared to the amount of the transaction in order to perform the transaction
					} else if (utilisateur.getSolde() < montant) {
						logger.error(
								"Make a transaction : Utilisateur {} solde = {} not sufficient for transaction amount = {}",
								initiateurEmail, utilisateur.getSolde(), montant);

					// If all is ok we perform the transaction :
					} else {
						// The solde of the initiateur is decreased from the amount of the transaction
						utilisateur.setSolde(utilisateur.getSolde() - montant);

						// The solde of the connection is increased by the amount of the transaction
						// minus the commission of 0.5%
						connection.setSolde(connection.getSolde() + montant * (1 - 0.005));

						utilisateurRepository.update(utilisateur);
						utilisateurRepository.update(connection);

						// Finally we create the financial transaction in the database
						Transaction transaction = new Transaction();
						transaction.setInitiateur(utilisateur);
						transaction.setContrepartie(connection);
						transaction.setMontant(montant);
						transaction.setCommentaire(commentaire);
						transaction.setCompte_initiateur(compteRepository.getPayMyBuddyCompte(initiateurEmail));
						transaction.setCompte_contrepartie(compteRepository.getPayMyBuddyCompte(contrepartieEmail));
						transaction.setType("transfert");
						double fraisTransaction = montant * 0.005;
						transaction.setFrais(fraisTransaction);

						transactionRepository.create(transaction);

						repositoryTxManager.commitTx();

						logger.info(
								"Transaction made by Utilisateur initiateur {} to Utilisateur contrepartie {} for amount = {} : done",
								initiateurEmail, contrepartieEmail, montant);

						transfertCompteACompteDone = true;
					}
				}
			} catch (Exception e) {
				logger.error("Make a transaction by Utilisateur intitateur {} : error", initiateurEmail);

				repositoryTxManager.rollbackTx();
			} finally {

				repositoryTxManager.closeCurrentSession();
			}
		}

		return transfertCompteACompteDone;
	}

	/**
	 * Method managing the wire by a user from a bank account to the paymybuddy
	 * account for a certain amount.
	 * 
	 * @param utilisateurEmail The email of the user for which to perform the wire
	 * 
	 * @param montant          The amount of the wire
	 * 
	 * @param numeroCompte     The bank account number
	 * 
	 * @param commentaire      An optional comment
	 * 
	 * @return True if the wire has been successfully executed, false if it has
	 *         failed
	 */
	public boolean depotSurComptePaymybuddy(String utilisateurEmail, Double montant, String numeroCompte,
			String commentaire) {

		boolean depotSurComptePaymybuddyDone = false;

		// We check that the amount to be wired is positive
		if (montant <= 0) {
			logger.error("Wire to account : Utilisateur {}, amount = {} must be positive", utilisateurEmail, montant);
		} else {

			try {
				repositoryTxManager.openCurrentSessionWithTx();

				Utilisateur utilisateurToUpdate = utilisateurRepository.read(utilisateurEmail);

				// We check that the utilisateur with this email address is registered in the
				// application
				if (utilisateurToUpdate == null) {
					logger.error("Wire to account : Utilisateur {} does not exist", utilisateurEmail);
				} else {

					Compte comptePaymybuddy = compteRepository.read(utilisateurEmail + "_PMB");
					Compte compteBancaire = compteRepository.read(numeroCompte);

					// If all is ok, we update the utilisateur with a new solde being the old one
					// plus the amount wired
					Double oldSolde = utilisateurToUpdate.getSolde();
					Double newSolde = oldSolde + montant;
					utilisateurToUpdate.setSolde(newSolde);

					utilisateurRepository.update(utilisateurToUpdate);

					// Finally we create the financial transaction in the database
					Transaction transaction = new Transaction();
					transaction.setInitiateur(utilisateurToUpdate);
					transaction.setContrepartie(utilisateurToUpdate);
					transaction.setMontant(montant);
					transaction.setCommentaire(commentaire);
					transaction.setType("depot");
					transaction.setCompte_initiateur(compteBancaire);
					transaction.setCompte_contrepartie(comptePaymybuddy);

					transactionRepository.create(transaction);

					repositoryTxManager.commitTx();

					logger.info("Wire to account by Utilisateur {} for amount {} : done", utilisateurEmail, montant);

					depotSurComptePaymybuddyDone = true;
				}
			} catch (Exception e) {
				logger.error("Wire to account : Error in Utilisateur {} wire to account", utilisateurEmail);

				repositoryTxManager.rollbackTx();
			} finally {

				repositoryTxManager.closeCurrentSession();
			}
		}

		return depotSurComptePaymybuddyDone;
	}

	/**
	 * Method managing the withdrawal by a user from his paymybuddy account to a
	 * bank account for a certain amount.
	 * 
	 * @param utilisateurEmail The email of the user for which to perform the
	 *                         withdrawal
	 * 
	 * @param montant          The amount of the withdrawal
	 * 
	 * @param numeroCompte     The bank account number
	 * 
	 * @param commentaire      An optional comment
	 * 
	 * @return True if the withdrawal has been successfully executed, false if it
	 *         has failed
	 */
	public boolean virementSurCompteBancaire(String utilisateurEmail, Double montant, String numeroCompte,
			String commentaire) {

		boolean virementSurCompteBancaireDone = false;

		// We check that the amount to be withdrawn is positive
		if (montant <= 0) {
			logger.error("Withdrawal from account : Utilisateur {}, amount = {} must be positive", utilisateurEmail,
					montant);
		} else {

			try {
				repositoryTxManager.openCurrentSessionWithTx();

				Utilisateur utilisateurToUpdate = utilisateurRepository.read(utilisateurEmail);

				// We check that the utilisateur with this email address is registered in the
				// application
				if (utilisateurToUpdate == null) {
					logger.error("Withdrawal from account : Utilisateur {} does not exist", utilisateurEmail);
				} else {

					Double oldSolde = utilisateurToUpdate.getSolde();

					// We check that the solde of the Utilisateur is sufficient to perform the
					// withdrawal
					if (oldSolde < montant) {
						logger.error(
								"Withdrawal from account : Utilisateur {} solde = {} not sufficient for amount = {}",
								utilisateurEmail, oldSolde, montant);
					} else {

						Compte comptePaymybuddy = compteRepository.read(utilisateurEmail + "_PMB");
						Compte compteBancaire = compteRepository.read(numeroCompte);

						// If all is ok, we update the utilisateur with a new solde being the old one
						// minus the amount withdrawn
						Double newSolde = oldSolde - montant;

						utilisateurToUpdate.setSolde(newSolde);

						utilisateurRepository.update(utilisateurToUpdate);

						// Finally we create the financial transaction in the database
						Transaction transaction = new Transaction();
						transaction.setInitiateur(utilisateurToUpdate);
						transaction.setContrepartie(utilisateurToUpdate);
						transaction.setMontant(montant);
						transaction.setCommentaire(commentaire);
						transaction.setType("virement");
						transaction.setCompte_initiateur(compteBancaire);
						transaction.setCompte_contrepartie(comptePaymybuddy);

						transactionRepository.create(transaction);

						repositoryTxManager.commitTx();

						logger.info("Withdrawal from account by Utilisateur {} for amount = {} done", utilisateurEmail,
								montant);

						virementSurCompteBancaireDone = true;
					}
				}
			} catch (Exception e) {
				logger.error("Withdrawal from account : Error in Utilisateur {} withdrawal from account",
						utilisateurEmail);

				repositoryTxManager.rollbackTx();
			} finally {

				repositoryTxManager.closeCurrentSession();
			}

		}

		return virementSurCompteBancaireDone;
	}
}