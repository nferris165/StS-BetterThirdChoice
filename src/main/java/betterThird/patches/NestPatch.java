package betterThird.patches;

import betterThird.relics.NestCultRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static betterThird.BetterThird.nest;

public class NestPatch {

    @SpirePatch(
            clz = DrawCardAction.class,
            method = "update"
    )

    public static class RelicHandPatch{

        public static void Prefix(DrawCardAction __instance){
            if(AbstractDungeon.player.hasRelic(NestCultRelic.ID) && GameActionManager.turn == 1){
                if(!NestCultRelic.firstTurn){
                    __instance.amount = 0;
                    NestCultRelic.firstTurn = true;
                }
            }
        }
    }
}
