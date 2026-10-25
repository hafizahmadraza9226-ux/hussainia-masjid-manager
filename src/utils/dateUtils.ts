const urduMonths = [
  "جنوری", "فروری", "مارچ", "اپریل", "مئی", "جون",
  "جولائی", "اگست", "ستمبر", "اکتوبر", "نومبر", "دسمبر"
];

export function formatUrduDate(timestamp: number): string {
  const date = new Date(timestamp);
  const day = date.getDate();
  const month = urduMonths[date.getMonth()];
  const year = date.getFullYear();
  return `${day} ${month} ${year}`;
}

export function formatUrduDateTime(timestamp: number): string {
  const date = new Date(timestamp);
  const datePart = formatUrduDate(timestamp);
  const hours = date.getHours();
  const minutes = date.getMinutes().toString().padStart(2, '0');
  const ampm = hours >= 12 ? 'شام' : 'صبح';
  const formattedHours = hours % 12 || 12;
  return `${datePart} (${formattedHours}:${minutes} ${ampm})`;
}

export function getCurrentMonthUrdu(): string {
  const date = new Date();
  return `${urduMonths[date.getMonth()]} ${date.getFullYear()}`;
}
