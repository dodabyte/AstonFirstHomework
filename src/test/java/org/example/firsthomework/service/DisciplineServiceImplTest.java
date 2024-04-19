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
import org.example.firsthomework.mapper.DisciplineMapperImpl;
import org.example.firsthomework.mapper.TeacherMapperImpl;
import org.example.firsthomework.service.global.DisciplineService;
import org.example.firsthomework.service.global.TeacherService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Optional;

class DisciplineServiceImplTest {
    private static DisciplineService disciplineService;
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
        disciplineService = DisciplineServiceImpl.getInstance(DisciplineDao.getInstance(),
                TeacherDao.getInstance(), TeacherDisciplineDao.getInstance(),
                DisciplineMapperImpl.getInstance(), TeacherMapperImpl.getInstance());
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
    void insertWithTeacher() throws InsertionException {
        long expectedId = 1;
        String expectedName = "Test Name";
        String expectedTeacherLastName = "Test Last Name";
        String expectedTeacherFirstName = "Test First Name";
        String expectedTeacherPatronymic = "Test Patronymic";

        TeacherDto.Request expectedTeacherDto =
                new TeacherDto.Request(expectedTeacherLastName, expectedTeacherFirstName, expectedTeacherPatronymic);
        DisciplineDto.Request disciplineDto = new DisciplineDto.Request(expectedName, expectedTeacherDto);

        Mockito.doReturn(Optional.of(new Teacher())).when(mockTeacherDao).findById(Mockito.anyLong());
        Mockito.doReturn(Optional.of(new Discipline())).when(mockDisciplineDao).findById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockTeacherDao).containsById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockDisciplineDao).containsById(Mockito.anyLong());
        Mockito.doReturn(expectedId).when(mockTeacherDao).insert(Mockito.any(Teacher.class));
        Mockito.doReturn(expectedId).when(mockDisciplineDao).insert(Mockito.any(Discipline.class));

        teacherService.insert(expectedTeacherDto);
        ArgumentCaptor<Teacher> argumentCaptorTeacher = ArgumentCaptor.forClass(Teacher.class);
        Mockito.verify(mockTeacherDao).insert(argumentCaptorTeacher.capture());
        Teacher teacher = argumentCaptorTeacher.getValue();

        disciplineService.insert(disciplineDto);
        ArgumentCaptor<Discipline> argumentCaptorDiscipline = ArgumentCaptor.forClass(Discipline.class);
        Mockito.verify(mockDisciplineDao).insert(argumentCaptorDiscipline.capture());
        Discipline discipline = argumentCaptorDiscipline.getValue();

        Assertions.assertEquals(expectedName, discipline.getName());
        Assertions.assertEquals(expectedTeacherLastName, teacher.getLastName());
        Assertions.assertEquals(expectedTeacherFirstName, teacher.getFirstName());
        Assertions.assertEquals(expectedTeacherPatronymic, teacher.getPatronymic());
    }

    @Test
    void insertWithTeacherInsertionException() {
        long expectedId = 1;
        String expectedName = "Test Name";
        String expectedTeacherLastName = "Test Last Name";
        String expectedTeacherFirstName = "Test First Name";
        String expectedTeacherPatronymic = "Test Patronymic";

        TeacherDto.Request expectedTeacherDto =
                new TeacherDto.Request(expectedTeacherLastName, expectedTeacherFirstName, expectedTeacherPatronymic);
        DisciplineDto.Request disciplineDto = new DisciplineDto.Request(expectedName, expectedTeacherDto);

        Mockito.doReturn(Optional.empty()).when(mockTeacherDao).findById(Mockito.anyLong());
        Mockito.doReturn(Optional.empty()).when(mockDisciplineDao).findById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockTeacherDao).containsById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockDisciplineDao).containsById(Mockito.anyLong());
        Mockito.doReturn(expectedId).when(mockTeacherDao).insert(Mockito.any(Teacher.class));
        Mockito.doReturn(expectedId).when(mockDisciplineDao).insert(Mockito.any(Discipline.class));

        InsertionException exception = Assertions.assertThrows(
                InsertionException.class,
                () -> disciplineService.insert(disciplineDto));
        Assertions.assertEquals(InsertionException.class, exception.getClass());
    }

    @Test
    void insertWithoutTeacher() throws InsertionException {
        long expectedId = 1;
        String expectedName = "Test Name";

        DisciplineDto.Request disciplineDto = new DisciplineDto.Request(expectedName, null);

        Mockito.doReturn(Optional.of(new Discipline())).when(mockDisciplineDao).findById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockDisciplineDao).containsById(Mockito.anyLong());
        Mockito.doReturn(expectedId).when(mockDisciplineDao).insert(Mockito.any(Discipline.class));

        disciplineService.insert(disciplineDto);
        ArgumentCaptor<Discipline> argumentCaptorDiscipline = ArgumentCaptor.forClass(Discipline.class);
        Mockito.verify(mockDisciplineDao).insert(argumentCaptorDiscipline.capture());
        Discipline discipline = argumentCaptorDiscipline.getValue();

        Assertions.assertEquals(expectedName, discipline.getName());
    }

    @Test
    void update() throws EntityNotFoundException {
        long expectedId = 1;
        String expectedName = "New Test Name";

        DisciplineDto.Update dto = new DisciplineDto.Update(expectedId, expectedName, null);

        Mockito.doReturn(true).when(mockDisciplineDao).containsById(Mockito.anyLong());
        disciplineService.update(dto);

        ArgumentCaptor<Discipline> argumentCaptor = ArgumentCaptor.forClass(Discipline.class);
        Mockito.verify(mockDisciplineDao).update(argumentCaptor.capture());

        Discipline result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void updateEntityNotFoundException() {
        long expectedId = 1;
        String expectedName = "New Test Name";

        DisciplineDto.Update dto = new DisciplineDto.Update(expectedId, expectedName, null);

        Mockito.doReturn(false).when(mockDisciplineDao).containsById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> disciplineService.update(dto));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void delete() throws EntityNotFoundException {
        long expectedId = 1;

        Mockito.doReturn(Optional.of(new Discipline())).when(mockDisciplineDao).findById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockDisciplineDao).containsById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockDisciplineDao).delete(Mockito.any(Discipline.class));

        boolean result = disciplineService.delete(expectedId);

        Assertions.assertTrue(result);

        ArgumentCaptor<Discipline> argumentCaptor = ArgumentCaptor.forClass(Discipline.class);
        Mockito.verify(mockDisciplineDao).delete(argumentCaptor.capture());

        Discipline resultDiscipline = argumentCaptor.getValue();
        Assertions.assertEquals(0, resultDiscipline.getId());
    }

    @Test
    void deleteEntityNotFoundException() {
        long expectedId = 1;

        Mockito.doReturn(Optional.empty()).when(mockDisciplineDao).findById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockDisciplineDao).containsById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockDisciplineDao).delete(Mockito.any(Discipline.class));

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> disciplineService.delete(expectedId));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void containsById() {
        long expectedId = 1;

        Mockito.doReturn(true).when(mockDisciplineDao).containsById(Mockito.anyLong());

        boolean result = disciplineService.containsById(expectedId);

        Assertions.assertTrue(result);
    }

    @Test
    void findAll() {
        disciplineService.findAll();
        Mockito.verify(mockDisciplineDao).findAll();
    }

    @Test
    void findById() throws EntityNotFoundException {
        long expectedId = 1;
        String name = "Test Name";

        Optional<Discipline> discipline = Optional.of(new Discipline(expectedId, name, null));

        Mockito.doReturn(true).when(mockDisciplineDao).containsById(Mockito.anyLong());
        Mockito.doReturn(discipline).when(mockDisciplineDao).findById(Mockito.anyLong());

        DisciplineDto.Response dto = disciplineService.findById(expectedId);

        Assertions.assertEquals(expectedId, dto.getId());
    }

    @Test
    void findByIdEntityNotFoundException() {
        Mockito.doReturn(false).when(mockDisciplineDao).containsById(Mockito.anyLong());
        Mockito.doReturn(Optional.empty()).when(mockDisciplineDao).findById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> disciplineService.findById(Mockito.anyLong()));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void addDisciplineToTeacher() throws EntityNotFoundException {
        long expectedDisciplineId = 1;
        long expectedTeacherId = 3;

        Teacher expectedTeacher = mockTeacherDisciplineDao.findTeacherByDisciplineId(expectedDisciplineId);

        Mockito.doReturn(true).when(mockDisciplineDao).containsById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockTeacherDao).containsById(Mockito.anyLong());
        Mockito.doReturn(Optional.of(new Discipline())).when(mockDisciplineDao).findById(Mockito.anyLong());
        Mockito.doReturn(expectedTeacher).when(mockTeacherDisciplineDao).findTeacherByDisciplineId(Mockito.anyLong());
        Mockito.doReturn(expectedTeacherId).when(mockTeacherDisciplineDao).insert(Mockito.any(TeacherDiscipline.class));

        disciplineService.addDisciplineToTeacher(expectedDisciplineId, expectedTeacherId);

        ArgumentCaptor<TeacherDiscipline> argumentCaptorTeacherDiscipline = ArgumentCaptor.forClass(TeacherDiscipline.class);
        Mockito.verify(mockTeacherDisciplineDao).insert(argumentCaptorTeacherDiscipline.capture());

        TeacherDiscipline resultTeacherDiscipline = argumentCaptorTeacherDiscipline.getValue();
        Assertions.assertEquals(expectedDisciplineId, resultTeacherDiscipline.getDisciplineId());
        Assertions.assertEquals(expectedTeacherId, resultTeacherDiscipline.getTeacherId());

        ArgumentCaptor<Discipline> argumentCaptorDiscipline = ArgumentCaptor.forClass(Discipline.class);
        Mockito.verify(mockDisciplineDao).update(argumentCaptorDiscipline.capture());

        Discipline resultDiscipline = argumentCaptorDiscipline.getValue();
        Assertions.assertEquals(expectedTeacher, resultDiscipline.getTeacher());
    }

    @Test
    void addDisciplineToTeacherEntityNotFoundException() {
        long expectedDisciplineId = 1;
        long expectedTeacherId = 1;

        Mockito.doReturn(false).when(mockTeacherDao).containsById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockDisciplineDao).containsById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> disciplineService.addDisciplineToTeacher(expectedDisciplineId, expectedTeacherId));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void deleteDisciplineFromTeacher() throws EntityNotFoundException {
        long expectedTeacherId = 1;

        Mockito.doReturn(true).when(mockTeacherDisciplineDao).containsByTeacherId(Mockito.anyLong());
        Mockito.doReturn(new Discipline()).when(mockTeacherDisciplineDao).findDisciplineByTeacherId(Mockito.anyLong());
        Mockito.doReturn(true).when(mockDisciplineDao).containsById(Mockito.anyLong());
        Mockito.doReturn(Optional.of(new TeacherDiscipline())).
                when(mockTeacherDisciplineDao).findByTeacherId(Mockito.anyLong());
        Mockito.doReturn(true).when(mockTeacherDisciplineDao).delete(Mockito.any(TeacherDiscipline.class));

        boolean result = disciplineService.deleteDisciplineFromTeacher(expectedTeacherId);

        ArgumentCaptor<Discipline> argumentCaptorDiscipline = ArgumentCaptor.forClass(Discipline.class);
        Mockito.verify(mockDisciplineDao).update(argumentCaptorDiscipline.capture());

        Discipline resultDiscipline = argumentCaptorDiscipline.getValue();
        Assertions.assertNull(resultDiscipline.getTeacher());

        ArgumentCaptor<TeacherDiscipline> argumentCaptorTeacherDiscipline = ArgumentCaptor.forClass(TeacherDiscipline.class);
        Mockito.verify(mockTeacherDisciplineDao).delete(argumentCaptorTeacherDiscipline.capture());

        Assertions.assertTrue(result);
    }

    @Test
    void deleteDisciplineFromTeacherEntityNotFoundException() {
        Mockito.doReturn(false).when(mockTeacherDao).containsById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> disciplineService.deleteDisciplineFromTeacher(Mockito.anyLong()));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }
}