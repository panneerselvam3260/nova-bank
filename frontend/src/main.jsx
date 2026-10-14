import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import './styles.css';

const json = async (res) => {
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.message || 'Request failed');
  return data;
};

async function api(path, options = {}) {
  const token = sessionStorage.getItem('token');
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  if (token) headers.Authorization = `Bearer ${token}`;
  return json(await fetch(path, { ...options, headers }));
}

function App() {
  const [auth, setAuth] = useState(() => sessionStorage.getItem('token') ? { username: sessionStorage.getItem('username') } : null);
  return auth ? <Dashboard auth={auth} onLogout={() => { sessionStorage.clear(); setAuth(null); }} /> : <Login onLogin={setAuth} />;
}

function Login({ onLogin }) {
  const [username, setUsername] = useState('demo@novabank.com');
  const [password, setPassword] = useState('password');
  const [error, setError] = useState('');
  const submit = async (e) => {
    e.preventDefault(); setError('');
    try {
      const data = await api('/api/auth/login', { method: 'POST', body: JSON.stringify({ username, password }) });
      sessionStorage.setItem('token', data.token); sessionStorage.setItem('username', data.username);
      onLogin({ username: data.username, role: data.role });
    } catch (err) { setError(err.message); }
  };
  return <div className="login-shell"><div className="login-card"><div className="brand">NOVA<span> BANK</span></div><h1>Welcome back</h1><p>Secure digital banking dashboard</p><form onSubmit={submit}><label>Email<input value={username} onChange={e=>setUsername(e.target.value)} /></label><label>Password<input type="password" value={password} onChange={e=>setPassword(e.target.value)} /></label>{error && <div className="error">{error}</div>}<button className="primary">Sign in</button></form><div className="hint">Demo: demo@novabank.com / password</div></div></div>;
}

function Dashboard({ auth, onLogout }) {
  const [accounts, setAccounts] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [type, setType] = useState('SAVINGS');
  const [from, setFrom] = useState(''); const [to, setTo] = useState(''); const [amount, setAmount] = useState('');
  const [message, setMessage] = useState(''); const [error, setError] = useState('');

  const load = async () => {
    try { setAccounts(await api('/api/accounts')); setTransactions(await api('/api/transactions')); setError(''); } catch (err) { setError(err.message); }
  };
  useEffect(() => { load(); }, []);
  useEffect(() => { if (!from && accounts[0]) setFrom(accounts[0].accountNumber); if (!to && accounts[1]) setTo(accounts[1].accountNumber); }, [accounts]);
  const total = useMemo(() => accounts.reduce((s,a)=>s+Number(a.balance),0), [accounts]);

  const createAccount = async () => { try { await api('/api/accounts', { method:'POST', body:JSON.stringify({accountType:type}) }); setMessage('New account created.'); load(); } catch(err){ setError(err.message); } };
  const transfer = async (e) => { e.preventDefault(); setMessage(''); setError(''); try { await api('/api/transactions/transfer', { method:'POST', body:JSON.stringify({fromAccount:from,toAccount:to,amount:Number(amount)}) }); setMessage(`Transfer of ₹${Number(amount).toLocaleString('en-IN')} completed.`); setAmount(''); load(); } catch(err){ setError(err.message); } };

  return <div className="app"><header><div className="brand">NOVA<span> BANK</span></div><div className="user"><span>{auth.username}</span><button onClick={onLogout}>Logout</button></div></header><main><section className="hero"><div><p className="eyebrow">DIGITAL BANKING</p><h1>Your money, one dashboard.</h1><p>Manage accounts and move money using a Java 17 microservices backend.</p></div><div className="total"><span>Total balance</span><strong>₹{total.toLocaleString('en-IN',{minimumFractionDigits:2})}</strong></div></section>{message && <div className="success">{message}</div>}{error && <div className="error">{error}</div>}
    <div className="grid">
      <section className="panel accounts"><div className="panel-title"><h2>My accounts</h2><span>{accounts.length} active</span></div>{accounts.map(a=><div className="account" key={a.accountNumber}><div><b>{a.accountType}</b><small>{a.accountNumber} · {a.currency} · {a.status}</small></div><strong>₹{Number(a.balance).toLocaleString('en-IN',{minimumFractionDigits:2})}</strong></div>)}<div className="create"><select value={type} onChange={e=>setType(e.target.value)}><option>SAVINGS</option><option>CURRENT</option><option>SALARY</option></select><button onClick={createAccount}>+ Open account</button></div></section>
      <section className="panel"><div className="panel-title"><h2>Transfer</h2><span>Internal accounts</span></div><form className="transfer" onSubmit={transfer}><label>From<select value={from} onChange={e=>setFrom(e.target.value)}>{accounts.map(a=><option key={a.accountNumber} value={a.accountNumber}>{a.accountNumber} — ₹{Number(a.balance).toLocaleString('en-IN')}</option>)}</select></label><label>To<select value={to} onChange={e=>setTo(e.target.value)}>{accounts.map(a=><option key={a.accountNumber} value={a.accountNumber}>{a.accountNumber}</option>)}</select></label><label>Amount<input type="number" min="0.01" step="0.01" value={amount} onChange={e=>setAmount(e.target.value)} placeholder="0.00" /></label><button className="primary">Transfer money</button></form></section>
    </div>
    <section className="panel"><div className="panel-title"><h2>Recent transactions</h2><button className="ghost" onClick={load}>Refresh</button></div>{transactions.length===0 ? <p className="empty">No transactions yet.</p> : <div className="table"><div className="thead"><span>Date</span><span>From</span><span>To</span><span>Amount</span><span>Status</span></div>{transactions.map(t=><div className="tr" key={t.id}><span>{new Date(t.createdAt).toLocaleString()}</span><span>{t.fromAccount}</span><span>{t.toAccount}</span><span>₹{Number(t.amount).toLocaleString('en-IN',{minimumFractionDigits:2})}</span><span className="badge">{t.status}</span></div>)}</div>}</section>
  </main><footer>Nova Bank demo project · Java 17 · Spring Boot · React · MySQL · Docker · Kubernetes · AWS</footer></div>;
}

createRoot(document.getElementById('root')).render(<App />);
