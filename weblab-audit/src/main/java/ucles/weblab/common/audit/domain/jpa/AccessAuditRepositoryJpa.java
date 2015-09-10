package ucles.weblab.common.audit.domain.jpa;

import org.springframework.data.repository.Repository;
import ucles.weblab.common.audit.domain.AccessAuditEntity;
import ucles.weblab.common.audit.domain.AccessAuditRepository;

/**
 * Implementation of the repository interface which uses Spring Data JPA to manage JPA entities.
 *
 * @since 27/07/15
 */
public interface AccessAuditRepositoryJpa extends AccessAuditRepository, Repository<AccessAuditEntityJpa, Long> {
    @Override
    AccessAuditEntityJpa save(AccessAuditEntity entity);
}
