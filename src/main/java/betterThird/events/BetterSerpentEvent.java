package betterThird.events;

import betterThird.BetterThird;
import betterThird.patches.MaskedBanditsPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Enlightenment;
import com.megacrit.cardcrawl.cards.curses.Doubt;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.shrines.GoldShrine;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.List;

public class BetterSerpentEvent extends AbstractImageEvent {

    public static final String ID = BetterThird.makeID("BetterSerpent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/liarsGame.jpg";

    private static final String AGREE_DIALOG, IGNORE_DIALOG, GOLD_RAIN_MSG, RENOUNCE_DIALOG, ENLIGHTEN_MSG;
    private CUR_SCREEN screen;
    private static final int GOLD_REWARD = 175;
    private static final int A_2_GOLD_REWARD = 150;
    private int goldReward, goldCost, maxHPGain;
    private AbstractCard curse, card;

    public BetterSerpentEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.screen = CUR_SCREEN.INTRO;
        this.card = new Enlightenment();
        this.card.misc = 1;
        this.maxHPGain = 6;
        this.goldCost = AbstractDungeon.player.gold;
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.goldReward = A_2_GOLD_REWARD;
        } else {
            this.goldReward = GOLD_REWARD;
        }

        this.curse = new Doubt();
        this.imageEventText.setDialogOption(OPTIONS[0] + this.goldReward + OPTIONS[1], CardLibrary.getCopy(this.curse.cardID));
        this.imageEventText.setDialogOption(OPTIONS[5] + this.card + OPTIONS[6], this.card);
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_SERPENT");
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {
            case INTRO:
                if (buttonPressed == 0) {
                    this.imageEventText.updateBodyText(AGREE_DIALOG);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[3]);
                    this.screen = CUR_SCREEN.AGREE;
                } else if (buttonPressed == 1){
                    this.imageEventText.updateBodyText(RENOUNCE_DIALOG);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[3]);
                    this.screen = CUR_SCREEN.DISAGREE;
                } else if (buttonPressed == 2){
                    this.imageEventText.updateBodyText(IGNORE_DIALOG);
                    this.imageEventText.removeDialogOption(1);
                    this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                    this.screen = CUR_SCREEN.COMPLETE;
                    logMetricTakeDamage(ID, "Ignored", this.goldCost);
                }
                break;
            case AGREE:
                logMetricGainGoldAndDamage(ID, "AGREE", this.goldReward, this.goldCost); //current gold stored as damage
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.curse, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                AbstractDungeon.effectList.add(new RainingGoldEffect(this.goldReward));
                AbstractDungeon.player.gainGold(this.goldReward);
                this.imageEventText.updateBodyText(GOLD_RAIN_MSG);
                this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                this.screen = CUR_SCREEN.COMPLETE;
                break;
            case DISAGREE:
                List<String> tempList = new ArrayList<>();
                tempList.add(this.card.cardID);
                logMetric(ID, "RENOUNCE", tempList, null, null,
                        null, null, null, null, this.goldCost,
                        0, 0, 0, 0, this.goldCost);
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.card, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                AbstractDungeon.player.loseGold(goldCost);
                AbstractDungeon.player.increaseMaxHp(maxHPGain, true);
                this.imageEventText.updateBodyText(ENLIGHTEN_MSG);
                this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                AbstractDungeon.shrineList.remove(GoldShrine.ID);
                this.screen = CUR_SCREEN.COMPLETE;
                break;
            default:
                this.openMap();
        }

    }// 89

    static {
        AGREE_DIALOG = DESCRIPTIONS[1];
        IGNORE_DIALOG = DESCRIPTIONS[2];
        GOLD_RAIN_MSG = DESCRIPTIONS[3];
        RENOUNCE_DIALOG = DESCRIPTIONS[4];
        ENLIGHTEN_MSG = DESCRIPTIONS[5];

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
