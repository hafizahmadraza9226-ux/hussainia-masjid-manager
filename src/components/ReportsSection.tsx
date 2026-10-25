import React, { useState } from 'react';
import { Mosque, Income, Expense, Budget, Meeting, ExpenseCategory } from '../types';
import { formatPKR } from '../utils/numberToWords';
import { formatUrduDate, getCurrentMonthUrdu } from '../utils/dateUtils';
import { Share2, Plus } from 'lucide-react';

interface ReportsSectionProps {
  mosque: Mosque | null;
  incomes: Income[];
  expenses: Expense[];
  budgets: Budget[];
  meetings: Meeting[];
  onAddBudget: (data: { category: ExpenseCategory; budgetedAmount: number; periodName: string }) => void;
  onAddMeeting: (data: {
    meetingNumber: string;
    venue: string;
    attendees: string;
    agenda: string;
    decisions: string;
    assignedPerson: string;
  }) => void;
}

export const ReportsSection: React.FC<ReportsSectionProps> = ({
  mosque,
  incomes,
  expenses,
  budgets,
  meetings,
  onAddBudget,
  onAddMeeting,
}) => {
  const [tab, setTab] = useState<'FINANCIAL' | 'BUDGET' | 'MEETINGS'>('FINANCIAL');
  const [showBudgetModal, setShowBudgetModal] = useState(false);
  const [showMeetingModal, setShowMeetingModal] = useState(false);

  // Budget form
  const [bCategory, setBCategory] = useState<ExpenseCategory>('بجلی کا بل');
  const [bAmount, setBAmount] = useState('');

  // Meeting form
  const [mNumber, setMNumber] = useState('اجلاس نمبر 1');
  const [mAgenda, setMAgenda] = useState('');
  const [mDecisions, setMDecisions] = useState('');
  const [mAssigned, setMAssigned] = useState('');

  const totalInc = incomes.filter(i => i.status === 'CONFIRMED').reduce((s, i) => s + i.amount, 0);
  const totalExp = expenses.filter(e => e.status === 'CONFIRMED').reduce((s, e) => s + e.amount, 0);
  const netSurplus = totalInc - totalExp;

  const handleExportCSV = () => {
    const lines = [
      `مسجد مینجمنٹ مالی رپورٹ`,
      `مسجد کا نام: ${mosque?.name}`,
      `شہر: ${mosque?.city}`,
      `رپورٹ کا مہینہ: ${getCurrentMonthUrdu()}`,
      `━━━━━━━━━━━━━━━━━━━━━━━━━`,
      `کل آمدن: ${formatPKR(totalInc)}`,
      `کل اخراجات: ${formatPKR(totalExp)}`,
      `خالص بیلنس: ${formatPKR(netSurplus)}`,
      `━━━━━━━━━━━━━━━━━━━━━━━━━`,
      `آمدن کی تفصیل:`,
      `رسید نمبر, معاون, مد, رقم, تاریخ`,
      ...incomes.map(i => `${i.receiptNumber}, ${i.donorName}, ${i.category}, ${i.amount}, ${formatUrduDate(i.createdAt)}`),
      `━━━━━━━━━━━━━━━━━━━━━━━━━`,
      `اخراجات کی تفصیل:`,
      `مد, وصول کنندہ, رقم, تاریخ`,
      ...expenses.map(e => `${e.category}, ${e.payeeName}, ${e.amount}, ${formatUrduDate(e.date)}`),
    ];

    const text = lines.join('\n');
    navigator.clipboard?.writeText(text);
    alert('مکمل مالی رپورٹ کلپ بورڈ پر کاپی ہو گئی ہے!');
  };

  const handleBudgetSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const amt = parseFloat(bAmount) || 0;
    if (amt <= 0) return;
    onAddBudget({
      category: bCategory,
      budgetedAmount: amt,
      periodName: getCurrentMonthUrdu(),
    });
    setShowBudgetModal(false);
    setBAmount('');
  };

  const handleMeetingSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!mAgenda.trim() || !mDecisions.trim()) return;
    onAddMeeting({
      meetingNumber: mNumber,
      venue: 'مسجد کمیٹی ہال',
      attendees: 'انتظامی کمیٹی اراکین',
      agenda: mAgenda.trim(),
      decisions: mDecisions.trim(),
      assignedPerson: mAssigned.trim(),
    });
    setShowMeetingModal(false);
    setMAgenda('');
    setMDecisions('');
    setMAssigned('');
  };

  return (
    <div className="space-y-4">
      {/* Sub tabs */}
      <div className="flex items-center justify-between border-b border-gray-200 dark:border-gray-800 pb-3">
        <div className="flex gap-2">
          <button
            onClick={() => setTab('FINANCIAL')}
            className={`px-4 py-2 rounded-xl text-sm font-bold transition-all ${
              tab === 'FINANCIAL'
                ? 'bg-islamic-green text-white shadow'
                : 'text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800'
            }`}
          >
            مالی رپورٹس
          </button>
          <button
            onClick={() => setTab('BUDGET')}
            className={`px-4 py-2 rounded-xl text-sm font-bold transition-all ${
              tab === 'BUDGET'
                ? 'bg-islamic-green text-white shadow'
                : 'text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800'
            }`}
          >
            بجٹ بمقابلہ اصل
          </button>
          <button
            onClick={() => setTab('MEETINGS')}
            className={`px-4 py-2 rounded-xl text-sm font-bold transition-all ${
              tab === 'MEETINGS'
                ? 'bg-islamic-green text-white shadow'
                : 'text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800'
            }`}
          >
            اجلاس و فیصلے ({meetings.length})
          </button>
        </div>

        {tab === 'BUDGET' && (
          <button
            onClick={() => setShowBudgetModal(true)}
            className="flex items-center gap-1.5 px-3 py-2 rounded-xl bg-gold hover:bg-gold-dark text-gray-900 font-bold text-xs sm:text-sm shadow"
          >
            <Plus className="w-4 h-4" />
            <span>بجٹ مقرر کریں</span>
          </button>
        )}

        {tab === 'MEETINGS' && (
          <button
            onClick={() => setShowMeetingModal(true)}
            className="flex items-center gap-1.5 px-3 py-2 rounded-xl bg-islamic-green hover:bg-islamic-green-dark text-white font-bold text-xs sm:text-sm shadow"
          >
            <Plus className="w-4 h-4" />
            <span>نیا اجلاس</span>
          </button>
        )}
      </div>

      {/* Tab: FINANCIAL */}
      {tab === 'FINANCIAL' && (
        <div className="space-y-4">
          <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-4">
            <h3 className="font-bold text-lg text-gray-900 dark:text-white">
              مجموعی مالیاتی گوشوارہ برائے {mosque?.name}
            </h3>

            <div className="space-y-3 border-t border-b border-gray-100 dark:border-gray-700 py-3 text-sm">
              <div className="flex justify-between items-center">
                <span className="text-gray-600 dark:text-gray-400">کل موصول شدہ آمدن:</span>
                <span className="text-base font-bold text-emerald-600 dark:text-emerald-400">{formatPKR(totalInc)}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-gray-600 dark:text-gray-400">کل ادا شدہ اخراجات:</span>
                <span className="text-base font-bold text-red-600 dark:text-red-400">{formatPKR(totalExp)}</span>
              </div>
              <div className="flex justify-between items-center pt-2 border-t border-gray-100 dark:border-gray-750">
                <span className="font-bold text-gray-900 dark:text-white">خالص بچت / سرپلس:</span>
                <span className={`text-xl font-black ${netSurplus >= 0 ? 'text-emerald-600 dark:text-emerald-400' : 'text-red-600'}`}>
                  {formatPKR(netSurplus)}
                </span>
              </div>
            </div>

            <div className="flex justify-end">
              <button
                onClick={handleExportCSV}
                className="flex items-center gap-2 px-4 py-2 rounded-xl bg-islamic-green hover:bg-islamic-green-dark text-white font-bold text-xs sm:text-sm shadow"
              >
                <Share2 className="w-4 h-4" />
                <span>رپورٹ کاپی / شیئر کریں (CSV)</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Tab: BUDGET */}
      {tab === 'BUDGET' && (
        <div className="space-y-3">
          {budgets.length === 0 ? (
            <div className="text-center py-12 text-gray-400">
              ابھی کوئی بجٹ مقرر نہیں کیا گیا۔ اوپر "بجٹ مقرر کریں" کا بٹن دبائیں۔
            </div>
          ) : (
            budgets.map((b) => {
              const actual = expenses.filter(e => e.category === b.category && e.status === 'CONFIRMED').reduce((s, e) => s + e.amount, 0);
              const percent = b.budgetedAmount > 0 ? Math.min(100, Math.round((actual / b.budgetedAmount) * 100)) : 0;
              const isOver = actual > b.budgetedAmount;

              return (
                <div key={b.category} className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-2">
                  <div className="flex justify-between items-center">
                    <span className="font-bold text-sm text-gray-900 dark:text-white">{b.category}</span>
                    <span className={`text-xs font-bold ${isOver ? 'text-red-600' : 'text-emerald-600'}`}>
                      {formatPKR(actual)} / {formatPKR(b.budgetedAmount)}
                    </span>
                  </div>

                  {/* Progress bar */}
                  <div className="w-full bg-gray-100 dark:bg-gray-700 h-2.5 rounded-full overflow-hidden">
                    <div
                      className={`h-full rounded-full transition-all ${isOver ? 'bg-red-500' : 'bg-emerald-500'}`}
                      style={{ width: `${percent}%` }}
                    />
                  </div>

                  <div className="flex justify-between text-xs text-gray-500">
                    <span>{isOver ? 'بجٹ سے زائد خرچ!' : `باقی رقم: ${formatPKR(b.budgetedAmount - actual)}`}</span>
                    <span>{percent}% خرچ</span>
                  </div>
                </div>
              );
            })
          )}
        </div>
      )}

      {/* Tab: MEETINGS */}
      {tab === 'MEETINGS' && (
        <div className="space-y-3">
          {meetings.length === 0 ? (
            <div className="text-center py-12 text-gray-400">ابھی کوئی اجلاس درج نہیں ہے۔</div>
          ) : (
            meetings.map((m) => (
              <div key={m.id} className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-2">
                <div className="flex justify-between items-center">
                  <h4 className="font-bold text-sm text-islamic-green dark:text-emerald-400">
                    {m.meetingNumber}
                  </h4>
                  <span className="text-xs bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-300 font-bold px-2 py-0.5 rounded">
                    مکمل
                  </span>
                </div>
                <p className="text-xs text-gray-400">تاریخ: {formatUrduDate(m.date)} • مقام: {m.venue}</p>
                <div className="text-xs space-y-1 pt-1">
                  <p><strong className="text-gray-700 dark:text-gray-300">ایجنڈا:</strong> {m.agenda}</p>
                  <p><strong className="text-gray-700 dark:text-gray-300">فیصلے:</strong> {m.decisions}</p>
                  {m.assignedPerson && (
                    <p><strong className="text-gold-dark dark:text-gold">ذمہ دار شخص:</strong> {m.assignedPerson}</p>
                  )}
                </div>
              </div>
            ))
          )}
        </div>
      )}

      {/* Add Budget Modal */}
      {showBudgetModal && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-gray-900 rounded-2xl max-w-lg w-full shadow-2xl p-6 text-right border border-gray-200 dark:border-gray-800">
            <h3 className="text-lg font-bold text-islamic-green mb-4">ماہانہ بجٹ کا تعین کریں</h3>
            <form onSubmit={handleBudgetSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold mb-1">خرچ کی مد</label>
                <select
                  value={bCategory}
                  onChange={(e) => setBCategory(e.target.value as ExpenseCategory)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                >
                  <option value="بجلی کا بل">بجلی کا بل</option>
                  <option value="صفائی ستھرائی و فنائل">صفائی ستھرائی و فنائل</option>
                  <option value="مرمت و دیکھ بھال">مرمت و دیکھ بھال</option>
                  <option value="برقی سامان و وائرنگ">برقی سامان و وائرنگ</option>
                  <option value="سولر سسٹم و بیٹری">سولر سسٹم و بیٹری</option>
                  <option value="تعمیراتی کام">تعمیراتی کام</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold mb-1">مقررہ بجٹ رقم (روپے) *</label>
                <input
                  type="number"
                  required
                  min="1"
                  placeholder="25000"
                  value={bAmount}
                  onChange={(e) => setBAmount(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div className="flex gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowBudgetModal(false)}
                  className="flex-1 py-2 rounded-xl border border-gray-300 dark:border-gray-700 text-xs font-bold"
                >
                  منسوخ
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 rounded-xl bg-islamic-green hover:bg-islamic-green-dark text-white text-xs font-bold shadow"
                >
                  بجٹ محفوظ کریں
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Add Meeting Modal */}
      {showMeetingModal && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-gray-900 rounded-2xl max-w-lg w-full shadow-2xl p-6 text-right border border-gray-200 dark:border-gray-800">
            <h3 className="text-lg font-bold text-islamic-green mb-4">نیا انتظامی اجلاس</h3>
            <form onSubmit={handleMeetingSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold mb-1">اجلاس نمبر</label>
                <input
                  type="text"
                  value={mNumber}
                  onChange={(e) => setMNumber(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold mb-1">ایجنڈا *</label>
                <input
                  type="text"
                  required
                  placeholder="رمضان انتظامات، سولر مرمت"
                  value={mAgenda}
                  onChange={(e) => setMAgenda(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold mb-1">فیصلے و تجاویز *</label>
                <textarea
                  required
                  placeholder="کمیٹی کے منظور شدہ فیصلے..."
                  value={mDecisions}
                  onChange={(e) => setMDecisions(e.target.value)}
                  rows={3}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold mb-1">ذمہ دار شخص</label>
                <input
                  type="text"
                  placeholder="محمد اکرم"
                  value={mAssigned}
                  onChange={(e) => setMAssigned(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-gray-300 dark:border-gray-700 dark:bg-gray-800 text-sm"
                />
              </div>

              <div className="flex gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowMeetingModal(false)}
                  className="flex-1 py-2 rounded-xl border border-gray-300 dark:border-gray-700 text-xs font-bold"
                >
                  منسوخ
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 rounded-xl bg-islamic-green hover:bg-islamic-green-dark text-white text-xs font-bold shadow"
                >
                  اجلاس محفوظ کریں
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
