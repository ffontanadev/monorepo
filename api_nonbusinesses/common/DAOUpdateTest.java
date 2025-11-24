package uy.com.bbva.services.nonbusinesses.common;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.services.commons.dao.ManagerDataAccessAs400;
import uy.com.bbva.services.commons.exceptions.ServiceException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Clase base abstracta para pruebas de operaciones UPDATE en DAO.
 * Encapsula la lógica común de configuración, ejecución y verificación.
 */
@ExtendWith(MockitoExtension.class)
public abstract class DAOUpdateTest {

    @Mock
    public ManagerDataAccessAs400 managerDataAccessAs400;

    @Mock
    public LogUtils logUtils;

    @Mock
    public PreparedStatement preparedStatement;

    public static final int SINGLE_ROW_UPDATED = 1;
    public static final int ZERO_ROWS_UPDATED = 0;

    public static final String DB_CONNECTION_ERROR = "Database connection error";
    public static final String UPDATE_FAILED_ERROR = "Update failed";
    public static final String PARAMETER_SETTING_ERROR = "Parameter setting failed";

    /**
     * Retorna la sentencia SQL específica para cada operación de actualización.
     */
    public abstract String getSqlStatement();

    /**
     * Retorna el mensaje de error interno esperado para ServiceException.
     */
    public abstract String getErrorMessage();

    /**
     * Retorna el nombre completo de la clase DAO.
     */
    public abstract String getDaoClassName();

    /**
     * Configura los parámetros del PreparedStatement según el caso de prueba específico.
     */
    public abstract void setStatementParameters(PreparedStatement ps, Object... params) throws SQLException;

    /**
     * Verifica que los parámetros del PreparedStatement fueron configurados correctamente.
     */
    public abstract void verifyStatementParameters(Object... params) throws SQLException;

    /**
     * Configuración exitosa del mock de PreparedStatement con número de filas afectadas.
     */
    public void setupSuccessfulUpdate(int rowsAffected) throws SQLException {
        when(managerDataAccessAs400.prepareStatement(getSqlStatement()))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(rowsAffected);
    }

    /**
     * Configuración exitosa del mock de PreparedStatement.
     */
    public void setupSuccessfulUpdate() throws SQLException {
        when(managerDataAccessAs400.prepareStatement(getSqlStatement()))
                .thenReturn(preparedStatement);
    }

    /**
     * Configuración del mock para lanzar SQLException en la preparación del statement.
     */
    public void setupPrepareStatementException(SQLException exception) throws SQLException {
        when(managerDataAccessAs400.prepareStatement(getSqlStatement()))
                .thenThrow(exception);
    }

    /**
     * Configuración del mock para lanzar SQLException en la ejecución del update.
     */
    public void setupExecuteUpdateException(SQLException exception) throws SQLException {
        when(managerDataAccessAs400.prepareStatement(getSqlStatement()))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(exception);
    }

    /**
     * Configuración del mock para lanzar SQLException al setear parámetros.
     */
    public void setupParameterSettingException(SQLException exception) throws SQLException {
        when(managerDataAccessAs400.prepareStatement(getSqlStatement()))
                .thenReturn(preparedStatement);
        doThrow(exception).when(preparedStatement).setString(anyInt(), anyString());
    }

    /**
     * Verifica que los recursos fueron cerrados correctamente después de una operación exitosa.
     */
    public void verifySuccessfulExecution(Object... params) throws SQLException {
        verify(managerDataAccessAs400).prepareStatement(getSqlStatement());
        verifyStatementParameters(params);
        verify(preparedStatement).executeUpdate();
        verify(managerDataAccessAs400).closeResources(preparedStatement, null);
    }

    /**
     * Verifica que se lanzó correctamente una ServiceException con los detalles esperados.
     */
    public void verifyServiceException(
            ServiceException exception,
            SQLException sqlException,
            String expectedSqlErrorMessage) {

        assertEquals(getErrorMessage(), exception.getInternalMessage());
        assertEquals(sqlException, exception.getCause());
        verify(logUtils).logError(
                eq(getDaoClassName()),
                eq(getErrorMessage()),
                eq(expectedSqlErrorMessage)
        );
    }

    /**
     * Verifica que los recursos fueron cerrados incluso cuando ocurrió una excepción.
     */
    public void verifyResourceClosedOnException() throws SQLException {
        verify(managerDataAccessAs400).closeResources(preparedStatement, null);
    }

    /**
     * Verifica que se intentó cerrar recursos cuando falló la preparación del statement.
     */
    public void verifyResourceClosedOnPrepareFailure() throws SQLException {
        verify(managerDataAccessAs400).closeResources(null, null);
    }
}