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
import uy.com.bbva.services.nonbusinesses.model.Balance;
import uy.com.bbva.services.nonbusinesses.model.EconomicData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uy.com.bbva.dtos.commons.utils.Constants.RUT_DOCUMENT_TYPE;
import static uy.com.bbva.dtos.commons.utils.Constants.UY_COUNTRY_CODE;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

/**
 * Suite de pruebas para DAOImpl.updateBusinessEconomicData()
 * Valida la actualización de datos económicos empresariales.
 */
@DisplayName("Suite de pruebas de DAOImpl.updateBusinessEconomicData():")
class UpdateBusinessEconomicDataTest extends DAOUpdateTest {

    @InjectMocks
    private DAOImpl daoImpl;

    private static final String ERROR_MESSAGE = "Error al actualizar la actividad economica";
    private static final String DAO_CLASS_NAME = "uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl";

    private static final int PARAM_INDEX_ECONOMIC_ACTIVITY_ID = 1;
    private static final int PARAM_INDEX_BALANCE_TYPE_INDICATOR = 2;
    public static final int PARAM_INDEX_PACKAGE_ASSIGNMENT = 3;
    private static final int PARAM_INDEX_INCOME_AMOUNT = 4;
    private static final int PARAM_INDEX_INCOME_DATE = 5;
    private static final int PARAM_INDEX_TAX_CONDITION_ID = 6;
    private static final int PARAM_INDEX_INCOME_DESCRIPTION = 7;
    private static final int PARAM_INDEX_BUSINESS_DOCUMENT = 8;
    private static final int PARAM_INDEX_COUNTRY = 9;
    private static final int PARAM_INDEX_DOCUMENT_TYPE = 10;

    private static final String BALANCE_INDICATOR_REAL = "REAL";
    private static final String BALANCE_INDICATOR_PROJECTED = "PROYECTADO";
    private static final String INCOME_DESCRIPTION = "Salario, remuneraciones, gastos personales, honorarios profesionales";

    private NonBusinessIdDatatype nonBusinessIdDatatype;
    private EconomicData economicData;
    private Date incomeDate;

    @BeforeEach
    void setUp() {
        nonBusinessIdDatatype = createBusinessDatatype();
        incomeDate = parseDate(INCOME_DATE_STRING);
        economicData = createEconomicDataWithRealIncome();
    }

    @Override
    public String getSqlStatement() {
        return SQLStatements.UPDATE_ECONOMIC_DATA;
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
        int economicActivityId = (int) params[0];
        String balanceTypeIndicator = (String) params[1];
        String packageAssignment = (String) params[2];
        double amount = (double) params[3];
        String formattedDate = (String) params[4];
        String taxConditionId = (String) params[5];

        ps.setInt(PARAM_INDEX_ECONOMIC_ACTIVITY_ID, economicActivityId);
        ps.setString(PARAM_INDEX_BALANCE_TYPE_INDICATOR, balanceTypeIndicator);
        ps.setString(PARAM_INDEX_PACKAGE_ASSIGNMENT, packageAssignment);
        ps.setDouble(PARAM_INDEX_INCOME_AMOUNT, amount);
        ps.setString(PARAM_INDEX_INCOME_DATE, formattedDate);
        ps.setString(PARAM_INDEX_TAX_CONDITION_ID, taxConditionId);
        ps.setString(PARAM_INDEX_INCOME_DESCRIPTION, INCOME_DESCRIPTION);
        ps.setString(PARAM_INDEX_BUSINESS_DOCUMENT, nonBusinessIdDatatype.getBusinessDocument());
        ps.setInt(PARAM_INDEX_COUNTRY, UY_COUNTRY_CODE);
        ps.setInt(PARAM_INDEX_DOCUMENT_TYPE, RUT_DOCUMENT_TYPE);
    }

