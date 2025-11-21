package uy.com.bbva.services.documents.commons;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.accountscommons.idmanagement.datatypes.AccountIdDatatype;
import uy.com.bbva.customerscommons.idmanagement.datatypes.CustomerIdDatatype;
import uy.com.bbva.documentscommons.dtos.models.Categorization;
import uy.com.bbva.documentscommons.dtos.models.DocumentInfo;
import uy.com.bbva.documentscommons.dtos.models.MetaData;
import uy.com.bbva.documentscommons.dtos.models.SecurityLevelInfo;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.mongo.commons.ManagerMongoDBAccess;
import uy.com.bbva.mongo.commons.MongoTemplateBBVA;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.commons.dao.ManagerDataAccessAs400;
import uy.com.bbva.services.documents.dao.impl.DAOImpl;
import uy.com.bbva.services.documents.model.FileData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class DAOTest {
    @Mock
    protected ManagerDataAccessAs400 manager;

    @Mock
    protected ManagerMongoDBAccess managerMongoDBAccess;

    @Mock
    protected MongoTemplateBBVA mongoTemplate;

    @Mock
    protected LogUtils logUtils;

    @Mock
    protected PreparedStatement preparedStatement;

    @Mock
    protected ResultSet resultSet;

    @InjectMocks
    protected DAOImpl dao;

    protected static final String DOCUMENT_DB_NAME = "documents";
    protected static final String FILENET_DB_NAME = "filenet";

    // se mantienen publicos para mayor flexibilidad.
    public NonBusinessIdDatatype createNonBusinessIdDatatype() {
        NonBusinessIdDatatype datatype = new NonBusinessIdDatatype();
        datatype.setBusinessDocument("12345678");
        datatype.setBusinessCountry(845);
        datatype.setBusinessDocumentType(1);
        datatype.setPersonDocument("87654321");
        datatype.setPersonCountry(858);
        datatype.setPersonDocumentType(2);
        return datatype;
    }

    public AccountIdDatatype createAccountIdDatatype() {
        AccountIdDatatype datatype = new AccountIdDatatype();
        datatype.setAccountNumber(12345);
        return datatype;
    }

    public CustomerIdDatatype createCustomerIdDatatype() {
        CustomerIdDatatype datatype = new CustomerIdDatatype();
        datatype.setDocument("12345678");
        datatype.setCountry(845);
        datatype.setDocumentType(1);
        return datatype;
    }

    public MetaData createMetaData() {
        MetaData metaData = new MetaData();

        DocumentInfo docInfo = new DocumentInfo();
        docInfo.setObjectName("Test Document");
        docInfo.setOwner("Test Owner");
        docInfo.setResponsible("Test Responsible");
        metaData.setDocumentInfo(docInfo);

        Categorization categorization = new Categorization();
        categorization.setDocumentTypeId("TEST_CONTRACT");
        metaData.setCategorization(categorization);

        SecurityLevelInfo securityInfo = new SecurityLevelInfo();
        securityInfo.setSecurityLevel(1);
        securityInfo.setClassificationLevel(1);
        metaData.setSecurityLevelInfo(securityInfo);

        return metaData;
    }

    public FileData createFileData() {
        FileData fileData = new FileData();
        fileData.setBase64Data("VGVzdCBkb2N1bWVudCBjb250ZW50"); // "Test document content" in base64
        fileData.setName("test-file.pdf");
        return fileData;
    }

    public void setupMockResultSetForBusinessData(boolean withValidData) throws SQLException {
        when(resultSet.next()).thenReturn(true);

        if (withValidData) {
            when(resultSet.getString("BBNCPJCTA")).thenReturn("123456");
            when(resultSet.getString("BRANCH_NAME")).thenReturn("Test Branch");
            when(resultSet.getString("BBNCPJRASO")).thenReturn("Test Business Name");
            when(resultSet.getString("DOCUMENT_COUNTRY")).thenReturn("UY");
            when(resultSet.getString("DOCUMENT_TYPE")).thenReturn("RUT");
            when(resultSet.getString("BBNCPJNDOC")).thenReturn("12345678");
            when(resultSet.getString("BBNCPJFCON")).thenReturn("20200101");
            when(resultSet.getString("BBNCPJFINR")).thenReturn("20200115");
            when(resultSet.getString("BBNCPJIBPS")).thenReturn("987654");
            when(resultSet.getString("BBNCPJNOMC")).thenReturn("Test Commercial Name");
            when(resultSet.getString("BBNCPJFING")).thenReturn("20230101");
            when(resultSet.getString("BBNCPJIANU")).thenReturn("100000");
            when(resultSet.getString("MAIN_ACTIVITY")).thenReturn("Test Activity");
            when(resultSet.getString("ACTIVITY_DESCRIPTION")).thenReturn("Test Description");
            when(resultSet.getString("BBNCPJFRUT")).thenReturn("20251231");
        } else {
            when(resultSet.getString("BBNCPJCTA")).thenReturn(null);
            when(resultSet.getString("BRANCH_NAME")).thenReturn("");
            when(resultSet.getString("BBNCPJRASO")).thenReturn("  ");
            when(resultSet.getString("DOCUMENT_COUNTRY")).thenReturn("UY");
            when(resultSet.getString("DOCUMENT_TYPE")).thenReturn("RUT");
            when(resultSet.getString("BBNCPJNDOC")).thenReturn("12345678");
            when(resultSet.getString("BBNCPJFCON")).thenReturn("20200101");
            when(resultSet.getString("BBNCPJFINR")).thenReturn("20200115");
            when(resultSet.getString("BBNCPJIBPS")).thenReturn(null);
            when(resultSet.getString("BBNCPJNOMC")).thenReturn("");
            when(resultSet.getString("BBNCPJFING")).thenReturn("20230101");
            when(resultSet.getString("BBNCPJIANU")).thenReturn("100000");
            when(resultSet.getString("MAIN_ACTIVITY")).thenReturn("Activity");
            when(resultSet.getString("ACTIVITY_DESCRIPTION")).thenReturn("");
            when(resultSet.getString("BBNCPJFRUT")).thenReturn("20251231");
        }
    }

    public void setupMockResultSetForAddressData() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("SNGC01DSC")).thenReturn("Level1");
        when(resultSet.getString("SNGC02DSC")).thenReturn("Level2");
        when(resultSet.getString("SNGC03DSC")).thenReturn("Level3");
        when(resultSet.getString("BBNCEDDSC1")).thenReturn("Description1");
        when(resultSet.getString("BBNCEDDSC2")).thenReturn("Description2");
        when(resultSet.getString("BBNCEDDSC3")).thenReturn("Description3");
        when(resultSet.getString("PANOM")).thenReturn("Uruguay");
        when(resultSet.getString("DEPNOM")).thenReturn("Montevideo");
        when(resultSet.getString("ADDRESS_LOCATION_TYPE")).thenReturn("Street");
        when(resultSet.getString("ADDRESS_CITY_NEIGHBORHOOD")).thenReturn("Centro");
        when(resultSet.getString("BBNCEDPOS")).thenReturn("11200");
    }

    public void setupMockResultSetForPersonData(boolean withValidData, boolean withSpouseData) throws SQLException {
        when(resultSet.next()).thenReturn(true);

        if (withValidData) {
            when(resultSet.getString("BBNCPFAPE1")).thenReturn("García");
            when(resultSet.getString("BBNCPFAPE2")).thenReturn("López");
            when(resultSet.getString("BBNCPFNOM1")).thenReturn("Juan");
            when(resultSet.getString("BBNCPFNOM2")).thenReturn("Carlos");
            when(resultSet.getString("DOCUMENT_COUNTRY")).thenReturn("UY");
            when(resultSet.getString("DOCUMENT_TYPE")).thenReturn("CI");
            when(resultSet.getString("BBNCPFNDOC")).thenReturn("87654321");

            when(resultSet.getString("BBNCPFFNAC")).thenReturn("19850515");
            when(resultSet.getString("BBNCPFFVCI")).thenReturn("20301231");

            when(resultSet.getString("MARITAL_STATUS")).thenReturn(withSpouseData ? "Casado" : "Soltero");
            when(resultSet.getString("BBNCPFSEXO")).thenReturn("M");
            when(resultSet.getString("BIRTH_COUNTRY")).thenReturn("Uruguay");

            if (withSpouseData) {
                when(resultSet.getString("BBNCPFCOA1")).thenReturn("Fernández");
                when(resultSet.getString("BBNCPFCOA2")).thenReturn("Martínez");
                when(resultSet.getString("BBNCPFCON1")).thenReturn("María");
                when(resultSet.getString("BBNCPFCON2")).thenReturn("Elena");
                when(resultSet.getString("SPOUSE_DOCUMENT_TYPE")).thenReturn("CI");
                when(resultSet.getString("SPOUSE_DOCUMENT_COUNTRY")).thenReturn("UY");
                when(resultSet.getString("BBNCPFCOND")).thenReturn("12349876");
            } else {
                when(resultSet.getString("BBNCPFCOA1")).thenReturn("");
                when(resultSet.getString("BBNCPFCOA2")).thenReturn("");
                when(resultSet.getString("BBNCPFCON1")).thenReturn("");
                when(resultSet.getString("BBNCPFCON2")).thenReturn("");
                when(resultSet.getString("SPOUSE_DOCUMENT_TYPE")).thenReturn("");
                when(resultSet.getString("SPOUSE_DOCUMENT_COUNTRY")).thenReturn("");
                when(resultSet.getString("BBNCPFCOND")).thenReturn("");
            }

            when(resultSet.getString("BBNCPFTEL1")).thenReturn("099123456");
            when(resultSet.getString("BBNCPFMAIL")).thenReturn("juan.garcia@email.com");
        }
    }
}