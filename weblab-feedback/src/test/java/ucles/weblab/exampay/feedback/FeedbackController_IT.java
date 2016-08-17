package ucles.weblab.exampay.feedback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import ucles.weblab.exampay.feedback.web.FeedbackResource;

import java.util.UUID;
import javax.transaction.Transactional;

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
@SpringApplicationConfiguration
@WebAppConfiguration("classpath:/public")
@Transactional
public class FeedbackController_IT extends ucles.weblab.common.test.webapi.AbstractRestController_IT {
    @Configuration
    @EnableAutoConfiguration
    public static class Config {
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

    @Test
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
