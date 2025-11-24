package uy.com.bbva.services.nonbusinesses.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uy.com.bbva.dtos.commons.model.DataList;
import uy.com.bbva.dtos.commons.v1.model.RelatedPerson;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.commons.exceptions.BusinessException;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.dao.DAO;
import uy.com.bbva.services.nonbusinesses.model.NonBusinessSearch;
import uy.com.bbva.services.nonbusinesses.model.external.BusinessInformation;
import uy.com.bbva.services.nonbusinesses.model.status.Status;
import uy.com.bbva.services.nonbusinesses.service.external.BusinessInformationService;
import uy.com.bbva.services.nonbusinesses.service.utils.validator.NameValidator;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NonBusinessesApiServiceImpl - search() method tests")
class SearchTests {

    @Mock
    private DAO dao;

    @Mock
    private LogUtils logUtils;

    @Mock
    private BusinessInformationService businessInformationService;

    @Mock
    private NameValidator nameValidator;

    @InjectMocks
    private NonBusinessesApiServiceImpl service;

    private NonBusinessSearch validSearch;
    private BusinessInformation validBusinessInfo;
    private RelatedPerson validOwner;

    private static final String ERROR_INVALID_USER_ID = "El user-id es invalido";
    private static final String ERROR_INVALID_STATE = "El estado actual no es el correcto";
    private static final String ERROR_DOCUMENT_MISMATCH = "Los documentos no coinciden";
    private static final String ERROR_DOCUMENT_NOT_ADMITTED = "El rut no corresponde a una empresa unipersonal";
    private static final String ERROR_EXPIRED_DGI_CERT = "La empresa tiene el certificado vencido en DGI";

    @BeforeEach
    void setUp() {
        validSearch = createNonBusinessSearch();
        validBusinessInfo = createBusinessInformationWithExpiration(
                CERTIFICATE_EXPIRATION_VALID);
        validOwner = createRelatedPerson();

        ReflectionTestUtils.setField(service, "validateName", true);
    }

    @Test
    void search_Success_ReturnsDataList() throws Exception {

        when(dao.checkIsClient(RUT_FROM_USER_ID)).thenReturn(false);
        when(dao.getStatus(OWNER_DOCUMENT_VALID, RUT_FROM_USER_ID)).thenReturn(STATUS_VALID);
        when(businessInformationService.getBusinessInformation(RUT_FROM_USER_ID)).thenReturn(validBusinessInfo);
        when(dao.getOwner(any(NonBusinessIdDatatype.class))).thenReturn(validOwner);
        when(nameValidator.validate(validBusinessInfo.getName())).thenReturn(true);
        when(nameValidator.similarName(validOwner, validBusinessInfo.getName())).thenReturn(true);

        DataList result = service.search(USER_ID_VALID, validSearch);

        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotEmpty();
        assertThat(result.getData()).hasSize(1);

        verify(dao).updateBusinessInformation(validBusinessInfo);
    }

    @Test
    void search_EmptyUserId_ThrowsServiceException() {
        String emptyUserId = USER_ID_INVALID_EMPTY;

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.search(emptyUserId, validSearch));

