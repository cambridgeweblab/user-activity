package ucles.weblab.common.feedback.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Interface to represent an entity object. 
 * 
 * @author Sukhraj
 */
public interface FeedbackEntity extends Feedback {
    
    UUID getId();
    Instant getCreated();
    
}
