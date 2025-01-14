package com.paymybuddy.repository;

import com.paymybuddy.entity.Compte;
import com.paymybuddy.entity.Utilisateur;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

/**
 * Class managing the data persistence for user using JPA implementation for
 * persistence and Hibernate for Tx management.
 */
public class UtilisateurRepositoryJpaTxHibernateImpl implements IUtilisateurRepository {

	private RepositoryTxManagerHibernate repositoryTxManager = null;

	public UtilisateurRepositoryJpaTxHibernateImpl(RepositoryTxManagerHibernate repositoryTxManager) {
		this.repositoryTxManager = repositoryTxManager;
	}

	/**
	 * Add a user in the repository.
	 * 
	 * @param Utilisateur The user to add
	 */
	@Override
	public void create(Utilisateur utilisateur) {

		repositoryTxManager.getCurrentSession().persist(utilisateur);
	}

	/**
	 * Update a user in the repository.
	 * 
	 * @param Utilisateur The user to update
	 */
	@Override
	public void update(Utilisateur utilisateur) {

		repositoryTxManager.getCurrentSession().merge(utilisateur);
	}

	/**
	 * Read a user from the repository.
	 * 
	 * @param email The email of the user to read
	 * 
	 * @return The user read
	 */
	@Override
	public Utilisateur read(String email) {

		return repositoryTxManager.getCurrentSession().find(Utilisateur.class, email);
	}

	/**
	 * Delete a user from the repository.
	 * 
	 * @param email The email of the user to delete
	 */
	@Override
	public void delete(String email) {

		Utilisateur utilisateur = repositoryTxManager.getCurrentSession().find(Utilisateur.class, email);

		repositoryTxManager.getCurrentSession().remove(utilisateur);
	}

	/**
	 * Add a connection for an user.
	 * 
	 * @param utilisateur The user for which to add a connection
	 * 
	 * @param connection  The connection to be added to the user
	 */
	@Override
	public void addConnection(Utilisateur utilisateur, Utilisateur connection) {
		update(utilisateur);
	}
	
	/**
	 * Add an account for an user.
	 * 
	 * @param utilisateur The user for which to add an account
	 * 
	 * @param compte The account to be added to the user
	 */
	@Override
	public void addCompte(Utilisateur utilisateur, Compte compte) {
		update(utilisateur);
	}
}
