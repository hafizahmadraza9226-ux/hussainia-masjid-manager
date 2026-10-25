import React, { useState } from 'react';
import { Income, Donor, IncomeCategory, PaymentMethod, Receipt } from '../types';
import { formatPKR, numberToUrduWords } from '../utils/numberToWords';
import { formatUrduDate } from '../utils/dateUtils';
import { Plus, Search, UserCheck } from 'lucide-react';

interface IncomeSectionProps {
  incomes: Income[];
  donors: Donor[];
  onAddIncome: (data: {
    amount: number;
    category: IncomeCategory;
    donorName: string;
    donorPhone: string;
    donorAddress: string;
    purpose: string;
    paymentMethod: PaymentMethod;
    notes: string;
  }) => Receipt;
  onAddDonor: (data: {
    name: string;
    phone: string;
    address: string;
    pledgedMonthlyAmount: number;
    notes: string;
  }) => void;
  onSelectReceipt: (receipt: Receipt) => void;
  receipts: Receipt[];
}

export const IncomeSection: React.FC<IncomeSectionProps> = ({
  incomes,
  donors,
  onAddIncome,
  onAddDonor,
  onSelectReceipt,
  receipts,
}) => {
  const [activeSubTab, setActiveSubTab] = useState<'INCOME' | 'DONORS'>('INCOME');
  const [showAddIncomeModal, setShowAddIncomeModal] = useState(false);
  const [showAddDonorModal, setShowAddDonorModal] = useState(false);
  const [search, setSearch] = useState('');

  // Form states for Income
  const [amountStr, setAmountStr] = useState('');
  const [category, setCategory] = useState<IncomeCategory>('عمومی چندہ');
  const [donorName, setDonorName] = useState('');
  const [donorPhone, setDonorPhone] = useState('');
  const [donorAddress, setDonorAddress] = useState('');
  const [purpose, setPurpose] = useState('');
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('نقد (Cash)');
  const [notes, setNotes] = useState('');

  // Form states for Donor
  const [dName, setDName] = useState('');
  const [dPhone, setDPhone] = useState('');
  const [dAddress, setDAddress] = useState('');
  const [dPledgedStr, setDPledgedStr] = useState('');
  const [dNotes, setDNotes] = useState('');

  const numAmount = parseFloat(amountStr) || 0;

  const handleIncomeSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (numAmount <= 0 || !donorName.trim()) return;

    const receipt = onAddIncome({
      amount: numAmount,
      category,
      donorName: donorName.trim(),
      donorPhone: donorPhone.trim(),
      donorAddress: donorAddress.trim(),
      purpose: purpose.trim() || category,
      paymentMethod,
      notes: notes.trim(),
    });

    setShowAddIncomeModal(false);
    setAmountStr('');
    setDonorName('');
    setDonorPhone('');
    setDonorAddress('');
    setPurpose('');
    onSelectReceipt(receipt);
  };

  const handleDonorSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const pledged = parseFloat(dPledgedStr) || 0;
    if (!dName.trim() || pledged <= 0) return;

    onAddDonor({
      name: dName.trim(),
      phone: dPhone.trim(),
      address: dAddress.trim(),
      pledgedMonthlyAmount: pledged,
      notes: dNotes.trim(),
    });

    setShowAddDonorModal(false);
    setDName('');
    setDPhone('');
    setDAddress('');
    setDPledgedStr('');
  };

  const filteredIncomes = incomes.filter(i =>
    i.donorName.toLowerCase().includes(search.toLowerCase()) ||
    i.receiptNumber.toLowerCase().includes(search.toLowerCase()) ||
    i.donorPhone.includes(search)
  );

  const filteredDonors = donors.filter(d =>
    d.name.toLowerCase().includes(search.toLowerCase()) ||
    d.phone.includes(search)
  );

  return (
    <div className="space-y-4">
      {/* Sub Tabs */}
      <div className="flex items-center justify-between border-b border-gray-200 dark:border-gray-800 pb-3">
        <div className="flex gap-2">
          <button
            onClick={() => setActiveSubTab('INCOME')}
            className={`px-4 py-2 rounded-xl text-sm font-bold transition-all ${
              activeSubTab === 'INCOME'
                ? 'bg-islamic-green text-white shadow'
                : 'text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800'
            }`}
          >
            آمدن ریکارڈ ({incomes.length})
          </button>
          <button
            onClick={() => setActiveSubTab('DONORS')}
            className={`px-4 py-2 rounded-xl text-sm font-bold transition-all ${
              activeSubTab === 'DONORS'
                ? 'bg-islamic-green text-white shadow'
                : 'text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800'
            }`}
          >
            مستقل معاونین ({donors.length})
          </button>
        </div>

        <button
          onClick={() => activeSubTab === 'INCOME' ? setShowAddIncomeModal(true) : setShowAddDonorModal(true)}
          className="flex items-center gap-1.5 px-3 sm:px-4 py-2 rounded-xl bg-gold hover:bg-gold-dark text-gray-900 font-bold text-xs sm:text-sm shadow transition-colors"
        >
          <Plus className="w-4 h-4" />
          <span>{activeSubTab === 'INCOME' ? 'نئی آمدن درج کریں' : 'نیا معاون درج کریں'}</span>
        </button>
      </div>

      {/* Search Bar */}
      <div className="relative">
        <Search className="w-4 h-4 text-gray-400 absolute right-3 top-3" />
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder={activeSubTab === 'INCOME' ? 'نام، رسید نمبر یا موبائل سے تلاش کریں...' : 'معاون کا نام یا فون تلاش کریں...'}
          className="w-full pl-4 pr-10 py-2.5 rounded-xl border border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-islamic-green"
        />
      </div>

      {/* Income List */}
      {activeSubTab === 'INCOME' && (
        <div className="space-y-2">
          {filteredIncomes.length === 0 ? (
            <div className="text-center py-12 text-gray-400">کوئی آمدن اندراج نہیں ملا۔</div>
          ) : (
            filteredIncomes.map((inc) => {
              const matchedReceipt = receipts.find(r => r.receiptNumber === inc.receiptNumber);
              return (
                <div
                  key={inc.id}
                  onClick={() => matchedReceipt && onSelectReceipt(matchedReceipt)}
                  className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700/80 hover:border-islamic-green cursor-pointer transition-all flex items-center justify-between shadow-sm"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full bg-emerald-100 dark:bg-emerald-950/60 text-emerald-600 flex items-center justify-center font-bold text-sm shrink-0">
                      Rs
                    </div>
                    <div>
                      <h4 className="font-bold text-sm text-gray-900 dark:text-white">{inc.donorName}</h4>
                      <p className="text-xs text-gray-500 dark:text-gray-400">
                        {inc.category} • {inc.paymentMethod} • {formatUrduDate(inc.createdAt)}
                      </p>
                      <span className="inline-block mt-0.5 text-xs font-bold text-gold-dark dark:text-gold bg-gold-subtle dark:bg-yellow-950/40 px-1.5 py-0.5 rounded">
                        {inc.receiptNumber}
                      </span>
                    </div>
                  </div>

                  <div className="text-left">
                    <span className="text-base font-bold text-emerald-600 dark:text-emerald-400">
                      +{formatPKR(inc.amount)}
                    </span>
                    <p className="text-xs text-islamic-green hover:underline">رسید دیکھیں &gt;</p>
                  </div>
                </div>
              );
            })
          )}
        </div>
      )}

      {/* Donors List */}
      {activeSubTab === 'DONORS' && (
        <div className="space-y-2">
          {filteredDonors.length === 0 ? (
            <div className="text-center py-12 text-gray-400">کوئی مستقل معاون درج نہیں ہے۔</div>
          ) : (
            filteredDonors.map((d) => (
              <div
                key={d.id}
                className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700/80 flex items-center justify-between shadow-sm"
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-gold-subtle dark:bg-yellow-950/60 text-gold-dark flex items-center justify-center shrink-0">
                    <UserCheck className="w-5 h-5" />
                  </div>
                  <div>
                    <h4 className="font-bold text-sm text-gray-900 dark:text-white">{d.name}</h4>
                    <p className="text-xs text-gray-500 dark:text-gray-400">
                      {d.phone ? `فون: ${d.phone}` : 'کوئی فون درج نہیں'} • {d.address || 'کوئی پتہ درج نہیں'}
                    </p>
                  </div>
                </div>

                <div className="text-left">
                  <span className="text-xs text-gray-400 block">ماہانہ تعاون</span>
                  <span className="text-base font-bold text-islamic-green dark:text-emerald-400">
                    {formatPKR(d.pledgedMonthlyAmount)}
                  </span>
                </div>
              </div>
            ))
          )}
        </div>
      )}

      {/* Add Income Modal */}
      {showAddIncomeModal && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-gray-900 rounded-2xl max-w-lg w-full shadow-2xl p-6 text-right border border-gray-200 dark:border-gray-800">
            <h3 className="text-lg font-bold text-islamic-green mb-4">نئی آمدن و رسید کا اجراء</h3>
            <form onSubmit={handleIncomeSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  رقم (روپے) *
                </label>
                <input
                  type="number"
                  required
                  min="1"
                  placeholder="مثلاً: 5000"
                  value={amountStr}
                  onChange={(e) => setAmountStr(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-islamic-green"
                />
              </div>

              {numAmount > 0 && (
                <div className="bg-gold-subtle dark:bg-yellow-950/40 p-2.5 rounded-xl text-xs font-medium text-gold-dark dark:text-gold border border-gold/30">
                  الفاظ میں: {numberToUrduWords(numAmount)}
                </div>
              )}

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                    آمدن کی مد
                  </label>
                  <select
                    value={category}
                    onChange={(e) => setCategory(e.target.value as IncomeCategory)}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                  >
                    <option value="عمومی چندہ">عمومی چندہ</option>
                    <option value="ماہانہ تعاون">ماہانہ تعاون</option>
                    <option value="جمعہ چندہ">جمعہ چندہ</option>
                    <option value="رمضان چندہ">رمضان چندہ</option>
                    <option value="تعمیراتی فنڈ">تعمیراتی فنڈ</option>
                    <option value="تنخواہوں کے لیے تعاون">تنخواہوں کے لیے تعاون</option>
                    <option value="دیگر منظور شدہ آمدن">دیگر منظور شدہ آمدن</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                    ادائیگی کا طریقہ
                  </label>
                  <select
                    value={paymentMethod}
                    onChange={(e) => setPaymentMethod(e.target.value as PaymentMethod)}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                  >
                    <option value="نقد (Cash)">نقد (Cash)</option>
                    <option value="بینک ٹرانسفر">بینک ٹرانسفر</option>
                    <option value="جاز کیش (JazzCash)">جاز کیش</option>
                    <option value="ایزی پیسہ (Easypaisa)">ایزی پیسہ</option>
                    <option value="بینک چیک (Cheque)">بینک چیک</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  معاون / چندہ دہندہ کا نام *
                </label>
                <input
                  type="text"
                  required
                  placeholder="حاجی منظور احمد"
                  value={donorName}
                  onChange={(e) => setDonorName(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-islamic-green"
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                    موبائل نمبر (SMS کے لیے)
                  </label>
                  <input
                    type="text"
                    placeholder="03001234567"
                    value={donorPhone}
                    onChange={(e) => setDonorPhone(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                    پتہ / محلہ
                  </label>
                  <input
                    type="text"
                    placeholder="اختیاری"
                    value={donorAddress}
                    onChange={(e) => setDonorAddress(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  اضافی تفصیل یا نوٹ
                </label>
                <input
                  type="text"
                  placeholder="اختیاری نوٹ"
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div className="flex gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowAddIncomeModal(false)}
                  className="flex-1 py-2 rounded-xl border border-gray-300 dark:border-gray-700 text-xs font-bold"
                >
                  منسوخ
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 rounded-xl bg-islamic-green hover:bg-islamic-green-dark text-white text-xs font-bold shadow"
                >
                  رسید جاری کریں
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Add Donor Modal */}
      {showAddDonorModal && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-gray-900 rounded-2xl max-w-lg w-full shadow-2xl p-6 text-right border border-gray-200 dark:border-gray-800">
            <h3 className="text-lg font-bold text-islamic-green mb-4">نیا مستقل معاون شامل کریں</h3>
            <form onSubmit={handleDonorSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  معاون کا نام *
                </label>
                <input
                  type="text"
                  required
                  placeholder="چوہدری بشیر احمد"
                  value={dName}
                  onChange={(e) => setDName(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  طے شدہ ماہانہ تعاون (روپے) *
                </label>
                <input
                  type="number"
                  required
                  min="1"
                  placeholder="2000"
                  value={dPledgedStr}
                  onChange={(e) => setDPledgedStr(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                    موبائل نمبر
                  </label>
                  <input
                    type="text"
                    placeholder="03xx-xxxxxxx"
                    value={dPhone}
                    onChange={(e) => setDPhone(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                    پتہ
                  </label>
                  <input
                    type="text"
                    value={dAddress}
                    onChange={(e) => setDAddress(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  نوٹ
                </label>
                <input
                  type="text"
                  placeholder="اختیاری نوٹ"
                  value={dNotes}
                  onChange={(e) => setDNotes(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div className="flex gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowAddDonorModal(false)}
                  className="flex-1 py-2 rounded-xl border border-gray-300 dark:border-gray-700 text-xs font-bold"
                >
                  منسوخ
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 rounded-xl bg-islamic-green hover:bg-islamic-green-dark text-white text-xs font-bold shadow"
                >
                  معاون محفوظ کریں
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
