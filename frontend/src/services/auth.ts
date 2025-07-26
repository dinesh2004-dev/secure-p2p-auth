import { fileAPI } from '../services/api';

export interface User {
  id: string;
  username: string;
  email: string;
}

export const authService = {
  setToken: (token: string) => {
    localStorage.setItem('jwt', token);
  },

  getToken: () => {
    return localStorage.getItem('jwt');
  },

  removeToken: () => {
    localStorage.removeItem('jwt');
  },

  isAuthenticated: () => {
    const token = localStorage.getItem('jwt');
    return !!token;
  },
  // Cryptographic status tracking
  setKeysLoaded: (loaded: boolean) => {
    localStorage.setItem('keysLoaded', loaded.toString());
  },

  areKeysLoaded: (): boolean => {
    const loaded = localStorage.getItem('keysLoaded');
    return loaded === 'true';
  },

  setAESGenerated: (generated: boolean) => {
    localStorage.setItem('aesGenerated', generated.toString());
  },

  isAESGenerated: (): boolean => {
    const generated = localStorage.getItem('aesGenerated');
    return generated === 'true';
  },
   setECDHCompleted: (completed: boolean) => {
    localStorage.setItem('ecdhCompleted', completed.toString());
  },

  isECDHCompleted: (): boolean => {
    const completed = localStorage.getItem('ecdhCompleted');
    return completed === 'true';
  },

  setECDSAVerified: (verified: boolean) => {
    localStorage.setItem('ecdsaVerified', verified.toString());
  },

  isECDSAVerified: (): boolean => {
    const verified = localStorage.getItem('ecdsaVerified');
    return verified === 'true';
  },


  logout: async () => {
    try{
      const peerIdentity = authService.getPeerIdentity();
      
      // Clear crypto state on backend if peer identity exists
      if (peerIdentity) {
        try {
          await fileAPI.clearCryptoStatus(peerIdentity);
          console.log('✅ Crypto state cleared on logout');
        } catch (error) {
          console.error('❌ Failed to clear crypto state on logout:', error);
        }
      }
      localStorage.removeItem('jwt');
      localStorage.removeItem('user');
      localStorage.removeItem('peerIdentity');
      localStorage.removeItem('keysLoaded');
      localStorage.removeItem('aesGenerated');
      localStorage.removeItem('ecdhCompleted');
      localStorage.removeItem('ecdsaVerified');
      console.log('🚪 User logged out successfully');
    }catch (error) {
      console.error('❌ Logout error:', error);
      // Still clear localStorage even if backend call fails
      localStorage.clear();
    }
  },

  setUser: (user: User) => {
    localStorage.setItem('user', JSON.stringify(user));
    console.log('User set in localStorage:', user);
  },

  getUser: (): User | null => {
    const user = localStorage.getItem('user');
    if(!user || user === 'undefined' || user === 'null'){
       return null;
    }
    try{
      return JSON.parse(user);
    }catch(error){
      console.error('Error parsing user data:',error);
      localStorage.removeItem('user');
      return null;
    }
    
  },

  setPeerIdentity: (identity: number) => {
    localStorage.setItem('peerIdentity', identity.toString());
  },

  getPeerIdentity: (): number | null => {
    const identity = localStorage.getItem('peerIdentity');
    return identity ? parseInt(identity) : null;
  },
};