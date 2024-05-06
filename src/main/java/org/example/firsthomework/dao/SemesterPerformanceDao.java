package org.example.firsthomework.dao;

import org.example.firsthomework.dao.global.DataAccesObject;
import org.example.firsthomework.entity.SemesterPerformance;
import org.example.firsthomework.exception.DataAccessObjectException;

import java.sql.*;

public class SemesterPerformanceDao extends DataAccesObject<SemesterPerformance> {
    private final static String INSERT = "INSERT INTO semester_performance (student_id, discipline_id, mark) VALUES ((?), (?), (?))";
    private final static String UPDATE = "UPDATE semester_performance set student_id = (?), discipline_id = (?), mark = (?) where id = (?)";

    private final StudentDao studentDao = StudentDao.getInstance();
    private final DisciplineDao disciplineDao = DisciplineDao.getInstance();

    private static SemesterPerformanceDao instance;

    private SemesterPerformanceDao() {}

    public static synchronized SemesterPerformanceDao getInstance() {
        if (instance == null) instance = new SemesterPerformanceDao();
        return instance;
    }

    @Override
    public SemesterPerformance create(ResultSet resultSet) throws SQLException {
        return new SemesterPerformance(
                resultSet.getLong("id"),
                studentDao.findById(resultSet.getLong("student_id")).orElse(null),
                disciplineDao.findById(resultSet.getLong("discipline_id")).orElse(null),
                resultSet.getInt("mark"));
    }

    @Override
    public long insert(SemesterPerformance entity) throws DataAccessObjectException {
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            if (entity.getStudent() == null) statement.setNull(1, Types.NULL); // не забывай про {} - это улучшает читаемость
            else statement.setLong(1, entity.getStudent().getId());

            if (entity.getDiscipline() == null) statement.setNull(2, Types.NULL);
            else statement.setLong(2, entity.getDiscipline().getId());

            statement.setInt(3, entity.getMark());

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
    public int update(SemesterPerformance entity) throws DataAccessObjectException {
        int countRowsUpdated;
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            if (entity.getStudent() == null) statement.setNull(1, Types.NULL);
            else statement.setLong(1, entity.getStudent().getId());

            if (entity.getDiscipline() == null) statement.setNull(2, Types.NULL);
            else statement.setLong(2, entity.getDiscipline().getId());

            statement.setInt(3, entity.getMark());
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
        return "semester_performance";
    }
}
