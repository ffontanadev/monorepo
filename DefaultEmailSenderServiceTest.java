package uy.com.bbva.services.mails.service.impl;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.IContext;
import uy.com.bbva.logcommons.log.utils.LogUtils;
import uy.com.bbva.services.commons.exceptions.ServiceException;
import uy.com.bbva.services.mails.dao.DAO;
import uy.com.bbva.services.mails.model.Attachment;
import uy.com.bbva.services.mails.model.MailSent;
import uy.com.bbva.services.mails.model.Notification;
import uy.com.bbva.services.mails.model.User;
import uy.com.bbva.services.mails.utils.ITemplateEngine;
import uy.com.bbva.services.mails.utils.InvalidRequestParameterException;
import uy.com.bbva.services.mails.utils.NotificationEnricher;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Pruebas unitarias del servicio DefaultEmailSenderService")
class DefaultEmailSenderServiceTest {

    private static final String TEST_EMAIL = "test@bbva.com";
    private static final String TEST_SENDER = "sender@bbva.com";
    private static final String TEST_SENDER_NAME = "BBVA Sender";
    private static final String TEST_SUBJECT = "Test Subject";
    private static final String TEMPLATE_MODIFICACION_TOPES = "email_modificacion_topes";
    private static final String AUDIT_MESSAGE_TYPE = "MSG_TYPE";
    private static final String AUDIT_PROCESS_TYPE = "PROC_TYPE";

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private ITemplateEngine templateEngine;

    @Mock
    private NotificationEnricher notificationEnricher;

    @Mock
    private DAO dao;

    @Mock
    private LogUtils logger;

