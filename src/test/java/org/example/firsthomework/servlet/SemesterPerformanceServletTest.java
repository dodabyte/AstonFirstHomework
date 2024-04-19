package org.example.firsthomework.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.firsthomework.dto.SemesterPerformanceDto;
import org.example.firsthomework.dto.StudentDto;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.service.SemesterPerformanceServiceImpl;
import org.example.firsthomework.service.StudentServiceImpl;
import org.example.firsthomework.service.global.SemesterPerformanceService;
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
class SemesterPerformanceServletTest {
    @InjectMocks
    private static SemesterPerformanceServlet semesterPerformanceServlet;
    private static SemesterPerformanceService mockSemesterPerformanceService;
    private static SemesterPerformanceServiceImpl oldInstance;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    @BeforeAll
    static void beforeAll() {
        mockSemesterPerformanceService = Mockito.mock(SemesterPerformanceService.class);
        try {
            Field instance = SemesterPerformanceServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (SemesterPerformanceServiceImpl) instance.get(instance);
            instance.set(instance, mockSemesterPerformanceService);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        semesterPerformanceServlet = new SemesterPerformanceServlet();
    }

    @AfterAll
    static void afterAll() {
        try {
            Field instance = SemesterPerformanceServiceImpl.class.getDeclaredField("instance");
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
        Mockito.reset(mockSemesterPerformanceService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("student/all").when(mockRequest).getPathInfo();

        semesterPerformanceServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockSemesterPerformanceService).findAll();
    }

    @Test
    void doGetById() throws IOException, EntityNotFoundException {
        Mockito.doReturn("semester_performance/1").when(mockRequest).getPathInfo();

        semesterPerformanceServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockSemesterPerformanceService).findById(Mockito.anyLong());
    }

    @Test
    void doGetIllegalArgumentException() throws IOException, IllegalArgumentException {
        Mockito.doReturn("semester_performance/2q").when(mockRequest).getPathInfo();

        semesterPerformanceServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(400, "Bad Request");
    }

    @Test
    void doGetEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("semester_performance/1000").when(mockRequest).getPathInfo();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockSemesterPerformanceService).findById(1000);

        semesterPerformanceServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Semester Performance Not Found");
    }

    @Test
    void doPost() throws IOException, InsertionException {
        int expectedStudentId = 1;
        int expectedDisciplineId = 1;
        String expectedStudent = "{\"id\":" + expectedStudentId + "}";
        String expectedDiscipline = "{\"id\":" + expectedDisciplineId + "}";
        int expectedMark = 100;

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"student\":" + expectedStudent + "," +
                        "\"discipline\":" + expectedDiscipline + "," +
                        "\"mark\":" + expectedMark +
                        "}",
                null
        ).when(mockBufferedReader).readLine();

        semesterPerformanceServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<SemesterPerformanceDto.Request> argumentCaptor = ArgumentCaptor.forClass(SemesterPerformanceDto.Request.class);
        Mockito.verify(mockSemesterPerformanceService).insert(argumentCaptor.capture());

        SemesterPerformanceDto.Request result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedStudentId, result.getStudent().getId());
        Assertions.assertEquals(expectedDisciplineId, result.getDiscipline().getId());
        Assertions.assertEquals(expectedMark, result.getMark());
    }

    @Test
    void doPut() throws IOException, EntityNotFoundException {
        Mockito.doReturn("semester_performance/1").when(mockRequest).getPathInfo();

        int expectedId = 1;
        int expectedStudentId = 1;
        int expectedDisciplineId = 1;
        String expectedStudent = "{\"id\":" + expectedStudentId + "}";
        String expectedDiscipline = "{\"id\":" + expectedDisciplineId + "}";
        int expectedMark = 100;

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"id\":" + expectedId + "," +
                        "\"student\":" + expectedStudent + "," +
                        "\"discipline\":" + expectedDiscipline + "," +
                        "\"mark\":" + expectedMark +
                        "}",
                null
        ).when(mockBufferedReader).readLine();

        semesterPerformanceServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<SemesterPerformanceDto.Update> argumentCaptor = ArgumentCaptor.forClass(SemesterPerformanceDto.Update.class);
        Mockito.verify(mockSemesterPerformanceService).update(argumentCaptor.capture());

        SemesterPerformanceDto.Update result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedStudentId, result.getStudent().getId());
        Assertions.assertEquals(expectedDisciplineId, result.getDiscipline().getId());
        Assertions.assertEquals(expectedMark, result.getMark());
    }

    @Test
    void doPutIllegalArgumentException() throws IOException, IllegalArgumentException {
        Mockito.doReturn("semester_performance/12rgd").when(mockRequest).getPathInfo();

        semesterPerformanceServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(400, "Bad Request");
    }

    @Test
    void doPutEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("semester_performance/1000").when(mockRequest).getPathInfo();

        int expectedId = 1;
        int expectedStudentId = 1;
        int expectedDisciplineId = 1;
        String expectedStudent = "{\"id\":" + expectedStudentId + "}";
        String expectedDiscipline = "{\"id\":" + expectedDisciplineId + "}";
        int expectedMark = 100;

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"id\":" + expectedId + "," +
                        "\"student\":" + expectedStudent + "," +
                        "\"discipline\":" + expectedDiscipline + "," +
                        "\"mark\":" + expectedMark +
                        "}",
                null
        ).when(mockBufferedReader).readLine();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockSemesterPerformanceService).update(Mockito.any(SemesterPerformanceDto.Update.class));

        semesterPerformanceServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Semester Performance Not Found");
        Mockito.verify(mockSemesterPerformanceService).update(Mockito.any(SemesterPerformanceDto.Update.class));
    }

    @Test
    void doDelete() throws IOException, EntityNotFoundException {
        Mockito.doReturn("semester_performance/1").when(mockRequest).getPathInfo();

        semesterPerformanceServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockSemesterPerformanceService).delete(Mockito.anyLong());
    }

    @Test
    void doDeleteEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("semester_performance/1000").when(mockRequest).getPathInfo();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockSemesterPerformanceService).delete(1000);

        semesterPerformanceServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Semester Performance Not Found");
    }

    @Test
    void doDeleteIllegalArgumentException() throws IOException, IllegalArgumentException {
        Mockito.doReturn("semester_performance/sdgk32123").when(mockRequest).getPathInfo();

        semesterPerformanceServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Semester Performance Not Found");
    }
}