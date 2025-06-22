const express = require('express');
const router = express.Router();
const axios = require('axios');

// POST /api/translate
router.post('/', async (req, res) => {
  console.log('🔁 /api/translate route hit'); // Confirm in terminal

  const { text, source, target } = req.body;

  if (!text || !source || !target) {
    return res.status(400).json({ error: 'Missing required fields: text, source, target' });
  }

  const translateEndpoints = [
    'https://libretranslate.de/translate',
    'https://translate.astian.org/translate'
  ];

  let translatedText = null;

  for (const url of translateEndpoints) {
    try {
      const response = await axios.post(
        url,
        {
          q: text,
          source,
          target,
          format: 'text'
        },
        {
          headers: { 'Content-Type': 'application/json' }
        }
      );
      translatedText = response.data.translatedText;
      break; // success, stop trying other endpoints
    } catch (err) {
      console.warn(`⚠️ Translation failed on ${url}, trying next...`);
    }
  }

  if (!translatedText) {
    return res.status(500).json({ error: 'Translation failed on all endpoints' });
  }

  res.json({ translated: translatedText });
});

module.exports = router;
