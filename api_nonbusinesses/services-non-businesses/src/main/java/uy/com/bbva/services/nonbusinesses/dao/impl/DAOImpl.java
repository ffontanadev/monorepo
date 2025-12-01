package uy.com.bbva.services.nonbusinesses.dao.impl;

import com.bbva.secarq.caas2.core.exception.CaasException;
import com.mongodb.client.MongoCollection;
import org.apache.commons.lang3.time.DateUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import uy.com.bbva.dtos.commons.model.*;
import uy.com.bbva.dtos.commons.v1.model.IdentityDocument;
import uy.com.bbva.dtos.commons.v1.model.RelatedPerson;
import uy.com.bbva.encrypter.MaskUtil;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.mongo.commons.ManagerMongoDBAccess;
import uy.com.bbva.mongo.commons.MongoTemplateBBVA;
import uy.com.bbva.nonbusinessescommons.dtos.models.ContactDetail;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.services.commons.dao.ManagerDataAccessAs400;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.dao.DAO;
import uy.com.bbva.services.nonbusinesses.dao.SQLStatements;
import uy.com.bbva.services.nonbusinesses.model.*;
import uy.com.bbva.services.nonbusinesses.model.User;
import uy.com.bbva.services.nonbusinesses.model.external.BusinessInformation;
import uy.com.bbva.services.nonbusinesses.model.status.Status;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static uy.com.bbva.dtos.commons.utils.Constants.*;

@Component
public class DAOImpl implements DAO {

    @Autowired
    private ManagerDataAccessAs400 managerDataAccessAs400;

    @Autowired
    private ManagerMongoDBAccess managerMongoDBAccess;

    @Autowired
    private MaskUtil maskUtil;

    @Autowired
    private LogUtils logUtils;

    @Value("${db.mongo.veridas.database}")
    private String veridasDBName;

    @Value("${caas.typeAccount}")
    private String encryptType;

    @Value("${api.non-businesses.no-package.threshold}")
    private double noPackageThreshold;

    private static final int GEMA_CHANNEL = 40;
    private static final int LEGAL_ADDRESS = 1;
    private static final String ENTERED_STATUS = "INGRESO";
    private static final String CREATION_TYPE = "UNI";
    private static final String YYYY_MM_DD_DATE_FORMAT = "yyyyMMdd";
    private static final String ERROR_UPDATE_BUSINESS_MOBILE = "Error al actualizar el celular de la empresa";
    private static final String ERROR_UPDATE_PERSON_MAIL = "Error al actualizar el correo de la persona";
    private static final String ERROR_UPDATE_ECONOMIC_DATA = "Error al actualizar la actividad economica";
    private static final String ERROR_UPDATE_BUSINESS_MAIL = "Error al actualizar el correo de la empresa";
    private static final String ERROR_UPDATE_PERSON_MOBILE = "Error al actualizar el celular de la persona";
    private static final String ERROR_CREATE_NON_CUSTOMER = "Error al insertar el no cliente";
    private static final String ERROR_NON_CUSTOMER_EXISTS = "Error al chequear si el no cliente existe";
    private static final String ERROR_NON_CUSTOMER_ON_FINAL_STATE = "Error al chequear si el no cliente esta en estado final";
    private static final String ERROR_UPDATE_BUSINESS_INFORMATION = "Error al completar la informacion de la empresa";
    private static final String ERROR_UPDATE_BUSINESS_BANK_BRANCH = "Error al actualizar la sucursal";
    private static final String ERROR_UPDATE_FORMATION_DATA = "Error al actualizar la informacion de la formacion de la empresa";
    private static final String ERROR_GET_DEPARTMENTS = "Error al obtener los departamentos";
    private static final String ERROR_CREATE_ADDRESS = "Error al crear la direccion";
    private static final String ERROR_UPDATE_BUSINESS_USER = "Error al actualizar el usuario de la empresa";
    private static final String ERROR_UPDATE_BUSINESS_COMMERCIAL_NAME = "Error al actualizar el nombre comercial de la empresa";
    private static final String ERROR_MASK_PWD = "Ocurrió un error al encriptar el password";
    private static final String ERROR_AUDIT_STATUS_CHANGE = "Ocurrio un error al auditar el cambio de estado";
    private static final String ERROR_GET_OWNER = "Ocurrio un error al obtener los datos del dueño de la unipersonal";
    private static final String ERROR_UPDATE_STATUS = "Ocurrio un error al actualizar el estado";
    private static final String ERROR_CHECK_IS_CLIENT = "Ocurrio un error al verificar si el RUT ya es cliente";

