package cn.woodwhales.gray.common.util;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author woodwhales on 2022-12-30 10:55
 */
public class DataTool {

    public static <S, T> List<T> toList(List<S> sourceList, Function<S, T> function) {
        if(CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        return sourceList.stream().map(function).collect(Collectors.toList());
    }

}
