package cn.handyplus.companions.service;

import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.lib.db.Db;
import cn.handyplus.lib.db.enter.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class CompanionsOwnedService {
    private CompanionsOwnedService() {
    }

    public static CompanionsOwnedService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    
    public void add(CompanionsOwnedEnter enter) {
        Db.use(CompanionsOwnedEnter.class).execution().insert(enter);
    }

    
    public void addBatch(List<CompanionsOwnedEnter> enterList) {
        Db.use(CompanionsOwnedEnter.class).execution().insertBatch(enterList);
    }

    
    public List<CompanionsOwnedEnter> findByPlayer(UUID playerUuid) {
        Db<CompanionsOwnedEnter> use = Db.use(CompanionsOwnedEnter.class);
        use.where().eq(CompanionsOwnedEnter::getPlayerUuid, playerUuid);
        return use.execution().list();
    }

    public int count(UUID playerUuid) {
        Db<CompanionsOwnedEnter> use = Db.use(CompanionsOwnedEnter.class);
        use.where().eq(CompanionsOwnedEnter::getPlayerUuid, playerUuid);
        return use.execution().count();
    }

    public Optional<CompanionsOwnedEnter> findByPlayerAndCompanion(UUID playerUuid, String companion) {
        Db<CompanionsOwnedEnter> use = Db.use(CompanionsOwnedEnter.class);
        use.where().eq(CompanionsOwnedEnter::getPlayerUuid, playerUuid)
                .eq(CompanionsOwnedEnter::getCompanion, companion);
        return use.execution().selectOne();
    }

    
    public Page<CompanionsOwnedEnter> page(UUID playerUuid, Integer pageNum, Integer pageSize) {
        Db<CompanionsOwnedEnter> db = Db.use(CompanionsOwnedEnter.class);
        db.where().eq(CompanionsOwnedEnter::getPlayerUuid, playerUuid);
        db.where().limit(pageNum, pageSize);
        return db.execution().page();
    }

    public void updateAbilityLevel(Integer id, Integer abilityLevel) {
        Db<CompanionsOwnedEnter> use = Db.use(CompanionsOwnedEnter.class);
        use.update().set(CompanionsOwnedEnter::getAbilityLevel, abilityLevel);
        use.execution().updateById(id);
    }

    public void updateNameVisible(Integer id, boolean nameVisible) {
        Db<CompanionsOwnedEnter> use = Db.use(CompanionsOwnedEnter.class);
        use.update().set(CompanionsOwnedEnter::getNameVisible, nameVisible);
        use.execution().updateById(id);
    }

    public void updateCustomName(Integer id, String customName) {
        Db<CompanionsOwnedEnter> use = Db.use(CompanionsOwnedEnter.class);
        use.update().set(CompanionsOwnedEnter::getCustomName, customName);
        use.execution().updateById(id);
    }

    public void updateCustomWeapon(Integer id, String customWeapon) {
        Db<CompanionsOwnedEnter> use = Db.use(CompanionsOwnedEnter.class);
        use.update().set(CompanionsOwnedEnter::getCustomWeapon, customWeapon);
        use.execution().updateById(id);
    }

    
    public void remove(UUID playerUuid) {
        Db<CompanionsOwnedEnter> db = Db.use(CompanionsOwnedEnter.class);
        db.where().eq(CompanionsOwnedEnter::getPlayerUuid, playerUuid);
        db.execution().delete();
    }

    public void remove(UUID playerUuid, String companion) {
        Db<CompanionsOwnedEnter> db = Db.use(CompanionsOwnedEnter.class);
        db.where().eq(CompanionsOwnedEnter::getPlayerUuid, playerUuid)
                .eq(CompanionsOwnedEnter::getCompanion, companion);
        db.execution().delete();
    }

    public void remove() {
        Db.use(CompanionsOwnedEnter.class).execution().delete();
    }

    public Optional<CompanionsOwnedEnter> findById(Integer id) {
        return Db.use(CompanionsOwnedEnter.class).execution().selectById(id);
    }

    private static class SingletonHolder {
        private static final CompanionsOwnedService INSTANCE = new CompanionsOwnedService();
    }

    public List<CompanionsOwnedEnter> findAll() {
        return Db.use(CompanionsOwnedEnter.class).execution().list();
    }

}