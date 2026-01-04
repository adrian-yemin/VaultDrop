import { api } from "./ApiClient.ts";

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
