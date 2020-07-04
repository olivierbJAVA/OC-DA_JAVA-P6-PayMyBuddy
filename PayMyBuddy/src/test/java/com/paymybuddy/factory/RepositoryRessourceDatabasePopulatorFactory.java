package com.paymybuddy.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * Class Factory in charge of the construction of RepositoryRessourceDatabase
 * objects in order to execute a SQL script
 */
public class RepositoryRessourceDatabasePopulatorFactory {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryRessourceDatabasePopulatorFactory.class);

	/**
	 * Create a RepositoryRessourceDatabase for the database.
	 * 
	 * @param filePathScript The file path of the SQL script to be executed
	 * 
	 * @return The ResourceDatabasePopulator
	 */
	public static ResourceDatabasePopulator getResourceDatabasePopulator(String filePathScript) {

		ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();

		resourceDatabasePopulator.addScript(new ClassPathResource(filePathScript));

		logger.info("ResourceDatabasePopulator sucessfully created.");

		return resourceDatabasePopulator;
	}

}
