package betterThird.patches;

import betterThird.relics.NestCultRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.FaceTrader;
import com.megacrit.cardcrawl.relics.CultistMask;
import com.megacrit.cardcrawl.relics.SsserpentHead;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Collections;

public class FaceTraderPatch {

    @SpirePatch(
            clz = FaceTrader.class,
            method = "getRandomFace"
    )

    public static class maskPatch{
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"ids"}
        )

        public static void Insert(FaceTrader __instance, @ByRef ArrayList<String>[] ids){
            if(AbstractDungeon.player.hasRelic(NestCultRelic.ID)){
                ids[0].remove(CultistMask.ID);
            }
            if(MaskedBanditsPatch.enlightenCheck()){
                ids[0].remove(SsserpentHead.ID);
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(Collections.class, "shuffle");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }
}
