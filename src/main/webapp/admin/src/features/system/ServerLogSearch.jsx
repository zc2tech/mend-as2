/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

import { useState } from 'react';
import { useToast } from '../../components/Toast';
import api from '../../api/client';

export default function ServerLogSearch() {
  const [searchText, setSearchText] = useState('');
  const [result, setResult] = useState(null);
  const [searching, setSearching] = useState(false);
  const toast = useToast();

  const handleSearch = async () => {
    setSearching(true);
    try {
      const params = new URLSearchParams();
      if (searchText.trim()) {
        params.append('searchText', searchText.trim());
      }
      params.append('limit', '500');

      const response = await api.get(`/system/serverlog?${params.toString()}`);
      setResult(response.data);

      if (response.data.matchedLines === 0) {
        toast.info('No matches found');
      }
    } catch (error) {
      toast.error('Failed to search server log: ' + (error.response?.data?.error || error.message));
    } finally {
      setSearching(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const cardStyle = {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '1.5rem'
  };

  const inputStyle = {
    flex: 1,
    padding: '0.5rem',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    fontSize: '0.875rem',
    fontFamily: 'monospace'
  };

  const buttonStyle = {
    padding: '0.5rem 1.5rem',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: searching ? 'not-allowed' : 'pointer',
    opacity: searching ? 0.6 : 1,
    fontSize: '0.875rem',
    fontWeight: '600'
  };

  const logContainerStyle = {
    backgroundColor: '#1e1e1e',
    color: '#d4d4d4',
    padding: '1rem',
    borderRadius: '4px',
    fontFamily: 'Consolas, Monaco, "Courier New", monospace',
    fontSize: '0.75rem',
    maxHeight: '600px',
    overflowY: 'auto',
    lineHeight: '1.5',
    whiteSpace: 'pre-wrap',
    wordBreak: 'break-all'
  };

  const infoStyle = {
    padding: '0.75rem',
    backgroundColor: '#e7f3ff',
    border: '1px solid #b3d9ff',
    borderRadius: '4px',
    marginBottom: '1rem',
    fontSize: '0.875rem',
    color: '#004085'
  };

  const warningStyle = {
    padding: '0.75rem',
    backgroundColor: '#fff3cd',
    border: '1px solid #ffc107',
    borderRadius: '4px',
    marginBottom: '1rem',
    fontSize: '0.875rem',
    color: '#856404'
  };

  const highlightText = (text, search) => {
    if (!search || !text) return text;

    const searchLower = search.toLowerCase();
    const textLower = text.toLowerCase();
    const index = textLower.indexOf(searchLower);

    if (index === -1) return text;

    const before = text.substring(0, index);
    const match = text.substring(index, index + search.length);
    const after = text.substring(index + search.length);

    return (
      <>
        {before}
        <span style={{ backgroundColor: '#ffff00', color: '#000', fontWeight: '600' }}>
          {match}
        </span>
        {highlightText(after, search)}
      </>
    );
  };

  return (
    <div>
      <div style={cardStyle}>
        <h2 style={{ marginTop: 0, marginBottom: '1rem' }}>Search in Today's Server Log</h2>

        <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
          <input
            type="text"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Enter search text (case-insensitive, leave empty to show all)"
            style={inputStyle}
            disabled={searching}
          />
          <button
            onClick={handleSearch}
            disabled={searching}
            style={buttonStyle}
          >
            {searching ? 'Searching...' : 'Search'}
          </button>
        </div>

        {result && (
          <>
            <div style={infoStyle}>
              <strong>Log file:</strong> {result.logFile}
              <br />
              <strong>Total lines:</strong> {result.totalLines} | <strong>Matched lines:</strong> {result.matchedLines}
              {result.truncated && (
                <>
                  {' '}| <strong style={{ color: '#dc3545' }}>Showing last 500 matches only</strong>
                </>
              )}
            </div>

            {result.truncated && (
              <div style={warningStyle}>
                <strong>⚠️ Results truncated:</strong> Only showing the last 500 matching lines.
                Try a more specific search to see all results.
              </div>
            )}

            {result.lines && result.lines.length > 0 ? (
              <div>
                <div style={{ marginBottom: '0.5rem', fontSize: '0.875rem', color: '#6c757d' }}>
                  {searchText.trim() && (
                    <>Matches are highlighted in yellow. </>
                  )}
                  Scroll down to see more lines.
                </div>
                <div style={logContainerStyle}>
                  {result.lines.map((line, index) => (
                    <div key={index} style={{ marginBottom: '0.1rem' }}>
                      {searchText.trim() ? highlightText(line, searchText.trim()) : line}
                    </div>
                  ))}
                </div>
              </div>
            ) : (
              <div style={{ padding: '2rem', textAlign: 'center', color: '#6c757d', backgroundColor: '#f8f9fa', borderRadius: '4px' }}>
                No log entries found
              </div>
            )}
          </>
        )}

        {!result && (
          <div style={{ padding: '2rem', textAlign: 'center', color: '#6c757d', backgroundColor: '#f8f9fa', borderRadius: '4px' }}>
            Enter search text and click Search, or click Search with empty field to show all log entries
          </div>
        )}
      </div>
    </div>
  );
}
