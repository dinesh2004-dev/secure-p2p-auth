import React, { useState } from 'react';
import { Download, FileText, AlertCircle, CheckCircle, User } from 'lucide-react';
import { fileAPI } from '../services/api';
import { authService } from '../services/auth';

const RequestFile: React.FC = () => {
  const [fileName, setFileName] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  
  const currentIdentity = authService.getPeerIdentity();

  const handleRequestFile = async () => {
    if (!fileName.trim() || !currentIdentity) return;

    setLoading(true);
    setMessage(null);

    try {
      const response = await fileAPI.requestFile(fileName, currentIdentity);
      if(response.data.toString().startsWith("File received")){
          setMessage({ type: 'success', text: 'File request sent successfully!' });
          setFileName('');
      }
      else{
          setMessage({ type: 'error', text: response.data });
      }
      
      // You could handle the response data here if the API returns file content
      console.log('File response:', response.data);
    } catch (err: any) {
      setMessage({ 
        type: 'error', 
        text: err.response?.data?.message || 'Failed to request file' 
      });
    } finally {
      setLoading(false);
    }
  };

  if (!currentIdentity) {
    return (
      <div className="max-w-2xl mx-auto">
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6 text-center">
          <AlertCircle className="w-12 h-12 text-yellow-600 mx-auto mb-4" />
          <h3 className="text-lg font-semibold text-yellow-900 mb-2">
            Peer Identity Required
          </h3>
          <p className="text-yellow-800">
            Please select your peer identity from the dashboard before requesting files.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto space-y-8">
      <div className="text-center">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">Request File</h1>
        <p className="text-gray-600">
          Request files from other peers in the network
        </p>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <div className="flex items-center space-x-3 mb-6">
          <div className="bg-purple-100 p-2 rounded-lg">
            <User className="w-6 h-6 text-purple-600" />
          </div>
          <div>
            <h3 className="text-lg font-semibold text-gray-900">Current Identity</h3>
            <p className="text-sm text-gray-600">You are operating as Peer {currentIdentity}</p>
          </div>
        </div>

        <div className="mb-6">
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

        {message && (
          <div className={`mb-6 p-4 rounded-lg flex items-center space-x-3 ${
            message.type === 'success' 
              ? 'bg-green-50 border border-green-200' 
              : 'bg-red-50 border border-red-200'
          }`}>
            {message.type === 'success' ? (
              <CheckCircle className="w-5 h-5 text-green-600 flex-shrink-0" />
            ) : (
              <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0" />
            )}
            <p className={`text-sm ${
              message.type === 'success' ? 'text-green-800' : 'text-red-800'
            }`}>
              {message.text}
            </p>
          </div>
        )}

        <button
          onClick={handleRequestFile}
          disabled={!fileName.trim() || loading}
          className="w-full bg-purple-600 text-white py-3 px-4 rounded-lg hover:bg-purple-700 focus:ring-2 focus:ring-purple-500 focus:ring-offset-2 transition-all duration-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2"
        >
          <Download className="w-5 h-5" />
          <span>{loading ? 'Requesting File...' : 'Request File'}</span>
        </button>

        <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
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
  );
};

export default RequestFile;