package net.ys.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.FileOutputStream;

/**
 * 切分、合并pdf
 * User: LiWenC
 * Date: 18-5-21
 */
public class SplitAndMergePdf {

    /**
     * 切分pdf
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @param ranges     复制规则     "1-7"表示复制1到7页、"8-"表示复制从第八页之后到文档末尾
     */
    public static void splitPdf(String sourceFile, String targetFile, String ranges) throws Exception {
        PdfReader pdfReader = new PdfReader(sourceFile);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(targetFile));
        pdfReader.selectPages(ranges);
        pdfStamper.close();
    }

    /**
     * 多个PDF合并功能
     *
     * @param files    多个PDF的路径
     * @param savePath 生成的新PDF绝对路径
     */
    public static void mergePdfFiles(String[] files, String savePath) {
        if (files.length > 0) {
            try {
                Document document = new Document(new PdfReader(files[0]).getPageSize(1));
                PdfCopy copy = new PdfCopy(document, new FileOutputStream(savePath));
                document.open();
                for (String file : files) {
                    PdfReader reader = new PdfReader(file);
                    int n = reader.getNumberOfPages();
                    for (int j = 1; j <= n; j++) {
                        document.newPage();
                        PdfImportedPage page = copy.getImportedPage(reader, j);
                        copy.addPage(page);
                    }
                }
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String sourceFile = "E:/sign/aaa.pdf";
        String targetFile1 = "E:/sign/aaa_out1.pdf";
        String targetFile2 = "E:/sign/aaa_out2.pdf";
        splitPdf(sourceFile, targetFile1, "1-10");
        splitPdf(sourceFile, targetFile2, "11-");

        targetFile1 = "E:/sign/test_enc1.pdf";
        targetFile2 = "E:/sign/test_enc1.pdf";
        String targetFile = "E:/sign/all.pdf";

        String[] files = {targetFile1, targetFile2};
        mergePdfFiles(files, targetFile);
    }
}
