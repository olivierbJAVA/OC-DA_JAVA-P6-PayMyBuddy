package com.paymybuddy.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.repository.CompteRepositoryJpaTxHibernateImpl;
import com.paymybuddy.repository.ICompteRepository;
import com.paymybuddy.repository.ITransactionRepository;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repository.TransactionRepositoryJpaTxHibernateImpl;
import com.paymybuddy.repository.UtilisateurRepositoryJpaTxHibernateImpl;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

/**
 * Class Factory in charge of construction and reset of Utilisateur,
 * Transaction and Compte repository.
 */
public class RepositoryFactory {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryFactory.class);

	private static ITransactionRepository transactionRepository = null;

	private static IUtilisateurRepository utilisateurRepository = null;

	private static ICompteRepository compteRepository = null;
	
	private RepositoryFactory() {
	}
	
	/**
	 * Create a Transaction repository (JPA persistence and Tx managed by
	 * Hibernate).
	 * 
	 * @param repositoryManger The repositoryManger used to manage tx
	 * 
	 * @return The Transaction repository created
	 */
	public static ITransactionRepository getTransactionRepository(RepositoryTxManagerHibernate repositoryManger) {

		if (transactionRepository == null) {

			transactionRepository = new TransactionRepositoryJpaTxHibernateImpl(repositoryManger);

			logger.info("Factory : Creation JPA persistence Transaction Repository with Hibernate Tx management : OK");
		}

		return transactionRepository;
	}

	/**
	 * Reset the Transaction repository.
	 */
	public static void resetTransactionRepository() {

		transactionRepository = null;
		
		logger.info("Factory : Reset JPA persistence Transaction Repository with Hibernate Tx management : OK");

	}

	/**
	 * Create a Utilisateur repository (JPA persistence and Tx managed by
	 * Hibernate).
	 * 
	 * @param repositoryManger The repositoryManger used to manage tx
	 * 
	 * @return The Utilisateur repository created
	 */
	public static IUtilisateurRepository getUtilisateurRepository(RepositoryTxManagerHibernate repositoryManger) {

		if (utilisateurRepository == null) {

			utilisateurRepository = new UtilisateurRepositoryJpaTxHibernateImpl(repositoryManger);

			logger.info("Factory : Creation JPA persistence Utilisateur Repository with Hibernate Tx management : OK");
		}

		return utilisateurRepository;
	}

	/**
	 * Reset the Utilisateur repository.
	 */
	public static void resetUtilisateurRepository() {

		utilisateurRepository = null;
		
		logger.info("Factory : Reset JPA persistence Utilisateur Repository with Hibernate Tx management : OK");

	}
	
	/**
	 * Create a Compte repository (JPA persistence and Tx managed by
	 * Hibernate).
	 * 
	 * @param repositoryManger The repositoryManger used to manage tx
	 * 
	 * @return The Compte repository created
	 */
	public static ICompteRepository getCompteRepository(RepositoryTxManagerHibernate repositoryManger) {

		if (compteRepository == null) {

			compteRepository = new CompteRepositoryJpaTxHibernateImpl(repositoryManger);

			logger.info("Factory : Creation JPA persistence Compte Repository with Hibernate Tx management : OK");
		}

		return compteRepository;
	}

	/**
	 * Reset the Compte repository.
	 */
	public static void resetCompteRepository() {

		compteRepository = null;
		
		logger.info("Factory : Reset JPA persistence Compte Repository with Hibernate Tx management : OK");

	}
	
}
