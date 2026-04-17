import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Users, UserPlus, Trash2, Calendar, MapPin, 
  ChevronRight, Loader2, CheckCircle2, Clock, Play, Sparkles 
} from 'lucide-react';
import toast from 'react-hot-toast';
import { 
  getMyGroups, 
  addGroupMember, 
  initiateGroupTrip, 
  getActiveGroupTrip,
  finalizeGroupTrip
} from '../api';
import { useAuth } from '../context/AuthContext';
import './GroupDetailPage.css';

export default function GroupDetailPage() {
  const { groupId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [group, setGroup] = useState(null);
  const [activeTrip, setActiveTrip] = useState(null);
  const [loading, setLoading] = useState(true);
  const [addingMember, setAddingMember] = useState(false);
  const [memberEmail, setMemberEmail] = useState('');
  const [initiating, setInitiating] = useState(false);
  const [finalizing, setFinalizing] = useState(false);

  // For initiating trip
  const [tripForm, setTripForm] = useState({ destination: '', days: 3 });

  const currentUserEmail = user?.email;

  useEffect(() => {
    fetchData();
  }, [groupId]);

  const fetchData = async () => {
    try {
      const { data: allGroups } = await getMyGroups();
      const match = allGroups.find(g => g.id === groupId);
      if (!match) throw new Error('Group not found');
      setGroup(match);

      const { data: trip } = await getActiveGroupTrip(groupId);
      setActiveTrip(trip);
    } catch (err) {
      toast.error('Failed to load group details');
      navigate('/groups');
    } finally {
      setLoading(false);
    }
  };

  const handleAddMember = async (e) => {
    e.preventDefault();
    if (!memberEmail.trim()) return;
    setAddingMember(true);
    try {
      await addGroupMember(groupId, memberEmail);
      toast.success('Member added!');
      setMemberEmail('');
      fetchData();
    } catch (err) {
      const msg = err.response?.data?.error || 'Failed to add member';
      toast.error(msg);
    } finally {
      setAddingMember(false);
    }
  };

  const handleInitiateTrip = async (e) => {
    e.preventDefault();
    setInitiating(true);
    try {
      await initiateGroupTrip(groupId, tripForm);
      toast.success('Group trip initiated! Everyone can now submit their preferences.');
      fetchData();
    } catch (err) {
      toast.error('Failed to initiate trip');
    } finally {
      setInitiating(false);
    }
  };

  const handleFinalize = async () => {
    setFinalizing(true);
    try {
      const { data } = await finalizeGroupTrip(activeTrip.id);
      toast.success('Trip Finalized! Generating consensus plan...');
      navigate('/result', { state: { plan: data } });
    } catch (err) {
      toast.error('Failed to finalize trip');
    } finally {
      setFinalizing(false);
    }
  };

  if (loading) return (
    <div className="detail-loading">
      <Loader2 size={48} className="spin" />
      <p>Loading group hub...</p>
    </div>
  );

  const isAdmin = group.adminEmail === currentUserEmail;
  const hasResponded = activeTrip?.submittedEmails?.includes(currentUserEmail);

  return (
    <div className="group-detail-page">
      <div className="detail-header">
        <motion.button 
          className="back-link" 
          onClick={() => navigate('/groups')}
          whileHover={{ x: -5 }}
        >
          ← Back to Groups
        </motion.button>
        <h1>{group.name}</h1>
        <div className="header-meta">
          <span><Users size={16} /> {group.memberEmails.length} Members</span>
          <span className="admin-badge">Admin: {group.adminEmail}</span>
        </div>
      </div>

      <div className="detail-grid">
        {/* MEMBERS SECTION */}
        <section className="detail-section members-section">
          <div className="section-title">
            <Users size={20} />
            <h2>Members</h2>
          </div>
          
          <div className="member-list">
            {group.memberEmails.map(email => (
              <div key={email} className="member-item">
                <div className="member-avatar">
                  {email[0].toUpperCase()}
                </div>
                <div className="member-info">
                  <span className="email">{email}</span>
                  {activeTrip && (
                    <span className={`status-tag ${activeTrip.submittedEmails.includes(email) ? 'submitted' : 'pending'}`}>
                      {activeTrip.submittedEmails.includes(email) ? (
                        <><CheckCircle2 size={12} /> Submitted</>
                      ) : (
                        <><Clock size={12} /> Pending</>
                      )}
                    </span>
                  )}
                </div>
                {email === group.adminEmail && <span className="admin-chip">ADMIN</span>}
              </div>
            ))}
          </div>

          {isAdmin && (
            <form onSubmit={handleAddMember} className="add-member-form">
              <label>Add New Member</label>
              <div className="add-member-input-row">
                <input 
                  type="email" 
                  placeholder="Friend's email" 
                  value={memberEmail}
                  onChange={(e) => setMemberEmail(e.target.value)}
                  required
                />
                <button type="submit" disabled={addingMember}>
                  {addingMember ? <Loader2 size={16} className="spin" /> : <UserPlus size={16} />}
                </button>
              </div>
            </form>
          )}
        </section>

        {/* TRIP STATUS SECTION */}
        <section className="detail-section trip-section">
          {!activeTrip ? (
            <div className="no-active-trip">
              <div className="empty-trip-icon"><Calendar size={48} /></div>
              <h3>No Active Trip</h3>
              <p>Start a collaborative planning session for your next adventure.</p>
              
              {isAdmin ? (
                <div className="initiate-box">
                  <div className="input-row">
                    <input 
                      type="text" 
                      placeholder="Destination" 
                      value={tripForm.destination}
                      onChange={(e) => setTripForm({...tripForm, destination: e.target.value})}
                    />
                    <input 
                      type="number" 
                      placeholder="Days" 
                      value={tripForm.days}
                      onChange={(e) => setTripForm({...tripForm, days: parseInt(e.target.value)})}
                    />
                  </div>
                  <button 
                    className="primary-btn" 
                    onClick={handleInitiateTrip}
                    disabled={initiating || !tripForm.destination}
                  >
                    {initiating ? <Loader2 className="spin" /> : <Play size={18} />} Initiate Group Trip
                  </button>
                </div>
              ) : (
                <div className="member-waiting">
                  <p>Wait for the admin to initiate a new trip.</p>
                </div>
              )}
            </div>
          ) : (
            <div className="active-trip-panel">
              <div className="trip-badge">ACTIVE SESSION</div>
              <h2>Trip to <span className="gradient-text">{activeTrip.destination}</span></h2>
              <p className="trip-sub">{activeTrip.days} Days · Collaborative Planning</p>

              <div className="submission-progress">
                <div className="progress-stats">
                  <span>Responses: {activeTrip.submittedEmails.length} / {group.memberEmails.length}</span>
                  <span className="percent">{Math.round((activeTrip.submittedEmails.length / group.memberEmails.length) * 100)}%</span>
                </div>
                <div className="progress-bar-bg">
                  <motion.div 
                    className="progress-bar-fill"
                    initial={{ width: 0 }}
                    animate={{ width: `${(activeTrip.submittedEmails.length / group.memberEmails.length) * 100}%` }}
                  />
                </div>
              </div>

              <div className="trip-actions">
                {!hasResponded ? (
                  <button 
                    className="primary-btn pulse"
                    onClick={() => navigate(`/group-trip/${activeTrip.id}`)}
                  >
                    <Sparkles size={18} /> Submit Your Preferences
                  </button>
                ) : (
                  <div className="success-box">
                    <CheckCircle2 size={24} />
                    <span>You've submitted your preferences!</span>
                    <button className="text-btn" onClick={() => navigate(`/group-trip/${activeTrip.id}`)}>Edit Response</button>
                  </div>
                )}

                {isAdmin && (
                  <div className="admin-finalize">
                    <p>Ready to generate the final itinerary based on everyone's input?</p>
                    <button 
                      className="finalize-btn"
                      onClick={handleFinalize}
                      disabled={finalizing || activeTrip.submittedEmails.length === 0}
                    >
                      {finalizing ? <Loader2 className="spin" /> : <ChevronRight />} Finalize Consensus Trip
                    </button>
                  </div>
                )}
              </div>
            </div>
          )}
        </section>
      </div>
    </div>
  );
}
