import React, { useState } from 'react';
import { Receipt } from '../types';
import { formatPKR } from '../utils/numberToWords';
import { formatUrduDate } from '../utils/dateUtils';
import { Search, Receipt as ReceiptIcon, ArrowLeft } from 'lucide-react';

interface ReceiptsSectionProps {
  receipts: Receipt[];
  onSelectReceipt: (receipt: Receipt) => void;
}

export const ReceiptsSection: React.FC<ReceiptsSectionProps> = ({
  receipts,
  onSelectReceipt,
}) => {
  const [search, setSearch] = useState('');
  const [filterConfirmed, setFilterConfirmed] = useState(false);

  const filtered = receipts.filter(r => {
    const matchesSearch =
      r.receiptNumber.toLowerCase().includes(search.toLowerCase()) ||
      r.donorName.toLowerCase().includes(search.toLowerCase()) ||
      r.donorPhone.includes(search);
    const matchesStatus = !filterConfirmed || r.status === 'CONFIRMED';
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-4">
      {/* Header and Filter */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-bold text-gray-900 dark:text-white">
            رسیدوں کا مکمل نظام
          </h2>
          <p className="text-xs text-gray-500 dark:text-gray-400">
            کل رسیدیں: {receipts.length} • ہر رسید کا خودکار یونیک سیریل نمبر
          </p>
        </div>

        <button
          onClick={() => setFilterConfirmed(!filterConfirmed)}
          className={`px-3 py-1.5 rounded-lg text-xs font-semibold border transition-colors ${
            filterConfirmed
              ? 'bg-emerald-100 dark:bg-emerald-950/60 border-emerald-400 text-emerald-700 dark:text-emerald-300'
              : 'border-gray-300 dark:border-gray-700 text-gray-600 dark:text-gray-300'
          }`}
        >
          صرف تصدیق شدہ
        </button>
      </div>

      {/* Search Input */}
      <div className="relative">
        <Search className="w-4 h-4 text-gray-400 absolute right-3 top-3" />
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="رسید نمبر، نام یا موبائل نمبر سے تلاش کریں..."
          className="w-full pl-4 pr-10 py-2.5 rounded-xl border border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-islamic-green"
        />
      </div>

      {/* List */}
      <div className="space-y-2">
        {filtered.length === 0 ? (
          <div className="text-center py-12 text-gray-400">کوئی رسید نہیں ملی۔</div>
        ) : (
          filtered.map((r) => {
            const isCancelled = r.status === 'CANCELLED';
            return (
              <div
                key={r.id}
                onClick={() => onSelectReceipt(r)}
                className={`p-4 rounded-2xl border cursor-pointer transition-all flex items-center justify-between shadow-sm ${
                  isCancelled
                    ? 'bg-gray-100 dark:bg-gray-800/40 border-gray-200 dark:border-gray-800 opacity-75'
                    : 'bg-white dark:bg-gray-800 border-gray-200 dark:border-gray-700/80 hover:border-islamic-green'
                }`}
              >
                <div className="flex items-center gap-3">
                  <div className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 ${
                    isCancelled ? 'bg-red-100 text-red-600' : 'bg-emerald-100 dark:bg-emerald-950 text-emerald-600'
                  }`}>
                    <ReceiptIcon className="w-5 h-5" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="font-bold text-sm text-gray-900 dark:text-white">
                        {r.donorName}
                      </span>
                      {isCancelled && (
                        <span className="text-[10px] font-bold text-red-600 bg-red-100 dark:bg-red-950/60 px-1.5 py-0.5 rounded">
                          منسوخ شدہ
                        </span>
                      )}
                    </div>
                    <p className="text-xs text-gray-500 dark:text-gray-400">
                      {r.purpose} • {formatUrduDate(r.issueDate)}
                    </p>
                    <span className="inline-block mt-0.5 text-xs font-bold text-islamic-green dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-950/50 px-1.5 py-0.5 rounded">
                      {r.receiptNumber}
                    </span>
                  </div>
                </div>

                <div className="text-left flex items-center gap-2">
                  <span className={`text-base font-bold ${isCancelled ? 'text-gray-500 line-through' : 'text-emerald-600 dark:text-emerald-400'}`}>
                    {formatPKR(r.amount)}
                  </span>
                  <ArrowLeft className="w-4 h-4 text-gray-400" />
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};
