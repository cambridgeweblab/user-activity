package ucles.weblab.common.feedback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.factories.JsonSchemaFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import ucles.weblab.common.audit.aspect.AccessAuditAspect;
import ucles.weblab.common.audit.domain.AccessAudit;
import ucles.weblab.common.audit.domain.AccessAuditRepository;
import ucles.weblab.common.audit.domain.AuditBuilders;
import ucles.weblab.common.audit.domain.AuditFactory;
import ucles.weblab.common.audit.domain.jpa.AccessAuditEntityJpa;
import ucles.weblab.common.audit.domain.jpa.AuditFactoryJpa;
import ucles.weblab.common.domain.BuilderProxyFactory;
import ucles.weblab.common.feedback.domain.Feedback;
import ucles.weblab.common.feedback.domain.FeedbackFactory;
import ucles.weblab.common.feedback.domain.FeedbackRepository;
import ucles.weblab.common.feedback.domain.jpa.FeedbackEntityJpa;
import ucles.weblab.common.feedback.domain.jpa.FeedbackFactoryJpa;
import ucles.weblab.common.feedback.web.*;
import ucles.weblab.common.schema.webapi.EnumSchemaCreator;
import ucles.weblab.common.schema.webapi.ResourceSchemaCreator;
import ucles.weblab.common.security.SecurityChecker;
import ucles.weblab.common.xc.service.CrossContextConversionService;
import ucles.weblab.common.xc.service.CrossContextConversionServiceImpl;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class to test REST API
 *
 * @author Sukhraj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest("classpath:/public")
@Transactional
public class FeedbackController_IT extends ucles.weblab.common.test.webapi.AbstractRestController_IT {
    @Configuration
    @EnableAutoConfiguration
    @EnableJpaRepositories(basePackageClasses = {FeedbackRepository.class, AccessAuditRepository.class})
    @EntityScan(basePackageClasses = {FeedbackEntityJpa.class, AccessAuditEntityJpa.class, Jsr310JpaConverters.class, Config.Converters.class})
    @ComponentScan(basePackageClasses = { FeedbackController.class })
    @Import({AuditBuilders.class})
    @EnableAspectJAutoProxy
    public static class Config {
        @Bean
        BuilderProxyFactory builderProxyFactory() {
            return new BuilderProxyFactory();
        }

        @Bean
        public AuditFactory auditFactory() {
            return new AuditFactoryJpa();
        }

        @Bean
        public AccessAuditAspect accessAuditAspect(AccessAuditRepository accessAuditRepository, AuditFactory auditFactory,
                                                   Supplier<AccessAudit.Builder> accessAuditBuilder) {
            return new AccessAuditAspect(accessAuditRepository, auditFactory, accessAuditBuilder);
        }

        @Bean
        FeedbackFactory feedbackFactory() {
            return new FeedbackFactoryJpa();
        }

        @Bean
        public Function<FeedbackResource, Feedback> feedbackResourceToValue() {
            return FeedbackAdaptor::new;
        }

        @Bean
        FeedbackDelegate feedbackDelegate(FeedbackFactory feedbackFactory,
                                          FeedbackRepository feedbackRepository,
                                          AccessAuditRepository accessAuditRepository,
                                          FeedbackResourceAssembler feedbackResourceAssembler) {
            return new FeedbackDelegate(feedbackFactory,
                    feedbackRepository,
                    accessAuditRepository,
                    feedbackResourceAssembler,
                    feedbackResourceToValue());
        }

        @Bean
        @ConditionalOnMissingBean(MethodSecurityExpressionHandler.class)
        MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
            return new DefaultMethodSecurityExpressionHandler();
        }

        @Bean
        SecurityChecker securityChecker(MethodSecurityExpressionHandler handler) {
            return new SecurityChecker(handler);
        }

        @Bean
        CrossContextConversionService crossContextConversionService() {
            return new CrossContextConversionServiceImpl();
        }

        @Bean
        EnumSchemaCreator enumSchemaCreator(final JsonSchemaFactory schemaFactory) {
            return new EnumSchemaCreator();
        }

