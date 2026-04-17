import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { Users, Plus, ChevronRight, Loader2, UserPlus, Shield } from 'lucide-react';
import toast from 'react-hot-toast';
import { getMyGroups, createGroup } from '../api';
import { useAuth } from '../context/AuthContext';
import './GroupsPage.css';

export default function GroupsPage() {
  const [groups, setGroups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [newGroupName, setNewGroupName] = useState('');
  const [creating, setCreating] = useState(false);
  const navigate = useNavigate();
  const { user } = useAuth();

  useEffect(() => {
    fetchGroups();
  }, []);

  const fetchGroups = async () => {
    try {
      const { data } = await getMyGroups();
      setGroups(data);
    } catch (err) {
      toast.error('Failed to load groups');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateGroup = async (e) => {
    e.preventDefault();
    if (!newGroupName.trim()) return;
    setCreating(true);
    try {
      await createGroup(newGroupName);
      toast.success('Group created! 🎉');
      setNewGroupName('');
      setShowCreate(false);
      fetchGroups();
    } catch (err) {
      toast.error('Failed to create group');
    } finally {
      setCreating(false);
    }
  };

  return (
    <div className="groups-page">
      <div className="groups-header">
        <div className="header-content">
          <span className="badge">Collaborative Travel</span>
          <h1>My <span className="gradient-text">Travel Groups</span></h1>
          <p>Plan adventures together with friends and family</p>
        </div>
        <motion.button
          className="create-btn"
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={() => setShowCreate(true)}
        >
          <Plus size={20} /> New Group
        </motion.button>
      </div>

      <AnimatePresence>
        {showCreate && (
          <motion.div
            className="modal-overlay"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          >
            <motion.div
              className="create-modal"
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
            >
              <h2>Create New Group</h2>
              <form onSubmit={handleCreateGroup}>
                <input
                  type="text"
                  placeholder="Group Name (e.g., Euro Trip 2026)"
                  value={newGroupName}
                  onChange={(e) => setNewGroupName(e.target.value)}
                  autoFocus
                />
                <div className="modal-actions">
                  <button type="button" className="cancel-btn" onClick={() => setShowCreate(false)}>Cancel</button>
                  <button type="submit" className="confirm-btn" disabled={creating || !newGroupName.trim()}>
                    {creating ? <Loader2 className="spin" /> : 'Create'}
                  </button>
                </div>
              </form>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      <div className="groups-grid">
        {loading ? (
          <div className="loading-state">
            <Loader2 size={40} className="spin" />
            <p>Loading your groups...</p>
          </div>
        ) : groups.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon"><Users size={48} /></div>
            <h3>No groups yet</h3>
            <p>Create a group to start planning collaborative trips with your friends!</p>
          </div>
        ) : (
          groups.map((group, idx) => (
            <motion.div
              key={group.id}
              className="group-card"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: idx * 0.1 }}
              onClick={() => navigate(`/groups/${group.id}`)}
              whileHover={{ y: -5 }}
            >
              <div className="group-card-header">
                <div className="group-icon-circle">
                  <Users size={24} />
                </div>
                <div className="group-info">
                  <h3>{group.name}</h3>
                  <span className="member-count">{group.memberEmails.length} Members</span>
                </div>
                <ChevronRight className="arrow" />
              </div>
              <div className="group-card-footer">
                <span className="role-tag">
                  {group.adminEmail === user?.email ? (
                    <><Shield size={12} /> Admin</>
                  ) : (
                    <><Users size={12} /> Member</>
                  )}
                </span>
                
                {group.adminEmail === user?.email && (
                  <motion.button 
                    className="quick-add-btn"
                    whileHover={{ scale: 1.1 }}
                    whileTap={{ scale: 0.9 }}
                    onClick={(e) => {
                      e.stopPropagation();
                      navigate(`/groups/${group.id}`);
                    }}
                    title="Add Group Members"
                  >
                    <UserPlus size={16} />
                  </motion.button>
                )}
              </div>
            </motion.div>
          ))
        )}
      </div>
    </div>
  );
}
