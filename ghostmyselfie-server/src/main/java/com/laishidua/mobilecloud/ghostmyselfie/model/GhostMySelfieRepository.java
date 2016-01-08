package com.laishidua.mobilecloud.ghostmyselfie.model;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * An interface for a repository that can store GhostMySelfie
 * objects and allow them to be searched by title.
 * 
 * @author jules
 *
 */
@Repository
public interface GhostMySelfieRepository extends CrudRepository<GhostMySelfie, Long>{

	public final static String FIND_EXACT_GHOSTMYSELFIE_QUERY = "SELECT gms " + 
            "FROM GhostMySelfie gms " +
            "WHERE gms.title = :title AND " +
            "gms.owner = :owner" ;
	
	public final static String FIND_EXACT_GHOSTMYSELFIE_FROM_ID_QUERY = "SELECT gms " + 
            "FROM GhostMySelfie gms " +
            "WHERE gms.id = :id AND " +
            "gms.owner = :owner" ;	
	
	// Find all ghostmyselfies with a matching title (e.g., GhostMySelfie.name)
	public Collection<GhostMySelfie> findByTitle(String title);
	
	// Find all ghostmyselfies by owner.
	public Collection<GhostMySelfie> findByOwner(String owner);	

	// Allowing overwriting of an already posted ghostmyselfies by a certain user 
	@Query(FIND_EXACT_GHOSTMYSELFIE_QUERY)
	public GhostMySelfie findExactGhostMySelfie(@Param("title")String title
			, @Param("owner") String owner);
	
	// Find Selfie by Id. 
	@Query(FIND_EXACT_GHOSTMYSELFIE_FROM_ID_QUERY)
	public GhostMySelfie findExactGhostMySelfieById(@Param("id")long id
			, @Param("owner") String owner);	
	
}
