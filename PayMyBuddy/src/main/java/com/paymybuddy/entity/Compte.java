package com.paymybuddy.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
		// TODO Auto-generated constructor stub
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((banque == null) ? 0 : banque.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((utilisateur == null) ? 0 : utilisateur.hashCode());
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
		Compte other = (Compte) obj;
		if (banque == null) {
			if (other.banque != null)
				return false;
		} else if (!banque.equals(other.banque))
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (utilisateur == null) {
			if (other.utilisateur != null)
				return false;
		} else if (!utilisateur.equals(other.utilisateur))
			return false;
		return true;
	}

	

	
}
