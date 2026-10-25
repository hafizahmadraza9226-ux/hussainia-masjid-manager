const ones = [
  "", "ایک", "دو", "تین", "چار", "پانچ", "چھ", "سات", "آٹھ", "نو", "دس",
  "گیارہ", "بارہ", "تیرہ", "چودہ", "پندرہ", "سولہ", "سترہ", "اٹھارہ", "انیس", "بیس",
  "اکیس", "بائیس", "تیئیس", "چوبیس", "پچیس", "چھبیس", "ستائیس", "اٹھائیس", "انتیس", "تیس",
  "اکتیس", "بتیس", "تینتیس", "چونتیس", "پینتیس", "چھتیس", "سینتیس", "اڑتیس", "انتالیس", "چالیس",
  "اکتالیس", "بیالیس", "تینتالیس", "چوالیس", "پینتالیس", "چھیاسلیس", "سینتالیس", "اڑتالیس", "انچاس", "پچاس",
  "اکیاون", "باون", "ترپن", "چون", "پچپن", "چھپن", "ستاون", "اٹھاون", "انسٹھ", "ساٹھ",
  "اکسٹھ", "باسٹھ", "تریسٹھ", "چونسٹھ", "پینسٹھ", "چھیاسٹھ", "ستاسٹھ", "اٹھاسٹھ", "انتر", "ستر",
  "اکہتر", "بہتر", "تہتر", "چوہتر", "پچہتر", "چھہتر", "ستتر", "اٹھہتر", "اناسی", "اسی",
  "اکیاسی", "بیاسی", "تراسی", "چوراسی", "پچاسی", "چھیاسی", "ستاسی", "اٹھاسی", "نواسی", "نوے",
  "اکیانوے", "بانوے", "ترانوے", "چورانوے", "پچانوے", "چھانوے", "ستانوے", "اٹھانوے", "ننانوے"
];

function convert(n: number): string {
  if (n === 0) return "";
  if (n >= 10000000) {
    const crores = Math.floor(n / 10000000);
    const rem = n % 10000000;
    return `${convert(crores)} کروڑ ${convert(rem)}`;
  }
  if (n >= 100000) {
    const lakhs = Math.floor(n / 100000);
    const rem = n % 100000;
    return `${convert(lakhs)} لاکھ ${convert(rem)}`;
  }
  if (n >= 1000) {
    const thousands = Math.floor(n / 1000);
    const rem = n % 1000;
    return `${convert(thousands)} ہزار ${convert(rem)}`;
  }
  if (n >= 100) {
    const hundreds = Math.floor(n / 100);
    const rem = n % 100;
    const hundredWord = hundreds === 1 ? "ایک سو" : hundreds === 2 ? "دو سو" : hundreds === 3 ? "تین سو" : `${convert(hundreds)} سو`;
    return `${hundredWord} ${convert(rem)}`;
  }
  if (n > 0 && n < 100) {
    return ones[n] || "";
  }
  return n.toString();
}

export function numberToUrduWords(amount: number): string {
  if (!amount || amount === 0) return "صفر روپے فقط";
  if (amount < 0) return "منفی " + numberToUrduWords(-amount);
  const words = convert(Math.floor(amount)).trim();
  return `${words} روپے فقط`;
}

export function formatPKR(amount: number): string {
  return "Rs. " + (amount || 0).toLocaleString('en-US');
}
