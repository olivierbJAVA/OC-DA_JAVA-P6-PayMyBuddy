package com.paymybuddy.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.configuration.RepositoryJdbcConfiguration;
import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;

/**
 * Class managing the data persistence for the financial transactions using JDBC implementation.
 */
public class TransactionRepositoryJdbcImpl implements ITransactionRepository {

	private static final Logger logger = LoggerFactory.getLogger(TransactionRepositoryJdbcImpl.class);

	private RepositoryJdbcConfiguration repositoryConfiguration;

	public TransactionRepositoryJdbcImpl(RepositoryJdbcConfiguration repositoryConfiguration) {
		this.repositoryConfiguration = repositoryConfiguration;
	}

	String propertiesFilePath = "paymybuddy.properties";

	IUtilisateurRepository utilisateurRepository = RepositoryFactory.getUtilisateurRepository(propertiesFilePath);

	/**
	 * Add a financial transaction in the repository.
	 * 
	 * @param Transaction The financial transaction to add
	 * 
	 * @return The financial transaction added
	 */
	@Override
	public Transaction create(Transaction transaction) {

		final String REQUEST_CREATE = "INSERT INTO transaction (initiateur_email, contrepartie_email, montant, commentaire) VALUES(?,?,?,?)";

		ResultSet rs = null;
		try (Connection postgreCon = repositoryConfiguration.getConnection();
				PreparedStatement ps = postgreCon.prepareStatement(REQUEST_CREATE, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, transaction.getInitiateur().getEmail());
			ps.setString(2, transaction.getContrepartie().getEmail());
			ps.setDouble(3, transaction.getMontant());
			ps.setString(4, transaction.getCommentaire());
			int updateRowCount = ps.executeUpdate();

			if (updateRowCount != 1) {
				logger.error("Error in Transaction creation.");
			}

			rs = ps.getGeneratedKeys();

			// We get the primary key generated by the database
			if (rs.next()) {
				transaction.setIdTransaction(rs.getLong(1));
			}

			logger.info("Transaction sucessfully created.");

			return transaction;

		} catch (Exception ex) {
			logger.error("Error in Transaction creation.", ex);
			return null;
		} finally {
			if (rs != null) {
				try {
					rs.close();
					logger.info("Closing Result Set.");
				} catch (SQLException e) {
					logger.error("Error while closing result set.", e);
				}
			}
		}

	}

