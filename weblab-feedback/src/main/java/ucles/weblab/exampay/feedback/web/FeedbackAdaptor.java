package ucles.weblab.exampay.feedback.web;

import java.util.Optional;
import java.util.UUID;
import ucles.weblab.exampay.feedback.domain.Feedback;

/**
 * 
 * @author Sukhraj
 */
public class FeedbackAdaptor implements Feedback {
    
    private final FeedbackResource resource;

    public FeedbackAdaptor(FeedbackResource resource) {
        this.resource = resource;
    }
    
    @Override
    public String getName() {
        return resource.getPageName();
    }

    @Override
    public Optional<String> getComment() {
        return Optional.of(resource.getComments());
    }

    @Override
    public Optional<Integer> getScore() {
        return Optional.of(resource.getScore());
    }
    
}
