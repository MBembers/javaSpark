document.querySelector("form").addEventListener("submit", async (e) => {
    e.preventDefault();
    debugger;

    let payload = {
        model: e.target.model.value,
        year: parseInt(e.target.year.value),
        color: e.target.color.value,
        airbags: [
            {
                "name":"Kierowca",
                "state":e.target.p_kierowca.checked
            },
            {
                "name":"Pasażer",
                "state":e.target.p_pasazer.checked
            },
            {
                "name":"Tylna kanapa",
                "state":e.target.p_kanapa.checked
            },
            {
                "name":"Boczne z tyłu",
                "state":e.target.p_boczne.checked
            }
        ]
    };

    let response = await fetch("/add", {
        body: JSON.stringify(payload),
        headers: {
            "Content-Type":"application/json"
        },
        method: "POST"
    }).then(res => res.json()).catch(e => alert(e));

    alert(JSON.stringify(response, null, 2));
});
