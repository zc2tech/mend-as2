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

import { Navigate } from 'react-router-dom';
import { useAuth } from './useAuth';

/**
 * Route wrapper that checks if user has required permissions
 * If user doesn't have any of the required permissions, redirect to dashboard
 */
export default function PermissionRoute({ children, requiredPermissions }) {
  const { hasAnyPermission } = useAuth();

  // Check if user has at least one of the required permissions
  if (!hasAnyPermission(...requiredPermissions)) {
    return <Navigate to="/" replace />;
  }

  return children;
}
