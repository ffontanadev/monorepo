package uy.com.bbva.services.nonbusinesses.dao.impl.get;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uy.com.bbva.dtos.commons.model.ContactDetail;
import uy.com.bbva.dtos.commons.model.EmailContact;
import uy.com.bbva.dtos.commons.model.MobileContact;
import uy.com.bbva.encrypter.MaskUtil;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.commons.dao.ManagerDataAccessAs400;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.dao.SQLStatements;
import uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl;
import uy.com.bbva.services.nonbusinesses.model.LegalRepresentative;
import uy.com.bbva.services.nonbusinesses.model.NonBusiness;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static uy.com.bbva.dtos.commons.utils.Constants.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite de pruebas de DAOImpl.getNonBusiness():")
class GetNonBusinessTest {

    // Test data constants
    private static final String BUSINESS_DOC_NUMBER = "123456789012";
    private static final String BUSINESS_DOC_NUMBER_WITH_SPACES = "  123456789012  ";
    private static final String PERSON_DOC_NUMBER = "12345678";
    private static final String COMPANY_NAME = "Test Company S.A.";
    private static final String COMPANY_NAME_WITH_SPACES = "  Test Company S.A.  ";
    private static final String FUTURE_DATE = "20251231";
    private static final String PAST_DATE = "20200101";
    private static final String BIRTH_DATE = "19900115";
    private static final String FIRST_NAME = "John";
    private static final String FIRST_NAME_WITH_SPACES = "  John  ";
    private static final String MIDDLE_NAME = "Michael";
    private static final String MIDDLE_NAME_WITH_SPACES = "  Michael  ";
    private static final String LAST_NAME = "Doe";
    private static final String LAST_NAME_WITH_SPACES = "  Doe  ";
    private static final String SECOND_LAST_NAME = "Smith";
    private static final String SECOND_LAST_NAME_WITH_SPACES = "  Smith  ";
    private static final String EMAIL_ADDRESS = "test@company.com";
    private static final String EMAIL_ADDRESS_WITH_SPACES = "  test@company.com  ";
    private static final String PHONE_NUMBER = "099123456";
    private static final String PHONE_NUMBER_WITH_SPACES = "  099123456  ";

    // Database configuration constants
    private static final String VERIDAS_DB_NAME = "veridas_db";
    private static final String ENCRYPT_TYPE = "AES";

    // Error messages
    private static final String ERROR_MESSAGE = "Error al chequear si el no cliente existe";
    private static final String DB_CONNECTION_ERROR = "Database connection error";
    private static final String CANNOT_PREPARE_STATEMENT = "Cannot prepare statement";
    private static final String COLUMN_NOT_FOUND = "Column not found";

    // Legal document constants
    private static final String LEGAL_DOC_TYPE_ID = "BUSINESS_LICENSE_CERTIFICATE_OF_GOOD_STANDING";
    private static final String OWNER_ROLE = "OWNER";

    @InjectMocks
    private DAOImpl dao;

    @Mock
    private ManagerDataAccessAs400 managerDataAccessAs400;

    @Mock
    private MaskUtil maskUtil;

    @Mock
    private LogUtils logUtils;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;
    
    private NonBusinessIdDatatype nonBusinessIdDatatype;

    @BeforeEach
    void setUp() {
        nonBusinessIdDatatype = createNonBusinessIdDatatype();
        configureDaoProperties();
    }

