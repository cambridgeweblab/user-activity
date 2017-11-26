package ucles.weblab.common.audit.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ucles.weblab.common.audit.domain.AccessAudit;
import ucles.weblab.common.audit.domain.AccessAuditEntity;
import ucles.weblab.common.audit.domain.AccessAuditRepository;
import ucles.weblab.common.audit.domain.AuditFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;

/**
 * Aspect which intercepts methods annotated with {@link ucles.weblab.common.webapi.AccessAudited @AccessAudited}
 * and logs their access using {@link ucles.weblab.common.audit.domain.AccessAuditRepository}.
 *
 * @since 28/07/15
 */
@Aspect
@SuppressWarnings({"PMD.UnusedPrivateMethod", "PMD.UncommentedEmptyMethodBody"}) // These are how you declare @Pointcuts
public class AccessAuditAspect {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AccessAuditRepository accessAuditRepository;
    private final AuditFactory auditFactory;
    private final Supplier<AccessAudit.Builder> accessAuditBuilder;
    private Clock clock = Clock.systemUTC();

    public AccessAuditAspect(AccessAuditRepository accessAuditRepository, AuditFactory auditFactory, Supplier<AccessAudit.Builder> accessAuditBuilder) {
        this.accessAuditRepository = accessAuditRepository;
        this.auditFactory = auditFactory;
        this.accessAuditBuilder = accessAuditBuilder;
    }

    @Autowired(required = false)
        // will fall back to default system UTC clock
    void configureClock(Clock clock) {
        logger.warn("Clock overridden with " + clock);
        this.clock = clock;
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    private void inRestController() {
    }

    @Pointcut("execution(public * *(..)) && @annotation(ucles.weblab.common.webapi.AccessAudited)")
    private void publicMethodAnnotatedWithAccessAudited() {
    }

    @AfterReturning("inRestController() && publicMethodAnnotatedWithAccessAudited()")
    public void logAccess() {
        final Optional<HttpServletRequest> currentRequest = getCurrentRequest();
        final AccessAuditEntity accessAudit = auditFactory.newAccessAudit(accessAuditBuilder.get()
                .when(Instant.now(clock))
                .what(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri())
                .who(getCurrentAuthenticatedUser().map(Authentication::getName))
                .how(currentRequest.map(r -> r.getHeader(HttpHeaders.USER_AGENT)))
                .where(currentRequest.map(this::getIpAddr).get())
                .get());
        accessAuditRepository.save(accessAudit);
        logger.debug("Logged: " + accessAudit);
    }

    private static Optional<Authentication> getCurrentAuthenticatedUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication);
    }

    private static Optional<HttpServletRequest> getCurrentRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(a -> a instanceof ServletRequestAttributes)
                .map(a -> ((ServletRequestAttributes) a).getRequest());
    }

    private InetAddress getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.indexOf(',') > 0) {
            ip = ip.substring(0, ip.indexOf(',')); // Chop at a comma if there's a list.
        }
        try {
            return InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            logger.warn("IP address could not be converted: '" + ip + "', using loopback.");
            return InetAddress.getLoopbackAddress();
        }
    }

}
