package org.example.firsthomework.dao;

import org.example.firsthomework.entity.Discipline;
import org.example.firsthomework.entity.Teacher;
import org.example.firsthomework.entity.TeacherDiscipline;
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
class TeacherDisciplineDaoTest {
    private static final String PROPERTY_FILE_PATH_DEFAULT = "connection/connection.properties";
    private static final String SQL_FILE_PATH_DEFAULT = "sql/db.sql";
    public static TeacherDisciplineDao teacherDisciplineDao;
    public static TeacherDao teacherDao;
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
        teacherDisciplineDao = TeacherDisciplineDao.getInstance();
        teacherDao = TeacherDao.getInstance();
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

    private Teacher createTeacher() {
        String expectedLastName = "Test Last Name";
        String expectedFirstName = "Test First Name";
        String expectedPatronymic = "Test Patronymic";

        Teacher teacher = new Teacher(0, expectedLastName, expectedFirstName, expectedPatronymic, null);

        long teacherId = teacherDao.insert(teacher);
        Optional<Teacher> result = teacherDao.findById(teacherId);

        Assertions.assertTrue(result.isPresent());

        Teacher insertedTeacher = result.get();

        Assertions.assertEquals(expectedLastName, insertedTeacher.getLastName());
        Assertions.assertEquals(expectedFirstName, insertedTeacher.getFirstName());
        Assertions.assertEquals(expectedPatronymic, insertedTeacher.getPatronymic());

        return insertedTeacher;
    }

    private Discipline createDiscipline(Teacher expectedTeacher) {
        String expectedName = "Test Name";

        Discipline discipline = new Discipline(0, expectedName, expectedTeacher);

        long disciplineId = disciplineDao.insert(discipline);
        Optional<Discipline> result = disciplineDao.findById(disciplineId);

        Assertions.assertTrue(result.isPresent());

        Discipline insertedDiscipline = result.get();

        Assertions.assertEquals(expectedName, insertedDiscipline.getName());
        Assertions.assertEquals(expectedTeacher, insertedDiscipline.getTeacher());

        return insertedDiscipline;
    }

    @Test
    void insert() {
        Teacher expectedTeacher = createTeacher();
        Discipline expectedDiscipline = createDiscipline(expectedTeacher);

        TeacherDiscipline teacherDiscipline = new TeacherDiscipline(0, expectedTeacher.getId(), expectedDiscipline.getId());

        long teacherDisciplineId = teacherDisciplineDao.insert(teacherDiscipline);
        Optional<TeacherDiscipline> result = teacherDisciplineDao.findById(teacherDisciplineId);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedTeacher.getId(), result.get().getTeacherId());
        Assertions.assertEquals(expectedDiscipline.getId(), result.get().getDisciplineId());
    }

    @Test
    void update() {
        long expectedId = 1;
        Teacher expectedTeacher = createTeacher();
        Discipline expectedDiscipline = createDiscipline(expectedTeacher);

        TeacherDiscipline updateTeacherDiscipline = teacherDisciplineDao.findById(expectedId).get();

        Assertions.assertNotEquals(expectedTeacher.getId(), updateTeacherDiscipline.getTeacherId());
        Assertions.assertNotEquals(expectedDiscipline.getId(), updateTeacherDiscipline.getDisciplineId());

        updateTeacherDiscipline.setTeacherId(expectedTeacher.getId());
        updateTeacherDiscipline.setDisciplineId(expectedDiscipline.getId());

        teacherDisciplineDao.update(updateTeacherDiscipline);

        TeacherDiscipline updatedTeacherDiscipline = teacherDisciplineDao.findById(expectedId).get();

        Assertions.assertEquals(expectedTeacher.getId(), updateTeacherDiscipline.getTeacherId());
        Assertions.assertEquals(expectedDiscipline.getId(), updateTeacherDiscipline.getDisciplineId());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "10; false"
    }, delimiter = ';')
    void containsById(long expectedId, boolean expectedValue)  {
        boolean isContains = teacherDisciplineDao.containsById(expectedId);

        Assertions.assertEquals(expectedValue, isContains);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "10; false"
    }, delimiter = ';')
    void containsByTeacherId(long expectedId, boolean expectedValue) {
        boolean isContains = teacherDisciplineDao.containsByTeacherId(expectedId);

        Assertions.assertEquals(expectedValue, isContains);
    }

    @Test
    void findAll() {
        int expectedSize = 4;
        int resultSize = teacherDisciplineDao.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "3; true",
            "10; false"
    }, delimiter = ';')
    void findById(long expectedId, boolean expectedValue) {
        Optional<TeacherDiscipline> teacherDiscipline = teacherDisciplineDao.findById(expectedId);

        Assertions.assertEquals(expectedValue, teacherDiscipline.isPresent());
        teacherDiscipline.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }


    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "2; true",
            "10; false"
    }, delimiter = ';')
    void findByTeacherId(long expectedId, boolean expectedValue) {
        Optional<TeacherDiscipline> teacherDiscipline = teacherDisciplineDao.findByTeacherId(expectedId);

        Assertions.assertEquals(expectedValue, teacherDiscipline.isPresent());
        teacherDiscipline.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "2; true",
            "10; false"
    }, delimiter = ';')
    void findDisciplineByTeacherId(long expectedId, boolean expectedValue) {
        Discipline discipline = teacherDisciplineDao.findDisciplineByTeacherId(expectedId);

        Assertions.assertEquals(expectedValue, discipline != null);
        if (discipline != null) Assertions.assertEquals(expectedId, discipline.getId());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "2; true",
            "10; false"
    }, delimiter = ';')
    void findTeacherByDisciplineId(long expectedId, boolean expectedValue) {
        Teacher teacher = teacherDisciplineDao.findTeacherByDisciplineId(expectedId);

        Assertions.assertEquals(expectedValue, teacher != null);
        if (teacher != null) Assertions.assertEquals(expectedId, teacher.getId());
    }
}