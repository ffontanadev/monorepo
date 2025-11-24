package uy.com.bbva.services.nonbusinesses.dao.impl.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.dao.SQLStatements;
import uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl;
import uy.com.bbva.services.nonbusinesses.common.DAOUpdateTest;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uy.com.bbva.dtos.commons.utils.Constants.UY_COUNTRY_CODE;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

/**
 * Suite de pruebas para DAOImpl.updateBusinessMobile()
 * Valida la actualización de número de celular de empresas.
 */
@DisplayName("Suite de pruebas de DAOImpl.updateBusinessMobile():")
class UpdateBusinessMobileTest extends DAOUpdateTest {

    @InjectMocks
    private DAOImpl daoImpl;

    private static final String ERROR_MESSAGE = "Error al actualizar el celular de la empresa";
    private static final String DAO_CLASS_NAME = "uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl";

    private static final int PARAM_INDEX_MOBILE = 1;
    private static final int PARAM_INDEX_DOCUMENT = 2;
    private static final int PARAM_INDEX_COUNTRY = 3;
    private static final int PARAM_INDEX_DOCUMENT_TYPE = 4;

    private NonBusinessIdDatatype nonBusinessDatatype;

    @BeforeEach
    void setUp() {
        nonBusinessDatatype = createBusinessDatatype();
    }

    @Override
    public String getSqlStatement() {
        return SQLStatements.UPDATE_BUSINESS_MOBILE;
    }

    @Override
    public String getErrorMessage() {
        return ERROR_MESSAGE;
    }

    @Override
    public String getDaoClassName() {
        return DAO_CLASS_NAME;
    }

    @Override
    public void setStatementParameters(PreparedStatement ps, Object... params) throws SQLException {
        String mobile = (String) params[0];
        ps.setString(PARAM_INDEX_MOBILE, mobile);
        ps.setString(PARAM_INDEX_DOCUMENT, nonBusinessDatatype.getBusinessDocument());
        ps.setInt(PARAM_INDEX_COUNTRY, UY_COUNTRY_CODE);
        ps.setInt(PARAM_INDEX_DOCUMENT_TYPE, nonBusinessDatatype.getBusinessDocumentType());
    }

    @Override
    public void verifyStatementParameters(Object... params) throws SQLException {
        String mobile = (String) params[0];
        verify(preparedStatement).setString(PARAM_INDEX_MOBILE, mobile);
        verify(preparedStatement).setString(PARAM_INDEX_DOCUMENT, BUSINESS_RUT_VALID);
        verify(preparedStatement).setInt(PARAM_INDEX_COUNTRY, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(PARAM_INDEX_DOCUMENT_TYPE, BUSINESS_DOCUMENT_TYPE_DEFAULT);
    }

    // ========== Happy Path  ==========

    @ParameterizedTest(name = "[{index}] Debe actualizar correctamente con celular: ''{0}''")
    @MethodSource("provideMobileVariations")
    @DisplayName("Debe actualizar el celular correctamente con diferentes variaciones de número")
    void updateBusinessMobile_givenValidMobileVariations_updatesSuccessfully(
            String mobileToTest,
            String displayDescription) throws Exception {

        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessMobile(nonBusinessDatatype, mobileToTest));

        verifySuccessfulExecution(mobileToTest);
    }

    private static Stream<Arguments> provideMobileVariations() {
        return Stream.of(
                Arguments.of(MOBILE_VALID, "celular válido estándar"),
                Arguments.of(MOBILE_EMPTY, "celular vacío"),
                Arguments.of(null, "celular nulo"),
                Arguments.of(MOBILE_WITH_SPACES, "celular con espacios")
        );
    }


    // ========== Exception Handling  ==========

    @ParameterizedTest(name = "[{index}] SQLException en: {0}")
    @EnumSource(SqlExceptionScenario.class)
    @DisplayName("Debe lanzar ServiceException cuando ocurre SQLException en diferentes puntos")
    void updateBusinessMobile_givenSQLExceptionAtDifferentPoints_throwsServiceException(
            SqlExceptionScenario scenario) throws Exception {

        SQLException sqlException = new SQLException(scenario.getErrorMessage());
        scenario.setupMockBehavior(this, sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> daoImpl.updateBusinessMobile(nonBusinessDatatype, MOBILE_VALID));

        verifyServiceException(exception, sqlException, scenario.getErrorMessage());
        scenario.verifyResourceClosure(this);
    }

    /**
     * Enum que representa los diferentes puntos donde puede fallar una operación de update.
     */
    enum SqlExceptionScenario {
        PREPARE_STATEMENT(DB_CONNECTION_ERROR) {
            @Override
            void setupMockBehavior(DAOUpdateTest test, SQLException exception) throws SQLException {
                test.setupPrepareStatementException(exception);
            }

            @Override
            void verifyResourceClosure(DAOUpdateTest test) throws SQLException {
                test.verifyResourceClosedOnPrepareFailure();
            }
        },

        EXECUTE_UPDATE(UPDATE_FAILED_ERROR) {
            @Override
            void setupMockBehavior(DAOUpdateTest test, SQLException exception) throws SQLException {
                test.setupExecuteUpdateException(exception);
            }

            @Override
            void verifyResourceClosure(DAOUpdateTest test) throws SQLException {
                test.verifyResourceClosedOnException();
            }
        },

        PARAMETER_SETTING(PARAMETER_SETTING_ERROR) {
            @Override
            void setupMockBehavior(DAOUpdateTest test, SQLException exception) throws SQLException {
                test.setupParameterSettingException(exception);
            }

            @Override
            void verifyResourceClosure(DAOUpdateTest test) throws SQLException {
                test.verifyResourceClosedOnException();
            }
        };

        private final String errorMessage;

        SqlExceptionScenario(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        String getErrorMessage() {
            return errorMessage;
        }

        abstract void setupMockBehavior(DAOUpdateTest test, SQLException exception) throws SQLException;
        abstract void verifyResourceClosure(DAOUpdateTest test) throws SQLException;
    }


    // ========== Edge Cases  ==========

    @Test
    @DisplayName("Debe cerrar recursos incluso cuando ocurre una excepción")
    void updateBusinessMobile_givenException_closesResourcesCorrectly() throws Exception {
        SQLException sqlException = new SQLException(PARAMETER_SETTING_ERROR);
        setupParameterSettingException(sqlException);

        assertThrows(ServiceException.class,
                () -> daoImpl.updateBusinessMobile(nonBusinessDatatype, MOBILE_VALID));

        verifyResourceClosedOnException();
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando no se actualiza ninguna fila")
    void updateBusinessMobile_givenNoRowsAffected_executesWithoutError() throws Exception {
        setupSuccessfulUpdate(ZERO_ROWS_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessMobile(nonBusinessDatatype, MOBILE_VALID));

        verifySuccessfulExecution(MOBILE_VALID);
    }
}