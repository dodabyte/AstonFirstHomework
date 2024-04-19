package org.example.firsthomework.dao;

import org.example.firsthomework.entity.Discipline;
import org.example.firsthomework.entity.Teacher;
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
class DisciplineDaoTest {
    private static final String PROPERTY_FILE_PATH_DEFAULT = "connection/connection.properties";
    private static final String SQL_FILE_PATH_DEFAULT = "sql/db.sql";
    public static DisciplineDao disciplineDao;
    public static TeacherDao teacherDao;
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
        disciplineDao = DisciplineDao.getInstance();
        teacherDao = TeacherDao.getInstance();
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

    private Teacher createTeacher() {
        String expectedLastName = "Test Last Name";
        String expectedFirstName = "Test First Name";
        String expectedPatronymic = "Test Patronymic";

        Teacher teacher = new Teacher(0, expectedLastName, expectedFirstName, expectedPatronymic, null);

        long teacherId = teacherDao.insert(teacher);
        Optional<Teacher> teacherResult = teacherDao.findById(teacherId);

        Assertions.assertTrue(teacherResult.isPresent());

        Teacher insertedTeacher = teacherResult.get();

        Assertions.assertEquals(expectedLastName, insertedTeacher.getLastName());
        Assertions.assertEquals(expectedFirstName, insertedTeacher.getFirstName());
        Assertions.assertEquals(expectedPatronymic, insertedTeacher.getPatronymic());

        return insertedTeacher;
    }

    @Test
    void insert() {
        String expectedName = "Test Name";
        Teacher expectedTeacher = createTeacher();

        Discipline discipline = new Discipline(0, expectedName, expectedTeacher);

        long disciplineId = disciplineDao.insert(discipline);
        Optional<Discipline> result = disciplineDao.findById(disciplineId);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedName, result.get().getName());
        Assertions.assertEquals(expectedTeacher, result.get().getTeacher());
    }

    @Test
    void update() {
        long expectedId = 2;
        String expectedName = "New Test Name";

        Discipline updateDiscipline = disciplineDao.findById(expectedId).get();

        Assertions.assertNotEquals(expectedName, updateDiscipline.getName());

        updateDiscipline.setName(expectedName);

        disciplineDao.update(updateDiscipline);

        Discipline updatedDiscipline = disciplineDao.findById(expectedId).get();

        Assertions.assertEquals(expectedName, updatedDiscipline.getName());
        Assertions.assertEquals(updateDiscipline.getTeacher(), updatedDiscipline.getTeacher());
    }

    @Test
    void delete() {
        String expectedName = "Test Temp Name";

        Discipline discipline = new Discipline(0, expectedName, null);

        long disciplineId = disciplineDao.insert(discipline);
        Optional<Discipline> result = disciplineDao.findById(disciplineId);

        Assertions.assertTrue(result.isPresent());

        Discipline insertedDiscipline = result.get();

        Assertions.assertEquals(expectedName, insertedDiscipline.getName());

        int expectedSize = disciplineDao.findAll().size();

        boolean isDeleted = disciplineDao.delete(insertedDiscipline);

        Assertions.assertTrue(isDeleted);
        Assertions.assertNotEquals(expectedSize, disciplineDao.findAll().size());
    }

    @Test
    void findAll() {
        int expectedSize = 4;
        int resultSize = disciplineDao.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "10; false"
    }, delimiter = ';')
    void findById(long expectedId, boolean expectedValue) {
        Optional<Discipline> discipline = disciplineDao.findById(expectedId);

        Assertions.assertEquals(expectedValue, discipline.isPresent());
        discipline.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "10; false"
    }, delimiter = ';')
    void containsById(long expectedId, boolean expectedValue) {
        boolean isContains = disciplineDao.containsById(expectedId);

        Assertions.assertEquals(expectedValue, isContains);
    }
}