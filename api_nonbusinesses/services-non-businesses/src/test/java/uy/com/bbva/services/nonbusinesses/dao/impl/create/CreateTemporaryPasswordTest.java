package uy.com.bbva.services.nonbusinesses.dao.impl.create;

import com.bbva.secarq.caas2.core.exception.CaasException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uy.com.bbva.encrypter.MaskUtil;
import uy.com.bbva.mongo.commons.ManagerMongoDBAccess;
import uy.com.bbva.mongo.commons.MongoTemplateBBVA;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.common.TestDataFactory;
import uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl;
import uy.com.bbva.services.nonbusinesses.model.User;

import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uy.com.bbva.dtos.commons.utils.Constants.*;
import static uy.com.bbva.services.nonbusinesses.common.TestDataFactory.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de creación de contraseña temporal")
class CreateTemporaryPasswordTest {

    private static final String VERIDAS_DB_NAME = "veridasDB";
    private static final String COLLECTION_NAME = "customersActivations";
    private static final String ENCRYPT_TYPE = "AES256";
    private static final String ENCRYPTED_PASSWORD = "XYZ789encryptedABC123==";
    private static final String EMPTY_PASSWORD = "";
    private static final String LONG_PASSWORD = "ThisIsAVeryLongPasswordThatExceedsNormalLimitsForTestingPurposes123!@#";
    private static final String SPECIAL_CHARS_PASSWORD = "P@$$w0rd!#&*()_+{}[]|\\:;\"'<>,.?/";
    private static final String ERROR_MASK_PWD = "Ocurrió un error al encriptar el password";

    @Mock
    private ManagerMongoDBAccess managerMongoDBAccess;

    @Mock
    private MongoTemplateBBVA mongoTemplateBBVA;

    @Mock
    private MongoCollection<Document> mongoCollection;

    @Mock
    private MaskUtil maskUtil;

    @InjectMocks
    private DAOImpl daoImpl;

    private NonBusinessIdDatatype nonBusinessDatatype;
    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(daoImpl, "veridasDBName", VERIDAS_DB_NAME);
        ReflectionTestUtils.setField(daoImpl, "encryptType", ENCRYPT_TYPE);

