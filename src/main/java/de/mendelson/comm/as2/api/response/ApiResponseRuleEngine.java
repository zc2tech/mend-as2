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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Rule engine to evaluate and match API response rules against incoming requests.
 * Rules are evaluated in priority order (first match wins).
 */
public class ApiResponseRuleEngine {

    /**
     * Result object containing the matched rule and captured regex groups (if any).
     */
    public static class MatchResult {
        private final UserApiResponseRule rule;
        private final Map<String, String> captureGroups;

        public MatchResult(UserApiResponseRule rule, Map<String, String> captureGroups) {
            this.rule = rule;
            this.captureGroups = captureGroups;
        }

        public UserApiResponseRule getRule() {
            return rule;
        }

        public Map<String, String> getCaptureGroups() {
            return captureGroups;
        }
    }

    /**
     * Find the first matching rule for the given HTTP method and path.
     *
     * @param rules List of rules to evaluate (should be ordered by priority)
     * @param httpMethod HTTP method (GET, POST, PUT, DELETE, etc.)
     * @param requestPath Request path (e.g., "/a/b")
     * @return MatchResult containing rule and captured groups, or null if no match found
     */
    public static MatchResult findMatchingRule(List<UserApiResponseRule> rules,
                                                String httpMethod,
                                                String requestPath) {
        if (rules == null || rules.isEmpty()) {
            return null;
        }

        for (UserApiResponseRule rule : rules) {
            if (!rule.isEnabled()) {
                continue;
            }

            MatchResult result = matchesRule(rule, httpMethod, requestPath);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    /**
     * Check if a single rule matches the request and return match result with captured groups.
     */
    private static MatchResult matchesRule(UserApiResponseRule rule, String httpMethod, String requestPath) {
        // 1. Check HTTP method match (case-insensitive, or wildcard "*")
        if (!"*".equals(rule.getHttpMethod()) &&
                !rule.getHttpMethod().equalsIgnoreCase(httpMethod)) {
            return null;
        }

        // 2. Check path match based on match type and capture groups if regex
        Map<String, String> captureGroups = new HashMap<>();
        boolean matches = matchesPath(rule.getPathPattern(), rule.getPathMatchType(), requestPath, captureGroups);

        if (matches) {
            return new MatchResult(rule, captureGroups);
        }

        return null;
    }

    /**
     * Check if request path matches the pattern based on match type.
     * For regex matches, populate captureGroups map with numbered and named groups.
     */
    private static boolean matchesPath(String pattern, String matchType, String requestPath,
                                       Map<String, String> captureGroups) {
        if (pattern == null || requestPath == null) {
            return false;
        }

        switch (matchType) {
            case UserApiResponseRule.MATCH_TYPE_EXACT:
                return pattern.equals(requestPath);

            case UserApiResponseRule.MATCH_TYPE_PREFIX:
                return requestPath.startsWith(pattern);

            case UserApiResponseRule.MATCH_TYPE_WILDCARD:
                return matchesWildcard(pattern, requestPath);

            case UserApiResponseRule.MATCH_TYPE_REGEX:
                return matchesRegexWithCapture(pattern, requestPath, captureGroups);

            default:
                return false;
        }
    }

    /**
     * Match wildcard pattern with * (matches any sequence).
     * Example: "/api/*" matches "/api/test" and "/api/test/123"
     */
    private static boolean matchesWildcard(String pattern, String path) {
        try {
            String regexPattern = wildcardToRegex(pattern);
            return Pattern.matches(regexPattern, path);
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    /**
     * Convert wildcard pattern to regex pattern.
     */
    private static String wildcardToRegex(String wildcard) {
        StringBuilder regex = new StringBuilder("^");
        for (int i = 0; i < wildcard.length(); i++) {
            char c = wildcard.charAt(i);
            if (c == '*') {
                regex.append(".*");
            } else if (c == '?') {
                regex.append(".");
            } else if ("\\[]{}()^$.|+".indexOf(c) >= 0) {
                regex.append('\\').append(c);
            } else {
                regex.append(c);
            }
        }
        regex.append("$");
        return regex.toString();
    }

    /**
     * Match regex pattern.
     */
    private static boolean matchesRegex(String pattern, String path) {
        try {
            return Pattern.matches(pattern, path);
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    /**
     * Match regex pattern and capture groups (both numbered and named).
     */
    private static boolean matchesRegexWithCapture(String pattern, String path,
                                                    Map<String, String> captureGroups) {
        try {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(path);

            if (m.matches()) {
                // Capture numbered groups: ${1}, ${2}, etc.
                for (int i = 1; i <= m.groupCount(); i++) {
                    String value = m.group(i);
                    captureGroups.put(String.valueOf(i), value != null ? value : "");
                }

                // Extract named groups from the pattern string
                // Java named groups use (?<name>...) syntax
                List<String> namedGroupNames = extractNamedGroupNames(pattern);
                for (String groupName : namedGroupNames) {
                    try {
                        String value = m.group(groupName);
                        captureGroups.put(groupName, value != null ? value : "");
                    } catch (IllegalArgumentException e) {
                        // Group name not found - ignore
                    }
                }

                return true;
            }

            return false;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    /**
     * Extract named group names from a regex pattern string.
     * Looks for (?<name>...) patterns.
     */
    private static List<String> extractNamedGroupNames(String pattern) {
        List<String> names = new java.util.ArrayList<>();
        Pattern namedGroupPattern = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");
        Matcher matcher = namedGroupPattern.matcher(pattern);

        while (matcher.find()) {
            names.add(matcher.group(1));
        }

        return names;
    }

    /**
     * Substitute variables in response body.
     * Supported variables: ${path}, ${method}, ${requestId}, ${1}, ${2}, ${groupName}
     */
    public static String substituteVariables(String body, String path, String method, String requestId,
                                             Map<String, String> captureGroups) {
        if (body == null) {
            return "";
        }

        String result = body;
        result = result.replace("${path}", path != null ? path : "");
        result = result.replace("${method}", method != null ? method : "");
        result = result.replace("${requestId}", requestId != null ? requestId : "");

        // Replace capture groups (numbered and named)
        if (captureGroups != null && !captureGroups.isEmpty()) {
            for (Map.Entry<String, String> entry : captureGroups.entrySet()) {
                String placeholder = "${" + entry.getKey() + "}";
                result = result.replace(placeholder, entry.getValue());
            }
        }

        return result;
    }
}
