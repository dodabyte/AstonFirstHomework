package org.example.firsthomework.dao;

import org.example.firsthomework.dao.global.DataAccesObject;
import org.example.firsthomework.entity.Group;
import org.example.firsthomework.exception.DataAccessObjectException;

import java.sql.*;
import java.util.List;


public class GroupDao extends DataAccesObject<Group> {
    private final static String INSERT = "INSERT INTO groups (name, course, semester) VALUES ((?), (?), (?))";
    private final static String UPDATE = "UPDATE groups set name = (?), course = (?), semester = (?) where id = (?)";

    private static GroupDao instance;

    private GroupDao() {}

    public static synchronized GroupDao getInstance() {
        if (instance == null) instance = new GroupDao();
        return instance;
    }

    @Override
    public Group create(ResultSet resultSet) throws SQLException {
        return new Group(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getInt("course"),
                resultSet.getInt("semester"),
                List.of());
    }

    @Override
    public long insert(Group entity) throws DataAccessObjectException {
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
            statement.setInt(2, entity.getCourse());
            statement.setInt(3, entity.getSemester());

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
    public int update(Group entity) throws DataAccessObjectException {
        int countRowsUpdated;
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(1, entity.getName());
            statement.setInt(2, entity.getCourse());
            statement.setInt(3, entity.getSemester());
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
        return "groups";
    }
}
