package com.paymybuddy.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Class Factory in charge of the construction of DriverManager DataSource
 * objects for interaction with the database
 */
public class RepositoryDataSourceFactory {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryDataSourceFactory.class);

	/**
	 * Create a DriverManager DataSource for the database.
	 * 
	 * @param driverClassName The name of the driver class of the database
	 * 
	 * @param url             The url of the database
	 * 
	 * @param username        The username for the connection to the database
	 * 
	 * @param password        The password for the connection to the database
	 * 
	 * @return The DriverManagerDataSource
	 */
	public static DriverManagerDataSource getDataSource(String driverClassName, String url, String username,
			String password) {

		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);

		logger.info("DriverManager DataSource sucessfully created.");

		return dataSource;
	}

}
