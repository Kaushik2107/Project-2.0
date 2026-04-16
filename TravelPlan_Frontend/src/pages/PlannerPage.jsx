import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { MapPin, Calendar, Wallet, Users, Utensils, Sparkles, Loader2, ChevronUp, ChevronDown } from 'lucide-react';
import toast from 'react-hot-toast';
import { generatePlan } from '../api';
import './PlannerPage.css';

const CITIES = [
  {
    id: 'Goa', label: 'Goa', emoji: '🌊',
    tagline: 'Sun, Sand & Seafood',
    tags: ['Beach', 'Nightlife', 'Culture'],
    image: 'https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&q=80&w=800',
    gradient: 'linear-gradient(135deg, #0052D4, #4364F7, #6FB1FC)',
  },
  {
    id: 'Manali', label: 'Manali', emoji: '🏔️',
    tagline: 'Snow, Trek & Serenity',
    tags: ['Mountains', 'Adventure', 'Nature'],
    image: 'https://images.unsplash.com/photo-1600947509785-29fb4e7d1362?auto=format&fit=crop&q=80&w=1000',
    gradient: 'linear-gradient(135deg, #134E5E, #71B280)',
  },
  {
    id: 'Jaipur', label: 'Jaipur', emoji: '🏰',
    tagline: 'Forts, Royalty & Spices',
    tags: ['Heritage', 'Culture', 'Food'],
    image: 'https://images.unsplash.com/photo-1477587458883-47145ed94245?auto=format&fit=crop&q=80&w=800',
    gradient: 'linear-gradient(135deg, #b06ab3, #4568dc)',
  },
];

const TRAVEL_STYLES = [
  { id: 'FAMILY', label: 'Family', emoji: '👨‍👩‍👧‍👦' },
  { id: 'SOLO', label: 'Solo', emoji: '🧍' },
  { id: 'FRIENDS', label: 'Friends', emoji: '👯' },
  { id: 'COUPLE', label: 'Couple', emoji: '💑' },
];

const PACES = [
  { id: 'RELAXED', label: 'Relaxed', desc: '2–3 places/day', emoji: '🌿' },
  { id: 'BALANCED', label: 'Balanced', desc: '3–4 places/day', emoji: '⚖️' },
  { id: 'FAST', label: 'Fast', desc: '5+ places/day', emoji: '⚡' },
];

const INTERESTS = [
  { id: 'FOOD', label: 'Food', emoji: '🍽️' },
  { id: 'NATURE', label: 'Nature', emoji: '🌿' },
  { id: 'SHOPPING', label: 'Shopping', emoji: '🛍️' },
  { id: 'CULTURE', label: 'Culture', emoji: '🏛️' },
  { id: 'ADVENTURE', label: 'Adventure', emoji: '🧗' },
  { id: 'RELAXATION', label: 'Relaxation', emoji: '🧘' },
];

