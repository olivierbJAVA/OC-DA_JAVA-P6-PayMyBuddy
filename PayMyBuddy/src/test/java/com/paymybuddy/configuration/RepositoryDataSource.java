package com.paymybuddy.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Class Factory in charge of the construction of DriverManager DataSource
 * objects for interaction with the database
 */
public class RepositoryDataSource {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryDataSource.class);

	private static DriverManagerDataSource dataSource = null;

	private static Properties properties = null;

	private static InputStream input = null;

	private static String paymybuddyPropertiesFilePath = null;

	/**
	 * Create a DriverManager DataSource for the database.
	 * 
	 * @param paymybuddyPropertiesFile The paymybuddy properties file
	 * 
	 * @return The DriverManagerDataSource
	 */
	public static DriverManagerDataSource getDataSource(String paymybuddyPropertiesFile) {

		paymybuddyPropertiesFilePath = "src/test/resources/" + paymybuddyPropertiesFile;

		properties = new Properties();

		try {
			input = new FileInputStream(paymybuddyPropertiesFilePath);
			properties.load(input);
		} catch (IOException ex) {
			logger.error("Error in loading paymybuddy.properties file", ex);
		}

		String driver = properties.getProperty("hibernate.connection.driver_class");
		String url = properties.getProperty("hibernate.connection.url");
		String username = properties.getProperty("hibernate.connection.username");
		String password = properties.getProperty("hibernate.connection.password");

		dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName(driver);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);

		return dataSource;
	}

	/**
	 * Close the DriverManager DataSource.
	 */
	public static void closeDatasource() {
		try {
			dataSource.getConnection().close();
		} catch (SQLException e) {
			logger.error("Error during close of datasource", e);
		}
	}
}
