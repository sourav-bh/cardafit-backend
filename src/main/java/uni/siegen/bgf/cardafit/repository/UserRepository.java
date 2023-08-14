package uni.siegen.bgf.cardafit.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import uni.siegen.bgf.cardafit.model.User;

@RepositoryRestResource(collectionResourceRel = "user", path = "user")
public interface UserRepository extends MongoRepository<User, String> {

	User findByUserName(@Param("userName") String userName);
	List<User> findByTeamName(@Param("teamName") String teamName);
	List<User> findByDeviceToken(@Param("deviceToken") String deviceToken);

}
