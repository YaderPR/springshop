// hooks/useUser.ts
import { useState, useEffect } from 'react';
import { userService } from '../services/user/userService';
import { UserResponse } from '../types/User.types';

export const useUser = (userId?: number) => {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchUser = async (id: number) => {
    if (!id) return;
    
    setLoading(true);
    setError(null);
    try {
      const userData = await userService.getUserById(id);
      setUser(userData);
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Error al cargar usuario';
      setError(errorMessage);
      console.error('Error fetching user:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchUserBySub = async (userSub: string) => {
    setLoading(true);
    setError(null);
    try {
      const userData = await userService.getUserBySub(userSub);
      setUser(userData);
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Error al cargar usuario';
      setError(errorMessage);
      console.error('Error fetching user by sub:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (userId) {
      fetchUser(userId);
    }
  }, [userId]);

  return {
    user,
    loading,
    error,
    fetchUser,
    fetchUserBySub,
    refreshUser: () => userId ? fetchUser(userId) : Promise.resolve()
  };
};