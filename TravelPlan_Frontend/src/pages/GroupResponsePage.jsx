import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { motion } from 'framer-motion';
import { 
  MapPin, Calendar, Wallet, Users, Utensils, 
  Sparkles, Loader2, ChevronUp, ChevronDown 
} from 'lucide-react';
import toast from 'react-hot-toast';
import { getGroupTrip, submitGroupResponse } from '../api';
import './PlannerPage.css'; // Reuse existing styles

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

export default function GroupResponsePage() {
  const { tripId } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(true);
  const [activeTrip, setActiveTrip] = useState(null);

  const [form, setForm] = useState({
    city: '',
    days: 3,
    budget: 20000,
    travelers: 1, // Individual response
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
    const fetchTrip = async () => {
      try {
        const { data } = await getGroupTrip(tripId);
        setActiveTrip(data);
        setForm(prev => ({ 
          ...prev, 
          city: data.destination, 
          days: data.days 
        }));
      } catch (err) {
        toast.error('Failed to load trip details');
        navigate('/groups');
      } finally {
        setFetching(false);
      }
    };
    fetchTrip();
  }, [tripId]);

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
    if (!form.travelStyle) { toast.error('Please select your travel style'); return; }
    setLoading(true);
    try {
      const payload = {
        ...form,
        days: Number(form.days),
        budget: Number(form.budget),
      };
      await submitGroupResponse(tripId, payload);
      toast.success('Preferences submitted! ✅');
      navigate(`/groups/${activeTrip.groupId}`);
    } catch (err) {
      const msg = err.response?.data?.error || 'Failed to submit response';
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  if (fetching) return (
    <div className="planner-wrapper" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <Loader2 size={48} className="spin" />
    </div>
  );

  return (
    <div className="planner-wrapper">
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
        >
          <span className="planner-badge">Group Response · {activeTrip.destination}</span>
          <h1>Submit Your <span className="gradient-text">Preferences</span></h1>
          <p>Help the group decide! Your preferences will be aggregated into the final plan.</p>
        </motion.div>

        <form onSubmit={handleSubmit} className="planner-form-full">
          {/* STEP: FIXED TRIP INFO */}
          <motion.section className="form-section highlight-section" initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }}>
            <div className="section-label">Trip Focus</div>
            <div className="fixed-trip-info">
              <div className="info-pill"><MapPin size={16} /> {activeTrip.destination}</div>
              <div className="info-pill"><Calendar size={16} /> {activeTrip.days} Days</div>
              <p className="notice">Destination and duration are fixed for this group trip.</p>
            </div>
          </motion.section>

          {/* STEP 2: TRAVEL STYLE */}
          <motion.section className="form-section" initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }}>
            <div className="section-label"><span className="step-num">1</span> Your Travel Style</div>
            <div className="style-grid">
              {TRAVEL_STYLES.map(s => (
                <button
                  key={s.id}
                  type="button"
                  className={`style-card ${form.travelStyle === s.id ? 'style-card--active' : ''}`}
                  onClick={() => setForm(prev => ({ ...prev, travelStyle: s.id }))}
                >
                  <span className="style-emoji">{s.emoji}</span>
                  <span>{s.label}</span>
                </button>
              ))}
            </div>
          </motion.section>

          {/* STEP 3: PACE */}
          <motion.section className="form-section" initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 }}>
            <div className="section-label"><span className="step-num">2</span> Your Preferred Pace</div>
            <div className="pace-grid">
              {PACES.map(p => (
                <button
                  key={p.id}
                  type="button"
                  className={`pace-card ${form.pace === p.id ? 'pace-card--active' : ''}`}
                  onClick={() => setForm(prev => ({ ...prev, pace: p.id }))}
                >
                  <span className="pace-emoji">{p.emoji}</span>
                  <strong>{p.label}</strong>
                  <span className="pace-desc">{p.desc}</span>
                </button>
              ))}
            </div>
          </motion.section>

          {/* STEP 4: INTERESTS */}
          <motion.section className="form-section" initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3 }}>
            <div className="section-label"><span className="step-num">3</span> What you're interested in</div>
            <div className="interests-grid">
              {INTERESTS.map(i => (
                <button
                  key={i.id}
                  type="button"
                  className={`interest-chip ${form.specialInterests.includes(i.id) ? 'interest-chip--active' : ''}`}
                  onClick={() => toggleInterest(i.id)}
                >
                  {i.emoji} {i.label}
                </button>
              ))}
            </div>
          </motion.section>

          {/* STEP 5: BUDGET & DETAILS */}
          <motion.section className="form-section" initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.4 }}>
            <div className="section-label"><span className="step-num">4</span> Your Budget & Food</div>
            <div className="details-grid">
              <div className="input-group">
                <label><Wallet size={14} /> My Budget for this trip (₹)</label>
                <input className="input-field" type="number" name="budget" value={form.budget} onChange={handleChange} min="1000" step="500" required />
              </div>

              <div className="input-group">
                <label><Utensils size={14} /> Food Preference</label>
                <select className="input-field" name="foodType" value={form.foodType} onChange={handleChange}>
                  <option value="budget">Street Food</option>
                  <option value="standard">Standard</option>
                  <option value="premium">Premium</option>
                </select>
              </div>

              <div className="input-group">
                <label><Utensils size={14} /> Diet</label>
                <select className="input-field" name="dietPreference" value={form.dietPreference} onChange={handleChange}>
                  <option value="both">Both</option>
                  <option value="veg">Veg</option>
                  <option value="nonveg">Non-Veg</option>
                </select>
              </div>
            </div>
          </motion.section>

          <motion.div className="submit-section" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}>
            <button
              type="submit"
              className="submit-btn"
              disabled={loading}
            >
              {loading ? <Loader2 size={20} className="spin" /> : <><Sparkles size={20} /> Submit Response</>}
            </button>
            <button type="button" className="text-btn" onClick={() => navigate(`/groups/${activeTrip.groupId}`)}>
              Cancel
            </button>
          </motion.div>
        </form>
      </div>

      <style>{`
        .highlight-section {
          background: rgba(59, 130, 246, 0.1);
          border: 1px solid rgba(59, 130, 246, 0.2);
          padding: 24px;
          border-radius: 20px;
        }
        .fixed-trip-info {
          display: flex;
          gap: 12px;
          flex-wrap: wrap;
          align-items: center;
          margin-top: 15px;
        }
        .info-pill {
          background: var(--primary);
          color: white;
          padding: 6px 16px;
          border-radius: 50px;
          font-weight: 600;
          display: flex;
          align-items: center;
          gap: 8px;
        }
        .notice {
          font-size: 0.9rem;
          color: var(--text-secondary);
          margin: 0;
          width: 100%;
          margin-top: 10px;
        }
      `}</style>
    </div>
  );
}
