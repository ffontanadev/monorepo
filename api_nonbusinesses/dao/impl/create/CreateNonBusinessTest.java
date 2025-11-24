package uy.com.bbva.services.nonbusinesses.dao.impl.create;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.dao.SQLStatements;
import uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl;
import uy.com.bbva.services.nonbusinesses.common.DAOCreateTest;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uy.com.bbva.dtos.commons.utils.Constants.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;
import static uy.com.bbva.services.nonbusinesses.model.status.Status.GEMA_CHANNEL;

/**
 * Suite de pruebas para DAOImpl.createNonBusiness()
 * Valida la creación de direcciones para entidades no-cliente.
 */
@DisplayName("Test suite para DAOImpl.createNonBusiness():")
class CreateNonBusinessTest extends DAOCreateTest {

    @InjectMocks
    private DAOImpl daoImpl;

    private static final String ERROR_MESSAGE = "Error al insertar el no cliente";
    // Constantes de índices
    private static final int PARAM_INDEX_PERSON_COUNTRY = 1;
    private static final int PARAM_INDEX_PERSON_DOCUMENT_TYPE = 2;
    private static final int PARAM_INDEX_PERSON_DOCUMENT = 3;
    private static final int PARAM_INDEX_BUSINESS_COUNTRY = 4;
    private static final int PARAM_INDEX_BUSINESS_DOCUMENT_TYPE = 5;
    private static final int PARAM_INDEX_BUSINESS_DOCUMENT = 6;
    private static final int PARAM_INDEX_CELLPHONE = 7;
    private static final int PARAM_INDEX_FLAG_1 = 8;
    private static final int PARAM_INDEX_FLAG_2 = 9;
    private static final int PARAM_INDEX_FLAG_3 = 10;
    private static final int PARAM_INDEX_FLAG_4 = 11;
    private static final int PARAM_INDEX_STATUS = 12;
    private static final int PARAM_INDEX_CREATION_TYPE = 13;
    private static final int PARAM_INDEX_CHANNEL = 14;

    private static final String FLAG_VALUE_N = "N";
    private static final String ENTERED_STATUS = "INGRESO";
    private static final String CREATION_TYPE = "UNI";

    @Override
    public String getSqlStatement() {
        return SQLStatements.CREATE_NON_BUSINESS;
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
    public void setStatementParameters(CallableStatement cs, Object... params) throws SQLException {
        String rut = (String) params[0];
        String ci = (String) params[1];
        String cellphone = (String) params[2];

        cs.setInt(PARAM_INDEX_PERSON_COUNTRY, UY_COUNTRY_CODE);
        cs.setInt(PARAM_INDEX_PERSON_DOCUMENT_TYPE, USER_DOCUMENT_TYPE_CI);
        cs.setString(PARAM_INDEX_PERSON_DOCUMENT, ci);
        cs.setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        cs.setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, RUT_DOCUMENT_TYPE);
        cs.setString(PARAM_INDEX_BUSINESS_DOCUMENT, rut);
        cs.setString(PARAM_INDEX_CELLPHONE, cellphone);
        cs.setString(PARAM_INDEX_FLAG_1, FLAG_VALUE_N);
        cs.setString(PARAM_INDEX_FLAG_2, FLAG_VALUE_N);
        cs.setString(PARAM_INDEX_FLAG_3, FLAG_VALUE_N);
        cs.setString(PARAM_INDEX_FLAG_4, FLAG_VALUE_N);
        cs.setString(PARAM_INDEX_STATUS, ENTERED_STATUS);
        cs.setString(PARAM_INDEX_CREATION_TYPE, CREATION_TYPE);
        cs.setInt(PARAM_INDEX_CHANNEL, GEMA_CHANNEL);
    }

