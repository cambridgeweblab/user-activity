package ucles.weblab.common.feedback.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import ucles.weblab.common.feedback.domain.FeedbackEntity;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Assembler class to create a FeedbackResource from a FeedbackEntity object
 *  
 * 
 * @author Sukhraj
 */
@Configuration
public class FeedbackResourceAssembler extends ResourceAssemblerSupport<FeedbackEntity, FeedbackResource>{
        
    
    @Autowired
    public FeedbackResourceAssembler() {
        super(FeedbackController.class, FeedbackResource.class);
    }

    @Override
    public FeedbackResource toResource(FeedbackEntity entity) {
        FeedbackResource resource = instantiateResource(entity);
        resource.add(linkTo(FeedbackController.class).slash(entity.getId()).withRel("self"));
        
        return resource;
    }
 
    @Override
    protected FeedbackResource instantiateResource(FeedbackEntity entity) { 
        
        //create a resource from the entity
        FeedbackResource resource = new FeedbackResource(entity.getName(),
                                                         entity.getScore().orElse(0), 
                                                         entity.getComment().orElse(null));
        return resource;
    }
    
}
