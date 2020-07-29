BEGIN TRANSACTION;

DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;

CREATE TABLE utilisateur (
                email VARCHAR(100) NOT NULL,
                password VARCHAR(100) NOT NULL,
                solde DECIMAL(10,2) NOT NULL DEFAULT 0,
		CONSTRAINT utilisateur_pk PRIMARY KEY (email)
);

CREATE SEQUENCE transaction_id_seq;

CREATE TABLE transaction (
                id_transaction BIGINT NOT NULL DEFAULT nextval('transaction_id_seq'),
                initiateur_email VARCHAR(100) NOT NULL,
                contrepartie_email VARCHAR(100) NOT NULL,
		compte_initiateur_numero VARCHAR(50) NOT NULL,	
		compte_contrepartie_numero VARCHAR(50) NOT NULL,       
         	montant DECIMAL(8,2) NOT NULL,
		frais DECIMAL(6,2) NOT NULL DEFAULT 0,
		commentaire VARCHAR(200),
		type VARCHAR(10) NOT NULL,	
		CONSTRAINT transaction_pk PRIMARY KEY (id_transaction)
);

CREATE TABLE utilisateur_connection (
                utilisateur_email VARCHAR(100) NOT NULL,
                utilisateur_connection_email VARCHAR(100),
                CONSTRAINT utilisateur_connection_pk PRIMARY KEY (utilisateur_email , utilisateur_connection_email) 
);

CREATE TABLE compte (
                numero VARCHAR(50) NOT NULL,
    		banque VARCHAR(50) NOT NULL,
		utilisateur_email VARCHAR(100) NOT NULL,
		type VARCHAR(10) NOT NULL,
                CONSTRAINT compte_pk PRIMARY KEY (numero) 
);

ALTER TABLE utilisateur_connection ADD CONSTRAINT utilisateur_email_fk
FOREIGN KEY (utilisateur_email)
REFERENCES utilisateur (email)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE utilisateur_connection ADD CONSTRAINT utilisateur_connection_email_fk
FOREIGN KEY (utilisateur_connection_email)
REFERENCES utilisateur (email)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE transaction ADD CONSTRAINT connection_initiateur_contrepartie_fk
FOREIGN KEY (initiateur_email, contrepartie_email)
REFERENCES utilisateur_connection (utilisateur_email, utilisateur_connection_email)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE transaction ADD CONSTRAINT compte_numero_initiateur_fk
FOREIGN KEY (compte_initiateur_numero)
REFERENCES compte (numero)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE transaction ADD CONSTRAINT compte_numero_contrepartie_fk
FOREIGN KEY (compte_contrepartie_numero)
REFERENCES compte (numero)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE compte ADD CONSTRAINT utilisateur_compte_fk
FOREIGN KEY (utilisateur_email)
REFERENCES utilisateur (email)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;


INSERT INTO utilisateur 
(
	email, password, solde
) 
VALUES 
	('achristie@policier.com', 'ac', 1),
	('jdcarr@policier.com', 'jdc', 2),
	('phalter@policier.com', 'ph', 3),
	
	('jrrtolkien@fantasy.com', 'jrrt', 123),
	('grrmartin@fantasy.com', 'grrm', 321),

	('iasimov@sf.com', 'ia', 123456)
;

INSERT INTO utilisateur_connection
 	(utilisateur_email, utilisateur_connection_email) 
VALUES 
	('achristie@policier.com', 'achristie@policier.com'),
	('jdcarr@policier.com', 'jdcarr@policier.com'),
	('phalter@policier.com', 'phalter@policier.com'),

	('jrrtolkien@fantasy.com', 'jrrtolkien@fantasy.com'),
	('grrmartin@fantasy.com', 'grrmartin@fantasy.com'),	

	('iasimov@sf.com', 'iasimov@sf.com'),

	('achristie@policier.com', 'jdcarr@policier.com'),
 	('achristie@policier.com', 'phalter@policier.com'),
	('jdcarr@policier.com', 'achristie@policier.com'),
	('jdcarr@policier.com', 'phalter@policier.com'),
	('phalter@policier.com', 'achristie@policier.com'),
	('phalter@policier.com', 'jdcarr@policier.com'),

	('jrrtolkien@fantasy.com', 'grrmartin@fantasy.com')
;


INSERT INTO compte 
(
	numero, banque, utilisateur_email, type
) 
VALUES 
	('123SG', 'Societe Generale', 'achristie@policier.com', 'bancaire'),
	('achristie@policier.com_PMB', 'PayMyBuddy', 'achristie@policier.com', 'paymybuddy'),	

	('456SG', 'Societe Generale', 'jdcarr@policier.com', 'bancaire'),
	('jdcarr@policier.com_PMB', 'PayMyBuddy', 'jdcarr@policier.com', 'paymybuddy'),

	('789SG', 'Societe Generale', 'phalter@policier.com', 'bancaire'),
	('phalter@policier.com_PMB', 'PayMyBuddy', 'phalter@policier.com', 'paymybuddy'),	

	('123BNP', 'BNP Paribas', 'jrrtolkien@fantasy.com', 'bancaire'),
	('jrrtolkien@fantasy.com_PMB', 'PayMyBuddy', 'jrrtolkien@fantasy.com', 'paymybuddy'),
	
	('456BNP', 'BNP Paribas', 'grrmartin@fantasy.com', 'bancaire'),
	('grrmartin@fantasy.com_PMB', 'PayMyBuddy', 'grrmartin@fantasy.com', 'paymybuddy'),
	
	('1CA', 'Credit Agricole', 'iasimov@sf.com', 'bancaire'),
	('1SG', 'Societe Generale', 'iasimov@sf.com', 'bancaire'),
	('1BNP', 'BNP Paribas', 'iasimov@sf.com', 'bancaire'),
	('iasimov@sf.com_PMB', 'PayMyBuddy', 'iasimov@sf.com', 'paymybuddy')
;

INSERT INTO transaction
 	(initiateur_email, contrepartie_email, montant, commentaire, type, compte_initiateur_numero, compte_contrepartie_numero, frais)
VALUES 
	('achristie@policier.com', 'achristie@policier.com', 123, 'ac_virement', 'virement', 'achristie@policier.com_PMB', '123SG', 0),
	('achristie@policier.com', 'achristie@policier.com', 456, 'ac_depot', 'depot', '123SG', 'achristie@policier.com_PMB', 0),
	('achristie@policier.com', 'jdcarr@policier.com', 100, 'ac_transfert', 'transfert', 'achristie@policier.com_PMB', 'jdcarr@policier.com_PMB', 0.5),
	('achristie@policier.com', 'jdcarr@policier.com', 300, 'ac_transfert', 'transfert', 'achristie@policier.com_PMB','jdcarr@policier.com_PMB', 1.5),
	('achristie@policier.com', 'phalter@policier.com', 10, 'ac_transfert', 'transfert', 'achristie@policier.com_PMB', 'phalter@policier.com_PMB', 0.05),
 
	('jrrtolkien@fantasy.com', 'grrmartin@fantasy.com', 321, 'jrrt_transfert', 'transfert', 'jrrtolkien@fantasy.com_PMB', 'grrmartin@fantasy.com_PMB', 1.61)
;

COMMIT;