const currencyFirstEl = document.getElementById("currency-first");

const worthFirstEl = document.getElementById("worth-first");

const currencySecondEl = document.getElementById("currency-second");

const worthSecondEl = document.getElementById("worth-second");

const exchangeRateEl = document.getElementById("exchange-rate");

let currentRate = 0;

function fetchExchangeRate() {
  fetch(
    `https://v6.exchangerate-api.com/v6/0964da014c7188ae729b9f92/latest/${currencyFirstEl.value}`
  )
    .then((res) => res.json())
    .then((data) => {
      if (data.result === "success") {
        currentRate = data.conversion_rates[currencySecondEl.value];
        exchangeRateEl.innerText = `1 ${currencyFirstEl.value} = ${currentRate} ${currencySecondEl.value}`;
        updateConversion();
      } else {
        exchangeRateEl.innerText = "Error fetching rate";
        console.error("API Error:", data);
      }
    })
    .catch((error) => {
      exchangeRateEl.innerText = "Network error";
      console.error("Fetch Error:", error);
    });
}

function updateConversion() {
  if (worthFirstEl.value === "") {
    worthSecondEl.value = "";
    return;
  }
  worthSecondEl.value = (worthFirstEl.value * currentRate).toFixed(2);
}

currencyFirstEl.addEventListener("change", fetchExchangeRate);
currencySecondEl.addEventListener("change", fetchExchangeRate);
worthFirstEl.addEventListener("input", updateConversion);

fetchExchangeRate();