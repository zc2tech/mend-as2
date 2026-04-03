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
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { LoadingPage } from '../../components/Loading';
import api from '../../api/client';

export default function UserManagement() {
  const [activeTab, setActiveTab] = useState('users');
  const [showUserForm, setShowUserForm] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [showPasswordForm, setShowPasswordForm] = useState(false);
  const [changingPasswordUserId, setChangingPasswordUserId] = useState(null);
  const queryClient = useQueryClient();

  // Fetch users
  const { data: users = [], isLoading: usersLoading, error: usersError } = useQuery({
    queryKey: ['users'],
    queryFn: async () => {
      const response = await api.get('/users');
      const usersData = response.data;

      // Fetch roles for each user
      const usersWithRoles = await Promise.all(
        usersData.map(async (user) => {
          try {
            const rolesResponse = await api.get(`/users/${user.id}/roles`);
            return { ...user, roles: rolesResponse.data || [] };
          } catch (error) {
            console.error(`Failed to fetch roles for user ${user.id}:`, error);
            return { ...user, roles: [] };
          }
        })
      );

      return usersWithRoles;
    },
    enabled: activeTab === 'users'
  });

  // Fetch roles
  const { data: roles = [], isLoading: rolesLoading } = useQuery({
    queryKey: ['roles'],
    queryFn: async () => {
      const response = await api.get('/users/roles');
      return response.data;
    },
    enabled: activeTab === 'roles'
  });

  // Fetch permissions
  const { data: permissions = [], isLoading: permissionsLoading } = useQuery({
    queryKey: ['permissions'],
    queryFn: async () => {
      const response = await api.get('/users/permissions');
      return response.data;
    },
    enabled: activeTab === 'permissions'
  });

  // Delete user mutation
  const deleteUserMutation = useMutation({
    mutationFn: async (userId) => {
      await api.delete(`/users/${userId}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['users']);
      alert('User deleted successfully');
    },
    onError: (error) => {
      alert('Failed to delete user: ' + (error.response?.data?.error || error.message));
    }
  });

  const handleDeleteUser = (user) => {
    if (window.confirm(`Are you sure you want to delete user "${user.username}"?`)) {
      deleteUserMutation.mutate(user.id);
    }
  };

  const handleEditUser = async (user) => {
    try {
      // Fetch user's current roles
      const response = await api.get(`/users/${user.id}/roles`);
      const userRoles = response.data;
      const roleIds = userRoles.map(r => r.id);

      setEditingUser({
        ...user,
        roleIds: roleIds
      });
      setShowUserForm(true);
    } catch (error) {
      console.error('Error fetching user roles:', error);
      setEditingUser(user);
      setShowUserForm(true);
    }
  };

  const handleCreateUser = () => {
    setEditingUser(null);
    setShowUserForm(true);
  };

  const handleChangePassword = (user) => {
    setChangingPasswordUserId(user.id);
    setShowPasswordForm(true);
  };

  const tabStyle = (isActive) => ({
    padding: '0.75rem 1.5rem',
    cursor: 'pointer',
    borderBottom: isActive ? '3px solid #007bff' : '3px solid transparent',
    color: isActive ? '#007bff' : '#495057',
    fontWeight: isActive ? '600' : '400',
    backgroundColor: 'transparent',
    border: 'none',
    fontSize: '1rem'
  });

  const buttonStyle = {
    padding: '0.5rem 1rem',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.875rem',
    fontWeight: '500'
  };

  const dangerButtonStyle = {
    ...buttonStyle,
    backgroundColor: '#dc3545',
    marginLeft: '0.5rem'
  };

  const secondaryButtonStyle = {
    ...buttonStyle,
    backgroundColor: '#6c757d',
    marginLeft: '0.5rem'
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <h2 style={{ margin: 0 }}>User Management</h2>
        {activeTab === 'users' && (
          <button onClick={handleCreateUser} style={buttonStyle}>
            + Create User
          </button>
        )}
      </div>

      {/* Tabs */}
      <div style={{ borderBottom: '1px solid #dee2e6', marginBottom: '1.5rem', display: 'flex' }}>
        <button onClick={() => setActiveTab('users')} style={tabStyle(activeTab === 'users')}>
          Users
        </button>
        <button onClick={() => setActiveTab('roles')} style={tabStyle(activeTab === 'roles')}>
          Roles
        </button>
        <button onClick={() => setActiveTab('permissions')} style={tabStyle(activeTab === 'permissions')}>
          Permissions
        </button>
      </div>

      {/* Users Tab */}
      {activeTab === 'users' && (
        <div>
          {usersLoading && <LoadingPage message="Loading users..." />}
          {usersError && <div style={{ color: 'red' }}>Error loading users: {usersError.message}</div>}
          {!usersLoading && !usersError && (
            <UserList
              users={users}
              onEdit={handleEditUser}
              onDelete={handleDeleteUser}
              onChangePassword={handleChangePassword}
            />
          )}
        </div>
      )}

      {/* Roles Tab */}
      {activeTab === 'roles' && (
        <div>
          {rolesLoading && <LoadingPage message="Loading roles..." />}
          {!rolesLoading && <RoleList roles={roles} />}
        </div>
      )}

      {/* Permissions Tab */}
      {activeTab === 'permissions' && (
        <div>
          {permissionsLoading && <LoadingPage message="Loading permissions..." />}
          {!permissionsLoading && <PermissionList permissions={permissions} />}
        </div>
      )}

      {/* User Form Modal */}
      {showUserForm && (
        <UserFormModal
          user={editingUser}
          onClose={() => setShowUserForm(false)}
          onSuccess={() => {
            setShowUserForm(false);
            queryClient.invalidateQueries(['users']);
          }}
        />
      )}

      {/* Password Change Modal */}
      {showPasswordForm && (
        <PasswordChangeModal
          userId={changingPasswordUserId}
          onClose={() => setShowPasswordForm(false)}
          onSuccess={() => {
            setShowPasswordForm(false);
            alert('Password changed successfully');
          }}
        />
      )}
    </div>
  );
}

// UserList Component
function UserList({ users, onEdit, onDelete, onChangePassword }) {
  const tableStyle = {
    width: '100%',
    borderCollapse: 'collapse',
    backgroundColor: 'white',
    borderRadius: '8px',
    overflow: 'hidden',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
  };

  const thStyle = {
    textAlign: 'left',
    padding: '0.75rem',
    backgroundColor: '#f8f9fa',
    borderBottom: '2px solid #dee2e6',
    fontWeight: '600',
    fontSize: '0.875rem'
  };

  const tdStyle = {
    padding: '0.75rem',
    borderBottom: '1px solid #dee2e6',
    fontSize: '0.875rem'
  };

  const actionButtonStyle = {
    padding: '0.25rem 0.75rem',
    fontSize: '0.75rem',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    marginRight: '0.5rem'
  };

  return (
    <table style={tableStyle}>
      <thead>
        <tr>
          <th style={thStyle}>Username</th>
          <th style={thStyle}>Full Name</th>
          <th style={thStyle}>Email</th>
          <th style={thStyle}>Roles</th>
          <th style={thStyle}>Enabled</th>
          <th style={thStyle}>Last Login</th>
          <th style={thStyle}>Actions</th>
        </tr>
      </thead>
      <tbody>
        {users.map((user, index) => (
          <tr key={user.id} style={{ backgroundColor: index % 2 === 0 ? 'white' : '#f8f9fa' }}>
            <td style={tdStyle}><strong>{user.username}</strong></td>
            <td style={tdStyle}>{user.fullName || '-'}</td>
            <td style={tdStyle}>{user.email || '-'}</td>
            <td style={tdStyle}>
              {user.roles && user.roles.length > 0
                ? user.roles.map(r => r.name).join(', ')
                : '-'
              }
            </td>
            <td style={tdStyle}>
              <span style={{
                padding: '0.25rem 0.5rem',
                borderRadius: '4px',
                fontSize: '0.75rem',
                backgroundColor: user.enabled ? '#d4edda' : '#f8d7da',
                color: user.enabled ? '#155724' : '#721c24'
              }}>
                {user.enabled ? 'Active' : 'Disabled'}
              </span>
            </td>
            <td style={tdStyle}>{user.lastLogin ? new Date(user.lastLogin).toLocaleString() : 'Never'}</td>
            <td style={tdStyle}>
              <button
                onClick={() => onEdit(user)}
                style={{...actionButtonStyle, backgroundColor: '#007bff', color: 'white'}}
              >
                Edit
              </button>
              <button
                onClick={() => onChangePassword(user)}
                style={{...actionButtonStyle, backgroundColor: '#6c757d', color: 'white'}}
              >
                Change Password
              </button>
              <button
                onClick={() => onDelete(user)}
                style={{...actionButtonStyle, backgroundColor: '#dc3545', color: 'white'}}
              >
                Delete
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

// RoleList Component
function RoleList({ roles }) {
  const [expandedRole, setExpandedRole] = useState(null);
  const queryClient = useQueryClient();

  const { data: rolePermissions } = useQuery({
    queryKey: ['rolePermissions', expandedRole],
    queryFn: async () => {
      const response = await api.get(`/users/roles/${expandedRole}/permissions`);
      return response.data;
    },
    enabled: !!expandedRole
  });

  const tableStyle = {
    width: '100%',
    borderCollapse: 'collapse',
    backgroundColor: 'white',
    borderRadius: '8px',
    overflow: 'hidden',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
  };

  const thStyle = {
    textAlign: 'left',
    padding: '0.75rem',
    backgroundColor: '#f8f9fa',
    borderBottom: '2px solid #dee2e6',
    fontWeight: '600',
    fontSize: '0.875rem'
  };

  const tdStyle = {
    padding: '0.75rem',
    borderBottom: '1px solid #dee2e6',
    fontSize: '0.875rem'
  };

  return (
    <table style={tableStyle}>
      <thead>
        <tr>
          <th style={thStyle}>Role Name</th>
          <th style={thStyle}>Description</th>
          <th style={thStyle}>Actions</th>
        </tr>
      </thead>
      <tbody>
        {roles.map((role, index) => (
          <>
            <tr key={role.id} style={{ backgroundColor: index % 2 === 0 ? 'white' : '#f8f9fa' }}>
              <td style={tdStyle}><strong>{role.name}</strong></td>
              <td style={tdStyle}>{role.description}</td>
              <td style={tdStyle}>
                <button
                  onClick={() => setExpandedRole(expandedRole === role.id ? null : role.id)}
                  style={{
                    padding: '0.25rem 0.75rem',
                    fontSize: '0.75rem',
                    backgroundColor: '#007bff',
                    color: 'white',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer'
                  }}
                >
                  {expandedRole === role.id ? 'Hide' : 'Show'} Permissions
                </button>
              </td>
            </tr>
            {expandedRole === role.id && rolePermissions && (
              <tr>
                <td colSpan="3" style={{ padding: '1rem', backgroundColor: '#f8f9fa' }}>
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '0.5rem' }}>
                    {rolePermissions.map(perm => (
                      <div key={perm.id} style={{
                        padding: '0.5rem',
                        backgroundColor: 'white',
                        borderRadius: '4px',
                        border: '1px solid #dee2e6'
                      }}>
                        <div style={{ fontWeight: '600', fontSize: '0.875rem' }}>{perm.name}</div>
                        <div style={{ fontSize: '0.75rem', color: '#6c757d' }}>{perm.description}</div>
                      </div>
                    ))}
                  </div>
                </td>
              </tr>
            )}
          </>
        ))}
      </tbody>
    </table>
  );
}

// PermissionList Component
function PermissionList({ permissions }) {
  // Group permissions by category
  const groupedPermissions = permissions.reduce((acc, perm) => {
    const category = perm.category || 'Other';
    if (!acc[category]) acc[category] = [];
    acc[category].push(perm);
    return acc;
  }, {});

  const cardStyle = {
    backgroundColor: 'white',
    padding: '1rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '1rem'
  };

  return (
    <div>
      {Object.keys(groupedPermissions).sort().map(category => (
        <div key={category} style={cardStyle}>
          <h3 style={{ marginTop: 0, marginBottom: '1rem', color: '#495057' }}>{category}</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '0.75rem' }}>
            {groupedPermissions[category].map(perm => (
              <div key={perm.id} style={{
                padding: '0.75rem',
                backgroundColor: '#f8f9fa',
                borderRadius: '4px',
                border: '1px solid #dee2e6'
              }}>
                <div style={{ fontWeight: '600', fontSize: '0.875rem', marginBottom: '0.25rem' }}>
                  {perm.name}
                </div>
                <div style={{ fontSize: '0.75rem', color: '#6c757d' }}>
                  {perm.description}
                </div>
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}

// UserFormModal Component
function UserFormModal({ user, onClose, onSuccess }) {
  const [formData, setFormData] = useState({
    username: user?.username || '',
    password: '',
    email: user?.email || '',
    fullName: user?.fullName || '',
    enabled: user?.enabled !== undefined ? user.enabled : true,
    roleIds: user?.roleIds || []
  });

  const { data: roles = [] } = useQuery({
    queryKey: ['roles'],
    queryFn: async () => {
      const response = await api.get('/users/roles');
      return response.data;
    }
  });

  const createUserMutation = useMutation({
    mutationFn: async (data) => {
      await api.post('/users', data);
    },
    onSuccess: () => {
      alert('User created successfully');
      onSuccess();
    },
    onError: (error) => {
      alert('Failed to create user: ' + (error.response?.data?.error || error.message));
    }
  });

  const updateUserMutation = useMutation({
    mutationFn: async (data) => {
      await api.put(`/users/${user.id}`, data);
    },
    onSuccess: () => {
      alert('User updated successfully');
      onSuccess();
    },
    onError: (error) => {
      alert('Failed to update user: ' + (error.response?.data?.error || error.message));
    }
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.username.trim()) {
      alert('Username is required');
      return;
    }
    if (!user && !formData.password.trim()) {
      alert('Password is required for new users');
      return;
    }

    try {
      if (user) {
        // Update user
        await updateUserMutation.mutateAsync({
          email: formData.email,
          fullName: formData.fullName,
          enabled: formData.enabled
        });
        // Update roles
        await saveUserRoles(user.id, formData.roleIds || []);
      } else {
        // Create user
        const result = await createUserMutation.mutateAsync(formData);
        // Note: createUserMutation doesn't return user ID, so we need to fetch it
        // For now, role assignment on create might need to be done via a separate call
        // or the backend needs to be updated to return the created user ID
      }
    } catch (error) {
      // Errors already handled by mutations
    }
  };

  const saveUserRoles = async (userId, roleIds) => {
    try {
      // First, get current roles
      const response = await api.get(`/users/${userId}/roles`);
      const currentRoles = response.data || [];
      const currentRoleIds = currentRoles.map(r => r.id);

      // Add new roles
      for (const roleId of roleIds) {
        if (!currentRoleIds.includes(roleId)) {
          await api.post(`/users/${userId}/roles`, { roleId });
        }
      }

      // Remove old roles
      for (const roleId of currentRoleIds) {
        if (!roleIds.includes(roleId)) {
          await api.delete(`/users/${userId}/roles/${roleId}`);
        }
      }
    } catch (error) {
      console.error('Error saving user roles:', error);
      alert('User saved but role assignment failed: ' + (error.response?.data?.error || error.message));
    }
  };

  const modalStyle = {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0,0,0,0.5)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 1000
  };

  const formStyle = {
    backgroundColor: 'white',
    padding: '2rem',
    borderRadius: '8px',
    maxWidth: '500px',
    width: '90%',
    maxHeight: '90vh',
    overflowY: 'auto'
  };

  const inputStyle = {
    width: '100%',
    padding: '0.5rem',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    fontSize: '0.875rem',
    marginBottom: '1rem'
  };

  return (
    <div style={modalStyle} onClick={onClose}>
      <div style={formStyle} onClick={(e) => e.stopPropagation()}>
        <h3 style={{ marginTop: 0 }}>{user ? 'Edit User' : 'Create User'}</h3>
        <form onSubmit={handleSubmit}>
          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600', fontSize: '0.875rem' }}>
            Username {!user && <span style={{ color: 'red' }}>*</span>}
          </label>
          <input
            type="text"
            value={formData.username}
            onChange={(e) => setFormData({...formData, username: e.target.value})}
            disabled={!!user}
            style={inputStyle}
          />

          {!user && (
            <>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600', fontSize: '0.875rem' }}>
                Password <span style={{ color: 'red' }}>*</span>
              </label>
              <input
                type="password"
                value={formData.password}
                onChange={(e) => setFormData({...formData, password: e.target.value})}
                style={inputStyle}
              />
            </>
          )}

          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600', fontSize: '0.875rem' }}>
            Email
          </label>
          <input
            type="email"
            value={formData.email}
            onChange={(e) => setFormData({...formData, email: e.target.value})}
            style={inputStyle}
          />

          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600', fontSize: '0.875rem' }}>
            Full Name
          </label>
          <input
            type="text"
            value={formData.fullName}
            onChange={(e) => setFormData({...formData, fullName: e.target.value})}
            style={inputStyle}
          />

          <label style={{ display: 'flex', alignItems: 'center', marginBottom: '1rem', fontSize: '0.875rem' }}>
            <input
              type="checkbox"
              checked={formData.enabled}
              onChange={(e) => setFormData({...formData, enabled: e.target.checked})}
              style={{ marginRight: '0.5rem' }}
            />
            Enabled
          </label>

          {/* Roles Section */}
          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600', fontSize: '0.875rem' }}>
            Assigned Roles
          </label>
          <div style={{
            maxHeight: '200px',
            overflowY: 'auto',
            border: '1px solid #ced4da',
            borderRadius: '4px',
            padding: '10px',
            marginBottom: '1rem'
          }}>
            {roles.length > 0 ? (
              roles.map(role => (
                <label key={role.id} style={{ display: 'flex', alignItems: 'flex-start', marginBottom: '0.5rem', fontSize: '0.875rem' }}>
                  <input
                    type="checkbox"
                    checked={formData.roleIds?.includes(role.id) || false}
                    onChange={(e) => {
                      const roleIds = formData.roleIds || [];
                      if (e.target.checked) {
                        setFormData({
                          ...formData,
                          roleIds: [...roleIds, role.id]
                        });
                      } else {
                        setFormData({
                          ...formData,
                          roleIds: roleIds.filter(id => id !== role.id)
                        });
                      }
                    }}
                    style={{ marginRight: '0.5rem', marginTop: '2px' }}
                  />
                  <span>
                    <strong>{role.name}</strong> - {role.description}
                  </span>
                </label>
              ))
            ) : (
              <div style={{ color: '#6c757d', fontSize: '0.875rem' }}>Loading roles...</div>
            )}
          </div>

          <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1.5rem' }}>
            <button type="submit" style={{
              padding: '0.5rem 1rem',
              backgroundColor: '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              flex: 1
            }}>
              {user ? 'Update' : 'Create'}
            </button>
            <button type="button" onClick={onClose} style={{
              padding: '0.5rem 1rem',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              flex: 1
            }}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// PasswordChangeModal Component
function PasswordChangeModal({ userId, onClose, onSuccess }) {
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const changePasswordMutation = useMutation({
    mutationFn: async (data) => {
      await api.post(`/users/${userId}/password`, data);
    },
    onSuccess: () => {
      onSuccess();
    },
    onError: (error) => {
      alert('Failed to change password: ' + (error.response?.data?.error || error.message));
    }
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!newPassword.trim()) {
      alert('Password is required');
      return;
    }
    if (newPassword !== confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    changePasswordMutation.mutate({ newPassword });
  };

  const modalStyle = {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0,0,0,0.5)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 1000
  };

  const formStyle = {
    backgroundColor: 'white',
    padding: '2rem',
    borderRadius: '8px',
    maxWidth: '400px',
    width: '90%'
  };

  const inputStyle = {
    width: '100%',
    padding: '0.5rem',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    fontSize: '0.875rem',
    marginBottom: '1rem'
  };

  return (
    <div style={modalStyle} onClick={onClose}>
      <div style={formStyle} onClick={(e) => e.stopPropagation()}>
        <h3 style={{ marginTop: 0 }}>Change Password</h3>
        <form onSubmit={handleSubmit}>
          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600', fontSize: '0.875rem' }}>
            New Password <span style={{ color: 'red' }}>*</span>
          </label>
          <input
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            style={inputStyle}
          />

          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600', fontSize: '0.875rem' }}>
            Confirm Password <span style={{ color: 'red' }}>*</span>
          </label>
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            style={inputStyle}
          />

          <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1.5rem' }}>
            <button type="submit" style={{
              padding: '0.5rem 1rem',
              backgroundColor: '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              flex: 1
            }}>
              Change Password
            </button>
            <button type="button" onClick={onClose} style={{
              padding: '0.5rem 1rem',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              flex: 1
            }}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
