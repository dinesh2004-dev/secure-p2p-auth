// import React, { useState, useEffect } from 'react';
// import { authService } from '../services/auth';
// import IdentitySelector from '../components/IdentitySelector';
// import SecurityStatus from '../components/SecurityStatus';

// const Dashboard: React.FC = () => {
//   const [selectedIdentity, setSelectedIdentity] = useState<number | null>(null);
//   const user = authService.getUser();

//   // useEffect(() => {
//   //   const savedIdentity = authService.getPeerIdentity();
//   //   if (savedIdentity) {
//   //     setSelectedIdentity(savedIdentity);
//   //   }
//   // }, []);

//   const handleIdentityChange = (identity: number) => {
//     setSelectedIdentity(identity);
//   };

//   return (
//     <div className="space-y-8">
//       <div className="text-center">
//         <h1 className="text-4xl font-bold text-gray-900 mb-4">
//           Welcome to SecureP2P
//         </h1>
//         <p className="text-xl text-gray-600 mb-2">
//           Hello, <span className="font-semibold text-blue-600">{user?.username}</span>!
//         </p>
//         <p className="text-gray-500">
//           Your secure peer-to-peer file sharing platform
//         </p>
//       </div>

//       <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
//         <IdentitySelector
//           selectedIdentity={selectedIdentity}
//           onIdentityChange={handleIdentityChange}
//         />
        
//         <SecurityStatus
//           isAuthenticated={authService.isAuthenticated()}
//           hasIdentity={selectedIdentity !== null}
//         />
//       </div>

//       {selectedIdentity && (
//         <div className="bg-gradient-to-r from-green-50 to-blue-50 rounded-xl p-6 border border-green-200">
//           <div className="text-center">
//             <h3 className="text-lg font-semibold text-gray-900 mb-2">
//               🎉 System Ready!
//             </h3>
//             <p className="text-gray-600 mb-4">
//               You're now connected as <strong>Peer {selectedIdentity}</strong>. 
//               You can start sending and receiving files securely.
//             </p>
//             <div className="flex flex-wrap justify-center gap-4">
//               <div className="bg-white px-4 py-2 rounded-lg shadow-sm border border-gray-100">
//                 <span className="text-sm text-gray-600">🔒 End-to-end encryption</span>
//               </div>
//               <div className="bg-white px-4 py-2 rounded-lg shadow-sm border border-gray-100">
//                 <span className="text-sm text-gray-600">🔑 ECDH key exchange</span>
//               </div>
//               <div className="bg-white px-4 py-2 rounded-lg shadow-sm border border-gray-100">
//                 <span className="text-sm text-gray-600">✅ Digital signatures</span>
//               </div>
//             </div>
//           </div>
//         </div>
//       )}
//     </div>
//   );
// };

// export default Dashboard;



import React, { useState, useEffect } from 'react';
import { Upload, Download, Files, FileText, AlertCircle, CheckCircle, User, RefreshCw, Folder } from 'lucide-react';
import { authService } from '../services/auth';
import { fileAPI } from '../services/api';
import IdentitySelector from '../components/IdentitySelector';
import SecurityStatus from '../components/SecurityStatus';

interface FileItem {
  name: string;
  size: number;
  lastModified: string;
}