    @Override
    public void verifyStatementParameters(Object... params) throws SQLException {
        String rut = (String) params[0];
        String ci = (String) params[1];
        String cellphone = (String) params[2];

        verify(callableStatement).setInt(PARAM_INDEX_PERSON_COUNTRY, UY_COUNTRY_CODE);
        verify(callableStatement).setInt(PARAM_INDEX_PERSON_DOCUMENT_TYPE, USER_DOCUMENT_TYPE_CI);
        verify(callableStatement).setString(PARAM_INDEX_PERSON_DOCUMENT, ci);
        verify(callableStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        verify(callableStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, RUT_DOCUMENT_TYPE);
        verify(callableStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, rut);
        verify(callableStatement).setString(PARAM_INDEX_CELLPHONE, cellphone);
        verify(callableStatement).setString(PARAM_INDEX_FLAG_1, FLAG_VALUE_N);
        verify(callableStatement).setString(PARAM_INDEX_FLAG_2, FLAG_VALUE_N);
        verify(callableStatement).setString(PARAM_INDEX_FLAG_3, FLAG_VALUE_N);
        verify(callableStatement).setString(PARAM_INDEX_FLAG_4, FLAG_VALUE_N);
        verify(callableStatement).setString(PARAM_INDEX_STATUS, ENTERED_STATUS);
        verify(callableStatement).setString(PARAM_INDEX_CREATION_TYPE, CREATION_TYPE);
        verify(callableStatement).setInt(PARAM_INDEX_CHANNEL, GEMA_CHANNEL);
    }

    // ========== Happy Path ==========

    @Test
    @DisplayName("Debe crear un non-business correctamente cuando todos los valores son válidos")
    void createNonBusiness_givenValidParameters_createsSuccessfully() throws Exception {
        setupSuccessfulCreate();

        assertDoesNotThrow(() ->
                daoImpl.createNonBusiness(BUSINESS_RUT_VALID, PERSON_DOCUMENT_VALID, MOBILE_VALID));

        verifySuccessfulExecution(BUSINESS_RUT_VALID, PERSON_DOCUMENT_VALID, MOBILE_VALID);
    }

    @ParameterizedTest(name = "[{index}] Should handle cellphone variation: ''{0}''")
    @MethodSource("provideCellphoneVariations")
    @DisplayName("Debe crear un non-business con diferentes celulares")
    void createNonBusiness_givenCellphoneVariations_createsSuccessfully(
            String cellphone,
            String description) throws Exception {

        setupSuccessfulCreate();

        assertDoesNotThrow(() ->
                daoImpl.createNonBusiness(BUSINESS_RUT_VALID, PERSON_DOCUMENT_VALID, cellphone));

        verifySuccessfulExecution(BUSINESS_RUT_VALID, PERSON_DOCUMENT_VALID, cellphone);
    }

    private static Stream<Arguments> provideCellphoneVariations() {
        return Stream.of(
                Arguments.of(MOBILE_VALID, "valid cellphone"),
                Arguments.of(null, "null cellphone"),
                Arguments.of(MOBILE_EMPTY, "empty cellphone"),
                Arguments.of(MOBILE_WITH_SPACES, "cellphone with spaces")
        );
    }

    @ParameterizedTest(name = "[{index}] Should handle document variation: RUT=''{0}'', CI=''{1}''")
    @MethodSource("provideDocumentVariations")
    @DisplayName("Debe handlear correctamente diferentes variaciones de RUT")
    void createNonBusiness_givenDocumentVariations_handlesCorrectly(
            String rut,
            String ci,
            String description) throws Exception {

        setupSuccessfulCreate();

        assertDoesNotThrow(() -> daoImpl.createNonBusiness(rut, ci, MOBILE_VALID));

        verifySuccessfulExecution(rut, ci, MOBILE_VALID);
    }

    private static Stream<Arguments> provideDocumentVariations() {
        return Stream.of(
                Arguments.of(BUSINESS_RUT_VALID, PERSON_DOCUMENT_VALID, "valid documents"),
                Arguments.of(null, PERSON_DOCUMENT_VALID, "null RUT"),
                Arguments.of(BUSINESS_RUT_VALID, null, "null CI"),
                Arguments.of("", "", "empty documents"),
                Arguments.of("  123456789012  ", "  12345678  ", "documents with spaces")
        );
    }

    // ========== Exception Handling ==========

    @ParameterizedTest(name = "[{index}] SQLException at: {0}")
    @EnumSource(SqlExceptionScenario.class)
    @DisplayName("Debe lanzar ServiceException cuando sucede un SQLException en diferentes puntos")
    void createNonBusiness_givenSQLExceptionAtDifferentPoints_throwsServiceException(
            SqlExceptionScenario scenario) throws Exception {

        SQLException sqlException = new SQLException(scenario.getErrorMessage());
        scenario.setupMockBehavior(this, sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> daoImpl.createNonBusiness(BUSINESS_RUT_VALID, PERSON_DOCUMENT_VALID, MOBILE_VALID));

        verifyServiceException(exception, sqlException, scenario.getErrorMessage());
        scenario.verifyResourceClosure(this);
    }

    /**
     * Enum que representa los diferentes puntos de fallo..
     */
    enum SqlExceptionScenario {
        PREPARE_CALL("Database connection error") {
            @Override
            void setupMockBehavior(DAOCreateTest test, SQLException exception) throws SQLException {
                test.setupPrepareCallException(exception);
            }

            @Override
            void verifyResourceClosure(DAOCreateTest test) throws SQLException {
                test.verifyResourceClosedOnPrepareFailure();
            }
        },

        EXECUTE("Insert failed") {
            @Override
            void setupMockBehavior(DAOCreateTest test, SQLException exception) throws SQLException {
                test.setupExecuteException(exception);
            }

            @Override
            void verifyResourceClosure(DAOCreateTest test) throws SQLException {
                test.verifyResourceClosedOnException();
            }
        },

        PARAMETER_SETTING("Parameter setting failed") {
            @Override
            void setupMockBehavior(DAOCreateTest test, SQLException exception) throws SQLException {
                test.setupParameterSettingException(exception);
            }

            @Override
            void verifyResourceClosure(DAOCreateTest test) throws SQLException {
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

        abstract void setupMockBehavior(DAOCreateTest test, SQLException exception) throws SQLException;
        abstract void verifyResourceClosure(DAOCreateTest test) throws SQLException;
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("Debe cerrar recursos incluso en escenarios de excepción")
    void createNonBusiness_givenException_closesResourcesCorrectly() throws Exception {
        SQLException sqlException = new SQLException(PARAMETER_SETTING_ERROR);
        setupParameterSettingException(sqlException);

        assertThrows(ServiceException.class,
                () -> daoImpl.createNonBusiness(BUSINESS_RUT_VALID, PERSON_DOCUMENT_VALID, MOBILE_VALID));

        verifyResourceClosedOnException();
    }

    @Test
    @DisplayName("Los parametros del statement deben estar en orden")
    void createNonBusiness_givenValidParameters_setsParametersInCorrectOrder() throws Exception {
        setupSuccessfulCreate();

        daoImpl.createNonBusiness(BUSINESS_RUT_VALID, PERSON_DOCUMENT_VALID, MOBILE_VALID);

        var inOrder = inOrder(callableStatement);
        inOrder.verify(callableStatement).setInt(PARAM_INDEX_PERSON_COUNTRY, UY_COUNTRY_CODE);
        inOrder.verify(callableStatement).setInt(PARAM_INDEX_PERSON_DOCUMENT_TYPE, USER_DOCUMENT_TYPE_CI);
        inOrder.verify(callableStatement).setString(PARAM_INDEX_PERSON_DOCUMENT, PERSON_DOCUMENT_VALID);
        inOrder.verify(callableStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        inOrder.verify(callableStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, RUT_DOCUMENT_TYPE);
        inOrder.verify(callableStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, BUSINESS_RUT_VALID);
        inOrder.verify(callableStatement).setString(PARAM_INDEX_CELLPHONE, MOBILE_VALID);
        inOrder.verify(callableStatement).setString(PARAM_INDEX_STATUS, ENTERED_STATUS);
        inOrder.verify(callableStatement).setString(PARAM_INDEX_CREATION_TYPE, CREATION_TYPE);
        inOrder.verify(callableStatement).setInt(PARAM_INDEX_CHANNEL, GEMA_CHANNEL);
        inOrder.verify(callableStatement).execute();
    }
}
