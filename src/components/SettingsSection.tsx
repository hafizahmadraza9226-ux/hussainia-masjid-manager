import React, { useState } from 'react';
import { Mosque, MosqueMember } from '../types';
import { Building2, Users, Copy, Check, ShieldCheck, Moon, Sun, MessageSquare } from 'lucide-react';

interface SettingsSectionProps {
  mosque: Mosque | null;
  members: MosqueMember[];
  isDarkMode: boolean;
  onToggleTheme: () => void;
  onUpdateMosque: (updated: Mosque) => void;
}

export const SettingsSection: React.FC<SettingsSectionProps> = ({
  mosque,
  members,
  isDarkMode,
  onToggleTheme,
  onUpdateMosque,
}) => {
  const [copiedCode, setCopiedCode] = useState<string | null>(null);

  // Mosque form states
  const [name, setName] = useState(mosque?.name || '');
  const [address, setAddress] = useState(mosque?.address || '');
  const [phone, setPhone] = useState(mosque?.phone || '');
  const [receiptNote, setReceiptNote] = useState(mosque?.receiptNote || '');
  const [smsSender, setSmsSender] = useState(mosque?.smsSenderName || '');
  const [smsKey, setSmsKey] = useState(mosque?.smsApiKey || '');

  const handleCopy = (code: string) => {
    navigator.clipboard?.writeText(code);
    setCopiedCode(code);
    setTimeout(() => setCopiedCode(null), 2000);
  };

  const handleSaveProfile = (e: React.FormEvent) => {
    e.preventDefault();
    if (!mosque) return;
    onUpdateMosque({
      ...mosque,
      name: name.trim(),
      address: address.trim(),
      phone: phone.trim(),
      receiptNote: receiptNote.trim(),
      smsSenderName: smsSender.trim(),
      smsApiKey: smsKey.trim(),
    });
    alert('مسجد کی ترتیبات کامیابی سے محفوظ ہو گئیں!');
  };

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      
      {/* Theme Card */}
      <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-full bg-gold-subtle dark:bg-yellow-950/60 text-gold-dark flex items-center justify-center">
            {isDarkMode ? <Moon className="w-5 h-5" /> : <Sun className="w-5 h-5" />}
          </div>
          <div>
            <h3 className="font-bold text-sm text-gray-900 dark:text-white">
              ڈارک تھیم (Dark Mode)
            </h3>
            <p className="text-xs text-gray-500 dark:text-gray-400">
              {isDarkMode ? 'ڈارک موڈ فعال ہے' : 'لائٹ موڈ فعال ہے'}
            </p>
          </div>
        </div>

        <button
          onClick={onToggleTheme}
          className="px-4 py-2 rounded-xl bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 font-bold text-xs transition-colors"
        >
          {isDarkMode ? 'لائٹ موڈ کریں' : 'ڈارک موڈ کریں'}
        </button>
      </div>

      {/* 4-Users and Invite Codes */}
      <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Users className="w-5 h-5 text-islamic-green" />
            <h3 className="font-bold text-base text-gray-900 dark:text-white">
              مسجد کے 4 مجاز صارفین اور دعوتی کوڈز
            </h3>
          </div>
          <span className="text-xs bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-300 font-bold px-2.5 py-1 rounded-full">
            {members.length}/4 صارفین
          </span>
        </div>

        <p className="text-xs text-gray-500 dark:text-gray-400 leading-relaxed">
          ہر مسجد کے لیے زیادہ سے زیادہ 4 محفوظ رولز ہیں۔ ساتھی اراکین کو جوڑنے کے لیے نیچے دیا گیا دعوتی کوڈ کاپی کر کے بھیجیں تاکہ وہ ایپ میں شامل ہو سکیں:
        </p>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2">
          {members.map((m) => (
            <div
              key={m.id}
              className="p-3.5 rounded-xl border border-gray-100 dark:border-gray-750 bg-gray-50/50 dark:bg-gray-800/60 flex items-center justify-between"
            >
              <div>
                <div className="flex items-center gap-1.5">
                  <ShieldCheck className="w-4 h-4 text-gold-dark" />
                  <span className="font-bold text-xs text-gray-900 dark:text-white">
                    {m.userName}
                  </span>
                </div>
                <p className="text-[11px] text-gray-500 dark:text-gray-400 mt-0.5">
                  رول: {m.role === 'SUPER_ADMIN' ? 'سپر ایڈمن' : m.role === 'FINANCE_MANAGER' ? 'فنانس مینیجر' : m.role === 'RECORD_MANAGER' ? 'ریکارڈ مینیجر' : 'ناظر'}
                </p>
              </div>

              <div className="flex items-center gap-2">
                <span className="px-2 py-0.5 rounded bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-300 font-mono font-bold text-xs">
                  {m.inviteCode}
                </span>
                <button
                  onClick={() => handleCopy(m.inviteCode)}
                  className="p-1.5 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-300 transition-colors"
                  title="دعوتی کوڈ کاپی کریں"
                >
                  {copiedCode === m.inviteCode ? (
                    <Check className="w-4 h-4 text-emerald-600" />
                  ) : (
                    <Copy className="w-4 h-4" />
                  )}
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Mosque Profile Settings */}
      <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-4">
        <div className="flex items-center gap-2">
          <Building2 className="w-5 h-5 text-islamic-green" />
          <h3 className="font-bold text-base text-gray-900 dark:text-white">
            مسجد کی معلومات و کوائف
          </h3>
        </div>

        <form onSubmit={handleSaveProfile} className="space-y-3">
          <div>
            <label className="block text-xs font-semibold mb-1">مسجد کا مکمل نام</label>
            <input
              type="text"
              required
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold mb-1">رابطہ نمبر</label>
              <input
                type="text"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold mb-1">مسجد کا پتہ</label>
              <input
                type="text"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold mb-1">رسید پر درج اختیاری دعا و عبارت</label>
            <input
              type="text"
              value={receiptNote}
              onChange={(e) => setReceiptNote(e.target.value)}
              className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
            />
          </div>

          {/* SMS Settings */}
          <div className="border-t border-gray-100 dark:border-gray-700 pt-3 space-y-3">
            <div className="flex items-center gap-2">
              <MessageSquare className="w-4 h-4 text-islamic-green" />
              <h4 className="text-xs font-bold text-gray-900 dark:text-white">
                SMS گیٹ وے سیٹنگز (برائے خودکار رسید SMS)
              </h4>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs text-gray-500 mb-1">Sender Name (Sender ID)</label>
                <input
                  type="text"
                  placeholder="HMR-Masjid"
                  value={smsSender}
                  onChange={(e) => setSmsSender(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs text-gray-500 mb-1">SMS API Key (اختیاری)</label>
                <input
                  type="password"
                  placeholder="Secret API Key"
                  value={smsKey}
                  onChange={(e) => setSmsKey(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>
            </div>
          </div>

          <div className="pt-2">
            <button
              type="submit"
              className="px-6 py-2.5 rounded-xl bg-islamic-green hover:bg-islamic-green-dark text-white font-bold text-xs sm:text-sm shadow transition-colors"
            >
              تبدیلیاں محفوظ کریں
            </button>
          </div>
        </form>
      </div>

    </div>
  );
};
