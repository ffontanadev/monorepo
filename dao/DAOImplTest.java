package uy.com.bbva.services.documents.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uy.com.bbva.accountscommons.idmanagement.datatypes.AccountIdDatatype;
import uy.com.bbva.customerscommons.idmanagement.datatypes.CustomerIdDatatype;
import uy.com.bbva.documentscommons.dtos.models.Categorization;
import uy.com.bbva.documentscommons.dtos.models.DocumentInfo;
import uy.com.bbva.documentscommons.dtos.models.MetaData;
import uy.com.bbva.dtos.commons.model.GenericObject;
import uy.com.bbva.filenetcommons.dtos.models.FilenetFile;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.mongo.commons.criteria.CriteriaBBVA;
import uy.com.bbva.mongo.commons.ManagerMongoDBAccess;
import uy.com.bbva.mongo.commons.MongoTemplateBBVA;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.commons.dao.ManagerDataAccessAs400;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.documents.commons.TestDataFactory;
import uy.com.bbva.services.documents.dao.impl.DAOImpl;
import uy.com.bbva.services.documents.model.*;
import uy.com.bbva.services.documents.utils.ErrorConstants;


import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DAOImplTest {

    @Mock
    private ManagerDataAccessAs400 manager;

    @Mock
    private ManagerMongoDBAccess managerMongoDBAccess;

    @Mock
    private MongoTemplateBBVA mongoTemplate;

    @Mock
    private LogUtils logUtils;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private DAOImpl dao;

    @BeforeEach
    void beforeEach() {
        ReflectionTestUtils.setField(dao, "documentDbName", TestDataFactory.DOCUMENT_DB_NAME);
        ReflectionTestUtils.setField(dao, "filenetDbName", TestDataFactory.FILENET_DB_NAME);
    }

    @Test
    void getDocument_shouldReturnDocument_whenPresentInStaticDocumentsRepository() throws ServiceException {

        final List<RepositoryStaticDocument> staticDocuments = new ArrayList<>();
        final RepositoryStaticDocument staticDocument = new RepositoryStaticDocument();
        staticDocuments.add(staticDocument);
        when(managerMongoDBAccess.getMongoTemplateBBVA(anyString())).thenReturn(mongoTemplate);
        when(mongoTemplate.findByParams(any(CriteriaBBVA.class), eq(RepositoryStaticDocument.class))).thenReturn(staticDocuments);

        final RepositoryDocument document = dao.getDocument("00000000-0000-0000-0000-000000000000");

        assertNotNull(document);
    }

    @Test
    void getDocument_shouldReturnDocument_whenPresentInDraftDocumentsRepository() throws ServiceException {

        final List<RepositoryStaticDocument> staticDocuments = new ArrayList<>();
        final List<RepositoryDraftDocument> draftDocuments = new ArrayList<>();
        final RepositoryDraftDocument draftDocument = new RepositoryDraftDocument();
        draftDocuments.add(draftDocument);
        when(managerMongoDBAccess.getMongoTemplateBBVA(anyString())).thenReturn(mongoTemplate);
        when(mongoTemplate.findByParams(any(CriteriaBBVA.class), eq(RepositoryStaticDocument.class))).thenReturn(staticDocuments);
        when(mongoTemplate.findByParams(any(CriteriaBBVA.class), eq(RepositoryDraftDocument.class))).thenReturn(draftDocuments);

        final RepositoryDocument document = dao.getDocument("00000000-0000-0000-0000-000000000000");

        assertNotNull(document);
    }

    @Test
    void getDocument_shouldReturnDocument_whenPresentInFilenetRepository() throws ServiceException {

        final List<RepositoryStaticDocument> staticDocuments = new ArrayList<>();
        final List<RepositoryDraftDocument> draftDocuments = new ArrayList<>();
        final List<FilenetFile> filenetDocuments = new ArrayList<>();
        final FilenetFile filenetDocument = new FilenetFile();
        filenetDocuments.add(filenetDocument);
        when(managerMongoDBAccess.getMongoTemplateBBVA(anyString())).thenReturn(mongoTemplate);
        when(mongoTemplate.findByParams(any(CriteriaBBVA.class), eq(RepositoryStaticDocument.class))).thenReturn(staticDocuments);
        when(mongoTemplate.findByParams(any(CriteriaBBVA.class), eq(RepositoryDraftDocument.class))).thenReturn(draftDocuments);
        when(mongoTemplate.findByParams(any(CriteriaBBVA.class), eq(FilenetFile.class))).thenReturn(filenetDocuments);

        final RepositoryDocument document = dao.getDocument("00000000-0000-0000-0000-000000000000");

        assertNotNull(document);
    }

    @Test
    void getDocument_shouldThrowServiceException_whenNotFoundAnywhere() {

        final List<RepositoryStaticDocument> staticDocuments = new ArrayList<>();
        final List<RepositoryDraftDocument> draftDocuments = new ArrayList<>();
        final List<FilenetFile> filenetDocuments = new ArrayList<>();
        when(managerMongoDBAccess.getMongoTemplateBBVA(anyString())).thenReturn(mongoTemplate);
        when(mongoTemplate.findByParams(any(CriteriaBBVA.class), eq(RepositoryStaticDocument.class))).thenReturn(staticDocuments);
        when(mongoTemplate.findByParams(any(CriteriaBBVA.class), eq(RepositoryDraftDocument.class))).thenReturn(draftDocuments);
        when(mongoTemplate.findByParams(any(CriteriaBBVA.class), eq(FilenetFile.class))).thenReturn(filenetDocuments);

        assertThrows(ServiceException.class, () -> dao.getDocument("00000000-0000-0000-0000-000000000000"));

    }

    @Test
    void getCountryByBantotalCode_shouldReturnGenericObject_whenCountryFound() throws ServiceException, SQLException {

        when(manager.prepareStatement(SQLStatementsBBVA.GET_COUNTRY)).thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("PAISCRC")).thenReturn("UY");
        when(resultSet.getString("PANOMBT")).thenReturn("Uruguay");

        final GenericObject genericObject = dao.getCountryByBantotalCode(845);

        assertNotNull(genericObject);

    }

    @Test
    void getCountryByBantotalCode_shouldReturnEmptyGenericObject_whenCountryNotFound() throws ServiceException, SQLException {

        when(manager.prepareStatement(SQLStatementsBBVA.GET_COUNTRY)).thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        final GenericObject genericObject = dao.getCountryByBantotalCode(645);

        assertNotNull(genericObject);
        assertNull(genericObject.getId());
        assertNull(genericObject.getName());

    }

    @Test
    void getBranchNameByBranchCode_shouldReturnBranchName_whenIsFound() throws ServiceException, SQLException {

        when(manager.prepareStatement(SQLStatementsBBVA.GET_BRANCH_NAME)).thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("SCNOM")).thenReturn("CASA CENTRAL");

        final String branchName = dao.getBranchNameByBranchCode("50");

        assertEquals("CASA CENTRAL", branchName);

    }

    @Test
    void getEntityName_shouldReturnEntityName_whenIsFound() throws ServiceException, SQLException {

        when(manager.prepareStatement(SQLStatementsBBVA.GET_ENTITY_NAME)).thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("TMTMARCA")).thenReturn("VISA");

        final String entityName = dao.getEntityName("221");

        assertEquals("VISA", entityName);

    }

    @Test
    void saveFinalDocument_shouldSaveDocument_whenIsValid() {

        when(managerMongoDBAccess.getMongoTemplateBBVA(anyString())).thenReturn(mongoTemplate);

        final FilenetFile filenetFile = new FilenetFile();
        filenetFile.setId("00000000-0000-0000-0000-000000000000");
        final String id = dao.saveFinalDocument(filenetFile);

        assertEquals("00000000-0000-0000-0000-000000000000", id);
        verify(mongoTemplate).save(any(FilenetFile.class));

    }

    @Test
    void saveDraftDocument_shouldSaveDocument_whenIsValid() throws IOException {

        when(managerMongoDBAccess.getMongoTemplateBBVA(anyString())).thenReturn(mongoTemplate);

        final FileData fileData = new FileData();
        final MetaData metaData = new MetaData();
        final Categorization categorization = new Categorization();
        metaData.setCategorization(categorization);
        final DocumentInfo documentInfo = new DocumentInfo();
        metaData.setDocumentInfo(documentInfo);
        final String documentId = "00000000-0000-0000-0000-000000000000";
        final AccountIdDatatype productIdDatatype = new AccountIdDatatype();
        final CustomerIdDatatype customerIdDatatype = new CustomerIdDatatype();
        final NonBusinessIdDatatype nonBusinessIdDatatype = new NonBusinessIdDatatype();
        final String id = dao.saveDraftDocument(metaData, fileData, documentId, productIdDatatype, customerIdDatatype, nonBusinessIdDatatype);

        assertEquals("00000000-0000-0000-0000-000000000000", id);
        verify(mongoTemplate).save(any(RepositoryDraftDocument.class));

    }

    @Test
    void deleteDocument_shouldRemoveDocument_whenIsDraft() throws ServiceException {

        final RepositoryDraftDocument object = new RepositoryDraftDocument();
        when(managerMongoDBAccess.getMongoTemplateBBVA(anyString())).thenReturn(mongoTemplate);
        when(mongoTemplate.findAndRemove(any(CriteriaBBVA.class), eq(RepositoryDraftDocument.class))).thenReturn(object);

        final Object deletedfile = dao.deleteDocument("00000000-0000-0000-0000-000000000000", "draft");

        assertNotNull(deletedfile);

    }

    @Test
    void deleteDocument_shouldRemoveDocument_whenIsFilenet() throws ServiceException {

        final FilenetFile object = new FilenetFile();
        when(managerMongoDBAccess.getMongoTemplateBBVA(anyString())).thenReturn(mongoTemplate);
        when(mongoTemplate.findAndRemove(any(CriteriaBBVA.class), eq(FilenetFile.class))).thenReturn(object);

        final Object deletedfile = dao.deleteDocument("00000000-0000-0000-0000-000000000000", "filenet");

        assertNotNull(deletedfile);

    }

    @Test
    void deleteDocument_shouldThrowServiceException_whenIsInvalidDatabaseType() {

        assertThrows(ServiceException.class, () -> dao.deleteDocument("00000000-0000-0000-0000-000000000000", "xxx"));

    }

    @Test
    void getStaticProperties_shouldReturnPropertiesMap_whenFoundInDatabase() {

        when(managerMongoDBAccess.getMongoTemplateBBVA(anyString())).thenReturn(mongoTemplate);

        final List<ContractStaticProperty> properties = new ArrayList<>();
        final ContractStaticProperty contractProperty = new ContractStaticProperty();
        contractProperty.setFieldName("FIELD");
        contractProperty.setValue("VALUE");
        properties.add(contractProperty);
        when(mongoTemplate.findByParams(any(CriteriaBBVA.class), eq(ContractStaticProperty.class))).thenReturn(properties);

        final Map<String, String> propertiesMap = dao.getStaticProperties("LOAN");

        assertNotNull(propertiesMap);

    }

    @Test
    void getPersonContacts_shouldReturnContacts_whenRecordIsFound() throws SQLException, ServiceException {

        when(manager.prepareStatement(SQLStatementSPIP.GET_PERSON_CONTACTS)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCLITEL3")).thenReturn("099121212");
        when(resultSet.getString("BBNCLIMAIL")).thenReturn("test@test.com");

        final ContactDetailDataType contactDetailDataType = dao.getPersonContacts("29124124", 1, 845);

        assertNotNull(contactDetailDataType);

    }

    @Test
    void getPersonContacts_shouldReturnNull_whenRecordNotFound() throws SQLException, ServiceException {

        when(manager.prepareStatement(SQLStatementSPIP.GET_PERSON_CONTACTS)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        final ContactDetailDataType contactDetailDataType = dao.getPersonContacts("29124124", 1, 845);

        assertNull(contactDetailDataType);

    }

    @Test
    void getNonBusinessDataMap_validData_returnsCompleteMap() throws Exception {
        // Arrange
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();
        setupMockResultSetForBusinessData(true);

        when(manager.prepareStatement(SQLStatementsBBVA.GET_NON_BUSINESS_DATA))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenReturn(resultSet);

        Map<String, String> result = dao.getNonBusinessDataMap(nonBusinessId);

        assertNotNull(result, "Result map should not be null");
        assertEquals(17, result.size(), "Map should contain all 17 business data fields");

        assertEquals("123456", result.get("ACCOUNT_NUMBER"));
        assertEquals("Test Branch", result.get("BRANCH_NAME"));
        assertEquals("Test Business Name", result.get("SOCIAL_NAME"));
        assertEquals("13 - PERSONA FISICA - UNIPERSONAL", result.get("LEGAL_NATURE"));
        assertEquals("12345678", result.get("BUSINESS_DOCUMENT_NUMBER"));
        assertEquals("01/01/2020", result.get("CONSTITUTION_DATE"));
        assertEquals("Test Commercial Name", result.get("COMMERCIAL_NAME"));
        assertEquals("100000", result.get("ANNUAL_BILLING"));
        assertEquals("Test Activity", result.get("MAIN_ACTIVITY"));

        verify(preparedStatement).setString(1, "12345678");
        verify(preparedStatement).setInt(2, 845);
        verify(preparedStatement).setInt(3, 1);
        verify(manager).closeResources(preparedStatement, resultSet);
    }

    @Test
    void getNonBusinessDataMap_noDataFound_returnsEmptyMap() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();

        when(manager.prepareStatement(SQLStatementsBBVA.GET_NON_BUSINESS_DATA))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Map<String, String> result = dao.getNonBusinessDataMap(nonBusinessId);

        assertTrue(result.isEmpty(), "Result map should be empty when no data found");
        verify(manager).closeResources(preparedStatement, resultSet);
    }

    @Test
    void getNonBusinessDataMap_withNullAndEmptyValues_trimsCorrectly() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();
        setupMockResultSetForBusinessData(false);

        when(manager.prepareStatement(SQLStatementsBBVA.GET_NON_BUSINESS_DATA))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenReturn(resultSet);

        Map<String, String> result = dao.getNonBusinessDataMap(nonBusinessId);

        assertNotNull(result);
        assertEquals("", result.get("ACCOUNT_NUMBER"), "Null values should become empty strings");
        assertEquals("", result.get("BRANCH_NAME"), "Empty strings should remain empty");
        assertEquals("", result.get("COMMERCIAL_NAME"), "Whitespace should be trimmed to empty");

        verify(manager).closeResources(preparedStatement, resultSet);
    }

    @Test
    void getNonBusinessDataMap_sqlException_throwsServiceException() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();
        SQLException sqlException = new SQLException("Database connection failed");

        when(manager.prepareStatement(SQLStatementsBBVA.GET_NON_BUSINESS_DATA))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenThrow(sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> dao.getNonBusinessDataMap(nonBusinessId),
                "Should throw ServiceException when SQLException occurs");

        assertEquals(ErrorConstants.GET_ENTITY_NAME_ERROR_MESSAGE, exception.getMessage(),
                "Should use configured error message (Note: This is misleading - should be GET_NON_BUSINESS_DATA_ERROR_MESSAGE)");
        assertEquals(sqlException, exception.getCause(),
                "Should wrap original SQLException");

        verify(manager).closeResources(preparedStatement, null);
    }

    @Test
    void getNonBusinessLegalAddressMap_validData_returnsCompleteAddressMap() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();
        setupMockResultSetForAddressData();

        when(manager.prepareStatement(SQLStatementsBBVA.GET_ADDRESS))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenReturn(resultSet);

        Map<String, String> result = dao.getNonBusinessLegalAddressMap(nonBusinessId);

        assertNotNull(result, "Legal address map should not be null");
        assertEquals(11, result.size(), "Map should contain all 11 address fields");

        assertEquals("Level1", result.get("LEGAL_ADDRESS_LEVEL1_ID"));
        assertEquals("Level2", result.get("LEGAL_ADDRESS_LEVEL2_ID"));
        assertEquals("Level3", result.get("LEGAL_ADDRESS_LEVEL3_ID"));
        assertEquals("Description1", result.get("LEGAL_ADDRESS_LEVEL1_DESCRIPTION"));
        assertEquals("Description2", result.get("LEGAL_ADDRESS_LEVEL2_DESCRIPTION"));
        assertEquals("Description3", result.get("LEGAL_ADDRESS_LEVEL3_DESCRIPTION"));
        assertEquals("Uruguay", result.get("LEGAL_ADDRESS_COUNTRY"));
        assertEquals("Montevideo", result.get("LEGAL_ADDRESS_STATE"));
        assertEquals("Street", result.get("LEGAL_ADDRESS_LOCATION_TYPE"));
        assertEquals("Centro", result.get("LEGAL_ADDRESS_CITY_NEIGHBORHOOD"));
        assertEquals("11200", result.get("LEGAL_ADDRESS_POSTAL_CODE"));

        verify(preparedStatement).setString(1, "12345678");
        verify(preparedStatement).setInt(2, 845);
        verify(preparedStatement).setInt(3, 1);
        verify(manager).closeResources(preparedStatement, resultSet);
    }

    @Test
    void getNonBusinessLegalAddressMap_noAddressFound_returnsEmptyMap() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();

        when(manager.prepareStatement(SQLStatementsBBVA.GET_ADDRESS))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Map<String, String> result = dao.getNonBusinessLegalAddressMap(nonBusinessId);

        assertNotNull(result, "Should return empty map, not null");
        assertTrue(result.isEmpty(), "Map should be empty when no address found");
        verify(manager).closeResources(preparedStatement, resultSet);
    }

    @Test
    void getNonBusinessLegalAddressMap_sqlException_throwsServiceException() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();
        SQLException sqlException = new SQLException("Address query failed");

        when(manager.prepareStatement(SQLStatementsBBVA.GET_ADDRESS))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenThrow(sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> dao.getNonBusinessLegalAddressMap(nonBusinessId),
                "Should throw ServiceException on database error");

        assertEquals(ErrorConstants.GET_ENTITY_NAME_ERROR_MESSAGE, exception.getMessage());
        assertEquals(sqlException, exception.getCause());
        verify(manager).closeResources(preparedStatement, null);
    }

    @Test
    void getNonBusinessHomeAddressMap_validData_returnsCompleteAddressMap() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();
        setupMockResultSetForAddressData();

        when(manager.prepareStatement(SQLStatementsBBVA.GET_ADDRESS))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenReturn(resultSet);

        Map<String, String> result = dao.getNonBusinessHomeAddressMap(nonBusinessId);

        assertNotNull(result, "Home address map should not be null");
        assertEquals(11, result.size(), "Map should contain all 11 address fields");

        assertEquals("Level1", result.get("HOME_ADDRESS_LEVEL1_ID"));
        assertEquals("Uruguay", result.get("HOME_ADDRESS_COUNTRY"));
        assertEquals("Montevideo", result.get("HOME_ADDRESS_STATE"));
        assertEquals("11200", result.get("HOME_ADDRESS_POSTAL_CODE"));

        verify(preparedStatement).setString(1, "87654321");
        verify(preparedStatement).setInt(2, 858);
        verify(preparedStatement).setInt(3, 2);
        verify(manager).closeResources(preparedStatement, resultSet);
    }

    @Test
    void getNonBusinessHomeAddressMap_noAddressFound_returnsEmptyMap() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();

        when(manager.prepareStatement(SQLStatementsBBVA.GET_ADDRESS))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Map<String, String> result = dao.getNonBusinessHomeAddressMap(nonBusinessId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(manager).closeResources(preparedStatement, resultSet);
    }

    @Test
    void getNonBusinessHomeAddressMap_sqlException_throwsServiceException() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();
        SQLException sqlException = new SQLException("Home address query failed");

        when(manager.prepareStatement(SQLStatementsBBVA.GET_ADDRESS))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenThrow(sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> dao.getNonBusinessHomeAddressMap(nonBusinessId),
                "Should throw ServiceException on database error");

        assertEquals(ErrorConstants.GET_ENTITY_NAME_ERROR_MESSAGE, exception.getMessage());
        assertEquals(sqlException, exception.getCause());
        verify(manager).closeResources(preparedStatement, null);
    }

    @Test
    void saveDraftDocument_withCustomerDatatype_savesDraftSuccessfully() throws Exception {
        MetaData metaData = TestDataFactory.createMetaData();
        FileData fileData = TestDataFactory.createFileData();
        String documentId = "test-doc-id-123";
        AccountIdDatatype accountIdDatatype = TestDataFactory.createAccountIdDatatype();
        CustomerIdDatatype customerIdDatatype = TestDataFactory.createCustomerIdDatatype();

        when(managerMongoDBAccess.getMongoTemplateBBVA(TestDataFactory.DOCUMENT_DB_NAME))
                .thenReturn(mongoTemplate);


        String result = dao.saveDraftDocument(metaData, fileData, documentId,
                accountIdDatatype, customerIdDatatype, null);

        assertNotNull(result, "Should return document ID");

        ArgumentCaptor<RepositoryDraftDocument> docCaptor =
                ArgumentCaptor.forClass(RepositoryDraftDocument.class);
        verify(mongoTemplate).save(docCaptor.capture());

        RepositoryDraftDocument savedDoc = docCaptor.getValue();
        assertNotNull(savedDoc, "Saved document should not be null");
        assertEquals(12345, savedDoc.getAccountNumber(),
                "Account number should be set from AccountIdDatatype");
        assertEquals("12345678", savedDoc.getUserDocumentNumber(),
                "Should use customer document number");
        assertEquals(845, savedDoc.getUserDocumentCountry(),
                "Should use customer country");
        assertEquals(1, savedDoc.getUserDocumentType(),
                "Should use customer document type");
        assertEquals("test-file.pdf", savedDoc.getFileName(),
                "File name should be set from FileData");
    }

    @Test
    void saveDraftDocument_withNonBusinessDatatype_savesDraftSuccessfully() throws Exception {
        MetaData metaData = TestDataFactory.createMetaData();
        FileData fileData = TestDataFactory.createFileData();
        String documentId = "test-doc-id-456";
        AccountIdDatatype accountIdDatatype = TestDataFactory.createAccountIdDatatype();
        NonBusinessIdDatatype nonBusinessIdDatatype = TestDataFactory.createNonBusinessIdDatatype();

        when(managerMongoDBAccess.getMongoTemplateBBVA(TestDataFactory.DOCUMENT_DB_NAME))
                .thenReturn(mongoTemplate);

        String result = dao.saveDraftDocument(metaData, fileData, documentId,
                accountIdDatatype, null, nonBusinessIdDatatype);

        assertNotNull(result);

        ArgumentCaptor<RepositoryDraftDocument> docCaptor =
                ArgumentCaptor.forClass(RepositoryDraftDocument.class);
        verify(mongoTemplate).save(docCaptor.capture());

        RepositoryDraftDocument savedDoc = docCaptor.getValue();
        assertEquals("87654321", savedDoc.getUserDocumentNumber(),
                "Should use non-business PERSON document number");
        assertEquals(858, savedDoc.getUserDocumentCountry(),
                "Should use non-business person country");
        assertEquals(2, savedDoc.getUserDocumentType(),
                "Should use non-business person document type");
    }

    @Test
    void saveDraftDocument_withoutAccountDatatype_savesDraftWithoutAccountNumber() throws Exception {
        MetaData metaData = TestDataFactory.createMetaData();
        FileData fileData = TestDataFactory.createFileData();
        String documentId = "test-doc-id-789";
        CustomerIdDatatype customerIdDatatype = TestDataFactory.createCustomerIdDatatype();

        when(managerMongoDBAccess.getMongoTemplateBBVA(TestDataFactory.DOCUMENT_DB_NAME))
                .thenReturn(mongoTemplate);

        String result = dao.saveDraftDocument(metaData, fileData, documentId,
                null, customerIdDatatype, null); // No account datatype

        assertNotNull(result);

        ArgumentCaptor<RepositoryDraftDocument> docCaptor =
                ArgumentCaptor.forClass(RepositoryDraftDocument.class);
        verify(mongoTemplate).save(docCaptor.capture());

        RepositoryDraftDocument savedDoc = docCaptor.getValue();
        assertEquals(0, savedDoc.getAccountNumber(),
                "Account number should remain at default value (0) when not provided");
        assertEquals("12345678", savedDoc.getUserDocumentNumber(),
                "Customer document should still be set");
    }

    @Test
    void saveDraftDocument_withBase64Data_preservesDataIntegrity() throws Exception {
        MetaData metaData = TestDataFactory.createMetaData();
        FileData fileData = new FileData();
        fileData.setBase64Data("VGhpcyBpcyBhIHRlc3QgZG9jdW1lbnQgd2l0aCBzcGVjaWFsIGNoYXJzOiDDsSDDqSDDrSDDsw==");
        fileData.setName("special-chars.pdf");

        String documentId = "test-doc-special";
        CustomerIdDatatype customerIdDatatype = TestDataFactory.createCustomerIdDatatype();

        when(managerMongoDBAccess.getMongoTemplateBBVA(TestDataFactory.DOCUMENT_DB_NAME))
                .thenReturn(mongoTemplate);

        String result = dao.saveDraftDocument(metaData, fileData, documentId,
                null, customerIdDatatype, null);

        assertNotNull(result);

        ArgumentCaptor<RepositoryDraftDocument> docCaptor =
                ArgumentCaptor.forClass(RepositoryDraftDocument.class);
        verify(mongoTemplate).save(docCaptor.capture());

        RepositoryDraftDocument savedDoc = docCaptor.getValue();
        assertNotNull(savedDoc.getBase64File(),
                "Base64 data should be preserved");
        assertEquals("special-chars.pdf", savedDoc.getFileName());
    }

    @Test
    void saveDraftDocument_ioException_propagatesException() {
        MetaData metaData = TestDataFactory.createMetaData();
        FileData fileData = TestDataFactory.createFileData();
        String documentId = "test-doc-error";
        CustomerIdDatatype customerIdDatatype = TestDataFactory.createCustomerIdDatatype();

        when(managerMongoDBAccess.getMongoTemplateBBVA(TestDataFactory.DOCUMENT_DB_NAME))
                .thenReturn(mongoTemplate);

        doThrow(new RuntimeException("MongoDB save failed"))
                .when(mongoTemplate).save(any(RepositoryDraftDocument.class));

        assertThrows(RuntimeException.class,
                () -> dao.saveDraftDocument(metaData, fileData, documentId,
                        null, customerIdDatatype, null),
                "Should propagate exception when MongoDB save fails");
    }

    @Test
    void saveDraftDocument_withBothDatatypesNull_throwsNullPointerException() {
        MetaData metaData = TestDataFactory.createMetaData();
        FileData fileData = TestDataFactory.createFileData();
        String documentId = "test-doc-no-id";

        when(managerMongoDBAccess.getMongoTemplateBBVA(TestDataFactory.DOCUMENT_DB_NAME))
                .thenReturn(mongoTemplate);

        assertThrows(NullPointerException.class,
                () -> dao.saveDraftDocument(metaData, fileData, documentId,
                        null, null, null),
                "Should throw NullPointerException when both ID datatypes are null");
    }

    @Test
    void getNonBusinessPersonDataMap_validData_returnsCompletePersonMap() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();
        setupMockResultSetForPersonData(true, true);

        when(manager.prepareStatement(SQLStatementsBBVA.GET_NON_BUSINESS_PERSON_DATA))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenReturn(resultSet);

        Map<String, String> result = dao.getNonBusinessPersonDataMap(nonBusinessId);

        assertEquals("García", result.get("FIRST_LAST_NAME"));
        assertEquals("López", result.get("SECOND_LAST_NAME"));
        assertEquals("Juan", result.get("FIRST_NAME"));
        assertEquals("Carlos", result.get("SECOND_NAME"));
        assertEquals("UY", result.get("BENEFICIARY_DOCUMENT_COUNTRY"));
        assertEquals("CI", result.get("BENEFICIARY_DOCUMENT_TYPE"));
        assertEquals("87654321", result.get("BENEFICIARY_DOCUMENT_NUMBER"));
        assertEquals("15/05/1985", result.get("BIRTH_DATE"));
        assertEquals("31/12/2030", result.get("DOCUMENT_EXPIRATION_DATE"));
        assertEquals("Casado", result.get("MARITAL_STATUS"));
        assertEquals("M", result.get("SEX"));
        assertEquals("Uruguay", result.get("BIRTH_COUNTRY"));
        assertEquals("Fernández", result.get("SPOUSE_FIRST_LAST_NAME"));
        assertEquals("Martínez", result.get("SPOUSE_SECOND_LAST_NAME"));
        assertEquals("María", result.get("SPOUSE_FIRST_NAME"));
        assertEquals("Elena", result.get("SPOUSE_SECOND_NAME"));
        assertEquals("CI", result.get("SPOUSE_DOCUMENT_TYPE"));
        assertEquals("UY", result.get("SPOUSE_DOCUMENT_COUNTRY"));
        assertEquals("12349876", result.get("SPOUSE_DOCUMENT_NUMBER"));
        assertEquals("099123456", result.get("CONTACT_MOBILE"));
        assertEquals("juan.garcia@email.com", result.get("CONTACT_EMAIL"));

        verify(preparedStatement).setString(1, "87654321");
        verify(preparedStatement).setInt(2, 858);
        verify(preparedStatement).setInt(3, 2);
        verify(manager).closeResources(preparedStatement, resultSet);
    }

    @Test
    void getNonBusinessPersonDataMap_singlePersonWithoutSpouse_returnsPersonDataWithEmptySpouseFields() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();
        setupMockResultSetForPersonData(true, false);

        when(manager.prepareStatement(SQLStatementsBBVA.GET_NON_BUSINESS_PERSON_DATA))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenReturn(resultSet);

        Map<String, String> result = dao.getNonBusinessPersonDataMap(nonBusinessId);

        assertNotNull(result);
        assertEquals(21, result.size(), "Should still have 21 fields");
        assertEquals("García", result.get("FIRST_LAST_NAME"));
        assertEquals("Juan", result.get("FIRST_NAME"));
        assertEquals("Soltero", result.get("MARITAL_STATUS"));
        assertEquals("", result.get("SPOUSE_FIRST_LAST_NAME"));
        assertEquals("", result.get("SPOUSE_SECOND_LAST_NAME"));
        assertEquals("", result.get("SPOUSE_FIRST_NAME"));
        assertEquals("", result.get("SPOUSE_SECOND_NAME"));
        assertEquals("", result.get("SPOUSE_DOCUMENT_TYPE"));
        assertEquals("", result.get("SPOUSE_DOCUMENT_COUNTRY"));
        assertEquals("", result.get("SPOUSE_DOCUMENT_NUMBER"));

        verify(manager).closeResources(preparedStatement, resultSet);
    }

    @Test
    void getNonBusinessPersonDataMap_noDataFound_returnsEmptyMap() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();

        when(manager.prepareStatement(SQLStatementsBBVA.GET_NON_BUSINESS_PERSON_DATA))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Map<String, String> result = dao.getNonBusinessPersonDataMap(nonBusinessId);

        assertNotNull(result, "Result should not be null even when no data found");
        assertTrue(result.isEmpty(), "Result map should be empty when person not found");
        verify(manager).closeResources(preparedStatement, resultSet);
    }

    @Test
    void getNonBusinessPersonDataMap_withNullAndEmptyValues_trimsCorrectly() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();

        when(manager.prepareStatement(SQLStatementsBBVA.GET_NON_BUSINESS_PERSON_DATA))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        when(resultSet.getString("BBNCPFAPE1")).thenReturn("García");
        when(resultSet.getString("BBNCPFAPE2")).thenReturn(null);
        when(resultSet.getString("BBNCPFNOM1")).thenReturn("Juan");
        when(resultSet.getString("BBNCPFNOM2")).thenReturn("  ");
        when(resultSet.getString("DOCUMENT_COUNTRY")).thenReturn("UY");
        when(resultSet.getString("DOCUMENT_TYPE")).thenReturn("CI");
        when(resultSet.getString("BBNCPFNDOC")).thenReturn("87654321");
        when(resultSet.getString("BBNCPFFNAC")).thenReturn("19850515");
        when(resultSet.getString("MARITAL_STATUS")).thenReturn("");
        when(resultSet.getString("BBNCPFSEXO")).thenReturn("M");
        when(resultSet.getString("BBNCPFFVCI")).thenReturn("20301231");
        when(resultSet.getString("BBNCPFCOA1")).thenReturn(null);
        when(resultSet.getString("BBNCPFCOA2")).thenReturn(null);
        when(resultSet.getString("BBNCPFCON1")).thenReturn(null);
        when(resultSet.getString("BBNCPFCON2")).thenReturn(null);
        when(resultSet.getString("SPOUSE_DOCUMENT_TYPE")).thenReturn("");
        when(resultSet.getString("SPOUSE_DOCUMENT_COUNTRY")).thenReturn("");
        when(resultSet.getString("BBNCPFCOND")).thenReturn(null);
        when(resultSet.getString("BBNCPFTEL1")).thenReturn("   ");
        when(resultSet.getString("BBNCPFMAIL")).thenReturn(null);
        when(resultSet.getString("BIRTH_COUNTRY")).thenReturn("Uruguay");

        Map<String, String> result = dao.getNonBusinessPersonDataMap(nonBusinessId);

        assertNotNull(result);
        assertEquals("García", result.get("FIRST_LAST_NAME"));
        assertEquals("", result.get("SECOND_LAST_NAME"), "Null should become empty string");
        assertEquals("Juan", result.get("FIRST_NAME"));
        assertEquals("", result.get("SECOND_NAME"), "Whitespace should be trimmed to empty");
        assertEquals("", result.get("MARITAL_STATUS"), "Empty should remain empty");
        assertEquals("", result.get("CONTACT_MOBILE"), "Whitespace should be trimmed");
        assertEquals("", result.get("CONTACT_EMAIL"), "Null should become empty string");

        verify(manager).closeResources(preparedStatement, resultSet);
    }

    @Test
    void getNonBusinessPersonDataMap_sqlExceptionOnExecuteQuery_throwsServiceException() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();
        SQLException sqlException = new SQLException("Query execution failed");

        when(manager.prepareStatement(SQLStatementsBBVA.GET_NON_BUSINESS_PERSON_DATA))
                .thenReturn(preparedStatement);
        when(manager.executeQuery(preparedStatement))
                .thenThrow(sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> dao.getNonBusinessPersonDataMap(nonBusinessId),
                "Should throw ServiceException when SQLException occurs");

        assertEquals(ErrorConstants.GET_ENTITY_NAME_ERROR_MESSAGE, exception.getMessage(),
                "Should use configured error message (Note: This is misleading - should be GET_PERSON_DATA_ERROR_MESSAGE)");
        assertEquals(sqlException, exception.getCause(),
                "Should wrap original SQLException");

        verify(manager).closeResources(preparedStatement, null);
    }

    @Test
    void getNonBusinessPersonDataMap_sqlExceptionOnPrepareStatement_throwsServiceException() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();
        SQLException sqlException = new SQLException("Failed to prepare statement");

        when(manager.prepareStatement(SQLStatementsBBVA.GET_NON_BUSINESS_PERSON_DATA))
                .thenThrow(sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> dao.getNonBusinessPersonDataMap(nonBusinessId),
                "Should throw ServiceException when prepareStatement fails");

        assertEquals(ErrorConstants.GET_ENTITY_NAME_ERROR_MESSAGE, exception.getMessage());
        assertEquals(sqlException, exception.getCause());

        verify(manager).closeResources(null, null);
    }

    @Test
    void getNonBusinessPersonDataMap_sqlExceptionOnSetParameter_throwsServiceException() throws Exception {
        NonBusinessIdDatatype nonBusinessId = TestDataFactory.createNonBusinessIdDatatype();
        SQLException sqlException = new SQLException("Parameter binding failed");

        when(manager.prepareStatement(SQLStatementsBBVA.GET_NON_BUSINESS_PERSON_DATA))
                .thenReturn(preparedStatement);
        doThrow(sqlException).when(preparedStatement).setString(anyInt(), anyString());

        ServiceException exception = assertThrows(ServiceException.class,
                () -> dao.getNonBusinessPersonDataMap(nonBusinessId),
                "Should throw ServiceException when parameter setting fails");

        assertEquals(ErrorConstants.GET_ENTITY_NAME_ERROR_MESSAGE, exception.getMessage());
        assertEquals(sqlException, exception.getCause());

        verify(manager).closeResources(preparedStatement, null);
    }

    // ========== HELPER METHODS FOR RESULTSET STUBBING ==========

    private void setupMockResultSetForBusinessData(boolean withValidData) throws SQLException {
        when(resultSet.next()).thenReturn(true);

        if (withValidData) {
            when(resultSet.getString("BBNCPJCTA")).thenReturn("123456");
            when(resultSet.getString("BRANCH_NAME")).thenReturn("Test Branch");
            when(resultSet.getString("BBNCPJRASO")).thenReturn("Test Business Name");
            when(resultSet.getString("DOCUMENT_COUNTRY")).thenReturn("UY");
            when(resultSet.getString("DOCUMENT_TYPE")).thenReturn("RUT");
            when(resultSet.getString("BBNCPJNDOC")).thenReturn("12345678");
            when(resultSet.getString("BBNCPJFCON")).thenReturn("20200101");
            when(resultSet.getString("BBNCPJFINR")).thenReturn("20200115");
            when(resultSet.getString("BBNCPJIBPS")).thenReturn("987654");
            when(resultSet.getString("BBNCPJNOMC")).thenReturn("Test Commercial Name");
            when(resultSet.getString("BBNCPJFING")).thenReturn("20230101");
            when(resultSet.getString("BBNCPJIANU")).thenReturn("100000");
            when(resultSet.getString("MAIN_ACTIVITY")).thenReturn("Test Activity");
            when(resultSet.getString("ACTIVITY_DESCRIPTION")).thenReturn("Test Description");
            when(resultSet.getString("BBNCPJFRUT")).thenReturn("20251231");
        } else {
            when(resultSet.getString("BBNCPJCTA")).thenReturn(null);
            when(resultSet.getString("BRANCH_NAME")).thenReturn("");
            when(resultSet.getString("BBNCPJRASO")).thenReturn("  ");
            when(resultSet.getString("DOCUMENT_COUNTRY")).thenReturn("UY");
            when(resultSet.getString("DOCUMENT_TYPE")).thenReturn("RUT");
            when(resultSet.getString("BBNCPJNDOC")).thenReturn("12345678");
            when(resultSet.getString("BBNCPJFCON")).thenReturn("20200101");
            when(resultSet.getString("BBNCPJFINR")).thenReturn("20200115");
            when(resultSet.getString("BBNCPJIBPS")).thenReturn(null);
            when(resultSet.getString("BBNCPJNOMC")).thenReturn("");
            when(resultSet.getString("BBNCPJFING")).thenReturn("20230101");
            when(resultSet.getString("BBNCPJIANU")).thenReturn("100000");
            when(resultSet.getString("MAIN_ACTIVITY")).thenReturn("Activity");
            when(resultSet.getString("ACTIVITY_DESCRIPTION")).thenReturn("");
            when(resultSet.getString("BBNCPJFRUT")).thenReturn("20251231");
        }
    }

    private void setupMockResultSetForAddressData() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("SNGC01DSC")).thenReturn("Level1");
        when(resultSet.getString("SNGC02DSC")).thenReturn("Level2");
        when(resultSet.getString("SNGC03DSC")).thenReturn("Level3");
        when(resultSet.getString("BBNCEDDSC1")).thenReturn("Description1");
        when(resultSet.getString("BBNCEDDSC2")).thenReturn("Description2");
        when(resultSet.getString("BBNCEDDSC3")).thenReturn("Description3");
        when(resultSet.getString("PANOM")).thenReturn("Uruguay");
        when(resultSet.getString("DEPNOM")).thenReturn("Montevideo");
        when(resultSet.getString("ADDRESS_LOCATION_TYPE")).thenReturn("Street");
        when(resultSet.getString("ADDRESS_CITY_NEIGHBORHOOD")).thenReturn("Centro");
        when(resultSet.getString("BBNCEDPOS")).thenReturn("11200");
    }

    private void setupMockResultSetForPersonData(boolean withValidData, boolean withSpouseData) throws SQLException {
        when(resultSet.next()).thenReturn(true);

        if (withValidData) {
            when(resultSet.getString("BBNCPFAPE1")).thenReturn("García");
            when(resultSet.getString("BBNCPFAPE2")).thenReturn("López");
            when(resultSet.getString("BBNCPFNOM1")).thenReturn("Juan");
            when(resultSet.getString("BBNCPFNOM2")).thenReturn("Carlos");
            when(resultSet.getString("DOCUMENT_COUNTRY")).thenReturn("UY");
            when(resultSet.getString("DOCUMENT_TYPE")).thenReturn("CI");
            when(resultSet.getString("BBNCPFNDOC")).thenReturn("87654321");

            when(resultSet.getString("BBNCPFFNAC")).thenReturn("19850515");
            when(resultSet.getString("BBNCPFFVCI")).thenReturn("20301231");

            when(resultSet.getString("MARITAL_STATUS")).thenReturn(withSpouseData ? "Casado" : "Soltero");
            when(resultSet.getString("BBNCPFSEXO")).thenReturn("M");
            when(resultSet.getString("BIRTH_COUNTRY")).thenReturn("Uruguay");

            if (withSpouseData) {
                when(resultSet.getString("BBNCPFCOA1")).thenReturn("Fernández");
                when(resultSet.getString("BBNCPFCOA2")).thenReturn("Martínez");
                when(resultSet.getString("BBNCPFCON1")).thenReturn("María");
                when(resultSet.getString("BBNCPFCON2")).thenReturn("Elena");
                when(resultSet.getString("SPOUSE_DOCUMENT_TYPE")).thenReturn("CI");
                when(resultSet.getString("SPOUSE_DOCUMENT_COUNTRY")).thenReturn("UY");
                when(resultSet.getString("BBNCPFCOND")).thenReturn("12349876");
            } else {
                when(resultSet.getString("BBNCPFCOA1")).thenReturn("");
                when(resultSet.getString("BBNCPFCOA2")).thenReturn("");
                when(resultSet.getString("BBNCPFCON1")).thenReturn("");
                when(resultSet.getString("BBNCPFCON2")).thenReturn("");
                when(resultSet.getString("SPOUSE_DOCUMENT_TYPE")).thenReturn("");
                when(resultSet.getString("SPOUSE_DOCUMENT_COUNTRY")).thenReturn("");
                when(resultSet.getString("BBNCPFCOND")).thenReturn("");
            }

            when(resultSet.getString("BBNCPFTEL1")).thenReturn("099123456");
            when(resultSet.getString("BBNCPFMAIL")).thenReturn("juan.garcia@email.com");
        }
    }
}