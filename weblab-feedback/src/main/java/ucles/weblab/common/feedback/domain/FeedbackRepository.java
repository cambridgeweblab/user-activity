package ucles.weblab.common.feedback.domain;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 *
 * @author Sukhraj
 */
public interface FeedbackRepository {
    Optional<? extends FeedbackEntity> findById(UUID id);
    <T extends FeedbackEntity> T save(T entity);

    Stream<? extends FeedbackEntity> streamAllByOrderByCreatedAsc();
}
