package org.example.firsthomework.service;

import org.example.firsthomework.dao.DisciplineDao;
import org.example.firsthomework.dao.TeacherDao;
import org.example.firsthomework.dao.TeacherDisciplineDao;
import org.example.firsthomework.dto.DisciplineDto;
import org.example.firsthomework.dto.TeacherDto;
import org.example.firsthomework.entity.Discipline;
import org.example.firsthomework.entity.Teacher;
import org.example.firsthomework.entity.TeacherDiscipline;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.mapper.TeacherMapperImpl;
import org.example.firsthomework.service.global.TeacherService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Optional;

class TeacherServiceImplTest {
    private static TeacherService teacherService;
    private static TeacherDao mockTeacherDao;
    private static DisciplineDao mockDisciplineDao;
    private static TeacherDisciplineDao mockTeacherDisciplineDao;
    private static TeacherDao oldTeacherInstance;
    private static DisciplineDao oldDisciplineInstance;
    private static TeacherDisciplineDao oldTeacherDisciplineInstance;

    @BeforeAll
    static void beforeAll() {
        mockTeacherDao = Mockito.mock(TeacherDao.class);
        mockDisciplineDao = Mockito.mock(DisciplineDao.class);
        mockTeacherDisciplineDao = Mockito.mock(TeacherDisciplineDao.class);
        try {
            Field teacherInstance = TeacherDao.class.getDeclaredField("instance");
            teacherInstance.setAccessible(true);
            oldTeacherInstance = (TeacherDao) teacherInstance.get(teacherInstance);
            teacherInstance.set(teacherInstance, mockTeacherDao);

            Field disciplineInstance = DisciplineDao.class.getDeclaredField("instance");
            disciplineInstance.setAccessible(true);
            oldDisciplineInstance = (DisciplineDao) disciplineInstance.get(disciplineInstance);
            disciplineInstance.set(disciplineInstance, mockDisciplineDao);

            Field teacherDisciplineInstance = TeacherDisciplineDao.class.getDeclaredField("instance");
            teacherDisciplineInstance.setAccessible(true);
            oldTeacherDisciplineInstance = (TeacherDisciplineDao) teacherDisciplineInstance.get(teacherDisciplineInstance);
            teacherDisciplineInstance.set(teacherDisciplineInstance, mockTeacherDisciplineDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        teacherService = TeacherServiceImpl.getInstance(TeacherDao.getInstance(),
                TeacherDisciplineDao.getInstance(), TeacherMapperImpl.getInstance());
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field teacherInstance = TeacherDao.class.getDeclaredField("instance");
        teacherInstance.setAccessible(true);
        teacherInstance.set(teacherInstance, oldTeacherInstance);

        Field disciplineInstance = DisciplineDao.class.getDeclaredField("instance");
        disciplineInstance.setAccessible(true);
        disciplineInstance.set(disciplineInstance, oldDisciplineInstance);

        Field teacherDisciplineInstance = TeacherDisciplineDao.class.getDeclaredField("instance");
        teacherDisciplineInstance.setAccessible(true);
        teacherDisciplineInstance.set(teacherDisciplineInstance, oldTeacherDisciplineInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockTeacherDao);
        Mockito.reset(mockDisciplineDao);
        Mockito.reset(mockTeacherDisciplineDao);
    }

    @Test
    void insert() throws InsertionException {
        long expectedId = 1;
        String expectedLastName = "Test First Name";
        String expectedFirstName = "Test Last Name";
        String expectedPatronymic = "Test Patronymic";

        TeacherDto.Request dto = new TeacherDto.Request(expectedLastName, expectedFirstName, expectedPatronymic);

        Mockito.doReturn(Optional.of(new Teacher())).when(mockTeacherDao).findById(Mockito.anyLong());
        Mockito.doReturn(expectedId).when(mockTeacherDao).insert(Mockito.any(Teacher.class));
        teacherService.insert(dto);

        ArgumentCaptor<Teacher> argumentCaptor = ArgumentCaptor.forClass(Teacher.class);
        Mockito.verify(mockTeacherDao).insert(argumentCaptor.capture());

        Teacher result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedLastName, result.getLastName());
        Assertions.assertEquals(expectedFirstName, result.getFirstName());
        Assertions.assertEquals(expectedPatronymic, result.getPatronymic());
    }

    @Test
    void insertWithTeacherInsertionException() {
        long expectedId = 1;
        String expectedLastName = "Test First Name";
        String expectedFirstName = "Test Last Name";
        String expectedPatronymic = "Test Patronymic";

        TeacherDto.Request dto = new TeacherDto.Request(expectedLastName, expectedFirstName, expectedPatronymic);

        Mockito.doReturn(Optional.empty()).when(mockTeacherDao).findById(Mockito.anyLong());
        Mockito.doReturn(expectedId).when(mockTeacherDao).insert(Mockito.any(Teacher.class));

        InsertionException exception = Assertions.assertThrows(
                InsertionException.class,
                () -> teacherService.insert(dto));
        Assertions.assertEquals(InsertionException.class, exception.getClass());
    }

    @Test
    void update() throws EntityNotFoundException {
        long expectedId = 1;
        String expectedLastName = "New Test First Name";
        String expectedFirstName = "New Test Last Name";
        String expectedPatronymic = "New Test Patronymic";

        TeacherDto.Update dto = new TeacherDto.Update(expectedId, expectedLastName, expectedFirstName, expectedPatronymic);

        Mockito.doReturn(true).when(mockTeacherDao).containsById(Mockito.anyLong());
        teacherService.update(dto);

        ArgumentCaptor<Teacher> argumentCaptor = ArgumentCaptor.forClass(Teacher.class);
        Mockito.verify(mockTeacherDao).update(argumentCaptor.capture());

        Teacher result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void updateEntityNotFoundException() {
        long expectedId = 1;
        String expectedLastName = "New Test First Name";
        String expectedFirstName = "New Test Last Name";
        String expectedPatronymic = "New Test Patronymic";

        TeacherDto.Update dto = new TeacherDto.Update(expectedId, expectedLastName, expectedFirstName, expectedPatronymic);

        Mockito.doReturn(false).when(mockTeacherDao).containsById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> teacherService.update(dto));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void delete() throws EntityNotFoundException {
        long expectedId = 1;

        Mockito.doReturn(Optional.of(new Teacher())).when(mockTeacherDao).findById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockTeacherDao).containsById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockTeacherDao).delete(Mockito.any(Teacher.class));

        boolean result = teacherService.delete(expectedId);

        Assertions.assertTrue(result);

        ArgumentCaptor<Teacher> argumentCaptor = ArgumentCaptor.forClass(Teacher.class);
        Mockito.verify(mockTeacherDao).delete(argumentCaptor.capture());

        Teacher resultTeacher = argumentCaptor.getValue();
        Assertions.assertEquals(0, resultTeacher.getId());
    }

