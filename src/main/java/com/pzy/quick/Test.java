package com.pzy.quick;

import com.itextpdf.text.DocumentException;

import java.io.IOException;

public class Test {
    private static final String INPUT_PDF = System.getProperty("user.dir") + "/template/TEST.pdf";
    private static final String OUTPUT_PDF = System.getProperty("user.dir") + "/template/TEST_SEAL.pdf";
    private static final String SIGNATURE_IMAGE_PATH = System.getProperty("user.dir") + "/template/signature.png";
    private static final Integer MAX_SIGNATURE_WIDTH = 100; // 设置签名的最大宽度
    private static final Integer MAX_SIGNATURE_HEIGHT = 100; // 设置签名的最大高度

    public static void main(String[] args) throws IOException, DocumentException {
        ElectronicSealUtil.pdfSignature(INPUT_PDF, OUTPUT_PDF, SIGNATURE_IMAGE_PATH);
    }
}
