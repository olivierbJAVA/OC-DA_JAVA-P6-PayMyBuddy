package com.paymybuddy.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Class materializing a financial transaction.
 */
@Entity
@Table(name = "transaction")
public class Transaction implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "postgre_sequence")
	@SequenceGenerator(name = "postgre_sequence", sequenceName = "transaction_id_seq", allocationSize = 1)
	@Column(name = "id_transaction")
	private long idTransaction;

	@ManyToOne
	private Utilisateur initiateur;

	@ManyToOne
	private Utilisateur contrepartie;

	@OneToOne
	private Compte compte_initiateur;

	@OneToOne
	private Compte compte_contrepartie;

	private double montant;

	private double frais;

	private String commentaire;

	private String type;

	public Transaction() {
		super();
	}

	public Transaction(Utilisateur initiateur, Utilisateur contrepartie, Compte compte_initiateur,
			Compte compte_contrepartie, double montant, double frais, String commentaire, String type) {
		super();
		this.initiateur = initiateur;
		this.contrepartie = contrepartie;
		this.compte_initiateur = compte_initiateur;
		this.compte_contrepartie = compte_contrepartie;
		this.montant = montant;
		this.frais = frais;
		this.commentaire = commentaire;
		this.type = type;
	}

	public long getIdTransaction() {
		return idTransaction;
	}

	public void setIdTransaction(long idTransaction) {
		this.idTransaction = idTransaction;
	}

	public Utilisateur getInitiateur() {
		return initiateur;
	}

	public void setInitiateur(Utilisateur initiateur) {
		this.initiateur = initiateur;
	}

	public Utilisateur getContrepartie() {
		return contrepartie;
	}

	public void setContrepartie(Utilisateur contrepartie) {
		this.contrepartie = contrepartie;
	}

	public Compte getCompte_initiateur() {
		return compte_initiateur;
	}

	public void setCompte_initiateur(Compte compte_initiateur) {
		this.compte_initiateur = compte_initiateur;
	}

	public Compte getCompte_contrepartie() {
		return compte_contrepartie;
	}

	public void setCompte_contrepartie(Compte compte_contrepartie) {
		this.compte_contrepartie = compte_contrepartie;
	}

	public double getMontant() {
		return montant;
	}

	public void setMontant(double montant) {
		this.montant = montant;
	}

	public double getFrais() {
		return frais;
	}

	public void setFrais(double frais) {
		this.frais = frais;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
