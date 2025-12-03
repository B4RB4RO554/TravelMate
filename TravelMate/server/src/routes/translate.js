const express = require('express');
const router = express.Router();
const axios = require('axios');

// POST /api/translate
router.post('/', async (req, res) => {
  console.log('🔁 /api/translate route hit');

  const { text, source, target } = req.body;

  if (!text || !source || !target) {
    return res.status(400).json({ error: 'Missing required fields: text, source, target' });
  }

  let translatedText = null;

  // Try MyMemory API first (free, reliable, no API key needed)
  try {
    const langPair = `${source}|${target}`;
    const response = await axios.get('https://api.mymemory.translated.net/get', {
      params: {
        q: text,
        langpair: langPair
      },
      timeout: 10000
    });
    
    if (response.data && response.data.responseStatus === 200) {
      translatedText = response.data.responseData.translatedText;
      console.log('✅ Translation successful via MyMemory');
    }
  } catch (err) {
    console.warn('⚠️ MyMemory translation failed:', err.message);
  }

  // Fallback to LibreTranslate endpoints
  if (!translatedText) {
    const translateEndpoints = [
      'https://libretranslate.com/translate',
      'https://libretranslate.de/translate',
      'https://translate.astian.org/translate'
    ];

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
            headers: { 'Content-Type': 'application/json' },
            timeout: 5000
          }
        );
        translatedText = response.data.translatedText;
        console.log(`✅ Translation successful via ${url}`);
        break;
      } catch (err) {
        console.warn(`⚠️ Translation failed on ${url}, trying next...`);
      }
    }
  }

  if (!translatedText) {
    return res.status(500).json({ error: 'Translation failed on all endpoints' });
  }

  res.json({ translated: translatedText });
});

module.exports = router;
