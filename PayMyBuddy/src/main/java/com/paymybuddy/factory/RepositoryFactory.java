package com.paymybuddy.factory;

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

		if (utilisateurRepository == null) {

			RepositoryJdbcConfiguration repositoryConfiguration = RepositoryJdbcConfiguration
					.getRepositoryConfiguration(properties);

			utilisateurRepository = new UtilisateurRepositoryJdbcImpl(repositoryConfiguration);

		}
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

		if (transactionRepository == null) {

			RepositoryJdbcConfiguration repositoryConfiguration = RepositoryJdbcConfiguration
					.getRepositoryConfiguration(properties);

			transactionRepository = new TransactionRepositoryJdbcImpl(repositoryConfiguration);

		}
		return transactionRepository;
	}

}
