import React from 'react';
import { Users, CheckCircle } from 'lucide-react';
import { authService } from '../services/auth';

interface IdentitySelectorProps {
  selectedIdentity: number | null;
  onIdentityChange: (identity: number) => void;
}

const IdentitySelector: React.FC<IdentitySelectorProps> = ({
  selectedIdentity,
  onIdentityChange,
}) => {
  const handleIdentitySelect = (identity: number) => {
    onIdentityChange(identity);
    authService.setPeerIdentity(identity);
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
      <div className="flex items-center space-x-3 mb-6">
        <div className="bg-purple-100 p-2 rounded-lg">
          <Users className="w-6 h-6 text-purple-600" />
        </div>
        <div>
          <h3 className="text-lg font-semibold text-gray-900">Select Peer Identity</h3>
          <p className="text-sm text-gray-600">Choose your role in the network</p>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {[1, 2].map((identity) => (
          <button
            key={identity}
            onClick={() => handleIdentitySelect(identity)}
            className={`relative p-4 rounded-lg border-2 transition-all duration-200 ${
              selectedIdentity === identity
                ? 'border-purple-500 bg-purple-50 shadow-md'
                : 'border-gray-200 hover:border-purple-300 hover:bg-purple-25'
            }`}
          >
            <div className="flex items-center justify-between">
              <div className="text-left">
                <h4 className="font-semibold text-gray-900">Peer {identity}</h4>
                <p className="text-sm text-gray-600">Network participant {identity}</p>
              </div>
              {selectedIdentity === identity && (
                <CheckCircle className="w-6 h-6 text-purple-600" />
              )}
            </div>
          </button>
        ))}
      </div>

      {selectedIdentity && (
        <div className="mt-4 p-3 bg-green-50 border border-green-200 rounded-lg">
          <p className="text-sm text-green-800">
            ✅ You are now operating as <strong>Peer {selectedIdentity}</strong>
          </p>
        </div>
      )}
    </div>
  );
};

export default IdentitySelector;