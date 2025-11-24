package uy.com.bbva.services.nonbusinesses.dao.impl.get;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.services.commons.dao.ManagerDataAccessAs400;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static uy.com.bbva.dtos.commons.utils.Constants.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite de pruebas de DAOImpl.nonBusinessOnFinalState():")
class GetStatusTest {

    @InjectMocks
    private DAOImpl dao;

    @Mock
    private ManagerDataAccessAs400 managerDataAccessAs400;

    @Mock
    private LogUtils logUtils;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private String TEST_CI = "12345678";
    private String TEST_RUT = "123456789012";

    @BeforeEach
    void resetMocks() {
        clearInvocations(managerDataAccessAs400, logUtils, preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Happy Path: retorna estado INGRESO cuando CI y RUT existen")
    void getStatus_returnsIngresoStatus_whenCiAndRutExist() throws Exception {
        String expectedStatus = "INGRESO";

        when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCEMESTA")).thenReturn(expectedStatus);

        String result = dao.getStatus(TEST_CI, TEST_RUT);

        assertEquals(expectedStatus, result, "Should return the actual status");
        verify(preparedStatement).setInt(1, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(2, USER_DOCUMENT_TYPE_CI);
        verify(preparedStatement).setString(3, TEST_CI);
        verify(preparedStatement).setInt(4, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(5, RUT_DOCUMENT_TYPE);
        verify(preparedStatement).setString(6, TEST_RUT);
        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Happy Path: retorna estado PRO cuando CI y RUT existen")
    void getStatus_returnsProStatus_whenCiAndRutExist() throws Exception {
        String expectedStatus = "PRO";

        when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCEMESTA")).thenReturn(expectedStatus);

        String result = dao.getStatus(TEST_CI, TEST_RUT);

        assertEquals(expectedStatus, result, "Should return the actual status");
        verify(preparedStatement).setInt(1, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(2, USER_DOCUMENT_TYPE_CI);
        verify(preparedStatement).setString(3, TEST_CI);
        verify(preparedStatement).setInt(4, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(5, RUT_DOCUMENT_TYPE);
        verify(preparedStatement).setString(6, TEST_RUT);
        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Happy Path: retorna estado ANU cuando CI y RUT existen")
    void getStatus_returnsAnuStatus_whenCiAndRutExist() throws Exception {
        String expectedStatus = "ANU";

        when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCEMESTA")).thenReturn(expectedStatus);

        String result = dao.getStatus(TEST_CI, TEST_RUT);

        assertEquals(expectedStatus, result, "Should return the actual status");
        verify(preparedStatement).setInt(1, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(2, USER_DOCUMENT_TYPE_CI);
        verify(preparedStatement).setString(3, TEST_CI);
        verify(preparedStatement).setInt(4, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(5, RUT_DOCUMENT_TYPE);
        verify(preparedStatement).setString(6, TEST_RUT);
        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Happy Path: retorna estado REJ cuando CI y RUT existen")
    void getStatus_returnsRejStatus_whenCiAndRutExist() throws Exception {
        String expectedStatus = "REJ";

        when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCEMESTA")).thenReturn(expectedStatus);

        String result = dao.getStatus(TEST_CI, TEST_RUT);

        assertEquals(expectedStatus, result, "Should return the actual status");
        verify(preparedStatement).setInt(1, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(2, USER_DOCUMENT_TYPE_CI);
        verify(preparedStatement).setString(3, TEST_CI);
        verify(preparedStatement).setInt(4, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(5, RUT_DOCUMENT_TYPE);
        verify(preparedStatement).setString(6, TEST_RUT);
        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }
    @Test
    @DisplayName("Retorna string vacío cuando CI y RUT no existen")
    void getStatus_returnsFalse_whenRutExistsButNotInFinalState() throws Exception {
        when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        String result = dao.getStatus(TEST_CI, TEST_RUT);

        assertEquals("", result, "Should return empty string when CI and RUT do not exist");
        verify(preparedStatement).setInt(1, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(2, USER_DOCUMENT_TYPE_CI);
        verify(preparedStatement).setString(3, TEST_CI);
        verify(preparedStatement).setInt(4, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(5, RUT_DOCUMENT_TYPE);
        verify(preparedStatement).setString(6, TEST_RUT);
        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
        verify(resultSet, never()).getString(anyString());
    }

    @Test
    @DisplayName("Retorna string vacio cuando se recibe null de la base de datos")
    void getStatus_returnsFalse_whenRutDoesNotExist() throws Exception {
        String rut = "999999999999";
        when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCEMESTA")).thenReturn(null);

        String result = dao.getStatus(TEST_CI, TEST_RUT);

        assertEquals("", result, "Should return empty string when db returns null");
        verify(preparedStatement).setInt(1, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(2, USER_DOCUMENT_TYPE_CI);
        verify(preparedStatement).setString(3, TEST_CI);
        verify(preparedStatement).setInt(4, UY_COUNTRY_CODE);
        verify(preparedStatement).setInt(5, RUT_DOCUMENT_TYPE);
        verify(preparedStatement).setString(6, TEST_RUT);
        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }
    @Nested
    @DisplayName("Manejo de errores")
    class ErrorHandling {

        @Test
        @DisplayName("Lanza ServiceException cuando ocurre SQLException al preparar statement")
        void getStatus_throwsServiceException_whenSqlExceptionOnPrepareStatement() throws Exception {
            String ci = "12345678";
            String rut = "123456789012";
            SQLException sqlException = new SQLException("Database connection failed");
            when(managerDataAccessAs400.prepareStatement(anyString())).thenThrow(sqlException);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> dao.getStatus(ci, rut));

            assertEquals("Error al chequear si el no cliente esta en estado final", exception.getInternalMessage());
            assertSame(sqlException, exception.getCause());

            verify(logUtils).logError(
                    eq("uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl"),
                    eq("Error al chequear si el no cliente esta en estado final"),
                    eq("Database connection failed"));
            verify(managerDataAccessAs400).closeResources(null, null);
        }

        @Test
        @DisplayName("Lanza ServiceException cuando ocurre SQLException al ejecutar query")
        void getStatus_throwsServiceException_whenSqlExceptionOnExecuteQuery() throws Exception {
            String ci = "12345678";
            String rut = "123456789012";
            SQLException sqlException = new SQLException("Query execution failed");
            when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenThrow(sqlException);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> dao.getStatus(ci, rut));

            assertEquals("Error al chequear si el no cliente esta en estado final", exception.getInternalMessage());
            assertSame(sqlException, exception.getCause());

            verify(logUtils).logError(
                    eq("uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl"),
                    eq("Error al chequear si el no cliente esta en estado final"),
                    eq("Query execution failed"));
            verify(managerDataAccessAs400).closeResources(preparedStatement, null);
        }

        @Test
        @DisplayName("Lanza ServiceException cuando ocurre SQLException al leer ResultSet")
        void getStatus_throwsServiceException_whenSqlExceptionOnResultSetAccess() throws Exception {
            String ci = "12345678";
            String rut = "123456789012";
            SQLException sqlException = new SQLException("ResultSet access failed");
            when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenThrow(sqlException);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> dao.getStatus(ci, rut));

            assertEquals("Error al chequear si el no cliente esta en estado final", exception.getInternalMessage());
            assertSame(sqlException, exception.getCause());

            verify(logUtils).logError(
                    eq("uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl"),
                    eq("Error al chequear si el no cliente esta en estado final"),
                    eq("ResultSet access failed"));
            verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
        }
    }

    @Nested
    @DisplayName("Casos edge con valores de entrada")
    class EdgeCases {

        @Test
        @DisplayName("Maneja CI vacío correctamente")
        void getStatus_handlesEmptyCi() throws Exception {
            String ci = "";
            String rut = "123456789012";
            when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            String result = dao.getStatus(ci, rut);

            assertEquals("", result);
            verify(preparedStatement).setString(3, "");
            verify(preparedStatement).setString(6, rut);
            verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
        }

        @Test
        @DisplayName("Maneja RUT vacío correctamente")
        void getStatus_handlesEmptyRut() throws Exception {
            String ci = "12345678";
            String rut = "";
            when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            String result = dao.getStatus(ci, rut);

            assertEquals("", result);
            verify(preparedStatement).setString(3, ci);
            verify(preparedStatement).setString(6, "");
            verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
        }

        @Test
        @DisplayName("Maneja CI y RUT con espacios correctamente")
        void getStatus_handlesCiAndRutWithSpaces() throws Exception {
            String ci = "  12345678  ";
            String rut = "  123456789012  ";
            String expectedStatus = "PRO";

            when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getString("BBNCEMESTA")).thenReturn(expectedStatus);

            String result = dao.getStatus(ci, rut);

            assertEquals(expectedStatus, result);
            verify(preparedStatement).setString(3, "  12345678  ");
            verify(preparedStatement).setString(6, "  123456789012  ");
            verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
        }

        @Test
        @DisplayName("Maneja CI y RUT null correctamente")
        void getStatus_handlesNullCiAndRut() throws Exception {
            String ci = null;
            String rut = null;
            when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            String result = dao.getStatus(ci, rut);

            assertEquals("", result);
            verify(preparedStatement).setString(3, null);
            verify(preparedStatement).setString(6, null);
            verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
        }
    }
}