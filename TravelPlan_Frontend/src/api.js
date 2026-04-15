import axios from 'axios';

const API_BASE = 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

// Automatically inject JWT token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('travelmind_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Trip Planning
export const generatePlan = (data) => api.post('/plan', data);

// Comparison
export const compareBudgets = (data) => api.post('/plan/compare', data);

// Hotels
export const getHotelsByCity = (city) => api.get(`/hotels/${city}`);
export const getAllHotels = () => api.get('/hotels');

// Places
export const getPlacesByCity = (city) => api.get(`/places/${city}`);
export const getAllPlaces = () => api.get('/places');

// Restaurants
export const getRestaurantsByCity = (city) => api.get(`/restaurants/${city}`);

// Analytics
export const getTrending = () => api.get('/analytics/trending');
export const getAvailableCities = () => api.get('/analytics/cities');

// Trip History
export const getTripHistory = () => api.get('/history');
export const getHistoryByVisitor = (name) => api.get(`/history/visitor/${name}`);
export const deleteHistory = (id) => api.delete(`/history/${id}`);

// Reviews
export const submitReview = (data) => api.post('/reviews', data);
export const getReviews = (entityId) => api.get(`/reviews/${entityId}`);

export default api;
