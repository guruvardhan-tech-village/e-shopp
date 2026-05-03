# Frontend Architecture & Code Explanation

Welcome to the React frontend of the Expense Tracker! This document is written for beginners to understand exactly how the React app is structured and how you can easily modify or add to it.

## 📁 How the Folders are Organized

This project is built using **React**, **Vite** (for fast building), and **Tailwind CSS** (for styling).

1. `src/components/`: Reusable building blocks (like Buttons, Navbars, Layouts).
2. `src/pages/`: Entire screens (like Dashboard, Expenses, Budgets).
3. `src/context/`: Global State Management (things the whole app needs to know about, like who is logged in).
4. `src/services/`: Code that talks to the Backend API.

---

## 🎨 1. Styling (`src/index.css`)

We use **Tailwind CSS**, which means we style things using class names directly in the HTML (e.g., `<div className="flex bg-blue-500">`).

However, to keep things clean, we defined a **Global Design System** inside `src/index.css`.
- **CSS Variables:** At the top of `index.css`, you'll see `:root` (for Light Mode) and `.dark` (for Dark Mode). This defines all our colors. 
- **How to change colors:** If you want the primary color to be Green instead of Purple, simply change the `--color-primary-500` hex codes in `index.css`! Everything in the app will instantly update.
- **Components:** We created reusable classes like `.btn-primary` and `.card` in `index.css` so you don't have to type 20 Tailwind classes every time you make a button.

---

## 🌐 2. Talking to the Backend (`src/services/api.js`)

This file uses a library called `axios` to make HTTP requests to your Spring Boot backend running on `localhost:8080`.
- **The Magic Interceptor:** When you log in, we save your JWT token in `localStorage`. The code in `api.js` has an "interceptor" that automatically grabs this token and attaches it to *every single request* you make. You never have to manually attach the token when writing a new feature!

---

## 🧠 3. Global State (`src/context/`)

Sometimes, many different pages need to know the same information. We use React Context for this.

### `AuthContext.jsx`
- Manages whether a user is logged in or not. 
- Provides the `login()`, `register()`, and `logout()` functions.
- Provides the `user` object (which contains the `displayName`) to any component that asks for it via `useAuth()`.

### `ThemeContext.jsx`
- Manages Light vs Dark mode.
- Saves your preference to `localStorage` so it remembers your choice when you refresh.

### `CurrencyContext.jsx`
- Manages the currently selected currency (USD, INR, EUR, etc.).
- Provides a `formatCurrency(amount)` function. Any component can call this to automatically format a number into the correct currency format with symbols.

---

## 📄 4. The Pages (`src/pages/`)

Each file here represents an entire screen.

- **`Dashboard.jsx`:** The home screen. It fetches the latest expenses, calculates the total, and uses a library called `recharts` to draw the big graph.
- **`Expenses.jsx`, `Categories.jsx`, `Budgets.jsx`:** These follow a very similar pattern:
  1. `useEffect()` runs when the page loads to fetch data from the backend.
  2. The data is saved into a React `useState` variable.
  3. The HTML (JSX) maps over that data to display a table or a grid of cards.
  4. There is an `isModalOpen` state. When true, it shows a popup HTML form over the screen to Add new items.

---

## 🛠️ How to Add a Completely New Page

Let's say you built the `Income` feature on the backend, and now you want an Income page on the frontend.

1. **Create the Page:** 
   - Create `Income.jsx` inside the `src/pages/` folder.
   - Write a basic React component: `const Income = () => { return <div>Income Page</div>; }; export default Income;`
2. **Add it to the Router:** 
   - Open `src/App.jsx`.
   - Import your new page: `import Income from './pages/Income';`
   - Add a new Route inside the `<Route element={<Layout />}>` block: `<Route path="/income" element={<Income />} />`
3. **Add a Button to the Sidebar:** 
   - Open `src/components/Layout.jsx`.
   - Find the `navItems` array at the top.
   - Add your new link: `{ name: 'Income', path: '/income', icon: DollarSign }` (Make sure to import `DollarSign` from `lucide-react`).
4. **Done!** You now have a working page with a navigation button! You can now start adding API calls (`api.get('/incomes')`) inside `Income.jsx` to show your data.
