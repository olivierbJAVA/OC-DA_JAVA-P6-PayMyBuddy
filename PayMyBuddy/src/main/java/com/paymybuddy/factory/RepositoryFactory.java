package com.paymybuddy.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.configuration.RepositoryJdbcConfiguration;
import com.paymybuddy.repository.ITransactionRepository;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repository.TransactionRepositoryJdbcImpl;
import com.paymybuddy.repository.UtilisateurRepositoryJdbcImpl;

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

		// We create an instance of the UtilisateurRepositoryJdbcImpl only if it does not
		// already exist
		if (utilisateurRepository == null) {

			RepositoryJdbcConfiguration repositoryConfiguration = RepositoryJdbcConfiguration
					.getRepositoryConfiguration(properties);

			utilisateurRepository = new UtilisateurRepositoryJdbcImpl(repositoryConfiguration);

		}
		
		logger.info("UtilisateurRepositoryJdbcImpl sucessfully created.");
		
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

		// We create an instance of the TransactionRepositoryJdbcImpl only if it does not
		// already exist
		if (transactionRepository == null) {

			RepositoryJdbcConfiguration repositoryConfiguration = RepositoryJdbcConfiguration
					.getRepositoryConfiguration(properties);

			transactionRepository = new TransactionRepositoryJdbcImpl(repositoryConfiguration);

		}
		
		logger.info("TransactionRepositoryJdbcImpl sucessfully created.");
		
		return transactionRepository;
	}

}
