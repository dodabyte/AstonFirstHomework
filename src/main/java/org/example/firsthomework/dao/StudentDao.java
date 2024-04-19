package org.example.firsthomework.dao;

import org.example.firsthomework.dao.global.DataAccesObject;
import org.example.firsthomework.entity.Student;
import org.example.firsthomework.exception.DataAccessObjectException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDao extends DataAccesObject<Student> {
    private final String INSERT = "INSERT INTO students (last_name, first_name, patronymic, group_id) VALUES ((?), (?), (?), (?))";
    private final String UPDATE = "UPDATE students SET last_name = (?), first_name = (?), patronymic = (?), group_id = (?) WHERE id = (?)";
    private final String FIND_ALL_BY_GROUP_ID = "SELECT * FROM students WHERE group_id = (?)";

    private final GroupDao groupDao = GroupDao.getInstance();

    private static StudentDao instance;

    private StudentDao() {}

    public static synchronized StudentDao getInstance() {
        if (instance == null) instance = new StudentDao();
        return instance;
    }

    @Override
    public Student create(ResultSet resultSet) throws SQLException {
        return new Student(
                resultSet.getLong("id"),
                resultSet.getString("last_name"),
                resultSet.getString("first_name"),
                resultSet.getString("patronymic"),
                groupDao.findById(resultSet.getLong("group_id")).orElse(null),
                List.of());
    }

    @Override
    public long insert(Student entity) throws DataAccessObjectException {
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getLastName());
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getPatronymic());
            if (entity.getGroup() == null) statement.setNull(4, Types.NULL);
            else statement.setLong(4, entity.getGroup().getId());

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
    public int update(Student entity) throws DataAccessObjectException {
        int countRowsUpdated;
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(1, entity.getLastName());
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getPatronymic());
            if (entity.getGroup() == null) statement.setNull(4, Types.NULL);
            else statement.setLong(4, entity.getGroup().getId());
            statement.setLong(5, entity.getId());

            countRowsUpdated = statement.executeUpdate();
            sessionManager.commit();
        } catch (SQLException e) {
            log().error(e.getMessage(), e);
            sessionManager.rollback();
            throw new DataAccessObjectException(e);
        }

        return countRowsUpdated;
    }

    public List<Student> findAllByGroupId(long id) {
        List<Student> results = new ArrayList<>();
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_GROUP_ID)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(create(resultSet));
                }
            }
        } catch (SQLException e) {
            log().error(e.getMessage(), e);
            sessionManager.rollback();
            throw new DataAccessObjectException(e);
        }

        return results;
    }

    @Override
    public String getTableName() {
        return "students";
    }
}
