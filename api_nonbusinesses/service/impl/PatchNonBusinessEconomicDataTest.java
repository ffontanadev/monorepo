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
import uy.com.bbva.services.nonbusinesses.model.EconomicData;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite de pruebas de NonBusinessesApiServiceImpl.patchNonBusinessEconomicData():")
class PatchNonBusinessEconomicDataTest extends ServiceTest {

    @InjectMocks
    private NonBusinessesApiServiceImpl nonBusinessesApiService;

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
    @DisplayName("Verifica orden de ejecución: primero desencripta ID, luego actualiza DAO")
    void patchNonBusinessEconomicData_verifyExecutionOrder() throws Exception {
        EconomicData economicData = createEconomicDataWithRealIncome();

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        nonBusinessesApiService.patchNonBusinessEconomicData(nonBusinessId, economicData);

        var inOrder = inOrder(nonBusinessIdManagement, dao);
        inOrder.verify(nonBusinessIdManagement).getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId);
        inOrder.verify(dao).updateBusinessEconomicData(datatype, economicData);
    }

    @Test
    @DisplayName("Captura correcta de parámetros: verifica que se pasan los objetos correctos al DAO")
    void patchNonBusinessEconomicData_capturesCorrectParameters() throws Exception {
        EconomicData economicData = createEconomicDataWithRealIncome();
        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        nonBusinessesApiService.patchNonBusinessEconomicData(nonBusinessId, economicData);

        ArgumentCaptor<NonBusinessIdDatatype> datatypeCaptor = ArgumentCaptor.forClass(NonBusinessIdDatatype.class);
        ArgumentCaptor<EconomicData> economicDataCaptor = ArgumentCaptor.forClass(EconomicData.class);
        verify(dao).updateBusinessEconomicData(datatypeCaptor.capture(), economicDataCaptor.capture());

        assertSame(datatype, datatypeCaptor.getValue(), "Should pass the same NonBusinessIdDatatype instance");
        assertSame(economicData, economicDataCaptor.getValue(), "Should pass the same EconomicData instance");
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
    @DisplayName("EconomicData con valores nulos: pasa objeto con nulls al DAO")
    void patchNonBusinessEconomicData_economicDataWithNulls_passesToDao() throws Exception {

        EconomicData economicData = new EconomicData();
        economicData.setEconomicActivity(null);
        economicData.setFinancialInformation(null);
        economicData.setTax(null);

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        nonBusinessesApiService.patchNonBusinessEconomicData(nonBusinessId, economicData);

        ArgumentCaptor<EconomicData> economicDataCaptor = ArgumentCaptor.forClass(EconomicData.class);
        verify(dao).updateBusinessEconomicData(eq(datatype), economicDataCaptor.capture());

        EconomicData capturedData = economicDataCaptor.getValue();
        assertSame(economicData, capturedData);
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

    @Test
    @DisplayName("NonBusinessId nulo: propaga el error del management")
    void patchNonBusinessEconomicData_nullId_propagatesError() throws Exception {
        EconomicData economicData = createEconomicDataWithRealIncome();

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(null))
                .thenThrow(new NullPointerException("ID cannot be null"));

        assertThrows(NullPointerException.class,
                () -> nonBusinessesApiService.patchNonBusinessEconomicData(null, economicData));

        verify(dao, never()).updateBusinessEconomicData(any(), any());
    }

    @Test
    @DisplayName("Error en DAO: propaga la excepción")
    void patchNonBusinessEconomicData_daoError_propagatesException() throws Exception {
        EconomicData economicData = createEconomicDataWithRealIncome();

        NonBusinessIdDatatype datatype = createBusinessWithPersonDatatype();
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId))
                .thenReturn(datatype);

        RuntimeException daoException = new RuntimeException("DAO update failed");
        doThrow(daoException).when(dao).updateBusinessEconomicData(datatype, economicData);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> nonBusinessesApiService.patchNonBusinessEconomicData(nonBusinessId, economicData));

        assertEquals("DAO update failed", ex.getMessage());
        assertSame(daoException, ex);

        verify(nonBusinessIdManagement).getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId);
        verify(dao).updateBusinessEconomicData(datatype, economicData);
    }

}