package uy.com.bbva.services.nonbusinesses.dao.impl.create;

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
import uy.com.bbva.services.nonbusinesses.common.SqlExceptionScenarios;
import uy.com.bbva.services.nonbusinesses.dao.SQLStatements;
import uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl;
import uy.com.bbva.services.nonbusinesses.common.DAOCreate;
import uy.com.bbva.services.nonbusinesses.model.AddressDatatype;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uy.com.bbva.dtos.commons.utils.Constants.RUT_DOCUMENT_TYPE;
import static uy.com.bbva.dtos.commons.utils.Constants.UY_COUNTRY_CODE;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

/**
 * Suite de pruebas para DAOImpl.createAddress()
 * Valida la creación de direcciones para entidades no-cliente.
 */
@DisplayName("Suite de pruebas de DAOImpl.createAddress():")
class CreateAddressTest extends DAOCreate {

    @InjectMocks
    private DAOImpl daoImpl;

    private static final String ERROR_MESSAGE = "Error al crear la direccion";

    private static final int LEGAL_ADDRESS = 1;

    private static final int PARAM_INDEX_BUSINESS_COUNTRY = 1;
    private static final int PARAM_INDEX_BUSINESS_DOCUMENT_TYPE = 2;
    private static final int PARAM_INDEX_BUSINESS_DOCUMENT = 3;
    private static final int PARAM_INDEX_ADDRESS_TYPE = 4;
    private static final int PARAM_INDEX_COUNTRY = 5;
    private static final int PARAM_INDEX_POSTAL_CODE = 6;
    private static final int PARAM_INDEX_DEPARTMENT = 7;
    private static final int PARAM_INDEX_CITY = 8;
    private static final int PARAM_INDEX_LEVEL1_ID = 9;
    private static final int PARAM_INDEX_LEVEL2_ID = 10;
    private static final int PARAM_INDEX_LEVEL3_ID = 11;
    private static final int PARAM_INDEX_LEVEL1_NAME = 12;
    private static final int PARAM_INDEX_LEVEL2_NAME = 13;
    private static final int PARAM_INDEX_LEVEL3_NAME = 14;


    private NonBusinessIdDatatype nonBusinessIdDatatype;
    private AddressDatatype addressDatatype;

    @BeforeEach
    void setUp() {
        nonBusinessIdDatatype = createBusinessDatatype();
        addressDatatype = createValidAddressDatatype();
    }

