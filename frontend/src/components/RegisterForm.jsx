import { useState } from 'react';

 function RegisterForm() {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: ''
  });

  const [token, setToken] = useState('');
  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setToken('');

    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });

      if (!response.ok) throw new Error('Registration failed');

      const data = await response.json();
      setToken(data.token);
      localStorage.setItem('token', data.token);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input name="name" placeholder="Name" value={formData.name} onChange={handleChange} required />
      <input name="email" placeholder="Email" value={formData.email} onChange={handleChange} type="email" required />
      <input name="password" placeholder="Password" value={formData.password} onChange={handleChange} type="password" required />
      <button type="submit">Register</button>
      {token && <p style={{ color: 'green' }}>Token saved</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </form>
  );
}

export default RegisterForm;
