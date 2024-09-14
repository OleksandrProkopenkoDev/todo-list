package ua.spro.todolist.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.spro.todolist.model.dto.RegistrationRequest;
import ua.spro.todolist.model.dto.TaskDto;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Nested
  class GetTasks {

    @Test
    public void getTasks_shouldReturnTasks_whenAuthenticated() throws Exception {
      // Perform login for Alice
      MockHttpSession aliceSession = loginUser("Alice", "alicepass");
      // Act: Call the 'getTasks' endpoint using the session
      MvcResult result =
          mockMvc
              .perform(
                  get("/api/task")
                      .session(aliceSession) // Use the authenticated session
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();

      assertEquals(200, result.getResponse().getStatus());
      assertEquals("application/json", result.getResponse().getContentType());
    }

    @Test
    public void getTasksForBob_shouldReturnTasks_whenAuthenticated() throws Exception {
      // Perform login for Bob
      MockHttpSession bobSession = loginUser("Bob", "bobpass");

      MvcResult result =
          mockMvc
              .perform(
                  get("/api/task")
                      .session(bobSession) // Use Bob's session
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();

      String responseContent = result.getResponse().getContentAsString();

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
      MockHttpSession adminSession = loginUser("admin", "password");

      MvcResult result =
          mockMvc
              .perform(
                  get("/api/task").session(adminSession).contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();

      String responseContent = result.getResponse().getContentAsString();

      assertEquals(200, result.getResponse().getStatus());
      assertEquals("application/json", result.getResponse().getContentType());

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

      assertEquals(401, result.getResponse().getStatus());
    }

    @Test
    public void getTasksForNewUser_shouldReturnEmptyList_whenNoTasksAssigned() throws Exception {
      // Register a user
      registerUser("newuser", "newuserpass");

      // Perform login for a new user (e.g., a newly registered user without any tasks)
      MockHttpSession newUserSession = loginUser("newuser", "newuserpass");

      MvcResult result =
          mockMvc
              .perform(
                  get("/api/task").session(newUserSession).contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();

      String responseContent = result.getResponse().getContentAsString();

      assertEquals(200, result.getResponse().getStatus());
      assertEquals("application/json", result.getResponse().getContentType());
      // Assert that the response content is an empty array
      assertEquals("[]", responseContent);
    }

    @Test
    public void getTasksForAlice_shouldReturnCompletedTasks_whenCompletedFilterApplied()
        throws Exception {
      //      registerUser("alice", "alicepass");
      // Perform login for Alice
      MockHttpSession aliceSession = loginUser("Alice", "alicepass");

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

      // Check if response contains only completed tasks
      assertEquals(200, result.getResponse().getStatus());
      assertEquals("application/json", result.getResponse().getContentType());
    }

    @Test
    public void getTasksForBob_shouldReturnTasksDueBeforeDate_whenDueDateBeforeFilterApplied()
        throws Exception {
      //      registerUser("Bob", "bobpass");
      // Perform login for Bob
      MockHttpSession bobSession = loginUser("Bob", "bobpass");

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

      // Check if response contains tasks with due dates before the specified date
      assertEquals(200, result.getResponse().getStatus());
      assertEquals("application/json", result.getResponse().getContentType());
    }
  }

  @Test
  public void createTask_shouldReturnCreatedTask_whenValidDataProvided() throws Exception {
    // Register and login user
    registerUser("user", "userpass");

    MvcResult loginResult =
        mockMvc
            .perform(post("/login").param("username", "user").param("password", "userpass"))
            .andExpect(status().is(302))
            .andReturn();

    MockHttpSession userSession = (MockHttpSession) loginResult.getRequest().getSession();

    // For file attachments
    MockMultipartFile attachment1 =
        new MockMultipartFile(
            "attachments",
            "attachment1.txt",
            "text/plain",
            "Attachment content 1".getBytes(StandardCharsets.UTF_8));
    MockMultipartFile attachment2 =
        new MockMultipartFile(
            "attachments",
            "attachment2.txt",
            "text/plain",
            "Attachment content 2".getBytes(StandardCharsets.UTF_8));

    // Create a new task with form-data (string params and file attachments)
    assert userSession != null;
    MvcResult result =
        mockMvc
            .perform(
                multipart("/api/task")
                    .file(attachment1)
                    .file(attachment2)
                    .param("title", "Task Title")
                    .param("description", "Task Description")
                    .param("dueDate", LocalDateTime.now().plusDays(1).toString())
                    .param("completed", "false")
                    .session(userSession)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();

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
  public void updateTask_shouldReturnUpdatedTask_whenValidDataProvided() throws Exception {
    // Register and login user
    registerUser("user", "userpass");

    MvcResult loginResult =
        mockMvc
            .perform(post("/login").param("username", "user").param("password", "userpass"))
            .andExpect(status().is(302))
            .andReturn();

    MockHttpSession userSession = (MockHttpSession) loginResult.getRequest().getSession();

    assert userSession != null;
    MvcResult createResult =
        mockMvc
            .perform(
                multipart("/api/task")
                    .file(
                        new MockMultipartFile(
                            "attachments",
                            "initialAttachment.txt",
                            "text/plain",
                            "Initial content".getBytes(StandardCharsets.UTF_8)))
                    .param("title", "Initial Title")
                    .param("description", "Initial Description")
                    .param("dueDate", LocalDateTime.now().plusDays(1).toString())
                    .param("completed", "false")
                    .session(userSession)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andReturn();

    String createResponseContent = createResult.getResponse().getContentAsString();
    TaskDto createdTask = objectMapper.readValue(createResponseContent, TaskDto.class);

    // For file attachments
    MockMultipartFile updatedAttachment =
        new MockMultipartFile(
            "attachments",
            "updatedAttachment.txt",
            "text/plain",
            "Updated content".getBytes(StandardCharsets.UTF_8));

    // Update the task
    MvcResult updateResult =
        mockMvc
            .perform(
                multipart("/api/task/{taskId}", createdTask.id())
                    .file(updatedAttachment)
                    .param("title", "Updated Title")
                    .param("description", "Updated Description")
                    .param("dueDate", LocalDateTime.now().plusDays(2).toString())
                    .param("completed", "true")
                    .session(userSession)
                    .with(
                        request -> {
                          request.setMethod(HttpMethod.PUT.name());
                          return request;
                        })
                    .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andReturn();

    String updateResponseContent = updateResult.getResponse().getContentAsString();

    // Check the response
    assertEquals(200, updateResult.getResponse().getStatus());
    assertEquals("application/json", updateResult.getResponse().getContentType());

    TaskDto updatedTask = objectMapper.readValue(updateResponseContent, TaskDto.class);
    assertNotNull(updatedTask.id()); // Ensure the task ID is preserved
    assertEquals("Updated Title", updatedTask.title());
    assertEquals("Updated Description", updatedTask.description());
    assertTrue(updatedTask.completed());
  }

  @Test
  public void deleteTask_shouldReturnNoContent_whenTaskDeleted() throws Exception {
    registerUser("user", "userpass");

    MvcResult loginResult =
        mockMvc
            .perform(post("/login").param("username", "user").param("password", "userpass"))
            .andExpect(status().is(302))
            .andReturn();

    MockHttpSession userSession = (MockHttpSession) loginResult.getRequest().getSession();

    assert userSession != null;
    MvcResult createResult =
        mockMvc
            .perform(
                multipart("/api/task")
                    .file(
                        new MockMultipartFile(
                            "attachments",
                            "attachment.txt",
                            "text/plain",
                            "Attachment content".getBytes(StandardCharsets.UTF_8)))
                    .param("title", "Task Title")
                    .param("description", "Task Description")
                    .param("dueDate", LocalDateTime.now().plusDays(1).toString())
                    .param("completed", "false")
                    .session(userSession)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andReturn();

    String createResponseContent = createResult.getResponse().getContentAsString();
    TaskDto createdTask = objectMapper.readValue(createResponseContent, TaskDto.class);

    // Delete the task
    MvcResult deleteResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.delete("/api/task/{taskId}", createdTask.id())
                    .session(userSession))
            .andExpect(status().isNoContent())
            .andReturn();

    // Verify response
    assertEquals(204, deleteResult.getResponse().getStatus()); // No Content status
  }

  private MockHttpSession loginUser(String username, String password) throws Exception {
    return (MockHttpSession)
        mockMvc
            .perform(post("/login").param("username", username).param("password", password))
            .andExpect(status().is(302))
            .andReturn()
            .getRequest()
            .getSession();
  }

  private void registerUser(String username, String password) throws Exception {
    RegistrationRequest registrationRequest = new RegistrationRequest(username, password);
    String registrationBody = objectMapper.writeValueAsString(registrationRequest);

    mockMvc
        .perform(
            post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registrationBody))
        .andExpect(status().isOk());
  }
}
