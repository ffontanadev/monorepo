package uy.com.bbva.services.nonbusinesses.service.impl;

import com.bbva.secarq.caas2.core.exception.CaasException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.dtos.commons.model.GenericIdDescription;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.nonbusinessescommons.idmanagement.idmanagement.NonBusinessIdManagement;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.common.ServiceTest;
import uy.com.bbva.services.nonbusinesses.dao.DAO;
import uy.com.bbva.services.nonbusinesses.model.LegalDocument;
import uy.com.bbva.services.nonbusinesses.model.NonBusiness;
import uy.com.bbva.services.nonbusinesses.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite de pruebas de NonBusinessesApiServiceImpl.patchNonBusiness():")
class PatchNonBusinessTest extends ServiceTest {

    @InjectMocks
    private NonBusinessesApiServiceImpl nonBusinessesApiService;

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
    @DisplayName("Solo formation con documento BPS")
    void patchNonBusiness_onlyFormationWithBps_updatesFormationData() throws Exception {

        NonBusinessIdDatatype datatype = createBusinessDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        NonBusiness nonBusiness = new NonBusiness();
        Formation formation = new Formation();
        nonBusiness.setFormation(formation);

        List<LegalDocument> legalDocuments = new ArrayList<>();
        LegalDocument bpsDoc = new LegalDocument();
        bpsDoc.setLegalDocumentType(new GenericIdDescription("BPS_REGISTRATION", "BPS Registration"));
        bpsDoc.setDocumentNumber("BPS12345");

        legalDocuments.add(bpsDoc);
        nonBusiness.setLegalDocuments(legalDocuments);

        nonBusinessesApiService.patchNonBusiness(nonBusinessId, nonBusiness);

        verify(dao).updateBusinessFormationData(datatype, formation, bpsDoc);
        verify(dao, never()).updateBusinessCommercialName(any(), anyString());
        verify(dao, never()).updateBusinessBankBranch(any(), anyString());
        verify(dao, never()).createTemporaryPassword(any(), any());
    }

    @Test
    @DisplayName("Formation sin documento BPS: no actualiza")
    void patchNonBusiness_formationWithoutBps_doesNotUpdateFormation() throws Exception {
        NonBusinessIdDatatype datatype = createBusinessDatatype();

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        NonBusiness nonBusiness = new NonBusiness();
        Formation formation = new Formation();
        nonBusiness.setFormation(formation);

        List<LegalDocument> legalDocuments = new ArrayList<>();
        LegalDocument otherDoc = new LegalDocument();
        otherDoc.setLegalDocumentType(new GenericIdDescription("OTHER_DOC", "Other Document"));
        legalDocuments.add(otherDoc);
        nonBusiness.setLegalDocuments(legalDocuments);

        nonBusinessesApiService.patchNonBusiness(nonBusinessId, nonBusiness);

        verify(dao, never()).updateBusinessFormationData(any(), any(), any());
        verify(dao, never()).updateBusinessCommercialName(any(), anyString());
        verify(dao, never()).updateBusinessBankBranch(any(), anyString());
        verify(dao, never()).createTemporaryPassword(any(), any());
    }

    @Test
    @DisplayName("Solo user: crea contraseña temporal")
    void patchNonBusiness_onlyUser_createsTemporaryPassword() throws Exception {
        NonBusinessIdDatatype datatype = createBusinessDatatype();

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        NonBusiness nonBusiness = new NonBusiness();
        User user = new User();
        user.setName(USER_NAME_VALID);
        user.setPassword(USER_PASSWORD_VALID);
        nonBusiness.setUser(user);

        nonBusinessesApiService.patchNonBusiness(nonBusinessId, nonBusiness);

        verify(dao).createTemporaryPassword(datatype, user);
        verify(dao, never()).updateBusinessCommercialName(any(), anyString());
        verify(dao, never()).updateBusinessBankBranch(any(), anyString());
        verify(dao, never()).updateBusinessFormationData(any(), any(), any());
    }

    @Test
    @DisplayName("DoingBusinessAs vacío: no actualiza nombre comercial")
    void patchNonBusiness_emptyDoingBusinessAs_doesNotUpdate() throws Exception {
        NonBusinessIdDatatype datatype = createBusinessDatatype();

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        NonBusiness nonBusiness = new NonBusiness();
        nonBusiness.setDoingBusinessAs("");

        nonBusinessesApiService.patchNonBusiness(nonBusinessId, nonBusiness);

        verify(dao, never()).updateBusinessCommercialName(any(), anyString());
    }

    @Test
    @DisplayName("Bank sin branch: no actualiza sucursal")
    void patchNonBusiness_bankWithoutBranch_doesNotUpdateBranch() throws Exception {
        NonBusinessIdDatatype datatype = createBusinessDatatype();

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        NonBusiness nonBusiness = new NonBusiness();
        Bank bank = new Bank();
        bank.setBranch(null);
        nonBusiness.setBank(bank);

        nonBusinessesApiService.patchNonBusiness(nonBusinessId, nonBusiness);

        verify(dao, never()).updateBusinessBankBranch(any(), anyString());
    }

    @Test
    @DisplayName("LegalDocuments nulo: no actualiza formation")
    void patchNonBusiness_nullLegalDocuments_doesNotUpdateFormation() throws Exception {

        NonBusinessIdDatatype datatype = createBusinessDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        NonBusiness nonBusiness = new NonBusiness();
        Formation formation = new Formation();
        nonBusiness.setFormation(formation);
        nonBusiness.setLegalDocuments(null);

        assertThrows(NullPointerException.class,
                () -> nonBusinessesApiService.patchNonBusiness(nonBusinessId, nonBusiness));

        verify(dao, never()).updateBusinessFormationData(any(), any(), any());
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