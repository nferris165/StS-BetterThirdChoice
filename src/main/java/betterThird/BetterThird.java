package betterThird;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.ReflectionHacks;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import betterThird.relics.NestCultRelic;
import betterThird.relics.SlimedRelic;
import betterThird.util.TextureLoader;
import betterThird.util.customMetrics;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.audio.Sfx;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;

@SuppressWarnings("unused")

@SpireInitializer
public class BetterThird implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        EditCharactersSubscriber,
        PostDeathSubscriber,
        PostInitializeSubscriber{

    public static final Logger logger = LogManager.getLogger(BetterThird.class.getName());

    //mod settings
    public static Properties defaultSettings = new Properties();
    private static final String nest_settings = "nest";
    public static boolean nest = true;
    private static final String goop_settings = "goop";
    public static boolean goop = true;
    private static final String portal_settings = "portal";
    public static boolean portal = true;
    private static final String scrap_settings = "scrap";
    public static boolean scrap = true;
    private static final String serpent_settings = "serpent";
    public static boolean serpent = true;
    private static final String shining_settings = "shining";
    public static boolean shining = true;
    private static final String writing_settings = "writing";
    public static boolean writing = true;
    private static final String devslime_settings = "devslime";
    public static boolean devslime = true;
    private static final String devnest_settings = "devnest";
    public static boolean devnest = true;

    private static final String MODNAME = "Better Third";
    private static final String AUTHOR = "Nichilas";
    private static final String DESCRIPTION = "A mod to add interesting or dynamic third options to many binary events.";

    private static final String BADGE_IMAGE = "betterThirdResources/images/Badge.png";

    private static final String AUDIO_PATH = "betterThirdResources/audio/";

    private static final String modID = "betterThird";


    //Image Directories
    public static String makeCardPath(String resourcePath) {
        return modID + "Resources/images/cards/" + resourcePath;
    }

    public static String makeEventPath(String resourcePath) {
        return modID + "Resources/images/events/" + resourcePath;
    }

    public static String makeMonsterPath(String resourcePath) {
        return modID + "Resources/images/monsters/" + resourcePath;
    }

    public static String makeOrbPath(String resourcePath) {
        return modID + "Resources/images/orbs/" + resourcePath;
    }

    public static String makePowerPath(String resourcePath) {
        return modID + "Resources/images/powers/" + resourcePath;
    }

    public static String makeRelicPath(String resourcePath) {
        return modID + "Resources/images/relics/" + resourcePath;
    }

    public static String makeRelicOutlinePath(String resourcePath) {
        return modID + "Resources/images/relics/outline/" + resourcePath;
    }

    public static String makeUIPath(String resourcePath) {
        return modID + "Resources/images/ui/" + resourcePath;
    }

    public static String makeVfxPath(String resourcePath) {
        return modID + "Resources/images/vfx/" + resourcePath;
    }


    public BetterThird() {
        BaseMod.subscribe(this);

        logger.info("Adding mod settings");
        defaultSettings.setProperty(goop_settings, "TRUE");
        defaultSettings.setProperty(nest_settings, "TRUE");
        defaultSettings.setProperty(portal_settings, "TRUE");
        defaultSettings.setProperty(scrap_settings, "TRUE");
        defaultSettings.setProperty(serpent_settings, "TRUE");
        defaultSettings.setProperty(shining_settings, "TRUE");
        defaultSettings.setProperty(writing_settings, "TRUE");
        defaultSettings.setProperty(devslime_settings, "FALSE");
        defaultSettings.setProperty(devnest_settings, "FALSE");

        try {
            SpireConfig config = new SpireConfig("betterThird", "betterThirdConfig", defaultSettings);
            config.load();
            goop = config.getBool(goop_settings);
            nest = config.getBool(nest_settings);
            portal = config.getBool(portal_settings);
            scrap = config.getBool(scrap_settings);
            serpent = config.getBool(serpent_settings);
            shining = config.getBool(shining_settings);
            writing = config.getBool(writing_settings);
            devslime = config.getBool(devslime_settings);
            devnest = config.getBool(devnest_settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initialize() {
        BetterThird betterThird = new BetterThird();
    }

    public void receiveEditPotions() {
        //BaseMod.addPotion(NewPotion.class, SLUMBERING_POTION_RUST, SLUMBERING_TEAL, SLUMBERING_POTION_RUST, NewPotion.POTION_ID, TheSlumbering.Enums.THE_SLUMBERING);
    }

    @Override
    public void receiveEditCards() {

    }

    @Override
    public void receiveEditCharacters() {
        receiveEditPotions();
    }

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String json = Gdx.files.internal(modID + "Resources/localization/eng/Keyword-Strings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(modID.toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new NestCultRelic(), RelicType.SHARED);
        BaseMod.addRelic(new SlimedRelic(), RelicType.SHARED);
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class,
                modID + "Resources/localization/eng/Card-Strings.json");
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                modID + "Resources/localization/eng/Character-Strings.json");
        BaseMod.loadCustomStringsFile(EventStrings.class,
                modID + "Resources/localization/eng/Event-Strings.json");
        BaseMod.loadCustomStringsFile(MonsterStrings.class,
                modID + "Resources/localization/eng/Monster-Strings.json");
        BaseMod.loadCustomStringsFile(OrbStrings.class,
                modID + "Resources/localization/eng/Orb-Strings.json");
        BaseMod.loadCustomStringsFile(PotionStrings.class,
                modID + "Resources/localization/eng/Potion-Strings.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                modID + "Resources/localization/eng/Power-Strings.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                modID + "Resources/localization/eng/Relic-Strings.json");
        BaseMod.loadCustomStringsFile(UIStrings.class,
                modID + "Resources/localization/eng/UI-Strings.json");
    }

    private void loadAudio() {
        HashMap<String, Sfx> map = (HashMap<String, Sfx>) ReflectionHacks.getPrivate(CardCrawlGame.sound, SoundMaster.class, "map");
        //map.put("Pop", new Sfx(AUDIO_PATH + "pop.ogg", false));
    }

    public static String makeID(String idText) {
        return modID + ":" + idText;
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        ModPanel settingsPanel = new ModPanel();

        ModLabeledToggleButton goopButton = new ModLabeledToggleButton("Enables Better World of Goop.",
                350.0f, 750.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                goop,
                settingsPanel,
                (label) -> {},
                (button) -> {

                    goop = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig("betterThird", "betterThirdConfig", defaultSettings);
                        config.setBool(goop_settings, goop);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        ModLabeledToggleButton nestButton = new ModLabeledToggleButton("Enables Better Nest.",
                350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                nest,
                settingsPanel,
                (label) -> {},
                (button) -> {

                    nest = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig("betterThird", "betterThirdConfig", defaultSettings);
                        config.setBool(nest_settings, nest);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        ModLabeledToggleButton portalButton = new ModLabeledToggleButton("Enables Better Secret Portal.",
                350.0f, 650.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                portal,
                settingsPanel,
                (label) -> {},
                (button) -> {

                    portal = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig("betterThird", "betterThirdConfig", defaultSettings);
                        config.setBool(portal_settings, portal);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        ModLabeledToggleButton scrapButton = new ModLabeledToggleButton("Enables Better Scrap Ooze.",
                350.0f, 600.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                scrap,
                settingsPanel,
                (label) -> {},
                (button) -> {

                    scrap = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig("betterThird", "betterThirdConfig", defaultSettings);
                        config.setBool(scrap_settings, scrap);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        ModLabeledToggleButton serpentButton = new ModLabeledToggleButton("Enables Better Ssssserpent.",
                350.0f, 550.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                serpent,
                settingsPanel,
                (label) -> {},
                (button) -> {

                    serpent = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig("betterThird", "betterThirdConfig", defaultSettings);
                        config.setBool(serpent_settings, serpent);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        ModLabeledToggleButton shiningButton = new ModLabeledToggleButton("Enables Better Shining Light.",
                350.0f, 500.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                shining,
                settingsPanel,
                (label) -> {},
                (button) -> {

                    shining = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig("betterThird", "betterThirdConfig", defaultSettings);
                        config.setBool(shining_settings, shining);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        ModLabeledToggleButton writingButton = new ModLabeledToggleButton("Enables Better Ancient Writing.",
                350.0f, 450.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                writing,
                settingsPanel,
                (label) -> {},
                (button) -> {

                    writing = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig("betterThird", "betterThirdConfig", defaultSettings);
                        config.setBool(writing_settings, writing);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        //TODO dev options

        ModLabeledToggleButton devslimeButton = new ModLabeledToggleButton("DEV: Start with Slime Mask.",
                350.0f, 400.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                devslime,
                settingsPanel,
                (label) -> {},
                (button) -> {

                    devslime = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig("betterThird", "betterThirdConfig", defaultSettings);
                        config.setBool(devslime_settings, devslime);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        ModLabeledToggleButton devnestButton = new ModLabeledToggleButton("DEV: Start with Ritual Mask",
                350.0f, 350.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                devnest,
                settingsPanel,
                (label) -> {},
                (button) -> {

                    devnest = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig("betterThird", "betterThirdConfig", defaultSettings);
                        config.setBool(devnest_settings, devnest);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });


        settingsPanel.addUIElement(goopButton);
        settingsPanel.addUIElement(nestButton);
        settingsPanel.addUIElement(portalButton);
        settingsPanel.addUIElement(scrapButton);
        settingsPanel.addUIElement(serpentButton);
        settingsPanel.addUIElement(shiningButton);
        settingsPanel.addUIElement(writingButton);
        settingsPanel.addUIElement(devslimeButton);
        settingsPanel.addUIElement(devnestButton);
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        //events
//        BaseMod.addEvent(new AddEventParams.Builder(BetterThirdEvent.ID, BetterThirdEvent.class)
//                .eventType(EventUtils.EventType.SHRINE).create());

        //audio
        loadAudio();
    }

    @Override
    public void receivePostDeath() {
        customMetrics metrics = new customMetrics();

        Thread t = new Thread(metrics);
        t.setName("Metrics");
        t.start();
    }
}
