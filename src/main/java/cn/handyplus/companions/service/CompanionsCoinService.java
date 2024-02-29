package cn.handyplus.companions.service;

import cn.handyplus.companions.enter.CompanionsCoinEnter;
import cn.handyplus.lib.db.Db;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class CompanionsCoinService {
    private CompanionsCoinService() {
    }

    public static CompanionsCoinService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    
    public void add(CompanionsCoinEnter enter) {
        Db.use(CompanionsCoinEnter.class).execution().insert(enter);
    }

    
    public void addBatch(List<CompanionsCoinEnter> enterList) {
        Db.use(CompanionsCoinEnter.class).execution().insertBatch(enterList);
    }

    
    public void init(OfflinePlayer player) {

        Optional<CompanionsCoinEnter> coinOptional = this.findByUid(player.getUniqueId());
        if (coinOptional.isPresent()) {
            return;
        }
        CompanionsCoinEnter companionsCoin = new CompanionsCoinEnter();
        companionsCoin.setPlayerUuid(player.getUniqueId().toString());
        companionsCoin.setPlayerName(player.getName());
        companionsCoin.setCoins(0L);
        this.add(companionsCoin);
    }

    
    public Optional<CompanionsCoinEnter> findByUid(UUID playerUuid) {
        Db<CompanionsCoinEnter> use = Db.use(CompanionsCoinEnter.class);
        use.where().eq(CompanionsCoinEnter::getPlayerUuid, playerUuid);
        return use.execution().selectOne();
    }

    
    public Long findCoinByUid(UUID playerUuid) {
        Optional<CompanionsCoinEnter> coinOptional = findByUid(playerUuid);
        return coinOptional.isPresent() ? coinOptional.get().getCoins() : 0L;
    }

    
    public boolean give(UUID playerUuid, Long amount) {
        Db<CompanionsCoinEnter> use = Db.use(CompanionsCoinEnter.class);
        use.update().add(CompanionsCoinEnter::getCoins, CompanionsCoinEnter::getCoins, amount);
        use.where().eq(CompanionsCoinEnter::getPlayerUuid, playerUuid);
        return use.execution().update() > 0;
    }

    
    public boolean set(UUID playerUuid, Long amount) {
        Db<CompanionsCoinEnter> use = Db.use(CompanionsCoinEnter.class);
        use.update().set(CompanionsCoinEnter::getCoins, amount);
        use.where().eq(CompanionsCoinEnter::getPlayerUuid, playerUuid);
        return use.execution().update() > 0;
    }

    
    public boolean take(UUID playerUuid, Long amount) {
        Db<CompanionsCoinEnter> use = Db.use(CompanionsCoinEnter.class);
        use.update().subtract(CompanionsCoinEnter::getCoins, CompanionsCoinEnter::getCoins, amount);
        use.where().eq(CompanionsCoinEnter::getPlayerUuid, playerUuid);
        return use.execution().update() > 0;
    }

    private static class SingletonHolder {
        private static final CompanionsCoinService INSTANCE = new CompanionsCoinService();
    }

    public void remove() {
        Db.use(CompanionsCoinEnter.class).execution().delete();
    }

    
    public void remove(UUID playerUuid) {
        Db<CompanionsCoinEnter> db = Db.use(CompanionsCoinEnter.class);
        db.where().eq(CompanionsCoinEnter::getPlayerUuid, playerUuid);
        db.execution().delete();
    }

    public List<CompanionsCoinEnter> findAll() {
        return Db.use(CompanionsCoinEnter.class).execution().list();
    }

}