package uy.com.bbva.services.nonbusinesses.service.impl;

import com.bbva.secarq.caas2.core.exception.CaasException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uy.com.bbva.dtos.commons.enums.ContactTypeEnum;
import uy.com.bbva.dtos.commons.model.*;
import uy.com.bbva.dtos.commons.utils.Constants;
import uy.com.bbva.dtos.commons.v1.model.RelatedPerson;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.nonbusinessescommons.dtos.models.ContactDetail;
import uy.com.bbva.nonbusinessescommons.idmanagement.datatypes.NonBusinessIdDatatype;
import uy.com.bbva.nonbusinessescommons.idmanagement.idmanagement.NonBusinessIdManagement;
import uy.com.bbva.services.commons.exceptions.BusinessException;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.dao.DAO;
import uy.com.bbva.services.nonbusinesses.model.*;
import uy.com.bbva.services.nonbusinesses.model.Location;
import uy.com.bbva.services.nonbusinesses.model.exceptions.DGIException;
import uy.com.bbva.services.nonbusinesses.model.external.BusinessInformation;
import uy.com.bbva.services.nonbusinesses.model.status.Status;
import uy.com.bbva.services.nonbusinesses.service.NonBusinessesApiService;
import uy.com.bbva.services.nonbusinesses.service.external.BusinessInformationService;
import uy.com.bbva.services.nonbusinesses.service.external.RisksService;
import uy.com.bbva.services.nonbusinesses.service.utils.AddressUtils;
import uy.com.bbva.services.nonbusinesses.service.utils.validator.NameValidator;
import uy.com.bbva.services.nonbusinesses.service.utils.validator.Validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;
import static uy.com.bbva.dtos.commons.utils.Constants.RUT_DOCUMENT_TYPE;

@Service
@SuppressWarnings({"java:S5843", "java:S5857", "java:S5998"})
public class NonBusinessesApiServiceImpl implements NonBusinessesApiService {

    @Autowired
    private DAO dao;

    @Autowired
    private LogUtils logUtils;

    @Autowired
    private AddressUtils addressUtil;

    @Autowired
    private RisksService risksService;

    @Autowired
    private BusinessInformationService businessInformationService;

    @Autowired
    private NonBusinessIdManagement nonBusinessIdManagement;

    @Autowired
    private NameValidator nameValidator;

    @Value("${api.non-businesses.validate.name:true}")
    private boolean validateName;

    private static final int CI_DOCUMENT = 1;
    private static final int RUT_DOCUMENT = 3;
    private static final int URUGUAY_CODE = 845;

    private static final String CELLPHONE_REGEX = "^09\\d{7}$";

    private static final String MAIL_REGEX = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    // Length between 8 and 16 characters
    // Contains at least 2 digits
    // Contains at least 2 letters (any case)
    private static final String PASSWORD_REGEX = "^(?=(?:.*\\d){2,})(?=(?:.*[a-zA-Z]){2,}).{8,16}$";

    private static final String ENTRY = "INGRESO";
    private static final String RESUME = "RETOMA";
    private static final List<String> FINAL_STATES = List.of("PROCESADO", "ANULADO");
    private static final List<String> RESUME_STATES = List.of(ENTRY, RESUME);

    private static final String SEARCH_PROCESS = "NBSEARCH";
    private static final String POST_PROCESS = "NBPOST";
    private static final String ADDRESS_PROCESS = "NBPADDR";
    private static final String CONTACT_PROCESS = "NBPCONT";
    private static final String ECONOMIC_DATA_PROCESS = "NBPECO";
    private static final String BUSINESS_DATA_PROCESS = "NBPATCH";
    private static final String ACCEPT_TERMS_PROCESS = "NBPTERMS";