    public String getStatus(final String ci, final String rut) throws ServiceException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = managerDataAccessAs400.prepareStatement(SQLStatements.GET_STATUS);
            ps.setInt(1, UY_COUNTRY_CODE);
            ps.setInt(2, USER_DOCUMENT_TYPE_CI);
            ps.setString(3, ci);
            ps.setInt(4, UY_COUNTRY_CODE);
            ps.setInt(5, RUT_DOCUMENT_TYPE);
            ps.setString(6, rut);
            rs = ps.executeQuery();
            if (rs.next()) {
                return trimToEmpty(rs.getString("BBNCEMESTA"));
            } else {
                return "";
            }

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_NON_CUSTOMER_ON_FINAL_STATE, sqlException.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_NON_CUSTOMER_ON_FINAL_STATE, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(ps, rs);
        }
    }

    @Override
    public void createNonBusiness(final String rut, final String ci, final String cellphone) throws ServiceException {
        CallableStatement cs = null;

        try {
            cs = managerDataAccessAs400.prepareCall(SQLStatements.CREATE_NON_BUSINESS);
            cs.setInt(1, UY_COUNTRY_CODE);
            cs.setInt(2, USER_DOCUMENT_TYPE_CI);
            cs.setString(3, ci);
            cs.setInt(4, UY_COUNTRY_CODE);
            cs.setInt(5, RUT_DOCUMENT_TYPE);
            cs.setString(6, rut);
            cs.setString(7, cellphone);
            cs.setString(8, "N");
            cs.setString(9, "N");
            cs.setString(10, "N");
            cs.setString(11, "N");
            cs.setString(12, ENTERED_STATUS);
            cs.setString(13, CREATION_TYPE);
            cs.setInt(14, GEMA_CHANNEL);

            cs.execute();

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_CREATE_NON_CUSTOMER, sqlException.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_CREATE_NON_CUSTOMER, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(cs, null);
        }
    }

    @Override
    public void updateBusinessInformation(final BusinessInformation businessInformation) throws ServiceException {
        PreparedStatement ps = null;

        try {
            ps = managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_INFORMATION);
            ps.setString(1, businessInformation.getName());
            ps.setString(2, businessInformation.getExpiration().replace("-", ""));
            ps.setString(3, businessInformation.getRut());
            ps.setInt(4, UY_COUNTRY_CODE);
            ps.setInt(5, RUT_DOCUMENT_TYPE);

            ps.executeUpdate();

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_UPDATE_BUSINESS_INFORMATION, sqlException.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_UPDATE_BUSINESS_INFORMATION, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(ps, null);
        }
    }

    @Override
    public NonBusiness getNonBusiness(final NonBusinessIdDatatype nonBusinessDatatype, final boolean includeOwnerData, boolean includeContactDetails) throws ServiceException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = managerDataAccessAs400.prepareStatement(SQLStatements.GET_NON_BUSINESS);
            ps.setString(1, nonBusinessDatatype.getBusinessDocument());
            ps.setInt(2, nonBusinessDatatype.getBusinessCountry());
            ps.setInt(3, nonBusinessDatatype.getBusinessDocumentType());
            ps.setString(4, nonBusinessDatatype.getPersonDocument());
            ps.setInt(5, nonBusinessDatatype.getPersonCountry());
            ps.setInt(6, nonBusinessDatatype.getPersonDocumentType());
            rs = ps.executeQuery();

            if (!rs.next()) {
                return null;
            }

            final NonBusiness nonBusiness = new NonBusiness();
            final BusinessDocument businessDocument = new BusinessDocument();
            businessDocument.setDocumentNumber(trim(rs.getString("BBNCPJNDOC")));
            businessDocument.setBusinessDocumentType(new GenericObject("RUT", "RUT"));
            nonBusiness.setBusinessDocuments(List.of(businessDocument));
            nonBusiness.setLegalName(trim(rs.getString("BBNCPJRASO")));
            final LegalDocument legalDocument = new LegalDocument();
            legalDocument.setLegalDocumentType(new GenericIdDescription("BUSINESS_LICENSE_CERTIFICATE_OF_GOOD_STANDING", "Certificate"));
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_DATE_FORMAT);
            final LocalDate date = LocalDate.parse(rs.getString("BBNCPJFRUT"), formatter);
            legalDocument.setExpirationDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            legalDocument.setActive(date.isAfter(LocalDate.now()));
            nonBusiness.setLegalDocuments(List.of(legalDocument));

            if (includeOwnerData) {
                final List<LegalRepresentative> legalRepresentatives = new ArrayList<>();
                final LegalRepresentative owner = new LegalRepresentative();
                owner.setFirstName(trim(rs.getString("BBNCPFNOM1")));
                owner.setMiddleName(trim(rs.getString("BBNCPFNOM2")));
                owner.setLastName(trim(rs.getString("BBNCPFAPE1")));
                owner.setSecondLastName(trim(rs.getString("BBNCPFAPE2")));
                owner.setRole("OWNER");
                final BirthData birthData = new BirthData();
                final Date birthDate = parseDate(rs.getString("BBNCPFFNAC"));
                birthData.setBirthDate(birthDate);
                owner.setBirthData(birthData);
                final IdentityDocument ownerDocument = new IdentityDocument();
                ownerDocument.setDocumentNumber(nonBusinessDatatype.getPersonDocument());
                ownerDocument.setDocumentType(new GenericIdDescription("DNI", "Cedula"));
                ownerDocument.setCountry(new GenericObject("UY", "Uruguay"));
                owner.setIdentityDocument(ownerDocument);
                legalRepresentatives.add(owner);
                nonBusiness.setLegalRepresentatives(legalRepresentatives);
            }

            if (includeContactDetails) {
                final List<ContactDetail> contactDetails = new ArrayList<>();
                final ContactDetail emailContact = new ContactDetail();
                final EmailContact email = new EmailContact();
                email.setContactDetailType("EMAIL");
                email.setAddress(trim(rs.getString("BBNCPJMAIL")));
                emailContact.setContact(email);
                contactDetails.add(emailContact);
                final ContactDetail cellphoneContact = new ContactDetail();
                final MobileContact cellphone = new MobileContact();
                cellphone.setContactDetailType("MOBILE");
                cellphone.setNumber(trim(rs.getString("BBNCPJTEL1")));
                cellphoneContact.setContact(cellphone);
                contactDetails.add(cellphoneContact);
                nonBusiness.setContactDetails(contactDetails);
            }

            return nonBusiness;

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_NON_CUSTOMER_EXISTS, sqlException.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_NON_CUSTOMER_EXISTS, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(ps, rs);
        }
    }

    @Override
    public void updateBusinessMobile(final NonBusinessIdDatatype nonBusinessDatatype, final String number) throws ServiceException {
        PreparedStatement ps = null;

        try {
            ps = managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_MOBILE);
            ps.setString(1, number);
            ps.setString(2, nonBusinessDatatype.getBusinessDocument());
            ps.setInt(3, nonBusinessDatatype.getBusinessCountry());
            ps.setInt(4, nonBusinessDatatype.getBusinessDocumentType());

            ps.executeUpdate();

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_UPDATE_BUSINESS_MOBILE, sqlException.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_UPDATE_BUSINESS_MOBILE, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(ps, null);
        }
    }


    public void updatePersonMobile(final NonBusinessIdDatatype nonBusinessDatatype, final String number) throws ServiceException {
        PreparedStatement ps = null;

        try {
            ps = managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_PERSON_MOBILE);
            ps.setString(1, number);
            ps.setString(2, nonBusinessDatatype.getPersonDocument());
            ps.setInt(3, nonBusinessDatatype.getPersonCountry());
            ps.setInt(4, nonBusinessDatatype.getPersonDocumentType());

            ps.executeUpdate();

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_UPDATE_PERSON_MOBILE, sqlException.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_UPDATE_PERSON_MOBILE, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(ps, null);
        }
    }

    @Override
    public void updateBusinessMail(final NonBusinessIdDatatype nonBusinessDatatype, final String address) throws ServiceException {
        PreparedStatement ps = null;

        try {
            ps = managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_MAIL);
            ps.setString(1, address);
            ps.setString(2, nonBusinessDatatype.getBusinessDocument());
            ps.setInt(3, nonBusinessDatatype.getBusinessCountry());
            ps.setInt(4, nonBusinessDatatype.getBusinessDocumentType());

            ps.executeUpdate();

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_UPDATE_BUSINESS_MAIL, sqlException.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_UPDATE_BUSINESS_MAIL, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(ps, null);
        }
    }

    @Override
    public void updatePersonMail(final NonBusinessIdDatatype nonBusinessDatatype, final String address) throws ServiceException {
        PreparedStatement ps = null;

        try {
            ps = managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_PERSON_MAIL);
            ps.setString(1, address);
            ps.setString(2, nonBusinessDatatype.getPersonDocument());
            ps.setInt(3, nonBusinessDatatype.getPersonCountry());
            ps.setInt(4, nonBusinessDatatype.getPersonDocumentType());

            ps.executeUpdate();

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_UPDATE_PERSON_MAIL, sqlException.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_UPDATE_PERSON_MAIL, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(ps, null);
        }
    }

    @Override
    public void createAddress(final NonBusinessIdDatatype nonBusinessDatatype, final AddressDatatype addressDatatype) throws ServiceException {
        CallableStatement ps = null;

        try {
            ps = managerDataAccessAs400.prepareCall(SQLStatements.INSERT_ADDRESS);
            ps.setInt(1, nonBusinessDatatype.getBusinessCountry());
            ps.setInt(2, nonBusinessDatatype.getBusinessDocumentType());
            ps.setString(3, nonBusinessDatatype.getBusinessDocument());
            ps.setInt(4, LEGAL_ADDRESS);
            ps.setInt(5, parseInt(addressDatatype.getCountry().getId()));
            ps.setString(6, trimToEmpty(addressDatatype.getPostalCode()));
            ps.setInt(7, parseInt(addressDatatype.getDepartment().getId()));
            ps.setInt(8, parseInt(addressDatatype.getCityOrNeighborhood().getId()));
            ps.setInt(9, parseInt(addressDatatype.getLevel1().getId()));
            ps.setInt(10, parseInt(addressDatatype.getLevel2().getId()));
            ps.setInt(11, parseInt(addressDatatype.getLevel3().getId()));
            ps.setString(12, trimToEmpty(addressDatatype.getLevel1().getName()));
            ps.setString(13, trimToEmpty(addressDatatype.getLevel2().getName()));
            ps.setString(14, trimToEmpty(addressDatatype.getLevel3().getName()));

            ps.execute();

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_CREATE_ADDRESS, ERROR_CREATE_ADDRESS, sqlException);
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_CREATE_ADDRESS, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(ps, null);
        }
    }

    @Override
    public Map<String, GenericIdDescription> getDepartments() throws ServiceException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = managerDataAccessAs400.prepareStatement(SQLStatements.GET_DEPARTMENTS);
            rs = ps.executeQuery();

            final Map<String, GenericIdDescription> departmentsMap = new HashMap<>();

            while (rs.next()) {
                final String departmentIsoCode = rs.getString("DEPISOCOD").trim();
                final Integer departmentBanTotalCode = rs.getInt("DEPBTCOD");
                final String departmentName = rs.getString("DEPNOM").trim();
                departmentsMap.put(departmentIsoCode, new GenericIdDescription(String.valueOf(departmentBanTotalCode), departmentName));
            }

            return departmentsMap;
        } catch (SQLException sqle) {
            logUtils.logError(this.getClass().getName(), ERROR_GET_DEPARTMENTS, ERROR_GET_DEPARTMENTS, sqle);
            throw new ServiceException(this.getClass().getName(), ERROR_GET_DEPARTMENTS, sqle);
        } finally {
            managerDataAccessAs400.closeResources(ps, rs);
        }
    }

    @Override
    public void updateBusinessBankBranch(final NonBusinessIdDatatype nonBusinessDatatype, final String bankBranch) throws ServiceException {
        PreparedStatement ps = null;

        try {
            ps = managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BANK_BRANCH);
            ps.setString(1, bankBranch);
            ps.setString(2, nonBusinessDatatype.getBusinessDocument());
            ps.setInt(3, nonBusinessDatatype.getBusinessCountry());
            ps.setInt(4, nonBusinessDatatype.getBusinessDocumentType());

            ps.executeUpdate();

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_UPDATE_BUSINESS_BANK_BRANCH, sqlException.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_UPDATE_BUSINESS_BANK_BRANCH, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(ps, null);
        }
    }

    @Override
    public void updateBusinessFormationData(final NonBusinessIdDatatype nonBusinessDatatype, final Formation formation, final LegalDocument legalDocument) throws ServiceException {
        PreparedStatement psUpdateBusinessFormationData = null;
        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD_DATE_FORMAT);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            psUpdateBusinessFormationData = managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_FORMATION_DATA);
            psUpdateBusinessFormationData.setString(1, dateFormat.format(formation.getDate()));
            psUpdateBusinessFormationData.setString(2, dateFormat.format(legalDocument.getIssueDate()));
            psUpdateBusinessFormationData.setString(3, legalDocument.getDocumentNumber());
            psUpdateBusinessFormationData.setString(4, dateFormat.format(legalDocument.getIssueDate()));
            psUpdateBusinessFormationData.setString(5, nonBusinessDatatype.getBusinessDocument());
            psUpdateBusinessFormationData.setInt(6, nonBusinessDatatype.getBusinessCountry());
            psUpdateBusinessFormationData.setInt(7, nonBusinessDatatype.getBusinessDocumentType());

            psUpdateBusinessFormationData.executeUpdate();

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_UPDATE_FORMATION_DATA, sqlException.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_UPDATE_FORMATION_DATA, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(psUpdateBusinessFormationData, null);
        }
    }

    @Override
    public void updateBusinessEconomicData(final NonBusinessIdDatatype nonBusinessDatatype, final EconomicData economicData) throws ServiceException {
        PreparedStatement psUpdateBusinessEconomicData = null;

        try {
            final Balance balance = economicData.getFinancialInformation().getBalances().stream()
                    .filter(balanceItem -> "REAL_ANNUAL_INCOME".equals(balanceItem.getBalanceType()) || "PROJECTED_ANNUAL_INCOME".equals(balanceItem.getBalanceType()))
                    .findFirst().orElse(null);

            if (balance != null) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD_DATE_FORMAT);
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                psUpdateBusinessEconomicData = managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_ECONOMIC_DATA);
                psUpdateBusinessEconomicData.setInt(1, parseInt(economicData.getEconomicActivity().getId()));
                psUpdateBusinessEconomicData.setString(2, "REAL_ANNUAL_INCOME".equals(balance.getBalanceType()) ? "REAL" : "PROYECTADO");
                psUpdateBusinessEconomicData.setString(3, balance.getAmount() > noPackageThreshold ? "INCOME_ASSIGN_PARTIAL" : "INCOME_ASSIGN_FULL");
                psUpdateBusinessEconomicData.setDouble(4, balance.getAmount());
                psUpdateBusinessEconomicData.setString(5, dateFormat.format(balance.getIncomeDate()));
                psUpdateBusinessEconomicData.setString(6, economicData.getTax().getCondition().getId());
                psUpdateBusinessEconomicData.setString(7, "Salario, remuneraciones, gastos personales, honorarios profesionales");
                psUpdateBusinessEconomicData.setString(8, nonBusinessDatatype.getBusinessDocument());
                psUpdateBusinessEconomicData.setInt(9, nonBusinessDatatype.getBusinessCountry());
                psUpdateBusinessEconomicData.setInt(10, nonBusinessDatatype.getBusinessDocumentType());
                psUpdateBusinessEconomicData.executeUpdate();
            }

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_UPDATE_ECONOMIC_DATA, sqlException.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_UPDATE_ECONOMIC_DATA, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(psUpdateBusinessEconomicData, null);
        }
    }

    @Override
    public void updateBusinessUser(final NonBusinessIdDatatype nonBusinessDatatype, final User user) throws ServiceException {
        PreparedStatement psUpdateBusinessUser = null;

        try {
            psUpdateBusinessUser = managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_USER);
            psUpdateBusinessUser.setString(1, nonBusinessDatatype.getPersonDocument());
            psUpdateBusinessUser.setInt(2, nonBusinessDatatype.getPersonCountry());
            psUpdateBusinessUser.setInt(3, nonBusinessDatatype.getPersonDocumentType());

            psUpdateBusinessUser.executeUpdate();

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_UPDATE_BUSINESS_USER, sqlException.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_UPDATE_BUSINESS_USER, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(psUpdateBusinessUser, null);
        }
    }

    @Override
    public void auditStatusChange(final NonBusinessIdDatatype nonBusinessIdDatatype, final Status status) throws ServiceException {
        CallableStatement csAuditStatusChange = null;

        try {
            csAuditStatusChange = managerDataAccessAs400.prepareCall(SQLStatements.AUDIT_STATUS_CHANGE);
            csAuditStatusChange.setInt(1, nonBusinessIdDatatype.getBusinessCountry());
            csAuditStatusChange.setInt(2, nonBusinessIdDatatype.getBusinessDocumentType());
            csAuditStatusChange.setString(3, nonBusinessIdDatatype.getBusinessDocument());
            csAuditStatusChange.setInt(4, nonBusinessIdDatatype.getPersonCountry());
            csAuditStatusChange.setInt(5, nonBusinessIdDatatype.getPersonDocumentType());
            csAuditStatusChange.setString(6, nonBusinessIdDatatype.getPersonDocument());
            csAuditStatusChange.setInt(7, status.getChannelId());
            csAuditStatusChange.setString(8, "");
            csAuditStatusChange.setString(9, status.getProcess());
            csAuditStatusChange.setString(10, status.getId());
            csAuditStatusChange.setInt(11, 0);
            csAuditStatusChange.setString(12, status.getMessage());
            csAuditStatusChange.execute();

        } catch (final SQLException e) {
            logUtils.logError(this.getClass().getName(), ERROR_AUDIT_STATUS_CHANGE, e.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_AUDIT_STATUS_CHANGE, e);
        } finally {
            managerDataAccessAs400.closeResources(csAuditStatusChange, null);
        }
    }

    @Override
    public int getTermVersion(final String termId) {
        final MongoTemplateBBVA mongoTemplateBBVA = managerMongoDBAccess.getMongoTemplateBBVA("documents");
        final Query query = new Query();
        query.addCriteria(Criteria.where("documentId").is(termId)).limit(1);
        final Document document = mongoTemplateBBVA.findOne(query, Document.class, "documents");
        return document != null ? document.getInteger("version", 0) : 0;
    }

    @Override
    public void updateBusinessCommercialName(final NonBusinessIdDatatype nonBusinessDatatype, final String doingBusinessAs) throws ServiceException {
        PreparedStatement ps = null;

        try {
            ps = managerDataAccessAs400.prepareStatement(SQLStatements.UPDATE_BUSINESS_COMMERCIAL_NAME);
            ps.setString(1, doingBusinessAs);
            ps.setString(2, nonBusinessDatatype.getBusinessDocument());
            ps.setInt(3, nonBusinessDatatype.getBusinessCountry());
            ps.setInt(4, nonBusinessDatatype.getBusinessDocumentType());

            ps.executeUpdate();

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_UPDATE_BUSINESS_COMMERCIAL_NAME, sqlException.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_UPDATE_BUSINESS_COMMERCIAL_NAME, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(ps, null);
        }
    }

    @Override
    public void createTemporaryPassword(final NonBusinessIdDatatype nonBusinessDatatype, final User user) throws ServiceException {
        final MongoTemplateBBVA mongoTemplateBBVA = managerMongoDBAccess.getMongoTemplateBBVA(veridasDBName);
        final MongoCollection<Document> collection = mongoTemplateBBVA.getCollection("customersActivations");

        final Document temporaryPassword = new Document();
        temporaryPassword.put("personDocumentType", nonBusinessDatatype.getPersonDocumentType());
        temporaryPassword.put("personDocumentCountry", nonBusinessDatatype.getPersonCountry());
        temporaryPassword.put("personDocumentNumber", nonBusinessDatatype.getPersonDocument());
        temporaryPassword.put("businessDocumentType", nonBusinessDatatype.getBusinessDocumentType());
        temporaryPassword.put("businessDocumentCountry", nonBusinessDatatype.getBusinessCountry());
        temporaryPassword.put("businessDocumentNumber", nonBusinessDatatype.getBusinessDocument());
        temporaryPassword.put("user", user.getName());
        temporaryPassword.put("cipherPassword", cipherPassword(user.getPassword()));
        temporaryPassword.put("date", new Date());

        collection.insertOne(temporaryPassword);
    }

    @Override
    public void updateStatus(final NonBusinessIdDatatype nonBusinessDatatype, final String status) throws ServiceException {
        CallableStatement cs = null;

        try {
            cs = managerDataAccessAs400.prepareCall(SQLStatements.UPDATE_STATUS);
            cs.setString(1, status);
            cs.setInt(2, UY_COUNTRY_CODE);
            cs.setInt(3, USER_DOCUMENT_TYPE_CI);
            cs.setString(4, nonBusinessDatatype.getPersonDocument());
            cs.setInt(5, UY_COUNTRY_CODE);
            cs.setInt(6, RUT_DOCUMENT_TYPE);
            cs.setString(7, nonBusinessDatatype.getBusinessDocument());
            cs.execute();

        } catch (SQLException sqlException) {
            logUtils.logError(this.getClass().getName(), ERROR_UPDATE_STATUS, sqlException.getMessage());
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_UPDATE_STATUS, sqlException);
        } finally {
            managerDataAccessAs400.closeResources(cs);
        }
    }

    @Override
    public RelatedPerson getOwner(final NonBusinessIdDatatype nonBusinessIdDatatype) throws ServiceException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = managerDataAccessAs400.prepareStatement(SQLStatements.GET_OWNER);
            ps.setString(1, nonBusinessIdDatatype.getPersonDocument());
            ps.setInt(2, nonBusinessIdDatatype.getPersonCountry());
            ps.setInt(3, nonBusinessIdDatatype.getPersonDocumentType());
            rs = ps.executeQuery();

            if (rs.next()) {
                final RelatedPerson owner = new RelatedPerson();
                owner.setFirstName(trimToEmpty(rs.getString("BBNCPFNOM1")));
                owner.setMiddleName(trimToEmpty(rs.getString("BBNCPFNOM2")));
                owner.setLastName(trimToEmpty(rs.getString("BBNCPFAPE1")));
                owner.setSecondLastName(trimToEmpty(rs.getString("BBNCPFAPE2")));
                return owner;
            }

            throw new ServiceException(this.getClass().getName(), ERROR_GET_OWNER, new Exception("Owner not found"));

        } catch (SQLException sqle) {
            logUtils.logError(this.getClass().getName(), ERROR_GET_OWNER, ERROR_GET_OWNER, sqle);
            throw new ServiceException(this.getClass().getName(), ERROR_GET_OWNER, sqle);
        } finally {
            managerDataAccessAs400.closeResources(ps, rs);
        }
    }

    @Override
    public boolean checkIsClient(final String rut) throws ServiceException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = managerDataAccessAs400.prepareStatement(SQLStatements.CHECK_IS_CLIENT);
            ps.setString(1, rut);
            ps.setInt(2, RUT_DOCUMENT_TYPE);
            ps.setInt(3, UY_COUNTRY_CODE);
            rs = ps.executeQuery();

            return  rs.next() && rs.getBoolean("IS_CLIENT");

        } catch (SQLException sqle) {
            logUtils.logError(this.getClass().getName(), ERROR_CHECK_IS_CLIENT, ERROR_CHECK_IS_CLIENT, sqle);
            throw new ServiceException(this.getClass().getName(), ERROR_CHECK_IS_CLIENT, sqle);
        } finally {
            managerDataAccessAs400.closeResources(ps, rs);
        }
    }

    private Date parseDate(final String stringDate) throws ServiceException {
        try {
            return DateUtils.parseDate(stringDate, YYYY_MM_DD_DATE_FORMAT);
        } catch (ParseException e) {
            throw new ServiceException(this.getClass().getCanonicalName(), "Error al parsear feha de nacimiento", e);
        }
    }

    private String cipherPassword(final String password) throws ServiceException {
        try {
            return maskUtil.encrypt(password, encryptType);
        } catch (final CaasException ex) {
            throw new ServiceException(ERROR_MASK_PWD, ERROR_MASK_PWD, ex);
        }
    }

}