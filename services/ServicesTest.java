package uy.com.bbva.services.documents.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.accountscommons.idmanagement.idmanagement.AccountIdManagement;
import uy.com.bbva.customerscommons.idmanagement.idmanagement.CustomerIdManagement;
import uy.com.bbva.documentscommons.dtos.models.SpecificMetadata;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.noncustomerscommons.idmanagement.idmanagement.NonCustomerIdManagement;
import uy.com.bbva.services.documents.commons.TestDataFactory;
import uy.com.bbva.services.documents.dao.DAO;
import uy.com.bbva.services.documents.external.services.CallCustomersService;
import uy.com.bbva.services.documents.external.services.impl.GDocumentalServiceImpl;
import uy.com.bbva.services.documents.service.impl.GenerateContractApiServiceImpl;
import uy.com.bbva.services.documents.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for service-level integration tests.
 * This class demonstrates the refactored test structure using TestDataFactory
 * instead of inheriting from abstract ServiceTest base class.
 *
 * To add new service tests:
 * 1. Add @InjectMocks for your service under test
 * 2. Use TestDataFactory static methods for creating test data
 * 3. Add any service-specific mocks as needed
 * 4. Initialize mocks and setup in @BeforeEach
 */
@ExtendWith(MockitoExtension.class)
class ServicesTest {

    // Standard mocks for service tests (originally from ServiceTest base class)
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

    // Test data fields
    protected NonBusinessIdDatatype nonBusinessIdDatatype;
    protected List<SpecificMetadata> specificMetadataList;

    @BeforeEach
    void setup() {
        // Initialize spy and setup contract generation services
        generateContractApiService = Mockito.spy(new GenerateContractApiServiceImpl());
        TestDataFactory.setupContractGenerationServices(generateContractApiService, dao);

        // Initialize common test data
        nonBusinessIdDatatype = new NonBusinessIdDatatype();
        nonBusinessIdDatatype.setBusinessCountry(858);
        nonBusinessIdDatatype.setBusinessDocumentType(2);
        nonBusinessIdDatatype.setBusinessDocument("12345678");

        specificMetadataList = new ArrayList<>();
    }

    // Example usage pattern for future tests:
    //
    // @Test
    // void testServiceMethod() throws Exception {
    //     // Arrange - use TestDataFactory for test data
    //     MetaData metaData = TestDataFactory.mockDocumentData("TEST_DOC");
    //     CustomerData customerData = TestDataFactory.mockCustomerData(true);
    //
    //     // Setup mocks
    //     when(dao.getStaticProperties(anyString())).thenReturn(new HashMap<>());
    //
    //     // Act
    //     Result result = yourService.yourMethod(metaData, customerData);
    //
    //     // Assert
    //     assertNotNull(result);
    //     verify(dao).getStaticProperties(anyString());
    // }
}
