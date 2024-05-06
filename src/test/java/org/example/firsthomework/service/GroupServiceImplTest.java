package org.example.firsthomework.service;

import org.example.firsthomework.dao.*;
import org.example.firsthomework.dto.GroupDto;
import org.example.firsthomework.dto.TeacherDto;
import org.example.firsthomework.entity.Group;
import org.example.firsthomework.entity.Student;
import org.example.firsthomework.entity.Teacher;
import org.example.firsthomework.entity.TeacherDiscipline;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.mapper.DisciplineMapperImpl;
import org.example.firsthomework.mapper.GroupMapperImpl;
import org.example.firsthomework.mapper.TeacherMapperImpl;
import org.example.firsthomework.service.global.DisciplineService;
import org.example.firsthomework.service.global.GroupService;
import org.example.firsthomework.service.global.TeacherService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GroupServiceImplTest {
    private static GroupService groupService;
    private static GroupDao mockGroupDao;
    private static StudentDao mockStudentDao;
    private static GroupDao oldGroupInstance;
    private static StudentDao oldStudentInstance;

    @BeforeAll
    static void beforeAll() {
        mockGroupDao = Mockito.mock(GroupDao.class);
        mockStudentDao = Mockito.mock(StudentDao.class);
        try {
            Field groupInstance = GroupDao.class.getDeclaredField("instance");
            groupInstance.setAccessible(true);
            oldGroupInstance = (GroupDao) groupInstance.get(groupInstance);
            groupInstance.set(groupInstance, mockGroupDao);

            Field studentInstance = StudentDao.class.getDeclaredField("instance");
            studentInstance.setAccessible(true);
            oldStudentInstance = (StudentDao) studentInstance.get(studentInstance);
            studentInstance.set(studentInstance, mockStudentDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        groupService = GroupServiceImpl.getInstance(GroupDao.getInstance(), StudentDao.getInstance(), GroupMapperImpl.getInstance());
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field groupInstance = GroupDao.class.getDeclaredField("instance");
        groupInstance.setAccessible(true);
        groupInstance.set(groupInstance, oldGroupInstance);

        Field studentInstance = StudentDao.class.getDeclaredField("instance");
        studentInstance.setAccessible(true);
        studentInstance.set(studentInstance, oldStudentInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockGroupDao);
        Mockito.reset(mockStudentDao);
    }

    @Test
    void insert() throws InsertionException {
        long expectedId = 1;
        String expectedName = "Test Name";
        int expectedCourse = 4;
        int expectedSemester = 8;

        GroupDto.Request dto = new GroupDto.Request(expectedName, expectedCourse, expectedSemester);

        Mockito.doReturn(Optional.of(new Group())).when(mockGroupDao).findById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockGroupDao).containsById(Mockito.anyLong());
        Mockito.doReturn(expectedId).when(mockGroupDao).insert(Mockito.any(Group.class));
        groupService.insert(dto);

        ArgumentCaptor<Group> argumentCaptor = ArgumentCaptor.forClass(Group.class);
        Mockito.verify(mockGroupDao).insert(argumentCaptor.capture());

        Group result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName()); // несколько ассертов нужно кидать в asserAll
        Assertions.assertEquals(expectedCourse, result.getCourse());
        Assertions.assertEquals(expectedSemester, result.getSemester());
    }

    @Test
    void insertInsertionException() {
        long expectedId = 1;
        String expectedName = "Test Name";
        int expectedCourse = 4;
        int expectedSemester = 8;

        GroupDto.Request dto = new GroupDto.Request(expectedName, expectedCourse, expectedSemester);

        Mockito.doReturn(Optional.empty()).when(mockGroupDao).findById(Mockito.anyLong());
        Mockito.doReturn(expectedId).when(mockGroupDao).insert(Mockito.any(Group.class));

        InsertionException exception = Assertions.assertThrows(
                InsertionException.class,
                () -> groupService.insert(dto));
        Assertions.assertEquals(InsertionException.class, exception.getClass());
    }

    @Test
    void update() throws EntityNotFoundException {
        long expectedId = 1;
        String expectedName = "New Test Name";
        int expectedCourse = 3;
        int expectedSemester = 5;

        GroupDto.Update dto = new GroupDto.Update(expectedId, expectedName, expectedCourse, expectedSemester);

        Mockito.doReturn(true).when(mockGroupDao).containsById(Mockito.anyLong());
        groupService.update(dto);

        ArgumentCaptor<Group> argumentCaptor = ArgumentCaptor.forClass(Group.class);
        Mockito.verify(mockGroupDao).update(argumentCaptor.capture());

        Group result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void updateEntityNotFoundException() {
        long expectedId = 1;
        String expectedName = "New Test Name";
        int expectedCourse = 3;
        int expectedSemester = 5;

        GroupDto.Update dto = new GroupDto.Update(expectedId, expectedName, expectedCourse, expectedSemester);

        Mockito.doReturn(false).when(mockGroupDao).containsById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> groupService.update(dto));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void delete() throws EntityNotFoundException {
        long expectedId = 1;

        Mockito.doReturn(Optional.of(new Group())).when(mockGroupDao).findById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockGroupDao).containsById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockGroupDao).delete(Mockito.any(Group.class));

        boolean result = groupService.delete(expectedId);

        Assertions.assertTrue(result);

        ArgumentCaptor<Group> argumentCaptor = ArgumentCaptor.forClass(Group.class);
        Mockito.verify(mockGroupDao).delete(argumentCaptor.capture());

        Group resultGroup = argumentCaptor.getValue();
        Assertions.assertEquals(0, resultGroup.getId());
    }

    @Test
    void deleteEntityNotFoundException() {
        long expectedId = 1;

        Mockito.doReturn(Optional.empty()).when(mockGroupDao).findById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockGroupDao).containsById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockGroupDao).delete(Mockito.any(Group.class));

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> groupService.delete(expectedId));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void containsById() {
        long expectedId = 1;

        Mockito.doReturn(true).when(mockGroupDao).containsById(Mockito.anyLong());

        boolean result = groupService.containsById(expectedId);

        Assertions.assertTrue(result);
    }

    @Test
    void findAll() {
        groupService.findAll();
        Mockito.verify(mockGroupDao).findAll();
    }

    @Test
    void findById() throws EntityNotFoundException {
        long expectedId = 1;
        String expectedName = "New Test Name";
        int expectedCourse = 3;
        int expectedSemester = 5;

        Optional<Group> group = Optional.of(new Group(expectedId, expectedName, expectedCourse, expectedSemester, null));

        Mockito.doReturn(true).when(mockGroupDao).containsById(Mockito.anyLong());
        Mockito.doReturn(group).when(mockGroupDao).findById(Mockito.anyLong());

        GroupDto.Response dto = groupService.findById(expectedId);

        Assertions.assertEquals(expectedId, dto.getId());
    }

    @Test
    void findByIdEntityNotFoundException() {
        Mockito.doReturn(false).when(mockGroupDao).containsById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> groupService.findById(Mockito.anyLong()));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }
}