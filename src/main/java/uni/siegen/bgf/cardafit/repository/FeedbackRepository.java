package uni.siegen.bgf.cardafit.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import uni.siegen.bgf.cardafit.model.Feedback;

@RepositoryRestResource(collectionResourceRel = "feedback", path = "feedback")
public interface FeedbackRepository extends MongoRepository<Feedback, String> {

	List<Feedback> findByUserId(@Param("userId") String userId);
}
