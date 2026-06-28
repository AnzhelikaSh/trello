import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_URL,
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        console.log('Токен из localStorage:', token ? `${token.substring(0, 50)}...` : 'Нет токена');

        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        console.log('Запрос:', config.method, config.url, config.headers);
        return config;
    },
    (error) => Promise.reject(error)
);

export default api;