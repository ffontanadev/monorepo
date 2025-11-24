package uy.com.bbva.services.nonbusinesses.service.impl;

import com.bbva.secarq.caas2.core.exception.CaasException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.common.ServiceTest;
import uy.com.bbva.services.nonbusinesses.model.DataNonBusiness;
import uy.com.bbva.services.nonbusinesses.model.NonBusiness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite de pruebas de NonBusinessesApiServiceImpl.getNonBusinessById():")
class GetNonBusinessByIdTest extends ServiceTest {

    @InjectMocks
    private NonBusinessesApiServiceImpl nonBusinessesApiService;

    private final String nonBusinessId = NON_BUSINESS_ID_VALID;

    @Test
    @DisplayName("Happy path: retorna DataNonBusiness con datos básicos sin expansión")
    void getNonBusinessById_happyPath_returnsBasicData() throws Exception {
        String expand = "";

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        NonBusiness nonBusiness = new NonBusiness();
        nonBusiness.setDoingBusinessAs("Test Business");
        when(dao.getNonBusiness(datatype, false, false)).thenReturn(nonBusiness);

        DataNonBusiness result = nonBusinessesApiService.getNonBusinessById(nonBusinessId, expand);

        assertNotNull(result, "DataNonBusiness should not be null");
        assertNotNull(result.getData(), "NonBusiness data should not be null");
        assertEquals(nonBusinessId, result.getData().getId(), "ID should be set on the returned NonBusiness");
        assertEquals("Test Business", result.getData().getDoingBusinessAs());

        verify(nonBusinessIdManagement).getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId);
        verify(dao).getNonBusiness(datatype, false, false);
    }

    @Test
    @DisplayName("Expansión LEGAL-REPRESENTATIVES: incluye datos del propietario")
    void getNonBusinessById_expandLegalRepresentatives_includesOwnerData() throws Exception {
        String expand = EXPAND_OPTIONS.LR.toString();

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        NonBusiness nonBusiness = new NonBusiness();
        when(dao.getNonBusiness(datatype, true, false)).thenReturn(nonBusiness);

        DataNonBusiness result = nonBusinessesApiService.getNonBusinessById(nonBusinessId, expand);

        assertNotNull(result);
        assertEquals(nonBusinessId, result.getData().getId());

        ArgumentCaptor<Boolean> includeOwnerCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Boolean> includeContactCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(dao).getNonBusiness(eq(datatype), includeOwnerCaptor.capture(), includeContactCaptor.capture());

        assertTrue(includeOwnerCaptor.getValue(), "Should include owner data when LEGAL-REPRESENTATIVES is expanded");
        assertFalse(includeContactCaptor.getValue(), "Should not include contact details");
    }

    @Test
    @DisplayName("Expansión CONTACT-DETAILS: incluye detalles de contacto")
    void getNonBusinessById_expandContactDetails_includesContactData() throws Exception {
        String expand = EXPAND_OPTIONS.CD.toString();

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        NonBusiness nonBusiness = new NonBusiness();
        when(dao.getNonBusiness(datatype, false, true)).thenReturn(nonBusiness);

        DataNonBusiness result = nonBusinessesApiService.getNonBusinessById(nonBusinessId, expand);

        assertNotNull(result);
        assertEquals(nonBusinessId, result.getData().getId());

        ArgumentCaptor<Boolean> includeOwnerCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Boolean> includeContactCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(dao).getNonBusiness(eq(datatype), includeOwnerCaptor.capture(), includeContactCaptor.capture());

        assertFalse(includeOwnerCaptor.getValue(), "Should not include owner data");
        assertTrue(includeContactCaptor.getValue(), "Should include contact details when CONTACT-DETAILS is expanded");
    }

    @Test
    @DisplayName("Expansión múltiple: incluye ambos tipos de datos")
    void getNonBusinessById_expandBoth_includesBothDataTypes() throws Exception {
        String expand = EXPAND_OPTIONS.concat(EXPAND_OPTIONS.LR, EXPAND_OPTIONS.CD);

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        NonBusiness nonBusiness = new NonBusiness();
        when(dao.getNonBusiness(datatype, true, true)).thenReturn(nonBusiness);

        DataNonBusiness result = nonBusinessesApiService.getNonBusinessById(nonBusinessId, expand);

        assertNotNull(result);
        assertEquals(nonBusinessId, result.getData().getId());

        verify(dao).getNonBusiness(datatype, true, true);
    }

    @Test
    @DisplayName("Expansión case-insensitive: debe funcionar con minúsculas")
    void getNonBusinessById_expandCaseInsensitive_works() throws Exception {
        String expand = EXPAND_OPTIONS.concat(EXPAND_OPTIONS.LR, EXPAND_OPTIONS.CD);

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        NonBusiness nonBusiness = new NonBusiness();
        when(dao.getNonBusiness(datatype, true, true)).thenReturn(nonBusiness);

        DataNonBusiness result = nonBusinessesApiService.getNonBusinessById(nonBusinessId, expand);

        assertNotNull(result);
        verify(dao).getNonBusiness(datatype, true, true);
    }

    @Test
    @DisplayName("Error de desencriptación: lanza ServiceException")
    void getNonBusinessById_decryptionError_throwsServiceException() throws Exception {
        String expand = "";

        CaasException caasException = new CaasException(ERROR_DECRYPTION);
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenThrow(caasException);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> nonBusinessesApiService.getNonBusinessById(nonBusinessId, expand));

        assertEquals(ERROR_DECRYPTION, ex.getInternalMessage());
        assertEquals(NonBusinessesApiServiceImpl.class.getName(), ex.getMessage());
        assertSame(caasException, ex.getCause());

        verify(dao, never()).getNonBusiness(any(), anyBoolean(), anyBoolean());
    }

    @Test
    @DisplayName("ID nulo: propaga el error del manager")
    void getNonBusinessById_nullId_propagatesError() throws Exception {
        String expand = "";

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(null))
                .thenThrow(new NullPointerException("ID cannot be null"));

        assertThrows(NullPointerException.class,
                () -> nonBusinessesApiService.getNonBusinessById(null, expand));

        verify(dao, never()).getNonBusiness(any(), anyBoolean(), anyBoolean());
    }
}