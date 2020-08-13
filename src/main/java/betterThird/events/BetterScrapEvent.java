package betterThird.events;

import betterThird.BetterThird;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BetterScrapEvent extends AbstractImageEvent {

    public static final String ID = BetterThird.makeID("BetterScrap");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/scrapOoze.jpg";

    private int relicObtainChance = 25;
    private int dmg = 3;
    private int totalDamageDealt = 0;
    private static final String FAIL_MSG;
    private static final String SUCCESS_MSG;
    private static final String ESCAPE_MSG;
    private CurScreen screen;
    private String optionsChosen;

    public BetterScrapEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.optionsChosen = "";
        this.screen = CurScreen.INTRO;
        if (AbstractDungeon.ascensionLevel >= 15) {// 35
            this.dmg = 5;// 36
        }

        this.imageEventText.setDialogOption(OPTIONS[0] + this.dmg + OPTIONS[1] + this.relicObtainChance + OPTIONS[2]);// 39
        this.imageEventText.setDialogOption(OPTIONS[3]);// 40
    }

    @Override
    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {// 45
            CardCrawlGame.sound.play("EVENT_OOZE");// 46
        }

    }// 48

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {// 52
            case INTRO:
                switch(buttonPressed) {// 54
                    case 0:
                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.dmg));// 56
                        CardCrawlGame.sound.play("ATTACK_POISON");// 57
                        this.totalDamageDealt += this.dmg;// 58
                        int random = AbstractDungeon.miscRng.random(0, 99);// 59
                        if (random >= 99 - this.relicObtainChance) {// 61
                            this.imageEventText.updateBodyText(SUCCESS_MSG);// 62
                            AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());// 63 64
                            AbstractEvent.logMetricObtainRelicAndDamage("Scrap Ooze", "Success", r, this.totalDamageDealt);// 65
                            this.imageEventText.updateDialogOption(0, OPTIONS[3]);// 66
                            this.imageEventText.removeDialogOption(1);// 67
                            this.screen = CurScreen.LEAVE;
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, r);// 69
                        } else {
                            this.imageEventText.updateBodyText(FAIL_MSG);// 74
                            this.relicObtainChance += 10;// 75
                            ++this.dmg;// 76
                            this.imageEventText.updateDialogOption(0, OPTIONS[4] + this.dmg + OPTIONS[1] + this.relicObtainChance + OPTIONS[2]);// 77
                            this.imageEventText.updateDialogOption(1, OPTIONS[3]);// 80
                        }

                        return;
                    case 1:
                        AbstractEvent.logMetricTakeDamage("Scrap Ooze", "Fled", this.totalDamageDealt);// 85
                        this.imageEventText.updateBodyText(ESCAPE_MSG);// 86
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);// 87
                        this.imageEventText.removeDialogOption(1);// 88
                        this.screen = CurScreen.LEAVE;
                        return;
                    default:
                        BetterThird.logger.info("ERROR: case " + buttonPressed + " should never be called");// 92
                        return;
                }
            case LEAVE:
                this.openMap();// 97
        }

    }

    static {
        FAIL_MSG = DESCRIPTIONS[1];
        SUCCESS_MSG = DESCRIPTIONS[2];
        ESCAPE_MSG = DESCRIPTIONS[3];
    }

    private enum CurScreen {
        INTRO,
        RESULT,
        LEAVE;

        CurScreen() {
        }
    }
}
