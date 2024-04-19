package org.example.firsthomework.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.firsthomework.dto.DisciplineDto;
import org.example.firsthomework.dto.TeacherDto;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.service.TeacherServiceImpl;
import org.example.firsthomework.service.global.TeacherService;
import org.example.firsthomework.servlet.TeacherServlet;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TeacherServletTest {
    @InjectMocks
    private static TeacherServlet teacherServlet;
    private static TeacherService mockTeacherService;
    private static TeacherServiceImpl oldInstance;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    @BeforeAll
    static void beforeAll() {
        mockTeacherService = Mockito.mock(TeacherService.class);
        try {
            Field instance = TeacherServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (TeacherServiceImpl) instance.get(instance);
            instance.set(instance, mockTeacherService);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        teacherServlet = new TeacherServlet();
    }

    @AfterAll
    static void afterAll() {
        try {
            Field instance = TeacherServiceImpl.class.getDeclaredField("instance");
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
        Mockito.reset(mockTeacherService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("teacher/all").when(mockRequest).getPathInfo();

        teacherServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockTeacherService).findAll();
    }

    @Test
    void doGetById() throws IOException, EntityNotFoundException {
        Mockito.doReturn("teacher/2").when(mockRequest).getPathInfo();

        teacherServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockTeacherService).findById(Mockito.anyLong());
    }

    @Test
    void doGetIllegalArgumentException() throws IOException, IllegalArgumentException {
        Mockito.doReturn("teacher/2q").when(mockRequest).getPathInfo();

        teacherServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(400, "Bad Request");
    }

    @Test
    void doGetEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("teacher/1000").when(mockRequest).getPathInfo();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockTeacherService).findById(1000);

        teacherServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Teacher Not Found");
    }

    @Test
    void doPost() throws IOException, InsertionException {
        String expectedLastName = "Test Last Name";
        String expectedFirstName = "Test First Name";
        String expectedPatronymic = "Test Patronymic";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"lastName\":\"" + expectedLastName + "\"," +
                        "\"firstName\":\"" + expectedFirstName + "\"," +
                        "\"patronymic\":\"" + expectedPatronymic + "\"" +
                        "}",
                null
        ).when(mockBufferedReader).readLine();

        teacherServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<TeacherDto.Request> argumentCaptor = ArgumentCaptor.forClass(TeacherDto.Request.class);
        Mockito.verify(mockTeacherService).insert(argumentCaptor.capture());

        TeacherDto.Request result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedLastName, result.getLastName());
        Assertions.assertEquals(expectedFirstName, result.getFirstName());
        Assertions.assertEquals(expectedPatronymic, result.getPatronymic());
    }

    @Test
    void doPut() throws IOException, EntityNotFoundException {
        Mockito.doReturn("teacher/9").when(mockRequest).getPathInfo();

        int expectedId = 9;
        String expectedLastName = "New Test Last Name";
        String expectedFirstName = "New Test First Name";
        String expectedPatronymic = "New Test Patronymic";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"id\": " + expectedId +"," +
                        "\"lastName\":\"" + expectedLastName + "\"," +
                        "\"firstName\":\"" + expectedFirstName + "\"," +
                        "\"patronymic\":\"" + expectedPatronymic + "\"" +
                        "}",
                null
        ).when(mockBufferedReader).readLine();

        teacherServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<TeacherDto.Update> argumentCaptor = ArgumentCaptor.forClass(TeacherDto.Update.class);
        Mockito.verify(mockTeacherService).update(argumentCaptor.capture());

        TeacherDto.Update result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedLastName, result.getLastName());
        Assertions.assertEquals(expectedFirstName, result.getFirstName());
        Assertions.assertEquals(expectedPatronymic, result.getPatronymic());
    }

    @Test
    void doPutEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("teacher/1000").when(mockRequest).getPathInfo();

        int expectedId = 1000;
        String expectedLastName = "Test Last Name";
        String expectedFirstName = "Test First Name";
        String expectedPatronymic = "Test Patronymic";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"id\": " + expectedId +"," +
                        "\"lastName\":\"" + expectedLastName + "\"," +
                        "\"firstName\":\"" + expectedFirstName + "\"," +
                        "\"patronymic\":\"" + expectedPatronymic + "\"" +
                        "}",
                null
        ).when(mockBufferedReader).readLine();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockTeacherService).update(Mockito.any(TeacherDto.Update.class));

        teacherServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Discipline Not Found");
        Mockito.verify(mockTeacherService).update(Mockito.any(TeacherDto.Update.class));
    }

    @Test
    void doDelete() throws IOException, EntityNotFoundException {
        Mockito.doReturn("teacher/9").when(mockRequest).getPathInfo();

        teacherServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockTeacherService).delete(Mockito.anyLong());
    }

    @Test
    void doDeleteEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("teacher/1000").when(mockRequest).getPathInfo();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockTeacherService).delete(1000);

        teacherServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Teacher Not Found");
    }

    @Test
    void doDeleteIllegalArgumentException() throws IOException, IllegalArgumentException {
        Mockito.doReturn("teacher/sf1f").when(mockRequest).getPathInfo();

        teacherServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Teacher Not Found");
    }
}