package ucles.weblab.exampay.feedback.domain.jpa;

import ucles.weblab.exampay.feedback.domain.Feedback;
import ucles.weblab.exampay.feedback.domain.FeedbackEntity;
import ucles.weblab.exampay.feedback.domain.FeedbackFactory;

import java.util.UUID;

/**
 * JPA implementation of the FeedbackFactory. It instatiates JPA entity objects. 
 * 
 * @author Sukhraj
 */
public class FeedbackFactoryJpa implements FeedbackFactory {

    @Override
    public FeedbackEntity newFeedback(UUID id, Feedback feedback) {
        return new FeedbackEntityJpa(id, feedback);
    }
    
}
