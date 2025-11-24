package uy.com.bbva.services.nonbusinesses.common;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.test.context.TestPropertySource;
import uy.com.bbva.dtos.commons.model.Address;
import uy.com.bbva.dtos.commons.model.AddressType;
import uy.com.bbva.dtos.commons.model.GenericIdDescription;
import uy.com.bbva.dtos.commons.model.GenericObject;
import uy.com.bbva.dtos.commons.v1.model.RelatedPerson;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.nonbusinesses.model.*;
import uy.com.bbva.services.nonbusinesses.model.external.BusinessInformation;
import uy.com.bbva.services.nonbusinesses.model.status.Status;

import java.util.*;

import static uy.com.bbva.dtos.commons.utils.Constants.*;

/**
 * Factory para crear objetos de datos de prueba de manera consistente.
 */
@TestPropertySource(properties = {"api.non-businesses.no-package.threshold=1900000"})
public class TestDataFactory {

    public static final int CHANNEL_ID_GEMA = 40;
    public static final int NO_PACKAGE_THRESHOLD = 1900000;


    // ========== Document Types ==========
    public static final int BUSINESS_DOCUMENT_TYPE_DEFAULT = RUT_DOCUMENT_TYPE;

    // ========== Person & Business Document ==========
    public static final String BUSINESS_RUT_VALID = "123456789112";
    public static final String BUSINESS_NAME_VALID = "Test Business S.A.";
    public static final String BUSINESS_NAME_EMPTY = "";
    public static final String BUSINESS_NAME_WITH_SPACES = " Test Business Corp ";
    public static final String BUSINESS_NAME_LONG = "Very Long Business Name That Exceeds Normal Length Limits For Testing Purposes";

    public static final String PERSON_DOCUMENT_VALID = "12345678";
    public static final String PERSON_DOCUMENT_ALTERNATIVE = "4321987-6";

    // ========== Non-Business ID==========
    public static final String NON_BUSINESS_ID_VALID = "nonBusinessId";

    // ========== User ==========
    public static final String USER_NAME_VALID = "testuser";
    public static final String USER_PASSWORD_VALID = "TestPass123";

    // ========== Contact ==========
    public static final String EMAIL_VALID = "correo@ejemplo.com";
    public static final String EMAIL_EMPTY = "";
    public static final String EMAIL_WITH_SPECIAL_CHARS = "correo+ejemplo@dominio.com";
    public static final String EMAIL_WITH_SPACES = " correo@ejemplo.com ";
    public static final String EMAIL_LONG = "very.long.email.address.for.testing@very.long.domain.name.example.com";

    public static final String MOBILE_VALID = "099123456";
    public static final String MOBILE_WITH_SPACES = " 099 123 456 ";
    public static final String MOBILE_EMPTY = "";

    // ========== Dates ==========
    public static final String DATE_FORMAT_PATTERN = "yyyyMMdd";
    public static final String DATE_VALID = "2025-12-31";
    public static final String DATE_VALID_FORMATTED = "20251231";
    public static final String DATE_ALTERNATIVE = "2025-10-03";
    public static final String DATE_ALTERNATIVE_FORMATTED = "20251003";

    // ========== Economic ==========
    public static final String ECONOMIC_ACTIVITY_ID_VALID = "1234";
    public static final String ECONOMIC_ACTIVITY_DESCRIPTION = "Desarrollo de software";
    public static final String TAX_CONDITION_ID_VALID = "MONOTRIBUTO";
    public static final String TAX_CONDITION_DESCRIPTION = "Monotributo";
    public static final double INCOME_AMOUNT_VALID = 2000000.50;
    public static final String INCOME_DATE_STRING = "20251003";

    public static final String BALANCE_TYPE_REAL = "REAL_ANNUAL_INCOME";
    public static final String BALANCE_TYPE_PROJECTED = "PROJECTED_ANNUAL_INCOME";
    public static final String BALANCE_TYPE_OTHER = "OTHER_BALANCE";

