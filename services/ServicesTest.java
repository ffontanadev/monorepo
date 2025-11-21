package uy.com.bbva.services.documents.services;

import com.bbva.secarq.caas2.core.exception.CaasException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import uy.com.bbva.accountscommons.idmanagement.datatypes.AccountIdDatatype;
import uy.com.bbva.customerscommons.idmanagement.datatypes.CustomerIdDatatype;
import uy.com.bbva.documentscommons.dtos.models.*;
import uy.com.bbva.documentscommons.dtos.responses.DocumentResponse;
import uy.com.bbva.dtos.commons.model.GenericObject;
import uy.com.bbva.filenetcommons.dtos.models.FilenetFile;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.noncustomerscommons.idmanagement.datatypes.NonCustomerIdDatatype;
import uy.com.bbva.pdf.management.commons.PDFGenerator;
import uy.com.bbva.pdf.management.commons.exceptions.PDFManageException;
import uy.com.bbva.services.commons.exceptions.BusinessException;
import uy.com.bbva.services.commons.exceptions.NotFoundException;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.commons.model.HeaderValues;
import uy.com.bbva.services.documents.commons.ServiceTest;
import uy.com.bbva.services.documents.dtos.response.DocumentResponseGroup;
import uy.com.bbva.services.documents.dtos.response.FileNetResponse;
import uy.com.bbva.services.documents.model.*;
import uy.com.bbva.services.documents.service.impl.DocumentsApiServiceImpl;
import uy.com.bbva.services.documents.service.impl.FileApiServiceImpl;
import uy.com.bbva.services.documents.service.impl.GenerateUnipersonalContractApiServiceImpl;
import uy.com.bbva.services.documents.service.impl.SPIPDocumentControlImpl;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ServicesTests extends ServiceTest {

    @InjectMocks
    private DocumentsApiServiceImpl service;

    @InjectMocks
    private SPIPDocumentControlImpl spipDocumentControl;

    @InjectMocks
    private GenerateUnipersonalContractApiServiceImpl unipContractService;

    @BeforeEach
    void setup() {

        FileApiServiceImpl fileApiService = new FileApiServiceImpl();

        ReflectionTestUtils.setField(fileApiService, "mapTemplateByCardProduct", getMapTemplateByCardProduct());
        ReflectionTestUtils.setField(fileApiService, "fileUtils", fileUtils);
        ReflectionTestUtils.setField(fileApiService, "dao", dao);
        ReflectionTestUtils.setField(fileApiService, "generateContractApiService", generateContractApiService);
        ReflectionTestUtils.setField(service, "fileApiService", fileApiService);
        ReflectionTestUtils.setField(service, "generateContractApiService", generateContractApiService);
        // ReflectionTestUtils.setField(service, "customerIdManagement", customerIdManagement);
    }

    @Test
    void getDocumentByDocumentId_shouldReturnDocument_whenDatabaseReturnsOne() throws ServiceException {
        final RepositoryDocument repositoryDocument = new RepositoryStaticDocument();
        when(dao.getDocument(anyString())).thenReturn(repositoryDocument);

        final DocumentResponse document = service.getDocumentByDocumentId("00000000-0000-0000-0000-000000000000");
        assertNotNull(document);
    }

    @Test
    void getDocumentFileByAccountAndDocumentCode_shouldReturnDocument_whenDocumentExists() throws ServiceException {
        final DocumentResponseGroup documentResponseGroup = new DocumentResponseGroup();
        final List<Document> data = new ArrayList<>();
        final Document document = new Document();
        final MetaData metaData = new MetaData();
        metaData.setId("00000000-0000-0000-0000-000000000000");
        document.setMetaData(metaData);
        data.add(document);
        documentResponseGroup.setData(data);
        when(gDocumentalService.getDocuments(anyString(), anyInt(), any(HeaderValues.class))).thenReturn(documentResponseGroup);

        final FileNetResponse filenetResponse = new FileNetResponse();
        when(gDocumentalService.getFileFromId(anyString(), any(HeaderValues.class))).thenReturn(filenetResponse);

        final HeaderValues headerValues = new HeaderValues();
        final FileNetResponse fileNetResponse = service.getDocumentFileByAccountAndDocumentCode("7889121", 1, headerValues);

        assertNotNull(fileNetResponse);

    }

    @Test
    void getDocumentFileByAccountAndDocumentCode_shouldReturnNull_whenNoDocumentFound() throws ServiceException {
        DocumentResponseGroup documentResponseGroup = new DocumentResponseGroup();
        List<Document> emptyData = new ArrayList<>();
        documentResponseGroup.setData(emptyData);

        when(gDocumentalService.getDocuments(anyString(), anyInt(), any(HeaderValues.class)))
                .thenReturn(documentResponseGroup);

        HeaderValues headerValues = new HeaderValues();
        FileNetResponse result = service.getDocumentFileByAccountAndDocumentCode("7889121", 1, headerValues);

        assertNull(result);
        verify(gDocumentalService, never()).getFileFromId(anyString(), any(HeaderValues.class));
    }

    @Test
    void postDocument_shouldReturnDocument_whenDocumentCreated_cchContract() throws ServiceException, IOException, ParseException, CaasException, PDFManageException {

        final CustomerIdDatatype customerIdDatatype = new CustomerIdDatatype();
        customerIdDatatype.setDocument("29141411");
        when(customerIdManagement.getCustomerIdDatatypeFromCustomerId(anyString())).thenReturn(customerIdDatatype);

        final Map<String, String> propertiesMap = new HashMap<>();
        when(dao.getStaticProperties(anyString())).thenReturn(propertiesMap);

        try (MockedStatic<PDFGenerator> pdfGeneratorMocked = Mockito.mockStatic(PDFGenerator.class)) {
            pdfGeneratorMocked.when(() -> PDFGenerator.fromHTMLtoPDF(anyString(), any(Map.class))).thenReturn(new byte[1]);

            final MetaData metaData = new MetaData();
            final DocumentInfo documentInfo = new DocumentInfo();
            documentInfo.setObjectName("objectName");
            documentInfo.setOwner("owner");
            documentInfo.setResponsible("responsible");
            metaData.setDocumentInfo(documentInfo);
            final Categorization categorization = new Categorization();
            categorization.setDocumentTypeId("CCH_CONTRACT");
            metaData.setCategorization(categorization);
            final SecurityLevelInfo securityLevelInfo = new SecurityLevelInfo();
            securityLevelInfo.setSecurityLevel(1);
            securityLevelInfo.setClassificationLevel(1);
            metaData.setSecurityLevelInfo(securityLevelInfo);
            final List<SpecificMetadata> specificMetatadataList = new ArrayList<>();
            specificMetatadataList.add(specificMetadata("CUSTOMER_ID", FieldTypeEnum.STRING, "29141411"));
            specificMetatadataList.add(specificMetadata("ENTITY", FieldTypeEnum.STRING, "VISA"));
            specificMetatadataList.add(specificMetadata("PRODUCT_TYPE_ID", FieldTypeEnum.STRING, "685"));
            specificMetatadataList.add(specificMetadata("DOCUMENT_NUMBER", FieldTypeEnum.STRING, "29141411"));
            specificMetatadataList.add(specificMetadata("PRODUCT_TYPE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("DUE_DATE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CREDIT_LIMIT", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_STREET", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_NUMBER", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_APARTMENT", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_LOCATION", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_DEPARTMENT", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_POSTAL_CODE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_STREET_2", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_NUMBER_2", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_APARTMENT_2", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_LOCATION_2", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_DEPARTMENT_2", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_POSTAL_CODE_2", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("ACCOUNT_ID", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("DOCUMENT_NUMBER", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CUSTOMER_NAME", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("DATE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("MOBILE_NUMBER", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("EMAIL", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("DISPLAY_MOBILE_NUMBER", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("DISPLAY_EMAIL", FieldTypeEnum.STRING, "-"));
            metaData.setSpecificMetadataList(specificMetatadataList);

            final DocumentResponse documentResponse = service.postDocument(null, metaData, true, 7889121, "29122323");

            assertNotNull(documentResponse);
        }
    }

    @Test
    void postDocument_shouldReturnDocument_whenDocumentCreated_loanContract() throws ServiceException, IOException, ParseException, CaasException, PDFManageException {

        final CustomerIdDatatype customerIdDatatype = new CustomerIdDatatype();
        customerIdDatatype.setDocument("29141411");
        customerIdDatatype.setDocumentType(1);
        customerIdDatatype.setCountry(858);
        when(customerIdManagement.getCustomerIdDatatypeFromCustomerId(anyString())).thenReturn(customerIdDatatype);

        final AccountIdDatatype accountIdDatatype = new AccountIdDatatype();
        accountIdDatatype.setModule(20);
        when(accountIdManagement.getAccountIdDatatypeFromAccountId(anyString())).thenReturn(accountIdDatatype);

        final GenericObject country = new GenericObject();
        country.setName("Uruguay");
        when(dao.getCountryByBantotalCode(anyInt())).thenReturn(country);

        final Map<String, String> propertiesMap = new HashMap<>();
        when(dao.getStaticProperties(anyString())).thenReturn(propertiesMap);

        try (MockedStatic<PDFGenerator> pdfGeneratorMocked = Mockito.mockStatic(PDFGenerator.class)) {
            pdfGeneratorMocked.when(() -> PDFGenerator.fromHTMLtoPDF(anyString(), any(Map.class))).thenReturn(new byte[1]);

            final MetaData metaData = new MetaData();
            final DocumentInfo documentInfo = new DocumentInfo();
            documentInfo.setObjectName("objectName");
            documentInfo.setOwner("owner");
            documentInfo.setResponsible("responsible");
            metaData.setDocumentInfo(documentInfo);

            final Categorization categorization = new Categorization();
            categorization.setDocumentTypeId("LOAN_CONTRACT");
            metaData.setCategorization(categorization);

            final SecurityLevelInfo securityLevelInfo = new SecurityLevelInfo();
            securityLevelInfo.setSecurityLevel(1);
            securityLevelInfo.setClassificationLevel(1);
            metaData.setSecurityLevelInfo(securityLevelInfo);

            final List<SpecificMetadata> specificMetatadataList = new ArrayList<>();
            specificMetatadataList.add(specificMetadata("CUSTOMER_ID", FieldTypeEnum.STRING, "29141411"));
            specificMetatadataList.add(specificMetadata("ACCOUNT_ID", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("INITIAL_AMOUNT_AMOUNT", FieldTypeEnum.STRING, "100"));
            specificMetatadataList.add(specificMetadata("INITIAL_AMOUNT_CURRENCY", FieldTypeEnum.STRING, "UYU"));
            specificMetatadataList.add(specificMetadata("TERMS_NUMBER", FieldTypeEnum.STRING, "12"));
            specificMetatadataList.add(specificMetadata("TERMS_FREQUENCY", FieldTypeEnum.STRING, "MONTHLY"));
            specificMetatadataList.add(specificMetadata("INSTALLMENT_PLAN_INSTALLMENT_AMOUNT", FieldTypeEnum.STRING, "10"));
            specificMetatadataList.add(specificMetadata("RATES_MODE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("RATES_RATE_TYPE_ID", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("RATES_UNIT", FieldTypeEnum.STRING, "5"));
            metaData.setSpecificMetadataList(specificMetatadataList);

            final DocumentResponse documentResponse = service.postDocument(null, metaData, true, 7889121, "29122323");

            assertNotNull(documentResponse);
        }
    }

    @Test
    void postDocument_shouldReturnDocument_whenDocumentCreated_accountContract() throws ServiceException, IOException, ParseException, CaasException, PDFManageException {

        final CustomerIdDatatype customerIdDatatype = new CustomerIdDatatype();
        customerIdDatatype.setDocument("29141411");
        customerIdDatatype.setDocumentType(1);
        when(customerIdManagement.getCustomerIdDatatypeFromCustomerId(anyString())).thenReturn(customerIdDatatype);

        final AccountIdDatatype accountIdDatatype = new AccountIdDatatype();
        accountIdDatatype.setModule(20);
        when(accountIdManagement.getAccountIdDatatypeFromAccountId(anyString())).thenReturn(accountIdDatatype);

        final Map<String, String> propertiesMap = new HashMap<>();
        when(dao.getStaticProperties(anyString())).thenReturn(propertiesMap);

        try (MockedStatic<PDFGenerator> pdfGeneratorMocked = Mockito.mockStatic(PDFGenerator.class)) {
            pdfGeneratorMocked.when(() -> PDFGenerator.fromHTMLtoPDF(anyString(), any(Map.class))).thenReturn(new byte[1]);

            final MetaData metaData = new MetaData();
            final DocumentInfo documentInfo = new DocumentInfo();
            documentInfo.setObjectName("objectName");
            documentInfo.setOwner("owner");
            documentInfo.setResponsible("responsible");
            metaData.setDocumentInfo(documentInfo);
            final Categorization categorization = new Categorization();
            categorization.setDocumentTypeId("ACCOUNT_CONTRACT");
            metaData.setCategorization(categorization);
            final SecurityLevelInfo securityLevelInfo = new SecurityLevelInfo();
            securityLevelInfo.setSecurityLevel(1);
            securityLevelInfo.setClassificationLevel(1);
            metaData.setSecurityLevelInfo(securityLevelInfo);
            final List<SpecificMetadata> specificMetatadataList = new ArrayList<>();
            specificMetatadataList.add(specificMetadata("CUSTOMER_ID", FieldTypeEnum.STRING, "29141411"));
            specificMetatadataList.add(specificMetadata("ACCOUNT_ID", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("BRANCH", FieldTypeEnum.STRING, "50"));
            specificMetatadataList.add(specificMetadata("RESIDENT", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("PEP_IS_PEP", FieldTypeEnum.STRING, "Si"));
            specificMetatadataList.add(specificMetadata("PEP_IS_PEP", FieldTypeEnum.STRING, "No"));
            specificMetatadataList.add(specificMetadata("PEP_BOUNDED", FieldTypeEnum.STRING, "Si"));
            specificMetatadataList.add(specificMetadata("PEP_BOUNDED", FieldTypeEnum.STRING, "Mo"));
            specificMetatadataList.add(specificMetadata("ACC_PURPOSE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("ACC_THIRD_PARTY", FieldTypeEnum.STRING, "Si"));
            specificMetatadataList.add(specificMetadata("ACC_THIRD_PARTY", FieldTypeEnum.STRING, "No"));
            specificMetatadataList.add(specificMetadata("ACC_ADDRESS", FieldTypeEnum.STRING, "Si"));
            specificMetatadataList.add(specificMetadata("ACC_ADDRESS", FieldTypeEnum.STRING, "No"));
            specificMetatadataList.add(specificMetadata("ACC_AVENUE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("ACC_EXTERIOR_NUMBER", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("ACC_INTERIOR_NUMBER", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("ACC_ADDRESS_COUNTRY", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("ACC_STATE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("ACC_CITY", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("ACC_POSTAL_CODE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("DOCUMENT_EXPIRATION_DATE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("EDUCATION_STATE_SITUATION", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("WORK_STATE_SITUATION", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("COIN_TYPE", FieldTypeEnum.STRING, "Si"));
            specificMetatadataList.add(specificMetadata("COIN_TYPE", FieldTypeEnum.STRING, "No"));
            specificMetatadataList.add(specificMetadata("OPERATIVE_OWNER", FieldTypeEnum.STRING, "Si"));
            specificMetatadataList.add(specificMetadata("OPERATIVE_OWNER", FieldTypeEnum.STRING, "No"));
            specificMetatadataList.add(specificMetadata("PACKAGE_NAME", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("PACKAGE_DESC", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("ALTABBVA", FieldTypeEnum.STRING, "Si"));
            specificMetatadataList.add(specificMetadata("ALTABBVA", FieldTypeEnum.STRING, "No"));
            specificMetatadataList.add(specificMetadata("TOKENSMS", FieldTypeEnum.STRING, "Si"));
            specificMetatadataList.add(specificMetadata("TOKENSMS", FieldTypeEnum.STRING, "No"));
            metaData.setSpecificMetadataList(specificMetatadataList);

            final DocumentResponse documentResponse = service.postDocument(null, metaData, true, 7889121, "29122323");

            assertNotNull(documentResponse);
        }
    }

    @Test
    void postDocument_shouldReturnDocument_whenDocumentCreated_additionalCCContract() throws ServiceException, IOException, ParseException, CaasException, PDFManageException {

        final CustomerIdDatatype customerIdDatatype = new CustomerIdDatatype();
        customerIdDatatype.setDocument("29141411");
        when(customerIdManagement.getCustomerIdDatatypeFromCustomerId(anyString())).thenReturn(customerIdDatatype);

        final Map<String, String> propertiesMap = new HashMap<>();
        when(dao.getStaticProperties(anyString())).thenReturn(propertiesMap);

        try (MockedStatic<PDFGenerator> pdfGeneratorMocked = Mockito.mockStatic(PDFGenerator.class)) {
            pdfGeneratorMocked.when(() -> PDFGenerator.fromHTMLtoPDF(anyString(), any(Map.class))).thenReturn(new byte[1]);

            final MetaData metaData = new MetaData();
            final DocumentInfo documentInfo = new DocumentInfo();
            documentInfo.setObjectName("objectName");
            documentInfo.setOwner("owner");
            documentInfo.setResponsible("responsible");
            metaData.setDocumentInfo(documentInfo);
            final Categorization categorization = new Categorization();
            categorization.setDocumentTypeId("ACC_CONTRACT");
            metaData.setCategorization(categorization);
            final SecurityLevelInfo securityLevelInfo = new SecurityLevelInfo();
            securityLevelInfo.setSecurityLevel(1);
            securityLevelInfo.setClassificationLevel(1);
            metaData.setSecurityLevelInfo(securityLevelInfo);
            final List<SpecificMetadata> specificMetatadataList = new ArrayList<>();
            specificMetatadataList.add(specificMetadata("CUSTOMER_ID", FieldTypeEnum.STRING, "29141411"));
            specificMetatadataList.add(specificMetadata("ENTITY", FieldTypeEnum.STRING, "VISA"));
            specificMetatadataList.add(specificMetadata("PRODUCT_TYPE_ID", FieldTypeEnum.STRING, "685"));
            specificMetatadataList.add(specificMetadata("DOCUMENT_NUMBER", FieldTypeEnum.STRING, "29141411"));
            specificMetatadataList.add(specificMetadata("PRODUCT_TYPE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("DUE_DATE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CREDIT_LIMIT", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_STREET", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_NUMBER", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_APARTMENT", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_LOCATION", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_DEPARTMENT", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_POSTAL_CODE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_STREET_2", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_NUMBER_2", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_APARTMENT_2", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_LOCATION_2", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_DEPARTMENT_2", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CARD_ADDRESS_POSTAL_CODE_2", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("ACCOUNT_ID", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("DOCUMENT_NUMBER", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CUSTOMER_NAME", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("DATE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("MOBILE_NUMBER", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("EMAIL", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("DISPLAY_MOBILE_NUMBER", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("DISPLAY_EMAIL", FieldTypeEnum.STRING, "-"));
            metaData.setSpecificMetadataList(specificMetatadataList);

            final DocumentResponse documentResponse = service.postDocument(null, metaData, true, 7889121, "29122323");

            assertNotNull(documentResponse);
        }
    }

    @Test
    void postDocument_shouldReturnDocument_whenDocumentCreated_ftdContract() throws ServiceException, IOException, ParseException, CaasException, PDFManageException {

        final CustomerIdDatatype customerIdDatatype = new CustomerIdDatatype();
        customerIdDatatype.setDocument("29141411");
        customerIdDatatype.setDocumentType(1);
        when(customerIdManagement.getCustomerIdDatatypeFromCustomerId(anyString())).thenReturn(customerIdDatatype);

        final AccountIdDatatype accountIdDatatype = new AccountIdDatatype();
        accountIdDatatype.setModule(20);
        when(accountIdManagement.getAccountIdDatatypeFromAccountId(anyString())).thenReturn(accountIdDatatype);

        final Map<String, String> propertiesMap = new HashMap<>();
        when(dao.getStaticProperties(anyString())).thenReturn(propertiesMap);

        try (MockedStatic<PDFGenerator> pdfGeneratorMocked = Mockito.mockStatic(PDFGenerator.class)) {
            pdfGeneratorMocked.when(() -> PDFGenerator.fromHTMLtoPDF(anyString(), any(Map.class))).thenReturn(new byte[1]);

            final MetaData metaData = new MetaData();
            final DocumentInfo documentInfo = new DocumentInfo();
            documentInfo.setObjectName("objectName");
            documentInfo.setOwner("owner");
            documentInfo.setResponsible("responsible");
            metaData.setDocumentInfo(documentInfo);
            final Categorization categorization = new Categorization();
            categorization.setDocumentTypeId("FTD_DOCUMENT_ID");
            metaData.setCategorization(categorization);
            final SecurityLevelInfo securityLevelInfo = new SecurityLevelInfo();
            securityLevelInfo.setSecurityLevel(1);
            securityLevelInfo.setClassificationLevel(1);
            metaData.setSecurityLevelInfo(securityLevelInfo);
            final List<SpecificMetadata> specificMetatadataList = new ArrayList<>();
            specificMetatadataList.add(specificMetadata("CUSTOMER_ID", FieldTypeEnum.STRING, "29141411"));
            specificMetatadataList.add(specificMetadata("ACCOUNT_ID", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("INITIAL_AMOUNT_AMOUNT", FieldTypeEnum.STRING, "100"));
            specificMetatadataList.add(specificMetadata("INITIAL_AMOUNT_CURRENCY", FieldTypeEnum.STRING, "UYU"));
            specificMetatadataList.add(specificMetadata("TERMS_NUMBER", FieldTypeEnum.STRING, "12"));
            specificMetatadataList.add(specificMetadata("TERMS_FREQUENCY", FieldTypeEnum.STRING, "MONTHLY"));
            specificMetatadataList.add(specificMetadata("INSTALLMENT_PLAN_INSTALLMENT_AMOUNT", FieldTypeEnum.STRING, "10"));
            specificMetatadataList.add(specificMetadata("RATES_MODE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("RATES_RATE_TYPE_ID", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("RATES_UNIT", FieldTypeEnum.STRING, "5"));
            metaData.setSpecificMetadataList(specificMetatadataList);

            final DocumentResponse documentResponse = service.postDocument(null, metaData, true, 7889121, "29122323");

            assertNotNull(documentResponse);
        }
    }

    @Test
    void postDocument_shouldReturnDocument_whenDocumentCreated_openMarketContract() throws ServiceException, IOException, ParseException, CaasException, PDFManageException {

        final CustomerIdDatatype customerIdDatatype = new CustomerIdDatatype();
        customerIdDatatype.setDocument("29141411");
        customerIdDatatype.setDocumentType(1);
        when(customerIdManagement.getCustomerIdDatatypeFromCustomerId(anyString())).thenReturn(customerIdDatatype);

        final AccountIdDatatype accountIdDatatype = new AccountIdDatatype();
        accountIdDatatype.setModule(20);
        when(accountIdManagement.getAccountIdDatatypeFromAccountId(anyString())).thenReturn(accountIdDatatype);

        final Map<String, String> propertiesMap = new HashMap<>();
        when(dao.getStaticProperties(anyString())).thenReturn(propertiesMap);

        try (MockedStatic<PDFGenerator> pdfGeneratorMocked = Mockito.mockStatic(PDFGenerator.class)) {
            pdfGeneratorMocked.when(() -> PDFGenerator.fromHTMLtoPDF(anyString(), any(Map.class))).thenReturn(new byte[1]);

            final MetaData metaData = new MetaData();
            final DocumentInfo documentInfo = new DocumentInfo();
            documentInfo.setObjectName("objectName");
            documentInfo.setOwner("owner");
            documentInfo.setResponsible("responsible");
            metaData.setDocumentInfo(documentInfo);
            final Categorization categorization = new Categorization();
            categorization.setDocumentTypeId("OPEN_MARKET_CONTRACT");
            metaData.setCategorization(categorization);
            final SecurityLevelInfo securityLevelInfo = new SecurityLevelInfo();
            securityLevelInfo.setSecurityLevel(1);
            securityLevelInfo.setClassificationLevel(1);
            metaData.setSecurityLevelInfo(securityLevelInfo);
            final List<SpecificMetadata> specificMetatadataList = new ArrayList<>();
            specificMetatadataList.add(specificMetadata("CUSTOMER_ID", FieldTypeEnum.STRING, "29141411"));
            specificMetatadataList.add(specificMetadata("ACCOUNT_ID", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("INITIAL_AMOUNT_AMOUNT", FieldTypeEnum.STRING, "100"));
            specificMetatadataList.add(specificMetadata("INITIAL_AMOUNT_CURRENCY", FieldTypeEnum.STRING, "UYU"));
            specificMetatadataList.add(specificMetadata("TERMS_NUMBER", FieldTypeEnum.STRING, "12"));
            specificMetatadataList.add(specificMetadata("TERMS_FREQUENCY", FieldTypeEnum.STRING, "MONTHLY"));
            specificMetatadataList.add(specificMetadata("INSTALLMENT_PLAN_INSTALLMENT_AMOUNT", FieldTypeEnum.STRING, "10"));
            specificMetatadataList.add(specificMetadata("RATES_MODE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("RATES_RATE_TYPE_ID", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("RATES_UNIT", FieldTypeEnum.STRING, "5"));
            metaData.setSpecificMetadataList(specificMetatadataList);

            final DocumentResponse documentResponse = service.postDocument(null, metaData, true, 7889121, "29122323");

            assertNotNull(documentResponse);
        }
    }

    @Test
    void postDocument_shouldReturnDocument_whenDocumentCreated_imageDocument() throws ServiceException, IOException, ParseException, CaasException, PDFManageException {

        final CustomerIdDatatype customerIdDatatype = new CustomerIdDatatype();
        customerIdDatatype.setDocument("29141411");
        customerIdDatatype.setDocumentType(1);
        when(customerIdManagement.getCustomerIdDatatypeFromCustomerId(anyString())).thenReturn(customerIdDatatype);

        final AccountIdDatatype accountIdDatatype = new AccountIdDatatype();
        accountIdDatatype.setModule(20);
        when(accountIdManagement.getAccountIdDatatypeFromAccountId(anyString())).thenReturn(accountIdDatatype);

        final Map<String, String> propertiesMap = new HashMap<>();
        when(dao.getStaticProperties(anyString())).thenReturn(propertiesMap);

        try (MockedStatic<PDFGenerator> pdfGeneratorMocked = Mockito.mockStatic(PDFGenerator.class)) {
            pdfGeneratorMocked.when(() -> PDFGenerator.fromHTMLtoPDF(anyString(), any(Map.class))).thenReturn(new byte[1]);

            final MetaData metaData = new MetaData();
            final DocumentInfo documentInfo = new DocumentInfo();
            documentInfo.setObjectName("objectName");
            documentInfo.setOwner("owner");
            documentInfo.setResponsible("responsible");
            metaData.setDocumentInfo(documentInfo);
            final Categorization categorization = new Categorization();
            categorization.setDocumentTypeId("IDENTITY_DOCUMENT");
            metaData.setCategorization(categorization);
            final SecurityLevelInfo securityLevelInfo = new SecurityLevelInfo();
            securityLevelInfo.setSecurityLevel(1);
            securityLevelInfo.setClassificationLevel(1);
            metaData.setSecurityLevelInfo(securityLevelInfo);
            final List<SpecificMetadata> specificMetatadataList = new ArrayList<>();
            specificMetatadataList.add(specificMetadata("CUSTOMER_ID", FieldTypeEnum.STRING, "29141411"));
            specificMetatadataList.add(specificMetadata("ACCOUNT_ID", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("OBVERSE_IMAGE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("REVERSE_IMAGE", FieldTypeEnum.STRING, "-"));
            metaData.setSpecificMetadataList(specificMetatadataList);

            final DocumentResponse documentResponse = service.postDocument(null, metaData, true, 7889121, "29122323");

            assertNotNull(documentResponse);
        }
    }

    @Test
    void postDocument_shouldReturnDocument_whenDocumentCreated_termsAndConditions() throws ServiceException, IOException, ParseException, CaasException, PDFManageException {

        final CustomerIdDatatype customerIdDatatype = new CustomerIdDatatype();
        customerIdDatatype.setDocument("29141411");
        customerIdDatatype.setDocumentType(1);
        when(customerIdManagement.getCustomerIdDatatypeFromCustomerId(anyString())).thenReturn(customerIdDatatype);

        final AccountIdDatatype accountIdDatatype = new AccountIdDatatype();
        accountIdDatatype.setModule(20);
        when(accountIdManagement.getAccountIdDatatypeFromAccountId(anyString())).thenReturn(accountIdDatatype);

        final Map<String, String> propertiesMap = new HashMap<>();
        when(dao.getStaticProperties(anyString())).thenReturn(propertiesMap);

        try (MockedStatic<PDFGenerator> pdfGeneratorMocked = Mockito.mockStatic(PDFGenerator.class)) {
            pdfGeneratorMocked.when(() -> PDFGenerator.fromHTMLtoPDF(anyString(), any(Map.class))).thenReturn(new byte[1]);

            final MetaData metaData = new MetaData();
            final DocumentInfo documentInfo = new DocumentInfo();
            documentInfo.setObjectName("objectName");
            documentInfo.setOwner("owner");
            documentInfo.setResponsible("responsible");
            metaData.setDocumentInfo(documentInfo);
            final Categorization categorization = new Categorization();
            categorization.setDocumentTypeId("ACCEPT_TERMS_AND_CONDITIONS");
            metaData.setCategorization(categorization);
            final SecurityLevelInfo securityLevelInfo = new SecurityLevelInfo();
            securityLevelInfo.setSecurityLevel(1);
            securityLevelInfo.setClassificationLevel(1);
            metaData.setSecurityLevelInfo(securityLevelInfo);
            final List<SpecificMetadata> specificMetatadataList = new ArrayList<>();
            specificMetatadataList.add(specificMetadata("CUSTOMER_ID", FieldTypeEnum.STRING, "29141411"));
            specificMetatadataList.add(specificMetadata("ACCOUNT_ID", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("ACCOUNT_NUMBER", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("DOCUMENT_NUMBER", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("VERSION", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("DATE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("HOUR", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CLIENT_ADDRESS", FieldTypeEnum.STRING, "-"));
            metaData.setSpecificMetadataList(specificMetatadataList);

            final DocumentResponse documentResponse = service.postDocument(null, metaData, true, 7889121, "29122323");

            assertNotNull(documentResponse);
        }
    }

    @Test
    void postDocument_shouldReturnDocument_whenDocumentCreated_loanAckContract() throws ServiceException, IOException, ParseException, CaasException, PDFManageException {

        final NonCustomerIdDatatype customerIdDatatype = new NonCustomerIdDatatype();
        customerIdDatatype.setDocument("29141411");
        customerIdDatatype.setDocumentType(1);
        when(nonCustomerIdManagement.getNonCustomerIdDatatypeFromNonCustomerId(anyString())).thenReturn(customerIdDatatype);

        final AccountIdDatatype accountIdDatatype = new AccountIdDatatype();
        accountIdDatatype.setModule(20);
        when(accountIdManagement.getAccountIdDatatypeFromAccountId(anyString())).thenReturn(accountIdDatatype);

        final Map<String, String> propertiesMap = new HashMap<>();
        when(dao.getStaticProperties(anyString())).thenReturn(propertiesMap);

        try (MockedStatic<PDFGenerator> pdfGeneratorMocked = Mockito.mockStatic(PDFGenerator.class)) {
            pdfGeneratorMocked.when(() -> PDFGenerator.fromHTMLtoPDF(anyString(), any(Map.class))).thenReturn(new byte[1]);

            final MetaData metaData = new MetaData();
            final DocumentInfo documentInfo = new DocumentInfo();
            documentInfo.setObjectName("objectName");
            documentInfo.setOwner("owner");
            documentInfo.setResponsible("responsible");
            metaData.setDocumentInfo(documentInfo);
            final Categorization categorization = new Categorization();
            categorization.setDocumentTypeId("LOAN_ACKNOWLEDGMENT");
            metaData.setCategorization(categorization);
            final SecurityLevelInfo securityLevelInfo = new SecurityLevelInfo();
            securityLevelInfo.setSecurityLevel(1);
            securityLevelInfo.setClassificationLevel(1);
            metaData.setSecurityLevelInfo(securityLevelInfo);
            final List<SpecificMetadata> specificMetatadataList = new ArrayList<>();
            specificMetatadataList.add(specificMetadata("ACCOUNT_ID", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("MUST_SHOW_DRAFT", FieldTypeEnum.STRING, "Si"));
            specificMetatadataList.add(specificMetadata("NON_CUSTOMER_ID", FieldTypeEnum.STRING, "29141411"));
            specificMetatadataList.add(specificMetadata("LOAN_QUANTITY", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("ARRANGEMENT_DATE", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CLIENT_NAME", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("DOCUMENT_NUMBER", FieldTypeEnum.STRING, "-"));
            specificMetatadataList.add(specificMetadata("CLIENT_ADDRESS", FieldTypeEnum.STRING, "-"));
            metaData.setSpecificMetadataList(specificMetatadataList);

            final DocumentResponse documentResponse = service.postDocument(null, metaData, true, 7889121, "29122323");

            assertNotNull(documentResponse);
        }
    }

    @Test
    void postDocument_shouldThrowBusinessException_whenMandatoryFieldsMissing() throws ServiceException, IOException, ParseException {

        final MetaData metaData = new MetaData();
        final MultipartFile multipartFile = mock(MultipartFile.class);
        assertThrows(BusinessException.class, () -> service.postDocument(multipartFile, metaData, true, 7889121, "29122323"));

    }

    @Test
    void postDocument_shouldHandleCaasException_whenCustomerIdDecryptFails() throws CaasException {
        MetaData metaData = mockDocumentData("LOAN_CONTRACT");
        List<SpecificMetadata> specificMetadataList = new ArrayList<>();
        specificMetadataList.add(specificMetadata("CUSTOMER_ID", FieldTypeEnum.STRING, "invalid"));
        specificMetadataList.add(specificMetadata("ACCOUNT_ID", FieldTypeEnum.STRING, "123456"));
        metaData.setSpecificMetadataList(specificMetadataList);

        when(customerIdManagement.getCustomerIdDatatypeFromCustomerId(anyString()))
                .thenThrow(new CaasException("Decryption failed"));
        when(accountIdManagement.getAccountIdDatatypeFromAccountId(anyString()))
                .thenReturn(new AccountIdDatatype());

        assertThrows(ServiceException.class, () ->
                service.postDocument(null, metaData, false, 7889121, "user123"));
    }

    @Test
    void postDocument_shouldThrowBusinessException_whenCustomerIdNullAndNotDraft() throws Exception {
        MetaData metaData = mockDocumentData("CCH_CONTRACT");

        specificMetadataList.add(specificMetadata("NON_CUSTOMER_ID", FieldTypeEnum.STRING, "29141411"));
        metaData.setSpecificMetadataList(specificMetadataList);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.postDocument(null, metaData, false, 12345, "userId"));

        assertEquals("No se están recibiendo los parámetros obligatorios", exception.getMessage());
    }

    @Test
    void patchDocument_shouldHandleEmptySpecificMetadataList() throws ServiceException {
        RepositoryDraftDocument repositoryDocument = new RepositoryDraftDocument();
        FilenetFile filenetFile = new FilenetFile();
        filenetFile.setAccountNumber(9001001);
        repositoryDocument.setFilenetFile(filenetFile);
        when(dao.getDocument(anyString())).thenReturn(repositoryDocument);

        MetaData metaData = new MetaData();
        metaData.setSpecificMetadataList(new ArrayList<>());

        DocumentResponse response = service.patchDocument("docId", metaData);

        assertNotNull(response);
        assertEquals(9001001, filenetFile.getAccountNumber());
    }

    @Test
    void patchDocument_shouldUpdateDocument_whenValidDataIsPassed() throws ServiceException {

        final RepositoryDocument repositoryDocument = new RepositoryDraftDocument();
        final FilenetFile filenetFile = new FilenetFile();
        repositoryDocument.setFilenetFile(filenetFile);
        when(dao.getDocument(anyString())).thenReturn(repositoryDocument);

        final MetaData metaData = new MetaData();
        final List<SpecificMetadata> specificMetadataList = new ArrayList<>();

        specificMetadataList.add(specificMetadata("accountNumber", FieldTypeEnum.STRING, "200001"));
        specificMetadataList.add(specificMetadata("userDocumentNumber", FieldTypeEnum.STRING, "200001"));
        specificMetadataList.add(specificMetadata("userDocumentCountry", FieldTypeEnum.STRING, "845"));
        specificMetadataList.add(specificMetadata("userDocumentType", FieldTypeEnum.STRING, "1"));
        specificMetadataList.add(specificMetadata("documentClass", FieldTypeEnum.STRING, "200001"));
        specificMetadataList.add(specificMetadata("mimeType", FieldTypeEnum.STRING, "200001"));
        specificMetadataList.add(specificMetadata("fileName", FieldTypeEnum.STRING, "200001"));
        specificMetadataList.add(specificMetadata("sendMail", FieldTypeEnum.STRING, "true"));
        specificMetadataList.add(specificMetadata("fileSentToFilenet", FieldTypeEnum.STRING, "true"));
        specificMetadataList.add(specificMetadata("fileSentByMail", FieldTypeEnum.STRING, "true"));
        specificMetadataList.add(specificMetadata("ready", FieldTypeEnum.STRING, "true"));

        metaData.setSpecificMetadataList(specificMetadataList);
        metaData.setId("00000000-0000-0000-0000-000000000000");

        final DocumentResponse documentResponse = service.patchDocument("29122323", metaData);
        assertNotNull(documentResponse);
    }

    @Test
    void deleteDocument_shouldDeleteDocument_whenValidDataIsPassed() throws ServiceException {
        when(dao.deleteDocument(anyString(), anyString())).thenReturn(new Object());

        final Object result = service.deleteDocument("00000000-0000-0000-0000-000000000000", "draft");
        assertNotNull(result);
    }

    @Test
    void deleteDocument_shouldThrowNotFoundException_whenDocumentNotExists() {
        assertThrows(NotFoundException.class, () -> service.deleteDocument("00000000-0000-0000-0000-000000000000", "draft"));
    }

    @Test
    void metadataCustomerBelongToUser_shouldReturnTrue_whenMetadataListHasCustomerId() throws CaasException, ServiceException {
        ReflectionTestUtils.setField(spipDocumentControl, "customerField", "NON_CUSTOMER_ID");

        final NonCustomerIdDatatype customerIdDatatype = new NonCustomerIdDatatype();
        customerIdDatatype.setDocument("29141411");
        customerIdDatatype.setDocumentType(1);

        when(nonCustomerIdManagement.getNonCustomerIdDatatypeFromNonCustomerId(anyString())).thenReturn(customerIdDatatype);

        final MetaData metadata = new MetaData();
        final List<SpecificMetadata> specificMetadataList = new ArrayList<>();

        specificMetadataList.add(specificMetadata("NON_CUSTOMER_ID", FieldTypeEnum.STRING, "customer-id"));
        metadata.setSpecificMetadataList(specificMetadataList);
        assertTrue(spipDocumentControl.metadataCustomerBelongToUser(metadata, "29141411"));
    }

    @Test
    void metadataCustomerBelongToUser_shouldReturnFalse_whenMetadataListNotHasCustomerId() throws CaasException, ServiceException {
        ReflectionTestUtils.setField(spipDocumentControl, "customerField", "NON_CUSTOMER_ID");

        final MetaData metadata = new MetaData();
        final List<SpecificMetadata> specificMetadataList = new ArrayList<>();

        metadata.setSpecificMetadataList(specificMetadataList);
        assertFalse(spipDocumentControl.metadataCustomerBelongToUser(metadata, "29141411"));
    }

    @Test
    void metadataCustomerBelongToUser_shouldReturnFalse_whenMetadataNull() throws CaasException, ServiceException {
        assertFalse(spipDocumentControl.metadataCustomerBelongToUser(null, "29141411"));
    }

    @Test
    void metadataCustomerBelongToUser_shouldReturnFalse_whenMetadataListNull() throws CaasException, ServiceException {
        final MetaData metadata = new MetaData();

        metadata.setSpecificMetadataList(null);
        assertFalse(spipDocumentControl.metadataCustomerBelongToUser(metadata, "29141411"));
    }

    @Test
    void metadataCustomerBelongToUser_shouldThrowServiceException_whenUserIdEmpty() throws CaasException, ServiceException {
        final MetaData metadata = new MetaData();
        assertThrows(ServiceException.class, () -> spipDocumentControl.metadataCustomerBelongToUser(metadata, ""));

    }

    @Test
    void metadataContactDetailsValidation_shouldReturnFalse_whenMetadataListNotHasCustomerId() throws CaasException, ServiceException {
        ReflectionTestUtils.setField(spipDocumentControl, "customerField", "NON_CUSTOMER_ID");
        final MetaData metadata = new MetaData();

        metadata.setSpecificMetadataList(specificMetadataList);
        assertFalse(spipDocumentControl.metadataCustomerBelongToUser(metadata, "29141411"));
    }

    @Test
    void metadataContactDetailsValidation_shouldReturnTrue_whenValidPhoneAndMail() throws CaasException, ServiceException {
        ReflectionTestUtils.setField(spipDocumentControl, "emailMetadataKeyValue", "EMAIL");
        ReflectionTestUtils.setField(spipDocumentControl, "phoneMetadataKeyValue", "MOBILE_NUMBER");

        final ContactDetailDataType contactDetails = new ContactDetailDataType();

        contactDetails.setEmail("test@test.com");
        contactDetails.setMobileNumber("099123456");
        when(dao.getPersonContacts(anyString(), anyInt(), anyInt())).thenReturn(contactDetails);

        final MetaData metadata = new MetaData();
        final List<SpecificMetadata> specificMetadataList = new ArrayList<>();

        specificMetadataList.add(specificMetadata("EMAIL", FieldTypeEnum.STRING, "test@test.com"));
        specificMetadataList.add(specificMetadata("MOBILE_NUMBER", FieldTypeEnum.STRING, "099123456"));
        metadata.setSpecificMetadataList(specificMetadataList);
        assertTrue(spipDocumentControl.metadataContactDetailsValidation("29141411", 1, 845, metadata));


    }

    @Test
    void metadataContactDetailsValidation_shouldReturnFalse_whenInvalidPhoneAndMail() throws CaasException, ServiceException {
        ReflectionTestUtils.setField(spipDocumentControl, "emailMetadataKeyValue", "EMAIL");
        ReflectionTestUtils.setField(spipDocumentControl, "phoneMetadataKeyValue", "MOBILE_NUMBER");

        final ContactDetailDataType contactDetails = new ContactDetailDataType();
        contactDetails.setEmail("test1@test.com");
        contactDetails.setMobileNumber("098123456");

        when(dao.getPersonContacts(anyString(), anyInt(), anyInt())).thenReturn(contactDetails);

        final MetaData metadata = new MetaData();
        final List<SpecificMetadata> specificMetadataList = new ArrayList<>();

        specificMetadataList.add(specificMetadata("EMAIL", FieldTypeEnum.STRING, "test@test.com"));
        specificMetadataList.add(specificMetadata("MOBILE_NUMBER", FieldTypeEnum.STRING, "099123456"));
        metadata.setSpecificMetadataList(specificMetadataList);
        assertFalse(spipDocumentControl.metadataContactDetailsValidation("29141411", 1, 845, metadata));


    }

    @Test
    void metadataContactDetailsValidation_shouldReturnFalse_whenMetadataNull() throws CaasException, ServiceException {
        ReflectionTestUtils.setField(spipDocumentControl, "emailMetadataKeyValue", "EMAIL");
        ReflectionTestUtils.setField(spipDocumentControl, "phoneMetadataKeyValue", "MOBILE_NUMBER");

        final MetaData metadata = new MetaData();
        final List<SpecificMetadata> specificMetadataList = new ArrayList<>();
        metadata.setSpecificMetadataList(specificMetadataList);
        assertFalse(spipDocumentControl.metadataContactDetailsValidation("29141411", 1, 845, metadata));


    }

    @Test
    void metadataContactDetailsValidation_shouldReturnFalse_whenMetadataListNull() throws CaasException, ServiceException {
        final MetaData metadata = new MetaData();
        metadata.setSpecificMetadataList(null);
        assertFalse(spipDocumentControl.metadataContactDetailsValidation("29141411", 1, 845, metadata));
    }

    @Test
    void metadataContactDetailsValidation_shouldThrowServiceException_whenUserIdEmpty() throws CaasException, ServiceException {
        final MetaData metadata = new MetaData();
        assertThrows(ServiceException.class, () -> spipDocumentControl.metadataContactDetailsValidation("", 1, 845, metadata));
    }

    @Test
    void getNonBusinessDataMap_shouldReturnCompleteDataMap() throws ServiceException {
        Map<String, String> businessData = new HashMap<>();
        businessData.put("COMMERCIAL_NAME", "Test Business");
        businessData.put("SOCIAL_NAME", "Test Social Name");
        businessData.put("LEGAL_NATURE", "SA");
        businessData.put("CONSTITUTION_DATE", "01/01/2020");

        Map<String, String> personData = new HashMap<>();
        personData.put("FIRST_NAME", "John");
        personData.put("FIRST_LAST_NAME", "Doe");
        personData.put("BIRTH_DATE", "01/01/1990");
        personData.put("SEX", "M");

        Map<String, String> legalAddress = new HashMap<>();
        legalAddress.put("LEGAL_ADDRESS_COUNTRY", "URUGUAY");
        legalAddress.put("LEGAL_ADDRESS_STATE", "MONTEVIDEO");
        legalAddress.put("LEGAL_ADDRESS_CITY_NEIGHBORHOOD", "CENTRO");
        legalAddress.put("LEGAL_ADDRESS_POSTAL_CODE", "11000");

        Map<String, String> homeAddress = new HashMap<>();
        homeAddress.put("HOME_ADDRESS_COUNTRY", "URUGUAY");
        homeAddress.put("HOME_ADDRESS_STATE", "MONTEVIDEO");
        homeAddress.put("HOME_ADDRESS_CITY_NEIGHBORHOOD", "POCITOS");
        homeAddress.put("HOME_ADDRESS_POSTAL_CODE", "11300");

        when(dao.getNonBusinessDataMap(any(NonBusinessIdDatatype.class))).thenReturn(businessData);
        when(dao.getNonBusinessPersonDataMap(any(NonBusinessIdDatatype.class))).thenReturn(personData);
        when(dao.getNonBusinessLegalAddressMap(any(NonBusinessIdDatatype.class))).thenReturn(legalAddress);
        when(dao.getNonBusinessHomeAddressMap(any(NonBusinessIdDatatype.class))).thenReturn(homeAddress);

        Map<String, String> result = unipContractService.getNonBusinessDataMap(specificMetadataList, nonBusinessIdDatatype);
        assertNotNull(result);

        assertEquals("NO", result.get("THIRD_PARTY_OPERATION"));
        assertEquals("Cuenta Express", result.get("PACKAGE_NAME"));
        assertEquals("Cuenta corriente sin chequera + caja de ahorro", result.get("PACKAGE_DESCRIPTION"));
        assertEquals("Pesos y dolares", result.get("CURRENCY_TYPE"));
        assertEquals("SI", result.get("NET_CHANNEL_CREATED"));
        assertEquals("SI", result.get("DEFAULT_TOKEN"));
        assertEquals("NO", result.get("IS_PEP"));
        assertEquals("NO", result.get("IS_PEP_RELATED"));
        assertEquals("NO", result.get("US_NATIONALITY"));
        assertEquals("NO", result.get("INCOME_SOURCE"));
        assertEquals("100", result.get("BENEFICIARY_PERCENTAGE"));
        assertEquals("DIRECTA", result.get("BENEFICIARY_DIRECT_INDIRECT"));
        assertEquals("URUGUAY", result.get("RESIDENCE_COUNTRY"));

        String datePattern = "\\d{2}/\\d{2}/\\d{4}";
        assertTrue(result.get("DATE").matches(datePattern));

        assertEquals("Test Business", result.get("COMMERCIAL_NAME"));
        assertEquals("Test Social Name", result.get("SOCIAL_NAME"));
        assertEquals("John", result.get("FIRST_NAME"));
        assertEquals("Doe", result.get("FIRST_LAST_NAME"));
        assertEquals("URUGUAY", result.get("LEGAL_ADDRESS_COUNTRY"));
        assertEquals("POCITOS", result.get("HOME_ADDRESS_CITY_NEIGHBORHOOD"));

        verify(dao).getNonBusinessDataMap(nonBusinessIdDatatype);
        verify(dao).getNonBusinessPersonDataMap(nonBusinessIdDatatype);
        verify(dao).getNonBusinessLegalAddressMap(nonBusinessIdDatatype);
        verify(dao).getNonBusinessHomeAddressMap(nonBusinessIdDatatype);

    }

    @Test
    void getNonBusinessDataMap_shouldReturnDefaultValues_whenDaoReturnsEmptyMaps() throws ServiceException {
        when(dao.getNonBusinessDataMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        when(dao.getNonBusinessPersonDataMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        when(dao.getNonBusinessLegalAddressMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        when(dao.getNonBusinessHomeAddressMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());

        Map<String, String> result = unipContractService.getNonBusinessDataMap(specificMetadataList, nonBusinessIdDatatype);

        assertNotNull(result);

        assertEquals("@P1", result.get("ACCOUNT_NUMBER"));
        assertEquals("@P2", result.get("BRANCH_NAME"));
        assertEquals("@P4", result.get("BUSINESS_DOCUMENT_COUNTRY"));
        assertEquals("@P5", result.get("BUSINESS_DOCUMENT_TYPE"));
        assertEquals("@P6", result.get("BUSINESS_DOCUMENT_NUMBER"));
        assertEquals("@P7", result.get("COMMERCIAL_NAME"));
        assertEquals("@P8", result.get("SOCIAL_NAME"));
        assertEquals("NO", result.get("THIRD_PARTY_OPERATION"));
        assertEquals("Cuenta Express", result.get("PACKAGE_NAME"));
        assertEquals("SI", result.get("NET_CHANNEL_CREATED"));
    }

    @Test
    void getNonBusinessDataMap_shouldThrowServiceException_whenDaoThrowsException() throws ServiceException {
        when(dao.getNonBusinessDataMap(any(NonBusinessIdDatatype.class)))
                .thenThrow(new ServiceException("Database error", "DB_ERROR", new Exception()));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> unipContractService.getNonBusinessDataMap(specificMetadataList, nonBusinessIdDatatype));

        assertTrue(exception.getMessage().contains("Database error"));
        verify(dao).getNonBusinessDataMap(nonBusinessIdDatatype);
        verify(dao, never()).getNonBusinessPersonDataMap(any());
        verify(dao, never()).getNonBusinessLegalAddressMap(any());
        verify(dao, never()).getNonBusinessHomeAddressMap(any());

    }

    @Test
    void getNonBusinessDataMap_shouldOverrideValues_whenDaoMapsHaveOverlappingKeys() throws ServiceException {

        Map<String, String> businessData = new HashMap<>();
        businessData.put("CONTACT_EMAIL", "business@test.com");
        businessData.put("CUSTOM_FIELD", "business_value");

        Map<String, String> personData = new HashMap<>();
        personData.put("CONTACT_EMAIL", "person@test.com");
        personData.put("FIRST_NAME", "John");

        Map<String, String> legalAddress = new HashMap<>();
        legalAddress.put("CUSTOM_FIELD", "legal_value");
        legalAddress.put("LEGAL_ADDRESS_COUNTRY", "URUGUAY");

        Map<String, String> homeAddress = new HashMap<>();
        homeAddress.put("CONTACT_EMAIL", "home@test.com");
        homeAddress.put("HOME_ADDRESS_COUNTRY", "ARGENTINA");

        when(dao.getNonBusinessDataMap(any(NonBusinessIdDatatype.class))).thenReturn(businessData);
        when(dao.getNonBusinessPersonDataMap(any(NonBusinessIdDatatype.class))).thenReturn(personData);
        when(dao.getNonBusinessLegalAddressMap(any(NonBusinessIdDatatype.class))).thenReturn(legalAddress);
        when(dao.getNonBusinessHomeAddressMap(any(NonBusinessIdDatatype.class))).thenReturn(homeAddress);

        Map<String, String> result = unipContractService.getNonBusinessDataMap(specificMetadataList, nonBusinessIdDatatype);

        assertEquals("home@test.com", result.get("CONTACT_EMAIL"));
        assertEquals("legal_value", result.get("CUSTOM_FIELD"));
        assertEquals("John", result.get("FIRST_NAME"));
        assertEquals("URUGUAY", result.get("LEGAL_ADDRESS_COUNTRY"));
        assertEquals("ARGENTINA", result.get("HOME_ADDRESS_COUNTRY"));
    }

    @Test
    void getNonBusinessDataMap_shouldHandleNullNonBusinessIdDatatype() throws ServiceException {
        when(dao.getNonBusinessDataMap(null)).thenReturn(new HashMap<>());
        when(dao.getNonBusinessPersonDataMap(null)).thenReturn(new HashMap<>());
        when(dao.getNonBusinessLegalAddressMap(null)).thenReturn(new HashMap<>());
        when(dao.getNonBusinessHomeAddressMap(null)).thenReturn(new HashMap<>());

        Map<String, String> result = unipContractService.getNonBusinessDataMap(specificMetadataList, null);

        assertNotNull(result);
        assertEquals("NO", result.get("THIRD_PARTY_OPERATION"));
        assertEquals("Cuenta Express", result.get("PACKAGE_NAME"));

        verify(dao).getNonBusinessDataMap(null);
        verify(dao).getNonBusinessPersonDataMap(null);
        verify(dao).getNonBusinessLegalAddressMap(null);
        verify(dao).getNonBusinessHomeAddressMap(null);
    }

    @Test
    void getNonBusinessDataMap_shouldWorkWithEmptySpecificMetadataList() throws ServiceException {
        List<SpecificMetadata> emptyList = new ArrayList<>();

        Map<String, String> businessData = new HashMap<>();
        businessData.put("COMMERCIAL_NAME", "Test");

        when(dao.getNonBusinessDataMap(any(NonBusinessIdDatatype.class))).thenReturn(businessData);
        when(dao.getNonBusinessPersonDataMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        when(dao.getNonBusinessLegalAddressMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        when(dao.getNonBusinessHomeAddressMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());

        Map<String, String> result = unipContractService.getNonBusinessDataMap(emptyList, nonBusinessIdDatatype);

        assertNotNull(result);
        assertEquals("Test", result.get("COMMERCIAL_NAME"));
        assertEquals("NO", result.get("THIRD_PARTY_OPERATION"));

    }

    @Test
    void getNonBusinessDataMap_shouldWorkWithNullSpecificMetadataList() throws ServiceException {
        Map<String, String> businessData = new HashMap<>();
        businessData.put("SOCIAL_NAME", "Test Social");

        when(dao.getNonBusinessDataMap(any(NonBusinessIdDatatype.class))).thenReturn(businessData);
        when(dao.getNonBusinessPersonDataMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        when(dao.getNonBusinessLegalAddressMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        when(dao.getNonBusinessHomeAddressMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());

        Map<String, String> result = unipContractService.getNonBusinessDataMap(null, nonBusinessIdDatatype);

        assertNotNull(result);
        assertEquals("Test Social", result.get("SOCIAL_NAME"));
        assertEquals("SI", result.get("NET_CHANNEL_CREATED"));

    }

    @Test
    void getNonBusinessDataMap_shouldSetAllConstantValuesCorrectly() throws ServiceException {
        when(dao.getNonBusinessDataMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        when(dao.getNonBusinessPersonDataMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        when(dao.getNonBusinessLegalAddressMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        when(dao.getNonBusinessHomeAddressMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());

        Map<String, String> result = unipContractService.getNonBusinessDataMap(specificMetadataList, nonBusinessIdDatatype);

        assertEquals("NO", result.get("THIRD_PARTY_OPERATION"));
        assertEquals("Cuenta Express", result.get("PACKAGE_NAME"));
        assertEquals("Cuenta corriente sin chequera + caja de ahorro", result.get("PACKAGE_DESCRIPTION"));
        assertEquals("Pesos y dolares", result.get("CURRENCY_TYPE"));
        assertEquals("SI", result.get("NET_CHANNEL_CREATED"));
        assertEquals("SI", result.get("DEFAULT_TOKEN"));
        assertEquals("NO", result.get("IS_PEP"));
        assertEquals("NO", result.get("IS_PEP_RELATED"));
        assertEquals("NO", result.get("US_NATIONALITY"));
        assertEquals("NO", result.get("INCOME_SOURCE"));
        assertEquals("100", result.get("BENEFICIARY_PERCENTAGE"));
        assertEquals("DIRECTA", result.get("BENEFICIARY_DIRECT_INDIRECT"));
        assertEquals("URUGUAY", result.get("RESIDENCE_COUNTRY"));

    }

    @Test
    void getNonBusinessDataMap_shouldInitializeAllPlaceholders() throws ServiceException {
        when(dao.getNonBusinessDataMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        when(dao.getNonBusinessPersonDataMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        when(dao.getNonBusinessLegalAddressMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        when(dao.getNonBusinessHomeAddressMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());

        Map<String, String> result = unipContractService.getNonBusinessDataMap(specificMetadataList, nonBusinessIdDatatype);

        assertEquals("@P1", result.get("ACCOUNT_NUMBER"));
        assertEquals("@P2", result.get("BRANCH_NAME"));
        assertEquals("@P4", result.get("BUSINESS_DOCUMENT_COUNTRY"));
        assertEquals("@P7", result.get("COMMERCIAL_NAME"));
        assertEquals("@P8", result.get("SOCIAL_NAME"));
        assertEquals("@P9", result.get("LEGAL_NATURE"));
        assertEquals("@P58", result.get("FIRST_LAST_NAME"));
        assertEquals("@P60", result.get("FIRST_NAME"));
        assertEquals("NO", result.get("IS_PEP"));
        assertEquals("NO", result.get("IS_PEP_RELATED"));
        assertEquals("@P22", result.get("LEGAL_ADDRESS_LEVEL1_DESCRIPTION"));
        assertEquals("@P33", result.get("HOME_ADDRESS_LEVEL1_DESCRIPTION"));
        assertEquals("@P70", result.get("SPOUSE_FIRST_LAST_NAME"));
        assertEquals("@P76", result.get("SPOUSE_DOCUMENT_NUMBER"));

    }

    @Test
    void getNonBusinessDataMap_shouldThrowException_whenPersonDataMapThrows() throws ServiceException {
        when(dao.getNonBusinessDataMap(any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        when(dao.getNonBusinessPersonDataMap(any(NonBusinessIdDatatype.class)))
                .thenThrow(new ServiceException("Person data error", "PERSON_ERROR", new Exception()));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> unipContractService.getNonBusinessDataMap(specificMetadataList, nonBusinessIdDatatype));
        assertTrue(exception.getMessage().contains("Person data error"));

        verify(dao).getNonBusinessDataMap(nonBusinessIdDatatype);
        verify(dao).getNonBusinessPersonDataMap(nonBusinessIdDatatype);
        verify(dao, never()).getNonBusinessLegalAddressMap(any());
        verify(dao, never()).getNonBusinessHomeAddressMap(any());

    }
}