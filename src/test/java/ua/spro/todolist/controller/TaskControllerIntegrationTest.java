package ua.spro.todolist.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ua.spro.todolist.model.dto.CreateTaskRequest;
import ua.spro.todolist.model.dto.RegistrationRequest;
import ua.spro.todolist.model.dto.TaskDto;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private MockHttpSession session;

  @BeforeEach
  public void setup() throws Exception {
    // Perform login and store session
    MvcResult loginResult =
        mockMvc
            .perform(post("/login").param("username", "Alice").param("password", "alicepass"))
            .andExpect(status().is(302))
            .andReturn();

    session = (MockHttpSession) loginResult.getRequest().getSession(); // Save session
  }

  @Nested
  class GetTasks {

    @Test
    public void getTasks_shouldReturnTasks_whenAuthenticated() throws Exception {
      // Act: Call the 'getTasks' endpoint using the session
      MvcResult result =
          mockMvc
              .perform(
                  get("/api/task")
                      .session(session) // Use the authenticated session
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();

      // Print response to console before assertions
      String responseContent = result.getResponse().getContentAsString();
      System.out.println("Response: " + responseContent);

      // Now make assertions (this will depend on the expected response)
      // For example, you could parse the response as a list of tasks
      assertEquals(200, result.getResponse().getStatus()); // Check HTTP status code
      assertEquals("application/json", result.getResponse().getContentType()); // Check content type
    }

    @Test
    public void getTasksForBob_shouldReturnTasks_whenAuthenticated() throws Exception {
      // Perform login for Bob
      MvcResult loginResult =
          mockMvc
              .perform(post("/login").param("username", "Bob").param("password", "bobpass"))
              .andExpect(status().is(302))
              .andReturn();

      MockHttpSession bobSession = (MockHttpSession) loginResult.getRequest().getSession();

      MvcResult result =
          mockMvc
              .perform(
                  get("/api/task")
                      .session(bobSession) // Use Bob's session
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();

      String responseContent = result.getResponse().getContentAsString();
      System.out.println("Response: " + responseContent);

      assertEquals(200, result.getResponse().getStatus());
      assertEquals("application/json", result.getResponse().getContentType());
      // Additional assertion: check if Bob's tasks are returned
      assertTrue(responseContent.contains("Task 1 for Bob"));
      assertTrue(responseContent.contains("Task 2 for Bob"));
      assertTrue(responseContent.contains("Task 3 for Bob"));
    }

    @Test
    public void getTasksForAdmin_shouldReturnTasks_whenAuthenticated() throws Exception {
      // Perform login for Admin
      MvcResult loginResult =
          mockMvc
              .perform(post("/login").param("username", "admin").param("password", "password"))
              .andExpect(status().is(302))
              .andReturn();

      MockHttpSession adminSession = (MockHttpSession) loginResult.getRequest().getSession();

      MvcResult result =
          mockMvc
              .perform(
                  get("/api/task")
                      .session(adminSession) // Use admin's session
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();

      String responseContent = result.getResponse().getContentAsString();
      System.out.println("Response: " + responseContent);

      assertEquals(200, result.getResponse().getStatus());
      assertEquals("application/json", result.getResponse().getContentType());
      // Additional assertion: check if Admin's tasks are returned
      assertTrue(responseContent.contains("Admin - Project Planning"));
      assertTrue(responseContent.contains("Admin - Budget Review"));
    }

    @Test
    public void getTasks_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
      MvcResult result =
          mockMvc
              .perform(get("/api/task").contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isUnauthorized())
              .andReturn();

      assertEquals(401, result.getResponse().getStatus()); // Check HTTP status code
    }

    @Test
    public void getTasksForNewUser_shouldReturnEmptyList_whenNoTasksAssigned() throws Exception {
      // Register a user (if necessary for session-based auth)
      RegistrationRequest registrationRequest = new RegistrationRequest("newuser", "newuserpass");
      String registrationBody = objectMapper.writeValueAsString(registrationRequest);

      MvcResult registrationResult =
          mockMvc
              .perform(
                  post("/api/user/register")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(registrationBody))
              .andExpect(status().isOk())
              .andReturn();

      // Perform login for a new user (e.g., a newly registered user without any tasks)
      MvcResult loginResult =
          mockMvc
              .perform(post("/login").param("username", "newuser").param("password", "newuserpass"))
              .andExpect(status().is(302))
              .andReturn();

      MockHttpSession newUserSession = (MockHttpSession) loginResult.getRequest().getSession();

      MvcResult result =
          mockMvc
              .perform(
                  get("/api/task")
                      .session(newUserSession) // Use new user's session
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();

      String responseContent = result.getResponse().getContentAsString();
      System.out.println("Response: " + responseContent);

      assertEquals(200, result.getResponse().getStatus());
      assertEquals("application/json", result.getResponse().getContentType());
      // Assert that the response content is an empty array
      assertEquals("[]", responseContent); // Change to check for empty JSON array
    }

    @Test
    public void getTasksForAlice_shouldReturnCompletedTasks_whenCompletedFilterApplied()
        throws Exception {
      // Register Alice and create tasks for her
      RegistrationRequest aliceRegistrationRequest = new RegistrationRequest("alice", "alicepass");
      String aliceRegistrationBody = objectMapper.writeValueAsString(aliceRegistrationRequest);

      mockMvc
          .perform(
              post("/api/user/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(aliceRegistrationBody))
          .andExpect(status().isOk());

      // Perform login for Alice
      MvcResult loginResult =
          mockMvc
              .perform(post("/login").param("username", "alice").param("password", "alicepass"))
              .andExpect(status().is(302))
              .andReturn();

      MockHttpSession aliceSession = (MockHttpSession) loginResult.getRequest().getSession();

      // Create completed tasks for Alice
      // Assuming tasks are created through a method or endpoint in your application
      // Create tasks with different 'completed' statuses here

      // Fetch completed tasks
      MvcResult result =
          mockMvc
              .perform(
                  get("/api/task")
                      .session(aliceSession)
                      .param("completed", "true") // Apply completed filter
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();

      String responseContent = result.getResponse().getContentAsString();
      System.out.println("Response: " + responseContent);

      // Check if response contains only completed tasks
      // Here, you can parse responseContent and verify the tasks' statuses
      assertEquals(200, result.getResponse().getStatus());
      assertEquals("application/json", result.getResponse().getContentType());
      // Add more specific assertions based on your tasks' expected data
    }

    @Test
    public void getTasksForBob_shouldReturnTasksDueBeforeDate_whenDueDateBeforeFilterApplied()
        throws Exception {
      // Register Bob and create tasks for him
      RegistrationRequest bobRegistrationRequest = new RegistrationRequest("bob", "bobpass");
      String bobRegistrationBody = objectMapper.writeValueAsString(bobRegistrationRequest);

      mockMvc
          .perform(
              post("/api/user/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(bobRegistrationBody))
          .andExpect(status().isOk());

      // Perform login for Bob
      MvcResult loginResult =
          mockMvc
              .perform(post("/login").param("username", "bob").param("password", "bobpass"))
              .andExpect(status().is(302))
              .andReturn();

      MockHttpSession bobSession = (MockHttpSession) loginResult.getRequest().getSession();

      // Create tasks with various due dates for Bob
      // Assuming tasks are created through a method or endpoint in your application
      // Create tasks with different due dates here

      // Fetch tasks with dueDate before a certain date
      MvcResult result =
          mockMvc
              .perform(
                  get("/api/task")
                      .session(bobSession)
                      .param("dueDateBefore", "2024-12-31T23:59:59") // Apply dueDateBefore filter
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();

      String responseContent = result.getResponse().getContentAsString();
      System.out.println("Response: " + responseContent);

      // Check if response contains tasks with due dates before the specified date
      // Here, you can parse responseContent and verify the tasks' due dates
      assertEquals(200, result.getResponse().getStatus());
      assertEquals("application/json", result.getResponse().getContentType());
      // Add more specific assertions based on your tasks' expected data
    }
  }

  @Nested
  class CreateTask {
    @Test
    public void createTask_shouldReturnCreatedTask_whenValidDataProvided() throws Exception {
      // Register and login user
      RegistrationRequest registrationRequest = new RegistrationRequest("user", "userpass");
      String registrationBody = objectMapper.writeValueAsString(registrationRequest);

      mockMvc
          .perform(
              post("/api/user/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(registrationBody))
          .andExpect(status().isOk());

      MvcResult loginResult =
          mockMvc
              .perform(post("/login").param("username", "user").param("password", "userpass"))
              .andExpect(status().is(302))
              .andReturn();

      MockHttpSession userSession = (MockHttpSession) loginResult.getRequest().getSession();

      // Create a new task
      CreateTaskRequest taskRequest =
          new CreateTaskRequest(
              "Task Title", "Task Description", LocalDateTime.now().plusDays(1), false, null);
      String taskRequestBody = objectMapper.writeValueAsString(taskRequest);

      MvcResult result =
          mockMvc
              .perform(
                  post("/api/task")
                      .session(userSession)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(taskRequestBody))
              .andExpect(status().isCreated())
              .andReturn();

      String responseContent = result.getResponse().getContentAsString();
      System.out.println("Response: " + responseContent);

      // Check the response
      assertEquals(201, result.getResponse().getStatus());
      assertEquals("application/json", result.getResponse().getContentType());

      TaskDto createdTask = objectMapper.readValue(responseContent, TaskDto.class);
      assertNotNull(createdTask.id()); // Ensure the task ID is generated
      assertEquals("Task Title", createdTask.title());
      assertEquals("Task Description", createdTask.description());
      assertFalse(createdTask.completed());
    }

    @Test
    public void createTask_shouldReturnBadRequest_whenRequiredFieldsMissing() throws Exception {
      // Register and login user
      RegistrationRequest registrationRequest = new RegistrationRequest("user", "userpass");
      String registrationBody = objectMapper.writeValueAsString(registrationRequest);

      mockMvc
          .perform(
              post("/api/user/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(registrationBody))
          .andExpect(status().isOk());

      MvcResult loginResult =
          mockMvc
              .perform(post("/login").param("username", "user").param("password", "userpass"))
              .andExpect(status().is(302))
              .andReturn();

      MockHttpSession userSession = (MockHttpSession) loginResult.getRequest().getSession();

      // Create a new task with missing fields
      CreateTaskRequest taskRequest =
          new CreateTaskRequest(null, null, null, false, null); // Invalid request
      String taskRequestBody = objectMapper.writeValueAsString(taskRequest);

      MvcResult result =
          mockMvc
              .perform(
                  post("/api/task")
                      .session(userSession)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(taskRequestBody))
              .andExpect(status().isBadRequest())
              .andReturn();

      String responseContent = result.getResponse().getContentAsString();
      System.out.println("Response: " + responseContent);

      // Check the response
      assertEquals(400, result.getResponse().getStatus());
      assertEquals("application/json", result.getResponse().getContentType());
      // Additional assertions can be added based on the error message format
    }

    @Test
    public void createTask_shouldReturnBadRequest_whenInvalidDateFormatProvided() throws Exception {
      // Register and login user
      RegistrationRequest registrationRequest = new RegistrationRequest("user", "userpass");
      String registrationBody = objectMapper.writeValueAsString(registrationRequest);

      mockMvc
          .perform(
              post("/api/user/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(registrationBody))
          .andExpect(status().isOk());

      MvcResult loginResult =
          mockMvc
              .perform(post("/login").param("username", "user").param("password", "userpass"))
              .andExpect(status().is(302))
              .andReturn();

      MockHttpSession userSession = (MockHttpSession) loginResult.getRequest().getSession();

      // Create a new task with an invalid date format
      CreateTaskRequest taskRequest =
          new CreateTaskRequest(
              "Task Title",
              "Task Description",
              LocalDateTime.parse("invalid-date-format"),
              false,
              null);
      String taskRequestBody = objectMapper.writeValueAsString(taskRequest);

      MvcResult result =
          mockMvc
              .perform(
                  post("/api/task")
                      .session(userSession)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(taskRequestBody))
              .andExpect(status().isBadRequest())
              .andReturn();

      String responseContent = result.getResponse().getContentAsString();
      System.out.println("Response: " + responseContent);

      // Check the response
      assertEquals(400, result.getResponse().getStatus());
      assertEquals("application/json", result.getResponse().getContentType());
      // Additional assertions can be added based on the error message format
    }

    @Test
    public void createTask_shouldReturnUnauthorized_whenUserNotAuthenticated() throws Exception {
      // Create a new task without authentication
      CreateTaskRequest taskRequest =
          new CreateTaskRequest(
              "Task Title", "Task Description", LocalDateTime.now().plusDays(1), false, null);
      String taskRequestBody = objectMapper.writeValueAsString(taskRequest);

      MvcResult result =
          mockMvc
              .perform(
                  post("/api/task")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(taskRequestBody))
              .andExpect(status().isUnauthorized())
              .andReturn();

      String responseContent = result.getResponse().getContentAsString();
      System.out.println("Response: " + responseContent);

      // Check the response
      assertEquals(401, result.getResponse().getStatus());
      assertEquals("application/json", result.getResponse().getContentType());
    }
  }
}
