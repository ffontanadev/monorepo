package uy.com.bbva.services.nonbusinesses.dao.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.commons.dao.ManagerDataAccessAs400;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.dao.SQLStatements;
import uy.com.bbva.services.nonbusinesses.model.status.Status;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

/**
 * Suite de pruebas para DAOImpl.auditStatusChange()
 * Valida la auditoría de cambios de estado en el sistema de no clientes.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Suite de pruebas de DAOImpl.auditStatusChange():")
class AuditStatusChangeTest {

    @InjectMocks
    private DAOImpl daoImpl;

    @Mock
    private ManagerDataAccessAs400 managerDataAccessAs400;

    @Mock
    private LogUtils logUtils;

    @Mock
    private CallableStatement callableStatement;

    private static final String ERROR_MESSAGE = "Ocurrio un error al auditar el cambio de estado";

    private static final int PARAM_INDEX_BUSINESS_COUNTRY = 1;
    private static final int PARAM_INDEX_BUSINESS_DOCUMENT_TYPE = 2;
    private static final int PARAM_INDEX_BUSINESS_DOCUMENT = 3;
    private static final int PARAM_INDEX_PERSON_COUNTRY = 4;
    private static final int PARAM_INDEX_PERSON_DOCUMENT_TYPE = 5;
    private static final int PARAM_INDEX_PERSON_DOCUMENT = 6;
    private static final int PARAM_INDEX_CHANNEL_ID = 7;
    private static final int PARAM_INDEX_EMPTY_STRING = 8;
    private static final int PARAM_INDEX_PROCESS = 9;
    private static final int PARAM_INDEX_STATUS_ID = 10;
    private static final int PARAM_INDEX_ZERO_INT = 11;
    private static final int PARAM_INDEX_MESSAGE = 12;

    private static final String EMPTY_STRING_PARAM = "";
    private static final int ZERO_INT_PARAM = 0;

    private NonBusinessIdDatatype nonBusinessDatatype;
    private Status status;

    private void verifyAllParametersSet(Status status) throws SQLException {
        verify(callableStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, nonBusinessDatatype.getBusinessCountry());
        verify(callableStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, nonBusinessDatatype.getBusinessDocumentType());
        verify(callableStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, nonBusinessDatatype.getBusinessDocument());
        verify(callableStatement).setInt(PARAM_INDEX_PERSON_COUNTRY, nonBusinessDatatype.getPersonCountry());
        verify(callableStatement).setInt(PARAM_INDEX_PERSON_DOCUMENT_TYPE, nonBusinessDatatype.getPersonDocumentType());
        verify(callableStatement).setString(PARAM_INDEX_PERSON_DOCUMENT, nonBusinessDatatype.getPersonDocument());
        verify(callableStatement).setInt(PARAM_INDEX_CHANNEL_ID, status.getChannelId());
        verify(callableStatement).setString(PARAM_INDEX_EMPTY_STRING, EMPTY_STRING_PARAM);
        verify(callableStatement).setString(PARAM_INDEX_PROCESS, status.getProcess());
        verify(callableStatement).setString(PARAM_INDEX_STATUS_ID, status.getId());
        verify(callableStatement).setInt(PARAM_INDEX_ZERO_INT, ZERO_INT_PARAM);
        verify(callableStatement).setString(PARAM_INDEX_MESSAGE, status.getMessage());
    }

    @BeforeEach
    void setUp() {
        nonBusinessDatatype = createBusinessWithPersonDatatype();
        status = createStatus();
    }

    // ========== Happy Path ==========

    @Test
    @DisplayName("Debe auditar el cambio de estado correctamente con valores válidos")
    void auditStatusChange_givenValidStatusAndDatatype_auditsSuccessfully() throws Exception {
        when(managerDataAccessAs400.prepareCall(SQLStatements.AUDIT_STATUS_CHANGE))
                .thenReturn(callableStatement);

        assertDoesNotThrow(() -> daoImpl.auditStatusChange(nonBusinessDatatype, status));

        verify(managerDataAccessAs400).prepareCall(SQLStatements.AUDIT_STATUS_CHANGE);
        verifyAllParametersSet(status);
        verify(callableStatement).execute();
        verify(managerDataAccessAs400).closeResources(callableStatement, null);
    }

    @ParameterizedTest(name = "[{index}] Debe auditar correctamente con status: {0}, proceso: {1}")
    @MethodSource("provideStatusVariations")
    @DisplayName("Debe auditar correctamente diferentes combinaciones de status y proceso")
    void auditStatusChange_givenDifferentStatusVariations_auditsSuccessfully(
            String statusId,
            String process,
            int channelId,
            String message,
            String displayDescription) throws Exception {

        status = createStatus(statusId, process, channelId, message);
        when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);

        assertDoesNotThrow(() -> daoImpl.auditStatusChange(nonBusinessDatatype, status));

        verifyAllParametersSet(status);
        verify(callableStatement).execute();

        assertEquals(channelId, status.getChannelId());
        assertEquals(process, status.getProcess());
        assertEquals(statusId, status.getId());
        assertEquals(message, status.getMessage());
    }

    private static Stream<Arguments> provideStatusVariations() {
        return Stream.of(
                Arguments.of(STATUS_ID_INGRESO, PROCESS_POST, CHANNEL_ID_GEMA, STATUS_MESSAGE_VALID,
                        "estado INGRESO en proceso POST"),
                Arguments.of(STATUS_ID_DGI_OK, PROCESS_SEARCH, CHANNEL_ID_GEMA, STATUS_MESSAGE_VALID,
                        "estado DGI_OK en proceso SEARCH"),
                Arguments.of(STATUS_ID_NB_CNT_OK, PROCESS_CONTACT, CHANNEL_ID_GEMA, STATUS_MESSAGE_VALID,
                        "estado contacto OK desde MOBILE"),
                Arguments.of(STATUS_ID_NB_ADD_OK, PROCESS_ADDRESS, CHANNEL_ID_GEMA, STATUS_MESSAGE_EMPTY,
                        "estado dirección OK con mensaje vacío"),
                Arguments.of(STATUS_ID_PROCESADO, PROCESS_BUSINESS, CHANNEL_ID_GEMA, STATUS_MESSAGE_LONG,
                        "estado PROCESADO con mensaje largo")
        );
    }


    /**
     * Solo GEMA pero facilmente ampliable.
     */
    @ParameterizedTest(name = "[{index}] Debe auditar con channel ID: {0}")
    @MethodSource("provideChannelIdVariations")
    @DisplayName("Debe auditar correctamente con diferentes IDs de canal")
    void auditStatusChange_givenDifferentChannelIds_auditsSuccessfully(
            int channelId,
            String displayDescription) throws Exception {

        status = Status.of(STATUS_ID_DGI_OK)
                .setProcess(PROCESS_SEARCH)
                .setMessage(STATUS_MESSAGE_VALID);


        when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);

        assertDoesNotThrow(() -> daoImpl.auditStatusChange(nonBusinessDatatype, status));

        verify(callableStatement).setInt(PARAM_INDEX_CHANNEL_ID, channelId);
        verify(callableStatement).execute();
    }

    private static Stream<Arguments> provideChannelIdVariations() {
        return Stream.of(
                Arguments.of(CHANNEL_ID_GEMA, "canal GEMA (40)")
        );
    }

    @Test
    @DisplayName("Debe usar valores por defecto del factory method Status.of() cuando no se especifican")
    void auditStatusChange_givenStatusWithDefaults_usesFactoryDefaults() throws Exception {
        status = createStatusWithDefaults();
        when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);

        assertDoesNotThrow(() -> daoImpl.auditStatusChange(nonBusinessDatatype, status));

        verify(callableStatement).setInt(PARAM_INDEX_CHANNEL_ID, CHANNEL_ID_GEMA); // 40
        verify(callableStatement).setString(PARAM_INDEX_MESSAGE, STATUS_MESSAGE_EMPTY); // ""
        verify(callableStatement).setString(PARAM_INDEX_PROCESS, STATUS_MESSAGE_EMPTY); // ""
        verify(callableStatement).execute();
    }

    // ========== Status Message ==========

    @Nested
    @DisplayName("Variaciones de Mensajes de Estado:")
    class StatusMessageVariations {

        @Test
        @DisplayName("Debe auditar correctamente con mensaje por defecto (cadena vacía)")
        void auditStatusChange_givenDefaultMessage_auditsSuccessfully() throws Exception {
            // Status.of() establece message="" por defecto
            status = Status.of(STATUS_ID_DGI_OK)
                    .setProcess(PROCESS_SEARCH);

            when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);

            assertDoesNotThrow(() -> daoImpl.auditStatusChange(nonBusinessDatatype, status));

            verify(callableStatement).setString(PARAM_INDEX_MESSAGE, STATUS_MESSAGE_EMPTY);
            verify(callableStatement).execute();
        }

        @Test
        @DisplayName("Debe auditar correctamente con mensaje explícitamente vacío")
        void auditStatusChange_givenExplicitEmptyMessage_auditsSuccessfully() throws Exception {
            status = Status.of(STATUS_ID_DGI_OK)
                    .setProcess(PROCESS_SEARCH)
                    .setMessage(STATUS_MESSAGE_EMPTY);

            when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);

            assertDoesNotThrow(() -> daoImpl.auditStatusChange(nonBusinessDatatype, status));

            verify(callableStatement).setString(PARAM_INDEX_MESSAGE, STATUS_MESSAGE_EMPTY);
            verify(callableStatement).execute();
        }

        @Test
        @DisplayName("Debe auditar correctamente con mensaje muy largo")
        void auditStatusChange_givenLongMessage_auditsSuccessfully() throws Exception {
            status = Status.of(STATUS_ID_PROCESADO)
                    .setProcess(PROCESS_BUSINESS)
                    .setMessage(STATUS_MESSAGE_LONG);

            when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);

            assertDoesNotThrow(() -> daoImpl.auditStatusChange(nonBusinessDatatype, status));

            verify(callableStatement).setString(PARAM_INDEX_MESSAGE, STATUS_MESSAGE_LONG);
            verify(callableStatement).execute();
        }


        @Test
        @DisplayName("Debe auditar correctamente con mensaje que contiene comillas simples")
        void auditStatusChange_givenMessageWithQuotes_auditsSuccessfully() throws Exception {
            String messageWithQuotes = "Error: Usuario 'admin' no autorizado";
            status = Status.of(STATUS_ID_ERROR)
                    .setProcess(PROCESS_SEARCH)
                    .setMessage(messageWithQuotes);

            when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);

            assertDoesNotThrow(() -> daoImpl.auditStatusChange(nonBusinessDatatype, status));

            verify(callableStatement).setString(PARAM_INDEX_MESSAGE, messageWithQuotes);
            verify(callableStatement).execute();
        }

        @Test
        @DisplayName("Debe auditar correctamente con mensaje que contiene saltos de línea")
        void auditStatusChange_givenMessageWithNewlines_auditsSuccessfully() throws Exception {
            String messageWithNewlines = "Error en línea 1\nDetalle en línea 2\nCausa en línea 3";
            status = Status.of(STATUS_ID_ERROR)
                    .setProcess(PROCESS_POST)
                    .setMessage(messageWithNewlines);

            when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);

            assertDoesNotThrow(() -> daoImpl.auditStatusChange(nonBusinessDatatype, status));

            verify(callableStatement).setString(PARAM_INDEX_MESSAGE, messageWithNewlines);
            verify(callableStatement).execute();
        }
    }

    // ========== Exception Handling ==========

        @Test
        @DisplayName("Debe lanzar ServiceException cuando falla prepareCall")
        void auditStatusChange_givenPrepareCallException_throwsServiceException() throws Exception {
            SQLException sqlException = new SQLException("Connection failed");
            when(managerDataAccessAs400.prepareCall(SQLStatements.AUDIT_STATUS_CHANGE))
                    .thenThrow(sqlException);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> daoImpl.auditStatusChange(nonBusinessDatatype, status));

            assertEquals(DAO_CLASS_NAME, exception.getMessage());
            assertEquals(ERROR_MESSAGE, exception.getInternalMessage());
            assertEquals(sqlException, exception.getCause());
            verify(logUtils).logError(eq(DAO_CLASS_NAME), eq(ERROR_MESSAGE), anyString());
            verify(managerDataAccessAs400).closeResources(null, null);
        }

        @Test
        @DisplayName("Debe lanzar ServiceException cuando falla execute")
        void auditStatusChange_givenExecuteException_throwsServiceException() throws Exception {
            SQLException sqlException = new SQLException("Execution failed");
            when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);
            doThrow(sqlException).when(callableStatement).execute();

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> daoImpl.auditStatusChange(nonBusinessDatatype, status));

            assertEquals(DAO_CLASS_NAME, exception.getMessage());
            assertEquals(ERROR_MESSAGE, exception.getInternalMessage());
            assertEquals(sqlException, exception.getCause());
            verify(logUtils).logError(eq(DAO_CLASS_NAME), eq(ERROR_MESSAGE), anyString());
            verify(managerDataAccessAs400).closeResources(callableStatement, null);
        }

        @Test
        @DisplayName("Debe lanzar ServiceException cuando falla setInt")
        void auditStatusChange_givenSetIntException_throwsServiceException() throws Exception {
            SQLException sqlException = new SQLException("Parameter setting failed");
            when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);
            doThrow(sqlException).when(callableStatement).setInt(anyInt(), anyInt());

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> daoImpl.auditStatusChange(nonBusinessDatatype, status));

            assertEquals(DAO_CLASS_NAME, exception.getMessage());
            assertEquals(ERROR_MESSAGE, exception.getInternalMessage());
            assertEquals(sqlException, exception.getCause());
            verify(managerDataAccessAs400).closeResources(callableStatement, null);
        }

        @Test
        @DisplayName("Debe lanzar ServiceException cuando falla setString")
        void auditStatusChange_givenSetStringException_throwsServiceException() throws Exception {
            SQLException sqlException = new SQLException("String parameter failed");
            when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);
            doThrow(sqlException).when(callableStatement).setString(anyInt(), anyString());

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> daoImpl.auditStatusChange(nonBusinessDatatype, status));

            assertEquals(DAO_CLASS_NAME, exception.getMessage());
            assertEquals(ERROR_MESSAGE, exception.getInternalMessage());
            assertEquals(sqlException, exception.getCause());
            verify(managerDataAccessAs400).closeResources(callableStatement, null);
        }

    // ========== Edge Cases ==========

        @Test
        @DisplayName("Debe cerrar recursos correctamente incluso cuando ocurre una excepción")
        void auditStatusChange_givenException_closesResourcesCorrectly() throws Exception {
            SQLException sqlException = new SQLException("Generic error");
            when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);
            doThrow(sqlException).when(callableStatement).execute();

            assertThrows(ServiceException.class,
                    () -> daoImpl.auditStatusChange(nonBusinessDatatype, status));

            verify(managerDataAccessAs400).closeResources(callableStatement, null);
        }

        @Test
        @DisplayName("Debe verificar el orden correcto de todos los parámetros")
        void auditStatusChange_givenValidInputs_setsParametersInCorrectOrder() throws Exception {
            when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);

            daoImpl.auditStatusChange(nonBusinessDatatype, status);

            var inOrder = inOrder(callableStatement);
            inOrder.verify(callableStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, nonBusinessDatatype.getBusinessCountry());
            inOrder.verify(callableStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, nonBusinessDatatype.getBusinessDocumentType());
            inOrder.verify(callableStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, nonBusinessDatatype.getBusinessDocument());
            inOrder.verify(callableStatement).setInt(PARAM_INDEX_PERSON_COUNTRY, nonBusinessDatatype.getPersonCountry());
            inOrder.verify(callableStatement).setInt(PARAM_INDEX_PERSON_DOCUMENT_TYPE, nonBusinessDatatype.getPersonDocumentType());
            inOrder.verify(callableStatement).setString(PARAM_INDEX_PERSON_DOCUMENT, nonBusinessDatatype.getPersonDocument());
            inOrder.verify(callableStatement).setInt(PARAM_INDEX_CHANNEL_ID, status.getChannelId());
            inOrder.verify(callableStatement).setString(PARAM_INDEX_EMPTY_STRING, EMPTY_STRING_PARAM);
            inOrder.verify(callableStatement).setString(PARAM_INDEX_PROCESS, status.getProcess());
            inOrder.verify(callableStatement).setString(PARAM_INDEX_STATUS_ID, status.getId());
            inOrder.verify(callableStatement).setInt(PARAM_INDEX_ZERO_INT, ZERO_INT_PARAM);
            inOrder.verify(callableStatement).setString(PARAM_INDEX_MESSAGE, status.getMessage());
            inOrder.verify(callableStatement).execute();
        }

        @Test
        @DisplayName("Debe usar correctamente los valores fijos de cadena vacía y cero")
        void auditStatusChange_givenValidInputs_usesCorrectFixedValues() throws Exception {
            when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);

            daoImpl.auditStatusChange(nonBusinessDatatype, status);

            verify(callableStatement).setString(PARAM_INDEX_EMPTY_STRING, EMPTY_STRING_PARAM);
            verify(callableStatement).setInt(PARAM_INDEX_ZERO_INT, ZERO_INT_PARAM);
        }

        @Test
        @DisplayName("Debe auditar múltiples cambios de estado para el mismo datatype")
        void auditStatusChange_givenMultipleStatusChanges_auditsAllCorrectly() throws Exception {
            when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);

            Status status1 = Status.of(STATUS_ID_INGRESO).setProcess(PROCESS_POST).setMessage("Primer estado");
            Status status2 = Status.of(STATUS_ID_DGI_OK).setProcess(PROCESS_SEARCH).setMessage("Segundo estado");
            Status status3 = Status.of(STATUS_ID_PROCESADO).setProcess(PROCESS_BUSINESS).setMessage("Tercer estado");

            assertDoesNotThrow(() -> {
                daoImpl.auditStatusChange(nonBusinessDatatype, status1);
                daoImpl.auditStatusChange(nonBusinessDatatype, status2);
                daoImpl.auditStatusChange(nonBusinessDatatype, status3);
            });

            verify(callableStatement, times(3)).execute();
            verify(managerDataAccessAs400, times(3)).closeResources(callableStatement, null);
        }

        @Test
        @DisplayName("Debe manejar correctamente todos los campos del datatype")
        void auditStatusChange_givenCompleteDatatype_usesAllFields() throws Exception {
            when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);

            daoImpl.auditStatusChange(nonBusinessDatatype, status);

            verify(callableStatement).setInt(PARAM_INDEX_BUSINESS_COUNTRY, nonBusinessDatatype.getBusinessCountry());
            verify(callableStatement).setInt(PARAM_INDEX_BUSINESS_DOCUMENT_TYPE, nonBusinessDatatype.getBusinessDocumentType());
            verify(callableStatement).setString(PARAM_INDEX_BUSINESS_DOCUMENT, nonBusinessDatatype.getBusinessDocument());
            verify(callableStatement).setInt(PARAM_INDEX_PERSON_COUNTRY, nonBusinessDatatype.getPersonCountry());
            verify(callableStatement).setInt(PARAM_INDEX_PERSON_DOCUMENT_TYPE, nonBusinessDatatype.getPersonDocumentType());
            verify(callableStatement).setString(PARAM_INDEX_PERSON_DOCUMENT, nonBusinessDatatype.getPersonDocument());
        }

        @Test
        @DisplayName("Debe mantener el channelId por defecto de GEMA cuando no se especifica")
        void auditStatusChange_givenNoChannelIdSpecified_usesGemaDefault() throws Exception {
            status = Status.of(STATUS_ID_DGI_OK)
                    .setProcess(PROCESS_SEARCH)
                    .setMessage(STATUS_MESSAGE_VALID);
            // No se llama setChannelId(), por lo que debe quedar el default (40)

            when(managerDataAccessAs400.prepareCall(anyString())).thenReturn(callableStatement);

            daoImpl.auditStatusChange(nonBusinessDatatype, status);

            verify(callableStatement).setInt(PARAM_INDEX_CHANNEL_ID, CHANNEL_ID_GEMA);
        }
}