package uni.siegen.bgf.cardafit.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.LinkDiscoverer;
import org.springframework.hateoas.hal.HalConfiguration;
import org.springframework.hateoas.hal.HalLinkDiscoverer;

import uni.siegen.bgf.cardafit.model.User;

@RepositoryRestResource(collectionResourceRel = "user", path = "user")
public interface UserRepository extends MongoRepository<User, String> {

	List<User> findByUserName(@Param("name") String name);

}
