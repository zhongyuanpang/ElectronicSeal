package com.pzy.quick;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class WordTest {
    private static final String INPUT_WORD = System.getProperty("user.dir") + "/template/test.docx";
    private static final String OUTPUT_WORD = System.getProperty("user.dir") + "/template/testResult.docx";
    private static final String SIGNATURE_IMAGE_PATH = System.getProperty("user.dir") + "/template/signature.png";
    private static final Integer MAX_SIGNATURE_WIDTH = 100; // 设置签名的最大宽度
    private static final Integer MAX_SIGNATURE_HEIGHT = 100; // 设置签名的最大高度

    public static void main(String[] args) {
//        try {
//            // 加载Word文档
//            Document doc = new Document(INPUT_WORD);
//
//            // 获取页数
//            int pageCount = doc.getPageCount();
//
//            // 加载签章图片
//            BufferedImage signatureImage = ImageIO.read(new File(SIGNATURE_IMAGE_PATH));
//            int imageHeight = signatureImage.getHeight();
//            int imageWidth = signatureImage.getWidth();
//
//            // 计算每页应分割的高度
//            int sliceHeight = pageCount > 1 ? imageHeight / pageCount : imageHeight;
//
//            // 使用DocumentBuilder逐页插入签章部分
//            DocumentBuilder builder = new DocumentBuilder(doc);
//            LayoutCollector layoutCollector = new LayoutCollector(doc);
//
//            // 对每页插入签章图片
//            for (int i = 0; i < pageCount; i++) {
//                BufferedImage slice;
//                if (pageCount > 1) {
//                    // 多页文档：垂直分割签章图片
//                    int y = i * sliceHeight;
//                    slice = signatureImage.getSubimage(0, y, imageWidth, sliceHeight);
//                } else {
//                    // 单页文档：使用完整签章图片
//                    slice = signatureImage;
//                }
//
//                // 将BufferedImage转为ByteArrayOutputStream
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                ImageIO.write(slice, "png", baos);
//                InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
//
//                // 查找当前页的节点，并将光标移动到该节点
//                for (Paragraph para : (Iterable<Paragraph>) doc.getChildNodes(NodeType.PARAGRAPH, true)) {
//                    if (layoutCollector.getStartPageIndex(para) == i + 1) {
//                        builder.moveTo(para);
//                        break;
//                    }
//                }
//
//                // 设置图片的大小和位置
//                double targetWidth = 200; // 设置目标宽度，单位：点（1点 = 1/72英寸）
//                double targetHeight = sliceHeight * (targetWidth / imageWidth); // 保持纵横比
//
//                // 插入图片
//                builder.insertImage(inputStream, targetWidth, targetHeight);
//
//            }
//
//            // 保存修改后的文档
//            doc.save(OUTPUT_WORD);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
