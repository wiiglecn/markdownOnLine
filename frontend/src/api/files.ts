import client from './client';

export interface Folder {
  id: number;
  name: string;
  parentId: number | null;
  createdAt: string;
}

export interface FileItem {
  id: number;
  name: string;
  contentType: 'TEXT' | 'MARKDOWN';
  folderId: number | null;
  createdAt: string;
  updatedAt: string;
}

export const filesApi = {
  listFolders: (parentId?: number | null) =>
    client.get<Folder[]>('/folders', { params: { parentId } }),

  createFolder: (name: string, parentId?: number | null) =>
    client.post<Folder>('/folders', { name, parentId }),

  deleteFolder: (id: number) =>
    client.delete(`/folders/${id}`),

  listFiles: (folderId?: number | null) =>
    client.get<FileItem[]>('/files', { params: { folderId } }),

  createFile: (name: string, contentType: 'TEXT' | 'MARKDOWN', folderId?: number | null) =>
    client.post<FileItem>('/files', { name, contentType, folderId }),

  getFile: (id: number) =>
    client.get<FileItem>(`/files/${id}`),

  renameFile: (id: number, name: string) =>
    client.put<FileItem>(`/files/${id}`, { name }),

  deleteFile: (id: number) =>
    client.delete(`/files/${id}`),
};