    // ========== Address ==========
    public static final String ADDRESS_ID_VALID = "addressId";
    public static final String POSTAL_CODE_VALID = "11200";
    public static final String POSTAL_CODE_EMPTY = "";
    public static final String COUNTRY_ID_VALID = "858";
    public static final String COUNTRY_NAME_VALID = "Uruguay";
    public static final String MVD_ID_VALID = "1";
    public static final String MVD_NAME_VALID = "Montevideo";
    public static final String CITY_ID_VALID = "10";
    public static final String CITY_NAME_VALID = "Young";
    public static final String LEVEL1_ID_VALID = "100";
    public static final String LEVEL1_NAME_VALID = "18 de Julio";
    public static final String LEVEL2_ID_VALID = "200";
    public static final String LEVEL2_NAME_VALID = "1234";
    public static final String LEVEL3_ID_VALID = "300";
    public static final String LEVEL3_NAME_VALID = "1811";


    // ========== Department Data ==========
    public static final String DEPARTMENT_MONTEVIDEO_ID = "01";
    public static final String DEPARTMENT_MONTEVIDEO_NAME = "Montevideo";
    public static final String DEPARTMENT_CANELONES_ID = "02";
    public static final String DEPARTMENT_CANELONES_NAME = "Canelones";

    // ========== Bank Branch ==========
    public static final String BANK_BRANCH_VALID = "001";
    public static final String BANK_BRANCH_ALTERNATIVE = "999";
    public static final String BANK_BRANCH_EMPTY = "";
    public static final String BANK_BRANCH_WITH_SPACES = "  001  ";
    public static final String BANK_BRANCH_LONG = "12345678901234567890";
    public static final String BANK_BRANCH_WITH_SPECIAL_CHARS = "001-ABC";
    public static final String BANK_BRANCH_NUMERIC_LARGE = "999999";

    // ========== Formation ==========
    public static final String FORMATION_DATE_FORMATTED = "20251003";
    public static final String FORMATION_DATE_ALTERNATIVE_FORMATTED = "20241003";
    public static final String FORMATION_DATE_OLD_FORMATTED = "19000101";
    public static final String FORMATION_DATE_FUTURE_FORMATTED = "20991231";

    // ========== Legal Document ==========
    public static final String LEGAL_DOC_NUMBER_VALID = "BPS-2024-001234";
    public static final String LEGAL_DOC_NUMBER_ALTERNATIVE = "BPS-2023-999999";
    public static final String LEGAL_DOC_NUMBER_EMPTY = "";
    public static final String LEGAL_DOC_NUMBER_WITH_SPACES = "  BPS-2024-001234  ";
    public static final String LEGAL_DOC_NUMBER_LONG = "BPS-2024-123456789012345678901234567890";
    public static final String LEGAL_DOC_ISSUE_DATE_FORMATTED = "20251003";
    public static final String LEGAL_DOC_ISSUE_DATE_ALTERNATIVE_FORMATTED = "20241003";

    // ========== Commercial Name ==========
    public static final String COMMERCIAL_NAME_WITH_ACCENTS = "Ñandú Inc";
    public static final String COMMERCIAL_NAME_WITH_NUMBERS = "Tech2024 Solutions";
    public static final String COMMERCIAL_NAME_SINGLE_CHAR = "X";
    public static final String COMMERCIAL_NAME_WITH_PUNCTUATION = "ffontana.dev, Inc.";

    // ========== Status Data ==========
    public static final String STATUS_ID_INGRESO = "INGRESO";
    public static final String STATUS_ID_DGI_OK = "DGI_OK";
    public static final String STATUS_ID_DGI_ERROR = "DGINTC_ERR";
    public static final String STATUS_ID_DGI_CERT_ERROR = "DGICRT_ERR";
    public static final String STATUS_ID_DGI_NOT_UNIP_ERROR = "DGINTU_ERR";
    public static final String STATUS_ID_NB_CNT_OK = "NB_CNT_OK";
    public static final String STATUS_ID_NB_ADD_OK = "NB_ADD_OK";
    public static final String STATUS_ID_PROCESADO = "PROCESADO";
    public static final String STATUS_ID_ANULADO = "ANULADO";
    public static final String STATUS_ID_ERROR = "NB_CLI_ERR";

