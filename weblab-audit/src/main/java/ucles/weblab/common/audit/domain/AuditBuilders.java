package ucles.weblab.common.audit.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ucles.weblab.common.domain.BuilderProxyFactory;

import java.util.function.Supplier;

/**
 * Factory beans for domain object builders.
 *
 * @since 30/07/15
 */
@Configuration
public class AuditBuilders {
    @Autowired
    protected BuilderProxyFactory builderProxyFactory;

    @Bean
    public Supplier<AccessAudit.Builder> accessAuditBuilder() {
        return () -> builderProxyFactory.builder(AccessAudit.Builder.class, AccessAudit.class);
    }
}
