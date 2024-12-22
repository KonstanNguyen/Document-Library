import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:5454',
  headers: {
    'Content-Type': 'application/json',
    "Access-Control-Allow-Origin": "*",
    'Authorization': localStorage.getItem("token"),

  },
});

export default apiClient;
