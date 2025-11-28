package uy.com.bbva.services.nonbusinesses.dao.impl.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.dao.SQLStatements;
import uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl;
import uy.com.bbva.services.nonbusinesses.common.DAOUpdateTest;
import uy.com.bbva.services.nonbusinesses.model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uy.com.bbva.dtos.commons.utils.Constants.UY_COUNTRY_CODE;
import static uy.com.bbva.dtos.commons.utils.Constants.USER_DOCUMENT_TYPE_CI;
import static uy.com.bbva.services.nonbusinesses.common.SqlExceptionScenarios.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

/**
 * Suite de pruebas para DAOImpl.updateBusinessUser()
 * Valida la actualización del usuario asociado a la empresa.
 */
@DisplayName("Suite de pruebas de DAOImpl.updateBusinessUser():")
class UpdateBusinessUserTest extends DAOUpdateTest {

    @InjectMocks
    private DAOImpl daoImpl;

    private static final String ERROR_MESSAGE = "Error al actualizar el usuario de la empresa";
    private static final String DAO_CLASS_NAME = "uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl";

    private static final int PARAM_INDEX_PERSON_DOCUMENT = 1;
    private static final int PARAM_INDEX_PERSON_COUNTRY = 2;
    private static final int PARAM_INDEX_PERSON_DOCUMENT_TYPE = 3;

    private NonBusinessIdDatatype nonBusinessDatatype;
    private User user;

    @BeforeEach
    void setUp() {
        nonBusinessDatatype = createBusinessWithPersonDatatype();
        user = createUser();
    }


    @Override
    public String getSqlStatement() {
        return SQLStatements.UPDATE_BUSINESS_USER;
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
        ps.setString(PARAM_INDEX_PERSON_DOCUMENT, nonBusinessDatatype.getPersonDocument());
        ps.setInt(PARAM_INDEX_PERSON_COUNTRY, nonBusinessDatatype.getPersonCountry());
        ps.setInt(PARAM_INDEX_PERSON_DOCUMENT_TYPE, nonBusinessDatatype.getPersonDocumentType());
    }

    @Override
    public void verifyStatementParameters(Object... params) throws SQLException {
        verify(preparedStatement).setString(PARAM_INDEX_PERSON_DOCUMENT, PERSON_DOCUMENT_VALID);
        verify(preparedStatement).setInt(PARAM_INDEX_PERSON_COUNTRY, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(PARAM_INDEX_PERSON_DOCUMENT_TYPE, USER_DOCUMENT_TYPE_CI);
    }

    // ========== Happy Path ==========

    @Test
    @DisplayName("Debe actualizar el usuario de la empresa correctamente con parámetros válidos")
    void updateBusinessUser_givenValidParameters_updatesSuccessfully() throws Exception {
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessUser(nonBusinessDatatype, user));

        verifySuccessfulExecution();
    }

    @Test
    @DisplayName("Debe actualizar correctamente incluso con User nulo (parámetro no usado)")
    void updateBusinessUser_givenNullUser_updatesSuccessfully() throws Exception {
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessUser(nonBusinessDatatype, null));

        verifySuccessfulExecution();
    }

    @Test
    @DisplayName("Debe actualizar correctamente con diferentes datos de usuario")
    void updateBusinessUser_givenDifferentUserData_updatesSuccessfully() throws Exception {
        User differentUser = new User();
        differentUser.setName("differentUsername");
        differentUser.setPassword("differentPassword123");

        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessUser(nonBusinessDatatype, differentUser));

        verifySuccessfulExecution();
    }

    // ========== Exception Handling ==========

    @ParameterizedTest(name = "[{index}] SQLException en: {0}")
    @EnumSource(SqlExceptionScenarios.class)
    @DisplayName("Debe lanzar ServiceException cuando ocurre SQLException en diferentes puntos")
    void updateBusinessUser_givenSQLExceptionAtDifferentPoints_throwsServiceException(
            SqlExceptionScenarios scenario) throws Exception {

        SQLException sqlException = new SQLException(scenario.getErrorMessage());
        scenario.setupMockBehavior(this, sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> daoImpl.updateBusinessUser(nonBusinessDatatype, user));

        verifyServiceException(exception, sqlException, scenario.getErrorMessage());
        scenario.verifyResourceClosure(this);
    }

    // ========== Edge Case ==========

    @Test
    @DisplayName("Debe cerrar recursos incluso cuando ocurre una excepción")
    void updateBusinessUser_givenException_closesResourcesCorrectly() throws Exception {
        SQLException sqlException = new SQLException(PARAMETER_SETTING_ERROR);
        setupParameterSettingException(sqlException);

        assertThrows(ServiceException.class,
                () -> daoImpl.updateBusinessUser(nonBusinessDatatype, user));

        verifyResourceClosedOnException();
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando no se actualiza ninguna fila")
    void updateBusinessUser_givenNoRowsAffected_executesWithoutError() throws Exception {
        setupSuccessfulUpdate(ZERO_ROWS_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessUser(nonBusinessDatatype, user));

        verifySuccessfulExecution();
    }

    @Test
    @DisplayName("Debe usar correctamente los valores de país y tipo de documento de la persona")
    void updateBusinessUser_givenValidData_usesCorrectPersonIdentificationData() throws Exception {
        setupSuccessfulUpdate(SINGLE_ROW_UPDATED);

        assertDoesNotThrow(() -> daoImpl.updateBusinessUser(nonBusinessDatatype, user));

        verify(preparedStatement).setString(PARAM_INDEX_PERSON_DOCUMENT, PERSON_DOCUMENT_VALID);
        verify(preparedStatement).setInt(PARAM_INDEX_PERSON_COUNTRY, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(PARAM_INDEX_PERSON_DOCUMENT_TYPE, USER_DOCUMENT_TYPE_CI);
        verify(managerDataAccessAs400).closeResources(preparedStatement, null);
    }
}