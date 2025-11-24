package uy.com.bbva.services.nonbusinesses.common;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.nonbusinessescommons.idmanagement.idmanagement.NonBusinessIdManagement;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.nonbusinesses.dao.DAO;
import uy.com.bbva.services.nonbusinesses.service.external.BusinessInformationService;
import uy.com.bbva.services.nonbusinesses.service.external.RisksService;
import uy.com.bbva.services.nonbusinesses.service.utils.AddressUtils;
import uy.com.bbva.services.nonbusinesses.service.utils.validator.NameValidator;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public abstract class ServiceTest{

    @Mock
    public DAO dao;

    @Mock
    public BusinessInformationService businessInformationService;

    @Mock
    public NonBusinessIdManagement nonBusinessIdManagement;

    @Mock
    public AddressUtils addressUtil;

    @Mock
    public LogUtils logUtils;

    @Mock
    public NameValidator nameValidator;

    @Mock
    public RisksService risksService;

    /**
     * Verifica que se lanzó correctamente una ServiceException con los detalles esperados usando logUtils.
     */
    protected void verifyServiceException(
            ServiceException exception, String msg, String intMsg) {

        assertEquals(intMsg, exception.getInternalMessage());
        assertEquals(msg, exception.getMessage());
        assertNotNull(exception.getCause());

        verify(logUtils).logError(
                eq("getDaoClassName()"),
                eq(exception.getMessage()),
                eq(exception.getInternalMessage())
        );
    }

    protected void verifyServiceException(
            SQLException sqlException, ServiceException exception, String msg, String intMsg) {

        assertEquals(intMsg, exception.getInternalMessage());
        assertEquals(msg, exception.getMessage());
        assertNotNull(exception.getCause());

        verify(logUtils).logError(
                eq("getDaoClassName()"),
                eq(exception.getMessage()),
                eq(exception.getInternalMessage()),
                eq(sqlException)
        );
    }

}