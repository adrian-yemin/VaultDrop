import "./TopNav.css";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../auth/AuthContext";

export default function PostLoginTopNav() {
    const navigate = useNavigate();
    const { logout } = useAuth();

    const handleLogout = () => {
        logout();
        navigate("/");
    };

    return (
        <nav className="topnav">
            <div className="topnav-logo">VaultDrop</div>
            <div className="topnav-actions">
                <button
                    onClick={handleLogout}
                    className="link-button"
                >
                    Log Out
                </button>
            </div>
        </nav>
    );
}
