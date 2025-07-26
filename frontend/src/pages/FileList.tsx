// import React, { useState, useEffect } from 'react';
// import { Files, RefreshCw, AlertCircle, FileText, Folder } from 'lucide-react';
// import { fileAPI } from '../services/api';

// interface FileItem {
//   name: string;
//   size: number;
//   lastModified: string;
// }

// const FileList: React.FC = () => {
//   const [peer1Files, setPeer1Files] = useState<FileItem[]>([]);
//   const [peer2Files, setPeer2Files] = useState<FileItem[]>([]);
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState('');

//   const fetchFiles = async () => {
//     setLoading(true);
//     setError('');

//     try {
//       const [peer1Response, peer2Response] = await Promise.all([
//         fileAPI.getPeer1Files(),
//         fileAPI.getPeer2Files(),
//       ]);

//       setPeer1Files(peer1Response.data || []);
//       setPeer2Files(peer2Response.data || []);
//     } catch (err: any) {
//       setError('Failed to fetch files from peers');
//       console.error('Error fetching files:', err);
//     } finally {
//       setLoading(false);
//     }
//   };

//   useEffect(() => {
//     fetchFiles();
//   }, []);

//   const formatFileSize = (bytes: number) => {
//     if (bytes === 0) return '0 Bytes';
//     const k = 1024;
//     const sizes = ['Bytes', 'KB', 'MB', 'GB'];
//     const i = Math.floor(Math.log(bytes) / Math.log(k));
//     return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
//   };

//   const formatDate = (dateString: string) => {
//     return new Date(dateString).toLocaleDateString('en-US', {
//       year: 'numeric',
//       month: 'short',
//       day: 'numeric',
//       hour: '2-digit',
//       minute: '2-digit',
//     });
//   };

//   const renderFileList = (files: FileItem[], peerName: string, color: string) => (
//     <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
//       <div className="flex items-center justify-between mb-6">
//         <div className="flex items-center space-x-3">
//           <div className={`bg-${color}-100 p-2 rounded-lg`}>
//             <Folder className={`w-6 h-6 text-${color}-600`} />
//           </div>
//           <div>
//             <h3 className="text-lg font-semibold text-gray-900">
//               📁 Files in {peerName}
//             </h3>
//             <p className="text-sm text-gray-600">
//               {files.length} {files.length === 1 ? 'file' : 'files'} available
//             </p>
//           </div>
//         </div>
//       </div>

//       {files.length === 0 ? (
//         <div className="text-center py-8">
//           <FileText className="w-12 h-12 text-gray-400 mx-auto mb-4" />
//           <p className="text-gray-600">No files available in this peer</p>
//         </div>
//       ) : (
//         <div className="space-y-3">
//           {files.map((file, index) => (
//             <div
//               key={index}
//               className="flex items-center justify-between p-4 rounded-lg border border-gray-200 hover:border-gray-300 transition-colors"
//             >
//               <div className="flex items-center space-x-3">
//                 <FileText className="w-5 h-5 text-gray-500" />
//                 <div>
//                   <p className="font-medium text-gray-900">{file.name}</p>
//                   <p className="text-sm text-gray-600">
//                     Modified: {formatDate(file.lastModified)}
//                   </p>
//                 </div>
//               </div>
//               <div className="text-right">
//                 <p className="text-sm font-medium text-gray-900">
//                   {formatFileSize(file.size)}
//                 </p>
//               </div>
//             </div>
//           ))}
//         </div>
//       )}
//     </div>
//   );

//   return (
//     <div className="space-y-8">
//       <div className="flex items-center justify-between">
//         <div>
//           <h1 className="text-3xl font-bold text-gray-900 mb-4">File Directory</h1>
//           <p className="text-gray-600">
//             Browse files available across the peer network
//           </p>
//         </div>
//         <button
//           onClick={fetchFiles}
//           disabled={loading}
//           className="flex items-center space-x-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
//         >
//           <RefreshCw className={`w-5 h-5 ${loading ? 'animate-spin' : ''}`} />
//           <span>Refresh</span>
//         </button>
//       </div>

