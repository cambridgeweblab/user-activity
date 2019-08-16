package ucles.weblab.common.feedback.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.transaction.annotation.Transactional;
import ucles.weblab.common.audit.domain.AccessAuditEntity;
import ucles.weblab.common.audit.domain.AccessAuditRepository;
import ucles.weblab.common.feedback.domain.Feedback;
import ucles.weblab.common.feedback.domain.FeedbackEntity;
import ucles.weblab.common.feedback.domain.FeedbackFactory;
import ucles.weblab.common.feedback.domain.FeedbackRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Delegate class to handle interactions between repositories and REST controllers
 *
 * @author Sukhraj
 */
public class FeedbackDelegate {

    private final FeedbackFactory feedbackFactory;

    private final FeedbackRepository feedbackRepository;

    private final AccessAuditRepository accessAuditRepository;

    private final FeedbackResourceAssembler feedbackResourceAssembler;

    private final Function<FeedbackResource, Feedback> feedbackResourceToValue;


    public FeedbackDelegate(FeedbackFactory feedbackFactory,
                            FeedbackRepository feedbackRepository,
                            AccessAuditRepository accessAuditRepository, FeedbackResourceAssembler feedbackResourceAssembler,
                            Function<FeedbackResource, Feedback> feedbackResourceToValue) {
        this.feedbackFactory = feedbackFactory;
        this.feedbackRepository = feedbackRepository;
        this.accessAuditRepository = accessAuditRepository;
        this.feedbackResourceAssembler = feedbackResourceAssembler;
        this.feedbackResourceToValue = feedbackResourceToValue;
    }

    public FeedbackResource createFeedback(UUID id, FeedbackResource feedbackResource) {
        // Error if we try to overwrite an existing value
        if (feedbackRepository.findById(id).isPresent()) {
            throw new DataIntegrityViolationException("Already exists");
        }

        final Feedback value = feedbackResourceToValue.apply(feedbackResource);
        final FeedbackEntity entity = feedbackFactory.newFeedback(id, value);
        final FeedbackEntity saved = feedbackRepository.save(entity);
        return feedbackResourceAssembler.toResource(saved);
    }

    @Transactional(readOnly = true)
    public List<AuditedFeedbackResource> list() {
        // Grab the access audit records for feedback creation and map by UUID.
        // TODO: validate that toUriComponentsBuilder() is OK and doesn't need replacing with UriComponentsBuilder.fromUriString(...toString()) to avoid double-encoding.
        String pathLike = ControllerLinkBuilder.linkTo(FeedbackController.class).toUriComponentsBuilder().pathSegment("%").build(false).getPath();
        List<? extends AccessAuditEntity> accessRecords = accessAuditRepository.findByWhatLike("%" + pathLike.toLowerCase(Locale.UK));
        Function<AccessAuditEntity, UUID> uuidExtractor = accessRecord -> {
            final String path = accessRecord.getWhat().getPath();
            return UUID.fromString(path.substring(path.lastIndexOf('/') + 1));
        };
        Map<UUID, AccessAuditEntity> mappedAccessRecords = accessRecords.stream().collect(toMap(uuidExtractor, identity(), (a, b) -> a));

        // Grab the feedback records
        Stream<? extends FeedbackEntity> feedbackRecords = feedbackRepository.streamAllByOrderByCreatedAsc();
        List<AuditedFeedbackResource> resources = feedbackRecords
                .map(feedback -> {
                    Optional<AccessAuditEntity> accessRecord = Optional.ofNullable(mappedAccessRecords.get(feedback.getId()));
                    return new AuditedFeedbackResource(feedback.getName(),
                            feedback.getScore().orElse(null),
                            feedback.getComment().orElse(null),
                            accessRecord.flatMap(AccessAuditEntity::getWho).orElse(null),
                            feedback.getCreated(),
                            accessRecord.map(e -> e.getWhere().toString()).orElse(null),
                            accessRecord.flatMap(AccessAuditEntity::getHow).orElse(null));
                })
                .collect(toList());
        return resources;
    }
}
