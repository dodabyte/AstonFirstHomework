package org.example.firsthomework.dao.global;

import lombok.extern.slf4j.Slf4j;
import org.example.firsthomework.entity.global.Entity;
import org.example.firsthomework.session.SessionManager;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.session.SessionManagerImpl;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class DataAccesObject<T extends Entity> {
    private final String DELETE = "DELETE FROM " + getTableName() + " WHERE id = (?)";
    private final String FIND_ALL = "SELECT * FROM " + getTableName();
    private final String FIND_BY_ID = "SELECT * FROM " + getTableName() + " WHERE id = (?)";
    private final String CONTAINS_BY_ID = "SELECT exists (SELECT 1 FROM " + getTableName() + " WHERE id = (?) LIMIT 1)";

    protected SessionManager sessionManager = SessionManagerImpl.getInstance();

    public abstract T create(ResultSet resultSet) throws SQLException;
    public abstract long insert(T entity) throws DataAccessObjectException;
    public abstract int update(T entity) throws DataAccessObjectException;

    public boolean delete(T entity) throws DataAccessObjectException {
        int countRowsUpdated;
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setLong(1, entity.getId());

            countRowsUpdated = statement.executeUpdate();
            sessionManager.commit();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            sessionManager.rollback();
            throw new DataAccessObjectException(e);
        }

        return countRowsUpdated > 0;
    }

    public boolean containsById(long id) throws DataAccessObjectException {
        boolean isContains = false;
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement preparedStatement = connection.prepareStatement(CONTAINS_BY_ID)) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isContains = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            sessionManager.rollback();
            throw new DataAccessObjectException(e);
        }

        return isContains;
    }
    public List<T> findAll() throws DataAccessObjectException {
        List<T> results = new ArrayList<>();
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(create(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            sessionManager.rollback();
            throw new DataAccessObjectException(e);
        }

        return results;
    }

    public Optional<T> findById(long id) throws DataAccessObjectException {
        T entity = null;
        sessionManager.begin();

        try (Connection connection = sessionManager.getSession();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    entity = create(resultSet);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            sessionManager.rollback();
            throw new DataAccessObjectException(e);
        }

        return Optional.ofNullable(entity);
    }

    public abstract String getTableName();

    public Logger log() {
        return log;
    }
}
