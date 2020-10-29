package com.zy.commons.lang.filter;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 使用方案:
 * @Configuration
 * public class ServletConfig {
 *     @Bean
 *     public FilterRegistrationBean<XSSFilter> filterRegistrationBean() {
 *         FilterRegistrationBean<XSSFilter> xssFilter = new FilterRegistrationBean<>();
 *         xssFilter.setFilter(new XSSFilter());
 *         xssFilter.setName("xssFilter");
 *         xssFilter.addUrlPatterns("/*");
 *         return xssFilter;
 *     }
 * }
 *
 */
public class XSSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(new XSSHttpServletRequestWrapper((HttpServletRequest) servletRequest), servletResponse);
    }

    private static class XSSHttpServletRequestWrapper extends HttpServletRequestWrapper {

        XSSHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> parameterMap = super.getParameterMap();
            if (MapUtils.isNotEmpty(parameterMap)) {
                Map<String, String[]> escapeMap = new HashMap<>();
                parameterMap.forEach((k, v) -> {
                    if (StringUtils.isNotBlank(k)) {
                        String k1 = StringEscapeUtils.escapeHtml4(k);
                        if (Objects.isNull(v)) {
                            escapeMap.put(k1, null);
                        } else {
                            String[] v1 = new String[v.length];
                            for (int i = 0; i < v.length; i ++) {
                                v1[i] = StringEscapeUtils.escapeHtml4(v[i]);
                            }
                            escapeMap.put(k1, v1);
                        }
                    }
                });
                return escapeMap;
            }
            return parameterMap;
        }

        @Override
        public String getHeader(String name) {
            return StringEscapeUtils.escapeHtml4(super.getHeader(name));
        }

        @Override
        public String getQueryString() {
            return StringEscapeUtils.escapeHtml4(super.getQueryString());
        }

        @Override
        public String getParameter(String name) {
            return StringEscapeUtils.escapeHtml4(super.getParameter(name));
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (Objects.nonNull(values) && values.length > 0) {
                int length = values.length;
                String[] escapeValues = new String[length];
                for (int i = 0; i < length; i ++) {
                    escapeValues[i] = StringEscapeUtils.escapeHtml4(values[i]);
                }
                return escapeValues;
            }
            return values;
        }
    }
}