    private static final String ERROR_FINAL_STATE = "La gestion se encuentra en un estado final";
    private static final String ERROR_INVALID_STATUS = "El estado actual no es el correcto";
    private static final String ERROR_EXPIRED_CERTIFICATE = "La empresa tiene el certificado vencido en DGI";
    private static final String ERROR_RUT_NOT_UNIPERSONAL = "El rut no corresponde a una empresa unipersonal";
    private static final String ERROR_ALREADY_CLIENT = "El rut pertenece a un cliente del banco";
    private static final String ERROR_GET_BUSINESS_INFORMATION = "Error al obtener los datos de la empresa";
    private static final String ERROR_INVALID_MAIL = "El correo esta en la lista negra";
    private static final String ERROR_INVALID_USER_ID = "El user-id es invalido";
    private static final String ERROR_DOCUMENT_NOT_MATCHING = "Los documentos no coinciden";

    @Override
    public DataList search(final String userId, final NonBusinessSearch nonBusinessSearch) throws ServiceException {

        final String[] parts = trimToEmpty(userId).split("-", 2);

        if (parts.length != 2) {
            logUtils.logError(this.getClass().getCanonicalName(), ERROR_INVALID_USER_ID + userId, ERROR_INVALID_USER_ID, new Exception(ERROR_INVALID_USER_ID));
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_INVALID_USER_ID + userId, new Exception(ERROR_INVALID_USER_ID));
        }

        final String rut = parts[1];
        final String ownerDocument = parts[0];

        if (!rut.equals(nonBusinessSearch.getBusinessDocument().getDocumentNumber())) {
            logUtils.logError(this.getClass().getCanonicalName(), ERROR_DOCUMENT_NOT_MATCHING, ERROR_DOCUMENT_NOT_MATCHING, new Exception(ERROR_DOCUMENT_NOT_MATCHING));
            throw new ServiceException(ERROR_DOCUMENT_NOT_MATCHING, ERROR_DOCUMENT_NOT_MATCHING, new Exception(ERROR_DOCUMENT_NOT_MATCHING));
        }

        final NonBusinessIdDatatype nonBusinessIdDatatype = new NonBusinessIdDatatype();
        nonBusinessIdDatatype.setPersonDocumentType(CI_DOCUMENT);
        nonBusinessIdDatatype.setPersonCountry(URUGUAY_CODE);
        nonBusinessIdDatatype.setPersonDocument(ownerDocument);
        nonBusinessIdDatatype.setBusinessDocumentType(RUT_DOCUMENT);
        nonBusinessIdDatatype.setBusinessCountry(URUGUAY_CODE);
        nonBusinessIdDatatype.setBusinessDocument(rut);

        final boolean isRutClient = dao.checkIsClient(rut);

        if (isRutClient) {
            logUtils.logError(this.getClass().getCanonicalName(), ERROR_ALREADY_CLIENT, ERROR_ALREADY_CLIENT, new Exception(ERROR_ALREADY_CLIENT));
            dao.auditStatusChange(nonBusinessIdDatatype, Status.of("NB_CLI_ERR").setProcess(SEARCH_PROCESS));
            throw new BusinessException(ERROR_ALREADY_CLIENT, ERROR_ALREADY_CLIENT, "NON_BUSINESS_SEARCH_ERROR_ALREADY_CLIENT", new Exception(ERROR_ALREADY_CLIENT));
        }

        final String currentStatus = dao.getStatus(ownerDocument, rut);

        if (FINAL_STATES.contains(currentStatus) || RESUME_STATES.contains(currentStatus)) {
            logUtils.logError(this.getClass().getCanonicalName(), ERROR_INVALID_STATUS, ERROR_INVALID_STATUS, new Exception(ERROR_INVALID_STATUS));
            dao.auditStatusChange(nonBusinessIdDatatype, Status.of("NB_ESI_ERR").setProcess(SEARCH_PROCESS).setMessage("Estado invalido " + currentStatus));
            throw new BusinessException(ERROR_INVALID_STATUS, ERROR_INVALID_STATUS, "NON_BUSINESS_SEARCH_INVALID_CURRENT_STATUS", new Exception(ERROR_INVALID_STATUS));
        }

