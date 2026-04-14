import { useState } from 'react';
import { motion } from 'framer-motion';
import { BarChart3, MapPin, Calendar, Wallet, Users, Loader2, ArrowRight } from 'lucide-react';
import toast from 'react-hot-toast';
import { compareBudgets } from '../api';
import './ComparePage.css';

export default function ComparePage() {
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);

  const [form, setForm] = useState({
    city: '',
    days: 3,
    budget1: 8000,
    budget2: 15000,
    budget3: 25000,
    foodType: 'standard',
    travelers: 1,
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.city.trim()) { toast.error('City is required'); return; }

    setLoading(true);
    try {
      const payload = {
        city: form.city,
        days: Number(form.days),
        budgets: [Number(form.budget1), Number(form.budget2), Number(form.budget3)],
        foodType: form.foodType,
        travelers: Number(form.travelers),
      };
      const { data } = await compareBudgets(payload);
      setResult(data);
      toast.success('Comparison ready!');
    } catch (err) {
      toast.error(err.response?.data?.error || 'Comparison failed');
    } finally {
      setLoading(false);
    }
  };

  const getTierLabel = (i) => ['Budget', 'Standard', 'Premium'][i] || `Tier ${i + 1}`;
  const getTierColor = (i) => ['#FF8C42', '#6C63FF', '#00E676'][i] || '#6C63FF';

  return (
    <div className="page-wrapper">
      <div className="container">
        <motion.div className="page-header" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}>
          <h1><span className="gradient-text">Budget Comparison</span> 📊</h1>
          <p>Compare 3 budget levels side-by-side to find the best value</p>
        </motion.div>

        <motion.form className="compare-form glass-card" onSubmit={handleSubmit} initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 }}>
          <div className="form-row grid-3">
            <div className="input-group">
              <label><MapPin size={14} /> City</label>
              <input className="input-field" name="city" value={form.city} onChange={handleChange} placeholder="e.g., Goa" required />
            </div>
            <div className="input-group">
              <label><Calendar size={14} /> Days</label>
              <input className="input-field" type="number" name="days" value={form.days} onChange={handleChange} min="1" />
            </div>
            <div className="input-group">
              <label><Users size={14} /> Travelers</label>
              <input className="input-field" type="number" name="travelers" value={form.travelers} onChange={handleChange} min="1" />
            </div>
          </div>

          <div className="form-row grid-3">
            <div className="input-group">
              <label style={{ color: '#FF8C42' }}>💰 Budget Tier (₹)</label>
              <input className="input-field" type="number" name="budget1" value={form.budget1} onChange={handleChange} />
            </div>
            <div className="input-group">
              <label style={{ color: '#6C63FF' }}>💎 Standard Tier (₹)</label>
              <input className="input-field" type="number" name="budget2" value={form.budget2} onChange={handleChange} />
            </div>
            <div className="input-group">
              <label style={{ color: '#00E676' }}>👑 Premium Tier (₹)</label>
              <input className="input-field" type="number" name="budget3" value={form.budget3} onChange={handleChange} />
            </div>
          </div>

          <button type="submit" className="btn btn-primary btn-lg submit-btn" disabled={loading}>
            {loading ? <><Loader2 size={18} className="spin" /> Comparing...</> : <><BarChart3 size={18} /> Compare Budgets</>}
          </button>
        </motion.form>

        {result && result.plans && (
          <motion.div className="compare-results" initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }}>
            {result.recommendation && (
              <div className="recommendation glass-card">
                <p>{result.recommendation}</p>
              </div>
            )}

            <div className="compare-grid grid-3">
              {result.plans.map((plan, i) => (
                <div key={i} className="compare-card glass-card" style={{ borderTop: `3px solid ${getTierColor(i)}` }}>
                  <div className="compare-tier" style={{ color: getTierColor(i) }}>{getTierLabel(i)}</div>
                  <div className="compare-price">₹{plan.totalCost?.toLocaleString()}</div>
                  {plan.perPersonCost && <div className="compare-pp">₹{plan.perPersonCost?.toLocaleString()}/person</div>}
                  <div className="compare-details">
                    <div className="compare-row"><span>🏨 Hotel</span><strong>{plan.hotel?.name || 'N/A'}</strong></div>
                    <div className="compare-row"><span>⭐ Rating</span><strong>{plan.hotel?.rating || '-'}</strong></div>
                    <div className="compare-row"><span>📍 Places</span><strong>{plan.places?.length || 0}</strong></div>
                    <div className="compare-row"><span>🍽️ Food</span><strong>₹{plan.foodCost?.toLocaleString()}</strong></div>
                    <div className="compare-row"><span>🚗 Travel</span><strong>₹{plan.travelCost?.toLocaleString()}</strong></div>
                    {plan.tripScore && (
                      <div className="compare-row"><span>🏆 Score</span><strong style={{ color: getTierColor(i) }}>{plan.tripScore.overallScore}/100</strong></div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </motion.div>
        )}
      </div>
    </div>
  );
}
