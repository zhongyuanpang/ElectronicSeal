package com.lwf.quick;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ElectronicSealUtil {

    public static void pdfSignature(String inputPdf,
                                    String outputPdf,
                                    String imagePath) throws IOException, DocumentException {
        pdfSignature(inputPdf, outputPdf, imagePath, 100, 100);
    }

    public static void pdfSignature(String inputPdf,
                                    String outputPdf,
                                    String imagePath,
                                    float maxSignatureWidth,
                                    float maxSignatureHeight) throws IOException, DocumentException {

        PdfReader reader = new PdfReader(inputPdf);

        // 获取pdf页数
        int numberOfPages = reader.getNumberOfPages();

        com.itextpdf.text.Rectangle pageSize = reader.getPageSizeWithRotation(1);
        Document document = new Document(pageSize);
        PdfWriter writer = PdfWriter.getInstance(document, Files.newOutputStream(Paths.get(outputPdf)));
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
                com.itextpdf.text.Image signaturePart = com.itextpdf.text.Image.getInstance(baos.toByteArray());

                // 缩放图片以适应最大尺寸
                signaturePart.scaleToFit(maxSignatureWidth, maxSignatureHeight);

                // 设置签名部分的位置（右下角）
                float pageWidth = reader.getPageSizeWithRotation(i).getWidth();
                signaturePart.setAbsolutePosition(pageWidth - signaturePart.getScaledWidth() - 20, 20);

                canvas.addImage(signaturePart);
                baos.reset();
            }
        } finally {
            document.close();
            writer.close();
            reader.close();
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