        final BusinessInformation businessInformation = getBusinessInformation(nonBusinessIdDatatype);
        dao.updateBusinessInformation(businessInformation);

        final LocalDate today = LocalDate.now();
        final LocalDate expirationDate = tryParseDate(businessInformation.getExpiration(), "yyyy-MM-dd");

        if (expirationDate != null && expirationDate.isBefore(today)) {
            logUtils.logError(this.getClass().getCanonicalName(), ERROR_EXPIRED_CERTIFICATE, ERROR_EXPIRED_CERTIFICATE, new Exception(ERROR_EXPIRED_CERTIFICATE));
            dao.auditStatusChange(nonBusinessIdDatatype, Status.of("DGICRT_ERR").setProcess(SEARCH_PROCESS).setMessage("Certificado vencido el " + businessInformation.getExpiration()));
            throw new BusinessException(ERROR_EXPIRED_CERTIFICATE, ERROR_EXPIRED_CERTIFICATE, "NON_BUSINESS_SEARCH_ERROR_DGI_CERTIFICADO_VENCIDO", new Exception(ERROR_EXPIRED_CERTIFICATE));
        }

        if (validateName && !nameValidator.validate(businessInformation.getName())) {
            logUtils.logError(this.getClass().getCanonicalName(), ERROR_RUT_NOT_UNIPERSONAL, ERROR_RUT_NOT_UNIPERSONAL, new Exception(ERROR_RUT_NOT_UNIPERSONAL));
            dao.auditStatusChange(nonBusinessIdDatatype, Status.of("DGINTU_ERR").setProcess(SEARCH_PROCESS).setMessage(businessInformation.getName()));
            throw new BusinessException(ERROR_RUT_NOT_UNIPERSONAL, ERROR_RUT_NOT_UNIPERSONAL, "NON_BUSINESS_SEARCH_ERROR_DGI_EMPRESA_NO_UNIPERSONAL", new Exception(ERROR_RUT_NOT_UNIPERSONAL));
        }

        final RelatedPerson owner = dao.getOwner(nonBusinessIdDatatype);
        if (validateName && !nameValidator.similarName(owner, businessInformation.getName())) {
            logUtils.logError(this.getClass().getCanonicalName(), ERROR_RUT_NOT_UNIPERSONAL, ERROR_RUT_NOT_UNIPERSONAL, new Exception(ERROR_RUT_NOT_UNIPERSONAL));
            dao.auditStatusChange(nonBusinessIdDatatype, Status.of("DGINTC_ERR").setProcess(SEARCH_PROCESS).setMessage(businessInformation.getName()));
            throw new BusinessException(ERROR_RUT_NOT_UNIPERSONAL, ERROR_RUT_NOT_UNIPERSONAL, "NON_BUSINESS_SEARCH_ERROR_DGI_NOMBRE_NO_CORRESPONDE_PF", new Exception(ERROR_RUT_NOT_UNIPERSONAL));
        }

        dao.updateStatus(nonBusinessIdDatatype, "DGI_OK");
        dao.auditStatusChange(nonBusinessIdDatatype, Status.of("DGI_OK").setProcess(SEARCH_PROCESS));