        assertEquals(ERROR_INVALID_USER_ID+emptyUserId, ex.getInternalMessage());
    }

    @Test
    void search_SinglePartUserId_ThrowsServiceException() {
        String singlePartUserId = USER_ID_INVALID_SINGLE_PART;

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.search(singlePartUserId, validSearch));

        assertEquals(ERROR_INVALID_USER_ID+singlePartUserId, ex.getInternalMessage());
    }

    @Test
    void search_DocumentMismatch_ThrowsServiceException() {
        NonBusinessSearch searchWithDifferentRut = createNonBusinessSearch(
                RUT_MISMATCH);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.search(USER_ID_VALID, searchWithDifferentRut));

        assertEquals(ERROR_DOCUMENT_MISMATCH, ex.getInternalMessage());

    }

    @Test
    void search_RutAlreadyClient_ThrowsBusinessException() throws Exception {
        when(dao.checkIsClient(RUT_FROM_USER_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.search(USER_ID_VALID, validSearch))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("El rut pertenece a un cliente del banco")
                .extracting("internCode").isEqualTo("NON_BUSINESS_SEARCH_ERROR_ALREADY_CLIENT");

        verify(dao).auditStatusChange(any(NonBusinessIdDatatype.class), argThat(status ->
                "NB_CLI_ERR".equals(status.getId())
        ));
    }

    @Test
    void search_StatusProcessado_ThrowsBusinessException() throws Exception {
        when(dao.checkIsClient(RUT_FROM_USER_ID)).thenReturn(false);
        when(dao.getStatus(OWNER_DOCUMENT_VALID, RUT_FROM_USER_ID))
                .thenReturn(STATUS_ID_PROCESADO);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.search(USER_ID_VALID, validSearch));

        assertEquals(ERROR_INVALID_STATE, ex.getInternalMessage());

        verify(dao).auditStatusChange(any(NonBusinessIdDatatype.class), argThat(status ->
                "NB_ESI_ERR".equals(status.getId())
        ));
    }

    @Test
    void search_StatusAnulado_ThrowsBusinessException() throws Exception {
        when(dao.checkIsClient(RUT_FROM_USER_ID)).thenReturn(false);
        when(dao.getStatus(OWNER_DOCUMENT_VALID, RUT_FROM_USER_ID))
                .thenReturn(STATUS_ID_ANULADO);

        assertThatThrownBy(() -> service.search(USER_ID_VALID, validSearch))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("El estado actual no es el correcto");
    }

    @Test
    void search_StatusIngreso_ThrowsBusinessException() throws Exception {
        when(dao.checkIsClient(RUT_FROM_USER_ID)).thenReturn(false);
        when(dao.getStatus(OWNER_DOCUMENT_VALID, RUT_FROM_USER_ID))
                .thenReturn(STATUS_ID_INGRESO);

        assertThatThrownBy(() -> service.search(USER_ID_VALID, validSearch))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("El estado actual no es el correcto");
    }

    @Test
    void search_ExpiredCertificate_ThrowsBusinessException() throws Exception {
        BusinessInformation expiredInfo = createBusinessInformationWithExpiration(
                CERTIFICATE_EXPIRATION_EXPIRED);

        when(dao.checkIsClient(RUT_FROM_USER_ID)).thenReturn(false);
        when(dao.getStatus(OWNER_DOCUMENT_VALID, RUT_FROM_USER_ID))
                .thenReturn(STATUS_VALID);
        when(businessInformationService.getBusinessInformation(RUT_FROM_USER_ID))
                .thenReturn(expiredInfo);

        assertThatThrownBy(() -> service.search(USER_ID_VALID, validSearch))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ERROR_EXPIRED_DGI_CERT);

        verify(dao).auditStatusChange(any(NonBusinessIdDatatype.class), argThat(status ->
                STATUS_ID_DGI_CERT_ERROR.equals(status.getId())
        ));
    }

    @Test
    void search_InvalidBusinessName_ThrowsBusinessException() throws Exception {
        when(dao.checkIsClient(RUT_FROM_USER_ID)).thenReturn(false);
        when(dao.getStatus(OWNER_DOCUMENT_VALID, RUT_FROM_USER_ID)).thenReturn(STATUS_VALID);
        when(businessInformationService.getBusinessInformation(RUT_FROM_USER_ID)).thenReturn(validBusinessInfo);
        when(nameValidator.validate(validBusinessInfo.getName())).thenReturn(false);

        assertThatThrownBy(() -> service.search(USER_ID_VALID, validSearch))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ERROR_DOCUMENT_NOT_ADMITTED);

        verify(dao).auditStatusChange(any(NonBusinessIdDatatype.class), argThat(status ->
                STATUS_ID_DGI_NOT_UNIP_ERROR.equals(status.getId())
        ));
    }

    @Test
    void search_NameNotSimilar_ThrowsBusinessException() throws Exception {

        when(dao.checkIsClient(RUT_FROM_USER_ID)).thenReturn(false);
        when(dao.getStatus(OWNER_DOCUMENT_VALID, RUT_FROM_USER_ID)).thenReturn(STATUS_VALID);
        when(businessInformationService.getBusinessInformation(RUT_FROM_USER_ID)).thenReturn(validBusinessInfo);
        when(dao.getOwner(any(NonBusinessIdDatatype.class))).thenReturn(validOwner);
        when(nameValidator.validate(validBusinessInfo.getName())).thenReturn(true);
        when(nameValidator.similarName(validOwner, validBusinessInfo.getName())).thenReturn(false);

        assertThatThrownBy(() -> service.search(USER_ID_VALID, validSearch))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ERROR_DOCUMENT_NOT_ADMITTED);

        verify(dao).auditStatusChange(any(NonBusinessIdDatatype.class), argThat(status -> STATUS_ID_DGI_ERROR.equals(status.getId())
        ));
    }

    @Test
    void search_ValidateNameFalse_SkipsNameValidations() throws Exception {
        ReflectionTestUtils.setField(service, "validateName", false);

        when(dao.checkIsClient(RUT_FROM_USER_ID)).thenReturn(false);
        when(dao.getStatus(OWNER_DOCUMENT_VALID, RUT_FROM_USER_ID)).thenReturn(STATUS_VALID);
        when(businessInformationService.getBusinessInformation(RUT_FROM_USER_ID)).thenReturn(validBusinessInfo);

        DataList result = service.search(USER_ID_VALID, validSearch);

        verify(nameValidator, never()).validate(anyString());
        verify(nameValidator, never()).similarName(any(), anyString());
        assertThat(result).isNotNull();
    }

    @Test
    void search_NullExpirationDate_ContinuesSuccessfully() throws Exception {
        BusinessInformation infoWithInvalidDate = createBusinessInformationWithExpiration(
                CERTIFICATE_EXPIRATION_INVALID_FORMAT);

        when(dao.checkIsClient(RUT_FROM_USER_ID)).thenReturn(false);
        when(dao.getStatus(OWNER_DOCUMENT_VALID, RUT_FROM_USER_ID)).thenReturn(STATUS_VALID);
        when(businessInformationService.getBusinessInformation(RUT_FROM_USER_ID)).thenReturn(infoWithInvalidDate);
        when(dao.getOwner(any(NonBusinessIdDatatype.class))).thenReturn(validOwner);
        when(nameValidator.validate(anyString())).thenReturn(true);
        when(nameValidator.similarName(any(), anyString())).thenReturn(true);

        DataList result = service.search(USER_ID_VALID, validSearch);

        assertThat(result).isNotNull();
    }

    @Test
    void search_EmptyStatus_ContinuesSuccessfully() throws Exception {
        when(dao.checkIsClient(RUT_FROM_USER_ID)).thenReturn(false);
        when(dao.getStatus(OWNER_DOCUMENT_VALID, RUT_FROM_USER_ID)).thenReturn(STATUS_EMPTY);
        when(businessInformationService.getBusinessInformation(RUT_FROM_USER_ID)).thenReturn(validBusinessInfo);
        when(dao.getOwner(any(NonBusinessIdDatatype.class))).thenReturn(validOwner);
        when(nameValidator.validate(anyString())).thenReturn(true);
        when(nameValidator.similarName(any(), anyString())).thenReturn(true);

        DataList result = service.search(USER_ID_VALID, validSearch);

        assertThat(result).isNotNull();
    }

    @Test
    void search_UserIdWithWhitespace_TrimsAndProcesses() throws Exception {

        when(dao.checkIsClient(RUT_FROM_USER_ID)).thenReturn(false);
        when(dao.getStatus(OWNER_DOCUMENT_VALID, RUT_FROM_USER_ID)).thenReturn(STATUS_VALID);
        when(businessInformationService.getBusinessInformation(RUT_FROM_USER_ID)).thenReturn(validBusinessInfo);
        when(dao.getOwner(any(NonBusinessIdDatatype.class))).thenReturn(validOwner);

        when(nameValidator.validate(anyString())).thenReturn(true);
        when(nameValidator.similarName(any(), anyString())).thenReturn(true);

        DataList result = service.search(USER_ID_WITH_WHITESPACE, validSearch);

        assertThat(result).isNotNull();
    }

    @Test
    void search_Success_VerifiesStatusUpdateInteractions() throws Exception {
        when(dao.checkIsClient(RUT_FROM_USER_ID)).thenReturn(false);
        when(dao.getStatus(OWNER_DOCUMENT_VALID, RUT_FROM_USER_ID)).thenReturn(STATUS_VALID);
        when(businessInformationService.getBusinessInformation(RUT_FROM_USER_ID)).thenReturn(validBusinessInfo);
        when(dao.getOwner(any(NonBusinessIdDatatype.class))).thenReturn(validOwner);
        when(nameValidator.validate(anyString())).thenReturn(true);
        when(nameValidator.similarName(any(), anyString())).thenReturn(true);

        service.search(USER_ID_VALID, validSearch);

        ArgumentCaptor<NonBusinessIdDatatype> datatypeCaptor = ArgumentCaptor.forClass(NonBusinessIdDatatype.class);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);

        verify(dao).updateStatus(datatypeCaptor.capture(), eq(STATUS_ID_DGI_OK));
        verify(dao).auditStatusChange(datatypeCaptor.capture(), statusCaptor.capture());

        NonBusinessIdDatatype capturedDatatype = datatypeCaptor.getValue();
        assertThat(capturedDatatype.getBusinessDocument()).isEqualTo(RUT_FROM_USER_ID);
        assertThat(capturedDatatype.getPersonDocument()).isEqualTo(OWNER_DOCUMENT_VALID);

        Status capturedStatus = statusCaptor.getValue();
        assertThat(capturedStatus.getId()).isEqualTo(STATUS_ID_DGI_OK);
        assertThat(capturedStatus.getProcess()).isEqualTo(PROCESS_SEARCH);
    }

    private void verifyBusinessException(BusinessException exception, String expectedMessagePart) {
        assertThat(exception.getInternCode())
                .as("El código de excepción no coincide")
                .contains(expectedMessagePart);
    }
}