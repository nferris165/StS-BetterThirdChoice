package betterThird.events;

import betterThird.BetterThird;
import betterThird.util.PortalInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
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

    private static final String DIALOG_2, DIALOG_3, DIALOG_4, DIALOG_41, DIALOG_42;
    private static String color;
    private CurScreen screen;
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

    }

    private static void generatePortal(){
        portals.add(new PortalInfo("[#FF0000]", 1.0F)); //Red
        portals.add(new PortalInfo("[#FF7F00]", 1.0F)); //Orange
        portals.add(new PortalInfo("[#FFFF00]", 1.0F)); //Yellow
        portals.add(new PortalInfo("[#00FF00]", 1.0F)); //Green
        portals.add(new PortalInfo("[#0000FF]", 1.0F)); //Blue
        portals.add(new PortalInfo("[#8B00FF]", 1.0F)); //Violet
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
                        //logMetric("SecretPortal", "Took Portal");
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        CardCrawlGame.screenShake.mildRumble(5.0F);
                        CardCrawlGame.sound.play("ATTACK_MAGIC_SLOW_2");
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DIALOG_4 + color + DIALOG_41 + color + DIALOG_42);
                        this.screen = CurScreen.RANDOM;
                        //logMetricIgnored("SecretPortal");
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        break;
                    case 2:
                        this.imageEventText.updateBodyText(DIALOG_3);
                        this.screen = CurScreen.LEAVE;
                        //logMetricIgnored("SecretPortal");
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
                break;
            default:
                this.openMap();
                break;
        }

    }

    static {
        DIALOG_2 = DESCRIPTIONS[1];
        DIALOG_3 = DESCRIPTIONS[2];
        DIALOG_4 = DESCRIPTIONS[3];
        DIALOG_41 = DESCRIPTIONS[4];
        DIALOG_42 = DESCRIPTIONS[5];
    }

    private enum CurScreen {
        INTRO,
        ACCEPT,
        RANDOM,
        LEAVE;

        CurScreen() {
        }
    }
}
