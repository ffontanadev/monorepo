package uy.com.bbva.services.nonbusinesses.dao.impl.get;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.dtos.commons.v1.model.RelatedPerson;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite de pruebas de DAOImpl.getOwner():")
class GetOwnerTest {

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

    @Mock
    private NonBusinessIdDatatype nonBusinessIdDatatype;

    private static final String VALID_PERSON_DOCUMENT = "12345678";
    private static final int VALID_PERSON_COUNTRY = 598;
    private static final int VALID_PERSON_DOCUMENT_TYPE = 1;
    private static final String ALTERNATIVE_PERSON_DOCUMENT = "87654321";
    private static final int ALTERNATIVE_PERSON_COUNTRY = 599;
    private static final int ALTERNATIVE_PERSON_DOCUMENT_TYPE = 2;
    private static final String FIRST_NAME_VALUE = "Juan";
    private static final String MIDDLE_NAME_VALUE = "Carlos";
    private static final String LAST_NAME_VALUE = "Perez";
    private static final String SECOND_LAST_NAME_VALUE = "Rodriguez";
    private static final String FIRST_NAME_WITH_SPACES = "  Juan  ";
    private static final String MIDDLE_NAME_WITH_SPACES = "  Carlos  ";
    private static final String LAST_NAME_WITH_SPACES = "  Perez  ";
    private static final String SECOND_LAST_NAME_WITH_SPACES = "  Rodriguez  ";
    private static final String EMPTY_STRING = "";
    private static final String ERROR_CLASS_NAME = "uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl";
    private static final String ERROR_GET_OWNER = "Ocurrio un error al obtener los datos del dueño de la unipersonal";
    private static final String OWNER_NOT_FOUND_MESSAGE = "Owner not found";

    @BeforeEach
    void setUp() {
        // Lanza StubbingException en los tests de SQLException, lenient previene errores en los casos que no se usa el stub
        lenient().when(nonBusinessIdDatatype.getPersonCountry()).thenReturn(VALID_PERSON_COUNTRY);
        lenient().when(nonBusinessIdDatatype.getPersonDocument()).thenReturn(VALID_PERSON_DOCUMENT);
        lenient().when(nonBusinessIdDatatype.getPersonDocumentType()).thenReturn(VALID_PERSON_DOCUMENT_TYPE);

    }

