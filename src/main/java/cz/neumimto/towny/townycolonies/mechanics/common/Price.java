package cz.neumimto.towny.townycolonies.mechanics.common;

import com.palmergames.bukkit.towny.TownyMessaging;
import cz.neumimto.towny.townycolonies.mechanics.RequirementMechanic;
import cz.neumimto.towny.townycolonies.mechanics.TownContext;


public class Price implements RequirementMechanic<DoubleWrapper> {

    @Override
    public boolean check(TownContext townContext, DoubleWrapper configContext) {
        return townContext.town.getAccount().getHoldingBalance() >= configContext.value;
    }

    @Override
    public void postAction(TownContext townContext, DoubleWrapper configContext) {
        townContext.town.getAccount().withdraw(configContext.value, "townycolonies - bought " + townContext.structure.id);
    }

    @Override
    public void nokmessage(TownContext townContext, DoubleWrapper configuration) {
        townContext.player.sendMessage("Not enough funds in town bank - " + configuration);
    }

    @Override
    public void okmessage(TownContext townContext, DoubleWrapper configuration) {
        TownyMessaging.sendPrefixedTownMessage(townContext.town, townContext.player.getName() + " bought " + townContext.structure.name);
    }

    @Override
    public DoubleWrapper getNew() {
        return new DoubleWrapper();
    }
}
