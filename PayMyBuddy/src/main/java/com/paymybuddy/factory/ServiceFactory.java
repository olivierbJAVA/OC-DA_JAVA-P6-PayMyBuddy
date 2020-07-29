package com.paymybuddy.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.repository.ICompteRepository;
import com.paymybuddy.repository.ITransactionRepository;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;
import com.paymybuddy.service.TransactionTxHibernateService;
import com.paymybuddy.service.UtilisateurTxHibernateService;

/**
 * Class Factory in charge of construction and reset of Utilisateur service and
 * Transaction service.
 */
public class ServiceFactory {

	private static final Logger logger = LoggerFactory.getLogger(ServiceFactory.class);

	private static TransactionTxHibernateService transactionService = null;

	private static UtilisateurTxHibernateService utilisateurService = null;

	private ServiceFactory() {
	}
	
	/**
	 * Create a Transaction service.
	 * 
	 * @param repositoryTxManager   The Tx manager to be used by the Service
	 * 
	 * @param utilisateurRepository The Utilisateur repository to be used by the
	 *                              Service
	 * 
	 * @param transactionRepository The Transaction repository to be used by the
	 *                              Service
	 * 
	 * @return The Transaction service
	 */
	public static TransactionTxHibernateService getTransactionService(RepositoryTxManagerHibernate repositoryTxManager,
			IUtilisateurRepository utilisateurRepository, ITransactionRepository transactionRepository) {

		if (transactionService == null) {

			transactionService = new TransactionTxHibernateService(repositoryTxManager, utilisateurRepository,
					transactionRepository);

			logger.info("Factory : Creation Transaction Service with Hibernate Tx management OK");
		}

		return transactionService;
	}

	/**
	 * Reset the Transaction service.
	 */
	public static void resetTransactionService() {

		transactionService = null;
		
		logger.info("Factory : Reset Transaction Service with Hibernate Tx management OK");

	}

	/**
	 * Create a Utilisateur service.
	 * 
	 * @param repositoryTxManager   The Tx manager to be used by the Service
	 * 
	 * @param utilisateurRepository The Utilisateur repository to be used by the
	 *                              Service
	 * 
	 * @return The Utilisateur service
	 */
	public static UtilisateurTxHibernateService getUtilisateurService(RepositoryTxManagerHibernate repositoryTxManager,
			IUtilisateurRepository utilisateurRepository, ICompteRepository compteRepository) {

		if (utilisateurService == null) {

			utilisateurService = new UtilisateurTxHibernateService(repositoryTxManager, utilisateurRepository, compteRepository);

			logger.info("Factory : Creation Utilisateur Service with Hibernate Tx management OK");
		}

		return utilisateurService;
	}

	/**
	 * Reset the Utilisateur service.
	 */
	public static void resetUtilisateurService() {

		utilisateurService = null;
		
		logger.info("Factory : Reset Utilisateur Service with Hibernate Tx management OK");
		
	}
}
