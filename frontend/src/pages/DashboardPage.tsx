import { useEffect, useState } from "react";
import PostLoginTopNav from "../components/TopNav/PostLoginTopNav.tsx";
import FileList from "../components/Dashboard/FileList";
import ShareLinkList from "../components/Dashboard/ShareLinkList";
import {
    getMyFiles,
    getMyShareLinks,
    uploadFileAuth,
    deleteFile,
    createShareLink,
    deleteShareLink,
} from "../services/FileService";
import type { FileDTO } from "../types/File";
import type { ShareLinkDTO } from "../types/ShareLink";
import "../components/Dashboard/Dashboard.css"
import Modal from "../components/Dashboard/Modal.tsx";
import ShareLinkOptionsForm from "../components/Dashboard/ShareLinkOptionsForm.tsx";

export default function DashboardPage() {
    const [files, setFiles] = useState<FileDTO[]>([]);
    const [links, setLinks] = useState<ShareLinkDTO[]>([]);
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [uploading, setUploading] = useState(false);
    const [shareFileId, setShareFileId] = useState<string | null>(null);

    const loadData = async () => {
        try {
            const [filesRes, linksRes] = await Promise.all([
                getMyFiles(),
                getMyShareLinks(),
            ]);

            setFiles([...filesRes.data.data]);
            setLinks([...linksRes.data.data]);
        } catch (err) {
            console.error("Failed to load dashboard data", err);
        }
    };

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        void loadData();
    }, []);

    const handleUpload = async () => {
        if (!selectedFile) return;

        setUploading(true);
        await uploadFileAuth(selectedFile);
        setUploading(false);

        setSelectedFile(null);
        await loadData();
    };

    const handleDeleteFile = async (fileId: string) => {
        await deleteFile(fileId);
        await loadData();
    };

    const handleCreateShareLink = async (options: {
        oneTimeUse: boolean;
        maxDownloads: number;
        expiresAt?: string;
    }) => {
        if (!shareFileId) return;

        const adjustedOptions = {
            ...options,
            oneTimeUse: options.maxDownloads === 1 ? true : options.oneTimeUse,
        };

        await createShareLink({
            fileId: shareFileId,
            ...adjustedOptions,
        });

        setShareFileId(null);
        await loadData();
    };


    const handleDeleteShareLink = async (token: string) => {
        try {
            await deleteShareLink(token);
        } catch (err) {
            console.error(err);
        }
        await loadData();
    };

    return (
        <>
            <PostLoginTopNav />

            <button className="reload-btn" onClick={() => void loadData()}>Reload</button>

            <main className="dashboard">
                <section className="dashboard-column">
                    <h2>Your Files</h2>

                    <div className="upload-row">
                        <input
                            type="file"
                            onChange={(e) => {
                                const file = e.target.files?.[0] ?? null;
                                setSelectedFile(file);
                            }}
                            disabled={uploading}
                        />

                        <button
                            onClick={handleUpload}
                            disabled={!selectedFile || uploading}
                        >
                            {uploading ? "Uploading..." : "Upload"}
                        </button>
                    </div>

                    <FileList
                        files={files}
                        onDelete={handleDeleteFile}
                        onShare={(fileId) => setShareFileId(fileId)}
                    />
                </section>

                <section className="dashboard-column">
                    <h2>Your Share Links</h2>

                    <ShareLinkList
                        links={links}
                        onDelete={handleDeleteShareLink}
                    />
                </section>
            </main>
            <Modal open={shareFileId !== null} onClose={() => setShareFileId(null)}>
                <h2>Create Share Link</h2>
                <ShareLinkOptionsForm
                    onSubmit={handleCreateShareLink}
                    onCancel={() => setShareFileId(null)}
                />
            </Modal>

        </>
    );
}
