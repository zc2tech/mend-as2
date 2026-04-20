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

import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useCreatePartner, useUpdatePartner } from './usePartners';
import { useToast } from '../../components/Toast';

const partnerSchema = z.object({
  name: z.string().min(1, 'Name is required').max(255, 'Name too long'),
  as2Identification: z.string().min(1, 'AS2 ID is required').max(255, 'AS2 ID too long'),
  url: z.string().refine((val) => !val || z.string().url().safeParse(val).success, {
    message: 'Must be a valid URL'
  }),
  mdnURL: z.string().refine((val) => !val || z.string().url().safeParse(val).success, {
    message: 'Must be a valid URL'
  }),
  email: z.string().refine((val) => !val || z.string().email().safeParse(val).success, {
    message: 'Must be a valid email'
  }),
  subject: z.string().max(255, 'Subject too long').optional(),
  contentType: z.string().optional(),
  localStation: z.boolean().default(false)
});

export default function PartnerForm({ partner, onClose, onSuccess }) {
  const createPartner = useCreatePartner();
  const updatePartner = useUpdatePartner();
  const toast = useToast();
  const isEdit = !!partner;

  // Handle ESC key to close dialog
  useEffect(() => {
    const handleEscKey = (event) => {
      if (event.key === 'Escape') {
        onClose();
      }
    };

    document.addEventListener('keydown', handleEscKey);
    return () => document.removeEventListener('keydown', handleEscKey);
  }, [onClose]);

  // Debug: log partner object when editing
  if (isEdit) {
    console.log('Editing partner:', partner);
    console.log('localStation:', partner.localStation);
    console.log('isLocalStation:', partner.isLocalStation);
  }

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting }
  } = useForm({
    resolver: zodResolver(partnerSchema),
    defaultValues: partner || {
      name: '',
      as2Identification: '',
      url: '',
      mdnURL: '',
      email: '',
      subject: '',
      contentType: 'application/xml',
      localStation: false
    }
  });

  const onSubmit = async (data) => {
    try {
      if (isEdit) {
        await updatePartner.mutateAsync({ as2id: partner.as2Identification, partner: data });
        toast.success('Partner updated successfully');
      } else {
        await createPartner.mutateAsync(data);
        toast.success('Partner created successfully');
      }
      onSuccess?.();
      onClose();
    } catch (error) {
      toast.error('Failed to save partner: ' + (error.response?.data?.error || error.message));
    }
  };

  const formGroupStyle = {
    marginBottom: '1rem'
  };

  const labelStyle = {
    display: 'block',
    marginBottom: '0.5rem',
    fontWeight: '600',
    fontSize: '0.875rem'
  };

  const inputStyle = {
    width: '100%',
    padding: '0.5rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '1rem'
  };

  const errorStyle = {
    color: '#dc3545',
    fontSize: '0.875rem',
    marginTop: '0.25rem'
  };

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0,0,0,0.5)',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      zIndex: 1000,
      padding: '1rem'
    }} onClick={onClose}>
      <div style={{
        backgroundColor: 'white',
        padding: '2rem',
        borderRadius: '8px',
        maxWidth: '600px',
        width: '100%',
        maxHeight: '90vh',
        overflow: 'auto'
      }} onClick={(e) => e.stopPropagation()}>
        <h2 style={{ marginTop: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          {isEdit ? (
            <>
              <span>Edit Partner</span>
              <span style={{ fontSize: '1.5rem' }} title={(partner.localStation || partner.isLocalStation) ? 'Local Station' : 'Remote Partner'}>
                {(partner.localStation || partner.isLocalStation) ? '🏠' : '🌐'}
              </span>
              <span style={{ fontSize: '0.875rem', color: '#666', fontWeight: 'normal' }}>
                {(partner.localStation || partner.isLocalStation) ? '(Local Station)' : '(Remote Partner)'}
              </span>
            </>
          ) : (
            'Add Partner'
          )}
        </h2>

        <form onSubmit={handleSubmit(onSubmit)}>
          <div style={formGroupStyle}>
            <label style={labelStyle}>Name *</label>
            <input
              type="text"
              {...register('name')}
              style={inputStyle}
              disabled={isSubmitting}
            />
            {errors.name && <div style={errorStyle}>{errors.name.message}</div>}
          </div>

          <div style={formGroupStyle}>
            <label style={labelStyle}>AS2 Identification *</label>
            <input
              type="text"
              {...register('as2Identification')}
              style={inputStyle}
              disabled={isSubmitting}
            />
            {errors.as2Identification && <div style={errorStyle}>{errors.as2Identification.message}</div>}
          </div>

          <div style={formGroupStyle}>
            <label style={labelStyle}>URL</label>
            <input
              type="text"
              {...register('url')}
              placeholder="https://partner.example.com/as2"
              style={inputStyle}
              disabled={isSubmitting}
            />
            {errors.url && <div style={errorStyle}>{errors.url.message}</div>}
          </div>

          <div style={formGroupStyle}>
            <label style={labelStyle}>MDN URL</label>
            <input
              type="text"
              {...register('mdnURL')}
              placeholder="https://partner.example.com/mdn"
              style={inputStyle}
              disabled={isSubmitting}
            />
            {errors.mdnURL && <div style={errorStyle}>{errors.mdnURL.message}</div>}
          </div>

          <div style={formGroupStyle}>
            <label style={labelStyle}>Email</label>
            <input
              type="email"
              {...register('email')}
              placeholder="contact@partner.example.com"
              style={inputStyle}
              disabled={isSubmitting}
            />
            {errors.email && <div style={errorStyle}>{errors.email.message}</div>}
          </div>

          <div style={formGroupStyle}>
            <label style={labelStyle}>Subject</label>
            <input
              type="text"
              {...register('subject')}
              placeholder="AS2 Message"
              style={inputStyle}
              disabled={isSubmitting}
            />
            {errors.subject && <div style={errorStyle}>{errors.subject.message}</div>}
          </div>

          <div style={formGroupStyle}>
            <label style={labelStyle}>Content Type</label>
            <input
              type="text"
              {...register('contentType')}
              placeholder="application/xml"
              style={inputStyle}
              disabled={isSubmitting}
            />
          </div>

          {/* Only show Local Station checkbox when creating new partner */}
          {!isEdit && (
            <div style={{ ...formGroupStyle, display: 'flex', alignItems: 'center' }}>
              <input
                type="checkbox"
                {...register('localStation')}
                id="localStation"
                style={{ marginRight: '0.5rem' }}
                disabled={isSubmitting}
              />
              <label htmlFor="localStation" style={{ fontWeight: '600', fontSize: '0.875rem', cursor: 'pointer' }}>
                Local Station
              </label>
            </div>
          )}

          <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1.5rem' }}>
            <button
              type="submit"
              disabled={isSubmitting}
              style={{
                padding: '0.75rem 1.5rem',
                backgroundColor: isSubmitting ? '#6c757d' : '#28a745',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: isSubmitting ? 'not-allowed' : 'pointer',
                fontSize: '1rem'
              }}
            >
              {isSubmitting ? 'Saving...' : (isEdit ? 'Update Partner' : 'Create Partner')}
            </button>
            <button
              type="button"
              onClick={onClose}
              disabled={isSubmitting}
              style={{
                padding: '0.75rem 1.5rem',
                backgroundColor: '#6c757d',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: isSubmitting ? 'not-allowed' : 'pointer',
                fontSize: '1rem'
              }}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
