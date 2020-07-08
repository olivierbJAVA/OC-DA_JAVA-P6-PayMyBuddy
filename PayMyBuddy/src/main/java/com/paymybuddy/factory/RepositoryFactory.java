package com.paymybuddy.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.repository.ITransactionRepository;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repository.TransactionRepositoryJpaTxHibernateImpl;
import com.paymybuddy.repository.UtilisateurRepositoryJpaTxHibernateImpl;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

/**
 * Class Factory in charge of the construction of Utilisateur repository and
 * Transaction repository.
 */
public class RepositoryFactory {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryFactory.class);

	private static ITransactionRepository transactionRepository = null;

	private static IUtilisateurRepository utilisateurRepository = null;

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
}
