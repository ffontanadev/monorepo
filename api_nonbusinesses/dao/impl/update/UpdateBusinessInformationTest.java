package uy.com.bbva.services.nonbusinesses.dao.impl.update;

import org.junit.jupiter.api.BeforeEach;
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
import uy.com.bbva.services.nonbusinesses.common.DAOUpdateTest;
import uy.com.bbva.services.nonbusinesses.model.external.BusinessInformation;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uy.com.bbva.dtos.commons.utils.Constants.RUT_DOCUMENT_TYPE;
import static uy.com.bbva.dtos.commons.utils.Constants.UY_COUNTRY_CODE;
import static uy.com.bbva.services.nonbusinesses.common.SqlExceptionScenarios.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

/**
 * Suite de pruebas para DAOImpl.updateBusinessInformation()
 * Valida la actualización de información empresarial (nombre, RUT y fecha de expiración).
 */
@DisplayName("Suite de pruebas de DAOImpl.updateBusinessInformation():")
class UpdateBusinessInformationTest extends DAOUpdateTest {

    @InjectMocks
    private DAOImpl daoImpl;

    private static final String ERROR_MESSAGE = "Error al completar la informacion de la empresa";
    private static final String DAO_CLASS_NAME = "uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl";

    private static final int PARAM_INDEX_NAME = 1;
    private static final int PARAM_INDEX_EXPIRATION = 2;
    private static final int PARAM_INDEX_RUT = 3;
    private static final int PARAM_INDEX_COUNTRY = 4;
    private static final int PARAM_INDEX_DOCUMENT_TYPE = 5;

    private BusinessInformation businessInformation;

    @BeforeEach
    void setUp() {
        businessInformation = createBusinessInformation();
    }

    @Override
    public String getSqlStatement() {
        return SQLStatements.UPDATE_BUSINESS_INFORMATION;
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
        String name = (String) params[0];
        String formattedExpiration = (String) params[1];

        ps.setString(PARAM_INDEX_NAME, name);
        ps.setString(PARAM_INDEX_EXPIRATION, formattedExpiration);
        ps.setString(PARAM_INDEX_RUT, businessInformation.getRut());
        ps.setInt(PARAM_INDEX_COUNTRY, UY_COUNTRY_CODE);
        ps.setInt(PARAM_INDEX_DOCUMENT_TYPE, RUT_DOCUMENT_TYPE);
    }

    @Override
    public void verifyStatementParameters(Object... params) throws SQLException {
        String name = (String) params[0];
        String formattedExpiration = (String) params[1];

        verify(preparedStatement).setString(PARAM_INDEX_NAME, name);
        verify(preparedStatement).setString(PARAM_INDEX_EXPIRATION, formattedExpiration);
        verify(preparedStatement).setString(PARAM_INDEX_RUT, BUSINESS_RUT_VALID);
        verify(preparedStatement).setInt(PARAM_INDEX_COUNTRY, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(PARAM_INDEX_DOCUMENT_TYPE, RUT_DOCUMENT_TYPE);
    }

    // ========== Happy Path ==========

    @ParameterizedTest(name = "[{index}] Debe actualizar correctamente con nombre: ''{0}''")
    @MethodSource("provideBusinessNameVariations")
    @DisplayName("Debe actualizar la información correctamente con diferentes variaciones de nombre")
    void updateBusinessInformation_givenValidNameVariations_updatesSuccessfully(
            String nameToTest,
            String displayDescription) throws Exception {

        businessInformation.setName(nameToTest);
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessInformation(businessInformation));

        verifySuccessfulExecution(nameToTest, DATE_VALID_FORMATTED);
    }

    private static Stream<Arguments> provideBusinessNameVariations() {
        return Stream.of(
                Arguments.of(BUSINESS_NAME_VALID, "nombre válido estándar"),
                Arguments.of(BUSINESS_NAME_EMPTY, "nombre vacío"),
                Arguments.of(null, "nombre nulo"),
                Arguments.of(BUSINESS_NAME_WITH_SPACES, "nombre con espacios"),
                Arguments.of(BUSINESS_NAME_LONG, "nombre muy largo")
        );
    }

    @ParameterizedTest(name = "[{index}] Debe formatear fecha correctamente: {0} -> {1}")
    @MethodSource("provideDateFormattingScenarios")
    @DisplayName("Debe formatear correctamente diferentes formatos de fecha de expiración")
    void updateBusinessInformation_givenDifferentDateFormats_formatsCorrectly(
            String inputDate,
            String expectedFormattedDate,
            String displayDescription) throws Exception {

        businessInformation.setExpiration(inputDate);
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessInformation(businessInformation));

        verifySuccessfulExecution(BUSINESS_NAME_VALID, expectedFormattedDate);
    }

    private static Stream<Arguments> provideDateFormattingScenarios() {
        return Stream.of(
                Arguments.of(DATE_VALID, DATE_VALID_FORMATTED, "fecha con formato ISO estándar"),
                Arguments.of(DATE_ALTERNATIVE, DATE_ALTERNATIVE_FORMATTED, "fecha alternativa con guiones"),
                Arguments.of("20251231", "20251231", "fecha ya formateada sin guiones")
        );
    }

    // ========== Exception Handling ==========

    @ParameterizedTest(name = "[{index}] SQLException en: {0}")
    @EnumSource(SqlExceptionScenarios.class)
    @DisplayName("Debe lanzar ServiceException cuando ocurre SQLException en diferentes puntos")
    void updateBusinessInformation_givenSQLExceptionAtDifferentPoints_throwsServiceException(
            SqlExceptionScenarios scenario) throws Exception{

        SQLException sqlException = new SQLException(scenario.getErrorMessage());
        scenario.setupMockBehavior(this, sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> daoImpl.updateBusinessInformation(businessInformation));

        verifyServiceException(exception, sqlException, scenario.getErrorMessage());
        scenario.verifyResourceClosure(this);
    }

    // ========== Edge Case  ==========

    @Test
    @DisplayName("Debe cerrar recursos incluso cuando ocurre una excepción")
    void updateBusinessInformation_givenException_closesResourcesCorrectly() throws Exception {
        SQLException sqlException = new SQLException(PARAMETER_SETTING_ERROR);
        setupParameterSettingException(sqlException);

        assertThrows(ServiceException.class,
                () -> daoImpl.updateBusinessInformation(businessInformation));

        verifyResourceClosedOnException();
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando no se actualiza ninguna fila")
    void updateBusinessInformation_givenNoRowsAffected_executesWithoutError() throws Exception {
        setupSuccessfulUpdate(ZERO_ROWS_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessInformation(businessInformation));

        verifySuccessfulExecution(BUSINESS_NAME_VALID, DATE_VALID_FORMATTED);
    }

    @Test
    @DisplayName("Debe usar correctamente los valores fijos de país y tipo de documento RUT")
    void updateBusinessInformation_givenValidData_usesCorrectCountryAndDocumentType() throws Exception {
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessInformation(businessInformation));

        verify(preparedStatement).setInt(PARAM_INDEX_COUNTRY, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(PARAM_INDEX_DOCUMENT_TYPE, RUT_DOCUMENT_TYPE);
        verify(managerDataAccessAs400).closeResources(preparedStatement, null);
    }
}