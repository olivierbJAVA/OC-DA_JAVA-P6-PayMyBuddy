package com.paymybuddy.repository;

import java.util.List;

import javax.persistence.TypedQuery;

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
	
	/**
	 * Return all accounts for the user having this email.
	 * 
	 * @param emailUtilisateur The email of the user to get accounts
	 * 
	 * @return The list of all accounts for the user
	 */
	@Override
	public List<Compte> getComptes(String emailUtilisateur) {
		String REQUEST_COMPTES = "SELECT c FROM Compte c WHERE c.utilisateur.email IN ( SELECT email FROM Utilisateur WHERE email = :email )";

		TypedQuery<Compte> query = repositoryTxManager.getCurrentSession().createQuery(REQUEST_COMPTES,
				Compte.class);

		return query.setParameter("email", emailUtilisateur).getResultList();
	}

	/**
	 * Return the paymybuddy account for the user having this email.
	 * 
	 * @param emailUtilisateur The email of the user to get paymybuddy account
	 * 
	 * @return The paymybuddy account for the user
	 */
	@Override
	public Compte getPayMyBuddyCompte(String emailUtilisateur) {
		String REQUEST_COMPTE_PMB = "SELECT c FROM Compte c WHERE c.utilisateur.email IN ( SELECT email FROM Utilisateur WHERE email = :email AND c.type='paymybuddy')";

		TypedQuery<Compte> query = repositoryTxManager.getCurrentSession().createQuery(REQUEST_COMPTE_PMB,
				Compte.class);

		return query.setParameter("email", emailUtilisateur).getSingleResult();
	}
	
}
