package uy.com.bbva.services.nonbusinesses.service.impl;

import com.bbva.secarq.caas2.core.exception.CaasException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.nonbusinessescommons.idmanagement.idmanagement.NonBusinessIdManagement;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.dao.DAO;
import uy.com.bbva.services.nonbusinesses.model.status.Status;
import uy.com.bbva.services.nonbusinesses.service.external.BusinessInformationService;
import uy.com.bbva.services.nonbusinesses.service.external.RisksService;
import uy.com.bbva.services.nonbusinesses.service.utils.AddressUtils;
import uy.com.bbva.services.nonbusinesses.service.utils.validator.NameValidator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite de pruebas de NonBusinessesApiServiceImpl.updateTerms():")
class UpdateTermsTest {

    private NonBusinessesApiServiceImpl nonBusinessesApiService;

    @Mock
    private DAO dao;

    @Mock
    private BusinessInformationService businessInformationService;

    @Mock
    private NonBusinessIdManagement nonBusinessIdManagement;

    @Mock
    private AddressUtils addressUtil;

    @Mock
    private LogUtils logUtils;

    @Mock
    private NameValidator nameValidator;

    @Mock
    private RisksService risksService;

    private NonBusinessIdDatatype mockDatatype;

    private String nonBusinessId = "nonBusinessId";
    private String termId = "TERMS_AND_CONDITIONS_V1";
    private int termVersion = 1;

    @BeforeEach
    void setUp() throws Exception {
        mockDatatype = createDatatype("12345678-9", "4321987-6");

        nonBusinessesApiService = new NonBusinessesApiServiceImpl();

        setField(nonBusinessesApiService, "dao", dao);
        setField(nonBusinessesApiService, "businessInformationService", businessInformationService);
        setField(nonBusinessesApiService, "nonBusinessIdManagement", nonBusinessIdManagement);
        setField(nonBusinessesApiService, "addressUtil", addressUtil);
        setField(nonBusinessesApiService, "logUtils", logUtils);
        setField(nonBusinessesApiService, "nameValidator", nameValidator);
        setField(nonBusinessesApiService, "risksService", risksService);
        setField(nonBusinessesApiService, "validateName", true);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    @DisplayName("Happy Path: actualiza términos exitosamente con todos los parámetros válidos")
    void updateTerms_happyPath_updatesTermsSuccessfully() throws Exception {
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(mockDatatype);
        when(dao.getTermVersion(termId)).thenReturn(termVersion);

        nonBusinessesApiService.updateTerms(nonBusinessId, termId);

        verify(nonBusinessIdManagement).getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId);
        verify(dao).getTermVersion(termId);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(dao).auditStatusChange(eq(mockDatatype), statusCaptor.capture());

        Status capturedStatus = statusCaptor.getValue();
        assertEquals("NB_TYC_OK", capturedStatus.getId());
        assertEquals("Aceptacion del documento: TERMS_AND_CONDITIONS_V1, version: 1", capturedStatus.getMessage());
    }

    @Test
    @DisplayName("Término con versión mayor: audita correctamente con versión actualizada")
    void updateTerms_withHigherVersion_auditsCorrectly() throws Exception {
        int higherVersion = 5;

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(mockDatatype);
        when(dao.getTermVersion(termId)).thenReturn(higherVersion);

        nonBusinessesApiService.updateTerms(nonBusinessId, termId);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(dao).auditStatusChange(eq(mockDatatype), statusCaptor.capture());

        Status capturedStatus = statusCaptor.getValue();
        assertEquals("NB_TYC_OK", capturedStatus.getId());
        assertEquals("Aceptacion del documento: TERMS_AND_CONDITIONS_V1, version: 5", capturedStatus.getMessage());
    }

    @Test
    @DisplayName("Término con versión cero: audita correctamente")
    void updateTerms_withVersionZero_auditsCorrectly() throws Exception {
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(mockDatatype);
        when(dao.getTermVersion(termId)).thenReturn(0);

        nonBusinessesApiService.updateTerms(nonBusinessId, termId);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(dao).auditStatusChange(eq(mockDatatype), statusCaptor.capture());

        Status capturedStatus = statusCaptor.getValue();
        assertEquals("Aceptacion del documento: TERMS_AND_CONDITIONS_V1, version: 0", capturedStatus.getMessage());
    }

