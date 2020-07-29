package com.paymybuddy.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Class materializing an account.
 */
@Entity
@Table(name = "compte")
public class Compte implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String numero;

	private String banque;

	private String type;

	@ManyToOne
	private Utilisateur utilisateur;

	public Compte() {
		super();
	}

	public Compte(String numero, String banque, String type, Utilisateur utilisateur) {
		super();
		this.numero = numero;
		this.banque = banque;
		this.type = type;
		this.utilisateur = utilisateur;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getBanque() {
		return banque;
	}

	public void setBanque(String banque) {
		this.banque = banque;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}

}
