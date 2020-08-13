package betterThird.events;

import betterThird.BetterThird;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Doubt;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class BetterSerpentEvent extends AbstractImageEvent {

    public static final String ID = BetterThird.makeID("BetterSerpent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/liarsGame.jpg";

    private static final String AGREE_DIALOG;
    private static final String DISAGREE_DIALOG;
    private static final String GOLD_RAIN_MSG;
    private CUR_SCREEN screen;
    private static final int GOLD_REWARD = 175;
    private static final int A_2_GOLD_REWARD = 150;
    private int goldReward;
    private AbstractCard curse;
    private String optionsChosen;

    public BetterSerpentEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.optionsChosen = "";
        this.screen = CUR_SCREEN.INTRO;// 27
        if (AbstractDungeon.ascensionLevel >= 15) {// 47
            this.goldReward = A_2_GOLD_REWARD;// 48
        } else {
            this.goldReward = GOLD_REWARD;// 50
        }

        this.curse = new Doubt();// 52
        this.imageEventText.setDialogOption(OPTIONS[0] + this.goldReward + OPTIONS[1], CardLibrary.getCopy(this.curse.cardID));// 54
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {// 39
            CardCrawlGame.sound.play("EVENT_SERPENT");// 40
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {// 60
            case INTRO:
                if (buttonPressed == 0) {// 62
                    this.imageEventText.updateBodyText(AGREE_DIALOG);// 63
                    this.imageEventText.removeDialogOption(1);// 64
                    this.imageEventText.updateDialogOption(0, OPTIONS[3]);// 65
                    this.screen = CUR_SCREEN.AGREE;// 66
                    AbstractEvent.logMetricGainGoldAndCard("Liars Game", "AGREE", this.curse, this.goldReward);// 67
                } else {
                    this.imageEventText.updateBodyText(DISAGREE_DIALOG);// 69
                    this.imageEventText.removeDialogOption(1);// 70
                    this.imageEventText.updateDialogOption(0, OPTIONS[4]);// 71
                    this.screen = CUR_SCREEN.DISAGREE;// 72
                    AbstractEvent.logMetricIgnored("Liars Game");// 73
                }
                break;
            case AGREE:
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.curse, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));// 77
                AbstractDungeon.effectList.add(new RainingGoldEffect(this.goldReward));// 79
                AbstractDungeon.player.gainGold(this.goldReward);// 80
                this.imageEventText.updateBodyText(GOLD_RAIN_MSG);// 81
                this.imageEventText.updateDialogOption(0, OPTIONS[4]);// 82
                this.screen = CUR_SCREEN.COMPLETE;// 83
                break;// 84
            default:
                this.openMap();// 86
        }

    }// 89

    static {
        AGREE_DIALOG = DESCRIPTIONS[1];// 24
        DISAGREE_DIALOG = DESCRIPTIONS[2];// 25
        GOLD_RAIN_MSG = DESCRIPTIONS[3];// 26
    }

    private enum CUR_SCREEN {
        INTRO,
        AGREE,
        DISAGREE,
        COMPLETE;

        CUR_SCREEN() {
        }
    }
}
