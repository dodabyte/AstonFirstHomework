package org.example.firsthomework.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.firsthomework.dto.GroupDto;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.service.GroupServiceImpl;
import org.example.firsthomework.service.global.GroupService;
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
class GroupServletTest {
    @InjectMocks
    private static GroupServlet groupServlet;
    private static GroupService mockGroupService;
    private static GroupServiceImpl oldInstance;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    @BeforeAll
    static void beforeAll() {
        mockGroupService = Mockito.mock(GroupService.class);
        try {
            Field instance = GroupServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (GroupServiceImpl) instance.get(instance);
            instance.set(instance, mockGroupService);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        groupServlet = new GroupServlet();
    }

    @AfterAll
    static void afterAll() {
        try {
            Field instance = GroupServiceImpl.class.getDeclaredField("instance");
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
        Mockito.reset(mockGroupService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("group/all").when(mockRequest).getPathInfo();

        groupServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockGroupService).findAll();
    }

    @Test
    void doGetById() throws IOException, EntityNotFoundException {
        Mockito.doReturn("group/1").when(mockRequest).getPathInfo();

        groupServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockGroupService).findById(Mockito.anyLong());
    }

    @Test
    void doGetIllegalArgumentException() throws IOException, IllegalArgumentException {
        Mockito.doReturn("group/2q").when(mockRequest).getPathInfo();

        groupServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(400, "Bad Request");
    }

    @Test
    void doGetEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("teacher/1000").when(mockRequest).getPathInfo();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockGroupService).findById(1000);

        groupServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Discipline Not Found");
    }

    @Test
    void doPost() throws IOException, InsertionException {
        String expectedName = "Test Name";
        int expectedCourse = 4;
        int expectedSemester = 8;

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"name\":\"" + expectedName + "\"," +
                        "\"course\":" + expectedCourse + "," +
                        "\"semester\":" + expectedSemester +
                        "}",
                null
        ).when(mockBufferedReader).readLine();

        groupServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<GroupDto.Request> argumentCaptor = ArgumentCaptor.forClass(GroupDto.Request.class);
        Mockito.verify(mockGroupService).insert(argumentCaptor.capture());

        GroupDto.Request result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
        Assertions.assertEquals(expectedCourse, result.getCourse());
        Assertions.assertEquals(expectedSemester, result.getSemester());
    }

    @Test
    void doPut() throws IOException, EntityNotFoundException {
        Mockito.doReturn("group/1").when(mockRequest).getPathInfo();

        int expectedId = 1;
        String expectedName = "New Test Name";
        int expectedCourse = 4;
        int expectedSemester = 8;

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"id\":" + expectedId + "," +
                        "\"name\":\"" + expectedName + "\"," +
                        "\"course\":" + expectedCourse + "," +
                        "\"semester\":" + expectedSemester +
                        "}",
                null
        ).when(mockBufferedReader).readLine();

        groupServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<GroupDto.Update> argumentCaptor = ArgumentCaptor.forClass(GroupDto.Update.class);
        Mockito.verify(mockGroupService).update(argumentCaptor.capture());

        GroupDto.Update result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
        Assertions.assertEquals(expectedCourse, result.getCourse());
        Assertions.assertEquals(expectedSemester, result.getSemester());
    }

    @Test
    void doPutStudentToGroup() throws IOException, EntityNotFoundException {
        Mockito.doReturn("group/1/student/1+2+3").when(mockRequest).getPathInfo();

        groupServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockGroupService).addStudentToGroup(Mockito.anyList(), Mockito.anyLong());
    }

    @Test
    void doPutIllegalArgumentException() throws IOException, IllegalArgumentException {
        Mockito.doReturn("group/3247h123").when(mockRequest).getPathInfo();

        groupServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(400, "Bad Request");
    }

    @Test
    void doPutEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("group/1000").when(mockRequest).getPathInfo();

        int expectedId = 1000;
        String expectedName = "New Test Name";
        int expectedCourse = 4;
        int expectedSemester = 8;

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"id\":" + expectedId + "," +
                        "\"name\":\"" + expectedName + "\"" +
                        "\"course\":" + expectedCourse + "," +
                        "\"semester\":" + expectedSemester +
                        "}",
                null
        ).when(mockBufferedReader).readLine();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockGroupService).update(Mockito.any(GroupDto.Update.class));

        groupServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Group Not Found");
        Mockito.verify(mockGroupService).update(Mockito.any(GroupDto.Update.class));
    }

    @Test
    void doDelete() throws IOException, EntityNotFoundException {
        Mockito.doReturn("group/1").when(mockRequest).getPathInfo();

        groupServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockGroupService).delete(Mockito.anyLong());
    }

    @Test
    void doDeleteStudentToGroup() throws IOException, EntityNotFoundException {
        Mockito.doReturn("group/1/student/2+3").when(mockRequest).getPathInfo();

        groupServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockGroupService).deleteStudentFromGroup(Mockito.anyList());
    }

    @Test
    void doDeleteEntityNotFoundException() throws IOException, EntityNotFoundException {
        Mockito.doReturn("group/1000").when(mockRequest).getPathInfo();
        Mockito.doThrow(new EntityNotFoundException("not found.")).when(mockGroupService).delete(1000);

        groupServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Group Not Found");
    }

    @Test
    void doDeleteIllegalArgumentException() throws IOException, IllegalArgumentException {
        Mockito.doReturn("group/124hj1h4j15").when(mockRequest).getPathInfo();

        groupServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendError(404, "Group Not Found");
    }
}