    @Test
    @DisplayName("Debe retornar NonBusiness con toda la información cuando includeOwnerData y includeContactDetails son true")
    void getNonBusiness_shouldReturnCompleteNonBusiness_whenAllFlagsAreTrue() throws Exception {
        setupMocksForQuery();
        mockResultSetWithSpacedData();

        NonBusiness result = dao.getNonBusiness(nonBusinessIdDatatype, true, true);

        assertNotNull(result);
        assertBusinessDocuments(result);
        assertLegalName(result, COMPANY_NAME);
        assertLegalDocuments(result, true);
        assertLegalRepresentatives(result);
        assertContactDetails(result);

        verifyPreparedStatementParameters();
        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Debe retornar NonBusiness sin datos de owner cuando includeOwnerData es false")
    void getNonBusiness_shouldReturnNonBusinessWithoutOwnerData_whenIncludeOwnerDataIsFalse() throws Exception {
        setupMocksForQuery();
        mockBasicResultSet();

        NonBusiness result = dao.getNonBusiness(nonBusinessIdDatatype, false, true);

        assertNotNull(result);
        assertNull(result.getLegalRepresentatives());
        assertNotNull(result.getContactDetails());
        assertEquals(2, result.getContactDetails().size());

        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Debe retornar NonBusiness sin datos de contacto cuando includeContactDetails es false")
    void getNonBusiness_shouldReturnNonBusinessWithoutContactDetails_whenIncludeContactDetailsIsFalse() throws Exception {
        setupMocksForQuery();
        mockResultSetWithOwnerData();

        NonBusiness result = dao.getNonBusiness(nonBusinessIdDatatype, true, false);

        assertNotNull(result);
        assertNotNull(result.getLegalRepresentatives());
        assertEquals(1, result.getLegalRepresentatives().size());
        assertNull(result.getContactDetails());

        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Debe retornar null cuando no hay resultados")
    void getNonBusiness_shouldReturnNull_whenNoResultsFound() throws Exception {
        setupMocksForQuery();
        when(resultSet.next()).thenReturn(false);

        NonBusiness result = dao.getNonBusiness(nonBusinessIdDatatype, true, true);

        assertNull(result);
        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Debe handlear documentos expirados")
    void getNonBusiness_shouldHandleExpiredLegalDocuments_whenExpirationDateIsInPast() throws Exception {
        setupMocksForQuery();
        mockResultSetWithExpiredDocument();

        NonBusiness result = dao.getNonBusiness(nonBusinessIdDatatype, false, true);

        assertNotNull(result);
        assertNotNull(result.getLegalDocuments());
        assertEquals(1, result.getLegalDocuments().size());
        assertFalse(result.getLegalDocuments().get(0).getActive());

        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Debe lanzar ServiceException cuando exista un SQLException en la consulta")
    void getNonBusiness_shouldThrowServiceException_whenSQLExceptionOccurs() throws Exception {
        setupMocksForQuery();
        SQLException sqlException = new SQLException(DB_CONNECTION_ERROR);
        when(preparedStatement.executeQuery()).thenThrow(sqlException);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                dao.getNonBusiness(nonBusinessIdDatatype, true, true)
        );

        assertServiceException(exception);
        verifyErrorLogging(DB_CONNECTION_ERROR);
        verify(managerDataAccessAs400).closeResources(eq(preparedStatement), isNull());
    }

    @Test
    @DisplayName("Debe lanzar ServiceException cuando exista un SQLException en la preparacion del statement")
    void getNonBusiness_shouldThrowServiceException_whenPrepareStatementFails() throws Exception {
        SQLException sqlException = new SQLException(CANNOT_PREPARE_STATEMENT);
        when(managerDataAccessAs400.prepareStatement(SQLStatements.GET_NON_BUSINESS))
                .thenThrow(sqlException);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                dao.getNonBusiness(nonBusinessIdDatatype, true, true)
        );

        assertServiceException(exception);
        verifyErrorLogging(CANNOT_PREPARE_STATEMENT);
    }

    @Test
    @DisplayName("Debe handlear valores null en el ResultSet")
    void getNonBusiness_shouldHandleNullValues_whenResultSetContainsNulls() throws Exception {
        setupMocksForQuery();
        mockResultSetWithNullValues();

        NonBusiness result = dao.getNonBusiness(nonBusinessIdDatatype, true, true);

        assertNotNull(result);
        assertNullHandlingForOwner(result);
        assertNullHandlingForContacts(result);

        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Debe handlear espacios en blanco en el ResultSet")
    void getNonBusiness_shouldTrimWhitespace_whenResultSetContainsWhitespace() throws Exception {
        setupMocksForQuery();
        mockResultSetWithWhitespace();

        NonBusiness result = dao.getNonBusiness(nonBusinessIdDatatype, true, true);

        assertNotNull(result);
        assertTrimmedValues(result);

        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Debe siempre cerrar recursos, incluyendo escenarios de excepción")
    void getNonBusiness_shouldAlwaysCloseResources_whenExceptionOccurs() throws Exception {
        setupMocksForQuery();
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCPJNDOC")).thenThrow(new SQLException(COLUMN_NOT_FOUND));

        assertThrows(ServiceException.class, () ->
                dao.getNonBusiness(nonBusinessIdDatatype, true, true)
        );

        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Debe retornar NonBusiness con la información minima cuando includeOwnerData y includeContactDetails son false")
    void getNonBusiness_shouldReturnMinimalNonBusiness_whenBothFlagsAreFalse() throws Exception {
        setupMocksForQuery();
        mockMinimalResultSet();

        NonBusiness result = dao.getNonBusiness(nonBusinessIdDatatype, false, false);

        assertNotNull(result);
        assertNotNull(result.getBusinessDocuments());
        assertEquals(1, result.getBusinessDocuments().size());
        assertEquals(COMPANY_NAME, result.getLegalName());
        assertNotNull(result.getLegalDocuments());
        assertEquals(1, result.getLegalDocuments().size());
        assertNull(result.getLegalRepresentatives());
        assertNull(result.getContactDetails());

        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }


    // Helpers

    private NonBusinessIdDatatype createNonBusinessIdDatatype() {
        NonBusinessIdDatatype datatype = new NonBusinessIdDatatype();
        datatype.setBusinessDocument(BUSINESS_DOC_NUMBER);
        datatype.setBusinessCountry(UY_COUNTRY_CODE);
        datatype.setBusinessDocumentType(RUT_DOCUMENT_TYPE);
        datatype.setPersonDocument(PERSON_DOC_NUMBER);
        datatype.setPersonCountry(UY_COUNTRY_CODE);
        datatype.setPersonDocumentType(USER_DOCUMENT_TYPE_CI);
        return datatype;
    }

    private void configureDaoProperties() {
        ReflectionTestUtils.setField(dao, "veridasDBName", VERIDAS_DB_NAME);
        ReflectionTestUtils.setField(dao, "encryptType", ENCRYPT_TYPE);
    }

    private void setupMocksForQuery() throws Exception {
        when(managerDataAccessAs400.prepareStatement(SQLStatements.GET_NON_BUSINESS))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    private void mockResultSetWithSpacedData() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCPJNDOC")).thenReturn(BUSINESS_DOC_NUMBER_WITH_SPACES);
        when(resultSet.getString("BBNCPJRASO")).thenReturn(COMPANY_NAME_WITH_SPACES);
        when(resultSet.getString("BBNCPJFRUT")).thenReturn(FUTURE_DATE);
        when(resultSet.getString("BBNCPFNOM1")).thenReturn(FIRST_NAME_WITH_SPACES);
        when(resultSet.getString("BBNCPFNOM2")).thenReturn(MIDDLE_NAME_WITH_SPACES);
        when(resultSet.getString("BBNCPFAPE1")).thenReturn(LAST_NAME_WITH_SPACES);
        when(resultSet.getString("BBNCPFAPE2")).thenReturn(SECOND_LAST_NAME_WITH_SPACES);
        when(resultSet.getString("BBNCPFFNAC")).thenReturn(BIRTH_DATE);
        when(resultSet.getString("BBNCPJMAIL")).thenReturn(EMAIL_ADDRESS_WITH_SPACES);
        when(resultSet.getString("BBNCPJTEL1")).thenReturn(PHONE_NUMBER_WITH_SPACES);
    }

    private void mockBasicResultSet() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCPJNDOC")).thenReturn(BUSINESS_DOC_NUMBER);
        when(resultSet.getString("BBNCPJRASO")).thenReturn(COMPANY_NAME);
        when(resultSet.getString("BBNCPJFRUT")).thenReturn(FUTURE_DATE);
        when(resultSet.getString("BBNCPJMAIL")).thenReturn(EMAIL_ADDRESS);
        when(resultSet.getString("BBNCPJTEL1")).thenReturn(PHONE_NUMBER);
    }

    private void mockResultSetWithOwnerData() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCPJNDOC")).thenReturn(BUSINESS_DOC_NUMBER);
        when(resultSet.getString("BBNCPJRASO")).thenReturn(COMPANY_NAME);
        when(resultSet.getString("BBNCPJFRUT")).thenReturn(FUTURE_DATE);
        when(resultSet.getString("BBNCPFNOM1")).thenReturn(FIRST_NAME);
        when(resultSet.getString("BBNCPFNOM2")).thenReturn(MIDDLE_NAME);
        when(resultSet.getString("BBNCPFAPE1")).thenReturn(LAST_NAME);
        when(resultSet.getString("BBNCPFAPE2")).thenReturn(SECOND_LAST_NAME);
        when(resultSet.getString("BBNCPFFNAC")).thenReturn(BIRTH_DATE);
    }

    private void mockResultSetWithExpiredDocument() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCPJNDOC")).thenReturn(BUSINESS_DOC_NUMBER);
        when(resultSet.getString("BBNCPJRASO")).thenReturn(COMPANY_NAME);
        when(resultSet.getString("BBNCPJFRUT")).thenReturn(PAST_DATE);
        when(resultSet.getString("BBNCPJMAIL")).thenReturn(EMAIL_ADDRESS);
        when(resultSet.getString("BBNCPJTEL1")).thenReturn(PHONE_NUMBER);
    }

    private void mockResultSetWithNullValues() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCPJNDOC")).thenReturn(BUSINESS_DOC_NUMBER);
        when(resultSet.getString("BBNCPJRASO")).thenReturn(COMPANY_NAME);
        when(resultSet.getString("BBNCPJFRUT")).thenReturn(FUTURE_DATE);
        when(resultSet.getString("BBNCPFNOM1")).thenReturn(null);
        when(resultSet.getString("BBNCPFNOM2")).thenReturn(null);
        when(resultSet.getString("BBNCPFAPE1")).thenReturn(LAST_NAME);
        when(resultSet.getString("BBNCPFAPE2")).thenReturn(null);
        when(resultSet.getString("BBNCPFFNAC")).thenReturn(BIRTH_DATE);
        when(resultSet.getString("BBNCPJMAIL")).thenReturn(null);
        when(resultSet.getString("BBNCPJTEL1")).thenReturn(null);
    }

    private void mockResultSetWithWhitespace() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCPJNDOC")).thenReturn(BUSINESS_DOC_NUMBER_WITH_SPACES);
        when(resultSet.getString("BBNCPJRASO")).thenReturn(COMPANY_NAME_WITH_SPACES);
        when(resultSet.getString("BBNCPJFRUT")).thenReturn(FUTURE_DATE);
        when(resultSet.getString("BBNCPFNOM1")).thenReturn(FIRST_NAME_WITH_SPACES);
        when(resultSet.getString("BBNCPFNOM2")).thenReturn("  ");
        when(resultSet.getString("BBNCPFAPE1")).thenReturn(LAST_NAME_WITH_SPACES);
        when(resultSet.getString("BBNCPFAPE2")).thenReturn(SECOND_LAST_NAME_WITH_SPACES);
        when(resultSet.getString("BBNCPFFNAC")).thenReturn(BIRTH_DATE);
        when(resultSet.getString("BBNCPJMAIL")).thenReturn(EMAIL_ADDRESS_WITH_SPACES);
        when(resultSet.getString("BBNCPJTEL1")).thenReturn(PHONE_NUMBER_WITH_SPACES);
    }

    private void mockMinimalResultSet() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCPJNDOC")).thenReturn(BUSINESS_DOC_NUMBER);
        when(resultSet.getString("BBNCPJRASO")).thenReturn(COMPANY_NAME);
        when(resultSet.getString("BBNCPJFRUT")).thenReturn(FUTURE_DATE);
    }

