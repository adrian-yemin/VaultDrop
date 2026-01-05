import { api } from "./ApiClient.ts";
import type { FileDTO } from "../types/File";
import type { ShareLinkDTO } from "../types/ShareLink";

export type AnonymousUploadRequest = {
    file: File;
    oneTimeUse: boolean;
    maxDownloads: number;
    expiresAt?: string;
};

export type AnonymousUploadResponse = {
    success: boolean;
    message: string;
    data: string;
};

export async function uploadAnonymousFile(
    request: AnonymousUploadRequest
): Promise<AnonymousUploadResponse> {
    const formData = new FormData();

    formData.append("file", request.file);
    formData.append("oneTimeUse", String(request.oneTimeUse));
    formData.append("maxDownloads", String(request.maxDownloads));

    if (request.expiresAt) {
        formData.append("expiresAt", request.expiresAt);
    }

    const response = await api.post<AnonymousUploadResponse>(
        "/api/upload_anonymous",
        formData,
        {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        }
    );

    return response.data;
}

export function getMyFiles() {
    return api.get<{ data: FileDTO[] }>("/api/my/files");
}

export function deleteFile(fileId: string) {
    return api.delete(`/api/my/files/${fileId}`);
}

export function uploadFileAuth(file: File) {
    const formData = new FormData();
    formData.append("file", file);
    return api.post("/api/upload", formData);
}

export function getMyShareLinks() {
    return api.get<{ data: ShareLinkDTO[] }>("/api/my/share/links");
}

export function createShareLink(payload: {
    fileId: string;
    oneTimeUse: boolean;
    maxDownloads: number;
    expiresAt?: string;
}) {
    return api.post("/api/share", payload);
}

export function deleteShareLink(token: string) {
    return api.delete(`/api/my/share/delete/${token}`);
}