    @Override
    public String getSqlStatement() {
        return SQLStatements.INSERT_ADDRESS;
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
        NonBusinessIdDatatype businessDatatype = (NonBusinessIdDatatype) params[0];
        AddressDatatype address = (AddressDatatype) params[1];

        cs.setInt(PARAM_INDEX_BUSINESS_COUNTRY, businessDatatype.getBusinessCountry());
        cs.setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, businessDatatype.getBusinessDocumentType());
        cs.setString(PARAM_INDEX_BUSINESS_DOCUMENT, businessDatatype.getBusinessDocument());
        cs.setInt(PARAM_INDEX_ADDRESS_TYPE, LEGAL_ADDRESS);
        cs.setInt(PARAM_INDEX_COUNTRY, Integer.parseInt(address.getCountry().getId()));
        cs.setString(PARAM_INDEX_POSTAL_CODE, trimToEmpty(address.getPostalCode()));
        cs.setInt(PARAM_INDEX_DEPARTMENT, Integer.parseInt(address.getDepartment().getId()));
        cs.setInt(PARAM_INDEX_CITY, Integer.parseInt(address.getCityOrNeighborhood().getId()));
        cs.setInt(PARAM_INDEX_LEVEL1_ID, Integer.parseInt(address.getLevel1().getId()));
        cs.setInt(PARAM_INDEX_LEVEL2_ID, Integer.parseInt(address.getLevel2().getId()));
        cs.setInt(PARAM_INDEX_LEVEL3_ID, Integer.parseInt(address.getLevel3().getId()));
        cs.setString(PARAM_INDEX_LEVEL1_NAME, trimToEmpty(address.getLevel1().getName()));
        cs.setString(PARAM_INDEX_LEVEL2_NAME, trimToEmpty(address.getLevel2().getName()));
        cs.setString(PARAM_INDEX_LEVEL3_NAME, trimToEmpty(address.getLevel3().getName()));
    }

    @Override
    public void verifyStatementParameters(Object... params) throws SQLException {
        NonBusinessIdDatatype businessDatatype = (NonBusinessIdDatatype) params[0];
        AddressDatatype address = (AddressDatatype) params[1];

        verify(callableStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, businessDatatype.getBusinessCountry());
        verify(callableStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, businessDatatype.getBusinessDocumentType());
        verify(callableStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, businessDatatype.getBusinessDocument());
        verify(callableStatement).setInt(PARAM_INDEX_ADDRESS_TYPE, LEGAL_ADDRESS);
        verify(callableStatement).setInt(PARAM_INDEX_COUNTRY, Integer.parseInt(address.getCountry().getId()));
        verify(callableStatement).setString(PARAM_INDEX_POSTAL_CODE, trimToEmpty(address.getPostalCode()));
        verify(callableStatement).setInt(PARAM_INDEX_DEPARTMENT, Integer.parseInt(address.getDepartment().getId()));
        verify(callableStatement).setInt(PARAM_INDEX_CITY, Integer.parseInt(address.getCityOrNeighborhood().getId()));
        verify(callableStatement).setInt(PARAM_INDEX_LEVEL1_ID, Integer.parseInt(address.getLevel1().getId()));
        verify(callableStatement).setInt(PARAM_INDEX_LEVEL2_ID, Integer.parseInt(address.getLevel2().getId()));
        verify(callableStatement).setInt(PARAM_INDEX_LEVEL3_ID, Integer.parseInt(address.getLevel3().getId()));
        verify(callableStatement).setString(PARAM_INDEX_LEVEL1_NAME, trimToEmpty(address.getLevel1().getName()));
        verify(callableStatement).setString(PARAM_INDEX_LEVEL2_NAME, trimToEmpty(address.getLevel2().getName()));
        verify(callableStatement).setString(PARAM_INDEX_LEVEL3_NAME, trimToEmpty(address.getLevel3().getName()));
    }

    @Test
    @DisplayName("Debe crear una dirección correctamente cuando todos los valores son válidos")
    void createAddress_givenValidParameters_createsSuccessfully() throws Exception {
        setupSuccessfulCreate();

        assertDoesNotThrow(() -> daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype));

        verifySuccessfulExecution(nonBusinessIdDatatype, addressDatatype);
    }

    @ParameterizedTest(name = "[{index}] Debe manejar código postal: ''{0}''")
    @MethodSource("providePostalCodeVariations")
    @DisplayName("Debe crear dirección correctamente con diferentes variaciones de código postal")
    void createAddress_givenPostalCodeVariations_createsSuccessfully(
            String postalCode,
            String description) throws Exception {

        addressDatatype.setPostalCode(postalCode);
        setupSuccessfulCreate();

        assertDoesNotThrow(() -> daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype));

        verifySuccessfulExecution(nonBusinessIdDatatype, addressDatatype);
    }

    private static Stream<Arguments> providePostalCodeVariations() {
        return Stream.of(
                Arguments.of(POSTAL_CODE_VALID, "código postal válido"),
                Arguments.of(POSTAL_CODE_EMPTY, "código postal vacío"),
                Arguments.of(null, "código postal nulo"),
                Arguments.of("  11200  ", "código postal con espacios")
        );
    }

    @ParameterizedTest(name = "[{index}] Debe manejar nombres de nivel: ''{0}''")
    @MethodSource("provideLevelNameVariations")
    @DisplayName("Debe manejar correctamente diferentes variaciones de nombres de nivel")
    void createAddress_givenLevelNameVariations_handlesCorrectly(
            String levelName,
            String description) throws Exception {

        addressDatatype.getLevel1().setName(levelName);
        addressDatatype.getLevel2().setName(levelName);
        addressDatatype.getLevel3().setName(levelName);

        setupSuccessfulCreate();

        assertDoesNotThrow(() -> daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype));

        verifySuccessfulExecution(nonBusinessIdDatatype, addressDatatype);
    }

    private static Stream<Arguments> provideLevelNameVariations() {
        return Stream.of(
                Arguments.of(LEVEL1_NAME_VALID, "nombre válido"),
                Arguments.of("", "nombre vacío"),
                Arguments.of(null, "nombre nulo"),
                Arguments.of("  Calle con espacios  ", "nombre con espacios")
        );
    }


    @ParameterizedTest(name = "[{index}] SQLException en: {0}")
    @EnumSource(SqlExceptionScenarios.class)
    @DisplayName("Debe lanzar ServiceException cuando ocurre SQLException en diferentes puntos")
    void createAddress_givenSQLExceptionAtDifferentPoints_throwsServiceException(
            SqlExceptionScenarios scenario) throws Exception {

        SQLException sqlException = new SQLException(scenario.getErrorMessage());
        scenario.setupMockBehavior(this, sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype));

        verifyServiceException(exception, sqlException, scenario.getErrorMessage());
        scenario.verifyResourceClosure(this);
    }


    @Test
    @DisplayName("Debe cerrar recursos incluso cuando ocurre una excepción")
    void createAddress_givenException_closesResourcesCorrectly() throws Exception {
        SQLException sqlException = new SQLException(PARAMETER_SETTING_ERROR);
        setupParameterSettingException(sqlException);

        assertThrows(ServiceException.class,
                () -> daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype));

        verifyResourceClosedOnException();
    }

    @Test
    @DisplayName("Debe parsear correctamente los IDs de String a Integer")
    void createAddress_givenValidIds_parsesCorrectly() throws Exception {
        setupSuccessfulCreate();

        assertDoesNotThrow(() -> daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype));

        verify(callableStatement).setInt(PARAM_INDEX_COUNTRY, Integer.parseInt(COUNTRY_ID_VALID));
        verify(callableStatement).setInt(PARAM_INDEX_DEPARTMENT, Integer.parseInt(MVD_ID_VALID));
        verify(callableStatement).setInt(PARAM_INDEX_CITY, Integer.parseInt(CITY_ID_VALID));
        verify(callableStatement).setInt(PARAM_INDEX_LEVEL1_ID, Integer.parseInt(LEVEL1_ID_VALID));
        verify(callableStatement).setInt(PARAM_INDEX_LEVEL2_ID, Integer.parseInt(LEVEL2_ID_VALID));
        verify(callableStatement).setInt(PARAM_INDEX_LEVEL3_ID, Integer.parseInt(LEVEL3_ID_VALID));
    }

    @Test
    @DisplayName("Debe lanzar NumberFormatException cuando los IDs no son números válidos")
    void createAddress_givenInvalidIds_throwsNumberFormatException() throws Exception {
        addressDatatype.getCountry().setId("invalid_id");
        setupSuccessfulCreate();

        assertThrows(NumberFormatException.class,
                () -> daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype));

        verify(managerDataAccessAs400).closeResources(callableStatement, null);
    }

    @Test
    @DisplayName("Debe usar el tipo de dirección LEGAL_ADDRESS correctamente")
    void createAddress_givenValidData_usesLegalAddressType() throws Exception {
        setupSuccessfulCreate();

        assertDoesNotThrow(() -> daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype));

        verify(callableStatement).setInt(PARAM_INDEX_ADDRESS_TYPE, LEGAL_ADDRESS);
        verify(managerDataAccessAs400).closeResources(callableStatement, null);
    }

    @Test
    @DisplayName("Debe verificar el orden correcto de parámetros en CallableStatement")
    void createAddress_givenValidParameters_setsParametersInCorrectOrder() throws Exception {
        setupSuccessfulCreate();

        daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype);

        var inOrder = inOrder(callableStatement);
        inOrder.verify(callableStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, UY_COUNTRY_CODE);
        inOrder.verify(callableStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, RUT_DOCUMENT_TYPE);
        inOrder.verify(callableStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, BUSINESS_RUT_VALID);
        inOrder.verify(callableStatement).setInt(PARAM_INDEX_ADDRESS_TYPE, LEGAL_ADDRESS);
        inOrder.verify(callableStatement).setInt(PARAM_INDEX_COUNTRY, Integer.parseInt(COUNTRY_ID_VALID));
        inOrder.verify(callableStatement).setString(PARAM_INDEX_POSTAL_CODE, POSTAL_CODE_VALID);
        inOrder.verify(callableStatement).setInt(PARAM_INDEX_DEPARTMENT, Integer.parseInt(MVD_ID_VALID));
        inOrder.verify(callableStatement).setInt(PARAM_INDEX_CITY, Integer.parseInt(CITY_ID_VALID));
        inOrder.verify(callableStatement).setInt(PARAM_INDEX_LEVEL1_ID, Integer.parseInt(LEVEL1_ID_VALID));
        inOrder.verify(callableStatement).setInt(PARAM_INDEX_LEVEL2_ID, Integer.parseInt(LEVEL2_ID_VALID));
        inOrder.verify(callableStatement).setInt(PARAM_INDEX_LEVEL3_ID, Integer.parseInt(LEVEL3_ID_VALID));
        inOrder.verify(callableStatement).setString(PARAM_INDEX_LEVEL1_NAME, LEVEL1_NAME_VALID);
        inOrder.verify(callableStatement).setString(PARAM_INDEX_LEVEL2_NAME, LEVEL2_NAME_VALID);
        inOrder.verify(callableStatement).setString(PARAM_INDEX_LEVEL3_NAME, LEVEL3_NAME_VALID);
        inOrder.verify(callableStatement).execute();
    }

}