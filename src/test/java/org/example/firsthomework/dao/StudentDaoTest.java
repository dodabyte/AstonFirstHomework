package org.example.firsthomework.dao;

import org.example.firsthomework.entity.Group;
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

@Testcontainers
@Tag("DockerRequired")
class StudentDaoTest {
    private static final String PROPERTY_FILE_PATH_DEFAULT = "connection/connection.properties";
    private static final String SQL_FILE_PATH_DEFAULT = "sql/db.sql";
    public static StudentDao studentDao;
    public static GroupDao groupDao;
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
        studentDao = StudentDao.getInstance();
        groupDao = GroupDao.getInstance();
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
        long expectedGroupId = 3;

        Group expectedGroup = groupDao.findById(expectedGroupId).get();

        Student student = new Student(0, expectedLastName, expectedFirstName,
                expectedPatronymic, expectedGroup, null);

        long studentId = studentDao.insert(student);
        Optional<Student> result = studentDao.findById(studentId);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedLastName, result.get().getLastName());
        Assertions.assertEquals(expectedFirstName, result.get().getFirstName());
        Assertions.assertEquals(expectedPatronymic, result.get().getPatronymic());
        Assertions.assertEquals(expectedGroup, result.get().getGroup());
    }

    @Test
    void update() {
        long expectedId = 2;
        String expectedLastName = "Test Last Name";
        String expectedFirstName = "Test First Name";
        String expectedPatronymic = "Test Patronymic";
        long expectedGroupId = 2;

        Group expectedGroup = groupDao.findById(expectedGroupId).get();

        Student updateStudent = studentDao.findById(expectedId).get();

        Assertions.assertNotEquals(expectedLastName, updateStudent.getLastName());
        Assertions.assertNotEquals(expectedFirstName, updateStudent.getFirstName());
        Assertions.assertNotEquals(expectedPatronymic, updateStudent.getPatronymic());
        Assertions.assertNotEquals(expectedGroup, updateStudent.getGroup());

        updateStudent.setLastName(expectedLastName);
        updateStudent.setFirstName(expectedFirstName);
        updateStudent.setPatronymic(expectedPatronymic);
        updateStudent.setGroup(expectedGroup);

        studentDao.update(updateStudent);

        Student updatedStudent = studentDao.findById(expectedId).get();

        Assertions.assertEquals(expectedLastName, updatedStudent.getLastName());
        Assertions.assertEquals(expectedFirstName, updatedStudent.getFirstName());
        Assertions.assertEquals(expectedPatronymic, updatedStudent.getPatronymic());
        Assertions.assertEquals(expectedGroup, updatedStudent.getGroup());
    }

    @Test
    void delete() {
        String expectedLastName = "Test Last Name";
        String expectedFirstName = "Test First Name";
        String expectedPatronymic = "Test Patronymic";

        Student student = new Student(0, expectedLastName, expectedFirstName, expectedPatronymic, null, null);

        long studentId = studentDao.insert(student);
        Optional<Student> result = studentDao.findById(studentId);

        Assertions.assertTrue(result.isPresent());

        Student insertedStudent = result.get();

        Assertions.assertEquals(expectedLastName, insertedStudent.getLastName());
        Assertions.assertEquals(expectedFirstName, insertedStudent.getFirstName());
        Assertions.assertEquals(expectedPatronymic, insertedStudent.getPatronymic());

        int expectedSize = studentDao.findAll().size();

        boolean isDeleted = studentDao.delete(insertedStudent);

        Assertions.assertTrue(isDeleted);
        Assertions.assertNotEquals(expectedSize, studentDao.findAll().size());
    }

    @Test
    void findAll() {
        int expectedSize = 10;
        int resultSize = studentDao.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "6; true",
            "9; true",
            "10; true",
            "14; false",
            "18; false"
    }, delimiter = ';')
    void findById(long expectedId, boolean expectedValue) {
        Optional<Student> student = studentDao.findById(expectedId);

        Assertions.assertEquals(expectedValue, student.isPresent());
        student.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; 3",
            "2; 2",
            "3; 3",
            "4; 2"
    }, delimiter = ';')
    void findAllByGroupId(long expectedId, int expectedValue) {
        int resultSize = studentDao.findAllByGroupId(expectedId).size();

        Assertions.assertEquals(expectedValue, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "6; true",
            "9; true",
            "10; true",
            "14; false",
            "18; false"
    }, delimiter = ';')
    void containsById(long expectedId, boolean expectedValue) {
        boolean isContains = studentDao.containsById(expectedId);

        Assertions.assertEquals(expectedValue, isContains);
    }
}