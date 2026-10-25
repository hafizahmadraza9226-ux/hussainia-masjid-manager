import React from 'react';
import { Mosque, UserRole } from '../types';
import { Building2, Moon, Sun, ArrowLeftRight } from 'lucide-react';

interface HeaderProps {
  activeMosque: Mosque | null;
  activeRole: UserRole;
  isDarkMode: boolean;
  onToggleTheme: () => void;
  onOpenSwitchMosque: () => void;
}

const roleTitles: Record<UserRole, string> = {
  SUPER_ADMIN: 'سپر ایڈمن',
  FINANCE_MANAGER: 'فنانس مینیجر',
  RECORD_MANAGER: 'ریکارڈ مینیجر',
  READ_REVIEW: 'ناظر / جائزہ',
};

export const Header: React.FC<HeaderProps> = ({
  activeMosque,
  activeRole,
  isDarkMode,
  onToggleTheme,
  onOpenSwitchMosque,
}) => {
  return (
    <header className="bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-800 sticky top-0 z-30 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3">
        <div className="flex items-center justify-between gap-4">
          
          {/* Mosque Branding */}
          <div 
            onClick={onOpenSwitchMosque}
            className="flex items-center gap-3 cursor-pointer group flex-1 min-w-0"
          >
            <div className="w-11 h-11 rounded-full bg-gradient-to-br from-islamic-green to-islamic-green-dark border-2 border-gold flex items-center justify-center text-white shrink-0 shadow">
              <Building2 className="w-6 h-6 text-gold" />
            </div>
            <div className="min-w-0">
              <div className="flex items-center gap-2">
                <h1 className="text-base sm:text-lg font-bold text-gray-900 dark:text-white truncate group-hover:text-islamic-green transition-colors">
                  {activeMosque?.name || 'مسجد منتخب نہیں'}
                </h1>
                <span className="hidden sm:inline-block px-2 py-0.5 rounded text-xs font-semibold bg-gold-subtle dark:bg-yellow-950/40 text-gold-dark dark:text-gold border border-gold/30">
                  {roleTitles[activeRole]}
                </span>
              </div>
              <p className="text-xs text-gray-500 dark:text-gray-400 truncate">
                {activeMosque?.code} • {activeMosque?.city} • {activeMosque?.phone}
              </p>
            </div>
          </div>

          {/* Action buttons */}
          <div className="flex items-center gap-2 shrink-0">
            <button
              onClick={onOpenSwitchMosque}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-islamic-green-subtle dark:bg-emerald-950/50 text-islamic-green hover:bg-emerald-100 dark:hover:bg-emerald-900/60 font-medium text-xs sm:text-sm border border-islamic-green/20 transition-colors"
            >
              <ArrowLeftRight className="w-4 h-4" />
              <span>مسجد بدلیں</span>
            </button>

            <button
              onClick={onToggleTheme}
              className="p-2 rounded-lg text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
              title="تھیم تبدیل کریں"
            >
              {isDarkMode ? <Sun className="w-5 h-5 text-gold" /> : <Moon className="w-5 h-5" />}
            </button>
          </div>

        </div>
      </div>
    </header>
  );
};
