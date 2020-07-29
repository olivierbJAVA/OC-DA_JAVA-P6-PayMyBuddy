package com.paymybuddy.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entity.Compte;
import com.paymybuddy.entity.Utilisateur;
import com.paymybuddy.repository.ICompteRepository;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

/**
 * Class managing the services related to users using Hibernate Tx management.
 */
public class UtilisateurTxHibernateService {

	private static final Logger logger = LoggerFactory.getLogger(UtilisateurTxHibernateService.class);

	private RepositoryTxManagerHibernate repositoryTxManager;

	private IUtilisateurRepository utilisateurRepository;

	private ICompteRepository compteRepository;

	public UtilisateurTxHibernateService(RepositoryTxManagerHibernate repositoryTxManager,
			IUtilisateurRepository utilisateurRepository, ICompteRepository compteRepository) {
		super();
		this.repositoryTxManager = repositoryTxManager;
		this.utilisateurRepository = utilisateurRepository;
		this.compteRepository = compteRepository;
	}

	/**
	 * Method managing the registration of a user to the application.
	 * 
	 * @param utilisateurEmail The email of the user to register
	 * 
	 * @param password         The password of the user to register
	 * 
	 * @return True if the registration has been successfully executed, false if it
	 *         has failed
	 */
	public boolean registerToApplication(String utilisateurEmail, String password) {

		boolean utilisateurRegistered = false;

		try {
			repositoryTxManager.openCurrentSessionWithTx();

			// We check that the utilisateur with this email address is not already
			// registered in the application
			if (utilisateurRepository.read(utilisateurEmail) != null) {
				logger.error("Registration : Utilisateur {} already exist", utilisateurEmail);

			} else {
				// We create the utilisateur and save it in the database in order to register it
				// in the application
				Utilisateur utilisateurToCreate = new Utilisateur();
				utilisateurToCreate.setEmail(utilisateurEmail);
				utilisateurToCreate.setPassword(password);
				utilisateurToCreate.setSolde(0d);
				/*
				 * Set<Utilisateur> connection = new HashSet<>();
				 * connection.add(utilisateurToCreate);
				 * utilisateurToCreate.setConnection(connection);
				 */
				utilisateurRepository.create(utilisateurToCreate);

				// We create the PayMyBuddy account for the utilisateur
				Compte paymybuddyAccount = new Compte();
				paymybuddyAccount.setNumero(utilisateurEmail + "_PMB");
				paymybuddyAccount.setBanque("PayMyBuddy");
				paymybuddyAccount.setType("paymybuddy");
				paymybuddyAccount.setUtilisateur(utilisateurToCreate);

				compteRepository.create(paymybuddyAccount);
				/*
				 * Set<Compte> utilisateurComptes = new HashSet<>();
				 * utilisateurComptes.add(paymybuddyAccount);
				 */

				// We add the PayMyBuddy account to the utilisateur list of accounts
				utilisateurRepository.addCompte(utilisateurToCreate, paymybuddyAccount);

				// utilisateurRepository.addConnection(utilisateurToCreate,
				// utilisateurToCreate);

				repositoryTxManager.commitTx();

				addConnection(utilisateurToCreate.getEmail(), utilisateurToCreate.getEmail());

				logger.info("Registration : Utilisateur {} registered", utilisateurEmail);

				utilisateurRegistered = true;
			}

		} catch (Exception e) {
			logger.error("Registration : Error in Utilisateur {} registration", utilisateurEmail);

			repositoryTxManager.rollbackTx();
		} finally {

			repositoryTxManager.closeCurrentSession();
		}

		return utilisateurRegistered;
	}

	/**
	 * Method managing the connection of a user to the application.
	 * 
	 * @param utilisateurEmail The email of the user to connect
	 * 
	 * @param password         The password of the user to connect
	 * 
	 * @return True if the connection has been successfully executed, false if it
	 *         has failed
	 */
	public boolean connectToApplication(String utilisateurEmail, String password) {

		boolean utilisateurConnected = false;

		try {
			repositoryTxManager.openCurrentSessionWithTx();

			// We check that the utilisateur with this email address is registered in the
			// application
			if (utilisateurRepository.read(utilisateurEmail) == null) {
				logger.error("Connection : Utilisateur {} does not exist", utilisateurEmail);

			// We check that the password is correct
			} else if (!utilisateurRepository.read(utilisateurEmail).getPassword().equals(password)) {
				logger.error("Connection : Utilisateur {} wrong password", utilisateurEmail);

			// If all is ok then the utilisateur is connected to the application
			} else {
				repositoryTxManager.commitTx();

				logger.info("Connection : Utilisateur {} connected", utilisateurEmail);

				utilisateurConnected = true;
			}
		} catch (Exception e) {
			logger.error("Connection : Error in Utilisateur {} connection", utilisateurEmail);

			repositoryTxManager.rollbackTx();
		} finally {

			repositoryTxManager.closeCurrentSession();
		}

		return utilisateurConnected;

	}

