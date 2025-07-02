import { useState, useEffect } from "react";

 function TransactionsPage() {
  const [transactions, setTransactions] = useState([]);
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState({
    amount: "",
    type: "EXPENSE",
    categoryId: "",
    date: "",
    description: "",
  });

  const token = localStorage.getItem("token");

  useEffect(() => {
    fetch("http://localhost:8080/api/transactions", {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => res.json())
      .then(setTransactions);

    fetch("http://localhost:8080/api/categories", {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => res.json())
      .then(setCategories);
  }, []);

  const handleChange = (e) => {
    setForm(prev => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const response = await fetch("http://localhost:8080/api/transactions", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(form)
    });

    if (response.ok) {
      const newTransaction = await response.json();
      setTransactions(prev => [...prev, newTransaction]);
      setForm({ amount: "", type: "EXPENSE", categoryId: "", date: "", description: "" });
    } else {
      alert("Error creating transaction");
    }
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>Transactions</h2>

      <form onSubmit={handleSubmit}>
        <input
          name="amount"
          type="number"
          placeholder="Amount"
          value={form.amount}
          onChange={handleChange}
          required
        />
        <select name="type" value={form.type} onChange={handleChange}>
          <option value="EXPENSE">Expense</option>
          <option value="INCOME">Income</option>
        </select>
        <select name="categoryId" value={form.categoryId} onChange={handleChange} required>
          <option value="">-- Category --</option>
          {categories.map(cat => (
            <option key={cat.id} value={cat.id}>{cat.name}</option>
          ))}
        </select>
        <input
          name="date"
          type="date"
          value={form.date}
          onChange={handleChange}
          required
        />
        <input
          name="description"
          type="text"
          placeholder="Description"
          value={form.description}
          onChange={handleChange}
        />
        <button type="submit">Create</button>
      </form>

      <ul>
        {transactions.map(tx => (
          <li key={tx.id}>
            [{tx.date}] {tx.type} {tx.amount} — {tx.categoryName} ({tx.description})
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TransactionsPage;
