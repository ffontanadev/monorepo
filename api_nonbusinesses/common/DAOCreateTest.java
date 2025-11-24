package uy.com.bbva.services.nonbusinesses.common;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.services.commons.dao.ManagerDataAccessAs400;
import uy.com.bbva.services.commons.exceptions.ServiceException;

import java.sql.CallableStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Clase base abstracta para pruebas de operaciones CREATE en DAO.
 * Encapsula la lógica común de configuración, ejecución y verificación.
 */
@ExtendWith(MockitoExtension.class)
public abstract class DAOCreateTest {

    @Mock
    public ManagerDataAccessAs400 managerDataAccessAs400;

    @Mock
    public LogUtils logUtils;

    @Mock
    public CallableStatement callableStatement;

    // Mensajes de error de base de datos
    public static final String DB_CONNECTION_ERROR = "Database connection error";
    public static final String INSERT_FAILED_ERROR = "Insert failed";
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
     * Configura los parámetros del CallableStatement según el caso de prueba específico.
     */
    public abstract void setStatementParameters(CallableStatement cs, Object... params) throws SQLException;

    /**
     * Verifica que los parámetros del CallableStatement fueron configurados correctamente.
     */
    public abstract void verifyStatementParameters(Object... params) throws SQLException;

    /**
     * Configuración exitosa del mock de CallableStatement.
     */
    public void setupSuccessfulCreate() throws SQLException {
        when(managerDataAccessAs400.prepareCall(getSqlStatement()))
                .thenReturn(callableStatement);
    }

    /**
     * Configuración del mock para lanzar SQLException en la preparación del statement.
     */
    public void setupPrepareCallException(SQLException exception) throws SQLException {
        when(managerDataAccessAs400.prepareCall(getSqlStatement()))
                .thenThrow(exception);
    }

    /**
     * Configuración del mock para lanzar SQLException en la ejecución del update.
     */
    public void setupExecuteException(SQLException exception) throws SQLException {
        when(managerDataAccessAs400.prepareCall(getSqlStatement()))
                .thenReturn(callableStatement);
        doThrow(exception).when(callableStatement).execute();
    }

    /**
     * Configuración del mock para lanzar SQLException al setear parámetros.
     */
    public void setupParameterSettingException(SQLException exception) throws SQLException {
        when(managerDataAccessAs400.prepareCall(getSqlStatement()))
                .thenReturn(callableStatement);
        doThrow(exception).when(callableStatement).setInt(anyInt(), anyInt());
    }

    /**
     * Verifica que los recursos fueron cerrados correctamente después de una operación exitosa.
     */
    public void verifySuccessfulExecution(Object... params) throws SQLException {
        verify(managerDataAccessAs400).prepareCall(getSqlStatement());
        verifyStatementParameters(params);
        verify(callableStatement).execute();
        verify(managerDataAccessAs400).closeResources(callableStatement, null);
    }

    /**
     * Verifica la construccion de ServiceException.
     */
    public void verifyServiceException(
            ServiceException exception,
            SQLException sqlException,
            String expectedSqlErrorMessage) {

        assertEquals(getErrorMessage(), exception.getInternalMessage());
        assertEquals(sqlException, exception.getCause());

        try {
            verify(logUtils, atLeastOnce()).logError(
                    eq(getDaoClassName()),
                    eq(getErrorMessage()),
                    anyString(),
                    any(SQLException.class)
            );
        } catch (AssertionError e) {
            verify(logUtils, atLeastOnce()).logError(
                    eq(getDaoClassName()),
                    eq(getErrorMessage()),
                    anyString()
            );
        }
    }
    /**
     * Verifica que los recursos se cierren inclusive en casos de excepción.
     */
    public void verifyResourceClosedOnException() {
        verify(managerDataAccessAs400).closeResources(callableStatement, null);
    }

    /**
     * Verifica que los recursos se cierren cuando hay excepción durante la preparacion del statement.
     */
    public void verifyResourceClosedOnPrepareFailure() {
        verify(managerDataAccessAs400).closeResources(null, null);
    }
}