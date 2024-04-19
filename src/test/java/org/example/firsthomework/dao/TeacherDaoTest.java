package org.example.firsthomework.dao;

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

@Testcontainers
@Tag("DockerRequired")
class TeacherDaoTest {
    private static final String PROPERTY_FILE_PATH_DEFAULT = "connection/connection.properties";
    private static final String SQL_FILE_PATH_DEFAULT = "sql/db.sql";
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

    @Test
    void insert() {
        String expectedLastName = "Test Last Name";
        String expectedFirstName = "Test First Name";
        String expectedPatronymic = "Test Patronymic";

        Teacher teacher = new Teacher(0, expectedLastName, expectedFirstName, expectedPatronymic, null);

        long teacherId = teacherDao.insert(teacher);
        Optional<Teacher> result = teacherDao.findById(teacherId);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedLastName, result.get().getLastName());
        Assertions.assertEquals(expectedFirstName, result.get().getFirstName());
        Assertions.assertEquals(expectedPatronymic, result.get().getPatronymic());
    }

    @Test
    void update() {
        long expectedId = 1;
        String expectedLastName = "New Test Last Name";
        String expectedFirstName = "New Test First Name";
        String expectedPatronymic = "New Test Patronymic";

        Teacher updateTeacher = teacherDao.findById(expectedId).get();

        Assertions.assertNotEquals(expectedLastName, updateTeacher.getLastName());
        Assertions.assertNotEquals(expectedFirstName, updateTeacher.getFirstName());
        Assertions.assertNotEquals(expectedPatronymic, updateTeacher.getPatronymic());

        updateTeacher.setLastName(expectedLastName);
        updateTeacher.setFirstName(expectedFirstName);
        updateTeacher.setPatronymic(expectedPatronymic);

        teacherDao.update(updateTeacher);

        Teacher updatedTeacher = teacherDao.findById(expectedId).get();

        Assertions.assertEquals(expectedLastName, updatedTeacher.getLastName());
        Assertions.assertEquals(expectedFirstName, updatedTeacher.getFirstName());
        Assertions.assertEquals(expectedPatronymic, updatedTeacher.getPatronymic());
    }

    @Test
    void delete() {
        String expectedLastName = "Test Temp Last Name";
        String expectedFirstName = "Test Temp First Name";
        String expectedPatronymic = "Test Temp Patronymic";

        Teacher teacher = new Teacher(0, expectedLastName, expectedFirstName, expectedPatronymic, null);

        long teacherId = teacherDao.insert(teacher);
        Optional<Teacher> result = teacherDao.findById(teacherId);

        Assertions.assertTrue(result.isPresent());

        Teacher insertedTeacher = result.get();

        Assertions.assertEquals(expectedLastName, insertedTeacher.getLastName());
        Assertions.assertEquals(expectedFirstName, insertedTeacher.getFirstName());
        Assertions.assertEquals(expectedPatronymic, insertedTeacher.getPatronymic());

        int expectedSize = teacherDao.findAll().size();

        boolean isDeleted = teacherDao.delete(insertedTeacher);

        Assertions.assertTrue(isDeleted);
        Assertions.assertNotEquals(expectedSize, teacherDao.findAll().size());
    }

    @Test
    void findAll() {
        int expectedSize = 4;
        int resultSize = teacherDao.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "10; false"
    }, delimiter = ';')
    void findById(long expectedId, boolean expectedValue) {
        Optional<Teacher> teacher = teacherDao.findById(expectedId);

        Assertions.assertEquals(expectedValue, teacher.isPresent());
        teacher.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "10; false"
    }, delimiter = ';')
    void containsById(long expectedId, boolean expectedValue)  {
        boolean isContains = teacherDao.containsById(expectedId);

        Assertions.assertEquals(expectedValue, isContains);
    }
}