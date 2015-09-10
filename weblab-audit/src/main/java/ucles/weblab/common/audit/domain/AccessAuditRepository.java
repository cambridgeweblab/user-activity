package ucles.weblab.common.audit.domain;

/**
 * DDD repository interface - persistence-technology-neutral interface providing repository (i.e. CRUD) methods for
 * manipulating access audit records.
 *
 * <p>
 * Although this is technology neutral, it uses Spring Data naming conventions for methods. This allows the
 * interface to be extended with a Spring Data Repository interface for which an implementation is proxied in
 * at runtime.
 * </p>
 *
 * @since 27/07/15
 */
public interface AccessAuditRepository {
    AccessAuditEntity save(AccessAuditEntity entity);
}
