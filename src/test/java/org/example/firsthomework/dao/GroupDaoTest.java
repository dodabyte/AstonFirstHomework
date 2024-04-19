package org.example.firsthomework.dao;

import org.example.firsthomework.entity.Discipline;
import org.example.firsthomework.entity.Group;
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
class GroupDaoTest {
    private static final String PROPERTY_FILE_PATH_DEFAULT = "connection/connection.properties";
    private static final String SQL_FILE_PATH_DEFAULT = "sql/db.sql";
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
        String expectedName = "Test Name";
        int expectedCourse = 4;
        int expectedSemester = 8;

        Group group = new Group(0, expectedName, expectedCourse, expectedSemester, null);

        long groupId = groupDao.insert(group);
        Optional<Group> groupResult = groupDao.findById(groupId);

        Assertions.assertTrue(groupResult.isPresent());
        Assertions.assertEquals(expectedName, groupResult.get().getName());
        Assertions.assertEquals(expectedCourse, groupResult.get().getCourse());
        Assertions.assertEquals(expectedSemester, groupResult.get().getSemester());
    }

    @Test
    void update() {
        long expectedId = 3;
        String expectedName = "New Test Name";
        int expectedCourse = 3;
        int expectedSemester = 6;

        Group updateGroup = groupDao.findById(expectedId).get();

        Assertions.assertNotEquals(expectedName, updateGroup.getName());
        Assertions.assertNotEquals(expectedCourse, updateGroup.getCourse());
        Assertions.assertNotEquals(expectedSemester, updateGroup.getSemester());

        updateGroup.setName(expectedName);
        updateGroup.setCourse(expectedCourse);
        updateGroup.setSemester(expectedSemester);

        groupDao.update(updateGroup);

        Group updatedGroup = groupDao.findById(expectedId).get();

        Assertions.assertEquals(expectedName, updatedGroup.getName());
        Assertions.assertEquals(expectedCourse, updatedGroup.getCourse());
        Assertions.assertEquals(expectedSemester, updatedGroup.getSemester());
    }

    @Test
    void delete() {
        String expectedName = "Test Name";
        int expectedCourse = 4;
        int expectedSemester = 8;

        Group group = new Group(0, expectedName, expectedCourse, expectedSemester, null);

        long groupId = groupDao.insert(group);
        Optional<Group> result = groupDao.findById(groupId);

        Assertions.assertTrue(result.isPresent());

        Group insertedGroup = result.get();

        Assertions.assertEquals(expectedName, insertedGroup.getName());
        Assertions.assertEquals(expectedCourse, insertedGroup.getCourse());
        Assertions.assertEquals(expectedSemester, insertedGroup.getSemester());

        int expectedSize = groupDao.findAll().size();

        boolean isDeleted = groupDao.delete(insertedGroup);

        Assertions.assertTrue(isDeleted);
        Assertions.assertNotEquals(expectedSize, groupDao.findAll().size());
    }

    @Test
    void findAll() {
        int expectedSize = 4;
        int resultSize = groupDao.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "2; true",
            "10; false"
    }, delimiter = ';')
    void findById(long expectedId, boolean expectedValue) {
        Optional<Group> group = groupDao.findById(expectedId);

        Assertions.assertEquals(expectedValue, group.isPresent());
        group.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "10; false"
    }, delimiter = ';')
    void containsById(long expectedId, boolean expectedValue)  {
        boolean isContains = groupDao.containsById(expectedId);

        Assertions.assertEquals(expectedValue, isContains);
    }
}