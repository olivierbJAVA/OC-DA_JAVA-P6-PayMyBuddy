package com.paymybuddy.repository;

import com.paymybuddy.entity.Compte;
import com.paymybuddy.entity.Utilisateur;

/**
 * Interface to implement for managing the data persistence for the user.
 */
public interface IUtilisateurRepository {

	/**
	 * Add a user in the repository.
	 * 
	 * @param Utilisateur The user to add
	 */
	public void create(Utilisateur utilisateur);

	/**
	 * Update a user in the repository.
	 * 
	 * @param Utilisateur The user to update
	 */
	public void update(Utilisateur utilisateur);

	/**
	 * Read a user from the repository.
	 * 
	 * @param email The email of the user to read
	 * 
	 * @return The user read
	 */
	public Utilisateur read(String email);

	/**
	 * Delete a user from the repository.
	 * 
	 * @param email The email of the user to delete
	 */
	public void delete(String email);

	/**
	 * Add a connection for an user.
	 * 
	 * @param utilisateur The user for which to add a connection
	 * 
	 * @param connection  The connection to be added to the user
	 */
	public void addConnection(Utilisateur utilisateur, Utilisateur connection);
	
	/**
	 * Add an account for an user.
	 * 
	 * @param utilisateur The user for which to add an account
	 * 
	 * @param compte The account to be added to the user
	 */
	public void addCompte(Utilisateur utilisateur, Compte compte);
}