//       {error && (
//         <div className="bg-red-50 border border-red-200 rounded-lg p-4 flex items-center space-x-3">
//           <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0" />
//           <p className="text-red-800">{error}</p>
//         </div>
//       )}

//       <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
//         {renderFileList(peer1Files, 'Peer1', 'blue')}
//         {renderFileList(peer2Files, 'Peer2', 'purple')}
//       </div>

//       <div className="bg-gradient-to-r from-green-50 to-blue-50 rounded-xl p-6 border border-green-200">
//         <div className="flex items-start space-x-3">
//           <div className="bg-green-100 p-2 rounded-lg">
//             <Files className="w-6 h-6 text-green-600" />
//           </div>
//           <div>
//             <h3 className="text-lg font-semibold text-gray-900 mb-2">
//               Secure File Network
//             </h3>
//             <p className="text-gray-600 mb-3">
//               All files in the network are protected with end-to-end encryption. 
//               File transfers use AES encryption with ECDH key exchange for maximum security.
//             </p>
//             <div className="flex flex-wrap gap-2">
//               <span className="bg-white px-3 py-1 rounded-full text-sm text-gray-700 border border-gray-200">
//                 🔒 AES Encryption
//               </span>
//               <span className="bg-white px-3 py-1 rounded-full text-sm text-gray-700 border border-gray-200">
//                 🔑 ECDH Key Exchange
//               </span>
//               <span className="bg-white px-3 py-1 rounded-full text-sm text-gray-700 border border-gray-200">
//                 ✅ Digital Signatures
//               </span>
//             </div>
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default FileList;
import React, { useState, useEffect } from 'react';
import { Files, RefreshCw, AlertCircle, FileText, Folder, Download, Loader2 } from 'lucide-react';
import { fileAPI } from '../services/api';
import { authService } from '../services/auth';

interface FileItem {
  name: string;
  size: number;
  lastModified: string;
}

