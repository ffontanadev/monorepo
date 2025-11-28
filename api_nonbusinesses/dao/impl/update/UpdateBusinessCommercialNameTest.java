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
 * Suite de pruebas para DAOImpl.updateBusinessCommercialName()
 * Valida la actualización del nombre comercial (doingBusinessAs) de empresas.
 */
@DisplayName("Suite de pruebas de DAOImpl.updateBusinessCommercialName():")
class UpdateBusinessCommercialNameTest extends DAOUpdateTest {

    @InjectMocks
    private DAOImpl daoImpl;

    private static final String ERROR_MESSAGE = "Error al actualizar el nombre comercial de la empresa";
    private static final String DAO_CLASS_NAME = "uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl";

    private static final int PARAM_INDEX_COMMERCIAL_NAME = 1;
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
        return SQLStatements.UPDATE_BUSINESS_COMMERCIAL_NAME;
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
        String commercialName = (String) params[0];
        ps.setString(PARAM_INDEX_COMMERCIAL_NAME, commercialName);
        ps.setString(PARAM_INDEX_BUSINESS_DOCUMENT, nonBusinessDatatype.getBusinessDocument());
        ps.setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        ps.setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, nonBusinessDatatype.getBusinessDocumentType());
    }

    @Override
    public void verifyStatementParameters(Object... params) throws SQLException {
        String commercialName = (String) params[0];
        verify(preparedStatement).setString(PARAM_INDEX_COMMERCIAL_NAME, commercialName);
        verify(preparedStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, BUSINESS_RUT_VALID);
        verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, BUSINESS_DOCUMENT_TYPE_DEFAULT);
    }

    // ========== Happy Path ==========

    @ParameterizedTest(name = "[{index}] Debe actualizar correctamente con nombre comercial: ''{0}''")
    @MethodSource("provideCommercialNameVariations")
    @DisplayName("Debe actualizar el nombre comercial correctamente con diferentes variaciones")
    void updateBusinessCommercialName_givenValidCommercialNameVariations_updatesSuccessfully(
            String commercialNameToTest,
            String displayDescription) throws Exception {

        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() ->
                daoImpl.updateBusinessCommercialName(nonBusinessDatatype, commercialNameToTest));

        verifySuccessfulExecution(commercialNameToTest);
    }

    private static Stream<Arguments> provideCommercialNameVariations() {
        return Stream.of(
                Arguments.of(BUSINESS_NAME_VALID, "nombre comercial válido estándar"),
                Arguments.of(BUSINESS_NAME_EMPTY, "nombre comercial vacío"),
                Arguments.of(null, "nombre comercial nulo"),
                Arguments.of(BUSINESS_NAME_WITH_SPACES, "nombre comercial con espacios"),
                Arguments.of(BUSINESS_NAME_LONG, "nombre comercial muy largo"),
                Arguments.of(COMMERCIAL_NAME_WITH_ACCENTS, "nombre comercial con acentos"),
                Arguments.of(COMMERCIAL_NAME_WITH_NUMBERS, "nombre comercial con números"),
                Arguments.of(COMMERCIAL_NAME_SINGLE_CHAR, "nombre comercial de un carácter"),
                Arguments.of(COMMERCIAL_NAME_WITH_PUNCTUATION, "nombre comercial con puntuación")
        );
    }

    // ========== Exception Handling ==========

    @ParameterizedTest(name = "[{index}] SQLException en: {0}")
    @EnumSource(SqlExceptionScenarios.class)
    @DisplayName("Debe lanzar ServiceException cuando ocurre SQLException en diferentes puntos")
    void updateBusinessCommercialName_givenSQLExceptionAtDifferentPoints_throwsServiceException(
            SqlExceptionScenarios scenario) throws Exception {

        SQLException sqlException = new SQLException(scenario.getErrorMessage());
        scenario.setupMockBehavior(this, sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> daoImpl.updateBusinessCommercialName(nonBusinessDatatype, BUSINESS_NAME_VALID));

        verifyServiceException(exception, sqlException, scenario.getErrorMessage());
        scenario.verifyResourceClosure(this);
    }

    // ========== Edge Cases ==========
    @Test
    @DisplayName("Debe cerrar recursos incluso cuando ocurre una excepción")
    void updateBusinessCommercialName_givenException_closesResourcesCorrectly() throws Exception {
        SQLException sqlException = new SQLException(PARAMETER_SETTING_ERROR);
        setupParameterSettingException(sqlException);

        assertThrows(ServiceException.class,
                () -> daoImpl.updateBusinessCommercialName(nonBusinessDatatype, BUSINESS_NAME_VALID));

        verifyResourceClosedOnException();
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando no se actualiza ninguna fila")
    void updateBusinessCommercialName_givenNoRowsAffected_executesWithoutError() throws Exception {
        setupSuccessfulUpdate(ZERO_ROWS_UPDATED);

        assertDoesNotThrow(() ->
                daoImpl.updateBusinessCommercialName(nonBusinessDatatype, BUSINESS_NAME_VALID));

        verifySuccessfulExecution(BUSINESS_NAME_VALID);
    }


    @Test
    @DisplayName("Debe verificar el orden correcto de los parámetros en el PreparedStatement")
    void updateBusinessCommercialName_givenValidParameters_setsParametersInCorrectOrder() throws Exception {
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        daoImpl.updateBusinessCommercialName(nonBusinessDatatype, BUSINESS_NAME_VALID);

        var inOrder = inOrder(preparedStatement);
        inOrder.verify(preparedStatement).setString(PARAM_INDEX_COMMERCIAL_NAME, BUSINESS_NAME_VALID);
        inOrder.verify(preparedStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, BUSINESS_RUT_VALID);
        inOrder.verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        inOrder.verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, BUSINESS_DOCUMENT_TYPE_DEFAULT);
        inOrder.verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Debe usar correctamente los valores del datatype para documento, país y tipo")
    void updateBusinessCommercialName_givenValidData_usesCorrectDatatypeValues() throws Exception {
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() ->
                daoImpl.updateBusinessCommercialName(nonBusinessDatatype, BUSINESS_NAME_VALID));

        verify(preparedStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, nonBusinessDatatype.getBusinessDocument());
        verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, nonBusinessDatatype.getBusinessDocumentType());
        verify(managerDataAccessAs400).closeResources(preparedStatement, null);
    }

    @Test
    @DisplayName("Debe permitir actualizar nombre comercial múltiples veces para la misma empresa")
    void updateBusinessCommercialName_givenMultipleUpdates_handlesAllCorrectly() throws Exception {
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> {
            daoImpl.updateBusinessCommercialName(nonBusinessDatatype, "Nombre Original");
            daoImpl.updateBusinessCommercialName(nonBusinessDatatype, "Nombre Modificado");
            daoImpl.updateBusinessCommercialName(nonBusinessDatatype, "Nombre Final");
        });

        verify(preparedStatement, times(3)).executeUpdate();
        verify(managerDataAccessAs400, times(3)).closeResources(preparedStatement, null);
    }

    @Test
    @DisplayName("Debe mantener la integridad del nombre comercial sin modificaciones")
    void updateBusinessCommercialName_givenCommercialName_preservesExactValue() throws Exception {
        String exactName = "  Exact Name with Spaces  ";
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        daoImpl.updateBusinessCommercialName(nonBusinessDatatype, exactName);

        verify(preparedStatement).setString(PARAM_INDEX_COMMERCIAL_NAME, exactName);
    }
}