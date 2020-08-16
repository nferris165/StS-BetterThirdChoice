package betterThird.patches;

import basemod.ReflectionHacks;
import betterThird.BetterThird;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Enlightenment;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.MaskedBandits;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class MaskedBanditsPatch {

    private static AbstractCard card;
    public static final String ID = BetterThird.makeID("Bandits");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

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
                __instance.roomEventText.updateBodyText(DESCRIPTIONS[0]);
                __instance.roomEventText.updateDialogOption(0, OPTIONS[0]);
                __instance.roomEventText.clearRemainingOptions();
                if(!card.upgraded){
                    card.upgrade();
                    AbstractDungeon.player.bottledCardUpgradeCheck(card);
                    AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(), 0.5F * (float) Settings.WIDTH, 0.5F * (float)Settings.HEIGHT));
                    ReflectionHacks.setPrivate(__instance,MaskedBandits.class,"screen", null);
                }
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
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
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

                if(card.upgraded){
                    __instance.roomEventText.updateDialogOption(0, OPTIONS[1]);
                }
                else{
                    __instance.roomEventText.clear();
                    __instance.roomEventText.addDialogOption(OPTIONS[1] + OPTIONS[2], card);
                    __instance.roomEventText.addDialogOption(OPTIONS[3]);
                }
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(MaskedBandits.class, "stealGold");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }

    public static boolean enlightenCheck(){
        for(AbstractCard c: AbstractDungeon.player.masterDeck.group){
            if(c instanceof Enlightenment){
                if(c.misc == 1){
                    card = c;
                    return true;
                }
            }
        }
        return false;
    }
}
