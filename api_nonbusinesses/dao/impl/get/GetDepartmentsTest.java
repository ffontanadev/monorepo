package uy.com.bbva.services.nonbusinesses.dao.impl.get;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.dtos.commons.model.GenericIdDescription;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.services.commons.dao.ManagerDataAccessAs400;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.dao.SQLStatements;
import uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite de pruebas de DAOImpl.getDepartments():")
class GetDepartmentsTest {

    @Mock
    private ManagerDataAccessAs400 managerDataAccessAs400;

    @Mock
    private LogUtils logUtils;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private DAOImpl daoImpl;

    private static final String ISO_CODE_1 = "MVD";
    private static final String ISO_CODE_2 = "CAN";
    private static final String ISO_CODE_3 = "MAL";
    private static final Integer BT_CODE_1 = 1;
    private static final Integer BT_CODE_2 = 2;
    private static final Integer BT_CODE_3 = 3;
    private static final String NAME_1 = "Rio Negro";
    private static final String NAME_2 = "Canelones";
    private static final String NAME_3 = "Maldonado";
    private static final String NAME_WITH_SPACES = "  San José  ";
    private static final String ISO_CODE_WITH_SPACES = "  SJO  ";

    private static final String ERROR_MESSAGE = "Error al obtener los departamentos";
    private static final String DAO_CLASS_NAME = "uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl";
    private static final String DB_CONNECTION_ERROR = "Database connection error";
    private static final String QUERY_EXECUTION_ERROR = "Query execution failed";
    private static final String RESULT_PROCESSING_ERROR = "Error processing result set";

    @Test
    @DisplayName("Debe obtener los departamentos correctamente cuando hay varios departamentos ")
    void getDepartments_shouldReturnDepartmentsMap_whenMultipleRecordsExist() throws Exception {
        setupMultipleDepartments();

        Map<String, GenericIdDescription> result = daoImpl.getDepartments();

        assertNotNull(result);
        assertEquals(3, result.size());
        verifyDepartmentData(result);
        verifyResourceManagement();
    }

    @Test
    @DisplayName("Debe retornar mapa vacío cuando no hay departamentos")
    void getDepartments_shouldReturnEmptyMap_whenNoRecordsExist() throws Exception {
        setupNoDepartments();

        Map<String, GenericIdDescription> result = daoImpl.getDepartments();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyResourceManagement();
    }

