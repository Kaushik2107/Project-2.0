import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import LoginPage from './pages/LoginPage';
import ProfilePage from './pages/ProfilePage';
import DashboardPage from './pages/DashboardPage';
import PlannerPage from './pages/PlannerPage';
import ResultPage from './pages/ResultPage';
import ComparePage from './pages/ComparePage';
import ExplorePage from './pages/ExplorePage';
import HistoryPage from './pages/HistoryPage';
import GroupsPage from './pages/GroupsPage';
import GroupDetailPage from './pages/GroupDetailPage';
import GroupResponsePage from './pages/GroupResponsePage';
import './App.css';

/* ─── Route Guards ─── */
function RequireAuth({ children }) {
  const { user, loading } = useAuth();
  if (loading) return null;
  if (!user?.loggedIn) return <Navigate to="/login" replace />;
  if (!user?.profileComplete) return <Navigate to="/profile" replace />;
  return children;
}

function RequireLogin({ children }) {
  const { user, loading } = useAuth();
  if (loading) return null;
  if (!user?.loggedIn) return <Navigate to="/login" replace />;
  return children;
}

function RedirectIfAuth({ children }) {
  const { user, loading } = useAuth();
  if (loading) return null;
  if (user?.loggedIn && user?.profileComplete) return <Navigate to="/dashboard" replace />;
  if (user?.loggedIn && !user?.profileComplete) return <Navigate to="/profile" replace />;
  return children;
}

/* ─── Layout with Navbar (shown only when authenticated) ─── */
function AuthenticatedLayout({ children }) {
  return (
    <>
      <Navbar />
      <main style={{ flex: 1 }}>{children}</main>
    </>
  );
}

function AppRoutes() {
  return (
    <Routes>
      {/* Public: Login */}
      <Route
        path="/login"
        element={
          <RedirectIfAuth>
            <LoginPage />
          </RedirectIfAuth>
        }
      />

      {/* Semi-protected: Profile Setup (needs login, not profile) */}
      <Route
        path="/profile"
        element={
          <RequireLogin>
            <ProfilePage />
          </RequireLogin>
        }
      />

      {/* Protected: All main app routes */}
      <Route
        path="/dashboard"
        element={
          <RequireAuth>
            <AuthenticatedLayout>
              <DashboardPage />
            </AuthenticatedLayout>
          </RequireAuth>
        }
      />
      <Route
        path="/plan"
        element={
          <RequireAuth>
            <AuthenticatedLayout>
              <PlannerPage />
            </AuthenticatedLayout>
          </RequireAuth>
        }
      />
      <Route
        path="/result"
        element={
          <RequireAuth>
            <AuthenticatedLayout>
              <ResultPage />
            </AuthenticatedLayout>
          </RequireAuth>
        }
      />
      <Route
        path="/compare"
        element={
          <RequireAuth>
            <AuthenticatedLayout>
              <ComparePage />
            </AuthenticatedLayout>
          </RequireAuth>
        }
      />
      <Route
        path="/explore"
        element={
          <RequireAuth>
            <AuthenticatedLayout>
              <ExplorePage />
            </AuthenticatedLayout>
          </RequireAuth>
        }
      />
      <Route
        path="/history"
        element={
          <RequireAuth>
            <AuthenticatedLayout>
              <HistoryPage />
            </AuthenticatedLayout>
          </RequireAuth>
        }
      />
      <Route
        path="/groups"
        element={
          <RequireAuth>
            <AuthenticatedLayout>
              <GroupsPage />
            </AuthenticatedLayout>
          </RequireAuth>
        }
      />
      <Route
        path="/groups/:groupId"
        element={
          <RequireAuth>
            <AuthenticatedLayout>
              <GroupDetailPage />
            </AuthenticatedLayout>
          </RequireAuth>
        }
      />
      <Route
        path="/group-trip/:tripId"
        element={
          <RequireAuth>
            <AuthenticatedLayout>
              <GroupResponsePage />
            </AuthenticatedLayout>
          </RequireAuth>
        }
      />

      {/* Default: redirect to dashboard */}
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <Toaster
          position="top-right"
          toastOptions={{
            style: {
              background: '#1A1F35',
              color: '#F8FAFC',
              border: '1px solid rgba(255,255,255,0.08)',
              borderRadius: '12px',
            },
          }}
        />
        <AppRoutes />
      </Router>
    </AuthProvider>
  );
}

export default App;
