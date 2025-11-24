package uy.com.bbva.services.nonbusinesses.service.impl;

import com.bbva.secarq.caas2.core.exception.CaasException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.dtos.commons.enums.ContactTypeEnum;
import uy.com.bbva.dtos.commons.model.Address;
import uy.com.bbva.dtos.commons.model.EmailContact;
import uy.com.bbva.dtos.commons.model.GenericIdDescription;
import uy.com.bbva.dtos.commons.model.MobileContact;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.nonbusinessescommons.dtos.models.ContactDetail;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.nonbusinessescommons.idmanagement.idmanagement.NonBusinessIdManagement;
import uy.com.bbva.services.commons.exceptions.BusinessException;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.dao.DAO;
import uy.com.bbva.services.nonbusinesses.model.*;
import uy.com.bbva.services.nonbusinesses.model.status.Status;
import uy.com.bbva.services.nonbusinesses.service.external.BusinessInformationService;
import uy.com.bbva.services.nonbusinesses.service.external.RisksService;
import uy.com.bbva.services.nonbusinesses.service.utils.AddressUtils;
import uy.com.bbva.services.nonbusinesses.service.utils.validator.NameValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite consolidada de pruebas de NonBusinessesApiServiceImpl")
class ServicesTest {

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

    @InjectMocks
    private NonBusinessesApiServiceImpl nonBusinessesApiService;

    @Nested
    @DisplayName("CreateAddress Tests")
    class CreateAddressTests {

        private NonBusinessIdDatatype mockDatatype;
        private Map<String, GenericIdDescription> departmentsMap;
        private AddressDatatype addressDatatype;

        @BeforeEach
        void setUp() throws Exception {
            mockDatatype = createBusinessDatatype();
            departmentsMap = createDepartmentsMap();
            addressDatatype = createValidAddressDatatype();
        }

        @Test
        @DisplayName("Happy Path: crea dirección exitosamente con todos los parámetros válidos")
        void createAddress_happyPath_createsAddressSuccessfully() throws Exception {
            Address address = createAddress();

            when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(NON_BUSINESS_ID_VALID))
                    .thenReturn(mockDatatype);
            when(dao.getDepartments()).thenReturn(departmentsMap);
            when(addressUtil.getAddressDatatypeFromAddress(address, departmentsMap))
                    .thenReturn(addressDatatype);

            nonBusinessesApiService.createAddress(NON_BUSINESS_ID_VALID, address);

            verify(nonBusinessIdManagement).getNonBusinessIdDatatypeFromNonBusinessId(NON_BUSINESS_ID_VALID);
            verify(dao).getDepartments();
            verify(addressUtil).getAddressDatatypeFromAddress(address, departmentsMap);
            verify(dao).createAddress(mockDatatype, addressDatatype);
            verify(dao).updateStatus(mockDatatype, STATUS_ID_NB_ADD_OK);

            ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
            verify(dao).auditStatusChange(eq(mockDatatype), statusCaptor.capture());
            assertEquals(STATUS_ID_NB_ADD_OK, statusCaptor.getValue().getId());
            assertEquals(PROCESS_ADDRESS, statusCaptor.getValue().getProcess());
        }

        @Test
        @DisplayName("Error de desencriptación: lanza ServiceException")
        void createAddress_decryptionError_throwsServiceException() throws Exception {
            Address address = createAddress();

            CaasException caasException = new CaasException("Decryption failed");
            when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(NON_BUSINESS_ID_VALID))
                    .thenThrow(caasException);

            ServiceException ex = assertThrows(ServiceException.class,
                    () -> nonBusinessesApiService.createAddress(NON_BUSINESS_ID_VALID, address));

            assertEquals("Decryption error", ex.getInternalMessage());
            assertSame(caasException, ex.getCause());

