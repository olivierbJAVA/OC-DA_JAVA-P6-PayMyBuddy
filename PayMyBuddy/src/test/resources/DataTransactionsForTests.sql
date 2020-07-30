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
	('abc@test.com', 'abc', 123),
	('def@test.com', 'def', 456),
	('ghi@test.com', 'ghi', 789),
	('klm@test.com', 'klm', 100)
;

INSERT INTO utilisateur_connection
 	(utilisateur_email, utilisateur_connection_email) 
VALUES 
	('abc@test.com', 'def@test.com'),
	('abc@test.com', 'ghi@test.com'),
	('def@test.com', 'ghi@test.com'),
	('abc@test.com', 'abc@test.com'),
	('def@test.com', 'def@test.com'),
	('ghi@test.com', 'ghi@test.com')
;

INSERT INTO compte 
(
	numero, banque, utilisateur_email, type
) 
VALUES 
	('abc@test.com_PMB', 'PayMyBuddy', 'abc@test.com', 'paymybuddy'),
	('123SG', 'SocieteGenerale', 'abc@test.com', 'bancaire'),
	('def@test.com_PMB', 'PayMyBuddy', 'def@test.com', 'paymybuddy'),	
	('ghi@test.com_PMB', 'PayMyBuddy', 'ghi@test.com', 'paymybuddy'),
	('klm@test.com_PMB', 'PayMyBuddy', 'klm@test.com', 'paymybuddy')
;

INSERT INTO transaction
   	(initiateur_email, contrepartie_email, montant, commentaire, type, compte_initiateur_numero, compte_contrepartie_numero, frais)
VALUES 
	('def@test.com', 'ghi@test.com', 123, 'test_transfert', 'transfert', 'def@test.com_PMB', 'ghi@test.com_PMB', 0)
;

COMMIT;
