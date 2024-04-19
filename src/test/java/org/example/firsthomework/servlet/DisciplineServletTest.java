package org.example.firsthomework.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.firsthomework.dto.DisciplineDto;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.service.DisciplineServiceImpl;
import org.example.firsthomework.service.global.DisciplineService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
class DisciplineServletTest {
    @InjectMocks
    private static DisciplineServlet disciplineServlet;
    private static DisciplineService mockDisciplineService;
    private static DisciplineServiceImpl oldInstance;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void beforeAll() {
        mockDisciplineService = Mockito.mock(DisciplineService.class);
        try {
            Field instance = DisciplineServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (DisciplineServiceImpl) instance.get(instance);
            instance.set(instance, mockDisciplineService);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        disciplineServlet = new DisciplineServlet();
    }

    @AfterAll
    static void afterAll() {
        try {
            Field instance = DisciplineServiceImpl.class.getDeclaredField("instance");
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
        Mockito.reset(mockDisciplineService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("discipline/all").when(mockRequest).getPathInfo();

        disciplineServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockDisciplineService).findAll();
    }

    @Test
    void doGetById() throws IOException, EntityNotFoundException {
        Mockito.doReturn("discipline/1").when(mockRequest).getPathInfo();

        disciplineServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockDisciplineService).findById(Mockito.anyLong());
    }

    @Test
    void doGetIllegalArgumentException() throws IOException, IllegalArgumentException {
        Mockito.doReturn("teacher/2q").when(mockRequest).getPathInfo();

        disciplineServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(400, "Bad Request");
    }

    @Test
    void doGetEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("teacher/1000").when(mockRequest).getPathInfo();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockDisciplineService).findById(1000);

        disciplineServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Discipline Not Found");
    }

    @Test
    void doPostWithoutTeacher() throws IOException, InsertionException {
        String expectedName = "Test Name";
        String expectedTeacher = "null";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"name\":\"" + expectedName + "\"," +
                        "\"teacher\":" + expectedTeacher +
                        "}",
                null
        ).when(mockBufferedReader).readLine();

        disciplineServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<DisciplineDto.Request> argumentCaptor = ArgumentCaptor.forClass(DisciplineDto.Request.class);
        Mockito.verify(mockDisciplineService).insert(argumentCaptor.capture());

        DisciplineDto.Request result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
        Assertions.assertEquals(expectedTeacher, objectMapper.writeValueAsString(result.getTeacher()));
    }

    @Test
    void doPostWithTeacher() throws IOException, InsertionException {
        String expectedName = "Test Name";
        String expectedTeacherLastName = "Test Last Name";
        String expectedTeacherFirstName = "Test First Name";
        String expectedTeacherPatronymic = "Test Patronymic";

        String expectedTeacher = "{\"lastName\":\"" + expectedTeacherLastName + "\"," +
                "\"firstName\":\"" + expectedTeacherFirstName + "\"," +
                "\"patronymic\":\"" + expectedTeacherPatronymic + "\"" +
                "}";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"name\":\"" + expectedName + "\"," +
                        "\"teacher\":" + expectedTeacher +
                        "}",
                null
        ).when(mockBufferedReader).readLine();

        disciplineServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<DisciplineDto.Request> argumentCaptor = ArgumentCaptor.forClass(DisciplineDto.Request.class);
        Mockito.verify(mockDisciplineService).insert(argumentCaptor.capture());

        DisciplineDto.Request result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
        Assertions.assertEquals(expectedTeacher, objectMapper.writeValueAsString(result.getTeacher()));
    }

    @Test
    void doPut() throws IOException, EntityNotFoundException {
        Mockito.doReturn("discipline/1").when(mockRequest).getPathInfo();

        int expectedId = 1;
        String expectedName = "New Test Name";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"id\":" + expectedId + "," +
                        "\"name\":\"" + expectedName + "\"" +
                        "}",
                null
        ).when(mockBufferedReader).readLine();

        disciplineServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<DisciplineDto.Update> argumentCaptor = ArgumentCaptor.forClass(DisciplineDto.Update.class);
        Mockito.verify(mockDisciplineService).update(argumentCaptor.capture());

        DisciplineDto.Update result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPutTeacherToDiscipline() throws IOException, EntityNotFoundException {
        Mockito.doReturn("discipline/1/teacher/4").when(mockRequest).getPathInfo();

        disciplineServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockDisciplineService).addDisciplineToTeacher(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void doPutIllegalArgumentException() throws IOException, IllegalArgumentException {
        Mockito.doReturn("discipline/2q").when(mockRequest).getPathInfo();

        disciplineServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(400, "Bad Request");
    }

    @Test
    void doPutEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("discipline/1000").when(mockRequest).getPathInfo();

        int expectedId = 1000;
        String expectedName = "New Test Name";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"id\":" + expectedId + "," +
                        "\"name\":\"" + expectedName + "\"" +
                        "}",
                null
        ).when(mockBufferedReader).readLine();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockDisciplineService).update(Mockito.any(DisciplineDto.Update.class));

        disciplineServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Discipline Not Found");
        Mockito.verify(mockDisciplineService).update(Mockito.any(DisciplineDto.Update.class));
    }

    @Test
    void doDelete() throws IOException, EntityNotFoundException {
        Mockito.doReturn("discipline/8").when(mockRequest).getPathInfo();

        disciplineServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockDisciplineService).delete(Mockito.anyLong());
    }

    @Test
    void doDeleteTeacherFromDiscipline() throws IOException, EntityNotFoundException {
        Mockito.doReturn("discipline/1/teacher/4").when(mockRequest).getPathInfo();

        disciplineServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockDisciplineService).deleteDisciplineFromTeacher(Mockito.anyLong());
    }

    @Test
    void doDeleteEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("discipline/1000").when(mockRequest).getPathInfo();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockDisciplineService).delete(1000);

        disciplineServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Discipline Not Found");
    }

    @Test
    void doDeleteIllegalArgumentException() throws IOException, IllegalArgumentException {
        Mockito.doReturn("discipline/sf1f").when(mockRequest).getPathInfo();

        disciplineServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Discipline Not Found");
    }
}