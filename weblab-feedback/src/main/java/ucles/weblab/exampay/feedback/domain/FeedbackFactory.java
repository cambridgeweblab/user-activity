package ucles.weblab.exampay.feedback.domain;

import java.util.UUID;

/**
 * INterface to define contract for any implementation that creates entity objects. 
 * 
 * @author Sukhraj
 */
public interface FeedbackFactory {
    
    /**
     * Create a new feedback entity object from the feedback value object parameter
     * 
     * @param feedback value object paramter
     * @return new Feedback entity object 
     */
    FeedbackEntity newFeedback(UUID id, Feedback feedback);

    
}
