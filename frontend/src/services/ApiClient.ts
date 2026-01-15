import axios from "axios";

export const api = axios.create({
    baseURL: "http://vaultdrop-load-balancer-549238236.us-east-1.elb.amazonaws.com",
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});