    @Override
    public void verifyStatementParameters(Object... params) throws SQLException {
        int economicActivityId = (int) params[0];
        String balanceTypeIndicator = (String) params[1];
        String packageAssignment = (String) params[2];
        double amount = (double) params[3];
        String formattedDate = (String) params[4];
        String taxConditionId = (String) params[5];

        verify(preparedStatement).setInt(PARAM_INDEX_ECONOMIC_ACTIVITY_ID, economicActivityId);
        verify(preparedStatement).setString(PARAM_INDEX_BALANCE_TYPE_INDICATOR, balanceTypeIndicator);
        verify(preparedStatement).setString(PARAM_INDEX_PACKAGE_ASSIGNMENT, packageAssignment);
        verify(preparedStatement).setDouble(PARAM_INDEX_INCOME_AMOUNT, amount);
        verify(preparedStatement).setString(PARAM_INDEX_INCOME_DATE, formattedDate);
        verify(preparedStatement).setString(PARAM_INDEX_TAX_CONDITION_ID, taxConditionId);
        verify(preparedStatement).setString(PARAM_INDEX_INCOME_DESCRIPTION, INCOME_DESCRIPTION);  // ✅ NEW
        verify(preparedStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, BUSINESS_RUT_VALID);
        verify(preparedStatement).setInt(PARAM_INDEX_COUNTRY, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(PARAM_INDEX_DOCUMENT_TYPE, RUT_DOCUMENT_TYPE);
    }

    // ========== Happy Path ==========

    @ParameterizedTest(name = "[{index}] Debe actualizar con balance tipo: {0}")
    @MethodSource("provideBalanceTypeScenarios")
    @DisplayName("Debe actualizar correctamente con diferentes tipos de balance")
    void updateBusinessEconomicData_givenDifferentBalanceTypes_updatesSuccessfully(
            String balanceType,
            String expectedIndicator,
            String displayDescription) throws Exception {

        economicData.getFinancialInformation().getBalances().get(0).setBalanceType(balanceType);
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessEconomicData(nonBusinessIdDatatype, economicData));

        verifySuccessfulExecution(
                parseInt(ECONOMIC_ACTIVITY_ID_VALID),
                expectedIndicator,
                calculatePackageAssignment(INCOME_AMOUNT_VALID),
                INCOME_AMOUNT_VALID,
                INCOME_DATE_STRING,
                TAX_CONDITION_ID_VALID
        );
    }

    private static Stream<Arguments> provideBalanceTypeScenarios() {
        return Stream.of(
                Arguments.of(BALANCE_TYPE_REAL, BALANCE_INDICATOR_REAL, "ingreso anual real"),
                Arguments.of(BALANCE_TYPE_PROJECTED, BALANCE_INDICATOR_PROJECTED, "ingreso anual proyectado")
        );
    }

    @ParameterizedTest(name = "[{index}] Debe manejar monto: {0}")
    @MethodSource("provideAmountEdgeCases")
    @DisplayName("Debe manejar correctamente casos extremos de montos")
    void updateBusinessEconomicData_givenAmountEdgeCases_handlesCorrectly(
            double amount) throws Exception {

        economicData.getFinancialInformation().getBalances().get(0).setAmount(amount);
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessEconomicData(nonBusinessIdDatatype, economicData));

