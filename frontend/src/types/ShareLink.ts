export interface ShareLinkDTO {
    token: string;
    url: string;
    fileId: string;
    fileName: string;
    oneTimeUse: boolean;
    maxDownloads: number;
    downloadsUsed: number;
    expiresAt?: string;
    expired: boolean;
}
