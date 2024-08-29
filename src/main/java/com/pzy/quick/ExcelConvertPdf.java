package com.pzy.quick;

public class ExcelConvertPdf {
    private static final String INPUT_PATH = System.getProperty("user.dir") + "/template/TEST.xlsx";
    private static final String OUTPUT_PATH = System.getProperty("user.dir") + "/template/TEST-DEMO.pdf";
    private static final String SIGNATURE_OUTPUT_PATH = System.getProperty("user.dir") + "/template/TEST-DEMO-SEAL.pdf";
    private static final String SIGNATURE_IMAGE_PATH = System.getProperty("user.dir") + "/template/signature.png";
    private static final Integer MAX_SIGNATURE_WIDTH = 100; // 设置签名的最大宽度
    private static final Integer MAX_SIGNATURE_HEIGHT = 100; // 设置签名的最大高度

    public static void main(String[] args) {
        ElectronicSealUtil.wordOrExcelConvertPdfSignature(INPUT_PATH, OUTPUT_PATH, SIGNATURE_OUTPUT_PATH, SIGNATURE_IMAGE_PATH);
    }
}
