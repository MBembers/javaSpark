import { html, render } from "https://cdn.jsdelivr.net/gh/lit/dist@2/all/lit-all.min.js";

const tableHeader = () => html`<tr>
    <th>Lp</th>
    <th>Marka</th>
    <th>Rok</th>
    <th>Poduszki</th>
    <th>Kolor</th>
    <th>Zdjęcie</th>
    <th>Data sprzedaży</th>
    <th>Cena</th>
    <th>VAT</th>
</tr>`;

const poduszka = ({name, state}) => html`<p>${name}: ${state}</p>`

const poduszki = (config) => html`<div>
    ${config.map(a => poduszka(a))}
</div>`

const kolor = (col) => html`<td class="kolor" style="--c: ${col}"></td>`;

const photo = (model) => {
    if (["audi", "bmw", "citroen", "ford", "mercedes", "renault", "toyota", "volkswagen"].includes(model.toLowerCase())) {
        return html`<img src="/assets/${model.toLowerCase()}.jpeg">`;
    } else {
        return html`<div></div>`
    }
}

const car = (car) => html`<tr>
    <td>${car.id}</td>
    <td>${car.model}</td>
    <td>${car.year}</td>
    <td>${poduszki(car.airbags)}</td>
    ${kolor(car.color)}
    <td>${photo(car.model)}</td>
    <td>${car.full_buy_date}</td>
    <td>${car.price}</td>
    <td>${car.vat}</td>
</tr>`

const invoiceLink = ({url, name}) => html`<a href="/invoice?id=${url.split("invoices/")[1].split(".pdf")[0]}" title="${name}">Pobierz</a>`

const overall = (cars) => html`${tableHeader()}${cars.map(a=>car(a))}`;

async function refetchCars() {
    let cars = await fetch("/json").then(res=>res.json()).catch(e=>alert(e));

    let table = document.querySelector("#car-list");

    render(overall(cars), table);
}

async function refetchInvoices() {
    let invoices = await fetch("/advanced_invoices").then(res => res.json()).catch(e => alert(e));
    console.log(invoices);

    let renderedGen = invoices.generalInvoices.map(a => invoiceLink(a));
    render(renderedGen, document.querySelector("#general_invoice_links"));

    let renderedYear = invoices.yearInvoices.map(a => invoiceLink(a));
    render(renderedYear, document.querySelector("#year_invoice_link"));

    let renderedRange = invoices.priceInvoices.map(a => invoiceLink(a));
    render(renderedRange, document.querySelector("#range_invoice_link"));
}

refetchCars();

document.getElementById("genrand").addEventListener("click", async () => {
    await fetch("/generate", {
        method: "POST",
        body: JSON.stringify({}),
        headers: {
            "Content-Type": "application/json"
        }
    });
    await refetchCars();
});

document.querySelector("#general_invoice_generator").addEventListener("click", async () => {
    await fetch("/invoice_all", {
        method: "POST",
        body: JSON.stringify({}),
        headers: {
            "Content-Type": "application/json"
        }
    });
    alert("Faktura za wszystkie auta wygenerowana");
    await refetchInvoices();
})

document.querySelector("#year_invoice_generator").addEventListener("click", async () => {
    let el = parseInt(document.querySelector("select#year_dropdown").value);
    await fetch("/invoice_year", {
        method: "POST",
        body: JSON.stringify({
            year: el
        }),
        headers: {
            "Content-Type": "application/json"
        }
    });
    alert("Faktura za auta z roku " + el + " wygenerowana");
    await refetchInvoices();
});

document.querySelector("#range_invoice_generator").addEventListener("click", async () => {
    let min = parseInt(document.querySelector("#range_invoice_min").value);
    let max = parseInt(document.querySelector("#range_invoice_max").value);

    if (min >= max)  return alert("Min musi być mniejsze niż max");

    await fetch("/invoice_range", {
        method: "POST",
        body: JSON.stringify({
            min, max
        }),
        headers: {
            "Content-Type": "application/json"
        }
    });

    alert("Faktura z zakresu cen " + min + " do " + max + " wygenerowana");
    await refetchInvoices();
})



refetchInvoices();