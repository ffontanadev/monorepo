package uy.com.bbva.services.documents.commons;

import uy.com.bbva.accountscommons.idmanagement.datatypes.AccountIdDatatype;
import uy.com.bbva.customerscommons.dtos.models.IdentityDocument;
import uy.com.bbva.customerscommons.dtos.models.v1.*;
import uy.com.bbva.customerscommons.idmanagement.datatypes.CustomerIdDatatype;
import uy.com.bbva.documentscommons.dtos.models.*;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.documents.model.FileData;
import uy.com.bbva.services.documents.utils.MapTemplateByCardProduct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TestDataFactory - Single source of truth for all shared test data builders and constants.
 * Contains all final constants and static helper methods for creating test data across all test classes.
 */
public final class TestDataFactory {

    private TestDataFactory() {
        // Utility class - prevent instantiation
    }

    // ========== FINAL CONSTANTS ==========

    public static final String DOCUMENT_DB_NAME = "documents";
    public static final String FILENET_DB_NAME = "filenet";

    // ========== DATA BUILDER METHODS ==========

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

    public static AccountIdDatatype createAccountIdDatatype() {
        AccountIdDatatype datatype = new AccountIdDatatype();
        datatype.setAccountNumber(12345);
        return datatype;
    }

    public static CustomerIdDatatype createCustomerIdDatatype() {
        CustomerIdDatatype datatype = new CustomerIdDatatype();
        datatype.setDocument("12345678");
        datatype.setCountry(845);
        datatype.setDocumentType(1);
        return datatype;
    }

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

    public static FileData createFileData() {
        FileData fileData = new FileData();
        fileData.setBase64Data("VGVzdCBkb2N1bWVudCBjb250ZW50"); // "Test document content" in base64
        fileData.setName("test-file.pdf");
        return fileData;
    }

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
        if(valid) {
            idDoc.setExpirationDate("2030-12-31");
        }else{
            idDoc.setExpirationDate(null);
        }
        customer.setIdentityDocuments(List.of(idDoc));

        data.setData(customer);
        return data;
    }

    public static SpecificMetadata specificMetadata(final String name, final FieldTypeEnum type, final String value) {
        final SpecificMetadata specificMetatadata = new SpecificMetadata();
        final Field customerField = new Field();
        customerField.setName(name);
        customerField.setType(type);
        specificMetatadata.setField(customerField);
        specificMetatadata.setValue(value);
        return specificMetatadata;
    }

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
}
