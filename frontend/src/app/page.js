'use client';

import { useState, useEffect } from 'react';
import { Search, ArrowRightLeft, Loader2, Volume2, History, Database } from 'lucide-react';
import './globals.css';

const API_BASE = 'http://localhost:8080/api';

export default function DictionaryApp() {
  const [word, setWord] = useState('');
  const [searchedWord, setSearchedWord] = useState('');
  const [direction, setDirection] = useState('en-ua');
  const [results, setResults] = useState([]);
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [history, setHistory] = useState([]);
  const [isTyping, setIsTyping] = useState(false);

  useEffect(() => {
    const savedHistory = JSON.parse(localStorage.getItem('dict-history')) || [];
    setHistory(savedHistory);
  }, []);

  useEffect(() => {
    if (word.length >= 2 && isTyping) {
      const delay = setTimeout(() => {
        fetch(`${API_BASE}/search?query=${encodeURIComponent(word)}`)
          .then(res => res.json())
          .then(data => setSuggestions(data))
          .catch(err => console.error('Autocomplete error:', err));
      }, 300);
      return () => clearTimeout(delay);
    } else {
      setSuggestions([]);
    }
  }, [word, isTyping]);

  const speakWord = (text, lang) => {
    const utterance = new SpeechSynthesisUtterance(text);
    // lang - це напрямок (en-ua або ua-en)
    // Якщо напрямок en-ua, озвучуємо англійською, інакше - українською
    utterance.lang = lang === 'en-ua' ? 'en-US' : 'uk-UA';
    utterance.rate = 0.9;
    window.speechSynthesis.speak(utterance);
  };

  const handleTranslate = async (targetWord = word) => {
    if (!targetWord.trim()) {
      setError('Будь ласка, введіть слово');
      return;
    }

    setLoading(true);
    setError('');
    setSuggestions([]);
    setIsTyping(false);
    setSearchedWord(targetWord);

    try {
      const res = await fetch(`${API_BASE}/translate?word=${encodeURIComponent(targetWord)}&direction=${direction}`);

      if (res.status === 404) {
        setError('Слово не знайдено у базі 😔');
        setResults([]);
      } else if (!res.ok) {
        throw new Error('Server error');
      } else {
        const data = await res.json();
        setResults(data);

        const newHistory = [targetWord, ...history.filter(w => w !== targetWord)].slice(0, 5);
        setHistory(newHistory);
        localStorage.setItem('dict-history', JSON.stringify(newHistory));
      }
    } catch (err) {
      setError('Помилка з\'єднання. Перевірте, чи запущений Java-бекенд.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleLoadSample = async () => {
    try {
      const sample = await fetch('/sample-data.json').then(r => r.json());
      const res = await fetch(`${API_BASE}/load`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(sample)
      });
      if (res.ok) alert('✅ Тестові дані успішно завантажено!');
      else alert('❌ Помилка завантаження даних');
    } catch (err) {
      alert('Помилка: ' + err.message);
    }
  };

  return (
    <div className="glass-container">
      <h1 className="app-title">PolyGlot Translator</h1>

      {history.length > 0 && (
        <div className="history-tags">
          <History size={14} style={{ color: 'var(--text-muted)', marginTop: '4px' }} />
          {history.map((h, i) => (
            <span
              key={i}
              className="history-tag"
              onClick={() => {
                setIsTyping(false);
                setWord(h);
                handleTranslate(h);
              }}
            >
              {h}
            </span>
          ))}
        </div>
      )}

      <div className="search-bar">
        <button
          className="glass-btn"
          onClick={() => setDirection(prev => prev === 'en-ua' ? 'ua-en' : 'en-ua')}
          title="Змінити напрям перекладу"
        >
          <ArrowRightLeft size={18} />
          {direction === 'en-ua' ? 'EN → UA' : 'UA → EN'}
        </button>

        <div style={{ flex: 1, position: 'relative' }}>
          <input
            type="text"
            className="glass-input"
            value={word}
            onChange={(e) => {
              setWord(e.target.value);
              setIsTyping(true);
            }}
            onKeyPress={(e) => e.key === 'Enter' && handleTranslate()}
            placeholder={direction === 'en-ua' ? "Type an English word..." : "Введіть українське слово..."}
            style={{ width: '100%' }}
          />

          {suggestions.length > 0 && (
            <ul className="suggestions-list">
              {suggestions.map(s => (
                <li
                  key={s}
                  onClick={() => {
                    setIsTyping(false);
                    setWord(s);
                    setSuggestions([]);
                    handleTranslate(s);
                  }}
                >
                  {s}
                </li>
              ))}
            </ul>
          )}
        </div>

        <button
          className="glass-btn primary"
          onClick={() => handleTranslate()}
          disabled={loading}
        >
          {loading ? <Loader2 className="animate-spin" size={18} /> : <Search size={18} />}
          Translate
        </button>
      </div>

      {error && <div style={{ color: '#ef4444', marginBottom: '20px', textAlign: 'center' }}>{error}</div>}

      {results.map((entry, idx) => {
        const isEnUa = direction === 'en-ua';

        // Шукаємо, що саме ввів користувач серед полів об'єкта
        const matchedEn = (entry.word.toLowerCase() === searchedWord.toLowerCase())
            ? entry.word
            : (entry.synonyms && entry.synonyms.find(s => s.toLowerCase() === searchedWord.toLowerCase()));

        const matchedUa = entry.translations.find(t => t.toLowerCase() === searchedWord.toLowerCase()) ||
                          (entry.uaSynonyms && entry.uaSynonyms.find(t => t.toLowerCase() === searchedWord.toLowerCase()));

        // Визначаємо заголовок: якщо знайшли точний збіг синоніма/слова, беремо його
        const displayTitle = isEnUa ? (matchedEn || entry.word) : (matchedUa || entry.translations[0]);

        // Визначаємо переклад
        const displayTranslation = isEnUa ? entry.translations.join(', ') : entry.word;

        // Озвучуємо завжди те, що в заголовку
        const wordToSpeak = displayTitle;

        return (
          <div key={idx} className="result-card">
            <div className="result-header">
              <h2 className="word-title">{displayTitle}</h2>
              <button className="icon-btn" onClick={() => speakWord(wordToSpeak, direction)} title="Озвучити слово">
                <Volume2 size={24} />
              </button>
            </div>

            <div style={{ marginBottom: '15px' }}>
              <span style={{ color: 'var(--text-muted)' }}>Переклад:</span>
              <p style={{ fontSize: '1.2rem', marginTop: '5px' }}>{displayTranslation}</p>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {isEnUa && entry.uaSynonyms && entry.uaSynonyms.length > 0 && (
                <div>
                  <span style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Синоніми (UA):</span>
                  <p style={{ color: 'var(--accent-color)', marginTop: '2px' }}>{entry.uaSynonyms.join(' • ')}</p>
                </div>
              )}

              {!isEnUa && entry.synonyms && entry.synonyms.length > 0 && (
                <div>
                  <span style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Синоніми (EN):</span>
                  <p style={{ color: 'var(--accent-color)', marginTop: '2px' }}>{entry.synonyms.join(' • ')}</p>
                </div>
              )}
            </div>

            {entry.examples && entry.examples.length > 0 && (
              <div style={{ marginTop: '15px', background: 'rgba(0,0,0,0.2)', padding: '10px 15px', borderRadius: '8px' }}>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem', textTransform: 'uppercase', letterSpacing: '1px' }}>Приклади:</span>
                <ul style={{ margin: '8px 0 0 20px', color: '#e2e8f0', fontSize: '0.95rem', fontStyle: 'italic', display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  {entry.examples.map((ex, i) => <li key={i}>{ex}</li>)}
                </ul>
              </div>
            )}
          </div>
        );
      })}

      <div style={{ marginTop: '30px', textAlign: 'center', borderTop: '1px solid var(--glass-border)', paddingTop: '20px' }}>
        <button className="icon-btn" style={{ fontSize: '0.9rem', display: 'inline-flex', gap: '8px', alignItems: 'center' }} onClick={handleLoadSample}>
          <Database size={16} />
          Завантажити демо-дані
        </button>
      </div>
    </div>
  );
}