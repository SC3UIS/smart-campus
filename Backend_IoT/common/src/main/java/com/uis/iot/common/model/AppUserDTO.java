package com.uis.iot.common.model;

import java.util.Objects;

/**
 * Represents the AppUser as a Data Transfer Object.
 * 
 * @author felipe.estupinan
 *
 */
public class AppUserDTO {

	private Long id;

	private String username;

	private String name;

	private String email;

	private String password;

	private boolean admin;

	public AppUserDTO() {
		super();
	}

	public AppUserDTO(Long id, String username, String name, String email, String password, boolean admin) {
		this.id = id;
		this.username = username;
		this.name = name;
		this.email = email;
		this.admin = admin;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof AppUserDTO) {
			AppUserDTO other = (AppUserDTO) obj;
			return Objects.equals(id, other.getId()) && Objects.equals(username, other.getUsername());
		}
		return false;
	}

	@Override
	public String toString() {
		return "[ " + id + ", " + username + " ]";
	}

}
