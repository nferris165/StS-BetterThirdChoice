package betterThird.patches;

import betterThird.events.*;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.beyond.SecretPortal;
import com.megacrit.cardcrawl.events.city.BackToBasics;
import com.megacrit.cardcrawl.events.city.Nest;
import com.megacrit.cardcrawl.events.exordium.GoopPuddle;
import com.megacrit.cardcrawl.events.exordium.ScrapOoze;
import com.megacrit.cardcrawl.events.exordium.ShiningLight;
import com.megacrit.cardcrawl.events.exordium.Sssserpent;
import com.megacrit.cardcrawl.helpers.EventHelper;

import static betterThird.BetterThird.*;

public class EventHelperPatch {
    @SpirePatch(
            clz = EventHelper.class,
            method = "getEvent"
    )

    public static class EventSwapPatch {
        public static AbstractEvent Postfix(AbstractEvent __result, String key){

            if (__result instanceof Nest && nest) {
                return new BetterNestEvent();
            } else if(__result instanceof ScrapOoze && scrap) {
                return new BetterScrapEvent();
            } else if(__result instanceof Sssserpent  && serpent) {
                return new BetterSerpentEvent();
            } else if(__result instanceof GoopPuddle && goop) {
                return new BetterGoopEvent();
            } else if(__result instanceof BackToBasics && writing) {
                return new BetterWritingEvent();
            } else if(__result instanceof SecretPortal && portal) {
                return new BetterPortalEvent();
            } else if(__result instanceof ShiningLight && shining) {
                return new BetterShiningEvent();
            }
            return __result;
        }
    }
}