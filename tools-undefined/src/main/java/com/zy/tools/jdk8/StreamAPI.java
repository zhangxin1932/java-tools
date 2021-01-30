package com.zy.tools.jdk8;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * https://blog.csdn.net/qq_42563841/article/details/105279512?utm_medium=distribute.pc_relevant.none-task-blog-baidujs_title-3&spm=1001.2101.3001.4242
 * 
 */
public class StreamAPI {

    private static final List<StorePoiRel> LIST = new ArrayList<>();

    static {
        LIST.add(new StorePoiRel("1236", 2001L, true));
        LIST.add(new StorePoiRel("1234", 1000L, true));
        LIST.add(new StorePoiRel("1234", 1000L, true));
        LIST.add(new StorePoiRel("1234", 1000L, false));
        LIST.add(new StorePoiRel("1235", 1001L, false));
    }

    @Test
    public void fn01() {
        if (!CollectionUtils.isEmpty(LIST)) {
            System.out.println("1.sortedList --------------------------------------------");
            List<StorePoiRel> sortedList = LIST.stream().filter(Objects::nonNull).filter(e -> StringUtils.isNotBlank(e.getStoreCode())).sorted(Comparator.comparing(StorePoiRel::getStoreCode)).collect(Collectors.toList());
            System.out.println(sortedList);

            System.out.println("2.poiIds --------------------------------------------");
            List<Long> poiIds = LIST.stream().filter(Objects::nonNull).map(StorePoiRel::getPoiId).collect(Collectors.toList());
            System.out.println(poiIds);

            System.out.println("3.sortedPoiIds --------------------------------------------");
            poiIds.sort(Comparator.comparing(e -> e));
            System.out.println(poiIds);

            System.out.println("4.statusMap --------------------------------------------");
            Map<String, Boolean> statusMap = LIST.stream().filter(Objects::nonNull).filter(e -> StringUtils.isNotBlank(e.getStoreCode())).collect(Collectors.toMap(StorePoiRel::getStoreCode, StorePoiRel::getStatus, (e1, e2) -> e1));
            System.out.println(statusMap);

            System.out.println("5.storePoiRelMap --------------------------------------------");
            Map<String, StorePoiRel> storePoiRelMap = LIST.stream().filter(Objects::nonNull).filter(e -> StringUtils.isNotBlank(e.getStoreCode())).collect(Collectors.toMap(StorePoiRel::getStoreCode, e -> e, (e1, e2) -> e1));
            System.out.println(storePoiRelMap);

        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class StorePoiRel implements Serializable {
        private static final long serialVersionUID = -8466819608252180639L;
        private String storeCode;
        private Long poiId;
        private Boolean status;
    }
}