        @Bean
        JsonSchemaFactory jsonSchemaFactory() {
            return new JsonSchemaFactory();
        }

        @Bean
        public ResourceSchemaCreator resourceSchemaCreator(SecurityChecker securityChecker,
                                                           CrossContextConversionService crossContextConversionService,
                                                           EnumSchemaCreator enumSchemaCreator,
                                                           JsonSchemaFactory jsonSchemaFactory,
                                                           MessageSource messageSource) {

            return new ResourceSchemaCreator(securityChecker,
                    new ObjectMapper(),
                    crossContextConversionService,
                    enumSchemaCreator,
                    jsonSchemaFactory,
                    messageSource);
        }

        static class Converters {
            @Converter(autoApply = true)
            public static class UriConverter implements AttributeConverter<URI, String> {
                @Override
                public String convertToDatabaseColumn(URI uri) {
                    return uri == null? null : uri.toString();
                }

                @Override
                public URI convertToEntityAttribute(String dbData) {
                    return dbData == null? null : URI.create(dbData);
                }
            }

            @Converter(autoApply = true)
            public static class InetAddressConverter implements AttributeConverter<InetAddress, String> {
                @Override
                public String convertToDatabaseColumn(InetAddress inetAddress) {
                    return inetAddress.getHostAddress();
                }

                @Override
                public InetAddress convertToEntityAttribute(String dbData) {
                    try {
                        return InetAddress.getByName(dbData);
                    } catch (UnknownHostException e) {
                        throw new IllegalArgumentException("Unknown value for InetAddress: " + dbData, e);
                    }
                }
            }
        }

    }

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void testCreateFeedback() throws Exception {
        FeedbackResource data = new FeedbackResource("Magic Number", 3, "De La Soul");
        final String json = this.json(data);
        final UUID uuid = UUID.randomUUID();

        mockMvc.perform(put("/api/feedback/" + uuid.toString())
                .contentType(APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.score", is(3)));
    }

    @Test(expected = Exception.class)
    public void testNoOverwrite() throws Exception {
        final UUID uuid = UUID.randomUUID();

        FeedbackResource data = new FeedbackResource("Magic Number", 3, "De La Soul");
        String json = this.json(data);

        mockMvc.perform(put("/api/feedback/" + uuid.toString())
                .contentType(APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.score", is(3)));

        data = new FeedbackResource("Magic Number", 4, "Fraud");
        json = this.json(data);
        mockMvc.perform(put("/api/feedback/" + uuid.toString())
                .contentType(APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void testCreateAndReturn() throws Exception {
        FeedbackResource data = new FeedbackResource("Lucky Number", 7, "Vegas, baby");
        final String json = this.json(data);
        final UUID uuid = UUID.randomUUID();

        mockMvc.perform(put("/api/feedback/" + uuid.toString())
                .header("User-Agent", "IntegrationTest/0.1-SNAPSHOT")
                .contentType(APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8));

        MvcResult mvcResult = mockMvc.perform(get("/api/feedback/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.links[?(@.rel=='download')].href", notNullValue()))
                .andReturn();

        String jsonString = mvcResult.getResponse().getContentAsString();
        JsonNode parsedData = new ObjectMapper().readTree(jsonString);

        assertTrue("Expect array", parsedData.get("list").isArray());
        parsedData = parsedData.get("list");
        System.out.println("parsedData.size() = " + parsedData.size());
        JsonNode feedback = parsedData.get(parsedData.size() - 1);
        assertTrue("Expect feedback object", feedback.isObject());
        assertEquals("Expect score", data.getScore().intValue(), feedback.get("score").asInt());
        assertEquals("Expect page", data.getPageName(), feedback.get("pageName").asText());
        assertEquals("Expect comments", data.getComments(), feedback.get("comments").asText());
        assertNotNull("Expect timestamp", feedback.get("timestamp").asText());
        assertEquals("Expect IP address", "/127.0.0.1", feedback.get("ipAddress").asText());
        assertEquals("Expect browser", "IntegrationTest/0.1-SNAPSHOT", feedback.get("browser").asText());
    }

}
