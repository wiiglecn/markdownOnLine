import client from './client';

export interface Version {
  id: number;
  fileId: number;
  versionNumber: number;
  createdBy: string;
  createdAt: string;
}

export const versionsApi = {
  listVersions: (fileId: number) =>
    client.get<Version[]>(`/files/${fileId}/versions`),

  createVersion: (fileId: number) =>
    client.post<Version>(`/files/${fileId}/versions`),

  restoreVersion: (fileId: number, versionId: number) =>
    client.post<Version>(`/files/${fileId}/versions/${versionId}/restore`),
};
