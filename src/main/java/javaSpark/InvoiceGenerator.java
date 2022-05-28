package javaSpark;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InvoiceGenerator {
    static InvoiceBaseEntry generateInvoiceForAllCars(ArrayList<DBCar> db) throws Exception {
        InvoiceBaseEntry entry = new InvoiceBaseEntry();
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat vatHeader = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");

        long unix = d.getTime();
        entry.url = String.format("invoices/Invoice_All_Cars_%d.pdf", unix);
        entry.name = String.format("Faktura za wszystkie samochody --> %s", f.format(d));

        Document doc = new Document();
        StringBuilder path_builder = new StringBuilder();
        PdfWriter.getInstance(doc, new FileOutputStream(entry.url));
        doc.open();

        BaseFont f1 = BaseFont.createFont(BaseFont.HELVETICA_BOLD, "CP1257", BaseFont.EMBEDDED);
        BaseFont f2 = BaseFont.createFont(BaseFont.HELVETICA, "CP1257", BaseFont.EMBEDDED);
        Font font = new Font(f1, 32, Font.BOLD);
        Paragraph c = new Paragraph(String.format("FAKTURA: VAT/%s", vatHeader.format(d)), font);
        doc.add(c);

        Font font2 = new Font(f2, 24, Font.NORMAL);
        Font font3 = new Font(f2, 24, Font.NORMAL);
        font3.setColor(255, 0, 0);
        Paragraph nabywca = new Paragraph("Nabywca: sprzedawca: firma sprzedająca auta", font2);
        Paragraph sprzedawca = new Paragraph("Sprzedawca: nabywca: nabywca", font2);
        Paragraph info = new Paragraph("Faktura za wszystkie auta\n\n", font3);

        doc.add(nabywca);
        doc.add(sprzedawca);
        doc.add(info);

        PdfPTable t = new PdfPTable(4);
        t.addCell("lp");
        t.addCell("cena");
        t.addCell("vat");
        t.addCell("wartość");

        double sum = 0;
        for (DBCar car:
             db) {
            double value = car.price;
            value = value * (1 + (((double)car.vat) / 100.0));

            sum += value;

            t.addCell(car.id.toString());
            t.addCell(car.price.toString());
            t.addCell(String.format("%d%%", car.vat));
            t.addCell(String.format("%.02f", value));
        }

        doc.add(t);

        Paragraph finalPrice = new Paragraph(String.format("DO ZAPŁATY: %.02f PLN", sum), font);
        doc.add(finalPrice);

        doc.close();
        return entry;
    }

    static InvoiceBaseEntry generateInvoiceForCarsFromYear(ArrayList<DBCar> db, int year) throws Exception {
        InvoiceBaseEntry entry = new InvoiceBaseEntry();
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat vatHeader = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");

        long unix = d.getTime();
        entry.url = String.format("invoices/Invoice_Cars_From_Year_%d_%d.pdf", year, unix);
        entry.name = String.format("Faktura za samochody z roku %d --> %s", year, f.format(d));

        Document doc = new Document();
        StringBuilder path_builder = new StringBuilder();
        PdfWriter.getInstance(doc, new FileOutputStream(entry.url));
        doc.open();

        BaseFont f1 = BaseFont.createFont(BaseFont.HELVETICA_BOLD, "CP1257", BaseFont.EMBEDDED);
        BaseFont f2 = BaseFont.createFont(BaseFont.HELVETICA, "CP1257", BaseFont.EMBEDDED);
        Font font = new Font(f1, 32, Font.BOLD);
        Paragraph c = new Paragraph(String.format("FAKTURA: VAT/%s", vatHeader.format(d)), font);
        doc.add(c);

        Font font2 = new Font(f2, 24, Font.NORMAL);
        Font font3 = new Font(f2, 24, Font.NORMAL);
        font3.setColor(255, 0, 0);
        Paragraph nabywca = new Paragraph("Nabywca: sprzedawca: firma sprzedająca auta", font2);
        Paragraph sprzedawca = new Paragraph("Sprzedawca: nabywca: nabywca", font2);
        Paragraph info = new Paragraph(String.format("Faktura za auta z roku %d\n\n", year), font3);

        doc.add(nabywca);
        doc.add(sprzedawca);
        doc.add(info);

        PdfPTable t = new PdfPTable(4);
        t.addCell("lp");
        t.addCell("cena");
        t.addCell("vat");
        t.addCell("wartość");

        double sum = 0;
        for (DBCar car:
                db) {
            if (car.year != year) continue;
            double value = car.price;
            value = value * (1 + (((double)car.vat) / 100.0));

            sum += value;

            t.addCell(car.id.toString());
            t.addCell(car.price.toString());
            t.addCell(String.format("%d%%", car.vat));
            t.addCell(String.format("%.02f", value));
        }

        doc.add(t);

        Paragraph finalPrice = new Paragraph(String.format("DO ZAPŁATY: %.02f PLN", sum), font);
        doc.add(finalPrice);

        doc.close();
        return entry;
    }

    static InvoiceBaseEntry generateInvoiceForCarsFromPriceRange(ArrayList<DBCar> db, int min, int max) throws Exception {
        InvoiceBaseEntry entry = new InvoiceBaseEntry();
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat vatHeader = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");

        long unix = d.getTime();
        entry.url = String.format("invoices/Invoice_Cars_Between_%d_and_%d_%d.pdf", min, max, unix);
        entry.name = String.format("Faktura za samochody z cenami między %d a %d --> %s", min, max, f.format(d));

        Document doc = new Document();
        StringBuilder path_builder = new StringBuilder();
        PdfWriter.getInstance(doc, new FileOutputStream(entry.url));
        doc.open();

        BaseFont f1 = BaseFont.createFont(BaseFont.HELVETICA_BOLD, "CP1257", BaseFont.EMBEDDED);
        BaseFont f2 = BaseFont.createFont(BaseFont.HELVETICA, "CP1257", BaseFont.EMBEDDED);
        Font font = new Font(f1, 32, Font.BOLD);
        Paragraph c = new Paragraph(String.format("FAKTURA: VAT/%s", vatHeader.format(d)), font);
        doc.add(c);

        Font font2 = new Font(f2, 24, Font.NORMAL);
        Font font3 = new Font(f2, 24, Font.NORMAL);
        font3.setColor(255, 0, 0);
        Paragraph nabywca = new Paragraph("Nabywca: sprzedawca: firma sprzedająca auta", font2);
        Paragraph sprzedawca = new Paragraph("Sprzedawca: nabywca: nabywca", font2);
        Paragraph info = new Paragraph(String.format("Faktura za auta z cenami między %d a %d\n\n", min, max), font3);

        doc.add(nabywca);
        doc.add(sprzedawca);
        doc.add(info);

        PdfPTable t = new PdfPTable(4);
        t.addCell("lp");
        t.addCell("cena");
        t.addCell("vat");
        t.addCell("wartość");

        double sum = 0;
        for (DBCar car:
                db) {
            if (car.price < min) continue;
            if (car.price > max) continue;
            double value = car.price;
            value = value * (1 + (((double)car.vat) / 100.0));

            sum += value;

            t.addCell(car.id.toString());
            t.addCell(car.price.toString());
            t.addCell(String.format("%d%%", car.vat));
            t.addCell(String.format("%.02f", value));
        }

        doc.add(t);

        Paragraph finalPrice = new Paragraph(String.format("DO ZAPŁATY: %.02f PLN", sum), font);
        doc.add(finalPrice);

        doc.close();
        return entry;
    }
}
