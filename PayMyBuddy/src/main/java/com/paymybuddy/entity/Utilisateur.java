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

	@OneToMany
	private Set<Compte> compte;

	public Utilisateur() {
		super();
		// TODO Auto-generated constructor stub
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((compte == null) ? 0 : compte.hashCode());
		result = prime * result + ((connection == null) ? 0 : connection.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((solde == null) ? 0 : solde.hashCode());
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
		Utilisateur other = (Utilisateur) obj;
		if (compte == null) {
			if (other.compte != null)
				return false;
		} else if (!compte.equals(other.compte))
			return false;
		if (connection == null) {
			if (other.connection != null)
				return false;
		} else if (!connection.equals(other.connection))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (solde == null) {
			if (other.solde != null)
				return false;
		} else if (!solde.equals(other.solde))
			return false;
		return true;
	}
	

}