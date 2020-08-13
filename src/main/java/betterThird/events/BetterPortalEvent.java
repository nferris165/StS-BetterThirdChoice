package betterThird.events;

import betterThird.BetterThird;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.vfx.FadeWipeParticle;

public class BetterPortalEvent extends AbstractImageEvent {

    public static final String ID = BetterThird.makeID("BetterPortal");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/secretPortal.jpg";

    private static final String DIALOG_2;
    private static final String DIALOG_3;
    private CurScreen screen;
    private String optionsChosen;

    public BetterPortalEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.optionsChosen = "";

        if(AbstractDungeon.ascensionLevel >= 15) {
        } else {
        }

    }

    @Override
    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_PORTAL");
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {// 47
            case INTRO:
                switch(buttonPressed) {// 49
                    case 0:
                        this.imageEventText.updateBodyText(DIALOG_2);// 51
                        this.screen = CurScreen.ACCEPT;// 52
                        logMetric("SecretPortal", "Took Portal");// 53
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);// 54
                        CardCrawlGame.screenShake.mildRumble(5.0F);// 55
                        CardCrawlGame.sound.play("ATTACK_MAGIC_SLOW_2");// 56
                        break;// 57
                    case 1:
                        this.imageEventText.updateBodyText(DIALOG_3);// 59
                        this.screen = CurScreen.LEAVE;// 60
                        logMetricIgnored("SecretPortal");// 61
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);// 62
                }

                this.imageEventText.clearRemainingOptions();// 67
                break;// 68
            case ACCEPT:
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;// 71
                MapRoomNode node = new MapRoomNode(-1, 15);// 72
                node.room = new MonsterRoomBoss();// 73
                AbstractDungeon.nextRoom = node;// 74
                CardCrawlGame.music.fadeOutTempBGM();// 75
                AbstractDungeon.pathX.add(1);// 76
                AbstractDungeon.pathY.add(15);// 77
                AbstractDungeon.topLevelEffects.add(new FadeWipeParticle());// 78
                AbstractDungeon.nextRoomTransitionStart();// 79
                break;// 80
            default:
                this.openMap();// 82
        }

    }

    static {
        DIALOG_2 = DESCRIPTIONS[1];
        DIALOG_3 = DESCRIPTIONS[2];
    }

    private enum CurScreen {
        INTRO,
        ACCEPT,
        LEAVE;

        CurScreen() {
        }
    }
}
