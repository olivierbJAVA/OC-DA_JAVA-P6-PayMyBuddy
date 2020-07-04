package com.paymybuddy.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entity.Transaction;

/**
 * Class managing the data persistence for the financial transactions using JPA
 * implementation.
 */
public class TransactionRepositoryJpaImpl implements ITransactionRepository {

	private static final Logger logger = LoggerFactory.getLogger(TransactionRepositoryJpaImpl.class);

	private EntityManagerFactory entityManagerFactory = null;

	public TransactionRepositoryJpaImpl(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	/**
	 * Add a financial transaction in the repository.
	 * 
	 * @param Transaction The financial transaction to add
	 * 
	 * @return The financial transaction added
	 */
	@Override
	public Transaction create(Transaction transactionToInsert) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();

		try {
			entityManager.persist(transactionToInsert);

			transaction.commit();

			logger.info("Transaction sucessfully created.");

			return transactionToInsert;

		} catch (Exception e) {

			transaction.rollback();

			logger.error("Error in Transaction creation.", e);

			return null;

		} finally {

			entityManager.close();

		}

	}

	/**
	 * Update a financial transaction in the repository.
	 * 
	 * @param Transaction The financial transaction to update
	 */
	@Override
	public void update(Transaction transactionToUpdate) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();

		try {
			entityManager.merge(transactionToUpdate);

			transaction.commit();

			logger.info("Transaction sucessfully updated.");

		} catch (Exception e) {

			transaction.rollback();

			logger.error("Error in Transaction update.", e);

		} finally {

			entityManager.close();

		}

	}

	/**
	 * Read a financial transaction from the repository.
	 * 
	 * @param idTransaction The id of the financial transaction to read
	 * 
	 * @return The financial transaction read
	 */
	@Override
	public Transaction read(long idTransaction) {

		Transaction transactionRead = null;

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();

		try {
			transactionRead = entityManager.find(Transaction.class, idTransaction);

			transaction.commit();

			logger.info("Transaction with id {} sucessfully read.", idTransaction);

			return transactionRead;

		} catch (Exception e) {

			transaction.rollback();

			logger.error("Error in Transaction read.", e);

			return null;

		} finally {

			entityManager.close();

		}

	}

	/**
	 * Delete a financial transaction from the repository.
	 * 
	 * @param idTransaction The id of the financial transaction to delete
	 */
	@Override
	public void delete(long idTransaction) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();

		try {
			Transaction transactionToDelete = entityManager.find(Transaction.class, idTransaction);

			entityManager.remove(transactionToDelete);

			transaction.commit();

			logger.info("Transaction with id {} sucessfully deleted.", idTransaction);

		} catch (Exception e) {

			transaction.rollback();

			logger.error("Error in Transaction with id {} deletion.", idTransaction);

		} finally {

			entityManager.close();

		}
	}

	/**
	 * Return all financial transactions performed by the user having this email.
	 * 
	 * @param email The email of the user to get financial transactions
	 * 
	 * @return The list of all financial transactions for the user
	 */
	@Override
	public List<Transaction> getTransactions(String emailUtilisateur) {

		String REQUEST_TRANSACTIONS = "SELECT t FROM Transaction t WHERE t.initiateur.email IN ( SELECT email FROM Utilisateur WHERE email = :email ) ORDER by t.id DESC";

		List<Transaction> transactions = new ArrayList<>();

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();

		try {
			TypedQuery<Transaction> query = entityManager.createQuery(REQUEST_TRANSACTIONS, Transaction.class);

			transactions = query.setParameter("email", emailUtilisateur).getResultList();

			transaction.commit();

			logger.info("Transactions sucessfully get.");

			return transactions;

		} catch (Exception e) {

			transaction.rollback();

			logger.error("Error in getting transactions.", e);

			return null;

		} finally {

			entityManager.close();

		}

	}
}
