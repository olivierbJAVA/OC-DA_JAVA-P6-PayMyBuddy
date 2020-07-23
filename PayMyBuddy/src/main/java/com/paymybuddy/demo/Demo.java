package com.paymybuddy.demo;

import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.factory.ServiceFactory;
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

		String paymybuddyPropertiesFile = "paymybuddy.properties";
		
		RepositoryTxManagerHibernate repositoryTxManager = RepositoryTxManagerHibernate.getRepositoryTxManagerHibernate(paymybuddyPropertiesFile);

		IUtilisateurRepository utilisateurRepositoryImpl = RepositoryFactory.getUtilisateurRepository(repositoryTxManager);
		ITransactionRepository transactionRepositoryImpl = RepositoryFactory.getTransactionRepository(repositoryTxManager);
			
		TransactionTxHibernateService transactionTxHibernateService = ServiceFactory.getTransactionService(repositoryTxManager,
				utilisateurRepositoryImpl, transactionRepositoryImpl);
		UtilisateurTxHibernateService utilisateurTxHibernateService = ServiceFactory.getUtilisateurService(repositoryTxManager,
				utilisateurRepositoryImpl);
		
		
//***** DEMO CAS OK **********************
		
		//S'enregistrer sur l'application
		//utilisateurTxHibernateService.registerToApplication("bertrand.simon@gmail.com", "bs");
		
		//Se connecter à l'application
		//utilisateurTxHibernateService.connectToApplication("bertrand.simon@gmail.com", "bs");
		
		//Faire un virement sur son compte PayMyBuddy
		//utilisateurTxHibernateService.wireToAccount("bertrand.simon@gmail.com", 300d);
	
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
	
		//Ajouter une connection (connection n'exite pas)
		//utilisateurTxHibernateService.addConnection("bertrand.simon@gmail.com", "emailConnectionNotExist@yahoo.fr");
		
		//Faire un virement à la connection (montant supérieur au solde)
		//transactionTxHibernateService.makeATransaction("bertrand.simon@gmail.com", "matthieu.dupond@yahoo.fr", 1000d);
		
	}

}