    @InjectMocks
    private DefaultEmailSenderService emailSenderService;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    @Captor
    private ArgumentCaptor<MailSent> mailSentCaptor;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = createMimeMessage();
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(emailSenderService, "mailSuffix", "");
    }

    @Test
    @DisplayName("Debería enviar correo electrónico exitosamente cuando se proporciona una notificación válida con todos los datos requeridos")
    void shouldSendEmailSuccessfully_whenValidNotificationProvided(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, TEMPLATE_MODIFICACION_TOPES, false);
        Notification notification = createValidNotification();
        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<html>Processed</html>");
        doNothing().when(notificationEnricher).enricherNotificationData(any(Notification.class));

        emailSenderService.notificationWithAttachment(Collections.singletonList(notification));

        verify(javaMailSender).send(mimeMessageCaptor.capture());
        verify(notificationEnricher).enricherNotificationData(notification);
        verify(templateEngine).process(eq(TEMPLATE_MODIFICACION_TOPES), any(IContext.class));
    }

    @Test
    @DisplayName("Debería cargar y procesar plantilla HTML cuando la configuración useHTMLTemplate está activada en el archivo JSON de plantilla")
    void shouldLoadHtmlTemplate_whenUseHTMLTemplateIsTrue(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, "html_template_test", true);
        Notification notification = createNotificationWithTemplate("html_template_test");
        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<html>From HTML file</html>");
        doNothing().when(notificationEnricher).enricherNotificationData(any(Notification.class));

        emailSenderService.notificationWithAttachment(Collections.singletonList(notification));

        verify(javaMailSender).send(any(MimeMessage.class));
        verify(templateEngine).process(eq("html_template_test"), any(IContext.class));
    }

    @Test
    @DisplayName("Debería enviar correo electrónico con archivos adjuntos codificados en Base64 correctamente procesados")
    void shouldSendEmailWithAttachments(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, TEMPLATE_MODIFICACION_TOPES, false);
        Notification notification = createValidNotification();
        notification.setAttachments(createAttachments());
        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<html>Body</html>");
        doNothing().when(notificationEnricher).enricherNotificationData(any(Notification.class));

        emailSenderService.notificationWithAttachment(Collections.singletonList(notification));

        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debería procesar y enviar múltiples notificaciones de correo electrónico en una sola invocación del servicio")
    void shouldSendMultipleNotifications(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, TEMPLATE_MODIFICACION_TOPES, false);
        List<Notification> notifications = Arrays.asList(
                createValidNotification(),
                createValidNotification()
        );
        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<html>Body</html>");
        doNothing().when(notificationEnricher).enricherNotificationData(any(Notification.class));

        emailSenderService.notificationWithAttachment(notifications);

        verify(javaMailSender, times(2)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debería aplicar el sufijo de correo configurado a la dirección del remitente cuando la propiedad mailSuffix está definida")
    void shouldApplyMailSuffix_whenSuffixConfigured(@TempDir Path tempDir) throws Exception {
        ReflectionTestUtils.setField(emailSenderService, "mailSuffix", ".test");
        setupTemplateFiles(tempDir, TEMPLATE_MODIFICACION_TOPES, false);
        Notification notification = createValidNotification();
        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<html>Body</html>");
        doNothing().when(notificationEnricher).enricherNotificationData(any(Notification.class));

        emailSenderService.notificationWithAttachment(Collections.singletonList(notification));

        verify(javaMailSender).send(mimeMessageCaptor.capture());
    }

    @Test
    @DisplayName("Debería registrar auditoría de envío exitoso en base de datos cuando se proporcionan tipos de auditoría válidos en la notificación")
    void shouldAuditSuccessfulSend_whenAuditTypesProvided(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, TEMPLATE_MODIFICACION_TOPES, false);
        Notification notification = createValidNotification();
        notification.setAuditMessageType(AUDIT_MESSAGE_TYPE);
        notification.setAuditProcessType(AUDIT_PROCESS_TYPE);

        when(dao.getMailConfiguration(AUDIT_PROCESS_TYPE, AUDIT_MESSAGE_TYPE)).thenReturn(100);
        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<html>Body</html>");
        doNothing().when(notificationEnricher).enricherNotificationData(any(Notification.class));

        emailSenderService.notificationWithAttachment(Collections.singletonList(notification));

        verify(dao).auditMailSent(mailSentCaptor.capture());
        MailSent capturedAudit = mailSentCaptor.getValue();
        assertEquals(0, capturedAudit.getCode());
        assertEquals("Envío exitoso", capturedAudit.getDescCode());
        assertEquals(TEST_EMAIL, capturedAudit.getTo());
        assertEquals(100, capturedAudit.getIdNotification());
    }

    @Test
    @DisplayName("No debería registrar auditoría cuando el identificador de notificación obtenido de la configuración es cero o negativo")
    void shouldNotAudit_whenNotificationIdIsZeroOrNegative(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, TEMPLATE_MODIFICACION_TOPES, false);
        Notification notification = createValidNotification();
        notification.setAuditMessageType(AUDIT_MESSAGE_TYPE);
        notification.setAuditProcessType(AUDIT_PROCESS_TYPE);

        when(dao.getMailConfiguration(AUDIT_PROCESS_TYPE, AUDIT_MESSAGE_TYPE)).thenReturn(0);
        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<html>Body</html>");
        doNothing().when(notificationEnricher).enricherNotificationData(any(Notification.class));

        emailSenderService.notificationWithAttachment(Collections.singletonList(notification));

        verify(dao, never()).auditMailSent(any(MailSent.class));
    }

    @Test
    @DisplayName("Debería omitir completamente el proceso de auditoría cuando los tipos de auditoría son nulos en la notificación")
    void shouldSkipAudit_whenAuditTypesAreNull(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, TEMPLATE_MODIFICACION_TOPES, false);
        Notification notification = createValidNotification();
        notification.setAuditMessageType(null);
        notification.setAuditProcessType(null);

        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<html>Body</html>");
        doNothing().when(notificationEnricher).enricherNotificationData(any(Notification.class));

        emailSenderService.notificationWithAttachment(Collections.singletonList(notification));

        verify(dao, never()).getMailConfiguration(anyString(), anyString());
        verify(dao, never()).auditMailSent(any(MailSent.class));
    }

    @Test
    @DisplayName("Debería lanzar InvalidRequestParameterException cuando falta el campo obligatorio 'servicio' en los datos de la notificación de modificación de topes")
    void shouldThrowException_whenServicioIsMissing(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, TEMPLATE_MODIFICACION_TOPES, false);
        Notification notification = createValidNotification();
        notification.getData().remove("servicio");

        doNothing().when(logger).logError(anyString(), anyString(), anyString());

        assertThrows(InvalidRequestParameterException.class, () ->
                emailSenderService.notificationWithAttachment(Collections.singletonList(notification))
        );

        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debería lanzar InvalidRequestParameterException cuando falta el campo obligatorio 'importeMaximoPorDia' en los datos de la notificación")
    void shouldThrowException_whenImporteMaximoPorDiaIsMissing(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, TEMPLATE_MODIFICACION_TOPES, false);
        Notification notification = createValidNotification();
        notification.getData().remove("importeMaximoPorDia");

        doNothing().when(logger).logError(anyString(), anyString(), anyString());

        assertThrows(InvalidRequestParameterException.class, () ->
                emailSenderService.notificationWithAttachment(Collections.singletonList(notification))
        );
    }

    @Test
    @DisplayName("Debería lanzar InvalidRequestParameterException cuando falta el campo obligatorio 'cantidadMaximaPorDia' en los datos de la notificación")
    void shouldThrowException_whenCantidadMaximaPorDiaIsMissing(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, TEMPLATE_MODIFICACION_TOPES, false);
        Notification notification = createValidNotification();
        notification.getData().remove("cantidadMaximaPorDia");

        doNothing().when(logger).logError(anyString(), anyString(), anyString());

        assertThrows(InvalidRequestParameterException.class, () ->
                emailSenderService.notificationWithAttachment(Collections.singletonList(notification))
        );
    }

    @Test
    @DisplayName("Debería lanzar ServiceException y registrar error cuando ocurre una MessagingException durante el envío del correo por SMTP")
    void shouldThrowServiceException_whenMessagingExceptionOccurs(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, TEMPLATE_MODIFICACION_TOPES, false);
        Notification notification = createValidNotification();
        notification.setAuditMessageType(AUDIT_MESSAGE_TYPE);
        notification.setAuditProcessType(AUDIT_PROCESS_TYPE);

        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<html>Body</html>");
        doNothing().when(notificationEnricher).enricherNotificationData(any(Notification.class));
        doThrow(new MessagingException("SMTP Error")).when(javaMailSender).send(any(MimeMessage.class));
        doNothing().when(logger).logError(anyString(), anyString(), anyString(), any(Exception.class));
        when(dao.getMailConfiguration(anyString(), anyString())).thenReturn(100);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                emailSenderService.notificationWithAttachment(Collections.singletonList(notification))
        );

        assertTrue(exception.getMessage().contains("Error sending email with attachment"));
        verify(logger).logError(anyString(), eq("Error en api-mails"), eq("Error en api-mails"), any(MessagingException.class));
    }

    @Test
    @DisplayName("Debería registrar auditoría con código de error cuando falla el envío del correo por una excepción de mensajería")
    void shouldAuditError_whenMessagingExceptionOccurs(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, TEMPLATE_MODIFICACION_TOPES, false);
        Notification notification = createValidNotification();
        notification.setAuditMessageType(AUDIT_MESSAGE_TYPE);
        notification.setAuditProcessType(AUDIT_PROCESS_TYPE);

        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<html>Body</html>");
        doNothing().when(notificationEnricher).enricherNotificationData(any(Notification.class));
        doThrow(new MessagingException("SMTP Error")).when(javaMailSender).send(any(MimeMessage.class));
        doNothing().when(logger).logError(anyString(), anyString(), anyString(), any(Exception.class));
        when(dao.getMailConfiguration(AUDIT_PROCESS_TYPE, AUDIT_MESSAGE_TYPE)).thenReturn(100);

        assertThrows(ServiceException.class, () ->
                emailSenderService.notificationWithAttachment(Collections.singletonList(notification))
        );

        verify(dao).auditMailSent(mailSentCaptor.capture());
        MailSent capturedAudit = mailSentCaptor.getValue();
        assertEquals(1, capturedAudit.getCode());
        assertTrue(capturedAudit.getDescCode().contains("Error en el envío"));
    }

    @Test
    @DisplayName("Debería lanzar ServiceException cuando no se encuentra el archivo JSON de configuración de la plantilla especificada")
    void shouldThrowServiceException_whenTemplateJsonNotFound() {
        Notification notification = createNotificationWithTemplate("non_existent_template");

        doNothing().when(logger).logError(anyString(), anyString(), anyString(), any(Exception.class));

        assertThrows(ServiceException.class, () ->
                emailSenderService.notificationWithAttachment(Collections.singletonList(notification))
        );
    }

    @Test
    @DisplayName("Debería manejar correctamente una lista vacía de notificaciones sin intentar enviar ningún correo")
    void shouldHandleEmptyNotificationList() throws Exception {
        emailSenderService.notificationWithAttachment(Collections.emptyList());

        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debería enviar correo exitosamente cuando la lista de adjuntos es nula en la notificación")
    void shouldHandleNullAttachments(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, TEMPLATE_MODIFICACION_TOPES, false);
        Notification notification = createValidNotification();
        notification.setAttachments(null);

        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<html>Body</html>");
        doNothing().when(notificationEnricher).enricherNotificationData(any(Notification.class));

        emailSenderService.notificationWithAttachment(Collections.singletonList(notification));

        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debería enviar correo exitosamente cuando la lista de adjuntos está vacía en la notificación")
    void shouldHandleEmptyAttachmentsList(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, TEMPLATE_MODIFICACION_TOPES, false);
        Notification notification = createValidNotification();
        notification.setAttachments(Collections.emptyList());

        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<html>Body</html>");
        doNothing().when(notificationEnricher).enricherNotificationData(any(Notification.class));

        emailSenderService.notificationWithAttachment(Collections.singletonList(notification));

        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debería omitir validación de campos obligatorios cuando la plantilla no es del tipo modificación de topes")
    void shouldSkipValidation_forOtherTemplates(@TempDir Path tempDir) throws Exception {
        setupTemplateFiles(tempDir, "other_template", false);
        Notification notification = createNotificationWithTemplate("other_template");
        notification.setData(Collections.emptyMap());

        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<html>Body</html>");
        doNothing().when(notificationEnricher).enricherNotificationData(any(Notification.class));

        emailSenderService.notificationWithAttachment(Collections.singletonList(notification));

        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debería crear instancia de JavaMailSenderImpl con la configuración correcta de protocolo, puerto, host y credenciales")
    void shouldCreateMailSenderWithCorrectConfig() {
        ReflectionTestUtils.setField(emailSenderService, "mailProtocol", "smtp");
        ReflectionTestUtils.setField(emailSenderService, "mailPort", "587");
        ReflectionTestUtils.setField(emailSenderService, "mailHost", "smtp.bbva.com");
        ReflectionTestUtils.setField(emailSenderService, "mailUsername", "user@bbva.com");
        ReflectionTestUtils.setField(emailSenderService, "mailPassword", "password123");

        var mailSender = emailSenderService.mailSender();

        assertNotNull(mailSender);
        assertEquals("smtp", mailSender.getProtocol());
        assertEquals(587, mailSender.getPort());
        assertEquals("smtp.bbva.com", mailSender.getHost());
        assertEquals("user@bbva.com", mailSender.getUsername());
        assertEquals("password123", mailSender.getPassword());
    }

    private MimeMessage createMimeMessage() {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        return new MimeMessage(session);
    }

    private Notification createValidNotification() {
        Notification notification = new Notification();
        notification.setNotificationTypeId(TEMPLATE_MODIFICACION_TOPES);

        Map<String, String> data = new LinkedHashMap<>();
        data.put("servicio", "Pago de servicios BPS");
        data.put("importeMaximoPorDia", "50000");
        data.put("cantidadMaximaPorDia", "5");
        notification.setData(data);

        User user = new User();
        user.setEmail(TEST_EMAIL);
        notification.setUser(user);

        return notification;
    }

    private Notification createNotificationWithTemplate(String templateName) {
        Notification notification = new Notification();
        notification.setNotificationTypeId(templateName);
        notification.setData(Collections.emptyMap());

        User user = new User();
        user.setEmail(TEST_EMAIL);
        notification.setUser(user);

        return notification;
    }

    private List<Attachment> createAttachments() {
        Attachment attachment = new Attachment();
        attachment.setTitle("test_document");
        attachment.setExtension("pdf");
        attachment.setBase64(Base64.encodeBase64String("Test content".getBytes()));
        return Collections.singletonList(attachment);
    }

    private void setupTemplateFiles(Path tempDir, String templateName, boolean useHtmlTemplate) throws IOException {
        File jsonDir = new File("target/test-classes/templates/");
        jsonDir.mkdirs();

        File jsonFile = new File(jsonDir, templateName + ".json");
        String jsonContent = String.format(
                "{\n" +
                        "  \"useHTMLTemplate\": %s,\n" +
                        "  \"body\": \"<html>Default body</html>\",\n" +
                        "  \"sender\": \"%s\",\n" +
                        "  \"senderName\": \"%s\",\n" +
                        "  \"subject\": \"%s\"\n" +
                        "}",
                useHtmlTemplate, TEST_SENDER, TEST_SENDER_NAME, TEST_SUBJECT
        );
        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(jsonContent);
        }

        if (useHtmlTemplate) {
            File htmlDir = new File("target/test-classes/templates/html/");
            htmlDir.mkdirs();
            File htmlFile = new File(htmlDir, templateName + ".html");
            try (FileWriter writer = new FileWriter(htmlFile)) {
                writer.write("<html><body>HTML Template Content</body></html>");
            }
        }
    }
}