export default function PlannerPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [customCity, setCustomCity] = useState('');
  const initialCity = location.state?.initialCity || '';

  const [form, setForm] = useState({
    city: initialCity,
    days: 3,
    budget: 20000,
    travelers: 2,
    foodType: 'standard',
    dietPreference: 'both',
    cuisinePreference: '',
    visitorName: '',
    travelDate: '',
    manualFoodBudget: 0,
    sourceCity: '',
    travelStyle: '',
    pace: 'BALANCED',
    specialInterests: [],
  });

  useEffect(() => {
    if (initialCity && !CITIES.find(c => c.id.toLowerCase() === initialCity.toLowerCase())) {
      setCustomCity(initialCity);
    } else if (initialCity) {
      const match = CITIES.find(c => c.id.toLowerCase() === initialCity.toLowerCase());
      if (match) setForm(prev => ({ ...prev, city: match.id }));
    }
  }, [initialCity]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const toggleInterest = (id) => {
    setForm(prev => ({
      ...prev,
      specialInterests: prev.specialInterests.includes(id)
        ? prev.specialInterests.filter(i => i !== id)
        : [...prev.specialInterests, id],
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.city) { toast.error('Please select a destination city'); return; }
    if (!form.travelStyle) { toast.error('Please select your travel style'); return; }
    if (form.days <= 0 || form.budget <= 0) { toast.error('Days and budget must be greater than 0'); return; }
    setLoading(true);
    try {
      const payload = {
        ...form,
        days: Number(form.days),
        budget: Number(form.budget),
        travelers: Number(form.travelers),
        manualFoodBudget: form.foodType === 'manual' ? Number(form.manualFoodBudget) : 0,
      };
      const { data } = await generatePlan(payload);
      toast.success('Your dream trip is ready! 🎉');
      navigate('/result', { state: { plan: data, request: payload } });
    } catch (err) {
      const msg = err.response?.data?.error || 'Failed to generate plan';
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  const selectedCity = CITIES.find(c => c.id === form.city);

  return (
    <div className="planner-wrapper">
      {/* Animated background */}
      <div className="planner-bg">
        <div className="planner-bg-orb orb-1" />
        <div className="planner-bg-orb orb-2" />
        <div className="planner-bg-orb orb-3" />
      </div>

      <div className="planner-container">
        <motion.div
          className="planner-header"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          <span className="planner-badge">Smart Trip Planner ✈️</span>
          <h1>Plan Your <span className="gradient-text">Dream Trip</span></h1>
          <p>Tell us about yourself — we'll craft the perfect itinerary</p>
        </motion.div>

        <form onSubmit={handleSubmit} className="planner-form-full">

          {/* ── STEP 1: CHOOSE CITY ── */}
          <motion.section className="form-section" initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }}>
            <div className="section-label"><span className="step-num">1</span> Choose Your Destination</div>
            <div className="city-cards-grid">
              {CITIES.map(city => (
                <motion.div
                  key={city.id}
                  className={`city-card ${form.city === city.id ? 'city-card--selected' : ''}`}
                  onClick={() => setForm(prev => ({ ...prev, city: city.id }))}
                  whileHover={{ y: -6, scale: 1.02 }}
                  whileTap={{ scale: 0.97 }}
                >
                  <img src={city.image} alt={city.label} className="city-card-img" />
                  <div className="city-card-overlay" style={{ background: city.gradient.replace('linear-gradient', 'linear-gradient').replace(')', ', 0.6)') }} />
                  <div className="city-card-content">
                    <div className="city-card-emoji">{city.emoji}</div>
                    <h3>{city.label}</h3>
                    <p>{city.tagline}</p>
                    <div className="city-tags">
                      {city.tags.map(t => <span key={t} className="city-tag">{t}</span>)}
                    </div>
                  </div>
                  {form.city === city.id && (
                    <motion.div className="city-check" initial={{ scale: 0 }} animate={{ scale: 1 }}>✓</motion.div>
                  )}
                </motion.div>
              ))}
            </div>
            <div className="custom-city-input">
              <label>Or type any other city:</label>
              <input
                type="text"
                className="input-field"
                placeholder="E.g. Mumbai, Kerala, Shimla..."
                value={customCity}
                onChange={(e) => {
                  setCustomCity(e.target.value);
                  setForm(prev => ({ ...prev, city: e.target.value }));
                }}
              />
            </div>
          </motion.section>

          {/* ── STEP 2: TRAVEL STYLE ── */}
          <motion.section className="form-section" initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 }}>
            <div className="section-label"><span className="step-num">2</span> Travel Style</div>
            <div className="style-grid">
              {TRAVEL_STYLES.map(s => (
                <motion.button
                  key={s.id}
                  type="button"
                  className={`style-card ${form.travelStyle === s.id ? 'style-card--active' : ''}`}
                  onClick={() => setForm(prev => ({ ...prev, travelStyle: s.id }))}
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                >
                  <span className="style-emoji">{s.emoji}</span>
                  <span>{s.label}</span>
                </motion.button>
              ))}
            </div>
          </motion.section>

          {/* ── STEP 3: PACE ── */}
          <motion.section className="form-section" initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.25 }}>
            <div className="section-label"><span className="step-num">3</span> Preferred Pace</div>
            <div className="pace-grid">
              {PACES.map(p => (
                <motion.button
                  key={p.id}
                  type="button"
                  className={`pace-card ${form.pace === p.id ? 'pace-card--active' : ''}`}
                  onClick={() => setForm(prev => ({ ...prev, pace: p.id }))}
                  whileHover={{ scale: 1.03 }}
                  whileTap={{ scale: 0.97 }}
                >
                  <span className="pace-emoji">{p.emoji}</span>
                  <strong>{p.label}</strong>
                  <span className="pace-desc">{p.desc}</span>
                </motion.button>
              ))}
            </div>
          </motion.section>

          {/* ── STEP 4: INTERESTS ── */}
          <motion.section className="form-section" initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3 }}>
            <div className="section-label"><span className="step-num">4</span> Special Interests <span className="optional-tag">optional</span></div>
            <div className="interests-grid">
              {INTERESTS.map(i => (
                <motion.button
                  key={i.id}
                  type="button"
                  className={`interest-chip ${form.specialInterests.includes(i.id) ? 'interest-chip--active' : ''}`}
                  onClick={() => toggleInterest(i.id)}
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                >
                  {i.emoji} {i.label}
                </motion.button>
              ))}
            </div>
          </motion.section>

          {/* ── STEP 5: TRIP DETAILS ── */}
          <motion.section className="form-section" initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.35 }}>
            <div className="section-label"><span className="step-num">5</span> Trip Details</div>
            <div className="details-grid">

              <div className="input-group">
                <label><Calendar size={14} /> Number of Days</label>
                <div className="stepper">
                  <button type="button" onClick={() => setForm(p => ({ ...p, days: Math.max(1, p.days - 1) }))}><ChevronDown size={16} /></button>
                  <span>{form.days}</span>
                  <button type="button" onClick={() => setForm(p => ({ ...p, days: Math.min(30, p.days + 1) }))}><ChevronUp size={16} /></button>
                </div>
              </div>

              <div className="input-group">
                <label><Users size={14} /> Travelers</label>
                <div className="stepper">
                  <button type="button" onClick={() => setForm(p => ({ ...p, travelers: Math.max(1, p.travelers - 1) }))}><ChevronDown size={16} /></button>
                  <span>{form.travelers}</span>
                  <button type="button" onClick={() => setForm(p => ({ ...p, travelers: Math.min(20, p.travelers + 1) }))}><ChevronUp size={16} /></button>
                </div>
              </div>

              <div className="input-group">
                <label><Wallet size={14} /> Total Budget (₹)</label>
                <input className="input-field" type="number" name="budget" value={form.budget} onChange={handleChange} min="1000" step="500" required />
                <div className="budget-presets">
                  {[10000, 25000, 50000, 100000].map(b => (
                    <button key={b} type="button" className={`preset-btn ${form.budget === b ? 'active' : ''}`} onClick={() => setForm(p => ({ ...p, budget: b }))}>
                      ₹{b >= 1000 ? `${b / 1000}K` : b}
                    </button>
                  ))}
                </div>
              </div>

              <div className="input-group">
                <label><Utensils size={14} /> Food Preference</label>
                <select className="input-field" name="foodType" value={form.foodType} onChange={handleChange}>
                  <option value="budget">Street Food (₹200/day)</option>
                  <option value="standard">Standard (₹400/day)</option>
                  <option value="premium">Premium (₹800/day)</option>
                  <option value="luxury">Luxury (₹1500/day)</option>
                </select>
              </div>

              <div className="input-group">
                <label><Utensils size={14} /> Diet Preference</label>
                <select className="input-field" name="dietPreference" value={form.dietPreference} onChange={handleChange}>
                  <option value="both">All (Veg + Non-Veg)</option>
                  <option value="veg">Vegetarian Only</option>
                  <option value="nonveg">Non-Vegetarian</option>
                </select>
              </div>

              <div className="input-group">
                <label><Calendar size={14} /> Travel Date</label>
                <input className="input-field" type="date" name="travelDate" value={form.travelDate} onChange={handleChange} />
              </div>

              <div className="input-group">
                <label><MapPin size={14} /> Source City <span className="optional-tag">optional</span></label>
                <input className="input-field" name="sourceCity" value={form.sourceCity} onChange={handleChange} placeholder="e.g., Ahmedabad" />
              </div>

              <div className="input-group">
                <label><Users size={14} /> Your Name <span className="optional-tag">optional</span></label>
                <input className="input-field" name="visitorName" value={form.visitorName} onChange={handleChange} placeholder="For trip history" />
              </div>
            </div>
          </motion.section>

          {/* ── SUBMIT ── */}
          <motion.div className="submit-section" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.45 }}>
            <div className="trip-preview">
              <span>{selectedCity ? selectedCity.emoji : '🌍'}</span>
              <span><strong>{form.days} days</strong> in <strong>{form.city || 'your destination'}</strong> for <strong>{form.travelers} traveler{form.travelers > 1 ? 's' : ''}</strong> · Budget: <strong>₹{Number(form.budget).toLocaleString()}</strong></span>
            </div>

            <motion.button
              type="submit"
              className="submit-btn"
              disabled={loading}
              whileHover={{ scale: 1.03 }}
              whileTap={{ scale: 0.97 }}
            >
              {loading ? (
                <><Loader2 size={20} className="spin" /> Crafting Your Perfect Journey...</>
              ) : (
                <><Sparkles size={20} /> Generate My Itinerary</>
              )}
            </motion.button>

          </motion.div>

        </form>
      </div>
    </div>
  );
}
