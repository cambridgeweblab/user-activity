package ucles.weblab.common.audit.domain.jpa;

import ucles.weblab.common.audit.domain.AccessAudit;
import ucles.weblab.common.audit.domain.AccessAuditEntity;
import ucles.weblab.common.audit.domain.AuditFactory;

/**
 * Implementation of the factory interface which creates JPA entities, suitable for persistence with
 * {@link AccessAuditRepositoryJpa}.
 *
 * @since 27/07/15
 */
public class AuditFactoryJpa implements AuditFactory {
    @Override
    public AccessAuditEntity newAccessAudit(AccessAudit data) {
        return new AccessAuditEntityJpa(data);
    }
}
