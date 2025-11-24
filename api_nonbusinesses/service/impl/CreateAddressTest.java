package uy.com.bbva.services.nonbusinesses.service.impl;

import com.bbva.secarq.caas2.core.exception.CaasException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import uy.com.bbva.dtos.commons.model.Address;
import uy.com.bbva.dtos.commons.model.GenericIdDescription;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.common.ServiceTest;
import uy.com.bbva.services.nonbusinesses.model.AddressDatatype;
import uy.com.bbva.services.nonbusinesses.model.status.Status;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

@DisplayName("Suite de pruebas de NonBusinessesApiServiceImpl.createAddress():")
class CreateAddressTest extends ServiceTest {

    private NonBusinessesApiServiceImpl nonBusinessesApiService;

    private NonBusinessIdDatatype mockDatatype;
    private Map<String, GenericIdDescription> departmentsMap;
    private AddressDatatype addressDatatype;

    @BeforeEach
    void setUp() throws Exception {
        mockDatatype = createBusinessDatatype();
        departmentsMap = createDepartmentsMap();
        addressDatatype = createValidAddressDatatype();

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
    @DisplayName("Dirección con calle completa y número: crea dirección correctamente")
    void createAddress_withFullStreetAndNumber_createsSuccessfully() throws Exception {
        Address address = createAddress();

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

    @Test
    @DisplayName("Departamento válido del mapa: crea dirección correctamente")
    void createAddress_withValidDepartmentFromMap_createsSuccessfully() throws Exception {
        Address address = createAddress();

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(NON_BUSINESS_ID_VALID))
                .thenReturn(mockDatatype);
        when(dao.getDepartments()).thenReturn(departmentsMap);
        when(addressUtil.getAddressDatatypeFromAddress(address, departmentsMap))
                .thenReturn(addressDatatype);

        nonBusinessesApiService.createAddress(NON_BUSINESS_ID_VALID, address);

        verify(addressUtil).getAddressDatatypeFromAddress(address, departmentsMap);
        verify(dao).createAddress(mockDatatype, addressDatatype);
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
    @DisplayName("Error al obtener departamentos: propaga excepción")
    void createAddress_getDepartmentsError_propagatesException() throws Exception {
        Address address = createAddress();

        RuntimeException departmentsException = new RuntimeException(ERROR_DATABASE);

        when(dao.getDepartments()).thenThrow(departmentsException);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> nonBusinessesApiService.createAddress(NON_BUSINESS_ID_VALID, address));

        assertSame(departmentsException, ex);

        verify(addressUtil, never()).getAddressDatatypeFromAddress(any(), any());
        verify(dao, never()).createAddress(any(), any());
        verify(dao, never()).updateStatus(any(), anyString());
        verify(dao, never()).auditStatusChange(any(), any());
    }

    @Test
    @DisplayName("Error al convertir dirección: propaga excepción")
    void createAddress_addressConversionError_propagatesException() throws Exception {
        Address address = createAddress();

        RuntimeException conversionException = new RuntimeException(ERROR_CONVERSION);
        when(dao.getDepartments()).thenReturn(departmentsMap);
        when(addressUtil.getAddressDatatypeFromAddress(address, departmentsMap))
                .thenThrow(conversionException);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> nonBusinessesApiService.createAddress(NON_BUSINESS_ID_VALID, address));

        assertSame(conversionException, ex);

        verify(dao, never()).createAddress(any(), any());
        verify(dao, never()).updateStatus(any(), anyString());
        verify(dao, never()).auditStatusChange(any(), any());
    }

    @Test
    @DisplayName("Error al crear dirección en DAO: propaga excepción")
    void createAddress_daoCreateError_propagatesException() throws Exception {
        Address address = createAddress();

        RuntimeException daoException = new RuntimeException(ERROR_DAO);
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(NON_BUSINESS_ID_VALID))
                .thenReturn(mockDatatype);
        when(dao.getDepartments()).thenReturn(departmentsMap);
        when(addressUtil.getAddressDatatypeFromAddress(address, departmentsMap))
                .thenReturn(addressDatatype);
        doThrow(daoException).when(dao).createAddress(mockDatatype, addressDatatype);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> nonBusinessesApiService.createAddress(NON_BUSINESS_ID_VALID, address));

        assertSame(daoException, ex);

        verify(dao, never()).updateStatus(any(), anyString());
        verify(dao, never()).auditStatusChange(any(), any());
    }

    @Test
    @DisplayName("Error al actualizar estado: propaga excepción")
    void createAddress_updateStatusError_propagatesException() throws Exception {
        Address address = createAddress();

        RuntimeException statusException = new RuntimeException(ERROR_UPDATE_STATUS);
        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(NON_BUSINESS_ID_VALID))
                .thenReturn(mockDatatype);
        when(dao.getDepartments()).thenReturn(departmentsMap);
        when(addressUtil.getAddressDatatypeFromAddress(address, departmentsMap))
                .thenReturn(addressDatatype);
        doThrow(statusException).when(dao).updateStatus(mockDatatype, STATUS_ID_NB_ADD_OK);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> nonBusinessesApiService.createAddress(NON_BUSINESS_ID_VALID, address));

        assertSame(statusException, ex);

        verify(dao).createAddress(mockDatatype, addressDatatype);
        verify(dao, never()).auditStatusChange(any(), any());
    }

    @Test
    @DisplayName("Error al auditar cambio de estado: propaga excepción")
    void createAddress_auditStatusChangeError_propagatesException() throws Exception {
        Address address = createAddress();
        RuntimeException auditException = new RuntimeException(ERROR_AUDIT);

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(NON_BUSINESS_ID_VALID))
                .thenReturn(mockDatatype);
        when(dao.getDepartments()).thenReturn(departmentsMap);
        when(addressUtil.getAddressDatatypeFromAddress(address, departmentsMap))
                .thenReturn(addressDatatype);
        doThrow(auditException).when(dao).auditStatusChange(eq(mockDatatype), any(Status.class));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> nonBusinessesApiService.createAddress(NON_BUSINESS_ID_VALID, address));

        assertSame(auditException, ex);

        verify(dao).createAddress(mockDatatype, addressDatatype);
        verify(dao).updateStatus(mockDatatype, STATUS_ID_NB_ADD_OK);
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

    @Test
    @DisplayName("Mapa de departamentos vacío: procesa correctamente")
    void createAddress_withEmptyDepartmentsMap_processesCorrectly() throws Exception {
        Address address = createAddress();
        Map<String, GenericIdDescription> emptyMap = new HashMap<>();

        when(nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(NON_BUSINESS_ID_VALID))
                .thenReturn(mockDatatype);
        when(dao.getDepartments()).thenReturn(emptyMap);
        when(addressUtil.getAddressDatatypeFromAddress(address, emptyMap))
                .thenReturn(addressDatatype);

        nonBusinessesApiService.createAddress(NON_BUSINESS_ID_VALID, address);

        verify(addressUtil).getAddressDatatypeFromAddress(address, emptyMap);
        verify(dao).createAddress(mockDatatype, addressDatatype);
    }
}