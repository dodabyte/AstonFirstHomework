package org.example.firsthomework.dao;

import org.example.firsthomework.dao.global.DataAccesObject;
import org.example.firsthomework.entity.Discipline;
import org.example.firsthomework.exception.DataAccessObjectException;

import java.sql.*;

public class DisciplineDao extends DataAccesObject<Discipline> {
    private static final String INSERT = "INSERT INTO disciplines (name, teacher_id) VALUES ((?), (?))";
    private static final String UPDATE = "UPDATE disciplines set name = (?), teacher_id = (?) where id = (?)";

    private final TeacherDao teacherDao = TeacherDao.getInstance();

    private static DisciplineDao instance;

    private DisciplineDao() {}

    public static synchronized DisciplineDao getInstance() {
        if (instance == null) instance = new DisciplineDao();
        return instance;
    }

    @Override
    public Discipline create(ResultSet resultSet) throws SQLException {
        return new Discipline(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                teacherDao.findById(resultSet.getLong("teacher_id")).orElse(null));
    }

    @Override
    public long insert(Discipline entity) throws DataAccessObjectException {
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
            if (entity.getTeacher() == null) statement.setNull(2, Types.NULL);
            else statement.setLong(2, entity.getTeacher().getId());

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
    public int update(Discipline entity) throws DataAccessObjectException {
        int countRowsUpdated;
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(1, entity.getName());
            if (entity.getTeacher() == null) statement.setNull(2, Types.NULL);
            else statement.setLong(2, entity.getTeacher().getId());
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

    @Override
    public String getTableName() {
        return "disciplines";
    }
}
