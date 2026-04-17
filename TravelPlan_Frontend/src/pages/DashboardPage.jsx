import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { motion } from 'framer-motion';
import {
  Compass, MapPin, Search, Globe, ArrowRight,
  TrendingUp, CalendarDays, Wallet
} from 'lucide-react';
import { getTrending, getAvailableCities, getTripHistory } from '../api';
import './DashboardPage.css';

export default function DashboardPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [trending, setTrending] = useState([]);
  const [cities, setCities] = useState([]);
  const [history, setHistory] = useState([]);
  const [searchCity, setSearchCity] = useState('');

  useEffect(() => {
    getTrending().then(r => setTrending(r.data || [])).catch(() => {});
    getAvailableCities().then(r => setCities(r.data || [])).catch(() => {});
    getTripHistory().then(r => setHistory(r.data?.slice(0, 5) || [])).catch(() => {});
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchCity.trim()) {
      // Pass the typed city to the planner page
      navigate('/plan', { state: { initialCity: searchCity } });
    }
  };

  const quickActions = [
    {
      to: '/plan',
      icon: <Compass size={24} />,
      title: 'Plan a Trip',
      desc: 'Build perfect itineraries instantly',
      gradient: 'linear-gradient(135deg, #6C63FF, #00D4FF)',
      color: '#00D4FF',
    },
    {
      to: '/compare',
      icon: <Wallet size={24} />,
      title: 'Compare Budgets',
      desc: 'Analyze different budget tiers',
      gradient: 'linear-gradient(135deg, #F59E0B, #FBBF24)',
      color: '#F59E0B',
    },
    {
      to: '/explore',
      icon: <Globe size={24} />,
      title: 'Explore India',
      desc: 'Discover beautiful destinations',
      gradient: 'linear-gradient(135deg, #10B981, #34D399)',
      color: '#10B981',
    },
    {
      to: '/history',
      icon: <CalendarDays size={24} />,
      title: 'Trip History',
      desc: 'View your previous adventures',
      gradient: 'linear-gradient(135deg, #EC4899, #F472B6)',
      color: '#EC4899',
    },
  ];

  const fadeUp = (delay = 0) => ({
    initial: { opacity: 0, y: 30 },
    animate: { opacity: 1, y: 0 },
    transition: { duration: 0.6, delay },
  });

  return (
    <div className="dash-wrapper">
      {/* ── HERO SECTION ── */}
      <div className="dash-hero">
        <video 
          className="dash-hero-video" 
          autoPlay 
          loop 
          muted 
          playsInline
          poster="https://images.unsplash.com/photo-1506461883276-59464324f11b?auto=format&fit=crop&q=80&w=2000"
        >
          {/* Fallback to image if video fails or is missing */}
        </video>
        <div className="dash-hero-overlay"></div>
        
        <div className="dash-hero-content">
          <motion.div {...fadeUp(0.1)} className="dash-hero-badge">
            ✨ Premium Travel
          </motion.div>
          <motion.h1 {...fadeUp(0.2)} className="dash-hero-title">
            Where to <span className="gradient-text">next</span>, {user?.name?.split(' ')[0] || 'Traveler'}?
          </motion.h1>
          <motion.p {...fadeUp(0.3)} className="dash-hero-sub">
            Type any city in India and get your perfect itinerary with optimized budgets and routes.
          </motion.p>
          
          <motion.form {...fadeUp(0.4)} className="dash-search-box" onSubmit={handleSearch}>
            <MapPin className="search-icon" size={24} />
            <input 
              type="text" 
              placeholder="E.g., Goa, Manali, Jaipur..." 
              value={searchCity}
              onChange={(e) => setSearchCity(e.target.value)}
              required
            />
            <button type="submit" className="search-btn">
              <Search size={18} /> Plan Now
            </button>
          </motion.form>
        </div>
      </div>

      <div className="dash-main">
        {/* ── QUICK ACTIONS ── */}
        <motion.div className="dash-quick-grid" {...fadeUp(0.5)}>
          {quickActions.map((action, i) => (
            <Link key={i} to={action.to} className="dash-qa-card">
              <div className="dash-qa-icon" style={{ background: `${action.color}15`, color: action.color }}>
                {action.icon}
              </div>
              <div className="dash-qa-info">
                <h3>{action.title}</h3>
                <p>{action.desc}</p>
              </div>
              <ArrowRight className="dash-qa-arrow" size={18} />
              <div className="dash-qa-hover" style={{ background: action.gradient }} />
            </Link>
          ))}
        </motion.div>

        {/* ── TRENDING DESTINATIONS ── */}
        <motion.div className="dash-section" {...fadeUp(0.6)}>
          <div className="dash-section-header">
            <h2><TrendingUp size={22} color="#00D4FF" /> Trending Destinations</h2>
            <Link to="/explore">View all</Link>
          </div>
          <div className="dash-trending-grid">
            {[
              { name: 'Goa', img: 'https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&q=80&w=600' },
              { name: 'Manali', img: 'https://images.unsplash.com/photo-1600947509785-29fb4e7d1362?auto=format&fit=crop&q=80&w=1000' },
              { name: 'Jaipur', img: 'https://images.unsplash.com/photo-1477587458883-47145ed94245?auto=format&fit=crop&q=80&w=600' }
            ].map((city, i) => (
              <div key={i} className="dash-trend-card" onClick={() => navigate('/plan', { state: { initialCity: city.name } })}>
                <img src={city.img} alt={city.name} />
                <div className="dash-trend-overlay">
                  <h3>{city.name}</h3>
                  <span>Plan Trip ➜</span>
                </div>
              </div>
            ))}
          </div>
        </motion.div>

        {/* ── RECENT TRIPS ── */}
        {history.length > 0 && (
          <motion.div className="dash-section" {...fadeUp(0.7)}>
            <div className="dash-section-header">
              <h2><CalendarDays size={22} color="#6C63FF" /> Your Recent Plans</h2>
              <Link to="/history">View history</Link>
            </div>
            <div className="dash-history-list">
              {history.map((trip, i) => (
                <div 
                  key={i} 
                  className="dash-history-item" 
                  onClick={() => {
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
                      console.error('Failed to open history plan:', err);
                    }
                  }}
                >
                  <div className="dash-hist-left">
                    <div className="dash-hist-icon"><MapPin size={18} /></div>
                    <div>
                      <h4>{trip.city || 'Trip'}</h4>
                      <p>{trip.days || '?'} Days • Budget: ₹{(trip.budget || 0).toLocaleString()}</p>
                    </div>
                  </div>
                  <ArrowRight size={16} color="#64748B" />
                </div>
              ))}
            </div>
          </motion.div>
        )}
      </div>
    </div>
  );
}
