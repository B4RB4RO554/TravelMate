const express = require('express');
const router = express.Router();

// Mock weather data for demo purposes
// In production, integrate with OpenWeatherMap, WeatherAPI, etc.
const mockWeatherData = {
    'paris': { temp: 18, condition: 'Partly Cloudy', humidity: 65, wind: 12, icon: '⛅' },
    'london': { temp: 14, condition: 'Rainy', humidity: 85, wind: 18, icon: '🌧️' },
    'new york': { temp: 22, condition: 'Sunny', humidity: 55, wind: 8, icon: '☀️' },
    'tokyo': { temp: 25, condition: 'Clear', humidity: 60, wind: 10, icon: '🌤️' },
    'dubai': { temp: 38, condition: 'Hot', humidity: 35, wind: 15, icon: '🌡️' },
    'sydney': { temp: 20, condition: 'Cloudy', humidity: 70, wind: 20, icon: '☁️' },
    'rome': { temp: 24, condition: 'Sunny', humidity: 50, wind: 7, icon: '☀️' },
    'barcelona': { temp: 23, condition: 'Clear', humidity: 58, wind: 14, icon: '🌤️' },
    'amsterdam': { temp: 15, condition: 'Overcast', humidity: 78, wind: 16, icon: '☁️' },
    'berlin': { temp: 16, condition: 'Cloudy', humidity: 72, wind: 13, icon: '☁️' },
    'tunis': { temp: 28, condition: 'Sunny', humidity: 45, wind: 11, icon: '☀️' },
    'cairo': { temp: 32, condition: 'Hot', humidity: 30, wind: 9, icon: '🌡️' },
    'marrakech': { temp: 30, condition: 'Sunny', humidity: 38, wind: 10, icon: '☀️' },
    'istanbul': { temp: 21, condition: 'Partly Cloudy', humidity: 62, wind: 12, icon: '⛅' },
    'default': { temp: 20, condition: 'Clear', humidity: 50, wind: 10, icon: '🌤️' }
};

// GET weather for a city
// @route GET /api/weather?city=Paris
router.get('/', async (req, res) => {
    try {
        const { city } = req.query;
        
        if (!city) {
            return res.status(400).json({ error: 'City parameter is required' });
        }
        
        const cityLower = city.toLowerCase();
        const weatherData = mockWeatherData[cityLower] || mockWeatherData['default'];
        
        // Add some randomness to make it more realistic
        const tempVariation = Math.floor(Math.random() * 5) - 2;
        const humidityVariation = Math.floor(Math.random() * 10) - 5;
        
        const response = {
            city: city,
            temperature: weatherData.temp + tempVariation,
            temperatureUnit: 'C',
            condition: weatherData.condition,
            humidity: Math.max(0, Math.min(100, weatherData.humidity + humidityVariation)),
            windSpeed: weatherData.wind,
            windUnit: 'km/h',
            icon: weatherData.icon,
            timestamp: new Date().toISOString(),
            forecast: generateForecast(weatherData)
        };
        
        res.json(response);
    } catch (error) {
        console.error('Weather API error:', error);
        res.status(500).json({ error: 'Failed to fetch weather data' });
    }
});

// GET 5-day forecast for a city
// @route GET /api/weather/forecast?city=Paris
router.get('/forecast', async (req, res) => {
    try {
        const { city } = req.query;
        
        if (!city) {
            return res.status(400).json({ error: 'City parameter is required' });
        }
        
        const cityLower = city.toLowerCase();
        const baseWeather = mockWeatherData[cityLower] || mockWeatherData['default'];
        
        const forecast = generateForecast(baseWeather);
        
        res.json({
            city: city,
            forecast: forecast
        });
    } catch (error) {
        console.error('Forecast API error:', error);
        res.status(500).json({ error: 'Failed to fetch forecast data' });
    }
});

// GET weather alerts for a city
// @route GET /api/weather/alerts?city=Paris
router.get('/alerts', async (req, res) => {
    try {
        const { city } = req.query;
        
        if (!city) {
            return res.status(400).json({ error: 'City parameter is required' });
        }
        
        const cityLower = city.toLowerCase();
        const weatherData = mockWeatherData[cityLower] || mockWeatherData['default'];
        
        const alerts = [];
        
        // Check for extreme conditions
        if (weatherData.temp >= 35) {
            alerts.push({
                type: 'HEAT_WARNING',
                severity: 'high',
                message: `Extreme heat expected: ${weatherData.temp}°C. Stay hydrated and avoid prolonged sun exposure.`,
                icon: '🌡️'
            });
        }
        
        if (weatherData.temp <= 0) {
            alerts.push({
                type: 'FREEZE_WARNING',
                severity: 'high',
                message: `Freezing temperatures expected: ${weatherData.temp}°C. Dress warmly and watch for ice.`,
                icon: '❄️'
            });
        }
        
        if (weatherData.wind >= 50) {
            alerts.push({
                type: 'WIND_WARNING',
                severity: 'medium',
                message: `Strong winds expected: ${weatherData.wind} km/h. Secure loose items outdoors.`,
                icon: '💨'
            });
        }
        
        if (weatherData.condition.toLowerCase().includes('rain') || 
            weatherData.condition.toLowerCase().includes('storm')) {
            alerts.push({
                type: 'RAIN_ALERT',
                severity: 'low',
                message: 'Rain expected. Bring an umbrella.',
                icon: '🌧️'
            });
        }
        
        res.json({
            city: city,
            alerts: alerts,
            timestamp: new Date().toISOString()
        });
    } catch (error) {
        console.error('Weather alerts API error:', error);
        res.status(500).json({ error: 'Failed to fetch weather alerts' });
    }
});

// Helper function to generate 5-day forecast
function generateForecast(baseWeather) {
    const conditions = ['Sunny', 'Partly Cloudy', 'Cloudy', 'Rainy', 'Clear'];
    const icons = ['☀️', '⛅', '☁️', '🌧️', '🌤️'];
    const forecast = [];
    
    for (let i = 0; i < 5; i++) {
        const date = new Date();
        date.setDate(date.getDate() + i);
        
        const tempVariation = Math.floor(Math.random() * 8) - 4;
        const conditionIndex = Math.floor(Math.random() * conditions.length);
        
        forecast.push({
            date: date.toISOString().split('T')[0],
            dayName: date.toLocaleDateString('en-US', { weekday: 'short' }),
            tempHigh: baseWeather.temp + tempVariation + 3,
            tempLow: baseWeather.temp + tempVariation - 5,
            condition: conditions[conditionIndex],
            icon: icons[conditionIndex],
            precipitationChance: Math.floor(Math.random() * 60)
        });
    }
    
    return forecast;
}

module.exports = router;