	/**
	 * Method managing the addition by a user of a new connection.
	 * 
	 * @param utilisateurEmail The email of the user for which to add a new
	 *                         connection
	 * 
	 * @param connectionEmail  The email of the the connection to add
	 * 
	 * @return True if the connection add has been successfully executed, false if
	 *         it has failed
	 */
	public boolean addConnection(String utilisateurEmail, String connectionEmail) {

		boolean connectionAdded = false;

		try {
			repositoryTxManager.openCurrentSessionWithTx();

			Utilisateur utilisateurToAddConnection = utilisateurRepository.read(utilisateurEmail);
			Utilisateur newConnection = utilisateurRepository.read(connectionEmail);

			// We check that the utilisateur to which add a connection is registered in the
			// application
			if (utilisateurToAddConnection == null) {
				logger.error("Add a connection : Utilisateur {} does not exist", utilisateurEmail);

			// We check that the new connection is registered in the application
			} else if (newConnection == null) {
				logger.error("Add a connection : Connection {} does not exist", connectionEmail);

			} else {
				Set<Utilisateur> utilisateurConnections = utilisateurToAddConnection.getConnection();

				// We check that the utilisateur and the new connection are not already
				// connected
				if (utilisateurConnections != null && utilisateurConnections.contains(newConnection)) {
					logger.error("Add a conection : Utilisateur {} has already Connection {}", utilisateurEmail,
							connectionEmail);
				// If all is ok, then we add the new connection to the utilisateur :
				} else {
					if (utilisateurConnections == null) {
						utilisateurConnections = new HashSet<>();
					}
					utilisateurConnections.add(newConnection);
					utilisateurToAddConnection.setConnection(utilisateurConnections);
					utilisateurRepository.addConnection(utilisateurToAddConnection, newConnection);

					repositoryTxManager.commitTx();

					logger.info("Add a conection : Utilisateur {} Connection {} added", utilisateurEmail,
							connectionEmail);

					connectionAdded = true;
				}
			}
		} catch (Exception e) {
			logger.error("Add a connection : Error in Utilisateur {} add connection", utilisateurEmail);

			repositoryTxManager.rollbackTx();
		} finally {

			repositoryTxManager.closeCurrentSession();
		}

		return connectionAdded;
	}

	/**
	 * Method managing the addition by a user of a new bank account.
	 * 
	 * @param utilisateurEmail The email of the user for which to add a new bank
	 *                         account
	 * 
	 * @param numeroCompte     The number of the bank account to add
	 * 
	 * @param banque           The bank of the bank account to add
	 * 
	 * @return True if the bank account add has been successfully executed, false if
	 *         it has failed
	 */
	public boolean addCompte(String utilisateurEmail, String numeroCompte, String banque) {

		boolean compteAdded = false;

		try {
			repositoryTxManager.openCurrentSessionWithTx();

			Utilisateur utilisateurToAddCompte = utilisateurRepository.read(utilisateurEmail);

			// We check that the utilisateur to which add the bank account is registered in
			// the application
			if (utilisateurToAddCompte == null) {
				logger.error("Add a bank account : Utilisateur {} does not exist", utilisateurEmail);

			} else {
				// We create the bank account to add
				Compte bankAccountToAdd = new Compte();
				bankAccountToAdd.setNumero(numeroCompte);
				bankAccountToAdd.setBanque(banque);
				bankAccountToAdd.setType("bancaire");
				bankAccountToAdd.setUtilisateur(utilisateurToAddCompte);

				// We check that the utilisateur has not already the account
				List<Compte> utilisateurComptes = compteRepository.getComptes(utilisateurEmail);
				if (utilisateurComptes.contains(bankAccountToAdd)) {
					logger.error("Add a bank account : Utilisateur {} has already Compte {}", utilisateurEmail,
							numeroCompte);
				} else {

					// We add the PayMyBuddy account to the utilisateur list of accounts
					compteRepository.create(bankAccountToAdd);
					utilisateurRepository.addCompte(utilisateurToAddCompte, bankAccountToAdd);

					repositoryTxManager.commitTx();

					logger.info("Add a bank account : Utilisateur {} Compte {} added", utilisateurEmail, numeroCompte);

					compteAdded = true;
				}
			}
		} catch (Exception e) {
			logger.error("Add a bank account : Error in Utilisateur {} add bank account", utilisateurEmail);

			repositoryTxManager.rollbackTx();
		} finally {

			repositoryTxManager.closeCurrentSession();
		}

		return compteAdded;
	}

}
