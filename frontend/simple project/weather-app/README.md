# Weather App

A simple weather app that fetches real-time weather data using the [WeatherAPI](https://www.weatherapi.com/).

## Features

- Search weather by city name
- Displays temperature, condition, feels like, humidity, and wind speed
- Dynamic background that changes based on weather condition
- Responsive design for mobile screens

## Setup

1. Clone or download the project.
2. Open `script.js` and replace the `apikey` value with your own key from [weatherapi.com](https://www.weatherapi.com/).
3. Open `index.html` in a browser.

## Files

- `index.html` — App structure
- `style.css` — Styling and responsive layout
- `script.js` — Weather API logic and DOM updates

## API

Uses [WeatherAPI](https://www.weatherapi.com/) `current.json` endpoint.

```
GET https://api.weatherapi.com/v1/current.json?key={API_KEY}&q={city}&aqi=no
```

## Note

Keep your API key private. Avoid committing it to public repositories.
