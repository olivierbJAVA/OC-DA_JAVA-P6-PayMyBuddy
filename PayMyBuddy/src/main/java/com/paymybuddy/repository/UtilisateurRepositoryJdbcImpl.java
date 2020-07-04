package com.paymybuddy.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.configuration.RepositoryJdbcConfiguration;
import com.paymybuddy.entity.Utilisateur;

/**
 * Class managing the data persistence for the user using JDBC implementation.
 */
public class UtilisateurRepositoryJdbcImpl implements IUtilisateurRepository {

	private static final Logger logger = LoggerFactory.getLogger(UtilisateurRepositoryJdbcImpl.class);

	private RepositoryJdbcConfiguration repositoryConfiguration = null;

	public UtilisateurRepositoryJdbcImpl(RepositoryJdbcConfiguration repositoryConfiguration) {
		this.repositoryConfiguration = repositoryConfiguration;
	}

	/**
	 * Add a user in the repository.
	 * 
	 * @param Utilisateur The user to add
	 */
	@Override
	public void create(Utilisateur utilisateur) {

		final String REQUEST_CREATE = "INSERT INTO utilisateur (email, password, solde) VALUES (?,?,?)";

		try (Connection postgreCon = repositoryConfiguration.getConnection();
				PreparedStatement ps = postgreCon.prepareStatement(REQUEST_CREATE)) {

			ps.setString(1, utilisateur.getEmail());
			ps.setString(2, utilisateur.getPassword());
			ps.setDouble(3, utilisateur.getSolde());
			int updateRowCount = ps.executeUpdate();

			if (updateRowCount != 1) {
				logger.error("Error in Utilisateur creation.");
			} else {
				logger.info("Utilisateur sucessfully created.");
			}

		} catch (Exception ex) {
			logger.error("Error in Utilisateur creation", ex);
		}
	}

	/**
	 * Update a user in the repository.
	 * 
	 * @param Utilisateur The user to update
	 */
	@Override
	public void update(Utilisateur utilisateur) {

		final String REQUEST_UPDATE = "UPDATE utilisateur SET password=?, solde=? WHERE email=?";

		try (Connection postgreCon = repositoryConfiguration.getConnection();
				PreparedStatement psUtilisateur = postgreCon.prepareStatement(REQUEST_UPDATE);) {
			psUtilisateur.setString(1, utilisateur.getPassword());
			psUtilisateur.setDouble(2, utilisateur.getSolde());
			psUtilisateur.setString(3, utilisateur.getEmail());

			int updateRowCountUtilisateur = psUtilisateur.executeUpdate();

			if (updateRowCountUtilisateur != 1) {
				logger.error("Error in Utilisateur update.");
			} else {
				logger.info("Utilisateur sucessfully updated.");
			}

		} catch (Exception ex) {
			logger.error("Error in Utilisateur update.", ex);
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

		final String REQUEST_READ_UTILISATEUR = "SELECT * FROM utilisateur WHERE email=?";

		final String REQUEST_READ_CONNECTIONS = "SELECT * FROM utilisateur WHERE utilisateur.email IN ( SELECT utilisateur_connection_email FROM utilisateur_connection WHERE utilisateur_email=?)";

		Utilisateur utilisateur = null;
		Utilisateur connection = null;
		ResultSet rsUtilisateur = null;
		ResultSet rsConnections = null;
		try (Connection postgreCon = repositoryConfiguration.getConnection();
				PreparedStatement psUtilisateur = postgreCon.prepareStatement(REQUEST_READ_UTILISATEUR);
				PreparedStatement psConnections = postgreCon.prepareStatement(REQUEST_READ_CONNECTIONS);) {

			psUtilisateur.setString(1, email);
			rsUtilisateur = psUtilisateur.executeQuery();

			if (rsUtilisateur.next()) {
				utilisateur = new Utilisateur();
				utilisateur.setEmail(rsUtilisateur.getString("email"));
				utilisateur.setPassword(rsUtilisateur.getString("password"));
				utilisateur.setSolde(rsUtilisateur.getDouble("solde"));

				psConnections.setString(1, email);
				rsConnections = psConnections.executeQuery();

				Set<Utilisateur> connections = new HashSet<>();

				while (rsConnections.next()) {
					connection = new Utilisateur();
					connection.setEmail(rsConnections.getString("email"));
					connection.setPassword(rsConnections.getString("password"));
					connection.setSolde(rsConnections.getDouble("solde"));
					connections.add(connection);
				}
				if (!connections.isEmpty()) {
					utilisateur.setConnection(connections);
				}

				logger.info("Utilisateur with email {} sucessfully read.", email);

			} else {
				logger.error("Error : Utilisateur with email {} not found", email);
			}
			return utilisateur;

		} catch (Exception ex) {
			logger.error("Error in Utilisateur read.", ex);
			return null;
		} finally {
			if (rsUtilisateur != null) {
				try {
					rsUtilisateur.close();
					logger.info("Closing Result Set.");
				} catch (SQLException e) {
					logger.error("Error while closing result set.", e);
				}
			}
			if (rsConnections != null) {
				try {
					rsConnections.close();
					logger.info("Closing Result Set.");
				} catch (SQLException e) {
					logger.error("Error while closing result set.", e);
				}
			}
		}
	}

	// With transaction
	/**
	 * Delete a user from the repository.
	 * 
	 * @param email The email of the user to delete
	 */
	@Override
	public void delete(String email) {

		final String REQUEST_DELETE = "DELETE FROM utilisateur WHERE email=?";

		Connection postgreCon = null;
		PreparedStatement ps = null;
		try {
			postgreCon = repositoryConfiguration.getConnection();

			boolean auto = postgreCon.getAutoCommit();
			postgreCon.setAutoCommit(false);

			ps = postgreCon.prepareStatement(REQUEST_DELETE);

			ps.setString(1, email);

			int updateRowCount = ps.executeUpdate();

			postgreCon.commit();
			postgreCon.setAutoCommit(auto);

			if (updateRowCount != 1) {
				logger.error("Error in Utilisateur with email {} deletion", email);
			} else {
				logger.info("Utilisateur with email {} sucessfully deleted.", email);
			}

		} catch (Exception ex) {
			logger.error("Error in Utilisateur deletion.", ex);
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
	 * Add a connection in the repository.
	 * 
	 * @param utilisateur The user for which to add a connection
	 * 
	 * @param connection  The connection to be added to the user
	 */
	@Override
	public void addConnection(Utilisateur utilisateur, Utilisateur connection) {

		final String REQUEST_CREATE = "INSERT INTO utilisateur_connection (utilisateur_email, utilisateur_connection_email) VALUES(?,?)";

		try (Connection postgreCon = repositoryConfiguration.getConnection();
				PreparedStatement ps = postgreCon.prepareStatement(REQUEST_CREATE)) {

			ps.setString(1, utilisateur.getEmail());
			ps.setString(2, connection.getEmail());
			int updateRowCount = ps.executeUpdate();

			if (updateRowCount != 1) {
				logger.error("Error in Connection add");
			} else {
				logger.info("Connection sucessfully added.");
			}

		} catch (Exception ex) {
			logger.info("Error in Connection add.", ex);
		}

	}

}