package ucles.weblab.common.audit.domain;

import ucles.weblab.common.domain.Buildable;

import java.net.InetAddress;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;

/**
 * Value object (i.e. unidentified) representation of an access audit record.
 *
 * @since 27/07/15
 */
public interface AccessAudit extends Buildable<AccessAudit> {
    /** Username */
    Optional<String> getWho();
    /** Time of access */
    Instant getWhen();
    URI getWhat();
    /** Client IP address */
    InetAddress getWhere();
    /** User agent. */
    Optional<String> getHow();

    interface Builder extends Buildable.Builder<AccessAudit> {
        Builder who(Optional<String> who);
        Builder when(Instant when);
        Builder what(URI what);
        Builder where(InetAddress where);
        Builder how(Optional<String> how);
    }
}
