BEGIN TRANSACTION;

DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;

CREATE TABLE utilisateur (
                email VARCHAR(100) NOT NULL,
                password VARCHAR(100) NOT NULL,
                solde DECIMAL(9,2) NOT NULL DEFAULT 0,
		CONSTRAINT utilisateur_pk PRIMARY KEY (email)
);

CREATE SEQUENCE transaction_id_seq;

CREATE TABLE transaction (
                id_transaction INTEGER NOT NULL DEFAULT nextval('transaction_id_seq'),
                initiateur_email VARCHAR(100) NOT NULL,
                contrepartie_email VARCHAR(100) NOT NULL,
                montant DECIMAL(9,2) NOT NULL,
		commentaire VARCHAR(255),		
		CONSTRAINT transaction_pk PRIMARY KEY (id_transaction)
);

CREATE TABLE utilisateur_connection (
                utilisateur_email VARCHAR(100) NOT NULL,
                utilisateur_connection_email VARCHAR(100),
                CONSTRAINT utilisateur_connection_pk PRIMARY KEY (utilisateur_email , utilisateur_connection_email) 
);

ALTER TABLE utilisateur_connection ADD CONSTRAINT email_utilisateur_fk
FOREIGN KEY (utilisateur_email)
REFERENCES utilisateur (email)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE utilisateur_connection ADD CONSTRAINT email_utilisateur_connection_fk
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

INSERT INTO utilisateur 
(
	email, password, solde
) 
VALUES 
	('abc@test.com', 'abc', 123),
	('def@test.com', 'def', 456),
	('ghi@test.com', 'ghi', 789)
;

INSERT INTO utilisateur_connection
 	(utilisateur_email, utilisateur_connection_email) 
VALUES 
	('abc@test.com', 'def@test.com'),
	('abc@test.com', 'ghi@test.com'),
	('def@test.com', 'ghi@test.com')
;

INSERT INTO transaction
 	(initiateur_email, contrepartie_email, montant) 
VALUES 
	('def@test.com', 'ghi@test.com', 123)
;

COMMIT;
