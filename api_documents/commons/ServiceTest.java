package uy.com.bbva.services.documents.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uy.com.bbva.accountscommons.idmanagement.idmanagement.AccountIdManagement;
import uy.com.bbva.customerscommons.dtos.models.IdentityDocument;
import uy.com.bbva.customerscommons.dtos.models.v1.*;
import uy.com.bbva.customerscommons.idmanagement.idmanagement.CustomerIdManagement;
import uy.com.bbva.documentscommons.dtos.models.*;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.noncustomerscommons.idmanagement.idmanagement.NonCustomerIdManagement;
import uy.com.bbva.pdf.management.commons.PDFGenerator;
import uy.com.bbva.services.documents.dao.DAO;
import uy.com.bbva.services.documents.external.services.CallCustomersService;
import uy.com.bbva.services.documents.external.services.impl.GDocumentalServiceImpl;
import uy.com.bbva.services.documents.service.impl.*;
import uy.com.bbva.services.documents.utils.FileUtils;
import uy.com.bbva.services.documents.utils.MapTemplateByCardProduct;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public abstract class ServiceTest {
    @Mock
    protected DAO dao;

    @Mock
    protected LogUtils logUtils;

    @Mock
    protected AccountIdManagement accountIdManagement;

    @Mock
    protected CustomerIdManagement customerIdManagement;

    @Mock
    protected NonCustomerIdManagement nonCustomerIdManagement;

    @Mock
    protected FileUtils fileUtils;

    @Mock
    protected GDocumentalServiceImpl gDocumentalService;

    @Mock
    protected CallCustomersService callCustomersService;

    @Spy
    protected GenerateContractApiServiceImpl generateContractApiService;

    protected NonBusinessIdDatatype nonBusinessIdDatatype;
    protected List<SpecificMetadata> specificMetadataList;

    @BeforeEach
    protected void beforeEach() {
        generateContractApiService = Mockito.spy(new GenerateContractApiServiceImpl());
        setupContractGenerationServices(generateContractApiService, dao);

        nonBusinessIdDatatype = new NonBusinessIdDatatype();
        nonBusinessIdDatatype.setBusinessCountry(858);
        nonBusinessIdDatatype.setBusinessDocumentType(2);
        nonBusinessIdDatatype.setBusinessDocument("12345678");

        specificMetadataList = new ArrayList<>();
    }

    protected void setupContractGenerationServices(GenerateContractApiServiceImpl contractService, DAO dao) {
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

    protected FileApiServiceImpl setupFileApiService() {
        FileApiServiceImpl fileApiService = new FileApiServiceImpl();

        ReflectionTestUtils.setField(fileApiService, "mapTemplateByCardProduct", getMapTemplateByCardProduct());
        ReflectionTestUtils.setField(fileApiService, "fileUtils", fileUtils);
        ReflectionTestUtils.setField(fileApiService, "dao", dao);
        ReflectionTestUtils.setField(fileApiService, "generateContractApiService", generateContractApiService);

        return fileApiService;
    }

    protected CustomerData mockCustomerData(boolean valid) {
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

    protected SpecificMetadata specificMetadata(final String name, final FieldTypeEnum type, final String value) {
        final SpecificMetadata specificMetatadata = new SpecificMetadata();
        final Field customerField = new Field();
        customerField.setName(name);
        customerField.setType(type);
        specificMetatadata.setField(customerField);
        specificMetatadata.setValue(value);
        return specificMetatadata;
    }

    protected MetaData mockDocumentData(String documentTypeId) {
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

    protected static MapTemplateByCardProduct getMapTemplateByCardProduct() {
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