package net.ys.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UndoPdfSignature {

    private static Log log = LogFactory.getLog(UndoPdfSignature.class);

    public static boolean undoSign(String sourcePath, String desPath, String fieldName) throws IOException, DocumentException {
        if (!isNotEmpty(sourcePath, desPath, fieldName)) {
            return false;
        }

        PdfReader reader = null;
        FileOutputStream os = null;
        InputStream ip = null;
        PdfStamper stamper = null;
        try {
            reader = new PdfReader(sourcePath);
            os = new FileOutputStream(desPath);
            stamper = new PdfStamper(reader, os, '\000', true);
            AcroFields fields = reader.getAcroFields();
            ArrayList names = fields.getSignatureNames();
            if (names.size() <= 0) {
                close(reader, os, ip, stamper);
                return false;
            }

            if (fields.getField(fieldName) == null) {
                close(reader, os, ip, stamper);
                return false;
            }

            if (clearSignatureField(fieldName, stamper)) {
                close(reader, os, ip, stamper);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            close(reader, os, ip, stamper);
        }
        return false;
    }

    private static boolean clearSignatureField(String fieldName, PdfStamper stamper) {
        stamper.getAcroFields().clearSignatureField(fieldName);
        return stamper.getAcroFields().removeField(fieldName);
    }

    /**
     * 判断多个字符串是否为空
     *
     * @param strings
     * @return
     */
    public static boolean isNotEmpty(String... strings) {
        if (strings == null || strings.length == 0) {
            return false;
        }
        for (String str : strings) {
            if (str == null || "".equals(str.trim())) {
                return false;
            }
        }
        return true;
    }

    private static void close(PdfReader reader, FileOutputStream os, InputStream ip, PdfStamper stamper) throws DocumentException, IOException {
        if (stamper != null) {
            stamper.close();
        }

        if (reader != null) {
            reader.close();
        }
        if (os != null) {
            os.close();
        }

        if (ip != null) {
            ip.close();
        }
    }
}