        verify(preparedStatement).setDouble(PARAM_INDEX_INCOME_AMOUNT, amount);
    }

    private static Stream<Arguments> provideAmountEdgeCases() {
        return Stream.of(
                Arguments.of(INCOME_AMOUNT_VALID, "monto válido positivo"),
                Arguments.of(0.0, "monto cero"),
                Arguments.of(-1000.0, "monto negativo")
        );
    }

    @Test
    @DisplayName("Debe usar el primer balance válido cuando hay múltiples balances")
    void updateBusinessEconomicData_givenMultipleBalances_usesFirstValidBalance() throws Exception {
        Balance otherBalance = new Balance();
        otherBalance.setBalanceType(BALANCE_TYPE_OTHER);
        otherBalance.setAmount(999999.99);
        otherBalance.setIncomeDate(incomeDate);
        economicData.getFinancialInformation().getBalances().add(0, otherBalance);

        Balance realBalance = new Balance();
        realBalance.setBalanceType(BALANCE_TYPE_REAL);
        realBalance.setAmount(INCOME_AMOUNT_VALID);
        realBalance.setIncomeDate(incomeDate);
        economicData.getFinancialInformation().getBalances().add(1, realBalance);

        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessEconomicData(nonBusinessIdDatatype, economicData));

        verify(preparedStatement).setString(PARAM_INDEX_BALANCE_TYPE_INDICATOR, BALANCE_INDICATOR_REAL);
        verify(preparedStatement).setDouble(PARAM_INDEX_INCOME_AMOUNT, INCOME_AMOUNT_VALID);
    }

    @Test
    @DisplayName("Debe formatear correctamente la fecha del balance")
    void updateBusinessEconomicData_givenValidDate_formatsCorrectly() throws Exception {
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessEconomicData(nonBusinessIdDatatype, economicData));

        verify(preparedStatement).setString(PARAM_INDEX_INCOME_DATE, INCOME_DATE_STRING);
    }


    @Test
    @DisplayName("No debe hacer nada cuando no hay balance de tipo REAL o PROJECTED")
    void updateBusinessEconomicData_givenNoMatchingBalanceType_doesNothing() throws Exception {
        economicData.getFinancialInformation().getBalances().get(0).setBalanceType(BALANCE_TYPE_OTHER);

        assertDoesNotThrow(() -> daoImpl.updateBusinessEconomicData(nonBusinessIdDatatype, economicData));

        verify(managerDataAccessAs400, never()).prepareStatement(any());
        verify(managerDataAccessAs400).closeResources(null, null);
    }

    @Test
    @DisplayName("No debe hacer nada cuando la lista de balances está vacía")
    void updateBusinessEconomicData_givenEmptyBalanceList_doesNothing() throws Exception {
        economicData.getFinancialInformation().setBalances(new ArrayList<>());

        assertDoesNotThrow(() -> daoImpl.updateBusinessEconomicData(nonBusinessIdDatatype, economicData));

        verify(managerDataAccessAs400, never()).prepareStatement(any());
        verify(managerDataAccessAs400).closeResources(null, null);
    }

    // ========== Exception Handling  ==========

    @ParameterizedTest(name = "[{index}] SQLException en: {0}")
    @EnumSource(SqlExceptionScenario.class)
    @DisplayName("Debe lanzar ServiceException cuando ocurre SQLException en diferentes puntos")
    void updateBusinessEconomicData_givenSQLExceptionAtDifferentPoints_throwsServiceException(
            SqlExceptionScenario scenario) throws Exception {

        SQLException sqlException = new SQLException(scenario.getErrorMessage());
        scenario.setupMockBehavior(this, sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> daoImpl.updateBusinessEconomicData(nonBusinessIdDatatype, economicData));

        verifyServiceException(exception, sqlException, scenario.getErrorMessage());
        scenario.verifyResourceClosure(this);
    }

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

    // ========== Edge Cases ==========

    @Test
    @DisplayName("Debe cerrar recursos incluso cuando ocurre una excepción")
    void updateBusinessEconomicData_givenException_closesResourcesCorrectly() throws Exception {
        SQLException sqlException = new SQLException("Generic SQL error");
        setupSuccessfulUpdate();
        doThrow(sqlException).when(preparedStatement).setDouble(anyInt(), anyDouble());

        assertThrows(ServiceException.class,
                () -> daoImpl.updateBusinessEconomicData(nonBusinessIdDatatype, economicData));

        verifyResourceClosedOnException();
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando no se actualiza ninguna fila")
    void updateBusinessEconomicData_givenNoRowsAffected_executesWithoutError() throws Exception {
        setupSuccessfulUpdate(ZERO_ROWS_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessEconomicData(nonBusinessIdDatatype, economicData));

        verifySuccessfulExecution(
                parseInt(ECONOMIC_ACTIVITY_ID_VALID),
                BALANCE_INDICATOR_REAL,
                calculatePackageAssignment(INCOME_AMOUNT_VALID),
                INCOME_AMOUNT_VALID,
                INCOME_DATE_STRING,
                TAX_CONDITION_ID_VALID
        );
    }
}