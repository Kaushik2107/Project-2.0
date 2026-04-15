import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { motion } from 'framer-motion';
import {
  User, MapPin, Wallet, Heart, ArrowRight, Camera,
  Plane, Mountain, Building2, Palmtree, Utensils, ShoppingBag
} from 'lucide-react';
import './ProfilePage.css';

const TRAVEL_STYLES = [
  { id: 'adventure', label: 'Adventure', icon: <Mountain size={20} />, color: '#FF6B9D' },
  { id: 'cultural', label: 'Cultural', icon: <Building2 size={20} />, color: '#6C63FF' },
  { id: 'relaxation', label: 'Relaxation', icon: <Palmtree size={20} />, color: '#00E676' },
  { id: 'foodie', label: 'Foodie', icon: <Utensils size={20} />, color: '#FF8C42' },
  { id: 'luxury', label: 'Luxury', icon: <ShoppingBag size={20} />, color: '#FFD700' },
  { id: 'backpacker', label: 'Backpacker', icon: <Plane size={20} />, color: '#00D4FF' },
];

const AVATARS = ['🧑‍💻', '👨‍🎓', '🧑‍🚀', '🧑‍🎨', '🧑‍💼', '🦸', '🧙', '🧑‍🔬'];

export default function ProfilePage() {
  const { completeProfile, user } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: user?.name || '',
    city: user?.city || '',
    budget: user?.budget || 'medium',
    travelStyles: user?.travelStyles || [],
    avatar: user?.avatar || '🧑‍💻',
  });

  const [step, setStep] = useState(1);

  const handleStyleToggle = (id) => {
    setForm((prev) => ({
      ...prev,
      travelStyles: prev.travelStyles.includes(id)
        ? prev.travelStyles.filter((s) => s !== id)
        : [...prev.travelStyles, id],
    }));
  };

  const handleSubmit = () => {
    completeProfile(form);
    navigate('/dashboard');
  };

  const canProceed = () => {
    if (step === 1) return form.name.trim() && form.city.trim();
    if (step === 2) return form.travelStyles.length > 0;
    return true;
  };

  return (
    <div className="profile-wrapper">
      <div className="profile-bg">
        <div className="profile-orb p-orb-1"></div>
        <div className="profile-orb p-orb-2"></div>
      </div>

      <motion.div
        className="profile-container"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
      >
        {/* Progress indicator */}
        <div className="profile-progress">
          {[1, 2, 3].map((s) => (
            <div key={s} className={`progress-step ${step >= s ? 'active' : ''} ${step === s ? 'current' : ''}`}>
              <div className="progress-dot">{s}</div>
              <span>{s === 1 ? 'Basics' : s === 2 ? 'Style' : 'Avatar'}</span>
            </div>
          ))}
          <div className="progress-line">
            <div className="progress-fill" style={{ width: `${((step - 1) / 2) * 100}%` }} />
          </div>
        </div>

        {/* Card */}
        <div className="profile-card glass-card">
          {/* Step 1: Basic Info */}
          {step === 1 && (
            <motion.div
              key="step1"
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -20 }}
              className="profile-step"
            >
              <div className="step-header">
                <div className="step-icon-ring">
                  <User size={24} />
                </div>
                <h2>Tell us about yourself</h2>
                <p>Let's personalize your travel experience</p>
              </div>

              <div className="profile-form">
                <div className="input-group">
                  <label htmlFor="profile-name">Your Name</label>
                  <div className="input-with-icon">
                    <User size={16} />
                    <input
                      id="profile-name"
                      type="text"
                      className="input-field"
                      placeholder="Enter your name"
                      value={form.name}
                      onChange={(e) => setForm({ ...form, name: e.target.value })}
                    />
                  </div>
                </div>

                <div className="input-group">
                  <label htmlFor="profile-city">Home City</label>
                  <div className="input-with-icon">
                    <MapPin size={16} />
                    <input
                      id="profile-city"
                      type="text"
                      className="input-field"
                      placeholder="Where are you from?"
                      value={form.city}
                      onChange={(e) => setForm({ ...form, city: e.target.value })}
                    />
                  </div>
                </div>

                <div className="input-group">
                  <label>Budget Preference</label>
                  <div className="budget-options">
                    {[
                      { id: 'budget', label: 'Budget', emoji: '💰', desc: 'Smart saver' },
                      { id: 'medium', label: 'Medium', emoji: '💎', desc: 'Best value' },
                      { id: 'luxury', label: 'Luxury', emoji: '👑', desc: 'No limits' },
                    ].map((b) => (
                      <button
                        key={b.id}
                        type="button"
                        className={`budget-option ${form.budget === b.id ? 'selected' : ''}`}
                        onClick={() => setForm({ ...form, budget: b.id })}
                      >
                        <span className="budget-emoji">{b.emoji}</span>
                        <span className="budget-label">{b.label}</span>
                        <span className="budget-desc">{b.desc}</span>
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            </motion.div>
          )}

          {/* Step 2: Travel Style */}
          {step === 2 && (
            <motion.div
              key="step2"
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -20 }}
              className="profile-step"
            >
              <div className="step-header">
                <div className="step-icon-ring" style={{ background: 'linear-gradient(135deg, rgba(255, 107, 157, 0.2), rgba(255, 140, 66, 0.1))' }}>
                  <Heart size={24} />
                </div>
                <h2>Your Travel Style</h2>
                <p>Select one or more styles that match you</p>
              </div>

              <div className="style-grid">
                {TRAVEL_STYLES.map((style) => (
                  <button
                    key={style.id}
                    type="button"
                    className={`style-card ${form.travelStyles.includes(style.id) ? 'selected' : ''}`}
                    onClick={() => handleStyleToggle(style.id)}
                    style={{ '--style-color': style.color }}
                  >
                    <div className="style-icon">{style.icon}</div>
                    <span className="style-label">{style.label}</span>
                    <div className="style-check">✓</div>
                  </button>
                ))}
              </div>
            </motion.div>
          )}

          {/* Step 3: Avatar */}
          {step === 3 && (
            <motion.div
              key="step3"
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -20 }}
              className="profile-step"
            >
              <div className="step-header">
                <div className="step-icon-ring" style={{ background: 'linear-gradient(135deg, rgba(0, 212, 255, 0.2), rgba(0, 230, 118, 0.1))' }}>
                  <Camera size={24} />
                </div>
                <h2>Choose Your Avatar</h2>
                <p>Pick an avatar that represents you</p>
              </div>

              <div className="avatar-preview">
                <motion.div
                  className="avatar-large"
                  key={form.avatar}
                  initial={{ scale: 0.5, opacity: 0 }}
                  animate={{ scale: 1, opacity: 1 }}
                  transition={{ type: 'spring', stiffness: 300 }}
                >
                  {form.avatar}
                </motion.div>
                <h3>{form.name || 'Traveler'}</h3>
                <p>{form.city || 'Explorer'}</p>
              </div>

              <div className="avatar-grid">
                {AVATARS.map((av) => (
                  <button
                    key={av}
                    type="button"
                    className={`avatar-option ${form.avatar === av ? 'selected' : ''}`}
                    onClick={() => setForm({ ...form, avatar: av })}
                  >
                    {av}
                  </button>
                ))}
              </div>
            </motion.div>
          )}

          {/* Navigation buttons */}
          <div className="profile-actions">
            {step > 1 && (
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setStep(step - 1)}
              >
                Back
              </button>
            )}
            <div style={{ flex: 1 }} />
            {step < 3 ? (
              <button
                id="profile-next-btn"
                type="button"
                className="btn btn-primary"
                disabled={!canProceed()}
                onClick={() => setStep(step + 1)}
              >
                Continue <ArrowRight size={16} />
              </button>
            ) : (
              <button
                id="profile-complete-btn"
                type="button"
                className="btn btn-primary btn-lg"
                onClick={handleSubmit}
              >
                Launch Dashboard 🚀
              </button>
            )}
          </div>
        </div>
      </motion.div>
    </div>
  );
}
