package uy.com.bbva.services.nonbusinesses.service.impl;

import com.bbva.secarq.caas2.core.exception.CaasException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.dtos.commons.enums.ContactTypeEnum;
import uy.com.bbva.dtos.commons.model.EmailContact;
import uy.com.bbva.dtos.commons.model.MobileContact;
import uy.com.bbva.nonbusinessescommons.dtos.models.ContactDetail;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.commons.exceptions.BusinessException;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.common.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite de pruebas de NonBusinessesApiServiceImpl.createContactDetail():")
class CreateContactDetailTest extends ServiceTest
{
    @InjectMocks
    private NonBusinessesApiServiceImpl nonBusinessesApiService;

    private final String nonBusinessId = NON_BUSINESS_ID_VALID;

    @Test
    @DisplayName("Happy path EMAIL: crea contacto de email y actualiza estado")
    void createContactDetail_emailHappyPath_updatesEmailAndStatus() throws Exception {
        String emailAddress = EMAIL_VALID;

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);
        when(risksService.isMailBlacklisted(any(EmailContact.class), eq(datatype))).thenReturn(false);

        EmailContact emailContact = new EmailContact();
        emailContact.setAddress(emailAddress);
        emailContact.setContactDetailType(ContactTypeEnum.EMAIL.getId());

        ContactDetail body = new ContactDetail();
        body.setContact(emailContact);

        nonBusinessesApiService.createContactDetail(nonBusinessId, body);

