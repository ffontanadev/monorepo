package uy.com.bbva.services.nonbusinesses.dao.impl;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.commons.dao.ManagerDataAccessAs400;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.dao.SQLStatements;
import uy.com.bbva.services.nonbusinesses.model.*;
import uy.com.bbva.services.nonbusinesses.model.external.BusinessInformation;
import uy.com.bbva.logcommons.log.utils.LogUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static uy.com.bbva.dtos.commons.utils.Constants.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite consolidada de pruebas de DAOImpl")
class DAOImplTest {

    @Mock
    private ManagerDataAccessAs400 managerDataAccessAs400;

    @Mock
    private LogUtils logUtils;

    @Mock
    private CallableStatement callableStatement;

    @Mock
    private PreparedStatement preparedStatement;

    @InjectMocks
    private DAOImpl daoImpl;

    @Nested
    @DisplayName("CreateAddress Tests")
    class CreateAddressTests {

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

        @Test
        @DisplayName("Debe crear una dirección correctamente cuando todos los valores son válidos")
        void createAddress_givenValidParameters_createsSuccessfully() throws Exception {
            when(managerDataAccessAs400.prepareCall(SQLStatements.INSERT_ADDRESS))
                    .thenReturn(callableStatement);

            assertDoesNotThrow(() -> daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype));

            verify(managerDataAccessAs400).prepareCall(SQLStatements.INSERT_ADDRESS);
            verify(callableStatement).execute();
            verify(managerDataAccessAs400).closeResources(callableStatement, null);
        }

        @ParameterizedTest(name = "[{index}] Debe manejar código postal: ''{0}''")
        @MethodSource("providePostalCodeVariations")
        @DisplayName("Debe crear dirección correctamente con diferentes variaciones de código postal")
        void createAddress_givenPostalCodeVariations_createsSuccessfully(
                String postalCode, String description) throws Exception {

            addressDatatype.setPostalCode(postalCode);
            when(managerDataAccessAs400.prepareCall(SQLStatements.INSERT_ADDRESS))
                    .thenReturn(callableStatement);

            assertDoesNotThrow(() -> daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype));

