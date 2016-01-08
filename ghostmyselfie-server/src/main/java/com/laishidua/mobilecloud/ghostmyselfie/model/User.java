package com.laishidua.mobilecloud.ghostmyselfie.model;


import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class User extends AbstractAuditableEntity implements UserDetails, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@JsonIgnore
	@Transient
	private transient Collection<GrantedAuthority> authorities;
	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", 
	joinColumns = @JoinColumn(name = "user_id", nullable = false),
	inverseJoinColumns = @JoinColumn(name = "roles_id", nullable = false))	
	private List<Role> roles;
	private String email;
	@JsonIgnore
	private String password;
	private String username;
	
	public User() {
		super();
	}

	public static User create(String username, String password,
			String email,
			String... authorities) {
		return new User(username, password, email, authorities);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private User(String username, String password) {
		this(username, password, Collections.EMPTY_LIST);
	}

	private User(String username, String password, String email, String... authorities) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.authorities = AuthorityUtils.createAuthorityList(authorities);
	}

	private User(String username, String password,
			Collection<GrantedAuthority> authorities) {
		super();
		this.username = username;
		this.password = password;
		this.email = "";
		this.authorities = authorities;
	}

	@JsonIgnore
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public Collection<GrantedAuthority> getAuthorities_() {
		return authorities;
	}

	@JsonIgnore
	public List<Role> getRoles() {
		return roles;
	}

	@JsonProperty
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty
	public void setAuthorities(Collection<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}


