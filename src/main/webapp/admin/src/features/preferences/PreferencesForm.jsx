import { useState } from 'react';
import { useToast } from '../../components/Toast';
import api from '../../api/client';

export default function PreferencesForm() {
  const [key, setKey] = useState('');
  const [value, setValue] = useState('');
  const [currentValue, setCurrentValue] = useState('');
  const [loading, setLoading] = useState(false);
  const toast = useToast();

  const handleGet = async () => {
    if (!key) {
      toast.warning('Please enter a preference key');
      return;
    }

    setLoading(true);
    try {
      const response = await api.get(`/preferences/${key}`);
      setValue(response.data.value);
      setCurrentValue(response.data.value);
      toast.success('Preference loaded successfully');
    } catch (error) {
      toast.error('Failed to get preference: ' + (error.response?.data?.error || error.message));
    } finally {
      setLoading(false);
    }
  };

  const handleSet = async () => {
    if (!key || !value) {
      toast.warning('Please enter both key and value');
      return;
    }

    setLoading(true);
    try {
      await api.put(`/preferences/${key}`, { key, value });
      toast.success('Preference updated successfully');
      setCurrentValue(value);
    } catch (error) {
      toast.error('Failed to set preference: ' + (error.response?.data?.error || error.message));
    } finally {
      setLoading(false);
    }
  };

  const formStyle = {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    maxWidth: '600px'
  };

  const inputStyle = {
    width: '100%',
    padding: '0.5rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
    marginBottom: '1rem'
  };

  const buttonStyle = {
    padding: '0.5rem 1.5rem',
    marginRight: '0.5rem',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '1rem'
  };

  return (
    <div>
      <h1>Server Preferences</h1>

      <div style={formStyle}>
        <div style={{ marginBottom: '1rem' }}>
          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600' }}>
            Preference Key
          </label>
          <input
            type="text"
            value={key}
            onChange={(e) => setKey(e.target.value)}
            placeholder="e.g., dir.msg.send"
            style={inputStyle}
            disabled={loading}
          />
        </div>

        <div style={{ marginBottom: '1rem' }}>
          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600' }}>
            Value
          </label>
          <input
            type="text"
            value={value}
            onChange={(e) => setValue(e.target.value)}
            placeholder="Preference value"
            style={inputStyle}
            disabled={loading}
          />
        </div>

        <div style={{ marginBottom: '1rem' }}>
          <button
            onClick={handleGet}
            disabled={loading}
            style={{
              ...buttonStyle,
              backgroundColor: '#007bff',
              color: 'white'
            }}
          >
            Get Value
          </button>
          <button
            onClick={handleSet}
            disabled={loading}
            style={{
              ...buttonStyle,
              backgroundColor: '#28a745',
              color: 'white'
            }}
          >
            Set Value
          </button>
        </div>

        {currentValue && (
          <div style={{
            marginTop: '1.5rem',
            padding: '1rem',
            backgroundColor: '#f8f9fa',
            borderRadius: '4px'
          }}>
            <strong>Current Value:</strong>
            <pre style={{ marginTop: '0.5rem', whiteSpace: 'pre-wrap' }}>{currentValue}</pre>
          </div>
        )}
      </div>

      <div style={{
        marginTop: '2rem',
        padding: '1rem',
        backgroundColor: '#fff3cd',
        borderRadius: '8px',
        color: '#856404'
      }}>
        <strong>Note:</strong> Common preference keys include dir.msg.send, dir.msg.receive, as2.use.http.1.1, etc.
        Refer to the AS2 server documentation for complete list of configuration options.
      </div>
    </div>
  );
}
