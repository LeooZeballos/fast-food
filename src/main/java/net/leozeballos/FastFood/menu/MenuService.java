package net.leozeballos.FastFood.menu;

import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.error.ResourceNotFoundException;
import net.leozeballos.FastFood.mapper.MenuMapper;
import net.leozeballos.FastFood.util.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;
    private final AuditService auditService;

    public List<MenuDTO> findAllDTO() {
        return menuRepository.findAllWithItems().stream()
                .map(menuMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public Menu findById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + id));
    }

    @Transactional
    public Menu save(Menu menu) {
        String action = (menu.getId() == null) ? "CREATE_MENU" : "UPDATE_MENU";
        Menu saved = menuRepository.save(menu);
        auditService.logAction(action, "ID=" + saved.getId() + ", Name=" + saved.getName());
        return saved;
    }

    @Transactional
    public void disableItem(Long id) {
        Menu menu = findById(id);
        menu.disable();
        auditService.logAction("DISABLE_MENU", "ID=" + id + ", Name=" + menu.getName());
        menuRepository.save(menu);
    }

    @Transactional
    public void enableItem(Long id) {
        Menu menu = findById(id);
        menu.enable();
        auditService.logAction("ENABLE_MENU", "ID=" + id + ", Name=" + menu.getName());
        menuRepository.save(menu);
    }

    @Transactional
    public void delete(Menu menu) {
        auditService.logAction("DELETE_MENU", "ID=" + menu.getId() + ", Name=" + menu.getName());
        menuRepository.delete(menu);
    }

    @Transactional
    public void deleteById(Long id) {
        Menu menu = findById(id);
        auditService.logAction("DELETE_MENU", "ID=" + id + ", Name=" + menu.getName());
        menuRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll() {
        menuRepository.deleteAll();
    }

}
