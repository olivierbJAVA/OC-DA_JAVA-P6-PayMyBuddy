package com.paymybuddy.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entity.Utilisateur;

/**
 * Class managing the data persistence for the user using JPA implementation.
 */
public class UtilisateurRepositoryJpaImpl implements IUtilisateurRepository {

	private static final Logger logger = LoggerFactory.getLogger(UtilisateurRepositoryJpaImpl.class);

	private EntityManagerFactory entityManagerFactory = null;

	public UtilisateurRepositoryJpaImpl(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	/**
	 * Add a user in the repository.
	 * 
	 * @param Utilisateur The user to add
	 */
	@Override
	public void create(Utilisateur utilisateur) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();

		try {
			entityManager.persist(utilisateur);

			transaction.commit();

			logger.info("Utilisateur sucessfully created.");

		} catch (Exception e) {

			transaction.rollback();

			logger.error("Error in Utilisateur creation", e);

		} finally {

			entityManager.close();

		}

	}

	/**
	 * Update a user in the repository.
	 * 
	 * @param Utilisateur The user to update
	 */
	@Override
	public void update(Utilisateur utilisateur) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();

		try {
			entityManager.merge(utilisateur);

			transaction.commit();

			logger.info("Utilisateur sucessfully updated.");

		} catch (Exception e) {

			transaction.rollback();

			logger.error("Error in Utilisateur update.", e);

		} finally {

			entityManager.close();

		}

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

		Utilisateur utilisateur = null;

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();

		try {
			utilisateur = entityManager.find(Utilisateur.class, email);

			transaction.commit();

			logger.info("Utilisateur with email {} sucessfully read.", email);

			return utilisateur;

		} catch (Exception e) {

			transaction.rollback();

			logger.error("Error in Utilisateur with email {} read.", e);

			return null;

		} finally {

			entityManager.close();

		}

	}

	/**
	 * Delete a user from the repository.
	 * 
	 * @param email The email of the user to delete
	 */
	@Override
	public void delete(String email) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();

		try {
			Utilisateur utilisateurToDelete = entityManager.find(Utilisateur.class, email);

			entityManager.remove(utilisateurToDelete);

			transaction.commit();

			logger.info("Utilisateur with email {} sucessfully deleted.", email);

		} catch (Exception e) {

			transaction.rollback();

			logger.error("Error in Utilisateur with email {} deletion", email);

		} finally {

			entityManager.close();

		}

	}

	/**
	 * Add a connection in the repository.
	 * 
	 * @param utilisateur The user for which to add a connection
	 * 
	 * @param connection  The connection to be added to the user
	 */
	@Override
	public void addConnection(Utilisateur utilisateur, Utilisateur connection) {
		update(utilisateur);
	}
}