import { useState } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Hotel, MapPin, Utensils, Car, Star, Calendar, TrendingUp,
  Trophy, Users, Wallet, ArrowLeft, Lightbulb, Clock,
  Gauge, Target, Leaf, CircleDollarSign, X, ChevronDown, ChevronUp,
  Sunrise, Sun, Sunset, Moon, Coffee, UtensilsCrossed
} from 'lucide-react';
import './ResultPage.css';

// ── City Hero Images ──
const CITY_HEROES = {
  goa: { img: 'https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&q=80&w=1800', gradient: 'linear-gradient(135deg, #0052D4 0%, #4364F7 50%, #6FB1FC 100%)' },
  manali: { img: 'https://images.unsplash.com/photo-1558222378-5a7ecb9308be?auto=format&fit=crop&q=80&w=1800', gradient: 'linear-gradient(135deg, #134E5E 0%, #71B280 100%)' },
  jaipur: { img: 'https://images.unsplash.com/photo-1477587458883-47145ed94245?auto=format&fit=crop&q=80&w=1800', gradient: 'linear-gradient(135deg, #b06ab3 0%, #4568dc 100%)' },
};

const TIMELINE_ICONS = {
  'Breakfast': <Coffee size={16} />,
  'Travel': <Car size={16} />,
  'Visit': <MapPin size={16} />,
  'Lunch': <UtensilsCrossed size={16} />,
  'Dinner': <UtensilsCrossed size={16} />,
  'Stay': <Hotel size={16} />,
};

const SLOT_COLORS = {
  'Breakfast': '#F59E0B',
  'Travel': '#6B7280',
  'Visit': '#6C63FF',
  'Lunch': '#10B981',
  'Dinner': '#EF4444',
  'Stay': '#8B5CF6',
};

const getIcon = (t) => TIMELINE_ICONS[t] || <MapPin size={16} />;
const getColor = (t) => SLOT_COLORS[t] || '#6C63FF';

