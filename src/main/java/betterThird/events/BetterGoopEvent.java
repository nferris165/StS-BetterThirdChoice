package betterThird.events;

import betterThird.BetterThird;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class BetterGoopEvent extends AbstractImageEvent {

    public static final String ID = BetterThird.makeID("BetterGoop");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/goopPuddle.jpg";

    private static final String GOLD_DIALOG;
    private static final String LEAVE_DIALOG;
    private CurScreen screen;
    private int damage;
    private int gold;
    private int goldLoss;
    private String optionsChosen;

    public BetterGoopEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.optionsChosen = "";
        this.screen = CurScreen.INTRO;
        this.damage = 11;// 27
        this.gold = 75;// 28
        if (AbstractDungeon.ascensionLevel >= 15) {// 38
            this.goldLoss = AbstractDungeon.miscRng.random(35, 75);// 39
        } else {
            this.goldLoss = AbstractDungeon.miscRng.random(20, 50);// 41
        }

        if (this.goldLoss > AbstractDungeon.player.gold) {// 44
            this.goldLoss = AbstractDungeon.player.gold;// 45
        }

        this.imageEventText.setDialogOption(OPTIONS[0] + this.gold + OPTIONS[1] + this.damage + OPTIONS[2]);// 47
        this.imageEventText.setDialogOption(OPTIONS[3] + this.goldLoss + OPTIONS[4]);

    }

    @Override
    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {// 53
            CardCrawlGame.sound.play("EVENT_SPIRITS");// 54
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {// 60
            case INTRO:
                switch(buttonPressed) {// 62
                    case 0:
                        this.imageEventText.updateBodyText(GOLD_DIALOG);// 64
                        this.imageEventText.clearAllDialogs();// 65
                        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, this.damage));// 66
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.FIRE));// 67
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.gold));// 72
                        AbstractDungeon.player.gainGold(this.gold);// 73
                        this.imageEventText.setDialogOption(OPTIONS[5]);// 74
                        this.screen = CurScreen.RESULT;// 75
                        AbstractEvent.logMetricGainGoldAndDamage("World of Goop", "Gather Gold", this.gold, this.damage);// 76
                        return;// 92
                    case 1:
                        this.imageEventText.updateBodyText(LEAVE_DIALOG);// 79
                        AbstractDungeon.player.loseGold(this.goldLoss);// 80
                        this.imageEventText.clearAllDialogs();// 81
                        this.imageEventText.setDialogOption(OPTIONS[5]);// 82
                        this.screen = CurScreen.RESULT;// 83
                        logMetricLoseGold("World of Goop", "Left Gold", this.goldLoss);// 84
                        return;
                    default:
                        return;
                }
            default:
                this.openMap();// 89
        }
    }

    static {
        GOLD_DIALOG = DESCRIPTIONS[1];
        LEAVE_DIALOG = DESCRIPTIONS[2];
    }

    private enum CurScreen {
        INTRO,
        RESULT;

        CurScreen() {
        }
    }
}