    @Test
    void deleteEntityNotFoundException() {
        long expectedId = 1;

        Mockito.doReturn(Optional.empty()).when(mockTeacherDao).findById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockTeacherDao).containsById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockTeacherDao).delete(Mockito.any(Teacher.class));

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> teacherService.delete(expectedId));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void containsById() {
        long expectedId = 1;

        Mockito.doReturn(true).when(mockTeacherDao).containsById(Mockito.anyLong());

        boolean result = teacherService.containsById(expectedId);

        Assertions.assertTrue(result);
    }

    @Test
    void findAll() {
        teacherService.findAll();
        Mockito.verify(mockTeacherDao).findAll();
    }

    @Test
    void findById() throws EntityNotFoundException {
        long expectedId = 1;
        String expectedLastName = "Test First Name";
        String expectedFirstName = "Test Last Name";
        String expectedPatronymic = "Test Patronymic";

        Optional<Teacher> teacher = Optional.of(new Teacher(expectedId, expectedLastName, expectedFirstName, expectedPatronymic, null));

        Mockito.doReturn(true).when(mockTeacherDao).containsById(Mockito.anyLong());
        Mockito.doReturn(teacher).when(mockTeacherDao).findById(Mockito.anyLong());

        TeacherDto.Response dto = teacherService.findById(expectedId);

        Assertions.assertEquals(expectedId, dto.getId());
    }

    @Test
    void findByIdEntityNotFoundException() {
        Mockito.doReturn(false).when(mockTeacherDao).containsById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> teacherService.findById(Mockito.anyLong()));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void addDisciplineToTeacher() throws EntityNotFoundException {
        long expectedDisciplineId = 1;
        long expectedTeacherId = 1;

        Mockito.doReturn(true).when(mockTeacherDao).containsById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockDisciplineDao).containsById(Mockito.anyLong());
        teacherService.addDisciplineToTeacher(expectedDisciplineId, expectedTeacherId);

        ArgumentCaptor<TeacherDiscipline> argumentCaptor = ArgumentCaptor.forClass(TeacherDiscipline.class);
        Mockito.verify(mockTeacherDisciplineDao).insert(argumentCaptor.capture());

        TeacherDiscipline result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedDisciplineId, result.getDisciplineId());
        Assertions.assertEquals(expectedTeacherId, result.getTeacherId());
    }

    @Test
    void addDisciplineToTeacherEntityNotFoundException() {
        long expectedDisciplineId = 1;
        long expectedTeacherId = 1;

        Mockito.doReturn(false).when(mockTeacherDao).containsById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockDisciplineDao).containsById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> teacherService.addDisciplineToTeacher(expectedDisciplineId, expectedTeacherId));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void deleteDisciplineFromTeacher() throws EntityNotFoundException {
        long expectedTeacherId = 1;

        Mockito.doReturn(true).when(mockTeacherDao).containsById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockDisciplineDao).containsById(Mockito.anyLong());
        Mockito.doReturn(Optional.of(new TeacherDiscipline())).when(mockTeacherDisciplineDao).findByTeacherId(Mockito.anyLong());
        Mockito.doReturn(true).when(mockTeacherDisciplineDao).delete(Mockito.any(TeacherDiscipline.class));

        boolean result = teacherService.deleteDisciplineFromTeacher(expectedTeacherId);

        Assertions.assertTrue(result);

        ArgumentCaptor<TeacherDiscipline> argumentCaptor = ArgumentCaptor.forClass(TeacherDiscipline.class);
        Mockito.verify(mockTeacherDisciplineDao).delete(argumentCaptor.capture());

        TeacherDiscipline restultTeacherDiscipline = argumentCaptor.capture();
        Assertions.assertNull(restultTeacherDiscipline);
    }

    @Test
    void deleteDisciplineFromTeacherEntityNotFoundException() {
        Mockito.doReturn(false).when(mockTeacherDao).containsById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> teacherService.deleteDisciplineFromTeacher(Mockito.anyLong()));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }
}