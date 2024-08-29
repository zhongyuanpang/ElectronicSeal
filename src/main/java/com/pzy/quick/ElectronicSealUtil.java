package com.pzy.quick;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import jdk.nashorn.internal.runtime.logging.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Logger
public class ElectronicSealUtil {
    private static final Integer MAX_SIGNATURE_WIDTH = 100; // 设置签名的最大宽度
    private static final Integer MAX_SIGNATURE_HEIGHT = 100; // 设置签名的最大高度

    public static void pdfSignature(String inputPath,
                                    String outputPath,
                                    String imagePath) throws IOException, DocumentException {
        pdfSignature(inputPath, outputPath, imagePath, MAX_SIGNATURE_WIDTH, MAX_SIGNATURE_HEIGHT);
    }

    public static void pdfSignature(String inputPath,
                                    String outputPath,
                                    String imagePath,
                                    float maxSignatureWidth,
                                    float maxSignatureHeight) throws IOException, DocumentException {

        PdfReader reader = new PdfReader(inputPath);

        // 获取pdf页数
        int numberOfPages = reader.getNumberOfPages();

        com.itextpdf.text.Rectangle pageSize = reader.getPageSizeWithRotation(1);
        Document document = new Document(pageSize);
        PdfWriter writer = PdfWriter.getInstance(document, Files.newOutputStream(Paths.get(outputPath)));
        document.open();

        // 读取图片
        BufferedImage fullSignature = ImageIO.read(new File(imagePath));

        // 根据页数分割页签章宽度
        int partWidth = fullSignature.getWidth() / numberOfPages;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (int i = 1; i <= numberOfPages; i++) {
                PdfImportedPage page = writer.getImportedPage(reader, i);
                document.newPage();
                PdfContentByte canvas = writer.getDirectContent();
                canvas.addTemplate(page, 0, 0);

                // 将签名图片垂直切割（单页时使用完整图片）
                BufferedImage partImage = (numberOfPages > 1)
                        ? fullSignature.getSubimage((i - 1) * partWidth, 0, partWidth, fullSignature.getHeight())
                        : fullSignature;

                ImageIO.write(partImage, "png", baos);
                Image signaturePart = Image.getInstance(baos.toByteArray());

                // 缩放图片以适应最大尺寸
                signaturePart.scaleToFit(maxSignatureWidth, maxSignatureHeight);

                // 设置签名部分的位置（右下角）
                float pageWidth = reader.getPageSizeWithRotation(i).getWidth();
                signaturePart.setAbsolutePosition(pageWidth - signaturePart.getScaledWidth() - 20, 20);

                canvas.addImage(signaturePart);
                baos.reset();

                // 如果多页 最后一页多添加一个完整签章
                if (numberOfPages > 1 && numberOfPages == i) {

                    ImageIO.write(fullSignature, "png", baos);
                    Image signature = Image.getInstance(baos.toByteArray());

                    // 缩放图片以适应最大尺寸
                    signature.scaleToFit(maxSignatureWidth, maxSignatureHeight);

                    //获取缩放后的尺寸
                    float signatureWidth = signature.getScaledWidth();
                    signature.setAbsolutePosition(pageWidth - signatureWidth - 480, 20);

                    canvas.addImage(signature);
                    baos.reset();
                }
            }
        } finally {
            document.close();
            writer.close();
            reader.close();
        }
    }

    public static void wordOrExcelConvertPdfSignature(String inputPdf,
                                                      String outputPdf,
                                                      String signatureOutputPdf,
                                                      String imagePath) {
        wordOrExcelConvertPdfSignature(inputPdf, outputPdf, signatureOutputPdf, imagePath, MAX_SIGNATURE_WIDTH, MAX_SIGNATURE_HEIGHT);
    }

    public static void wordOrExcelConvertPdfSignature(String inputPath,
                                                      String outputPath,
                                                      String signatureOutputPath,
                                                      String imagePath,
                                                      float maxSignatureWidth,
                                                      float maxSignatureHeight) {

        // 获取文件类型
        String fileType = inputPath.substring(inputPath.lastIndexOf("."));

        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        try {
            InputStream inputStream = Files.newInputStream(inputFile.toPath());
            OutputStream outputStream = Files.newOutputStream(outputFile.toPath());
            IConverter converter = LocalConverter.builder().build();

            switch (fileType) {
                case ".docx":
                    converter.convert(inputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
                    break;
                case ".doc":
                    converter.convert(inputStream).as(DocumentType.DOC).to(outputStream).as(DocumentType.PDF).execute();
                    break;
                case ".xls":
                    converter.convert(inputStream).as(DocumentType.XLS).to(outputStream).as(DocumentType.PDF).execute();
                    break;
                case ".xlsx":
                    converter.convert(inputStream).as(DocumentType.XLSX).to(outputStream).as(DocumentType.PDF).execute();
                    break;
            }

            inputStream.close();
            outputStream.close();
            converter.shutDown();
        } catch (Exception e) {
            System.err.println("DOCX到PDF转换过程中出错 " + e.getMessage());
            e.printStackTrace();
        }

        // 调用pdf签章
        try {
            pdfSignature(outputPath, signatureOutputPath, imagePath, maxSignatureWidth, maxSignatureHeight);
        } catch (Exception e) {
            System.err.println("pdf文件签章失败");
        } finally {
            // 删除转换后的pdf文件
            File file = new File(outputPath);
            if (file.exists()) {
                file.delete();
            }
        }

    }


    /**
     * 将图片分割为扇形
     *
     * @param source
     * @param totalSectors
     * @param sectorNumber
     * @return
     */
    private static BufferedImage createSectorImage(BufferedImage source, int totalSectors, int sectorNumber) {
        int size = Math.max(source.getWidth(), source.getHeight());
        BufferedImage result = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = result.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 计算扇形的起始和结束角度（从左边开始，顺时针方向）
        double startAngle = 180 - ((sectorNumber - 1) * 360.0 / totalSectors);
        double arcAngle = 360.0 / totalSectors;

        // 创建扇形形状
        Arc2D.Double sector = new Arc2D.Double(
                0, 0, size, size,
                startAngle, -arcAngle,
                Arc2D.PIE
        );

        g2d.setClip(sector);

        // 计算绘制原始图像的位置使其居中
        int x = (size - source.getWidth()) / 2;
        int y = (size - source.getHeight()) / 2;

        // 绘制原始图像
        g2d.drawImage(source, x, y, null);

        g2d.dispose();
        return result;
    }
}
