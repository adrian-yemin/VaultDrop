import { useState } from "react";
import FileUploadBox from "./FileUploadBox";
import ShareLinkDisplay from "./ShareLinkDisplay";
import { uploadAnonymousFile } from "../../services/FileService";
import "./AnonymousUpload.css";

export default function AnonymousUploadSection() {
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [shareLink, setShareLink] = useState<string | null>(null);
    const [isUploading, setIsUploading] = useState(false);

    const [oneTimeUse, setOneTimeUse] = useState(false);
    const [maxDownloads, setMaxDownloads] = useState(1);
    const [expiresAt, setExpiresAt] = useState<string | undefined>(undefined);


    const handleUpload = async () => {
        if (!selectedFile) return;

        try {
            setIsUploading(true);

            const result = await uploadAnonymousFile({
                file: selectedFile,
                oneTimeUse,
                maxDownloads: oneTimeUse ? 1 : maxDownloads,
                expiresAt,
            });

            setShareLink(result.data);
        } catch (err) {
            console.error(err);
            alert("Upload failed");
        }
        finally {
            setIsUploading(false);
        }
    };


    return (
        <section className="upload-section">
            <h1>Upload a File and Get a Shareable Link</h1>
            <p className="subtitle">No Account Required</p>

            <div className="upload-options-card">
                <h2 className="options-title">Upload Options</h2>

                <label className="checkbox">
                    <input
                        type="checkbox"
                        checked={oneTimeUse}
                        onChange={(e) => {
                            setOneTimeUse(e.target.checked)
                            setMaxDownloads(1)
                        }}
                    />
                    One-Time Use Link
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
            </div>

            <FileUploadBox
                onFileChange={(file) => {
                    setSelectedFile(file);
                    setShareLink(null);
                }}
                onUpload={handleUpload}
                disabled={!selectedFile || isUploading || maxDownloads < 1}
            />

            {shareLink ? (
                <ShareLinkDisplay link={shareLink} />
            ) : (
                <p className="share-link-placeholder">
                    Your share link will appear here after upload.
                </p>
            )}
        </section>
    );
}
