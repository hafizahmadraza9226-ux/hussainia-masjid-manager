import React, { useState } from 'react';
import { Expense, UtilityBill, Salary, ExpenseCategory, PaymentMethod } from '../types';
import { formatPKR } from '../utils/numberToWords';
import { formatUrduDate, getCurrentMonthUrdu } from '../utils/dateUtils';
import { Plus, Bolt, BadgeCheck, MinusCircle } from 'lucide-react';

interface ExpensesSectionProps {
  expenses: Expense[];
  bills: UtilityBill[];
  salaries: Salary[];
  onAddExpense: (data: {
    amount: number;
    category: ExpenseCategory;
    payeeName: string;
    billReceiptRef: string;
    paymentMethod: PaymentMethod;
    details: string;
  }) => void;
  onAddBill: (data: {
    utilityType: 'بجلی' | 'گیس' | 'پانی';
    companyName: string;
    consumerNumber: string;
    billingMonth: string;
    amount: number;
  }) => void;
  onPayBill: (billId: string) => void;
  onAddSalary: (data: {
    staffType: 'امام و خطیب' | 'مؤذن صاحب' | 'خادم مسجد';
    staffName: string;
    monthlySalary: number;
    monthYear: string;
    paidAmount: number;
    paymentMethod: PaymentMethod;
  }) => void;
}

