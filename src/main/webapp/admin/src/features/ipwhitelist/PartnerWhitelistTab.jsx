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
import { usePartners } from '../partners/usePartners';
import { usePartnerWhitelist, useAddPartnerWhitelist, useDeletePartnerWhitelist } from './useIPWhitelist';
import { useToast } from '../../components/Toast';
import { LoadingPage } from '../../components/Loading';
import IPWhitelistForm from './IPWhitelistForm';

export default function PartnerWhitelistTab() {
  const { data: partners, isLoading: partnersLoading } = usePartners(false); // Get all partners
  const [selectedPartnerId, setSelectedPartnerId] = useState(null);
  const { data: entries, isLoading: entriesLoading } = usePartnerWhitelist(selectedPartnerId);
  const addMutation = useAddPartnerWhitelist();
  const deleteMutation = useDeletePartnerWhitelist();
  const toast = useToast();
  const [showForm, setShowForm] = useState(false);

  if (partnersLoading) {
    return <LoadingPage message="Loading partners..." />;
  }

  const handleAdd = () => {
    if (!selectedPartnerId) {
      toast.error('Please select a partner first');
      return;
    }
    setShowForm(true);
  };

  const handleDelete = async (entry) => {
    if (window.confirm(`Delete IP pattern "${entry.ipPattern}"?`)) {
      try {
        await deleteMutation.mutateAsync({ partnerId: selectedPartnerId, entryId: entry.id });
        toast.success('Entry deleted successfully');
      } catch (error) {
        toast.error('Failed to delete entry: ' + (error.response?.data?.error || error.message));
      }
    }
  };

  const handleSubmit = async (formData) => {
    try {
      await addMutation.mutateAsync({ partnerId: selectedPartnerId, ...formData });
      toast.success('Entry added successfully');
      setShowForm(false);
    } catch (error) {
      toast.error('Failed to save entry: ' + (error.response?.data?.error || error.message));
    }
  };

  const headerStyle = {
    marginBottom: '1.5rem'
  };

  const partnerSelectStyle = {
    marginBottom: '1.5rem',
    display: 'flex',
    gap: '1rem',
    alignItems: 'center'
  };

  const selectStyle = {
    padding: '0.5rem',
    borderRadius: '4px',
    border: '1px solid #ccc',
    fontSize: '0.9rem',
    minWidth: '300px'
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
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.85rem',
    backgroundColor: '#dc3545',
    color: 'white'
  };

  return (
    <div>
      <div style={headerStyle}>
        <h2 style={{ fontSize: '1.25rem', marginBottom: '0.5rem' }}>Partner-Specific Whitelist</h2>
        <p style={{ color: '#666', fontSize: '0.9rem' }}>
          Whitelist IP addresses for specific AS2 partners. These rules apply only to AS2 message endpoints.
        </p>
      </div>

      {showForm ? (
        <IPWhitelistForm
          mode="partner"
          onSubmit={handleSubmit}
          onCancel={() => setShowForm(false)}
        />
      ) : (
        <>
          <div style={partnerSelectStyle}>
            <label style={{ fontSize: '0.9rem', fontWeight: '600' }}>Select Partner:</label>
            <select
              value={selectedPartnerId || ''}
              onChange={(e) => setSelectedPartnerId(e.target.value ? parseInt(e.target.value) : null)}
              style={selectStyle}
            >
              <option value="">-- Select a partner --</option>
              {partners?.filter(p => !p.isLocalStation).map((partner) => {
                const username = partner.createdByUsername || `User ${partner.createdByUserId || 'Unknown'}`;
                const displayName = `${username}:${partner.name}`;
                return (
                  <option key={partner.dbId} value={partner.dbId}>
                    {displayName}
                  </option>
                );
              })}
            </select>
            <button onClick={handleAdd} style={buttonStyle} disabled={!selectedPartnerId}>
              + Add Entry
            </button>
          </div>

          {selectedPartnerId && (
            entriesLoading ? (
              <LoadingPage message="Loading entries..." />
            ) : entries && entries.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '2rem', color: '#666' }}>
                No entries found for this partner. Add an entry to start whitelisting IPs.
              </div>
            ) : (
              <table style={tableStyle}>
                <thead>
                  <tr>
                    <th style={thStyle}>IP Pattern</th>
                    <th style={thStyle}>Description</th>
                    <th style={thStyle}>Enabled</th>
                    <th style={thStyle}>Created</th>
                    <th style={thStyle}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {entries?.map((entry) => (
                    <tr key={entry.id}>
                      <td style={tdStyle}>
                        <code>{entry.ipPattern}</code>
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
                      </td>
                      <td style={tdStyle}>
                        <button
                          onClick={() => handleDelete(entry)}
                          style={actionButtonStyle}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )
          )}
        </>
      )}
    </div>
  );
}
