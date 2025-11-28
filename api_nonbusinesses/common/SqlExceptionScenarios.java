package uy.com.bbva.services.nonbusinesses.common;

import java.sql.SQLException;

import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

/**
 * Shared SQL exception test scenarios for all DAO tests.
 * Provides standardized error simulation for database operations.
 *
 * <p>This enum eliminates duplication across multiple test classes by providing
 * a single source of truth for common exception scenarios in DAO testing.
 *
 * <p>Usage example:
 * <pre>
 * {@code
 * @ParameterizedTest
 * @EnumSource(SqlExceptionScenarios.class)
 * void testMethod_givenSQLException_throwsServiceException(SqlExceptionScenarios scenario) {
 *     SQLException sqlException = new SQLException(scenario.getErrorMessage());
 *     scenario.setupMockBehavior(this, sqlException);
 *
 *     // ... test assertions
 *
 *     scenario.verifyResourceClosure(this);
 * }
 * }
 * </pre>
 *
 * @see DAOUpdateTest
 * @see DAOCreateTest
 */
public enum SqlExceptionScenarios {

    /**
     * Simulates SQLException during PreparedStatement/CallableStatement preparation.
     * Typically indicates database connection issues.
     */
    PREPARE_STATEMENT(DB_CONNECTION_ERROR) {
        @Override
        public void setupMockBehavior(DAOUpdateTest test, SQLException exception) throws SQLException {
            test.setupPrepareStatementException(exception);
        }

        @Override
        public void verifyResourceClosure(DAOUpdateTest test) throws SQLException {
            test.verifyResourceClosedOnPrepareFailure();
        }

        @Override
        public void setupMockBehavior(DAOCreateTest test, SQLException exception) throws SQLException {
            test.setupPrepareCallException(exception);
        }

        @Override
        public void verifyResourceClosure(DAOCreateTest test) throws SQLException {
            test.verifyResourceClosedOnPrepareFailure();
        }
    },

    /**
     * Simulates SQLException during statement execution (executeUpdate/execute).
     * Typically indicates constraint violations or execution failures.
     */
    EXECUTE_UPDATE(UPDATE_FAILED_ERROR) {
        @Override
        public void setupMockBehavior(DAOUpdateTest test, SQLException exception) throws SQLException {
            test.setupExecuteUpdateException(exception);
        }

        @Override
        public void verifyResourceClosure(DAOUpdateTest test) throws SQLException {
            test.verifyResourceClosedOnException();
        }

        @Override
        public void setupMockBehavior(DAOCreateTest test, SQLException exception) throws SQLException {
            test.setupExecuteException(exception);
        }

        @Override
        public void verifyResourceClosure(DAOCreateTest test) throws SQLException {
            test.verifyResourceClosedOnException();
        }
    },

    /**
     * Simulates SQLException during parameter setting (setString, setInt, etc.).
     * Typically indicates type mismatch or invalid parameter values.
     */
    PARAMETER_SETTING(PARAMETER_SETTING_ERROR) {
        @Override
        public void setupMockBehavior(DAOUpdateTest test, SQLException exception) throws SQLException {
            test.setupParameterSettingException(exception);
        }

        @Override
        public void verifyResourceClosure(DAOUpdateTest test) throws SQLException {
            test.verifyResourceClosedOnException();
        }

        @Override
        public void setupMockBehavior(DAOCreateTest test, SQLException exception) throws SQLException {
            test.setupParameterSettingException(exception);
        }

        @Override
        public void verifyResourceClosure(DAOCreateTest test) throws SQLException {
            test.verifyResourceClosedOnException();
        }
    };

    private final String errorMessage;

    SqlExceptionScenarios(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the error message associated with this scenario.
     *
     * @return the error message from TestDataFactory constants
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets up mock behavior for UPDATE operations.
     * Configures the test's mocks to simulate this exception scenario.
     *
     * @param test the DAOUpdateTest instance
     * @param exception the SQLException to throw
     * @throws SQLException if mock setup fails
     */
    public abstract void setupMockBehavior(DAOUpdateTest test, SQLException exception) throws SQLException;

    /**
     * Verifies that resources were properly closed for UPDATE operations.
     *
     * @param test the DAOUpdateTest instance
     * @throws SQLException if verification fails
     */
    public abstract void verifyResourceClosure(DAOUpdateTest test) throws SQLException;

    /**
     * Sets up mock behavior for CREATE operations.
     * Configures the test's mocks to simulate this exception scenario.
     *
     * @param test the DAOCreateTest instance
     * @param exception the SQLException to throw
     * @throws SQLException if mock setup fails
     */
    public abstract void setupMockBehavior(DAOCreateTest test, SQLException exception) throws SQLException;

    /**
     * Verifies that resources were properly closed for CREATE operations.
     *
     * @param test the DAOCreateTest instance
     * @throws SQLException if verification fails
     */
    public abstract void verifyResourceClosure(DAOCreateTest test) throws SQLException;
}
