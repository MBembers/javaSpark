function editor(car) {
    return new Promise(resolve => {
        let initialCar = JSON.parse(JSON.stringify(car));
        // Clean up the form
        document.querySelectorAll("*[data-removeme='1']").forEach(v=>v.remove());
        document.querySelector("ul").innerHTML = "";

        // Populate form
        document.querySelector("#model").value = car.model;
        document.querySelector("#year").value = car.year;
        document.querySelector("#color").value = car.color;

        let ppow = new Map();

        for(let airbag of car.airbags) {
            let li = document.createElement("li");
            let tn = document.createTextNode(airbag.name);
            let box = document.createElement("input");
            box.type = "checkbox";
            box.checked = airbag.state;

            li.append(tn, box);
            document.querySelector("ul").append(li);

            ppow.set(airbag, box);
        }

        let abandonButton = document.createElement("button");
        abandonButton.dataset.removeme="1";
        abandonButton.addEventListener("click", () => {
            resolve(initialCar);
            document.querySelector("#edit-modal").style.display = "none";
        });
        abandonButton.innerText = "cancel";

        let saveButton = document.createElement("button");
        saveButton.dataset.removeme="1";
        saveButton.addEventListener("click", () => {
            let payload = {};
            payload.model = document.querySelector("#model").value;
            payload.year = parseInt(document.querySelector("#year").value);
            payload.color = document.querySelector("#color").value;
            payload.airbags = [];

            for (let a of ppow.keys()) {
                a.state = ppow.get(a).checked;
                payload.airbags.push(a);
            }

            resolve(payload);

            document.querySelector("#edit-modal").style.display = "none";
        });
        saveButton.innerText = "save";

        document.querySelector("#edit-modal-inner").append(abandonButton, saveButton);
        document.querySelector("#edit-modal").style.display = "flex";
    });
}

async function populateTable(altMode) {
    let cars = await fetch("/json").then(res=>res.json()).catch(e=>alert(e));
    document.querySelector("tbody").innerHTML = "";
    for (let car of cars) {
        let tr = document.createElement("tr");

        let idNode = document.createElement("td");
        idNode.innerText = car.id;
        tr.appendChild(idNode);

        let uuidNode = document.createElement("td");
        uuidNode.innerText = car.uuid;
        tr.appendChild(uuidNode);

        let modelNode = document.createElement("td");
        modelNode.innerText = car.model;
        tr.appendChild(modelNode);

        let yearNode = document.createElement("td");
        yearNode.innerText = car.year;
        tr.appendChild(yearNode);

        let airbagsText = [];
        for (let bag of car.airbags) {
            airbagsText.push(`${bag.name}: ${bag.state?"TAK":"NIE"}`)
        }
        airbagsText = airbagsText.join("\n");


        let colorNode = document.createElement("td");
        colorNode.classList.add("colorNode");
        colorNode.style.setProperty("--color", car.color);

        let airbagsNode = document.createElement("td");
        airbagsNode.innerText = airbagsText;
        tr.appendChild(airbagsNode);
        tr.appendChild(colorNode);

        if (!altMode) {
            let deleteCarHolderNode = document.createElement("td");
            let deleteCarButton = document.createElement("button");
            deleteCarButton.addEventListener("click", async function () {
                await fetch("/delete", {
                    headers: {"Content-Type": "application/json"},
                    method: "POST",
                    body: JSON.stringify({victim: car.uuid})
                }).then(() => populateTable()).catch(alert);
            });
            deleteCarButton.innerText = "Delete";
            deleteCarHolderNode.appendChild(deleteCarButton);
            tr.appendChild(deleteCarHolderNode);

            let editCarHolderNode = document.createElement("td");
            let editCarButton = document.createElement("button");
            editCarButton.addEventListener("click", async function () {
                await fetch("/update", {
                    headers: {"Content-Type": "application/json"},
                    method: "POST",
                    body: JSON.stringify({victim: car.uuid, newCar: await editor(car)})
                }).then(() => populateTable()).catch(alert);
            });
            editCarButton.innerText = "Update";
            editCarHolderNode.appendChild(editCarButton);
            tr.appendChild(editCarHolderNode);
        } else {
            let generateInvoiceButtonHolderNode = document.createElement("td");
            let generateInvoiceButton = document.createElement("button");
            generateInvoiceButton.addEventListener("click", async function() {
                await fetch("/invoice", {
                    headers: {"Content-Type":"application/json"},
                    method: "POST",
                    body: JSON.stringify({id: car.uuid})
                }).then(() => populateTable(true)).catch(alert);
            });
            generateInvoiceButton.innerText = "Generuj fakturę VAT";
            generateInvoiceButtonHolderNode.appendChild(generateInvoiceButton);
            tr.appendChild(generateInvoiceButtonHolderNode);


            let dlInvoiceHolder = document.createElement("td");
            if (car.hasInvoice) {
                let dlInvoice = document.createElement("a");
                dlInvoice.download = `${car.uuid}.pdf`;
                dlInvoice.href = `/invoice?id=${car.uuid}`;

                dlInvoice.text = "pobierz";
                dlInvoiceHolder.append(dlInvoice);
            }

            tr.appendChild(dlInvoiceHolder);
        }
        document.querySelector("tbody").appendChild(tr);
    }
}