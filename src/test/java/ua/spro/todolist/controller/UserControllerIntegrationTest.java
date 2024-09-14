package ua.spro.todolist.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ua.spro.todolist.model.dto.RegistrationRequest;
import ua.spro.todolist.model.dto.UserDto;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void registerUser_shouldReturnUserDto_whenValidRequest() throws Exception {
    // Arrange
    RegistrationRequest registrationRequest = new RegistrationRequest("Kate", "katepass");
    String requestBody = objectMapper.writeValueAsString(registrationRequest);

    // Act
    MvcResult result =
        mockMvc
            .perform(
                post("/api/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andReturn();

    // Print response to console before assertions
    String responseContent = result.getResponse().getContentAsString();
    System.out.println("Response: " + responseContent);

    // Now make assertions
    assertEquals(200, result.getResponse().getStatus()); // Check HTTP status code
    assertEquals("application/json", result.getResponse().getContentType()); // Check content type

    // Use ObjectMapper to parse the response
    UserDto responseUser = objectMapper.readValue(responseContent, UserDto.class);
    assertEquals("Kate", responseUser.username());
  }
}
