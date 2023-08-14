package uni.siegen.bgf.cardafit.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import uni.siegen.bgf.cardafit.model.Team;

@RepositoryRestResource(collectionResourceRel = "team", path = "team")
public interface TeamRepository extends MongoRepository<Team, String> {

	Team findByTeamName(@Param("teamName") String teamName);

}
