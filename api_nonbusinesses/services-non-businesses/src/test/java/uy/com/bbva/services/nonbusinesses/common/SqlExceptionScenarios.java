package uy.com.bbva.services.nonbusinesses.common;
import java.sql.SQLException;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

public enum SqlExceptionScenarios {

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
        public void setupMockBehavior(DAOCreate test, SQLException exception) throws SQLException {
            test.setupPrepareCallException(exception);
        }

        @Override
        public void verifyResourceClosure(DAOCreate test) throws SQLException {
            test.verifyResourceClosedOnPrepareFailure();
        }
    },

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
        public void setupMockBehavior(DAOCreate test, SQLException exception) throws SQLException {
            test.setupExecuteException(exception);
        }

        @Override
        public void verifyResourceClosure(DAOCreate test) throws SQLException {
            test.verifyResourceClosedOnException();
        }
    },

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
        public void setupMockBehavior(DAOCreate test, SQLException exception) throws SQLException {
            test.setupParameterSettingException(exception);
        }

        @Override
        public void verifyResourceClosure(DAOCreate test) throws SQLException {
            test.verifyResourceClosedOnException();
        }
    };

    private final String errorMessage;

    SqlExceptionScenarios(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


    // DAOUpdate
    public abstract void setupMockBehavior (DAOUpdateTest test, SQLException exception) throws SQLException;

    public abstract void verifyResourceClosure(DAOUpdateTest test) throws SQLException;

    // DAOCreate
    public abstract void setupMockBehavior(DAOCreate test, SQLException exception) throws SQLException;

    public abstract void verifyResourceClosure(DAOCreate test) throws SQLException;
}