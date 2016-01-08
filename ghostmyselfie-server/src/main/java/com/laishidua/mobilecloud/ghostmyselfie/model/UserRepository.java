package com.laishidua.mobilecloud.ghostmyselfie.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.laishidua.mobilecloud.ghostmyselfie.client.UserSvcApi;

@RepositoryRestResource(path = UserSvcApi.USER_SVC_PATH)
public interface UserRepository extends PagingAndSortingRepository<User, Long>{

	public User findByUsernameIgnoreCase(@Param(value=UserSvcApi.USERNAME_PARAMETER)String username);

	public User findByEmailIgnoreCase(@Param(value=UserSvcApi.EMAIL_PARAMETER)String email);
	
	public User findByUsernameAndPassword(@Param(value=UserSvcApi.USERNAME_PARAMETER)String username,@Param(value=UserSvcApi.PASSWORD_PARAMETER)String password);
	
	public Page<User> findAll(Pageable pageable);
	
}
