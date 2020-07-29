package com.paymybuddy.repository;

import com.paymybuddy.entity.Compte;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

/**
 * Class managing the data persistence for account using JPA implementation for
 * persistence and Hibernate for Tx management.
 */
public class CompteRepositoryJpaTxHibernateImpl implements ICompteRepository {

	private RepositoryTxManagerHibernate repositoryTxManager = null;

	public CompteRepositoryJpaTxHibernateImpl(RepositoryTxManagerHibernate repositoryTxManager) {
		this.repositoryTxManager = repositoryTxManager;
	}

	/**
	 * Add an account in the repository.
	 * 
	 * @param Compte The account to add
	 */
	@Override
	public void create(Compte compte) {

		repositoryTxManager.getCurrentSession().persist(compte);
	}

	/**
	 * Update  an account in the repository.
	 * 
	 * @param Compte The account to update
	 */
	@Override
	public void update(Compte compte) {

		repositoryTxManager.getCurrentSession().merge(compte);
	}

	/**
	 * Read an account from the repository.
	 * 
	 * @param numero The number of the account to read
	 * 
	 * @return The account read
	 */
	@Override
	public Compte read(String numero) {

		return repositoryTxManager.getCurrentSession().find(Compte.class, numero);
	}

	/**
	 * Delete an account from the repository.
	 * 
	 * @param numero The number of the account to delete
	 */
	@Override
	public void delete(String numero) {

		Compte compte = repositoryTxManager.getCurrentSession().find(Compte.class, numero);

		repositoryTxManager.getCurrentSession().remove(compte);
	}

}
