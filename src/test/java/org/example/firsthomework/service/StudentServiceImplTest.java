package org.example.firsthomework.service;

import org.example.firsthomework.dao.GroupDao;
import org.example.firsthomework.dao.StudentDao;
import org.example.firsthomework.dto.GroupDto;
import org.example.firsthomework.dto.StudentDto;
import org.example.firsthomework.entity.Group;
import org.example.firsthomework.entity.Student;
import org.example.firsthomework.entity.Teacher;
import org.example.firsthomework.entity.TeacherDiscipline;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.mapper.GroupMapperImpl;
import org.example.firsthomework.mapper.StudentMapperImpl;
import org.example.firsthomework.service.global.StudentService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class StudentServiceImplTest {
    private static StudentService studentService;
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
        studentService = StudentServiceImpl.getInstance(StudentDao.getInstance(), GroupDao.getInstance(), StudentMapperImpl.getInstance());
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
        String expectedLastName = "Test Last Name";
        String expectedFirstName = "Test First Name";
        String expectedPatronymic = "Test Patronymic";
        long expectedGroupId = 4;

        GroupDto.ShortRequest expectedGroup = new GroupDto.ShortRequest(expectedGroupId);

        StudentDto.Request dto = new StudentDto.Request(expectedLastName, expectedFirstName, expectedPatronymic, expectedGroup);

        Mockito.doReturn(Optional.of(new Student())).when(mockStudentDao).findById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockStudentDao).containsById(Mockito.anyLong());
        Mockito.doReturn(expectedId).when(mockStudentDao).insert(Mockito.any(Student.class));
        studentService.insert(dto);

        ArgumentCaptor<Student> argumentCaptor = ArgumentCaptor.forClass(Student.class);
        Mockito.verify(mockStudentDao).insert(argumentCaptor.capture());

        Student result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedLastName, result.getLastName());
        Assertions.assertEquals(expectedFirstName, result.getFirstName());
        Assertions.assertEquals(expectedPatronymic, result.getPatronymic());
        Assertions.assertEquals(expectedGroupId, result.getGroup().getId());
    }

    @Test
    void insertInsertionException() {
        long expectedId = 1;
        String expectedLastName = "Test Last Name";
        String expectedFirstName = "Test First Name";
        String expectedPatronymic = "Test Patronymic";
        long expectedGroupId = 4;

        GroupDto.ShortRequest expectedGroup = new GroupDto.ShortRequest(expectedGroupId);

        StudentDto.Request dto = new StudentDto.Request(expectedLastName, expectedFirstName, expectedPatronymic, expectedGroup);

        Mockito.doReturn(Optional.empty()).when(mockStudentDao).findById(Mockito.anyLong());
        Mockito.doReturn(expectedId).when(mockStudentDao).insert(Mockito.any(Student.class));

        InsertionException exception = Assertions.assertThrows(
                InsertionException.class,
                () -> studentService.insert(dto));
        Assertions.assertEquals(InsertionException.class, exception.getClass());
    }

    @Test
    void update() throws EntityNotFoundException {
        long expectedId = 1;
        String expectedLastName = "New Test Last Name";
        String expectedFirstName = "New Test First Name";
        String expectedPatronymic = "New Test Patronymic";
        long expectedGroupId = 4;

        GroupDto.ShortUpdate expectedGroup = new GroupDto.ShortUpdate(expectedGroupId);

        StudentDto.Update dto = new StudentDto.Update(expectedId, expectedLastName, expectedFirstName, expectedPatronymic, expectedGroup);

        Mockito.doReturn(true).when(mockStudentDao).containsById(Mockito.anyLong());
        studentService.update(dto);

        ArgumentCaptor<Student> argumentCaptor = ArgumentCaptor.forClass(Student.class);
        Mockito.verify(mockStudentDao).update(argumentCaptor.capture());

        Student result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void updateEntityNotFoundException() {
        long expectedId = 1;
        String expectedLastName = "New Test Last Name";
        String expectedFirstName = "New Test First Name";
        String expectedPatronymic = "New Test Patronymic";
        long expectedGroupId = 4;

        GroupDto.ShortUpdate expectedGroup = new GroupDto.ShortUpdate(expectedGroupId);

        StudentDto.Update dto = new StudentDto.Update(expectedId, expectedLastName, expectedFirstName, expectedPatronymic, expectedGroup);

        Mockito.doReturn(false).when(mockStudentDao).containsById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> studentService.update(dto));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void delete() throws EntityNotFoundException {
        long expectedId = 1;

        Mockito.doReturn(Optional.of(new Student())).when(mockStudentDao).findById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockStudentDao).containsById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockStudentDao).delete(Mockito.any(Student.class));

        boolean result = studentService.delete(expectedId);

        Assertions.assertTrue(result);

        ArgumentCaptor<Student> argumentCaptor = ArgumentCaptor.forClass(Student.class);
        Mockito.verify(mockStudentDao).delete(argumentCaptor.capture());

        Student resultStudent = argumentCaptor.getValue();
        Assertions.assertEquals(0, resultStudent.getId());
    }

    @Test
    void deleteEntityNotFoundException() {
        long expectedId = 1;

        Mockito.doReturn(Optional.empty()).when(mockStudentDao).findById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockStudentDao).containsById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockStudentDao).delete(Mockito.any(Student.class));

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> studentService.delete(expectedId));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void containsById() {
        long expectedId = 1;

        Mockito.doReturn(true).when(mockStudentDao).containsById(Mockito.anyLong());

        boolean result = studentService.containsById(expectedId);

        Assertions.assertTrue(result);
    }

    @Test
    void findAll() {
        studentService.findAll();
        Mockito.verify(mockStudentDao).findAll();
    }

    @Test
    void findById() throws EntityNotFoundException {
        long expectedId = 1;
        String expectedLastName = "Test Last Name";
        String expectedFirstName = "Test First Name";
        String expectedPatronymic = "Test Patronymic";
        long expectedGroupId = 4;
        String expectedGroupName = "Test Name";
        int expectedGroupCourse = 4;
        int expectedGroupSemester = 8;

        Group group = new Group(expectedGroupId, expectedGroupName, expectedGroupCourse, expectedGroupSemester, null);

        Optional<Student> student =
                Optional.of(new Student(expectedId, expectedLastName, expectedFirstName, expectedPatronymic, group, null));

        group.setStudents(List.of(student.get()));

        Mockito.doReturn(true).when(mockStudentDao).containsById(Mockito.anyLong());
        Mockito.doReturn(student).when(mockStudentDao).findById(Mockito.anyLong());

        StudentDto.Response dto = studentService.findById(expectedId);

        Assertions.assertEquals(expectedId, dto.getId());
    }

    @Test
    void findByIdEntityNotFoundException() {
        Mockito.doReturn(false).when(mockStudentDao).containsById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> studentService.findById(Mockito.anyLong()));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void addGroupToStudent() throws EntityNotFoundException {
        long expectedStudentId = 1;
        String expectedLastName = "Test Last Name";
        String expectedFirstName = "Test First Name";
        String expectedPatronymic = "Test Patronymic";
        long expectedGroupId = 4;
        String expectedGroupName = "Test Name";
        int expectedGroupCourse = 4;
        int expectedGroupSemester = 8;

        Optional<Group> group =
                Optional.of(new Group(expectedGroupId, expectedGroupName, expectedGroupCourse, expectedGroupSemester, null));

        Optional<Student> student =
                Optional.of(new Student(expectedStudentId, expectedLastName, expectedFirstName, expectedPatronymic, group.get(), null));

        group.get().setStudents(List.of(student.get()));

        Mockito.doReturn(true).when(mockStudentDao).containsById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockGroupDao).containsById(Mockito.anyLong());
        Mockito.doReturn(student).when(mockStudentDao).findById(Mockito.anyLong());
        Mockito.doReturn(group).when(mockGroupDao).findById(Mockito.anyLong());
        studentService.addGroupToStudent(expectedStudentId, expectedGroupId);

        ArgumentCaptor<Student> argumentCaptor = ArgumentCaptor.forClass(Student.class);
        Mockito.verify(mockStudentDao).update(argumentCaptor.capture());

        Student result = argumentCaptor.getValue();
        Assertions.assertEquals(group.get(), result.getGroup());
    }

    @Test
    void addGroupToStudentEntityNotFoundException() {
        long expectedStudentId = 1;
        long expectedGroupId = 4;

        Mockito.doReturn(false).when(mockStudentDao).containsById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockGroupDao).containsById(Mockito.anyLong());
        Mockito.doReturn(Optional.empty()).when(mockStudentDao).findById(Mockito.anyLong());
        Mockito.doReturn(Optional.empty()).when(mockGroupDao).findById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> studentService.addGroupToStudent(expectedStudentId, expectedGroupId));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void deleteGroupFromStudent() throws EntityNotFoundException {
        long expectedStudentId = 1;
        String expectedLastName = "Test Last Name";
        String expectedFirstName = "Test First Name";
        String expectedPatronymic = "Test Patronymic";
        long expectedGroupId = 4;
        String expectedGroupName = "Test Name";
        int expectedGroupCourse = 4;
        int expectedGroupSemester = 8;

        Optional<Group> group =
                Optional.of(new Group(expectedGroupId, expectedGroupName, expectedGroupCourse, expectedGroupSemester, null));

        Optional<Student> student =
                Optional.of(new Student(expectedStudentId, expectedLastName, expectedFirstName, expectedPatronymic, group.get(), null));

        group.get().setStudents(List.of(student.get()));

        Mockito.doReturn(true).when(mockStudentDao).containsById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockGroupDao).containsById(Mockito.anyLong());
        Mockito.doReturn(student).when(mockStudentDao).findById(Mockito.anyLong());
        Mockito.doReturn(group).when(mockGroupDao).findById(Mockito.anyLong());
        studentService.deleteGroupFromStudent(expectedStudentId);

        ArgumentCaptor<Student> argumentCaptor = ArgumentCaptor.forClass(Student.class);
        Mockito.verify(mockStudentDao).update(argumentCaptor.capture());

        Student result = argumentCaptor.getValue();
        Assertions.assertNull(result.getGroup());
    }

    @Test
    void deleteGroupFromStudentEntityNotFoundException() {
        long expectedId = 1;

        Mockito.doReturn(Optional.empty()).when(mockStudentDao).findById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockStudentDao).containsById(Mockito.anyLong());
        Mockito.doReturn(0).when(mockStudentDao).update(Mockito.any(Student.class));

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> studentService.deleteGroupFromStudent(expectedId));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }
}