    @Test
    @DisplayName("Happy Path: retorna RelatedPerson exitosamente con todos los datos")
    void getOwner_returnsRelatedPerson_withAllData() throws Exception {
        when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCPFNOM1")).thenReturn(FIRST_NAME_VALUE);
        when(resultSet.getString("BBNCPFNOM2")).thenReturn(MIDDLE_NAME_VALUE);
        when(resultSet.getString("BBNCPFAPE1")).thenReturn(LAST_NAME_VALUE);
        when(resultSet.getString("BBNCPFAPE2")).thenReturn(SECOND_LAST_NAME_VALUE);

        RelatedPerson result = dao.getOwner(nonBusinessIdDatatype);

        assertNotNull(result);
        assertEquals(FIRST_NAME_VALUE, result.getFirstName());
        assertEquals(MIDDLE_NAME_VALUE, result.getMiddleName());
        assertEquals(LAST_NAME_VALUE, result.getLastName());
        assertEquals(SECOND_LAST_NAME_VALUE, result.getSecondLastName());

        verify(preparedStatement).setString(1, VALID_PERSON_DOCUMENT);
        verify(preparedStatement).setInt(2, VALID_PERSON_COUNTRY);
        verify(preparedStatement).setInt(3, VALID_PERSON_DOCUMENT_TYPE);
        verify(preparedStatement).executeQuery();
        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Happy Path: retorna RelatedPerson con datos parciales cuando algunos campos son nulos")
    void getOwner_returnsRelatedPerson_withPartialData() throws Exception {
        when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCPFNOM1")).thenReturn(FIRST_NAME_VALUE);
        when(resultSet.getString("BBNCPFNOM2")).thenReturn(null);
        when(resultSet.getString("BBNCPFAPE1")).thenReturn(LAST_NAME_VALUE);
        when(resultSet.getString("BBNCPFAPE2")).thenReturn(null);

        RelatedPerson result = dao.getOwner(nonBusinessIdDatatype);

        assertNotNull(result);
        assertEquals(FIRST_NAME_VALUE, result.getFirstName());
        assertEquals(EMPTY_STRING, result.getMiddleName());
        assertEquals(LAST_NAME_VALUE, result.getLastName());
        assertEquals(EMPTY_STRING, result.getSecondLastName());

        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Happy Path: retorna RelatedPerson con datos trimmed cuando hay espacios")
    void getOwner_returnsRelatedPerson_withTrimmedData() throws Exception {
        when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("BBNCPFNOM1")).thenReturn(FIRST_NAME_WITH_SPACES);
        when(resultSet.getString("BBNCPFNOM2")).thenReturn(MIDDLE_NAME_WITH_SPACES);
        when(resultSet.getString("BBNCPFAPE1")).thenReturn(LAST_NAME_WITH_SPACES);
        when(resultSet.getString("BBNCPFAPE2")).thenReturn(SECOND_LAST_NAME_WITH_SPACES);

        RelatedPerson result = dao.getOwner(nonBusinessIdDatatype);

        assertNotNull(result);
        assertEquals(FIRST_NAME_VALUE, result.getFirstName());
        assertEquals(MIDDLE_NAME_VALUE, result.getMiddleName());
        assertEquals(LAST_NAME_VALUE, result.getLastName());
        assertEquals(SECOND_LAST_NAME_VALUE, result.getSecondLastName());

        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Verifica orden correcto de parámetros en PreparedStatement")
    void getOwner_setsParametersInCorrectOrder() throws Exception {
        when(nonBusinessIdDatatype.getPersonDocument()).thenReturn(ALTERNATIVE_PERSON_DOCUMENT);
        when(nonBusinessIdDatatype.getPersonCountry()).thenReturn(ALTERNATIVE_PERSON_COUNTRY);
        when(nonBusinessIdDatatype.getPersonDocumentType()).thenReturn(ALTERNATIVE_PERSON_DOCUMENT_TYPE);
        when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(anyString())).thenReturn(FIRST_NAME_VALUE);

        dao.getOwner(nonBusinessIdDatatype);

        var inOrder = inOrder(preparedStatement);
        inOrder.verify(preparedStatement).setString(1, ALTERNATIVE_PERSON_DOCUMENT);
        inOrder.verify(preparedStatement).setInt(2, ALTERNATIVE_PERSON_COUNTRY);
        inOrder.verify(preparedStatement).setInt(3, ALTERNATIVE_PERSON_DOCUMENT_TYPE);
        inOrder.verify(preparedStatement).executeQuery();
    }

    @Nested
    @DisplayName("Manejo de errores")
    class ErrorHandling {

        @Test
        @DisplayName("Lanza ServiceException cuando no se encuentra el owner")
        void getOwner_throwsServiceException_whenOwnerNotFound() throws Exception {
            when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false); // No owner found

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> dao.getOwner(nonBusinessIdDatatype));

            assertEquals(ERROR_CLASS_NAME, exception.getMessage());
            assertEquals(ERROR_GET_OWNER, exception.getInternalMessage());
            assertInstanceOf(Exception.class, exception.getCause());
            assertEquals(OWNER_NOT_FOUND_MESSAGE, exception.getCause().getMessage());
            verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
        }

        @Test
        @DisplayName("Lanza ServiceException cuando ocurre SQLException al preparar statement")
        void getOwner_throwsServiceException_whenSqlExceptionOnPrepareStatement() throws Exception {
            SQLException sqlException = new SQLException("Database connection failed");
            when(managerDataAccessAs400.prepareStatement(anyString())).thenThrow(sqlException);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> dao.getOwner(nonBusinessIdDatatype));

