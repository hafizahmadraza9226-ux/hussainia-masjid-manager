/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        islamic: {
          green: '#0B5E3B',
          'green-dark': '#073F27',
          'green-light': '#148554',
          'green-subtle': '#E8F5EF',
        },
        gold: {
          DEFAULT: '#D4AF37',
          dark: '#AA8820',
          light: '#F5E6B3',
          subtle: '#FFF9E6',
        }
      }
    },
  },
  plugins: [],
}
