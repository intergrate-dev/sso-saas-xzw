package com.founder.sso.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WebUtil {
    public static final String REQUEST_PATH_ATTR_NAME = "source_request_path";
    public static final String CLEAN_PARAMS_ATTR_NAME="clean_params";
    private static Predicate<String> blankStringPredicate = new Predicate<String>() {
        @Override
        public boolean apply(String input) {
            if(StringUtils.isNoneBlank(input)) {
                return true;
            }
            return false;
        }
    };

    /**
     * 预处理参数
     * 将请求中的参数进行预处理清理所有的值为blank的键值对
     * 
     * @param request
     * @return
     */
    public static Map<String, List<String>> preProcessParams(ServletRequest request) {
        HttpServletRequest req = (HttpServletRequest) request;
        Map<String, String[]> parameterMap = req.getParameterMap();
        Map<String, List<String>> mappedValues = Maps.newHashMap();
        for (Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            List<String> values = Arrays.asList(entry.getValue());
            Collection<String> filtered = Collections2.filter(values, blankStringPredicate);
            if (filtered.size() != 0) {
                mappedValues.put(key, values);
            }
        }
        mappedValues.put(REQUEST_PATH_ATTR_NAME, Lists.newArrayList(req.getRequestURI()));
        request.setAttribute(CLEAN_PARAMS_ATTR_NAME, mappedValues);
        return mappedValues;
    }

    public static String getFirstValue(Map<String, List<String>> mappedParams, String key) {
        List<String> list = mappedParams.get(key);
        if (list.size() >= 1) {
            return list.get(0);
        } else {
            throw new IllegalArgumentException("没有获得指定的[key=" + key + "]的参数值");
        }

    }
    
    public static String getFirstValue( ServletRequest request, String key) {
        Map<String, List<String>> mappedParams = getCleanParams(request);
        List<String> list = mappedParams.get(key);
        if (list != null && list.size() >= 1) {
            return list.get(0);
        } else {
            throw new IllegalArgumentException("没有获得指定的[key=" + key + "]的参数值");
        }

    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, List<String>> getCleanParams(ServletRequest request) {
        Map<String, List<String>>  cleanParams = (Map<String, List<String>>) request.getAttribute(CLEAN_PARAMS_ATTR_NAME);
        if(cleanParams==null) {
            cleanParams=preProcessParams(request);
        }
        return cleanParams;
    }
    

}
