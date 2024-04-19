package org.example.firsthomework.dao;

import org.example.firsthomework.dao.global.DataAccesObject;
import org.example.firsthomework.entity.Teacher;
import org.example.firsthomework.exception.DataAccessObjectException;

import java.sql.*;

public class TeacherDao extends DataAccesObject<Teacher> {
    private static final String INSERT = "INSERT INTO teachers (last_name, first_name, patronymic) VALUES ((?), (?), (?))";
    private static final String UPDATE = "UPDATE teachers set last_name = (?), first_name = (?), patronymic = (?) where id = (?)";

    private static TeacherDao instance;

    private TeacherDao() {}

    public static synchronized TeacherDao getInstance() {
        if (instance == null) instance = new TeacherDao();
        return instance;
    }

    @Override
    public Teacher create(ResultSet resultSet) throws SQLException {
        return new Teacher(
                resultSet.getLong("id"),
                resultSet.getString("last_name"),
                resultSet.getString("first_name"),
                resultSet.getString("patronymic"),
                null);
    }

    @Override
    public long insert(Teacher entity) throws DataAccessObjectException {
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getLastName());
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getPatronymic());

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
    public int update(Teacher entity) throws DataAccessObjectException {
        int countRowsUpdated;
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(1, entity.getLastName());
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getPatronymic());
            statement.setLong(4, entity.getId());

            countRowsUpdated = statement.executeUpdate();
            sessionManager.commit();
        } catch (SQLException e) {
            log().error(e.getMessage(), e);
            sessionManager.rollback();
            throw new DataAccessObjectException(e);
        }

        return countRowsUpdated;
    }

    @Override
    public String getTableName() {
        return "teachers";
    }
}
