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
import static uy.com.bbva.services.nonbusinesses.common.SqlExceptionScenarios.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

/**
 * Suite de pruebas para DAOImpl.updateBusinessBankBranch()
 * Valida la actualización de sucursal bancaria de empresas.
 */
@DisplayName("Suite de pruebas de DAOImpl.updateBusinessBankBranch():")
class UpdateBusinessBankBranchTest extends DAOUpdateTest {

    @InjectMocks
    private DAOImpl daoImpl;

    private static final String ERROR_MESSAGE = "Error al actualizar la sucursal";

    private static final int PARAM_INDEX_BANK_BRANCH = 1;
    private static final int PARAM_INDEX_BUSINESS_DOCUMENT = 2;
    private static final int PARAM_INDEX_BUSINESS_COUNTRY = 3;
    private static final int PARAM_INDEX_BUSINESS_DOCUMENT_TYPE = 4;

    private NonBusinessIdDatatype nonBusinessDatatype;

    @BeforeEach
    void setUp() {
        nonBusinessDatatype = createBusinessDatatype();
    }

    @Override
    public String getSqlStatement() {
        return SQLStatements.UPDATE_BANK_BRANCH;
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
        String bankBranch = (String) params[0];
        ps.setString(PARAM_INDEX_BANK_BRANCH, bankBranch);
        ps.setString(PARAM_INDEX_BUSINESS_DOCUMENT, nonBusinessDatatype.getBusinessDocument());
        ps.setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        ps.setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, nonBusinessDatatype.getBusinessDocumentType());
    }

    @Override
    public void verifyStatementParameters(Object... params) throws SQLException {
        String bankBranch = (String) params[0];
        verify(preparedStatement).setString(PARAM_INDEX_BANK_BRANCH, bankBranch);
        verify(preparedStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, BUSINESS_RUT_VALID);
        verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, BUSINESS_DOCUMENT_TYPE_DEFAULT);
    }

    // ========== Happy Path ==========

    @ParameterizedTest(name = "[{index}] Debe actualizar correctamente con sucursal: ''{0}''")
    @MethodSource("provideBankBranchVariations")
    @DisplayName("Debe actualizar la sucursal bancaria correctamente con diferentes variaciones")
    void updateBusinessBankBranch_givenValidBankBranchVariations_updatesSuccessfully(
            String bankBranchToTest,
            String displayDescription) throws Exception {

        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessBankBranch(nonBusinessDatatype, bankBranchToTest));

        verifySuccessfulExecution(bankBranchToTest);
    }

    private static Stream<Arguments> provideBankBranchVariations() {
        return Stream.of(
                Arguments.of(BANK_BRANCH_VALID, "sucursal válida estándar"),
                Arguments.of(BANK_BRANCH_ALTERNATIVE, "sucursal alternativa"),
                Arguments.of(BANK_BRANCH_EMPTY, "sucursal vacía"),
                Arguments.of(null, "sucursal nula"),
                Arguments.of(BANK_BRANCH_WITH_SPACES, "sucursal con espacios"),
                Arguments.of(BANK_BRANCH_WITH_SPECIAL_CHARS, "sucursal con caracteres especiales"),
                Arguments.of(BANK_BRANCH_LONG, "sucursal muy larga")
        );
    }

    // ========== Exception Handling ==========

    @ParameterizedTest(name = "[{index}] SQLException en: {0}")
    @EnumSource(SqlExceptionScenarios.class)
    @DisplayName("Debe lanzar ServiceException cuando ocurre SQLException en diferentes puntos")
    void updateBusinessBankBranch_givenSQLExceptionAtDifferentPoints_throwsServiceException(
            SqlExceptionScenarios scenario) throws Exception {

        SQLException sqlException = new SQLException(scenario.getErrorMessage());
        scenario.setupMockBehavior(this, sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> daoImpl.updateBusinessBankBranch(nonBusinessDatatype, BANK_BRANCH_VALID));

        verifyServiceException(exception, sqlException, scenario.getErrorMessage());
        scenario.verifyResourceClosure(this);
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("Debe cerrar recursos incluso cuando ocurre una excepción")
    void updateBusinessBankBranch_givenException_closesResourcesCorrectly() throws Exception {
        SQLException sqlException = new SQLException(PARAMETER_SETTING_ERROR);
        setupParameterSettingException(sqlException);

        assertThrows(ServiceException.class,
                () -> daoImpl.updateBusinessBankBranch(nonBusinessDatatype, BANK_BRANCH_VALID));

        verifyResourceClosedOnException();
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando no se actualiza ninguna fila")
    void updateBusinessBankBranch_givenNoRowsAffected_executesWithoutError() throws Exception {
        setupSuccessfulUpdate(ZERO_ROWS_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessBankBranch(nonBusinessDatatype, BANK_BRANCH_VALID));

        verifySuccessfulExecution(BANK_BRANCH_VALID);
    }

    @Test
    @DisplayName("Debe manejar sucursales con solo números")
    void updateBusinessBankBranch_givenNumericBankBranch_updatesSuccessfully() throws Exception {
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessBankBranch(nonBusinessDatatype, BANK_BRANCH_NUMERIC_LARGE));

        verifySuccessfulExecution(BANK_BRANCH_NUMERIC_LARGE);
    }

    @Test
    @DisplayName("Debe verificar el orden correcto de los parámetros en el PreparedStatement")
    void updateBusinessBankBranch_givenValidParameters_setsParametersInCorrectOrder() throws Exception {
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        daoImpl.updateBusinessBankBranch(nonBusinessDatatype, BANK_BRANCH_VALID);

        var inOrder = inOrder(preparedStatement);
        inOrder.verify(preparedStatement).setString(PARAM_INDEX_BANK_BRANCH, BANK_BRANCH_VALID);
        inOrder.verify(preparedStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, BUSINESS_RUT_VALID);
        inOrder.verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        inOrder.verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, BUSINESS_DOCUMENT_TYPE_DEFAULT);
        inOrder.verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Debe usar correctamente los valores del datatype para documento y país")
    void updateBusinessBankBranch_givenValidData_usesCorrectDatatypeValues() throws Exception {
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessBankBranch(nonBusinessDatatype, BANK_BRANCH_VALID));

        verify(preparedStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, nonBusinessDatatype.getBusinessDocument());
        verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, nonBusinessDatatype.getBusinessDocumentType());
        verify(managerDataAccessAs400).closeResources(preparedStatement, null);
    }
}