import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthCard from "../components/Authentication/AuthCard";
import { login as loginApi } from "../services/AuthService";
import { useAuth } from "../auth/AuthContext";
import AuthTopNav from "../components/TopNav/AuthTopNav.tsx";

export default function LoginPage() {
    const navigate = useNavigate();
    const { login } = useAuth();
    const [error, setError] = useState("");

    const handleLogin = async (email: string, password: string) => {
        setError("");
        try {
            const res = await loginApi(email, password);
            login(res.data.data);
            navigate("/dashboard");
        } catch (err: any) {
            setError(err.response?.data?.message || "Invalid credentials");
        }
    };

    return (
        <>
            <AuthTopNav></AuthTopNav>
            <AuthCard
                title="Login"
                submitText="Login"
                onSubmit={handleLogin}
            />
            {error && <p className="auth-error">{error}</p>}
        </>
    );
}
