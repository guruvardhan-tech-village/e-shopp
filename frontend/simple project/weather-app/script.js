const apikey = "23ce4d39c91d422eb2a63513263004&q";

const weatherDataEl = document.getElementById("weather-data");
const cityInputEl = document.getElementById("city-input");
const formEl = document.querySelector("form");

formEl.addEventListener("submit", (event) => {
  event.preventDefault();
  const cityValue = cityInputEl.value;
  getWeatherData(cityValue);
});

async function getWeatherData(cityValue) {
  try {
    weatherDataEl.querySelector(".description").textContent = "Loading...";

    const response = await fetch(
      `https://api.weatherapi.com/v1/current.json?key=${apikey}&q=${cityValue}&aqi=no`
    );

    if (!response.ok) {
      throw new Error("City not found");
    }

    const data = await response.json();

    // ✅ City name
    document.querySelector(".city-name").textContent = data.location.name;

    const temperature = data.current.temp_c;
    const description = data.current.condition.text;
    const icon = data.current.condition.icon;

    const details = [
      `Feels like: ${data.current.feelslike_c}°C`,
      `Humidity: ${data.current.humidity}%`,
      `Wind speed: ${data.current.wind_kph} km/h`,
    ];

    weatherDataEl.querySelector(".icon").innerHTML =
      `<img src="https:${icon}" alt="Weather Icon">`;

    weatherDataEl.querySelector(".temperature").textContent =
      `${temperature}°C`;

    weatherDataEl.querySelector(".description").textContent = description;

    // ✅ Background change
    const body = document.body;
    if (description.toLowerCase().includes("cloud")) {
      body.style.background = "linear-gradient(to right, #757f9a, #d7dde8)";
    } else if (description.toLowerCase().includes("rain")) {
      body.style.background = "linear-gradient(to right, #373b44, #4286f4)";
    } else if (description.toLowerCase().includes("sunny")) {
      body.style.background = "linear-gradient(to right, #fceabb, #f8b500)";
    } else {
      body.style.background = "#f0f0f0";
    }

    weatherDataEl.querySelector(".details").innerHTML = details
      .map((detail) => `<div>${detail}</div>`)
      .join("");

  } catch (error) {
    weatherDataEl.querySelector(".icon").innerHTML = "";
    weatherDataEl.querySelector(".temperature").textContent = "";
    weatherDataEl.querySelector(".description").textContent =
      "City not found or API error";
    weatherDataEl.querySelector(".details").innerHTML = "";
  }
}