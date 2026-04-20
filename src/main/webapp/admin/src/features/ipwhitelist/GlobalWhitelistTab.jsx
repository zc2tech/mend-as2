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
import { useGlobalWhitelist, useAddGlobalWhitelist, useUpdateGlobalWhitelist, useDeleteGlobalWhitelist } from './useIPWhitelist';
import { useToast } from '../../components/Toast';
import { LoadingPage } from '../../components/Loading';
import IPWhitelistForm from './IPWhitelistForm';

export default function GlobalWhitelistTab() {
  const [targetTypeFilter, setTargetTypeFilter] = useState(null);
  const [ipPatternFilter, setIpPatternFilter] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(50);
  const { data: entries, isLoading, error } = useGlobalWhitelist(targetTypeFilter);
  const addMutation = useAddGlobalWhitelist();
  const updateMutation = useUpdateGlobalWhitelist();
  const deleteMutation = useDeleteGlobalWhitelist();
  const toast = useToast();
  const [showForm, setShowForm] = useState(false);
  const [editingEntry, setEditingEntry] = useState(null);

  if (isLoading) {
    return <LoadingPage message="Loading global whitelist..." />;
  }

  if (error) {
    return <div style={{ color: 'red' }}>Error loading whitelist: {error.message}</div>;
  }

  const handleAdd = () => {
    setEditingEntry(null);
    setShowForm(true);
  };

  const handleEdit = (entry) => {
    setEditingEntry(entry);
    setShowForm(true);
  };

  const handleDelete = async (entry) => {
    if (window.confirm(`Delete IP pattern "${entry.ipPattern}" for ${entry.targetType}?`)) {
      try {
        await deleteMutation.mutateAsync(entry.id);
        toast.success('Entry deleted successfully');
      } catch (error) {
        toast.error('Failed to delete entry: ' + (error.response?.data?.error || error.message));
      }
    }
  };

  const handleSubmit = async (formData) => {
    try {
      if (editingEntry) {
        await updateMutation.mutateAsync({ id: editingEntry.id, ...formData });
        toast.success('Entry updated successfully');
      } else {
        await addMutation.mutateAsync(formData);
        toast.success('Entry added successfully');
      }
      setShowForm(false);
      setEditingEntry(null);
    } catch (error) {
      toast.error('Failed to save entry: ' + (error.response?.data?.error || error.message));
    }
  };

  // Filter entries by IP pattern
  const filteredEntries = entries?.filter(entry => {
    if (!ipPatternFilter) return true;
    return entry.ipPattern.toLowerCase().includes(ipPatternFilter.toLowerCase());
  }) || [];

  // Calculate pagination
  const totalEntries = filteredEntries.length;
  const totalPages = Math.ceil(totalEntries / pageSize);
  const startIndex = (currentPage - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedEntries = filteredEntries.slice(startIndex, endIndex);

  // Reset to page 1 when filters change
  const handleTargetTypeChange = (value) => {
    setTargetTypeFilter(value || null);
    setCurrentPage(1);
  };

  const handleIpPatternChange = (value) => {
    setIpPatternFilter(value);
    setCurrentPage(1);
  };

  const headerStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '1.5rem'
  };

  const filterStyle = {
    padding: '0.5rem',
    borderRadius: '4px',
    border: '1px solid #ccc',
    fontSize: '0.9rem'
  };

  const buttonStyle = {
    padding: '0.5rem 1rem',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.9rem'
  };

  const tableStyle = {
    width: '100%',
    borderCollapse: 'collapse',
    backgroundColor: 'white',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    borderRadius: '8px',
    overflow: 'hidden'
  };

  const thStyle = {
    textAlign: 'left',
    padding: '1rem',
    backgroundColor: '#f8f9fa',
    borderBottom: '2px solid #dee2e6',
    fontWeight: '600',
    fontSize: '0.9rem'
  };

  const tdStyle = {
    padding: '0.75rem 1rem',
    borderBottom: '1px solid #dee2e6',
    fontSize: '0.9rem'
  };

  const actionButtonStyle = {
    padding: '0.4rem 0.8rem',
    marginRight: '0.5rem',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.85rem'
  };

  const paginationStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: '1rem',
    padding: '1rem',
    backgroundColor: '#f8f9fa',
    borderRadius: '4px'
  };

  const paginationButtonStyle = {
    padding: '0.5rem 1rem',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.9rem',
    marginLeft: '0.5rem'
  };

  const paginationButtonDisabledStyle = {
    ...paginationButtonStyle,
    backgroundColor: '#6c757d',
    cursor: 'not-allowed',
    opacity: 0.5
  };

  return (
    <div>
      {showForm ? (
        <>
          <IPWhitelistForm
            initialData={editingEntry}
            mode="global"
            onSubmit={handleSubmit}
            onCancel={() => {
              setShowForm(false);
              setEditingEntry(null);
            }}
          />
        </>
      ) : (
        <>
          <div style={headerStyle}>
            <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
              <div>
                <label style={{ marginRight: '0.5rem', fontSize: '0.9rem' }}>Target Type:</label>
                <select
                  value={targetTypeFilter || ''}
                  onChange={(e) => handleTargetTypeChange(e.target.value)}
                  style={filterStyle}
                >
                  <option value="">All</option>
                  <option value="AS2">AS2</option>
                  <option value="TRACKER">TRACKER</option>
                  <option value="WEBUI">WEBUI</option>
                  <option value="API">API</option>
                </select>
              </div>
              <div>
                <label style={{ marginRight: '0.5rem', fontSize: '0.9rem' }}>IP Pattern:</label>
                <input
                  type="text"
                  placeholder="Filter by IP pattern..."
                  value={ipPatternFilter}
                  onChange={(e) => handleIpPatternChange(e.target.value)}
                  style={{ ...filterStyle, minWidth: '200px' }}
                />
              </div>
              <div>
                <label style={{ marginRight: '0.5rem', fontSize: '0.9rem' }}>Page Size:</label>
                <select
                  value={pageSize}
                  onChange={(e) => {
                    setPageSize(Number(e.target.value));
                    setCurrentPage(1);
                  }}
                  style={filterStyle}
                >
                  <option value="25">25</option>
                  <option value="50">50</option>
                  <option value="100">100</option>
                  <option value="200">200</option>
                </select>
              </div>
            </div>
            <button onClick={handleAdd} style={buttonStyle}>
              + Add Entry
            </button>
          </div>

          {filteredEntries.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '2rem', color: '#666' }}>
              {ipPatternFilter || targetTypeFilter
                ? 'No entries match the current filters.'
                : 'No entries found. Add an entry to start whitelisting IPs.'}
            </div>
          ) : (
            <table style={tableStyle}>
              <thead>
                <tr>
                  <th style={thStyle}>IP Pattern</th>
                  <th style={thStyle}>Target Type</th>
                  <th style={thStyle}>Description</th>
                  <th style={thStyle}>Enabled</th>
                  <th style={thStyle}>Created</th>
                  <th style={thStyle}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {paginatedEntries.map((entry) => (
                  <tr key={entry.id}>
                    <td style={tdStyle}>
                      <code>{entry.ipPattern}</code>
                    </td>
                    <td style={tdStyle}>
                      <span style={{
                        padding: '0.25rem 0.5rem',
                        backgroundColor: '#e3f2fd',
                        borderRadius: '4px',
                        fontSize: '0.85rem'
                      }}>
                        {entry.targetType}
                      </span>
                    </td>
                    <td style={tdStyle}>{entry.description || '-'}</td>
                    <td style={tdStyle}>
                      <span style={{
                        padding: '0.25rem 0.5rem',
                        backgroundColor: entry.enabled ? '#d4edda' : '#f8d7da',
                        color: entry.enabled ? '#155724' : '#721c24',
                        borderRadius: '4px',
                        fontSize: '0.85rem'
                      }}>
                        {entry.enabled ? 'Yes' : 'No'}
                      </span>
                    </td>
                    <td style={tdStyle}>
                      {entry.createdAt ? new Date(entry.createdAt).toLocaleString() : '-'}
                      {entry.createdBy && <div style={{ fontSize: '0.8rem', color: '#666' }}>by {entry.createdBy}</div>}
                    </td>
                    <td style={tdStyle}>
                      <button
                        onClick={() => handleEdit(entry)}
                        style={{ ...actionButtonStyle, backgroundColor: '#ffc107', color: '#000' }}
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDelete(entry)}
                        style={{ ...actionButtonStyle, backgroundColor: '#dc3545', color: 'white' }}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          {filteredEntries.length > 0 && (
            <div style={paginationStyle}>
              <div style={{ fontSize: '0.9rem', color: '#666' }}>
                Showing {startIndex + 1}-{Math.min(endIndex, totalEntries)} of {totalEntries} entries
                {(ipPatternFilter || targetTypeFilter) && entries && ` (filtered from ${entries.length} total)`}
              </div>
              <div>
                <button
                  onClick={() => setCurrentPage(1)}
                  disabled={currentPage === 1}
                  style={currentPage === 1 ? paginationButtonDisabledStyle : paginationButtonStyle}
                >
                  First
                </button>
                <button
                  onClick={() => setCurrentPage(currentPage - 1)}
                  disabled={currentPage === 1}
                  style={currentPage === 1 ? paginationButtonDisabledStyle : paginationButtonStyle}
                >
                  Previous
                </button>
                <span style={{ margin: '0 1rem', fontSize: '0.9rem' }}>
                  Page {currentPage} of {totalPages}
                </span>
                <button
                  onClick={() => setCurrentPage(currentPage + 1)}
                  disabled={currentPage === totalPages}
                  style={currentPage === totalPages ? paginationButtonDisabledStyle : paginationButtonStyle}
                >
                  Next
                </button>
                <button
                  onClick={() => setCurrentPage(totalPages)}
                  disabled={currentPage === totalPages}
                  style={currentPage === totalPages ? paginationButtonDisabledStyle : paginationButtonStyle}
                >
                  Last
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}
