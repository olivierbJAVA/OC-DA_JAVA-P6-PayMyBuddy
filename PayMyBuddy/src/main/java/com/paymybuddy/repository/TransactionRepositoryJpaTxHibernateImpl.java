package com.paymybuddy.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

/**
 * Class managing the data persistence for financial transaction using JPA
 * implementation for persistence and Hibernate for Tx management.
 */
public class TransactionRepositoryJpaTxHibernateImpl implements ITransactionRepository {

	private static final Logger logger = LoggerFactory.getLogger(TransactionRepositoryJpaTxHibernateImpl.class);

	private RepositoryTxManagerHibernate repositoryTxManager = null;

	public TransactionRepositoryJpaTxHibernateImpl(RepositoryTxManagerHibernate repositoryTxManager) {
		this.repositoryTxManager = repositoryTxManager;
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

		repositoryTxManager.getCurrentSession().persist(transactionToInsert);
		
		Transaction transactionCreated = repositoryTxManager.getCurrentSession().find(Transaction.class,
				transactionToInsert.getIdTransaction());

		return transactionCreated;
	}

	/**
	 * Update a financial transaction in the repository.
	 * 
	 * @param Transaction The financial transaction to update
	 */
	@Override
	public void update(Transaction transactionToUpdate) {

		repositoryTxManager.getCurrentSession().merge(transactionToUpdate);
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

		Transaction transactionRead = repositoryTxManager.getCurrentSession().find(Transaction.class, idTransaction);
		
		return transactionRead;
	}

	/**
	 * Delete a financial transaction from the repository.
	 * 
	 * @param idTransaction The id of the financial transaction to delete
	 */
	@Override
	public void delete(long idTransaction) {
		
		Transaction transactionToDelete = repositoryTxManager.getCurrentSession().find(Transaction.class, idTransaction);
		
		repositoryTxManager.getCurrentSession().remove(transactionToDelete);
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

		TypedQuery<Transaction> query = repositoryTxManager.getCurrentSession().createQuery(REQUEST_TRANSACTIONS,
				Transaction.class);
		transactions = query.setParameter("email", emailUtilisateur).getResultList();

		return transactions;
	}
}
