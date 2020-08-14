package betterThird.events;

import betterThird.BetterThird;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.RitualDagger;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class BetterNestEvent extends AbstractImageEvent {

    public static final String ID = BetterThird.makeID("BetterNest");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/theNest.jpg";

    private static final String INTRO_BODY_M_2;
    private static final String ACCEPT_BODY;
    private static final String EXIT_BODY;
    private static final int HP_LOSS = 6;
    private int goldGain;
    private CurScreen screen;
    private String optionsChosen;

    public BetterNestEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.optionsChosen = "";
        this.screen = CurScreen.INTRO;
        this.imageEventText.setDialogOption(OPTIONS[5]);
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.goldGain = 50;
        } else {
            this.goldGain = 99;
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(screen) {
            case INTRO:
                this.imageEventText.updateBodyText(INTRO_BODY_M_2);
                this.imageEventText.setDialogOption(OPTIONS[0] + HP_LOSS + OPTIONS[1], new RitualDagger());
                UnlockTracker.markCardAsSeen("RitualDagger");
                this.imageEventText.updateDialogOption(0, OPTIONS[2] + this.goldGain + OPTIONS[3]);
                this.screen = CurScreen.RESULT;
                break;
            case RESULT:
                switch(buttonPressed) {
                    case 0:
                        //logMetricGainGold("Nest", "Stole From Cult", this.goldGain);
                        this.imageEventText.updateBodyText(EXIT_BODY);
                        this.screen = CurScreen.LEAVE;
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.goldGain));
                        AbstractDungeon.player.gainGold(this.goldGain);
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        return;
                    case 1:
                        AbstractCard c = new RitualDagger();
                        //logMetricObtainCardAndDamage("Nest", "Joined the Cult", c, HP_LOSS);
                        this.imageEventText.updateBodyText(ACCEPT_BODY);
                        AbstractDungeon.player.damage(new DamageInfo(null, HP_LOSS));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float) Settings.WIDTH * 0.3F, (float)Settings.HEIGHT / 2.0F));
                        this.screen = CurScreen.LEAVE;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        return;
                    default:
                        return;
                }
            case LEAVE:
                this.openMap();
                break;
            default:
                this.openMap();
        }

    }

    static {
        INTRO_BODY_M_2 = DESCRIPTIONS[1];
        ACCEPT_BODY = DESCRIPTIONS[2];
        EXIT_BODY = DESCRIPTIONS[3];
    }

    private enum CurScreen {
        INTRO,
        RESULT,
        LEAVE;

        CurScreen() {
        }
    }
}
