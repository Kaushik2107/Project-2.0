import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard, Compass, BarChart3, MapPin, Clock,
  Menu, X, LogOut, User, Sparkles
} from 'lucide-react';
import { useState } from 'react';
import './Navbar.css';

export default function Navbar() {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const [mobileOpen, setMobileOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);

  const links = [
    { to: '/dashboard', label: 'Dashboard', icon: <LayoutDashboard size={16} /> },
    { to: '/plan', label: 'Plan Trip', icon: <Compass size={16} /> },
    { to: '/compare', label: 'Compare', icon: <BarChart3 size={16} /> },
    { to: '/explore', label: 'Explore', icon: <MapPin size={16} /> },
    { to: '/history', label: 'History', icon: <Clock size={16} /> },
    { to: '/motivation', label: 'Motivation', icon: <Sparkles size={16} /> },
  ];

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-inner container">
        <Link to="/dashboard" className="navbar-brand">
          <span className="brand-icon">✈️</span>
          <span className="brand-text">
            Travel<span className="gradient-text">Mind</span>
          </span>
        </Link>

        <div className={`navbar-links ${mobileOpen ? 'open' : ''}`}>
          {links.map((link) => (
            <Link
              key={link.to}
              to={link.to}
              className={`nav-link ${location.pathname === link.to ? 'active' : ''}`}
              onClick={() => setMobileOpen(false)}
            >
              {link.icon}
              {link.label}
            </Link>
          ))}

          {/* Mobile-only logout */}
          <button className="nav-link mobile-logout-btn" onClick={handleLogout}>
            <LogOut size={16} />
            Logout
          </button>
        </div>

        {/* Desktop profile dropdown */}
        <div className="navbar-profile">
          <button
            className="profile-trigger"
            onClick={() => setProfileOpen(!profileOpen)}
            id="navbar-profile-btn"
          >
            <span className="profile-avatar">{user?.avatar || '🧑‍💻'}</span>
            <span className="profile-name">{user?.name || 'User'}</span>
          </button>

          {profileOpen && (
            <>
              <div className="profile-backdrop" onClick={() => setProfileOpen(false)} />
              <div className="profile-dropdown glass-card">
                <div className="dropdown-header">
                  <span className="dropdown-avatar">{user?.avatar || '🧑‍💻'}</span>
                  <div>
                    <strong>{user?.name || 'Traveler'}</strong>
                    <span>{user?.city || ''}</span>
                  </div>
                </div>
                <div className="dropdown-divider" />
                <Link
                  to="/profile"
                  className="dropdown-item"
                  onClick={() => setProfileOpen(false)}
                >
                  <User size={14} />
                  Edit Profile
                </Link>
                <button className="dropdown-item logout-item" onClick={handleLogout}>
                  <LogOut size={14} />
                  Logout
                </button>
              </div>
            </>
          )}
        </div>

        <button className="mobile-toggle" onClick={() => setMobileOpen(!mobileOpen)}>
          {mobileOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>
    </nav>
  );
}
