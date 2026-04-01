export function Spinner({ size = 'medium' }) {
  const sizes = {
    small: '20px',
    medium: '40px',
    large: '60px'
  };

  const spinnerSize = sizes[size] || sizes.medium;

  return (
    <div style={{
      display: 'inline-block',
      width: spinnerSize,
      height: spinnerSize,
      border: '4px solid #f3f3f3',
      borderTop: '4px solid #007bff',
      borderRadius: '50%',
      animation: 'spin 1s linear infinite'
    }} />
  );
}

export function LoadingPage({ message = 'Loading...' }) {
  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
      alignItems: 'center',
      minHeight: '400px',
      gap: '1rem'
    }}>
      <Spinner size="large" />
      <div style={{ color: '#666', fontSize: '1.125rem' }}>{message}</div>
    </div>
  );
}

export function LoadingOverlay({ message = 'Processing...' }) {
  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0,0,0,0.5)',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
      alignItems: 'center',
      gap: '1rem',
      zIndex: 9999
    }}>
      <Spinner size="large" />
      <div style={{ color: 'white', fontSize: '1.125rem' }}>{message}</div>
    </div>
  );
}

// Add keyframe animation to CSS
const style = document.createElement('style');
style.textContent = `
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
`;
document.head.appendChild(style);
