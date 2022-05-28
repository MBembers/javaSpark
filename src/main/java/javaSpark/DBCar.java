package javaSpark;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

class DBCar extends Car {
    Integer id;
    UUID uuid;
    Boolean hasInvoice;

    Integer buyYear;
    String buyDate;
    Integer price;
    Integer vat;

    public DBCar(Car base, int id) {
        this.model = base.model;
        this.hasInvoice = false;
        this.year = base.year;
        this.airbags = base.airbags;
        this.color = base.color;
        this.id = id;
        this.uuid = UUID.randomUUID();

        int year = new Random().nextInt(2000, 2022);
        int month = new Random().nextInt(1, 12);
        int day = new Random().nextInt(1, 31);
        int price =new Random().nextInt(5000, 105000);
        int vat = new Random().nextInt(0, 25);

        this.buyYear = year;
        this.buyDate = String.format("%d/%02d/%02d", year, month, day);
        this.price = price;
        this.vat = vat;
    }

    void makeInvoice() throws Exception {
        Document doc = new Document();
        StringBuilder path_builder = new StringBuilder();
        path_builder.append("invoices/");
        path_builder.append(this.uuid.toString());
        path_builder.append(".pdf");
        String path = path_builder.toString();

        PdfWriter.getInstance(doc, new FileOutputStream(path));

        doc.open();

        BaseFont f = BaseFont.createFont(BaseFont.HELVETICA_BOLD, "CP1257", BaseFont.EMBEDDED);
        BaseFont f2 = BaseFont.createFont(BaseFont.HELVETICA, "CP1257", BaseFont.EMBEDDED);
        Font font = new Font(f, 32, Font.BOLD);
        Paragraph c = new Paragraph("Faktura VAT", font);
        doc.add(c);

        Font mainContentFont = new Font(f2, 16, Font.NORMAL);
        Font coloredContentFont = new Font(f2, 16, Font.NORMAL);
        coloredContentFont.setColor(
                Color.decode(this.color).getRed(),
                Color.decode(this.color).getGreen(),
                Color.decode(this.color).getBlue()
        );
        Paragraph idChunk = new Paragraph(String.format("ID Samochodu: %s", this.uuid.toString()), mainContentFont);
        Paragraph modelChunk = new Paragraph(String.format("Model: %s", this.model), mainContentFont);
        Paragraph colorChunk = new Paragraph(String.format("Kolor: %s", this.color), coloredContentFont);
        Paragraph yearChunk = new Paragraph(String.format("Rok: %d", this.year), mainContentFont);
        doc.add(idChunk);
        doc.add(modelChunk);
        doc.add(colorChunk);
        doc.add(yearChunk);

        for (Airbag a : this.airbags) {
            Paragraph airbagChunk = new Paragraph(String.format("%s: %s", a.name, a.state.toString()), mainContentFont);
            doc.add(airbagChunk);
        }

        File file = new File("invoices/images/" + this.model.toLowerCase(Locale.ROOT) + ".jpeg");
        if (file.exists()) {
            com.itextpdf.text.Image img = Image.getInstance(file.getAbsolutePath());
            img.scaleToFit(300, 300);
            doc.add(img);
        }

        doc.close();

        this.hasInvoice = true;
    }

    void chcar(Car base) {
        this.model = base.model;
        this.year = base.year;
        this.airbags = base.airbags;
        this.color = base.color;
    }
}
