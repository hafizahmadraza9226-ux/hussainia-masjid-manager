import React from 'react';
import { Mosque, Receipt } from '../types';
import { formatPKR } from '../utils/numberToWords';
import { formatUrduDateTime } from '../utils/dateUtils';
import { X, Share2, Printer, CheckCircle } from 'lucide-react';

interface ReceiptModalProps {
  receipt: Receipt | null;
  mosque: Mosque | null;
  onClose: () => void;
  onCancelReceipt?: (receipt: Receipt) => void;
}

export const ReceiptModal: React.FC<ReceiptModalProps> = ({
  receipt,
  mosque,
  onClose,
  onCancelReceipt,
}) => {
  if (!receipt) return null;

  const handleShareWhatsApp = () => {
    const text = [
      `🕌 *${mosque?.name || 'جامع مسجد'}*`,
      `━━━━━━━━━━━━━━━━━━━`,
      `📄 *رسید نمبر:* ${receipt.receiptNumber}`,
      `📅 *تاریخ:* ${formatUrduDateTime(receipt.issueDate)}`,
      `👤 *محترم جناب:* ${receipt.donorName}`,
      receipt.donorPhone ? `📱 *موبائل:* ${receipt.donorPhone}` : '',
      `💰 *رقم:* ${formatPKR(receipt.amount)}`,
      `📝 *الفاظ میں:* ${receipt.amountInWords}`,
      `🎯 *مقصد:* ${receipt.purpose}`,
      `💳 *ادائیگی کا طریقہ:* ${receipt.paymentMethodUrdu}`,
      `━━━━━━━━━━━━━━━━━━━`,
      mosque?.receiptNote || 'اللہ تعالیٰ آپ کے مالی تعاون کو قبول فرمائے۔ آمین۔',
      `منتظمین: ${mosque?.name}`,
    ].filter(Boolean).join('\n');

    const url = `https://api.whatsapp.com/send?text=${encodeURIComponent(text)}`;
    window.open(url, '_blank');
  };

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white dark:bg-gray-900 rounded-2xl max-w-lg w-full shadow-2xl border border-gold/40 overflow-hidden text-right">
        
        {/* Header bar */}
        <div className="bg-gradient-to-l from-islamic-green to-islamic-green-dark p-4 text-white flex items-center justify-between">
          <div className="flex items-center gap-2">
            <CheckCircle className="w-5 h-5 text-gold" />
            <span className="font-bold text-sm">سرکاری رسید (Official Receipt)</span>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg hover:bg-white/20 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Printable Receipt Body */}
        <div className="p-6 space-y-4">
          
          {/* Mosque Branding on Receipt */}
          <div className="border-b border-gray-200 dark:border-gray-800 pb-4 text-center">
            <h2 className="text-xl font-bold text-islamic-green dark:text-emerald-400">
              {mosque?.name || 'جامع مسجد'}
            </h2>
            <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
              {mosque?.address} • {mosque?.city} • فون: {mosque?.phone}
            </p>
          </div>

          {/* Receipt Info */}
          <div className="flex justify-between items-center bg-gray-50 dark:bg-gray-800/60 p-3 rounded-xl border border-gray-100 dark:border-gray-800 text-xs">
            <div>
              <span className="text-gray-500 dark:text-gray-400">تاریخ و وقت: </span>
              <span className="font-semibold text-gray-800 dark:text-gray-200">{formatUrduDateTime(receipt.issueDate)}</span>
            </div>
            <div>
              <span className="text-gray-500 dark:text-gray-400">رسید نمبر: </span>
              <span className="font-bold text-islamic-green dark:text-emerald-400 bg-emerald-100 dark:bg-emerald-950 px-2 py-0.5 rounded">
                {receipt.receiptNumber}
              </span>
            </div>
          </div>

          {/* Donor & Details Table */}
          <div className="space-y-2 text-sm">
            <div className="flex justify-between py-1.5 border-b border-gray-100 dark:border-gray-800">
              <span className="text-gray-500 dark:text-gray-400">معاون / چندہ دہندہ:</span>
              <span className="font-bold text-gray-900 dark:text-white">{receipt.donorName}</span>
            </div>

            {receipt.donorPhone && (
              <div className="flex justify-between py-1.5 border-b border-gray-100 dark:border-gray-800">
                <span className="text-gray-500 dark:text-gray-400">موبائل نمبر:</span>
                <span className="font-medium text-gray-700 dark:text-gray-300">{receipt.donorPhone}</span>
              </div>
            )}

            {receipt.donorAddress && (
              <div className="flex justify-between py-1.5 border-b border-gray-100 dark:border-gray-800">
                <span className="text-gray-500 dark:text-gray-400">پتہ / محلہ:</span>
                <span className="font-medium text-gray-700 dark:text-gray-300">{receipt.donorAddress}</span>
              </div>
            )}

            <div className="flex justify-between py-1.5 border-b border-gray-100 dark:border-gray-800">
              <span className="text-gray-500 dark:text-gray-400">آمدن کی مد / مقصد:</span>
              <span className="font-bold text-gray-800 dark:text-gray-200">{receipt.purpose}</span>
            </div>

            <div className="flex justify-between py-1.5 border-b border-gray-100 dark:border-gray-800">
              <span className="text-gray-500 dark:text-gray-400">ادائیگی کا طریقہ:</span>
              <span className="font-medium text-gray-700 dark:text-gray-300">{receipt.paymentMethodUrdu}</span>
            </div>
          </div>

          {/* Amount Box */}
          <div className="bg-gradient-to-r from-gold-subtle to-yellow-50 dark:from-yellow-950/40 dark:to-yellow-900/20 p-4 rounded-xl border border-gold/40 text-center">
            <div className="text-xs text-gold-dark dark:text-gold font-semibold">موصولہ رقم</div>
            <div className="text-2xl font-black text-gray-900 dark:text-white mt-1">
              {formatPKR(receipt.amount)}
            </div>
            <div className="text-xs font-semibold text-gold-dark dark:text-gold mt-1">
              الفاظ میں: {receipt.amountInWords}
            </div>
          </div>

          {/* Dua */}
          <p className="text-xs text-center text-islamic-green dark:text-emerald-400 italic">
            "{mosque?.receiptNote || 'اللہ تعالیٰ آپ کے مالی تعاون کو قبول فرمائے۔ آمین۔'}"
          </p>

          <div className="text-xs text-gray-500 dark:text-gray-400 pt-2 flex justify-between">
            <span>جاری کنندہ: {receipt.issuedBy}</span>
            <span>اسٹیٹس: {receipt.status === 'CONFIRMED' ? 'منظور شدہ' : 'منسوخ'}</span>
          </div>

        </div>

        {/* Footer Actions */}
        <div className="bg-gray-50 dark:bg-gray-800/80 p-4 border-t border-gray-200 dark:border-gray-800 flex items-center justify-between gap-2">
          {onCancelReceipt && receipt.status === 'CONFIRMED' && (
            <button
              onClick={() => onCancelReceipt(receipt)}
              className="px-3 py-2 rounded-xl text-xs font-semibold text-red-600 hover:bg-red-50 dark:hover:bg-red-950/40 transition-colors"
            >
              منسوخ کریں
            </button>
          )}

          <div className="flex items-center gap-2 mr-auto">
            <button
              onClick={handlePrint}
              className="flex items-center gap-1.5 px-3 py-2 rounded-xl bg-gray-200 dark:bg-gray-700 text-gray-800 dark:text-gray-200 text-xs font-bold hover:bg-gray-300 transition-colors"
            >
              <Printer className="w-4 h-4" />
              <span>پرنٹ</span>
            </button>

            <button
              onClick={handleShareWhatsApp}
              className="flex items-center gap-1.5 px-4 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold shadow transition-colors"
            >
              <Share2 className="w-4 h-4" />
              <span>واٹس ایپ پر بھیجیں</span>
            </button>
          </div>
        </div>

      </div>
    </div>
  );
};
