package com.shgonzal.qrgen.services;

import com.shgonzal.qrgen.services.impl.QRGeneratorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

public class QRGenertorServiceTest {

    private QRGeneratorServiceImpl qrGeneratorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        qrGeneratorService = new QRGeneratorServiceImpl();
    }

}
