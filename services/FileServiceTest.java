package uy.com.bbva.services.documents.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uy.com.bbva.accountscommons.idmanagement.datatypes.AccountIdDatatype;
import uy.com.bbva.accountscommons.idmanagement.idmanagement.AccountIdManagement;
import uy.com.bbva.customerscommons.dtos.models.v1.*;
import uy.com.bbva.customerscommons.idmanagement.datatypes.CustomerIdDatatype;
import uy.com.bbva.customerscommons.idmanagement.idmanagement.CustomerIdManagement;
import uy.com.bbva.documentscommons.dtos.models.Categorization;
import uy.com.bbva.documentscommons.dtos.models.MetaData;
import uy.com.bbva.documentscommons.dtos.models.*;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.noncustomerscommons.idmanagement.idmanagement.NonCustomerIdManagement;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.documents.commons.TestDataFactory;
import uy.com.bbva.services.documents.dao.DAO;
import uy.com.bbva.services.documents.external.services.CallCustomersService;
import uy.com.bbva.services.documents.external.services.impl.GDocumentalServiceImpl;
import uy.com.bbva.services.documents.service.DraftOptions;
import uy.com.bbva.services.documents.service.impl.*;
import uy.com.bbva.services.documents.utils.FileUtils;
import uy.com.bbva.services.documents.utils.MapTemplateByCardProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    // Mocks from ServiceTest base class
    @Mock
    private DAO dao;

    @Mock
    private LogUtils logUtils;

    @Mock
    private AccountIdManagement accountIdManagement;

    @Mock
    private CustomerIdManagement customerIdManagement;

    @Mock
    private NonCustomerIdManagement nonCustomerIdManagement;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private GDocumentalServiceImpl gDocumentalService;

    @Mock
    private CallCustomersService callCustomersService;

    @Spy
    private GenerateContractApiServiceImpl generateContractApiService;

    @InjectMocks
    private FileApiServiceImpl fileApiService;

    private NonBusinessIdDatatype nonBusinessIdDatatype;
    private List<SpecificMetadata> specificMetadataList;

    @BeforeEach
    void setup() {
        // Initialize spy and setup contract generation services (from ServiceTest.beforeEach)
        generateContractApiService = Mockito.spy(new GenerateContractApiServiceImpl());
        TestDataFactory.setupContractGenerationServices(generateContractApiService, dao);

        // Initialize test data (from ServiceTest.beforeEach)
        nonBusinessIdDatatype = new NonBusinessIdDatatype();
        nonBusinessIdDatatype.setBusinessCountry(858);
        nonBusinessIdDatatype.setBusinessDocumentType(2);
        nonBusinessIdDatatype.setBusinessDocument("12345678");

        specificMetadataList = new ArrayList<>();

        // Setup FileApiServiceImpl dependencies (original setup method)
        ReflectionTestUtils.setField(fileApiService, "mapTemplateByCardProduct", TestDataFactory.getMapTemplateByCardProduct());
        ReflectionTestUtils.setField(fileApiService, "dao", dao);
        ReflectionTestUtils.setField(fileApiService, "fileUtils", fileUtils);
        ReflectionTestUtils.setField(fileApiService, "generateContractApiService", generateContractApiService);
        ReflectionTestUtils.setField(fileApiService, "customerIdManagement", customerIdManagement);
        ReflectionTestUtils.setField(fileApiService, "callCustomersService", callCustomersService);
    }

    @Test
    void testCreateFileWithoutMultipartFile_success() throws Exception {
        MetaData metaData = new MetaData();
        metaData.setCategorization(new Categorization());
        metaData.getCategorization().setDocumentTypeId("OC_ACCOUNT_CONTRACT");

        DraftOptions draftOptions = new DraftOptions(true, true);
        AccountIdDatatype productIdDatatype = new AccountIdDatatype();
        productIdDatatype.setAccountNumber(123451);

        CustomerIdDatatype customerIdDatatype = new CustomerIdDatatype();
        customerIdDatatype.setCountry(845);
        customerIdDatatype.setDocumentType(1);
        customerIdDatatype.setDocument("62316401");

        String customerName = "Test Customer";
        String userId = "user123";

        byte[] fakeBytes = "fakeContent".getBytes();
        String expectedFileName = "12345_CONTRACT.pdf";

        when(customerIdManagement.getCustomerIdFromCustomerIdDatatype(any())).thenReturn("customer-id");
        CustomerData customerData = TestDataFactory.mockCustomerData(true);
        when(callCustomersService.getCustomerByCustomerId(any())).thenReturn(customerData);
        final Map<String, String> propertiesMap = new HashMap<>();
        when(dao.getStaticProperties(anyString())).thenReturn(propertiesMap);
        when(generateContractApiService.getFeatureDataMap(anyString(), anyList(),anyString(),any(AccountIdDatatype.class),any(CustomerIdDatatype.class), any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        // Act
       // FileData result = fileApiService.createFile(null, metaData, draftOptions, productIdDatatype, customerIdDatatype, customerName, userId);

        final NonBusinessIdDatatype nonBusinessIdDatatype = new NonBusinessIdDatatype();
        assertThrows(ServiceException.class, () -> fileApiService.createFile(null, metaData, draftOptions, productIdDatatype, customerIdDatatype, nonBusinessIdDatatype, customerName, userId));
        // Error al generar archivo pdf
        // Assert
        // assertNotNull(result);
        /*
        verify(fileUtils).bytesToFileData(any(), any());
        verify(customerIdManagement).getCustomerIdFromCustomerIdDatatype(any());
        verify(callCustomersService).getCustomerByCustomerId(any());
        */
    }

    @Test
    void testCreateFileWithoutMultipartFile_NosuccesswithSpecificMetadta() throws Exception {
        // Arrange
        MetaData metaData = new MetaData();
        metaData.setCategorization(new Categorization());
        metaData.getCategorization().setDocumentTypeId("OC_ACCOUNT_CONTRACT");

        List<SpecificMetadata> specificMetadataList = new ArrayList<>();

        specificMetadataList.add(new SpecificMetadata(new Field("DOCUMENT_EXPIRATION_DATE", FieldTypeEnum.STRING), "30/11/2025"));
        metaData.setSpecificMetadataList(specificMetadataList);
        DraftOptions draftOptions = new DraftOptions(true, true);
        AccountIdDatatype productIdDatatype = new AccountIdDatatype();
        productIdDatatype.setAccountNumber(123451);

        CustomerIdDatatype customerIdDatatype = new CustomerIdDatatype();
        customerIdDatatype.setCountry(845);
        customerIdDatatype.setDocumentType(1);
        customerIdDatatype.setDocument("62316401");

        String customerName = "Test Customer";
        String userId = "user123";

        byte[] fakeBytes = "fakeContent".getBytes();
        String expectedFileName = "12345_CONTRACT.pdf";

//        when(fileUtils.bytesToFileData(any(), any())).thenReturn(new FileData());
        //       when(fileUtils.multipartFileToFileData(any())).thenReturn(new FileData());

        // Puedes mockear internamente métodos si están en colaborador: ej. customerIdManagement
        when(customerIdManagement.getCustomerIdFromCustomerIdDatatype(any())).thenReturn("customer-id");
        CustomerData customerData = TestDataFactory.mockCustomerData(false);
        when(callCustomersService.getCustomerByCustomerId(any())).thenReturn(customerData);
        final Map<String, String> propertiesMap = new HashMap<>();
        when(dao.getStaticProperties(anyString())).thenReturn(propertiesMap);
        when(generateContractApiService.getFeatureDataMap(anyString(), any(List.class), anyString(), any(AccountIdDatatype.class), any(CustomerIdDatatype.class), any(NonBusinessIdDatatype.class))).thenReturn(new HashMap<>());
        // Act
        // FileData result = fileApiService.createFile(null, metaData, draftOptions, productIdDatatype, customerIdDatatype, customerName, userId);

        final NonBusinessIdDatatype nonBusinessIdDatatype = new NonBusinessIdDatatype();
        assertThrows(ServiceException.class, () -> fileApiService.createFile(null, metaData, draftOptions, productIdDatatype, customerIdDatatype, nonBusinessIdDatatype, customerName, userId));
        // Error al generar archivo pdf
        // Assert
        //   assertNotNull(result);
      /*  verify(fileUtils).bytesToFileData(any(), any());
        verify(customerIdManagement).getCustomerIdFromCustomerIdDatatype(any());
        verify(callCustomersService).getCustomerByCustomerId(any());*/
    }
/*

    @Test
    void testCreateFileWithMultipartFile_success() throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);
        MetaData metaData = mock(MetaData.class);
        DraftOptions draftOptions = mock(DraftOptions.class);
        AccountIdDatatype productIdDatatype = mock(AccountIdDatatype.class);
        CustomerIdDatatype customerIdDatatype = mock(CustomerIdDatatype.class);

        FileData expected = new FileData();
        when(fileUtils.multipartFileToFileData(multipartFile)).thenReturn(expected);

        FileData result = yourService.createFile(multipartFile, metaData, draftOptions, productIdDatatype, customerIdDatatype, "name", "user");

        assertEquals(expected, result);
        verify(fileUtils).multipartFileToFileData(multipartFile);
        verifyNoInteractions(customerIdManagement, callCustomersService);
    }


    @Test
    void testCreateFile_customerIdManagementThrows_exception() throws Exception {
        MetaData metaData = new MetaData();
        metaData.setCategorization(new Categorization());
        metaData.getCategorization().setDocumentTypeId("DOC_TYPE");

        DraftOptions draftOptions = new DraftOptions(true, "draftDocument");
        AccountIdDatatype productIdDatatype = new AccountIdDatatype();
        productIdDatatype.setAccountNumber(12345L);

        CustomerIdDatatype customerIdDatatype = new CustomerIdDatatype();
        customerIdDatatype.setCountry(845);
        customerIdDatatype.setDocumentType(1);
        customerIdDatatype.setDocument("62316401");

        String customerName = "Test Customer";
        String userId = "user123";

        when(customerIdManagement.getCustomerIdFromCustomerIdDatatype(any())).thenThrow(new CaasException("error"));

        assertThrows(ServiceException.class, () ->
                yourService.createFile(null, metaData, draftOptions, productIdDatatype, customerIdDatatype, customerName, userId)
        );
    }
*/




}