    public static final String PROCESS_SEARCH = "NBSEARCH";
    public static final String PROCESS_POST = "NBPOST";
    public static final String PROCESS_CONTACT = "NBCONDET";
    public static final String PROCESS_ADDRESS = "NBPADDR";
    public static final String PROCESS_ECONOMIC = "NBPECO";
    public static final String PROCESS_BUSINESS = "NBPATCH";

    // ========== Package Data ==========
    public static final String PACKAGE_ASSIGNMENT_FULL = "INCOME_ASSIGN_FULL";
    public static final String PACKAGE_ASSIGNMENT_PARTIAL = "INCOME_ASSIGN_PARTIAL";


    // ========== Error Messages ==========
    public static final String ERROR_ENCRYPTION = "Encryption error";
    public static final String ERROR_DECRYPTION = "Decryption error";
    public static final String ERROR_DATABASE = "Database error";
    public static final String ERROR_CONVERSION = "Conversion error";
    public static final String ERROR_PARAMETER = "Parameter error";
    public static final String ERROR_EXECUTION= "Execution failed";


    public static final String ERROR_DAO = "DAO error";
    public static final String ERROR_UPDATE_STATUS = "Update status error";
    public static final String ERROR_AUDIT = "Audit error";

    public static final String STATUS_MESSAGE_VALID = "Operación exitosa";
    public static final String STATUS_MESSAGE_EMPTY = "";
    public static final String STATUS_MESSAGE_LONG = "Este es un mensaje muy largo que describe con gran detalle todos los aspectos de la operación realizada y puede contener información técnica específica sobre el proceso ejecutado";

    public static final int SINGLE_ROW_UPDATED = 1;
    public static final int ZERO_ROWS_UPDATED = 0;

    public static final String DAO_CLASS_NAME = "uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl";

    // ========== Search Method Test Data ==========
    public static final String USER_ID_VALID = "24747904-41256078";
    public static final String USER_ID_INVALID_NO_HYPHEN = "2474790441256078";
    public static final String USER_ID_INVALID_EMPTY = "";
    public static final String USER_ID_INVALID_SINGLE_PART = "24747904";
    public static final String USER_ID_WITH_WHITESPACE = " 24747904-41256078 ";
    public static final String USER_ID_MULTIPLE_HYPHENS = "24-74-79-04-41256078";

    public static final String OWNER_DOCUMENT_VALID = "24747904";
    public static final String RUT_FROM_USER_ID = "41256078";
    public static final String RUT_MISMATCH = "99999999";

    public static final String STATUS_EMPTY = "";
    public static final String STATUS_VALID = "DGI_PENDING";
    public static final String CERTIFICATE_EXPIRATION_VALID = "2026-12-31";
    public static final String CERTIFICATE_EXPIRATION_EXPIRED = "2024-01-01";
    public static final String CERTIFICATE_EXPIRATION_TODAY = "2025-10-28";
    public static final String CERTIFICATE_EXPIRATION_INVALID_FORMAT = "invalid-date";

    public static final String LEGAL_ADDRESS_VALID = "Av. Italia 1234, Montevideo";

    /**
     * Creates a NonBusinessSearch with valid business document.
     */
    public static NonBusinessSearch createNonBusinessSearch() {
        return createNonBusinessSearch(RUT_FROM_USER_ID);
    }

    /**
     * Creates a NonBusinessSearch with custom business document number.
     */
    public static NonBusinessSearch createNonBusinessSearch(String rutNumber) {
        NonBusinessSearch search = new NonBusinessSearch();
        BusinessDocument businessDocument = new BusinessDocument();
        businessDocument.setDocumentNumber(rutNumber);
        businessDocument.setBusinessDocumentType(new GenericObject(String.valueOf(RUT_DOCUMENT_TYPE), "RUT"));
        search.setBusinessDocument(businessDocument);
        return search;
    }

    /**
     * Creates a BusinessInformation with custom expiration date.
     */
    public static BusinessInformation createBusinessInformationWithExpiration(String expirationDate) {
        BusinessInformation info = new BusinessInformation();
        info.setName(BUSINESS_NAME_VALID);
        info.setRut(RUT_FROM_USER_ID);
        info.setExpiration(expirationDate);
        info.setLegalAddress(LEGAL_ADDRESS_VALID);
        return info;
    }

