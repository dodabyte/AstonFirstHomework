package org.example.firsthomework.dao;

import org.example.firsthomework.dao.global.DataAccesObject;
import org.example.firsthomework.entity.Discipline;
import org.example.firsthomework.entity.Teacher;
import org.example.firsthomework.entity.TeacherDiscipline;
import org.example.firsthomework.exception.DataAccessObjectException;

import java.sql.*;
import java.util.Optional;

public class TeacherDisciplineDao extends DataAccesObject<TeacherDiscipline> {
    private static final String INSERT = "INSERT INTO teacher_discipline (teacher_id, discipline_id) VALUES ((?), (?))";
    private static final String UPDATE = "UPDATE teacher_discipline set teacher_id = (?), discipline_id = (?) where id = (?)";
    private static final String FIND_BY_TEACHER_ID = "SELECT teacher_discipline.id, teacher_discipline.teacher_id, teacher_discipline.discipline_id from teacher_discipline where teacher_id = (?)";
    private static final String FIND_BY_DISCIPLINE_ID = "SELECT teacher_discipline.id, teacher_discipline.teacher_id, teacher_discipline.discipline_id from teacher_discipline where discipline_id = (?)";
    private static final String CONTAINS_BY_TEACHER_ID = "SELECT exists (SELECT 1 FROM teacher_discipline WHERE teacher_id = (?) LIMIT 1)";

    private final TeacherDao teacherDao = TeacherDao.getInstance();
    private final DisciplineDao disciplineDao = DisciplineDao.getInstance();

    private static TeacherDisciplineDao instance;

    private TeacherDisciplineDao() {}

    public static synchronized TeacherDisciplineDao getInstance() {
        if (instance == null) instance = new TeacherDisciplineDao();
        return instance;
    }

    @Override
    public TeacherDiscipline create(ResultSet resultSet) throws SQLException {
        return new TeacherDiscipline(
                resultSet.getLong("id"),
                resultSet.getLong("teacher_id"),
                resultSet.getLong("discipline_id"));
    }

    @Override
    public long insert(TeacherDiscipline entity) throws DataAccessObjectException {
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, entity.getTeacherId());
            statement.setLong(2, entity.getDisciplineId());

            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                resultSet.next();
                long id = resultSet.getLong(1);
                sessionManager.commit();
                return id;
            }
        } catch (SQLException e) {
            log().error(e.getMessage(), e);
            sessionManager.rollback();
            throw new DataAccessObjectException(e);
        }
    }

    @Override
    public int update(TeacherDiscipline entity) throws DataAccessObjectException {
        int countRowsUpdated;
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setLong(1, entity.getTeacherId());
            statement.setLong(2, entity.getDisciplineId());
            statement.setLong(3, entity.getId());

            countRowsUpdated = statement.executeUpdate();
            sessionManager.commit();
        } catch (SQLException e) {
            log().error(e.getMessage(), e);
            sessionManager.rollback();
            throw new DataAccessObjectException(e);
        }

        return countRowsUpdated;
    }

    public boolean containsByTeacherId(long id) throws DataAccessObjectException {
        boolean isContains = false;
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement preparedStatement = connection.prepareStatement(CONTAINS_BY_TEACHER_ID)) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isContains = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            log().error(e.getMessage(), e);
            sessionManager.rollback();
            throw new DataAccessObjectException(e);
        }

        return isContains;
    }

    public Optional<TeacherDiscipline> findByTeacherId(long id) throws DataAccessObjectException {
        TeacherDiscipline entity = null;
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_TEACHER_ID)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    entity = create(resultSet);
                }
            }
        } catch (SQLException e) {
            log().error(e.getMessage(), e);
            sessionManager.rollback();
            throw new DataAccessObjectException(e);
        }

        return Optional.ofNullable(entity);
    }

    public Discipline findDisciplineByTeacherId(long id) throws DataAccessObjectException {
        Discipline result = null;
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_TEACHER_ID)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Optional<Discipline> discipline = disciplineDao.findById(resultSet.getLong("discipline_id"));
                    if (discipline.isPresent()) result = discipline.get();
                }
            }
        } catch (SQLException e) {
            log().error(e.getMessage(), e);
            sessionManager.rollback();
            throw new DataAccessObjectException(e);
        }

        return result;
    }

    public Teacher findTeacherByDisciplineId(long id) throws DataAccessObjectException {
        Teacher result = null;
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_DISCIPLINE_ID)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Optional<Teacher> teacher = teacherDao.findById(resultSet.getLong("teacher_id"));
                    if (teacher.isPresent()) result = teacher.get();
                }
            }
        } catch (SQLException e) {
            log().error(e.getMessage(), e);
            sessionManager.rollback();
            throw new DataAccessObjectException(e);
        }

        return result;
    }

    @Override
    public String getTableName() {
        return "teacher_discipline";
    }
}
