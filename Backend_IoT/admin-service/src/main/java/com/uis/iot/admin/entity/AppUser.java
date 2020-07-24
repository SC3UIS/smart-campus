package com.uis.iot.admin.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.uis.iot.common.model.AppUserDTO;

/**
 * The persistent class for the app_user database table.
 * 
 */
@Entity
@Table(name = "app_user")
@NamedQuery(name = "AppUser.findAll", query = "SELECT a FROM AppUser a")
public class AppUser implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user")
	private Long id;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(name = "is_admin")
	private boolean admin;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String password;

	@Column(unique = true)
	private String username;

	// bi-directional many-to-one association to Application
	@OneToMany(mappedBy = "user")
	private List<Application> applications;

	// bi-directional many-to-one association to Notification
	@OneToMany(mappedBy = "user")
	private List<Notification> notifications;
	
	// bi-directional many-to-one association to Process
	@OneToMany(mappedBy = "user")
	private List<Gateway> gateways;

	public AppUser() {
		super();
	}

	public AppUser(Long id) {
		this.id = id;
	}

	public AppUser(Long id, String email, boolean admin, String name, String password, String username) {
		this.id = id;
		this.email = email;
		this.admin = admin;
		this.name = name;
		this.password = password;
		this.username = username;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isAdmin() {
		return this.admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Application> getApplications() {
		return this.applications;
	}

	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}

	public Application addApplication(Application application) {
		getApplications().add(application);
		application.setAppUser(this);

		return application;
	}

	public Application removeApplication(Application application) {
		getApplications().remove(application);
		application.setAppUser(null);

		return application;
	}
	

	public List<Gateway> getGateways() {
		return gateways;
	}

	public void setGateways(List<Gateway> gateways) {
		this.gateways = gateways;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public AppUserDTO toDTO() {
		return new AppUserDTO(this.id, this.username, this.name, this.email, null, this.admin);
	}

	public static AppUser fromDTO(AppUserDTO dto) {
		return new AppUser(dto.getId(), dto.getEmail(), dto.isAdmin(), dto.getName(), dto.getPassword(),
				dto.getUsername());
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
		if (obj instanceof AppUser) {
			AppUser other = (AppUser) obj;
			return Objects.equals(id, other.getId()) && Objects.equals(username, other.getUsername());
		}
		return false;
	}

	@Override
	public String toString() {
		return "[ " + id + ", " + username + " ]";
	}

}