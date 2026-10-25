export type UserRole = 'SUPER_ADMIN' | 'FINANCE_MANAGER' | 'RECORD_MANAGER' | 'READ_REVIEW';

export interface Mosque {
  id: string;
  name: string;
  shortName: string;
  code: string;
  address: string;
  city: string;
  country: string;
  phone: string;
  email: string;
  currency: string;
  receiptNote: string;
  smsSenderName: string;
  smsTemplate: string;
  smsApiKey?: string;
  isDefaultInitial?: boolean;
  createdAt: number;
}

export interface MosqueMember {
  id: string;
  mosqueId: string;
  userId: string;
  userName: string;
  userEmail: string;
  userPhone: string;
  role: UserRole;
  inviteCode: string;
  isConfirmed: boolean;
  joinedAt: number;
}

export type IncomeCategory =
  | 'عمومی چندہ'
  | 'ماہانہ تعاون'
  | 'جمعہ چندہ'
  | 'رمضان چندہ'
  | 'تعمیراتی فنڈ'
  | 'تنخواہوں کے لیے تعاون'
  | 'دیگر منظور شدہ آمدن';

export type PaymentMethod =
  | 'نقد (Cash)'
  | 'بینک ٹرانسفر'
  | 'جاز کیش (JazzCash)'
  | 'ایزی پیسہ (Easypaisa)'
  | 'بینک چیک (Cheque)';

export interface Income {
  id: string;
  mosqueId: string;
  amount: number;
  category: IncomeCategory;
  donorName: string;
  donorPhone: string;
  donorAddress: string;
  purpose: string;
  paymentMethod: PaymentMethod;
  receiptNumber: string;
  recordedByUserName: string;
  status: 'CONFIRMED' | 'CANCELLED';
  notes: string;
  createdAt: number;
}

export interface Donor {
  id: string;
  mosqueId: string;
  name: string;
  phone: string;
  address: string;
  pledgedMonthlyAmount: number;
  active: boolean;
  notes: string;
  createdAt: number;
}

export interface Receipt {
  id: string;
  mosqueId: string;
  receiptNumber: string;
  incomeId?: string;
  donorName: string;
  donorPhone: string;
  donorAddress: string;
  amount: number;
  amountInWords: string;
  purpose: string;
  categoryUrdu: string;
  paymentMethodUrdu: string;
  issuedBy: string;
  issueDate: number;
  status: 'CONFIRMED' | 'CANCELLED';
  smsSent: boolean;
  notes: string;
}

export type ExpenseCategory =
  | 'بجلی کا بل'
  | 'سوئی گیس کا بل'
  | 'پانی کا بل / واسا'
  | 'تنخواہ امام صاحب'
  | 'تنخواہ مؤذن صاحب'
  | 'صفائی ستھرائی و فنائل'
  | 'تعمیراتی کام'
  | 'مرمت و دیکھ بھال'
  | 'برقی سامان و وائرنگ'
  | 'سولر سسٹم و بیٹری'
  | 'CCTV کیمرے و سیکیورٹی'
  | 'قالین، صفیں اور چٹائیاں'
  | 'دیگر مسجدی ضروریات';

export interface Expense {
  id: string;
  mosqueId: string;
  amount: number;
  category: ExpenseCategory;
  payeeName: string;
  billReceiptRef: string;
  paymentMethod: PaymentMethod;
  details: string;
  recordedByUserName: string;
  status: 'CONFIRMED' | 'CANCELLED';
  notes: string;
  date: number;
}

export interface UtilityBill {
  id: string;
  mosqueId: string;
  utilityType: 'بجلی' | 'گیس' | 'پانی';
  companyName: string;
  consumerNumber: string;
  billingMonth: string;
  issueDate: number;
  dueDate: number;
  amount: number;
  paidAmount: number;
  paidDate?: number;
  status: 'UNPAID' | 'PAID' | 'OVERDUE';
}

export interface Salary {
  id: string;
  mosqueId: string;
  staffType: 'امام و خطیب' | 'مؤذن صاحب' | 'خادم مسجد';
  staffName: string;
  monthlySalary: number;
  monthYear: string;
  paidAmount: number;
  remainingAmount: number;
  paymentDate: number;
  paymentMethod: PaymentMethod;
  notes: string;
}

export interface Budget {
  id: string;
  mosqueId: string;
  periodName: string;
  category: ExpenseCategory;
  budgetedAmount: number;
}

export interface Meeting {
  id: string;
  mosqueId: string;
  meetingNumber: string;
  date: number;
  venue: string;
  attendees: string;
  agenda: string;
  decisions: string;
  assignedPerson: string;
  status: 'COMPLETED' | 'IN_PROGRESS' | 'PENDING';
}

export interface SmsLog {
  id: string;
  mosqueId: string;
  receiptNumber: string;
  recipientPhone: string;
  messageBody: string;
  sentAt: number;
  status: 'SENT' | 'FAILED' | 'QUEUED';
}