            verify(managerDataAccessAs400).prepareCall(SQLStatements.INSERT_ADDRESS);
            verify(callableStatement).execute();
            verify(managerDataAccessAs400).closeResources(callableStatement, null);
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
                String levelName, String description) throws Exception {

            addressDatatype.getLevel1().setName(levelName);
            addressDatatype.getLevel2().setName(levelName);
            addressDatatype.getLevel3().setName(levelName);

            when(managerDataAccessAs400.prepareCall(SQLStatements.INSERT_ADDRESS))
                    .thenReturn(callableStatement);

            assertDoesNotThrow(() -> daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype));

            verify(managerDataAccessAs400).prepareCall(SQLStatements.INSERT_ADDRESS);
            verify(callableStatement).execute();
            verify(managerDataAccessAs400).closeResources(callableStatement, null);
        }

        private static Stream<Arguments> provideLevelNameVariations() {
            return Stream.of(
                    Arguments.of(LEVEL1_NAME_VALID, "nombre válido"),
                    Arguments.of("", "nombre vacío"),
                    Arguments.of(null, "nombre nulo"),
                    Arguments.of("  Calle con espacios  ", "nombre con espacios")
            );
        }

        @Test
        @DisplayName("Debe cerrar recursos incluso cuando ocurre una excepción")
        void createAddress_givenException_closesResourcesCorrectly() throws Exception {
            SQLException sqlException = new SQLException("Parameter setting failed");
            when(managerDataAccessAs400.prepareCall(SQLStatements.INSERT_ADDRESS))
                    .thenReturn(callableStatement);
            doThrow(sqlException).when(callableStatement).setInt(anyInt(), anyInt());

            assertThrows(ServiceException.class,
                    () -> daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype));

            verify(managerDataAccessAs400).closeResources(callableStatement, null);
        }

        @Test
        @DisplayName("Debe parsear correctamente los IDs de String a Integer")
        void createAddress_givenValidIds_parsesCorrectly() throws Exception {
            when(managerDataAccessAs400.prepareCall(SQLStatements.INSERT_ADDRESS))
                    .thenReturn(callableStatement);

            assertDoesNotThrow(() -> daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype));

            verify(callableStatement).setInt(PARAM_INDEX_COUNTRY, Integer.parseInt(COUNTRY_ID_VALID));
            verify(callableStatement).setInt(PARAM_INDEX_DEPARTMENT, Integer.parseInt(MVD_ID_VALID));
            verify(callableStatement).setInt(PARAM_INDEX_CITY, Integer.parseInt(CITY_ID_VALID));
        }

        @Test
        @DisplayName("Debe lanzar NumberFormatException cuando los IDs no son números válidos")
        void createAddress_givenInvalidIds_throwsNumberFormatException() throws Exception {
            addressDatatype.getCountry().setId("invalid_id");
            when(managerDataAccessAs400.prepareCall(SQLStatements.INSERT_ADDRESS))
                    .thenReturn(callableStatement);

            assertThrows(NumberFormatException.class,
                    () -> daoImpl.createAddress(nonBusinessIdDatatype, addressDatatype));

            verify(managerDataAccessAs400).closeResources(callableStatement, null);
        }
    }

    @Nested
    @DisplayName("UpdateBusinessUser Tests")
    class UpdateBusinessUserTests {

        private static final String ERROR_MESSAGE = "Error al actualizar el usuario de la empresa";
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

        @Test
        @DisplayName("Debe actualizar el usuario de la empresa correctamente con parámetros válidos")
        void updateBusinessUser_givenValidParameters_updatesSuccessfully() throws Exception {
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_USER))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(SINGLE_ROW_UPDATED);

            assertDoesNotThrow(() -> daoImpl.updateBusinessUser(nonBusinessDatatype, user));

            verify(managerDataAccessAs400).prepareStatement(SQLStatements.UPDATE_BUSINESS_USER);
            verify(preparedStatement).executeUpdate();
            verify(managerDataAccessAs400).closeResources(preparedStatement, null);
        }

        @Test
        @DisplayName("Debe actualizar correctamente incluso con User nulo (parámetro no usado)")
        void updateBusinessUser_givenNullUser_updatesSuccessfully() throws Exception {
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_USER))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(SINGLE_ROW_UPDATED);

            assertDoesNotThrow(() -> daoImpl.updateBusinessUser(nonBusinessDatatype, null));

            verify(preparedStatement).executeUpdate();
        }

        @Test
        @DisplayName("Debe cerrar recursos incluso cuando ocurre una excepción")
        void updateBusinessUser_givenException_closesResourcesCorrectly() throws Exception {
            SQLException sqlException = new SQLException("Parameter setting failed");
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_USER))
                    .thenReturn(preparedStatement);
            doThrow(sqlException).when(preparedStatement).setString(anyInt(), anyString());

            assertThrows(ServiceException.class,
                    () -> daoImpl.updateBusinessUser(nonBusinessDatatype, user));

            verify(managerDataAccessAs400).closeResources(preparedStatement, null);
        }

        @Test
        @DisplayName("Debe manejar correctamente cuando no se actualiza ninguna fila")
        void updateBusinessUser_givenNoRowsAffected_executesWithoutError() throws Exception {
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_USER))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(ZERO_ROWS_UPDATED);

            assertDoesNotThrow(() -> daoImpl.updateBusinessUser(nonBusinessDatatype, user));

            verify(preparedStatement).executeUpdate();
        }
    }

    @Nested
    @DisplayName("UpdateBusinessMail Tests")
    class UpdateBusinessMailTests {

        private static final String ERROR_MESSAGE = "Error al actualizar el correo de la empresa";

        private NonBusinessIdDatatype nonBusinessDatatype;

        @BeforeEach
        void setUp() {
            nonBusinessDatatype = createBusinessDatatype();
        }

        @ParameterizedTest(name = "[{index}] Debe actualizar correctamente con email: ''{0}''")
        @MethodSource("provideEmailVariations")
        @DisplayName("Debe actualizar el correo correctamente con diferentes variaciones de email")
        void updateBusinessMail_givenValidEmailVariations_updatesSuccessfully(
                String emailToTest, String displayDescription) throws Exception {

            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_MAIL))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(SINGLE_ROW_UPDATED);

            assertDoesNotThrow(() -> daoImpl.updateBusinessMail(nonBusinessDatatype, emailToTest));

            verify(managerDataAccessAs400).prepareStatement(SQLStatements.UPDATE_BUSINESS_MAIL);
            verify(preparedStatement).executeUpdate();
            verify(managerDataAccessAs400).closeResources(preparedStatement, null);
        }

        private static Stream<Arguments> provideEmailVariations() {
            return Stream.of(
                    Arguments.of(EMAIL_VALID, "email válido estándar"),
                    Arguments.of(EMAIL_EMPTY, "email vacío"),
                    Arguments.of(null, "email nulo"),
                    Arguments.of(EMAIL_WITH_SPACES, "email con espacios"),
                    Arguments.of(EMAIL_WITH_SPECIAL_CHARS, "email con caracteres especiales"),
                    Arguments.of(EMAIL_LONG, "email muy largo")
            );
        }

        @Test
        @DisplayName("Debe cerrar recursos incluso cuando ocurre una excepción")
        void updateBusinessMail_givenException_closesResourcesCorrectly() throws Exception {
            SQLException sqlException = new SQLException("Parameter setting failed");
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_MAIL))
                    .thenReturn(preparedStatement);
            doThrow(sqlException).when(preparedStatement).setString(anyInt(), anyString());

            assertThrows(ServiceException.class,
                    () -> daoImpl.updateBusinessMail(nonBusinessDatatype, EMAIL_VALID));

            verify(managerDataAccessAs400).closeResources(preparedStatement, null);
        }

        @Test
        @DisplayName("Debe manejar correctamente cuando no se actualiza ninguna fila")
        void updateBusinessMail_givenNoRowsAffected_executesWithoutError() throws Exception {
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_MAIL))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(ZERO_ROWS_UPDATED);

            assertDoesNotThrow(() -> daoImpl.updateBusinessMail(nonBusinessDatatype, EMAIL_VALID));

            verify(preparedStatement).executeUpdate();
        }
    }

    @Nested
    @DisplayName("UpdateBusinessMobile Tests")
    class UpdateBusinessMobileTests {

        private NonBusinessIdDatatype nonBusinessDatatype;

        @BeforeEach
        void setUp() {
            nonBusinessDatatype = createBusinessDatatype();
        }

        @ParameterizedTest(name = "[{index}] Debe actualizar correctamente con celular: ''{0}''")
        @MethodSource("provideMobileVariations")
        @DisplayName("Debe actualizar el celular correctamente con diferentes variaciones de número")
        void updateBusinessMobile_givenValidMobileVariations_updatesSuccessfully(
                String mobileToTest, String displayDescription) throws Exception {

            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_MOBILE))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(SINGLE_ROW_UPDATED);

            assertDoesNotThrow(() -> daoImpl.updateBusinessMobile(nonBusinessDatatype, mobileToTest));

            verify(preparedStatement).executeUpdate();
        }

        private static Stream<Arguments> provideMobileVariations() {
            return Stream.of(
                    Arguments.of(MOBILE_VALID, "celular válido estándar"),
                    Arguments.of(MOBILE_EMPTY, "celular vacío"),
                    Arguments.of(null, "celular nulo"),
                    Arguments.of(MOBILE_WITH_SPACES, "celular con espacios")
            );
        }

        @Test
        @DisplayName("Debe cerrar recursos incluso cuando ocurre una excepción")
        void updateBusinessMobile_givenException_closesResourcesCorrectly() throws Exception {
            SQLException sqlException = new SQLException("Parameter setting failed");
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_MOBILE))
                    .thenReturn(preparedStatement);
            doThrow(sqlException).when(preparedStatement).setString(anyInt(), anyString());

            assertThrows(ServiceException.class,
                    () -> daoImpl.updateBusinessMobile(nonBusinessDatatype, MOBILE_VALID));

            verify(managerDataAccessAs400).closeResources(preparedStatement, null);
        }

        @Test
        @DisplayName("Debe manejar correctamente cuando no se actualiza ninguna fila")
        void updateBusinessMobile_givenNoRowsAffected_executesWithoutError() throws Exception {
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_MOBILE))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(ZERO_ROWS_UPDATED);

            assertDoesNotThrow(() -> daoImpl.updateBusinessMobile(nonBusinessDatatype, MOBILE_VALID));

            verify(preparedStatement).executeUpdate();
        }
    }

    @Nested
    @DisplayName("UpdateBusinessCommercialName Tests")
    class UpdateBusinessCommercialNameTests {

        private NonBusinessIdDatatype nonBusinessDatatype;

        @BeforeEach
        void setUp() {
            nonBusinessDatatype = createBusinessDatatype();
        }

        @ParameterizedTest(name = "[{index}] Debe actualizar correctamente con nombre comercial: ''{0}''")
        @MethodSource("provideCommercialNameVariations")
        @DisplayName("Debe actualizar el nombre comercial correctamente con diferentes variaciones")
        void updateBusinessCommercialName_givenValidCommercialNameVariations_updatesSuccessfully(
                String commercialNameToTest, String displayDescription) throws Exception {

            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_COMMERCIAL_NAME))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(SINGLE_ROW_UPDATED);

            assertDoesNotThrow(() ->
                    daoImpl.updateBusinessCommercialName(nonBusinessDatatype, commercialNameToTest));

            verify(preparedStatement).executeUpdate();
        }

        private static Stream<Arguments> provideCommercialNameVariations() {
            return Stream.of(
                    Arguments.of(BUSINESS_NAME_VALID, "nombre comercial válido estándar"),
                    Arguments.of(BUSINESS_NAME_EMPTY, "nombre comercial vacío"),
                    Arguments.of(null, "nombre comercial nulo"),
                    Arguments.of(BUSINESS_NAME_WITH_SPACES, "nombre comercial con espacios"),
                    Arguments.of(BUSINESS_NAME_LONG, "nombre comercial muy largo"),
                    Arguments.of(COMMERCIAL_NAME_WITH_ACCENTS, "nombre comercial con acentos"),
                    Arguments.of(COMMERCIAL_NAME_WITH_NUMBERS, "nombre comercial con números")
            );
        }

        @Test
        @DisplayName("Debe cerrar recursos incluso cuando ocurre una excepción")
        void updateBusinessCommercialName_givenException_closesResourcesCorrectly() throws Exception {
            SQLException sqlException = new SQLException("Parameter setting failed");
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_COMMERCIAL_NAME))
                    .thenReturn(preparedStatement);
            doThrow(sqlException).when(preparedStatement).setString(anyInt(), anyString());

            assertThrows(ServiceException.class,
                    () -> daoImpl.updateBusinessCommercialName(nonBusinessDatatype, BUSINESS_NAME_VALID));

            verify(managerDataAccessAs400).closeResources(preparedStatement, null);
        }

        @Test
        @DisplayName("Debe manejar correctamente cuando no se actualiza ninguna fila")
        void updateBusinessCommercialName_givenNoRowsAffected_executesWithoutError() throws Exception {
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_COMMERCIAL_NAME))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(ZERO_ROWS_UPDATED);

            assertDoesNotThrow(() ->
                    daoImpl.updateBusinessCommercialName(nonBusinessDatatype, BUSINESS_NAME_VALID));

            verify(preparedStatement).executeUpdate();
        }
    }

    @Nested
    @DisplayName("UpdateBusinessEconomicData Tests")
    class UpdateBusinessEconomicDataTests {

        private static final String BALANCE_INDICATOR_REAL = "REAL";
        private static final String BALANCE_INDICATOR_PROJECTED = "PROYECTADO";

        private NonBusinessIdDatatype nonBusinessIdDatatype;
        private EconomicData economicData;

        @BeforeEach
        void setUp() {
            nonBusinessIdDatatype = createBusinessDatatype();
            economicData = createEconomicDataWithRealIncome();
        }

        @ParameterizedTest(name = "[{index}] Debe actualizar con balance tipo: {0}")
        @MethodSource("provideBalanceTypeScenarios")
        @DisplayName("Debe actualizar correctamente con diferentes tipos de balance")
        void updateBusinessEconomicData_givenDifferentBalanceTypes_updatesSuccessfully(
                String balanceType, String expectedIndicator, String displayDescription) throws Exception {

            economicData.getFinancialInformation().getBalances().get(0).setBalanceType(balanceType);
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_ECONOMIC_DATA))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(SINGLE_ROW_UPDATED);

            assertDoesNotThrow(() -> daoImpl.updateBusinessEconomicData(nonBusinessIdDatatype, economicData));

            verify(preparedStatement).executeUpdate();
        }

        private static Stream<Arguments> provideBalanceTypeScenarios() {
            return Stream.of(
                    Arguments.of(BALANCE_TYPE_REAL, BALANCE_INDICATOR_REAL, "ingreso anual real"),
                    Arguments.of(BALANCE_TYPE_PROJECTED, BALANCE_INDICATOR_PROJECTED, "ingreso anual proyectado")
            );
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
    }

    @Nested
    @DisplayName("UpdateBusinessInformation Tests")
    class UpdateBusinessInformationTests {

        private BusinessInformation businessInformation;

        @BeforeEach
        void setUp() {
            businessInformation = createBusinessInformation();
        }

        @ParameterizedTest(name = "[{index}] Debe actualizar correctamente con nombre: ''{0}''")
        @MethodSource("provideBusinessNameVariations")
        @DisplayName("Debe actualizar la información correctamente con diferentes variaciones de nombre")
        void updateBusinessInformation_givenValidNameVariations_updatesSuccessfully(
                String nameToTest, String displayDescription) throws Exception {

            businessInformation.setName(nameToTest);
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_INFORMATION))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(SINGLE_ROW_UPDATED);

            assertDoesNotThrow(() -> daoImpl.updateBusinessInformation(businessInformation));

            verify(preparedStatement).executeUpdate();
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

        @Test
        @DisplayName("Debe manejar correctamente cuando no se actualiza ninguna fila")
        void updateBusinessInformation_givenNoRowsAffected_executesWithoutError() throws Exception {
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_INFORMATION))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(ZERO_ROWS_UPDATED);

            assertDoesNotThrow(() -> daoImpl.updateBusinessInformation(businessInformation));

            verify(preparedStatement).executeUpdate();
        }
    }

    @Nested
    @DisplayName("UpdateBusinessBankBranch Tests")
    class UpdateBusinessBankBranchTests {

        private NonBusinessIdDatatype nonBusinessDatatype;

        @BeforeEach
        void setUp() {
            nonBusinessDatatype = createBusinessDatatype();
        }

        @ParameterizedTest(name = "[{index}] Debe actualizar correctamente con sucursal: ''{0}''")
        @MethodSource("provideBankBranchVariations")
        @DisplayName("Debe actualizar la sucursal bancaria correctamente con diferentes variaciones")
        void updateBusinessBankBranch_givenValidBankBranchVariations_updatesSuccessfully(
                String bankBranchToTest, String displayDescription) throws Exception {

            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BANK_BRANCH))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(SINGLE_ROW_UPDATED);

            assertDoesNotThrow(() -> daoImpl.updateBusinessBankBranch(nonBusinessDatatype, bankBranchToTest));

            verify(preparedStatement).executeUpdate();
        }

        private static Stream<Arguments> provideBankBranchVariations() {
            return Stream.of(
                    Arguments.of(BANK_BRANCH_VALID, "sucursal válida estándar"),
                    Arguments.of(BANK_BRANCH_ALTERNATIVE, "sucursal alternativa"),
                    Arguments.of(BANK_BRANCH_EMPTY, "sucursal vacía"),
                    Arguments.of(null, "sucursal nula"),
                    Arguments.of(BANK_BRANCH_WITH_SPACES, "sucursal con espacios")
            );
        }

        @Test
        @DisplayName("Debe manejar correctamente cuando no se actualiza ninguna fila")
        void updateBusinessBankBranch_givenNoRowsAffected_executesWithoutError() throws Exception {
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BANK_BRANCH))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(ZERO_ROWS_UPDATED);

            assertDoesNotThrow(() -> daoImpl.updateBusinessBankBranch(nonBusinessDatatype, BANK_BRANCH_VALID));

            verify(preparedStatement).executeUpdate();
        }
    }

    @Nested
    @DisplayName("UpdateBusinessFormationData Tests")
    class UpdateBusinessFormationDataTests {

        private NonBusinessIdDatatype nonBusinessDatatype;
        private Formation formation;
        private LegalDocument legalDocument;

        @BeforeEach
        void setUp() {
            nonBusinessDatatype = createBusinessDatatype();
            formation = createFormation();
            legalDocument = createLegalDocumentBPS();
        }

        @Test
        @DisplayName("Debe actualizar los datos de formación correctamente con valores válidos")
        void updateBusinessFormationData_givenValidFormationAndLegalDocument_updatesSuccessfully() throws Exception {
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_FORMATION_DATA))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(SINGLE_ROW_UPDATED);

            assertDoesNotThrow(() ->
                    daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument));

            verify(preparedStatement).executeUpdate();
        }

        @ParameterizedTest(name = "[{index}] Debe actualizar con número de documento: ''{0}''")
        @MethodSource("provideDocumentNumberVariations")
        @DisplayName("Debe actualizar correctamente con diferentes variaciones de número de documento")
        void updateBusinessFormationData_givenDocumentNumberVariations_updatesSuccessfully(
                String documentNumber, String displayDescription) throws Exception {

            legalDocument.setDocumentNumber(documentNumber);
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_FORMATION_DATA))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(SINGLE_ROW_UPDATED);

            assertDoesNotThrow(() ->
                    daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument));

            verify(preparedStatement).executeUpdate();
        }

        private static Stream<Arguments> provideDocumentNumberVariations() {
            return Stream.of(
                    Arguments.of(LEGAL_DOC_NUMBER_VALID, "número válido estándar"),
                    Arguments.of(LEGAL_DOC_NUMBER_ALTERNATIVE, "número alternativo"),
                    Arguments.of(LEGAL_DOC_NUMBER_EMPTY, "número vacío"),
                    Arguments.of(null, "número nulo"),
                    Arguments.of(LEGAL_DOC_NUMBER_WITH_SPACES, "número con espacios")
            );
        }

        @Test
        @DisplayName("Debe manejar correctamente cuando no se actualiza ninguna fila")
        void updateBusinessFormationData_givenNoRowsAffected_executesWithoutError() throws Exception {
            when(managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_FORMATION_DATA))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(ZERO_ROWS_UPDATED);

            assertDoesNotThrow(() ->
                    daoImpl.updateBusinessFormationData(nonBusinessDatatype, formation, legalDocument));

            verify(preparedStatement).executeUpdate();
        }
    }
}
