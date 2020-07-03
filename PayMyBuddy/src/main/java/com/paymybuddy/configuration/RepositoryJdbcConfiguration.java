package com.paymybuddy.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class in charge of managing the configuration of the JDBC connection to the
 * database
 */
public class RepositoryJdbcConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryJdbcConfiguration.class);

	private static final String PROPERTY_URL = "url";
	private static final String PROPERTY_USER_NAME = "username";
	private static final String PROPERTY_PASSWORD = "password";

	private String url;
	private String username;
	private String password;

	private RepositoryJdbcConfiguration(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	private static RepositoryJdbcConfiguration repositoryConfiguration = null;

	/**
	 * Return an instance of a JDBC repository configuration if it does not already
	 * exist.
	 * 
	 * @param propertiesFilePath The path and filename to the file containing
	 *                           properties for the connection to the database
	 * 
	 * @return An instance of a JDBC repository configuration
	 */
	public static RepositoryJdbcConfiguration getRepositoryConfiguration(String propertiesFilePath) {
		if (repositoryConfiguration == null) {
			repositoryConfiguration = getRepositoryConfigurationInstance(propertiesFilePath);
		}
		return repositoryConfiguration;
	}

	private static RepositoryJdbcConfiguration getRepositoryConfigurationInstance(String propertiesFilePath) {
		Properties properties = new Properties();

		String url = null;
		String username = null;
		String password = null;

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream propertiesFile = classLoader.getResourceAsStream(propertiesFilePath);

		if (propertiesFile == null) {
			logger.error("Le fichier properties {} est introuvable.", propertiesFilePath);
		}

		try {
			properties.load(propertiesFile);
			url = properties.getProperty(PROPERTY_URL);
			username = properties.getProperty(PROPERTY_USER_NAME);
			password = properties.getProperty(PROPERTY_PASSWORD);
		} catch (IOException e) {
			logger.error("Impossible de charger le fichier properties {}", propertiesFilePath);
		}

		RepositoryJdbcConfiguration repositoryConfigurationInstance = new RepositoryJdbcConfiguration(url, username,
				password);

		return repositoryConfigurationInstance;
	}

	/**
	 * Return a JDBC connection to the database.
	 * 
	 * @return A connection to the database
	 */
	public Connection getConnection() {
		try {
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			logger.error("Impossible d'obtenir la connexion à la base de donnée.", e);
			return null;
		}
	}

}
