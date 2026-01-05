import {useState} from "react";
import "../AnonymousUpload/AnonymousUpload.css"

interface ShareLinkOptions {
    oneTimeUse: boolean;
    maxDownloads: number;
    expiresAt?: string;
}

interface Props {
    onSubmit: (options: ShareLinkOptions) => void;
    onCancel: () => void;
}

export default function ShareLinkOptionsForm({ onSubmit, onCancel }: Props) {
    const [oneTimeUse, setOneTimeUse] = useState(false);
    const [maxDownloads, setMaxDownloads] = useState(1);
    const [expiresAt, setExpiresAt] = useState<string | undefined>(undefined);

    return (
        <div className="share-form">
            <label className="checkbox">
                <input
                    type="checkbox"
                    checked={oneTimeUse}
                    onChange={(e) => {
                        setOneTimeUse(e.target.checked)
                        setMaxDownloads(1)
                    }}
                />
                One-time use link
            </label>

            <label className="input-group">
                Maximum Number of Downloads
                <input
                    type="number"
                    min={1}
                    value={oneTimeUse ? 1 : maxDownloads === 0 ? "" : maxDownloads}
                    onChange={(e) => {
                        if (oneTimeUse) return;
                        const val = e.target.value;
                        setMaxDownloads(val === "" ? 0 : Math.max(1, Number(val)));
                    }}
                    disabled={oneTimeUse}
                />
            </label>

            <label className="input-group">
                Expires At (Optional)
                <input
                    type="datetime-local"
                    onChange={(e) =>
                        setExpiresAt(
                            e.target.value ? new Date(e.target.value).toISOString() : undefined
                        )
                    }
                />
            </label>

            <div className="actions">
                <button onClick={onCancel}>Cancel</button>
                <button
                    onClick={() =>
                        onSubmit({
                            oneTimeUse,
                            maxDownloads,
                            expiresAt: expiresAt || undefined,
                        })
                    }
                >
                    Create Share Link
                </button>
            </div>
        </div>
    );
}
