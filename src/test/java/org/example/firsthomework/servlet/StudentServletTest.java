package org.example.firsthomework.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.firsthomework.dto.GroupDto;
import org.example.firsthomework.dto.StudentDto;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.service.GroupServiceImpl;
import org.example.firsthomework.service.StudentServiceImpl;
import org.example.firsthomework.service.global.GroupService;
import org.example.firsthomework.service.global.StudentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
class StudentServletTest {
    @InjectMocks
    private static StudentServlet studentServlet;
    private static StudentService mockStudentService;
    private static StudentServiceImpl oldInstance;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    @BeforeAll
    static void beforeAll() {
        mockStudentService = Mockito.mock(StudentService.class);
        try {
            Field instance = StudentServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (StudentServiceImpl) instance.get(instance);
            instance.set(instance, mockStudentService);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        studentServlet = new StudentServlet();
    }

    @AfterAll
    static void afterAll() {
        try {
            Field instance = StudentServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(instance, oldInstance);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockStudentService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("student/all").when(mockRequest).getPathInfo();

        studentServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockStudentService).findAll();
    }

    @Test
    void doGetById() throws IOException, EntityNotFoundException {
        Mockito.doReturn("student/1").when(mockRequest).getPathInfo();

        studentServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockStudentService).findById(Mockito.anyLong());
    }

    @Test
    void doGetIllegalArgumentException() throws IOException, IllegalArgumentException {
        Mockito.doReturn("student/2q").when(mockRequest).getPathInfo();

        studentServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(400, "Bad Request");
    }

    @Test
    void doGetEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("student/1000").when(mockRequest).getPathInfo();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockStudentService).findById(1000);

        studentServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Student Not Found");
    }

    @Test
    void doPost() throws IOException, InsertionException {
        String expectedLastName = "Test Last Name";
        String expectedFirstName = "Test First Name";
        String expectedPatronymic = "Test Patronymic";

        int expectedGroupId = 1;
        String expectedGroup = "{\"id\":" + expectedGroupId + "}";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"lastName\":\"" + expectedLastName + "\"," +
                        "\"firstName\":\"" + expectedFirstName + "\"," +
                        "\"patronymic\":\"" + expectedPatronymic + "\"," +
                        "\"group\":" + expectedGroup +
                        "}",
                null
        ).when(mockBufferedReader).readLine();

        studentServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<StudentDto.Request> argumentCaptor = ArgumentCaptor.forClass(StudentDto.Request.class);
        Mockito.verify(mockStudentService).insert(argumentCaptor.capture());

        StudentDto.Request result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedLastName, result.getLastName());
        Assertions.assertEquals(expectedFirstName, result.getFirstName());
        Assertions.assertEquals(expectedPatronymic, result.getPatronymic());
        Assertions.assertEquals(expectedGroupId, result.getGroup().getId());
    }

    @Test
    void doPut() throws IOException, EntityNotFoundException {
        Mockito.doReturn("student/1").when(mockRequest).getPathInfo();

        int expectedId = 1;
        String expectedLastName = "New Test Last Name";
        String expectedFirstName = "New Test First Name";
        String expectedPatronymic = "New Test Patronymic";

        int expectedGroupId = 1;
        String expectedGroup = "{\"id\":" + expectedGroupId + "}";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"id\":" + expectedId + "," +
                        "\"lastName\":\"" + expectedLastName + "\"," +
                        "\"firstName\":\"" + expectedFirstName + "\"," +
                        "\"patronymic\":\"" + expectedPatronymic + "\"," +
                        "\"group\":" + expectedGroup +
                        "}",
                        null,
                null
        ).when(mockBufferedReader).readLine();

        studentServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<StudentDto.Update> argumentCaptor = ArgumentCaptor.forClass(StudentDto.Update.class);
        Mockito.verify(mockStudentService).update(argumentCaptor.capture());

        StudentDto.Update result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedLastName, result.getLastName());
        Assertions.assertEquals(expectedFirstName, result.getFirstName());
        Assertions.assertEquals(expectedPatronymic, result.getPatronymic());
        Assertions.assertEquals(expectedGroupId, result.getGroup().getId());
    }

    @Test
    void doPutStudentToGroup() throws IOException, EntityNotFoundException {
        Mockito.doReturn("student/1/group/1").when(mockRequest).getPathInfo();

        studentServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockStudentService).addGroupToStudent(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void doPutIllegalArgumentException() throws IOException, IllegalArgumentException {
        Mockito.doReturn("student/12rgd").when(mockRequest).getPathInfo();

        studentServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(400, "Bad Request");
    }

    @Test
    void doPutEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("student/1000").when(mockRequest).getPathInfo();

        int expectedId = 1;
        String expectedLastName = "New Test Last Name";
        String expectedFirstName = "New Test First Name";
        String expectedPatronymic = "New Test Patronymic";

        int expectedGroupId = 1;
        String expectedGroup = "{\"id\":" + expectedGroupId + "}";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"id\":" + expectedId + "," +
                        "\"lastName\":\"" + expectedLastName + "\"," +
                        "\"firstName\":\"" + expectedFirstName + "\"," +
                        "\"patronymic\":\"" + expectedPatronymic + "\"," +
                        "\"group\":" + expectedGroup +
                        "}",
                "}",
                null
        ).when(mockBufferedReader).readLine();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockStudentService).update(Mockito.any(StudentDto.Update.class));

        studentServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Student Not Found");
        Mockito.verify(mockStudentService).update(Mockito.any(StudentDto.Update.class));
    }

    @Test
    void doDelete() throws IOException, EntityNotFoundException {
        Mockito.doReturn("student/1").when(mockRequest).getPathInfo();

        studentServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockStudentService).delete(Mockito.anyLong());
    }

    @Test
    void doDeleteStudentToGroup() throws IOException, EntityNotFoundException {
        Mockito.doReturn("student/1/group").when(mockRequest).getPathInfo();

        studentServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockStudentService).deleteGroupFromStudent(Mockito.anyLong());
    }

    @Test
    void doDeleteEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("student/1000").when(mockRequest).getPathInfo();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockStudentService).delete(1000);

        studentServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Student Not Found");
    }

    @Test
    void doDeleteIllegalArgumentException() throws IOException, IllegalArgumentException {
        Mockito.doReturn("student/sdgk32123").when(mockRequest).getPathInfo();

        studentServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Student Not Found");
    }
}