    /**
     * Creates a RelatedPerson (owner) with valid data.
     */
    public static RelatedPerson createRelatedPerson() {
        RelatedPerson person = new RelatedPerson();
        person.setFirstName("Topo");
        person.setFirstName("John");
        person.setLastName("Doe");
        return person;
    }

    public enum EXPAND_OPTIONS {
        LR("LEGAL-REPRESENTATIVES"),
        CD("CONTACT-DETAILS");

        private final String value;

        EXPAND_OPTIONS(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static String concat(EXPAND_OPTIONS... options) {
            StringJoiner joiner = new StringJoiner(",");
            for (EXPAND_OPTIONS option : options) {
                joiner.add(option.toString());
            }
            return joiner.toString();
        }
    }

    /**
     * Crea un Date desde un string en formato yyyyMMdd.
     */
    public static Date parseDate(String dateString) {
        try {
            return DateUtils.parseDate(dateString, DATE_FORMAT_PATTERN);

        } catch (Exception e) {

            throw new RuntimeException("Error parsing date: " + dateString, e);
        }

    }

    /**
     * Crea un NonBusinessIdDatatype válido con datos de negocio.
     */
    public static NonBusinessIdDatatype createBusinessDatatype() {
        return createBusinessDatatype(
                BUSINESS_RUT_VALID,
                UY_COUNTRY_CODE,
                BUSINESS_DOCUMENT_TYPE_DEFAULT
        );
    }

    /**
     * Crea un NonBusinessIdDatatype con parámetros personalizados.
     */
    public static NonBusinessIdDatatype createBusinessDatatype(
            String businessDocument,
            int businessCountry,
            int businessDocumentType) {

        NonBusinessIdDatatype datatype = new NonBusinessIdDatatype();
        datatype.setBusinessDocument(businessDocument);
        datatype.setBusinessCountry(businessCountry);
        datatype.setBusinessDocumentType(businessDocumentType);
        return datatype;
    }

    /**
     * Crea un NonBusinessIdDatatype con documentos alternativos para testing.
     */
    public static NonBusinessIdDatatype createBusinessWithPersonDatatypeAlternative() {
        return createBusinessWithPersonDatatype(
                BUSINESS_RUT_VALID,
                BUSINESS_DOCUMENT_TYPE_DEFAULT,
                PERSON_DOCUMENT_ALTERNATIVE,
                USER_DOCUMENT_TYPE_CI
        );
    }

    /**
     * Crea un NonBusinessIdDatatype válido con datos de persona y negocio.
     */
    public static NonBusinessIdDatatype createBusinessWithPersonDatatype() {
        return createBusinessWithPersonDatatype(
                BUSINESS_RUT_VALID,
                BUSINESS_DOCUMENT_TYPE_DEFAULT,
                PERSON_DOCUMENT_VALID,
                USER_DOCUMENT_TYPE_CI
        );
    }

    /**
     * Crea un NonBusinessIdDatatype con datos de persona y negocio personalizados.
     */
    public static NonBusinessIdDatatype createBusinessWithPersonDatatype(
            String businessDocument,
            int businessDocumentType,
            String personDocument,
            int personDocumentType) {

        NonBusinessIdDatatype datatype = createBusinessDatatype(
                businessDocument,
                UY_COUNTRY_CODE,
                businessDocumentType
        );
        datatype.setPersonDocument(personDocument);
        datatype.setPersonCountry(UY_COUNTRY_CODE);
        datatype.setPersonDocumentType(personDocumentType);
        return datatype;
    }

    /**
     * Crea un BusinessInformation válido con datos predeterminados.
     */
    public static BusinessInformation createBusinessInformation() {
        return createBusinessInformation(
                BUSINESS_NAME_VALID,
                BUSINESS_RUT_VALID,
                DATE_VALID
        );
    }

    /**
     * Crea un BusinessInformation válido con datos personalizados.
     */
    public static BusinessInformation createBusinessInformation(
            String name,
            String rut,
            String expiration) {
        BusinessInformation info = new BusinessInformation();
        info.setName(name);
        info.setRut(rut);
        info.setExpiration(expiration);

        return info;

    }

