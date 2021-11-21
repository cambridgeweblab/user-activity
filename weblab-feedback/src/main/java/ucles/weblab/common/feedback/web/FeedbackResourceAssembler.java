package ucles.weblab.common.feedback.web;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import ucles.weblab.common.feedback.domain.FeedbackEntity;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * Assembler class to create a FeedbackResource from a FeedbackEntity object
 *
 *
 * @author Sukhraj
 */
@Component
public class FeedbackResourceAssembler extends RepresentationModelAssemblerSupport<FeedbackEntity, FeedbackResource> {

    public FeedbackResourceAssembler() {
        super(FeedbackController.class, FeedbackResource.class);
    }

    @Override
    public FeedbackResource toModel(FeedbackEntity entity) {
        FeedbackResource resource = instantiateModel(entity);
        resource.add(linkTo(FeedbackController.class).slash(entity.getId()).withRel("self"));

        return resource;
    }

    @Override
    protected FeedbackResource instantiateModel(FeedbackEntity entity) {

        //create a resource from the entity
        return new FeedbackResource(
                entity.getName(),
                entity.getScore().orElse(0),
                entity.getComment().orElse(null));
    }

}
