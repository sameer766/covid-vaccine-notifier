package com.sameer.coviddatafetcher.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

@Component
@Slf4j
public class GeneratePdf {

    private static String INPUT_FILE;
    private static String USER_RESPONSE_FILE;
    private static final int X_COORDINATE = 50;

    public static void generatePdf(String username, String pincode, int age, String vaccineName) throws DocumentException, IOException {
        username = camelCaseUserName(username);
        generateDoc(username, pincode, vaccineName);
        if (!generateSignatureAndDeleteFile(username, pincode, age)) {
            log.error("Error while deleting file");
        }
    }

    private static void generateDoc(String username, String pincode, String vaccineName) throws IOException, DocumentException {
        Document document = new Document();
        INPUT_FILE = "/Users/sameer/Desktop/pdfs/input_" + username + "_.pdf";
        PdfWriter.getInstance(document, new FileOutputStream(INPUT_FILE));
        document.open();
        Paragraph paragraph = new Paragraph();
        paragraph.add("Your Report!");
        document.add(paragraph);


        document.add(new Paragraph("              "));

        PdfPTable table = new PdfPTable(3);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        PdfPCell cell1 = new PdfPCell(new Paragraph("Username"));
        PdfPCell cell2 = new PdfPCell(new Paragraph("Pincode"));
        PdfPCell cell3 = new PdfPCell(new Paragraph("Vaccine Name"));

        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.setHeaderRows(1);

        table.addCell(String.valueOf(username));
        table.addCell(String.valueOf(pincode));
        table.addCell(String.valueOf(vaccineName));

        String imageUrl = "/Users/sameer/Documents/covid-vaccine-notifier/covid-data-fetcher/src/main/java/com/sameer/coviddatafetcher/pdf/signature.jpg";
        Image image = Image.getInstance(imageUrl);
        image.setAbsolutePosition(X_COORDINATE, 650);
        image.scaleToFit(100, 100);
        document.add(image);
        document.add(table);
        document.close();
    }

    private static String camelCaseUserName(String username) {
        String[] split = username.split("\\s+");
        StringBuilder stringBuilder=new StringBuilder();
        Arrays.asList(split).forEach(word->{
            String firstChar = String.valueOf(word.charAt(0)).toUpperCase(Locale.ROOT);
            stringBuilder.append(firstChar + word.substring(1));
            stringBuilder.append("_");
        });
        return stringBuilder.substring(0,stringBuilder.length()-1).toString();
    }

    private static boolean generateSignatureAndDeleteFile(String username, String pincode, int age) throws IOException, DocumentException {
        PdfReader pdfReader = new PdfReader(INPUT_FILE);
        USER_RESPONSE_FILE = "/Users/sameer/Desktop/pdfs/" + username + "_" + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now()) + ".pdf";
        FileOutputStream fileOutputStream = new FileOutputStream(USER_RESPONSE_FILE);
        PdfStamper stamper = new PdfStamper(pdfReader, fileOutputStream);
        BaseFont bf = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED);


        PdfContentByte cb = stamper.getOverContent(1);
        cb.setColorFill(BaseColor.BLACK);
        cb.setFontAndSize(bf, 24);

        int angle = 0;
//        float height = pdfReader.getPageSizeWithRotation(1).getHeight() / 2;
//        float width = pdfReader.getPageSizeWithRotation(1).getWidth() / 2;
        cb.beginText();
        cb.showTextAligned(Element.ALIGN_LEFT, getTodaysDate(), X_COORDINATE, 625, angle);
        cb.endText();


        stamper.close();
        pdfReader.close();
        fileOutputStream.close();
        com.sameer.scheduler.utils.FileUtils.passwordProtectPdfFile(new File(USER_RESPONSE_FILE), String.valueOf(age).concat(pincode));
        return deleteFile();
    }

    private static boolean deleteFile() {
        return new File(INPUT_FILE).delete();
    }

    private static String getTodaysDate() {
        String pattern = "MM-dd-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        return date;
    }

}

