package org.example.firsthomework.dao;

import org.example.firsthomework.entity.Discipline;
import org.example.firsthomework.entity.Group;
import org.example.firsthomework.entity.SemesterPerformance;
import org.example.firsthomework.entity.Student;
import org.example.firsthomework.session.PropertyFile;
import org.example.firsthomework.session.SessionManagerImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@Tag("DockerRequired")
class SemesterPerformanceDaoTest {
    private static final String PROPERTY_FILE_PATH_DEFAULT = "connection/connection.properties";
    private static final String SQL_FILE_PATH_DEFAULT = "sql/db.sql";
    public static SemesterPerformanceDao semesterPerformanceDao;
    public static StudentDao studentDao;
    public static GroupDao groupDao;
    public static DisciplineDao disciplineDao;
    private static final PropertyFile propertyFile = new PropertyFile(PROPERTY_FILE_PATH_DEFAULT);
    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("AstonFirstHomework")
            .withUsername(propertyFile.getValue("username"))
            .withPassword(propertyFile.getValue("password"))
            .withInitScript(SQL_FILE_PATH_DEFAULT);
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
        SessionManagerImpl.getInstance(container.getJdbcUrl(), container.getUsername(),
                container.getPassword(), container.getDriverClassName());
        semesterPerformanceDao = SemesterPerformanceDao.getInstance();
        studentDao = StudentDao.getInstance();
        groupDao = GroupDao.getInstance();
        disciplineDao = DisciplineDao.getInstance();
        jdbcDatabaseDelegate = new JdbcDatabaseDelegate(container, "");
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @BeforeEach
    void setUp() {
        ScriptUtils.runInitScript(jdbcDatabaseDelegate, SQL_FILE_PATH_DEFAULT);
    }

    @Test
    void insert() {
        long expectedStudentId = 2;
        long expectedDisciplineId = 4;
        int expectedMark = 78;

        Student expectedStudent = studentDao.findById(expectedStudentId).get();
        Discipline expectedDiscipline = disciplineDao.findById(expectedDisciplineId).get();

        SemesterPerformance semesterPerformance =
                new SemesterPerformance(0, expectedStudent, expectedDiscipline, expectedMark);

        long semesterPerformanceId = semesterPerformanceDao.insert(semesterPerformance);
        Optional<SemesterPerformance> result = semesterPerformanceDao.findById(semesterPerformanceId);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedStudent, result.get().getStudent());
        Assertions.assertEquals(expectedDiscipline, result.get().getDiscipline());
        Assertions.assertEquals(expectedMark, result.get().getMark());
    }

    @Test
    void update() {
        long expectedId = 2;
        long expectedStudentId = 3;
        long expectedDisciplineId = 3;
        int expectedMark = 100;

        Student expectedStudent = studentDao.findById(expectedStudentId).get();
        Discipline expectedDiscipline = disciplineDao.findById(expectedDisciplineId).get();

        SemesterPerformance updateSemesterPerformance = semesterPerformanceDao.findById(expectedId).get();

        Assertions.assertNotEquals(expectedStudent, updateSemesterPerformance.getStudent());
        Assertions.assertNotEquals(expectedDiscipline, updateSemesterPerformance.getDiscipline());
        Assertions.assertNotEquals(expectedMark, updateSemesterPerformance.getMark());

        updateSemesterPerformance.setStudent(expectedStudent);
        updateSemesterPerformance.setDiscipline(expectedDiscipline);
        updateSemesterPerformance.setMark(expectedMark);

        semesterPerformanceDao.update(updateSemesterPerformance);

        SemesterPerformance updatedSemesterPerformance = semesterPerformanceDao.findById(expectedId).get();

        Assertions.assertEquals(expectedStudent, updatedSemesterPerformance.getStudent());
        Assertions.assertEquals(expectedDiscipline, updatedSemesterPerformance.getDiscipline());
        Assertions.assertEquals(expectedMark, updatedSemesterPerformance.getMark());
    }

    @Test
    void delete() {
        long expectedStudentId = 2;
        long expectedDisciplineId = 4;
        int expectedMark = 78;

        Student expectedStudent = studentDao.findById(expectedStudentId).get();
        Discipline expectedDiscipline = disciplineDao.findById(expectedDisciplineId).get();

        SemesterPerformance semesterPerformance =
                new SemesterPerformance(0, expectedStudent, expectedDiscipline, expectedMark);

        long semesterPerformanceId = semesterPerformanceDao.insert(semesterPerformance);
        Optional<SemesterPerformance> result = semesterPerformanceDao.findById(semesterPerformanceId);

        Assertions.assertTrue(result.isPresent());

        SemesterPerformance insertedSemesterPerformance = result.get();

        Assertions.assertEquals(expectedStudent, insertedSemesterPerformance.getStudent());
        Assertions.assertEquals(expectedDiscipline, insertedSemesterPerformance.getDiscipline());
        Assertions.assertEquals(expectedMark, insertedSemesterPerformance.getMark());

        int expectedSize = semesterPerformanceDao.findAll().size();

        boolean isDeleted = semesterPerformanceDao.delete(insertedSemesterPerformance);

        Assertions.assertTrue(isDeleted);
        Assertions.assertNotEquals(expectedSize, semesterPerformanceDao.findAll().size());
    }

    @Test
    void findAll() {
        int expectedSize = 20;
        int resultSize = semesterPerformanceDao.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "10; true",
            "13; true",
            "16; true",
            "19; true",
            "23; false",
            "27; false",
    }, delimiter = ';')
    void findById(long expectedId, boolean expectedValue) {
        Optional<SemesterPerformance> semesterPerformance = semesterPerformanceDao.findById(expectedId);

        Assertions.assertEquals(expectedValue, semesterPerformance.isPresent());
        semesterPerformance.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "10; true",
            "13; true",
            "16; true",
            "19; true",
            "23; false",
            "27; false",
    }, delimiter = ';')
    void containsById(long expectedId, boolean expectedValue) {
        boolean isContains = semesterPerformanceDao.containsById(expectedId);

        Assertions.assertEquals(expectedValue, isContains);
    }
}