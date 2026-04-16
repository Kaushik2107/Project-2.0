import { Link } from 'react-router-dom';
import { Sparkles, ArrowRight, Users, Brain, Map, TrendingUp } from 'lucide-react';
import { motion } from 'framer-motion';
import './Hero.css';

export default function Hero() {
  const features = [
    { icon: <Brain size={24} />, title: 'Smart Planning', desc: 'Optimized itineraries for your perfect trip', color: '#6C63FF' },
    { icon: <Users size={24} />, title: 'Group Trip Splitting', desc: 'Cost sharing — hotel rooms, transport pooling', color: '#FF6B9D' },
    { icon: <Map size={24} />, title: 'Proximity Routing', desc: 'Intelligent routing for nearest destinations', color: '#00D4FF' },
    { icon: <TrendingUp size={24} />, title: 'Budget Comparison', desc: 'Compare 3 budget tiers side-by-side', color: '#00E676' },
  ];

  return (
    <div className="hero-wrapper">
      {/* Animated background orbs */}
      <div className="hero-orb orb-1"></div>
      <div className="hero-orb orb-2"></div>
      <div className="hero-orb orb-3"></div>

      <div className="container">
        <motion.div
          className="hero-content"
          initial={{ opacity: 0, y: 40 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, ease: 'easeOut' }}
        >
          <div className="hero-badge">
            <Sparkles size={14} />
            Premium Travel Planner
          </div>

          <h1 className="hero-title">
            Plan Your Perfect Trip with{' '}
            <span className="gradient-text">TravelMind</span>
          </h1>

          <p className="hero-subtitle">
            Smart budget optimization, real restaurant recommendations, proximity-based itineraries,
            group cost splitting, and 14+ premium features — all in one platform.
          </p>

          <div className="hero-actions">
            <Link to="/plan" className="btn btn-primary btn-lg">
              Start Planning <ArrowRight size={18} />
            </Link>
            <Link to="/explore" className="btn btn-secondary btn-lg">
              Explore Cities
            </Link>
          </div>

          <div className="hero-stats">
            <div className="stat">
              <span className="stat-value">14+</span>
              <span className="stat-label">Features</span>
            </div>
            <div className="stat-divider"></div>
            <div className="stat">
              <span className="stat-value">Smart</span>
              <span className="stat-label">Routing</span>
            </div>
            <div className="stat-divider"></div>
            <div className="stat">
              <span className="stat-value">Smart</span>
              <span className="stat-label">Group Splitting</span>
            </div>
          </div>
        </motion.div>

        <motion.div
          className="features-grid"
          initial={{ opacity: 0, y: 60 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.3 }}
        >
          {features.map((f, i) => (
            <div key={i} className="feature-card glass-card">
              <div className="feature-icon" style={{ background: `${f.color}20`, color: f.color }}>
                {f.icon}
              </div>
              <h3>{f.title}</h3>
              <p>{f.desc}</p>
            </div>
          ))}
        </motion.div>
      </div>
    </div>
  );
}
