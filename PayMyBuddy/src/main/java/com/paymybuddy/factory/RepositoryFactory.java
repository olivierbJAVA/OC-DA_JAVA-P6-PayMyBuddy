package com.paymybuddy.factory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.repository.ITransactionRepository;
import com.paymybuddy.repository.IUtilisateurRepository;


/**
 * Class Factory in charge of the construction of Utilisateur repository and
 * Transaction repository
 */
public class RepositoryFactory {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryFactory.class);

	private static IUtilisateurRepository utilisateurRepository = null;

	private static ITransactionRepository transactionRepository = null;

	/**
	 * Create a Utilisateur repository.
	 * 
	 * @param properties The path of the file containing properties for the
	 *                   repository configuration
	 * 
	 * @return The Utilisateur repository
	 */
	public static IUtilisateurRepository getUtilisateurRepository(String properties) {

		// We create an instance of the UtilisateurRepositoryJpaImpl only if it does not
		// already exist
		if (utilisateurRepository == null) {

			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(properties);

			utilisateurRepository = new UtilisateurRepositoryJpaImpl(entityManagerFactory);

		}

		logger.info("UtilisateurRepositoryJpaImpl sucessfully created.");

		return utilisateurRepository;
	}

	/**
	 * Create a Transaction repository.
	 * 
	 * @param properties The path of the file containing properties for the
	 *                   repository configuration
	 * 
	 * @return The Transaction repository
	 */
	public static ITransactionRepository getTransactionRepository(String properties) {

		// We create an instance of the TransactionRepositoryJpaImpl only if it does not
		// already exist
		if (transactionRepository == null) {

			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(properties);

			transactionRepository = new TransactionRepositoryJpaImpl(entityManagerFactory);

		}

		logger.info("TransactionRepositoryJpaImpl sucessfully created.");

		return transactionRepository;
	}

}