        verify(dao).updatePersonMail(datatype, emailAddress);
        verify(dao).updateBusinessMail(datatype, emailAddress);
        verify(dao).updateStatus(datatype, STATUS_ID_NB_CNT_OK);
        verify(dao).auditStatusChange(eq(datatype), any());
    }

    @Test
    @DisplayName("Happy path MOBILE: crea contacto móvil y actualiza estado")
    void createContactDetail_mobileHappyPath_updatesMobileAndStatus() throws Exception {
        String phoneNumber = MOBILE_VALID;

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        MobileContact mobileContact = new MobileContact();
        mobileContact.setNumber(phoneNumber);
        mobileContact.setContactDetailType(ContactTypeEnum.MOBILE.getId());

        ContactDetail body = new ContactDetail();
        body.setContact(mobileContact);

        nonBusinessesApiService.createContactDetail(nonBusinessId, body);

        verify(dao).updatePersonMobile(datatype, phoneNumber);
        verify(dao).updateBusinessMobile(datatype, phoneNumber);
        verify(dao).updateStatus(datatype, STATUS_ID_NB_CNT_OK);
        verify(dao).auditStatusChange(eq(datatype), any());
    }

    @Test
    @DisplayName("Email con espacios: trim funciona correctamente")
    void createContactDetail_emailWithSpaces_trimsContactType() throws Exception {
        String emailAddress = EMAIL_VALID;

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);
        when(risksService.isMailBlacklisted(any(EmailContact.class), eq(datatype))).thenReturn(false);

        EmailContact emailContact = new EmailContact();
        emailContact.setAddress(emailAddress);
        emailContact.setContactDetailType("  " + ContactTypeEnum.EMAIL.getId() + "  ");

        ContactDetail body = new ContactDetail();
        body.setContact(emailContact);

        nonBusinessesApiService.createContactDetail(nonBusinessId, body);

        verify(dao).updatePersonMail(datatype, emailAddress);
        verify(dao).updateBusinessMail(datatype, emailAddress);
        verify(dao).updateStatus(datatype, STATUS_ID_NB_CNT_OK);
    }

    @Test
    @DisplayName("Tipo de contacto desconocido: no realiza operaciones")
    void createContactDetail_unknownContactType_doesNothing() throws Exception {

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        EmailContact unknownContact = new EmailContact();
        unknownContact.setAddress(EMAIL_VALID);
        unknownContact.setContactDetailType("UNKNOWN_TYPE");

        ContactDetail body = new ContactDetail();
        body.setContact(unknownContact);

        nonBusinessesApiService.createContactDetail(nonBusinessId, body);

        verify(dao, never()).updatePersonMail(any(), anyString());
        verify(dao, never()).updateBusinessMail(any(), anyString());
        verify(dao, never()).updatePersonMobile(any(), anyString());
        verify(dao, never()).updateBusinessMobile(any(), anyString());
        verify(dao, never()).updateStatus(any(), anyString());
    }

    @Test
    @DisplayName("Email vacío: lanza BusinessException con código EMPTY_MAIL")
    void createContactDetail_emptyEmail_throwsBusinessException() throws Exception {

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        EmailContact emailContact = new EmailContact();
        emailContact.setAddress("");
        emailContact.setContactDetailType(ContactTypeEnum.EMAIL.getId());

        ContactDetail body = new ContactDetail();
        body.setContact(emailContact);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> nonBusinessesApiService.createContactDetail(nonBusinessId, body));

        assertEquals("EMPTY_MAIL", ex.getInternCode());
        assertEquals("Email is required", ex.getMessage());

        verify(dao, never()).updatePersonMail(any(), anyString());
        verify(dao, never()).updateBusinessMail(any(), anyString());
    }

    @Test
    @DisplayName("Email con formato inválido: lanza BusinessException con código INVALID_MAIL_PATTERN")
    void createContactDetail_invalidEmailFormat_throwsBusinessException() throws Exception {
        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        EmailContact emailContact = new EmailContact();
        String invalidPattern = "invalid-email-format";

        emailContact.setAddress(invalidPattern);
        emailContact.setContactDetailType(ContactTypeEnum.EMAIL.getId());

        ContactDetail body = new ContactDetail();
        body.setContact(emailContact);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> nonBusinessesApiService.createContactDetail(nonBusinessId, body));

        assertEquals("INVALID_MAIL_PATTERN", ex.getInternCode());
        assertEquals("Invalid mail", ex.getMessage());

        verify(dao, never()).updatePersonMail(any(), anyString());
        verify(dao, never()).updateBusinessMail(any(), anyString());
    }

    @Test
    @DisplayName("Email en lista negra: lanza BusinessException con código correspondiente")
    void createContactDetail_blacklistedEmail_throwsBusinessException() throws Exception {
        String blacklistedEmail = "blacklisted@gmail.com";

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);
        when(risksService.isMailBlacklisted(any(EmailContact.class), eq(datatype))).thenReturn(true);

        EmailContact emailContact = new EmailContact();
        emailContact.setAddress(blacklistedEmail);
        emailContact.setContactDetailType(ContactTypeEnum.EMAIL.getId());

        ContactDetail body = new ContactDetail();
        body.setContact(emailContact);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> nonBusinessesApiService.createContactDetail(nonBusinessId, body));

        assertEquals("NON_BUSINESS_SEARCH_ERROR_DGI_NOMBRE_NO_CORRESPONDE_PF", ex.getInternCode());

        verify(dao, never()).updatePersonMail(any(), anyString());
        verify(dao, never()).updateBusinessMail(any(), anyString());
        verify(dao).auditStatusChange(eq(datatype), any());
    }

    @Test
    @DisplayName("Móvil vacío: lanza BusinessException con código EMPTY_MOBILE")
    void createContactDetail_emptyMobile_throwsBusinessException() throws Exception {

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        MobileContact mobileContact = new MobileContact();
        mobileContact.setNumber("");
        mobileContact.setContactDetailType(ContactTypeEnum.MOBILE.getId());

        ContactDetail body = new ContactDetail();
        body.setContact(mobileContact);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> nonBusinessesApiService.createContactDetail(nonBusinessId, body));

        assertEquals("EMPTY_MOBILE", ex.getInternCode());
        assertEquals("Cellphone is required", ex.getMessage());

        verify(dao, never()).updatePersonMobile(any(), anyString());
        verify(dao, never()).updateBusinessMobile(any(), anyString());
    }

    @Test
    @DisplayName("Móvil con formato inválido: lanza BusinessException con código INVALID_MOBILE_PATTERN")
    void createContactDetail_invalidMobileFormat_throwsBusinessException() throws Exception {
        String invalidMobile = "123456";

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        MobileContact mobileContact = new MobileContact();
        mobileContact.setNumber(invalidMobile);
        mobileContact.setContactDetailType(ContactTypeEnum.MOBILE.getId());

        ContactDetail body = new ContactDetail();
        body.setContact(mobileContact);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> nonBusinessesApiService.createContactDetail(nonBusinessId, body));

        assertEquals("INVALID_MOBILE_PATTERN", ex.getInternCode());
        assertEquals("Invalid cellphone", ex.getMessage());

        verify(dao, never()).updatePersonMobile(any(), anyString());
        verify(dao, never()).updateBusinessMobile(any(), anyString());
    }

    @Test
    @DisplayName("Error de desencriptación en business ID management: lanza ServiceException")
    void createContactDetail_businessIdManagementError_throwsServiceException() throws Exception {

        CaasException caasException = new CaasException("Business ID decryption failed");
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenThrow(caasException);

        EmailContact emailContact = new EmailContact();
        emailContact.setAddress(EMAIL_VALID);
        emailContact.setContactDetailType(ContactTypeEnum.EMAIL.getId());

        ContactDetail body = new ContactDetail();
        body.setContact(emailContact);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> nonBusinessesApiService.createContactDetail(nonBusinessId, body));

        assertEquals(ERROR_DECRYPTION, ex.getInternalMessage());
        assertSame(caasException, ex.getCause());

        verify(dao, never()).updatePersonMail(any(), anyString());
        verify(dao, never()).updateBusinessMail(any(), anyString());
    }

    @Test
    @DisplayName("Contact detail con tipo nulo: lanza NullPointerException")
    void createContactDetail_nullContactType_throwsNullPointerException() throws Exception {

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        EmailContact emailContact = new EmailContact();
        emailContact.setAddress(EMAIL_VALID);
        emailContact.setContactDetailType(null);

        ContactDetail body = new ContactDetail();
        body.setContact(emailContact);

        assertThrows(NullPointerException.class,
                () -> nonBusinessesApiService.createContactDetail(nonBusinessId, body));
    }
}