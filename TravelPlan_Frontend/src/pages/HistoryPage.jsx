import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Clock, MapPin, Wallet, Users, Calendar, Trash2, Loader2, ArrowRight } from 'lucide-react';
import toast from 'react-hot-toast';
import { getTripHistory, deleteHistory } from '../api';
import './HistoryPage.css';

export default function HistoryPage() {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadHistory();
  }, []);

  const loadHistory = async () => {
    try {
      const { data } = await getTripHistory();
      setHistory(data);
    } catch (err) {
      console.error('Failed to load history:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleViewPlan = (trip) => {
    if (!trip.planSummary) {
      toast.error('No detailed plan found for this trip');
      return;
    }
    try {
      const plan = JSON.parse(trip.planSummary);
      const request = {
        city: trip.city,
        budget: trip.budget,
        days: trip.days,
        travelers: trip.travelers,
        foodType: trip.foodType
      };
      navigate('/result', { state: { plan, request } });
    } catch (err) {
      console.error('Failed to parse plan summary:', err);
      toast.error('Error opening plan');
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteHistory(id);
      setHistory(prev => prev.filter(h => h.id !== id));
      toast.success('Trip removed from history');
    } catch (err) {
      toast.error('Failed to delete');
    }
  };

  if (loading) {
    return (
      <div className="page-wrapper container text-center" style={{ paddingTop: '100px' }}>
        <Loader2 size={48} className="spin" style={{ color: 'var(--primary)' }} />
        <p style={{ marginTop: '16px', color: 'var(--text-secondary)' }}>Loading trip history...</p>
      </div>
    );
  }

  return (
    <div className="page-wrapper">
      <div className="container">
        <motion.div className="page-header" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}>
          <h1><span className="gradient-text">Trip History</span> 📜</h1>
          <p>Your past trip plans, saved for reference</p>
        </motion.div>

        {history.length === 0 ? (
          <div className="text-center" style={{ padding: '80px 0' }}>
            <div style={{ fontSize: '64px', marginBottom: '16px' }}>🗺️</div>
            <h3 style={{ marginBottom: '8px' }}>No trips saved yet</h3>
            <p style={{ color: 'var(--text-secondary)' }}>
              Generate a trip plan and it will appear here
            </p>
          </div>
        ) : (
          <div className="history-list">
            {history.map((trip, i) => (
              <motion.div
                key={trip.id}
                className="history-card glass-card clickable-card"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: i * 0.05 }}
                onClick={() => handleViewPlan(trip)}
              >
                <div className="history-header">
                  <div className="history-title">
                    <h3><MapPin size={18} /> {trip.city}</h3>
                    <span className="badge badge-primary">{trip.visitorName || 'Anonymous'}</span>
                  </div>
                  <button className="btn btn-ghost btn-sm" onClick={(e) => { e.stopPropagation(); handleDelete(trip.id); }}>
                    <Trash2 size={16} />
                  </button>
                </div>

                <div className="history-details">
                  <div className="history-stat">
                    <Calendar size={14} />
                    <span>{trip.days} days</span>
                  </div>
                  <div className="history-stat">
                    <Users size={14} />
                    <span>{trip.travelers || 1} travelers</span>
                  </div>
                  <div className="history-stat">
                    <Wallet size={14} />
                    <span>Budget: ₹{trip.budget?.toLocaleString()}</span>
                  </div>
                  <div className="history-stat">
                    <span>Total: ₹{trip.totalCost?.toLocaleString()}</span>
                  </div>
                </div>

                {trip.hotelName && (
                  <div className="history-hotel">🏨 {trip.hotelName}</div>
                )}

                <div className="history-footer">
                  <div className="history-time">
                    <Clock size={12} /> {trip.createdAt ? new Date(trip.createdAt).toLocaleDateString() : 'Unknown date'}
                  </div>
                  <div className="view-plan-hint">
                    View Full Plan <ArrowRight size={14} />
                  </div>
                </div>
              </motion.div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
