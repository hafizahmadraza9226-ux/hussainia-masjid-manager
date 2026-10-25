import { Mosque, MosqueMember, Salary } from '../types';

const DEFAULT_MOSQUE: Mosque = {
  id: 'hussainia_adda_wali',
  name: 'جامع مسجد حسینیہ رضویہ المعروف اڈے والی مسجد',
  shortName: 'جامع مسجد حسینیہ',
  code: 'HMR',
  address: 'مین اڈا لاری، نزد جی ٹی روڈ',
  city: 'لاہور',
  country: 'پاکستان',
  phone: '0300-7654321',
  email: 'hussainia.masjid@gmail.com',
  currency: 'PKR',
  receiptNote: 'اللہ تعالیٰ آپ کے تعاون کو قبول فرمائے اور آپ کے لیے اجرِ عظیم کا ذریعہ بنائے۔ آمین۔',
  smsSenderName: 'HMR-Masjid',
  smsTemplate: 'جامع مسجد {مسجد کا نام} میں مالی تعاون کرنے کا بہت شکریہ۔ آپ کی رقم مبلغ {رقم} روپے وصول کر لی گئی ہے۔ رسید نمبر: {رسید نمبر}، تاریخ: {تاریخ}، مقصد: {مقصد}۔ جزاک اللہ خیراً۔',
  isDefaultInitial: true,
  createdAt: Date.now(),
};

const DEFAULT_MEMBERS: MosqueMember[] = [
  {
    id: 'mem_1',
    mosqueId: 'hussainia_adda_wali',
    userId: 'u_admin',
    userName: 'حافظ احمد رضا (سپر ایڈمن)',
    userEmail: 'hafizahmadraza9226@gmail.com',
    userPhone: '0300-1112233',
    role: 'SUPER_ADMIN',
    inviteCode: 'HMR-ADM',
    isConfirmed: true,
    joinedAt: Date.now(),
  },
  {
    id: 'mem_2',
    mosqueId: 'hussainia_adda_wali',
    userId: 'u_fin',
    userName: 'محمد عثمان (فنانس مینیجر)',
    userEmail: 'finance@hussainia.pk',
    userPhone: '0321-4455667',
    role: 'FINANCE_MANAGER',
    inviteCode: 'HMR-FIN',
    isConfirmed: true,
    joinedAt: Date.now(),
  },
  {
    id: 'mem_3',
    mosqueId: 'hussainia_adda_wali',
    userId: 'u_rec',
    userName: 'قاری عبدالرشید (ریکارڈ مینیجر)',
    userEmail: 'records@hussainia.pk',
    userPhone: '0333-7788990',
    role: 'RECORD_MANAGER',
    inviteCode: 'HMR-REC',
    isConfirmed: true,
    joinedAt: Date.now(),
  },
  {
    id: 'mem_4',
    mosqueId: 'hussainia_adda_wali',
    userId: 'u_rev',
    userName: 'چوہدری بشیر احمد (ناظر)',
    userEmail: 'review@hussainia.pk',
    userPhone: '0345-9988776',
    role: 'READ_REVIEW',
    inviteCode: 'HMR-REV',
    isConfirmed: true,
    joinedAt: Date.now(),
  },
];

export function getStoredData<T>(key: string, defaultValue: T): T {
  try {
    const item = localStorage.getItem(key);
    return item ? JSON.parse(item) : defaultValue;
  } catch {
    return defaultValue;
  }
}

export function setStoredData<T>(key: string, value: T): void {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (err) {
    console.error("Storage error:", err);
  }
}

export function initializeDatabase() {
  const mosques = getStoredData<Mosque[]>('mm_mosques', []);
  if (mosques.length === 0) {
    setStoredData('mm_mosques', [DEFAULT_MOSQUE]);
    setStoredData('mm_active_mosque_id', DEFAULT_MOSQUE.id);
    setStoredData('mm_members', DEFAULT_MEMBERS);
    
    // Seed initial salaries for Imam and Moazzin
    const sampleSalaries: Salary[] = [
      {
        id: 'sal_1',
        mosqueId: 'hussainia_adda_wali',
        staffType: 'امام و خطیب',
        staffName: 'مولانا حافظ احمد رضا صاحب',
        monthlySalary: 45000,
        monthYear: 'اکتوبر 2026',
        paidAmount: 45000,
        remainingAmount: 0,
        paymentDate: Date.now() - 86400000 * 3,
        paymentMethod: 'بینک ٹرانسفر',
        notes: 'بروقت ادائیگی بابت ماہانہ خدمات'
      },
      {
        id: 'sal_2',
        mosqueId: 'hussainia_adda_wali',
        staffType: 'مؤذن صاحب',
        staffName: 'قاری محمد بلال صاحب',
        monthlySalary: 28000,
        monthYear: 'اکتوبر 2026',
        paidAmount: 28000,
        remainingAmount: 0,
        paymentDate: Date.now() - 86400000 * 3,
        paymentMethod: 'نقد (Cash)',
        notes: 'بروقت نقد ادائیگی'
      }
    ];
    setStoredData('mm_salaries', sampleSalaries);
  }
}
