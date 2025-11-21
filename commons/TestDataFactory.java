package uy.com.bbva.services.documents.commons;

import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import uy.com.bbva.accountscommons.idmanagement.datatypes.AccountIdDatatype;
import uy.com.bbva.customerscommons.dtos.models.IdentityDocument;
import uy.com.bbva.customerscommons.dtos.models.v1.*;
import uy.com.bbva.customerscommons.idmanagement.datatypes.CustomerIdDatatype;
import uy.com.bbva.documentscommons.dtos.models.*;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.documents.dao.DAO;
import uy.com.bbva.services.documents.model.FileData;
import uy.com.bbva.services.documents.service.impl.*;
import uy.com.bbva.services.documents.utils.MapTemplateByCardProduct;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.mockito.Mockito.when;

/**
 * Central factory for creating test data and mock objects.
 * Replaces shared functionality previously in DAOTest and ServiceTest base classes.
 */
public class TestDataFactory {

    // Constants from DAOTest
    public static final String DOCUMENT_DB_NAME = "documents";
    public static final String FILENET_DB_NAME = "filenet";

    // ========================================
    // Data Builders from DAOTest
    // ========================================

    /**
     * Creates a NonBusinessIdDatatype with test data for business and person documents.
     */
    public static NonBusinessIdDatatype createNonBusinessIdDatatype() {
        NonBusinessIdDatatype datatype = new NonBusinessIdDatatype();
        datatype.setBusinessDocument("12345678");
        datatype.setBusinessCountry(845);
        datatype.setBusinessDocumentType(1);
        datatype.setPersonDocument("87654321");
        datatype.setPersonCountry(858);
        datatype.setPersonDocumentType(2);
        return datatype;
    }

    /**
     * Creates an AccountIdDatatype with test account number.
     */
    public static AccountIdDatatype createAccountIdDatatype() {
        AccountIdDatatype datatype = new AccountIdDatatype();
        datatype.setAccountNumber(12345);
        return datatype;
    }

    /**
     * Creates a CustomerIdDatatype with test customer identification data.
     */
    public static CustomerIdDatatype createCustomerIdDatatype() {
        CustomerIdDatatype datatype = new CustomerIdDatatype();
        datatype.setDocument("12345678");
        datatype.setCountry(845);
        datatype.setDocumentType(1);
        return datatype;
    }

    /**
     * Creates a MetaData object with complete test document metadata.
     */
    public static MetaData createMetaData() {
        MetaData metaData = new MetaData();

        DocumentInfo docInfo = new DocumentInfo();
        docInfo.setObjectName("Test Document");
        docInfo.setOwner("Test Owner");
        docInfo.setResponsible("Test Responsible");
        metaData.setDocumentInfo(docInfo);

        Categorization categorization = new Categorization();
        categorization.setDocumentTypeId("TEST_CONTRACT");
        metaData.setCategorization(categorization);

        SecurityLevelInfo securityInfo = new SecurityLevelInfo();
        securityInfo.setSecurityLevel(1);
        securityInfo.setClassificationLevel(1);
        metaData.setSecurityLevelInfo(securityInfo);

        return metaData;
    }

    /**
     * Creates a FileData object with base64-encoded test content.
     */
    public static FileData createFileData() {
        FileData fileData = new FileData();
        fileData.setBase64Data("VGVzdCBkb2N1bWVudCBjb250ZW50"); // "Test document content" in base64
        fileData.setName("test-file.pdf");
        return fileData;
    }

