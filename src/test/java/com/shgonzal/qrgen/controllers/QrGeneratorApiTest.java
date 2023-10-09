package com.shgonzal.qrgen.controllers;

import com.shgonzal.qrgen.services.QRGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class QrGeneratorApiTest {

    private MockMvc mockMvc;
    @Mock
    private QRGeneratorService qrGeneratorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new QRGeneratorApi()).build();
    }

    @Test
    public void testGenerateQrCode() throws Exception {
        byte[] qrCodeBytes = new byte[]{1, 2, 3};
        when(qrGeneratorService.generateQrCode("Texto de prueba")).thenReturn(qrCodeBytes);

        mockMvc.perform(get("/generateQR")
                        .param("text", "Texto de prueba")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(qrCodeBytes));
    }

    @Test
    public void testGenerateQrCodeV2() throws Exception {
        byte[] qrCodeBytes = new byte[]{4, 5, 6};
        when(qrGeneratorService.generateQrCodeV2()).thenReturn(qrCodeBytes);

        mockMvc.perform(get("/generateQRV2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(qrCodeBytes));
    }
}
