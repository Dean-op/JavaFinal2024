//PdfExporter类

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

public class PdfExporter {
    public void exportToPdf(String htmlContent, File file) {
        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file.getPath()));
            document.open();
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, new java.io.StringReader(htmlContent));
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
