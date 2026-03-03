package com.bugmaker.apt.common.util.menuInit;

import com.bugmaker.apt.domain.common.Menu;
import com.bugmaker.apt.enums.member.Status;
import com.bugmaker.apt.enums.common.MenuLevel;
import com.bugmaker.apt.enums.common.MenuType;
import com.bugmaker.apt.repository.common.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuBatchService {

    private final MenuRepository menuRepository;
    private final MenuCsvLoader menuCsvLoader;

    public void init() throws Exception {

        if (menuRepository.count() > 0) return;

        List<String[]> rows = menuCsvLoader.load();

        Map<String, Long> nameIdMap = new HashMap<>();

        for (String[] row : rows) {

            String name = row[0];
            MenuType type = MenuType.valueOf(row[1]);
            MenuLevel level = MenuLevel.valueOf(row[2]);
            Integer seq = Integer.parseInt(row[3]);
            String parentName = row.length > 4 ? row[4] : null;

            Long parentId = parentName == null || parentName.isBlank()
                    ? null
                    : nameIdMap.get(parentName);

            Menu menu = menuRepository.save(
                    Menu.builder()
                            .name(name)
                            .type(type)
                            .level(level)
                            .seq(seq)
                            .parentId(parentId)
                            .status(Status.ACTIVE)
                            .build()
            );

            nameIdMap.put(name, menu.getId());
        }
    }
}

