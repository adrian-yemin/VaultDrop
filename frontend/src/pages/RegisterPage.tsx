import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthCard from "../components/Authentication/AuthCard";
import { register as registerApi } from "../services/AuthService";
import AuthTopNav from "../components/TopNav/AuthTopNav.tsx";

export default function RegisterPage() {
    const navigate = useNavigate();
    const [error, setError] = useState("");

    const handleRegister = async (email: string, password: string) => {
        setError("");
        try {
            await registerApi(email, password);
            navigate("/login");
        } catch (err: any) {
            setError(err.response?.data?.message || "Username already exists");
        }
    };

    return (
        <>
            <AuthTopNav></AuthTopNav>
            <AuthCard
                title="Create an Account"
                submitText="Register"
                onSubmit={handleRegister}
            />
            {error && <p className="auth-error">{error}</p>}
        </>
    );
}

