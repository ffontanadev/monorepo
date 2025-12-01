package uy.com.bbva.services.nonbusinesses.service.impl;

import com.bbva.secarq.caas2.core.exception.CaasException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.dtos.commons.utils.Constants;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.nonbusinessescommons.idmanagement.idmanagement.NonBusinessIdManagement;
import uy.com.bbva.services.commons.exceptions.BusinessException;
import uy.com.bbva.services.nonbusinesses.dao.DAO;
import uy.com.bbva.services.nonbusinesses.model.status.Status;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uy.com.bbva.dtos.commons.utils.Constants.RUT_DOCUMENT_TYPE;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite de pruebas de NonBusinessesApiServiceImpl.postNonBusiness():")
class PostNonBusinessTest {

    @Mock
    private DAO dao;

    @Mock
    private NonBusinessIdManagement nonBusinessIdManagement;

    @InjectMocks
    private NonBusinessesApiServiceImpl nonBusinessesApiService;

    @Test
    @DisplayName("Final state: lanza BusinessException y no llama a DAO.createNonBusiness")
    void postNonBusiness_finalState_throwsBusinessException() throws Exception {
        String rut = "001100110011";
        String documento = "43219876";
        String celular = "099111222";

        when(dao.getStatus(documento, rut)).thenReturn("PROCESADO");

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> nonBusinessesApiService.postNonBusiness(rut, documento, celular)
        );

        assertEquals("La gestion se encuentra en un estado final", ex.getMessage());
        assertEquals("NON_BUSINESS_POST_USER_ON_FINAL_STATE", ex.getInternCode());

        ArgumentCaptor<NonBusinessIdDatatype> datatypeCaptor = ArgumentCaptor.forClass(NonBusinessIdDatatype.class);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(dao, times(1)).auditStatusChange(datatypeCaptor.capture(), statusCaptor.capture());

        assertEquals("NB_ESI_ERR", statusCaptor.getValue().getId());

        verify(dao, never()).createNonBusiness(anyString(), anyString(), anyString());
        verify(dao, times(1)).getStatus(documento, rut);
    }

    @Test
    @DisplayName("Happy path: crea un nuevo non-business, construye datatype correctamente")
    void postNonBusiness_happyPath_returnsIdAndMapsDatatype() throws Exception {
        String rut = "123456789";
        String ownerDocument = "43219876";
        String cellphone = "099111222";
        String expectedId = "NBID-abc-123";

        when(dao.getStatus(ownerDocument, rut)).thenReturn("");

        ArgumentCaptor<NonBusinessIdDatatype> datatypeCaptor =
                ArgumentCaptor.forClass(NonBusinessIdDatatype.class);

        when(nonBusinessIdManagement.getNonBusinessIdFromNonBusinessIdDatatype(datatypeCaptor.capture()))
                .thenReturn(expectedId);

        String result = nonBusinessesApiService.postNonBusiness(rut, ownerDocument, cellphone);

        assertEquals(expectedId, result);

        verify(dao, times(1)).getStatus(ownerDocument, rut);
        verify(dao, times(1)).createNonBusiness(rut, ownerDocument, cellphone);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(dao, times(1)).auditStatusChange(any(NonBusinessIdDatatype.class), statusCaptor.capture());
        assertEquals("INGRESO", statusCaptor.getValue().getId());

        NonBusinessIdDatatype sent = datatypeCaptor.getValue();
        assertNotNull(sent, "NonBusinessIdDatatype no puede ser null");

        assertEquals(Constants.UY_COUNTRY_CODE, sent.getBusinessCountry(), "businessCountry debe ser 'UY' ");
        assertEquals(Constants.UY_COUNTRY_CODE, sent.getPersonCountry(), "personCountry debe ser 'UY'");
        assertEquals(rut, sent.getBusinessDocument(), "businessDocument debe ser igual al de entrada");
        assertEquals(ownerDocument, sent.getPersonDocument(), "personDocument must equal input ownerDocument");
        assertEquals(1, sent.getPersonDocumentType(), "personDocumentType must be CI_DOCUMENT_TYPE (=1)");
        assertEquals(Integer.valueOf(RUT_DOCUMENT_TYPE), sent.getBusinessDocumentType());

        verify(nonBusinessIdManagement, times(1))
                .getNonBusinessIdFromNonBusinessIdDatatype(any(NonBusinessIdDatatype.class));
    }

    @Test
    @DisplayName("Resume state: actualiza status a RETOMA cuando el estado actual es INGRESO")
    void postNonBusiness_resumeState_updatesStatusToRetoma() throws Exception {
        String rut = "123456789";
        String ownerDocument = "43219876";
        String cellphone = "099111222";
        String expectedId = "NBID-abc-123";

        when(dao.getStatus(ownerDocument, rut)).thenReturn("INGRESO");
        when(nonBusinessIdManagement.getNonBusinessIdFromNonBusinessIdDatatype(any()))
                .thenReturn(expectedId);

        String result = nonBusinessesApiService.postNonBusiness(rut, ownerDocument, cellphone);

        assertEquals(expectedId, result);

        verify(dao, times(1)).updateStatus(any(NonBusinessIdDatatype.class), eq("RETOMA"));
        verify(dao, times(1)).createNonBusiness(rut, ownerDocument, cellphone);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(dao, times(1)).auditStatusChange(any(NonBusinessIdDatatype.class), statusCaptor.capture());
        assertEquals("RETOMA", statusCaptor.getValue().getId());
    }

    @Test
    @DisplayName("Validación de RUT: lanza BusinessException cuando el RUT no es numérico")
    void postNonBusiness_invalidRut_throwsBusinessException() throws Exception {
        String rut = "ABC123";
        String ownerDocument = "43219876";
        String cellphone = "099111222";

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> nonBusinessesApiService.postNonBusiness(rut, ownerDocument, cellphone)
        );

        assertEquals("NON_BUSINESS_POST_RUT_NOT_NUMERIC", ex.getInternCode());
        verify(dao, never()).getStatus(anyString(), anyString());
        verify(dao, never()).createNonBusiness(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Validación de CI: lanza BusinessException cuando el documento no es numérico")
    void postNonBusiness_invalidDocument_throwsBusinessException() throws Exception {
        String rut = "123456789";
        String ownerDocument = "4.321.987-6";
        String cellphone = "099111222";

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> nonBusinessesApiService.postNonBusiness(rut, ownerDocument, cellphone)
        );

        assertEquals("NON_BUSINESS_POST_CI_NOT_NUMERIC", ex.getInternCode());
        verify(dao, never()).getStatus(anyString(), anyString());
        verify(dao, never()).createNonBusiness(anyString(), anyString(), anyString());
    }
}