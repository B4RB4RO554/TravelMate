const express = require('express');
const router = express.Router();
const axios = require('axios');

// GET /api/currency/convert?from=USD&to=EUR&amount=100
router.get('/convert', async (req, res) => {
  try {
    const { from, to, amount } = req.query;

    if (!from || !to || !amount) {
      return res.status(400).json({ error: 'Please provide from, to, and amount query parameters' });
    }

    const apiKey = process.env.EXCHANGE_RATE_API_KEY;
    if (!apiKey) {
      return res.status(500).json({ error: 'ExchangeRate API key not configured' });
    }

    const url = `https://v6.exchangerate-api.com/v6/${apiKey}/pair/${from}/${to}/${amount}`;

    const { data } = await axios.get(url);

    if (data.result !== 'success') {
      return res.status(400).json({ error: 'Failed to convert currency', details: data['error-type'] });
    }

    res.json({
      from: data.base_code,
      to: data.target_code,
      amount: data.base_code === from.toUpperCase() ? +amount : data.base_amount,
      converted: data.conversion_result,
      rate: data.conversion_rate
    });

  } catch (err) {
    console.error('Error converting currency:', err.message);
    res.status(500).json({ error: 'Currency conversion failed' });
  }
});

module.exports = router;
