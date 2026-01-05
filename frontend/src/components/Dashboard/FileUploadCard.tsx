import {useState} from "react";
import { uploadFileAuth } from "../../services/FileService";

export default function FileUploadCard({ onUploaded }: { onUploaded: () => void }) {
    const [file, setFile] = useState<File | null>(null);

    const handleUpload = async () => {
        if (!file) return;
        await uploadFileAuth(file);
        setFile(null);
        onUploaded();
    };

    return (
        <div className="card">
            <h3>Upload File</h3>
            <input type="file" onChange={e => setFile(e.target.files?.[0] ?? null)} />
            <button onClick={handleUpload} disabled={!file}>
                Upload
            </button>
        </div>
    );
}
