package ucles.weblab.common.feedback.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.types.LinkDescriptionObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ucles.weblab.common.identity.domain.Belongs;
import ucles.weblab.common.identity.domain.PartyRoles;
import ucles.weblab.common.webapi.AccessAudited;
import ucles.weblab.common.webapi.MoreMediaTypes;
import ucles.weblab.common.webapi.resource.ResourceListWrapper;
import ucles.weblab.common.schema.webapi.MoreFormats;
import ucles.weblab.common.schema.webapi.SchemaMediaTypes;
import ucles.weblab.common.schema.webapi.SchemaProvidingController;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static ucles.weblab.common.webapi.HateoasUtils.locationHeader;

/**
 * Controller to handle REST interfactions
 * @author Sukhraj
 */

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController extends SchemaProvidingController<FeedbackController> {
    
    private static final Logger log = LoggerFactory.getLogger(FeedbackController.class);

    private final FeedbackDelegate feedbackDelegate;

    @Autowired
    public FeedbackController(FeedbackDelegate feedbackDelegate) {
        this.feedbackDelegate = feedbackDelegate;
    }
    
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MoreMediaTypes.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Secured(PartyRoles.ROLE_ADMIN)
    public ResourceListWrapper<AuditedFeedbackResource> list() {
        List<AuditedFeedbackResource> list = feedbackDelegate.list();
        ResourceListWrapper<AuditedFeedbackResource> response = ResourceListWrapper.wrap(list);
        addDescribedByLink(response);
        response.add(linkTo(self().list()).withRel("download"));

        return response;
    }

    @AccessAudited
    @RequestMapping(value = "/{key}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MoreMediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<FeedbackResource> create(@PathVariable UUID key, @RequestBody @Valid FeedbackResource data) {
        final FeedbackResource created = feedbackDelegate.createFeedback(key, data);
        return new ResponseEntity<>(created, locationHeader(created), HttpStatus.CREATED);
    }

    static class IdResource extends ResourceSupport {
        @JsonProperty(value = "id", required = true)
        @ucles.weblab.common.schema.webapi.JsonSchema(format = MoreFormats.UUID)
        String uuid;

        public String getUuid() {
            return uuid;
        }
    }

    @RequestMapping(value = "/$schema-create", method = GET, produces = SchemaMediaTypes.APPLICATION_SCHEMA_JSON_UTF8_VALUE)
    public ResponseEntity<JsonSchema> describeForCreate() {

        // Generate a LDO for the create link with a templated URL.
        JsonSchema idResourceSchema = getSchemaCreator().create(IdResource.class, URI.create("urn:id"), Optional.empty(), Optional.empty());
        LinkDescriptionObject createLink = new LinkDescriptionObject() {
            @JsonProperty
           	private Map<String, JsonSchema> properties = idResourceSchema.asObjectSchema().getProperties();
        }
                // TODO: This should probably be .setSchema(idResourceSchema) instead of the declaration above
                .setHref(linkTo(FeedbackController.class).toUriComponentsBuilder().pathSegment("{id}").build(false).toString())
                .setRel("create")
                .setMethod(RequestMethod.PUT.toString());

        JsonSchema schema = getSchemaCreator().create(FeedbackResource.class,
                self().describeForCreate(),
                Optional.empty(),
                Optional.of(createLink));

        return ResponseEntity.ok(schema);
    }

    public ResponseEntity<JsonSchema> describe(@AuthenticationPrincipal Belongs principal) {

        JsonSchema schema = getSchemaCreator().create(AuditedFeedbackResource.class,
                self().describe(principal),
                Optional.of(self().list()),
                Optional.empty());

        return ResponseEntity.ok(schema);
    }
}
