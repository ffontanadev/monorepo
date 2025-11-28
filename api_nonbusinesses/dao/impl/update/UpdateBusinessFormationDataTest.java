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
import uy.com.bbva.services.nonbusinesses.model.Formation;
import uy.com.bbva.services.nonbusinesses.model.LegalDocument;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uy.com.bbva.dtos.commons.utils.Constants.UY_COUNTRY_CODE;
import static uy.com.bbva.services.nonbusinesses.common.SqlExceptionScenarios.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

/**
 * Suite de pruebas para DAOImpl.updateBusinessFormationData()
 * Valida la actualización de datos de formación empresarial (fecha de formación y documento legal BPS).
 */
@DisplayName("Suite de pruebas de DAOImpl.updateBusinessFormationData():")
class UpdateBusinessFormationDataTest extends DAOUpdateTest {

    @InjectMocks
    private DAOImpl daoImpl;

    private static final String ERROR_MESSAGE = "Error al actualizar la informacion de la formacion de la empresa";
    private static final String DAO_CLASS_NAME = "uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl";

    private static final int PARAM_INDEX_FORMATION_DATE = 1;
    private static final int PARAM_INDEX_ISSUE_DATE = 2;
    private static final int PARAM_INDEX_DOCUMENT_NUMBER = 3;
    private static final int PARAM_INDEX_ISSUE_DATE_DUPLICATE = 4;
    private static final int PARAM_INDEX_BUSINESS_DOCUMENT = 5;
    private static final int PARAM_INDEX_BUSINESS_COUNTRY = 6;
    private static final int PARAM_INDEX_BUSINESS_DOCUMENT_TYPE = 7;

    private NonBusinessIdDatatype nonBusinessDatatype;
    private Formation formation;
    private LegalDocument legalDocument;

    @BeforeEach
    void setUp() {
        nonBusinessDatatype = createBusinessDatatype();
        formation = createFormation();
        legalDocument = createLegalDocumentBPS();
    }

