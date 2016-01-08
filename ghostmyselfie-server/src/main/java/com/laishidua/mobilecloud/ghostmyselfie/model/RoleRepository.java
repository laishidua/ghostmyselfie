package com.laishidua.mobilecloud.ghostmyselfie.model;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.laishidua.mobilecloud.ghostmyselfie.client.UserSvcApi;

@RepositoryRestResource(path = UserSvcApi.ROLE_PATH)
public interface RoleRepository extends PagingAndSortingRepository<Role, Long>{
	
	List<Role> findAll();
}
