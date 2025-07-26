
import React, { useState, useEffect } from 'react';
import { authService } from '../services/auth';
import { fileAPI } from '../services/api';
import { Shield, Key, Lock, CheckCircle, Wifi, FileCheck } from 'lucide-react';

interface SecurityStatusProps {
  isAuthenticated: boolean;
  hasIdentity: boolean;
}

// Update SecurityStatus.tsx
const SecurityStatus: React.FC<SecurityStatusProps> = ({
  isAuthenticated,
  hasIdentity,
}) => {
  const [cryptoStatus, setCryptoStatus] = useState({
    keysLoaded: false,
    ecdhCompleted: false,
    aesGenerated: false,
    ecdsaVerified: false,
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // let interval: NodeJS.Timeout;
    let interval: ReturnType<typeof setInterval>;

    const checkCryptoStatus = async () => {
      if (isAuthenticated && hasIdentity) {
        try {
          setLoading(true);
          const peerIdentity = authService.getPeerIdentity();
          const response = await fileAPI.getCryptoStatus(peerIdentity!);
          
          const status = response.data;
          setCryptoStatus({
            keysLoaded: status.keysLoaded,
            ecdhCompleted: status.ecdhCompleted,
            aesGenerated: status.aesGenerated,
            ecdsaVerified: status.ecdsaVerified,
          });

          // Update localStorage
          authService.setKeysLoaded(status.keysLoaded);
          authService.setECDHCompleted(status.ecdhCompleted);
          authService.setAESGenerated(status.aesGenerated);
          authService.setECDSAVerified(status.ecdsaVerified);

        } catch (error) {
          console.error('Failed to check crypto status:', error);
          setCryptoStatus({
            keysLoaded: false,
            ecdhCompleted: false,
            aesGenerated: false,
            ecdsaVerified: false,
          });
        } finally {
          setLoading(false);
        }
      }
    };

    // Initial check
    checkCryptoStatus();

    // Poll every 3 seconds for real-time updates
    interval = setInterval(checkCryptoStatus, 3000);

    return () => clearInterval(interval);
  }, [isAuthenticated, hasIdentity]);

  const securitySteps = [
    {
      id: 1,
      title: 'User Authentication',
      description: 'JWT token verified',
      icon: Shield,
      completed: isAuthenticated,
    },
    {
      id: 2,
      title: 'Peer Identity Selected',
      description: 'Network role established',
      icon: CheckCircle,
      completed: hasIdentity,
    },
    {
      id: 3,
      title: 'Key Pair Loaded',
      description: loading ? 'Checking keys...' : 'Cryptographic keys ready',
      icon: Key,
      completed: isAuthenticated && hasIdentity && cryptoStatus.keysLoaded,
    },
    {
      id: 4,
      title: 'ECDH Handshake',
      description: 'Secure key agreement established',
      icon: Wifi,
      completed: isAuthenticated && hasIdentity && cryptoStatus.ecdhCompleted,
    },
    {
      id: 5,
      title: 'AES Encryption',
      description: 'File transfer protection active',
      icon: Lock,
      completed: isAuthenticated && hasIdentity && cryptoStatus.aesGenerated,
    },
    {
      id: 6,
      title: 'ECDSA Signature',
      description: 'Integrity verification ready',
      icon: FileCheck,
      completed: isAuthenticated && hasIdentity && cryptoStatus.ecdsaVerified,
    },
  ];

  // ... rest of component


  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
      <div className="flex items-center space-x-3 mb-6">
        <div className="bg-green-100 p-2 rounded-lg">
          <Shield className="w-6 h-6 text-green-600" />
        </div>
        <div>
          <h3 className="text-lg font-semibold text-gray-900">Security Status</h3>
          <p className="text-sm text-gray-600">System security verification</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {securitySteps.map((step) => {
          const IconComponent = step.icon;
          return (
            <div
              key={step.id}
              className={`p-4 rounded-lg border transition-all duration-200 ${
                step.completed
                  ? 'border-green-200 bg-green-50'
                  : 'border-gray-200 bg-gray-50'
              }`}
            >
              <div className="flex items-start space-x-3">
                <div
                  className={`p-2 rounded-lg ${
                    step.completed
                      ? 'bg-green-100 text-green-600'
                      : 'bg-gray-100 text-gray-400'
                  }`}
                >
                  <IconComponent className="w-5 h-5" />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center space-x-2">
                    <h4 className="text-sm font-semibold text-gray-900">
                      {step.title}
                    </h4>
                    {step.completed && (
                      <CheckCircle className="w-4 h-4 text-green-600 flex-shrink-0" />
                    )}
                  </div>
                  <p className="text-xs text-gray-600 mt-1">
                    {step.description}
                  </p>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      <div className="mt-6 p-4 rounded-lg bg-gradient-to-r from-blue-50 to-purple-50 border border-blue-200">
        <div className="flex items-center space-x-2">
          <Shield className="w-5 h-5 text-blue-600" />
          <p className="text-sm font-medium text-blue-900">
            System Security: {isAuthenticated && hasIdentity ? 'Active' : 'Pending Setup'}
          </p>
        </div>
        <p className="text-xs text-blue-700 mt-1">
          {isAuthenticated && hasIdentity
            ? 'All security protocols are active and your files are protected with end-to-end encryption.'
            : 'Complete authentication and peer selection to activate security protocols.'}
        </p>
      </div>
    </div>
  );
};

export default SecurityStatus;