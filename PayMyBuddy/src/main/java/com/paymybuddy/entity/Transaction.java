package com.paymybuddy.entity;

import java.io.Serializable;

/**
 * Class materializing a financial transaction.
 */
public class Transaction implements Serializable {

	private static final long serialVersionUID = 1L;

	private long idTransaction;

	private Utilisateur initiateur;

	private Utilisateur contrepartie;

	private double montant;

	private String commentaire;

	public Transaction() {
		super();
	}

	public Transaction(long idTransaction, Utilisateur initiateur, Utilisateur contrepartie, double montant,
			String commentaire) {
		super();
		this.idTransaction = idTransaction;
		this.initiateur = initiateur;
		this.contrepartie = contrepartie;
		this.montant = montant;
		this.commentaire = commentaire;
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

	public double getMontant() {
		return montant;
	}

	public void setMontant(double montant) {
		this.montant = montant;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commentaire == null) ? 0 : commentaire.hashCode());
		result = prime * result + ((contrepartie == null) ? 0 : contrepartie.hashCode());
		result = prime * result + (int) (idTransaction ^ (idTransaction >>> 32));
		result = prime * result + ((initiateur == null) ? 0 : initiateur.hashCode());
		long temp;
		temp = Double.doubleToLongBits(montant);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		if (commentaire == null) {
			if (other.commentaire != null)
				return false;
		} else if (!commentaire.equals(other.commentaire))
			return false;
		if (contrepartie == null) {
			if (other.contrepartie != null)
				return false;
		} else if (!contrepartie.equals(other.contrepartie))
			return false;
		if (idTransaction != other.idTransaction)
			return false;
		if (initiateur == null) {
			if (other.initiateur != null)
				return false;
		} else if (!initiateur.equals(other.initiateur))
			return false;
		if (Double.doubleToLongBits(montant) != Double.doubleToLongBits(other.montant))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Transaction [idTransaction=" + idTransaction + ", initiateur=" + initiateur + ", contrepartie="
				+ contrepartie + ", montant=" + montant + ", commentaire=" + commentaire + "]";
	}

}
