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

import { useState, useEffect, useRef, useMemo } from 'react';
import { useMessages } from './useMessages';
import { LoadingPage } from '../../components/Loading';
import { format } from 'date-fns';
import ManualSend from './ManualSend';
import MessageDetails from './MessageDetails';
import { useQueryClient } from '@tanstack/react-query';
import { useToast } from '../../components/Toast';
import { useAuth } from '../auth/useAuth';
import { useLocation } from 'react-router-dom';
import api from '../../api/client';

export default function MessageList() {
  const { hasPermission, user } = useAuth();
  const canWrite = hasPermission('MESSAGE_WRITE');

  // Check if current user has ADMIN role - recalculate when user changes
  const isAdmin = useMemo(() => {
    return user?.roleIds?.includes(1) || user?.roles?.some(r => r.name === 'ADMIN');
  }, [user?.roleIds, user?.roles]);

  const defaultFilters = {
    limit: 100,
    direction: 0, // 0=all, 1=in, 2=out
    showFinished: true,
    showPending: true,
    showStopped: true,
    partnerId: '',
    localStationId: '',
    fromDate: '',
    toDate: '',
    messageId: '',
    format: '',  // cXML, X12, EDIFACT
    userId: ''   // Filter by user
  };

  const [filters, setFilters] = useState(defaultFilters);
  const [queryFilters, setQueryFilters] = useState(defaultFilters);
  const { data, isLoading, error } = useMessages(queryFilters);
  const [selectedMessage, setSelectedMessage] = useState(null);
  const [showManualSend, setShowManualSend] = useState(false);
  const [partners, setPartners] = useState([]);
  const [localStations, setLocalStations] = useState([]);
  const [users, setUsers] = useState([]);
  const queryClient = useQueryClient();
  const toast = useToast();
  const searchTimeoutRef = useRef(null);
  const location = useLocation();

  // Handle keyboard shortcut navigation (Cmd+M / Ctrl+M)
  useEffect(() => {
    if (location.state?.openManualSend) {
      setShowManualSend(true);
      // Clear the state to prevent reopening on subsequent renders
      window.history.replaceState({}, document.title);
    }
  }, [location.state]);

  // Apply search immediately for non-text filters
  const applyFiltersImmediately = (newFilters) => {
    setFilters(newFilters);
    setQueryFilters(newFilters);
  };

  // Debounced search for text input (messageId)
  const applyFiltersDebounced = (newFilters) => {
    setFilters(newFilters);

    // Clear existing timeout
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }

    // Set new timeout - apply search after 1 second of no input
    searchTimeoutRef.current = setTimeout(() => {
      setQueryFilters(newFilters);
    }, 1000);
  };

  // Cleanup timeout on unmount
  useEffect(() => {
    return () => {
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
    };
  }, []);

  const handleSearch = () => {
    setQueryFilters({ ...filters });
    // Invalidate the query cache to force a fresh fetch from the database
    queryClient.invalidateQueries(['messages']);
  };

  const handleResetFilters = () => {
    setFilters(defaultFilters);
    setQueryFilters(defaultFilters);
    // Clear any pending debounced search
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }
    // Invalidate the query cache to force a fresh fetch from the database
    queryClient.invalidateQueries(['messages']);
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      // Clear timeout and apply immediately on Enter
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
      handleSearch();
    }
  };

  // Fetch partners for filter dropdowns
  useEffect(() => {
    const fetchPartners = async () => {
      try {
        // For ADMIN users, get all partners; for regular users, only get their own partners
        const params = isAdmin ? {} : { visibleToUser: user?.id };
        const response = await api.get('/partners', { params });
        const allPartners = response.data || [];

        // Sort partners by display name (username:name or just name)
        const sortedLocalStations = allPartners
          .filter(p => p.localStation === true)
          .sort((a, b) => {
            const aDisplay = isAdmin && a.createdByUsername ? `${a.createdByUsername}:${a.name}` : a.name;
            const bDisplay = isAdmin && b.createdByUsername ? `${b.createdByUsername}:${b.name}` : b.name;
            return aDisplay.localeCompare(bDisplay);
          });

        const sortedPartners = allPartners
          .filter(p => p.localStation !== true)
          .sort((a, b) => {
            const aDisplay = isAdmin && a.createdByUsername ? `${a.createdByUsername}:${a.name}` : a.name;
            const bDisplay = isAdmin && b.createdByUsername ? `${b.createdByUsername}:${b.name}` : b.name;
            return aDisplay.localeCompare(bDisplay);
          });

        setLocalStations(sortedLocalStations);
        setPartners(sortedPartners);
      } catch (error) {
        console.error('Failed to fetch partners:', error);
      }
    };

    const fetchUsers = async () => {
      try {
        const response = await api.get('/users');
        setUsers(response.data || []);
      } catch (error) {
        console.error('Failed to fetch users:', error);
      }
    };

    fetchPartners();
    if (isAdmin) {
      fetchUsers();
    }
  }, [isAdmin, user?.id]);

  const handleDeleteMessage = async (messageId) => {
    if (!window.confirm(`Are you sure you want to delete message ${messageId}?`)) {
      return;
    }

    try {
      await api.delete(`/messages/${messageId}`);
      toast.success('Message deleted successfully');
      queryClient.invalidateQueries(['messages']);
    } catch (error) {
      const errorMsg = error.response?.data?.error || error.message || 'Unknown error';
      toast.error('Failed to delete message: ' + errorMsg);
    }
  };

  const getTimezoneOffset = () => {
    const offset = -new Date().getTimezoneOffset();
    const hours = Math.floor(Math.abs(offset) / 60);
    const minutes = Math.abs(offset) % 60;
    const sign = offset >= 0 ? '+' : '-';
    return `UTC${sign}${hours}${minutes > 0 ? ':' + minutes.toString().padStart(2, '0') : ''}`;
  };

  if (isLoading) {
    return <LoadingPage message="Loading messages..." />;
  }

  if (error) {
    return <div style={{ color: 'red' }}>Error loading messages: {error.message}</div>;
  }

  const messages = data?.messages || [];
  const totalCount = data?.totalCount || 0;

  // Apply client-side message ID filtering (partial match)
  const filteredMessages = filters.messageId
    ? messages.filter(msg =>
        msg.messageId && msg.messageId.toLowerCase().includes(filters.messageId.toLowerCase())
      )
    : messages;

  const getStatusColor = (state) => {
    if (state === 1) return '#28a745'; // FINISHED
    if (state === 2) return '#ffc107'; // PENDING
    if (state === 3) return '#dc3545'; // STOPPED
    return '#6c757d'; // Unknown
  };

  const getStatusText = (state) => {
    if (state === 1) return 'FINISHED';
    if (state === 2) return 'PENDING';
    if (state === 3) return 'STOPPED';
    return 'UNKNOWN';
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
    padding: '0.75rem 1rem',
    backgroundColor: '#f8f9fa',
    borderBottom: '2px solid #dee2e6',
    fontWeight: '600',
    fontSize: '0.875rem'
  };

  const tdStyle = {
    padding: '0.5rem 1rem',
    borderBottom: '1px solid #dee2e6',
    fontSize: '0.875rem'
  };

  return (
    <div>
      <div style={{ marginBottom: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 style={{ margin: 0 }}>AS2 Messages</h1>
          <p style={{ color: '#666', margin: '0.5rem 0 0 0' }}>
            Showing {filteredMessages.length} of {totalCount} messages
          </p>
        </div>
        {canWrite && (
          <button
            style={{
              padding: '0.5rem 1rem',
              backgroundColor: '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
            onClick={() => setShowManualSend(true)}
          >
            Send Message
          </button>
        )}
      </div>

      <div style={{ marginBottom: '1rem', padding: '1rem', backgroundColor: 'white', borderRadius: '8px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h3 style={{ margin: 0, fontSize: '1rem', fontWeight: '600' }}>Filters</h3>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <button
              style={{
                padding: '0.375rem 0.75rem',
                backgroundColor: '#007bff',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '0.875rem'
              }}
              onClick={handleSearch}
            >
              Search
            </button>
            <button
              style={{
                padding: '0.375rem 0.75rem',
                backgroundColor: '#6c757d',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '0.875rem'
              }}
              onClick={handleResetFilters}
            >
              Reset Filters
            </button>
          </div>
        </div>

        {/* Row 1: Basic filters */}
        <div style={{ display: 'grid', gridTemplateColumns: isAdmin ? '0.5fr 0.75fr 0.5fr 1fr 1fr 1fr' : '0.5fr 0.75fr 0.5fr 1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
              Limit
            </label>
            <input
              type="number"
              value={filters.limit || 100}
              onChange={(e) => applyFiltersImmediately({ ...filters, limit: parseInt(e.target.value) })}
              onKeyPress={handleKeyPress}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            />
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
              Direction
            </label>
            <select
              value={filters.direction}
              onChange={(e) => applyFiltersImmediately({ ...filters, direction: parseInt(e.target.value) })}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            >
              <option value={0}>All</option>
              <option value={1}>Inbound</option>
              <option value={2}>Outbound</option>
            </select>
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
              Format
            </label>
            <select
              value={filters.format}
              onChange={(e) => applyFiltersImmediately({ ...filters, format: e.target.value })}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            >
              <option value="">All</option>
              <option value="cXML">cXML</option>
              <option value="X12">X12</option>
              <option value="EDIFACT">EDIFACT</option>
            </select>
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
              Remote Partner
            </label>
            <select
              value={filters.partnerId}
              onChange={(e) => applyFiltersImmediately({ ...filters, partnerId: e.target.value })}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            >
              <option value="">All Partners</option>
              {partners.map(p => (
                <option key={p.dbid} value={p.dbid}>
                  {isAdmin && p.createdByUsername ? `${p.createdByUsername}:${p.name}` : p.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
              Local Station
            </label>
            <select
              value={filters.localStationId}
              onChange={(e) => applyFiltersImmediately({ ...filters, localStationId: e.target.value })}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            >
              <option value="">All Stations</option>
              {localStations.map(p => (
                <option key={p.dbid} value={p.dbid}>
                  {isAdmin && p.createdByUsername ? `${p.createdByUsername}:${p.name}` : p.name}
                </option>
              ))}
            </select>
          </div>

          {isAdmin && (
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
                User
              </label>
              <select
                value={filters.userId}
                onChange={(e) => applyFiltersImmediately({ ...filters, userId: e.target.value })}
                style={{
                  width: '100%',
                  padding: '0.5rem',
                  border: '1px solid #ddd',
                  borderRadius: '4px'
                }}
              >
                <option value="">All Users</option>
                {users.map(u => (
                  <option key={u.id} value={u.id}>{u.username}</option>
                ))}
              </select>
            </div>
          )}
        </div>

        {/* Row 2: Date range, Message ID, and Status checkboxes */}
        <div style={{ display: 'grid', gridTemplateColumns: '0.75fr 0.75fr 1fr 1.5fr', gap: '1rem', alignItems: 'end' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
              From
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="date"
                value={filters.fromDate}
                onChange={(e) => applyFiltersImmediately({ ...filters, fromDate: e.target.value })}
                onKeyPress={handleKeyPress}
                style={{
                  width: '100%',
                  padding: '0.5rem',
                  paddingRight: '2rem',
                  border: '1px solid #ddd',
                  borderRadius: '4px'
                }}
              />
              {filters.fromDate && (
                <button
                  onClick={() => applyFiltersImmediately({ ...filters, fromDate: '' })}
                  style={{
                    position: 'absolute',
                    right: '0.5rem',
                    top: '50%',
                    transform: 'translateY(-50%)',
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    padding: '0.25rem',
                    fontSize: '1rem',
                    color: '#6c757d',
                    lineHeight: '1'
                  }}
                  title="Clear from date"
                >
                  ✕
                </button>
              )}
            </div>
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
              To
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="date"
                value={filters.toDate}
                onChange={(e) => applyFiltersImmediately({ ...filters, toDate: e.target.value })}
                onKeyPress={handleKeyPress}
                style={{
                  width: '100%',
                  padding: '0.5rem',
                  paddingRight: '2rem',
                  border: '1px solid #ddd',
                  borderRadius: '4px'
                }}
              />
              {filters.toDate && (
                <button
                  onClick={() => applyFiltersImmediately({ ...filters, toDate: '' })}
                  style={{
                    position: 'absolute',
                    right: '0.5rem',
                    top: '50%',
                    transform: 'translateY(-50%)',
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    padding: '0.25rem',
                    fontSize: '1rem',
                    color: '#6c757d',
                    lineHeight: '1'
                  }}
                  title="Clear to date"
                >
                  ✕
                </button>
              )}
            </div>
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
              Message ID
            </label>
            <input
              type="text"
              placeholder="Search by message ID"
              value={filters.messageId}
              onChange={(e) => applyFiltersDebounced({ ...filters, messageId: e.target.value })}
              onKeyPress={handleKeyPress}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            />
          </div>
          <div style={{ display: 'flex', gap: '1.5rem', paddingBottom: '0.5rem' }}>
            <label style={{ display: 'flex', alignItems: 'center', fontSize: '0.875rem', cursor: 'pointer', whiteSpace: 'nowrap' }}>
              <input
                type="checkbox"
                checked={filters.showFinished}
                onChange={(e) => applyFiltersImmediately({ ...filters, showFinished: e.target.checked })}
                style={{ marginRight: '0.5rem' }}
              />
              Finished
            </label>
            <label style={{ display: 'flex', alignItems: 'center', fontSize: '0.875rem', cursor: 'pointer', whiteSpace: 'nowrap' }}>
              <input
                type="checkbox"
                checked={filters.showPending}
                onChange={(e) => applyFiltersImmediately({ ...filters, showPending: e.target.checked })}
                style={{ marginRight: '0.5rem' }}
              />
              Pending
            </label>
            <label style={{ display: 'flex', alignItems: 'center', fontSize: '0.875rem', cursor: 'pointer', whiteSpace: 'nowrap' }}>
              <input
                type="checkbox"
                checked={filters.showStopped}
                onChange={(e) => applyFiltersImmediately({ ...filters, showStopped: e.target.checked })}
                style={{ marginRight: '0.5rem' }}
              />
              Stopped
            </label>
          </div>
        </div>
      </div>

      <table style={tableStyle}>
        <thead>
          <tr>
            <th style={thStyle}>Message ID</th>
            <th style={thStyle}>Direction</th>
            <th style={thStyle}>Sender</th>
            <th style={thStyle}>Receiver</th>
            <th style={thStyle}>Init Date ({getTimezoneOffset()})</th>
            <th style={thStyle}>User</th>
            <th style={thStyle}>Format</th>
            <th style={thStyle}>Doc Type</th>
            <th style={thStyle}>Status</th>
            <th style={thStyle}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {filteredMessages.length === 0 ? (
            <tr>
              <td colSpan="10" style={{ ...tdStyle, textAlign: 'center', padding: '2rem' }}>
                No messages found
              </td>
            </tr>
          ) : (
            filteredMessages.map(message => (
              <tr key={message.messageId}>
                <td style={tdStyle}>
                  <code style={{ fontSize: '0.75rem' }}>{message.messageId}</code>
                </td>
                <td style={{ ...tdStyle, textAlign: 'center' }}>
                  {message.direction === 1 ? (
                    <span style={{ color: '#28a745', fontSize: '1.2rem', fontWeight: 'bold' }} title="Inbound">←</span>
                  ) : (
                    <span style={{ color: '#dc3545', fontSize: '1.2rem', fontWeight: 'bold' }} title="Outbound">→</span>
                  )}
                </td>
                <td style={tdStyle}>{message.senderId || '-'}</td>
                <td style={tdStyle}>{message.receiverId || '-'}</td>
                <td style={tdStyle}>
                  {message.initDate ? format(new Date(message.initDate), 'yyyy-MM-dd HH:mm:ss') : '-'}
                </td>
                <td style={tdStyle}>
                  {message.ownerUserId === 0 ? (
                    <span style={{
                      display: 'inline-block',
                      padding: '0.25rem 0.5rem',
                      borderRadius: '4px',
                      fontSize: '0.75rem',
                      backgroundColor: '#6c757d',
                      color: 'white',
                      fontWeight: '500'
                    }}>
                      System
                    </span>
                  ) : message.ownerUsername ? (
                    <span style={{
                      display: 'inline-block',
                      padding: '0.25rem 0.5rem',
                      borderRadius: '4px',
                      fontSize: '0.75rem',
                      backgroundColor: '#007bff',
                      color: 'white',
                      fontWeight: '500'
                    }}>
                      {message.ownerUsername}
                    </span>
                  ) : message.ownerUserId > 0 ? (
                    <span style={{
                      display: 'inline-block',
                      padding: '0.25rem 0.5rem',
                      borderRadius: '4px',
                      fontSize: '0.75rem',
                      backgroundColor: '#28a745',
                      color: 'white',
                      fontWeight: '500'
                    }}>
                      User {message.ownerUserId}
                    </span>
                  ) : (
                    <span style={{ color: '#6c757d', fontSize: '0.75rem' }}>-</span>
                  )}
                </td>
                <td style={tdStyle}>{message.payloadFormat || '-'}</td>
                <td style={tdStyle}>{message.payloadDocType || '-'}</td>
                <td style={tdStyle}>
                  <span style={{
                    padding: '0.25rem 0.5rem',
                    borderRadius: '4px',
                    backgroundColor: getStatusColor(message.state) + '20',
                    color: getStatusColor(message.state),
                    fontWeight: '600',
                    fontSize: '0.75rem'
                  }}>
                    {getStatusText(message.state)}
                  </span>
                </td>
                <td style={tdStyle}>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button
                      style={{
                        padding: '0.375rem 0.75rem',
                        backgroundColor: '#007bff',
                        color: 'white',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: 'pointer',
                        fontSize: '0.875rem'
                      }}
                      onClick={() => setSelectedMessage(message)}
                    >
                      Details
                    </button>
                    {canWrite && (
                      <button
                        style={{
                          padding: '0.375rem 0.75rem',
                          backgroundColor: '#dc3545',
                          color: 'white',
                          border: 'none',
                          borderRadius: '4px',
                          cursor: 'pointer',
                          fontSize: '0.875rem'
                        }}
                        onClick={() => handleDeleteMessage(message.messageId)}
                      >
                        Delete
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {selectedMessage && (
        <MessageDetails
          message={selectedMessage}
          onClose={() => setSelectedMessage(null)}
        />
      )}

      {showManualSend && (
        <ManualSend onClose={() => setShowManualSend(false)} />
      )}
    </div>
  );
}