    private void assertBusinessDocuments(NonBusiness result) {
        assertNotNull(result.getBusinessDocuments());
        assertEquals(1, result.getBusinessDocuments().size());
        assertEquals(BUSINESS_DOC_NUMBER, result.getBusinessDocuments().get(0).getDocumentNumber());
        assertEquals("RUT", result.getBusinessDocuments().get(0).getBusinessDocumentType().getId());
    }

    private void assertLegalName(NonBusiness result, String expectedName) {
        assertEquals(expectedName, result.getLegalName());
    }

    private void assertLegalDocuments(NonBusiness result, boolean expectedActive) {
        assertNotNull(result.getLegalDocuments());
        assertEquals(1, result.getLegalDocuments().size());
        assertEquals(LEGAL_DOC_TYPE_ID, result.getLegalDocuments().get(0).getLegalDocumentType().getId());
        assertEquals(expectedActive, result.getLegalDocuments().get(0).getActive());
    }

    private void assertLegalRepresentatives(NonBusiness result) {
        assertNotNull(result.getLegalRepresentatives());
        assertEquals(1, result.getLegalRepresentatives().size());
        LegalRepresentative owner = result.getLegalRepresentatives().get(0);
        assertEquals(FIRST_NAME, owner.getFirstName());
        assertEquals(MIDDLE_NAME, owner.getMiddleName());
        assertEquals(LAST_NAME, owner.getLastName());
        assertEquals(SECOND_LAST_NAME, owner.getSecondLastName());
        assertEquals(OWNER_ROLE, owner.getRole());
    }