	/**
	 * Update a financial transaction in the repository.
	 * 
	 * @param Transaction The financial transaction to update
	 */
	@Override
	public void update(Transaction transaction) {

		final String REQUEST_UPDATE = "UPDATE transaction SET initiateur_email=?, contrepartie_email=?, montant=?, commentaire=? WHERE id_transaction=?";

		try (Connection postgreCon = repositoryConfiguration.getConnection();
				PreparedStatement ps = postgreCon.prepareStatement(REQUEST_UPDATE)) {
			ps.setString(1, transaction.getInitiateur().getEmail());
			ps.setString(2, transaction.getContrepartie().getEmail());
			ps.setDouble(3, transaction.getMontant());
			ps.setString(4, transaction.getCommentaire());
			ps.setLong(5, transaction.getIdTransaction());

			int updateRowCount = ps.executeUpdate();

			if (updateRowCount != 1) {
				logger.error("Error in Transaction update.");
			} else {
				logger.info("Transaction sucessfully updated.");
			}

		} catch (Exception ex) {
			logger.error("Error in Transaction update.", ex);
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

		final String REQUEST_READ = "SELECT * FROM transaction WHERE id_transaction=?";

		Transaction transaction = null;
		ResultSet rs = null;
		try (Connection postgreCon = repositoryConfiguration.getConnection();
				PreparedStatement ps = postgreCon.prepareStatement(REQUEST_READ)) {
			ps.setLong(1, idTransaction);

			rs = ps.executeQuery();

			if (rs.next()) {
				transaction = new Transaction();
				transaction.setIdTransaction(rs.getLong("id_transaction"));

				// We get the initiateur and contrepartie of the transaction
				Utilisateur initiateur = utilisateurRepository.read(rs.getString("initiateur_email"));
				Utilisateur contrepartie = utilisateurRepository.read(rs.getString("contrepartie_email"));

				transaction.setInitiateur(initiateur);
				transaction.setContrepartie(contrepartie);
				transaction.setMontant(rs.getDouble("montant"));
				transaction.setCommentaire(rs.getString("commentaire"));

				logger.info("Transaction with id {} sucessfully read.", idTransaction);

			} else {
				logger.error("Error : transaction with id {} not found.", idTransaction);
			}
			return transaction;

		} catch (Exception ex) {
			logger.error("Error in Transaction read.", ex);
			return null;
		} finally {
			if (rs != null) {
				try {
					rs.close();
					logger.info("Closing Result Set.");
				} catch (SQLException e) {
					logger.error("Error while closing result set.", e);
				}
			}
		}
	}

	// With transaction
	/**
	 * Delete a financial transaction from the repository.
	 * 
	 * @param idTransaction The id of the financial transaction to delete
	 */
	@Override
	public void delete(long idTransaction) {

		final String REQUEST_DELETE = "DELETE FROM transaction WHERE id_transaction=?";

		Connection postgreCon = null;
		PreparedStatement ps = null;
		try {
			postgreCon = repositoryConfiguration.getConnection();

			boolean auto = postgreCon.getAutoCommit();
			postgreCon.setAutoCommit(false);

			ps = postgreCon.prepareStatement(REQUEST_DELETE);

			ps.setLong(1, idTransaction);

			int updateRowCount = ps.executeUpdate();

			postgreCon.commit();
			postgreCon.setAutoCommit(auto);

			if (updateRowCount != 1) {
				logger.error("Error in Transaction with id {} deletion.", idTransaction);
			} else {
				logger.info("Transaction with id {} sucessfully deleted.", idTransaction);
			}

		} catch (Exception ex) {
			logger.error("Error in Transaction deletion.", ex);
			try {
				if (postgreCon != null) {
					postgreCon.rollback();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} finally {
			if (postgreCon != null) {
				try {
					postgreCon.close();
					logger.info("Closing Connection.");
				} catch (SQLException e) {
					logger.error("Error while closing Connection.", e);
				}
			}
			if (ps != null) {
				try {
					ps.close();
					logger.info("Closing Prepared Statement.");
				} catch (SQLException e) {
					logger.error("Error while closing Prepared Statement.", e);
				}
			}
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

		final String REQUEST_TRANSACTIONS = "SELECT * from transaction WHERE initiateur_email IN ( SELECT email FROM utilisateur WHERE email = ?) ORDER BY id_transaction DESC ;";

		List<Transaction> transactions = new ArrayList<>();

		ResultSet rs = null;
		try (Connection postgreCon = repositoryConfiguration.getConnection();
				PreparedStatement ps = postgreCon.prepareStatement(REQUEST_TRANSACTIONS)) {
			ps.setString(1, emailUtilisateur);

			rs = ps.executeQuery();

			while (rs.next()) {
				Transaction transaction = new Transaction();
				transaction.setIdTransaction(rs.getLong("id_transaction"));

				// We get the initiateur and contrepartie of the transaction
				Utilisateur initiateur = utilisateurRepository.read(rs.getString("initiateur_email"));
				Utilisateur contrepartie = utilisateurRepository.read(rs.getString("contrepartie_email"));

				transaction.setInitiateur(initiateur);
				transaction.setContrepartie(contrepartie);
				transaction.setMontant(rs.getDouble("montant"));
				transaction.setCommentaire(rs.getString("commentaire"));

				transactions.add(transaction);
			}

			logger.info("Transactions sucessfully get.");

			return transactions;

		} catch (Exception ex) {
			logger.info("Error in getting transactions.", ex);
			return null;
		} finally {
			if (rs != null) {
				try {
					rs.close();
					logger.info("Closing Result Set");
				} catch (SQLException e) {
					logger.error("Error while closing result set", e);
				}
			}
		}

	}

}
