package com.paymybuddy.repository;

import java.util.List;

import com.paymybuddy.entity.Transaction;

/**
 * Interface to implement for managing the data persistence for the financial
 * transactions.
 */
public interface ITransactionRepository {

	/**
	 * Add a financial transaction in the repository.
	 * 
	 * @param Transaction The financial transaction to add
	 * 
	 * @return The financial transaction added
	 */
	public Transaction create(Transaction transaction);

	/**
	 * Update a financial transaction in the repository.
	 * 
	 * @param Transaction The financial transaction to update
	 */
	public void update(Transaction transaction);

	/**
	 * Read a financial transaction from the repository.
	 * 
	 * @param idTransaction The id of the financial transaction to read
	 * 
	 * @return The financial transaction read
	 */
	public Transaction read(long idTransaction);

	/**
	 * Delete a financial transaction from the repository.
	 * 
	 * @param idTransaction The id of the financial transaction to delete
	 */
	public void delete(long idTransaction);

	/**
	 * Return all financial transactions performed by the user having this email.
	 * 
	 * @param email The email of the user to get financial transactions
	 * 
	 * @return The list of all financial transactions for the user
	 */
	public List<Transaction> getTransactions(String email);
}