    /**
     * Crea un EconomicData válido con balance de tipo REAL_ANNUAL_INCOME.
     */
    public static EconomicData createEconomicDataWithRealIncome() {
        return createEconomicData(
                ECONOMIC_ACTIVITY_ID_VALID,
                ECONOMIC_ACTIVITY_DESCRIPTION,
                TAX_CONDITION_ID_VALID,
                TAX_CONDITION_DESCRIPTION,
                BALANCE_TYPE_REAL,
                INCOME_AMOUNT_VALID,
                parseDate(INCOME_DATE_STRING)
        );
    }

    /**
     * Crea un EconomicData con parámetros personalizados.
     */
    public static EconomicData createEconomicData(
            String economicActivityId,
            String economicActivityDescription,
            String taxConditionId,
            String taxConditionDescription,
            String balanceType,
            double amount,
            Date incomeDate) {

        EconomicData data = new EconomicData();

        // Economic Activity
        EconomicActivity economicActivity = new EconomicActivity();
        economicActivity.setId(economicActivityId);
        economicActivity.setDescription(economicActivityDescription);
        data.setEconomicActivity(economicActivity);

        // Tax
        Tax tax = new Tax();
        GenericObject taxCondition = new GenericObject(taxConditionId, taxConditionDescription);
        tax.setCondition(taxCondition);
        data.setTax(tax);

        // Financial Information
        FinancialInformation financialInfo = new FinancialInformation();
        List<Balance> balances = new ArrayList<>();

        Balance balance = new Balance();
        balance.setBalanceType(balanceType);
        balance.setAmount(amount);
        balance.setIncomeDate(incomeDate);
        balances.add(balance);

        financialInfo.setBalances(balances);
        data.setFinancialInformation(financialInfo);

        return data;
    }

    /**
     * Crea un AddressDatatype con parámetros válidos.
     */
    public static AddressDatatype createValidAddressDatatype() {
        AddressDatatype address = new AddressDatatype();
        address.setPostalCode(POSTAL_CODE_VALID);

        GenericObject country = new GenericObject(COUNTRY_ID_VALID, COUNTRY_NAME_VALID);
        address.setCountry(country);

        GenericObject department = new GenericObject(MVD_ID_VALID, MVD_NAME_VALID);
        address.setDepartment(department);

        GenericObject city = new GenericObject(CITY_ID_VALID, CITY_NAME_VALID);
        address.setCityOrNeighborhood(city);

        GenericObject level1 = new GenericObject(LEVEL1_ID_VALID, LEVEL1_NAME_VALID);
        address.setLevel1(level1);

        GenericObject level2 = new GenericObject(LEVEL2_ID_VALID, LEVEL2_NAME_VALID);
        address.setLevel2(level2);

        GenericObject level3 = new GenericObject(LEVEL3_ID_VALID, LEVEL3_NAME_VALID);
        address.setLevel3(level3);

        return address;
    }

    public static Branch createBranch(String id) {
        Branch bankBranch = new Branch();
        bankBranch.setId(id);

        return bankBranch;
    }

    public static NonBusiness createFullNonBusiness() {
        NonBusiness nonBusiness = new NonBusiness();
        nonBusiness.setDoingBusinessAs(TestDataFactory.BUSINESS_NAME_VALID);

        Bank bank = new Bank();
        Branch branch = createBranch(TestDataFactory.BANK_BRANCH_VALID);
        bank.setBranch(branch);
        nonBusiness.setBank(bank);

        Formation formation = new Formation();
        formation.setDate(TestDataFactory.parseDate(TestDataFactory.FORMATION_DATE_FORMATTED));
        nonBusiness.setFormation(formation);

        List<LegalDocument> legalDocuments = new ArrayList<>();
        legalDocuments.add(
                TestDataFactory.createLegalDocumentBPS(
                        TestDataFactory.LEGAL_DOC_NUMBER_VALID,
                        TestDataFactory.parseDate(TestDataFactory.LEGAL_DOC_ISSUE_DATE_FORMATTED)
                )
        );
        nonBusiness.setLegalDocuments(legalDocuments);

        User user = new User();
        user.setName(TestDataFactory.USER_NAME_VALID);
        user.setPassword(TestDataFactory.USER_PASSWORD_VALID);
        nonBusiness.setUser(user);

        return nonBusiness;
    }

