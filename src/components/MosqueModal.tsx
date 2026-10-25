import React, { useState } from 'react';
import { Mosque } from '../types';
import { X, Building2, Plus, Check, KeyRound } from 'lucide-react';

interface MosqueModalProps {
  isOpen: boolean;
  onClose: () => void;
  allMosques: Mosque[];
  activeMosqueId: string;
  onSelectMosque: (id: string) => void;
  onCreateMosque: (data: {
    name: string;
    shortName: string;
    code: string;
    address: string;
    city: string;
    phone: string;
    adminName: string;
    adminEmail: string;
    adminPhone: string;
  }) => void;
  onJoinMosque: (code: string, name: string, phone: string) => void;
}

export const MosqueModal: React.FC<MosqueModalProps> = ({
  isOpen,
  onClose,
  allMosques,
  activeMosqueId,
  onSelectMosque,
  onCreateMosque,
  onJoinMosque,
}) => {
  const [view, setView] = useState<'LIST' | 'CREATE' | 'JOIN'>('LIST');

  // Form states for Create
  const [name, setName] = useState('');
  const [shortName, setShortName] = useState('');
  const [code, setCode] = useState('');
  const [address, setAddress] = useState('');
  const [city, setCity] = useState('لاہور');
  const [phone, setPhone] = useState('');
  const [adminName, setAdminName] = useState('');
  const [adminPhone, setAdminPhone] = useState('');

  // Form states for Join
  const [inviteCode, setInviteCode] = useState('');
  const [userName, setUserName] = useState('');
  const [userPhone, setUserPhone] = useState('');

  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleCreateSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      setError('مسجد کا مکمل نام درج کرنا لازمی ہے۔');
      return;
    }
    onCreateMosque({
      name: name.trim(),
      shortName: shortName.trim() || name.slice(0, 15).trim(),
      code: (code.trim() || 'MSQ').toUpperCase(),
      address: address.trim(),
      city: city.trim(),
      phone: phone.trim(),
      adminName: adminName.trim() || 'ایڈمن',
      adminEmail: 'admin@masjid.pk',
      adminPhone: adminPhone.trim(),
    });
    setView('LIST');
    onClose();
  };

  const handleJoinSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inviteCode.trim() || !userName.trim()) {
      setError('دعوتی کوڈ اور آپ کا نام لازمی ہیں۔');
      return;
    }
    onJoinMosque(inviteCode.trim().toUpperCase(), userName.trim(), userPhone.trim());
    setView('LIST');
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white dark:bg-gray-900 rounded-2xl max-w-lg w-full shadow-2xl border border-gray-200 dark:border-gray-800 overflow-hidden text-right">
        
        {/* Modal Header */}
        <div className="p-4 border-b border-gray-200 dark:border-gray-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Building2 className="w-5 h-5 text-islamic-green" />
            <h2 className="font-bold text-base sm:text-lg text-gray-900 dark:text-white">
              {view === 'LIST' && 'مسجد کا انتخاب یا نیا ورکسپیس'}
              {view === 'CREATE' && 'نئی مسجد کا اندراج'}
              {view === 'JOIN' && 'دعوتی کوڈ کے ذریعے شمولیت'}
            </h2>
          </div>
          <button onClick={onClose} className="p-1 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* View Content */}
        <div className="p-6">
          {view === 'LIST' && (
            <div className="space-y-4">
              <p className="text-xs text-gray-500 dark:text-gray-400">
                ہر مسجد کا الگ نام، لوگو اور مکمل محفوظ ڈیٹا ہے۔ متعلقہ مسجد کا انتخاب کریں:
              </p>

              <div className="space-y-2 max-h-60 overflow-y-auto">
                {allMosques.map((m) => {
                  const isSelected = m.id === activeMosqueId;
                  return (
                    <div
                      key={m.id}
                      onClick={() => {
                        onSelectMosque(m.id);
                        onClose();
                      }}
                      className={`p-3 rounded-xl border flex items-center justify-between cursor-pointer transition-all ${
                        isSelected
                          ? 'border-islamic-green bg-emerald-50 dark:bg-emerald-950/40 text-islamic-green dark:text-emerald-300'
                          : 'border-gray-200 dark:border-gray-800 hover:border-gray-300 dark:hover:border-gray-700 bg-gray-50/50 dark:bg-gray-800/40'
                      }`}
                    >
                      <div className="flex items-center gap-3 min-w-0">
                        <div className={`w-9 h-9 rounded-full flex items-center justify-center shrink-0 ${
                          isSelected ? 'bg-islamic-green text-white' : 'bg-gold text-white'
                        }`}>
                          <Building2 className="w-4 h-4" />
                        </div>
                        <div className="min-w-0">
                          <h4 className="font-bold text-sm text-gray-900 dark:text-white truncate">
                            {m.name}
                          </h4>
                          <p className="text-xs text-gray-500 dark:text-gray-400">
                            {m.code} • {m.city} • {m.phone}
                          </p>
                        </div>
                      </div>

                      {isSelected && (
                        <Check className="w-5 h-5 text-islamic-green shrink-0" />
                      )}
                    </div>
                  );
                })}
              </div>

              <div className="pt-2 flex gap-2">
                <button
                  onClick={() => {
                    setError(null);
                    setView('CREATE');
                  }}
                  className="flex-1 flex items-center justify-center gap-1.5 py-2.5 rounded-xl bg-islamic-green hover:bg-islamic-green-dark text-white font-medium text-xs sm:text-sm shadow transition-colors"
                >
                  <Plus className="w-4 h-4" />
                  <span>نئی مسجد بنائیں</span>
                </button>

                <button
                  onClick={() => {
                    setError(null);
                    setView('JOIN');
                  }}
                  className="flex-1 flex items-center justify-center gap-1.5 py-2.5 rounded-xl border border-gray-300 dark:border-gray-700 hover:bg-gray-100 dark:hover:bg-gray-800 text-gray-800 dark:text-gray-200 font-medium text-xs sm:text-sm transition-colors"
                >
                  <KeyRound className="w-4 h-4 text-gold-dark" />
                  <span>کوڈ سے شامل ہوں</span>
                </button>
              </div>
            </div>
          )}

          {view === 'CREATE' && (
            <form onSubmit={handleCreateSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  مسجد کا مکمل نام *
                </label>
                <input
                  type="text"
                  required
                  placeholder="مثلاً: جامع مسجد نورانیہ رضویہ"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-islamic-green"
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                    مختصر نام
                  </label>
                  <input
                    type="text"
                    placeholder="جامع مسجد"
                    value={shortName}
                    onChange={(e) => setShortName(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-islamic-green"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                    رسید کوڈ (3-4 حروف)
                  </label>
                  <input
                    type="text"
                    placeholder="JMN"
                    value={code}
                    onChange={(e) => setCode(e.target.value.toUpperCase())}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm uppercase focus:outline-none focus:ring-2 focus:ring-islamic-green"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                    شہر *
                  </label>
                  <input
                    type="text"
                    required
                    value={city}
                    onChange={(e) => setCity(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-islamic-green"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                    رابطہ نمبر
                  </label>
                  <input
                    type="text"
                    placeholder="0300-1234567"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-islamic-green"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  مسجد کا پتہ
                </label>
                <input
                  type="text"
                  placeholder="محلہ، گلی، مین روڈ"
                  value={address}
                  onChange={(e) => setAddress(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-islamic-green"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  سپر ایڈمن کا نام *
                </label>
                <input
                  type="text"
                  required
                  placeholder="محمد اکرم"
                  value={adminName}
                  onChange={(e) => setAdminName(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-islamic-green"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  ایڈمن کا موبائل نمبر
                </label>
                <input
                  type="text"
                  placeholder="03xx-xxxxxxx"
                  value={adminPhone}
                  onChange={(e) => setAdminPhone(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-islamic-green"
                />
              </div>

              {error && <p className="text-xs text-red-500">{error}</p>}

              <div className="pt-2 flex gap-2">
                <button
                  type="button"
                  onClick={() => setView('LIST')}
                  className="flex-1 py-2 rounded-xl border border-gray-300 dark:border-gray-700 text-xs font-semibold"
                >
                  واپس
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 rounded-xl bg-islamic-green hover:bg-islamic-green-dark text-white text-xs font-bold shadow"
                >
                  ورکسپیس بنائیں
                </button>
              </div>
            </form>
          )}

          {view === 'JOIN' && (
            <form onSubmit={handleJoinSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  دعوتی کوڈ (Invite Code) *
                </label>
                <input
                  type="text"
                  required
                  placeholder="مثلاً: HMR-FIN یا JMN-REC"
                  value={inviteCode}
                  onChange={(e) => setInviteCode(e.target.value.toUpperCase())}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm uppercase tracking-wider font-bold focus:outline-none focus:ring-2 focus:ring-islamic-green"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  آپ کا نام *
                </label>
                <input
                  type="text"
                  required
                  placeholder="محمد عثمان"
                  value={userName}
                  onChange={(e) => setUserName(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-islamic-green"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  موبائل نمبر
                </label>
                <input
                  type="text"
                  placeholder="03xx-xxxxxxx"
                  value={userPhone}
                  onChange={(e) => setUserPhone(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-islamic-green"
                />
              </div>

              {error && <p className="text-xs text-red-500">{error}</p>}

              <div className="pt-2 flex gap-2">
                <button
                  type="button"
                  onClick={() => setView('LIST')}
                  className="flex-1 py-2 rounded-xl border border-gray-300 dark:border-gray-700 text-xs font-semibold"
                >
                  واپس
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 rounded-xl bg-islamic-green hover:bg-islamic-green-dark text-white text-xs font-bold shadow"
                >
                  مسجد میں شامل ہوں
                </button>
              </div>
            </form>
          )}

        </div>
      </div>
    </div>
  );
};
