package com.aiinterviewcoach;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.mock.web.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.aiinterviewcoach.security.AiQuotaFilter;
import com.aiinterviewcoach.modules.questionbank.service.impl.LocalFileStorageServiceImpl;
import com.aiinterviewcoach.modules.questionbank.exception.FileStorageException;

class SecurityBoundaryTests {
    @TempDir Path directory;
    @AfterEach void clearAuthentication() { SecurityContextHolder.clearContext(); }

    @Test void aiQuotaAppliesAcrossRequestsButSeparatesAccounts() throws Exception {
        var filter = new AiQuotaFilter(1);
        var request = new MockHttpServletRequest("POST", "/api/interviews/start");
        request.setServletPath("/api/interviews/start");
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("first@example.com", null, java.util.List.of()));
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        var limited = new MockHttpServletResponse();
        filter.doFilter(request, limited, new MockFilterChain());
        assertEquals(429, limited.getStatus());
        assertNotNull(limited.getHeader("Retry-After"));
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("second@example.com", null, java.util.List.of()));
        var allowed = new MockHttpServletResponse();
        filter.doFilter(request, allowed, new MockFilterChain());
        assertEquals(200, allowed.getStatus());
    }
    @Test void fileTypesAndTraversalAreRejected() {
        var storage = new LocalFileStorageServiceImpl(directory.toString());
        assertThrows(FileStorageException.class, () -> storage.store(
            new MockMultipartFile("file", "resume.pdf", "application/pdf", "fake content".getBytes())));
        assertThrows(FileStorageException.class, () -> storage.store(
            new MockMultipartFile("file", "resume.exe", "application/pdf", "%PDF-1.7".getBytes())));
        assertThrows(FileStorageException.class, () -> storage.read("../outside.pdf"));
    }
    @Test void validFileIsStoredAndRead() {
        var storage = new LocalFileStorageServiceImpl(directory.toString());
        byte[] content = "%PDF-1.7\n".getBytes();
        var stored = storage.store(new MockMultipartFile("file", "resume.pdf", "application/pdf", content));
        assertArrayEquals(content, storage.read(stored.storageKey()));
    }
}
