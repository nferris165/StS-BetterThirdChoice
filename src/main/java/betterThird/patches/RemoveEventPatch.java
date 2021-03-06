package betterThird.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.GoldShrine;

@SpirePatch(
        clz=AbstractDungeon.class,
        method="initializeCardPools"
)
public class RemoveEventPatch {

    public static void Prefix(AbstractDungeon dungeon_instance) {
        /* // USING REPLACE INSTEAD OF REMOVE AND ADD
        AbstractDungeon.eventList.remove(Nest.ID);
        AbstractDungeon.eventList.remove(ScrapOoze.ID);
        AbstractDungeon.eventList.remove(Sssserpent.ID);
        AbstractDungeon.eventList.remove(GoopPuddle.ID);    //World of Goop
        AbstractDungeon.eventList.remove(BackToBasics.ID);  //Ancient Writing
        AbstractDungeon.specialOneTimeEventList.remove(SecretPortal.ID);
        AbstractDungeon.eventList.remove(ShiningLight.ID);
        BetterThird.logger.info("Removing base events.");
        */

        if(MaskedBanditsPatch.enlightenCheck()){
            AbstractDungeon.shrineList.remove(GoldShrine.ID);
        }
    }
}
