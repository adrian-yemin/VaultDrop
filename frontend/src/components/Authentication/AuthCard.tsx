import { useState } from "react";
import type {FormEvent} from "react";
import "./Authentication.css"

type AuthCardProps = {
    title: string;
    submitText: string;
    onSubmit: (username: string, password: string) => Promise<void>;
};

export default function AuthCard({ title, submitText, onSubmit }: AuthCardProps) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            await onSubmit(username, password);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="auth-card">
            <h1>{title}</h1>

            <form onSubmit={handleSubmit} className="auth-form">
                <label className="input-group">
                    Username
                    <input
                        type="text"
                        required
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </label>

                <label className="input-group">
                    Password
                    <input
                        type="password"
                        required
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </label>

                <button type="submit" disabled={isLoading}>
                    {isLoading ? "Please wait..." : submitText}
                </button>
            </form>
        </div>
    );
}
