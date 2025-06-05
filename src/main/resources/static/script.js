/* ========== Theme Toggle ========== */
const root = document.documentElement;
const btn = document.getElementById("themeToggle");
if (localStorage.getItem("theme") === "dark") {
  root.setAttribute("data-theme", "dark");
  btn.textContent = "☀️";
}
btn.addEventListener("click", () => {
  const dark = root.getAttribute("data-theme") === "dark";
  root.setAttribute("data-theme", dark ? "light" : "dark");
  btn.textContent = dark ? "🌙" : "☀️";
  localStorage.setItem("theme", dark ? "light" : "dark");
});

/* ========== Kalender ========== */
let visibleInput;
flatpickr("#startDate", {
  dateFormat: "Y-m-d",
  altInput: true,
  altFormat: "d.m.Y",
  locale: "de",
  allowInput: true,
  clickOpens: true,
  errorHandler: () => {},
  onReady: (sel, str, inst) => {
    visibleInput = inst.altInput;
    visibleInput.addEventListener("input", e => {
      let d = e.target.value.replace(/\D/g, "").slice(0, 8);
      if (d.length >= 5) d = d.slice(0, 2) + "." + d.slice(2, 4) + "." + d.slice(4);
      else if (d.length >= 3) d = d.slice(0, 2) + "." + d.slice(2);
      e.target.value = d;
    });
  }
});

/* ========== Inline-Validierung für Name & Anbieter & Preis ========== */
const nameInput     = document.getElementById("name");
const providerInput = document.getElementById("provider");
const priceInput    = document.getElementById("price");

// Patterns
const textPattern  = /^[A-Za-zÄÖÜäöüß\s+&\-.]+$/;
const pricePattern = /^\d+(?:[.,]\d{0,2})?$/;

// Name & Anbieter inline validieren
[nameInput, providerInput].forEach(input => {
  input.addEventListener("input", () => {
    if (!textPattern.test(input.value)) {
      input.setCustomValidity("Nur Buchstaben, Leerzeichen, + & - . erlaubt");
    } else {
      input.setCustomValidity("");
    }
    input.reportValidity();
  });
});

// Preis inline validieren
priceInput.addEventListener("input", () => {
  if (!pricePattern.test(priceInput.value)) {
    priceInput.setCustomValidity("Nur Zahlen und optional Dezimalpunkt mit max. 2 Nachkommastellen");
  } else {
    priceInput.setCustomValidity("");
  }
  priceInput.reportValidity();
});

/* ========== Daten laden & Tabelle rendern ========== */
function ladeAbos() {
  fetch("/api/subscriptions")
    .then(r => r.json())
    .then(list => {
      const tbody = document.querySelector("#aboTable tbody");
      tbody.innerHTML = "";
      list.forEach(a => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${a.name}</td>
          <td>${a.provider}</td>
          <td>${a.price.toFixed(2)} €</td>
          <td>${a.interval}</td>
          <td>${a.startDate}</td>
          <td>${a.canceled ? "Ja" : "Nein"}</td>
          <td>
            <button class="edit-button"   onclick="editAbo(${a.id})">Edit</button>
            ${!a.canceled ? `<button class="cancel-button" onclick="aboKündigen(${a.id})">Kündigen</button>` : ""}
            <button class="delete-button" onclick="aboLoeschen(${a.id})">Löschen</button>
          </td>`;
        tbody.appendChild(tr);
      });
    });

  fetch("/api/subscriptions/summary")
    .then(r => r.text())
    .then(sum => {
      document.getElementById("summary").textContent =
        parseFloat(sum).toFixed(2) + " €";
    });
}
ladeAbos();

/* ========== Formular (Create / Update) ========== */
document.getElementById("aboForm").addEventListener("submit", e => {
  e.preventDefault();

  // Name & Anbieter: falls invalid, Inline-Validierung meldet schon
  if (!textPattern.test(nameInput.value) || !textPattern.test(providerInput.value)) {
    return;
  }

  // Preis prüfen
  const price = parseFloat(priceInput.value.replace(",", "."));
  if (price < 0 || isNaN(price)) {
    alert("Preis darf nicht negativ sein.");
    priceInput.focus();
    return;
  }

  const id = document.getElementById("aboId").value;
  const data = {
    name: nameInput.value,
    provider: providerInput.value,
    price: price,
    interval: document.getElementById("interval").value,
    startDate: document.getElementById("startDate").value,
    canceled: false
  };

  const method = id ? "PUT" : "POST";
  const url    = id ? `/api/subscriptions/${id}` : "/api/subscriptions";

  fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  })
  .then(r => r.ok ? ladeAbos() : r.text().then(t => alert(t || "Fehler")))
  .then(resetForm);
});

function resetForm() {
  document.getElementById("aboForm").reset();
  document.getElementById("aboId").value = "";
  if (visibleInput) visibleInput.value = "";
}

/* ========== Edit / Cancel / Delete ========== */
function editAbo(id) {
  fetch("/api/subscriptions")
    .then(r => r.json())
    .then(list => {
      const a = list.find(x => x.id === id);
      if (!a) return;
      document.getElementById("aboId").value = id;
      nameInput.value     = a.name;
      providerInput.value = a.provider;
      priceInput.value    = a.price;
      document.getElementById("interval").value = a.interval;
      visibleInput.value  = a.startDate.split("-").reverse().join(".");
      document.getElementById("startDate").value = a.startDate;
      window.scrollTo({ top: 0, behavior: "smooth" });
    });
}

function aboKündigen(id) {
  if (!confirm("Wirklich kündigen?")) return;
  fetch(`/api/subscriptions/${id}/cancel`, { method: "PUT" })
    .then(ladeAbos);
}

function aboLoeschen(id) {
  if (!confirm("Abo wirklich endgültig löschen?")) return;
  fetch(`/api/subscriptions/${id}`, { method: "DELETE" })
    .then(r => r.ok ? ladeAbos() : alert("Löschen fehlgeschlagen"));
}
