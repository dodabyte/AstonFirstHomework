package org.example.firsthomework.service;

import org.example.firsthomework.dao.DisciplineDao;
import org.example.firsthomework.dao.GroupDao;
import org.example.firsthomework.dao.SemesterPerformanceDao;
import org.example.firsthomework.dao.StudentDao;
import org.example.firsthomework.dto.DisciplineDto;
import org.example.firsthomework.dto.GroupDto;
import org.example.firsthomework.dto.SemesterPerformanceDto;
import org.example.firsthomework.dto.StudentDto;
import org.example.firsthomework.entity.*;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.mapper.SemesterPerformanceMapperImpl;
import org.example.firsthomework.mapper.StudentMapperImpl;
import org.example.firsthomework.service.global.SemesterPerformanceService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

class SemesterPerformanceServiceImplTest {
    private static SemesterPerformanceService semesterPerformanceService;
    private static SemesterPerformanceDao mockSemesterPerformanceDao;
    private static GroupDao mockGroupDao;
    private static StudentDao mockStudentDao;
    private static DisciplineDao mockDisciplineDao;
    private static SemesterPerformanceDao oldSemesterPerformanceInstance;
    private static GroupDao oldGroupInstance;
    private static StudentDao oldStudentInstance;
    private static DisciplineDao oldDisciplineInstance;

    @BeforeAll
    static void beforeAll() {
        mockSemesterPerformanceDao = Mockito.mock(SemesterPerformanceDao.class);
        mockGroupDao = Mockito.mock(GroupDao.class);
        mockStudentDao = Mockito.mock(StudentDao.class);
        mockDisciplineDao = Mockito.mock(DisciplineDao.class);
        try {
            Field semesterPerformanceInstance = SemesterPerformanceDao.class.getDeclaredField("instance");
            semesterPerformanceInstance.setAccessible(true);
            oldSemesterPerformanceInstance = (SemesterPerformanceDao) semesterPerformanceInstance.get(semesterPerformanceInstance);
            semesterPerformanceInstance.set(semesterPerformanceInstance, mockSemesterPerformanceDao);

            Field groupInstance = GroupDao.class.getDeclaredField("instance");
            groupInstance.setAccessible(true);
            oldGroupInstance = (GroupDao) groupInstance.get(groupInstance);
            groupInstance.set(groupInstance, mockGroupDao);

            Field studentInstance = StudentDao.class.getDeclaredField("instance");
            studentInstance.setAccessible(true);
            oldStudentInstance = (StudentDao) studentInstance.get(studentInstance);
            studentInstance.set(studentInstance, mockStudentDao);

            Field disciplineInstance = DisciplineDao.class.getDeclaredField("instance");
            disciplineInstance.setAccessible(true);
            oldDisciplineInstance = (DisciplineDao) disciplineInstance.get(disciplineInstance);
            disciplineInstance.set(disciplineInstance, mockDisciplineDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        semesterPerformanceService = SemesterPerformanceServiceImpl.getInstance(SemesterPerformanceDao.getInstance(),
                StudentDao.getInstance(), DisciplineDao.getInstance(), SemesterPerformanceMapperImpl.getInstance());
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field semesterPerformanceInstance = SemesterPerformanceDao.class.getDeclaredField("instance");
        semesterPerformanceInstance.setAccessible(true);
        semesterPerformanceInstance.set(semesterPerformanceInstance, oldSemesterPerformanceInstance);

        Field groupInstance = GroupDao.class.getDeclaredField("instance");
        groupInstance.setAccessible(true);
        groupInstance.set(groupInstance, oldGroupInstance);

        Field studentInstance = StudentDao.class.getDeclaredField("instance");
        studentInstance.setAccessible(true);
        studentInstance.set(studentInstance, oldStudentInstance);

        Field disciplineInstance = DisciplineDao.class.getDeclaredField("instance");
        disciplineInstance.setAccessible(true);
        disciplineInstance.set(disciplineInstance, oldDisciplineInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockSemesterPerformanceDao);
        Mockito.reset(mockGroupDao);
        Mockito.reset(mockStudentDao);
        Mockito.reset(mockDisciplineDao);
    }

    @Test
    void insert() throws InsertionException {
        long expectedId = 1;
        int expectedMark = 87;
        long expectedStudentId = 4;
        long expectedDisciplineId = 2;

        StudentDto.ShortRequest expectedStudent = new StudentDto.ShortRequest(expectedStudentId);
        DisciplineDto.ShortRequest expectedDiscipline = new DisciplineDto.ShortRequest(expectedDisciplineId);

        SemesterPerformanceDto.Request dto = new SemesterPerformanceDto.Request(expectedStudent, expectedDiscipline, expectedMark);

        Mockito.doReturn(Optional.of(new SemesterPerformance())).when(mockSemesterPerformanceDao).findById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockSemesterPerformanceDao).containsById(Mockito.anyLong());
        Mockito.doReturn(expectedId).when(mockSemesterPerformanceDao).insert(Mockito.any(SemesterPerformance.class));
        semesterPerformanceService.insert(dto);

        ArgumentCaptor<SemesterPerformance> argumentCaptor = ArgumentCaptor.forClass(SemesterPerformance.class);
        Mockito.verify(mockSemesterPerformanceDao).insert(argumentCaptor.capture());

        SemesterPerformance result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedStudentId, result.getStudent().getId());
        Assertions.assertEquals(expectedDisciplineId, result.getDiscipline().getId());
        Assertions.assertEquals(expectedMark, result.getMark());
    }