    @Override
    public String getSqlStatement() {
        return SQLStatements.UPDATE_BUSINESS_FORMATION_DATA;
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
        String formationDate = (String) params[0];
        String issueDate = (String) params[1];
        String documentNumber = (String) params[2];

        ps.setString(PARAM_INDEX_FORMATION_DATE, formationDate);
        ps.setString(PARAM_INDEX_ISSUE_DATE, issueDate);
        ps.setString(PARAM_INDEX_DOCUMENT_NUMBER, documentNumber);
        ps.setString(PARAM_INDEX_ISSUE_DATE_DUPLICATE, issueDate);
        ps.setString(PARAM_INDEX_BUSINESS_DOCUMENT, nonBusinessDatatype.getBusinessDocument());
        ps.setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        ps.setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, nonBusinessDatatype.getBusinessDocumentType());
    }

    @Override
    public void verifyStatementParameters(Object... params) throws SQLException {
        String formationDate = (String) params[0];
        String issueDate = (String) params[1];
        String documentNumber = (String) params[2];

        verify(preparedStatement).setString(PARAM_INDEX_FORMATION_DATE, formationDate);
        verify(preparedStatement).setString(PARAM_INDEX_ISSUE_DATE, issueDate);
        verify(preparedStatement).setString(PARAM_INDEX_DOCUMENT_NUMBER, documentNumber);
        verify(preparedStatement).setString(PARAM_INDEX_ISSUE_DATE_DUPLICATE, issueDate);
        verify(preparedStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, BUSINESS_RUT_VALID);
        verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, BUSINESS_DOCUMENT_TYPE_DEFAULT);
    }

    // ========== Happy Path ==========

    @Test
    @DisplayName("Debe actualizar los datos de formación correctamente con valores válidos")
    void updateBusinessFormationData_givenValidFormationAndLegalDocument_updatesSuccessfully() throws Exception {
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() ->
                daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument));

        verifySuccessfulExecution(
                FORMATION_DATE_FORMATTED,
                LEGAL_DOC_ISSUE_DATE_FORMATTED,
                LEGAL_DOC_NUMBER_VALID
        );
    }

    @ParameterizedTest(name = "[{index}] Debe actualizar con número de documento: ''{0}''")
    @MethodSource("provideDocumentNumberVariations")
    @DisplayName("Debe actualizar correctamente con diferentes variaciones de número de documento")
    void updateBusinessFormationData_givenDocumentNumberVariations_updatesSuccessfully(
            String documentNumber,
            String displayDescription) throws Exception {

        legalDocument.setDocumentNumber(documentNumber);
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() ->
                daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument));

        verifySuccessfulExecution(
                FORMATION_DATE_FORMATTED,
                LEGAL_DOC_ISSUE_DATE_FORMATTED,
                documentNumber
        );
    }

    private static Stream<Arguments> provideDocumentNumberVariations() {
        return Stream.of(
                Arguments.of(LEGAL_DOC_NUMBER_VALID, "número válido estándar"),
                Arguments.of(LEGAL_DOC_NUMBER_ALTERNATIVE, "número alternativo"),
                Arguments.of(LEGAL_DOC_NUMBER_EMPTY, "número vacío"),
                Arguments.of(null, "número nulo"),
                Arguments.of(LEGAL_DOC_NUMBER_WITH_SPACES, "número con espacios"),
                Arguments.of(LEGAL_DOC_NUMBER_LONG, "número muy largo")
        );
    }

    // ========== Date Formatting Tests ==========

    @ParameterizedTest(name = "[{index}] Debe formatear fecha de formación: {0} -> {1}")
    @MethodSource("provideFormationDateFormattingScenarios")
    @DisplayName("Debe formatear correctamente diferentes fechas de formación")
    void updateBusinessFormationData_givenDifferentFormationDates_formatsCorrectly(
            Date formationDate,
            String expectedFormatted,
            String displayDescription) throws Exception {

        formation.setDate(formationDate);
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() ->
                daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument));

        verifySuccessfulExecution(
                expectedFormatted,
                LEGAL_DOC_ISSUE_DATE_FORMATTED,
                LEGAL_DOC_NUMBER_VALID
        );
    }

    private static Stream<Arguments> provideFormationDateFormattingScenarios() {
        return Stream.of(
                Arguments.of(
                        parseDate(FORMATION_DATE_FORMATTED),
                        FORMATION_DATE_FORMATTED,
                        "fecha estándar"
                ),
                Arguments.of(
                        parseDate(FORMATION_DATE_ALTERNATIVE_FORMATTED),
                        FORMATION_DATE_ALTERNATIVE_FORMATTED,
                        "fecha alternativa"
                ),
                Arguments.of(
                        parseDate(FORMATION_DATE_OLD_FORMATTED),
                        FORMATION_DATE_OLD_FORMATTED,
                        "fecha límite antigua (1900)"
                ),
                Arguments.of(
                        parseDate(FORMATION_DATE_FUTURE_FORMATTED),
                        FORMATION_DATE_FUTURE_FORMATTED,
                        "fecha límite futura (2099)"
                )
        );
    }

    @ParameterizedTest(name = "[{index}] Debe formatear fecha de emisión: {0} -> {1}")
    @MethodSource("provideIssueDateFormattingScenarios")
    @DisplayName("Debe formatear correctamente diferentes fechas de emisión del documento legal")
    void updateBusinessFormationData_givenDifferentIssueDates_formatsCorrectly(
            Date issueDate,
            String expectedFormatted,
            String displayDescription) throws Exception {

        legalDocument.setIssueDate(issueDate);
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() ->
                daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument));

        verifySuccessfulExecution(
                FORMATION_DATE_FORMATTED,
                expectedFormatted,
                LEGAL_DOC_NUMBER_VALID
        );
    }

    private static Stream<Arguments> provideIssueDateFormattingScenarios() {
        return Stream.of(
                Arguments.of(
                        parseDate(LEGAL_DOC_ISSUE_DATE_FORMATTED),
                        LEGAL_DOC_ISSUE_DATE_FORMATTED,
                        "fecha de emisión estándar"
                ),
                Arguments.of(
                        parseDate(LEGAL_DOC_ISSUE_DATE_ALTERNATIVE_FORMATTED),
                        LEGAL_DOC_ISSUE_DATE_ALTERNATIVE_FORMATTED,
                        "fecha de emisión alternativa"
                ),
                Arguments.of(
                        parseDate("20250101"),
                        "20250101",
                        "fecha de emisión primer día del año"
                ),
                Arguments.of(
                        parseDate("20241231"),
                        "20241231",
                        "fecha de emisión último día del año"
                )
        );
    }

    @Test
    @DisplayName("Debe formatear ambas fechas correctamente cuando son diferentes")
    void updateBusinessFormationData_givenDifferentFormationAndIssueDates_formatsBothCorrectly() throws Exception {
        Date formationDate = parseDate(FORMATION_DATE_ALTERNATIVE_FORMATTED);
        Date issueDate = parseDate(LEGAL_DOC_ISSUE_DATE_ALTERNATIVE_FORMATTED);

        formation.setDate(formationDate);
        legalDocument.setIssueDate(issueDate);

        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() ->
                daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument));

        verify(preparedStatement).setString(PARAM_INDEX_FORMATION_DATE, FORMATION_DATE_ALTERNATIVE_FORMATTED);
        verify(preparedStatement).setString(PARAM_INDEX_ISSUE_DATE, LEGAL_DOC_ISSUE_DATE_ALTERNATIVE_FORMATTED);
    }

    // ========== Exception Handling ==========

    @ParameterizedTest(name = "[{index}] SQLException en: {0}")
    @EnumSource(SqlExceptionScenarios.class)
    @DisplayName("Debe lanzar ServiceException cuando ocurre SQLException en diferentes puntos")
    void updateBusinessFormationData_givenSQLExceptionAtDifferentPoints_throwsServiceException(
            SqlExceptionScenarios scenario) throws Exception {

        SQLException sqlException = new SQLException(scenario.getErrorMessage());
        scenario.setupMockBehavior(this, sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument));

        verifyServiceException(exception, sqlException, scenario.getErrorMessage());
        scenario.verifyResourceClosure(this);
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("Debe cerrar recursos incluso cuando ocurre una excepción")
    void updateBusinessFormationData_givenException_closesResourcesCorrectly() throws Exception {
        SQLException sqlException = new SQLException(PARAMETER_SETTING_ERROR);
        setupParameterSettingException(sqlException);

        assertThrows(ServiceException.class,
                () -> daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument));

        verifyResourceClosedOnException();
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando no se actualiza ninguna fila")
    void updateBusinessFormationData_givenNoRowsAffected_executesWithoutError() throws Exception {
        setupSuccessfulUpdate(ZERO_ROWS_UPDATED);

        assertDoesNotThrow(() ->
                daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument));

        verifySuccessfulExecution(
                FORMATION_DATE_FORMATTED,
                LEGAL_DOC_ISSUE_DATE_FORMATTED,
                LEGAL_DOC_NUMBER_VALID
        );
    }

    @Test
    @DisplayName("Debe verificar el orden correcto de los parámetros en el PreparedStatement")
    void updateBusinessFormationData_givenValidParameters_setsParametersInCorrectOrder() throws Exception {
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument);

        var inOrder = inOrder(preparedStatement);
        inOrder.verify(preparedStatement).setString(PARAM_INDEX_FORMATION_DATE, FORMATION_DATE_FORMATTED);
        inOrder.verify(preparedStatement).setString(PARAM_INDEX_ISSUE_DATE, LEGAL_DOC_ISSUE_DATE_FORMATTED);
        inOrder.verify(preparedStatement).setString(PARAM_INDEX_DOCUMENT_NUMBER, LEGAL_DOC_NUMBER_VALID);
        inOrder.verify(preparedStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, BUSINESS_RUT_VALID);
        inOrder.verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        inOrder.verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, BUSINESS_DOCUMENT_TYPE_DEFAULT);
        inOrder.verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Debe usar correctamente los valores fijos de país y tipo de documento")
    void updateBusinessFormationData_givenValidData_usesCorrectCountryAndDocumentType() throws Exception {
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() ->
                daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument));

        verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, BUSINESS_DOCUMENT_TYPE_DEFAULT);
        verify(managerDataAccessAs400).closeResources(preparedStatement, null);
    }

    @Test
    @DisplayName("Debe manejar fechas con la misma fecha de formación y emisión")
    void updateBusinessFormationData_givenSameFormationAndIssueDate_updatesSuccessfully() throws Exception {
        Date sameDate = parseDate("20240515");
        formation.setDate(sameDate);
        legalDocument.setIssueDate(sameDate);

        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() ->
                daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument));

        verify(preparedStatement).setString(PARAM_INDEX_FORMATION_DATE, "20240515");
        verify(preparedStatement).setString(PARAM_INDEX_ISSUE_DATE, "20240515");
    }

    @Test
    @DisplayName("Debe usar correctamente la zona horaria GMT para el formateo de fechas")
    void updateBusinessFormationData_givenDates_usesGMTTimezone() throws Exception {
        // Este test verifica que el formateo usa GMT y no la zona horaria local
        // que podría causar diferencias de día en fechas cerca de medianoche
        Date dateNearMidnight = parseDate("20240101"); // Medianoche GMT

        formation.setDate(dateNearMidnight);
        legalDocument.setIssueDate(dateNearMidnight);

        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() ->
                daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument));

        // Ambas fechas deben ser formateadas como "20240101" independientemente de la zona horaria local
        verify(preparedStatement).setString(PARAM_INDEX_FORMATION_DATE, "20240101");
        verify(preparedStatement).setString(PARAM_INDEX_ISSUE_DATE, "20240101");
    }
}