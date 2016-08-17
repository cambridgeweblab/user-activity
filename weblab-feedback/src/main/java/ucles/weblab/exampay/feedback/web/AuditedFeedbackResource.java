package ucles.weblab.exampay.feedback.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import ucles.weblab.common.schema.webapi.JsonSchema;
import ucles.weblab.common.schema.webapi.JsonSchemaMetadata;

import java.time.Instant;

/**
 * Feedback, with added information about the submitter.
 *
 * @since 12/01/16
 */
public class AuditedFeedbackResource extends FeedbackResource {
    @JsonSchema(format = JsonValueFormat.EMAIL_VALUE, readOnly = true)
    @JsonSchemaMetadata(title = "Username", order = 50)
    private String username;

    @JsonSchema(format = JsonValueFormat.DATE_TIME_VALUE, readOnly = true)
    @JsonSchemaMetadata(title = "Time", order = 40)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    @JsonSchema(format = JsonValueFormat.IP_ADDRESS_VALUE, readOnly = true)
    @JsonSchemaMetadata(title = "IP Address", order = 70)
    private String ipAddress;

    @JsonSchema(readOnly = true)
    @JsonSchemaMetadata(title = "Browser", order = 60)
    private String browser;

    protected AuditedFeedbackResource() {
        // For Jackson
    }

    public AuditedFeedbackResource(String pageName, int score, String comments, String username, Instant timestamp, String ipAddress, String browser) {
        super(pageName, score, comments);
        this.username = username;
        this.timestamp = timestamp;
        this.ipAddress = ipAddress;
        this.browser = browser;
    }

    public String getUsername() {
        return username;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getBrowser() {
        return browser;
    }
}