    @Test
    @DisplayName("Término con ID diferente: audita con ID correcto")
    void updateTerms_withDifferentTermId_auditsWithCorrectId() throws Exception {
        String differentTermId = "PRIVACY_POLICY_V2";

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(mockDatatype);
        when(dao.getTermVersion(differentTermId)).thenReturn(termVersion);

        nonBusinessesApiService.updateTerms(nonBusinessId, differentTermId);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(dao).auditStatusChange(eq(mockDatatype), statusCaptor.capture());

        Status capturedStatus = statusCaptor.getValue();
        assertEquals("Aceptacion del documento: PRIVACY_POLICY_V2, version: 1", capturedStatus.getMessage());
    }

    @Test
    @DisplayName("Error de desencriptación: lanza ServiceException")
    void updateTerms_decryptionError_throwsServiceException() throws Exception {
        CaasException caasException = new CaasException("Decryption failed");
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenThrow(caasException);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> nonBusinessesApiService.updateTerms(nonBusinessId, termId));

        assertEquals("Decryption error", ex.getInternalMessage());
        assertSame(caasException, ex.getCause());

        verify(dao, never()).getTermVersion(anyString());
        verify(dao, never()).auditStatusChange(any(), any());
    }

    @Test
    @DisplayName("Error al obtener versión del término: propaga excepción")
    void updateTerms_getTermVersionError_propagatesException() throws Exception {
        RuntimeException termVersionException = new RuntimeException("Database error");

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(mockDatatype);
        when(dao.getTermVersion(termId)).thenThrow(termVersionException);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> nonBusinessesApiService.updateTerms(nonBusinessId, termId));

        assertSame(termVersionException, ex);

        verify(dao).getTermVersion(termId);
        verify(dao, never()).auditStatusChange(any(), any());
    }

    @Test
    @DisplayName("Error al auditar cambio de estado: propaga excepción")
    void updateTerms_auditStatusChangeError_propagatesException() throws Exception {
        RuntimeException auditException = new RuntimeException("Audit error");

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(mockDatatype);
        when(dao.getTermVersion(termId)).thenReturn(termVersion);
        doThrow(auditException).when(dao).auditStatusChange(eq(mockDatatype), any(Status.class));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> nonBusinessesApiService.updateTerms(nonBusinessId, termId));

        assertSame(auditException, ex);

        verify(dao).getTermVersion(termId);
        verify(dao).auditStatusChange(eq(mockDatatype), any(Status.class));
    }

    @Test
    @DisplayName("Versión negativa del término: audita correctamente")
    void updateTerms_withNegativeVersion_auditsCorrectly() throws Exception {
        int negativeVersion = -1;

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(mockDatatype);
        when(dao.getTermVersion(termId)).thenReturn(negativeVersion);

        nonBusinessesApiService.updateTerms(nonBusinessId, termId);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(dao).auditStatusChange(eq(mockDatatype), statusCaptor.capture());

        Status capturedStatus = statusCaptor.getValue();
        assertEquals("Aceptacion del documento: TERMS_AND_CONDITIONS_V1, version: -1", capturedStatus.getMessage());
    }

    @Test
    @DisplayName("Término con ID vacío: procesa correctamente")
    void updateTerms_withEmptyTermId_processesCorrectly() throws Exception {
        String emptyTermId = "";

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(mockDatatype);
        when(dao.getTermVersion(emptyTermId)).thenReturn(termVersion);

        nonBusinessesApiService.updateTerms(nonBusinessId, emptyTermId);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(dao).auditStatusChange(eq(mockDatatype), statusCaptor.capture());

        Status capturedStatus = statusCaptor.getValue();
        assertEquals("Aceptacion del documento: , version: 1", capturedStatus.getMessage());
    }

    @Test
    @DisplayName("Término con caracteres especiales en ID: audita correctamente")
    void updateTerms_withSpecialCharactersInTermId_auditsCorrectly() throws Exception {
        String specialTermId = "TERMS_&_CONDITIONS_#1.0";

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(mockDatatype);
        when(dao.getTermVersion(specialTermId)).thenReturn(termVersion);

        nonBusinessesApiService.updateTerms(nonBusinessId, specialTermId);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(dao).auditStatusChange(eq(mockDatatype), statusCaptor.capture());

        Status capturedStatus = statusCaptor.getValue();
        assertEquals("Aceptacion del documento: TERMS_&_CONDITIONS_#1.0, version: 1", capturedStatus.getMessage());
    }

    @Test
    @DisplayName("Múltiples llamadas con mismo nonBusinessId: procesa cada una correctamente")
    void updateTerms_multipleCallsSameNonBusinessId_processesEachCorrectly() throws Exception {
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(mockDatatype);
        when(dao.getTermVersion(anyString())).thenReturn(termVersion);

        nonBusinessesApiService.updateTerms(nonBusinessId, "TERM_1");
        nonBusinessesApiService.updateTerms(nonBusinessId, "TERM_2");

        verify(nonBusinessIdManagement, times(2)).getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId);
        verify(dao, times(2)).auditStatusChange(eq(mockDatatype), any(Status.class));
    }

    @Test
    @DisplayName("Verificar formato del mensaje de auditoría: incluye termId y version correctamente")
    void updateTerms_verifyAuditMessageFormat_includesTermIdAndVersionCorrectly() throws Exception {
        String testTermId = "TEST_TERM";
        int testVersion = 42;

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(mockDatatype);
        when(dao.getTermVersion(testTermId)).thenReturn(testVersion);

        nonBusinessesApiService.updateTerms(nonBusinessId, testTermId);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(dao).auditStatusChange(eq(mockDatatype), statusCaptor.capture());

        Status capturedStatus = statusCaptor.getValue();
        String expectedMessage = String.format("Aceptacion del documento: %s, version: %s", testTermId, testVersion);
        assertEquals(expectedMessage, capturedStatus.getMessage());
    }

    @Test
    @DisplayName("NonBusinessId nulo: lanza NullPointerException en desencriptación")
    void updateTerms_withNullNonBusinessId_throwsNullPointerException() throws Exception {
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(null))
                .thenThrow(new NullPointerException("NonBusinessId cannot be null"));

        assertThrows(NullPointerException.class,
                () -> nonBusinessesApiService.updateTerms(null, termId));

        verify(dao, never()).getTermVersion(anyString());
        verify(dao, never()).auditStatusChange(any(), any());
    }

    @Test
    @DisplayName("TermId nulo: procesa con null en mensaje de auditoría")
    void updateTerms_withNullTermId_processesWithNullInMessage() throws Exception {
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(mockDatatype);
        when(dao.getTermVersion(null)).thenReturn(termVersion);

        nonBusinessesApiService.updateTerms(nonBusinessId, null);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(dao).auditStatusChange(eq(mockDatatype), statusCaptor.capture());

        Status capturedStatus = statusCaptor.getValue();
        assertEquals("Aceptacion del documento: null, version: 1", capturedStatus.getMessage());
    }

    @Test
    @DisplayName("Verificar orden de llamadas: desencriptación, obtener versión, auditar")
    void updateTerms_verifyCallOrder_decryptGetVersionAudit() throws Exception {
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(mockDatatype);
        when(dao.getTermVersion(termId)).thenReturn(termVersion);

        nonBusinessesApiService.updateTerms(nonBusinessId, termId);

        var inOrder = inOrder(nonBusinessIdManagement, dao);
        inOrder.verify(nonBusinessIdManagement).getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId);
        inOrder.verify(dao).getTermVersion(termId);
        inOrder.verify(dao).auditStatusChange(eq(mockDatatype), any(Status.class));
    }

    // Helpers
    private static NonBusinessIdDatatype createDatatype(String businessDoc, String personDoc) {
        NonBusinessIdDatatype datatype = new NonBusinessIdDatatype();
        datatype.setBusinessDocument(businessDoc);
        datatype.setPersonDocument(personDoc);
        return datatype;
    }
}