import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
    "Access-Control-Allow-Origin": "*",
    'Authorization': localStorage.getItem("token"),

  },
});

export default apiClient;