    /**
     * Crea un address minimo.
     */
    public static Address createMinimalAddress() {
        Address address = new Address();
        address.setAddressType(new AddressType());
        return address;
    }

    /**
     * Crea un User válido con datos predeterminados.
     */
    public static User createUser() {
        return createUser(USER_NAME_VALID, USER_PASSWORD_VALID);
    }

    /**
     * Crea un User con parámetros personalizados.
     */
    public static User createUser(String name, String password) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        return user;
    }

    /**
     * Crea una Formation válida con fecha predeterminada.
     */
    public static Formation createFormation() {
        return createFormation(parseDate(FORMATION_DATE_FORMATTED));
    }

    /**
     * Crea una Formation con fecha personalizada.
     */
    public static Formation createFormation(Date formationDate) {
        Formation formation = new Formation();
        formation.setDate(formationDate);
        return formation;
    }

    /**
     * Crea un LegalDocument válido (tipo BPS_REGISTRATION).
     */
    public static LegalDocument createLegalDocumentBPS() {
        return createLegalDocumentBPS(
                LEGAL_DOC_NUMBER_VALID,
                parseDate(LEGAL_DOC_ISSUE_DATE_FORMATTED)
        );
    }

    /**
     * Crea un LegalDocument con parámetros personalizados.
     */
    public static LegalDocument createLegalDocumentBPS(String documentNumber, Date issueDate) {
        LegalDocument legalDocument = new LegalDocument();
        legalDocument.setDocumentNumber(documentNumber);
        legalDocument.setIssueDate(issueDate);

        GenericIdDescription docType = new GenericIdDescription();
        docType.setId("BPS_REGISTRATION");
        docType.setDescription("Registro BPS");
        legalDocument.setLegalDocumentType(docType);

        return legalDocument;
    }

    /**
     * Crea un Status válido con valores predeterminados.
     */
    public static Status createStatus() {
        return Status.of(STATUS_ID_DGI_OK)
                .setProcess(PROCESS_SEARCH)
                .setMessage(STATUS_MESSAGE_VALID);
    }

    /**
     * Crea un Status con parámetros personalizados
     */
    public static Status createStatus(String statusId, String process, int channelId, String message) {
        return Status.of(statusId)
                .setProcess(process)
                .setMessage(message);
    }

    /**
     * Crea un Status con parámetros por defecto
     */
    public static Status createStatusWithDefaults() {
        return Status.of(STATUS_ID_DGI_OK);
    }

    /**
     * Crea un Status sin mensaje (null).
     */
    public static Status createStatusWithoutMessage() {
        return createStatus(STATUS_ID_DGI_OK, PROCESS_SEARCH, CHANNEL_ID_GEMA, null);
    }

    /**
     * Calcula cual es el package a asignar dependiendo del threshold de properties.
     */
    public static String calculatePackageAssignment(double amount) {
        return amount > NO_PACKAGE_THRESHOLD
                ? PACKAGE_ASSIGNMENT_PARTIAL
                : PACKAGE_ASSIGNMENT_FULL;
    }


    /**
     * Crea un address completo con todos los campos.
     */

    public static Address createAddress() {
        Address address = new Address();
        address.setAddressType(new AddressType());
        address.setId(ADDRESS_ID_VALID);
        return address;
    }

    /**
     * Crea un mapa de departamentos para testing.
     */
    public static Map<String, GenericIdDescription> createDepartmentsMap() {
        Map<String, GenericIdDescription> map = new HashMap<>();

        GenericIdDescription montevideo = new GenericIdDescription();
        montevideo.setId(DEPARTMENT_MONTEVIDEO_ID);
        montevideo.setDescription(DEPARTMENT_MONTEVIDEO_NAME);

        map.put(DEPARTMENT_MONTEVIDEO_NAME, montevideo);

        GenericIdDescription canelones = new GenericIdDescription();
        canelones.setId(DEPARTMENT_CANELONES_ID);
        canelones.setDescription(DEPARTMENT_CANELONES_NAME);

        map.put(DEPARTMENT_CANELONES_NAME, canelones);

        return map;
    }

}