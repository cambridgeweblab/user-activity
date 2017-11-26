package ucles.weblab.common.feedback.domain.jpa;

import org.springframework.data.domain.Persistable;
import ucles.weblab.common.feedback.domain.Feedback;
import ucles.weblab.common.feedback.domain.FeedbackEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Concreate JPA implementation for a feedback entity. The id for this entity
 * is passed through the value object rather than randomly generated on creation.
 * 
 * 
 * @author Sukhraj
 */

@Entity(name = "Feedback")
@Table(name = "feedback")
public class FeedbackEntityJpa implements Persistable<UUID>, FeedbackEntity {

    @Id
    private UUID id;
    
    @Transient
    private boolean unsaved;

    private String name;

    @Column(name = "comments")
    private String comment;
    private Integer score;
    private Instant created;
    
    protected FeedbackEntityJpa() { // For Hibernate, Jackson
    }
    
    public FeedbackEntityJpa(UUID id, Feedback feedback) {
        this.id = id;
        this.unsaved = true;
        this.name = feedback.getName();
        this.comment = feedback.getComment().orElse(null);
        this.score = feedback.getScore().orElse(null);
        created = Instant.now();
    }

    @Override
    public boolean isNew() {
        return unsaved;
    }

    @PostPersist
    void markNotNew() {
        this.unsaved = false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Optional<String> getComment() {
        return Optional.ofNullable(this.comment);
    }

    @Override
    public Optional<Integer> getScore() {
        return Optional.ofNullable(this.score);
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public Instant getCreated() {
        return this.created;
    }
    
}
