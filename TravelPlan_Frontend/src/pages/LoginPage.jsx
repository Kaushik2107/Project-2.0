import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { motion, AnimatePresence } from 'framer-motion';
import { Mail, Lock, User, ArrowRight, Sparkles, Shield, Eye, EyeOff } from 'lucide-react';
import './LoginPage.css';

export default function LoginPage() {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({ name: '', email: '', password: '' });
  const [error, setError] = useState('');
  const [showPwd, setShowPwd] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { login, register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    let success = false;
    if (isLogin) {
      success = await login({ email: formData.email, password: formData.password });
    } else {
      success = await register(formData);
    }

    if (success) {
      navigate('/profile');
    } else {
      setError(isLogin ? 'Invalid credentials. Please try again.' : 'Registration failed. Email might exist.');
      setIsLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError('');
  }

  return (
    <div className="login-wrapper">
      <div className="login-bg">
        <div className="login-orb orb-a"></div>
        <div className="login-orb orb-b"></div>
        <div className="login-orb orb-c"></div>
        <div className="login-grid-overlay"></div>
      </div>

      <div className="login-particles">
        {[...Array(20)].map((_, i) => (
          <div key={i} className="particle" style={{ left: `${Math.random() * 100}%`, top: `${Math.random() * 100}%`, animationDelay: `${Math.random() * 5}s`, animationDuration: `${3 + Math.random() * 4}s` }} />
        ))}
      </div>

      <motion.div className="login-container" initial={{ opacity: 0, y: 30, scale: 0.95 }} animate={{ opacity: 1, y: 0, scale: 1 }} transition={{ duration: 0.7, ease: [0.16, 1, 0.3, 1] }}>
        <div className="login-brand">
          <motion.div className="login-logo" animate={{ rotate: [0, 5, -5, 0] }} transition={{ duration: 4, repeat: Infinity, ease: 'easeInOut' }}>✈️</motion.div>
          <h1 className="login-title">Travel<span className="gradient-text">Mind</span></h1>
          <p className="login-subtitle">Your Personal Travel Companion</p>
        </div>

        <div className="login-card glass-card">
          <div className="login-card-header">
            <div className="login-icon-ring"><Shield size={24} /></div>
            <h2>{isLogin ? 'Welcome Back' : 'Create Account'}</h2>
            <p>{isLogin ? 'Enter your details to access your trips' : 'Join TravelMind today'}</p>
          </div>

          <form onSubmit={handleSubmit} className="login-form">
            {!isLogin && (
              <div className="login-input-wrapper">
                <div className="login-input-icon"><User size={18} /></div>
                <input name="name" type="text" value={formData.name} onChange={handleChange} placeholder="Full Name" className="login-input" required={!isLogin} />
              </div>
            )}

            <div className="login-input-wrapper">
              <div className="login-input-icon"><Mail size={18} /></div>
              <input name="email" type="email" value={formData.email} onChange={handleChange} placeholder="Email address" className="login-input" required />
            </div>

            <div className="login-input-wrapper">
              <div className="login-input-icon"><Lock size={18} /></div>
              <input name="password" type={showPwd ? 'text' : 'password'} value={formData.password} onChange={handleChange} placeholder="Password" className="login-input" required />
              <button type="button" className="login-toggle-vis" onClick={() => setShowPwd(!showPwd)}>{showPwd ? <EyeOff size={16} /> : <Eye size={16} />}</button>
            </div>

            <AnimatePresence>
              {error && (
                <motion.div className="login-error" initial={{ opacity: 0, y: -10, height: 0 }} animate={{ opacity: 1, y: 0, height: 'auto' }} exit={{ opacity: 0, y: -10, height: 0 }}>
                  {error}
                </motion.div>
              )}
            </AnimatePresence>

            <button id="login-submit-btn" type="submit" className={`btn btn-primary btn-lg login-btn ${isLoading ? 'loading' : ''}`} disabled={isLoading || (!formData.email || !formData.password)}>
              {isLoading ? <div className="login-spinner" /> : <>{isLogin ? 'Login' : 'Register'} <ArrowRight size={18} /></>}
            </button>
          </form>

          <div style={{ textAlign: 'center', marginTop: '16px', color: 'var(--text-light)', fontSize: '14px' }}>
            {isLogin ? "Don't have an account? " : "Already have an account? "}
            <span style={{ color: 'var(--accent-orange)', cursor: 'pointer', fontWeight: 'bold' }} onClick={() => { setIsLogin(!isLogin); setError(''); }}>
              {isLogin ? "Sign Up" : "Log In"}
            </span>
          </div>

        </div>
      </motion.div>
    </div>
  );
}
