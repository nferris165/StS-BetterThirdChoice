package betterThird.events;

import basemod.CustomEventRoom;
import betterThird.BetterThird;
import betterThird.util.PortalInfo;
import betterThird.util.PortalTreasureRoom;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.events.beyond.SensoryStone;
import com.megacrit.cardcrawl.events.city.ForgottenAltar;
import com.megacrit.cardcrawl.events.city.KnowingSkull;
import com.megacrit.cardcrawl.events.shrines.GremlinMatchGame;
import com.megacrit.cardcrawl.events.shrines.NoteForYourself;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.vfx.FadeWipeParticle;

import java.util.ArrayList;

public class BetterPortalEvent extends AbstractImageEvent {

    public static final String ID = BetterThird.makeID("BetterPortal");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/secretPortal.jpg";

    private static final String DIALOG_2, DIALOG_3, DIALOG_4, DIALOG_41, DIALOG_42, DIALOG_5;
    private static String color;
    private CurScreen screen;
    public AbstractChest chest;
    private static ArrayList<PortalInfo> portals = new ArrayList<>();


    public BetterPortalEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.screen = CurScreen.INTRO;
        generatePortal();
        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[2]);

        if(AbstractDungeon.ascensionLevel >= 15) {
        } else {
        }

        //TODO prismatic
    }

    private static void generatePortal(){
        portals.add(new PortalInfo("[#FF0000]", 1.0F)); //Red
        portals.add(new PortalInfo("[#FF7F00]", 0.9F)); //Orange
        portals.add(new PortalInfo("[#FFFF00]", 0.8F)); //Yellow
        portals.add(new PortalInfo("[#00FF00]", 1.1F)); //Green
        portals.add(new PortalInfo("[#0000FF]", 1.3F)); //Blue
        portals.add(new PortalInfo("[#8B00FF]", 0.6F)); //Violet
        PortalInfo.normalizeWeights(portals);
        color = PortalInfo.roll(portals, AbstractDungeon.miscRng.random());
    }

    @Override
    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_PORTAL");
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {
            case INTRO:
                switch(buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DIALOG_2);
                        this.screen = CurScreen.ACCEPT;
                        logMetric(ID, "Portal");
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        CardCrawlGame.screenShake.mildRumble(5.0F);
                        CardCrawlGame.sound.play("ATTACK_MAGIC_SLOW_2");
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DIALOG_4 + color + DIALOG_41 + color + DIALOG_42);
                        this.screen = CurScreen.RANDOM;
                        CardCrawlGame.screenShake.mildRumble(5.0F);
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        break;
                    case 2:
                        this.imageEventText.updateBodyText(DIALOG_3);
                        this.screen = CurScreen.LEAVE;
                        logMetricIgnored(ID);
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        break;
                }

                this.imageEventText.clearRemainingOptions();
                break;
            case ACCEPT:
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                MapRoomNode node = new MapRoomNode(-1, 15);
                node.room = new MonsterRoomBoss();
                AbstractDungeon.nextRoom = node;
                CardCrawlGame.music.fadeOutTempBGM();
                AbstractDungeon.pathX.add(1);
                AbstractDungeon.pathY.add(15);
                AbstractDungeon.topLevelEffects.add(new FadeWipeParticle());
                AbstractDungeon.nextRoomTransitionStart();
                break;
            case RANDOM:
                portalAction();
                break;
            case RED:
                chestTransition();
                break;
            default:
                this.openMap();
                break;
        }
    }

    private void portalAction(){
        //TODO readable?
        logMetric(ID, color);
        switch(color){
            case "[#FF0000]":   //Red
                this.imageEventText.updateBodyText(DIALOG_5);
                this.screen = CurScreen.RED;
                //logMetricIgnored("SecretPortal");
                this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                break;
            case "[#FF7F00]":   //Orange

                eventTransition(GremlinMatchGame.ID);
                break;
            case "[#FFFF00]":   //Yellow
                eventTransition(NoteForYourself.ID);
                break;
            case "[#00FF00]":   //Green
                eventTransition(KnowingSkull.ID);
                break;
            case "[#0000FF]":   //Blue
                eventTransition(SensoryStone.ID);
                break;
            case "[#8B00FF]":   //Violet
                eventTransition(ForgottenAltar.ID);
                break;
            default:
                this.openMap();
                break;
        }
    }

    private void chestTransition(){
        RoomEventDialog.optionList.clear();
        MapRoomNode cur = AbstractDungeon.currMapNode;
        MapRoomNode node = new MapRoomNode(cur.x, cur.y);
        node.room = new PortalTreasureRoom();
        node.room.phase = AbstractRoom.RoomPhase.INCOMPLETE;
        ArrayList<MapEdge> curEdges = cur.getEdges();

        for (MapEdge edge : curEdges) {
            node.addEdge(edge);
        }

        AbstractDungeon.player.releaseCard();
        AbstractDungeon.overlayMenu.hideCombatPanels();
        AbstractDungeon.previousScreen = null;
        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.dungeonMapScreen.closeInstantly();
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.fadeIn();
        AbstractDungeon.effectList.clear();
        AbstractDungeon.topLevelEffects.clear();
        AbstractDungeon.topLevelEffectsQueue.clear();
        AbstractDungeon.effectsQueue.clear();
        AbstractDungeon.dungeonMapScreen.dismissable = true;
        AbstractDungeon.nextRoom = node;
        AbstractDungeon.setCurrMapNode(node);
        AbstractDungeon.getCurrRoom().onPlayerEntry();
        AbstractDungeon.scene.nextRoom(node.room);
        AbstractDungeon.rs = node.room.event instanceof AbstractImageEvent ? AbstractDungeon.RenderScene.EVENT : AbstractDungeon.RenderScene.NORMAL;
    }

    private void eventTransition(String eventName){
        RoomEventDialog.optionList.clear();
        AbstractDungeon.eventList.add(0, eventName);
        MapRoomNode cur = AbstractDungeon.currMapNode;
        MapRoomNode node = new MapRoomNode(cur.x, cur.y);
        node.room = new CustomEventRoom();
        ArrayList<MapEdge> curEdges = cur.getEdges();

        for (MapEdge edge : curEdges) {
            node.addEdge(edge);
        }

        AbstractDungeon.player.releaseCard();
        AbstractDungeon.overlayMenu.hideCombatPanels();
        AbstractDungeon.previousScreen = null;
        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.dungeonMapScreen.closeInstantly();
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.fadeIn();
        AbstractDungeon.effectList.clear();
        AbstractDungeon.topLevelEffects.clear();
        AbstractDungeon.topLevelEffectsQueue.clear();
        AbstractDungeon.effectsQueue.clear();
        AbstractDungeon.dungeonMapScreen.dismissable = true;
        AbstractDungeon.nextRoom = node;
        AbstractDungeon.setCurrMapNode(node);
        AbstractDungeon.getCurrRoom().onPlayerEntry();
        AbstractDungeon.scene.nextRoom(node.room);
        AbstractDungeon.rs = node.room.event instanceof AbstractImageEvent ? AbstractDungeon.RenderScene.EVENT : AbstractDungeon.RenderScene.NORMAL;
    }

    static {
        DIALOG_2 = DESCRIPTIONS[1];
        DIALOG_3 = DESCRIPTIONS[2];
        DIALOG_4 = DESCRIPTIONS[3];
        DIALOG_41 = DESCRIPTIONS[4];
        DIALOG_42 = DESCRIPTIONS[5];
        DIALOG_5 = DESCRIPTIONS[6];
    }

    private enum CurScreen {
        INTRO,
        ACCEPT,
        RANDOM,
        RED,
        LEAVE;

        CurScreen() {
        }
    }
}
