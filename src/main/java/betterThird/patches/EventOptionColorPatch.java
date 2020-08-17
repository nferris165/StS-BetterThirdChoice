package betterThird.patches;

import betterThird.BetterThird;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.badlogic.gdx.graphics.Color;
import javassist.CannotCompileException;
import javassist.CtBehavior;


import java.util.ArrayList;


public class EventOptionColorPatch {

//    @SpirePatch(
//            clz = LargeDialogOptionButton.class,
//            method = "render"
//    )
//
//    public static class buttonPatch{
//        @SpireInsertPatch(
//                locator = Locator.class
//        )
//
//        public static SpireReturn Insert(LargeDialogOptionButton __instance, @ByRef SpriteBatch sb[]){
//            //ReflectionHacks.setPrivate(__instance, LargeDialogOptionButton.class, "textColor", Color.MAROON);
//            float x = (float)ReflectionHacks.getPrivate(__instance, LargeDialogOptionButton.class, "x");
//            float y = (float)ReflectionHacks.getPrivate(__instance, LargeDialogOptionButton.class, "y");
//            Color color =  (Color)ReflectionHacks.getPrivate(__instance, LargeDialogOptionButton.class, "textColor");
//            FontHelper.exampleNonWordWrappedText(sb[0], FontHelper.smallDialogOptionFont, __instance.msg,
//                    x + -400.0F * Settings.scale, y + 10.0F * Settings.scale, color, (float) Settings.WIDTH, 0.0F);
//            return SpireReturn.Return(null);
//        }
//    }
//
//    private static class Locator extends SpireInsertLocator {
//        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
//            Matcher finalMatcher = new Matcher.MethodCallMatcher(FontHelper.class, "getSmartWidth");
//            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
//        }
//    }


    @SpirePatch(
            clz = FontHelper.class,
            method = "identifyColor"
    )

    public static class fontPatch{
        @SpireInsertPatch(
                rloc = 0
        )

        public static SpireReturn Insert(@ByRef String[] word){
            if (word[0].length() > 0 && word[0].charAt(0) == '#' && word[0].charAt(1) == '#') {
                String color = word[0].substring(1, 8);
                Color retVal = Color.valueOf(color);
                return SpireReturn.Return(retVal);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = FontHelper.class,
            method = "renderSmartText",
            paramtypez = {SpriteBatch.class, BitmapFont.class, String.class, float.class, float.class, float.class, float.class, Color.class}
    )

    public static class trimPatch{
        @SpireInsertPatch(
                locator = Locator.class
        )

        public static void Insert(SpriteBatch sb, BitmapFont font, String msg, float x, float y, float lineWidth, float lineSpacing, Color baseColor, @ByRef String[] ___word){
            if (___word[0].length() > 0 && ___word[0].charAt(0) == '#' && ___word[0].charAt(1) == '#') {
                ___word[0] = ___word[0].substring(6);
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(String.class, "substring");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }
}