const FileList: React.FC = () => {
  const [peer1Files, setPeer1Files] = useState<FileItem[]>([]);
  const [peer2Files, setPeer2Files] = useState<FileItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [downloadingFiles, setDownloadingFiles] = useState<Set<string>>(new Set());

  const fetchFiles = async () => {
    setLoading(true);
    setError('');

    try {
      const [peer1Response, peer2Response] = await Promise.all([
        fileAPI.getPeer1Files(),
        fileAPI.getPeer2Files(),
      ]);

      setPeer1Files(peer1Response.data || []);
      setPeer2Files(peer2Response.data || []);
    } catch (err: any) {
      setError('Failed to fetch files from peers');
      console.error('Error fetching files:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFiles();
  }, []);

  const handleFileRequest = async (fileName: string) => {
    const peerIdentity = authService.getPeerIdentity();
    
    if (!peerIdentity) {
      setError('Please select a peer identity first');
      return;
    }

    // Add to downloading set
    setDownloadingFiles(prev => new Set(prev).add(fileName));
    setError('');

    try {
      console.log(`🔍 Requesting file: ${fileName} with peer identity: ${peerIdentity}`);
      
      const response = await fileAPI.requestFile(fileName, peerIdentity);

      if (response.data.toString().startsWith("File received")) {
        // Success message
        setError(''); // Clear any previous errors
        console.log(`✅ File downloaded successfully: ${fileName}`);
        
        // Show success notification (you can replace this with a toast)
        alert(`File "${fileName}" downloaded successfully to your Downloads folder!`);
      }
      else{
        alert(`⚠️ Error downloading file: ${response.data}`);
      }
    } catch (err: any) {
      console.error('❌ Error requesting file:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to download file';
      setError(`Failed to download "${fileName}": ${errorMessage}`);
    } finally {
      // Remove from downloading set
      setDownloadingFiles(prev => {
        const newSet = new Set(prev);
        newSet.delete(fileName);
        return newSet;
      });
    }
  };

  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const renderFileList = (files: FileItem[], peerName: string, color: string) => (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center space-x-3">
          <div className={`bg-${color}-100 p-2 rounded-lg`}>
            <Folder className={`w-6 h-6 text-${color}-600`} />
          </div>
          <div>
            <h3 className="text-lg font-semibold text-gray-900">
              📁 Files in {peerName}
            </h3>
            <p className="text-sm text-gray-600">
              {files.length} {files.length === 1 ? 'file' : 'files'} available
            </p>
          </div>
        </div>
      </div>

      {files.length === 0 ? (
        <div className="text-center py-8">
          <FileText className="w-12 h-12 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-600">No files available in this peer</p>
        </div>
      ) : (
        <div className="space-y-3">
          {files.map((file, index) => {
            const isDownloading = downloadingFiles.has(file.name);
            return (
              <div
                key={index}
                className="flex items-center justify-between p-4 rounded-lg border border-gray-200 hover:border-gray-300 transition-colors"
              >
                <div className="flex items-center space-x-3 flex-1">
                  <FileText className="w-5 h-5 text-gray-500" />
                  <div className="flex-1">
                    <p className="font-medium text-gray-900">{file.name}</p>
                    <p className="text-sm text-gray-600">
                      Modified: {formatDate(file.lastModified)}
                    </p>
                  </div>
                </div>
                
                <div className="flex items-center space-x-4">
                  <div className="text-right">
                    <p className="text-sm font-medium text-gray-900">
                      {formatFileSize(file.size)}
                    </p>
                  </div>
                  
                  <button
                    onClick={() => handleFileRequest(file.name)}
                    disabled={isDownloading}
                    className={`
                      flex items-center space-x-2 px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200
                      ${isDownloading 
                        ? 'bg-gray-100 text-gray-400 cursor-not-allowed' 
                        : `bg-${color}-600 text-white hover:bg-${color}-700 focus:ring-2 focus:ring-${color}-500 focus:ring-offset-2`
                      }
                    `}
                  >
                    {isDownloading ? (
                      <>
                        <Loader2 className="w-4 h-4 animate-spin" />
                        <span>Downloading...</span>
                      </>
                    ) : (
                      <>
                        <Download className="w-4 h-4" />
                        <span>Request</span>
                      </>
                    )}
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 mb-4">File Directory</h1>
          <p className="text-gray-600">
            Browse and download files available across the peer network
          </p>
        </div>
        <button
          onClick={fetchFiles}
          disabled={loading}
          className="flex items-center space-x-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <RefreshCw className={`w-5 h-5 ${loading ? 'animate-spin' : ''}`} />
          <span>Refresh</span>
        </button>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 flex items-center space-x-3">
          <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0" />
          <p className="text-red-800">{error}</p>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {renderFileList(peer1Files, 'Peer1', 'blue')}
        {renderFileList(peer2Files, 'Peer2', 'purple')}
      </div>

      <div className="bg-gradient-to-r from-green-50 to-blue-50 rounded-xl p-6 border border-green-200">
        <div className="flex items-start space-x-3">
          <div className="bg-green-100 p-2 rounded-lg">
            <Files className="w-6 h-6 text-green-600" />
          </div>
          <div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">
              Secure File Network
            </h3>
            <p className="text-gray-600 mb-3">
              All files in the network are protected with end-to-end encryption. 
              File transfers use AES encryption with ECDH key exchange for maximum security.
              Click "Request" to securely download any file to your Downloads folder.
            </p>
            <div className="flex flex-wrap gap-2">
              <span className="bg-white px-3 py-1 rounded-full text-sm text-gray-700 border border-gray-200">
                🔒 AES Encryption
              </span>
              <span className="bg-white px-3 py-1 rounded-full text-sm text-gray-700 border border-gray-200">
                🔑 ECDH Key Exchange
              </span>
              <span className="bg-white px-3 py-1 rounded-full text-sm text-gray-700 border border-gray-200">
                ✅ Digital Signatures
              </span>
              <span className="bg-white px-3 py-1 rounded-full text-sm text-gray-700 border border-gray-200">
                📥 Secure Downloads
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FileList;