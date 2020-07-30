package com.paymybuddy.repository;

import java.util.List;

import com.paymybuddy.entity.Compte;

/**
 * Interface to implement for managing the data persistence for the account.
 */
public interface ICompteRepository {

	/**
	 * Add an account in the repository.
	 * 
	 * @param Compte The account to add
	 */
	public void create(Compte compte);

	/**
	 * Update an account in the repository.
	 * 
	 * @param Compte The account to update
	 */
	public void update(Compte compte);

	/**
	 * Read an account from the repository.
	 * 
	 * @param numero The number of the account to read
	 * 
	 * @return The account read
	 */
	public Compte read(String numero);

	/**
	 * Delete an account from the repository.
	 * 
	 * @param numero The number of the account to delete
	 */
	public void delete(String numero);

	/**
	 * Return all accounts for the user having this email.
	 * 
	 * @param emailUtilisateur The email of the user to get accounts
	 * 
	 * @return The list of all accounts for the user
	 */
	public List<Compte> getComptes(String emailUtilisateur);

	/**
	 * Return the paymybuddy account for the user having this email.
	 * 
	 * @param emailUtilisateur The email of the user to get paymybuddy account
	 * 
	 * @return The paymybuddy account for the user
	 */
	public Compte getPayMyBuddyCompte(String emailUtilisateur);
}
