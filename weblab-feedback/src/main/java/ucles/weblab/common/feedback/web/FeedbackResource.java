package ucles.weblab.common.feedback.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.ResourceSupport;
import ucles.weblab.common.schema.webapi.EnumConstant;
import ucles.weblab.common.schema.webapi.JsonSchema;
import ucles.weblab.common.schema.webapi.JsonSchemaMetadata;
import ucles.weblab.common.schema.webapi.MoreFormats;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Feedback resource 
 * 
 * @author Sukhraj
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "feedback")
@XmlAccessorType(XmlAccessType.FIELD)
public class FeedbackResource extends ResourceSupport {

    @XmlAttribute
    @NotNull
    @Size(min = 1, max = 100)
    @JsonSchemaMetadata(title = "Page name", order = 10)
    @JsonSchema(format = MoreFormats.CURRENT_VIEW_CONTEXT)
    private String pageName;
    
    @XmlAttribute
    @Min(0)
    @Max(10)
    @JsonSchemaMetadata(title = "Score out of 10", order = 20)
    @JsonSchema(format = MoreFormats.RATING, enumValues = {
            @EnumConstant("0"), @EnumConstant("1"), @EnumConstant("2"), @EnumConstant("3"), @EnumConstant("4"),
            @EnumConstant("5"), @EnumConstant("6"), @EnumConstant("7"), @EnumConstant("8"), @EnumConstant("9"),
            @EnumConstant("10")
    })
    private Integer score;
    
    @XmlAttribute
    @JsonSchema(format = MoreFormats.TEXTAREA)
    @JsonSchemaMetadata(title = "Comments", order = 30, description = "Additional comments for the page")
    protected String comments;
    
    /**
     * Protected no-arg constructor used by JSON. 
     * 
     */
    protected FeedbackResource(){}

    public FeedbackResource(String pageName, Integer score, String comments) {
        this.pageName = pageName;
        this.score = score;
        this.comments = comments;
    }

    public String getPageName() {
        return pageName;
    }

    public Integer getScore() {
        return score;
    }

    public String getComments() {
        return comments;
    }

}
