import React, { useState, useEffect } from 'react';
import { 
  Mosque, 
  MosqueMember, 
  Income, 
  Donor, 
  Receipt, 
  Expense, 
  UtilityBill, 
  Salary, 
  Budget, 
  Meeting, 
  SmsLog,
  UserRole,
  IncomeCategory,
  PaymentMethod,
  ExpenseCategory
} from './types';
import { 
  initializeDatabase, 
  getStoredData, 
  setStoredData 
} from './utils/storage';
import { numberToUrduWords } from './utils/numberToWords';
import { Header } from './components/Header';
import { Dashboard } from './components/Dashboard';
import { IncomeSection } from './components/IncomeSection';
import { ReceiptsSection } from './components/ReceiptsSection';
import { ExpensesSection } from './components/ExpensesSection';
import { ReportsSection } from './components/ReportsSection';
import { SettingsSection } from './components/SettingsSection';
import { ReceiptModal } from './components/ReceiptModal';
import { MosqueModal } from './components/MosqueModal';
import { 
  LayoutDashboard, 
  TrendingUp, 
  Receipt as ReceiptIcon, 
  ReceiptText, 
  Settings as SettingsIcon 
} from 'lucide-react';

export const App: React.FC = () => {
  const [isDarkMode, setIsDarkMode] = useState<boolean>(() => {
    return getStoredData('mm_dark_mode', false);
  });

  const [allMosques, setAllMosques] = useState<Mosque[]>([]);
  const [activeMosqueId, setActiveMosqueId] = useState<string>('');
  const [members, setMembers] = useState<MosqueMember[]>([]);
  const [incomes, setIncomes] = useState<Income[]>([]);
  const [donors, setDonors] = useState<Donor[]>([]);
  const [receipts, setReceipts] = useState<Receipt[]>([]);
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [bills, setBills] = useState<UtilityBill[]>([]);
  const [salaries, setSalaries] = useState<Salary[]>([]);
  const [budgets, setBudgets] = useState<Budget[]>([]);
  const [meetings, setMeetings] = useState<Meeting[]>([]);
  const [_smsLogs, setSmsLogs] = useState<SmsLog[]>([]);

  const [activeTab, setActiveTab] = useState<number>(0);
  const [activeRole, _setActiveRole] = useState<UserRole>('SUPER_ADMIN');

  // Modals
  const [showMosqueModal, setShowMosqueModal] = useState<boolean>(false);
  const [selectedReceipt, setSelectedReceipt] = useState<Receipt | null>(null);

  // Initialize data on mount
  useEffect(() => {
    initializeDatabase();

    const storedMosques = getStoredData<Mosque[]>('mm_mosques', []);
    const storedActiveId = getStoredData<string>('mm_active_mosque_id', storedMosques[0]?.id || '');
    const storedMembers = getStoredData<MosqueMember[]>('mm_members', []);
    const storedIncomes = getStoredData<Income[]>('mm_incomes', []);
    const storedDonors = getStoredData<Donor[]>('mm_donors', []);
    const storedReceipts = getStoredData<Receipt[]>('mm_receipts', []);
    const storedExpenses = getStoredData<Expense[]>('mm_expenses', []);
    const storedBills = getStoredData<UtilityBill[]>('mm_bills', []);
    const storedSalaries = getStoredData<Salary[]>('mm_salaries', []);
    const storedBudgets = getStoredData<Budget[]>('mm_budgets', []);
    const storedMeetings = getStoredData<Meeting[]>('mm_meetings', []);
    const storedSmsLogs = getStoredData<SmsLog[]>('mm_sms_logs', []);

    setAllMosques(storedMosques);
    setActiveMosqueId(storedActiveId);
    setMembers(storedMembers);
    setIncomes(storedIncomes);
    setDonors(storedDonors);
    setReceipts(storedReceipts);
    setExpenses(storedExpenses);
    setBills(storedBills);
    setSalaries(storedSalaries);
    setBudgets(storedBudgets);
    setMeetings(storedMeetings);
    setSmsLogs(storedSmsLogs);
  }, []);

  // Update theme class on HTML element
  useEffect(() => {
    if (isDarkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
    setStoredData('mm_dark_mode', isDarkMode);
  }, [isDarkMode]);

  const activeMosque = allMosques.find(m => m.id === activeMosqueId) || allMosques[0] || null;

  // Filter isolated records for active mosque only!
  const currentIncomes = incomes.filter(i => i.mosqueId === activeMosqueId);
  const currentDonors = donors.filter(d => d.mosqueId === activeMosqueId);
  const currentReceipts = receipts.filter(r => r.mosqueId === activeMosqueId);
  const currentExpenses = expenses.filter(e => e.mosqueId === activeMosqueId);
  const currentBills = bills.filter(b => b.mosqueId === activeMosqueId);
  const currentSalaries = salaries.filter(s => s.mosqueId === activeMosqueId);
  const currentBudgets = budgets.filter(b => b.mosqueId === activeMosqueId);
  const currentMeetings = meetings.filter(m => m.mosqueId === activeMosqueId);
  const currentMembers = members.filter(m => m.mosqueId === activeMosqueId);

  // Switch Mosque
  const handleSelectMosque = (id: string) => {
    setActiveMosqueId(id);
    setStoredData('mm_active_mosque_id', id);
  };

  // Create Mosque
  const handleCreateMosque = (data: {
    name: string;
    shortName: string;
    code: string;
    address: string;
    city: string;
    phone: string;
    adminName: string;
    adminEmail: string;
    adminPhone: string;
  }) => {
    const newId = 'mosque_' + Math.random().toString(36).substring(2, 9);
    const newMosque: Mosque = {
      id: newId,
      name: data.name,
      shortName: data.shortName,
      code: data.code,
      address: data.address,
      city: data.city,
      country: 'پاکستان',
      phone: data.phone,
      email: data.adminEmail,
      currency: 'PKR',
      receiptNote: 'اللہ تعالیٰ آپ کے مالی تعاون کو قبول فرمائے۔ آمین۔',
      smsSenderName: data.code,
      smsTemplate: `جامع مسجد {مسجد کا نام} میں مالی تعاون کرنے کا بہت شکریہ۔ آپ کی رقم مبلغ {رقم} روپے وصول کر لی گئی ہے۔ رسید نمبر: {رسید نمبر}۔ جزاک اللہ۔`,
      createdAt: Date.now(),
    };

    const newMembers: MosqueMember[] = [
      {
        id: 'mem_' + Math.random().toString(36).substring(2, 9),
        mosqueId: newId,
        userId: 'u_admin',
        userName: data.adminName,
        userEmail: data.adminEmail,
        userPhone: data.adminPhone,
        role: 'SUPER_ADMIN',
        inviteCode: `${data.code}-ADM`,
        isConfirmed: true,
        joinedAt: Date.now(),
      },
      {
        id: 'mem_' + Math.random().toString(36).substring(2, 9),
        mosqueId: newId,
        userId: 'u_fin',
        userName: 'دعوت یافتہ فنانس مینیجر',
        userEmail: `fin@${data.code.toLowerCase()}.pk`,
        userPhone: '',
        role: 'FINANCE_MANAGER',
        inviteCode: `${data.code}-FIN`,
        isConfirmed: false,
        joinedAt: Date.now(),
      },
      {
        id: 'mem_' + Math.random().toString(36).substring(2, 9),
        mosqueId: newId,
        userId: 'u_rec',
        userName: 'دعوت یافتہ ریکارڈ مینیجر',
        userEmail: `rec@${data.code.toLowerCase()}.pk`,
        userPhone: '',
        role: 'RECORD_MANAGER',
        inviteCode: `${data.code}-REC`,
        isConfirmed: false,
        joinedAt: Date.now(),
      },
      {
        id: 'mem_' + Math.random().toString(36).substring(2, 9),
        mosqueId: newId,
        userId: 'u_rev',
        userName: 'دعوت یافتہ ناظر',
        userEmail: `rev@${data.code.toLowerCase()}.pk`,
        userPhone: '',
        role: 'READ_REVIEW',
        inviteCode: `${data.code}-REV`,
        isConfirmed: false,
        joinedAt: Date.now(),
      },
    ];

    const updatedMosques = [...allMosques, newMosque];
    const updatedMembers = [...members, ...newMembers];

    setAllMosques(updatedMosques);
    setMembers(updatedMembers);
    setActiveMosqueId(newId);

    setStoredData('mm_mosques', updatedMosques);
    setStoredData('mm_members', updatedMembers);
    setStoredData('mm_active_mosque_id', newId);
  };

  // Join Mosque
  const handleJoinMosque = (code: string, userName: string, userPhone: string) => {
    const foundMember = members.find(m => m.inviteCode === code);
    if (!foundMember) {
      alert('درج کردہ دعوتی کوڈ درست نہیں ہے۔');
      return;
    }

    const updatedMembers = members.map(m => {
      if (m.inviteCode === code) {
        return {
          ...m,
          userName,
          userPhone,
          isConfirmed: true,
          joinedAt: Date.now(),
        };
      }
      return m;
    });

    setMembers(updatedMembers);
    setActiveMosqueId(foundMember.mosqueId);

    setStoredData('mm_members', updatedMembers);
    setStoredData('mm_active_mosque_id', foundMember.mosqueId);
    alert('آپ کامیابی سے ورکسپیس میں شامل ہو گئے ہیں!');
  };

  // Add Income & Receipt
  const handleAddIncome = (data: {
    amount: number;
    category: IncomeCategory;
    donorName: string;
    donorPhone: string;
    donorAddress: string;
    purpose: string;
    paymentMethod: PaymentMethod;
    notes: string;
  }): Receipt => {
    const mosqueCode = activeMosque?.code || 'MSQ';
    const year = new Date().getFullYear();
    const count = currentReceipts.length + 1;
    const receiptNumber = `${mosqueCode}-${year}-${count.toString().padStart(6, '0')}`;
    const words = numberToUrduWords(data.amount);

    const newIncome: Income = {
      id: 'inc_' + Math.random().toString(36).substring(2, 9),
      mosqueId: activeMosqueId,
      amount: data.amount,
      category: data.category,
      donorName: data.donorName,
      donorPhone: data.donorPhone,
      donorAddress: data.donorAddress,
      purpose: data.purpose,
      paymentMethod: data.paymentMethod,
      receiptNumber,
      recordedByUserName: 'ایڈمن',
      status: 'CONFIRMED',
      notes: data.notes,
      createdAt: Date.now(),
    };

    const newReceipt: Receipt = {
      id: 'rcp_' + Math.random().toString(36).substring(2, 9),
      mosqueId: activeMosqueId,
      receiptNumber,
      incomeId: newIncome.id,
      donorName: data.donorName,
      donorPhone: data.donorPhone,
      donorAddress: data.donorAddress,
      amount: data.amount,
      amountInWords: words,
      purpose: data.purpose,
      categoryUrdu: data.category,
      paymentMethodUrdu: data.paymentMethod,
      issuedBy: 'ایڈمن',
      issueDate: Date.now(),
      status: 'CONFIRMED',
      smsSent: Boolean(data.donorPhone),
      notes: data.notes,
    };

    const updatedIncomes = [newIncome, ...incomes];
    const updatedReceipts = [newReceipt, ...receipts];

    setIncomes(updatedIncomes);
    setReceipts(updatedReceipts);

    setStoredData('mm_incomes', updatedIncomes);
    setStoredData('mm_receipts', updatedReceipts);

    // Save SMS log if phone exists
    if (data.donorPhone) {
      const newSms: SmsLog = {
        id: 'sms_' + Math.random().toString(36).substring(2, 9),
        mosqueId: activeMosqueId,
        receiptNumber,
        recipientPhone: data.donorPhone,
        messageBody: `جامع مسجد میں مالی تعاون کا شکریہ۔ رسید نمبر: ${receiptNumber}، رقم: Rs. ${data.amount}۔ جزاک اللہ۔`,
        sentAt: Date.now(),
        status: 'SENT',
      };
      setSmsLogs(prev => {
        const u = [newSms, ...prev];
        setStoredData('mm_sms_logs', u);
        return u;
      });
    }

    return newReceipt;
  };

  // Cancel Receipt
  const handleCancelReceipt = (receipt: Receipt) => {
    if (!confirm(`کیا آپ واقعی رسید نمبر ${receipt.receiptNumber} منسوخ کرنا چاہتے ہیں؟`)) return;

    const updatedReceipts = receipts.map(r => r.id === receipt.id ? { ...r, status: 'CANCELLED' as const } : r);
    const updatedIncomes = incomes.map(i => i.receiptNumber === receipt.receiptNumber ? { ...i, status: 'CANCELLED' as const } : i);

    setReceipts(updatedReceipts);
    setIncomes(updatedIncomes);

    setStoredData('mm_receipts', updatedReceipts);
    setStoredData('mm_incomes', updatedIncomes);
    setSelectedReceipt(null);
  };

  // Add Donor
  const handleAddDonor = (data: {
    name: string;
    phone: string;
    address: string;
    pledgedMonthlyAmount: number;
    notes: string;
  }) => {
    const newDonor: Donor = {
      id: 'dnr_' + Math.random().toString(36).substring(2, 9),
      mosqueId: activeMosqueId,
      name: data.name,
      phone: data.phone,
      address: data.address,
      pledgedMonthlyAmount: data.pledgedMonthlyAmount,
      active: true,
      notes: data.notes,
      createdAt: Date.now(),
    };
    const updated = [newDonor, ...donors];
    setDonors(updated);
    setStoredData('mm_donors', updated);
  };

  // Add Expense
  const handleAddExpense = (data: {
    amount: number;
    category: ExpenseCategory;
    payeeName: string;
    billReceiptRef: string;
    paymentMethod: PaymentMethod;
    details: string;
  }) => {
    const newExp: Expense = {
      id: 'exp_' + Math.random().toString(36).substring(2, 9),
      mosqueId: activeMosqueId,
      amount: data.amount,
      category: data.category,
      payeeName: data.payeeName,
      billReceiptRef: data.billReceiptRef,
      paymentMethod: data.paymentMethod,
      details: data.details,
      recordedByUserName: 'ایڈمن',
      status: 'CONFIRMED',
      notes: '',
      date: Date.now(),
    };
    const updated = [newExp, ...expenses];
    setExpenses(updated);
    setStoredData('mm_expenses', updated);
  };

  // Add Utility Bill
  const handleAddBill = (data: {
    utilityType: 'بجلی' | 'گیس' | 'پانی';
    companyName: string;
    consumerNumber: string;
    billingMonth: string;
    amount: number;
  }) => {
    const newBill: UtilityBill = {
      id: 'bill_' + Math.random().toString(36).substring(2, 9),
      mosqueId: activeMosqueId,
      utilityType: data.utilityType,
      companyName: data.companyName,
      consumerNumber: data.consumerNumber,
      billingMonth: data.billingMonth,
      issueDate: Date.now(),
      dueDate: Date.now() + 86400000 * 7,
      amount: data.amount,
      paidAmount: 0,
      status: 'UNPAID',
    };
    const updated = [newBill, ...bills];
    setBills(updated);
    setStoredData('mm_bills', updated);
  };

  const handlePayBill = (billId: string) => {
    const updated = bills.map(b => b.id === billId ? { ...b, status: 'PAID' as const, paidAmount: b.amount, paidDate: Date.now() } : b);
    setBills(updated);
    setStoredData('mm_bills', updated);
  };

  // Add Salary
  const handleAddSalary = (data: {
    staffType: 'امام و خطیب' | 'مؤذن صاحب' | 'خادم مسجد';
    staffName: string;
    monthlySalary: number;
    monthYear: string;
    paidAmount: number;
    paymentMethod: PaymentMethod;
  }) => {
    const newSal: Salary = {
      id: 'sal_' + Math.random().toString(36).substring(2, 9),
      mosqueId: activeMosqueId,
      staffType: data.staffType,
      staffName: data.staffName,
      monthlySalary: data.monthlySalary,
      monthYear: data.monthYear,
      paidAmount: data.paidAmount,
      remainingAmount: 0,
      paymentDate: Date.now(),
      paymentMethod: data.paymentMethod,
      notes: 'بروقت ادائیگی',
    };
    const updated = [newSal, ...salaries];
    setSalaries(updated);
    setStoredData('mm_salaries', updated);
  };

  // Add Budget
  const handleAddBudget = (data: { category: ExpenseCategory; budgetedAmount: number; periodName: string }) => {
    const filtered = budgets.filter(b => !(b.mosqueId === activeMosqueId && b.category === data.category));
    const newBudget: Budget = {
      id: 'bdg_' + Math.random().toString(36).substring(2, 9),
      mosqueId: activeMosqueId,
      periodName: data.periodName,
      category: data.category,
      budgetedAmount: data.budgetedAmount,
    };
    const updated = [...filtered, newBudget];
    setBudgets(updated);
    setStoredData('mm_budgets', updated);
  };

  // Add Meeting
  const handleAddMeeting = (data: {
    meetingNumber: string;
    venue: string;
    attendees: string;
    agenda: string;
    decisions: string;
    assignedPerson: string;
  }) => {
    const newM: Meeting = {
      id: 'mtg_' + Math.random().toString(36).substring(2, 9),
      mosqueId: activeMosqueId,
      meetingNumber: data.meetingNumber,
      date: Date.now(),
      venue: data.venue,
      attendees: data.attendees,
      agenda: data.agenda,
      decisions: data.decisions,
      assignedPerson: data.assignedPerson,
      status: 'COMPLETED',
    };
    const updated = [newM, ...meetings];
    setMeetings(updated);
    setStoredData('mm_meetings', updated);
  };

  // Update Mosque Profile
  const handleUpdateMosque = (updated: Mosque) => {
    const updatedList = allMosques.map(m => m.id === updated.id ? updated : m);
    setAllMosques(updatedList);
    setStoredData('mm_mosques', updatedList);
  };

  return (
    <div className="min-h-screen flex flex-col bg-gray-50 dark:bg-gray-950 text-gray-900 dark:text-gray-100">
      
      {/* Top Header */}
      <Header
        activeMosque={activeMosque}
        activeRole={activeRole}
        isDarkMode={isDarkMode}
        onToggleTheme={() => setIsDarkMode(!isDarkMode)}
        onOpenSwitchMosque={() => setShowMosqueModal(true)}
      />

      {/* Main Content Area */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6 pb-24 md:pb-8">
        {activeTab === 0 && (
          <Dashboard
            mosque={activeMosque}
            incomes={currentIncomes}
            expenses={currentExpenses}
            bills={currentBills}
            onOpenAddIncome={() => setActiveTab(1)}
            onOpenAddExpense={() => setActiveTab(3)}
            onNavigateTab={setActiveTab}
            onSelectReceipt={setSelectedReceipt}
            receipts={currentReceipts}
          />
        )}

        {activeTab === 1 && (
          <IncomeSection
            incomes={currentIncomes}
            donors={currentDonors}
            onAddIncome={handleAddIncome}
            onAddDonor={handleAddDonor}
            onSelectReceipt={setSelectedReceipt}
            receipts={currentReceipts}
          />
        )}

        {activeTab === 2 && (
          <ReceiptsSection
            receipts={currentReceipts}
            onSelectReceipt={setSelectedReceipt}
          />
        )}

        {activeTab === 3 && (
          <ExpensesSection
            expenses={currentExpenses}
            bills={currentBills}
            salaries={currentSalaries}
            onAddExpense={handleAddExpense}
            onAddBill={handleAddBill}
            onPayBill={handlePayBill}
            onAddSalary={handleAddSalary}
          />
        )}

        {activeTab === 4 && (
          <div className="space-y-8">
            <ReportsSection
              mosque={activeMosque}
              incomes={currentIncomes}
              expenses={currentExpenses}
              budgets={currentBudgets}
              meetings={currentMeetings}
              onAddBudget={handleAddBudget}
              onAddMeeting={handleAddMeeting}
            />

            <SettingsSection
              mosque={activeMosque}
              members={currentMembers}
              isDarkMode={isDarkMode}
              onToggleTheme={() => setIsDarkMode(!isDarkMode)}
              onUpdateMosque={handleUpdateMosque}
            />
          </div>
        )}
      </main>

      {/* Bottom Navigation Bar */}
      <nav className="fixed bottom-0 inset-x-0 z-30 bg-white/95 dark:bg-gray-900/95 backdrop-blur-md border-t border-gray-200 dark:border-gray-800 shadow-lg">
        <div className="max-w-md mx-auto grid grid-cols-5 text-center">
          <button
            onClick={() => setActiveTab(0)}
            className={`py-3 flex flex-col items-center justify-center gap-1 transition-colors ${
              activeTab === 0 ? 'text-islamic-green font-bold' : 'text-gray-400 hover:text-gray-600'
            }`}
          >
            <LayoutDashboard className="w-5 h-5" />
            <span className="text-[11px]">ڈیش بورڈ</span>
          </button>

          <button
            onClick={() => setActiveTab(1)}
            className={`py-3 flex flex-col items-center justify-center gap-1 transition-colors ${
              activeTab === 1 ? 'text-islamic-green font-bold' : 'text-gray-400 hover:text-gray-600'
            }`}
          >
            <TrendingUp className="w-5 h-5" />
            <span className="text-[11px]">آمدن و چندہ</span>
          </button>

          <button
            onClick={() => setActiveTab(2)}
            className={`py-3 flex flex-col items-center justify-center gap-1 transition-colors ${
              activeTab === 2 ? 'text-islamic-green font-bold' : 'text-gray-400 hover:text-gray-600'
            }`}
          >
            <ReceiptIcon className="w-5 h-5" />
            <span className="text-[11px]">رسیدیں</span>
          </button>

          <button
            onClick={() => setActiveTab(3)}
            className={`py-3 flex flex-col items-center justify-center gap-1 transition-colors ${
              activeTab === 3 ? 'text-islamic-green font-bold' : 'text-gray-400 hover:text-gray-600'
            }`}
          >
            <ReceiptText className="w-5 h-5" />
            <span className="text-[11px]">اخراجات و بل</span>
          </button>

          <button
            onClick={() => setActiveTab(4)}
            className={`py-3 flex flex-col items-center justify-center gap-1 transition-colors ${
              activeTab === 4 ? 'text-islamic-green font-bold' : 'text-gray-400 hover:text-gray-600'
            }`}
          >
            <SettingsIcon className="w-5 h-5" />
            <span className="text-[11px]">رپورٹس و ترتیبات</span>
          </button>
        </div>
      </nav>

      {/* Receipt Preview Modal */}
      <ReceiptModal
        receipt={selectedReceipt}
        mosque={activeMosque}
        onClose={() => setSelectedReceipt(null)}
        onCancelReceipt={handleCancelReceipt}
      />

      {/* Mosque Workspace Switcher Modal */}
      <MosqueModal
        isOpen={showMosqueModal}
        onClose={() => setShowMosqueModal(false)}
        allMosques={allMosques}
        activeMosqueId={activeMosqueId}
        onSelectMosque={handleSelectMosque}
        onCreateMosque={handleCreateMosque}
        onJoinMosque={handleJoinMosque}
      />

    </div>
  );
};
