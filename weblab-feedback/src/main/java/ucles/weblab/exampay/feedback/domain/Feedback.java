package ucles.weblab.exampay.feedback.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Value object interface for feedback domain object. 
 * 
 * @author Sukhraj
 */
public interface Feedback {
    
    String getName();
    Optional<String> getComment();
    Optional<Integer> getScore();
}
