package cn.handyplus.companions.service;

import cn.handyplus.companions.enter.CompanionsActiveEnter;
import cn.handyplus.lib.db.Db;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class CompanionsActiveService {
    private CompanionsActiveService() {
    }

    public static CompanionsActiveService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final CompanionsActiveService INSTANCE = new CompanionsActiveService();
    }

    
    public void add(CompanionsActiveEnter enter) {
        Db.use(CompanionsActiveEnter.class).execution().insert(enter);
    }

    
    public void addBatch(List<CompanionsActiveEnter> enterList) {
        Db.use(CompanionsActiveEnter.class).execution().insertBatch(enterList);
    }

    
    public Optional<CompanionsActiveEnter> findByUid(UUID playerUuid) {
        Db<CompanionsActiveEnter> use = Db.use(CompanionsActiveEnter.class);
        use.where().eq(CompanionsActiveEnter::getPlayerUuid, playerUuid);
        return use.execution().selectOne();
    }

    
    public void remove(UUID playerUuid) {
        Db<CompanionsActiveEnter> db = Db.use(CompanionsActiveEnter.class);
        db.where().eq(CompanionsActiveEnter::getPlayerUuid, playerUuid);
        db.execution().delete();
    }

    public void remove() {
        Db.use(CompanionsActiveEnter.class).execution().delete();
    }

    public List<CompanionsActiveEnter> findAll() {
        return Db.use(CompanionsActiveEnter.class).execution().list();
    }

}