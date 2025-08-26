import React, { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import api from '../../services/api';
import { toast } from 'sonner';
import { Shield, Copy, CheckCircle, XCircle } from 'lucide-react';

const TwoFactorSetup = ({ onClose, onSuccess }) => {
  const [step, setStep] = useState(1);
  const [setupData, setSetupData] = useState(null);
  const [verificationCode, setVerificationCode] = useState('');
  const [copiedCodes, setCopiedCodes] = useState(false);

  const setupMutation = useMutation({
    mutationFn: async () => {
      const response = await api.post('/auth/2fa/setup');
      return response.data;
    },
    onSuccess: (data) => {
      setSetupData(data);
      setStep(2);
    },
    onError: () => {
      toast.error('Failed to initialize 2FA setup');
    }
  });

  const enableMutation = useMutation({
    mutationFn: async (data) => {
      const response = await api.post('/auth/2fa/enable', data);
      return response.data;
    },
    onSuccess: () => {
      toast.success('Two-factor authentication enabled successfully!');
      onSuccess();
    },
    onError: (error) => {
      toast.error(error.response?.data?.error || 'Failed to enable 2FA');
    }
  });

  const handleSetup = () => {
    setupMutation.mutate();
  };

  const handleVerification = () => {
    if (!verificationCode || verificationCode.length !== 6) {
      toast.error('Please enter a valid 6-digit code');
      return;
    }

    enableMutation.mutate({
      secret: setupData.secret,
      verificationCode,
      recoveryCodes: setupData.recoveryCodes
    });
  };

  const copyRecoveryCodes = () => {
    const codesText = Array.from(setupData.recoveryCodes).join('\n');
    navigator.clipboard.writeText(codesText);
    setCopiedCodes(true);
    toast.success('Recovery codes copied to clipboard');
    setTimeout(() => setCopiedCodes(false), 3000);
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white dark:bg-gray-800 rounded-lg p-6 max-w-md w-full mx-4">
        <div className="flex items-center mb-4">
          <Shield className="w-6 h-6 text-blue-600 mr-2" />
          <h2 className="text-xl font-semibold">Enable Two-Factor Authentication</h2>
        </div>

        {step === 1 && (
          <div className="space-y-4">
            <p className="text-gray-600 dark:text-gray-400">
              Two-factor authentication adds an extra layer of security to your account.
              You'll need to enter a code from your authenticator app in addition to your password.
            </p>

            <div className="bg-blue-50 dark:bg-blue-900/20 p-4 rounded-lg">
              <h3 className="font-medium mb-2">You'll need:</h3>
              <ul className="list-disc list-inside text-sm space-y-1">
                <li>An authenticator app (Google Authenticator, Authy, etc.)</li>
                <li>A secure place to store recovery codes</li>
              </ul>
            </div>

            <div className="flex space-x-3">
              <button
                onClick={onClose}
                className="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700"
              >
                Cancel
              </button>
              <button
                onClick={handleSetup}
                disabled={setupMutation.isPending}
                className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
              >
                {setupMutation.isPending ? 'Setting up...' : 'Continue'}
              </button>
            </div>
          </div>
        )}

        {step === 2 && setupData && (
          <div className="space-y-4">
            <div className="text-center">
              <p className="text-sm text-gray-600 dark:text-gray-400 mb-4">
                Scan this QR code with your authenticator app
              </p>
              <div className="bg-white p-4 rounded-lg inline-block">
                <img src={setupData.qrCode} alt="2FA QR Code" className="w-48 h-48" />
              </div>
              <p className="text-xs text-gray-500 mt-2">
                Can't scan? Enter this code manually: <br />
                <code className="text-xs bg-gray-100 dark:bg-gray-700 px-2 py-1 rounded">
                  {setupData.secret}
                </code>
              </p>
            </div>

            <div>
              <label className="block text-sm font-medium mb-2">
                Enter verification code from your app
              </label>
              <input
                type="text"
                value={verificationCode}
                onChange={(e) => setVerificationCode(e.target.value.replace(/\D/g, '').slice(0, 6))}
                placeholder="000000"
                className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 text-center text-lg"
                maxLength="6"
              />
            </div>

            <button
              onClick={() => setStep(3)}
              disabled={verificationCode.length !== 6}
              className="w-full px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
            >
              Next: Save Recovery Codes
            </button>
          </div>
        )}

        {step === 3 && setupData && (
          <div className="space-y-4">
            <div className="bg-yellow-50 dark:bg-yellow-900/20 p-4 rounded-lg">
              <p className="text-sm font-medium text-yellow-800 dark:text-yellow-200">
                Important: Save these recovery codes
              </p>
              <p className="text-xs text-yellow-700 dark:text-yellow-300 mt-1">
                Use these codes to access your account if you lose your authenticator device.
                Each code can only be used once.
              </p>
            </div>

            <div className="bg-gray-50 dark:bg-gray-700 p-4 rounded-lg">
              <div className="grid grid-cols-2 gap-2 font-mono text-sm">
                {Array.from(setupData.recoveryCodes).map((code, index) => (
                  <div key={index} className="text-center">
                    {code}
                  </div>
                ))}
              </div>
            </div>

            <button
              onClick={copyRecoveryCodes}
              className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 flex items-center justify-center"
            >
              {copiedCodes ? (
                <>
                  <CheckCircle className="w-4 h-4 mr-2 text-green-600" />
                  Copied!
                </>
              ) : (
                <>
                  <Copy className="w-4 h-4 mr-2" />
                  Copy Recovery Codes
                </>
              )}
            </button>

            <div className="flex space-x-3">
              <button
                onClick={() => setStep(2)}
                className="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700"
              >
                Back
              </button>
              <button
                onClick={handleVerification}
                disabled={enableMutation.isPending}
                className="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50"
              >
                {enableMutation.isPending ? 'Enabling...' : 'Enable 2FA'}
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default TwoFactorSetup;
