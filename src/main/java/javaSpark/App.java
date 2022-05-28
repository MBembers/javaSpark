package javaSpark;

import com.google.gson.Gson;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import static spark.Spark.*;

class YearInvoiceRequest {
    Integer year;
}

class RangeInvoiceRequest {
    Integer min;
    Integer max;
}

public class App {
    static ArrayList<DBCar> cars = new ArrayList<>();
    static int lastId = 0;

    static InvoiceBase ibase = new InvoiceBase();

    public static void main(String[] args) {
        port(5000);

        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
        staticFiles.location("public/javaSpark");

        post("/add", (req, res) -> {
            Gson gson = new Gson();
            Car postedCar = gson.fromJson(req.body(), Car.class);
            lastId++;
            DBCar c = new DBCar(postedCar, lastId);
            cars.add(c);
            res.header("Content-Type", "application/json");
            return gson.toJson(c);
        });

        get("/json", (req, res) -> {
            Gson gson = new Gson();
            return gson.toJson(cars);
        });

        get("/advanced_invoices", (req, res) -> {
            Gson gson = new Gson();
            return gson.toJson(ibase);
        });

        post("/invoice", (req, res) -> {
            Gson gson = new Gson();
            Posted p = gson.fromJson(req.body(), Posted.class);
            for(DBCar c: cars) {
                if (c.uuid.equals(p.id)) {
                    c.makeInvoice();
                }
            }
            return "ok";
        });

        post("/invoice_year", (req, res) -> {
            Gson gson = new Gson();
            YearInvoiceRequest r = gson.fromJson(req.body(), YearInvoiceRequest.class);
            InvoiceBaseEntry entry = InvoiceGenerator.generateInvoiceForCarsFromYear(cars, r.year);
            ibase.yearInvoices.add(entry);

            return "ok";
        });

        post("/invoice_range", (req, res) -> {
            Gson gson = new Gson();
            RangeInvoiceRequest r = gson.fromJson(req.body(), RangeInvoiceRequest.class);
            InvoiceBaseEntry entry = InvoiceGenerator.generateInvoiceForCarsFromPriceRange(cars, r.min, r.max);
            ibase.priceInvoices.add(entry);

            return "ok";
        });


        post("/invoice_all", (req, res) -> {
           InvoiceBaseEntry entry = InvoiceGenerator.generateInvoiceForAllCars(cars);
           ibase.generalInvoices.add(entry);

           return "ok";
        });

        get("/invoice", (req, res) -> {
            String id = req.queryParamOrDefault("id", "NOT_EXISTANT_ID");
            File f = new File("invoices/" + id + ".pdf");
            if (!f.exists()) {
                res.status(404);
                return "<h1>nie ma</h1>";
            } else {
                res.header("Content-Disposition", "attachment; filename=FAKTURA_" + id + ".pdf");
                OutputStream o = res.raw().getOutputStream();
                o.write(Files.readAllBytes(f.toPath()));

                return "ok";
            }
        });

        post("/generate", (req, res) -> {
            String[] validModels = {"Audi", "Renault", "Ford", "Citroen", "Volkswagen", "BMW", "Mercedes", "Toyota"};
            for (int i = 0; i < 10; i++) {
                Car baseCar =new Car();
                Random r = new Random();
                baseCar.airbags = new ArrayList<>();
                baseCar.airbags.add(new Airbag("Kierowca", r.nextBoolean()));
                baseCar.airbags.add(new Airbag("Pasażer", r.nextBoolean()));
                baseCar.airbags.add(new Airbag("Tylna kanapa", r.nextBoolean()));
                baseCar.airbags.add(new Airbag("Boczne z tyłu", r.nextBoolean()));
                baseCar.color = "#" + String.format("%02x", r.nextInt(0, 256))+ String.format("%02x", r.nextInt(0, 256))+ String.format("%02x", r.nextInt(0, 256));
                baseCar.year = r.nextInt(2001, 2020);
                baseCar.model = validModels[r.nextInt(0, validModels.length)];

                lastId++;
                DBCar c = new DBCar(baseCar, lastId);
                cars.add(c);
            }
            return "ok";
        });

        post("/delete", (req, res) -> {
            Gson gson = new Gson();
            DeleteCarDescriptor descriptor = gson.fromJson(req.body(), DeleteCarDescriptor.class);
            UUID target = descriptor.victim;

            cars.removeIf(car -> car.uuid.equals(target));

            return "ok";
        });

        post("/update", (req, res) -> {
            Gson gson = new Gson();
            UpdateCarDescriptor descriptor = gson.fromJson(req.body(), UpdateCarDescriptor.class);
            UUID target = descriptor.victim;

            for(DBCar c: cars) {
                if (c.uuid.equals(target)) {
                    c.chcar(descriptor.newCar);
                    c.hasInvoice = false;
                }
            }
            return "ok";
        });
    }
}