        final NonBusinessInformation nonBusinessInformation = new NonBusinessInformation();
        nonBusinessInformation.setLegalName(businessInformation.getName());
        final BusinessAddress businessAddress = new BusinessAddress();
        final Location location = new Location();
        location.setFormattedAddress(businessInformation.getLegalAddress());
        businessAddress.setLocation(location);
        nonBusinessInformation.setAddress(businessAddress);
        final BusinessDocument businessDocument = new BusinessDocument();
        businessDocument.setDocumentNumber(businessInformation.getRut());
        businessDocument.setBusinessDocumentType(new GenericObject("RUT", "RUT"));
        nonBusinessInformation.setBusinessDocument(businessDocument);
        final DataList dataList = new DataList();
        final List<NonBusinessInformation> data = new ArrayList<>();
        data.add(nonBusinessInformation);
        dataList.setData(data);
        return dataList;
    }

    @Override
    public String postNonBusiness(final String rut, final String ownerDocument, final String cellphone) throws ServiceException {

        Validator.of(rut).check(StringUtils::isNumeric, "NON_BUSINESS_POST_RUT_NOT_NUMERIC", "Invalid rut");
        Validator.of(ownerDocument).check(StringUtils::isNumeric, "NON_BUSINESS_POST_CI_NOT_NUMERIC", "Invalid document");

        final String currentStatus = dao.getStatus(ownerDocument, rut);

        final NonBusinessIdDatatype nonBusinessIdDatatype = new NonBusinessIdDatatype();
        nonBusinessIdDatatype.setBusinessCountry(Constants.UY_COUNTRY_CODE);
        nonBusinessIdDatatype.setBusinessDocumentType(RUT_DOCUMENT_TYPE);
        nonBusinessIdDatatype.setBusinessDocument(rut);
        nonBusinessIdDatatype.setPersonCountry(Constants.UY_COUNTRY_CODE);
        nonBusinessIdDatatype.setPersonDocumentType(CI_DOCUMENT);
        nonBusinessIdDatatype.setPersonDocument(ownerDocument);

        if (FINAL_STATES.contains(currentStatus)) {
            dao.auditStatusChange(nonBusinessIdDatatype, Status.of("NB_ESI_ERR").setProcess(POST_PROCESS).setMessage("El estado actual no permite retomar " + currentStatus));
            throw new BusinessException(ERROR_FINAL_STATE, ERROR_FINAL_STATE, "NON_BUSINESS_POST_USER_ON_FINAL_STATE", new Exception(ERROR_FINAL_STATE));
        }

        if (isEmpty(currentStatus) || RESUME_STATES.contains(currentStatus)) {
            dao.createNonBusiness(rut, ownerDocument, cellphone);
        }

        if (ENTRY.equals(currentStatus)) {
            dao.updateStatus(nonBusinessIdDatatype, RESUME);
        }

        final String status = isEmpty(currentStatus) ? ENTRY : RESUME;
        dao.auditStatusChange(nonBusinessIdDatatype, Status.of(status).setProcess(POST_PROCESS));

        return getNonBusinessIdFromnonBusinessIdDatatype(nonBusinessIdDatatype);
    }

    @Override
    public DataNonBusiness getNonBusinessById(final String nonBusinessId, final String expand) throws ServiceException {

        final NonBusinessIdDatatype nonBusinessIdDatatype = getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId);
        final boolean includeOwnerData = expand.toUpperCase().contains("LEGAL-REPRESENTATIVES");
        final boolean includeContactDetails = expand.toUpperCase().contains("CONTACT-DETAILS");

        final NonBusiness nonBusiness = dao.getNonBusiness(nonBusinessIdDatatype, includeOwnerData, includeContactDetails);
        nonBusiness.setId(nonBusinessId);

        final DataNonBusiness dataNonBusiness = new DataNonBusiness();
        dataNonBusiness.setData(nonBusiness);

        return dataNonBusiness;

    }

    @Override
    public void createContactDetail(final String nonBusinessId, final ContactDetail contactDetailBody) throws ServiceException {

        final NonBusinessIdDatatype nonBusinessIdDatatype = getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId);
        if (ContactTypeEnum.EMAIL.getId().equals(contactDetailBody.getContact().getContactDetailType().trim())) {

            final EmailContact emailContact = (EmailContact) contactDetailBody.getContact();

            Validator.of(emailContact)
                    .check(mail -> isNotEmpty(mail.getAddress()), "EMPTY_MAIL", "Email is required")
                    .check(mail -> Pattern.compile(MAIL_REGEX).matcher(mail.getAddress()).matches(), "INVALID_MAIL_PATTERN", "Invalid mail");

            if (risksService.isMailBlacklisted(emailContact, nonBusinessIdDatatype)) {
                logUtils.logError(this.getClass().getCanonicalName(), ERROR_INVALID_MAIL, ERROR_INVALID_MAIL, new Exception(ERROR_INVALID_MAIL));
                dao.auditStatusChange(nonBusinessIdDatatype, Status.of("NB_EBL_ERR").setProcess(CONTACT_PROCESS).setMessage(emailContact.getAddress() + " es un correo invalido para el documento"));
                throw new BusinessException(ERROR_INVALID_MAIL, ERROR_INVALID_MAIL, "ERROR_BLACKLISTED_EMAIL", new Exception(ERROR_INVALID_MAIL));
            }

            dao.updatePersonMail(nonBusinessIdDatatype, emailContact.getAddress());
            dao.updateBusinessMail(nonBusinessIdDatatype, emailContact.getAddress());

            dao.updateStatus(nonBusinessIdDatatype, "NB_CNT_OK");
            dao.auditStatusChange(nonBusinessIdDatatype, Status.of("NB_CNTE_OK").setProcess(CONTACT_PROCESS));

        } else if (ContactTypeEnum.MOBILE.getId().equals(contactDetailBody.getContact().getContactDetailType().trim())) {

            final MobileContact mobileContact = (MobileContact) contactDetailBody.getContact();

            Validator.of(mobileContact)
                    .check(mobile -> isNotEmpty(mobile.getNumber()), "EMPTY_MOBILE", "Cellphone is required")
                    .check(mobile -> Pattern.compile(CELLPHONE_REGEX).matcher(mobile.getNumber()).matches(), "ERROR_INVALID_MOBILE_PATTERN", "Invalid cellphone");

            dao.updatePersonMobile(nonBusinessIdDatatype, mobileContact.getNumber());
            dao.updateBusinessMobile(nonBusinessIdDatatype, mobileContact.getNumber());

            dao.updateStatus(nonBusinessIdDatatype, "NB_CNT_OK");
            dao.auditStatusChange(nonBusinessIdDatatype, Status.of("NB_CNTM_OK").setProcess(CONTACT_PROCESS));

        }

    }

    @Override
    public void createAddress(final String nonBusinessId, final Address address) throws ServiceException {

        final Map<String, GenericIdDescription> departmentsMap = dao.getDepartments();
        final AddressDatatype addressDatatype = addressUtil.getAddressDatatypeFromAddress(address, departmentsMap);
        final NonBusinessIdDatatype nonBusinessIdDatatype = getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId);


        dao.createAddress(nonBusinessIdDatatype, addressDatatype);

        dao.updateStatus(nonBusinessIdDatatype, "NB_ADD_OK");
        dao.auditStatusChange(nonBusinessIdDatatype, Status.of("NB_ADD_OK").setProcess(ADDRESS_PROCESS));

    }

    @Override
    public void patchNonBusiness(final String nonBusinessId, final NonBusiness nonBusiness) throws ServiceException {

        final NonBusinessIdDatatype nonBusinessIdDatatype = getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId);

        if (!isEmpty(nonBusiness.getDoingBusinessAs())) {
            dao.updateBusinessCommercialName(nonBusinessIdDatatype, nonBusiness.getDoingBusinessAs());
        }

        if (nonBusiness.getBank() != null && nonBusiness.getBank().getBranch() != null) {
            dao.updateBusinessBankBranch(nonBusinessIdDatatype, nonBusiness.getBank().getBranch().getId());
        }

        if (nonBusiness.getFormation() != null) {
            final LegalDocument legalDocument = nonBusiness.getLegalDocuments().stream()
                    .filter(document -> document.getLegalDocumentType() != null && "BPS_REGISTRATION".equals(document.getLegalDocumentType().getId()))
                    .findFirst().orElse(null);

            if (legalDocument != null) {
                dao.updateBusinessFormationData(nonBusinessIdDatatype, nonBusiness.getFormation(), legalDocument);
            }
        }

        if (nonBusiness.getUser() != null) {
            Validator.of(nonBusiness.getUser())
                    .check(user -> isNotEmpty(user.getName()), "EMPTY_NAME", "Name is required")
                    .check(user -> isNotEmpty(user.getPassword()), "EMPTY_PASSWORD", "Password is required")
                    .check(user -> user.getPassword().length() >= 8, "PASSWORD_TOO_SHORT", "Password too short")
                    .check(user -> user.getPassword().length() <= 16, "PASSWORD_TOO_LARGE", "Password too large")
                    .check(user -> Pattern.compile(PASSWORD_REGEX).matcher(user.getPassword()).matches(), "INVALID_PASSWORD_PATTERN", "Invalid password");

            dao.createTemporaryPassword(nonBusinessIdDatatype, nonBusiness.getUser());
        }

        dao.updateStatus(nonBusinessIdDatatype, "NB_USER_OK");
        dao.auditStatusChange(nonBusinessIdDatatype, Status.of("NB_USER_OK").setProcess(BUSINESS_DATA_PROCESS));

    }

    @Override
    public void patchNonBusinessEconomicData(final String nonBusinessId, final EconomicData economicData) throws ServiceException {
        final NonBusinessIdDatatype nonBusinessIdDatatype = getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId);
        dao.updateBusinessEconomicData(nonBusinessIdDatatype, economicData);
        dao.updateStatus(nonBusinessIdDatatype, "NB_ECO_OK");
        dao.auditStatusChange(nonBusinessIdDatatype, Status.of("NB_ECO_OK").setProcess(ECONOMIC_DATA_PROCESS));
    }

    @Override
    public void updateTerms(final String nonBusinessId, final String termId) throws ServiceException {
        final NonBusinessIdDatatype nonBusinessIdDatatype = getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId);
        final int version = dao.getTermVersion(termId);
        dao.updateStatus(nonBusinessIdDatatype, "NB_TYC_OK");
        dao.auditStatusChange(nonBusinessIdDatatype, Status.of("NB_TYC_OK").setProcess(ACCEPT_TERMS_PROCESS).setMessage(String.format("Aceptacion del documento: %s, version: %s", termId, version)));
    }

    private BusinessInformation getBusinessInformation(final NonBusinessIdDatatype nonBusinessIdDatatype) throws ServiceException {
        try {
            return businessInformationService.getBusinessInformation(nonBusinessIdDatatype.getBusinessDocument());
        } catch (DGIException e) {
            dao.auditStatusChange(nonBusinessIdDatatype, Status.of("DGICNX_ERR").setMessage("Error consultando DGI para el rut" + e.getRut()));
            throw new ServiceException(this.getClass().getCanonicalName(), ERROR_GET_BUSINESS_INFORMATION, e);
        }
    }

    private LocalDate tryParseDate(final String dateString, final String format) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String getNonBusinessIdFromnonBusinessIdDatatype(final NonBusinessIdDatatype nonBusinessIdDatatype) throws ServiceException {
        try {
            return nonBusinessIdManagement.getNonBusinessIdFromNonBusinessIdDatatype(nonBusinessIdDatatype);
        } catch (CaasException e) {
            throw new ServiceException(this.getClass().getName(), "Encryption error", e);
        }
    }

    private NonBusinessIdDatatype getNonBusinessIdDatatypeFromNonBusinessId(final String nonBusinessId) throws ServiceException {
        NonBusinessIdDatatype nonBusinessIdDatatype;
        try {
            nonBusinessIdDatatype = nonBusinessIdManagement.getNonBusinessIdDatatypeFromNonBusinessId(nonBusinessId);
        } catch (CaasException e) {
            throw new ServiceException(this.getClass().getName(), "Decryption error", e);
        }
        return nonBusinessIdDatatype;
    }

}