            assertEquals(ERROR_CLASS_NAME, exception.getMessage());
            assertEquals(ERROR_GET_OWNER, exception.getInternalMessage());
            assertSame(sqlException, exception.getCause());
            verify(logUtils).logError(eq(ERROR_CLASS_NAME), eq(ERROR_GET_OWNER), eq(ERROR_GET_OWNER), eq(sqlException));
            verify(managerDataAccessAs400).closeResources(null, null);
        }

        @Test
        @DisplayName("Lanza ServiceException cuando ocurre SQLException al setear parámetros string")
        void getOwner_throwsServiceException_whenSqlExceptionOnSetString() throws Exception {
            SQLException sqlException = new SQLException("Parameter setting failed");
            when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
            doThrow(sqlException).when(preparedStatement).setString(eq(1), any()); // Changed from anyString()

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> dao.getOwner(nonBusinessIdDatatype));

            assertEquals(ERROR_CLASS_NAME, exception.getMessage());
            assertEquals(ERROR_GET_OWNER, exception.getInternalMessage());
            assertSame(sqlException, exception.getCause());
            verify(logUtils).logError(eq(ERROR_CLASS_NAME), eq(ERROR_GET_OWNER), eq(ERROR_GET_OWNER), eq(sqlException));
            verify(managerDataAccessAs400).closeResources(preparedStatement, null);
        }

        @Test
        @DisplayName("Lanza ServiceException cuando ocurre SQLException al setear parámetros int")
        void getOwner_throwsServiceException_whenSqlExceptionOnSetInt() throws Exception {
            SQLException sqlException = new SQLException("Parameter setting failed");
            when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
            doThrow(sqlException).when(preparedStatement).setInt(eq(2), anyInt());

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> dao.getOwner(nonBusinessIdDatatype));

            assertEquals(ERROR_CLASS_NAME, exception.getMessage());
            assertEquals(ERROR_GET_OWNER, exception.getInternalMessage());
            assertSame(sqlException, exception.getCause());
            verify(logUtils).logError(eq(ERROR_CLASS_NAME), eq(ERROR_GET_OWNER), eq(ERROR_GET_OWNER), eq(sqlException));
            verify(managerDataAccessAs400).closeResources(preparedStatement, null);
        }

        @Test
        @DisplayName("Lanza ServiceException cuando ocurre SQLException al ejecutar query")
        void getOwner_throwsServiceException_whenSqlExceptionOnExecuteQuery() throws Exception {
            SQLException sqlException = new SQLException("Query execution failed");
            when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenThrow(sqlException);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> dao.getOwner(nonBusinessIdDatatype));

            assertEquals(ERROR_CLASS_NAME, exception.getMessage());
            assertEquals(ERROR_GET_OWNER, exception.getInternalMessage());
            assertSame(sqlException, exception.getCause());
            verify(logUtils).logError(eq(ERROR_CLASS_NAME), eq(ERROR_GET_OWNER), eq(ERROR_GET_OWNER), eq(sqlException));
            verify(managerDataAccessAs400).closeResources(preparedStatement, null);
        }

        @Test
        @DisplayName("Lanza ServiceException cuando ocurre SQLException al verificar resultSet.next()")
        void getOwner_throwsServiceException_whenSqlExceptionOnResultSetNext() throws Exception {
            SQLException sqlException = new SQLException("ResultSet navigation failed");
            when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenThrow(sqlException);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> dao.getOwner(nonBusinessIdDatatype));

            assertEquals(ERROR_CLASS_NAME, exception.getMessage());
            assertEquals(ERROR_GET_OWNER, exception.getInternalMessage());
            assertSame(sqlException, exception.getCause());
            verify(logUtils).logError(eq(ERROR_CLASS_NAME), eq(ERROR_GET_OWNER), eq(ERROR_GET_OWNER), eq(sqlException));
            verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
        }

        @Test
        @DisplayName("Lanza ServiceException cuando ocurre SQLException al obtener datos del ResultSet")
        void getOwner_throwsServiceException_whenSqlExceptionOnGetString() throws Exception {
            SQLException sqlException = new SQLException("Data retrieval failed");
            when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getString("BBNCPFNOM1")).thenThrow(sqlException);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> dao.getOwner(nonBusinessIdDatatype));

            assertEquals(ERROR_CLASS_NAME, exception.getMessage());
            assertEquals(ERROR_GET_OWNER, exception.getInternalMessage());
            assertSame(sqlException, exception.getCause());
            verify(logUtils).logError(eq(ERROR_CLASS_NAME), eq(ERROR_GET_OWNER), eq(ERROR_GET_OWNER), eq(sqlException));
            verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
        }
    }

    @Test
    @DisplayName("Cierra recursos correctamente si hay excepcion durante la consulta")
    void getOwner_closesResources_whenExceptionDuringExecution() throws Exception {
        when(managerDataAccessAs400.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenThrow(new SQLException("Test exception"));

        assertThrows(ServiceException.class, () -> dao.getOwner(nonBusinessIdDatatype));

        verify(managerDataAccessAs400).closeResources(preparedStatement, resultSet);
    }

    @Test
    @DisplayName("Cierra recursos correctamente si falla la preparación del statement")
    void getOwner_closesResources_whenPrepareStatementFails() throws Exception {
        when(managerDataAccessAs400.prepareStatement(anyString())).thenThrow(new SQLException("Test exception"));

        assertThrows(ServiceException.class, () -> dao.getOwner(nonBusinessIdDatatype));

        verify(managerDataAccessAs400).closeResources(null, null);
    }
}