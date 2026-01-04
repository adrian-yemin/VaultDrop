import { useAuth } from "../auth/AuthContext";
import { useNavigate } from "react-router-dom";

export default function DashboardPage() {
    const { token, logout } = useAuth();
    const navigate = useNavigate();

    if (!token) {
        navigate("/login");
        return null;
    }

    return (
        <div style={{ padding: "2rem" }}>
            <h1>Dashboard</h1>
            <p>You are logged in.</p>

            <button
                onClick={() => {
                    logout();
                    navigate("/");
                }}
            >
                Logout
            </button>
        </div>
    );
}
