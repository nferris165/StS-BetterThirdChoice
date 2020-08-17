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

    public BetterPortalEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.screen = CurScreen.INTRO;
        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[2]);

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
                        this.imageEventText.updateBodyText("");
                        this.screen = CurScreen.RANDOM;
                        //logMetricIgnored("SecretPortal");
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                    case 2:
                        this.imageEventText.updateBodyText(DIALOG_3);
                        this.screen = CurScreen.LEAVE;
                        //logMetricIgnored("SecretPortal");
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
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
