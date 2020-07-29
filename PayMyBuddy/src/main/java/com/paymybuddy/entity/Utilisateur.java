package com.paymybuddy.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Class materializing a user of the application.
 */
@Entity
@Table(name = "utilisateur")
public class Utilisateur implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String email;

	private String password;

	private Double solde;

	@ManyToMany
	@JoinTable(name = "utilisateur_connection", joinColumns = @JoinColumn(name = "utilisateur_email"), inverseJoinColumns = @JoinColumn(name = "utilisateur_connection_email", nullable = true))
	private Set<Utilisateur> connection;

	@OneToMany(mappedBy = "utilisateur")
	private Set<Compte> compte;

	public Utilisateur() {
		super();
	}

	public Utilisateur(String email, String password, Double solde, Set<Utilisateur> connection, Set<Compte> compte) {
		super();
		this.email = email;
		this.password = password;
		this.solde = solde;
		this.connection = connection;
		this.compte = compte;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Double getSolde() {
		return solde;
	}

	public void setSolde(Double solde) {
		this.solde = solde;
	}

	public Set<Utilisateur> getConnection() {
		return connection;
	}

	public void setConnection(Set<Utilisateur> connection) {
		this.connection = connection;
	}

	public Set<Compte> getCompte() {
		return compte;
	}

	public void setCompte(Set<Compte> compte) {
		this.compte = compte;
	}

}