            verify(dao, times(1)).getDepartments();
            verify(dao, never()).createAddress(any(), any());
            verify(dao, never()).updateStatus(any(), anyString());
            verify(dao, never()).auditStatusChange(any(), any());
        }

        @Test
        @DisplayName("Dirección con campos mínimos: crea dirección correctamente")
        void createAddress_withMinimalFields_createsSuccessfully() throws Exception {
            Address address = createMinimalAddress();

            when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(NON_BUSINESS_ID_VALID))
                    .thenReturn(mockDatatype);
            when(dao.getDepartments()).thenReturn(departmentsMap);
            when(addressUtil.getAddressDatatypeFromAddress(address, departmentsMap))
                    .thenReturn(addressDatatype);

            nonBusinessesApiService.createAddress(NON_BUSINESS_ID_VALID, address);

            verify(dao).createAddress(mockDatatype, addressDatatype);
            verify(dao).updateStatus(mockDatatype, STATUS_ID_NB_ADD_OK);
            verify(dao).auditStatusChange(eq(mockDatatype), any(Status.class));
        }
    }

    @Nested
    @DisplayName("CreateContactDetail Tests")
    class CreateContactDetailTests {

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
    }

    @Nested
    @DisplayName("PatchNonBusinessEconomicData Tests")
    class PatchNonBusinessEconomicDataTests {

        private final String nonBusinessId = NON_BUSINESS_ID_VALID;

        @Test
        @DisplayName("Happy path: actualiza datos económicos exitosamente")
        void patchNonBusinessEconomicData_happyPath_updatesEconomicData() throws Exception {
            EconomicData economicData = createEconomicDataWithRealIncome();

            NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
            when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                    .thenReturn(datatype);

            nonBusinessesApiService.patchNonBusinessEconomicData(nonBusinessId, economicData);

            verify(nonBusinessIdManagement).getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId);
            verify(dao).updateBusinessEconomicData(datatype, economicData);
        }

        @Test
        @DisplayName("EconomicData nulo: pasa null al DAO")
        void patchNonBusinessEconomicData_nullEconomicData_passesToDao() throws Exception {

            NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
            when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                    .thenReturn(datatype);

            nonBusinessesApiService.patchNonBusinessEconomicData(nonBusinessId, null);

            verify(dao).updateBusinessEconomicData(datatype, null);
        }

        @Test
        @DisplayName("Error de desencriptación: lanza ServiceException")
        void patchNonBusinessEconomicData_decryptionError_throwsServiceException() throws Exception {
            EconomicData economicData = createEconomicDataWithRealIncome();

            CaasException caasException = new CaasException(ERROR_DECRYPTION);
            when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                    .thenThrow(caasException);

            ServiceException ex = assertThrows(ServiceException.class,
                    () -> nonBusinessesApiService.patchNonBusinessEconomicData(nonBusinessId, economicData));

            assertEquals(ERROR_DECRYPTION, ex.getInternalMessage());
            assertSame(caasException, ex.getCause());

            verify(dao, never()).updateBusinessEconomicData(any(), any());
        }
    }

    @Nested
    @DisplayName("PatchNonBusiness Tests")
    class PatchNonBusinessTests {

        private final String nonBusinessId = NON_BUSINESS_ID_VALID;

        @Test
        @DisplayName("Happy path completo: actualiza doingBusinessAs, bank branch, formation y user")
        void patchNonBusiness_fullUpdate_updatesAllFields() throws Exception {

            NonBusinessIdDatatype datatype = createBusinessDatatype();
            when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                    .thenReturn(datatype);

            NonBusiness nonBusiness = createFullNonBusiness();

            nonBusinessesApiService.patchNonBusiness(nonBusinessId, nonBusiness);

            verify(dao).updateBusinessCommercialName(datatype, "Test Business S.A.");
            verify(dao).updateBusinessBankBranch(datatype, "001");
            verify(dao).updateBusinessFormationData(eq(datatype), eq(nonBusiness.getFormation()), any(LegalDocument.class));
            verify(dao).createTemporaryPassword(datatype, nonBusiness.getUser());
        }

        @Test
        @DisplayName("Solo doingBusinessAs: actualiza únicamente nombre comercial")
        void patchNonBusiness_onlyDoingBusinessAs_updatesOnlyCommercialName() throws Exception {

            NonBusinessIdDatatype datatype = createBusinessDatatype();
            when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                    .thenReturn(datatype);

            NonBusiness nonBusiness = new NonBusiness();
            nonBusiness.setDoingBusinessAs("Solo Nombre");

            nonBusinessesApiService.patchNonBusiness(nonBusinessId, nonBusiness);

            verify(dao).updateBusinessCommercialName(datatype, "Solo Nombre");
            verify(dao, never()).updateBusinessBankBranch(any(), anyString());
            verify(dao, never()).updateBusinessFormationData(any(), any(), any());
            verify(dao, never()).createTemporaryPassword(any(), any());
        }

        @Test
        @DisplayName("Solo bank branch: actualiza únicamente sucursal bancaria")
        void patchNonBusiness_onlyBankBranch_updatesOnlyBranch() throws Exception {

            NonBusinessIdDatatype datatype = createBusinessDatatype();
            when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                    .thenReturn(datatype);

            NonBusiness nonBusiness = new NonBusiness();
            Bank bank = new Bank();
            Branch branch = createBranch("002");

            bank.setBranch(branch);
            nonBusiness.setBank(bank);

            nonBusinessesApiService.patchNonBusiness(nonBusinessId, nonBusiness);

            verify(dao).updateBusinessBankBranch(datatype, "002");
            verify(dao, never()).updateBusinessCommercialName(any(), anyString());
            verify(dao, never()).updateBusinessFormationData(any(), any(), any());
            verify(dao, never()).createTemporaryPassword(any(), any());
        }

        @Test
        @DisplayName("Error de desencriptación: lanza ServiceException")
        void patchNonBusiness_decryptionError_throwsServiceException() throws Exception {

            CaasException caasException = new CaasException(ERROR_DECRYPTION);
            when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                    .thenThrow(caasException);

            NonBusiness nonBusiness = new NonBusiness();
            nonBusiness.setDoingBusinessAs("Test");

            ServiceException ex = assertThrows(ServiceException.class,
                    () -> nonBusinessesApiService.patchNonBusiness(nonBusinessId, nonBusiness));

            assertEquals(ERROR_DECRYPTION, ex.getInternalMessage());
            assertSame(caasException, ex.getCause());

            verify(dao, never()).updateBusinessCommercialName(any(), anyString());
        }

        @Test
        @DisplayName("NonBusiness completamente vacío: no ejecuta ninguna actualización")
        void patchNonBusiness_emptyNonBusiness_doesNothing() throws Exception {

            NonBusinessIdDatatype datatype = createBusinessDatatype();
            when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                    .thenReturn(datatype);

            NonBusiness nonBusiness = new NonBusiness();

            nonBusinessesApiService.patchNonBusiness(nonBusinessId, nonBusiness);

            verify(dao, never()).updateBusinessCommercialName(any(), anyString());
            verify(dao, never()).updateBusinessBankBranch(any(), anyString());
            verify(dao, never()).updateBusinessFormationData(any(), any(), any());
            verify(dao, never()).createTemporaryPassword(any(), any());
        }
    }

    @Nested
    @DisplayName("GetNonBusinessById Tests")
    class GetNonBusinessByIdTests {

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
    }
}