const Dashboard: React.FC = () => {
  const [selectedIdentity, setSelectedIdentity] = useState<number | null>(null);
  const [activeTab, setActiveTab] = useState<'overview' | 'send' | 'request' | 'files'>('overview');
  
  // Send File states
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [targetPeer, setTargetPeer] = useState<string>('peer1');
  const [sendLoading, setSendLoading] = useState(false);
  const [sendMessage, setSendMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  
  // Request File states
  const [fileName, setFileName] = useState('');
  const [requestLoading, setRequestLoading] = useState(false);
  const [requestMessage, setRequestMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  
  // File List states
  const [peer1Files, setPeer1Files] = useState<FileItem[]>([]);
  const [peer2Files, setPeer2Files] = useState<FileItem[]>([]);
  const [filesLoading, setFilesLoading] = useState(false);
  const [filesError, setFilesError] = useState('');
  
  const user = authService.getUser();

  useEffect(() => {
    const savedIdentity = authService.getPeerIdentity();
    if (savedIdentity) {
      setSelectedIdentity(savedIdentity);
    }
    if (savedIdentity) {
      fetchFiles();
    }
  }, []);

  // const handleIdentityChange = (identity: number) => {
  //   setSelectedIdentity(identity);
  //   fetchFiles();
  // };

  const handleIdentityChange = async (identity: number) => {
  setSelectedIdentity(identity);
  authService.setPeerIdentity(identity);
  
  try {
    // Initialize crypto for this peer
    await fileAPI.initializeCrypto(identity);
  } catch (error) {
    console.error('Failed to initialize crypto:', error);
  }
};

  // Send File Functions
  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setSelectedFile(file);
      setSendMessage(null);
    }
  };

  const handleSendFile = async () => {
    if (!selectedFile || !selectedIdentity) return;

    setSendLoading(true);
    setSendMessage(null);

    try {
      const formData = new FormData();
      formData.append('file', selectedFile);
      formData.append('choice', selectedIdentity.toString());
      formData.append('peerName', targetPeer);

      await fileAPI.sendFile(formData);
      setSendMessage({ type: 'success', text: 'File sent successfully!' });
      setSelectedFile(null);
      fetchFiles(); // Refresh file list
    } catch (err: any) {
      setSendMessage({ 
        type: 'error', 
        text: err.response?.data?.message || 'Failed to send file' 
      });
    } finally {
      setSendLoading(false);
    }
  };

  // Request File Functions
  const handleRequestFile = async () => {
    if (!fileName.trim() || !selectedIdentity) return;

    setRequestLoading(true);
    setRequestMessage(null);

    try {
      const response =await fileAPI.requestFile(fileName, selectedIdentity);
      if(response.data.toString().startsWith("File received")){
      setRequestMessage({ type: 'success', text: 'File request sent successfully!' });
      setFileName('');
      }
      else{
        setRequestMessage({ type: 'error', text: response.data });
      }
      fetchFiles(); // Refresh file list
      
    } catch (err: any) {
      setRequestMessage({ 
        type: 'error', 
        text: err.response?.data?.message || 'Failed to request file' 
      });
    } finally {
      setRequestLoading(false);
    }
  };

  // File List Functions
  const fetchFiles = async () => {
    setFilesLoading(true);
    setFilesError('');

    try {
      const [peer1Response, peer2Response] = await Promise.all([
        fileAPI.getPeer1Files(),
        fileAPI.getPeer2Files(),
      ]);

      setPeer1Files(peer1Response.data || []);
      setPeer2Files(peer2Response.data || []);
    } catch (err: any) {
      setFilesError('Failed to fetch files from peers');
    } finally {
      setFilesLoading(false);
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
      <div className="flex items-center space-x-3 mb-4">
        <div className={`bg-${color}-100 p-2 rounded-lg`}>
          <Folder className={`w-5 h-5 text-${color}-600`} />
        </div>
        <div>
          <h4 className="font-semibold text-gray-900">📁 {peerName}</h4>
          <p className="text-sm text-gray-600">
            {files.length} {files.length === 1 ? 'file' : 'files'}
          </p>
        </div>
      </div>

      {files.length === 0 ? (
        <div className="text-center py-4">
          <FileText className="w-8 h-8 text-gray-400 mx-auto mb-2" />
          <p className="text-sm text-gray-600">No files available</p>
        </div>
      ) : (
        <div className="space-y-2 max-h-48 overflow-y-auto">
          {files.map((file, index) => (
            <div
              key={index}
              className="flex items-center justify-between p-3 rounded-lg border border-gray-200 hover:border-gray-300 transition-colors"
            >
              <div className="flex items-center space-x-2 min-w-0">
                <FileText className="w-4 h-4 text-gray-500 flex-shrink-0" />
                <div className="min-w-0">
                  <p className="text-sm font-medium text-gray-900 truncate">{file.name}</p>
                  <p className="text-xs text-gray-600">{formatDate(file.lastModified)}</p>
                </div>
              </div>
              <p className="text-xs font-medium text-gray-900 flex-shrink-0">
                {formatFileSize(file.size)}
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );

  return (
    <div className="space-y-8">
      <div className="text-center">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">
          Welcome to SecureP2P
        </h1>
        <p className="text-xl text-gray-600 mb-2">
          Hello, <span className="font-semibold text-blue-600">{user?.username}</span>!
        </p>
        <p className="text-gray-500">
          Your secure peer-to-peer file sharing platform
        </p>
      </div>

      {!selectedIdentity ? (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <IdentitySelector
            selectedIdentity={selectedIdentity}
            onIdentityChange={handleIdentityChange}
          />
          
          <SecurityStatus
            isAuthenticated={authService.isAuthenticated()}
            hasIdentity={selectedIdentity !== null}
          />
        </div>
      ) : (
        <>
          {/* Tab Navigation */}
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-2">
            <div className="flex space-x-2">
              {[
                { id: 'overview', label: 'Overview', icon: User },
                { id: 'send', label: 'Send File', icon: Upload },
                { id: 'request', label: 'Request File', icon: Download },
                { id: 'files', label: 'File List', icon: Files },
              ].map((tab) => {
                const IconComponent = tab.icon;
                return (
                  <button
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id as any)}
                    className={`flex items-center space-x-2 px-4 py-2 rounded-lg transition-all duration-200 ${
                      activeTab === tab.id
                        ? 'bg-blue-600 text-white shadow-md'
                        : 'text-gray-600 hover:bg-gray-100'
                    }`}
                  >
                    <IconComponent className="w-4 h-4" />
                    <span className="font-medium">{tab.label}</span>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Tab Content */}
          {activeTab === 'overview' && (
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              <IdentitySelector
                selectedIdentity={selectedIdentity}
                onIdentityChange={handleIdentityChange}
              />
              
              <SecurityStatus
                isAuthenticated={authService.isAuthenticated()}
                hasIdentity={selectedIdentity !== null}
              />
            </div>
          )}

          {activeTab === 'send' && (
            <div className="max-w-2xl mx-auto">
              <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
                <div className="flex items-center space-x-3 mb-6">
                  <div className="bg-blue-100 p-2 rounded-lg">
                    <Upload className="w-6 h-6 text-blue-600" />
                  </div>
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900">Send File</h3>
                    <p className="text-sm text-gray-600">Operating as Peer {selectedIdentity}</p>
                  </div>
                </div>

                <div className="space-y-6">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-3">
                      Select Target Peer
                    </label>
                    <div className="grid grid-cols-2 gap-4">
                      {['peer1', 'peer2'].map((peer) => (
                        <button
                          key={peer}
                          onClick={() => setTargetPeer(peer)}
                          className={`p-3 rounded-lg border-2 transition-all duration-200 ${
                            targetPeer === peer
                              ? 'border-blue-500 bg-blue-50'
                              : 'border-gray-200 hover:border-blue-300'
                          }`}
                        >
                          <div className="text-center">
                            <div className="font-semibold text-gray-900 capitalize">{peer}</div>
                            {targetPeer === peer && (
                              <CheckCircle className="w-4 h-4 text-blue-600 mx-auto mt-1" />
                            )}
                          </div>
                        </button>
                      ))}
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-3">
                      Select File
                    </label>
                    <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
                      {selectedFile ? (
                        <div className="space-y-2">
                          <FileText className="w-8 h-8 text-blue-600 mx-auto" />
                          <p className="font-medium text-gray-900">{selectedFile.name}</p>
                          <p className="text-sm text-gray-600">
                            {(selectedFile.size / 1024 / 1024).toFixed(2)} MB
                          </p>
                          <button
                            onClick={() => setSelectedFile(null)}
                            className="text-sm text-red-600 hover:text-red-700"
                          >
                            Remove file
                          </button>
                        </div>
                      ) : (
                        <div className="space-y-2">
                          <Upload className="w-8 h-8 text-gray-400 mx-auto" />
                          <p className="text-gray-900">Click to select file</p>
                          <input
                            type="file"
                            onChange={handleFileSelect}
                            className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
                          />
                        </div>
                      )}
                    </div>
                  </div>

                  {sendMessage && (
                    <div className={`p-4 rounded-lg flex items-center space-x-3 ${
                      sendMessage.type === 'success' 
                        ? 'bg-green-50 border border-green-200' 
                        : 'bg-red-50 border border-red-200'
                    }`}>
                      {sendMessage.type === 'success' ? (
                        <CheckCircle className="w-5 h-5 text-green-600 flex-shrink-0" />
                      ) : (
                        <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0" />
                      )}
                      <p className={`text-sm ${
                        sendMessage.type === 'success' ? 'text-green-800' : 'text-red-800'
                      }`}>
                        {sendMessage.text}
                      </p>
                    </div>
                  )}

                  <button
                    onClick={handleSendFile}
                    disabled={!selectedFile || sendLoading}
                    className="w-full bg-blue-600 text-white py-3 px-4 rounded-lg hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-all duration-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {sendLoading ? 'Sending File...' : 'Send File Securely'}
                  </button>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'request' && (
            <div className="max-w-2xl mx-auto">
              <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
                <div className="flex items-center space-x-3 mb-6">
                  <div className="bg-purple-100 p-2 rounded-lg">
                    <Download className="w-6 h-6 text-purple-600" />
                  </div>
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900">Request File</h3>
                    <p className="text-sm text-gray-600">Operating as Peer {selectedIdentity}</p>
                  </div>
                </div>

                <div className="space-y-6">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-3">
                      File Name
                    </label>
                    <div className="relative">
                      <FileText className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                      <input
                        type="text"
                        value={fileName}
                        onChange={(e) => setFileName(e.target.value)}
                        placeholder="Enter the name of the file you want to request"
                        className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all duration-200"
                      />
                    </div>
                    <p className="text-sm text-gray-600 mt-2">
                      Enter the exact filename including extension (e.g., document.pdf, image.jpg)
                    </p>
                  </div>

                  {requestMessage && (
                    <div className={`p-4 rounded-lg flex items-center space-x-3 ${
                      requestMessage.type === 'success' 
                        ? 'bg-green-50 border border-green-200' 
                        : 'bg-red-50 border border-red-200'
                    }`}>
                      {requestMessage.type === 'success' ? (
                        <CheckCircle className="w-5 h-5 text-green-600 flex-shrink-0" />
                      ) : (
                        <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0" />
                      )}
                      <p className={`text-sm ${
                        requestMessage.type === 'success' ? 'text-green-800' : 'text-red-800'
                      }`}>
                        {requestMessage.text}
                      </p>
                    </div>
                  )}

                  <button
                    onClick={handleRequestFile}
                    disabled={!fileName.trim() || requestLoading}
                    className="w-full bg-purple-600 text-white py-3 px-4 rounded-lg hover:bg-purple-700 focus:ring-2 focus:ring-purple-500 focus:ring-offset-2 transition-all duration-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2"
                  >
                    <Download className="w-5 h-5" />
                    <span>{requestLoading ? 'Requesting File...' : 'Request File'}</span>
                  </button>

                  <div className="p-4 bg-blue-50 border border-blue-200 rounded-lg">
                    <div className="flex items-start space-x-3">
                      <div className="bg-blue-100 p-1 rounded">
                        <Download className="w-4 h-4 text-blue-600" />
                      </div>
                      <div>
                        <h4 className="text-sm font-semibold text-blue-900">How File Requests Work</h4>
                        <p className="text-sm text-blue-800 mt-1">
                          When you request a file, the system securely searches for it across the peer network. 
                          If found, it will be transferred to you using end-to-end encryption.
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'files' && (
            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-xl font-semibold text-gray-900">File Directory</h3>
                  <p className="text-gray-600">Browse files available across the peer network</p>
                </div>
                <button
                  onClick={fetchFiles}
                  disabled={filesLoading}
                  className="flex items-center space-x-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <RefreshCw className={`w-4 h-4 ${filesLoading ? 'animate-spin' : ''}`} />
                  <span>Refresh</span>
                </button>
              </div>

              {filesError && (
                <div className="bg-red-50 border border-red-200 rounded-lg p-4 flex items-center space-x-3">
                  <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0" />
                  <p className="text-red-800">{filesError}</p>
                </div>
              )}

              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {renderFileList(peer1Files, 'Peer1', 'blue')}
                {renderFileList(peer2Files, 'Peer2', 'purple')}
              </div>

              <div className="bg-gradient-to-r from-green-50 to-blue-50 rounded-xl p-6 border border-green-200">
                <div className="flex items-start space-x-3">
                  <div className="bg-green-100 p-2 rounded-lg">
                    <Files className="w-6 h-6 text-green-600" />
                  </div>
                  <div>
                    <h4 className="text-lg font-semibold text-gray-900 mb-2">
                      Secure File Network
                    </h4>
                    <p className="text-gray-600 mb-3">
                      All files in the network are protected with end-to-end encryption. 
                      File transfers use AES encryption with ECDH key exchange for maximum security.
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
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}
        </>
      )}

      {selectedIdentity && (
        <div className="bg-gradient-to-r from-green-50 to-blue-50 rounded-xl p-6 border border-green-200 mt-8">
          <div className="text-center">
            <h3 className="text-lg font-semibold text-gray-900 mb-2">
              🎉 System Ready!
            </h3>
            <p className="text-gray-600 mb-4">
              You're now connected as <strong>Peer {selectedIdentity}</strong>. 
              Use the tabs above to send files, request files, or browse the network.
            </p>
            <div className="flex flex-wrap justify-center gap-4">
              <div className="bg-white px-4 py-2 rounded-lg shadow-sm border border-gray-100">
                <span className="text-sm text-gray-600">🔒 End-to-end encryption</span>
              </div>
              <div className="bg-white px-4 py-2 rounded-lg shadow-sm border border-gray-100">
                <span className="text-sm text-gray-600">🔑 ECDH key exchange</span>
              </div>
              <div className="bg-white px-4 py-2 rounded-lg shadow-sm border border-gray-100">
                <span className="text-sm text-gray-600">✅ Digital signatures</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;