    /**
     * Sets up mock ResultSet for business data queries.
     *
     * @param resultSet      The mock ResultSet to configure
     * @param withValidData  If true, populates with valid data; if false, uses null/empty values
     */
    public static void setupMockResultSetForBusinessData(ResultSet resultSet, boolean withValidData) throws SQLException {
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

    /**
     * Sets up mock ResultSet for address data queries.
     *
     * @param resultSet The mock ResultSet to configure
     */
    public static void setupMockResultSetForAddressData(ResultSet resultSet) throws SQLException {
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

    /**
     * Sets up mock ResultSet for person data queries.
     *
     * @param resultSet       The mock ResultSet to configure
     * @param withValidData   If true, populates with valid data; if false, uses incomplete data
     * @param withSpouseData  If true, includes spouse information
     */
    public static void setupMockResultSetForPersonData(ResultSet resultSet, boolean withValidData, boolean withSpouseData) throws SQLException {
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

    // ========================================
    // Data Builders from ServiceTest
    // ========================================

    /**
     * Creates a CustomerData object with mock customer information.
     *
     * @param valid If true, creates data with valid expiration date; if false, null expiration
     */
    public static CustomerData mockCustomerData(boolean valid) {
        CustomerData data = new CustomerData();

        Customer customer = new Customer();

        Location location = new Location();
        AddressComponent comp = new AddressComponent();
        comp.setComponentTypes(new String[]{"AVENUE"});
        comp.setName("Fake Avenue");
        location.setAddressComponents(List.of(comp));

        Address addr = new Address();
        addr.setLocation(location);
        customer.setAddresses(List.of(addr));

        EmailContact email = new EmailContact();
        email.setContactDetailType("EMAIL");
        email.setAddress("test@example.com");

        MobileContact mobile = new MobileContact();
        mobile.setContactDetailType("MOBILE");
        mobile.setNumber("123456789");

        ContactDetail emailDetail = new ContactDetail();
        emailDetail.setContact(email);

        ContactDetail mobileDetail = new ContactDetail();
        mobileDetail.setContact(mobile);

        customer.setContactDetails(List.of(emailDetail, mobileDetail));

        IdentityDocument idDoc = new IdentityDocument();
        if (valid) {
            idDoc.setExpirationDate("2030-12-31");
        } else {
            idDoc.setExpirationDate(null);
        }
        customer.setIdentityDocuments(List.of(idDoc));

        data.setData(customer);
        return data;
    }

    /**
     * Creates a SpecificMetadata object with the given field information.
     *
     * @param name  Field name
     * @param type  Field type enum
     * @param value Field value
     */
    public static SpecificMetadata specificMetadata(final String name, final FieldTypeEnum type, final String value) {
        final SpecificMetadata specificMetadata = new SpecificMetadata();
        final Field customerField = new Field();
        customerField.setName(name);
        customerField.setType(type);
        specificMetadata.setField(customerField);
        specificMetadata.setValue(value);
        return specificMetadata;
    }

    /**
     * Creates a MetaData object with document information for the specified document type.
     *
     * @param documentTypeId The document type identifier
     */
    public static MetaData mockDocumentData(String documentTypeId) {
        MetaData metaData = new MetaData();
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setObjectName("objectName");
        documentInfo.setOwner("owner");
        documentInfo.setResponsible("responsible");
        metaData.setDocumentInfo(documentInfo);

        Categorization categorization = new Categorization();
        categorization.setDocumentTypeId(documentTypeId);
        metaData.setCategorization(categorization);

        SecurityLevelInfo securityLevelInfo = new SecurityLevelInfo();
        securityLevelInfo.setSecurityLevel(1);
        securityLevelInfo.setClassificationLevel(1);
        metaData.setSecurityLevelInfo(securityLevelInfo);

        return metaData;
    }

    /**
     * Creates a MapTemplateByCardProduct with test template mappings.
     */
    public static MapTemplateByCardProduct getMapTemplateByCardProduct() {
        final MapTemplateByCardProduct mapTemplateByCardProduct = new MapTemplateByCardProduct();
        final Map<String, Map<String, String>> productTemplateMap = new HashMap<>();
        final Map<String, String> cchMapProduct = new HashMap<>();
        cchMapProduct.put("685", "oc_cch_sm_template.html");
        cchMapProduct.put("689", "oc_cch_fg_template.html");
        cchMapProduct.put("700", "oc_cch_tata_template.html");
        cchMapProduct.put("701", "oc_cch_tata_template.html");
        cchMapProduct.put("default", "oc_cch_template.html");
        productTemplateMap.put("CCH_CONTRACT", cchMapProduct);

        final Map<String, String> accMapProduct = new HashMap<>();
        accMapProduct.put("685", "oc_acc_sm_template.html");
        accMapProduct.put("689", "oc_acc_fg_template.html");
        accMapProduct.put("default", "oc_acc_template.html");
        productTemplateMap.put("ACC_CONTRACT", accMapProduct);

        mapTemplateByCardProduct.setProductTemplateMap(productTemplateMap);
        return mapTemplateByCardProduct;
    }

    /**
     * Sets up contract generation services with ReflectionTestUtils for a GenerateContractApiServiceImpl.
     * This method configures all sub-services needed for contract generation.
     *
     * @param contractService The GenerateContractApiServiceImpl to configure
     * @param dao            The DAO to inject into services that require it
     */
    public static void setupContractGenerationServices(GenerateContractApiServiceImpl contractService, DAO dao) {
        Map<String, Class<?>> serviceMap = new LinkedHashMap<>();
        serviceMap.put("generateLoansContractService", GenerateLoansContractApiServiceImpl.class);
        serviceMap.put("generateCreditCardContractService", GenerateCreditCardContractApiServiceImpl.class);
        serviceMap.put("generateAdditionalCCContractService", GenerateAdditionalCCContractApiServiceImpl.class);
        serviceMap.put("generateFTDContractService", GenerateFTDContractApiServiceImpl.class);
        serviceMap.put("generateAccountContractService", GenerateAccountContractApiServiceImpl.class);
        serviceMap.put("generateIMGDocumentIDApiService", GenerateIMGDocumentIDApiServiceImpl.class);
        serviceMap.put("generateAcceptTermsAndConditionsDocumentApiService",
                GenerateAcceptTermsAndConditionsDocumentApiServiceImpl.class);
        serviceMap.put("generateLoanAcknowledgmentApiService", GenerateLoanAcknowledgmentApiServiceImpl.class);
        serviceMap.put("generateUnipersonalContractApiService", GenerateUnipersonalContractApiServiceImpl.class);

        Set<String> servicesNeedingDao = Set.of("generateLoansContractService");

        serviceMap.forEach((fieldName, serviceClass) -> {
            try {
                Object serviceInstance = serviceClass.getDeclaredConstructor().newInstance();

                if (servicesNeedingDao.contains(fieldName)) {
                    ReflectionTestUtils.setField(serviceInstance, "dao", dao);
                }

                ReflectionTestUtils.setField(contractService, fieldName, serviceInstance);

            } catch (Exception e) {
                throw new RuntimeException("Failed to setup service: " + fieldName, e);
            }
        });
    }
}
