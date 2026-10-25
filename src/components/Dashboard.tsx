import React from 'react';
import { Mosque, Income, Expense, UtilityBill, Receipt } from '../types';
import { formatPKR } from '../utils/numberToWords';
import { formatUrduDate } from '../utils/dateUtils';
import { 
  TrendingUp, 
  TrendingDown, 
  Wallet, 
  AlertTriangle, 
  PlusCircle, 
  MinusCircle, 
  Receipt as ReceiptIcon,
  ArrowUpRight,
  ArrowDownRight
} from 'lucide-react';

interface DashboardProps {
  mosque: Mosque | null;
  incomes: Income[];
  expenses: Expense[];
  bills: UtilityBill[];
  onOpenAddIncome: () => void;
  onOpenAddExpense: () => void;
  onNavigateTab: (tab: number) => void;
  onSelectReceipt: (receipt: Receipt) => void;
  receipts: Receipt[];
}

export const Dashboard: React.FC<DashboardProps> = ({
  mosque,
  incomes,
  expenses,
  bills,
  onOpenAddIncome,
  onOpenAddExpense,
  onNavigateTab,
  onSelectReceipt,
  receipts,
}) => {
  const confirmedIncomes = incomes.filter(i => i.status === 'CONFIRMED');
  const confirmedExpenses = expenses.filter(e => e.status === 'CONFIRMED');

  const totalIncome = confirmedIncomes.reduce((sum, i) => sum + i.amount, 0);
  const totalExpense = confirmedExpenses.reduce((sum, e) => sum + e.amount, 0);
  const netBalance = totalIncome - totalExpense;

  // Unpaid bills
  const unpaidBills = bills.filter(b => b.status !== 'PAID');
  const unpaidTotal = unpaidBills.reduce((sum, b) => sum + (b.amount - b.paidAmount), 0);

  return (
    <div className="space-y-6">
      
      {/* Hero Net Balance Card */}
      <div className="bg-gradient-to-br from-islamic-green-subtle/50 via-white to-gold-subtle/30 dark:from-emerald-950/40 dark:via-gray-900 dark:to-yellow-950/20 p-6 rounded-3xl border border-gold/40 shadow-sm relative overflow-hidden">
        <div className="relative z-10 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div>
            <div className="flex items-center gap-2">
              <Wallet className="w-5 h-5 text-islamic-green" />
              <span className="text-xs sm:text-sm font-semibold text-gray-500 dark:text-gray-400">
                دستیاب خالص فنڈز (Net Balance)
              </span>
            </div>
            <h2 className="text-3xl sm:text-4xl font-black text-gray-900 dark:text-white mt-1">
              {formatPKR(netBalance)}
            </h2>
            <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
              {mosque?.name}
            </p>
          </div>

          <div className="flex gap-4 border-t sm:border-t-0 sm:border-r border-gray-200 dark:border-gray-800 pt-3 sm:pt-0 sm:pr-6">
            <div className="text-right">
              <span className="text-xs text-gray-500 dark:text-gray-400 flex items-center gap-1">
                <ArrowDownRight className="w-3.5 h-3.5 text-emerald-600" />
                کل آمدن
              </span>
              <span className="text-base sm:text-lg font-bold text-emerald-600 dark:text-emerald-400">
                {formatPKR(totalIncome)}
              </span>
            </div>

            <div className="text-right">
              <span className="text-xs text-gray-500 dark:text-gray-400 flex items-center gap-1">
                <ArrowUpRight className="w-3.5 h-3.5 text-red-600" />
                کل اخراجات
              </span>
              <span className="text-base sm:text-lg font-bold text-red-600 dark:text-red-400">
                {formatPKR(totalExpense)}
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Unpaid Utility Bills Warning */}
      {unpaidBills.length > 0 && (
        <div className="bg-amber-50 dark:bg-amber-950/40 border border-amber-200 dark:border-amber-800 p-4 rounded-2xl flex items-center justify-between gap-3">
          <div className="flex items-center gap-3">
            <AlertTriangle className="w-6 h-6 text-amber-600 shrink-0" />
            <div>
              <h4 className="text-sm font-bold text-amber-900 dark:text-amber-200">
                {unpaidBills.length} یوٹیلیٹی بل غیر ادا شدہ ہیں
              </h4>
              <p className="text-xs text-amber-700 dark:text-amber-300">
                کل واجب الادا رقم: {formatPKR(unpaidTotal)}
              </p>
            </div>
          </div>
          <button
            onClick={() => onNavigateTab(3)}
            className="px-3 py-1.5 rounded-lg bg-amber-600 hover:bg-amber-700 text-white text-xs font-bold shrink-0 shadow-sm"
          >
            بلز دیکھیں
          </button>
        </div>
      )}

      {/* Quick Actions */}
      <div>
        <h3 className="text-sm font-bold text-gray-800 dark:text-gray-200 mb-3">
          فوری کارروائیاں (Quick Actions)
        </h3>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
          <button
            onClick={onOpenAddIncome}
            className="flex items-center justify-center gap-2 p-3 rounded-2xl bg-islamic-green hover:bg-islamic-green-dark text-white font-bold text-xs sm:text-sm shadow transition-colors"
          >
            <PlusCircle className="w-4 h-4 text-gold" />
            <span>نئی آمدن درج کریں</span>
          </button>

          <button
            onClick={onOpenAddExpense}
            className="flex items-center justify-center gap-2 p-3 rounded-2xl bg-amber-700 hover:bg-amber-800 text-white font-bold text-xs sm:text-sm shadow transition-colors"
          >
            <MinusCircle className="w-4 h-4 text-yellow-200" />
            <span>نیا خرچ درج کریں</span>
          </button>

          <button
            onClick={() => onNavigateTab(2)}
            className="flex items-center justify-center gap-2 p-3 rounded-2xl bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700/50 text-gray-800 dark:text-gray-200 font-bold text-xs sm:text-sm shadow-sm transition-colors"
          >
            <ReceiptIcon className="w-4 h-4 text-islamic-green" />
            <span>رسیدیں دیکھیں</span>
          </button>

          <button
            onClick={() => onNavigateTab(3)}
            className="flex items-center justify-center gap-2 p-3 rounded-2xl bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700/50 text-gray-800 dark:text-gray-200 font-bold text-xs sm:text-sm shadow-sm transition-colors"
          >
            <Wallet className="w-4 h-4 text-blue-600" />
            <span>یوٹیلیٹی و تنخواہیں</span>
          </button>
        </div>
      </div>

      {/* Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center justify-between">
          <div>
            <span className="text-xs text-gray-500 dark:text-gray-400">کل موصول شدہ آمدن</span>
            <div className="text-xl font-bold text-emerald-600 dark:text-emerald-400 mt-1">
              {formatPKR(totalIncome)}
            </div>
          </div>
          <div className="w-10 h-10 rounded-full bg-emerald-100 dark:bg-emerald-950/60 flex items-center justify-center text-emerald-600">
            <TrendingUp className="w-5 h-5" />
          </div>
        </div>

        <div className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center justify-between">
          <div>
            <span className="text-xs text-gray-500 dark:text-gray-400">کل مسجدی اخراجات</span>
            <div className="text-xl font-bold text-red-600 dark:text-red-400 mt-1">
              {formatPKR(totalExpense)}
            </div>
          </div>
          <div className="w-10 h-10 rounded-full bg-red-100 dark:bg-red-950/60 flex items-center justify-center text-red-600">
            <TrendingDown className="w-5 h-5" />
          </div>
        </div>

        <div className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center justify-between">
          <div>
            <span className="text-xs text-gray-500 dark:text-gray-400">کل رسیدیں</span>
            <div className="text-xl font-bold text-gray-900 dark:text-white mt-1">
              {receipts.length}
            </div>
          </div>
          <div className="w-10 h-10 rounded-full bg-gold-subtle dark:bg-yellow-950/60 flex items-center justify-center text-gold-dark">
            <ReceiptIcon className="w-5 h-5" />
          </div>
        </div>

        <div className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center justify-between">
          <div>
            <span className="text-xs text-gray-500 dark:text-gray-400">واجب الادا بلز</span>
            <div className="text-xl font-bold text-amber-600 dark:text-amber-400 mt-1">
              {unpaidBills.length}
            </div>
          </div>
          <div className="w-10 h-10 rounded-full bg-amber-100 dark:bg-amber-950/60 flex items-center justify-center text-amber-600">
            <AlertTriangle className="w-5 h-5" />
          </div>
        </div>
      </div>

      {/* Recent Activity Table */}
      <div className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-5 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-bold text-base text-gray-900 dark:text-white">
            حالیہ مالیاتی رسیدیں و اندراجات
          </h3>
          <button
            onClick={() => onNavigateTab(2)}
            className="text-xs font-bold text-islamic-green hover:underline"
          >
            تمام دیکھیں &gt;
          </button>
        </div>

        {receipts.length === 0 ? (
          <div className="text-center py-8 text-gray-400 text-sm">
            ابھی کوئی رسید جاری نہیں ہوئی۔
          </div>
        ) : (
          <div className="space-y-2">
            {receipts.slice(0, 5).map((r) => (
              <div
                key={r.id}
                onClick={() => onSelectReceipt(r)}
                className="p-3 rounded-xl border border-gray-100 dark:border-gray-750 hover:bg-gray-50 dark:hover:bg-gray-700/40 flex items-center justify-between cursor-pointer transition-colors"
              >
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-full bg-emerald-100 dark:bg-emerald-950 text-emerald-600 flex items-center justify-center font-bold text-xs">
                    ✓
                  </div>
                  <div>
                    <h4 className="text-sm font-bold text-gray-900 dark:text-white">
                      {r.donorName}
                    </h4>
                    <p className="text-xs text-gray-500 dark:text-gray-400">
                      {r.receiptNumber} • {formatUrduDate(r.issueDate)} • {r.purpose}
                    </p>
                  </div>
                </div>

                <div className="text-left">
                  <span className="text-sm font-bold text-emerald-600 dark:text-emerald-400">
                    +{formatPKR(r.amount)}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

    </div>
  );
};
