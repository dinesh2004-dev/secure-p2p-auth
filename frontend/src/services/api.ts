import axios from 'axios';

const USER_API = 'http://localhost:8080/api/v1/user';
const SERVER_API = 'http://localhost:8080/api/v1/server';
// Create axios instance
const userApi = axios.create({
  baseURL: USER_API,
  headers: {
    'Content-Type': 'application/json',
  },
});
const serverApi = axios.create({
  baseURL: SERVER_API,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include JWT token
userApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Auth API calls
export const authAPI = {
  register: (userData: { email: string; username: string; password: string }) =>
    userApi.post('/register', userData),
  
  login: (credentials: { username: string; password: string }) =>
    userApi.post('/login', credentials),
};

// Add request interceptor to serverApi as well
serverApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// File API calls
export const fileAPI = {
 sendFile:(formData : FormData) => {
    return serverApi.post('/sendFile',formData,{
      headers:{
        'Content-Type' : 'multipart/form-data',
      },
    });
  },

  
  requestFile: (fileName: string, choice: number) =>
    serverApi.get(`/requestFile?fileName=${fileName}&choice=${choice}`),
  
  getPeer1Files: () => serverApi.get('/peer1/files'),
  
  getPeer2Files: () => serverApi.get('/peer2/files'),

  // Crypto status monitoring
  getCryptoStatus: (peerIdentity: number) =>
    serverApi.get(`/crypto/status/${peerIdentity}`),

  initializeCrypto: (peerIdentity: number) =>
    serverApi.post(`/crypto/initialize/${peerIdentity}`),
  clearCryptoStatus: (peerIdentity: number) => {
    return serverApi.post(`/crypto/clear/${peerIdentity}`);
  },
};

export default {
  userApi,
  serverApi,
};