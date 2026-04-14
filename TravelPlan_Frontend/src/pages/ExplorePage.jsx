import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { MapPin, Star, TrendingUp, Hotel, Utensils, Loader2 } from 'lucide-react';
import { getTrending } from '../api';
import './ExplorePage.css';

export default function ExplorePage() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTrending();
  }, []);

  const loadTrending = async () => {
    try {
      const { data } = await getTrending();
      setData(data);
    } catch (err) {
      console.error('Failed to load trending data:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="page-wrapper container text-center" style={{ paddingTop: '100px' }}>
        <Loader2 size={48} className="spin" style={{ color: 'var(--primary)' }} />
        <p style={{ marginTop: '16px', color: 'var(--text-secondary)' }}>Loading trending data...</p>
      </div>
    );
  }

  return (
    <div className="page-wrapper">
      <div className="container">
        <motion.div className="page-header" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}>
          <h1><span className="gradient-text">Explore</span> 🌍</h1>
          <p>Discover trending cities, top-rated hotels, and must-visit places</p>
        </motion.div>

        {/* Trending Cities */}
        {data?.trendingCities && data.trendingCities.length > 0 && (
          <motion.div className="section" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }}>
            <div className="section-title"><TrendingUp size={22} /> Trending Cities</div>
            <div className="trending-grid">
              {data.trendingCities.map((city, i) => (
                <div key={i} className="city-card glass-card">
                  <div className="city-rank">{i + 1}</div>
                  <div className="city-info">
                    <h3><MapPin size={16} /> {city.city}</h3>
                    <div className="city-meta">
                      <span>{city.listings} listings</span>
                      {city.avgRating > 0 && <span><Star size={12} fill="#FFD700" color="#FFD700" /> {city.avgRating}</span>}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </motion.div>
        )}

        {/* Top Hotels */}
        {data?.topHotels && data.topHotels.length > 0 && (
          <motion.div className="section" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 }}>
            <div className="section-title"><Hotel size={22} /> Top Rated Hotels</div>
            <div className="top-grid grid-3">
              {data.topHotels.slice(0, 6).map((hotel, i) => (
                <div key={i} className="top-card glass-card">
                  <div className="top-card-header">
                    <h4>{hotel.name}</h4>
                    <span className="badge badge-primary">{hotel.type || 'Hotel'}</span>
                  </div>
                  <div className="top-card-meta">
                    <span><MapPin size={12} /> {hotel.city}</span>
                    <span><Star size={12} fill="#FFD700" color="#FFD700" /> {hotel.rating}</span>
                    <span>₹{hotel.pricePerNight}/night</span>
                  </div>
                </div>
              ))}
            </div>
          </motion.div>
        )}

        {/* Top Places */}
        {data?.topPlaces && data.topPlaces.length > 0 && (
          <motion.div className="section" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3 }}>
            <div className="section-title"><MapPin size={22} /> Top Rated Places</div>
            <div className="top-grid grid-3">
              {data.topPlaces.slice(0, 6).map((place, i) => (
                <div key={i} className="top-card glass-card">
                  <div className="top-card-header">
                    <h4>{place.name}</h4>
                    {place.category && <span className="badge badge-cyan">{place.category}</span>}
                  </div>
                  <div className="top-card-meta">
                    <span><MapPin size={12} /> {place.city}</span>
                    <span><Star size={12} fill="#FFD700" color="#FFD700" /> {place.rating}</span>
                    <span>₹{place.entryFee} entry</span>
                  </div>
                </div>
              ))}
            </div>
          </motion.div>
        )}

        {/* Top Restaurants */}
        {data?.topRestaurants && data.topRestaurants.length > 0 && (
          <motion.div className="section" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.4 }}>
            <div className="section-title"><Utensils size={22} /> Top Rated Restaurants</div>
            <div className="top-grid grid-3">
              {data.topRestaurants.slice(0, 6).map((r, i) => (
                <div key={i} className="top-card glass-card">
                  <div className="top-card-header">
                    <h4>{r.name}</h4>
                    {r.cuisine && <span className="badge badge-orange">{r.cuisine}</span>}
                  </div>
                  <div className="top-card-meta">
                    <span><MapPin size={12} /> {r.city}</span>
                    <span><Star size={12} fill="#FFD700" color="#FFD700" /> {r.rating}</span>
                    <span>₹{r.pricePerMeal}/meal</span>
                  </div>
                </div>
              ))}
            </div>
          </motion.div>
        )}

        {!data?.trendingCities?.length && !data?.topHotels?.length && (
          <div className="text-center" style={{ padding: '60px 0' }}>
            <p style={{ color: 'var(--text-secondary)', fontSize: '18px' }}>
              No data available yet. Add hotels, places, and restaurants to MongoDB to see analytics.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
