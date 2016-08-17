package ucles.weblab.common.feedback.domain.jpa;

import org.springframework.data.repository.Repository;
import ucles.weblab.common.feedback.domain.FeedbackRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation for feedback entity, return values will be the JPA entity types.
 * 
 * @author Sukhraj
 */
public interface FeedbackRepositoryJpa extends FeedbackRepository, Repository<FeedbackEntityJpa, UUID> {
    
    @Override
    Optional<? extends FeedbackEntityJpa> findOne(UUID id);

    <S extends FeedbackEntityJpa> S save(S s);

}
