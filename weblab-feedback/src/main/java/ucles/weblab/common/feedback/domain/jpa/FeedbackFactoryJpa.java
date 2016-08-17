package ucles.weblab.common.feedback.domain.jpa;

import ucles.weblab.common.feedback.domain.Feedback;
import ucles.weblab.common.feedback.domain.FeedbackFactory;
import ucles.weblab.common.feedback.domain.FeedbackEntity;

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
