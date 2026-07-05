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

package de.mendelson.comm.as2.api.response;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database access layer for user API response rules.
 */
public class UserApiResponseRuleDB {

    /**
     * Load all response rules for a user, ordered by priority (ascending).
     *
     * @param userId The user's database ID
     * @param configConnection Database connection to config DB
     * @return List of rules (may be empty)
     */
    public List<UserApiResponseRule> loadRules(int userId, Connection configConnection)
            throws SQLException {
        List<UserApiResponseRule> rules = new ArrayList<>();

        String sql = "SELECT id, user_id, priority, enabled, http_method, path_pattern, "
                + "path_match_type, status_code, content_type, response_body, "
                + "created_at, updated_at "
                + "FROM user_api_response_rules WHERE user_id=? ORDER BY priority ASC, id ASC";

        try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    UserApiResponseRule rule = new UserApiResponseRule();
                    rule.setId(result.getInt("id"));
                    rule.setUserId(result.getInt("user_id"));
                    rule.setPriority(result.getInt("priority"));
                    rule.setEnabled(result.getBoolean("enabled"));
                    rule.setHttpMethod(result.getString("http_method"));
                    rule.setPathPattern(result.getString("path_pattern"));
                    rule.setPathMatchType(result.getString("path_match_type"));
                    rule.setStatusCode(result.getInt("status_code"));
                    rule.setContentType(result.getString("content_type"));
                    rule.setResponseBody(result.getString("response_body"));
                    rule.setCreatedAt(result.getTimestamp("created_at"));
                    rule.setUpdatedAt(result.getTimestamp("updated_at"));
                    rules.add(rule);
                }
            }
        }

        return rules;
    }

    /**
     * Load only enabled response rules for a user.
     */
    public List<UserApiResponseRule> loadEnabledRules(int userId, Connection configConnection)
            throws SQLException {
        List<UserApiResponseRule> rules = new ArrayList<>();

        String sql = "SELECT id, user_id, priority, enabled, http_method, path_pattern, "
                + "path_match_type, status_code, content_type, response_body, "
                + "created_at, updated_at "
                + "FROM user_api_response_rules WHERE user_id=? AND enabled=true "
                + "ORDER BY priority ASC, id ASC";

        try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    UserApiResponseRule rule = new UserApiResponseRule();
                    rule.setId(result.getInt("id"));
                    rule.setUserId(result.getInt("user_id"));
                    rule.setPriority(result.getInt("priority"));
                    rule.setEnabled(result.getBoolean("enabled"));
                    rule.setHttpMethod(result.getString("http_method"));
                    rule.setPathPattern(result.getString("path_pattern"));
                    rule.setPathMatchType(result.getString("path_match_type"));
                    rule.setStatusCode(result.getInt("status_code"));
                    rule.setContentType(result.getString("content_type"));
                    rule.setResponseBody(result.getString("response_body"));
                    rule.setCreatedAt(result.getTimestamp("created_at"));
                    rule.setUpdatedAt(result.getTimestamp("updated_at"));
                    rules.add(rule);
                }
            }
        }

        return rules;
    }

    /**
     * Insert a new response rule.
     *
     * @return The generated ID of the new rule
     */
    public int insertRule(UserApiResponseRule rule, Connection configConnection)
            throws SQLException {
        String sql = "INSERT INTO user_api_response_rules "
                + "(user_id, priority, enabled, http_method, path_pattern, path_match_type, "
                + "status_code, content_type, response_body) "
                + "VALUES (?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement statement = configConnection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, rule.getUserId());
            statement.setInt(2, rule.getPriority());
            statement.setBoolean(3, rule.isEnabled());
            statement.setString(4, rule.getHttpMethod());
            statement.setString(5, rule.getPathPattern());
            statement.setString(6, rule.getPathMatchType());
            statement.setInt(7, rule.getStatusCode());
            statement.setString(8, rule.getContentType());
            statement.setString(9, rule.getResponseBody());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        return -1;
    }

    /**
     * Update an existing response rule.
     */
    public void updateRule(UserApiResponseRule rule, Connection configConnection)
            throws SQLException {
        String sql = "UPDATE user_api_response_rules SET "
                + "priority=?, enabled=?, http_method=?, path_pattern=?, path_match_type=?, "
                + "status_code=?, content_type=?, response_body=? "
                + "WHERE id=?";

        try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
            statement.setInt(1, rule.getPriority());
            statement.setBoolean(2, rule.isEnabled());
            statement.setString(3, rule.getHttpMethod());
            statement.setString(4, rule.getPathPattern());
            statement.setString(5, rule.getPathMatchType());
            statement.setInt(6, rule.getStatusCode());
            statement.setString(7, rule.getContentType());
            statement.setString(8, rule.getResponseBody());
            statement.setInt(9, rule.getId());

            statement.executeUpdate();
        }
    }

    /**
     * Delete a response rule by ID.
     */
    public void deleteRule(int ruleId, Connection configConnection) throws SQLException {
        String sql = "DELETE FROM user_api_response_rules WHERE id=?";
        try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
            statement.setInt(1, ruleId);
            statement.executeUpdate();
        }
    }

    /**
     * Delete all response rules for a user.
     */
    public void deleteAllRulesForUser(int userId, Connection configConnection) throws SQLException {
        String sql = "DELETE FROM user_api_response_rules WHERE user_id=?";
        try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    /**
     * Update priorities for multiple rules (batch operation).
     * Used when reordering rules.
     */
    public void updatePriorities(List<UserApiResponseRule> rules, Connection configConnection)
            throws SQLException {
        String sql = "UPDATE user_api_response_rules SET priority=? WHERE id=?";

        try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
            for (UserApiResponseRule rule : rules) {
                statement.setInt(1, rule.getPriority());
                statement.setInt(2, rule.getId());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
}
