package com.example.ruoyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruoyi.entity.SysMenu;
import com.example.ruoyi.mapper.SysMenuMapper;
import com.example.ruoyi.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单表 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    @Override
    @Cacheable(value = "menuTreeCache", key = "#menu.menuName + '_' + #menu.status")
    public List<SysMenu> selectMenuList(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(menu.getMenuName() != null, SysMenu::getMenuName, menu.getMenuName());
        wrapper.eq(menu.getMenuType() != null, SysMenu::getMenuType, menu.getMenuType());
        wrapper.eq(menu.getStatus() != null, SysMenu::getStatus, menu.getStatus());
        wrapper.orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum);
        return sysMenuMapper.selectList(wrapper);
    }

    @Override
    public SysMenu selectMenuById(Long menuId) {
        return sysMenuMapper.selectById(menuId);
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<>();
        List<Long> tempList = menus.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
        for (SysMenu menu : menus) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<>();
        for (SysMenu n : list) {
            if (n.getParentId().equals(t.getMenuId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"menuTreeCache", "userMenuCache"}, allEntries = true)
    public int insertMenu(SysMenu menu) {
        return sysMenuMapper.insert(menu);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"menuTreeCache", "userMenuCache"}, allEntries = true)
    public int updateMenu(SysMenu menu) {
        return sysMenuMapper.updateById(menu);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"menuTreeCache", "userMenuCache"}, allEntries = true)
    public int deleteMenuByIds(Long[] menuIds) {
        return sysMenuMapper.deleteBatchIds(Arrays.asList(menuIds));
    }

    @Override
    @Cacheable(value = "userMenuCache", key = "#userId")
    public List<SysMenu> selectMenusByUserId(Long userId) {
        return sysMenuMapper.selectMenusByUserId(userId);
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getMenuName, menu.getMenuName());
        wrapper.eq(SysMenu::getParentId, menu.getParentId());
        wrapper.ne(menu.getMenuId() != null, SysMenu::getMenuId, menu.getMenuId());
        return sysMenuMapper.selectCount(wrapper) == 0;
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, menuId);
        return sysMenuMapper.selectCount(wrapper) > 0;
    }
}