package ucles.weblab.common.audit.domain;

/**
 * DDD factory interface, to create new entity objects.
 *
 * @since 27/07/15
 */
public interface AuditFactory {
    /**
     * Create a new access audit record and populate it with all the data from the value object.
     * The audit record identity will be generated as a sequential number.
     *
     * @param data value object containing all required data for the access audit
     * @return the newly-created entity
     */
    AccessAuditEntity newAccessAudit(AccessAudit data);
}