    @Test
    void insertInsertionException() {
        long expectedId = 1;
        int expectedMark = 87;
        long expectedStudentId = 4;
        long expectedDisciplineId = 2;

        StudentDto.ShortRequest expectedStudent = new StudentDto.ShortRequest(expectedStudentId);
        DisciplineDto.ShortRequest expectedDiscipline = new DisciplineDto.ShortRequest(expectedDisciplineId);

        SemesterPerformanceDto.Request dto = new SemesterPerformanceDto.Request(expectedStudent, expectedDiscipline, expectedMark);

        Mockito.doReturn(Optional.empty()).when(mockSemesterPerformanceDao).findById(Mockito.anyLong());
        Mockito.doReturn(expectedId).when(mockSemesterPerformanceDao).insert(Mockito.any(SemesterPerformance.class));

        InsertionException exception = Assertions.assertThrows(
                InsertionException.class,
                () -> semesterPerformanceService.insert(dto));
        Assertions.assertEquals(InsertionException.class, exception.getClass());
    }

    @Test
    void update() throws EntityNotFoundException {
        long expectedId = 1;
        int expectedMark = 87;
        long expectedStudentId = 4;
        long expectedDisciplineId = 2;

        StudentDto.ShortUpdate expectedStudent = new StudentDto.ShortUpdate(expectedStudentId);
        DisciplineDto.ShortUpdate expectedDiscipline = new DisciplineDto.ShortUpdate(expectedDisciplineId);

        SemesterPerformanceDto.Update dto = new SemesterPerformanceDto.Update(expectedId, expectedStudent, expectedDiscipline, expectedMark);

        Mockito.doReturn(true).when(mockSemesterPerformanceDao).containsById(Mockito.anyLong());
        semesterPerformanceService.update(dto);

        ArgumentCaptor<SemesterPerformance> argumentCaptor = ArgumentCaptor.forClass(SemesterPerformance.class);
        Mockito.verify(mockSemesterPerformanceDao).update(argumentCaptor.capture());

        SemesterPerformance result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void updateEntityNotFoundException() {
        long expectedId = 1;
        int expectedMark = 87;
        long expectedStudentId = 4;
        long expectedDisciplineId = 2;

        StudentDto.ShortUpdate expectedStudent = new StudentDto.ShortUpdate(expectedStudentId);
        DisciplineDto.ShortUpdate expectedDiscipline = new DisciplineDto.ShortUpdate(expectedDisciplineId);

        SemesterPerformanceDto.Update dto = new SemesterPerformanceDto.Update(expectedId, expectedStudent, expectedDiscipline, expectedMark);

        Mockito.doReturn(false).when(mockSemesterPerformanceDao).containsById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> semesterPerformanceService.update(dto));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void delete() throws EntityNotFoundException {
        long expectedId = 1;

        Mockito.doReturn(Optional.of(new SemesterPerformance())).when(mockSemesterPerformanceDao).findById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockSemesterPerformanceDao).containsById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockSemesterPerformanceDao).delete(Mockito.any(SemesterPerformance.class));

        boolean result = semesterPerformanceService.delete(expectedId);

        Assertions.assertTrue(result);

        ArgumentCaptor<SemesterPerformance> argumentCaptor = ArgumentCaptor.forClass(SemesterPerformance.class);
        Mockito.verify(mockSemesterPerformanceDao).delete(argumentCaptor.capture());

        SemesterPerformance resultSemesterPerformance = argumentCaptor.getValue();
        Assertions.assertEquals(0, resultSemesterPerformance.getId());
    }

    @Test
    void deleteEntityNotFoundException() {
        long expectedId = 1;

        Mockito.doReturn(Optional.empty()).when(mockSemesterPerformanceDao).findById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockSemesterPerformanceDao).containsById(Mockito.anyLong());
        Mockito.doReturn(false).when(mockSemesterPerformanceDao).delete(Mockito.any(SemesterPerformance.class));

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> semesterPerformanceService.delete(expectedId));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void containsById() {
        long expectedId = 1;

        Mockito.doReturn(true).when(mockSemesterPerformanceDao).containsById(Mockito.anyLong());

        boolean result = semesterPerformanceService.containsById(expectedId);

        Assertions.assertTrue(result);
    }

    @Test
    void findAll() {
        semesterPerformanceService.findAll();
        Mockito.verify(mockSemesterPerformanceDao).findAll();
    }

    @Test
    void findById() throws EntityNotFoundException {
        long expectedId = 1;
        int expectedMark = 87;
        long expectedStudentId = 4;
        String expectedStudentLastName = "Test Last Name";
        String expectedStudentFirstName = "Test First Name";
        String expectedStudentPatronymic = "Test Patronymic";
        long expectedDisciplineId = 2;
        String expectedDisciplineName = "Test Name";

        Student student = new Student(expectedStudentId, expectedStudentLastName, expectedStudentFirstName,
                expectedStudentPatronymic, null, null);
        Discipline discipline = new Discipline(expectedDisciplineId, expectedDisciplineName, null);

        Optional<SemesterPerformance> semesterPerformance =
                Optional.of(new SemesterPerformance(expectedId, student, discipline, expectedMark));

        Mockito.doReturn(true).when(mockSemesterPerformanceDao).containsById(Mockito.anyLong());
        Mockito.doReturn(semesterPerformance).when(mockSemesterPerformanceDao).findById(Mockito.anyLong());

        SemesterPerformanceDto.Response dto = semesterPerformanceService.findById(expectedId);

        Assertions.assertEquals(expectedId, dto.getId());
    }

    @Test
    void findByIdEntityNotFoundException() {
        Mockito.doReturn(false).when(mockSemesterPerformanceDao).containsById(Mockito.anyLong());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> semesterPerformanceService.findById(Mockito.anyLong()));
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }
}