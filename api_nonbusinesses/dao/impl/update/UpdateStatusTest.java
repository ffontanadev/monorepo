package uy.com.bbva.services.nonbusinesses.dao.impl.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.commons.dao.ManagerDataAccessAs400;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.common.TestDataFactory;
import uy.com.bbva.services.nonbusinesses.dao.SQLStatements;
import uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl;

import java.sql.CallableStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static uy.com.bbva.dtos.commons.utils.Constants.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

/**
 * Pruebas unitarias para el método updateStatus de DAOImpl.
 * Verifica la correcta actualización del estado de un no-cliente en la base de datos.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de actualización de estado de no-cliente")
@SuppressWarnings("squid:S2095")
class UpdateStatusTest {

    @Mock
    private ManagerDataAccessAs400 managerDataAccessAs400;

    @Mock
    private LogUtils logUtils;

    @Mock
    private CallableStatement callableStatement;

    @InjectMocks
    private DAOImpl daoImpl;

    private NonBusinessIdDatatype nonBusinessDatatype;
    private static final String ERROR_UPDATE_STATUS = "Ocurrio un error al actualizar el estado";

    @BeforeEach
    void setUp() {
        nonBusinessDatatype = TestDataFactory.createBusinessWithPersonDatatype();
    }

    @Test
    @DisplayName("Debe actualizar el estado exitosamente con todos los campos válidos")
    void testUpdateStatus_Success_WithValidData() throws SQLException {
        String status = STATUS_ID_DGI_OK;
        when(managerDataAccessAs400.prepareCall(SQLStatements.UPDATE_STATUS))
                .thenReturn(callableStatement);
        when(callableStatement.execute()).thenReturn(true);

        assertDoesNotThrow(() -> daoImpl.updateStatus(nonBusinessDatatype, status));

        verify(managerDataAccessAs400).prepareCall(SQLStatements.UPDATE_STATUS);
        verify(callableStatement).setString(1, status);
        verify(callableStatement).setInt(2, UY_COUNTRY_CODE);
        verify(callableStatement).setInt(3, USER_DOCUMENT_TYPE_CI);
        verify(callableStatement).setString(4, PERSON_DOCUMENT_VALID);
        verify(callableStatement).setInt(5, UY_COUNTRY_CODE);
        verify(callableStatement).setInt(6, RUT_DOCUMENT_TYPE);
        verify(callableStatement).setString(7, BUSINESS_RUT_VALID);
        verify(callableStatement).execute();
        verify(managerDataAccessAs400).closeResources(callableStatement);
    }

    @Test
    @DisplayName("Debe actualizar exitosamente con estado PROCESADO")
    void testUpdateStatus_Success_WithProcessedStatus() throws SQLException {
        String status = STATUS_ID_PROCESADO;
        when(managerDataAccessAs400.prepareCall(SQLStatements.UPDATE_STATUS))
                .thenReturn(callableStatement);
        when(callableStatement.execute()).thenReturn(true);

        assertDoesNotThrow(() -> daoImpl.updateStatus(nonBusinessDatatype, status));

        verify(callableStatement).setString(1, status);
        verify(callableStatement).execute();
        verify(managerDataAccessAs400).closeResources(callableStatement);
    }

    @Test
    @DisplayName("Debe actualizar exitosamente con estado ANULADO")
    void testUpdateStatus_Success_WithCancelledStatus() throws SQLException {
        String status = STATUS_ID_ANULADO;
        when(managerDataAccessAs400.prepareCall(SQLStatements.UPDATE_STATUS))
                .thenReturn(callableStatement);
        when(callableStatement.execute()).thenReturn(true);

        assertDoesNotThrow(() -> daoImpl.updateStatus(nonBusinessDatatype, status));

        verify(callableStatement).setString(1, status);
        verify(callableStatement).execute();
        verify(managerDataAccessAs400).closeResources(callableStatement);
    }

    @Test
    @DisplayName("Debe manejar estado vacío correctamente")
    void testUpdateStatus_Success_WithEmptyStatus() throws SQLException {
        String status = "";
        when(managerDataAccessAs400.prepareCall(SQLStatements.UPDATE_STATUS))
                .thenReturn(callableStatement);
        when(callableStatement.execute()).thenReturn(true);

        assertDoesNotThrow(() -> daoImpl.updateStatus(nonBusinessDatatype, status));

        verify(callableStatement).setString(1, status);
        verify(callableStatement).execute();
        verify(managerDataAccessAs400).closeResources(callableStatement);
    }

    @Test
    @DisplayName("Debe lanzar ServiceException cuando falla la preparación del statement")
    void testUpdateStatus_ThrowsServiceException_WhenPrepareCallFails() throws SQLException {
        SQLException sqlException = new SQLException(ERROR_DATABASE);

        when(managerDataAccessAs400.prepareCall(SQLStatements.UPDATE_STATUS))
                .thenThrow(sqlException);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> daoImpl.updateStatus(nonBusinessDatatype, STATUS_ID_DGI_OK)
        );

        assertEquals(ERROR_UPDATE_STATUS, exception.getInternalMessage());
        assertEquals(sqlException, exception.getCause());
        verify(logUtils).logError(DAO_CLASS_NAME, ERROR_UPDATE_STATUS, ERROR_DATABASE
        );
        verify(managerDataAccessAs400).closeResources((CallableStatement) null);
    }

    @Test
    @DisplayName("Debe lanzar ServiceException cuando falla la ejecución del statement")
    void testUpdateStatus_ThrowsServiceException_WhenExecuteFails() throws SQLException {
        SQLException sqlException = new SQLException(ERROR_EXECUTION);

        when(managerDataAccessAs400.prepareCall(SQLStatements.UPDATE_STATUS))
                .thenReturn(callableStatement);
        when(callableStatement.execute()).thenThrow(sqlException);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> daoImpl.updateStatus(nonBusinessDatatype, STATUS_ID_DGI_OK)
        );

        assertEquals(ERROR_UPDATE_STATUS, exception.getInternalMessage());
        assertEquals(sqlException, exception.getCause());
        verify(logUtils).logError(
                DAO_CLASS_NAME,
                ERROR_UPDATE_STATUS,
                ERROR_EXECUTION
        );
        verify(managerDataAccessAs400).closeResources(callableStatement);
    }

    @Test
    @DisplayName("Debe lanzar ServiceException cuando falla el seteo de parámetros")
    void testUpdateStatus_ThrowsServiceException_WhenSettingParametersFails() throws SQLException {
        SQLException sqlException = new SQLException(ERROR_PARAMETER);
        when(managerDataAccessAs400.prepareCall(SQLStatements.UPDATE_STATUS))
                .thenReturn(callableStatement);
        doThrow(sqlException).when(callableStatement).setString(anyInt(), anyString());

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> daoImpl.updateStatus(nonBusinessDatatype, STATUS_ID_DGI_OK)
        );

        assertEquals(ERROR_UPDATE_STATUS, exception.getInternalMessage());
        assertEquals(sqlException, exception.getCause());
        verify(logUtils).logError(
                DAO_CLASS_NAME,
                ERROR_UPDATE_STATUS,
                ERROR_PARAMETER
        );
        verify(managerDataAccessAs400).closeResources(callableStatement);
    }

    @Test
    @DisplayName("Debe cerrar recursos incluso cuando ocurre excepción")
    void testUpdateStatus_ClosesResources_OnException() throws SQLException {
        SQLException sqlException = new SQLException(ERROR_DATABASE);

        when(managerDataAccessAs400.prepareCall(SQLStatements.UPDATE_STATUS))
                .thenReturn(callableStatement);
        doThrow(sqlException).when(callableStatement).execute();

        assertThrows(
                ServiceException.class,
                () -> daoImpl.updateStatus(nonBusinessDatatype, STATUS_ID_DGI_OK)
        );

        verify(managerDataAccessAs400).closeResources(callableStatement);
    }

    @Test
    @DisplayName("Debe manejar valores null en NonBusinessIdDatatype")
    void testUpdateStatus_Success_WithNullValuesInDatatype() throws SQLException {
        NonBusinessIdDatatype datatypeWithNulls = new NonBusinessIdDatatype();
        datatypeWithNulls.setBusinessDocument(null);
        datatypeWithNulls.setBusinessCountry(UY_COUNTRY_CODE);
        datatypeWithNulls.setBusinessDocumentType(RUT_DOCUMENT_TYPE);
        datatypeWithNulls.setPersonDocument(null);
        datatypeWithNulls.setPersonCountry(UY_COUNTRY_CODE);
        datatypeWithNulls.setPersonDocumentType(USER_DOCUMENT_TYPE_CI);

        String status = STATUS_ID_ERROR;

        when(managerDataAccessAs400.prepareCall(SQLStatements.UPDATE_STATUS))
                .thenReturn(callableStatement);
        when(callableStatement.execute()).thenReturn(true);

        assertDoesNotThrow(() -> daoImpl.updateStatus(datatypeWithNulls, status));

        verify(callableStatement).setString(1, status);
        verify(callableStatement).setString(4, null);
        verify(callableStatement).setString(7, null);
        verify(callableStatement).execute();
        verify(managerDataAccessAs400).closeResources(callableStatement);
    }

    @Test
    @DisplayName("Debe manejar correctamente un retorno false del execute")
    void testUpdateStatus_Success_WithExecuteReturnsFalse() throws SQLException {
        when(managerDataAccessAs400.prepareCall(SQLStatements.UPDATE_STATUS))
                .thenReturn(callableStatement);
        when(callableStatement.execute()).thenReturn(false);

        assertDoesNotThrow(() -> daoImpl.updateStatus(nonBusinessDatatype, STATUS_ID_NB_ADD_OK));

        verify(callableStatement).execute();
        verify(managerDataAccessAs400).closeResources(callableStatement);
    }
}