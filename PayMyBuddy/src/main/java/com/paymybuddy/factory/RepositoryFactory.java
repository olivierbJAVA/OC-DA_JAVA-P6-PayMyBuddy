package com.paymybuddy.factory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Factory in charge of the construction of Utilisateur repository and
 * Transaction repository
 */
public class RepositoryFactory {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryFactory.class);

	private static IUtilisateurRepository utilisateurRepository = null;

	private static ITransactionRepository transactionRepository = null;

	/**
	 * Create a Transaction repository.
	 * 
	 * @param repositoryName The name of the repository to create
	 * 
	 * @param properties     The path of the file containing properties for the
	 *                       repository configuration
	 * 
	 * @return The Transaction repository
	 */
	public static ITransactionRepository getTransactionRepository(String repositoryName, String properties) {

		if (repositoryName.equals("jdbc")) {

			RepositoryJdbcConfiguration repositoryConfiguration = RepositoryJdbcConfiguration
					.getRepositoryConfiguration(properties);

			transactionRepository = new TransactionRepositoryJdbcImpl(repositoryConfiguration);

			logger.info("Factory : Creation JDBC Transaction Repository OK");

		} else if (repositoryName.equals("jpa")) {

			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(properties);

			transactionRepository = new TransactionRepositoryJpaImpl(entityManagerFactory);

			logger.info("Factory : Creation JPA Transaction Repository OK");

		// If repository name is not JDBC or JPA we create a JPA repository by default
		} else {

			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(properties);

			transactionRepository = new TransactionRepositoryJpaImpl(entityManagerFactory);

			logger.info(
					"Factory : Transaction Repository requested does not exist -> Creation JPA Transaction Repository by default");

		}

		return transactionRepository;
	}

	/**
	 * Create a Utilisateur repository.
	 * 
	 * @param repositoryName The name of the repository to create
	 * 
	 * @param properties     The path of the file containing properties for the
	 *                       repository configuration
	 * 
	 * @return The Utilisateur repository
	 */
	public static IUtilisateurRepository getUtilisateurRepository(String repositoryName, String properties) {

		if (repositoryName.equals("jdbc")) {

			RepositoryJdbcConfiguration repositoryConfiguration = RepositoryJdbcConfiguration
					.getRepositoryConfiguration(properties);

			utilisateurRepository = new UtilisateurRepositoryJdbcImpl(repositoryConfiguration);

			logger.info("Factory : Creation JDBC Utilisateur Repository OK");

		}

		else if (repositoryName.equals("jpa")) {

			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(properties);

			utilisateurRepository = new UtilisateurRepositoryJpaImpl(entityManagerFactory);

			logger.info("Factory : Creation JPA Utilisateur Repository OK");

		}

		// If repository name is not JDBC or JPA we create a JPA repository by default
		else {

			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(properties);

			utilisateurRepository = new UtilisateurRepositoryJpaImpl(entityManagerFactory);

			logger.info(
					"Factory : Transaction Repository requested does not exist -> Creation JPA Utilisateur Repository by default");

		}

		return utilisateurRepository;
	}

}
