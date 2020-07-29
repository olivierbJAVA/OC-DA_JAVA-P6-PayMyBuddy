package com.paymybuddy.demo;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.factory.ServiceFactory;
import com.paymybuddy.repository.ICompteRepository;
import com.paymybuddy.repository.ITransactionRepository;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;
import com.paymybuddy.service.TransactionTxHibernateService;
import com.paymybuddy.service.UtilisateurTxHibernateService;

/**
 * Class including tests for demo.
 */
public class Demo {

	public static void main(String[] args) {

		ResourceDatabasePopulator resourceDatabasePopulator;

		DriverManagerDataSource dataSource;
		
		String paymybuddyPropertiesFile = "paymybuddy.properties";
		
		// We get a dataSource
		dataSource = RepositoryDataSource.getDataSource(paymybuddyPropertiesFile);

		// We get a resourceDatabasePopulator
		resourceDatabasePopulator = RepositoryRessourceDatabasePopulator
				.getResourceDatabasePopulator("/DatabaseInitializationEmpty.sql");

		// We close the dataSource
		RepositoryDataSource.closeDatasource();
		
		// We clear the database
		DatabasePopulatorUtils.execute(resourceDatabasePopulator, dataSource);
		
		
		RepositoryTxManagerHibernate repositoryTxManager = RepositoryTxManagerHibernate.getRepositoryTxManagerHibernate(paymybuddyPropertiesFile);

		IUtilisateurRepository utilisateurRepositoryImpl = RepositoryFactory.getUtilisateurRepository(repositoryTxManager);
		ITransactionRepository transactionRepositoryImpl = RepositoryFactory.getTransactionRepository(repositoryTxManager);
		ICompteRepository compteRepositoryImpl = RepositoryFactory.getCompteRepository(repositoryTxManager);	
		
		TransactionTxHibernateService transactionTxHibernateService = ServiceFactory.getTransactionService(repositoryTxManager,
				utilisateurRepositoryImpl, transactionRepositoryImpl);
		UtilisateurTxHibernateService utilisateurTxHibernateService = ServiceFactory.getUtilisateurService(repositoryTxManager,
				utilisateurRepositoryImpl, transactionRepositoryImpl, compteRepositoryImpl);
		
		
//***** DEMO CAS OK **********************
		
		//S'enregistrer sur l'application
		utilisateurTxHibernateService.registerToApplication("bertrand.simon@gmail.com", "bs");
		
		//Se connecter à l'application
		utilisateurTxHibernateService.connectToApplication("bertrand.simon@gmail.com", "bs");
		
		// Ajouter un compte bancaire externe
		utilisateurTxHibernateService.addCompte("bertrand.simon@gmail.com", "123SG", "SG");
		utilisateurTxHibernateService.addCompte("bertrand.simon@gmail.com", "123SG", "SG");
		
		//Faire un virement sur son compte PayMyBuddy
		utilisateurTxHibernateService.wireToAccount("bertrand.simon@gmail.com", 300d, "123SG");
	
		//Ajouter une connection
		//utilisateurTxHibernateService.registerToApplication("matthieu.dupond@yahoo.fr", "md");
		//utilisateurTxHibernateService.addConnection("bertrand.simon@gmail.com", "matthieu.dupond@yahoo.fr");
		
		//Faire un virement à la connection
		//transactionTxHibernateService.makeATransaction("bertrand.simon@gmail.com", "matthieu.dupond@yahoo.fr", 100d, "Demo transaction");

		
//***** DEMO CAS NOK **********************
		
		//S'enregistrer sur l'application (utilisateur existe déjà)
		//utilisateurTxHibernateService.registerToApplication("bertrand.simon@gmail.com", "bs");
		
		//Se connecter à l'application (mauvais mot de passe)
		//utilisateurTxHibernateService.connectToApplication("bertrand.simon@gmail.com", "MAUVAIS_MOT_DE_PASSE");
		
		//Faire un virement sur son compte PayMyBuddy (montant négatif)
		//utilisateurTxHibernateService.wireToAccount("bertrand.simon@gmail.com", -300d);
	
		//Ajouter une connection (connection n'existe pas)
		//utilisateurTxHibernateService.addConnection("bertrand.simon@gmail.com", "emailConnectionNotExist@yahoo.fr");
		
		//Faire un virement à la connection (montant supérieur au solde)
		//transactionTxHibernateService.makeATransaction("bertrand.simon@gmail.com", "matthieu.dupond@yahoo.fr", 1000d, "Transaction test");
		
	}

}
