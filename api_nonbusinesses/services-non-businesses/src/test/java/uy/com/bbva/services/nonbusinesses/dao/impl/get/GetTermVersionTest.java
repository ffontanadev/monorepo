package uy.com.bbva.services.nonbusinesses.dao.impl.get;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;
import uy.com.bbva.mongo.commons.ManagerMongoDBAccess;
import uy.com.bbva.mongo.commons.MongoTemplateBBVA;
import uy.com.bbva.services.nonbusinesses.dao.impl.DAOImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite de pruebas de DAOImpl.getTermVersion():")
class GetTermVersionTest {

    @Mock
    private ManagerMongoDBAccess managerMongoDBAccess;

    @Mock
    private MongoTemplateBBVA mongoTemplateBBVA;

    @InjectMocks
    private DAOImpl dao;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dao, "managerMongoDBAccess", managerMongoDBAccess);
    }

    @Test
    @DisplayName("Debe retornar version cuando el documento existe")
    void getTermVersion_shouldReturnVersion_whenDocumentExists() throws Exception {
        String termId = "TERM001";
        Document document = new Document();
        document.put("version", 5);

        when(managerMongoDBAccess.getMongoTemplateBBVA("documents")).thenReturn(mongoTemplateBBVA);
        when(mongoTemplateBBVA.findOne(any(Query.class), eq(Document.class), eq("documents")))
                .thenReturn(document);

        int result = dao.getTermVersion(termId);

        assertEquals(5, result);
    }

    @Test
    @DisplayName("Debe retornar 0 cuando document no existe")
    void getTermVersion_shouldReturnZero_whenDocumentNotExists() throws Exception {
        String termId = "TERM001";

        when(managerMongoDBAccess.getMongoTemplateBBVA("documents")).thenReturn(mongoTemplateBBVA);
        when(mongoTemplateBBVA.findOne(any(Query.class), eq(Document.class), eq("documents")))
                .thenReturn(null);

        int result = dao.getTermVersion(termId);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("Debe retornar 0 cuando falte el campo version")
    void getTermVersion_shouldReturnZero_whenVersionFieldMissing() throws Exception {
        String termId = "TERM001";
        Document document = new Document();

        when(managerMongoDBAccess.getMongoTemplateBBVA("documents")).thenReturn(mongoTemplateBBVA);
        when(mongoTemplateBBVA.findOne(any(Query.class), eq(Document.class), eq("documents")))
                .thenReturn(document);

        int result = dao.getTermVersion(termId);

        assertEquals(0, result);
    }
}