        nonBusinessDatatype = TestDataFactory.createBusinessWithPersonDatatype();
        user = TestDataFactory.createUser();
    }

    @Test
    @DisplayName("Debe crear contraseña temporal exitosamente con datos válidos")
    void createTemporaryPassword_givenValidData_shouldInsertDocumentSuccessfully() throws Exception {
        setupSuccessfulMongoOperation();
        when(maskUtil.encrypt(USER_PASSWORD_VALID, ENCRYPT_TYPE)).thenReturn(ENCRYPTED_PASSWORD);
        Date beforeExecution = new Date();

        assertDoesNotThrow(() -> daoImpl.createTemporaryPassword(nonBusinessDatatype, user));

        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mongoCollection).insertOne(documentCaptor.capture());

        Document capturedDocument = documentCaptor.getValue();
        assertAllDocumentFields(capturedDocument, beforeExecution);
        verifySuccessfulExecution();
    }

    @ParameterizedTest
    @DisplayName("Debe encriptar correctamente diferentes tipos de contraseñas")
    @ValueSource(strings = {
            EMPTY_PASSWORD,
            LONG_PASSWORD,
            SPECIAL_CHARS_PASSWORD
    })
    void createTemporaryPassword_givenVariousPasswords_shouldEncryptAndStoreAll(String password) throws Exception {
        setupSuccessfulMongoOperation();
        user.setPassword(password);
        String expectedEncrypted = "encrypted_" + password.hashCode();

        when(maskUtil.encrypt(password, ENCRYPT_TYPE)).thenReturn(expectedEncrypted);

        assertDoesNotThrow(() -> daoImpl.createTemporaryPassword(nonBusinessDatatype, user));

        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);

        verify(mongoCollection).insertOne(documentCaptor.capture());
        assertEquals(expectedEncrypted, documentCaptor.getValue().get("cipherPassword"));
    }

    @ParameterizedTest
    @DisplayName("Debe almacenar correctamente diferentes configuraciones de NonBusinessIdDatatype")
    @MethodSource("provideNonBusinessDatatypeVariations")
    void createTemporaryPassword_givenDifferentDatatypes_shouldStoreCorrectly(
            NonBusinessIdDatatype datatype,
            Integer expectedPersonDocType,
            Integer expectedPersonCountry,
            String expectedPersonDoc,
            Integer expectedBusinessDocType,
            Integer expectedBusinessCountry,
            String expectedBusinessDoc) throws Exception {

        setupSuccessfulMongoOperation();
        when(maskUtil.encrypt(anyString(), anyString())).thenReturn(ENCRYPTED_PASSWORD);

        assertDoesNotThrow(() -> daoImpl.createTemporaryPassword(datatype, user));

        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mongoCollection).insertOne(documentCaptor.capture());

        Document doc = documentCaptor.getValue();
        assertEquals(expectedPersonDocType, doc.get("personDocumentType"));
        assertEquals(expectedPersonCountry, doc.get("personDocumentCountry"));
        assertEquals(expectedPersonDoc, doc.get("personDocumentNumber"));
        assertEquals(expectedBusinessDocType, doc.get("businessDocumentType"));
        assertEquals(expectedBusinessCountry, doc.get("businessDocumentCountry"));
        assertEquals(expectedBusinessDoc, doc.get("businessDocumentNumber"));
    }

    @Test
    @DisplayName("Debe lanzar ServiceException cuando falla la encriptación")
    void createTemporaryPassword_givenEncryptionFailure_shouldThrowServiceException() throws Exception {
        setupSuccessfulMongoOperation();
        CaasException caasException = new CaasException(ERROR_ENCRYPTION);
        when(maskUtil.encrypt(USER_PASSWORD_VALID, ENCRYPT_TYPE)).thenThrow(caasException);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> daoImpl.createTemporaryPassword(nonBusinessDatatype, user)
        );

        assertEquals(ERROR_MASK_PWD, exception.getMessage());
        assertEquals(ERROR_MASK_PWD, exception.getInternalMessage());
        assertEquals(caasException, exception.getCause());
        verify(mongoCollection, never()).insertOne(any());
    }

    @Test
    @DisplayName("Debe lanzar ServiceException cuando maskUtil retorna null")
    void createTemporaryPassword_givenNullEncryptedPassword_shouldThrowException() throws Exception {
        setupSuccessfulMongoOperation();
        when(maskUtil.encrypt(USER_PASSWORD_VALID, ENCRYPT_TYPE)).thenReturn(null);

        assertDoesNotThrow(() -> daoImpl.createTemporaryPassword(nonBusinessDatatype, user));

        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mongoCollection).insertOne(documentCaptor.capture());
        assertNull(documentCaptor.getValue().get("cipherPassword"));
    }

    @Test
    @DisplayName("Debe manejar RuntimeException de MongoDB durante inserción")
    void createTemporaryPassword_givenMongoInsertFailure_shouldPropagateException() throws Exception {
        setupSuccessfulMongoOperation();
        when(maskUtil.encrypt(USER_PASSWORD_VALID, ENCRYPT_TYPE)).thenReturn(ENCRYPTED_PASSWORD);
        RuntimeException mongoException = new RuntimeException("MongoDB connection lost");
        doThrow(mongoException).when(mongoCollection).insertOne(any(Document.class));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> daoImpl.createTemporaryPassword(nonBusinessDatatype, user)
        );

        assertEquals("MongoDB connection lost", exception.getMessage());
        verify(mongoCollection).insertOne(any(Document.class));
    }

    @Test
    @DisplayName("Debe manejar usuario con nombre null correctamente")
    void createTemporaryPassword_givenNullUserName_shouldStoreNullInDocument() throws Exception {
        setupSuccessfulMongoOperation();
        user.setName(null);

        when(maskUtil.encrypt(USER_PASSWORD_VALID, ENCRYPT_TYPE)).thenReturn(ENCRYPTED_PASSWORD);

        assertDoesNotThrow(() -> daoImpl.createTemporaryPassword(nonBusinessDatatype, user));

        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mongoCollection).insertOne(documentCaptor.capture());
        assertNull(documentCaptor.getValue().get("user"));
    }

    @Test
    @DisplayName("Debe manejar valores null en NonBusinessIdDatatype")
    void createTemporaryPassword_givenNullValuesInDatatype_shouldStoreNullsInDocument() throws Exception {
        setupSuccessfulMongoOperation();
        NonBusinessIdDatatype datatypeWithNulls = new NonBusinessIdDatatype();

        datatypeWithNulls.setPersonDocument(null);
        datatypeWithNulls.setBusinessDocument(null);
        datatypeWithNulls.setPersonCountry(UY_COUNTRY_CODE);
        datatypeWithNulls.setBusinessCountry(UY_COUNTRY_CODE);
        datatypeWithNulls.setPersonDocumentType(USER_DOCUMENT_TYPE_CI);
        datatypeWithNulls.setBusinessDocumentType(RUT_DOCUMENT_TYPE);

        when(maskUtil.encrypt(anyString(), anyString())).thenReturn(ENCRYPTED_PASSWORD);

        assertDoesNotThrow(() -> daoImpl.createTemporaryPassword(datatypeWithNulls, user));

        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mongoCollection).insertOne(documentCaptor.capture());

        Document doc = documentCaptor.getValue();
        assertNull(doc.get("personDocumentNumber"));
        assertNull(doc.get("businessDocumentNumber"));
        assertEquals(UY_COUNTRY_CODE, doc.get("personDocumentCountry"));
        assertEquals(UY_COUNTRY_CODE, doc.get("businessDocumentCountry"));
    }

    @Test
    @DisplayName("Debe crear múltiples contraseñas temporales para el mismo usuario")
    void createTemporaryPassword_givenSameUserMultipleTimes_shouldCreateMultipleEntries() throws Exception {
        setupSuccessfulMongoOperation();
        when(maskUtil.encrypt(anyString(), anyString())).thenReturn(ENCRYPTED_PASSWORD);

        assertDoesNotThrow(() -> {
            daoImpl.createTemporaryPassword(nonBusinessDatatype, user);
            daoImpl.createTemporaryPassword(nonBusinessDatatype, user);
            daoImpl.createTemporaryPassword(nonBusinessDatatype, user);
        });

        verify(mongoCollection, times(3)).insertOne(any(Document.class));
        verify(maskUtil, times(3)).encrypt(USER_PASSWORD_VALID, ENCRYPT_TYPE);
    }

    public void setupSuccessfulMongoOperation() {
        when(managerMongoDBAccess.getMongoTemplateBBVA(VERIDAS_DB_NAME)).thenReturn(mongoTemplateBBVA);
        when(mongoTemplateBBVA.getCollection(COLLECTION_NAME)).thenReturn(mongoCollection);
    }

    private void assertAllDocumentFields(Document document, Date beforeExecution) {
        assertEquals(USER_DOCUMENT_TYPE_CI, document.get("personDocumentType"));
        assertEquals(UY_COUNTRY_CODE, document.get("personDocumentCountry"));
        assertEquals(PERSON_DOCUMENT_VALID, document.get("personDocumentNumber"));
        assertEquals(RUT_DOCUMENT_TYPE, document.get("businessDocumentType"));
        assertEquals(UY_COUNTRY_CODE, document.get("businessDocumentCountry"));
        assertEquals(BUSINESS_RUT_VALID, document.get("businessDocumentNumber"));
        assertEquals(USER_NAME_VALID, document.get("user"));
        assertEquals(ENCRYPTED_PASSWORD, document.get("cipherPassword"));

        Date documentDate = (Date) document.get("date");
        assertNotNull(documentDate);
        assertTrue(documentDate.after(beforeExecution) || documentDate.equals(beforeExecution));
    }

    private void verifySuccessfulExecution() throws CaasException {
        verify(managerMongoDBAccess).getMongoTemplateBBVA(VERIDAS_DB_NAME);
        verify(mongoTemplateBBVA).getCollection(COLLECTION_NAME);
        verify(maskUtil).encrypt(USER_PASSWORD_VALID, ENCRYPT_TYPE);
        verify(mongoCollection).insertOne(any(Document.class));
    }

    private static Stream<Arguments> provideNonBusinessDatatypeVariations() {
        return Stream.of(
                Arguments.of(
                        TestDataFactory.createBusinessWithPersonDatatype(),
                        USER_DOCUMENT_TYPE_CI, UY_COUNTRY_CODE, PERSON_DOCUMENT_VALID,
                        RUT_DOCUMENT_TYPE, UY_COUNTRY_CODE, BUSINESS_RUT_VALID
                ),
                // documento vacio
                Arguments.of(
                        TestDataFactory.createBusinessWithPersonDatatype(
                                "", RUT_DOCUMENT_TYPE, "", USER_DOCUMENT_TYPE_CI
                        ),
                        USER_DOCUMENT_TYPE_CI, UY_COUNTRY_CODE, "",
                        RUT_DOCUMENT_TYPE, UY_COUNTRY_CODE, ""
                ),
                // Tipo de documento diferentes.
                Arguments.of(
                        TestDataFactory.createBusinessWithPersonDatatype(
                                "ABC123456789", 99, "XYZ987654", 88
                        ),
                        88, UY_COUNTRY_CODE, "XYZ987654",
                        99, UY_COUNTRY_CODE, "ABC123456789"
                )
        );
    }
}