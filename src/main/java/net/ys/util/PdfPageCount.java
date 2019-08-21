package net.ys.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 获取pdf总页数
 * User: LiWenC
 * Date: 18-5-21
 */
public class PdfPageCount {
    public static void main(String[] args) throws Exception {
        System.out.println(count("e:/docker.pdf"));
    }

    public static int count(String src) throws IOException, DocumentException {
        PdfReader pdfReader = new PdfReader(new FileInputStream(src));
        return pdfReader.getNumberOfPages();
    }
}
