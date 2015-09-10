package ucles.weblab.common.audit.domain;

/**
 * Persistence-technology-neutral interface representing a persistable access audit record.
 *
 * @since 27/07/15
 */
public interface AccessAuditEntity extends AccessAudit {
    Long getId();
    boolean isNew();
}