    @Test
    @DisplayName("Debe manejar un solo departamento correctamente")
    void getDepartments_shouldReturnSingleDepartment_whenOneRecordExists() throws Exception {
        setupSingleDepartment();

        Map<String, GenericIdDescription> result = daoImpl.getDepartments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(ISO_CODE_1));
        assertEquals(String.valueOf(BT_CODE_1), result.get(ISO_CODE_1).getId());
        assertEquals(NAME_1, result.get(ISO_CODE_1).getDescription());
        verifyResourceManagement();
    }

    @Test
    @DisplayName("Debe trimear espacios en blanco de los códigos ISO y nombres")
    void getDepartments_shouldTrimWhitespace_fromIsoCodeAndNames() throws Exception {
        setupDepartmentWithSpaces();

        Map<String, GenericIdDescription> result = daoImpl.getDepartments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("SJO"));
        assertEquals("San José", result.get("SJO").getDescription());
        verifyResourceManagement();
    }

    @Test
    @DisplayName("Lanza ServiceException cuando hay un SQLException en la preparación del statement")
    void getDepartments_shouldThrowServiceException_whenSQLExceptionOnPrepareStatement() throws Exception {
        SQLException sqlException = new SQLException(DB_CONNECTION_ERROR);
        when(managerDataAccessAs400.prepareStatement(SQLStatements.GET_DEPARTMENTS))
                .thenThrow(sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> daoImpl.getDepartments());

        verifyServiceException(exception, sqlException);
        verify(managerDataAccessAs400).closeResources(null, null);
    }

    @Test
    @DisplayName("Lanza ServiceException cuando hay un SQLException en la ejecución de la consulta")
    void getDepartments_shouldThrowServiceException_whenSQLExceptionOnExecuteQuery() throws Exception {
        SQLException sqlException = new SQLException(QUERY_EXECUTION_ERROR);
        setupPreparedStatementMock();
        when(preparedStatement.executeQuery()).thenThrow(sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> daoImpl.getDepartments());

        verifyServiceException(exception, sqlException);
        verify(managerDataAccessAs400).closeResources(preparedStatement, null);
    }

    @Test
    @DisplayName("Lanza ServiceException cuando hay un SQLException al procesar el ResultSet")
    void getDepartments_shouldThrowServiceException_whenSQLExceptionOnResultSetProcessing() throws Exception {
        SQLException sqlException = new SQLException(RESULT_PROCESSING_ERROR);
        setupPreparedStatementMock();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenThrow(sqlException);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> daoImpl.getDepartments());

        verifyServiceException(exception, sqlException);
        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Debe cerrar recursos incluso cuando ocurre una excepción")
    void getDepartments_shouldCloseResources_whenExceptionOccurs() throws Exception {
        SQLException sqlException = new SQLException("Generic error");
        setupPreparedStatementMock();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("DEPISOCOD")).thenThrow(sqlException);

        assertThrows(ServiceException.class, () -> daoImpl.getDepartments());

        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Debe manejar departamentos con mismo código ISO (último gana)")
    void getDepartments_shouldHandleDuplicateIsoCodes_lastOneWins() throws Exception {
        setupDuplicateIsoCodes();

        Map<String, GenericIdDescription> result = daoImpl.getDepartments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(ISO_CODE_1));

        // se sobreescribe???
        assertEquals(String.valueOf(99), result.get(ISO_CODE_1).getId());
        assertEquals("Updated Name", result.get(ISO_CODE_1).getDescription());
        verifyResourceManagement();
    }

    private void setupPreparedStatementMock() throws SQLException {
        when(managerDataAccessAs400.prepareStatement(SQLStatements.GET_DEPARTMENTS))
                .thenReturn(preparedStatement);
    }

    private void setupMultipleDepartments() throws SQLException {
        setupPreparedStatementMock();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, true, false);

        when(resultSet.getString("DEPISOCOD"))
                .thenReturn(ISO_CODE_1, ISO_CODE_2, ISO_CODE_3);
        when(resultSet.getInt("DEPBTCOD"))
                .thenReturn(BT_CODE_1, BT_CODE_2, BT_CODE_3);
        when(resultSet.getString("DEPNOM"))
                .thenReturn(NAME_1, NAME_2, NAME_3);
    }

    private void setupNoDepartments() throws SQLException {
        setupPreparedStatementMock();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
    }

    private void setupSingleDepartment() throws SQLException {
        setupPreparedStatementMock();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("DEPISOCOD")).thenReturn(ISO_CODE_1);
        when(resultSet.getInt("DEPBTCOD")).thenReturn(BT_CODE_1);
        when(resultSet.getString("DEPNOM")).thenReturn(NAME_1);
    }

    private void setupDepartmentWithSpaces() throws SQLException {
        setupPreparedStatementMock();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("DEPISOCOD")).thenReturn(ISO_CODE_WITH_SPACES);
        when(resultSet.getInt("DEPBTCOD")).thenReturn(4);
        when(resultSet.getString("DEPNOM")).thenReturn(NAME_WITH_SPACES);
    }

    private void setupDuplicateIsoCodes() throws SQLException {
        setupPreparedStatementMock();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);

        when(resultSet.getString("DEPISOCOD")).thenReturn(ISO_CODE_1, ISO_CODE_1);
        when(resultSet.getInt("DEPBTCOD")).thenReturn(BT_CODE_1, 99);
        when(resultSet.getString("DEPNOM")).thenReturn(NAME_1, "Updated Name");
    }

    private void verifyDepartmentData(Map<String, GenericIdDescription> result) {
        assertTrue(result.containsKey(ISO_CODE_1));
        assertEquals(String.valueOf(BT_CODE_1), result.get(ISO_CODE_1).getId());
        assertEquals(NAME_1, result.get(ISO_CODE_1).getDescription());

        assertTrue(result.containsKey(ISO_CODE_2));
        assertEquals(String.valueOf(BT_CODE_2), result.get(ISO_CODE_2).getId());
        assertEquals(NAME_2, result.get(ISO_CODE_2).getDescription());

        assertTrue(result.containsKey(ISO_CODE_3));
        assertEquals(String.valueOf(BT_CODE_3), result.get(ISO_CODE_3).getId());
        assertEquals(NAME_3, result.get(ISO_CODE_3).getDescription());
    }

    private void verifyResourceManagement() throws SQLException {
        verify(managerDataAccessAs400).prepareStatement(SQLStatements.GET_DEPARTMENTS);
        verify(preparedStatement).executeQuery();
        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    private void verifyServiceException(ServiceException exception, SQLException sqlException) {
        assertEquals(ERROR_MESSAGE, exception.getInternalMessage());
        assertEquals(sqlException, exception.getCause());
        verify(logUtils).logError(
                eq(DAO_CLASS_NAME),
                eq(ERROR_MESSAGE),
                eq(ERROR_MESSAGE),
                eq(sqlException)
        );
    }
}