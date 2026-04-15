import { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check localStorage for saved user token
    const token = localStorage.getItem('travelmind_token');
    const savedUser = localStorage.getItem('travelmind_user');
    
    if (token && savedUser) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      try {
        setUser(JSON.parse(savedUser));
      } catch (e) {
        // Auto-login as guest if saved data is corrupted
        const guest = { loggedIn: true, profileComplete: true, name: 'Traveler', email: 'guest@travelmind.app', avatar: '🌍' };
        setUser(guest);
      }
    } else {
      // Auto-login as guest — no registration required
      const guest = { loggedIn: true, profileComplete: true, name: 'Traveler', email: 'guest@travelmind.app', avatar: '🌍' };
      setUser(guest);
      localStorage.setItem('travelmind_user', JSON.stringify(guest));
    }
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    try {
      const res = await axios.post('http://localhost:8080/api/auth/login', credentials);
      const { token, name, email } = res.data;
      
      const userData = { loggedIn: true, profileComplete: true, name, email, avatar: '👤' };
      setUser(userData);
      localStorage.setItem('travelmind_token', token);
      localStorage.setItem('travelmind_user', JSON.stringify(userData));
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      return true;
    } catch (err) {
      console.error("Login failed", err);
      return false;
    }
  };

  const register = async (data) => {
    try {
      const res = await axios.post('http://localhost:8080/api/auth/register', data);
      const { token, name, email } = res.data;
      
      const userData = { loggedIn: true, profileComplete: true, name, email, avatar: '👤' };
      setUser(userData);
      localStorage.setItem('travelmind_token', token);
      localStorage.setItem('travelmind_user', JSON.stringify(userData));
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      return true;
    } catch (err) {
      console.error("Register failed", err);
      return false;
    }
  };

  const completeProfile = (profile) => {
    const updatedUser = { ...user, ...profile, profileComplete: true };
    setUser(updatedUser);
    localStorage.setItem('travelmind_user', JSON.stringify(updatedUser));
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('travelmind_token');
    localStorage.removeItem('travelmind_user');
    delete axios.defaults.headers.common['Authorization'];
  };

  const updateUser = (updates) => {
    const updatedUser = { ...user, ...updates };
    setUser(updatedUser);
    localStorage.setItem('travelmind_user', JSON.stringify(updatedUser));
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, completeProfile, updateUser, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
