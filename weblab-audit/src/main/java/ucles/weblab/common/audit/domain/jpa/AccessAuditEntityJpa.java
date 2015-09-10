package ucles.weblab.common.audit.domain.jpa;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.domain.Persistable;
import ucles.weblab.common.audit.domain.AccessAudit;
import ucles.weblab.common.audit.domain.AccessAuditEntity;

import java.net.InetAddress;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import static org.apache.commons.lang.StringUtils.abbreviate;

/**
 * Entity class for persisting an access audit record.
 *
 * @since 27/07/15
 */
@Entity(name = "AccessAudit")
@Table(name = "audit_access_log")
public class AccessAuditEntityJpa implements Persistable<Long>, AccessAuditEntity {
    private static final int MAX_USER_AGENT_LENGTH = 500;

    @Id
    @GeneratedValue(generator="accessAuditGen", strategy = GenerationType.TABLE)
    @GenericGenerator(name = "accessAuditGen", strategy = "org.hibernate.id.enhanced.TableGenerator",
            parameters = {
                    @Parameter(name = "increment_size", value = "1"),
                    @Parameter(name = "optimizer", value = "none"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "segment_value", value = "access")
            })
    private Long id;

    @Column(name = "username")
    private String who;
    @Column(name = "log_instant")
    private Instant when;
    @Column(name = "request_uri")
    private URI what;
    @Column(name = "from_ip")
    private InetAddress where;
    @Column(name = "user_agent")
    private String how;

    protected AccessAuditEntityJpa() { // For Jackson/Hibernate
    }

    public AccessAuditEntityJpa(AccessAudit vo) {
        this.who = vo.getWho().orElse(null);
        this.when = vo.getWhen();
        this.what = vo.getWhat();
        this.where = vo.getWhere();
        this.how = vo.getHow().map(userAgent -> abbreviate(userAgent, MAX_USER_AGENT_LENGTH)).orElse(null);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    @Transient
    public boolean isNew() {
        return id == null;
    }

    @Override
    public Optional<String> getWho() {
        return Optional.ofNullable(who);
    }

    @Override
    public Instant getWhen() {
        return when;
    }

    @Override
    public URI getWhat() {
        return what;
    }

    @Override
    public InetAddress getWhere() {
        return where;
    }

    @Override
    public Optional<String> getHow() {
        return Optional.ofNullable(how);
    }

    @Override
    public String toString() {
        return "AccessAuditEntityJpa{" +
                "who='" + who + '\'' +
                ", when=" + when +
                ", what=" + what +
                ", where=" + where +
                ", how='" + how + '\'' +
                '}';
    }
}
