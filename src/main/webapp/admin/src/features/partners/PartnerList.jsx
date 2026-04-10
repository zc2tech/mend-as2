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
import { usePartners, useDeletePartner } from './usePartners';
import { useToast } from '../../components/Toast';
import { LoadingPage } from '../../components/Loading';
import PartnerFormTabs from './PartnerFormTabs';

export default function PartnerList() {
  const { data: partners, isLoading, error } = usePartners();
  const deletePartner = useDeletePartner();
  const toast = useToast();
  const [searchTerm, setSearchTerm] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingPartner, setEditingPartner] = useState(null);

  if (isLoading) {
    return <LoadingPage message="Loading partners..." />;
  }

  if (error) {
    return <div style={{ color: 'red' }}>Error loading partners: {error.message}</div>;
  }

  const filteredPartners = partners?.filter(partner =>
    partner.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    partner.as2Identification?.toLowerCase().includes(searchTerm.toLowerCase())
  ) || [];

  const handleDelete = async (as2id, name) => {
    if (window.confirm(`Are you sure you want to delete partner "${name}"?`)) {
      try {
        await deletePartner.mutateAsync(as2id);
        toast.success(`Partner "${name}" deleted successfully`);
      } catch (error) {
        toast.error('Failed to delete partner: ' + (error.response?.data?.error || error.message));
      }
    }
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
    fontWeight: '600'
  };

  const tdStyle = {
    padding: '0.75rem 1rem',
    borderBottom: '1px solid #dee2e6'
  };

  const buttonStyle = {
    padding: '0.5rem 1rem',
    marginRight: '0.5rem',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.875rem'
  };

  const handleAddPartner = () => {
    setEditingPartner(null);
    setShowForm(true);
  };

  const handleEditPartner = (partner) => {
    setEditingPartner(partner);
    setShowForm(true);
  };

  const handleCloseForm = () => {
    setShowForm(false);
    setEditingPartner(null);
  };

  return (
    <div>
      <div style={{ marginBottom: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 style={{ margin: 0 }}>Partners</h1>
        <button
          style={{
            ...buttonStyle,
            backgroundColor: '#28a745',
            color: 'white',
            marginRight: 0
          }}
          onClick={handleAddPartner}
        >
          Add Partner
        </button>
      </div>

      <div style={{ marginBottom: '1rem' }}>
        <input
          type="text"
          placeholder="Search partners..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          style={{
            width: '100%',
            maxWidth: '400px',
            padding: '0.5rem',
            border: '1px solid #ddd',
            borderRadius: '4px'
          }}
        />
      </div>

      <table style={tableStyle}>
        <thead>
          <tr>
            <th style={thStyle}>Name</th>
            <th style={thStyle}>AS2 ID</th>
            <th style={thStyle}>URL</th>
            <th style={thStyle}>Direction</th>
            <th style={thStyle}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {filteredPartners.length === 0 ? (
            <tr>
              <td colSpan="5" style={{ ...tdStyle, textAlign: 'center', padding: '2rem' }}>
                {searchTerm ? 'No partners match your search' : 'No partners configured'}
              </td>
            </tr>
          ) : (
            filteredPartners.map(partner => (
              <tr key={partner.id || partner.as2Identification}>
                <td style={tdStyle}>{partner.name || '-'}</td>
                <td style={tdStyle}>{partner.as2Identification || '-'}</td>
                <td style={tdStyle}>
                  {partner.url ? (
                    <a href={partner.url} target="_blank" rel="noopener noreferrer">
                      {partner.url}
                    </a>
                  ) : '-'}
                </td>
                <td style={tdStyle}>
                  {partner.localStation ? (
                    <span title="Local Station" style={{ fontSize: '1.5rem' }}>🏠</span>
                  ) : (
                    <span title="Remote Partner" style={{ fontSize: '1.5rem' }}>🌐</span>
                  )}
                </td>
                <td style={tdStyle}>
                  <button
                    style={{
                      ...buttonStyle,
                      backgroundColor: '#007bff',
                      color: 'white'
                    }}
                    onClick={() => handleEditPartner(partner)}
                  >
                    Edit
                  </button>
                  <button
                    style={{
                      ...buttonStyle,
                      backgroundColor: '#dc3545',
                      color: 'white'
                    }}
                    onClick={() => handleDelete(partner.as2Identification, partner.name)}
                    disabled={deletePartner.isPending}
                  >
                    {deletePartner.isPending ? 'Deleting...' : 'Delete'}
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {showForm && (
        <PartnerFormTabs
          partner={editingPartner}
          onClose={handleCloseForm}
          onSuccess={handleCloseForm}
        />
      )}
    </div>
  );
}
