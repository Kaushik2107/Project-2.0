import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Compass, Sparkles, Map, Mountain } from 'lucide-react';
import './MotivationPage.css';

const MOTIVATIONS = [
  {
    quote: "Adventure awaits. The world is yours to explore.",
    author: "TravelMind",
    image: "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&q=80&w=2000",
    icon: <Mountain size={48} />
  },
  {
    quote: "Travel is the only thing you buy that makes you richer.",
    author: "Anonymous",
    image: "https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?auto=format&fit=crop&q=80&w=2000",
    icon: <Compass size={48} />
  },
  {
    quote: "Don't listen to what they say. Go see.",
    author: "Chinese Proverb",
    image: "https://images.unsplash.com/photo-1504280741564-faacb95c39b3?auto=format&fit=crop&q=80&w=2000",
    icon: <Sparkles size={48} />
  },
  {
    quote: "To live will be an awfully big adventure.",
    author: "J.M. Barrie",
    image: "https://images.unsplash.com/photo-1452421822248-d4c2b47f0c81?auto=format&fit=crop&q=80&w=2000",
    icon: <Map size={48} />
  }
];

export default function MotivationPage() {
  const [currentIndex, setCurrentIndex] = useState(0);
  const navigate = useNavigate();

  // Rotate quotes every 8 seconds
  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % MOTIVATIONS.length);
    }, 8000);
    return () => clearInterval(timer);
  }, []);

  const current = MOTIVATIONS[currentIndex];

  return (
    <div className="motivation-container">
      {/* Background Image Setup */}
      <AnimatePresence mode="popLayout">
        <motion.div
          key={current.image}
          className="motivation-bg"
          initial={{ opacity: 0, scale: 1.05 }}
          animate={{ opacity: 1, scale: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 1.5, ease: "easeInOut" }}
          style={{ backgroundImage: `url(${current.image})` }}
        />
      </AnimatePresence>
      <div className="motivation-overlay" />

      {/* Floating particles/orbs in background purely for dynamic feel */}
      <div className="mot-particles">
        <div className="mot-orb orb-a"></div>
        <div className="mot-orb orb-b"></div>
      </div>

      <div className="motivation-content">
        <AnimatePresence mode="wait">
          <motion.div
            key={current.quote}
            className="quote-block"
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -30 }}
            transition={{ duration: 0.8, ease: "easeOut" }}
          >
            <div className="quote-icon">
              {current.icon}
            </div>
            
            <h1 className="quote-text">
              &quot;{current.quote}&quot;
            </h1>
            <p className="quote-author">— {current.author}</p>
          </motion.div>
        </AnimatePresence>

        <motion.button 
          className="motivation-cta"
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={() => navigate('/plan')}
        >
          <span>Plan Your Dream Trip Now</span>
          <Compass size={20} />
        </motion.button>
      </div>
      
      {/* Dots Indicator */}
      <div className="motivation-dots">
        {MOTIVATIONS.map((_, idx) => (
          <button
            key={idx}
            className={`mot-dot ${idx === currentIndex ? 'active' : ''}`}
            onClick={() => setCurrentIndex(idx)}
          />
        ))}
      </div>
    </div>
  );
}
