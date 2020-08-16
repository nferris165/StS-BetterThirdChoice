package betterThird.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Enlightenment;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.MaskedBandits;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class MaskedBanditsPatch {

    @SpirePatch(
            clz = MaskedBandits.class,
            method = "buttonEffect"
    )

    public static class buttonPatch{
        @SpireInsertPatch(
                locator = Locator.class
        )

        public static SpireReturn Insert(MaskedBandits __instance, int buttonPressed){
            if(enlightenCheck() && buttonPressed == 0){
                __instance.roomEventText.updateBodyText("hit snek");
                __instance.roomEventText.updateDialogOption(0, "[Leave]");
                __instance.roomEventText.clearRemainingOptions();
                //update screen
                ReflectionHacks.setPrivate(__instance,MaskedBandits.class,"screen", null);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = MaskedBandits.class,
            method = "buttonEffect"
    )

    public static class buttonPrefixPatch{

        public static SpireReturn Prefix(MaskedBandits __instance, int buttonPressed){
            if(ReflectionHacks.getPrivate(__instance,MaskedBandits.class,"screen") == null){
                AbstractDungeon.dungeonMapScreen.open(false);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = MaskedBandits.class,
            method = SpirePatch.CONSTRUCTOR
    )

    public static class optionPatch{

        public static void Postfix(MaskedBandits __instance){
            if(enlightenCheck()){
                __instance.roomEventText.updateDialogOption(0, "[Serpent]");
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(MaskedBandits.class, "stealGold");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }

    private static boolean enlightenCheck(){
        for(AbstractCard c: AbstractDungeon.player.masterDeck.group){
            if(c instanceof Enlightenment){
                if(c.misc == 1){
                    return true;
                }
            }
        }
        return false;
    }
}