export default function ResultPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const plan = location.state?.plan;
  const request = location.state?.request;
  const [activePhoto, setActivePhoto] = useState(null);
  const [expandedDay, setExpandedDay] = useState(0); // index of expanded day

  if (!plan) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '60vh', gap: 16 }}>
        <h2 style={{ color: '#F8FAFC' }}>No trip plan found</h2>
        <p style={{ color: '#94A3B8' }}>Generate a plan first to see results</p>
        <Link to="/plan" className="rp-btn-primary">Plan a Trip ✈️</Link>
      </div>
    );
  }

  const score = plan.tripScore;
  const bb = plan.budgetBreakdown;
  const gcb = plan.groupCostBreakdown;
  const cityKey = request?.city?.toLowerCase() || 'goa';
  const hero = plan.cityImageUrl ? { img: plan.cityImageUrl, gradient: 'linear-gradient(135deg, rgba(0,0,0,0.4) 0%, rgba(0,0,0,0.6) 100%)' } : (CITY_HEROES[cityKey] || CITY_HEROES.goa);

  const getScoreColor = (val) => {
    if (val >= 85) return '#00E676';
    if (val >= 70) return '#FFD700';
    if (val >= 50) return '#FF8C42';
    return '#FF6B9D';
  };

  const days = plan.dayWisePlanDetailed || [];

  return (
    <div className="rp-wrapper">

      {/* ══════════════ HERO ══════════════ */}
      <div className="rp-hero" style={{ backgroundImage: `url(${hero.img})` }}>
        <div className="rp-hero-overlay" style={{ background: hero.gradient.replace(')', ', 0.72)').replace('linear-gradient(', 'linear-gradient(') }} />
        <div className="rp-hero-content">
          <motion.button className="rp-back-btn" onClick={() => navigate('/plan')} initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
            <ArrowLeft size={16} /> Back
          </motion.button>
          <motion.div initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6 }}>
            <div className="rp-hero-badges">
              {plan.seasonInfo && <span className="rp-badge rp-badge-season">🌤 {plan.seasonInfo}</span>}
              {plan.tripVibe && <span className="rp-badge rp-badge-vibe">✨ {plan.tripVibe}</span>}
              {request?.travelStyle && <span className="rp-badge rp-badge-style">🎯 {request.travelStyle}</span>}
            </div>
            <h1 className="rp-hero-title">
              Your {request?.days}-Day <span>{request?.city}</span> Journey
            </h1>
            <div className="rp-hero-meta">
              <span><Calendar size={15} /> {request?.travelDate || 'Flexible Date'}</span>
              <span><Users size={15} /> {plan.travelers || 1} Traveler{plan.travelers > 1 ? 's' : ''}</span>
              <span><Wallet size={15} /> Budget: ₹{Number(request?.budget).toLocaleString()}</span>
              {plan.weatherForecast && <span>🌡️ {plan.weatherForecast}</span>}
            </div>
          </motion.div>
          {score && (
            <motion.div
              className="rp-score-ring"
              style={{ background: `conic-gradient(${getScoreColor(score.overallScore)} ${score.overallScore * 3.6}deg, rgba(255,255,255,0.12) 0deg)` }}
              initial={{ opacity: 0, scale: 0.5 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.4 }}
            >
              <div className="rp-score-inner">
                <span className="rp-score-val" style={{ color: getScoreColor(score.overallScore) }}>{score.overallScore}</span>
                <span className="rp-score-label">Score</span>
              </div>
            </motion.div>
          )}
        </div>
      </div>

      {/* ══════════════ COST STRIP ══════════════ */}
      <motion.div className="rp-cost-strip" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3 }}>
        {[
          { icon: <Wallet size={22} />, label: 'Total Cost', val: plan.totalCost, color: '#6C63FF' },
          { icon: <CircleDollarSign size={22} />, label: 'Per Person', val: plan.perPersonCost, color: '#00D4FF' },
          { icon: <Hotel size={22} />, label: 'Hotel', val: plan.hotelCost, color: '#00E676' },
          { icon: <Utensils size={22} />, label: 'Food', val: plan.foodCost, color: '#FF6B9D' },
          { icon: <Car size={22} />, label: 'Transport', val: plan.transportCost || plan.travelCost, color: '#FFD700' },
          { icon: <MapPin size={22} />, label: 'Activities', val: plan.placesCost, color: '#FF8C42' },
        ].map((item, i) => (
          <div key={i} className="rp-cost-card">
            <div className="rp-cost-icon" style={{ color: item.color, background: `${item.color}18` }}>{item.icon}</div>
            <div>
              <div className="rp-cost-label">{item.label}</div>
              <div className="rp-cost-val" style={{ color: item.color }}>₹{(item.val || 0).toLocaleString()}</div>
            </div>
          </div>
        ))}
      </motion.div>

      <div className="rp-main">

        {/* ══════════════ ARRIVAL BANNER ══════════════ */}
        {plan.arrivalDetails && (
          <motion.div className="rp-arrival-banner" initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.35 }}>
            <span className="rp-arrival-icon">🚀</span>
            <div>
              <strong>Journey Begins</strong>
              <p>{plan.arrivalDetails}</p>
            </div>
          </motion.div>
        )}

        {/* ══════════════ HOTEL ══════════════ */}
        {plan.hotel && (
          <motion.div className="rp-section" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.4 }}>
            <div className="rp-section-title"><Hotel size={20} /> Your Accommodation</div>
            <div
              className="rp-hotel-card"
              onClick={() => setActivePhoto({ url: plan.hotel.imageUrl, title: plan.hotel.name, type: 'Hotel' })}
            >
              <div className="rp-hotel-img-wrap">
                <img
                  src={plan.hotel.imageUrl}
                  alt={plan.hotel.name}
                  onError={e => { e.target.src = 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&q=80&w=800'; }}
                />
                <div className="rp-hotel-type-pill">{plan.hotel.type}</div>
              </div>
              <div className="rp-hotel-info">
                <h2>{plan.hotel.name}</h2>
                <div className="rp-hotel-meta">
                  <span className="rp-stars"><Star size={14} fill="#FFD700" color="#FFD700" /> {plan.hotel.rating}</span>
                  <span className="rp-price-pill">₹{plan.hotel.pricePerNight?.toLocaleString()}/night</span>
                  <span style={{ color: '#94A3B8', fontSize: 13 }}><MapPin size={12} /> {request?.city}</span>
                </div>
                {plan.hotel.amenities && (
                  <div className="rp-amenities">
                    {plan.hotel.amenities.split(',').map((a, i) => (
                      <span key={i} className="rp-amenity-tag">{a.trim()}</span>
                    ))}
                  </div>
                )}
                <div className="rp-hotel-nights">
                  <Calendar size={14} /> {request?.days} Nights Stay
                  <span className="rp-hotel-total">Total: ₹{plan.hotelCost?.toLocaleString()}</span>
                </div>
              </div>
            </div>
          </motion.div>
        )}

        {/* ══════════════ DAY-BY-DAY ITINERARY ══════════════ */}
        {days.length > 0 && (
          <motion.div className="rp-section" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.5 }}>
            <div className="rp-section-title"><TrendingUp size={20} /> Day-by-Day Itinerary</div>

            <div className="rp-days-container">
              {days.map((day, di) => (
                <motion.div
                  key={di}
                  className="rp-day-card"
                  initial={{ opacity: 0, y: 30 }}
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: true, margin: '-60px' }}
                  transition={{ delay: di * 0.08 }}
                >
                  {/* Day Header */}
                  <div
                    className="rp-day-header"
                    onClick={() => setExpandedDay(expandedDay === di ? -1 : di)}
                    style={{ cursor: 'pointer' }}
                  >
                    <div className="rp-day-num-badge" style={{ background: di % 2 === 0 ? '#6C63FF' : '#00D4FF' }}>
                      Day {day.day}
                    </div>
                    <div className="rp-day-header-info">
                      <h3>{day.title || `Day ${day.day}`}</h3>
                      <p>{day.notes}</p>
                    </div>
                    <div className="rp-day-meta-right">
                      {day.hotelAction && <span className="rp-hotel-action-badge">{day.hotelAction === 'Check-in' ? '🏨 Check-in' : day.hotelAction === 'Check-out' ? '🧳 Check-out' : '🏨 Stay'}</span>}
                      {day.places?.length > 0 && <span className="rp-places-count">{day.places.length} place{day.places.length > 1 ? 's' : ''}</span>}
                      <span className="rp-expand-icon">{expandedDay === di ? <ChevronUp size={18} /> : <ChevronDown size={18} />}</span>
                    </div>
                  </div>

                  {/* Place Images Strip */}
                  {day.places?.length > 0 && (
                    <div className="rp-day-places-strip">
                      {day.places.map((place, pi) => (
                        <div
                          key={pi}
                          className="rp-day-place-img"
                          onClick={() => setActivePhoto({ url: place.imageUrl, title: place.name, type: place.category })}
                        >
                          <img
                            src={place.imageUrl || 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&q=80&w=600'}
                            alt={place.name}
                            onError={e => { e.target.src = 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&q=80&w=600'; }}
                          />
                          <div className="rp-place-img-overlay">
                            <span className="rp-place-img-name">{place.name}</span>
                            <span className="rp-place-img-meta">
                              <Star size={10} fill="#FFD700" color="#FFD700" /> {place.rating}
                              {place.entryFee > 0 ? ` · ₹${place.entryFee}` : ' · Free'}
                            </span>
                          </div>
                          {place.category && <span className="rp-place-cat-badge">{place.category}</span>}
                        </div>
                      ))}
                    </div>
                  )}

                  {/* Timeline (expanded) */}
                  <AnimatePresence>
                    {expandedDay === di && day.timeline?.length > 0 && (
                      <motion.div
                        className="rp-timeline"
                        initial={{ height: 0, opacity: 0 }}
                        animate={{ height: 'auto', opacity: 1 }}
                        exit={{ height: 0, opacity: 0 }}
                        transition={{ duration: 0.35 }}
                      >
                        {day.timeline.map((step, si) => (
                          <div key={si} className="rp-timeline-step">
                            <div className="rp-timeline-left">
                              <div className="rp-tl-icon" style={{ background: `${getColor(step.activityType)}20`, color: getColor(step.activityType) }}>
                                {getIcon(step.activityType)}
                              </div>
                              {si < day.timeline.length - 1 && <div className="rp-tl-line" />}
                            </div>
                            <div className="rp-timeline-body">
                              <div className="rp-tl-time">
                                <Clock size={11} /> {step.startTime} — {step.endTime}
                              </div>
                              <div className="rp-tl-title">{step.title}</div>
                              {step.details && (
                                <div className="rp-tl-details">
                                  {step.details.description && <p>{step.details.description}</p>}
                                  {step.details.entryFee && <span className="rp-tl-chip">🎟 {step.details.entryFee}</span>}
                                  {step.details.costForTwo && <span className="rp-tl-chip">💰 {step.details.costForTwo}</span>}
                                  {step.details.mode && <span className="rp-tl-chip">🚗 {step.details.mode} · {step.details.duration}</span>}
                                  {step.details.famousFor && <span className="rp-tl-chip">🍽 {step.details.famousFor}</span>}
                                </div>
                              )}
                            </div>
                          </div>
                        ))}
                      </motion.div>
                    )}
                  </AnimatePresence>

                  {/* Meals Row */}
                  <div className="rp-day-meals">
                    {day.breakfast && (
                      <div className="rp-meal-chip" onClick={() => setActivePhoto({ url: day.breakfast.imageUrl, title: day.breakfast.name, type: 'Breakfast' })}>
                        <img src={day.breakfast.imageUrl} alt={day.breakfast.name} onError={e => { e.target.src = 'https://images.unsplash.com/photo-1533089860892-a7c6f0a88666?auto=format&fit=crop&q=80&w=200'; }} />
                        <div>
                          <span className="rp-meal-label">☀️ Breakfast</span>
                          <span className="rp-meal-name">{day.breakfast.name}</span>
                        </div>
                      </div>
                    )}
                    {day.lunch && (
                      <div className="rp-meal-chip" onClick={() => setActivePhoto({ url: day.lunch.imageUrl, title: day.lunch.name, type: 'Lunch' })}>
                        <img src={day.lunch.imageUrl} alt={day.lunch.name} onError={e => { e.target.src = 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?auto=format&fit=crop&q=80&w=200'; }} />
                        <div>
                          <span className="rp-meal-label">🌤️ Lunch</span>
                          <span className="rp-meal-name">{day.lunch.name}</span>
                        </div>
                      </div>
                    )}
                    {day.dinner && (
                      <div className="rp-meal-chip" onClick={() => setActivePhoto({ url: day.dinner.imageUrl, title: day.dinner.name, type: 'Dinner' })}>
                        <img src={day.dinner.imageUrl} alt={day.dinner.name} onError={e => { e.target.src = 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?auto=format&fit=crop&q=80&w=200'; }} />
                        <div>
                          <span className="rp-meal-label">🌙 Dinner</span>
                          <span className="rp-meal-name">{day.dinner.name}</span>
                        </div>
                      </div>
                    )}
                  </div>
                </motion.div>
              ))}
            </div>
          </motion.div>
        )}

        {/* ══════════════ PLACES GALLERY ══════════════ */}
        {plan.places?.length > 0 && (
          <motion.div className="rp-section" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.55 }}>
            <div className="rp-section-title"><MapPin size={20} /> Curated Experiences</div>
            <div className="rp-places-masonry">
              {plan.places.map((place, i) => (
                <motion.div
                  key={i}
                  className="rp-place-tile"
                  whileHover={{ y: -6, scale: 1.02 }}
                  onClick={() => setActivePhoto({ url: place.imageUrl, title: place.name, type: place.category })}
                  style={{ gridRow: i % 3 === 0 ? 'span 2' : 'span 1' }}
                >
                  <img
                    src={place.imageUrl}
                    alt={place.name}
                    onError={e => { e.target.src = 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&q=80&w=600'; }}
                  />
                  <div className="rp-place-tile-overlay">
                    <span className="rp-place-tile-cat">{place.category}</span>
                    <h4>{place.name}</h4>
                    <div className="rp-place-tile-meta">
                      <span><Star size={11} fill="#FFD700" color="#FFD700" /> {place.rating}</span>
                      <span>{place.entryFee > 0 ? `₹${place.entryFee}` : 'Free Entry'}</span>
                      <span><Clock size={11} /> {place.durationMinutes || 60}m</span>
                    </div>
                  </div>
                </motion.div>
              ))}
            </div>
          </motion.div>
        )}

        {/* ══════════════ RESTAURANTS ══════════════ */}
        {plan.restaurants?.length > 0 && (
          <motion.div className="rp-section" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.6 }}>
            <div className="rp-section-title"><Utensils size={20} /> Gastronomy Guide</div>
            <div className="rp-restaurant-scroll">
              {plan.restaurants.map((r, i) => (
                <motion.div
                  key={i}
                  className="rp-restaurant-card"
                  whileHover={{ y: -5, scale: 1.02 }}
                  onClick={() => setActivePhoto({ url: r.imageUrl, title: r.name, type: r.cuisine })}
                >
                  <img
                    src={r.imageUrl}
                    alt={r.name}
                    onError={e => { e.target.src = 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?auto=format&fit=crop&q=80&w=400'; }}
                  />
                  <div className="rp-restaurant-info">
                    <span className="rp-rest-meal-tag">{r.mealType === 'breakfast' ? '☀️' : r.mealType === 'lunch' ? '🌤️' : '🌙'} {r.mealType}</span>
                    <h4>{r.name}</h4>
                    <p>{r.cuisine}</p>
                    <div className="rp-rest-meta">
                      <span><Star size={12} fill="#FFD700" color="#FFD700" /> {r.rating}</span>
                      <span>₹{r.pricePerMeal}/meal</span>
                    </div>
                  </div>
                </motion.div>
              ))}
            </div>
          </motion.div>
        )}

        {/* ══════════════ BOTTOM ROW: Score + Budget ══════════════ */}
        <div className="rp-bottom-row">

          {/* Trip Score */}
          {score && (
            <motion.div className="rp-section rp-score-section" initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.65 }}>
              <div className="rp-section-title"><Trophy size={20} /> Trip Quality Score</div>
              <p className="rp-score-verdict">{score.verdict}</p>
              <div className="rp-score-bars">
                {[
                  { label: 'Budget Use', val: score.budgetUtilization, icon: <Wallet size={13} /> },
                  { label: 'Diversity', val: score.diversityScore, icon: <Leaf size={13} /> },
                  { label: 'Hotel', val: score.hotelQuality, icon: <Hotel size={13} /> },
                  { label: 'Food', val: score.foodQuality, icon: <Utensils size={13} /> },
                  { label: 'Itinerary', val: score.itineraryBalance, icon: <Target size={13} /> },
                ].map((item, i) => (
                  <div key={i} className="rp-score-bar-row">
                    <div className="rp-score-bar-lbl">{item.icon} {item.label} <span style={{ color: getScoreColor(item.val) }}>{item.val}</span></div>
                    <div className="rp-score-track">
                      <motion.div
                        className="rp-score-fill"
                        initial={{ width: 0 }}
                        animate={{ width: `${item.val}%` }}
                        transition={{ delay: 0.7 + i * 0.1, duration: 0.8 }}
                        style={{ background: getScoreColor(item.val) }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </motion.div>
          )}

          {/* Budget Breakdown + Group */}
          <div className="rp-right-col">
            {plan.transportMode && (
              <motion.div className="rp-section" initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.65 }}>
                <div className="rp-section-title"><Car size={20} /> Transport</div>
                <div className="rp-info-rows">
                  <div className="rp-info-row"><span>Mode</span><strong style={{ textTransform: 'capitalize' }}>{plan.transportMode}</strong></div>
                  <div className="rp-info-row"><span>Total Distance</span><strong>{plan.totalDistanceKm} km</strong></div>
                  <div className="rp-info-row"><span>Cost</span><strong>₹{plan.transportCost?.toLocaleString()}</strong></div>
                </div>
              </motion.div>
            )}

            {gcb && (
              <motion.div className="rp-section" initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.7 }}>
                <div className="rp-section-title"><Users size={20} /> Group Split</div>
                <div className="rp-info-rows">
                  <div className="rp-info-row"><span>Travelers</span><strong>{gcb.travelers}</strong></div>
                  <div className="rp-info-row"><span>Rooms Needed</span><strong>{gcb.roomsNeeded}</strong></div>
                  <div className="rp-info-row"><span>Hotel/Person</span><strong>₹{gcb.hotelCostPerPerson?.toLocaleString()}</strong></div>
                  <div className="rp-info-row"><span>Food/Person</span><strong>₹{gcb.foodCostPerPerson?.toLocaleString()}</strong></div>
                  <div className="rp-info-row rp-info-row--highlight"><span>Per Person Total</span><strong>₹{gcb.perPersonCost?.toLocaleString()}</strong></div>
                  {gcb.savingsVsSolo > 0 && <div className="rp-savings-msg">💰 Save ₹{gcb.savingsVsSolo?.toLocaleString()} vs solo!</div>}
                </div>
              </motion.div>
            )}

            {bb && (
              <motion.div className="rp-section" initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.75 }}>
                <div className="rp-section-title"><Gauge size={20} /> Budget Breakdown</div>
                <div className="rp-budget-items">
                  {[
                    { label: 'Hotel', alloc: bb.hotelBudget, spent: bb.hotelActual, color: '#00E676' },
                    { label: 'Food', alloc: bb.foodBudget, spent: bb.foodActual, color: '#FF6B9D' },
                    { label: 'Travel', alloc: bb.travelBudget, spent: bb.travelActual, color: '#FFD700' },
                    { label: 'Places', alloc: bb.placesBudget, spent: bb.placesActual, color: '#6C63FF' },
                  ].map((item, i) => (
                    <div key={i} className="rp-budget-item">
                      <div className="rp-budget-item-header">
                        <span style={{ color: item.color }}>{item.label}</span>
                        <span style={{ fontSize: 12, color: '#94A3B8' }}>₹{(item.spent || 0).toLocaleString()} / ₹{(item.alloc || 0).toLocaleString()}</span>
                      </div>
                      <div className="rp-budget-track">
                        <motion.div
                          className="rp-budget-fill"
                          initial={{ width: 0 }}
                          animate={{ width: `${Math.min(100, ((item.spent || 0) / (item.alloc || 1)) * 100)}%` }}
                          transition={{ delay: 0.8 + i * 0.1 }}
                          style={{ background: item.color }}
                        />
                      </div>
                    </div>
                  ))}
                  {plan.totalCost < request?.budget && (
                    <div className="rp-budget-savings">✨ ₹{(request.budget - plan.totalCost).toLocaleString()} saved vs your budget!</div>
                  )}
                </div>
              </motion.div>
            )}

            {plan.suggestionMessage && (
              <motion.div className="rp-section rp-suggestion" initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.8 }}>
              <div className="rp-section-title"><Lightbulb size={20} /> Planner Insight</div>
                <p>{plan.suggestionMessage}</p>
              </motion.div>
            )}
          </div>
        </div>

      </div>

      {/* ══════════════ LIGHTBOX ══════════════ */}
      <AnimatePresence>
        {activePhoto && (
          <motion.div
            className="rp-lightbox"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={() => setActivePhoto(null)}
          >
            <motion.div
              className="rp-lightbox-content"
              initial={{ scale: 0.85, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.85, opacity: 0 }}
              onClick={e => e.stopPropagation()}
            >
              <button className="rp-lightbox-close" onClick={() => setActivePhoto(null)}><X size={22} /></button>
              <img src={activePhoto.url} alt={activePhoto.title} onError={e => { e.target.src = 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&q=80&w=800'; }} />
              <div className="rp-lightbox-info">
                <span className="rp-badge rp-badge-vibe">{activePhoto.type}</span>
                <h3>{activePhoto.title}</h3>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