export const ExpensesSection: React.FC<ExpensesSectionProps> = ({
  expenses,
  bills,
  salaries,
  onAddExpense,
  onAddBill,
  onPayBill,
  onAddSalary,
}) => {
  const [tab, setTab] = useState<'EXPENSES' | 'BILLS' | 'SALARIES'>('EXPENSES');
  
  const [showExpenseModal, setShowExpenseModal] = useState(false);
  const [showBillModal, setShowBillModal] = useState(false);
  const [showSalaryModal, setShowSalaryModal] = useState(false);

  // Expense form
  const [expAmount, setExpAmount] = useState('');
  const [expCat, setExpCat] = useState<ExpenseCategory>('صفائی ستھرائی و فنائل');
  const [payee, setPayee] = useState('');
  const [expDetails, setExpDetails] = useState('');
  const [expRef, setExpRef] = useState('');

  // Bill form
  const [bType, setBType] = useState<'بجلی' | 'گیس' | 'پانی'>('بجلی');
  const [bCompany, setBCompany] = useState('LESCO');
  const [bConsumer, setBConsumer] = useState('');
  const [bAmount, setBAmount] = useState('');
  const [bMonth, setBMonth] = useState(getCurrentMonthUrdu());

  // Salary form
  const [sType, setSType] = useState<'امام و خطیب' | 'مؤذن صاحب' | 'خادم مسجد'>('امام و خطیب');
  const [sName, setSName] = useState('مولانا حافظ احمد رضا');
  const [sAmount, setSAmount] = useState('45000');
  const [sMonth, setSMonth] = useState(getCurrentMonthUrdu());

  const handleExpenseSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const amt = parseFloat(expAmount) || 0;
    if (amt <= 0 || !payee.trim()) return;
    onAddExpense({
      amount: amt,
      category: expCat,
      payeeName: payee.trim(),
      billReceiptRef: expRef.trim(),
      paymentMethod: 'نقد (Cash)',
      details: expDetails.trim(),
    });
    setShowExpenseModal(false);
    setExpAmount('');
    setPayee('');
    setExpDetails('');
  };

  const handleBillSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const amt = parseFloat(bAmount) || 0;
    if (amt <= 0 || !bConsumer.trim()) return;
    onAddBill({
      utilityType: bType,
      companyName: bCompany,
      consumerNumber: bConsumer.trim(),
      billingMonth: bMonth,
      amount: amt,
    });
    setShowBillModal(false);
    setBAmount('');
    setBConsumer('');
  };

  const handleSalarySubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const amt = parseFloat(sAmount) || 0;
    if (amt <= 0 || !sName.trim()) return;
    onAddSalary({
      staffType: sType,
      staffName: sName.trim(),
      monthlySalary: amt,
      monthYear: sMonth,
      paidAmount: amt,
      paymentMethod: 'نقد (Cash)',
    });
    setShowSalaryModal(false);
  };

  return (
    <div className="space-y-4">
      {/* Sub Tabs */}
      <div className="flex items-center justify-between border-b border-gray-200 dark:border-gray-800 pb-3">
        <div className="flex gap-2 overflow-x-auto">
          <button
            onClick={() => setTab('EXPENSES')}
            className={`px-4 py-2 rounded-xl text-sm font-bold shrink-0 transition-all ${
              tab === 'EXPENSES'
                ? 'bg-amber-600 text-white shadow'
                : 'text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800'
            }`}
          >
            اخراجات ({expenses.length})
          </button>
          <button
            onClick={() => setTab('BILLS')}
            className={`px-4 py-2 rounded-xl text-sm font-bold shrink-0 transition-all ${
              tab === 'BILLS'
                ? 'bg-amber-600 text-white shadow'
                : 'text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800'
            }`}
          >
            یوٹیلیٹی بلز ({bills.length})
          </button>
          <button
            onClick={() => setTab('SALARIES')}
            className={`px-4 py-2 rounded-xl text-sm font-bold shrink-0 transition-all ${
              tab === 'SALARIES'
                ? 'bg-amber-600 text-white shadow'
                : 'text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800'
            }`}
          >
            تنخواہیں ({salaries.length})
          </button>
        </div>

        <button
          onClick={() => {
            if (tab === 'EXPENSES') setShowExpenseModal(true);
            if (tab === 'BILLS') setShowBillModal(true);
            if (tab === 'SALARIES') setShowSalaryModal(true);
          }}
          className="flex items-center gap-1.5 px-3 sm:px-4 py-2 rounded-xl bg-amber-600 hover:bg-amber-700 text-white font-bold text-xs sm:text-sm shadow transition-colors shrink-0"
        >
          <Plus className="w-4 h-4" />
          <span>
            {tab === 'EXPENSES' && 'نیا خرچ'}
            {tab === 'BILLS' && 'نیا بل'}
            {tab === 'SALARIES' && 'تنخواہ کی ادائیگی'}
          </span>
        </button>
      </div>

      {/* Tab: EXPENSES */}
      {tab === 'EXPENSES' && (
        <div className="space-y-2">
          {expenses.length === 0 ? (
            <div className="text-center py-12 text-gray-400">کوئی اخراجات درج نہیں ہیں۔</div>
          ) : (
            expenses.map((e) => (
              <div
                key={e.id}
                className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700/80 flex items-center justify-between shadow-sm"
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-red-100 dark:bg-red-950/60 text-red-600 flex items-center justify-center shrink-0">
                    <MinusCircle className="w-5 h-5" />
                  </div>
                  <div>
                    <h4 className="font-bold text-sm text-gray-900 dark:text-white">
                      {e.category}
                    </h4>
                    <p className="text-xs text-gray-500 dark:text-gray-400">
                      وصول کنندہ: {e.payeeName} • {formatUrduDate(e.date)}
                    </p>
                    {e.details && (
                      <p className="text-xs text-gray-400 mt-0.5">{e.details}</p>
                    )}
                  </div>
                </div>

                <div className="text-left">
                  <span className="text-base font-bold text-red-600 dark:text-red-400">
                    -{formatPKR(e.amount)}
                  </span>
                </div>
              </div>
            ))
          )}
        </div>
      )}

      {/* Tab: BILLS */}
      {tab === 'BILLS' && (
        <div className="space-y-2">
          {bills.length === 0 ? (
            <div className="text-center py-12 text-gray-400">کوئی یوٹیلیٹی بل درج نہیں ہے۔</div>
          ) : (
            bills.map((b) => (
              <div
                key={b.id}
                className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700/80 flex items-center justify-between shadow-sm"
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-blue-100 dark:bg-blue-950/60 text-blue-600 flex items-center justify-center shrink-0">
                    <Bolt className="w-5 h-5" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <h4 className="font-bold text-sm text-gray-900 dark:text-white">
                        {b.companyName} ({b.utilityType})
                      </h4>
                      <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded ${
                        b.status === 'PAID'
                          ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-950/60 dark:text-emerald-300'
                          : 'bg-amber-100 text-amber-700 dark:bg-amber-950/60 dark:text-amber-300'
                      }`}>
                        {b.status === 'PAID' ? 'ادا شدہ' : 'غیر ادا شدہ'}
                      </span>
                    </div>
                    <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
                      ریفرنس: {b.consumerNumber} • ماہ: {b.billingMonth}
                    </p>
                  </div>
                </div>

                <div className="text-left flex items-center gap-3">
                  <span className="text-base font-bold text-gray-900 dark:text-white">
                    {formatPKR(b.amount)}
                  </span>
                  {b.status !== 'PAID' && (
                    <button
                      onClick={() => onPayBill(b.id)}
                      className="px-2.5 py-1 rounded-lg bg-islamic-green hover:bg-islamic-green-dark text-white text-xs font-bold transition-colors"
                    >
                      ادا ہو گیا
                    </button>
                  )}
                </div>
              </div>
            ))
          )}
        </div>
      )}

      {/* Tab: SALARIES */}
      {tab === 'SALARIES' && (
        <div className="space-y-2">
          {salaries.length === 0 ? (
            <div className="text-center py-12 text-gray-400">کوئی تنخواہ ریکارڈ درج نہیں ہے۔</div>
          ) : (
            salaries.map((s) => (
              <div
                key={s.id}
                className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700/80 flex items-center justify-between shadow-sm"
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-emerald-100 dark:bg-emerald-950/60 text-emerald-600 flex items-center justify-center shrink-0">
                    <BadgeCheck className="w-5 h-5" />
                  </div>
                  <div>
                    <h4 className="font-bold text-sm text-gray-900 dark:text-white">
                      {s.staffName} ({s.staffType})
                    </h4>
                    <p className="text-xs text-gray-500 dark:text-gray-400">
                      ماہ: {s.monthYear} • ادائیگی: {formatUrduDate(s.paymentDate)}
                    </p>
                  </div>
                </div>

                <div className="text-left">
                  <span className="text-base font-bold text-emerald-600 dark:text-emerald-400">
                    {formatPKR(s.paidAmount)}
                  </span>
                  <span className="block text-[10px] text-gray-400">مکمل ادا شدہ</span>
                </div>
              </div>
            ))
          )}
        </div>
      )}

      {/* Add Expense Modal */}
      {showExpenseModal && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-gray-900 rounded-2xl max-w-lg w-full shadow-2xl p-6 text-right border border-gray-200 dark:border-gray-800">
            <h3 className="text-lg font-bold text-amber-600 mb-4">نیا خرچ درج کریں</h3>
            <form onSubmit={handleExpenseSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold mb-1">خرچ کی رقم (روپے) *</label>
                <input
                  type="number"
                  required
                  min="1"
                  placeholder="3500"
                  value={expAmount}
                  onChange={(e) => setExpAmount(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold mb-1">خرچ کی مد</label>
                <select
                  value={expCat}
                  onChange={(e) => setExpCat(e.target.value as ExpenseCategory)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                >
                  <option value="صفائی ستھرائی و فنائل">صفائی ستھرائی و فنائل</option>
                  <option value="بجلی کا بل">بجلی کا بل</option>
                  <option value="مرمت و دیکھ بھال">مرمت و دیکھ بھال</option>
                  <option value="برقی سامان و وائرنگ">برقی سامان و وائرنگ</option>
                  <option value="سولر سسٹم و بیٹری">سولر سسٹم و بیٹری</option>
                  <option value="قالین، صفیں اور چٹائیاں">قالین، صفیں اور چٹائیاں</option>
                  <option value="CCTV کیمرے و سیکیورٹی">CCTV کیمرے و سیکیورٹی</option>
                  <option value="تعمیراتی کام">تعمیراتی کام</option>
                  <option value="دیگر مسجدی ضروریات">دیگر مسجدی ضروریات</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold mb-1">دکاندار / وصول کنندہ کا نام *</label>
                <input
                  type="text"
                  required
                  placeholder="اکرم الیکٹرونکس"
                  value={payee}
                  onChange={(e) => setPayee(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold mb-1">بل یا رسید نمبر</label>
                <input
                  type="text"
                  placeholder="اختیاری واؤچر نمبر"
                  value={expRef}
                  onChange={(e) => setExpRef(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold mb-1">خرچ کی تفصیل</label>
                <input
                  type="text"
                  placeholder="پنکھے کی مرمت، وائرنگ"
                  value={expDetails}
                  onChange={(e) => setExpDetails(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div className="flex gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowExpenseModal(false)}
                  className="flex-1 py-2 rounded-xl border border-gray-300 dark:border-gray-700 text-xs font-bold"
                >
                  منسوخ
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 rounded-xl bg-amber-600 hover:bg-amber-700 text-white text-xs font-bold shadow"
                >
                  خرچ محفوظ کریں
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Add Bill Modal */}
      {showBillModal && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-gray-900 rounded-2xl max-w-lg w-full shadow-2xl p-6 text-right border border-gray-200 dark:border-gray-800">
            <h3 className="text-lg font-bold text-blue-600 mb-4">نیا یوٹیلیٹی بل درج کریں</h3>
            <form onSubmit={handleBillSubmit} className="space-y-3">
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-semibold mb-1">قسم</label>
                  <select
                    value={bType}
                    onChange={(e) => {
                      const val = e.target.value as 'بجلی' | 'گیس' | 'پانی';
                      setBType(val);
                      setBCompany(val === 'بجلی' ? 'LESCO' : val === 'گیس' ? 'SNGPL' : 'WASA');
                    }}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                  >
                    <option value="بجلی">بجلی (Electricity)</option>
                    <option value="گیس">گیس (Gas)</option>
                    <option value="پانی">پانی (Water)</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold mb-1">کمپنی</label>
                  <input
                    type="text"
                    value={bCompany}
                    onChange={(e) => setBCompany(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold mb-1">ریفرنس یا کنزیومر نمبر *</label>
                <input
                  type="text"
                  required
                  placeholder="24 11223 3445500"
                  value={bConsumer}
                  onChange={(e) => setBConsumer(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-semibold mb-1">بل کی رقم (روپے) *</label>
                  <input
                    type="number"
                    required
                    min="1"
                    placeholder="12000"
                    value={bAmount}
                    onChange={(e) => setBAmount(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold mb-1">بل کا مہینہ</label>
                  <input
                    type="text"
                    value={bMonth}
                    onChange={(e) => setBMonth(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                  />
                </div>
              </div>

              <div className="flex gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowBillModal(false)}
                  className="flex-1 py-2 rounded-xl border border-gray-300 dark:border-gray-700 text-xs font-bold"
                >
                  منسوخ
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 rounded-xl bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold shadow"
                >
                  بل محفوظ کریں
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Add Salary Modal */}
      {showSalaryModal && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-gray-900 rounded-2xl max-w-lg w-full shadow-2xl p-6 text-right border border-gray-200 dark:border-gray-800">
            <h3 className="text-lg font-bold text-islamic-green mb-4">تنخواہ کی ادائیگی کا اندراج</h3>
            <form onSubmit={handleSalarySubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold mb-1">عہدہ</label>
                <select
                  value={sType}
                  onChange={(e) => {
                    const val = e.target.value as 'امام و خطیب' | 'مؤذن صاحب' | 'خادم مسجد';
                    setSType(val);
                    if (val === 'امام و خطیب') {
                      setSName('مولانا حافظ احمد رضا');
                      setSAmount('45000');
                    } else if (val === 'مؤذن صاحب') {
                      setSName('قاری محمد بلال');
                      setSAmount('28000');
                    } else {
                      setSName('خادم صاحب');
                      setSAmount('20000');
                    }
                  }}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                >
                  <option value="امام و خطیب">امام و خطیب</option>
                  <option value="مؤذن صاحب">مؤذن صاحب</option>
                  <option value="خادم مسجد">خادم مسجد</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold mb-1">نام ملازم *</label>
                <input
                  type="text"
                  required
                  value={sName}
                  onChange={(e) => setSName(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-semibold mb-1">ادا شدہ رقم (روپے) *</label>
                  <input
                    type="number"
                    required
                    min="1"
                    value={sAmount}
                    onChange={(e) => setSAmount(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold mb-1">متعلقہ مہینہ</label>
                  <input
                    type="text"
                    value={sMonth}
                    onChange={(e) => setSMonth(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                  />
                </div>
              </div>

              <div className="flex gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowSalaryModal(false)}
                  className="flex-1 py-2 rounded-xl border border-gray-300 dark:border-gray-700 text-xs font-bold"
                >
                  منسوخ
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 rounded-xl bg-islamic-green hover:bg-islamic-green-dark text-white text-xs font-bold shadow"
                >
                  تنخواہ محفوظ کریں
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
};
