package ucles.weblab.common.feedback.domain;

import java.util.Optional;

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