    private void assertContactDetails(NonBusiness result) {
        assertNotNull(result.getContactDetails());
        assertEquals(2, result.getContactDetails().size());
    }

    private void verifyPreparedStatementParameters() throws SQLException {
        verify(preparedStatement).setString(1, BUSINESS_DOC_NUMBER);
        verify(preparedStatement).setInt(2, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(3, RUT_DOCUMENT_TYPE);
        verify(preparedStatement).setString(4, PERSON_DOC_NUMBER);
        verify(preparedStatement).setInt(5, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(6, USER_DOCUMENT_TYPE_CI);
    }

    private void assertServiceException(ServiceException exception) {
        assertTrue(exception.getInternalMessage().contains(ERROR_MESSAGE));
    }

    private void verifyErrorLogging(String errorDetail) {
        verify(logUtils).logError(eq(DAOImpl.class.getName()),
                eq(ERROR_MESSAGE),
                eq(errorDetail));
    }

    private void assertNullHandlingForOwner(NonBusiness result) {
        LegalRepresentative owner = result.getLegalRepresentatives().get(0);
        assertNull(owner.getFirstName());
        assertNull(owner.getMiddleName());
        assertEquals(LAST_NAME, owner.getLastName());
        assertNull(owner.getSecondLastName());
    }

    private void assertNullHandlingForContacts(NonBusiness result) {
        ContactDetail emailContact = result.getContactDetails().get(0);
        EmailContact email = (EmailContact) emailContact.getContact();
        assertNull(email.getAddress());

        ContactDetail cellphoneContact = result.getContactDetails().get(1);
        MobileContact cellphone = (MobileContact) cellphoneContact.getContact();
        assertNull(cellphone.getNumber());
    }

    private void assertTrimmedValues(NonBusiness result) {
        assertEquals(BUSINESS_DOC_NUMBER, result.getBusinessDocuments().get(0).getDocumentNumber());
        assertEquals(COMPANY_NAME, result.getLegalName());

        LegalRepresentative owner = result.getLegalRepresentatives().get(0);
        assertEquals(FIRST_NAME, owner.getFirstName());
        assertEquals("", owner.getMiddleName());
        assertEquals(LAST_NAME, owner.getLastName());
        assertEquals(SECOND_LAST_NAME, owner.getSecondLastName());

        ContactDetail emailContact = result.getContactDetails().get(0);
        EmailContact email = (EmailContact) emailContact.getContact();
        assertEquals(EMAIL_ADDRESS, email.getAddress());

        ContactDetail cellphoneContact = result.getContactDetails().get(1);
        MobileContact cellphone = (MobileContact) cellphoneContact.getContact();
        assertEquals(PHONE_NUMBER, cellphone.getNumber());
    }
}